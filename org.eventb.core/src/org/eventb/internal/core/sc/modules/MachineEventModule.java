/*******************************************************************************
 * Copyright (c) 2006, 2008 ETH Zurich and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     ETH Zurich - initial API and implementation
 *     Systerel - added deleteAll() method
 *******************************************************************************/
package org.eventb.internal.core.sc.modules;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eventb.core.EventBAttributes;
import org.eventb.core.EventBPlugin;
import org.eventb.core.IEvent;
import org.eventb.core.ILabeledElement;
import org.eventb.core.IMachineFile;
import org.eventb.core.ISCEvent;
import org.eventb.core.ISCMachineFile;
import org.eventb.core.ast.FormulaFactory;
import org.eventb.core.ast.FreeIdentifier;
import org.eventb.core.ast.ITypeEnvironment;
import org.eventb.core.sc.GraphProblem;
import org.eventb.core.sc.SCCore;
import org.eventb.core.sc.state.IConcreteEventInfo;
import org.eventb.core.sc.state.IConcreteEventTable;
import org.eventb.core.sc.state.IEventAccuracyInfo;
import org.eventb.core.sc.state.IIdentifierSymbolTable;
import org.eventb.core.sc.state.ILabelSymbolTable;
import org.eventb.core.sc.state.IMachineLabelSymbolTable;
import org.eventb.core.sc.state.ISCStateRepository;
import org.eventb.core.sc.symbolTable.IEventSymbolInfo;
import org.eventb.core.sc.symbolTable.ILabelSymbolInfo;
import org.eventb.core.sc.symbolTable.ISymbolInfo;
import org.eventb.core.sc.symbolTable.IVariableSymbolInfo;
import org.eventb.core.tool.IModuleType;
import org.eventb.internal.core.sc.ConcreteEventInfo;
import org.eventb.internal.core.sc.ConcreteEventTable;
import org.eventb.internal.core.sc.EventAccuracyInfo;
import org.eventb.internal.core.sc.Messages;
import org.eventb.internal.core.sc.symbolTable.EventLabelSymbolTable;
import org.eventb.internal.core.sc.symbolTable.EventSymbolInfo;
import org.eventb.internal.core.sc.symbolTable.StackedIdentifierSymbolTable;
import org.rodinp.core.IInternalParent;
import org.rodinp.core.IRodinElement;
import org.rodinp.core.RodinDBException;

/**
 * @author Stefan Hallerstede
 *
 */
public class MachineEventModule extends LabeledElementModule {
	
	public static final IModuleType<MachineEventModule> MODULE_TYPE = 
		SCCore.getModuleType(EventBPlugin.PLUGIN_ID + ".machineEventModule"); //$NON-NLS-1$
	
	public IModuleType<?> getModuleType() {
		return MODULE_TYPE;
	}

	public static int EVENT_LABEL_SYMTAB_SIZE = 47;
	public static int EVENT_IDENT_SYMTAB_SIZE = 29;

	private IIdentifierSymbolTable identifierSymbolTable;
	
	private FormulaFactory factory;
	
	private ITypeEnvironment machineTypeEnvironment;
	
	private IEvent[] events;
	
	private IConcreteEventTable concreteEventTable;
	
	public void process(
			IRodinElement element, 
			IInternalParent target,
			ISCStateRepository repository, 
			IProgressMonitor monitor) throws CoreException {
		
		IMachineFile machineFile = (IMachineFile) element;
		
		if (events.length == 0)
			return;
		
		monitor.subTask(Messages.bind(Messages.progress_MachineEvents));
		
		IEventSymbolInfo[] symbolInfos = 
			fetchEvents(machineFile, repository, monitor);
		
		ISCEvent[] scEvents = new ISCEvent[events.length];
		
		commitEvents(machineFile, (ISCMachineFile) target, scEvents, symbolInfos, monitor);
				
		processEvents(scEvents, repository, symbolInfos, monitor);
				
	}

	private void commitEvents(
			IMachineFile machineFile,
			ISCMachineFile target, 
			ISCEvent[] scEvents, 
			IEventSymbolInfo[] symbolInfos, 
			IProgressMonitor monitor) throws CoreException {
		
		int index = 0;
		
		for (int i=0; i < events.length; i++) {
			if (symbolInfos[i] != null && !symbolInfos[i].hasError()) {
				scEvents[i] = createSCEvent(target, index++, symbolInfos[i], events[i], monitor);
			}
		}
		
	}

	private static final String EVENT_NAME_PREFIX = "EVT";
	
	private ISCEvent createSCEvent(
			ISCMachineFile target, 
			int index,
			IEventSymbolInfo symbolInfo,
			IEvent event,
			IProgressMonitor monitor) throws RodinDBException {
		ISCEvent scEvent = target.getSCEvent(EVENT_NAME_PREFIX + index);
		scEvent.create(null, monitor);
		scEvent.setLabel(symbolInfo.getSymbol(), monitor);
		scEvent.setSource(event, monitor);
		return scEvent;
	}

	private void processEvents(
			ISCEvent[] scEvents, 
			ISCStateRepository repository, 
			IEventSymbolInfo[] symbolInfos, 
			IProgressMonitor monitor) throws CoreException {
		
		for (int i=0; i < events.length; i++) {
			
			if (symbolInfos[i] != null) {
				
				
				repository.setState(concreteEventTable.getConcreteEventInfo(symbolInfos[i].getSymbol()));
			
				repository.setState(
						new StackedIdentifierSymbolTable(
								identifierSymbolTable, 
								EVENT_IDENT_SYMTAB_SIZE, 
								factory));
			
				repository.setState(
						new EventLabelSymbolTable(EVENT_LABEL_SYMTAB_SIZE));
			
				ITypeEnvironment eventTypeEnvironment = factory.makeTypeEnvironment();
				eventTypeEnvironment.addAll(machineTypeEnvironment);
				addPostValues(eventTypeEnvironment);
				repository.setTypeEnvironment(eventTypeEnvironment);
				
				final IEventAccuracyInfo accuracyInfo = new EventAccuracyInfo();
				repository.setState(accuracyInfo);
			
				initProcessorModules(events[i], repository, null);
			
				processModules(events[i], scEvents[i], repository, monitor);
			
				endProcessorModules(events[i], repository, null);
				
				if (scEvents[i] != null)
					scEvents[i].setAccuracy(accuracyInfo.isAccurate(), null);
			}
			
			monitor.worked(1);
		}
		
	}

	private IEventSymbolInfo[] fetchEvents(
			IMachineFile machineFile, 
			ISCStateRepository repository, 
			IProgressMonitor monitor) throws CoreException {
		
		String machineName = machineFile.getElementName();
		
		initFilterModules(repository, null);
		
		IEventSymbolInfo[] symbolInfos = new IEventSymbolInfo[events.length];
		
		IEventSymbolInfo init = null;

		for (int i=0; i < events.length; i++) {
			
			IEvent event = events[i];
			
			symbolInfos[i] = 
				(IEventSymbolInfo) fetchLabel(event, machineName, null);
			
			if (symbolInfos[i] == null)
				continue;
			
			IConcreteEventInfo concreteEventInfo = new ConcreteEventInfo(event, symbolInfos[i]);
			
			concreteEventTable.addConcreteEventInfo(concreteEventInfo);
			
			if (!filterModules(event, repository, null)) {
				
				symbolInfos[i].setError();
				
			} else {
				
				if (concreteEventInfo.isInitialisation()) {
					init = symbolInfos[i];
				}
				
			}
			
		}
		
		if (init == null)
			createProblemMarker(
					machineFile,
					GraphProblem.MachineWithoutInitialisationWarning);
		
		endFilterModules(repository, null);
		
		return symbolInfos;
	}
	
	private void addPostValues(ITypeEnvironment typeEnvironment) {
		for (ISymbolInfo symbolInfo : identifierSymbolTable.getSymbolInfosFromTop())
			if (symbolInfo instanceof IVariableSymbolInfo){
				IVariableSymbolInfo variableSymbolInfo = (IVariableSymbolInfo) symbolInfo;
				if (!variableSymbolInfo.hasError() 
						&& (variableSymbolInfo.isConcrete() || variableSymbolInfo.isAbstract())) {
					FreeIdentifier identifier = 
						factory.makeFreeIdentifier(
								variableSymbolInfo.getSymbol(), null, 
								variableSymbolInfo.getType()).withPrime(factory);
					typeEnvironment.addName(identifier.getName(), identifier.getType());
				}
		}
	}

	/* (non-Javadoc)
	 * @see org.eventb.core.sc.ProcessorModule#initModule(org.rodinp.core.IRodinElement, org.eventb.core.sc.IStateRepository, org.eclipse.core.runtime.IProgressMonitor)
	 */
	@Override
	public void initModule(
			IRodinElement element, 
			ISCStateRepository repository, 
			IProgressMonitor monitor) throws CoreException {
		super.initModule(element, repository, monitor);
		IMachineFile machineFile = (IMachineFile) element;
		
		events = machineFile.getEvents();
		
		concreteEventTable = new ConcreteEventTable();
		repository.setState(concreteEventTable);
		
		identifierSymbolTable =
			(IIdentifierSymbolTable) repository.getState(IIdentifierSymbolTable.STATE_TYPE);
		
		factory = FormulaFactory.getDefault();
		
		machineTypeEnvironment = repository.getTypeEnvironment();
				
	}

	/* (non-Javadoc)
	 * @see org.eventb.core.sc.ProcessorModule#endModule(org.rodinp.core.IRodinElement, org.eventb.core.sc.IStateRepository, org.eclipse.core.runtime.IProgressMonitor)
	 */
	@Override
	public void endModule(
			IRodinElement element, 
			ISCStateRepository repository, 
			IProgressMonitor monitor) throws CoreException {
		repository.setState(identifierSymbolTable);
		repository.setTypeEnvironment(machineTypeEnvironment);
		identifierSymbolTable = null;
		concreteEventTable = null;
		factory = null;
		machineTypeEnvironment = null;
		events = null;
		super.endModule(element, repository, monitor);
	}
	
	/* (non-Javadoc)
	 * @see org.eventb.internal.core.sc.modules.LabeledElementModule#getLabelSymbolTableFromRepository(org.eventb.core.sc.IStateRepository)
	 */
	@Override
	protected ILabelSymbolTable getLabelSymbolTableFromRepository(
			ISCStateRepository repository) throws CoreException {
		return (ILabelSymbolTable) repository.getState(IMachineLabelSymbolTable.STATE_TYPE);
	}

	@Override
	protected ILabelSymbolInfo createLabelSymbolInfo(
			String symbol, ILabeledElement element, String component) throws CoreException {
		return new EventSymbolInfo(symbol, element, EventBAttributes.LABEL_ATTRIBUTE, component);
	}

}

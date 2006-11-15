/*******************************************************************************
 * Copyright (c) 2006 ETH Zurich.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.eventb.internal.core.sc.modules;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eventb.core.EventBAttributes;
import org.eventb.core.IMachineFile;
import org.eventb.core.IRefinesMachine;
import org.eventb.core.ISCAction;
import org.eventb.core.ISCCarrierSet;
import org.eventb.core.ISCConstant;
import org.eventb.core.ISCEvent;
import org.eventb.core.ISCGuard;
import org.eventb.core.ISCIdentifierElement;
import org.eventb.core.ISCInternalContext;
import org.eventb.core.ISCMachineFile;
import org.eventb.core.ISCRefinesMachine;
import org.eventb.core.ISCVariable;
import org.eventb.core.ast.Assignment;
import org.eventb.core.ast.FormulaFactory;
import org.eventb.core.ast.FreeIdentifier;
import org.eventb.core.ast.ITypeEnvironment;
import org.eventb.core.ast.Predicate;
import org.eventb.core.ast.Type;
import org.eventb.core.sc.GraphProblem;
import org.eventb.core.sc.ProcessorModule;
import org.eventb.core.sc.state.IAbstractEventInfo;
import org.eventb.core.sc.state.IAbstractEventTable;
import org.eventb.core.sc.state.IContextTable;
import org.eventb.core.sc.state.IIdentifierSymbolTable;
import org.eventb.core.sc.state.IStateSC;
import org.eventb.core.sc.state.ITypingState;
import org.eventb.core.sc.symbolTable.IIdentifierSymbolInfo;
import org.eventb.core.sc.symbolTable.IVariableSymbolInfo;
import org.eventb.core.state.IStateRepository;
import org.eventb.internal.core.sc.AbstractEventInfo;
import org.eventb.internal.core.sc.AbstractEventTable;
import org.eventb.internal.core.sc.Messages;
import org.eventb.internal.core.sc.symbolTable.SymbolInfoFactory;
import org.rodinp.core.IAttributeType;
import org.rodinp.core.IInternalElement;
import org.rodinp.core.IInternalParent;
import org.rodinp.core.IRodinElement;
import org.rodinp.core.RodinDBException;

/**
 * @author Stefan Hallerstede
 *
 */
public class MachineRefinesModule extends ProcessorModule {
	
	private static int ABSEVT_SYMTAB_SIZE = 1013;
	
	private FreeIdentifier[] emptyVariableList;
	private Predicate[] emptyPredicateList;
	private Assignment[] emptyAssignmentList;
	
	ISCMachineFile scMachineFile;
	IRefinesMachine refinesMachine;
	IAbstractEventTable abstractEventTable;
	ITypeEnvironment typeEnvironment;

	public void process(
			IRodinElement element, 
			IInternalParent target,
			IStateRepository<IStateSC> repository,
			IProgressMonitor monitor)
			throws CoreException {
		
		// now we can finish if there is no abstraction
		
		if (scMachineFile == null)
			return;
		
		monitor.subTask(Messages.bind(Messages.progress_MachineRefines));
		
		saveRefinesMachine(target, null);
		
		IIdentifierSymbolTable abstractIdentifierSymbolTable =
			(IIdentifierSymbolTable) repository.getState(IIdentifierSymbolTable.STATE_TYPE);
		
		IContextTable contextTable =
			(IContextTable) repository.getState(IContextTable.STATE_TYPE);
		
		fetchSCMachine(
				abstractIdentifierSymbolTable, 
				contextTable,
				repository.getFormulaFactory(), 
				null);
		
		monitor.worked(1);
		
	}

	private void saveRefinesMachine(IInternalParent target, IProgressMonitor monitor) throws RodinDBException {
		ISCRefinesMachine scRefinesMachine =
			(ISCRefinesMachine) target.createInternalElement(
					ISCRefinesMachine.ELEMENT_TYPE, "REF", null, monitor);
		scRefinesMachine.setAbstractSCMachine(scMachineFile, null);
	}
	
	/**
	 * Fetches all elements of the abstract machine and fills the symbol tables.
	 * All these elements are considered to be successfully checked and the corresponding
	 * symbol made immutable!
	 * 
	 * @param identifierSymbolTable the identifier symbol table
	 * @param contextTable the abstract contexts
	 * @param factory the formula factory
	 * @param monitor a progress monitor
	 * @throws CoreException if there was a problem
	 */
	protected void fetchSCMachine(
			IIdentifierSymbolTable identifierSymbolTable, 
			IContextTable contextTable,
			FormulaFactory factory, 
			IProgressMonitor monitor) throws CoreException {
		
		fetchSCContexts(
				identifierSymbolTable, 
				contextTable,
				factory, 
				monitor);
		
		fetchSCVariables(identifierSymbolTable, factory, monitor);
		
		fetchSCEvents(factory, monitor);
		
	}
	
	protected void fetchSCEvents(
			FormulaFactory factory, 
			IProgressMonitor monitor) throws CoreException {
		
		ISCEvent[] events = scMachineFile.getSCEvents(null);
		
		for (ISCEvent event : events) {
			
			fetchSCEvent(event, factory, monitor);
			
		}
		
	}

	protected void fetchSCContexts(
			IIdentifierSymbolTable identifierSymbolTable, 
			IContextTable contextTable,
			FormulaFactory factory, 
			IProgressMonitor monitor) throws CoreException {
		
		ISCInternalContext[] contexts = scMachineFile.getSCSeenContexts(null);
		
		for (ISCInternalContext context : contexts) {
			
			final String component = context.getElementName();
			
			contextTable.addContext(component, context);
			
			ISCCarrierSet[] sets = context.getSCCarrierSets(null);
			
			for (ISCCarrierSet set : sets) {
				IIdentifierSymbolInfo symbolInfo =
					fetchSymbol(
							set, 
							refinesMachine, 
							EventBAttributes.TARGET_ATTRIBUTE,
							identifierSymbolTable, 
							factory, 
							component);
				symbolInfo.setImmutable();
			}
			
			ISCConstant[] constants = context.getSCConstants(null);
			
			for (ISCConstant constant : constants) {
				IIdentifierSymbolInfo symbolInfo =
					fetchSymbol(
							constant, 
							refinesMachine, 
							EventBAttributes.TARGET_ATTRIBUTE,
							identifierSymbolTable, 
							factory, 
							component);
				symbolInfo.setImmutable();
			}
						
		}
		
	}
	
	protected void fetchSCVariables(
			IIdentifierSymbolTable identifierSymbolTable, 
			FormulaFactory factory, 
			IProgressMonitor monitor) throws CoreException {
		
		ISCVariable[] variables = scMachineFile.getSCVariables(null);
		
		if (variables.length == 0)
			return;
		
		String component = scMachineFile.getElementName();
		
		for (ISCVariable variable : variables) {
			IVariableSymbolInfo symbolInfo = (IVariableSymbolInfo)
				fetchSymbol(
						variable, 
						refinesMachine, 
						EventBAttributes.TARGET_ATTRIBUTE,
						identifierSymbolTable, 
						factory, 
						component);
			if (variable.isForbidden(monitor))
				symbolInfo.setForbidden();
			symbolInfo.setImmutable();
		}
		
	}

	protected IIdentifierSymbolInfo fetchSymbol(
			ISCIdentifierElement identifier, 
			IInternalElement pointerElement, 
			IAttributeType.String pointerAttribute,
			IIdentifierSymbolTable identifierSymbolTable,
			FormulaFactory factory,
			String component) throws CoreException {
		
		String name = identifier.getIdentifierString(null);
		
//		this condition cannot be true:
//		if (identifierSymbolTable.containsKey(name))
//			return;

		Type type = identifier.getType(factory, null);
		
		IIdentifierSymbolInfo symbolInfo = 
			SymbolInfoFactory.createIdentifierSymbolInfo(
					name, identifier, pointerElement, pointerAttribute, component);
		
		symbolInfo.setType(type);
		
		symbolInfo.setVisible();
		
		identifierSymbolTable.putSymbolInfo(symbolInfo);
		
		typeEnvironment.addName(name, type);
		
		return symbolInfo;
		
	}

	protected void fetchSCEvent(
			ISCEvent event, 
			FormulaFactory factory,
			IProgressMonitor monitor) throws CoreException {
		
		String label = event.getLabel(monitor);
		
		IAbstractEventInfo abstractEventInfo;
		
		boolean forbidden = event.isForbidden(monitor);
		
		if (forbidden) {
			abstractEventInfo =
				new AbstractEventInfo(
						event,
						label, 
						emptyVariableList,
						emptyPredicateList,
						emptyAssignmentList);
		} else {
			abstractEventInfo =
				new AbstractEventInfo(
						event,
						label, 
						fetchEventVariables(event, factory),
						fetchEventGuards(event, factory),
						fetchEventActions(event, factory));
		}
		abstractEventInfo.setForbidden(forbidden);
		
		abstractEventTable.putAbstractEventInfo(abstractEventInfo);
	}
	
	private FreeIdentifier[] fetchEventVariables(
			ISCEvent event, 
			FormulaFactory factory) throws CoreException {
		ISCVariable[] variables = event.getSCVariables(null);
		FreeIdentifier[] identifiers = new FreeIdentifier[variables.length];
		
		for (int i=0; i<variables.length; i++) {
			identifiers[i] = variables[i].getIdentifier(factory, null);
		}
		
		return identifiers;
	}
	
	private Predicate[] fetchEventGuards(
			ISCEvent event, 
			FormulaFactory factory) throws CoreException {
		ISCGuard[] guards = event.getSCGuards(null);
		Predicate[] predicates = new Predicate[guards.length];
		
		for (int i=0; i<guards.length; i++) {
			predicates[i] = guards[i].getPredicate(factory, (IProgressMonitor) null);
		}
		return predicates;
	}
	
	private Assignment[] fetchEventActions(
			ISCEvent event, 
			FormulaFactory factory) throws CoreException {
		ISCAction[] actions = event.getSCActions(null);
		Assignment[] assignments = new Assignment[actions.length];
		
		for (int i=0; i<actions.length; i++) {
			assignments[i] = actions[i].getAssignment(factory, (IProgressMonitor) null);
		}
		return assignments;
	}

	/* (non-Javadoc)
	 * @see org.eventb.core.sc.ProcessorModule#initModule(org.rodinp.core.IRodinElement, org.eventb.core.sc.IStateRepository, org.eclipse.core.runtime.IProgressMonitor)
	 */
	@Override
	public void initModule(
			IRodinElement element, 
			IStateRepository<IStateSC> repository, 
			IProgressMonitor monitor) throws CoreException {

		emptyVariableList = new FreeIdentifier[0];
		emptyPredicateList = new Predicate[0];
		emptyAssignmentList = new Assignment[0];
		
		typeEnvironment = 
			((ITypingState) repository.getState(ITypingState.STATE_TYPE)).getTypeEnvironment();
		
		IMachineFile machineFile = (IMachineFile) element;
		
		refinesMachine = machineFile.getRefinesClause(null);
		
		scMachineFile = 
			(refinesMachine == null) ? null : refinesMachine.getAbstractSCMachine(null);
		
		if (scMachineFile != null && !scMachineFile.exists()) {
			createProblemMarker(
					refinesMachine, 
					EventBAttributes.TARGET_ATTRIBUTE, 
					GraphProblem.AbstractMachineNotFoundError);
			
			scMachineFile = null;
		}
		
		abstractEventTable = 
			new AbstractEventTable(ABSEVT_SYMTAB_SIZE, scMachineFile);
		
		repository.setState(abstractEventTable);
		
	}
	
	/* (non-Javadoc)
	 * @see org.eventb.core.sc.ProcessorModule#endModule(org.rodinp.core.IRodinElement, org.eventb.core.sc.IStateRepository, org.eclipse.core.runtime.IProgressMonitor)
	 */
	@Override
	public void endModule(
			IRodinElement element, 
			IStateRepository<IStateSC> repository, 
			IProgressMonitor monitor) throws CoreException {
		refinesMachine = null;
		scMachineFile = null;
		abstractEventTable = null;
	}

}


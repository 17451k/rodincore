/*******************************************************************************
 * Copyright (c) 2006 ETH Zurich.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.eventb.internal.core.sc.modules;

import java.util.Arrays;
import java.util.List;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eventb.core.EventBAttributes;
import org.eventb.core.EventBPlugin;
import org.eventb.core.IEvent;
import org.eventb.core.IGuard;
import org.eventb.core.ILabeledElement;
import org.eventb.core.ISCEvent;
import org.eventb.core.ISCGuard;
import org.eventb.core.ast.FreeIdentifier;
import org.eventb.core.ast.ITypeEnvironment;
import org.eventb.core.ast.Predicate;
import org.eventb.core.ast.Type;
import org.eventb.core.sc.GraphProblem;
import org.eventb.core.sc.ISCFilterModule;
import org.eventb.core.sc.IModuleManager;
import org.eventb.core.sc.state.IAbstractEventInfo;
import org.eventb.core.sc.state.IEventLabelSymbolTable;
import org.eventb.core.sc.state.IEventRefinesInfo;
import org.eventb.core.sc.state.ILabelSymbolTable;
import org.eventb.core.sc.state.ISCStateRepository;
import org.eventb.core.sc.symbolTable.ILabelSymbolInfo;
import org.eventb.core.sc.symbolTable.ISymbolInfo;
import org.eventb.core.sc.symbolTable.IVariableSymbolInfo;
import org.eventb.core.tool.state.IStateRepository;
import org.eventb.internal.core.sc.ModuleManager;
import org.eventb.internal.core.sc.symbolTable.GuardSymbolInfo;
import org.rodinp.core.IInternalElement;
import org.rodinp.core.IInternalParent;
import org.rodinp.core.IRodinElement;
import org.rodinp.core.RodinDBException;

/**
 * @author Stefan Hallerstede
 *
 */
public class MachineEventGuardModule extends PredicateWithTypingModule<IGuard> {

	public static final String MACHINE_EVENT_GUARD_FILTER = 
		EventBPlugin.PLUGIN_ID + ".machineEventGuardFilter";

	private ISCFilterModule[] filterModules;

	public MachineEventGuardModule() {
		IModuleManager manager = ModuleManager.getModuleManager();
		filterModules = manager.getFilterModules(MACHINE_EVENT_GUARD_FILTER);
	}

	private static String GUARD_NAME_PREFIX = "GRD";

	public void process(
			IRodinElement element, 
			IInternalParent target,
			ISCStateRepository repository,
			IProgressMonitor monitor)
			throws CoreException {
		
		if (formulaElements.size() == 0)
			return;

		if (checkInitialisation(element, monitor))
			checkAndType(
					target, 
					filterModules,
					element.getElementName(),
					repository,
					monitor);
		
		saveGuards((ISCEvent) target, monitor);

	}
	
	private boolean checkInitialisation(
			IRodinElement event, 
			IProgressMonitor monitor) throws RodinDBException {
		if (((ILabeledElement) event).getLabel().equals(IEvent.INITIALISATION))
			if (formulaElements.size() > 0) {
				for (IGuard guard : formulaElements)
					createProblemMarker(
							guard, 
							getFormulaAttributeType(), 
							GraphProblem.InitialisationGuardError);
				return false;
			}
		return true;
	}
	
	private void saveGuards(
			ISCEvent target, 
			IProgressMonitor monitor) throws RodinDBException {
		
		if (target == null)
			return;
		
		int index = 0;
		
		for (int i=0; i<formulaElements.size(); i++) {
			if (formulas.get(i) == null)
				continue;
			ISCGuard scGuard = target.getSCGuard(GUARD_NAME_PREFIX + index++);
			scGuard.create(null, monitor);
			scGuard.setLabel(formulaElements.get(i).getLabel(), monitor);
			scGuard.setPredicate(formulas.get(i), null);
			scGuard.setSource(formulaElements.get(i), monitor);
		}
	}

	protected IEventRefinesInfo refinedEventTable;
	
	/* (non-Javadoc)
	 * @see org.eventb.internal.core.sc.modules.LabeledFormulaModule#typeCheckFormula(int, org.rodinp.core.IInternalElement[], org.eventb.core.ast.Formula[], org.eventb.core.ast.ITypeEnvironment)
	 */
	@Override
	protected ITypeEnvironment typeCheckFormula(
			IGuard formulaElement, 
			Predicate formula, 
			ITypeEnvironment typeEnvironment) throws CoreException {
		
		// local variables must not change their type from an abstract machine
		// to a concrete machine
		
		ITypeEnvironment inferredTypeEnvironment =
			super.typeCheckFormula(formulaElement, formula, typeEnvironment);
		
		if (inferredTypeEnvironment == null)
			return null;
		
		boolean ok = true;
		
		ITypeEnvironment.IIterator iterator = inferredTypeEnvironment.getIterator();
		
		List<IAbstractEventInfo> eventInfos = refinedEventTable.getAbstractEventInfos();
		IAbstractEventInfo eventInfo = eventInfos.size() > 0 ? refinedEventTable.getAbstractEventInfos().get(0) : null;
			
		while (iterator.hasNext()) {
			iterator.advance();
			String name = iterator.getName();
			Type type = iterator.getType();
			
			if (eventInfo != null) {
			
				FreeIdentifier identifier = eventInfo.getIdentifier(name);
			
				if (identifier == null || identifier.getType().equals(type))
					continue;
			
				ok = false;
						
				createProblemMarker(
						formulaElement, 
						getFormulaAttributeType(), 
						GraphProblem.LocalVariableChangedTypeError, 
						name, type.toString(), identifier.getType().toString());
			}
		}
		
		return ok ? inferredTypeEnvironment : null;

	}

	/* (non-Javadoc)
	 * @see org.eventb.core.sc.Module#initModule(org.eventb.core.sc.IStateRepository, org.eclipse.core.runtime.IProgressMonitor)
	 */
	@Override
	public void initModule(
			IRodinElement element, 
			ISCStateRepository repository, 
			IProgressMonitor monitor) throws CoreException {
		super.initModule(element, repository, monitor);
		refinedEventTable = (IEventRefinesInfo) repository.getState(IEventRefinesInfo.STATE_TYPE);
	}

	/* (non-Javadoc)
	 * @see org.eventb.core.sc.Module#endModule(org.eventb.core.sc.IStateRepository, org.eclipse.core.runtime.IProgressMonitor)
	 */
	@Override
	public void endModule(
			IRodinElement element, 
			ISCStateRepository repository, 
			IProgressMonitor monitor) throws CoreException {
		super.endModule(element, repository, monitor);
		refinedEventTable = null;
	}

	@Override
	protected void makeProgress(IProgressMonitor monitor) {
		// no progress inside event
	}
	
	/* (non-Javadoc)
	 * @see org.eventb.internal.core.sc.modules.LabeledElementModule#getLabelSymbolTableFromRepository(org.eventb.core.sc.IStateRepository)
	 */
	@Override
	protected ILabelSymbolTable getLabelSymbolTableFromRepository(
			IStateRepository repository) throws CoreException {
		return (ILabelSymbolTable) repository.getState(IEventLabelSymbolTable.STATE_TYPE);
	}
	
	private boolean allLocalVariables(ITypeEnvironment typeEnvironment) {
		ITypeEnvironment.IIterator iterator = typeEnvironment.getIterator();
		while (iterator.hasNext()) {
			iterator.advance();
			ISymbolInfo symbolInfo = 
				identifierSymbolTable.getSymbolInfoFromTop(iterator.getName());
			if (symbolInfo instanceof IVariableSymbolInfo) {
				IVariableSymbolInfo variableSymbolInfo = (IVariableSymbolInfo) symbolInfo;
				if (variableSymbolInfo.isLocal())
					continue;
			}
			return false;
		}
		return true;
	}
	
	@Override
	protected boolean updateIdentifierSymbolTable(
			IInternalElement formulaElement, 
			ITypeEnvironment inferredEnvironment, 
			ITypeEnvironment typeEnvironment) throws CoreException {
		
		if (allLocalVariables(inferredEnvironment))
			return super.updateIdentifierSymbolTable(
					formulaElement, 
					inferredEnvironment, 
					typeEnvironment);
		else
			return false;

	}

	@Override
	protected ILabelSymbolInfo createLabelSymbolInfo(
			String symbol, ILabeledElement element, String component) throws CoreException {
		return new GuardSymbolInfo(symbol, element, EventBAttributes.LABEL_ATTRIBUTE, component);
	}

	@Override
	protected List<IGuard> getFormulaElements(IRodinElement element) throws CoreException {
		IEvent event = (IEvent) element;
		IGuard[] guards = event.getGuards();
		return Arrays.asList(guards);
	}

}

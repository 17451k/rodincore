/*******************************************************************************
 * Copyright (c) 2006 ETH Zurich.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.eventb.internal.core.pog.modules;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eventb.core.EventBPlugin;
import org.eventb.core.IPOFile;
import org.eventb.core.IPOSource;
import org.eventb.core.ISCAction;
import org.eventb.core.ISCEvent;
import org.eventb.core.ast.Assignment;
import org.eventb.core.ast.BecomesEqualTo;
import org.eventb.core.ast.Predicate;
import org.eventb.core.pog.IPOGPredicate;
import org.eventb.core.pog.POGCore;
import org.eventb.core.pog.state.IPOGStateRepository;
import org.eventb.core.tool.IModuleType;
import org.rodinp.core.IRodinElement;
import org.rodinp.core.RodinDBException;

/**
 * @author Stefan Hallerstede
 *
 */
public class FwdMachineEventActionBodySimModule extends
		MachineEventRefinementModule {
	
	public static final IModuleType<FwdMachineEventActionBodySimModule> MODULE_TYPE = 
		POGCore.getModuleType(EventBPlugin.PLUGIN_ID + ".fwdMachineEventActionBodySimModule"); //$NON-NLS-1$
	
	public IModuleType<?> getModuleType() {
		return MODULE_TYPE;
	}

	/* (non-Javadoc)
	 * @see org.eventb.core.pog.IProcessorModule#process(org.rodinp.core.IRodinElement, org.eventb.core.IPOFile, org.eventb.core.sc.IStateRepository, org.eclipse.core.runtime.IProgressMonitor)
	 */
	public void process(
			IRodinElement element, 
			IPOGStateRepository repository,
			IProgressMonitor monitor)
			throws CoreException {
		
		ISCEvent abstractEvent = abstractEventGuardList.getFirstAbstractEvent();
		
		// this POG module applies to refined events
		if (abstractEvent == null)
			return;
		
		createBodySimProofObligations(
				repository.getTarget(), 
				abstractEvent,
				monitor);

	}

	private void createBodySimProofObligations(
			IPOFile target, 
			ISCEvent abstractEvent, 
			IProgressMonitor monitor) throws RodinDBException {

		List<Assignment> simAssignments = 
			abstractEventActionTable.getSimAssignments();
		List<ISCAction> simActions = abstractEventActionTable.getSimActions();
		
		for (int i=0; i<simActions.size(); i++) {
			ISCAction action = simActions.get(i);
			String actionLabel = action.getLabel();
			Assignment simAssignment = simAssignments.get(i);
			
			if (abstractEventActionTable.getIndexOfCorrespondingConcrete(i) != -1)
				continue;
			
			Predicate simPredicate = simAssignment.getBAPredicate(factory);
			
			String sequentName = concreteEventLabel + "/" + actionLabel + "/SIM";
			
			if (goalIsTrivial(simPredicate)) {
				if (DEBUG_TRIVIAL)
					debugTraceTrivial(sequentName);
				continue;
			}
			
			LinkedList<BecomesEqualTo> substitution = new LinkedList<BecomesEqualTo>();
			substitution.addAll(witnessTable.getMachinePrimedDetAssignments());
			substitution.addAll(witnessTable.getEventDetAssignments());
			simPredicate = simPredicate.applyAssignments(substitution, factory);
			substitution.clear();
			if (concreteEventActionTable.getXiUnprime() != null)
				substitution.add(concreteEventActionTable.getXiUnprime());
			substitution.addAll(concreteEventActionTable.getPrimedDetAssignments());
			simPredicate = simPredicate.applyAssignments(substitution, factory);
			
			ArrayList<IPOGPredicate> hyp = makeActionAndWitnessHypothesis(simPredicate);
			
			createPO(
					target, 
					sequentName, 
					"Action simulation",
					fullHypothesis,
					hyp,
					makePredicate(simPredicate, action.getSource()),
					sources(
							makeSource(IPOSource.ABSTRACT_ROLE, abstractEvent.getSource()),
							makeSource(IPOSource.ABSTRACT_ROLE, action.getSource()),
							makeSource(IPOSource.CONCRETE_ROLE, concreteEvent.getSource())),
					hints(getLocalHypothesisSelectionHint(target, sequentName)),
					monitor);

		}
	}	

}

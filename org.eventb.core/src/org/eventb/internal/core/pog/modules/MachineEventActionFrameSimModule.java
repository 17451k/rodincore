/*******************************************************************************
 * Copyright (c) 2006 ETH Zurich.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.eventb.internal.core.pog.modules;

import java.util.ArrayList;
import java.util.Set;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eventb.core.IPOFile;
import org.eventb.core.ISCAction;
import org.eventb.core.ISCEvent;
import org.eventb.core.ast.Assignment;
import org.eventb.core.ast.BecomesEqualTo;
import org.eventb.core.ast.Formula;
import org.eventb.core.ast.FreeIdentifier;
import org.eventb.core.ast.Predicate;
import org.eventb.core.pog.POGPredicate;
import org.eventb.core.pog.POGSource;
import org.eventb.core.pog.state.IMachineVariableTable;
import org.eventb.core.pog.state.IStatePOG;
import org.eventb.core.state.IStateRepository;
import org.rodinp.core.IRodinElement;
import org.rodinp.core.RodinDBException;

/**
 * @author Stefan Hallerstede
 *
 */
public class MachineEventActionFrameSimModule extends MachineEventRefinementModule {

	protected ISCEvent abstractEvent;
	protected IMachineVariableTable machineVariableTable;
	
	public void process(
			IRodinElement element, 
			IPOFile target, 
			IStateRepository<IStatePOG> repository, 
			IProgressMonitor monitor) throws CoreException {
		
		createFrameSimProofObligations(
				target, 
				monitor);
	
	}
	
	private void createFrameSimProofObligations(
			IPOFile target, 
			IProgressMonitor monitor) throws RodinDBException {
		
		if (abstractEvent == null)
			return;
		
		ArrayList<POGPredicate> hyp = 
			new ArrayList<POGPredicate>(1);
		
		ArrayList<Assignment> nondetAssignments = 
			concreteEventActionTable.getNondetAssignments();
		ArrayList<ISCAction> nondetActions = 
			concreteEventActionTable.getNondetActions();
		ArrayList<BecomesEqualTo> detAssignments = 
			concreteEventActionTable.getDetAssignments();
		ArrayList<BecomesEqualTo> primedDetAssignments = 
			concreteEventActionTable.getPrimedDetAssignments();
		ArrayList<ISCAction> detActions = 
			concreteEventActionTable.getDetActions();
		
		Set<FreeIdentifier> abstractAssignedVariables = 
			abstractEventActionTable.getAssignedVariables();
		
		for (FreeIdentifier variable : machineVariableTable.getPreservedVariables()) {
			
			if(abstractAssignedVariables.contains(variable))
				continue;
			
			hyp.clear();
			
			Predicate predicate =
				factory.makeRelationalPredicate(Formula.EQUAL, 
						variable.withPrime(factory), 
						variable, null);
			
			IRodinElement source = null;
			
			int pos = findIndex(variable, nondetAssignments);
			
			if (pos >= 0) {
				hyp.add(
						new POGPredicate(nondetActions.get(pos),
								nondetAssignments.get(pos).getBAPredicate(factory)));
				source = nondetActions.get(pos);
			} else {
				pos = findIndex(variable, detAssignments);
				
				if (pos >= 0) {
					predicate = predicate.applyAssignment(primedDetAssignments.get(pos), factory);
					source = detActions.get(pos);
				} else
					continue;
			}
			
			createPO(
					target, 
					concreteEventLabel + "/" + variable.getName() + "/EQL", 
					"Equality " + (isInitialisation ? " establishment" : " preservation"),
					fullHypothesisName,
					hyp,
					new POGPredicate(source, predicate),
					sources(
							new POGSource("abstract event", abstractEvent),
							new POGSource("concrete event", concreteEvent)),
					emptyHints,
					monitor);

		}
	}

	private int findIndex(FreeIdentifier variable, ArrayList assignments) {
		int pos = -1;
		
		for (int i=0; i<assignments.size(); i++) {
			Assignment assignment = (Assignment) assignments.get(i);
			for (FreeIdentifier ident : assignment.getAssignedIdentifiers()) {
				if (variable.equals(ident)) {
					pos = i;
					break;
				}
			}
		}
		return pos;
	}
	

	@Override
	public void initModule(
			IRodinElement element, 
			IPOFile target, 
			IStateRepository<IStatePOG> repository, 
			IProgressMonitor monitor) throws CoreException {
		super.initModule(element, target, repository, monitor);
		abstractEvent = eventHypothesisManager.getFirstAbstractEvent();
		machineVariableTable =
			(IMachineVariableTable) repository.getState(IMachineVariableTable.STATE_TYPE);
	}

	@Override
	public void endModule(
			IRodinElement element, 
			IPOFile target, 
			IStateRepository<IStatePOG> repository, 
			IProgressMonitor monitor) throws CoreException {
		abstractEvent = null;
		machineVariableTable = null;
		super.endModule(element, target, repository, monitor);
	}

}

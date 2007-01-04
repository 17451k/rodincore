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
import org.eventb.core.IPOFile;
import org.eventb.core.IPOSource;
import org.eventb.core.ISCEvent;
import org.eventb.core.ISCGuard;
import org.eventb.core.ast.BecomesEqualTo;
import org.eventb.core.ast.Formula;
import org.eventb.core.ast.Predicate;
import org.eventb.core.pog.state.IAbstractEventGuardList;
import org.eventb.core.pog.state.IAbstractEventGuardTable;
import org.eventb.core.pog.state.IConcreteEventGuardTable;
import org.eventb.core.pog.state.IStateRepository;
import org.eventb.core.pog.util.POGPredicate;
import org.eventb.core.pog.util.POGSource;
import org.eventb.core.pog.util.POGTraceablePredicate;
import org.rodinp.core.IRodinElement;
import org.rodinp.core.RodinDBException;

/**
 * @author Stefan Hallerstede
 *
 */
public class MachineEventStrengthenGuardModule extends MachineEventRefinementModule {
	
	protected IConcreteEventGuardTable concreteEventGuardTable;

	/* (non-Javadoc)
	 * @see org.eventb.core.pog.IProcessorModule#process(org.rodinp.core.IRodinElement, org.eventb.core.IPOFile, org.eventb.core.sc.IStateRepository, org.eclipse.core.runtime.IProgressMonitor)
	 */
	public void process(
			IRodinElement element, 
			IStateRepository repository,
			IProgressMonitor monitor)
			throws CoreException {
		
		IPOFile target = repository.getTarget();
		
		if (abstractEventGuardList.getRefinementType() != IAbstractEventGuardList.MERGE) {
		
			ISCEvent abstractEvent = abstractEventGuardList.getFirstAbstractEvent();
			IAbstractEventGuardTable abstractEventGuardTable = 
				abstractEventGuardList.getFirstAbstractEventGuardTable();
			
			if (abstractEvent == null)
				return;
		
			createSplitProofObligation(
					target, 
					abstractEvent,
					abstractEventGuardTable,
					monitor);
		} else {
			
			createMergeProofObligation(target, monitor);
			
		}
	}

	private void createMergeProofObligation(
			IPOFile target, 
			IProgressMonitor monitor) throws RodinDBException {
		
		IAbstractEventGuardTable[] absGuardTables = 
			abstractEventGuardList.getAbstractEventGuardTables();
		
		String sequentName = concreteEventLabel + "/MRG";
		
		List<Predicate> disjPredList = 
			new ArrayList<Predicate>(absGuardTables.length);
		
		for (IAbstractEventGuardTable absGuardTable : absGuardTables) {
			
			Predicate[] absGuards = absGuardTable.getPredicates();
			
			if (absGuards.length == 0) {
				if (DEBUG_TRIVIAL)
					debugTraceTrivial(sequentName);
				return;
			}
			
			List<Predicate> conjPredList = new ArrayList<Predicate>(absGuards.length);
			
			for (int i=0; i<absGuards.length; i++) {
				Predicate absGuard = absGuards[i];
				boolean absGuardIsNew = 
					absGuardTable.getIndexOfCorrespondingConcrete(i) == -1;
				
				if (!goalIsTrivial(absGuard) && absGuardIsNew)
					conjPredList.add(absGuard);
			}
			
			if (conjPredList.size() > 0)
				disjPredList.add(
						conjPredList.size() == 1 ? conjPredList.get(0) :
						factory.makeAssociativePredicate(
								Formula.LAND, 
								conjPredList, null));
			else { // no proof obligation: one branch is true!
				if (DEBUG_TRIVIAL)
					debugTraceTrivial(sequentName);
				return;
			}
		}
		
		// disjPredList must have at least two elements
		// if the size was reduced the preceding loop waould have returned from this method
		
		Predicate disjPredicate = 
			factory.makeAssociativePredicate(
					Formula.LOR, 
					disjPredList, null);
		
		ArrayList<POGPredicate> hyp = makeActionHypothesis();
		hyp.addAll(makeWitnessHypothesis());

		disjPredicate = disjPredicate.applyAssignments(witnessTable.getEventDetAssignments(), factory);
		LinkedList<BecomesEqualTo> substitution = new LinkedList<BecomesEqualTo>();
		if (concreteEventActionTable.getXiUnprime() != null)
			substitution.add(concreteEventActionTable.getXiUnprime());
		substitution.addAll(concreteEventActionTable.getPrimedDetAssignments());
		disjPredicate = disjPredicate.applyAssignments(substitution, factory);
		
		ISCEvent[] absEvents = abstractEventGuardList.getAbstractEvents();
		
		List<POGSource> sourceList = new ArrayList<POGSource>(absEvents.length + 1);
		for (ISCEvent absEvent : absEvents)
			sourceList.add(new POGSource(IPOSource.ABSTRACT_ROLE, absEvent));
		sourceList.add(new POGSource(IPOSource.CONCRETE_ROLE, concreteEvent));
		
		POGSource[] sources = new POGSource[sourceList.size()];
		sourceList.toArray(sources);
	
		createPO(
				target, 
				sequentName, 
				"Guard strengthening (merge)",
				fullHypothesis,
				hyp,
				new POGTraceablePredicate(disjPredicate, concreteEvent),
				sources,
				hints(getLocalHypothesisSelectionHint(target, sequentName)),
				monitor);
	}

	private void createSplitProofObligation(
			IPOFile target, 
			ISCEvent abstractEvent, 
			IAbstractEventGuardTable abstractEventGuardTable,
			IProgressMonitor monitor) throws RodinDBException {
		
		ISCGuard[] absGuardElements = abstractEventGuardTable.getElements();
		Predicate[] absGuardPredicates = abstractEventGuardTable.getPredicates();
		
		ArrayList<POGPredicate> hyp = makeActionHypothesis();
		hyp.addAll(makeWitnessHypothesis());
		for (int i=0; i<absGuardElements.length; i++) {
			String guardLabel = absGuardElements[i].getLabel();
			Predicate absGuard = absGuardPredicates[i];
			String sequentName = concreteEventLabel + "/" + guardLabel + "/REF";
			
			if (goalIsTrivial(absGuard) 
					|| abstractEventGuardTable.getIndexOfCorrespondingConcrete(i) != -1) {
				if (DEBUG_TRIVIAL)
					debugTraceTrivial(sequentName);
				continue;
			}
			
			absGuard = absGuard.applyAssignments(witnessTable.getEventDetAssignments(), factory);
			LinkedList<BecomesEqualTo> substitution = new LinkedList<BecomesEqualTo>();
			if (concreteEventActionTable.getXiUnprime() != null)
				substitution.add(concreteEventActionTable.getXiUnprime());
			substitution.addAll(concreteEventActionTable.getPrimedDetAssignments());
			absGuard = absGuard.applyAssignments(substitution, factory);
			
			createPO(
					target, 
					sequentName, 
					"Guard strengthening (split)",
					fullHypothesis,
					hyp,
					new POGTraceablePredicate(absGuard, absGuardElements[i]),
					sources(
							new POGSource(IPOSource.ABSTRACT_ROLE, abstractEvent),
							new POGSource(IPOSource.ABSTRACT_ROLE, absGuardElements[i]),
							new POGSource(IPOSource.CONCRETE_ROLE, concreteEvent)),
					hints(getLocalHypothesisSelectionHint(target, sequentName)),
					monitor);
	
		}
	}
	
	/* (non-Javadoc)
	 * @see org.eventb.core.pog.ProcessorModule#initModule(org.rodinp.core.IRodinElement, org.eventb.core.IPOFile, org.eventb.core.sc.IStateRepository, org.eclipse.core.runtime.IProgressMonitor)
	 */
	@Override
	public void initModule(
			IRodinElement element, 
			IStateRepository repository, 
			IProgressMonitor monitor) throws CoreException {
		super.initModule(element, repository, monitor);
		concreteEventGuardTable = 
			(IConcreteEventGuardTable) repository.getState(IConcreteEventGuardTable.STATE_TYPE);
	}

	/* (non-Javadoc)
	 * @see org.eventb.core.pog.ProcessorModule#endModule(org.rodinp.core.IRodinElement, org.eventb.core.IPOFile, org.eventb.core.sc.IStateRepository, org.eclipse.core.runtime.IProgressMonitor)
	 */
	@Override
	public void endModule(
			IRodinElement element, 
			IStateRepository repository, 
			IProgressMonitor monitor) throws CoreException {
		concreteEventGuardTable = null;
		super.endModule(element, repository, monitor);
	}

}

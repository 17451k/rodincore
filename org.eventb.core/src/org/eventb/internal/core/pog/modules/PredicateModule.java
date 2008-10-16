/*******************************************************************************
 * Copyright (c) 2006, 2008 ETH Zurich and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     ETH Zurich - initial API and implementation
 *     Systerel - separation of file and root element
 *******************************************************************************/
package org.eventb.internal.core.pog.modules;

import java.util.List;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eventb.core.ILabeledElement;
import org.eventb.core.IPOPredicateSet;
import org.eventb.core.IPORoot;
import org.eventb.core.IPOSource;
import org.eventb.core.ISCPredicateElement;
import org.eventb.core.ITraceableElement;
import org.eventb.core.ast.Predicate;
import org.eventb.core.pog.IPOGHint;
import org.eventb.core.pog.IPOGSource;
import org.eventb.core.pog.state.IHypothesisManager;
import org.eventb.core.pog.state.IPOGStateRepository;
import org.eventb.core.pog.state.IPredicateTable;
import org.rodinp.core.IRodinElement;

/**
 * @author Stefan Hallerstede
 *
 */
public abstract class PredicateModule<PE extends ISCPredicateElement> extends UtilityModule {
	
	protected IPredicateTable<PE> predicateTable;
	protected IHypothesisManager hypothesisManager;

	@Override
	public void initModule(
			IRodinElement element, 
			IPOGStateRepository repository, 
			IProgressMonitor monitor) throws CoreException {
		super.initModule(element, repository, monitor);
		predicateTable = 
			getPredicateTable(repository);
		hypothesisManager = 
			getHypothesisManager(repository);
	}

	protected abstract IHypothesisManager getHypothesisManager(IPOGStateRepository repository) 
	throws CoreException;

	protected abstract IPredicateTable<PE> getPredicateTable(IPOGStateRepository repository) 
	throws CoreException;
	
	protected abstract boolean isAccurate();
	
	@Override
	public void endModule(IRodinElement element, IPOGStateRepository repository, IProgressMonitor monitor) throws CoreException {
		predicateTable = null;
		hypothesisManager = null;
		super.endModule(element, repository, monitor);
	}
	
	/* (non-Javadoc)
	 * @see org.eventb.core.pog.IModule#process(org.rodinp.core.IRodinElement, org.eventb.core.IPOFile, org.eventb.core.state.IStateRepository, org.eclipse.core.runtime.IProgressMonitor)
	 */
	public void process(IRodinElement element, IPOGStateRepository repository,
			IProgressMonitor monitor)
			throws CoreException {
		
		List<PE> elements = predicateTable.getElements();
		
		if(elements.size() == 0)
			return;
		
		IPORoot targetRoot = repository.getTarget();
		
		List<Predicate> predicates = predicateTable.getPredicates();
		
		for (int i=0; i<elements.size(); i++) {
			PE predicateElement = elements.get(i);
			String elementLabel = ((ILabeledElement) predicateElement).getLabel();
			
			Predicate predicate = predicates.get(i);
			
			createWDProofObligation(targetRoot, elementLabel, predicateElement, predicate, i, monitor);
			
			createProofObligation(targetRoot, elementLabel, predicateElement, predicate, monitor);

		}

	}

	protected void createProofObligation(
			IPORoot target, 
			String elementLabel, 
			PE predicateElement, 
			Predicate predicate, 
			IProgressMonitor monitor) throws CoreException {
		// create proof obligation (used for theorems)
	}

	protected void createWDProofObligation(
			IPORoot target, 
			String elementLabel, 
			PE predicateElement, 
			Predicate predicate, 
			int index,
			IProgressMonitor monitor) throws CoreException {
		Predicate wdPredicate = predicate.getWDPredicate(factory);
		if(!goalIsTrivial(wdPredicate)) {
			IPOPredicateSet hypothesis = hypothesisManager.makeHypothesis(predicateElement);
			IRodinElement predicateSource = ((ITraceableElement) predicateElement).getSource();
			createPO(
					target, 
					getWDProofObligationName(elementLabel), 
					getWDProofObligationDescription(),
					hypothesis,
					emptyPredicates,
					makePredicate(wdPredicate, predicateSource),
					new IPOGSource[] {
						makeSource(IPOSource.DEFAULT_ROLE, predicateSource)
					},
					new IPOGHint[] {
						makeIntervalSelectionHint(
								hypothesisManager.getRootHypothesis(), 
								hypothesis)
					},
					isAccurate(),
					monitor);
		} else {
			if (DEBUG_TRIVIAL)
				debugTraceTrivial(getWDProofObligationName(elementLabel));
		}
	}

	protected abstract String getWDProofObligationDescription();

	protected abstract String getWDProofObligationName(String elementLabel);

}

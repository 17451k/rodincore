/*******************************************************************************
 * Copyright (c) 2005, 2017 ETH Zurich and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     ETH Zurich - initial API and implementation
 *     Systerel - added unselected added hypotheses
 *******************************************************************************/
package org.eventb.core.basis;

import java.util.ArrayList;
import java.util.Set;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eventb.core.IPRHypAction;
import org.eventb.core.IPRRuleAntecedent;
import org.eventb.core.IProofStoreCollector;
import org.eventb.core.IProofStoreReader;
import org.eventb.core.ast.FreeIdentifier;
import org.eventb.core.ast.Predicate;
import org.eventb.core.seqprover.IHypAction;
import org.eventb.core.seqprover.IProofRule.IAntecedent;
import org.eventb.core.seqprover.ProverFactory;
import org.rodinp.core.IInternalElementType;
import org.rodinp.core.IRodinElement;
import org.rodinp.core.RodinDBException;

/**
 * @author Farhad Mehta
 *
 * @since 1.0
 * @noinstantiate This class is not intended to be instantiated by clients.
 */
public class PRRuleAntecedent extends EventBProofElement implements IPRRuleAntecedent {

	public PRRuleAntecedent(String name, IRodinElement parent) {
		super(name, parent);
	}

	@Override
	public IInternalElementType<IPRRuleAntecedent> getElementType() {
		return ELEMENT_TYPE;
	}
	
	@Override
	public IAntecedent getAntecedent(IProofStoreReader store) throws CoreException {

		Predicate goal = null;
		if (hasGoal()) goal = getGoal(store);
		
		// optional entries
		FreeIdentifier[] addedFreeIdents = null;
		Set<Predicate> addedHyps = null;
		Set<Predicate> unselAddedHyps = null;
		ArrayList<IHypAction> hypAction = null;

		addedFreeIdents = getFreeIdents(store.getFormulaFactory());
		
		
		if (hasHyps()) {
			addedHyps = getHyps(store);
		}
		if (hasUnselHyps()) {
			unselAddedHyps = getUnselHyps(store);
		}
		
		IRodinElement[] children = getChildrenOfType(IPRHypAction.ELEMENT_TYPE);
		if (children.length != 0)
		{
			hypAction = new ArrayList<IHypAction>(children.length);
			for (IRodinElement action : children) {
				hypAction.add(((IPRHypAction)action).getAction(store));				
			}
		}
		
		return ProverFactory.makeAntecedent(goal, addedHyps, unselAddedHyps,
				addedFreeIdents, hypAction);
	}


	@Override
	public void setAntecedent(IAntecedent antecedent,
			IProofStoreCollector store, IProgressMonitor monitor)
			throws RodinDBException {

		if (antecedent.getAddedFreeIdents().length != 0){
			setFreeIdents(antecedent.getAddedFreeIdents(), monitor);
		}
		
		if (! antecedent.getAddedHyps().isEmpty()){
			setHyps(antecedent.getAddedHyps(), store, monitor);
		}
		
		if (! antecedent.getUnselectedAddedHyps().isEmpty()){
			setUnselHyps(antecedent.getUnselectedAddedHyps(), store, monitor);
		}
		
		if (! antecedent.getHypActions().isEmpty()){
			int count = 0;
			for (IHypAction action : antecedent.getHypActions()) {
				final String name = action.getActionType() + count;
				IPRHypAction child = getInternalElement(
						IPRHypAction.ELEMENT_TYPE,
						name);
				child.create(null, null);
				child.setAction(action, store, null);
				count ++;
			}
		}
		
		if (antecedent.getGoal() != null) setGoal(antecedent.getGoal(), store, monitor);		
	}
}

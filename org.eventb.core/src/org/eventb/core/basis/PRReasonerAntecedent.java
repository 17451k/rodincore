/*******************************************************************************
 * Copyright (c) 2005 ETH Zurich.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.eventb.core.basis;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eventb.core.IPRHypAction;
import org.eventb.core.IPRReasonerAntecedent;
import org.eventb.core.IProofStoreCollector;
import org.eventb.core.IProofStoreReader;
import org.eventb.core.ast.FreeIdentifier;
import org.eventb.core.ast.Predicate;
import org.eventb.core.seqprover.ProverFactory;
import org.eventb.core.seqprover.HypothesesManagement.Action;
import org.eventb.core.seqprover.IProofRule.IAntecedent;
import org.rodinp.core.IInternalElementType;
import org.rodinp.core.IRodinElement;
import org.rodinp.core.RodinDBException;

/**
 * @author Farhad Mehta
 *
 */
public class PRReasonerAntecedent extends EventBProofElement implements IPRReasonerAntecedent {

	public PRReasonerAntecedent(String name, IRodinElement parent) {
		super(name, parent);
	}

	@Override
	public IInternalElementType getElementType() {
		return ELEMENT_TYPE;
	}
	
	public IAntecedent getAntecedent(IProofStoreReader store, IProgressMonitor monitor) throws RodinDBException {
		
		
		Predicate goal = getGoal(store, monitor);
		
		// optional entries
		FreeIdentifier[] addedFreeIdents = null;
		Set<Predicate> addedHyps = null;
		List<Action> hypAction = null;

		addedFreeIdents = getFreeIdents(store.getFormulaFactory(), monitor);
		
		
		if (hasHyps(monitor))
		addedHyps = getHyps(store, monitor);
		
		IRodinElement[] children = getChildrenOfType(IPRHypAction.ELEMENT_TYPE);
		if (children.length != 0)
		{
			hypAction = new ArrayList<Action>(children.length);
			for (IRodinElement action : children) {
				hypAction.add(((IPRHypAction)action).getAction(store, null));				
			}
		}
		
		return ProverFactory.makeAntecedent(goal,addedHyps,addedFreeIdents,hypAction);
	}


public void setAntecedent(IAntecedent antecedent, IProofStoreCollector store, IProgressMonitor monitor) throws RodinDBException {

		if (antecedent.getAddedFreeIdents().length != 0){
			setFreeIdents(antecedent.getAddedFreeIdents(), monitor);
//			((IPRTypeEnvironment)(this.createInternalElement(IPRTypeEnvironment.ELEMENT_TYPE,
//					"addedFreeIdents",
//					null,null))).setFreeIdents(antecedent.getAddedFreeIdents(), null);
		}
		
		if (! antecedent.getAddedHyps().isEmpty()){
			setHyps(antecedent.getAddedHyps(), store, monitor);
		}
		
		if (! antecedent.getHypAction().isEmpty()){
			int count = 0;
			for (Action action : antecedent.getHypAction()) {
				((IPRHypAction)(this.createInternalElement(IPRHypAction.ELEMENT_TYPE,
						action.getType().toString(),
						null,null))).setAction(action, store, null);
				count ++;
			}
		}
		
		setGoal(antecedent.getGoal(), store, monitor);
		
	}
}

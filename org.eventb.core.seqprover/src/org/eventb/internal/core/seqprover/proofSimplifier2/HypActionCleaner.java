/*******************************************************************************
 * Copyright (c) 2011 Systerel and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Systerel - initial API and implementation
 *******************************************************************************/
package org.eventb.internal.core.seqprover.proofSimplifier2;

import static org.eventb.core.seqprover.ProverFactory.makeAntecedent;
import static org.eventb.core.seqprover.ProverFactory.makeForwardInfHypAction;
import static org.eventb.core.seqprover.ProverFactory.makeProofRule;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.ListIterator;

import org.eventb.core.ast.Predicate;
import org.eventb.core.seqprover.IHypAction;
import org.eventb.core.seqprover.IHypAction.IForwardInfHypAction;
import org.eventb.core.seqprover.IHypAction.ISelectionHypAction;
import org.eventb.core.seqprover.IProofRule;
import org.eventb.core.seqprover.IProofRule.IAntecedent;
import org.eventb.core.seqprover.IProofTreeNode;
import org.eventb.core.seqprover.IProverSequent;
import org.eventb.internal.core.seqprover.IInternalHypAction;
import org.eventb.internal.core.seqprover.SelectionHypAction;

/**
 * @author Nicolas Beauger
 * 
 */
public class HypActionCleaner {
	
	private static IHypAction makeHypAction(IHypAction original, Collection<Predicate> newHyps) {
		if (original instanceof IForwardInfHypAction) {
			final IForwardInfHypAction fwd = (IForwardInfHypAction) original;
			return makeForwardInfHypAction(newHyps, fwd.getAddedFreeIdents(),
					fwd.getInferredHyps());
		} else if (original instanceof ISelectionHypAction) {
			return new SelectionHypAction(original.getActionType(), newHyps);
		} else { // unknown hyp action type
			assert false;
			return null;
		}
	}
	
	private static IHypAction cleanHypAction(IHypAction hypAction,
			IProverSequent sequent) {
		final IInternalHypAction hypAct = (IInternalHypAction) hypAction;

		final Collection<Predicate> original = hypAct.getHyps();
		final List<Predicate> clean = new ArrayList<Predicate>(hypAct.getHyps());
		final ListIterator<Predicate> iter = clean.listIterator();
		// must test hypotheses individually, using containsHypotheses(original)
		// would remove useful predicates
		while (iter.hasNext()) {
			final Predicate hyp = iter.next();
			if (!sequent.containsHypothesis(hyp)) {
				iter.remove();
			}
		}
		if (clean.isEmpty()) {
			return null;
		}
		if (clean.size() < original.size()) {
			return makeHypAction(hypAction, clean);
		}
		return hypAction;
	}
	
	private static IAntecedent cleanHypActions(IAntecedent antecedent,
			IProverSequent sequent) {
		final List<IHypAction> original = antecedent.getHypActions();
		final List<IHypAction> clean = new ArrayList<IHypAction>(original);

		final ListIterator<IHypAction> iter = clean.listIterator();
		while (iter.hasNext()) {
			final IHypAction hypAct = iter.next();
			final IHypAction cleanHypAct = cleanHypAction(hypAct, sequent);
			
			if (cleanHypAct == null) {
				iter.remove();
			} else {
				if (cleanHypAct != hypAct) {
					iter.set(cleanHypAct);
				}
			}
		}
		if (!clean.equals(original)) {
			return makeAntecedent(antecedent.getGoal(),
					antecedent.getAddedHyps(),
					antecedent.getUnselectedAddedHyps(),
					antecedent.getAddedFreeIdents(), clean);
		} else {
			return antecedent;
		}

	}
	
	/**
	 * Returns a rule with unused hypothesis actions removed, or the given
	 * rule if no change occurred.
	 * 
	 * @param node
	 *            a proof tree node with a rule
	 * @return a new DependRule
	 */
	public static IProofRule cleanHypActions(IProofTreeNode node) {
		final IProofRule rule = node.getRule();
		final IProverSequent sequent = node.getSequent();
		final IAntecedent[] antecedents = rule.getAntecedents();
		final IAntecedent[] newAntecedents = new IAntecedent[antecedents.length];

		for (int i = 0; i < antecedents.length; i++) {
			newAntecedents[i] = cleanHypActions(antecedents[i], sequent);
		}
		if (!Arrays.equals(newAntecedents, antecedents)) {
			return makeProofRule(rule.getReasonerDesc(), rule.generatedUsing(),
					rule.getGoal(), rule.getNeededHyps(), rule.getConfidence(),
					rule.getDisplayName(), newAntecedents);
		}
		return rule;
	}

}

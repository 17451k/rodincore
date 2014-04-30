/*******************************************************************************
 * Copyright (c) 2006, 2014 ETH Zurich and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     ETH Zurich - initial API and implementation
 *******************************************************************************/
package org.eventb.internal.core.seqprover;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Set;

import org.eventb.core.ast.FormulaFactory;
import org.eventb.core.ast.Predicate;
import org.eventb.core.seqprover.IHypAction;
import org.eventb.core.seqprover.IHypAction.ISelectionHypAction;

public class SelectionHypAction implements IInternalHypAction, ISelectionHypAction {

	private final String actionType;
	private final Collection<Predicate> hyps;

	private static final Set<Predicate> NO_HYPS = Collections.emptySet();
	
	/**
	 * @param actionType
	 * 			Must be the same as the action types defined in {@link ISelectionHypAction}
	 * @param hyps
	 */
	public SelectionHypAction(final String actionType, final Collection<Predicate> hyps) {
		super();
		assert hyps != null;
		this.actionType = actionType;
		this.hyps = hyps == null ? NO_HYPS : new ArrayList<Predicate>(hyps);
	}

	@Override
	public Collection<Predicate> getHyps() {
		return hyps;
	}

	@Override
	public IInternalProverSequent perform(IInternalProverSequent seq) {
		if (actionType.equals(ISelectionHypAction.SELECT_ACTION_TYPE))
			return seq.selectHypotheses(hyps);
		if (actionType.equals(ISelectionHypAction.DESELECT_ACTION_TYPE))
			return seq.deselectHypotheses(hyps);
		if (actionType.equals(ISelectionHypAction.HIDE_ACTION_TYPE))
			return seq.hideHypotheses(hyps);
		if (actionType.equals(ISelectionHypAction.SHOW_ACTION_TYPE))
			return seq.showHypotheses(hyps);
		return seq;
	}

	@Override
	public String getActionType() {
		return actionType;
	}

	@Override
	public void processDependencies(ProofDependenciesBuilder proofDeps) {
		// Nothing to do
	}

	@Override
	public IHypAction translate(FormulaFactory factory) {
		final Collection<Predicate> trHyps = new ArrayList<Predicate>(hyps.size());
		for (Predicate hyp : hyps) {
			trHyps.add(hyp.translate(factory));
		}
		return new SelectionHypAction(actionType, trHyps);
	}	

}

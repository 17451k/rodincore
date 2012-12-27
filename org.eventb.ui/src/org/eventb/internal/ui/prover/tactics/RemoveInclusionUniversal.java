/*******************************************************************************
 * Copyright (c) 2007, 2012 ETH Zurich and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     ETH Zurich - initial API and implementation
 *******************************************************************************/
package org.eventb.internal.ui.prover.tactics;

import java.util.List;

import org.eventb.core.ast.IAccumulator;
import org.eventb.core.ast.IPosition;
import org.eventb.core.ast.Predicate;
import org.eventb.core.ast.RelationalPredicate;
import org.eventb.core.seqprover.IProofTreeNode;
import org.eventb.core.seqprover.ITactic;
import org.eventb.core.seqprover.eventbExtensions.Tactics;
import org.eventb.ui.prover.DefaultTacticProvider.DefaultPositionApplication;
import org.eventb.ui.prover.ITacticApplication;

/**
 * Provider for the "remove ⊆ (universal)" tactic.
 * <ul>
 * <li>Provider ID : <code>org.eventb.ui.riUniv</code></li>
 * <li>Target : any</li>
 * <ul>
 */
public class RemoveInclusionUniversal extends AbstractHypGoalTacticProvider {

	public static class RemoveInclusionUniversalApplication extends
			DefaultPositionApplication {

		public RemoveInclusionUniversalApplication(Predicate hyp,
				IPosition position) {
			super(hyp, position);
		}

		@Override
		public ITactic getTactic(String[] inputs, String globalInput) {
			return Tactics.removeInclusionUniversal(hyp, position);
		}

		@Override
		public String getTacticID() {
			return "org.eventb.ui.riUniv";
		}

	}

	public static class RemoveInclusionUniversalAppliInspector extends
			DefaultApplicationInspector {

		public RemoveInclusionUniversalAppliInspector(Predicate hyp) {
			super(hyp);
		}

		@Override
		public void inspect(RelationalPredicate predicate,
				IAccumulator<ITacticApplication> accumulator) {
			if (predicate.getTag() == Predicate.SUBSETEQ) {
				final IPosition position = accumulator.getCurrentPosition();
				accumulator.add(new RemoveInclusionUniversalApplication(
						hyp, position));
			}
		}

	}

	@Override
	protected List<ITacticApplication> getApplicationsOnPredicate(
			IProofTreeNode node, Predicate hyp, String globalInput,
			Predicate predicate) {
		return predicate
				.inspect(new RemoveInclusionUniversalAppliInspector(hyp));
	}

}

/*******************************************************************************
 * Copyright (c) 2009, 2014 Systerel and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Systerel - initial API and implementation
 *******************************************************************************/
package org.eventb.internal.core.seqprover.eventbExtensions;

import static java.util.Arrays.asList;
import static java.util.Collections.singleton;
import static org.eventb.core.seqprover.ProverFactory.makeAntecedent;
import static org.eventb.core.seqprover.ProverFactory.makeHideHypAction;
import static org.eventb.core.seqprover.ProverFactory.makeRewriteHypAction;

import java.util.Set;

import org.eventb.core.ast.Expression;
import org.eventb.core.ast.Formula;
import org.eventb.core.ast.Predicate;
import org.eventb.core.ast.QuantifiedPredicate;
import org.eventb.core.seqprover.IHypAction;
import org.eventb.core.seqprover.IProofRule.IAntecedent;
import org.eventb.core.seqprover.IProverSequent;
import org.eventb.core.seqprover.IVersionedReasoner;
import org.eventb.core.seqprover.ProverRule;
import org.eventb.core.seqprover.SequentProver;
import org.eventb.core.seqprover.eventbExtensions.DLib;
import org.eventb.core.seqprover.reasonerInputs.HypothesisReasoner;

/**
 * Generates a proof rule for one point rules ONE_POINT_L and ONE_POINT_R.
 * 
 * @author Nicolas Beauger
 * @author Benoît Lucet
 */
public class OnePointRule extends HypothesisReasoner implements
		IVersionedReasoner {

	// NB: One Point Rule is used by AutoRewrites; thus, modifications here also
	// affect it => don't forget to upgrade its version at the same time !
	private static final int REASONER_VERSION = 2;

	public static final String REASONER_ID = SequentProver.PLUGIN_ID
			+ ".onePointRule";

	@Override
	public String getReasonerID() {
		return REASONER_ID;
	}

	@Override
	public int getVersion() {
		return REASONER_VERSION;
	}

	@Override
	protected String getDisplay(Predicate pred) {
		return "One Point Rule in " + (pred == null ? "goal" : pred);
	}

	public static boolean isApplicable(Formula<?> formula) {
		if (!(formula instanceof QuantifiedPredicate)) {
			return false;
		}

		final OnePointProcessorInference matcher = new OnePointProcessorInference(
				(QuantifiedPredicate) formula);
		matcher.matchAndInstantiate();
		return matcher.wasSuccessfullyApplied();
	}

	@Override
	protected boolean isGoalDependent(IProverSequent sequent, Predicate pred) {
		return pred == null;
	}

	@ProverRule({ "ONE_POINT_L", "ONE_POINT_R" })
	@Override
	protected IAntecedent[] getAntecedents(IProverSequent sequent,
			Predicate pred) {

		final boolean appliesToGoal = isGoalDependent(sequent, pred);
		final Predicate applyTo = appliesToGoal ? sequent.goal() : pred;

		if (!(applyTo instanceof QuantifiedPredicate)) {
			throw new IllegalArgumentException(
					"One point rule applied to not quantified predicate "
							+ applyTo);
		}
		
		final OnePointProcessorInference processor = new OnePointProcessorInference(
				(QuantifiedPredicate) applyTo);
		processor.matchAndInstantiate();

		if (!processor.wasSuccessfullyApplied()) {
			throw new IllegalArgumentException(
					"One point processing unsuccessful for predicate "
							+ applyTo);
		}

		final Predicate simplified = processor.getProcessedResult();
		final Expression replacement = processor.getReplacement();

		final Predicate replacementWD = DLib.WD(replacement);

		// There will be 2 antecedents
		final IAntecedent a1;
		final IAntecedent a2;
		if (appliesToGoal) {
			a1 = makeAntecedent(simplified);
			a2 = makeAntecedent(replacementWD);
		} else {
			final Set<Predicate> applyToHyp = singleton(applyTo);
			final IHypAction rewrite = makeRewriteHypAction(applyToHyp,
					singleton(simplified), applyToHyp);
			final IHypAction hideHyp = makeHideHypAction(applyToHyp);
			a1 = makeAntecedent(null, null, null, asList(rewrite));
			a2 = makeAntecedent(replacementWD, null, hideHyp);
		}
		return new IAntecedent[] { a1, a2 };
	}

}

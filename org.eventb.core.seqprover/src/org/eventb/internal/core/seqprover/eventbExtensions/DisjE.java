package org.eventb.internal.core.seqprover.eventbExtensions;

import java.util.Arrays;

import org.eventb.core.ast.Predicate;
import org.eventb.core.seqprover.IHypAction;
import org.eventb.core.seqprover.IProverSequent;
import org.eventb.core.seqprover.ProverFactory;
import org.eventb.core.seqprover.SequentProver;
import org.eventb.core.seqprover.IProofRule.IAntecedent;
import org.eventb.core.seqprover.eventbExtensions.Lib;
import org.eventb.core.seqprover.reasonerInputs.HypothesisReasoner;

public class DisjE extends HypothesisReasoner {
	
	public static String REASONER_ID = SequentProver.PLUGIN_ID + ".disjE";
	
	public String getReasonerID() {
		return REASONER_ID;
	}

	@Override
	protected IAntecedent[] getAntecedents(IProverSequent sequent,
			Predicate pred) throws IllegalArgumentException {

		if (pred == null) {
			throw new IllegalArgumentException("Null hypothesis");
		}
		if (!Lib.isDisj(pred)) {
			throw new IllegalArgumentException(
					"Hypothesis is not a disjunction: " + pred);
		}

		final Predicate[] disjuncts = Lib.disjuncts(pred);

		final int length = disjuncts.length;
		final IAntecedent[] antecedents = new IAntecedent[length];
		final Predicate goal = sequent.goal();
		final IHypAction action = ProverFactory.makeDeselectHypAction(Arrays.asList(pred));
		for (int i = 0; i < length; i++) {
			antecedents[i] = ProverFactory.makeAntecedent(goal, Lib
					.breakPossibleConjunct(disjuncts[i]), action);
		}
		return antecedents;
	}

	@Override
	protected String getDisplay(Predicate pred) {
		return "∨ hyp (" + pred + ")";
	}

}

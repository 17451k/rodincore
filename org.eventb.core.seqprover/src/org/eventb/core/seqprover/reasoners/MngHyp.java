package org.eventb.core.seqprover.reasoners;

import java.util.List;
import java.util.Set;

import org.eventb.core.ast.Predicate;
import org.eventb.core.seqprover.Hypothesis;
import org.eventb.core.seqprover.IProofMonitor;
import org.eventb.core.seqprover.IProofRule;
import org.eventb.core.seqprover.IProverSequent;
import org.eventb.core.seqprover.IReasoner;
import org.eventb.core.seqprover.IReasonerInput;
import org.eventb.core.seqprover.IReasonerInputReader;
import org.eventb.core.seqprover.IReasonerInputWriter;
import org.eventb.core.seqprover.IReasonerOutput;
import org.eventb.core.seqprover.ProverFactory;
import org.eventb.core.seqprover.SequentProver;
import org.eventb.core.seqprover.SerializeException;
import org.eventb.core.seqprover.HypothesesManagement.Action;
import org.eventb.core.seqprover.HypothesesManagement.ActionType;
import org.eventb.core.seqprover.IProofRule.IAntecedent;
import org.eventb.core.seqprover.proofBuilder.ReplayHints;

public class MngHyp implements IReasoner {

	public static String REASONER_ID = SequentProver.PLUGIN_ID + ".mngHyp";

	public static class Input implements IReasonerInput {

		Action action;

		public Input(ActionType type, Set<Hypothesis> hyps) {
			this.action = new Action(type, hyps);
		}

		public Input(Action action) {
			this.action = action;
		}

		// TODO share this with Review reasoner input
		public void applyHints(ReplayHints hints) {

			final ActionType type = action.getType();
			final Set<Hypothesis> hyps = action.getHyps();
			Predicate[] newPreds = new Predicate[hyps.size()];
			int i = 0;
			for (Hypothesis hyp : hyps) {
				newPreds[i++] = hints.applyHints(hyp.getPredicate());
			}
			action = new Action(type, Hypothesis.Hypotheses(newPreds));
		}

		public String getError() {
			return null;
		}

		public boolean hasError() {
			return false;
		}

	}

	public String getReasonerID() {
		return REASONER_ID;
	}

	public void serializeInput(IReasonerInput rInput, IReasonerInputWriter writer) throws SerializeException {
		// Nothing to serialize, all is in the rule
	}
	
	public IReasonerInput deserializeInput(IReasonerInputReader reader)
			throws SerializeException {

		final IAntecedent[] antecedents = reader.getAntecedents();
		if (antecedents.length != 1) {
			throw new SerializeException(new IllegalStateException(
					"Two many antecedents in the rule"));
		}
		final List<Action> actions = antecedents[0].getHypAction();
		if (actions.size() != 1) {
			throw new SerializeException(new IllegalStateException(
					"Two many actions in the rule antecedent"));
		}
		return new Input(actions.get(0));
	}

	public IReasonerOutput apply(IProverSequent seq,
			IReasonerInput reasonerInput, IProofMonitor pm) {

		Input input = (Input) reasonerInput;
		Action action = input.action;
		IAntecedent antecedent = ProverFactory.makeAntecedent(seq.goal(), null,
				action);
		IProofRule reasonerOutput = ProverFactory.makeProofRule(this, input,
				seq.goal(), "sl/ds", antecedent);
		return reasonerOutput;
	}

}

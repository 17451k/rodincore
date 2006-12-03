package org.eventb.core.seqprover.reasoners;

import java.util.Set;

import org.eventb.core.ast.Predicate;
import org.eventb.core.seqprover.Hypothesis;
import org.eventb.core.seqprover.IConfidence;
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
import org.eventb.core.seqprover.IProofRule.IAntecedent;
import org.eventb.core.seqprover.proofBuilder.ReplayHints;

public class Review implements IReasoner{
	
	public static String REASONER_ID = SequentProver.PLUGIN_ID + ".review";
	
	public static class Input implements IReasonerInput {

		Set<Hypothesis> hyps;
		Predicate goal;
		int confidence;

		// TODO add check on confidence parameter
		public Input(IProverSequent sequent, int confidence) {
			this.hyps = sequent.selectedHypotheses();
			this.goal = sequent.goal();
			this.confidence = confidence;
		}
		
		public Input(Set<Hypothesis> hyps, Predicate goal, int confidence) {
			this.hyps = hyps;
			this.goal = goal;
			this.confidence = confidence;
		}

		public void applyHints(ReplayHints hints) {
			Predicate[] newPreds = new Predicate[hyps.size()];
			int i = 0;
			for (Hypothesis hyp: hyps) {
				newPreds[i++] = hints.applyHints(hyp.getPredicate());
			}
			hyps = Hypothesis.Hypotheses(newPreds);
			goal = hints.applyHints(goal);
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
	
	public void serializeInput(IReasonerInput input, IReasonerInputWriter writer) {
		// Nothing to serialize, all is in the rule.
	}
	
	public IReasonerInput deserializeInput(IReasonerInputReader reader)
			throws SerializeException {
		
		return new Input(
				reader.getNeededHyps(),
				reader.getGoal(),
				reader.getConfidence()
		);
	}
	
	public IReasonerOutput apply(IProverSequent seq,
			IReasonerInput reasonerInput, IProofMonitor pm) {
	
		// Organize Input
		Input input = (Input) reasonerInput;
		
		Set<Hypothesis> hyps = input.hyps;
		Predicate goal = input.goal;
		int reviewerConfidence = input.confidence;
		
		if ((! (seq.goal().equals(goal))) ||
		   (! (seq.hypotheses().containsAll(hyps)))) {
			return ProverFactory.reasonerFailure(this, input,
					"Reviewed sequent does not match");
		}
		
		assert reviewerConfidence > 0;
		assert reviewerConfidence <= IConfidence.REVIEWED_MAX;
	
		IProofRule reasonerOutput = ProverFactory.makeProofRule(
				this,input,
				seq.goal(),
				hyps,
				reviewerConfidence,
				"rv (confidence "+reviewerConfidence+")",
				new IAntecedent[0]);		
		
		return reasonerOutput;
	}
	
}

package org.eventb.internal.core.seqprover.eventbExtensions;

import java.util.Set;

import org.eventb.core.ast.Predicate;
import org.eventb.core.seqprover.Hypothesis;
import org.eventb.core.seqprover.IProofMonitor;
import org.eventb.core.seqprover.IProofRule;
import org.eventb.core.seqprover.IProverSequent;
import org.eventb.core.seqprover.IReasonerInput;
import org.eventb.core.seqprover.IReasonerOutput;
import org.eventb.core.seqprover.ProverFactory;
import org.eventb.core.seqprover.ProverLib;
import org.eventb.core.seqprover.SequentProver;
import org.eventb.core.seqprover.IProofRule.IAntecedent;
import org.eventb.core.seqprover.eventbExtensions.Lib;
import org.eventb.core.seqprover.reasonerInputs.SinglePredInput;
import org.eventb.core.seqprover.reasonerInputs.SinglePredInputReasoner;

public class ImpE extends SinglePredInputReasoner{
	
	public static String REASONER_ID = SequentProver.PLUGIN_ID + ".impE";
	
	public String getReasonerID() {
		return REASONER_ID;
	}
	
	public IReasonerOutput apply(IProverSequent seq,IReasonerInput reasonerInput, IProofMonitor pm){
		
		SinglePredInput input = (SinglePredInput) reasonerInput;
		
		Predicate impHypPred = input.getPredicate();
		Hypothesis impHyp = new Hypothesis(impHypPred);
		
		
		if (! seq.hypotheses().contains(impHyp))
			return ProverFactory.reasonerFailure(this,input,
					"Nonexistent hypothesis:"+impHyp);
		if (! Lib.isImp(impHypPred))
			return ProverFactory.reasonerFailure(this,input,
					"Hypothesis is not an implication:"+impHyp);
		
		// Generate the anticident
		Predicate toAssume = Lib.impRight(impHypPred);
		Predicate toShow = Lib.impLeft(impHypPred);
		IAntecedent[] anticidents = new IAntecedent[2];
		
		anticidents[0] = ProverFactory.makeAntecedent(toShow);
		
		Set<Predicate> addedHyps = Lib.breakPossibleConjunct(toAssume);
		addedHyps.addAll(Lib.breakPossibleConjunct(toShow));
		anticidents[1] = ProverFactory.makeAntecedent(
				seq.goal(),
				addedHyps,
				ProverLib.deselect(impHyp));
		
		// Generate the successful reasoner output
		IProofRule reasonerOutput = ProverFactory.makeProofRule(
				this,input,
				seq.goal(),
				impHyp,
				"⇒ hyp ("+impHyp+")",
				anticidents);
		
//		// Generate the successful reasoner output
//		ProofRule reasonerOutput = new ProofRule(this,input);
//		reasonerOutput.display = "⇒ hyp ("+impHyp+")";
//		reasonerOutput.neededHypotheses.add(impHyp);
//		reasonerOutput.goal = seq.goal();
//
//		// Generate the anticident
//		Predicate toAssume = Lib.impRight(impHypPred);
//		Predicate toShow = Lib.impLeft(impHypPred);
//		reasonerOutput.anticidents = new Antecedent[2];
//		
//		reasonerOutput.anticidents[0] = new Antecedent();
//		reasonerOutput.anticidents[0].goal = toShow;
//		
//		reasonerOutput.anticidents[1] = new Antecedent();
//		reasonerOutput.anticidents[1].addConjunctsToAddedHyps(toShow);
//		reasonerOutput.anticidents[1].addConjunctsToAddedHyps(toAssume);
//		reasonerOutput.anticidents[1].hypAction.add(Lib.deselect(impHyp));
//		reasonerOutput.anticidents[1].goal = seq.goal();
		
		return reasonerOutput;
	}

}

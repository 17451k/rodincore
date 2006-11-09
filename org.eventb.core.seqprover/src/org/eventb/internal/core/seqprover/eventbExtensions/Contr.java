package org.eventb.internal.core.seqprover.eventbExtensions;

import java.util.HashSet;
import java.util.Set;

import org.eventb.core.ast.Predicate;
import org.eventb.core.seqprover.Hypothesis;
import org.eventb.core.seqprover.IProofMonitor;
import org.eventb.core.seqprover.IProofRule;
import org.eventb.core.seqprover.IProverSequent;
import org.eventb.core.seqprover.IReasonerInput;
import org.eventb.core.seqprover.IReasonerOutput;
import org.eventb.core.seqprover.ProverFactory;
import org.eventb.core.seqprover.SequentProver;
import org.eventb.core.seqprover.IProofRule.IAntecedent;
import org.eventb.core.seqprover.eventbExtensions.Lib;
import org.eventb.core.seqprover.reasonerInputs.SinglePredInput;
import org.eventb.core.seqprover.reasonerInputs.SinglePredInputReasoner;

public class Contr extends SinglePredInputReasoner{
	
	public static String REASONER_ID = SequentProver.PLUGIN_ID + ".contr";
	
	public String getReasonerID() {
		return REASONER_ID;
	}
	
	public IReasonerOutput apply(IProverSequent seq,IReasonerInput reasonerInput, IProofMonitor pm){
		
		SinglePredInput input = (SinglePredInput) reasonerInput;
		
		if (input.hasError())
			return ProverFactory.reasonerFailure(this,reasonerInput,input.getError());
		
		Predicate falseHypPred = input.getPredicate();
		Hypothesis falseHyp = new Hypothesis(falseHypPred);
		
		if ((!falseHypPred.equals(Lib.True)) && (! seq.hypotheses().contains(falseHyp)))
		return ProverFactory.reasonerFailure(this,input,
					"Nonexistent hypothesis:"+falseHyp);

		// Generate the successful reasoner output
		
		Predicate goal;
		String display;
		Set<Hypothesis> neededHypotheses = new HashSet<Hypothesis>();
		
		if (falseHypPred.equals(Lib.True))
		{
			goal = Lib.False;
			display = "ct goal";
		}
		else
		{
			goal = Lib.makeNeg(falseHypPred);
			display = "ct hyp ("+falseHyp+")";
			neededHypotheses.add(falseHyp);
		}
			
		IAntecedent[] anticidents = new IAntecedent[1];
		anticidents[0] = ProverFactory.makeAntecedent(
				goal,
				Lib.breakPossibleConjunct(Lib.makeNeg(seq.goal())),
				null);
		
		IProofRule reasonerOutput = ProverFactory.makeProofRule(
				this,input,
				seq.goal(),
				neededHypotheses,
				null,
				display,
				anticidents);
		
//		ProofRule reasonerOutput = new ProofRule(this,input);
//		String display;
//		
//		if (falseHypPred.equals(Lib.True))
//		{
//			display = "ct goal";
//		}
//		else 
//		{
//			display = "ct hyp ("+falseHyp+")";
//			reasonerOutput.neededHypotheses.add(falseHyp);
//		}
//		reasonerOutput.goal = seq.goal();
//
//		// Generate the anticident
//		reasonerOutput.anticidents = new Antecedent[1];
//		
//		Predicate goal;
//		if (falseHypPred.equals(Lib.True))
//			goal = Lib.False;
//		else
//			goal = Lib.makeNeg(falseHypPred);
//		
//		reasonerOutput.anticidents[0] = new Antecedent(goal);		
//		reasonerOutput.anticidents[0].addToAddedHyps(Lib.makeNeg(seq.goal()));
		return reasonerOutput;
	}

}

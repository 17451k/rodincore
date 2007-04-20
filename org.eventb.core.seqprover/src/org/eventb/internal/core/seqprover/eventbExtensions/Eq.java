package org.eventb.internal.core.seqprover.eventbExtensions;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import org.eventb.core.ast.Expression;
import org.eventb.core.ast.Predicate;
import org.eventb.core.seqprover.IHypAction;
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

// TODO : take input from the genearted rule (maybe, since it removes backward compatability)
// TODO : make this a hypothesis reasoner
public class Eq extends SinglePredInputReasoner{
	
	public static String REASONER_ID = SequentProver.PLUGIN_ID + ".eq";
	
	public String getReasonerID() {
		return REASONER_ID;
	}
	
	public IReasonerOutput apply(IProverSequent seq,IReasonerInput reasonerInput, IProofMonitor pm){
		
		SinglePredInput input = (SinglePredInput) reasonerInput;

		// Predicate eqHypPred = input.getPredicate();
		Predicate eqHyp = input.getPredicate();
		
		if (! seq.containsHypothesis(eqHyp))
		return ProverFactory.reasonerFailure(this,input,
					"Nonexistent hypothesis:"+eqHyp);
		if (! Lib.isEq(eqHyp))
			return ProverFactory.reasonerFailure(this,input,
					"Hypothesis is not an equality:"+eqHyp);
		
		Expression from;
		Expression to;
		 
		from = Lib.eqLeft(eqHyp);
		to = Lib.eqRight(eqHyp);
		
		// This code is common to the reasoners Eq and He
		
		List<IHypAction> rewrites = new ArrayList<IHypAction>();
		Set<Predicate> toDeselect = new LinkedHashSet<Predicate>();
		Set<Predicate> toSelect = new LinkedHashSet<Predicate>();
		toDeselect.add(eqHyp);
		
		for (Predicate shyp : seq.selectedHypIterable()){
			if (!shyp.equals(eqHyp)) {
				Predicate rewritten = (Lib.rewrite(shyp,from,to));
				if (rewritten != shyp)
				{
					rewrites.add(ProverFactory.makeForwardInfHypAction(
							Collections.singleton(shyp),
							Collections.singleton(rewritten)));
					toDeselect.add(shyp);
					toSelect.add(rewritten);
				}
			}
		}

		Predicate rewrittenGoal = Lib.rewrite(seq.goal(),from,to);
		
		if (rewrittenGoal == seq.goal() && rewrites.isEmpty())
			return ProverFactory.reasonerFailure(this,input,
					"Nothing to do");

		Predicate goalDep = seq.goal();
		Predicate newGoal = rewrittenGoal;
		
		// remove goal dependency if goal is not rewritten
		if (rewrittenGoal == seq.goal()){
			goalDep = null;
			newGoal = null;
		}
		rewrites.add(ProverFactory.makeDeselectHypAction(toDeselect));
		rewrites.add(ProverFactory.makeSelectHypAction(toSelect));
		IAntecedent[] anticidents = new IAntecedent[1];
		anticidents[0] = ProverFactory.makeAntecedent(
				newGoal,null,null,
				rewrites);
		
		IProofRule reasonerOutput = ProverFactory.makeProofRule(
		this,input,
		goalDep,
		Collections.singleton(eqHyp),
		null,
		"eh ("+eqHyp+")",
		anticidents);

		return reasonerOutput;
		
	}

}

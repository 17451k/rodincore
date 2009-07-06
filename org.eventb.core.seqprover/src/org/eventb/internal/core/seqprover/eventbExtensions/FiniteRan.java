package org.eventb.internal.core.seqprover.eventbExtensions;

import org.eventb.core.ast.Expression;
import org.eventb.core.ast.FormulaFactory;
import org.eventb.core.ast.Predicate;
import org.eventb.core.ast.SimplePredicate;
import org.eventb.core.ast.UnaryExpression;
import org.eventb.core.seqprover.IProofMonitor;
import org.eventb.core.seqprover.IProverSequent;
import org.eventb.core.seqprover.IReasonerInput;
import org.eventb.core.seqprover.IReasonerOutput;
import org.eventb.core.seqprover.ProverFactory;
import org.eventb.core.seqprover.ProverRule;
import org.eventb.core.seqprover.SequentProver;
import org.eventb.core.seqprover.IProofRule.IAntecedent;
import org.eventb.core.seqprover.eventbExtensions.Lib;
import org.eventb.core.seqprover.reasonerInputs.EmptyInputReasoner;

public class FiniteRan extends EmptyInputReasoner {

	public static String REASONER_ID = SequentProver.PLUGIN_ID + ".finiteRan";

	private static FormulaFactory ff = FormulaFactory.getDefault();
	
	public String getReasonerID() {
		return REASONER_ID;
	}

	@ProverRule("FIN_REL_RAN_R")
	protected IAntecedent[] getAntecedents(IProverSequent seq) {
		Predicate goal = seq.goal();

		// goal should have the form finite(ran(r))
		if (!Lib.isFinite(goal))
			return null;
		SimplePredicate sPred = (SimplePredicate) goal;
		if (!Lib.isRan(sPred.getExpression()))
			return null;
		
		// There will be 1 antecidents
		IAntecedent[] antecidents = new IAntecedent[1];
		
		UnaryExpression expression = (UnaryExpression) sPred.getExpression();
		
		Expression r = expression.getChild();
		
		// finite(r)
		Predicate newGoal = ff.makeSimplePredicate(Predicate.KFINITE, r, null);
		antecidents[0] = ProverFactory.makeAntecedent(newGoal);

		return antecidents;
	}

	protected String getDisplayName() {
		return "finite of range of a relation";
	}

	public IReasonerOutput apply(IProverSequent seq, IReasonerInput input,
			IProofMonitor pm) {
		IAntecedent[] antecidents = getAntecedents(seq);
		if (antecidents == null)
			return ProverFactory.reasonerFailure(this, input,
					"Inference " + getReasonerID()
							+ " is not applicable");

		// Generate the successful reasoner output
		return ProverFactory.makeProofRule(this, input, seq.goal(),
				getDisplayName(), antecidents);
	}

}

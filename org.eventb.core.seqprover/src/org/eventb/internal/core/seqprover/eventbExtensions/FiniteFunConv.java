package org.eventb.internal.core.seqprover.eventbExtensions;

import org.eventb.core.ast.BinaryExpression;
import org.eventb.core.ast.Expression;
import org.eventb.core.ast.FormulaFactory;
import org.eventb.core.ast.ITypeCheckResult;
import org.eventb.core.ast.Predicate;
import org.eventb.core.ast.SimplePredicate;
import org.eventb.core.seqprover.IProofMonitor;
import org.eventb.core.seqprover.IProofRule;
import org.eventb.core.seqprover.IProverSequent;
import org.eventb.core.seqprover.IReasonerInput;
import org.eventb.core.seqprover.IReasonerOutput;
import org.eventb.core.seqprover.ProverFactory;
import org.eventb.core.seqprover.ProverRule;
import org.eventb.core.seqprover.SequentProver;
import org.eventb.core.seqprover.IProofRule.IAntecedent;
import org.eventb.core.seqprover.eventbExtensions.Lib;
import org.eventb.core.seqprover.reasonerInputs.SingleExprInput;
import org.eventb.core.seqprover.reasonerInputs.SingleExprInputReasoner;

public class FiniteFunConv extends SingleExprInputReasoner {

	public static String REASONER_ID = SequentProver.PLUGIN_ID + ".finiteFunConv";

	private static FormulaFactory ff = FormulaFactory.getDefault();
	
	public String getReasonerID() {
		return REASONER_ID;
	}
	
	@ProverRule("FIN_FUN2_R")
	public IReasonerOutput apply(IProverSequent seq, IReasonerInput input,
			IProofMonitor pm) {
		
		Predicate goal = seq.goal();
		if (!Lib.isFinite(goal))
			return ProverFactory.reasonerFailure(this, input,
					"Goal is not a finiteness");
		SimplePredicate sPred = (SimplePredicate) goal;
		if (!Lib.isRelation(sPred.getExpression()))
			return ProverFactory.reasonerFailure(this, input,
				"Goal is not a finiteness of a relation");
		
		Expression f = ((SimplePredicate) goal).getExpression();
		
		if (!(input instanceof SingleExprInput))
			return ProverFactory.reasonerFailure(this, input,
					"Expected a single expression input");

		if (((SingleExprInput) input).hasError()) {
			return ProverFactory.reasonerFailure(this, input,
					((SingleExprInput) input).getError());
		}
		Expression function = ((SingleExprInput) input).getExpression();

		if (!Lib.isSetOfPartialFunction(function)) {
			return ProverFactory.reasonerFailure(this, input,
				"Expected a set of all partial functions S ⇸ T");
		}

		// There will be 2 antecidents
		IAntecedent[] antecidents = new IAntecedent[2];
		
		Expression S = ((BinaryExpression) function).getLeft();
		
		// f~ : S +-> T
		Expression fConverse = ff.makeUnaryExpression(Expression.CONVERSE, f, null);
		Predicate newGoal0 = ff.makeRelationalPredicate(Predicate.IN,
				fConverse, function, null);
		ITypeCheckResult typeCheck = newGoal0.typeCheck(ff.makeTypeEnvironment());
		if (!typeCheck.isSuccess()) {
			return ProverFactory.reasonerFailure(this, input,
					"Type check failed for " + newGoal0);			
		}
		
		antecidents[0] = ProverFactory.makeAntecedent(newGoal0);
		
		// finite(S)
		Predicate newGoal1 = ff.makeSimplePredicate(Predicate.KFINITE, S, null);
		antecidents[1] = ProverFactory.makeAntecedent(newGoal1);
		
		IProofRule reasonerOutput = ProverFactory.makeProofRule(
				this,input,
				goal,
				"finite of function converse",
				antecidents);
		
		return reasonerOutput;
	}

}

package org.eventb.internal.core.seqprover.eventbExtensions;

import java.util.Collections;

import org.eventb.core.ast.Expression;
import org.eventb.core.ast.FreeIdentifier;
import org.eventb.core.ast.ITypeEnvironment;
import org.eventb.core.ast.Predicate;
import org.eventb.core.seqprover.IProofMonitor;
import org.eventb.core.seqprover.IProofRule;
import org.eventb.core.seqprover.IProverSequent;
import org.eventb.core.seqprover.IReasonerInput;
import org.eventb.core.seqprover.IReasonerOutput;
import org.eventb.core.seqprover.ProverFactory;
import org.eventb.core.seqprover.SequentProver;
import org.eventb.core.seqprover.IProofRule.IAntecedent;
import org.eventb.core.seqprover.eventbExtensions.Lib;
import org.eventb.core.seqprover.reasonerInputs.SingleExprInput;
import org.eventb.core.seqprover.reasonerInputs.SingleExprInputReasoner;

/**
 * This reasoner abstracts a given expression with a fresh free identifier.
 * 
 * It does this by intruducing a new free variable and an equality hypothesis that can be
 * used to later rewrite all occurances of the expression by the free variable.
 * 
 * @author Farhad Mehta
 *
 */
public class AbstrExpr extends SingleExprInputReasoner {

	public static String REASONER_ID = SequentProver.PLUGIN_ID + ".ae";
	
	public String getReasonerID() {
		return REASONER_ID;
	}
	
	public IReasonerOutput apply(IProverSequent seq,IReasonerInput reasonerInput, IProofMonitor pm){
		
		// Organize Input
		SingleExprInput input = (SingleExprInput) reasonerInput;
		
		if (input.hasError())
			return ProverFactory.reasonerFailure(this,reasonerInput,input.getError());

		Expression expr = input.getExpression();
				
		// We can now assume that lemma has been properly parsed and typed.
		
		// Generate the well definedness condition for the lemma
		Predicate exprWD = Lib.WD(expr);

		// Generate a fresh free identifier
		FreeIdentifier freeIdent = Lib.ff.makeFreeIdentifier(
				genFreshFreeIdentName(seq.typeEnvironment()),
				null, expr.getType());
		
		// Generate the equality predicate
		Predicate aeEq = Lib.makeEq(freeIdent, expr);
		
		// Generate the anticidents
		IAntecedent[] anticidents = new IAntecedent[2];
		
		// Well definedness condition
		anticidents[0] = ProverFactory.makeAntecedent(exprWD);
		
		// 
		anticidents[1] = ProverFactory.makeAntecedent(
				null, Collections.singleton(aeEq),
				new FreeIdentifier[] {freeIdent}, null);
		
		// Generate the proof rule
		IProofRule reasonerOutput = ProverFactory.makeProofRule(
				this,input,
				null,
				"ae ("+expr.toString()+")",
				anticidents);
				
		return reasonerOutput;
	}
	

	/**
	 * Generates a name for an identifier that does not occur in the
	 * given type environment.
	 * 
	 * @param typeEnv
	 * 			the given type environment.
	 * @return a fresh identifier name.
	 * 			
	 */
	private String genFreshFreeIdentName(ITypeEnvironment typeEnv){
		String prefix = "ae";
		String identName = prefix;
		int i = 0;
		while (typeEnv.contains(identName)){
			identName = prefix + Integer.toString(i);
			i++;
		}
		return identName;
	}
	
}

package org.eventb.core.seqprover.reasonerInputs;

import org.eventb.core.ast.ITypeEnvironment;
import org.eventb.core.ast.Expression;
import org.eventb.core.seqprover.IReasonerInput;
import org.eventb.core.seqprover.IReasonerInputReader;
import org.eventb.core.seqprover.IReasonerInputWriter;
import org.eventb.core.seqprover.SerializeException;
import org.eventb.core.seqprover.eventbExtensions.Lib;
import org.eventb.core.seqprover.proofBuilder.ReplayHints;

public class SingleExprInput implements IReasonerInput{
	
	private static final String SERIALIZATION_KEY = "expr";

	private Expression expression;
	private String error;
	
	public SingleExprInput(String exprString, ITypeEnvironment typeEnv){
		
		expression = Lib.parseExpression(exprString);
		if (expression == null)
		{
			error = "Parse error for expression: "+ exprString;
			return;
		}
		if (! Lib.typeCheckClosed(expression,typeEnv)){
			error = "Type check failed for expression: "+expression;
			expression = null;
			return;
		}
		error = null;
	}
	
	public SingleExprInput(Expression expression){
		assert expression != null;
		this.expression = expression;
		this.error = null;
	}
	
	public final boolean hasError(){
		return (error != null);
	}
	
	/**
	 * @return Returns the error.
	 */
	public final String getError() {
		return error;
	}

	/**
	 * @return Returns the Expression.
	 */
	public final Expression getExpression() {
		return expression;
	}

	public void serialize(IReasonerInputWriter writer) throws SerializeException {
		assert ! hasError();
		assert expression != null;
		writer.putExpressions(SERIALIZATION_KEY, expression);
	}
	
	public SingleExprInput(IReasonerInputReader reader) throws SerializeException {
		final Expression[] preds = reader.getExpressions(SERIALIZATION_KEY);
		if (preds.length != 1) {
			throw new SerializeException(
					new IllegalStateException("Expected exactly one expression")
			);
		}
		expression = preds[0];
	}

	public void applyHints(ReplayHints hints) {
		expression = hints.applyHints(expression);
		
	}

}

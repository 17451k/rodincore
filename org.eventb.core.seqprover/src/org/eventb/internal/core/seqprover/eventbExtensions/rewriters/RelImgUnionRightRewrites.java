package org.eventb.internal.core.seqprover.eventbExtensions.rewriters;

import java.util.Arrays;

import org.eventb.core.ast.BinaryExpression;
import org.eventb.core.ast.Formula;
import org.eventb.core.ast.FormulaFactory;
import org.eventb.core.ast.IFormulaRewriter;
import org.eventb.core.ast.IPosition;
import org.eventb.core.ast.Predicate;
import org.eventb.core.seqprover.IHypAction;
import org.eventb.core.seqprover.ProverFactory;
import org.eventb.core.seqprover.SequentProver;

public class RelImgUnionRightRewrites extends AbstractManualRewrites {

	public static final String REASONER_ID = SequentProver.PLUGIN_ID
			+ ".relImgUnionRightRewrites";

	public String getReasonerID() {
		return REASONER_ID;
	}

	@Override
	protected String getDisplayName(Predicate pred, IPosition position) {
		if (pred == null)
			return "relational image with ∪ right in goal";
		return "relational image with ∪ right in hyp (" + pred.getSubFormula(position) + ")";
	}

	@Override
	protected IHypAction getHypAction(Predicate pred, IPosition position) {
		if (pred == null) {
			return null;
		}
		return ProverFactory.makeHideHypAction(Arrays.asList(pred));
	}

	@Override
	protected Predicate[] rewrite(Predicate pred, IPosition position) {
		IFormulaRewriter rewriter = new RelImgConjRightRewriterImpl();
		
		FormulaFactory ff = FormulaFactory.getDefault();
		Formula subFormula = pred.getSubFormula(position);
		
		Formula newSubFormula = null;
		if (subFormula instanceof BinaryExpression) {
			newSubFormula = rewriter
				.rewrite((BinaryExpression) subFormula);
		}
		if (newSubFormula == null)
			return null;
		
		if (newSubFormula == subFormula) // No rewrite occurs
			return null;

		return new Predicate[] { pred.rewriteSubFormula(position,
				newSubFormula, ff) };
	}

}

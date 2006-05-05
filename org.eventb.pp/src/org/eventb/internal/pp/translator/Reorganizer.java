package org.eventb.internal.pp.translator;

import org.eventb.core.ast.Expression;
import org.eventb.core.ast.Formula;
import org.eventb.core.ast.FormulaFactory;
import org.eventb.core.ast.Predicate;
import org.eventb.core.ast.RelationalPredicate;

public abstract class Reorganizer {
	
	public static Predicate reorganize(RelationalPredicate pred, FormulaFactory ff) {
		
		final ConditionalQuant forall = new ConditionalQuant(ff);

		final Predicate newPred = doPhase(pred, new ExpressionExtractor(forall, ff), ff);
		if(newPred == pred) return pred;
		else {
			forall.startPhase2();
			pred = doPhase(pred, new ExpressionExtractor(forall, ff), ff);
			
			return forall.conditionalQuantify(Formula.FORALL, pred, null);
		}
	}
	
	protected static RelationalPredicate doPhase(
			RelationalPredicate pred, ExpressionExtractor extractor, FormulaFactory ff) {
	
		Expression left = extractor.translate(pred.getLeft());
		Expression right = extractor.translate(pred.getRight());
		
		if(left != pred.getLeft() || right != pred.getRight())
			return ff.makeRelationalPredicate(pred.getTag(), left, right, pred.getSourceLocation());
		else
			return pred;
	}

	private static class ExpressionExtractor extends IdentityTranslator {
		private final ConditionalQuant quantification;

		public ExpressionExtractor(ConditionalQuant quantification, FormulaFactory ff) {
			super(ff);
			this.quantification = quantification;
		}
		
		@Override
		protected Expression translate(Expression expr) {
			switch(expr.getTag()) {
			case Formula.KCARD:
			case Formula.FUNIMAGE:
			case Formula.KMIN:
			case Formula.KMAX:
				return  quantification.condSubstitute(expr);
			case Formula.BOUND_IDENT:
				return quantification.push(expr);
			default:
				return super.translate(expr);
			}
		}
		
		@Override
		protected Predicate translate(Predicate pred) {
			return pred;
		}
	}


}

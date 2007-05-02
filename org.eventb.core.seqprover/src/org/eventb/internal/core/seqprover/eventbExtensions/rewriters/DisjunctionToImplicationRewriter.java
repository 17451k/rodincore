package org.eventb.internal.core.seqprover.eventbExtensions.rewriters;

import org.eventb.core.ast.AssociativePredicate;
import org.eventb.core.ast.DefaultRewriter;
import org.eventb.core.ast.FormulaFactory;
import org.eventb.core.ast.Predicate;
import org.eventb.core.seqprover.eventbExtensions.Lib;

public class DisjunctionToImplicationRewriter extends DefaultRewriter {

	public DisjunctionToImplicationRewriter(boolean autoFlattening,
			FormulaFactory ff) {
		super(autoFlattening, ff);
	}

	@Override
	public Predicate rewrite(AssociativePredicate predicate) {
		if (Lib.isDisj(predicate))
		{
			Predicate[] disjuncts = Lib.disjuncts(predicate);
			assert disjuncts.length >= 2;
			Predicate firstDisjunct = disjuncts[0];
			Predicate[] restDisjuncts = new Predicate[disjuncts.length - 1];
			System.arraycopy(disjuncts,1,restDisjuncts,0,disjuncts.length - 1);
			return Lib.makeImp(
					Lib.makeNeg(firstDisjunct),
					Lib.makeDisj(restDisjuncts)
					);
		}
		return predicate;
	}

}
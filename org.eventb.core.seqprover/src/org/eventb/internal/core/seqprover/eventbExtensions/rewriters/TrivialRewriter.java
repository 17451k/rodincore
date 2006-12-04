package org.eventb.internal.core.seqprover.eventbExtensions.rewriters;

import org.eventb.core.ast.BoundIdentDecl;
import org.eventb.core.ast.BoundIdentifier;
import org.eventb.core.ast.Expression;
import org.eventb.core.ast.Predicate;
import org.eventb.core.ast.RelationalPredicate;
import org.eventb.core.ast.Type;

import static org.eventb.core.seqprover.eventbExtensions.Lib.*;

public class TrivialRewriter implements Rewriter{

	public String getRewriterID() {
		return "trivialRewriter";
	}
	
	public String getName() {
		return "trivial";
	}
	
	public boolean isApplicable(Predicate p) {
		if ((isNeg(p)) && (isNeg(negPred(p)))) 
			return  true;
		
		if (isEq(p) && eqLeft(p).equals(eqRight(p)))
			return true;
		
		if (isNotEq(p) && notEqLeft(p).equals(notEqRight(p)))
			return true;
		
		if (isInclusion(p) && isEmptySet(getSet(p)))
			return true;
		if (isNotInclusion(p) && isEmptySet(getSet(p)))
			return true;
		
		if (isNotEq(p) && (isEmptySet(notEqRight(p)) || isEmptySet(notEqLeft(p))))
			return true;
		
		return false;
	}

	public Predicate apply(Predicate p) {
		if ((isNeg(p)) && (isNeg(negPred(p)))) 
			return  negPred(negPred(p));
		
		if (isEq(p) && eqLeft(p).equals(eqRight(p)))
			return True;	
		if (isNotEq(p) && notEqLeft(p).equals(notEqRight(p)))
			return False;
		
		if (isInclusion(p) && isEmptySet(getSet(p)))
			return False;
		
		if (isNotInclusion(p) && isEmptySet(getSet(p)))
			return True;
		
		if (isNotEq(p) && (isEmptySet(notEqRight(p)) || isEmptySet(notEqLeft(p))))
		{
			Expression nonEmptySet = isEmptySet(notEqRight(p)) ?
					notEqLeft(p) : notEqRight(p);
			
			Type type = nonEmptySet.getType().getBaseType();
			assert type != null;
			// TODO : find a nice way to generate a name for this variable
			// Although it is bound freeing it may clutter the name space and
			// maybe force refactoring.
			String varName = "e";
			BoundIdentDecl[] boundIdentDecls = {ff.makeBoundIdentDecl(varName,null,type)};
			BoundIdentifier boundIdent = ff.makeBoundIdentifier(0,null,type);
			Predicate pred = ff.makeRelationalPredicate(RelationalPredicate.IN,boundIdent,nonEmptySet,null);
			Predicate exPred = makeExQuant(boundIdentDecls,pred);
			typeCheck(exPred);
			assert exPred.isTypeChecked();
			return exPred;
		}
		return null;
	}

}

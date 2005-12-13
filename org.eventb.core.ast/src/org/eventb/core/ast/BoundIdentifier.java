/*
 * Created on 03-jun-2005
 *
 */
package org.eventb.core.ast;

import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

import org.eventb.internal.core.ast.AdjustFreeReplacement;
import org.eventb.internal.core.ast.AdjustUnbindReplacement;
import org.eventb.internal.core.ast.BindReplacement;
import org.eventb.internal.core.ast.Info;
import org.eventb.internal.core.ast.LegibilityResult;
import org.eventb.internal.core.ast.Replacement;
import org.eventb.internal.core.ast.UnbindReplacement;
import org.eventb.internal.core.typecheck.TypeCheckResult;
import org.eventb.internal.core.typecheck.TypeUnifier;

/**
 * Represents a bound identifier inside an event-B formula.
 * <p>
 * A bound identifier is encoded using the De Bruijn notation. The corresponding
 * quantifier (which is a {@link FreeIdentifier}) is retrieved using the index
 * of the bound identifier. Index 0 represents the nearest quantifier up in the
 * formula.
 * </p>
 * 
 * TODO: give examples and a better specification.
 * 
 * @author François Terrier
 */
public class BoundIdentifier extends Identifier {
	
	// index of this bound identifier
	// helps find its corresponding free variable in the formula
	private final int boundIndex;

	protected BoundIdentifier(int boundIndex, int tag, SourceLocation location) {
		super(tag, location, boundIndex);
		this.boundIndex = boundIndex;
		assert tag == Formula.BOUND_IDENT;
		assert 0 <= boundIndex;
	}

	/**
	 * Returns the De Bruijn index of this identifier.
	 * 
	 * @return the index of this bound identifier
	 */
	public int getBoundIndex() {
		return boundIndex;
	}

	private static String resolveIndex(int index, String[] boundIdents) {
		if (index < boundIdents.length) {
			return boundIdents[boundIdents.length - index - 1];
		}
		return null;
	}
	
	@Override
	protected String toString(boolean isRightChild, int parentTag, String[] boundNames) {
		return toStringFullyParenthesized(boundNames);
	}
	
	@Override
	protected String toStringFullyParenthesized(String[] boundNames) {
		String result = resolveIndex(boundIndex, boundNames);
		if (result == null) {
			// Fallback default in case this can not be resolved.
			result = "[[" + boundIndex + "]]";
		}
		return result;
	}

	@Override
	protected String getSyntaxTree(String[] boundNames, String tabs) {
		final String typeName = getType()!=null?" [type: "+getType().toString()+"]":"";
		return tabs + this.getClass().getSimpleName() + " [name: "
				+ toStringFullyParenthesized(boundNames) + "] [index: "
				+ boundIndex + "]" + typeName + "\n";
	}

	@Override
	protected void isLegible(LegibilityResult result, BoundIdentDecl[] quantifiedIdents) {
		// this has now been moved to isWellFormed because the user cannot cause this problem!
//		if (boundIndex >= quantifiedIdents.length) {
//			result.addProblem(new LegibilityProblem(getSourceLocation(),Problem.BoundIdentifierIndexOutOfBounds,new String[]{""},ProblemSeverities.Error));
//		}
	}
	
	@Override
	protected boolean equals(Formula other, boolean withAlphaConversion) {
		return hasSameType(other)
				&& boundIndex == ((BoundIdentifier) other).boundIndex;
	}

	@Override
	public Expression flatten(FormulaFactory factory) {
		return this;
	}

	@Override
	protected void typeCheck(TypeCheckResult result, BoundIdentDecl[] quantifiedIdentifiers) {
		final BoundIdentDecl decl = quantifiedIdentifiers[quantifiedIdentifiers.length - boundIndex - 1];
		assert decl != null : "Bound variable without a declaration";
		setType(decl.getType(), result);
	}
	
	@Override
	protected boolean solveType(TypeUnifier unifier) {
		return finalizeType(true, unifier);
	}

	@Override
	protected void collectFreeIdentifiers(LinkedHashSet<FreeIdentifier> freeIdents) {
		// Nothing to do
	}

	@Override
	protected void collectNamesAbove(Set<String> names, String[] boundNames, int offset) {
		if (boundIndex < offset) {
			// Locally bound, nothing to do
		}
		else {
			names.add(resolveIndex(boundIndex - offset, boundNames));
		}
	}
	
	@Override
	protected Expression bindTheseIdents(Map<String, Integer> binding, int offset, FormulaFactory factory) {
		if (boundIndex < offset) {
			//  Tightly bound so not changed
			return this;
		}
		return factory.makeBoundIdentifier(boundIndex + binding.size(), getSourceLocation());
	}

	@Override
	public boolean accept(IVisitor visitor) {
		return visitor.visitBOUND_IDENT(this);
	}

	@Override
	public Predicate getWDPredicateRaw(FormulaFactory formulaFactory) {
		return formulaFactory.makeLiteralPredicate(BTRUE, null);
	}

	@Override
	protected boolean isWellFormed(int noOfBoundVars) {
		return boundIndex < noOfBoundVars;
	}

	@Override
	protected Expression substituteAll(int noOfBoundVars, Replacement replacement, FormulaFactory formulaFactory) {
		if (replacement.getClass() == AdjustFreeReplacement.class) {
			if(boundIndex < noOfBoundVars)
				return this;
			else {
				int  offset = ((AdjustFreeReplacement) replacement).getOffset();
				BoundIdentifier id = formulaFactory.makeBoundIdentifier(boundIndex + offset, getSourceLocation());
				id.setType(getType(), null);
				return id;
			}
		} else if(replacement.getClass() == BindReplacement.class) {
			if (boundIndex < noOfBoundVars) {
				//  Tightly bound so not changed
				return this;
			} else {
				int offset = ((BindReplacement) replacement).size();
				BoundIdentifier id = formulaFactory.makeBoundIdentifier(boundIndex + offset, getSourceLocation());
				id.setType(getType(), null);
				return id;
			}
		} else if(replacement.getClass() == UnbindReplacement.class) {
			if(boundIndex < noOfBoundVars)
				return this;
			else {
				UnbindReplacement unbindReplacement = (UnbindReplacement) replacement;
				int relOffset = unbindReplacement.getDisplacement(boundIndex - noOfBoundVars);
				if(relOffset >= 0)
					return formulaFactory.makeBoundIdentifier(relOffset + noOfBoundVars, getSourceLocation());
				else {
					Info info = unbindReplacement.getInfo(boundIndex - noOfBoundVars);
					Expression expr = info.getExpression();
					
					assert getType().equals(expr.getType());
					
					if(info.isIndexClosed())
						return expr;
					else
						return expr.adjustIndicesRelative(noOfBoundVars, unbindReplacement, formulaFactory);
				}
			}
		} else if (replacement.getClass() == AdjustUnbindReplacement.class) {
			if(boundIndex < noOfBoundVars)
				return this;
			else {
				AdjustUnbindReplacement relativeReplacement = (AdjustUnbindReplacement) replacement;
				int offset = relativeReplacement.getOffset();
				int relOffset = offset + noOfBoundVars + relativeReplacement.getMaxDisplacement(boundIndex);
				BoundIdentifier id = formulaFactory.makeBoundIdentifier(boundIndex + relOffset, getSourceLocation());
				id.setType(getType(), null);
				return id;
			}
		} else
			return this;
	}

}

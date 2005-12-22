/*
 * Created on 20-may-2005
 *
 */
package org.eventb.core.ast;

import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

import org.eventb.internal.core.ast.LegibilityResult;
import org.eventb.internal.core.ast.Replacement;
import org.eventb.internal.core.typecheck.TypeCheckResult;
import org.eventb.internal.core.typecheck.TypeUnifier;

/**
 * This class represents a literal predicate in an event-B formula.
 * <p>
 * Can take value {BTRUE} or {BFALSE}.
 * </p>
 * 
 * @author François Terrier
 */
public class LiteralPredicate extends Predicate {

	// offset of the corresponding tag-interval in Formula
	protected static final int firstTag = FIRST_LITERAL_PREDICATE;
	protected static final String[] tags = {
		"\u22a4", // BTRUE
		"\u22a5"  // BFALSE
	};
	// For testing purposes
	public static final int TAGS_LENGTH = tags.length;
	
	protected LiteralPredicate(int tag, SourceLocation location) {
			super(tag, location, 0);
			assert tag >= firstTag && tag < firstTag+tags.length;
			// Always type-checked.
			finalizeTypeCheck(true);
	}

	@Override
	protected String toString(boolean isRightChild, int parentTag, String[] boundNames) {
		return tags[getTag()-firstTag];
	}

	@Override
	protected String toStringFullyParenthesized(String[] boundNames) {
		return tags[getTag()-firstTag];
	}

	@Override
	protected void isLegible(LegibilityResult result, BoundIdentDecl[] quantifiedIdents) {
		// Nothing to do, this subformula is always well-formed.
	}
	
	@Override
	protected boolean equals(Formula other, boolean withAlphaConversion) {
		return true;
	}

	@Override
	public Predicate flatten(FormulaFactory factory) {
		return this;
	}

	@Override
	protected void typeCheck(TypeCheckResult result, BoundIdentDecl[] quantifiedIdentifiers) {
		return;
	}
	
	@Override
	protected boolean solveType(TypeUnifier unifier) {
		return finalizeTypeCheck(true);
	}
	
	@Override
	protected String getSyntaxTree(String[] boundNames, String tabs) {
		return tabs + this.getClass().getSimpleName() + " ["+tags[getTag()-firstTag] + "]" + "\n";
	}

	@Override
	protected void collectFreeIdentifiers(LinkedHashSet<FreeIdentifier> freeIdents) {
		// Nothing to do
	}

	@Override
	protected void collectNamesAbove(Set<String> names, String[] boundNames, int offset) {
		// Nothing to do
	}
	
	@Override
	protected Predicate bindTheseIdents(Map<String, Integer> binding, int offset, FormulaFactory factory) {
		return this;
	}

	@Override
	public boolean accept(IVisitor visitor) {
		switch (getTag()) {
		case BTRUE:  return visitor.visitBTRUE(this);
		case BFALSE: return visitor.visitBFALSE(this);
		default:     return true;
		}
	}

	@Override
	public Predicate getWDPredicateRaw(FormulaFactory formulaFactory) {
		return formulaFactory.makeLiteralPredicate(BTRUE, null);
	}

	@Override
	protected boolean isWellFormed(int noOfBoundVars) {
		return true;
	}

	@Override
	protected Predicate substituteAll(int noOfBoundVars, Replacement replacement, FormulaFactory formulaFactory) {
		return this;
	}

}

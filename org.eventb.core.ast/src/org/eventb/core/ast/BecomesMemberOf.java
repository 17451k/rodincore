/*******************************************************************************
 * Copyright (c) 2005 ETH Zurich.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/

package org.eventb.core.ast;

import java.util.LinkedHashSet;
import java.util.Set;

import org.eventb.internal.core.ast.IdentListMerger;
import org.eventb.internal.core.ast.LegibilityResult;
import org.eventb.internal.core.typecheck.TypeCheckResult;
import org.eventb.internal.core.typecheck.TypeUnifier;

/**
 * Implements the set-based assignment, where a set expression is given for
 * the assigned identifier.
 * 
 * @author Laurent Voisin
 */
public class BecomesMemberOf extends Assignment {

	private final Expression setExpr;
	
	public BecomesMemberOf(FreeIdentifier assignedIdent, Expression setExpr,
			SourceLocation location) {
		super(BECOMES_MEMBER_OF, location, setExpr.hashCode(), assignedIdent);
		this.setExpr = setExpr;

		IdentListMerger freeIdentMerger = IdentListMerger.makeMerger(
					assignedIdent.freeIdents, setExpr.freeIdents);
		this.freeIdents = freeIdentMerger.getFreeMergedArray();

		IdentListMerger boundIdentMerger = IdentListMerger.makeMerger(
					assignedIdent.boundIdents, setExpr.boundIdents);
		this.boundIdents = boundIdentMerger.getBoundMergedArray();

		if (freeIdentMerger.containsError() || boundIdentMerger.containsError()) {
			// Incompatible type environments, don't bother going further.
			return;
		}

		// Check equality of types
		final Type type = assignedIdent.getType();
		if (type != null && type.equals(setExpr.getType().getBaseType())) {
			finalizeTypeCheck(true, null);
		}
	}

	/**
	 * Returns the set that occurs in the right-hand side of this assignment.
	 * 
	 * @return the set on the right-hand side of this assignment
	 */
	public Expression getSet() {
		return setExpr;
	}
	
	@Override
	public Assignment flatten(FormulaFactory factory) {
		final Expression newSetExpr = setExpr.flatten(factory);
		if (newSetExpr == setExpr)
			return this;
		return factory.makeBecomesMemberOf(assignedIdents[0],
				newSetExpr, getSourceLocation());
	}

	@Override
	protected void collectFreeIdentifiers(LinkedHashSet<FreeIdentifier> freeIdentSet) {
		for (FreeIdentifier ident: assignedIdents) {
			ident.collectFreeIdentifiers(freeIdentSet);
		}
		setExpr.collectFreeIdentifiers(freeIdentSet);
	}

	@Override
	protected void collectNamesAbove(Set<String> names, String[] boundNames,
			int offset) {

		for (FreeIdentifier ident: assignedIdents) {
			ident.collectNamesAbove(names, boundNames, offset);
		}
		setExpr.collectNamesAbove(names, boundNames, offset);
	}

	@Override
	protected String getSyntaxTree(String[] boundNames, String tabs) {
		final String childTabs = tabs + '\t';
		
		final StringBuilder result = new StringBuilder();
		result.append(tabs);
		result.append(this.getClass().getSimpleName());
		result.append(" [:\u2208]\n");
		for (FreeIdentifier ident: assignedIdents) {
			result.append(ident.getSyntaxTree(boundNames, childTabs));
		}
		result.append(setExpr.getSyntaxTree(boundNames, childTabs));
		return result.toString();
	}

	@Override
	protected boolean isWellFormed(int noOfBoundVars) {
		return setExpr.isWellFormed(noOfBoundVars);
	}

	@Override
	protected boolean equals(Formula otherFormula, boolean withAlphaConversion) {
		BecomesMemberOf other = (BecomesMemberOf) otherFormula;
		return this.hasSameAssignedIdentifiers(other)
				&& setExpr.equals(other.setExpr, withAlphaConversion);
	}

	@Override
	protected void typeCheck(TypeCheckResult result, BoundIdentDecl[] boundAbove) {
		final FreeIdentifier lhs = assignedIdents[0];
		lhs.typeCheck(result, boundAbove);
		setExpr.typeCheck(result, boundAbove);

		final SourceLocation loc = getSourceLocation();
		result.unify(setExpr.getType(), result.makePowerSetType(lhs.getType()), loc);
	}

	/* (non-Javadoc)
	 * @see org.eventb.core.ast.Formula#isLegible(org.eventb.internal.core.ast.LegibilityResult, org.eventb.core.ast.BoundIdentDecl[])
	 */
	@Override
	protected void isLegible(LegibilityResult result, BoundIdentDecl[] quantifiedIdents) {
		for (FreeIdentifier ident: assignedIdents) {
			ident.isLegible(result, quantifiedIdents);
			if (! result.isSuccess())
				return;
		}
		setExpr.isLegible(result, quantifiedIdents);
	}

	/* (non-Javadoc)
	 * @see org.eventb.core.ast.Formula#getWDPredicateRaw(org.eventb.core.ast.FormulaFactory)
	 */
	@Override
	protected Predicate getWDPredicateRaw(FormulaFactory formulaFactory) {
		return setExpr.getWDPredicate(formulaFactory);
	}

	@Override
	protected boolean solveType(TypeUnifier unifier) {
		boolean result = setExpr.solveType(unifier);
		return finalizeTypeCheck(result, unifier);
	}

	@Override
	protected String toString(boolean isRightChild, int parentTag,
			String[] boundNames, boolean withTypes) {

		StringBuilder result = new StringBuilder();
		appendAssignedIdents(result);
		result.append(" :\u2208 ");
		result.append(setExpr.toString(false, STARTTAG, boundNames, withTypes));
		return result.toString();
	}

	@Override
	protected String toStringFullyParenthesized(String[] boundNames) {
		StringBuilder result = new StringBuilder();
		appendAssignedIdents(result);
		result.append(" :\u2208 (");
		result.append(setExpr.toStringFullyParenthesized(boundNames));
		result.append(')');
		return result.toString();
	}

	@Override
	public boolean accept(IVisitor visitor) {
		boolean goOn = visitor.enterBECOMES_MEMBER_OF(this);

		if (goOn) {
			goOn = assignedIdents[0].accept(visitor);
		}
		if (goOn) {
			goOn = visitor.continueBECOMES_MEMBER_OF(this);
		}
		if (goOn) {
			goOn = setExpr.accept(visitor);
		}
		return visitor.exitBECOMES_MEMBER_OF(this);
	}

	@Override
	protected Predicate getFISPredicateRaw(FormulaFactory ff) {
		final SourceLocation loc = getSourceLocation();
		final Expression emptySet = ff.makeEmptySet(setExpr.getType(), null);
		return ff.makeRelationalPredicate(NOTEQUAL, setExpr, emptySet, loc);
	}

	@Override
	protected Predicate getBAPredicateRaw(FormulaFactory ff) {
		final SourceLocation loc = getSourceLocation();
		final FreeIdentifier primedIdentifier = 
			ff.makePrimedFreeIdentifier(assignedIdents[0]);
		return ff.makeRelationalPredicate(IN, primedIdentifier, setExpr, loc);
	}

	@Override
	public FreeIdentifier[] getUsedIdentifiers() {
		return setExpr.getFreeIdentifiers();
	}

}

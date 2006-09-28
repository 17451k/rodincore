/*******************************************************************************
 * Copyright (c) 2005 ETH Zurich.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/

package org.eventb.core.ast;

import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import org.eventb.internal.core.ast.IdentListMerger;
import org.eventb.internal.core.ast.LegibilityResult;
import org.eventb.internal.core.typecheck.TypeCheckResult;
import org.eventb.internal.core.typecheck.TypeUnifier;

/**
 * Implements the deterministic assignment, where an expression is given for
 * each assigned identifier.
 * 
 * @author Laurent Voisin
 */
public class BecomesEqualTo extends Assignment {

	private final Expression[] values;
	
	protected BecomesEqualTo(FreeIdentifier assignedIdent, Expression value,
			SourceLocation location, FormulaFactory ff) {
		super(BECOMES_EQUAL_TO, location, value.hashCode(), assignedIdent);
		this.values = new Expression[] {value};
		checkPreconditions();
		synthesizeType(ff);
	}

	protected BecomesEqualTo(FreeIdentifier[] assignedIdents, Expression[] values,
			SourceLocation location, FormulaFactory ff) {
		super(BECOMES_EQUAL_TO, location, combineHashCodes(values), assignedIdents);
		this.values = new Expression[values.length];
		System.arraycopy(values, 0, this.values, 0, values.length);
		checkPreconditions();
		synthesizeType(ff);
	}

	protected BecomesEqualTo(List<FreeIdentifier> assignedIdents, List<Expression> values,
			SourceLocation location, FormulaFactory ff) {
		super(BECOMES_EQUAL_TO, location, combineHashCodes(values), assignedIdents);
		this.values = values.toArray(new Expression[values.size()]);
		checkPreconditions();
		synthesizeType(ff);
	}

	private void checkPreconditions() {
		assert assignedIdents.length != 0;
		assert assignedIdents.length == values.length;
	}
	
	@Override
	protected void synthesizeType(FormulaFactory ff) {
		final int length = assignedIdents.length;
		final Expression[] children = new Expression[length * 2];
		System.arraycopy(assignedIdents, 0, children, 0, length);
		System.arraycopy(values, 0, children, length, length);
		
		IdentListMerger freeIdentMerger = mergeFreeIdentifiers(children);
		this.freeIdents = freeIdentMerger.getFreeMergedArray();

		IdentListMerger boundIdentMerger = mergeBoundIdentifiers(children);
		this.boundIdents = boundIdentMerger.getBoundMergedArray();

		if (freeIdentMerger.containsError() || boundIdentMerger.containsError()) {
			// Incompatible type environments, don't bother going further.
			return;
		}

		// Check equality of types
		for (int i = 0; i < length; i++) {
			final Type type = assignedIdents[i].getType();
			if (type == null || ! type.equals(values[i].getType())) {
				return;
			}
		}
		typeChecked = true;
	}
	
	/**
	 * Returns the expressions that occur in the right-hand side of this
	 * assignment.
	 * 
	 * @return an array containing the expressions on the right-hand side of
	 *         this assignment
	 */
	public Expression[] getExpressions() {
		Expression[] result = new Expression[values.length];
		System.arraycopy(values, 0, result, 0, values.length);
		return result;
	}
	
	@Override
	public Assignment flatten(FormulaFactory factory) {
		final Expression[] newValues = new Expression[values.length];
		boolean changed = false;
		for (int i = 0; i < values.length; i++) {
			newValues[i] = values[i].flatten(factory);
			changed |= newValues[i] != values[i];
		}
		if (! changed)
			return this;
		return factory.makeBecomesEqualTo(assignedIdents, values, getSourceLocation());
	}

	@Override
	protected void collectFreeIdentifiers(LinkedHashSet<FreeIdentifier> freeIdentSet) {
		for (FreeIdentifier ident: assignedIdents) {
			ident.collectFreeIdentifiers(freeIdentSet);
		}
		for (Expression value: values) {
			value.collectFreeIdentifiers(freeIdentSet);
		}
	}

	@Override
	protected void collectNamesAbove(Set<String> names, String[] boundNames,
			int offset) {
		
		for (FreeIdentifier ident: assignedIdents) {
			ident.collectNamesAbove(names, boundNames, offset);
		}
		for (Expression value: values) {
			value.collectNamesAbove(names, boundNames, offset);
		}
	}

	/* (non-Javadoc)
	 * @see org.eventb.core.ast.Formula#getSyntaxTree(java.lang.String[], java.lang.String)
	 */
	@Override
	protected String getSyntaxTree(String[] boundNames, String tabs) {
		final String childTabs = tabs + '\t';
		
		final StringBuilder result = new StringBuilder();
		result.append(tabs);
		result.append(this.getClass().getSimpleName());
		result.append(" [:=]\n");
		for (FreeIdentifier ident: assignedIdents) {
			result.append(ident.getSyntaxTree(boundNames, childTabs));
		}
		for (Expression value: values) {
			result.append(value.getSyntaxTree(boundNames, childTabs));
		}
		return result.toString();
	}

	@Override
	protected boolean equals(Formula otherFormula, boolean withAlphaConversion) {
		BecomesEqualTo other = (BecomesEqualTo) otherFormula;
		if (! this.hasSameAssignedIdentifiers(other))
			return false;
		for (int i = 0; i < values.length; i++) {
			if (! values[i].equals(other.values[i], withAlphaConversion))
				return false;
		}
		return true;
	}

	@Override
	protected void typeCheck(TypeCheckResult result, BoundIdentDecl[] boundAbove) {
		final SourceLocation loc = getSourceLocation();
		for (int i = 0; i < values.length; i++) {
			assignedIdents[i].typeCheck(result, boundAbove);
			values[i].typeCheck(result, boundAbove);
			result.unify(assignedIdents[i].getType(), values[i].getType(), loc);
		}
	}

	@Override
	protected void isLegible(LegibilityResult result, BoundIdentDecl[] quantifiedIdents) {
		for (FreeIdentifier ident: assignedIdents) {
			ident.isLegible(result, quantifiedIdents);
			if (! result.isSuccess())
				return;
		}
		for (Expression value: values) {
			value.isLegible(result, quantifiedIdents);
			if (! result.isSuccess())
				return;
		}
	}

	@Override
	protected Predicate getWDPredicateRaw(FormulaFactory formulaFactory) {
		return getWDConjunction(formulaFactory, values);
	}

	@Override
	protected boolean solveChildrenTypes(TypeUnifier unifier) {
		boolean success = true;
		for (Expression value: values) {
			success &= value.solveType(unifier);
		}
		return success;
	}

	@Override
	protected void toString(StringBuilder builder, boolean isRightChild,
			int parentTag, String[] boundNames, boolean withTypes) {
		
		appendAssignedIdents(builder);
		builder.append(" \u2254 ");
		String comma = "";
		for (Expression value: values) {
			builder.append(comma);
			value.toString(builder, false, STARTTAG, boundNames, withTypes);
			comma = ", ";
		}
	}

	@Override
	protected void toStringFullyParenthesized(StringBuilder builder,
			String[] boundNames) {
		
		appendAssignedIdents(builder);
		builder.append(" \u2254 ");
		boolean comma = false;
		for (Expression value: values) {
			if (comma) builder.append(", ");
			builder.append('(');
			value.toStringFullyParenthesized(builder, boundNames);
			builder.append(')');
			comma = true;
		}
	}

	@Override
	public boolean accept(IVisitor visitor) {
		boolean goOn = visitor.enterBECOMES_EQUAL_TO(this);

		for (int i = 0; goOn && i < assignedIdents.length; ++i) {
			goOn = assignedIdents[i].accept(visitor);
			if (goOn) {
				goOn = visitor.continueBECOMES_EQUAL_TO(this);
			}
		}
		
		for (int i = 0; goOn && i < values.length; i++) {
			if (i != 0) {
				goOn = visitor.continueBECOMES_EQUAL_TO(this);
			}
			if (goOn) {
				goOn = values[i].accept(visitor);
			}
		}

		return visitor.exitBECOMES_EQUAL_TO(this);
	}

	@Override
	protected Predicate getFISPredicateRaw(FormulaFactory formulaFactory) {
		return formulaFactory.makeLiteralPredicate(BTRUE, getSourceLocation());
	}

	@Override
	protected Predicate getBAPredicateRaw(FormulaFactory ff) {
		final SourceLocation loc = getSourceLocation();
		final int length = assignedIdents.length;
		final Predicate[] predicates = new Predicate[length];
		for (int i=0; i<length; i++) {
			predicates[i] = 
				ff.makeRelationalPredicate(EQUAL, 
						assignedIdents[i].withPrime(ff),
						values[i], 
						loc);
		}
		if (predicates.length > 1)
			return ff.makeAssociativePredicate(LAND, predicates, loc);
		else
			return predicates[0];
	}

	@Override
	public FreeIdentifier[] getUsedIdentifiers() {
		if (values.length == 1) {
			return values[0].getFreeIdentifiers();
		}

		// More than one value, we need to merge the free identifiers of every
		// child
		IdentListMerger freeIdentMerger = mergeFreeIdentifiers(values);
		FreeIdentifier[] idents = freeIdentMerger.getFreeMergedArray();

		// Need to copy the array, as it can be maximal for one child (and then
		// we would expose an internal array to clients)
		FreeIdentifier[] result = new FreeIdentifier[idents.length];
		System.arraycopy(idents, 0, result, 0, idents.length);
		return result;
	}

}

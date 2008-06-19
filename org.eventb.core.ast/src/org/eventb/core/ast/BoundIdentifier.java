/*******************************************************************************
 * Copyright (c) 2005, 2008 ETH Zurich and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     ETH Zurich - initial API and implementation
 *     Systerel - added accept for ISimpleVisitor
 *******************************************************************************/
package org.eventb.core.ast;

import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.eventb.internal.core.ast.IntStack;
import org.eventb.internal.core.ast.LegibilityResult;
import org.eventb.internal.core.ast.Position;
import org.eventb.internal.core.typecheck.TypeCheckResult;
import org.eventb.internal.core.typecheck.TypeUnifier;

/**
 * Represents a bound identifier inside an event-B formula.
 * <p>
 * A bound identifier is encoded using the De Bruijn notation. The corresponding
 * quantifier (which is a {@link BoundIdentDecl}) is retrieved using the index
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
	// helps find its corresponding declaration in the formula
	private final int boundIndex;

	protected BoundIdentifier(int boundIndex, int tag, SourceLocation location,
			Type type, FormulaFactory ff) {

		super(tag, location, boundIndex);
		assert tag == Formula.BOUND_IDENT;
		assert 0 <= boundIndex;
		
		this.boundIndex = boundIndex;
		
		synthesizeType(ff, type);
	}

	@Override
	protected void synthesizeType(FormulaFactory ff, Type givenType) {
		this.freeIdents = NO_FREE_IDENT;
		this.boundIdents = new BoundIdentifier[] {this};
		
		if (givenType == null) {
			return;
		}
		setFinalType(givenType, givenType);
	}

	/**
	 * Returns the De Bruijn index of this identifier.
	 * 
	 * @return the index of this bound identifier
	 */
	public int getBoundIndex() {
		return boundIndex;
	}

	/**
	 * Returns the declaration of this identifier.
	 * 
	 * @param boundIdentDecls
	 *            declarations of bound identifier above this node
	 * @return the declaration of this bound identifier
	 */
	public BoundIdentDecl getDeclaration(BoundIdentDecl[] boundIdentDecls) {
		return boundIdentDecls[boundIdentDecls.length - boundIndex - 1];
	}

	private static String resolveIndex(int index, String[] boundIdents) {
		if (index < boundIdents.length) {
			return boundIdents[boundIdents.length - index - 1];
		}
		return null;
	}
	
	@Override
	protected void toString(StringBuilder builder, boolean isRightChild,
			int parentTag, String[] boundNames, boolean withTypes) {

		toStringFullyParenthesized(builder, boundNames);
	}

	@Override
	protected void toStringFullyParenthesized(StringBuilder builder,
			String[] boundNames) {
		
		String image = resolveIndex(boundIndex, boundNames);
		if (image == null) {
			// Fallback default in case this can not be resolved.
			builder.append("[[");
			builder.append(boundIndex);
			builder.append("]]");
		} else {
			builder.append(image);
		}
	}

	@Override
	protected String getSyntaxTree(String[] boundNames, String tabs) {
		final StringBuilder builder = new StringBuilder();
		builder.append(tabs);
		builder.append(this.getClass().getSimpleName());
		builder.append(" [name: ");
		toStringFullyParenthesized(builder, boundNames);
		builder.append("] [index: ");
		builder.append(boundIndex);
		if (getType() != null) {
			builder.append("] [type: ");
			builder.append(getType().toString());
		}
		builder.append("]\n");
		return builder.toString();
	}

	@Override
	protected void isLegible(LegibilityResult result, BoundIdentDecl[] quantifiedIdents) {
		// this has now been moved to isWellFormed because the user cannot cause this problem!
//		if (boundIndex >= quantifiedIdents.length) {
//			result.addProblem(new LegibilityProblem(getSourceLocation(),Problem.BoundIdentifierIndexOutOfBounds,new String[]{""},ProblemSeverities.Error));
//		}
	}
	
	@Override
	protected boolean equals(Formula<?> other, boolean withAlphaConversion) {
		if (this.getTag() != other.getTag()) {
			return false;
		}
		return hasSameType(other)
				&& boundIndex == ((BoundIdentifier) other).boundIndex;
	}

	@Override
	protected void typeCheck(TypeCheckResult result, BoundIdentDecl[] quantifiedIdentifiers) {
		final BoundIdentDecl decl = getDeclaration(quantifiedIdentifiers);
		assert decl != null : "Bound variable without a declaration";
		setTemporaryType(decl.getType(), result);
	}
	
	@Override
	protected boolean solveChildrenTypes(TypeUnifier unifier) {
		return true;
	}

	@Override
	protected void collectFreeIdentifiers(LinkedHashSet<FreeIdentifier> freeIdentSet) {
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
		return factory.makeBoundIdentifier(
				boundIndex + binding.size(), 
				getSourceLocation(),
				getType());
	}

	@Override
	public boolean accept(IVisitor visitor) {
		return visitor.visitBOUND_IDENT(this);
	}

	@Override
	public void accept(ISimpleVisitor visitor) {
		visitor.visitBoundIdentifier(this);
	}

	@Override
	protected Predicate getWDPredicateRaw(FormulaFactory formulaFactory) {
		return formulaFactory.makeLiteralPredicate(BTRUE, null);
	}

	@Override
	public Expression rewrite(IFormulaRewriter rewriter) {
		return checkReplacement(rewriter.rewrite(this));
	}

	@Override
	protected void getPositions(IFormulaFilter filter, IntStack indexes,
			List<IPosition> positions) {

		if (filter.select(this)) {
			positions.add(new Position(indexes));
		}
	}

}

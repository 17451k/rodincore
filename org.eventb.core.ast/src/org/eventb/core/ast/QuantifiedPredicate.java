/*
 * Created on 11-may-2005
 *
 */
package org.eventb.core.ast;

import static org.eventb.core.ast.QuantifiedHelper.addUsedBoundIdentifiers;
import static org.eventb.core.ast.QuantifiedHelper.appendBoundIdentifiersString;
import static org.eventb.core.ast.QuantifiedHelper.areAllUsed;
import static org.eventb.core.ast.QuantifiedHelper.areEqualQuantifiers;
import static org.eventb.core.ast.QuantifiedHelper.checkBoundIdentTypes;
import static org.eventb.core.ast.QuantifiedHelper.getBoundIdentsAbove;
import static org.eventb.core.ast.QuantifiedHelper.getSyntaxTreeQuantifiers;
import static org.eventb.core.ast.QuantifiedUtil.catenateBoundIdentLists;
import static org.eventb.core.ast.QuantifiedUtil.resolveIdents;

import java.util.BitSet;
import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.eventb.internal.core.ast.BoundIdentDeclRemover;
import org.eventb.internal.core.ast.BoundIdentSubstitution;
import org.eventb.internal.core.ast.IntStack;
import org.eventb.internal.core.ast.LegibilityResult;
import org.eventb.internal.core.ast.Position;
import org.eventb.internal.core.typecheck.TypeCheckResult;
import org.eventb.internal.core.typecheck.TypeUnifier;

/**
 * QuantifiedPredicate is the class for all quantified predicates in an event-B
 * formula.
 * <p>
 * It can accept tags {FORALL, EXISTS}. The list of quantifiers is inherited
 * from QuantifiedFormula.
 * </p>
 * 
 * @author François Terrier
 */
public class QuantifiedPredicate extends Predicate {
	
	// child
	private final BoundIdentDecl[] quantifiedIdentifiers;
	private final Predicate pred;
	
	// offset in the corresponding tag interval
	protected final static int firstTag = FIRST_QUANTIFIED_PREDICATE;
	protected final static String[] tags = {
		"\u2200", // FORALL
		"\u2203"  // EXISTS
	};
	// For testing purposes
	public static final int TAGS_LENGTH = tags.length;

	protected QuantifiedPredicate(Predicate pred, BoundIdentDecl[] boundIdentifiers, int tag,
			SourceLocation location, FormulaFactory ff) {
		super(tag, location,
				combineHashCodes(boundIdentifiers.length, pred.hashCode()));

		this.quantifiedIdentifiers = boundIdentifiers.clone();
		this.pred = pred;
		
		checkPreconditions();
		synthesizeType(ff);
	}

	protected QuantifiedPredicate(Predicate pred,
			Collection<BoundIdentDecl> boundIdentifiers, int tag,
			SourceLocation location, FormulaFactory ff) {
		super(tag, location,
				combineHashCodes(boundIdentifiers.size(), pred.hashCode()));
		
		BoundIdentDecl[] model = new BoundIdentDecl[boundIdentifiers.size()];
		this.quantifiedIdentifiers = boundIdentifiers.toArray(model);
		this.pred = pred;
		
		checkPreconditions();
		synthesizeType(ff);
	}

	private void checkPreconditions() {
		assert getTag() >= firstTag && getTag() < firstTag+tags.length;
		assert quantifiedIdentifiers != null;
		assert 1 <= quantifiedIdentifiers.length;
		assert pred != null;
	}

	@Override
	protected void synthesizeType(FormulaFactory ff) {
		this.freeIdents = pred.freeIdents;

		final BoundIdentifier[] boundIdentsBelow = pred.boundIdents; 
		this.boundIdents = 
			getBoundIdentsAbove(boundIdentsBelow, quantifiedIdentifiers, ff);

		// Check types of identifiers bound here.
		if (! checkBoundIdentTypes(boundIdentsBelow, quantifiedIdentifiers)) {
			return;
		}

		if (! pred.isTypeChecked())
			return;
		
		typeChecked = true;
	}
	
	// indicates when the toString method should put parentheses
	private final static BitSet parenthesesMap = new BitSet();
	static {
		parenthesesMap.set(Formula.NOT);
		parenthesesMap.set(Formula.LIMP);
		parenthesesMap.set(Formula.LEQV);
		parenthesesMap.set(Formula.LAND);
		parenthesesMap.set(Formula.LOR);
	}
	
	/**
	 * Returns the list of the identifiers which are bound by this formula.
	 * 
	 * @return list of bound identifiers
	 */
	public BoundIdentDecl[] getBoundIdentDecls() {
		return quantifiedIdentifiers.clone();
	}
	
	/**
	 * Returns the predicate which is quantified here.
	 * 
	 * @return the child predicate
	 */
	public Predicate getPredicate() {
		return pred;
	}
	
	@Override
	protected void toString(StringBuilder builder, boolean isRightChild,
			int parentTag, String[] boundNames, boolean withTypes) {

		String[] localNames = resolveIdentsPred(boundNames);
		String[] newBoundNames = catenateBoundIdentLists(boundNames, localNames);
		final boolean needsParen = parenthesesMap.get(parentTag);

		if (needsParen) builder.append('(');
		builder.append(tags[getTag() - firstTag]);
		appendBoundIdentifiersString(builder, localNames,
				quantifiedIdentifiers, withTypes);
		builder.append("\u00b7");
		pred.toString(builder, false, getTag(), newBoundNames, withTypes);
		if (needsParen) builder.append(')');
	}

	@Override
	protected void toStringFullyParenthesized(StringBuilder builder,
			String[] boundNames) {

		String[] localNames = resolveIdentsPred(boundNames);
		String[] newBoundNames = catenateBoundIdentLists(boundNames, localNames);

		builder.append(tags[getTag() - firstTag]);
		appendBoundIdentifiersString(builder, localNames,
				quantifiedIdentifiers, false);
		builder.append("\u00b7(");
		pred.toStringFullyParenthesized(builder, newBoundNames);
		builder.append(")");
	}

	private String[] resolveIdentsPred(String[] boundNames) {
		HashSet<String> usedNames = new HashSet<String>();
		pred.collectNamesAbove(usedNames, boundNames, quantifiedIdentifiers.length);
		return resolveIdents(quantifiedIdentifiers, usedNames);
	}
	
	@Override
	protected String getSyntaxTree(String[] boundNames, String tabs) {
		String[] boundNamesBelow = catenateBoundIdentLists(boundNames, quantifiedIdentifiers);
		return tabs
				+ this.getClass().getSimpleName()
				+ " ["
				+ tags[getTag() - firstTag]
				+ "]\n"
				+ getSyntaxTreeQuantifiers(boundNamesBelow,tabs + "\t",quantifiedIdentifiers)
				+ pred.getSyntaxTree(boundNamesBelow,tabs + "\t");
	}

	@Override
	protected void isLegible(LegibilityResult result, BoundIdentDecl[] boundAbove) {

		for (BoundIdentDecl decl: quantifiedIdentifiers) {
			decl.isLegible(result, boundAbove);
			if (! result.isSuccess()) {
				break;
			}
		}
		final BoundIdentDecl[] boundBelow = catenateBoundIdentLists(boundAbove, quantifiedIdentifiers);
		if (result.isSuccess()) {
			pred.isLegible(result, boundBelow);
		}
	}
	
	@Override
	protected boolean equals(Formula<?> other, boolean withAlphaConversion) {
		if (this.getTag() != other.getTag()) {
			return false;
		}
		QuantifiedPredicate temp = (QuantifiedPredicate) other;
		return areEqualQuantifiers(quantifiedIdentifiers,
				temp.quantifiedIdentifiers, withAlphaConversion)
				&& pred.equals(temp.pred, withAlphaConversion);
	}

	@Override
	protected void typeCheck(TypeCheckResult result, BoundIdentDecl[] boundAbove) {
		for (BoundIdentDecl ident : quantifiedIdentifiers) {
			ident.typeCheck(result, boundAbove);
		}
		BoundIdentDecl[] boundBelow = catenateBoundIdentLists(boundAbove, quantifiedIdentifiers);
		pred.typeCheck(result, boundBelow);
	}
	
	@Override
	protected boolean solveChildrenTypes(TypeUnifier unifier) {
		boolean success = true;
		for (BoundIdentDecl ident: quantifiedIdentifiers) {
			success &= ident.solveType(unifier);
		}
		success &= pred.solveType(unifier);
		return success;
	}

	@Override
	protected void collectFreeIdentifiers(LinkedHashSet<FreeIdentifier> freeIdentSet) {
		pred.collectFreeIdentifiers(freeIdentSet);
	}

	/**
	 * Returns the list of all names that either occur free in this formula, or
	 * have been quantified somewhere above this node (that is closer to the
	 * root of the tree).
	 * 
	 * @param boundNames
	 *            array of names that are declared above this formula. These
	 *            names must be stored in the order in which they appear when
	 *            the formula is written from left to right
	 * @return the list of all names that occur in this formula and are not
	 *         declared within.
	 */
	public Set<String> collectNamesAbove(String[] boundNames) {
		Set<String> result = new HashSet<String>();
		pred.collectNamesAbove(result, boundNames, quantifiedIdentifiers.length);
		return result;
	}

	@Override
	protected void collectNamesAbove(Set<String> names, String[] boundNames, int offset) {
		final int newOffset = offset + quantifiedIdentifiers.length;
		pred.collectNamesAbove(names, boundNames, newOffset);
	}

	@Override
	protected Predicate bindTheseIdents(Map<String, Integer> binding, int offset, FormulaFactory factory) {
		final int newOffset = offset + quantifiedIdentifiers.length; 
		Predicate newPred = pred.bindTheseIdents(binding, newOffset, factory);
		if (newPred == pred) {
			return this;
		}
		return factory.makeQuantifiedPredicate(getTag(), quantifiedIdentifiers, newPred, getSourceLocation());
	}

	@Override
	public boolean accept(IVisitor visitor) {
		boolean goOn = true;

		switch (getTag()) {
		case FORALL: goOn = visitor.enterFORALL(this); break;
		case EXISTS: goOn = visitor.enterEXISTS(this); break;
		default:     assert false;
		}

		for (int i = 0; goOn && i < quantifiedIdentifiers.length; i++) {
			goOn = quantifiedIdentifiers[i].accept(visitor);
			if (goOn) {
				switch (getTag()) {
				case FORALL: goOn = visitor.continueFORALL(this); break;
				case EXISTS: goOn = visitor.continueEXISTS(this); break;
				default:     assert false;
				}
			}
		}
		if (goOn) goOn = pred.accept(visitor);
		
		switch (getTag()) {
		case FORALL: return visitor.exitFORALL(this);
		case EXISTS: return visitor.exitEXISTS(this);
		default:     return true;
		}
	}

	@Override
	protected Predicate getWDPredicateRaw(FormulaFactory formulaFactory) {
		final Predicate predWD = pred.getWDPredicateRaw(formulaFactory);
		final SourceLocation loc = getSourceLocation();
		return getWDSimplifyQ(formulaFactory, FORALL, quantifiedIdentifiers,
				predWD, loc);
	}

	/**
	 * Instantiates this quantified predicate with the given expressions.
	 * <p>
	 * Instantiating means substituting all occurrences of some identifiers
	 * bound by this quantified predicate by their corresponding replacement
	 * expressions.
	 * </p>
	 * <p>
	 * The replacements to do are specified by the given array. This array must
	 * have exactly the same length as the array returned by
	 * {@link #getBoundIdentDecls()}. Each element of the given array
	 * specifies the replacement expression for the bound identifier declaration
	 * with the same index. The element can be <code>null</code>, in which
	 * case the corresponding bound identifier declaration will be kept (no
	 * substitution will be done for it).
	 * </p>
	 * <p>
	 * For instance, if this method is applied to the predicate
	 * <pre>
	 *     ∀x,y· x = y + 1
	 * </pre>
	 * with the replacement <code>{null, "a"}</code>, then the result is
	 * <pre>
	 *     ∀x· x = a + 1
	 * </pre>
	 * If, instead, the replacement is <code>{"a", "b"}</code>, then the
	 * result is
	 * <pre>
	 * a = b + 1
	 * </pre>
	 * </p>
	 * 
	 * @param replacements
	 *            an array of replacement expressions. Its length must be the
	 *            number of identifiers bound by this quantified expression.
	 *            Some elements can be <code>null</code>
	 * @param formulaFactory
	 *            formula factory to use for building the result
	 * @return This formula after application of the substitution.
	 */
	public Predicate instantiate(Expression[] replacements, FormulaFactory formulaFactory) {
		BoundIdentSubstitution subst = 
			new BoundIdentSubstitution(quantifiedIdentifiers, replacements, formulaFactory);
		Predicate newPred = pred.rewrite(subst);
		List<BoundIdentDecl> newBoundIdentDecls = subst.getNewDeclarations();
		if (newBoundIdentDecls.isEmpty())
			return newPred;
		return formulaFactory.makeQuantifiedPredicate(getTag(), newBoundIdentDecls, newPred, getSourceLocation());
	}
	
	@Override
	public Predicate rewrite(IFormulaRewriter rewriter) {
		final int nbOfBoundIdentDecls = quantifiedIdentifiers.length;
		
		rewriter.enteringQuantifier(nbOfBoundIdentDecls);
		Predicate newPred = pred.rewrite(rewriter);
		rewriter.leavingQuantifier(nbOfBoundIdentDecls);

		final FormulaFactory ff = rewriter.getFactory();
		BoundIdentDecl[] newDecls = quantifiedIdentifiers;
		if (rewriter.autoFlatteningMode()) {
			final boolean[] used = new boolean[nbOfBoundIdentDecls];
			addUsedBoundIdentifiers(used, newPred);
			if (! areAllUsed(used)) {
				final BoundIdentDeclRemover subst = 
					new BoundIdentDeclRemover(quantifiedIdentifiers, used, ff);
				newPred = newPred.rewrite(subst);
				final List<BoundIdentDecl> newDeclL = subst.getNewDeclarations();
				final int size = newDeclL.size();
				if (size == 0) {
					// Child predicate as already been rewritten
					return newPred;
				} else {
					newDecls = newDeclL.toArray(new BoundIdentDecl[size]);
				}
			}

			if (newPred.getTag() == getTag()) {
				QuantifiedPredicate quantChild = (QuantifiedPredicate) newPred;
				newDecls = catenateBoundIdentLists(
						newDecls, quantChild.quantifiedIdentifiers);
				newPred = quantChild.pred;
			}
		}

		final QuantifiedPredicate before;
		if (newDecls == quantifiedIdentifiers && newPred == pred) {
			before = this;
		} else {
			final SourceLocation sloc = getSourceLocation();
			before = ff.makeQuantifiedPredicate(getTag(),
					newDecls, newPred, sloc);
		}
		return checkReplacement(rewriter.rewrite(before));
	}

	@Override
	protected void addGivenTypes(Set<GivenType> set) {
		for (BoundIdentDecl decl: quantifiedIdentifiers) {
			decl.addGivenTypes(set);
		}
		pred.addGivenTypes(set);
	}

	@Override
	protected void getPositions(IFormulaFilter filter, IntStack indexes,
			List<IPosition> positions) {
		
		if (filter.select(this)) {
			positions.add(new Position(indexes));
		}

		indexes.push(0);
		for (BoundIdentDecl decl: quantifiedIdentifiers) {
			decl.getPositions(filter, indexes, positions);
			indexes.incrementTop();
		}
		pred.getPositions(filter, indexes, positions);
		indexes.pop();
	}

	@Override
	protected Formula<?> getChild(int index) {
		if (index < quantifiedIdentifiers.length) {
			return quantifiedIdentifiers[index];
		}
		if (index == quantifiedIdentifiers.length) {
			return pred;
		}
		return null;
	}

	@Override
	protected IPosition getDescendantPos(SourceLocation sloc, IntStack indexes) {
		IPosition pos;
		indexes.push(0);
		for (BoundIdentDecl decl: quantifiedIdentifiers) {
			pos = decl.getPosition(sloc, indexes);
			if (pos != null)
				return pos;
			indexes.incrementTop();
		}
		pos = pred.getPosition(sloc, indexes);
		if (pos != null)
			return pos;
		indexes.pop();
		return new Position(indexes);
	}

	@Override
	protected Predicate rewriteChild(int index, SingleRewriter rewriter) {
		BoundIdentDecl[] newDecls = quantifiedIdentifiers;
		Predicate newPred = pred;
		final int length = quantifiedIdentifiers.length;
		if (index < length) {
			newDecls = quantifiedIdentifiers.clone();
			newDecls[index] = rewriter.rewrite(quantifiedIdentifiers[index]);
		} else if (index == length) {
			newPred = rewriter.rewrite(pred);
		} else {
			throw new IllegalArgumentException("Position is outside the formula");
		}
		return rewriter.factory.makeQuantifiedPredicate(getTag(),
				newDecls, newPred, getSourceLocation());
	}

}

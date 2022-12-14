/*******************************************************************************
 * Copyright (c) 2014, 2016 Systerel and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Systerel - initial API and implementation
 *******************************************************************************/
package org.eventb.core.ast.tests.extension;

import static java.util.Arrays.asList;
import static java.util.Collections.singletonList;
import static org.eventb.core.ast.tests.AbstractTests.parseExpression;
import static org.eventb.core.ast.tests.AbstractTests.parsePredicate;
import static org.eventb.core.ast.tests.AbstractTests.parseType;
import static org.eventb.core.ast.tests.AbstractTests.typeCheck;
import static org.eventb.core.ast.tests.FastFactory.ff;
import static org.eventb.core.ast.tests.extension.Extensions.EXTS_FAC;
import static org.eventb.internal.core.ast.extension.ExtensionSignature.getSignature;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

import org.eventb.core.ast.BooleanType;
import org.eventb.core.ast.Expression;
import org.eventb.core.ast.ExtendedExpression;
import org.eventb.core.ast.ExtendedPredicate;
import org.eventb.core.ast.ParametricType;
import org.eventb.core.ast.Predicate;
import org.eventb.core.ast.Type;
import org.eventb.core.ast.tests.extension.Extensions.CProd;
import org.eventb.core.ast.tests.extension.Extensions.FSet;
import org.eventb.core.ast.tests.extension.Extensions.Real;
import org.eventb.internal.core.ast.TypeRewriter;
import org.eventb.internal.core.ast.extension.ExtensionSignature;
import org.eventb.internal.core.ast.extension.ExtensionSignature.ExpressionExtSignature;
import org.eventb.internal.core.ast.extension.ExtensionSignature.PredicateExtSignature;
import org.eventb.internal.core.ast.extension.FunctionalTypeBuilder;
import org.junit.Test;

/**
 * Unit tests for the {@link ExtensionSignature} class.
 * 
 * @author Thomas Muller
 */
public class TestExtensionSignature {

	private static final Type INT = EXTS_FAC.makeIntegerType();
	private static final Type PINT = EXTS_FAC.makePowerSetType(INT);
	private static final Type BOOL = EXTS_FAC.makeBooleanType();
	private static final Type PBOOL = EXTS_FAC.makePowerSetType(BOOL);
	private static final Type REAL = EXTS_FAC.makeParametricType(Real.EXT,
			Real.NO_PARAMS);
	private static final Type PREAL = EXTS_FAC.makePowerSetType(REAL);

	private static final Type PFBOOL = EXTS_FAC.makePowerSetType(fin(BOOL));

	private static final Type PBOOL_REAL = EXTS_FAC.makePowerSetType(cprod(BOOL, REAL));

	/**
	 * A type rewriter that just rewrites the axiomatic real type to a given
	 * type of the same name.
	 */
	private static final FunctionalTypeBuilder BUILDER = new FunctionalTypeBuilder(
			new TypeRewriter(ff) {
				@Override
				public void visit(ParametricType type) {
					if (type.getExprExtension() == Real.EXT) {
						result = ff.makeGivenType(Real.EXT.getSyntaxSymbol());
						return;
					}
					final Type[] params = type.getTypeParameters();
					if (type.getExprExtension() == FSet.EXT) {
						if (params[0] instanceof BooleanType) {
							result = ff.makeGivenType("FIN");
							return;
						}
					}
					if (type.getExprExtension() == CProd.EXT) {
						if (params[0] instanceof BooleanType
								&& params[1] instanceof ParametricType
								&& ((ParametricType) params[1])
										.getExprExtension() == Real.EXT) {
							result = ff.makeGivenType("ext");
							return;
						}
					}
					super.visit(type);
				}
			});

	private static Type fin(Type type) {
		return EXTS_FAC.makeParametricType(FSet.EXT,
				singletonList(type));
	}

	private static Type cprod(Type left, Type right) {
		return EXTS_FAC.makeParametricType(CProd.EXT, asList(left, right));
	}

	/**
	 * Ensures that signature are correctly computed for the ?????? operator.
	 */
	@Test
	public void testAnd() {
		// checkPred("??????()", 0); // Does not parse
		checkPred("??????(???)", "BOOL???BOOL", 1);
		checkPred("??????(???, ???)", "BOOL??BOOL???BOOL", 2);
		checkPred("??????(???, ???, ???)", "BOOL??BOOL??BOOL???BOOL", 3);
	}

	/**
	 * Ensures that signature are correctly computed for the belongs operator.
	 */
	@Test
	public void testBelongs() {
		checkPred("belongs(1, ???, {2})", "????????(???)??BOOL???BOOL", 1, INT, PINT);
		checkPred("belongs(TRUE, ???, {FALSE})", "BOOL?????(BOOL)??BOOL???BOOL", 1,
				BOOL, PBOOL);
	}

	/**
	 * Ensures that signature are correctly computed for the union2 operator.
	 */
	@Test
	public void testUnion2() {
		checkExpr("union2({TRUE}, {FALSE})", "???(BOOL)?????(BOOL)??????(BOOL)", PBOOL,
				0, PBOOL, PBOOL);
		checkExpr("union2({1}, {2}, {2})", "???(???)?????(???)?????(???)??????(???)", PINT, 0,
				PINT, PINT, PINT);
	}

	/**
	 * Ensures that signature are correctly computed for the empty operator.
	 */
	@Test
	public void testEmpty() {
		checkExpr("empty??????(???)", "???(???)", PINT, 0);
		checkExpr("empty??????(BOOL)", "???(BOOL)", PBOOL, 0);
	}

	/**
	 * Ensures that signature are correctly computed for the COND operator.
	 */
	@Test
	public void testCOND() {
		checkExpr("COND(???, 1, 2)", "??????????BOOL??????", INT, 1, INT, INT);
		checkExpr("COND(???, TRUE, FALSE)", "BOOL??BOOL??BOOL???BOOL", BOOL, 1, BOOL,
				BOOL);
	}

	/**
	 * Ensures that signature are correctly computed for the real type.
	 */
	@Test
	public void testReal() {
		checkExpr("???", "???(???)", PREAL, 0);
	}

	/**
	 * Ensures that signature are correctly computed for the real zero.
	 */
	@Test
	public void testRealZero() {
		checkExpr("zero", "???", REAL, 0);
	}

	/**
	 * Ensures that signature are correctly computed for the real addition.
	 */
	@Test
	public void testRealPlus() {
		checkExpr("r +. s", "??????????????", REAL, 0, REAL, REAL);
	}

	/**
	 * Ensures that signature are correctly computed for the real empty set.
	 */
	@Test
	public void testRealEmpty() {
		checkExpr("emptyR", "???(???)", PREAL, 0);
	}

	/**
	 * Ensures that signature are correctly computed for finite sets.
	 */
	@Test
	public void testFSet() {
		checkExpr("FIN(BOOL)", "???(BOOL)??????(FIN)", PFBOOL, 0, PBOOL);
	}

	/**
	 * Ensures that signature are correctly computed for Cartesian product.
	 */
	@Test
	public void testCProd() {
		checkExpr("BOOL**???", "???(BOOL)?????(???)??????(ext)", PBOOL_REAL, 0, PBOOL, PREAL);
	}

	private void checkPred(String image, String functionalTypeImage,
			int nbOfPred, Type... types) {
		final Predicate pred = parsePredicate(image, EXTS_FAC);
		final ExtendedPredicate extPred = (ExtendedPredicate) pred;
		final ExtensionSignature expected = new PredicateExtSignature(
				extPred.getFactory(), extPred.getExtension(), nbOfPred, types);
		final ExtensionSignature actual = getSignature(extPred);
		assertCorrectSignature(expected, actual, functionalTypeImage);
	}

	private void checkExpr(String image, String functionalTypeImage,
			Type resultType, int nbOfPred, Type... types) {
		final Expression expr = parseExpression(image, EXTS_FAC);
		typeCheck(expr);
		final ExtendedExpression extExpr = (ExtendedExpression) expr;
		final ExtensionSignature expected = new ExpressionExtSignature(
				extExpr.getFactory(), extExpr.getExtension(), resultType,
				nbOfPred, types);
		final ExtensionSignature actual = getSignature(extExpr);
		assertCorrectSignature(expected, actual, functionalTypeImage);
	}

	private void assertCorrectSignature(ExtensionSignature expected,
			ExtensionSignature actual, String functionalTypeImage) {
		assertEquals(expected, actual);
		final Type functionalType = parseType(functionalTypeImage, ff);
		assertEquals(functionalType, actual.getFunctionalType(BUILDER));
	}

	/**
	 * Ensures that equality is correctly computed. Actually, we are more
	 * interested in disequality, as equality is tested for each extension in
	 * the other tests.
	 */
	@Test
	public void disequality() {
		// Different extensions
		assertDifferentExprSignatures("union2({1}, {2})", "union3({1}, {2})");

		// Different numbers of predicates
		assertDifferentPredSignatures("??????(???)", "??????(???, ???)");

		// Different numbers of expressions
		assertDifferentExprSignatures("union2({1}, {2})",
				"union2({1}, {2}, {3})");

		// Different expression types
		assertDifferentExprSignatures("union2({1}, {2})",
				"union2({TRUE}, {TRUE})");

		// Different return types
		assertDifferentExprSignatures("empty??????(???)", "empty??????(BOOL)");
	}

	private void assertDifferentPredSignatures(String image1, String image2) {
		assertDifferentSignatures(predSignature(image1), predSignature(image2));
	}

	private ExtensionSignature predSignature(String image) {
		final Predicate pred = parsePredicate(image, EXTS_FAC);
		return getSignature((ExtendedPredicate) pred);
	}

	private void assertDifferentExprSignatures(String image1, String image2) {
		assertDifferentSignatures(exprSignature(image1), exprSignature(image2));
	}

	private ExtensionSignature exprSignature(String image) {
		final Expression expr = parseExpression(image, EXTS_FAC);
		return getSignature((ExtendedExpression) expr);
	}

	private void assertDifferentSignatures(ExtensionSignature sig1,
			ExtensionSignature sig2) {
		assertFalse(sig1.equals(sig2));
		assertFalse(sig2.equals(sig1));
	}

}

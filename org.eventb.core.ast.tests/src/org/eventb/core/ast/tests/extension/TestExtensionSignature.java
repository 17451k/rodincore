/*******************************************************************************
 * Copyright (c) 2014 Systerel and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Systerel - initial API and implementation
 *******************************************************************************/
package org.eventb.core.ast.tests.extension;

import static org.eventb.core.ast.tests.AbstractTests.parseExpression;
import static org.eventb.core.ast.tests.AbstractTests.parsePredicate;
import static org.eventb.core.ast.tests.AbstractTests.parseType;
import static org.eventb.core.ast.tests.extension.Extensions.EXTS_FAC;
import static org.eventb.internal.core.ast.extension.ExtensionSignature.getSignature;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

import org.eventb.core.ast.Expression;
import org.eventb.core.ast.ExtendedExpression;
import org.eventb.core.ast.ExtendedPredicate;
import org.eventb.core.ast.Predicate;
import org.eventb.core.ast.Type;
import org.eventb.internal.core.ast.extension.ExtensionSignature;
import org.eventb.internal.core.ast.extension.ExtensionSignature.ExpressionExtSignature;
import org.eventb.internal.core.ast.extension.ExtensionSignature.PredicateExtSignature;
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

	/**
	 * Ensures that signature are correctly computed for the ∧∧ operator.
	 */
	@Test
	public void testAnd() {
		// checkPred("∧∧()", 0); // Does not parse
		checkPred("∧∧(⊤)", "BOOL↔BOOL", 1);
		checkPred("∧∧(⊤, ⊤)", "BOOL×BOOL↔BOOL", 2);
		checkPred("∧∧(⊤, ⊤, ⊤)", "BOOL×BOOL×BOOL↔BOOL", 3);
	}

	/**
	 * Ensures that signature are correctly computed for the belongs operator.
	 */
	@Test
	public void testBelongs() {
		checkPred("belongs(1, ⊤, {2})", "ℤ×ℙ(ℤ)×BOOL↔BOOL", 1, INT, PINT);
		checkPred("belongs(TRUE, ⊤, {FALSE})", "BOOL×ℙ(BOOL)×BOOL↔BOOL", 1,
				BOOL, PBOOL);
	}

	/**
	 * Ensures that signature are correctly computed for the union2 operator.
	 */
	@Test
	public void testUnion2() {
		checkExpr("union2({TRUE}, {FALSE})", "ℙ(BOOL)×ℙ(BOOL)↔ℙ(BOOL)", PBOOL,
				0, PBOOL, PBOOL);
		checkExpr("union2({1}, {2}, {2})", "ℙ(ℤ)×ℙ(ℤ)×ℙ(ℤ)↔ℙ(ℤ)", PINT, 0,
				PINT, PINT, PINT);
	}

	/**
	 * Ensures that signature are correctly computed for the empty operator.
	 */
	@Test
	public void testEmpty() {
		checkExpr("empty⦂ℙ(ℤ)", "ℙ(ℤ)", PINT, 0);
		checkExpr("empty⦂ℙ(BOOL)", "ℙ(BOOL)", PBOOL, 0);
	}

	/**
	 * Ensures that signature are correctly computed for the COND operator.
	 */
	@Test
	public void testCOND() {
		checkExpr("COND(⊤, 1, 2)", "ℤ×ℤ×BOOL↔ℤ", INT, 1, INT, INT);
		checkExpr("COND(⊤, TRUE, FALSE)", "BOOL×BOOL×BOOL↔BOOL", BOOL, 1, BOOL,
				BOOL);
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
		final Type functionalType = parseType(functionalTypeImage, EXTS_FAC);
		assertEquals(functionalType, actual.getFunctionalType());
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
		assertDifferentPredSignatures("∧∧(⊤)", "∧∧(⊤, ⊤)");

		// Different numbers of expressions
		assertDifferentExprSignatures("union2({1}, {2})",
				"union2({1}, {2}, {3})");

		// Different expression types
		assertDifferentExprSignatures("union2({1}, {2})",
				"union2({TRUE}, {TRUE})");

		// Different return types
		assertDifferentExprSignatures("empty⦂ℙ(ℤ)", "empty⦂ℙ(BOOL)");
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
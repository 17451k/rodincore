/*******************************************************************************
 * Copyright (c) 2012 Systerel and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Systerel - initial API and implementation
 *******************************************************************************/
package org.eventb.pptrans.tests;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;
import static org.eventb.core.ast.Formula.IN;
import static org.eventb.core.ast.tests.FastFactory.mRelationalPredicate;
import static org.eventb.core.ast.tests.FastFactory.mTypeEnvironment;

import org.eventb.core.ast.Expression;
import org.eventb.core.ast.ITypeEnvironmentBuilder;
import org.eventb.core.ast.Predicate;
import org.eventb.core.ast.QuantifiedPredicate;
import org.eventb.core.ast.RelationalPredicate;
import org.eventb.internal.pptrans.translator.MapletDecomposer;
import org.junit.Test;

/**
 * Unit tests for class {@link MapletDecomposer}.
 * 
 * Note : All expected predicates must be quantified, so sometimes a dummy
 * quantifier "∃x⦂ℤ·" is added in front of the expected predicate in the test
 * specification.
 * 
 * @author Laurent Voisin
 */
public class MapletDecomposerTests extends AbstractTranslationTests {

	/**
	 * Parse and type check the given predicate which must be either a
	 * relational predicate or a quantified predicate that contains a relational
	 * predicate.
	 * 
	 * @param predImage
	 *            predicate as a string
	 * @param typenv
	 *            typing environment
	 * @return the type-checked expression that occurs on the left-hand side of
	 *         the relational predicate
	 */
	private static Expression parseExpr(String predImage,
			ITypeEnvironmentBuilder typenv) {
		final Predicate pred = parsePred(predImage, typenv);
		return ((RelationalPredicate) pred).getLeft();
	}

	private static Predicate parsePred(String predImage, ITypeEnvironmentBuilder typenv) {
		Predicate pred = parse(predImage, typenv);
		if (pred instanceof QuantifiedPredicate) {
			pred = ((QuantifiedPredicate) pred).getPredicate();
		}
		return pred;
	}

	private final MapletDecomposer decomposer = new MapletDecomposer(ff);

	private void doTest(ITypeEnvironmentBuilder typenv, String inputImage,
			String expectedImage) {
		final RelationalPredicate pred = (RelationalPredicate) parsePred(
				inputImage, typenv);
		final Expression lhs = pred.getLeft();
		final Expression rhs = pred.getRight();
		decomposer.decompose(lhs);
		if (expectedImage == null) {
			assertFalse(decomposer.needsDecomposition());
			decomposer.startPhase2();
			assertSame(lhs, decomposer.decompose(lhs));
			assertSame(rhs, decomposer.push(rhs));
			assertSame(pred, decomposer.bind(pred));
		} else {
			assertTrue(decomposer.needsDecomposition());
			decomposer.startPhase2();
			final Expression newLhs = decomposer.decompose(lhs);
			final Expression newRhs = decomposer.push(rhs);
			final Predicate newPred = mRelationalPredicate(IN, newLhs, newRhs);
			final Predicate actual = decomposer.bind(newPred);
			final Predicate expected = parsePred(expectedImage, typenv);
			assertEquals(expected, actual);
		}
	}

	/**
	 * Ensure that recording an already decomposed expression does not change
	 * anything.
	 */
	@Test
	public void testRecordingDecomposeNoChange() {
		final ITypeEnvironmentBuilder typenv = mTypeEnvironment("a=S; b=T", ff);
		final Expression expr = parseExpr("a↦b ∈ S×T", typenv);
		decomposer.decompose(expr);
		assertEquals(0, decomposer.offset());
		assertFalse(decomposer.needsDecomposition());
	}

	/**
	 * Ensure that recording an expression that needs decomposition creates
	 * bound variables.
	 */
	@Test
	public void testRecordingDecomposeCreateSimple() {
		final ITypeEnvironmentBuilder typenv = mTypeEnvironment("a=S; b=T×U", ff);
		final Expression expr = parseExpr("a↦b ∈ S×(T×U)", typenv);
		decomposer.decompose(expr);
		assertEquals(2, decomposer.offset());
		assertTrue(decomposer.needsDecomposition());
	}

	/**
	 * Ensure that recording an expression that needs decomposition creates
	 * bound variables, even in a complicated case.
	 */
	@Test
	public void testRecordingDecomposeCreateComplex() {
		final ITypeEnvironmentBuilder typenv = mTypeEnvironment(//
				"a=S; b=T×U; c=T×U×V; d=S×(T×U)×V", ff);
		final Expression expr = parseExpr("a↦(b↦c)↦d ∈ A", typenv);
		decomposer.decompose(expr);
		assertEquals(2 + 3 + 4, decomposer.offset());
		assertTrue(decomposer.needsDecomposition());
	}

	/**
	 * Ensure that pushing an expression does not change anything.
	 */
	@Test
	public void testRecordingPushNoChange() {
		final ITypeEnvironmentBuilder typenv = mTypeEnvironment("b=S; c=T×U", ff);
		final Expression toPush = parseExpr("∃a⦂S · a↦b ∈ AB", typenv);
		final Expression toDecompose = parseExpr("c ∈ A", typenv);
		assertEquals(toPush, decomposer.push(toPush));
		assertEquals(0, decomposer.offset());
		decomposer.decompose(toDecompose);
		assertEquals(2, decomposer.offset());
		assertEquals(toPush, decomposer.push(toPush));
		assertEquals(2, decomposer.offset());
	}

	/**
	 * Ensure that processing an expression that do not need to be decomposed
	 * doesn't make any change.
	 */
	@Test
	public void testDecomposeNoChange() {
		final ITypeEnvironmentBuilder typenv = mTypeEnvironment("a=S; b=T", ff);
		doTest(typenv, "a↦b ∈ A", null);
	}

	/**
	 * Ensure that processing an expression that needs to be decomposed produces
	 * the expected predicate in a simple case.
	 */
	@Test
	public void testDecomposeSimpleLeft() {
		final ITypeEnvironmentBuilder typenv = mTypeEnvironment("a=S×T; b=U", ff);
		doTest(typenv, "a↦b ∈ A", //
				"∃x⦂ℤ·∀a1⦂S, a2⦂T· a1↦a2 = a ⇒ a1↦a2↦b ∈ A");
	}

	/**
	 * Ensure that processing an expression that needs to be decomposed produces
	 * the expected predicate in a simple case.
	 */
	@Test
	public void testDecomposeSimpleRight() {
		final ITypeEnvironmentBuilder typenv = mTypeEnvironment("a=S; b=T×U", ff);
		doTest(typenv, "a↦b ∈ A", //
				"∃x⦂ℤ·∀b1⦂T, b2⦂U· b1↦b2 = b ⇒ a↦(b1↦b2) ∈ A");
	}

	/**
	 * Ensure that processing an expression that needs to be decomposed produces
	 * the expected predicate in a complex case.
	 */
	@Test
	public void testDecomposeComplex() {
		final ITypeEnvironmentBuilder typenv = mTypeEnvironment(//
				"a=S; b=T×U; c=T×U×V; d=S×(T×U)×V", ff);
		doTest(typenv, "a↦(b↦c)↦d ∈ A",
				"∃x⦂ℤ·∀d1⦂S, d2⦂T, d3⦂U, d4⦂V, c1⦂T, c2⦂U, c3⦂V, b1⦂T, b2⦂U·"
						+ "b1↦b2 = b ∧ c1↦c2↦c3 = c ∧ d1↦(d2↦d3)↦d4 = d"
						+ "⇒ a↦((b1↦b2)↦(c1↦c2↦c3))↦(d1↦(d2↦d3)↦d4) ∈ A");
	}

	/**
	 * Ensure that processing an expression that needs to be decomposed produces
	 * the expected predicate even when variables are already bound.
	 */
	@Test
	public void testDecomposeAlreadyBound() {
		final ITypeEnvironmentBuilder typenv = mTypeEnvironment("a=S×T; b=U", ff);
		doTest(typenv, "∀a⦂S×T, b⦂U, A⦂ℙ(S×T×U)· a↦b ∈ A", //
				"∀a⦂S×T, b⦂U, A⦂ℙ(S×T×U)· "
						+ "∀a1⦂S, a2⦂T· a1↦a2 = a ⇒ a1↦a2↦b ∈ A");
	}

}

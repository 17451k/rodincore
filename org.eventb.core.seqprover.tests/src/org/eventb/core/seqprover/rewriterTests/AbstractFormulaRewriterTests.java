/*******************************************************************************
 * Copyright (c) 2007 ETH Zurich.
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Rodin @ ETH Zurich
 ******************************************************************************/

package org.eventb.core.seqprover.rewriterTests;

import static org.junit.Assert.assertEquals;

import org.eclipse.core.runtime.Assert;
import org.eventb.core.ast.Expression;
import org.eventb.core.ast.FormulaFactory;
import org.eventb.core.ast.IFormulaRewriter;
import org.eventb.core.ast.ITypeCheckResult;
import org.eventb.core.ast.Predicate;
import org.eventb.core.seqprover.eventbExtensions.Lib;

/**
 * @author htson
 *         <p>
 *         This is the abstract class for testing formula rewriters. This
 *         provides several utility methods including
 *         {@link #predicateTest(String, String)} and
 *         {@link #expressionTest(String, String)}.
 */
public abstract class AbstractFormulaRewriterTests {

	/**
	 * The formula factory used to create different formulas for testing.
	 */
	protected static final FormulaFactory ff = FormulaFactory.getDefault();
	
	/**
	 * The rewriter under test.
	 */
	protected IFormulaRewriter r;
	
	/**
	 * Constructor.
	 * <p>
	 * Client extends this class should provide the rewriter for testing.
	 * 
	 * @param r
	 *            the rewriter under test
	 */
	protected AbstractFormulaRewriterTests(IFormulaRewriter r) {
		this.r = r;
	}
	
	/**
	 * Utility method for making an input predicate from its string image. This
	 * method will make the test failed if the string image does not correspond
	 * to an well-defined and well-typed predicate.
	 * 
	 * @param inputImage
	 *            the string image of a predicate.
	 * @return a predicate corresponding to the string image.
	 */
	protected Predicate makeInputPredicate(String inputImage) {
		Predicate input = Lib.parsePredicate(inputImage);
		if (input == null)
			Assert.isTrue(false, "Input predicate: \n\t" + inputImage
					+ "\n\tcannot be parsed");
		ITypeCheckResult typeCheck = input.typeCheck(ff.makeTypeEnvironment());
		if (!typeCheck.isSuccess())
			Assert.isTrue(false, "Input predicate: \n\t" + inputImage
					+ "\n\tcannot be type checked");
		return input;
	}
	

	/**
	 * Utility method for making an expected predicate from its string image. This
	 * method will make the test failed if the string image does not correspond
	 * to an well-defined and well-typed predicate.
	 * 
	 * @param expectedImage
	 *            the string image of a predicate.
	 * @return a predicate corresponding to the string image.
	 */
	protected Predicate makeExpectedPredicate(String expectedImage) {
		Predicate expected = Lib.parsePredicate(expectedImage);
		if (expected == null)
			Assert.isTrue(false, "Expected predicate: \n\t" + expectedImage
					+ "\n\tcannot be parsed");
		ITypeCheckResult typeCheck = expected.typeCheck(ff.makeTypeEnvironment());
		if (!typeCheck.isSuccess())
			Assert.isTrue(false, "Expected predicate: \n\t" + expectedImage
					+ "\n\tcannot be type checked");
		return expected;
	}
	
	/**
	 * Test the rewriter for rewriting from an input predicate (represented by
	 * its string image) to an expected predicate (represented by its string
	 * image).
	 * 
	 * @param expectedImage
	 *            the string image of the expected predicate.
	 * @param inputImage
	 *            the string image of the input predicate.
	 */
	protected void predicateTest(String expectedImage,
			String inputImage) {
		Predicate input = makeInputPredicate(inputImage);
		Predicate expected = makeExpectedPredicate(expectedImage);
		predicateTest(expected, input);
	}

	/**
	 * Test the rewriter for rewriting from an input predicate to an expected
	 * predicate.
	 * 
	 * @param expected
	 *            the expected predicate.
	 * @param input
	 *            the input predicate.
	 */
	protected void predicateTest(Predicate expected,
			Predicate input) {
		assertEquals("Input expression should be type checked ", true, input
				.isTypeChecked());
		assertEquals("Expected expression should be type checked ", true,
				expected.isTypeChecked());
		assertEquals(input + " --> " + expected, expected, input.rewrite(r));
	}

	/**
	 * Utility method for making an input expression from its string image. This
	 * method will make the test failed if the string image does not correspond
	 * to an well-defined and well-typed expression.
	 * 
	 * @param inputImage
	 *            the string image of an expression.
	 * @return an expression corresponding to the string image.
	 */
	protected Expression makeInputExpression(String inputImage) {
		Expression input = Lib.parseExpression(inputImage);
		if (input == null)
			Assert.isTrue(false, "Input expression: \n\t" + inputImage
					+ "\n\tcannot be parsed");
		ITypeCheckResult typeCheck = input.typeCheck(ff.makeTypeEnvironment());
		if (!typeCheck.isSuccess())
			Assert.isTrue(false, "Input expression: \n\t" + inputImage
					+ "\n\tcannot be type checked");
		return input;
	}
	
	/**
	 * Utility method for making an expected expression from its string image. This
	 * method will make the test failed if the string image does not correspond
	 * to an well-defined and well-typed expression.
	 * 
	 * @param expectedImage
	 *            the string image of an expression.
	 * @return an expression corresponding to the string image.
	 */
	protected Expression makeExpectedExpression(String expectedImage) {
		Expression expected = Lib.parseExpression(expectedImage);
		if (expected == null)
			Assert.isTrue(false, "Expected expression: \n\t" + expectedImage
					+ "\n\tcannot be parsed");
		ITypeCheckResult typeCheck = expected.typeCheck(ff
				.makeTypeEnvironment());
		if (!typeCheck.isSuccess())
			Assert.isTrue(false, "Expected expression: \n\t" + expectedImage
					+ "\n\tcannot be type checked");
		return expected;
	}

	/**
	 * Test the rewriter for rewriting from an input expression to an expected
	 * expression.
	 * 
	 * @param expected
	 *            the expected expression.
	 * @param input
	 *            the input expression.
	 */
	protected void expressionTest(Expression expected,
			Expression input) {
		Assert.isTrue(input.isTypeChecked(),
				"Input expression " + input + " should be type checked");
		Assert.isTrue(expected.isTypeChecked(),
				"Expected expression " + expected + " should be type checked");
		assertEquals("Expected expression: " + expected
				+ " and input expression: " + input
				+ " should be of the same type ", expected.getType(), input
				.getType());
		assertEquals(input + " --> " + expected, expected, input.rewrite(r));
	}

	/**
	 * Test the rewriter for rewriting from an input expression (represented by
	 * its string image) to an expected expression (represented by its string
	 * image).
	 * 
	 * @param expectedImage
	 *            the string image of the expected expression.
	 * @param inputImage
	 *            the string image of the input expression.
	 */
	protected void expressionTest(String expectedImage,
			String inputImage) {
		Expression input = makeInputExpression(inputImage);
		Expression expected = makeExpectedExpression(expectedImage);
		expressionTest(expected, input);
	}
	

}

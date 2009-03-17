/*******************************************************************************
 * Copyright (c) 2009 Systerel and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Systerel - initial API and implementation
 *******************************************************************************/
package org.eventb.core.ast.tests;

import junit.framework.TestCase;

import org.eventb.core.ast.Assignment;
import org.eventb.core.ast.Expression;
import org.eventb.core.ast.Formula;
import org.eventb.core.ast.FormulaFactory;
import org.eventb.core.ast.IParseResult;
import org.eventb.core.ast.IResult;
import org.eventb.core.ast.ITypeCheckResult;
import org.eventb.core.ast.ITypeEnvironment;
import org.eventb.core.ast.Predicate;
import org.eventb.core.ast.Type;

/**
 * Base abstract class for AST tests.
 *
 * @author Laurent Voisin
 */
public abstract class AbstractTests extends TestCase {

	public static final FormulaFactory ff = FormulaFactory.getDefault();

	public static void assertSuccess(String message, IResult result) {
		assertTrue(message, result.isSuccess());
		assertFalse(message, result.hasProblem());
	}

	public static void assertFailure(String message, IResult result) {
		assertFalse(message, result.isSuccess());
		assertTrue(message, result.hasProblem());
	}

	public static Expression parseExpression(String image) {
		final IParseResult result = ff.parseExpression(image);
		assertSuccess("Parse failed for " + image, result);
		return result.getParsedExpression();
	}

	public static Predicate parsePredicate(String image) {
		final IParseResult result = ff.parsePredicate(image);
		assertSuccess("Parse failed for " + image, result);
		return result.getParsedPredicate();
	}

	public static Assignment parseAssignment(String image) {
		final IParseResult result = ff.parseAssignment(image);
		assertSuccess("Parse failed for " + image, result);
		return result.getParsedAssignment();
	}

	public static Type parseType(String image) {
		final IParseResult result = ff.parseType(image);
		assertSuccess("Parse failed for " + image, result);
		return result.getParsedType();
	}

	/**
	 * Type-checks the given formula and returns the type environment made of the
	 * given type environment and the type environment inferred during
	 * type-check.
	 * 
	 * @param formula
	 *            a formula to type-check
	 * @param tenv
	 *            initial type environment
	 * @return augmented type environment
	 */
	public static ITypeEnvironment typeCheck(Formula<?> formula,
			ITypeEnvironment tenv) {
		if (tenv == null) {
			tenv = ff.makeTypeEnvironment();
		}
		final ITypeCheckResult result = formula.typeCheck(tenv);
		assertSuccess(formula.toString(), result);
		assertTrue(formula.isTypeChecked());

		final ITypeEnvironment newEnv = tenv.clone();
		newEnv.addAll(result.getInferredEnvironment());
		return newEnv;
	}
	
	public static ITypeEnvironment typeCheck(Formula<?> formula) {
		return typeCheck(formula, ff.makeTypeEnvironment());
	}

}

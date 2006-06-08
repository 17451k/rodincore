/*******************************************************************************
 * Copyright (c) 2006 ETH Zurich.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.eventb.pp.tests;

import junit.framework.TestCase;

import org.eventb.core.ast.Formula;
import org.eventb.core.ast.FormulaFactory;
import org.eventb.core.ast.IParseResult;
import org.eventb.core.ast.ITypeCheckResult;
import org.eventb.core.ast.ITypeEnvironment;
import org.eventb.core.ast.Predicate;
import org.eventb.core.ast.Type;

public abstract class AbstractTranslationTests extends TestCase {
	
	protected static final FormulaFactory ff = FormulaFactory.getDefault();
	
	protected static final Type INT = ff.makeIntegerType();
	protected static final Type BOOL = ff.makeBooleanType();
	protected static final Type INT_SET = POW(INT);
	protected static final Type ty_S = ff.makeGivenType("S");

	protected static Type POW(Type base) {
		return ff.makePowerSetType(base);
	}

	protected static Type CPROD(Type left, Type right) {
		return ff.makeProductType(left, right);
	}

	protected static Type REL(Type left, Type right) {
		return ff.makeRelationalType(left, right);
	}

	public static Predicate parse(String string, ITypeEnvironment te) {
		IParseResult parseResult = ff.parsePredicate(string);
		assertTrue("Parse error for: " + string +
				"\nProblems: " + parseResult.getProblems(),
				parseResult.isSuccess());
		Predicate pred = parseResult.getParsedPredicate();
		ITypeCheckResult tcResult = pred.typeCheck(te);
		assertTrue(string + " is not typed. Problems: " + tcResult.getProblems(),
				pred.isTypeChecked());
		return pred;
	}

	public static Predicate parse(String string) {
		return parse(string, ff.makeTypeEnvironment());
	}

	public static void assertTypeChecked(Formula formula) {
		assertTrue("Formula is not typed: " + formula, formula.isTypeChecked());
	}

}

/*******************************************************************************
 * Copyright (c) 2005, 2009 ETH Zurich and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     ETH Zurich - initial API and implementation
 *     Systerel - added abstract test class
 *******************************************************************************/
package org.eventb.core.ast.tests;

import static org.eventb.core.ast.tests.FastFactory.mList;
import static org.eventb.core.ast.tests.FastFactory.mTypeEnvironment;

import org.eventb.core.ast.Assignment;
import org.eventb.core.ast.BooleanType;
import org.eventb.core.ast.ITypeEnvironment;
import org.eventb.core.ast.IntegerType;
import org.eventb.core.ast.Predicate;
import org.eventb.core.ast.Type;

public class TestBA extends AbstractTests {

	@Override
	protected void setUp() throws Exception {
		super.setUp();
	}
	
	private static IntegerType INTEGER = ff.makeIntegerType();
	private static BooleanType BOOL = ff.makeBooleanType();
	
	private static Type POW(Type base) {
		return ff.makePowerSetType(base);
	}
	
	private static Type CPROD(Type left, Type right) {
		return ff.makeProductType(left, right);
	}
	
	private ITypeEnvironment defaultTEnv = mTypeEnvironment(
			mList(
					"x",
					"y",
					"A",
					"B",
					"f",
					"Y"
			),
			mList(
					INTEGER,
					INTEGER,
					POW(INTEGER),
					POW(INTEGER),
					POW(CPROD(INTEGER,INTEGER)),
					POW(BOOL)
			)
	);
	
	private class TestItem {
		String input;
		String expected;
		ITypeEnvironment tenv;
		TestItem(String input, String expected, ITypeEnvironment tenv) {
			this.input = input;
			this.expected = expected;
			this.tenv = tenv;
		}
		
		public void doTest() throws Exception {
			Assignment inA = parseAssignment(input);
			ITypeEnvironment newEnv = typeCheck(inA, tenv);
			
			Predicate inBA = inA.getBAPredicate(ff);
			assertTrue(input + "\n" + inBA.toString() + "\n"
					+ inBA.getSyntaxTree() + "\n",
					inBA.isTypeChecked());
			
			Predicate exP = parsePredicate(expected).flatten(ff);
			typeCheck(exP, newEnv);
			assertEquals(input, exP, inBA);
		}
	}

	private TestItem[] testItems = new TestItem[] {
			new TestItem("x≔x+y", "x'=x+y", defaultTEnv),
			new TestItem("x:∈A", "x'∈A", defaultTEnv),
			new TestItem("x:\u2223 x'∈A", "x'∈A", defaultTEnv)
	};
	
	public void testBA() throws Exception {
		for (TestItem item : testItems)
			item.doTest();
	}

	
}
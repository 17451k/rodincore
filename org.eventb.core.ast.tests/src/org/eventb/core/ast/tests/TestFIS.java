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
 *     Systerel - mathematical language v2
 *******************************************************************************/
package org.eventb.core.ast.tests;

import static org.eventb.core.ast.tests.FastFactory.mList;
import static org.eventb.core.ast.tests.FastFactory.mTypeEnvironment;

import org.eventb.core.ast.Assignment;
import org.eventb.core.ast.BooleanType;
import org.eventb.core.ast.ITypeEnvironment;
import org.eventb.core.ast.IntegerType;
import org.eventb.core.ast.Predicate;

public class TestFIS extends AbstractTests {
	
	@Override
	protected void setUp() throws Exception {
		super.setUp();
	}
	
	private static IntegerType INTEGER = ff.makeIntegerType();
	private static BooleanType BOOL = ff.makeBooleanType();

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
		
		public void test() throws Exception {
			Assignment inA = parseAssignment(input);
			ITypeEnvironment newEnv = typeCheck(inA, tenv);
			
			Predicate inFIS = inA.getFISPredicate(ff);
			assertTrue(input + "\n" + inFIS.toString() + "\n"
					+ inFIS.getSyntaxTree() + "\n",
					inFIS.isTypeChecked());
			
			Predicate exP = parsePredicate(expected).flatten(ff);
			typeCheck(exP, newEnv);
			
			assertEquals(input, exP, inFIS);
		}
	}
	
	private TestItem[] testItems = new TestItem[] {
			new TestItem("x≔x+y", "⊤", defaultTEnv),
			new TestItem("x,y≔y,x", "⊤", defaultTEnv),
			new TestItem("x:∈A", "A≠∅", defaultTEnv),
			new TestItem("x:\u2223 x'∈A", "∃x'·x'∈A", defaultTEnv),
			// Test where no after-value occurs in the condition.
			new TestItem("x:\u2223 x∈A", "x∈A", defaultTEnv),
			// Test where one after-value doesn't occur in the condition.
			new TestItem("x,y:\u2223 x'∈A", "∃x'·x'∈A", defaultTEnv),
	};
	
	public void testFIS() throws Exception {
		for(TestItem item : testItems)
			item.test();
	}
}

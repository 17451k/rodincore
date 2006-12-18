/*******************************************************************************
 * Copyright (c) 2006 ETH Zurich.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.eventb.core.ast.tests;

import org.eventb.internal.core.ast.IntStack;

import junit.framework.TestCase;

/**
 * Ensures that the IntStack class works properly.
 *
 * @author Laurent Voisin
 */
public class TestIntStack extends TestCase {

	private void assertStack(IntStack stack, int... expected) {
		final int length = expected.length;
		if (length == 0) {
			assertTrue("Stack should be empty", stack.isEmpty());
		} else {
			assertFalse("Stack should not be empty", stack.isEmpty());
			assertEquals("Wrong stack top", expected[length-1], stack.top());
		}
		
		int[] actual = stack.toArray();
		assertEquals("Differing number of elements", length, actual.length);
		for (int i = 0; i < length; ++ i) {
			assertEquals("Differing value at index " + i, expected[i], actual[i]);
		}
	}
	
	public final void testIsEmpty() {
		final IntStack stack = new IntStack();
		assertTrue(stack.isEmpty());
		stack.push(0);
		assertFalse(stack.isEmpty());
		stack.pop();
		assertTrue(stack.isEmpty());
	}

	public final void testPush() {
		final IntStack stack = new IntStack();
		assertStack(stack);
		stack.push(1);
		assertStack(stack, 1);
		stack.push(2);
		assertStack(stack, 1, 2);
	}

	public final void testPop() {
		final IntStack stack = new IntStack();
		assertStack(stack);
		stack.push(1);
		stack.push(2);
		assertStack(stack, 1, 2);
		stack.pop();
		assertStack(stack, 1);
		stack.pop();
		assertStack(stack);
	}

	public final void testRealloc() {
		final int size = 1000; 
		IntStack stack = new IntStack();
		int[] expected = new int[size];
		for (int i = 0; i < size; ++ i) {
			stack.push(i);
			expected[i] = i;
		}
		assertStack(stack, expected);
	}

	public final void testIncrementTop() {
		IntStack stack = new IntStack();
		stack.push(1);
		assertStack(stack, 1);
		stack.incrementTop();
		assertStack(stack, 2);
	}

	public final void testReplaceTop() {
		IntStack stack = new IntStack();
		stack.push(1);
		assertStack(stack, 1);
		stack.replaceTop(5);
		assertStack(stack, 5);
	}

	public final void testToString() {
		IntStack stack = new IntStack();
		stack.push(1);
		assertEquals("[1]", stack.toString());
		stack.push(2);
		assertEquals("[1, 2]", stack.toString());
		stack.pop();
		assertEquals("[1]", stack.toString());
	}

}

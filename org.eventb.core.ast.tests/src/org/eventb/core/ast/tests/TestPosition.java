package org.eventb.core.ast.tests;

import org.eventb.core.ast.IPosition;
import org.eventb.internal.core.ast.IntStack;
import org.eventb.internal.core.ast.Position;

import junit.framework.TestCase;

/**
 * Tests for interface {@link IPosition} and its standard implementation.
 * 
 * @author Laurent Voisin
 */
public class TestPosition extends TestCase {

	private void assertSameSign(int expected, int actual) {
		if (expected == 0 && actual != 0)
			fail("Expected zero, was " + actual);
		else if (expected < 0 && actual >= 0)
			fail("Expected negative, was " + actual);
		else if (expected > 0 && actual <= 0)
			fail("Expected positive, was " + actual);
	}
	
	/*
	 * Ensures that the total order on position is implemented correctly, and is
	 * compatible with equality.
	 */
	private void assertComparison(int expected, IPosition left, IPosition right) {
		assertSameSign(expected, left.compareTo(right));
		assertSameSign(- expected, right.compareTo(left));
		if (expected == 0) {
			assertEquals(left, right);
			assertEquals(right, left);
		}
	}
	
	private IPosition mPos(String image) {
		if (image.length() == 0)
			return IPosition.ROOT;
		final IntStack stack = new IntStack();
		for (String token: image.split("\\.")) {
			stack.push(Integer.parseInt(token));
		}
		final IPosition pos = new Position(stack);
		assertEquals(image, pos.toString());
		return pos;
	}
	
	public final void testCompareTo() {
		assertComparison(0, mPos(""),      mPos(""));
		assertComparison(0, mPos("1"),     mPos("1"));
		assertComparison(0, mPos("1.2.3"), mPos("1.2.3"));
		assertComparison(0, mPos("3.1.2"), mPos("3.1.2"));
		
		assertComparison(-1, mPos(""),      mPos("0"));
		assertComparison(-1, mPos(""),      mPos("1.2"));
		assertComparison(-1, mPos("0"),     mPos("0.0"));
		assertComparison(-1, mPos("0"),     mPos("0.1"));
		assertComparison(-1, mPos("0"),     mPos("1"));
		assertComparison(-1, mPos("1.1"),   mPos("1.2"));
		assertComparison(-1, mPos("1.2"),   mPos("1.2.0"));
		assertComparison(-1, mPos("1.2"),   mPos("1.2.1"));
		assertComparison(-1, mPos("1.2"),   mPos("1.2.3.4"));
	}

	private void assertFirstChild(String image) {
		final IPosition pos = mPos(image);
		final IPosition expect = mPos(image.length() == 0 ? "0" : image + ".0");
		final IPosition actual = pos.getFirstChild();
		assertEquals(expect, actual);
		
		// Compatibility with other methods
		assertEquals(pos, actual.getParent());
		assertFalse(actual.isRoot());
		assertTrue(actual.isFirstChild());
	}
	
	public final void testGetFirstChild() {
		assertFirstChild("");
		assertFirstChild("0");
		assertFirstChild("2");
		assertFirstChild("1.2.3");
	}

	private void assertNextSibling(String image, String expected) {
		final IPosition pos = mPos(image);
		assertFalse(pos.isRoot());

		final IPosition expect = mPos(expected);
		final IPosition actual = pos.getNextSibling();
		assertEquals(expect, actual);
		
		// Compatibility with other methods
		assertEquals(pos.getParent(), actual.getParent());
		assertEquals(pos, actual.getPreviousSibling());
		assertFalse(actual.isRoot());
		assertFalse(actual.isFirstChild());
	}
	
	public final void testGetNextSibling() {
		assertNextSibling("0", "1");
		assertNextSibling("1", "2");
		assertNextSibling("0.0", "0.1");
		assertNextSibling("1.2.3", "1.2.4");
		
		try {
			IPosition.ROOT.getNextSibling();
			fail("No exception raised");
		} catch (IllegalStateException e) {
			// pass
		}
	}

	public final void testGetParent() {
		assertEquals(mPos(""), mPos("0").getParent());
		assertEquals(mPos(""), mPos("1").getParent());
		assertEquals(mPos("0"), mPos("0.0").getParent());
		assertEquals(mPos("0"), mPos("0.2").getParent());
		assertEquals(mPos("1.2"), mPos("1.2.3").getParent());
		
		try {
			IPosition.ROOT.getParent();
			fail("No exception raised");
		} catch (IllegalStateException e) {
			// pass
		}
	}

	private void failPreviousSibling(String image) {
		IPosition pos = mPos(image);
		assertTrue(pos.isRoot() || pos.isFirstChild());

		try {
			pos.getPreviousSibling();
			fail("No exception raised");
		} catch (IllegalStateException e) {
			// pass
		}
	}

	public final void testGetPreviousSibling() {
		// Tests for regular values already done with testGetNextSibling.

		failPreviousSibling("");
		failPreviousSibling("0");
		failPreviousSibling("1.2.0");
	}

	public final void testIsFirstChild() {
		assertFalse(mPos("").isFirstChild());
		assertTrue(mPos("0").isFirstChild());
		assertFalse(mPos("1").isFirstChild());
		assertFalse(mPos("2").isFirstChild());
		assertTrue(mPos("0.0").isFirstChild());
		assertFalse(mPos("0.1").isFirstChild());
		assertFalse(mPos("0.2").isFirstChild());
		assertTrue(mPos("1.2.3.0").isFirstChild());
		assertFalse(mPos("1.2.3.4").isFirstChild());
	}

	public final void testIsRoot() {
		assertTrue(mPos("").isRoot());
		assertFalse(mPos("0").isRoot());
		assertFalse(mPos("2").isRoot());
		assertFalse(mPos("0.0").isRoot());
		assertFalse(mPos("0.2").isRoot());
		assertFalse(mPos("1.2.3.0").isRoot());
		assertFalse(mPos("1.2.3.4").isRoot());
	}
	
	public void testRoot() {
		final IPosition root = IPosition.ROOT;
		assertTrue(root.isRoot());
		assertEquals("", root.toString());
	}

}

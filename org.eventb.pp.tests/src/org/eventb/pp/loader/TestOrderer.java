package org.eventb.pp.loader;

import org.eventb.core.ast.ITypeEnvironment;
import org.eventb.core.ast.Predicate;
import org.eventb.internal.pp.core.elements.terms.AbstractPPTest;
import org.eventb.internal.pp.core.elements.terms.Util;
import org.eventb.internal.pp.loader.predicate.AbstractContext;
import org.eventb.internal.pp.loader.predicate.INormalizedFormula;

public class TestOrderer extends AbstractPPTest {

	private static ITypeEnvironment typenv = ff.makeTypeEnvironment();
	static {
		typenv.addName("x0", ty_A);
		typenv.addName("x1", ty_B);
		typenv.addName("a", ty_S);
		typenv.addName("e", ty_BOOL);
		typenv.addName("f", REL(ty_A, ty_B));
		typenv.addName("n", INT);
		typenv.addName("N", POW(INT));
		typenv.addName("S", POW(ty_S));
		typenv.addName("P", POW(ty_B));
		typenv.addName("Q", POW(ty_A));
		typenv.addName("R", POW(ty_A));
		typenv.addName("U", POW(ty_U));
		typenv.addName("M", REL(CPROD(ty_B, ty_A), ty_A));
		typenv.addName("SS", POW(ty_S));
		typenv.addName("T", REL(INT, INT));
		typenv.addName("TT", REL(CPROD(INT, INT), INT));
	}

	public static void doTest(String... images) {
		final AbstractContext context = new AbstractContext();
		INormalizedFormula formula = null;
		for (String image : images) {
			final Predicate pred = Util.parsePredicate(image, typenv);
			context.load(pred, false);
			final INormalizedFormula newFormula = context.getLastResult();
			if (formula == null) {
				formula = newFormula;
				continue;
			}
			assertEquals(formula, newFormula);
		}
	}

	public void testOrdering() {
		doTest("a ∈ S ∨ d ∈ U", "d ∈ U ∨ a ∈ S");
		doTest("a ∈ S ∨ ¬(a ∈ S)", "¬(a ∈ S) ∨ a ∈ S");
		doTest("a = b ∨ a ∈ S", "a ∈ S ∨ a = b");
		doTest("a = b ∨ n = 1", "n = 1 ∨ a = b");
		doTest("a = b ∨ ¬(a = b)", "¬(a = b) ∨ a = b");
		doTest("a = b ∨ ¬(a ∈ S)", "¬(a ∈ S) ∨ a = b");
		doTest("¬(a = b) ∨ a ∈ S", "a ∈ S ∨ ¬(a = b)");
		doTest("n < 1 ∨ a = b", "a = b ∨ n < 1");
		doTest("¬(a ∈ S ∨ d ∈ U) ∨ a = b", "a = b ∨ ¬(a ∈ S ∨ d ∈ U)");
		doTest("¬(a ∈ S ∨ d ∈ U) ∨ b ∈ S", "b ∈ S ∨ ¬(a ∈ S ∨ d ∈ U)");
		doTest("(∀x·x ∈ S) ∨ (∀x·x ∈ U)", "(∀x·x ∈ U) ∨ (∀x·x ∈ S)");
		doTest("a ∈ S ∨ (∀x·x ∈ U)", "(∀x·x ∈ U) ∨ a ∈ S");
		doTest("a = b ∨ (∀x·x ∈ U)", "(∀x·x ∈ U) ∨ a = b");
	}

}

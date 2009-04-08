package org.evenb.ui.prover.tests;

import junit.framework.TestCase;

import org.eventb.core.ast.IParseResult;
import org.eventb.core.ast.Predicate;
import org.eventb.core.seqprover.eventbExtensions.Lib;
import org.eventb.internal.ui.prover.PredicateUtil;

public class TestPrettyPrintPredicate extends TestCase {

	private void predTest(String msg, String predString,
			String expectedPrettyPrint) {
		IParseResult parseResult = Lib.ff.parsePredicate(predString);
		assertFalse("Parse Successful", parseResult.hasProblem());
		Predicate parsedPred = parseResult.getParsedPredicate();

		String prettyPrint = PredicateUtil.prettyPrint(30, predString,
				parsedPred);

		assertEquals(msg, expectedPrettyPrint, prettyPrint);
	}

	public void testAssociativePredicate() {
		predTest("And 1", "⊤\u2227⊤", "⊤ \u2227 ⊤");
		predTest(
				"And 2",
				"⊤\u2227⊤\u2227⊤\u2227⊤\u2227⊤\u2227⊤\u2227⊤\u2227⊤\u2227⊤\u2227⊤\u2227⊤\u2227⊤",
				"⊤ \u2227 ⊤ \u2227 ⊤ \u2227 ⊤ \u2227 ⊤ \u2227 ⊤ \u2227 ⊤ \u2227"
						+ "\n" + "⊤ \u2227 ⊤ \u2227 ⊤ \u2227 ⊤ \u2227 ⊤");
		predTest("Or 1", "⊤" + "\u2228" + "⊤", "⊤ \u2228 ⊤");
		predTest(
				"Or 2",
				"⊤\u2228⊤\u2228⊤\u2228⊤\u2228⊤\u2228⊤\u2228⊤\u2228⊤\u2228⊤\u2228⊤\u2228⊤\u2228⊤",
				"⊤ \u2228 ⊤ \u2228 ⊤ \u2228 ⊤ \u2228 ⊤ \u2228 ⊤ \u2228 ⊤ \u2228"
						+ "\n" + "⊤ \u2228 ⊤ \u2228 ⊤ \u2228 ⊤ \u2228 ⊤");
		predTest("And with quantifier",
				"a=beeeeeeeeeeeeeeeeeeeeeeeeeeeeeeh∧"
				+ "(∃x·∀y·x+y=ceeeeeeeeeeeeeeeeeeeeeeeeeh)",
				"a=beeeeeeeeeeeeeeeeeeeeeeeeeeeeeeh∧\n"
				+ "(∃ x · \n"
				+ "  ∀ y · \n"
				+ "    x+y=ceeeeeeeeeeeeeeeeeeeeeeeeeh)"
		);
	}

	public void testBinaryPredicate() {
		predTest("Imply 1", "⊤" + "\u21d2" + "⊤", "⊤ \u21d2 ⊤");
		predTest(
				"Imply 2",
				"⊤\u2227⊤\u2227⊤\u2227⊤\u2227⊤\u21d2⊤\u2228⊤\u2228⊤\u2228⊤\u2228⊤\u2228⊤\u2228⊤\u2228⊤",
				"  ⊤ \u2227 ⊤ \u2227 ⊤ \u2227 ⊤ \u2227 ⊤"
						+ "\n\u21d2\n"
						+ "  ⊤ \u2228 ⊤ \u2228 ⊤ \u2228 ⊤ \u2228 ⊤ \u2228 ⊤ \u2228 ⊤ \u2228"
						+ "\n" + "  ⊤");
		predTest("Equivalent", "⊤" + "\u21d4" + "⊤", "⊤ \u21d4 ⊤");
	}

	public void testLiteralPredicate() {
		// TODO implement this test
	}

	public void testQuantifiedPredicate() {
		// TODO implement this test
		predTest("Forall", "x∈dom(f)⇒f(x)∈T", "x∈dom(f) ⇒ f(x)∈T");
	}

	public void testRelationalPred() {
		predTest("Equal", "1" + "=" + "2", "1=2");
		predTest("Not Equal", "1" + "\u2260" + "2", "1" + "\u2260" + "2");
		predTest("Less Than", "1" + "<" + "2", "1<2");
		predTest("Less Than Equal", "1" + "\u2264" + "2", "1" + "\u2264" + "2");
		predTest("Greater Than", "1" + ">" + "2", "1>2");
		predTest("Greater Than Equal", "1" + "\u2265" + "2", "1\u2265" + "2");
		predTest("In", "1" + "\u2208" + "ℕ", "1\u2208ℕ");
		predTest("Not In", "1" + "\u2209" + "ℕ", "1\u2209ℕ");
		predTest("Subset", "ℕ" + "\u2282" + "ℕ", "ℕ\u2282ℕ");
		predTest("Not Subset", "ℕ" + "\u2284" + "ℕ", "ℕ\u2284ℕ");
		predTest("Subset Equal", "ℕ" + "\u2286" + "ℕ", "ℕ\u2286ℕ");
		predTest("Not Subset Equal", "ℕ" + "\u2288" + "ℕ", "ℕ\u2288ℕ");
	}

	public void testSimplePredicate() {
		// TODO implement this test
	}

	public void testUnaryPredicate() {
		predTest("Not", "\u00ac" + "⊤", " \u00ac " + "⊤");
	}

	public void testBrackets() {
		predTest("Brackets", "(1=2" + "\u2228" + "2=3" + "\u2228" + "3=4)"
				+ "\u2227" + "4=5" + "\u2227" + "5=6", "(1=2" + " \u2228 "
				+ "2=3" + " \u2228 " + "3=4)" + "  \u2227  " + "4 = 5"
				+ "  \u2227" + "\n" + "5=6");
	}

}

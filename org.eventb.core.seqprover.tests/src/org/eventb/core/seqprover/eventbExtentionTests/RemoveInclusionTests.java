package org.eventb.core.seqprover.eventbExtentionTests;

import static org.junit.Assert.assertTrue;

import java.util.List;

import org.eventb.core.ast.FormulaFactory;
import org.eventb.core.ast.IPosition;
import org.eventb.core.ast.Predicate;
import org.eventb.core.seqprover.IProofRule;
import org.eventb.core.seqprover.IProverSequent;
import org.eventb.core.seqprover.IReasoner;
import org.eventb.core.seqprover.IReasonerFailure;
import org.eventb.core.seqprover.IReasonerOutput;
import org.eventb.core.seqprover.eventbExtensions.Tactics;
import org.eventb.core.seqprover.tests.TestLib;
import org.eventb.internal.core.seqprover.eventbExtensions.rewriters.RemoveInclusion;
import org.eventb.internal.core.seqprover.eventbExtensions.rewriters.RemoveNegation;
import org.junit.Test;

/**
 * Unit tests for the rn reasoner
 * 
 * @author htson
 */
public class RemoveInclusionTests extends AbstractTests {

	private static final IReasoner riReasoner = new RemoveInclusion();

	private static final FormulaFactory ff = FormulaFactory.getDefault();

	Predicate P1 = TestLib.genPred("(0 = 1) ⇒ {1} ⊆ {1, 2}");

	Predicate P2 = TestLib.genPred("∀x·x = TRUE ⇒ {x} ⊆ {x, FALSE}");

	Predicate P3 = TestLib.genPred("(0 = 1) ⇒ ∅ ⊆ {1, 2}");

	Predicate P4 = TestLib.genPred("∀x·x = TRUE ⇒ ∅ ⊆ {x, FALSE}");

	Predicate P5 = TestLib.genPred("(0 = 1) ⇒ {1, 2} ⊆ {1, 2}");

	Predicate P6 = TestLib.genPred("∀x·x = TRUE ⇒ {x, FALSE} ⊆ {x, FALSE}");

	Predicate P7 = TestLib.genPred("(0 = 1) ⇒ {1 ↦ 2} ⊆ {1 ↦ 2, 2 ↦ 3}");

	Predicate P8 = TestLib.genPred("∀x·x = TRUE ⇒ {x ↦ 2} ⊆ {x ↦ 2, x ↦ 3}");

	Predicate P9 = TestLib
			.genPred("(0 = 1) ⇒ {(0 ↦ 2) ↦ (2 ↦ 3)} ⊆ {(1 ↦ 2) ↦ (2 ↦ 3)}");

	Predicate P10 = TestLib
			.genPred("∀x·x = TRUE ⇒ {FALSE ↦ (2 ↦ 2) ↦ x} ⊆ {x ↦ (2 ↦ 2) ↦ TRUE}");

	Predicate P11 = TestLib.genPred("(0 = 1) ⇒ {1 ↦ {2}} ⊆ {1 ↦ {2}, 2 ↦ {3}}");

	Predicate P12 = TestLib
			.genPred("∀x·x = TRUE ⇒ {{x} ↦ 2} ⊆ {{x} ↦ 2, {x} ↦ 3}");

	@Test
	public void testGoalNotApplicable() {
		IProverSequent seq;
		IReasonerOutput output;

		// Goal is not applicable
		seq = TestLib.genSeq(" ⊤ |- ⊤ ");
		output = riReasoner.apply(seq, new RemoveNegation.Input(null, ff
				.makePosition("")), null);
		assertTrue(output instanceof IReasonerFailure);
	}

	@Test
	public void testPositionGoalIncorrect() {
		IProverSequent seq;
		IReasonerOutput output;

		// Position in goal is incorrect
		seq = TestLib.genSeq(" ⊤ |- " + P1);
		output = riReasoner.apply(seq, new RemoveNegation.Input(null, ff
				.makePosition("0")), null);
		assertTrue(output instanceof IReasonerFailure);

		// Position in goal is incorrect
		seq = TestLib.genSeq(" ⊤ |- " + P2);
		output = riReasoner.apply(seq, new RemoveNegation.Input(null, ff
				.makePosition("0.1")), null);
		assertTrue(output instanceof IReasonerFailure);

		// Position in goal is incorrect
		seq = TestLib.genSeq(" ⊤ |- " + P3);
		output = riReasoner.apply(seq, new RemoveNegation.Input(null, ff
				.makePosition("0")), null);
		assertTrue(output instanceof IReasonerFailure);

		// Position in goal is incorrect
		seq = TestLib.genSeq(" ⊤ |- " + P4);
		output = riReasoner.apply(seq, new RemoveNegation.Input(null, ff
				.makePosition("0.1")), null);
		assertTrue(output instanceof IReasonerFailure);

		// Position in goal is incorrect
		seq = TestLib.genSeq(" ⊤ |- " + P5);
		output = riReasoner.apply(seq, new RemoveNegation.Input(null, ff
				.makePosition("0")), null);
		assertTrue(output instanceof IReasonerFailure);

		// Position in goal is incorrect
		seq = TestLib.genSeq(" ⊤ |- " + P6);
		output = riReasoner.apply(seq, new RemoveNegation.Input(null, ff
				.makePosition("0.1")), null);
		assertTrue(output instanceof IReasonerFailure);

		// Position in goal is incorrect
		seq = TestLib.genSeq(" ⊤ |- " + P7);
		output = riReasoner.apply(seq, new RemoveNegation.Input(null, ff
				.makePosition("0")), null);
		assertTrue(output instanceof IReasonerFailure);

		// Position in goal is incorrect
		seq = TestLib.genSeq(" ⊤ |- " + P8);
		output = riReasoner.apply(seq, new RemoveNegation.Input(null, ff
				.makePosition("0.1")), null);
		assertTrue(output instanceof IReasonerFailure);

		// Position in goal is incorrect
		seq = TestLib.genSeq(" ⊤ |- " + P9);
		output = riReasoner.apply(seq, new RemoveNegation.Input(null, ff
				.makePosition("0")), null);
		assertTrue(output instanceof IReasonerFailure);

		// Position in goal is incorrect
		seq = TestLib.genSeq(" ⊤ |- " + P10);
		output = riReasoner.apply(seq, new RemoveNegation.Input(null, ff
				.makePosition("0.1")), null);
		assertTrue(output instanceof IReasonerFailure);

		// Position in goal is incorrect
		seq = TestLib.genSeq(" ⊤ |- " + P11);
		output = riReasoner.apply(seq, new RemoveNegation.Input(null, ff
				.makePosition("0")), null);
		assertTrue(output instanceof IReasonerFailure);

		// Position in goal is incorrect
		seq = TestLib.genSeq(" ⊤ |- " + P12);
		output = riReasoner.apply(seq, new RemoveNegation.Input(null, ff
				.makePosition("0.1")), null);
		assertTrue(output instanceof IReasonerFailure);
	}

	/**
	 * Tests for correct reasoner failure
	 */
	@Test
	public void testHypNotPresent() {
		IProverSequent seq;
		IReasonerOutput output;

		// Hyp is not present
		seq = TestLib.genSeq(" ⊤ |- ⊤ ");
		output = riReasoner.apply(seq, new RemoveNegation.Input(P1, ff
				.makePosition("")), null);
		assertTrue(output instanceof IReasonerFailure);
	}

	@Test
	public void testHypPositionIncorrect() {
		IProverSequent seq;
		IReasonerOutput output;

		// Position in hyp is incorrect
		seq = TestLib.genSeq(P1 + " |- ⊤ ");
		output = riReasoner.apply(seq, new RemoveNegation.Input(P1, ff
				.makePosition("0")), null);
		assertTrue(output instanceof IReasonerFailure);

		// Position in hyp is incorrect
		seq = TestLib.genSeq(P2 + " |- ⊤ ");
		output = riReasoner.apply(seq, new RemoveNegation.Input(P2, ff
				.makePosition("0.1")), null);
		assertTrue(output instanceof IReasonerFailure);

		// Position in hyp is incorrect
		seq = TestLib.genSeq(P3 + " |- ⊤ ");
		output = riReasoner.apply(seq, new RemoveNegation.Input(P3, ff
				.makePosition("0")), null);
		assertTrue(output instanceof IReasonerFailure);

		// Position in hyp is incorrect
		seq = TestLib.genSeq(P4 + " |- ⊤ ");
		output = riReasoner.apply(seq, new RemoveNegation.Input(P4, ff
				.makePosition("0.1")), null);
		assertTrue(output instanceof IReasonerFailure);

		// Position in hyp is incorrect
		seq = TestLib.genSeq(P5 + " |- ⊤ ");
		output = riReasoner.apply(seq, new RemoveNegation.Input(P5, ff
				.makePosition("0")), null);
		assertTrue(output instanceof IReasonerFailure);

		// Position in hyp is incorrect
		seq = TestLib.genSeq(P6 + " |- ⊤ ");
		output = riReasoner.apply(seq, new RemoveNegation.Input(P6, ff
				.makePosition("0.1")), null);
		assertTrue(output instanceof IReasonerFailure);

		// Position in hyp is incorrect
		seq = TestLib.genSeq(P7 + " |- ⊤ ");
		output = riReasoner.apply(seq, new RemoveNegation.Input(P7, ff
				.makePosition("0")), null);
		assertTrue(output instanceof IReasonerFailure);

		// Position in hyp is incorrect
		seq = TestLib.genSeq(P8 + " |- ⊤ ");
		output = riReasoner.apply(seq, new RemoveNegation.Input(P8, ff
				.makePosition("0.1")), null);
		assertTrue(output instanceof IReasonerFailure);

		// Position in hyp is incorrect
		seq = TestLib.genSeq(P9 + " |- ⊤ ");
		output = riReasoner.apply(seq, new RemoveNegation.Input(P9, ff
				.makePosition("0")), null);
		assertTrue(output instanceof IReasonerFailure);

		// Position in hyp is incorrect
		seq = TestLib.genSeq(P10 + " |- ⊤ ");
		output = riReasoner.apply(seq, new RemoveNegation.Input(P10, ff
				.makePosition("0.1")), null);
		assertTrue(output instanceof IReasonerFailure);

		// Position in hyp is incorrect
		seq = TestLib.genSeq(P11 + " |- ⊤ ");
		output = riReasoner.apply(seq, new RemoveNegation.Input(P11, ff
				.makePosition("0")), null);
		assertTrue(output instanceof IReasonerFailure);

		// Position in hyp is incorrect
		seq = TestLib.genSeq(P12 + " |- ⊤ ");
		output = riReasoner.apply(seq, new RemoveNegation.Input(P12, ff
				.makePosition("0.1")), null);
		assertTrue(output instanceof IReasonerFailure);
	}

	/**
	 * Tests for applicable positions
	 */
	@Test
	public void testGetPositions() {
		List<IPosition> positions;
		positions = Tactics.riGetPositions(P1);
		assertPositions("Position found for P1 ", "1", positions);
		positions = Tactics.riGetPositions(P2);
		assertPositions("Position found for P2 ", "1.1", positions);
		positions = Tactics.riGetPositions(P3);
		assertPositions("Position found for P3 ", "1", positions);
		positions = Tactics.riGetPositions(P4);
		assertPositions("Position found for P4 ", "1.1", positions);
		positions = Tactics.riGetPositions(P5);
		assertPositions("Position found for P5 ", "1", positions);
		positions = Tactics.riGetPositions(P6);
		assertPositions("Position found for P6 ", "1.1", positions);
		positions = Tactics.riGetPositions(P7);
		assertPositions("Position found for P7 ", "1", positions);
		positions = Tactics.riGetPositions(P8);
		assertPositions("Position found for P8 ", "1.1", positions);
		positions = Tactics.riGetPositions(P9);
		assertPositions("Position found for P9 ", "1", positions);
		positions = Tactics.riGetPositions(P10);
		assertPositions("Position found for P10 ", "1.1", positions);
		positions = Tactics.riGetPositions(P11);
		assertPositions("Position found for P11 ", "1", positions);
		positions = Tactics.riGetPositions(P12);
		assertPositions("Position found for P12 ", "1.1", positions);
	}

	/**
	 * Tests for reasoner success
	 */
	@Test
	public void testSuccess() {

		IProverSequent seq;
		IProverSequent[] newSeqs;
		IReasonerOutput output;

		seq = TestLib.genSeq(" ⊤ |- " + P1);
		output = riReasoner.apply(seq, new RemoveNegation.Input(null, ff
				.makePosition("1")), null);
		assertTrue(output instanceof IProofRule);
		newSeqs = ((IProofRule) output).apply(seq);
		assertSequents("Applied successfully goal P1 ",
				"{}[][][⊤] |- 0=1⇒(∀x·x∈{1}⇒x∈{1,2})", newSeqs);

		seq = TestLib.genSeq(P1 + " |- ⊤ ");
		output = riReasoner.apply(seq, new RemoveNegation.Input(P1, ff
				.makePosition("1")), null);
		assertTrue(output instanceof IProofRule);
		newSeqs = ((IProofRule) output).apply(seq);
		assertSequents("Applied successfully hyp P1 ",
				"{}[0=1⇒{1}⊆{1,2}][][0=1⇒(∀x·x∈{1}⇒x∈{1,2})] |- ⊤", newSeqs);

		seq = TestLib.genSeq(" ⊤ |- " + P2);
		output = riReasoner.apply(seq, new RemoveNegation.Input(null, ff
				.makePosition("1.1")), null);
		assertTrue(output instanceof IProofRule);
		newSeqs = ((IProofRule) output).apply(seq);
		assertSequents("Applied successfully goal P2 ",
				"{}[][][⊤] |- ∀x·x=TRUE⇒(∀x0·x0∈{x}⇒x0∈{x,FALSE})", newSeqs);

		seq = TestLib.genSeq(P2 + " |- ⊤ ");
		output = riReasoner.apply(seq, new RemoveNegation.Input(P2, ff
				.makePosition("1.1")), null);
		assertTrue(output instanceof IProofRule);
		newSeqs = ((IProofRule) output).apply(seq);
		assertSequents("Applied successfully hyp P2 ",
				"{}[∀x·x=TRUE⇒{x}⊆{x,FALSE}][][∀x·x=TRUE⇒(∀x0·x0∈{x}⇒x0∈{x,FALSE})] |- ⊤",
				newSeqs);

		seq = TestLib.genSeq(" ⊤ |- " + P3);
		output = riReasoner.apply(seq, new RemoveNegation.Input(null, ff
				.makePosition("1")), null);
		assertTrue(output instanceof IProofRule);
		newSeqs = ((IProofRule) output).apply(seq);
		assertSequents("Applied successfully goal P3 ", "{}[][][⊤] |- 0=1⇒⊤",
				newSeqs);

		seq = TestLib.genSeq(P3 + " |- ⊤ ");
		output = riReasoner.apply(seq, new RemoveNegation.Input(P3, ff
				.makePosition("1")), null);
		assertTrue(output instanceof IProofRule);
		newSeqs = ((IProofRule) output).apply(seq);
		assertSequents("Applied successfully hyp P3 ",
				"{}[0=1⇒∅⊆{1,2}][][0=1⇒⊤] |- ⊤", newSeqs);

		seq = TestLib.genSeq(" ⊤ |- " + P4);
		output = riReasoner.apply(seq, new RemoveNegation.Input(null, ff
				.makePosition("1.1")), null);
		assertTrue(output instanceof IProofRule);
		newSeqs = ((IProofRule) output).apply(seq);
		assertSequents("Applied successfully goal P4 ",
				"{}[][][⊤] |- ∀x·x=TRUE⇒⊤", newSeqs);

		seq = TestLib.genSeq(P4 + " |- ⊤ ");
		output = riReasoner.apply(seq, new RemoveNegation.Input(P4, ff
				.makePosition("1.1")), null);
		assertTrue(output instanceof IProofRule);
		newSeqs = ((IProofRule) output).apply(seq);
		assertSequents("Applied successfully hyp P4 ",
				"{}[∀x·x=TRUE⇒∅⊆{x,FALSE}][][∀x·x=TRUE⇒⊤] |- ⊤", newSeqs);

		seq = TestLib.genSeq(" ⊤ |- " + P5);
		output = riReasoner.apply(seq, new RemoveNegation.Input(null, ff
				.makePosition("1")), null);
		assertTrue(output instanceof IProofRule);
		newSeqs = ((IProofRule) output).apply(seq);
		assertSequents("Applied successfully goal P5 ", "{}[][][⊤] |- 0=1⇒⊤",
				newSeqs);

		seq = TestLib.genSeq(P5 + " |- ⊤ ");
		output = riReasoner.apply(seq, new RemoveNegation.Input(P5, ff
				.makePosition("1")), null);
		assertTrue(output instanceof IProofRule);
		newSeqs = ((IProofRule) output).apply(seq);
		assertSequents("Applied successfully hyp P5 ",
				"{}[0=1⇒{1,2}⊆{1,2}][][0=1⇒⊤] |- ⊤", newSeqs);

		seq = TestLib.genSeq(" ⊤ |- " + P6);
		output = riReasoner.apply(seq, new RemoveNegation.Input(null, ff
				.makePosition("1.1")), null);
		assertTrue(output instanceof IProofRule);
		newSeqs = ((IProofRule) output).apply(seq);
		assertSequents("Applied successfully goal P6 ",
				"{}[][][⊤] |- ∀x·x=TRUE⇒⊤", newSeqs);

		seq = TestLib.genSeq(P6 + " |- ⊤ ");
		output = riReasoner.apply(seq, new RemoveNegation.Input(P6, ff
				.makePosition("1.1")), null);
		assertTrue(output instanceof IProofRule);
		newSeqs = ((IProofRule) output).apply(seq);
		assertSequents("Applied successfully hyp P6 ",
				"{}[∀x·x=TRUE⇒{x,FALSE}⊆{x,FALSE}][][∀x·x=TRUE⇒⊤] |- ⊤", newSeqs);

		seq = TestLib.genSeq(" ⊤ |- " + P7);
		output = riReasoner.apply(seq, new RemoveNegation.Input(null, ff
				.makePosition("1")), null);
		assertTrue(output instanceof IProofRule);
		newSeqs = ((IProofRule) output).apply(seq);
		assertSequents("Applied successfully goal P7 ",
				"{}[][][⊤] |- 0=1⇒(∀x,x0·x ↦ x0∈{1 ↦ 2}⇒x ↦ x0∈{1 ↦ 2,2 ↦ 3})",
				newSeqs);

		seq = TestLib.genSeq(P7 + " |- ⊤ ");
		output = riReasoner.apply(seq, new RemoveNegation.Input(P7, ff
				.makePosition("1")), null);
		assertTrue(output instanceof IProofRule);
		newSeqs = ((IProofRule) output).apply(seq);
		assertSequents(
				"Applied successfully hyp P7 ",
				"{}[0=1⇒{1 ↦ 2}⊆{1 ↦ 2,2 ↦ 3}][][0=1⇒(∀x,x0·x ↦ x0∈{1 ↦ 2}⇒x ↦ x0∈{1 ↦ 2,2 ↦ 3})] |- ⊤",
				newSeqs);

		seq = TestLib.genSeq(" ⊤ |- " + P8);
		output = riReasoner.apply(seq, new RemoveNegation.Input(null, ff
				.makePosition("1.1")), null);
		assertTrue(output instanceof IProofRule);
		newSeqs = ((IProofRule) output).apply(seq);
		assertSequents(
				"Applied successfully goal P8 ",
				"{}[][][⊤] |- ∀x·x=TRUE⇒(∀x0,x1·x0 ↦ x1∈{x ↦ 2}⇒x0 ↦ x1∈{x ↦ 2,x ↦ 3})",
				newSeqs);

		seq = TestLib.genSeq(P8 + " |- ⊤ ");
		output = riReasoner.apply(seq, new RemoveNegation.Input(P8, ff
				.makePosition("1.1")), null);
		assertTrue(output instanceof IProofRule);
		newSeqs = ((IProofRule) output).apply(seq);
		assertSequents(
				"Applied successfully hyp P8 ",
				"{}[∀x·x=TRUE⇒{x ↦ 2}⊆{x ↦ 2,x ↦ 3}][][∀x·x=TRUE⇒(∀x0,x1·x0 ↦ x1∈{x ↦ 2}⇒x0 ↦ x1∈{x ↦ 2,x ↦ 3})] |- ⊤",
				newSeqs);

		seq = TestLib.genSeq(" ⊤ |- " + P9);
		output = riReasoner.apply(seq, new RemoveNegation.Input(null, ff
				.makePosition("1")), null);
		assertTrue(output instanceof IProofRule);
		newSeqs = ((IProofRule) output).apply(seq);
		assertSequents(
				"Applied successfully goal P9 ",
				"{}[][][⊤] |- 0=1⇒(∀x,x0,x1,x2·x ↦ x0 ↦ (x1 ↦ x2)∈{0 ↦ 2 ↦ (2 ↦ 3)}⇒x ↦ x0 ↦ (x1 ↦ x2)∈{1 ↦ 2 ↦ (2 ↦ 3)})",
				newSeqs);

		seq = TestLib.genSeq(P9 + " |- ⊤ ");
		output = riReasoner.apply(seq, new RemoveNegation.Input(P9, ff
				.makePosition("1")), null);
		assertTrue(output instanceof IProofRule);
		newSeqs = ((IProofRule) output).apply(seq);
		assertSequents(
				"Applied successfully hyp P9 ",
				"{}[0=1⇒{0 ↦ 2 ↦ (2 ↦ 3)}⊆{1 ↦ 2 ↦ (2 ↦ 3)}][][0=1⇒(∀x,x0,x1,x2·x ↦ x0 ↦ (x1 ↦ x2)∈{0 ↦ 2 ↦ (2 ↦ 3)}⇒x ↦ x0 ↦ (x1 ↦ x2)∈{1 ↦ 2 ↦ (2 ↦ 3)})] |- ⊤",
				newSeqs);

		seq = TestLib.genSeq(" ⊤ |- " + P10);
		output = riReasoner.apply(seq, new RemoveNegation.Input(null, ff
				.makePosition("1.1")), null);
		assertTrue(output instanceof IProofRule);
		newSeqs = ((IProofRule) output).apply(seq);
		assertSequents(
				"Applied successfully goal P10 ",
				"{}[][][⊤] |- ∀x·x=TRUE⇒(∀x0,x1,x2,x3·x0 ↦ (x1 ↦ x2) ↦ x3∈{FALSE ↦ (2 ↦ 2) ↦ x}⇒x0 ↦ (x1 ↦ x2) ↦ x3∈{x ↦ (2 ↦ 2) ↦ TRUE})",
				newSeqs);

		seq = TestLib.genSeq(P10 + " |- ⊤ ");
		output = riReasoner.apply(seq, new RemoveNegation.Input(P10, ff
				.makePosition("1.1")), null);
		assertTrue(output instanceof IProofRule);
		newSeqs = ((IProofRule) output).apply(seq);
		assertSequents(
				"Applied successfully hyp P10 ",
				"{}[∀x·x=TRUE⇒{FALSE ↦ (2 ↦ 2) ↦ x}⊆{x ↦ (2 ↦ 2) ↦ TRUE}][][∀x·x=TRUE⇒(∀x0,x1,x2,x3·x0 ↦ (x1 ↦ x2) ↦ x3∈{FALSE ↦ (2 ↦ 2) ↦ x}⇒x0 ↦ (x1 ↦ x2) ↦ x3∈{x ↦ (2 ↦ 2) ↦ TRUE})] |- ⊤",
				newSeqs);

		seq = TestLib.genSeq(" ⊤ |- " + P11);
		output = riReasoner.apply(seq, new RemoveNegation.Input(null, ff
				.makePosition("1")), null);
		assertTrue(output instanceof IProofRule);
		newSeqs = ((IProofRule) output).apply(seq);
		assertSequents(
				"Applied successfully goal P11 ",
				"{}[][][⊤] |- 0=1⇒(∀x,x0·x ↦ x0∈{1 ↦ {2}}⇒x ↦ x0∈{1 ↦ {2},2 ↦ {3}})",
				newSeqs);

		seq = TestLib.genSeq(P11 + " |- ⊤ ");
		output = riReasoner.apply(seq, new RemoveNegation.Input(P11, ff
				.makePosition("1")), null);
		assertTrue(output instanceof IProofRule);
		newSeqs = ((IProofRule) output).apply(seq);
		assertSequents(
				"Applied successfully hyp P11 ",
				"{}[0=1⇒{1 ↦ {2}}⊆{1 ↦ {2},2 ↦ {3}}][][0=1⇒(∀x,x0·x ↦ x0∈{1 ↦ {2}}⇒x ↦ x0∈{1 ↦ {2},2 ↦ {3}})] |- ⊤",
				newSeqs);

		seq = TestLib.genSeq(" ⊤ |- " + P12);
		output = riReasoner.apply(seq, new RemoveNegation.Input(null, ff
				.makePosition("1.1")), null);
		assertTrue(output instanceof IProofRule);
		newSeqs = ((IProofRule) output).apply(seq);
		assertSequents(
				"Applied successfully goal P12 ",
				"{}[][][⊤] |- ∀x·x=TRUE⇒(∀x0,x1·x0 ↦ x1∈{{x} ↦ 2}⇒x0 ↦ x1∈{{x} ↦ 2,{x} ↦ 3})",
				newSeqs);

		seq = TestLib.genSeq(P12 + " |- ⊤ ");
		output = riReasoner.apply(seq, new RemoveNegation.Input(P12, ff
				.makePosition("1.1")), null);
		assertTrue(output instanceof IProofRule);
		newSeqs = ((IProofRule) output).apply(seq);
		assertSequents(
				"Applied successfully hyp P12 ",
				"{}[∀x·x=TRUE⇒{{x} ↦ 2}⊆{{x} ↦ 2,{x} ↦ 3}][][∀x·x=TRUE⇒(∀x0,x1·x0 ↦ x1∈{{x} ↦ 2}⇒x0 ↦ x1∈{{x} ↦ 2,{x} ↦ 3})] |- ⊤",
				newSeqs);
	}

}

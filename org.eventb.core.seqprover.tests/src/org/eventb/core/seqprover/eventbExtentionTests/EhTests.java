package org.eventb.core.seqprover.eventbExtentionTests;

import junit.framework.TestCase;

import org.eventb.core.ast.Predicate;
import org.eventb.core.seqprover.IProofRule;
import org.eventb.core.seqprover.IProverSequent;
import org.eventb.core.seqprover.IReasoner;
import org.eventb.core.seqprover.IReasonerFailure;
import org.eventb.core.seqprover.IReasonerOutput;
import org.eventb.core.seqprover.reasonerInputs.SinglePredInput;
import org.eventb.core.seqprover.tests.TestLib;
import org.eventb.core.seqprover.tests.Util;
import org.eventb.internal.core.seqprover.eventbExtensions.Eq;

/**
 * Unit tests for the rn reasoner
 * 
 * @author htson
 */
public class EhTests extends TestCase {

	private static final IReasoner ehReasoner = new Eq();

	Predicate P1 = TestLib.genPred("0 = 1");

	Predicate P2 = TestLib.genPred("0 + 1 = 1");

	Predicate P3 = TestLib.genPred("0 + 1 + 2 = 2 + 1");

	public void testHypIsNotWellForm() {
		IProverSequent seq;
		IReasonerOutput output;

		// Hyp is not equality
		seq = TestLib.genSeq(" 1 = 2 ⇒ 2 = 3 |- ⊤ ");
		output = ehReasoner.apply(seq, new SinglePredInput(TestLib
				.genPred("1 = 2 ⇒ 2 = 3")), null);
		assertTrue(output instanceof IReasonerFailure);
	}

	public void testNothingToDo() {
		IProverSequent seq;
		IReasonerOutput output;

		seq = TestLib.genSeq(P1 + " ;; ⊤ |- ⊤ ");
		output = ehReasoner.apply(seq, new SinglePredInput(P1), null);
		assertTrue(output instanceof IReasonerFailure);
	}

	/**
	 * Tests for correct reasoner failure
	 */
	public void testHypNotPresent() {
		IProverSequent seq;
		IReasonerOutput output;

		// Hyp is not present
		seq = TestLib.genSeq(" ⊤ |- ⊤ ");
		output = ehReasoner.apply(seq, new SinglePredInput(P1), null);
		assertTrue(output instanceof IReasonerFailure);
	}

	/**
	 * Tests for reasoner success
	 */
	public void testSuccess() {

		IProverSequent seq;
		IProverSequent[] newSeqs;
		IReasonerOutput output;

		seq = TestLib.genSeq(P1 + " ;; 0+1 = 2 |- 1+0+1 = 3 ");
		output = ehReasoner.apply(seq, new SinglePredInput(P1), null);
		assertTrue(output instanceof IProofRule);
		newSeqs = ((IProofRule) output).apply(seq);
		assertSequents("Applied successfully equality P1 ",
				"{}[][0=1, 0+1=2][1+1=2] |- 1+1+1=3", newSeqs);

		seq = TestLib.genSeq(P2 + " ;; 0+1 = 2 |- 2+0+1 = 3 ");
		output = ehReasoner.apply(seq, new SinglePredInput(P2), null);
		assertTrue(output instanceof IProofRule);
		newSeqs = ((IProofRule) output).apply(seq);
		assertSequents("Applied successfully equality P2 ",
				"{}[][0+1=1, 0+1=2][1=2] |- 2+1=3", newSeqs);

		seq = TestLib.genSeq(P3 + " ;; 0+1 = 0+1+2 |- 2+0+1 = 0+1+2+3 ");
		output = ehReasoner.apply(seq, new SinglePredInput(P3), null);
		assertTrue(output instanceof IProofRule);
		newSeqs = ((IProofRule) output).apply(seq);
		assertSequents("Applied successfully equality P2 ",
				"{}[][0+1+2=2+1, 0+1=0+1+2][0+1=2+1] |- 2+0+1=2+1+3", newSeqs);
	}

	private void assertSequents(String message, String expected,
			IProverSequent... sequents) {
		StringBuilder builder = new StringBuilder();
		boolean sep = false;
		for (IProverSequent sequent : sequents) {
			if (sep)
				builder.append('\n');
			builder.append(sequent);
			sep = true;
		}
		String actual = builder.toString();
		if (!expected.equals(actual)) {
			System.out.println(Util.displayString(actual));
			fail(message + ":\n" + actual);
		}
	}

}

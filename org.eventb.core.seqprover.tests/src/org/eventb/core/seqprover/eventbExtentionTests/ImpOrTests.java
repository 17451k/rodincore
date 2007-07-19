package org.eventb.core.seqprover.eventbExtentionTests;

import java.util.List;

import org.eventb.core.ast.IPosition;
import org.eventb.core.ast.Predicate;
import org.eventb.core.seqprover.eventbExtensions.Tactics;
import org.eventb.internal.core.seqprover.eventbExtensions.rewriters.ImpAndRewrites;

/**
 * Unit tests for the {@link ImpAndRewrites} reasoner
 * 
 * @author htson
 *
 */
public class ImpOrTests extends AbstractManualRewriterTests {

	String P1 = "x = 1 ∨ x = 2 ∨ x = 3 ⇒ x = 0";
	
	String resultP1a = "x=1⇒x=0";

	String resultP1b = "x=2⇒x=0";
	
	String resultP1c = "x=3⇒x=0";
	
	String P2 = "x = 4 ⇒ (x = 1 ∨ x = 2 ∨ x = 3 ⇒ x = 0)";
	
	String resultP2 = "x=4⇒(x=1⇒x=0)∧(x=2⇒x=0)∧(x=3⇒x=0)";
	
	String P3 = "∀x·x = 4 ⇒ (x = 1 ∨ x = 2 ∨ x = 3 ⇒ x = 0)";
	
	String resultP3 = "∀x·x=4⇒(x=1⇒x=0)∧(x=2⇒x=0)∧(x=3⇒x=0)";

	String P4 = "∀x·((x = 1 ∨ x = 2 ∨ x = 3 ⇒ x = 0) ∨ (x = 1 ∨ x = 2 ∨ x = 3 ⇒ x = 0)) ⇒ x = 4";
	
	@Override
	public String getReasonerID() {
		return "org.eventb.core.seqprover.impOrRewrites";
	}

	@Override
	protected SuccessfulTest[] getSuccessfulTests() {
		return new SuccessfulTest[] {
				new SuccessfulTest(P1, "", resultP1a, resultP1b, resultP1c),
				new SuccessfulTest(P2, "1", resultP2),
				new SuccessfulTest(P3, "1.1", resultP3)
		};
	}

	@Override
	protected String[] getUnsuccessfulTests() {
		return new String[] {
				P1, "0",
				P2, "0",
				P3, "0.1"
		};
	}

	@Override
	protected List<IPosition> getPositions(Predicate predicate) {
		return Tactics.impOrGetPositions(predicate);
	}

	@Override
	protected String[] getTestGetPositions() {
		return new String[] {
				P1, "",
				P2, "1",
				P3, "1.1",
				P4, "1\n" + "1.0.0\n" + "1.0.1" 
		};
	}

	// Commented out, but makes the tests succeed
//	@Override
//	public ITactic getJustDischTactic() {
//		return B4freeCore.externalPP(false);
//	}

}

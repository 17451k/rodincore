package org.eventb.core.seqprover.eventbExtentionTests;

import java.util.List;

import org.eventb.core.ast.IPosition;
import org.eventb.core.ast.Predicate;
import org.eventb.core.seqprover.eventbExtensions.Tactics;
import org.eventb.internal.core.seqprover.eventbExtensions.FiniteNegative;

/**
 * Unit tests for the Finite of set of non-positive numbers reasoner
 * {@link FiniteNegative}
 * 
 * @author htson
 */
public class FiniteNegativeTests extends AbstractEmptyInputReasonerTests {

	String P1 = "(x = 2) ⇒ finite({0,x,1})";

	String P2 = "∀x· x = 2 ⇒ finite({0,x,1})";

	String P3 = "finite({0,x,1})";

	String resultP3GoalA = "{x=ℤ}[][][⊤] |- ∃n·∀x0·x0∈{0,x,1}⇒n≤x0";
	
	String resultP3GoalB = "{x=ℤ}[][][⊤] |- {0,x,1}⊆ℤ ∖ ℕ1";
	
	String P4 = "finite({0↦x,x↦1})";
	
	protected List<IPosition> getPositions(Predicate predicate) {
		return Tactics.finiteNegativeGetPositions(predicate);
	}

	@Override
	public String getReasonerID() {
		return "org.eventb.core.seqprover.finiteNegative";
	}

	protected SuccessfulTest[] getSuccessfulTests() {
		return new SuccessfulTest[] {
				// P3 in goal
				new SuccessfulTest(" ⊤ |- " + P3, resultP3GoalA, resultP3GoalB)
		};
	}

	protected String[] getUnsuccessfulTests() {
		return new String[] {
				// P1 in goal
				" ⊤ |- " + P1,
				// P2 in goal
				" ⊤ |- " + P2,
				// P4 in goal
				" ⊤ |- " + P4
		};
	}

	@Override
	protected String[] getTestGetPositions() {
		return new String[] {
				P1, "",
				P2, "",
				P3, "",
				P4, ""
		};
	}

	// Commented out, but makes the tests succeed
//	@Override
//	public ITactic getJustDischTactic() {
//		return B4freeCore.externalPP(false);
//	}

}

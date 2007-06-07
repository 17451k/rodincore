package org.eventb.core.seqprover.eventbExtentionTests;

import java.util.List;

import org.eventb.core.ast.IPosition;
import org.eventb.core.ast.Predicate;
import org.eventb.core.seqprover.eventbExtensions.Tactics;
import org.eventb.internal.core.seqprover.eventbExtensions.rewriters.EqvRewrites;

/**
 * Unit tests for the Equivalence Rewrites reasoner
 * {@link EqvRewrites}
 * 
 * @author htson
 */
public class EquivalentTests extends AbstractManualRewriterTests {

	// P <=> Q == (P => Q) & (Q => P)
	String P1 = "(1 = x) ⇒ (y = 1 ⇔ x = y)";

	String resultP1 = "1=x⇒(y=1⇒x=y)∧(x=y⇒y=1)";

	String P2 = "∀x·x = 0 ⇒ (x = y ⇔ y = 1)";

	String resultP2 = "∀x·x=0⇒(x=y⇒y=1)∧(y=1⇒x=y)";

	@Override
	public String getReasonerID() {
		return "org.eventb.core.seqprover.eqvRewrites";
	}

	protected List<IPosition> getPositions(Predicate predicate) {
		return Tactics.eqvGetPositions(predicate);
	}

	@Override
	protected String[] getSuccessfulTests() {
		return new String[] {
				P1, "1", resultP1,
				P2, "1.1", resultP2,
		};
	}

	@Override
	protected String[] getUnsuccessfulTests() {
		return new String[] {
				P1, "0",
				P2, "1.0"
		};
	}

	@Override
	protected String[] getTestGetPositions() {
		return new String[] {
				P1, "1",
				P2, "1.1"
		};
	}

}

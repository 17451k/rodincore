package org.eventb.core.seqprover.eventbExtentionTests;

import java.util.List;

import org.eventb.core.ast.IPosition;
import org.eventb.core.ast.Predicate;
import org.eventb.core.seqprover.eventbExtensions.Tactics;
import org.eventb.internal.core.seqprover.eventbExtensions.rewriters.RemoveNegation;

/**
 * Unit tests for the rn reasoner {@link RemoveNegation}
 * 
 * @author htson
 */
public class RemoveNegationTests extends AbstractManualRewriterTests {

	String P1 = "(0 = 1) ⇒ (¬ (1 = 2 ∧ 2 = 3 ∧ 3 = 4))";

	String resultP1 = "0=1⇒¬1=2∨¬2=3∨¬3=4";
	
	String P2 = "∀x·x = 0 ⇒ (¬ (x = 1 ∧ x = 2 ∧ x = 3))";

	String resultP2 = "∀x·x=0⇒¬x=1∨¬x=2∨¬x=3";
	
	String P3 = "(0 = 1) ⇒ (¬ (1 = 2 \u2228 2 = 3 \u2228 3 = 4))";

	String resultP3 = "0=1⇒¬1=2∧¬2=3∧¬3=4";
	
	String P4 = "∀x·x = 0 ⇒ (¬ (x = 1 \u2228 x = 2 \u2228 x = 3))";
	
	String resultP4 = "∀x·x=0⇒¬x=1∧¬x=2∧¬x=3"; 
	
	String P5 = "(0 = 1) ⇒ (¬⊤)";

	String resultP5 = "0=1⇒⊥";

	String P6 = "∀x·x = 0 ⇒ (¬⊤)";

	String resultP6 = "∀x·x=0⇒⊥";
	
	String P7 = "(0 = 1) ⇒ (¬⊥)";

	String resultP7 = "0=1⇒⊤";
	
	String P8 = "∀x·x = 0 ⇒ (¬⊥)";

	String resultP8 = "∀x·x=0⇒⊤";
	
	String P9 = "(0 = 1) ⇒ (¬¬(1=2))";

	String resultP9 = "0=1⇒1=2";
	
	String P10 = "∀x·x = 0 ⇒ (¬¬(x=1))";

	String resultP10 = "∀x·x=0⇒x=1";
	
	String P11 = "(0 = 1) ⇒ (¬(1 = 2 ⇒ 2 = 3))";
	
	String resultP11 = "0=1⇒1=2∧¬2=3";
	
	String P12 = "∀x·x = 0 ⇒ (¬(x = 1 ⇒ x = 2))";

	String resultP12 = "∀x·x=0⇒x=1∧¬x=2";
	
	String P13 = "(0 = 1) ⇒ (¬(∀y·y ∈ ℕ ⇒ 0 ≤ y))";

	String resultP13 = "0=1⇒(∃y·¬(y∈ℕ⇒0≤y))";
	
	String P14 = "∀x·x = 0 ⇒ (¬(∀y·y ∈ ℕ ⇒ x ≤ y))";

	String resultP14 = "∀x·x=0⇒(∃y·¬(y∈ℕ⇒x≤y))";
	
	String P15 = "(0 = 1) ⇒ ¬({1} = ∅)";

	String resultP15 = "0=1⇒(∃x·x∈{1})";
	
	String P16 = "∀x·x = 0 ⇒ ¬({x} = ∅)";

	String resultP16 = "∀x·x=0⇒(∃x0·x0∈{x})";

	String P17 = "(0 = 1) ⇒ ¬({1 ↦ 2} = ∅)";
	
	String resultP17 = "0=1⇒(∃x,x0·x ↦ x0∈{1 ↦ 2})";
	
	String P18 = "∀x·x = 0 ⇒ ¬({x ↦ 0} = ∅)";
	
	String resultP18 = "∀x·x=0⇒(∃x0,x1·x0 ↦ x1∈{x ↦ 0})";

	String P19 = "(0 = 1) ⇒ ¬({(1 ↦ 2) ↦ (3 ↦ 4)} = ∅)";

	String resultP19 = "0=1⇒(∃x,x0,x1,x2·x ↦ x0 ↦ (x1 ↦ x2)∈{1 ↦ 2 ↦ (3 ↦ 4)})";
	
	String P20 = "∀x·x = 0 ⇒ ¬({1 ↦ ((x ↦ 0) ↦ x)} = ∅)";

	String resultP20 = "∀x·x=0⇒(∃x0,x1,x2,x3·x0 ↦ (x1 ↦ x2 ↦ x3)∈{1 ↦ (x ↦ 0 ↦ x)})";
	
	String P21 = "(0 = 1) ⇒ ¬({1 ↦ {2}} = ∅)";

	String resultP21 = "0=1⇒(∃x,x0·x ↦ x0∈{1 ↦ {2}})";
	
	String P22 = "∀x·x = 0 ⇒ ¬({{x} ↦ 0} = ∅)";
	
	String resultP22 = "∀x·x=0⇒(∃x0,x1·x0 ↦ x1∈{{x} ↦ 0})";

	@Override
	public String getReasonerID() {
		return "org.eventb.core.seqprover.rn";
	}

	protected List<IPosition> getPositions(Predicate predicate) {
		return Tactics.rnGetPositions(predicate);
	}

	@Override
	protected SuccessfulTest[] getSuccessfulTests() {
		return new SuccessfulTest[] {
				new SuccessfulTest(P1, "1", resultP1),
				new SuccessfulTest(P2, "1.1", resultP2),
				new SuccessfulTest(P3, "1", resultP3),
				new SuccessfulTest(P4, "1.1", resultP4),
				new SuccessfulTest(P5, "1", resultP5),
				new SuccessfulTest(P6, "1.1", resultP6),
				new SuccessfulTest(P7, "1", resultP7),
				new SuccessfulTest(P8, "1.1", resultP8),
				new SuccessfulTest(P9, "1", resultP9),
				new SuccessfulTest(P10, "1.1", resultP10),
				new SuccessfulTest(P11, "1", resultP11),
				new SuccessfulTest(P12, "1.1", resultP12),
				new SuccessfulTest(P13, "1", resultP13),
				new SuccessfulTest(P14, "1.1", resultP14),
				new SuccessfulTest(P15, "1", resultP15),
				new SuccessfulTest(P16, "1.1", resultP16),
				new SuccessfulTest(P17, "1", resultP17),
				new SuccessfulTest(P18, "1.1", resultP18),
				new SuccessfulTest(P19, "1", resultP19),
				new SuccessfulTest(P20, "1.1", resultP20),
				new SuccessfulTest(P21, "1", resultP21),
				new SuccessfulTest(P22, "1.1", resultP22)
		};
	}


	@Override
	protected String[] getUnsuccessfulTests() {
		return new String[] {
				P1, "0",
				P2, "1.0",
				P3, "0",
				P4, "1.0",
				P5, "0",
				P6, "1.0",
				P7, "0",
				P8, "1.0",
				P9, "0",
				P10, "1.0",
				P11, "0",
				P12, "1.0",
				P13, "0",
				P14, "1.0",
				P15, "0",
				P16, "1.0",
				P17, "0",
				P18, "1.0",
				P19, "0",
				P20, "1.0",
				P21, "0",
				P22, "1.0"
		};
	}

	@Override
	protected String[] getTestGetPositions() {
		return new String[] {
				P1, "1",
				P2, "1.1",
				P3, "1",
				P4, "1.1",
				P5, "1",
				P6, "1.1",
				P7, "1",
				P8, "1.1",
				P9, "1",
				P10, "1.1",
				P11, "1",
				P12, "1.1",
				P13, "1",
				P14, "1.1",
				P15, "1",
				P16, "1.1",
				P17, "1",
				P18, "1.1",
				P19, "1",
				P20, "1.1",
				P21, "1",
				P22, "1.1"
		};
	}

	// Commented out, but makes the tests succeed
//	@Override
//	public ITactic getJustDischTactic() {
//		return B4freeCore.externalPP(false);
//	}

}

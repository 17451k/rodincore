/*******************************************************************************
 * Copyright (c) 2006, 2010 ETH Zurich and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     ETH Zurich - initial API and implementation
 *     Systerel - Fixed DISTRI_DOMSUB_BUNION_L and DISTRI_DOMSUB_BINTER_L
 *******************************************************************************/
package org.eventb.core.seqprover.eventbExtensionTests;

import java.util.List;

import org.eventb.core.ast.IPosition;
import org.eventb.core.ast.Predicate;
import org.eventb.core.seqprover.eventbExtensions.Tactics;
import org.eventb.internal.core.seqprover.eventbExtensions.rewriters.DomDistLeftRewrites;

/**
 * Unit tests for the Domain distribution left Rewrites reasoner
 * {@link DomDistLeftRewrites}
 * 
 * @author htson
 */
public class DomDistLeftTests extends AbstractManualRewriterTests {

	// (s \/ ... \/ t) <| r == (s <| r) \/ ... \/ (t <| r)
	String P1 = "({1, x} ∪ {2, 4} ∪ {3}) ◁ r = {x ↦ x}";

	String resultP1 = "({1,x} ◁ r)∪({2,4} ◁ r)∪({3} ◁ r)={x ↦ x}";

	String P2 = "(1 = x) ⇒ {x ↦ x} = ({1, x} ∪ {2, 4} ∪ {3}) ◁ r";

	String resultP2 = "1=x⇒{x ↦ x}=({1,x} ◁ r)∪({2,4} ◁ r)∪({3} ◁ r)";

	String P3 = "∀x·x = 0 ⇒ ({1, x} ∪ {2, 4} ∪ {3}) ◁ r = {x ↦ x}";

	String resultP3 = "∀x·x=0⇒({1,x} ◁ r)∪({2,4} ◁ r)∪({3} ◁ r)={x ↦ x}";


	// (s /\ ... /\ t) <| r == (s <| r) /\ ... /\ (t <| r)
	String P4 = "({1, x} ∩ {2, 4} ∩ {3}) ◁ r = {x ↦ x}";

	String resultP4 = "{1,x} ◁ r∩({2,4} ◁ r)∩({3} ◁ r)={x ↦ x}";

	String P5 = "(1 = x) ⇒ {x ↦ x} = ({1, x} ∩ {2, 4} ∩ {3}) ◁ r";

	String resultP5 = "1=x⇒{x ↦ x}={1,x} ◁ r∩({2,4} ◁ r)∩({3} ◁ r)";

	String P6 = "∀x·x = 0 ⇒ ({1, x} ∩ {2, 4} ∩ {3}) ◁ r = {x ↦ x}";

	String resultP6 = "∀x·x=0⇒{1,x} ◁ r∩({2,4} ◁ r)∩({3} ◁ r)={x ↦ x}";


	// (s \/ ... \/ t) <<| r == (s <<| r) /\ ... /\ (t <<| r)
	String P7 = "({1, x} ∪ {2, 4} ∪ {3}) ⩤ r = {x ↦ x}";

	String resultP7 = "({1,x} ⩤ r)∩({2,4} ⩤ r)∩({3} ⩤ r)={x ↦ x}";

	String P8 = "(1 = x) ⇒ {x ↦ x} = ({1, x} ∪ {2, 4} ∪ {3}) ⩤ r";

	String resultP8 = "1=x⇒{x ↦ x}=({1,x} ⩤ r)∩({2,4} ⩤ r)∩({3} ⩤ r)";

	String P9 = "∀x·x = 0 ⇒ ({1, x} ∪ {2, 4} ∪ {3}) ⩤ r = {x ↦ x}";

	String resultP9 = "∀x·x=0⇒({1,x} ⩤ r)∩({2,4} ⩤ r)∩({3} ⩤ r)={x ↦ x}";


	// (s /\ ... /\ t) <<| r == (s <<| r) \/ ... \/ (t <<| r)
	String P10 = "({1, x} ∩ {2, 4} ∩ {3}) ⩤ r = {x ↦ x}";

	String resultP10 = "({1,x} ⩤ r)∪({2,4} ⩤ r)∪({3} ⩤ r)={x ↦ x}";

	String P11 = "(1 = x) ⇒ {x ↦ x} = ({1, x} ∩ {2, 4} ∩ {3}) ⩤ r";

	String resultP11 = "1=x⇒{x ↦ x}=({1,x} ⩤ r)∪({2,4} ⩤ r)∪({3} ⩤ r)";

	String P12 = "∀x·x = 0 ⇒ ({1, x} ∩ {2, 4} ∩ {3}) ⩤ r = {x ↦ x}";

	String resultP12 = "∀x·x=0⇒({1,x} ⩤ r)∪({2,4} ⩤ r)∪({3} ⩤ r)={x ↦ x}";

	@Override
	public String getReasonerID() {
		return "org.eventb.core.seqprover.domDistLeftRewrites";
	}

	protected List<IPosition> getPositions(Predicate predicate) {
		return Tactics.domDistLeftGetPositions(predicate);
	}

	@Override
	protected SuccessfulTest[] getSuccessfulTests() {
		return new SuccessfulTest[] {
				new SuccessfulTest(P1, "0", resultP1),
				new SuccessfulTest(P2, "1.1", resultP2),
				new SuccessfulTest(P3, "1.1.0", resultP3),
				new SuccessfulTest(P4, "0", resultP4),
				new SuccessfulTest(P5, "1.1", resultP5),
				new SuccessfulTest(P6, "1.1.0", resultP6),
				new SuccessfulTest(P7, "0", resultP7),
				new SuccessfulTest(P8, "1.1", resultP8),
				new SuccessfulTest(P9, "1.1.0", resultP9),
				new SuccessfulTest(P10, "0", resultP10),
				new SuccessfulTest(P11, "1.1", resultP11),
				new SuccessfulTest(P12, "1.1.0", resultP12)
		};
	}

	@Override
	protected String[] getUnsuccessfulTests() {
		return new String[] {
				P1, "1",
				P2, "1.0",
				P3, "1.0.1",
				P4, "1",
				P5, "1.0",
				P6, "1.0.1",
				P7, "1",
				P8, "1.0",
				P9, "1.0.1",
				P10, "1",
				P11, "1.0",
				P12, "1.0.1"
		};
	}

	@Override
	protected String[] getTestGetPositions() {
		return new String[] {
				P1, "0",
				P2, "1.1",
				P3, "1.1.0",
				P4, "0",
				P5, "1.1",
				P6, "1.1.0",
				P7, "0",
				P8, "1.1",
				P9, "1.1.0",
				P10, "0",
				P11, "1.1",
				P12, "1.1.0"
		};
	}

	// Commented out, makes the tests NOT succeed
	// TODO: Verify with another external prover
//	@Override
//	public ITactic getJustDischTactic() {
//		return B4freeCore.externalPP(false);
//	}

}

/*******************************************************************************
 * Copyright (c) 2010, 2016 Systerel and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Systerel - initial API and implementation
 *******************************************************************************/
package org.eventb.core.seqprover.rewriterTests;

import static org.eventb.core.ast.FormulaFactory.makePosition;
import static org.eventb.core.seqprover.tests.TestLib.genExpr;
import static org.eventb.core.seqprover.tests.TestLib.genPred;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.eventb.core.ast.IPosition;
import org.eventb.core.ast.ITypeEnvironmentBuilder;
import org.eventb.core.seqprover.IProverSequent;
import org.eventb.core.seqprover.IReasoner;
import org.eventb.core.seqprover.IReasonerFailure;
import org.eventb.core.seqprover.IReasonerInput;
import org.eventb.core.seqprover.IReasonerOutput;
import org.eventb.core.seqprover.eventbExtensions.Tactics;
import org.eventb.core.seqprover.reasonerExtensionTests.AbstractReasonerTests;
import org.eventb.core.seqprover.tests.TestLib;
import org.eventb.internal.core.seqprover.eventbExtensions.rewriters.AbstractManualRewrites.Input;
import org.eventb.internal.core.seqprover.eventbExtensions.rewriters.FunImgSimplifies;
import org.junit.Test;

public class FunImgSimpRewriterTests extends AbstractReasonerTests {

	private static final IPosition FIRST_CHILD = IPosition.ROOT.getFirstChild();
	private static final IReasoner rewriter = new FunImgSimplifies();
	private static final IReasonerInput input = new Input(null, FIRST_CHILD);
	private static final String typeEnvString = "S=ā(S); F=SāT; T=ā(T)";
	private static final ITypeEnvironmentBuilder typeEnv = TestLib
			.mTypeEnvironment(typeEnvString);
	
	private static final String valids[] = { 
			"āø", // partial function
			"ā", // total function
			"ā¤", // partial injection
			"ā£", // total injection
			"ā¤", // partial sujection
			"ā ", // total surjection
			"ā¤", // total bijection
	};
	
	private static final String domSymbols[] = {
			"ā", // domain restriction
			"ā©¤", // domain substraction	
	};

	
	private static final String ranSymbols[] = {
			"ā·", // range restriction
			"ā©„", // range substraction
	};
	

	private static final String invalids[] = { 
			"ā", // relation
			"ī", // total surjective relation
			"ī", // total relation
			"ī", // surjective relation
	};

	/**
	 * Testing all valid relational symbols.
	 */
	@Override
	public SuccessfullReasonerApplication[] getSuccessfulReasonerApplications() {
		final Input input = new Input(null, makePosition("0"));
		final List<SuccessfullReasonerApplication> result = new ArrayList<SuccessfullReasonerApplication>();
		for (String arrow : valids) {
			for (String symbol : domSymbols) {
				final String resultDom = "{"+typeEnvString+"}[][][EāS ;; FāS"+ arrow + "T ;; Gādom(E "+ symbol + " F)] |- F(G)=F(G)";
				final String funImage = "(E " + symbol + " F)(G)";
				final String goalImage = funImage + "=F(G)";
				final IProverSequent seq = TestLib.genFullSeq(typeEnv, "", "", "EāS;; Gādom(E " + symbol + " F) ;; FāS"+ arrow +"T", goalImage);
				assertApplicability(seq, input, funImage, goalImage);
				result.add(new SuccessfullReasonerApplication(seq, input, resultDom));
			}
			for (String symbol : ranSymbols) {
				final String resultRan = "{S=ā(S); F=SāT; T=ā(T)}[][][EāT ;; FāS"+ arrow + "T ;; Gādom(F" + symbol + "E)] |- F(G)=F(G)";
				final String funImage = "(F" + symbol + "E)(G)";
				final String goalImage = funImage + "=F(G)";
				final IProverSequent seq = TestLib.genFullSeq(typeEnv, "", "", "EāT;; Gādom(F" + symbol + "E) ;; FāS" + arrow + "T", goalImage);
				assertApplicability(seq, input, funImage, goalImage);
				result.add(new SuccessfullReasonerApplication(seq, input, resultRan));
			}

			final String resultSetMinus = "{"+typeEnvString+"}[][][EāSāT ;; FāS"+ arrow + "T ;; Gādom(F ā E)] |- F(G)=F(G)";
			final String funImage = "( F ā E )(G)";
			final String goalImage = funImage + "=F(G)";
			final IProverSequent seq = TestLib.genFullSeq(typeEnv, "", "", "EāSāT;; Gādom( F ā E ) ;; FāS" + arrow + "T", goalImage);
			assertApplicability(seq, input, funImage, goalImage);
			result.add(new SuccessfullReasonerApplication(seq, input, resultSetMinus));
		}
		return result.toArray(new SuccessfullReasonerApplication[result.size()]);
	}

	private static void assertApplicability(IProverSequent seq, Input input,
			String funImage, String goalImage) {
		assertTrue(Tactics.isFunImgSimpApplicable(
				genExpr(seq.typeEnvironment(), funImage), seq));
		assertEquals(
				Collections.singletonList(input.getPosition()),
				Tactics.funImgSimpGetPositions(
						genPred(seq.typeEnvironment(), goalImage), seq));
	}

	/**
	 * Testing all invalid relational symbols.
	 */
	@Override
	public UnsuccessfullReasonerApplication[] getUnsuccessfullReasonerApplications() {
		final Input input = new Input(null, makePosition("0"));
		final List<UnsuccessfullReasonerApplication> result = new ArrayList<UnsuccessfullReasonerApplication>();
		for (String arrow : invalids) {
			for (String symbol : domSymbols) {
				final String goal1 = "(E " + symbol + " F)(G)=F(G)";
				final String resultString1 = "Rewriter org.eventb.core.seqprover."
					    + "funImgSimplifies is inapplicable for goal "
						+ goal1
						+ " at position 0";
				final IProverSequent seq = TestLib.genFullSeq(typeEnv, "", "", "EāS;; Gādom(E " + symbol + " F) ;; FāS" + arrow + "T", "(E " + symbol + " F)(G)=F(G)");
				result.add(new UnsuccessfullReasonerApplication(seq, input,	resultString1));
			}
			for (String symbol : ranSymbols) {
				final String goal2 = "(F " + symbol + " E)(G)=F(G)";
				final String resultString2 = "Rewriter org.eventb.core.seqprover."+ "funImgSimplifies is inapplicable for goal "
						+ goal2
						+ " at position 0";
				final IProverSequent seq = TestLib.genFullSeq(typeEnv, "", "", "EāT;; Gādom(F" + symbol + "E) ;; FāS" + arrow + "T",	"(F" + symbol + "E)(G)=F(G)");
				result.add(new UnsuccessfullReasonerApplication(seq, input,	resultString2));
			}
			final String goal3 = "(F ā E)(G)=F(G)";
			final String resultString3 = "Rewriter org.eventb.core.seqprover."
					+ "funImgSimplifies is inapplicable for goal " + goal3
					+ " at position 0";
			final IProverSequent seq = TestLib.genFullSeq(typeEnv, "", "", "EāSāT;; Gādom( F ā E ) ;; FāS" + arrow + "T", "( F ā E )(G)=F(G)");
			result.add(new UnsuccessfullReasonerApplication(seq, input,	resultString3));
		}
		return result.toArray(new UnsuccessfullReasonerApplication[result.size()]);
	}

	private static void assertInvalid(IProverSequent seq) {
		final IReasonerOutput output = rewriter.apply(seq, input, null);
		assertTrue(output instanceof IReasonerFailure);
	}

	/**
	 * The missing hypothesis fāā op ā results in a reasoner failure.
	 */
	@Test
	public void testMissingHypothesis() {
		final IProverSequent seq = TestLib.genSeq("ā¤ |- ({1}ā©¤f)(5)=6");
		assertInvalid(seq);
	}

	/**
	 * Checking that the reasoner doesn't apply with wrong positions.
	 * 
	 * @throws Exception
	 */
	@Test
	public void testWrongPosition() throws Exception {
		final IProverSequent seq = TestLib.genSeq("fāāāā |- ({1}ā©¤f)(5)=6");

		// position out of the goal
		final IPosition posOut = makePosition("3");
		doTestWrongPosition(seq, new Input(null, posOut));

		// position of ā
		final IPosition posN = makePosition("1");
		doTestWrongPosition(seq, new Input(null, posN));

		// position of (5)
		final IPosition posDom01 = makePosition("0.1");
		doTestWrongPosition(seq, new Input(null, posDom01));
	}

	private static void doTestWrongPosition(IProverSequent seq, Input input) {
		final IReasonerOutput output = rewriter.apply(seq, input, null);
		assertTrue(output instanceof IReasonerFailure);
	}

	/**
	 * Try to apply the FunImgSimp rule on a relation.
	 * 
	 * @throws Exception
	 */
	@Test
	public void testNotApplicable() throws Exception {
		final IProverSequent seq = TestLib
				.genSeq("ā¤ |- ({4} ā©¤ {1 ā¦ 2,1 ā¦ 3,4 ā¦ 5})(4)=5");
		final IReasonerOutput output = rewriter.apply(seq, input, null);
		assertTrue(output instanceof IReasonerFailure);
	}

	/**
	 * Testing with a wrong hypothesis
	 * 
	 * @throws Exception
	 */
	@Test
	public void testNotConcerned() throws Exception {
		final IProverSequent seq = TestLib
				.genSeq("fāāāā  |- ({4} ā©¤ {1 ā¦ 2,1 ā¦ 3,4 ā¦ 5})(1)=2");
		final IReasonerOutput output = rewriter.apply(seq, input, null);
		assertTrue(output instanceof IReasonerFailure);
	}

	@Override
	public String getReasonerID() {
		return (new FunImgSimplifies()).getReasonerID();
	}

}

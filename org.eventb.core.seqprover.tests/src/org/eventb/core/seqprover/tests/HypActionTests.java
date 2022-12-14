/*******************************************************************************
 * Copyright (c) 2013, 2014 Systerel and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Systerel - initial API and implementation
 *******************************************************************************/
package org.eventb.core.seqprover.tests;

import static java.util.Collections.emptyList;
import static org.eventb.core.seqprover.ProverFactory.makeRewriteHypAction;
import static org.eventb.core.seqprover.tests.TestLib.genFullSeq;
import static org.eventb.core.seqprover.tests.TestLib.genPred;
import static org.eventb.core.seqprover.tests.TestLib.mTypeEnvironment;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import org.eventb.core.ast.FormulaFactory;
import org.eventb.core.ast.FreeIdentifier;
import org.eventb.core.ast.ITypeEnvironment;
import org.eventb.core.ast.Predicate;
import org.eventb.core.seqprover.IHypAction;
import org.eventb.core.seqprover.IHypAction.IForwardInfHypAction;
import org.eventb.core.seqprover.IHypAction.IRewriteHypAction;
import org.eventb.core.seqprover.IHypAction.ISelectionHypAction;
import org.eventb.core.seqprover.IProverSequent;
import org.eventb.core.seqprover.ProverFactory;
import org.eventb.core.seqprover.ProverLib;
import org.eventb.internal.core.seqprover.ForwardInfHypAction;
import org.eventb.internal.core.seqprover.IInternalHypAction;
import org.eventb.internal.core.seqprover.IInternalProverSequent;
import org.eventb.internal.core.seqprover.RewriteHypAction;
import org.eventb.internal.core.seqprover.SelectionHypAction;
import org.junit.Test;

/**
 * Test class for {@link IHypAction} implementations.
 */
public class HypActionTests {

	private static final FormulaFactory factory = FormulaFactory.getDefault();

	/**
	 * Ensures that {@link ForwardInfHypAction} manipulates a copy of the
	 * collections of predicates and array of free identifiers which are given
	 * to its constructor.
	 */
	@Test
	public void testFwdInfHypActionField() {
		final Predicate p1 = genPred("1=1");
		final Predicate p2 = genPred("2=2");
		final FreeIdentifier x = factory.makeFreeIdentifier("x", null);

		final List<Predicate> s1 = list(p1);
		final List<Predicate> s2 = list(p2);
		final FreeIdentifier[] a1 = new FreeIdentifier[] { x };
		final IForwardInfHypAction action = ProverFactory
				.makeForwardInfHypAction(s1, a1, s2);
		s1.clear();
		final Collection<Predicate> hyps = action.getHyps();
		assertEquals(1, hyps.size());
		assertEquals(p1, hyps.iterator().next());
		s2.clear();
		final Collection<Predicate> inferredHyps = action.getInferredHyps();
		assertEquals(1, inferredHyps.size());
		assertEquals(p2, inferredHyps.iterator().next());
		final FreeIdentifier[] idents = action.getAddedFreeIdents();
		assertEquals(1, idents.length);
		assertEquals(x, idents[0]);
	}

	/**
	 * Ensures that {@link SelectionHypAction} manipulates a copy of the
	 * collection of predicates which are given to its constructor. This test is
	 * valid for all kind of action types managed by the selection hypothesis
	 * action.
	 */
	@Test
	public void testSelectHypActionField() {
		final Predicate p1 = genPred("1=1");
		final List<Predicate> s1 = list(p1);
		final ISelectionHypAction action = ProverFactory
				.makeSelectHypAction(s1);
		s1.clear();
		assertEquals(1, action.getHyps().size());
		assertEquals(p1, action.getHyps().iterator().next());
	}

	/**
	 * Ensures that {@link RewriteHypAction} manipulates a copy of the
	 * collection of predicates representing disappearing hypotheses and which
	 * are given to its constructor.
	 * <p>
	 * This test checks only the disappearing hypothesis field, as other fields
	 * are covered by above tests.
	 * </p>
	 */
	@Test
	public void testRewriteHypActionField() {
		final List<Predicate> s1 = emptyList();
		final Predicate p2 = genPred("2=2");
		final List<Predicate> s2 = list(p2);
		final IRewriteHypAction action = makeRewriteHypAction(s2, s1, s2);
		s2.clear();
		final Collection<Predicate> dHyps = action.getDisappearingHyps();
		assertEquals(1, dHyps.size());
		assertEquals(p2, dHyps.iterator().next());
	}

	private static final Predicate p1 = genPred("1=1");
	private static final Predicate p2 = genPred("2=2");
	private static final Predicate p3 = genPred("3=3");
	private static final IRewriteHypAction rewriteP1P2P3 = makeRewriteHypAction(
			list(p1, p2), list(p3), list(p1, p2));

	private void assertRewriteP1P2P3(Set<Predicate> hyps, Set<Predicate> expectedHidden, Set<Predicate> expectedSelected) {
		final ITypeEnvironment emptyTEnv = mTypeEnvironment();
		final Set<Predicate> noPred = set();
		final Predicate goal = genPred("0=1");
		final IProverSequent sequent = genFullSeq(emptyTEnv, noPred, noPred,
				hyps, goal);
		final IProverSequent expected = genFullSeq(emptyTEnv, expectedHidden,
				noPred, expectedSelected, goal);
		final IProverSequent actual = ((IInternalHypAction) rewriteP1P2P3)
				.perform((IInternalProverSequent) sequent);
		assertTrue("Wrong sequent, expected:\n" + expected + "\nbut was:\n"
				+ actual, ProverLib.deepEquals(expected, actual));
	}

	@Test
	public void testRewriteHypActionNominal() throws Exception {
		assertRewriteP1P2P3(set(p1, p2), set(p1, p2), set(p3));
	}

	@Test
	public void testRewriteHypActionNotApplicable() throws Exception {
		assertRewriteP1P2P3(set(p1), set(), set(p1));
	}

	@Test
	public void testRewriteHypActionHideOnly() throws Exception {
		assertRewriteP1P2P3(set(p1, p2, p3), set(p1, p2), set(p3));
	}

	private static Set<Predicate> set(Predicate... predicates) {
		final Set<Predicate> result = new LinkedHashSet<Predicate>();
		for (Predicate p : predicates) {
			result.add(p);
		}
		return result;
	}

	private static List<Predicate> list(Predicate... predicates) {
		final List<Predicate> result = new ArrayList<Predicate>();
		for (Predicate p : predicates) {
			result.add(p);
		}
		return result;
	}

}

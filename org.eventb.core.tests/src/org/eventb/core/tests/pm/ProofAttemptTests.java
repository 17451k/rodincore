/*******************************************************************************
 * Copyright (c) 2008 Systerel and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     Systerel - initial API and implementation
 *******************************************************************************/
package org.eventb.core.tests.pm;

import static org.eventb.core.EventBAttributes.POSTAMP_ATTRIBUTE;
import static org.eventb.core.ast.Formula.BTRUE;
import static org.eventb.core.ast.Formula.EQUAL;
import static org.eventb.core.seqprover.IConfidence.DISCHARGED_MAX;
import static org.eventb.core.seqprover.IConfidence.UNATTEMPTED;
import static org.eventb.core.tests.pom.POUtil.addPredicateSet;
import static org.eventb.core.tests.pom.POUtil.addSequent;
import static org.eventb.core.tests.pom.POUtil.mTypeEnvironment;

import java.math.BigInteger;

import org.eventb.core.IPOFile;
import org.eventb.core.IPOPredicateSet;
import org.eventb.core.IPOSequent;
import org.eventb.core.ast.Expression;
import org.eventb.core.ast.ITypeEnvironment;
import org.eventb.core.ast.Predicate;
import org.eventb.core.pm.IProofAttempt;
import org.eventb.core.pm.IProofComponent;
import org.eventb.core.seqprover.IProofTree;
import org.eventb.core.seqprover.IProofTreeNode;
import org.eventb.core.seqprover.IProverSequent;
import org.eventb.core.seqprover.ITactic;
import org.eventb.core.seqprover.eventbExtensions.AutoTactics;
import org.eventb.core.tests.DeltaListener;
import org.rodinp.core.RodinDBException;

/**
 * Unit tests for Proof Components.
 * 
 * @author Laurent Voisin
 */
public class ProofAttemptTests extends AbstractProofTests {

	private static final Predicate GOAL;
	private static final Predicate GHYP;
	private static final Predicate LHYP;

	static {
		final Expression zero = ff.makeIntegerLiteral(BigInteger.ZERO, null);
		final Expression one = ff.makeIntegerLiteral(BigInteger.ONE, null);
		GOAL = ff.makeLiteralPredicate(BTRUE, null);
		GHYP = ff.makeRelationalPredicate(EQUAL, zero, zero, null);
		LHYP = ff.makeRelationalPredicate(EQUAL, one, one, null);
	}

	private IPOFile poFile;

	private IProofComponent pc;

	@Override
	protected void setUp() throws Exception {
		super.setUp();
		poFile = (IPOFile) rodinProject.getRodinFile("m.bpo");
		createPOFile();
		runBuilder();
		pc = pm.getProofComponent(poFile);
	}

	@Override
	protected void tearDown() throws Exception {
		for (final IProofAttempt pa : pm.getProofAttempts()) {
			pa.dispose();
		}
		super.tearDown();
	}

	/**
	 * Ensures that creating a proof attempt for an existing PO returns an
	 * object with the appropriate properties and that the proof obligation has
	 * been properly loaded.
	 */
	public void testAccessors() throws Exception {
		final IProofAttempt pa = pc.createProofAttempt(PO1, TEST, null);
		assertEquals(pc, pa.getComponent());
		assertEquals(PO1, pa.getName());
		assertEquals(TEST, pa.getOwner());
		assertEquals(pc.getStatus(PO1), pa.getStatus());

		final IProofTree pt = pa.getProofTree();
		final IProverSequent sequent = pt.getSequent();
		assertEquals(GOAL, sequent.goal());
		assertEquals(mSet(GHYP, LHYP), mSet(sequent.hypIterable()));
	}

	/**
	 * Ensures that the isDisposed() method works properly.
	 */
	public void testIsDisposed() throws Exception {
		final IProofAttempt pa = pc.createProofAttempt(PO1, TEST, null);
		assertFalse(pa.isDisposed());
		pa.dispose();
		assertTrue(pa.isDisposed());
	}

	/**
	 * Ensures that a proof attempt is not broken if the PO didn't change.
	 */
	public void testNotBroken() throws Exception {
		final IProofAttempt pa = pc.createProofAttempt(PO1, TEST, null);
		assertFalse(pa.isBroken());
	}

	/**
	 * Ensures that a proof attempt is not broken if the PO didn't change when
	 * the PO didn't have a stamp initially.
	 */
	public void testNotBrokenNoStamp() throws Exception {
		removePOStamps();
		final IProofAttempt pa = pc.createProofAttempt(PO1, TEST, null);
		assertFalse(pa.isBroken());
	}

	/**
	 * Ensures that a proof attempt is not broken if another PO changes.
	 */
	public void testNotBrokenOtherPOChanges() throws Exception {
		final IProofAttempt pa = pc.createProofAttempt(PO1, TEST, null);
		increasePOStamp(PO2);
		assertFalse(pa.isBroken());
	}

	/**
	 * Ensures that a proof attempt is broken if the PO changed.
	 */
	public void testBroken() throws Exception {
		final IProofAttempt pa = pc.createProofAttempt(PO1, TEST, null);
		increasePOStamp(PO1);
		runBuilder();
		assertTrue(pa.isBroken());
	}

	/**
	 * Ensures that a proof attempt is broken if the PO changed when the PO
	 * didn't have a stamp initially.
	 */
	public void testBrokenNoStamp() throws Exception {
		removePOStamps();
		final IProofAttempt pa = pc.createProofAttempt(PO1, TEST, null);
		increasePOStamp(PO1);
		runBuilder();
		assertTrue(pa.isBroken());
	}

	/**
	 * Ensures that a proof attempt is broken if the PO disappeared.
	 */
	public void testBrokenNoPO() throws Exception {
		final IProofAttempt pa = pc.createProofAttempt(PO1, TEST, null);
		poFile.getSequent(PO1).delete(false, null);
		poFile.save(null, false);
		runBuilder();
		assertTrue(pa.isBroken());
	}

	/**
	 * Ensures that a proof attempt is broken if the project has been cleaned.
	 */
	public void testBrokenClean() throws Exception {
		final IProofAttempt pa = pc.createProofAttempt(PO1, TEST, null);
		poFile.delete(false, null);
		createPOFile();
		runBuilder();
		assertTrue(pa.isBroken());
	}

	/**
	 * Ensures that one can commit an empty proof attempt successfully.
	 */
	public void testCommitEmpty() throws Exception {
		final DeltaListener dl = new DeltaListener();
		try {
			dl.start();
			final IProofAttempt pa = pc.createProofAttempt(PO1, TEST, null);
			pa.commit(true, null);
			dl.assertDeltas("Unexpected deltas for proof commit",
					"P[*]: {CHILDREN}\n" + "	m.bpr[*]: {CHILDREN}\n"
							+ "		PO1[org.eventb.core.prProof][*]: {ATTRIBUTE}\n"
							+ "	m.bps[*]: {CHILDREN}\n"
							+ "		PO1[org.eventb.core.psStatus][*]: {ATTRIBUTE}"

			);
			assertEmptyProof(pc.getProofSkeleton(PO1, ff, null));
			assertStatus(UNATTEMPTED, false, true, pc.getStatus(PO1));
		} finally {
			dl.stop();
		}
	}

	/**
	 * Ensures that one can commit a discharging proof attempt successfully.
	 */
	public void testCommitDischarge() throws Exception {
		final DeltaListener dl = new DeltaListener();
		try {
			dl.start();
			final IProofAttempt pa = pc.createProofAttempt(PO1, TEST, null);
			dischargeTrueGoal(pa);
			pa.commit(true, null);
			dl
					.assertDeltas(
							"Unexpected deltas for proof commit",
							"P[*]: {CHILDREN}\n"
									+ "	m.bpr[*]: {CHILDREN}\n"
									+ "		PO1[org.eventb.core.prProof][*]: {CHILDREN | ATTRIBUTE}\n"
									+ "			org.eventb.core.seqprover.trueGoal[org.eventb.core.prRule][+]: {}\n"
									+ "			p0[org.eventb.core.prPred][+]: {}\n"
									+ "	m.bps[*]: {CHILDREN}\n"
									+ "		PO1[org.eventb.core.psStatus][*]: {ATTRIBUTE}");
			assertNonEmptyProof(pc.getProofSkeleton(PO1, ff, null));
			assertStatus(DISCHARGED_MAX, false, true, pc.getStatus(PO1));
		} finally {
			dl.stop();
		}
	}

	/**
	 * Ensures that one can commit a discharging proof attempt successfully,
	 * even if the proof attempt is broken.
	 */
	public void testCommitBroken() throws Exception {
		final IProofAttempt pa = pc.createProofAttempt(PO1, TEST, null);
		dischargeTrueGoal(pa);
		increasePOStamp(PO1);
		pa.commit(true, null);
		assertNonEmptyProof(pc.getProofSkeleton(PO1, ff, null));
		assertStatus(DISCHARGED_MAX, true, true, pc.getStatus(PO1));
	}

	/**
	 * Ensures that one can commit a discharging proof attempt successfully,
	 * even if the proof obligation has no stamp.
	 */
	public void testCommitNoStamp() throws Exception {
		removePOStamps();
		final IProofAttempt pa = pc.createProofAttempt(PO1, TEST, null);
		dischargeTrueGoal(pa);
		pa.commit(true, null);
		assertNonEmptyProof(pc.getProofSkeleton(PO1, ff, null));
		assertStatus(DISCHARGED_MAX, false, true, pc.getStatus(PO1));
	}

	private void createPOFile() throws RodinDBException {
		poFile.create(true, null);
		final ITypeEnvironment typenv = mTypeEnvironment();
		final IPOPredicateSet hyp = addPredicateSet(poFile, "hyp", null,
				typenv, GHYP.toString());
		addSequent(poFile, PO1, GOAL.toString(), hyp, typenv, LHYP.toString());
		addSequent(poFile, PO2, GOAL.toString(), hyp, typenv, LHYP.toString());
		poFile.save(null, true);
	}

	private void dischargeTrueGoal(final IProofAttempt pa) {
		final IProofTreeNode root = pa.getProofTree().getRoot();
		ITactic tactic = new AutoTactics.TrueGoalTac();
		tactic.apply(root, null);
		assertTrue(root.isClosed());
	}

	private void increasePOStamp(String poName) throws RodinDBException {
		final IPOSequent sequent = poFile.getSequent(poName);
		final long stamp;
		if (sequent.hasPOStamp()) {
			stamp = sequent.getPOStamp();
		} else {
			stamp = 0;
		}
		sequent.setPOStamp(stamp + 1, null);
		poFile.setPOStamp(stamp + 1, null);
	}

	private void removePOStamps() throws RodinDBException {
		poFile.removeAttribute(POSTAMP_ATTRIBUTE, null);
		for (IPOSequent s : poFile.getSequents()) {
			s.removeAttribute(POSTAMP_ATTRIBUTE, null);
		}
	}

}

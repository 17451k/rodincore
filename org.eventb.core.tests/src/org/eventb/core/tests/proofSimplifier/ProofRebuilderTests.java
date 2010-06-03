/*******************************************************************************
 * Copyright (c) 2010 Systerel and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     Systerel - initial API and implementation
 *******************************************************************************/
package org.eventb.core.tests.proofSimplifier;

import org.eventb.core.EventBPlugin;
import org.eventb.core.IAxiom;
import org.eventb.core.IContextRoot;
import org.eventb.core.IPRProof;
import org.eventb.core.IPSRoot;
import org.eventb.core.IPSStatus;
import org.eventb.core.pm.IProofState;
import org.eventb.core.pm.IUserSupport;
import org.eventb.core.pm.IUserSupportManager;
import org.eventb.core.seqprover.IConfidence;
import org.eventb.core.seqprover.IProofTree;
import org.eventb.core.seqprover.IProofTreeNode;
import org.eventb.core.seqprover.ITactic;
import org.eventb.core.seqprover.eventbExtensions.AutoTactics;
import org.eventb.core.seqprover.eventbExtensions.Tactics;
import org.eventb.core.tests.pog.EventBPOTest;
import org.rodinp.core.RodinDBException;

/**
 * @author Nicolas Beauger
 * 
 */
public class ProofRebuilderTests extends EventBPOTest {

	private static void assertDischargedClosed(IPSStatus status)
			throws RodinDBException {
		assertFalse(status.isBroken());
		assertTrue(status.getConfidence() == IConfidence.DISCHARGED_MAX);
		final IPRProof proof = status.getProof();
		final IProofTree proofTree = proof.getProofTree(null);
		assertNotNull(proofTree);
		assertTrue(proofTree.isClosed());
	}

	private IPSStatus getOnlyStatus() throws RodinDBException {
		final IPSRoot[] statusFiles = rodinProject
				.getRootElementsOfType(IPSRoot.ELEMENT_TYPE);
		assertEquals(1, statusFiles.length);
		final IPSRoot statusRoot = statusFiles[0];
		final IPSStatus[] statuses = statusRoot.getStatuses();
		assertEquals(1, statuses.length);
		return statuses[0];
	}

	private IAxiom createTheorem(String thmLabel, String thmString)
			throws Exception {
		final IContextRoot ctx = createContext("C");
		addAxioms(ctx, makeSList(thmLabel), makeSList(thmString),
				makeBList(true));
		saveRodinFileOf(ctx);
		return ctx.getAxioms()[0];
	}

	private void prove(ITactic... tactics) throws RodinDBException {
		final IPSStatus status = getOnlyStatus();
		final IUserSupportManager usm = EventBPlugin.getUserSupportManager();
		final IUserSupport us = usm.newUserSupport();
		us.setInput(status.getRodinFile());
		us.setCurrentPO(status, null);
		final IProofState po = us.getCurrentPO();
		assertNotNull(po);
		final IProofTreeNode node = po.getCurrentNode();
		assertNotNull(node);
		for (ITactic tactic : tactics) {
			us.applyTactic(tactic, false, null);
		}
		us.doSave(us.getUnsavedPOs(), null);
		assertTrue(po.isClosed());
		us.dispose();
	}

	public void testRebuild() throws Exception {
		// create context and PO for ∀ x oftype ℤ· ∃ y · x=y
		final IAxiom thm = createTheorem("axm", "∀x⦂ℤ·∃y·x=y");
		// build
		runBuilder();
		// prove (free x, y inst x, true goal, simpl rewrite, true goal)
		prove(Tactics.allI(), Tactics.exI("x"), new AutoTactics.TrueGoalTac(),
				new AutoTactics.AutoRewriteTac(), new AutoTactics.TrueGoalTac());
		// change predicate into ∀ x · ∃ y · y=x
		thm.setPredicateString("∀x⦂ℤ·∃y·y=x", null);
		saveRodinFileOf(thm);
		// build
		runBuilder();
		// check proof broken
		final IPSStatus status = getOnlyStatus();
		assertTrue(status.isBroken());
		// final int confidence = status.getConfidence();
		// call EventBPlugin.rebuild()
		final boolean success = EventBPlugin.rebuildProof(status.getProof(),
				factory, null);
		// verify that rebuild worked properly
		assertTrue(success);
		assertDischargedClosed(status);
	}

}

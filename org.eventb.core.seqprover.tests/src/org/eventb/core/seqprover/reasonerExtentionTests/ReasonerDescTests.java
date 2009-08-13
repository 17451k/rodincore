/*******************************************************************************
 * Copyright (c) 2009 Systerel and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     Systerel - initial API and implementation
 *******************************************************************************/
package org.eventb.core.seqprover.reasonerExtentionTests;

import static org.eventb.core.seqprover.IReasonerDesc.NO_VERSION;
import static org.eventb.core.seqprover.reasonerExtentionTests.ReasonerRegistryTest.getDummyId;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import org.eventb.core.seqprover.IReasoner;
import org.eventb.core.seqprover.IReasonerDesc;
import org.eventb.core.seqprover.IReasonerFailure;
import org.eventb.core.seqprover.IReasonerOutput;
import org.eventb.core.seqprover.IReasonerRegistry;
import org.eventb.core.seqprover.SequentProver;
import org.eventb.core.seqprover.reasonerInputs.EmptyInput;
import org.eventb.core.seqprover.tests.TestLib;
import org.junit.Test;

/**
 * @author "Nicolas Beauger"
 * 
 */
public class ReasonerDescTests {

	private static IReasonerDesc getDesc(String id) {
		final IReasonerRegistry registry = SequentProver.getReasonerRegistry();
		return registry.getReasonerDesc(id);
	}

	@Test
	public void testGetInstance() {
		IReasoner reasoner = getDesc(TrueGoal.REASONER_ID).getInstance();
		assertTrue(reasoner instanceof TrueGoal);

		reasoner = getDesc(getDummyId()).getInstance();
		assertTrue(reasoner instanceof IReasoner);
	}

	@Test
	public void testGetName() {
		assertTrue(getDesc(TrueGoal.REASONER_ID).getName().equals("⊤ goal"));
		assertNotNull(getDesc(getDummyId()).getName());
	}

	@Test
	public void testGetId() throws Exception {
		final String id1 = getDesc(ReasonerV1.REASONER_ID).getId();
		assertEquals("Unexpected id", ReasonerV1.REASONER_ID, id1);

		final String id2 = getDesc(ReasonerV1.REASONER_ID + ":0").getId();
		assertEquals("Unexpected id", ReasonerV1.REASONER_ID, id2);
	}

	@Test
	public void testGetVersionedId() throws Exception {
		final IReasonerDesc desc = getDesc(ReasonerV1.REASONER_ID);
		final String versionedID = desc.getVersionedId();
		assertEquals("Unexpected versioned reasoner name",
				ReasonerV1.REASONER_ID + ":1", versionedID);
	}

	@Test
	public void testGetVersionedIdOtherVersion() throws Exception {
		final IReasonerDesc desc = getDesc(ReasonerV1.REASONER_ID + ":0");
		final String versionedID = desc.getVersionedId();
		assertEquals("Unexpected versioned reasoner name",
				ReasonerV1.REASONER_ID + ":0", versionedID);
	}

	@Test
	public void testGetVersion() throws Exception {
		final String msg = "Unexpected reasoner version";
		assertEquals(msg, NO_VERSION, getDesc(getDummyId()).getVersion());
		assertEquals(msg, NO_VERSION, getDesc(TrueGoal.REASONER_ID)
				.getVersion());
		assertEquals(msg, 1, getDesc(ReasonerV1.REASONER_ID).getVersion());
	}

	@Test
	public void testGetVersionedDesc() throws Exception {
		final String versionedID = ReasonerV1.REASONER_ID + ":2";
		final int version = getDesc(versionedID).getVersion();
		assertEquals("Unexpected version", 2, version);
	}

	@Test
	public void testHasVersionConflict() throws Exception {
		final IReasonerDesc desc1 = getDesc(ReasonerV1.REASONER_ID);
		assertFalse("Unexpected conflict", desc1.hasVersionConflict());

		final IReasonerDesc desc2 = getDesc(ReasonerV1.REASONER_ID + ":0");
		assertTrue("Expected a conflict", desc2.hasVersionConflict());

		final IReasonerDesc desc3 = getDesc(getDummyId());
		assertFalse("Unexpected conflict", desc3.hasVersionConflict());
	}

	/**
	 * Ensures that a dummy reasoner always fails.
	 */
	@Test
	public void testDummyReasoner() {
		final String id = getDummyId();
		IReasoner dummyReasoner = getDesc(id).getInstance();
		assertEquals(dummyReasoner.getReasonerID(), id);
		IReasonerOutput reasonerOutput = dummyReasoner.apply(TestLib
				.genSeq(" 1=1 |- 1=1"), new EmptyInput(), null);
		assertTrue(reasonerOutput instanceof IReasonerFailure);
	}

}

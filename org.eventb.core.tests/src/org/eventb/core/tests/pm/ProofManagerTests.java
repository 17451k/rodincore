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

import org.eventb.core.EventBPlugin;
import org.eventb.core.IContextRoot;
import org.eventb.core.IMachineRoot;
import org.eventb.core.pm.IProofComponent;
import org.eventb.core.pm.IProofManager;
import org.rodinp.core.IRodinFile;

/**
 * Unit tests for the Proof Manager.
 * 
 * @author Laurent Voisin
 */
public class ProofManagerTests extends AbstractProofTests {

	/**
	 * Ensures that one can get an instance of the Proof Manager.
	 */
	public void testPMExists() throws Exception {
		assertNotNull(pm);
	}

	/**
	 * Ensures that the Proof Manager is unique.
	 */
	public void testPMUnique() throws Exception {
		final IProofManager otherPM = EventBPlugin.getProofManager();
		assertEquals(pm, otherPM);
	}

	/**
	 * Ensures that one can get a Proof Component from any file related to a
	 * context.
	 */
	public void testContextProofComponent() throws Exception {
		final IRodinFile ctx = rodinProject.getRodinFile("c.buc");
		IContextRoot root = (IContextRoot) ctx.getRoot();
		final IProofComponent pc = pm.getProofComponent((IContextRoot) ctx
				.getRoot());
		assertNotNull(pc);
		assertEquals(pc, pm.getProofComponent(root.getSCContextRoot()));
		assertEquals(pc, pm.getProofComponent(root.getPORoot()));
		assertEquals(pc, pm.getProofComponent(root.getPRRoot()));
		assertEquals(pc, pm.getProofComponent(root.getPSRoot()));
	}

	/**
	 * Ensures that one can get a Proof Component from any file related to a
	 * machine.
	 */
	public void testMachineProofComponent() throws Exception {
		final IRodinFile mch = rodinProject
				.getRodinFile("m.bum");
		final IMachineRoot root = (IMachineRoot)mch.getRoot();
		final IProofComponent pc = pm.getProofComponent((IMachineRoot) mch.getRoot());
		assertNotNull(pc);
		assertEquals(pc, pm.getProofComponent(root.getSCMachineRoot()));
		assertEquals(pc, pm.getProofComponent(root.getPORoot()));
		assertEquals(pc, pm.getProofComponent(root.getPRRoot()));
		assertEquals(pc, pm.getProofComponent(root.getPSRoot()));
	}

}

/*******************************************************************************
 * Copyright (c) 2006, 2009 ETH Zurich and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     ETH Zurich - initial API and implementation
 *     Systerel - separation of file and root element
 *******************************************************************************/
package org.eventb.core.tests.pm;

import java.util.Collection;

import org.eclipse.core.runtime.CoreException;
import org.eventb.core.IPORoot;
import org.eventb.core.IPSRoot;
import org.eventb.core.pm.IUserSupport;
import org.eventb.core.pm.IUserSupportManager;
import org.rodinp.core.RodinDBException;

/**
 * Unit tests for class {@link IUserSupportManager}
 * 
 * @author htson
 */
public class TestUserSupportManagers extends TestPM {

	public void testUserSupportManager() throws RodinDBException, CoreException {
		IPORoot poRoot1 = createPOFile("x");
		IPSRoot psRoot1 = poRoot1.getPSRoot();

		IPORoot poRoot2 = createPOFile("y");
		IPSRoot psRoot2 = poRoot2.getPSRoot();

		runBuilder();

		// Initial number of opened user supports
		final int nbUS = manager.getUserSupports().size();
		
		IUserSupport userSupport1 = manager.newUserSupport();

		assertNotNull("First user support is not null ", userSupport1);
		assertNull("There is no input yet for the first user support ",
				userSupport1.getInput());

		Collection<IUserSupport> userSupports = manager.getUserSupports();
		assertEquals("There is at least one user support ", nbUS + 1, userSupports.size());
		assertTrue("The first user support is stored ", userSupports
				.contains(userSupport1));

		userSupport1.setInput(psRoot1.getRodinFile());

		assertEquals(
				"The input for first user support has been set correctly ",
				psRoot1.getRodinFile(), userSupport1.getInput());

		IUserSupport userSupport2 = manager.newUserSupport();

		assertNotNull("Second user support is not null ", userSupport2);
		assertNull("There is no input yet for the second user support ",
				userSupport2.getInput());

		userSupports = manager.getUserSupports();
		assertEquals("There are at least two user support ",
				nbUS + 2, userSupports.size());
		assertTrue("The first user support is stored ", userSupports
				.contains(userSupport1));
		assertTrue("The second user support is stored ", userSupports
				.contains(userSupport2));

		userSupport2.setInput(psRoot2.getRodinFile());

		assertEquals(
				"The input for second user support has been set correctly ",
				psRoot2.getRodinFile(), userSupport2.getInput());

		userSupport1.dispose();
		userSupports = manager.getUserSupports();
		assertEquals("There is only one user support left ", 
				nbUS + 1, userSupports.size());
		assertTrue("The second user support still exists ", userSupports
				.contains(userSupport2));

		userSupport2.dispose();
		userSupports = manager.getUserSupports();
		assertEquals("There are no user supports left ",
				nbUS, userSupports.size());

	}

}

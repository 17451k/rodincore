/*******************************************************************************
 * Copyright (c) 2008 Systerel and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License  v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Systerel - initial API and implementation
  *******************************************************************************/

package fr.systerel.explorer.tests.masterDetails.statistics;

import static junit.framework.Assert.assertNotNull;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import org.eventb.core.IEvent;
import org.eventb.core.IInvariant;
import org.eventb.core.IMachineRoot;
import org.eventb.core.IPSStatus;
import org.eventb.core.ITheorem;
import org.eventb.core.IVariable;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.rodinp.core.IRodinProject;
import org.rodinp.core.RodinDBException;

import fr.systerel.explorer.masterDetails.statistics.StatisticsTab;
import fr.systerel.explorer.model.ModelController;
import fr.systerel.explorer.model.ModelElementNode;
import fr.systerel.explorer.model.ModelMachine;
import fr.systerel.explorer.tests.ExplorerTest;

/**
 * 
 *
 */
public class StatisticsTabTest extends ExplorerTest {

	protected static StatisticsTab tab;
	protected static IRodinProject rodinProject2;
	
	@Before
	@Override
	public void setUp() throws Exception {
		super.setUp();
		tab = new StatisticsTab();
		rodinProject2 = createRodinProject("P2");
		
	}
	

	@After
	@Override
	public void tearDown() throws Exception {
		super.tearDown();
		deleteProject("P2");
		ModelController.removeProject(rodinProject);
		ModelController.removeProject(rodinProject2);
		
	}
	
	
	@Test
	public void isValidSelectionUnprocessedProject() {
		Object[] input = {rodinProject.getProject()};
		assertNotNull(tab.isValidSelection(input));
	}

	@Test
	public void isValidSelectionProcessedProject() {
		ModelController.processProject(rodinProject);
		Object[] input = {rodinProject.getProject()};
		assertNull(tab.isValidSelection(input));
	}

	@Test
	public void isValidSelectionMachine() throws RodinDBException {
		Object[] input = {createMachine("m1")};
		assertNull(tab.isValidSelection(input));
	}

	@Test
	public void isValidSelectionContext() throws RodinDBException {
		Object[] input = {createContext("c1")};
		assertNull(tab.isValidSelection(input));
	}

	@Test
	public void isValidSelectionValidNode() throws RodinDBException {
		ModelMachine machine = new ModelMachine(createMachine("m0"));
		Object[] input = {new ModelElementNode(IInvariant.ELEMENT_TYPE, machine )};
		assertNull(tab.isValidSelection(input));
	}

	@Test
	public void isValidSelectionInvalidNode() throws RodinDBException {
		ModelMachine machine = new ModelMachine(createMachine("m0"));
		Object[] input = {new ModelElementNode(IVariable.ELEMENT_TYPE, machine )};
		assertNotNull(tab.isValidSelection(input));
	}
	
	@Test
	public void isValidSelectionInvariant() throws RodinDBException {
		IMachineRoot m0 = createMachine("m0");
		IInvariant inv = createInvariant(m0, "inv");
		Object[] input = {inv};
		assertNull(tab.isValidSelection(input));
	}

	@Test
	public void isValidSelectionVariable() throws RodinDBException {
		IMachineRoot m0 = createMachine("m0");
		IVariable var = createVariable(m0, "var");
		Object[] input = {var};
		assertNotNull(tab.isValidSelection(input));
	}
	
	@Test
	public void isValidSelectionMultipleProjects() throws RodinDBException {
		ModelController.processProject(rodinProject);
		ModelController.processProject(rodinProject2);
		Object[] input = {rodinProject.getProject(), rodinProject2.getProject()};
		assertNull(tab.isValidSelection(input));
	}
	
	@Test
	public void isValidSelectionMultipleRoots() throws RodinDBException {
		Object[] input = {createMachine("m1"), createContext("c1")};
		assertNull(tab.isValidSelection(input));
	}

	@Test
	public void isValidSelectionMultipleNodes() throws RodinDBException {
		ModelMachine machine = new ModelMachine(createMachine("m0"));
		ModelElementNode inv_node = new ModelElementNode(IInvariant.ELEMENT_TYPE, machine );
		ModelElementNode evt_node = new ModelElementNode(IEvent.ELEMENT_TYPE, machine );
		Object[] input = {inv_node, evt_node};
		assertNull(tab.isValidSelection(input));
	}

	@Test
	public void isValidSelectionMultipleElements() throws RodinDBException {
		IMachineRoot m0 = createMachine("m0");
		IInvariant inv = createInvariant(m0, "inv");
		IInvariant inv2 = createInvariant(m0, "inv2");
		IEvent evt = createEvent(m0, "evt");
		ITheorem thm = createTheorem(m0, "thm");
		Object[] input = {inv, inv2, evt, thm};
		assertNull(tab.isValidSelection(input));
	}
	
	@Test
	public void isValidSelectionMultipleInvalid1() throws RodinDBException {
		ModelMachine machine = new ModelMachine(createMachine("m0"));
		ModelElementNode inv_node = new ModelElementNode(IInvariant.ELEMENT_TYPE, machine );
		ModelElementNode po_node = new ModelElementNode(IPSStatus.ELEMENT_TYPE, machine );
		Object[] input = {inv_node, po_node};
		assertNotNull(tab.isValidSelection(input));
	}

	@Test
	public void isValidSelectionMultipleInvalid2() throws RodinDBException {
		ModelController.processProject(rodinProject);
		Object[] input = {rodinProject.getProject(), createMachine("m1")};
		assertNotNull(tab.isValidSelection(input));
	}
	
	@Test
	public void isValidSelectionMultipleInvalid3() throws RodinDBException {
		IMachineRoot m0 = createMachine("m0");
		IInvariant inv = createInvariant(m0, "inv");
		Object[] input = {inv, createMachine("m1")};
		assertNotNull(tab.isValidSelection(input));
	}
	
	@Test
	public void detailsRequiredProject() {
		Object[] input = {rodinProject.getProject()};
		assertTrue(tab.detailsRequired(input));
	}

	@Test
	public void detailsRequiredMachine() throws RodinDBException {
		Object[] input = {createMachine("m1")};
		assertTrue(tab.detailsRequired(input));
	}

	@Test
	public void detailsRequiredContext() throws RodinDBException {
		Object[] input = {createContext("c1")};
		assertTrue(tab.detailsRequired(input));
	}
	
	@Test
	public void detailsRequiredNode() throws RodinDBException {
		ModelMachine machine = new ModelMachine(createMachine("m0"));
		Object[] input = {new ModelElementNode(IInvariant.ELEMENT_TYPE, machine )};
		assertTrue(tab.detailsRequired(input));
	}

	@Test
	public void detailsRequiredInvariant() throws RodinDBException {
		IMachineRoot m0 = createMachine("m0");
		IInvariant inv = createInvariant(m0, "inv");
		Object[] input = {inv};
		assertFalse(tab.detailsRequired(input));
	}

	@Test
	public void detailsRequiredMultipleRoots() throws RodinDBException {
		Object[] input = {createContext("c1"), createMachine("m1")};
		assertTrue(tab.detailsRequired(input));
	}

	@Test
	public void detailsRequiredMultipleElements() throws RodinDBException {
		IMachineRoot m0 = createMachine("m0");
		IInvariant inv = createInvariant(m0, "inv");
		IEvent evt = createEvent(m0, "evt");
		Object[] input = {inv, evt};
		assertTrue(tab.detailsRequired(input));
	}
	
}

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

package fr.systerel.explorer.tests.contentProviders;

import static junit.framework.Assert.assertNotNull;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.eventb.core.IMachineRoot;
import org.junit.Before;
import org.junit.Test;
import org.rodinp.core.RodinDBException;

import fr.systerel.explorer.model.ModelController;
import fr.systerel.explorer.navigator.contentProviders.complex.ComplexMachineContentProvider;
import fr.systerel.explorer.tests.ExplorerTest;

/**
 * 
 *
 */
public class ComplexMachineContentProviderTest extends ExplorerTest {

	private static ComplexMachineContentProvider contentProvider;
	protected static IMachineRoot m0;
	protected static IMachineRoot m1;
	protected static IMachineRoot m2;
	protected static IMachineRoot m3;

	@Before
	@Override
	public void setUp() throws Exception {
		super.setUp();
		contentProvider = new ComplexMachineContentProvider();
				
		//create some machines
		m0 = createMachine("m0");
		assertNotNull("m0 should be created successfully ", m0);

		m1 = createMachine("m1");
		assertNotNull("m1 should be created successfully ", m1);
		
		m2 = createMachine("m2");
		assertNotNull("m2 should be created successfully ", m2);

		m3 = createMachine("m3");
		assertNotNull("m3 should be created successfully ", m3);
		
		//create dependencies between machines
		createRefinesMachineClause(m1, m0, "refines1");
		assertTrue(m1.getRefinesClause("refines1").exists());
		createRefinesMachineClause(m2, m1, "refines2");
		assertTrue(m2.getRefinesClause("refines2").exists());
		createRefinesMachineClause(m3, m0, "refines3");
		assertTrue(m3.getRefinesClause("refines3").exists());
	}
	
	@Test
	public void getChildrenProject() throws RodinDBException {
		//check the children of the project
		Object[] children = contentProvider.getChildren(rodinProject.getProject());
		assertArray(children, m0, m1, m2);
}

	@Test
	public void getChildrenMachine() throws RodinDBException {
		ModelController.processProject(rodinProject);
		//check the children of the machine m0
		assertArray(contentProvider.getChildren(m0), m3);
	}
	
	@Test
	public void getParent() throws RodinDBException {
		ModelController.processProject(rodinProject);

		assertEquals(contentProvider.getParent(m0), rodinProject);
		assertEquals(contentProvider.getParent(m1), rodinProject);
		assertEquals(contentProvider.getParent(m2), rodinProject);

	}

	@Test
	public void hasChildrenProject() {
		ModelController.processProject(rodinProject);
		assertTrue(contentProvider.hasChildren(rodinProject.getProject()));
		assertTrue(contentProvider.hasChildren(m0));
		assertFalse(contentProvider.hasChildren(m1));
	}

	@Test
	public void hasChildrenMachine() {
		ModelController.processProject(rodinProject);
		assertTrue(contentProvider.hasChildren(m0));
		assertFalse(contentProvider.hasChildren(m1));
	}
	
	
	@Test
	public void getElementsProject() {
		Object[] elements = contentProvider.getElements(rodinProject.getProject());
		assertArray(elements, m0, m1, m2);
		assertArray(contentProvider.getElements(m0), m3);
	}

	@Test
	public void getElementsMachine() {
		ModelController.processProject(rodinProject);
		assertArray(contentProvider.getElements(m0), m3);
	}
	
	
}

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
package org.rodinp.internal.core.index.tests;

import static org.rodinp.internal.core.index.tests.IndexTestsUtil.assertContains;
import static org.rodinp.internal.core.index.tests.IndexTestsUtil.assertContainsNot;
import static org.rodinp.internal.core.index.tests.IndexTestsUtil.assertDescDeclaration;
import static org.rodinp.internal.core.index.tests.IndexTestsUtil.createDefaultOccurrence;
import static org.rodinp.internal.core.index.tests.IndexTestsUtil.createNamedElement;
import static org.rodinp.internal.core.index.tests.IndexTestsUtil.createRodinFile;

import org.rodinp.core.IRodinFile;
import org.rodinp.core.IRodinProject;
import org.rodinp.core.index.IDeclaration;
import org.rodinp.core.tests.basis.NamedElement;
import org.rodinp.internal.core.index.Declaration;
import org.rodinp.internal.core.index.Descriptor;
import org.rodinp.internal.core.index.Occurrence;

public class DescriptorTests extends IndexTests {

	public DescriptorTests(String name) {
		super(name, true);
	}

	private IRodinProject rodinProject;
	private IRodinFile file;
	private Descriptor testDesc;
	private NamedElement testElt1;
	private NamedElement testElt2;
	private IDeclaration declTestElt1;

	private static final String testEltName = "testElt1";

	@Override
	protected void setUp() throws Exception {
		super.setUp();
		rodinProject = createRodinProject("P");
		file = createRodinFile(rodinProject, "desc.test");
		testElt1 = createNamedElement(file, "internalName1");
		testElt2 = createNamedElement(file, "internalName2");
		declTestElt1 = new Declaration(testElt1, testEltName);
		testDesc = new Descriptor(declTestElt1);
	}

	@Override
	protected void tearDown() throws Exception {
		deleteProject("P");
		testElt1 = null;
		testElt2 = null;
		super.tearDown();
	}

	public void testConstructor() throws Exception {
		final Descriptor desc = new Descriptor(declTestElt1);
		assertDescDeclaration(desc, declTestElt1);
		assertNotNull("occurrences should not be null", testDesc
				.getOccurrences());
	}

	public void testAddHasOccurrence() throws Exception {
		final Occurrence occ =
				createDefaultOccurrence(file.getRoot(), declTestElt1);

		testDesc.addOccurrence(occ);

		assertTrue("occurrence expected: " + occ, testDesc.hasOccurrence(occ));
	}

	public void testGetOccurrences() throws Exception {
		final Occurrence occ1 = createDefaultOccurrence(testElt2, declTestElt1);
		final Occurrence occ2 = createDefaultOccurrence(file.getRoot(), declTestElt1);

		testDesc.addOccurrence(occ1);
		testDesc.addOccurrence(occ2);

		IndexTestsUtil.assertContainsAll(testDesc, occ1, occ2);
	}

	public void testRemoveOccurrences() throws Exception {
		final Occurrence localOcc = createDefaultOccurrence(testElt2, declTestElt1);
		final IRodinFile importer =
				createRodinFile(rodinProject, "importerFile.test");
		final Occurrence importOcc =
				createDefaultOccurrence(importer.getRoot(), declTestElt1);

		testDesc.addOccurrence(localOcc);
		testDesc.addOccurrence(importOcc);

		testDesc.removeOccurrences(testElt1.getRodinFile());

		assertContainsNot(testDesc, localOcc);
		assertContains(testDesc, importOcc);
	}

}

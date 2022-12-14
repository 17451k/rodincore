/*******************************************************************************
 * Copyright (c) 2000, 2014 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *     ETH Zurich - adapted from org.eclipse.jdt.core.tests.model.CopyMoveElementsTests
 *     Systerel - fixed use of pseudo-attribute "contents"
 *     Systerel - separation of file and root element
 *     Systerel - add database relations
 *******************************************************************************/
package org.rodinp.core.tests;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.rodinp.core.IRodinDBStatusConstants.INVALID_CHILD_TYPE;

import org.eclipse.core.runtime.CoreException;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.rodinp.core.IInternalElement;
import org.rodinp.core.IRodinDBStatus;
import org.rodinp.core.IRodinDBStatusConstants;
import org.rodinp.core.IRodinElement;
import org.rodinp.core.IRodinFile;
import org.rodinp.core.IRodinProject;
import org.rodinp.core.RodinDBException;
import org.rodinp.core.tests.basis.NamedElement;
import org.rodinp.core.tests.basis.NamedElement2;
import org.rodinp.core.tests.basis.RodinTestRoot;

public class CopyMoveElementsTests extends CopyMoveTests {

	// TODO add tests with two operations done at the same time
	
	@Before
	public void setUp() throws Exception {
		super.setUp();
		createRodinProject("P");
	}

	@After
	public void tearDown() throws Exception {
		deleteProject("P");
		super.tearDown();
	}

	/**
	 * Ensures that an internal element can be copied to a different file.
	 */
	@Test
	public void testCopyInt() throws CoreException {
		IRodinFile rfSource = createRodinFile("P/X.test");
		IInternalElement rSource = rfSource.getRoot();
		NamedElement neParent = createNEPositive(rSource, "parent", null);
		NamedElement neSource = createNEPositive(neParent, "foo", null);
		
		IRodinFile rfDest = createRodinFile("P/Y.test");
		IInternalElement rDest = rfDest.getRoot();
		NamedElement neDest = createNEPositive(rDest, "target", null);
		
		copyPositive(neSource, neDest, null, null, false);
	}
	
	/**
	 * Ensures that an internal element can be copied to a different
	 * file replacing an existing element.
	 */
	@Test
	public void testCopyIntForce() throws CoreException {
		IRodinFile rfSource = createRodinFile("P/X.test");
		IInternalElement rSource = rfSource.getRoot();
		NamedElement neParent = createNEPositive(rSource, "parent", null);
		NamedElement neSource = createNEPositive(neParent, "foo", null);

		IRodinFile rfDest = createRodinFile("P/Y.test");
		IInternalElement rDest = rfDest.getRoot();
		NamedElement neDest = createNEPositive(rDest, "target", null);
		createNEPositive(neDest, "foo", null);
		
		copyPositive(neSource, neDest, null, null, true);
	}

	/**
	 * Ensures that an internal element snapshot can be copied to a
	 * different file.
	 */
	@Test
	public void testCopyIntFromSnapshotToOtherFile() throws CoreException {
		IRodinFile rfSource = createRodinFile("P/X.test");
		IInternalElement rSource = rfSource.getRoot();
		NamedElement neParent = createNEPositive(rSource, "parent", null);
		NamedElement neSource = createNEPositive(neParent, "foo", null);
		rfSource.save(null, false);
		
		IRodinFile rfDest = createRodinFile("P/Y.test");
		IInternalElement rDest = rfDest.getRoot();
		NamedElement neDest = createNEPositive(rDest, "target", null);
		copyPositive(neSource.getSnapshot(), neDest, null, null, false);
	}
	
	/**
	 * Ensures that an internal element snapshot can be copied to the
	 * mutable copy of its file with a different name.
	 */
	@Test
	public void testCopyIntFromSnapshotToSameFile() throws CoreException {
		IRodinFile rfSource = createRodinFile("P/X.test");
		IInternalElement rSource = rfSource.getRoot();
		NamedElement neParent = createNEPositive(rSource, "parent", null);
		NamedElement neSource = createNEPositive(neParent, "foo", null);
		rfSource.save(null, false);
		
		copyPositive(neSource.getSnapshot(), neSource, null, "bar", false);
	}
	
	/**
	 * Ensures that copying an internal element to itself is a no-op.
	 */
	@Test
	public void testCopyIntNoop() throws CoreException {
		IRodinFile rfSource = createRodinFile("P/X.test");
		IInternalElement rSource = rfSource.getRoot();
		NamedElement neParent = createNEPositive(rSource, "parent", null);
		NamedElement neSource = createNEPositive(neParent, "foo", null);
		createNEPositive(neSource, "bar", null);

		copyNoop(neSource, null);
	}
	
	/**
	 * Ensures that an internal element can be copied to a different file,
	 * and renamed.
	 */
	@Test
	public void testCopyIntRename() throws CoreException {
		IRodinFile rfSource = createRodinFile("P/X.test");
		IInternalElement rSource = rfSource.getRoot();
		NamedElement neParent = createNEPositive(rSource, "parent", null);
		NamedElement neSource = createNEPositive(neParent, "foo", null);

		IRodinFile rfDest = createRodinFile("P/Y.test");
		IInternalElement rDest = rfDest.getRoot();
		NamedElement neDest = createNEPositive(rDest, "target", null);
		createNEPositive(neDest, "foo", null);

		copyPositive(neSource, neDest, null, "bar", false);
	}
	
	/**
	 * Ensures that an internal element can be copied to a different file,
	 * and renamed, overwriting an existing element.
	 */
	@Test
	public void testCopyIntRenameForce() throws CoreException {
		IRodinFile rfSource = createRodinFile("P/X.test");
		IInternalElement rSource = rfSource.getRoot();
		NamedElement neParent = createNEPositive(rSource, "parent", null);
		NamedElement neSource = createNEPositive(neParent, "foo", null);
		
		IRodinFile rfDest = createRodinFile("P/Y.test");
		IInternalElement rDest = rfDest.getRoot();
		NamedElement neDest = createNEPositive(rDest, "target", null);
		createNEPositive(neDest, "bar", null);

		copyPositive(neSource, neDest, null, "bar", true);
	}

	/**
	 * Ensures that an internal element can be duplicated in the same file.
	 */
	@Test
	public void testCopyIntSameParent() throws CoreException {
		IRodinFile rfSource = createRodinFile("P/X.test");
		IInternalElement rSource = rfSource.getRoot();
		NamedElement neParent = createNEPositive(rSource, "parent", null);
		NamedElement neSource = createNEPositive(neParent, "foo", null);
		
		copyPositive(neSource, neParent, null, "bar", false);
	}
	
    /**
     * Ensures that an internal element can be copied to a different file, and
     * that all its children are copied.
     */
	@Test
    public void testCopyIntTree() throws CoreException {
            IRodinFile rfSource = createRodinFile("P/X.test");
    		IInternalElement rSource = rfSource.getRoot();
            NamedElement neParent = createNEPositive(rSource, "parent", null);
            NamedElement neSource = createNEPositive(neParent, "foo", null);
            createNEPositive(neSource, "bar", null);

            IRodinFile rfDest = createRodinFile("P/Y.test");
    		IInternalElement rDest = rfDest.getRoot();
            NamedElement neDest = createNEPositive(rDest, "target", null);

            copyPositive(neSource, neDest, null, null, false);
            NamedElement neCopy = getNamedElement(neDest, "foo");
            assertEquals("Child not copied with parent",
                            neSource.getChildren().length,
                            neCopy.getChildren().length);
    }

	/**
	 * Ensures that an internal element cannot be copied to a different
	 * file replacing an existing element if no force.
	 */
	@Test
	public void testCopyIntWithCollision() throws CoreException {
		IRodinFile rfSource = createRodinFile("P/X.test");
		IInternalElement rSource = rfSource.getRoot();
		NamedElement neParent = createNEPositive(rSource, "parent", null);
		NamedElement neSource = createNEPositive(neParent, "foo", null);

		IRodinFile rfDest = createRodinFile("P/Y.test");
		IInternalElement rDest = rfDest.getRoot();
		NamedElement neDest = createNEPositive(rDest, "target", null);
		createNEPositive(neDest, "foo", null);

		copyNegative(neSource, neDest, null, null, false, IRodinDBStatusConstants.NAME_COLLISION);
	}
	
	/**
	 * Ensures that an internal element can be copied to a different
	 * file with positioning.
	 */
	@Test
	public void testCopyIntWithPositioning() throws CoreException {
		IRodinFile rfSource = createRodinFile("P/X.test");
		IInternalElement rSource = rfSource.getRoot();
		NamedElement neParent = createNEPositive(rSource, "parent", null);
		NamedElement neSource = createNEPositive(neParent, "foo", null);
		
		IRodinFile rfDest = createRodinFile("P/Y.test");
		IInternalElement rDest = rfDest.getRoot();
		NamedElement neDest = createNEPositive(rDest, "target", null);
		NamedElement neDestNext = createNEPositive(neDest, "bar", null);
		
		copyPositive(neSource, neDest, neDestNext, null, false);
	}

	/**
	 * Ensures that an internal element can be copied to a different file across projects 
	 * replacing an existing element.
	 */
	@Test
	public void testCopyIntForceInDifferentProject() throws CoreException {
		try {
			IRodinFile rfSource = createRodinFile("P/X.test");
			IInternalElement rSource = rfSource.getRoot();
			NamedElement neSource = createNEPositive(rSource, "foo", null);

			createRodinProject("P2");
			IRodinFile rfDest = createRodinFile("P2/Y.test");
			IInternalElement rDest = rfDest.getRoot();
			createNEPositive(rDest, "foo", null);

			copyPositive(neSource, rDest, null, null, true);
		} finally {
			deleteProject("P2");
		}
	}
	
	/**
	 * Ensures that one cannot copy to a snapshot.
	 */
	@Test
	public void testCopyIntToSnapshot() throws CoreException {
		IRodinFile rfSource = createRodinFile("P/X.test");
		IInternalElement rSource = rfSource.getRoot();
		NamedElement neSource = createNEPositive(rSource, "foo", null);
		rfSource.save(null, false);
		
		copyNegative(neSource, rSource.getSnapshot(), null, "bar", false,
				IRodinDBStatusConstants.READ_ONLY);
		copyNegative(neSource, neSource.getSnapshot(), null, "bar", false,
				IRodinDBStatusConstants.READ_ONLY);
	}
	
	/**
	 * Ensures that a top-level internal element can be copied to a different file in a different project.
	 */
	@Test
	public void testCopyIntInDifferentProject() throws CoreException {
		try {
			IRodinFile rfSource = createRodinFile("P/X.test");
			IInternalElement rSource = rfSource.getRoot();
			NamedElement neSource = createNEPositive(rSource, "foo", null);

			createRodinProject("P2");
			IRodinFile rfDest = createRodinFile("P2/Y.test");
			IInternalElement rDest = rfDest.getRoot();

			copyPositive(neSource, rDest, null, null, false);
		} finally {
			deleteProject("P2");
		}
	}
	
	/**
	 * Ensures that a multi status exception is generated when copying internal elements.
	 */
	@Test
	public void testCopyIntMultiStatus() throws CoreException {
		IRodinFile rfSource = createRodinFile("P/X.test");
		IInternalElement rSource = rfSource.getRoot();
		createNEPositive(rSource, "foo", null);
		createNEPositive(rSource, "bar", null);
		createNEPositive(rSource, "baz", null);

		IRodinFile rfDest = createRodinFile("P/Y.test");
		IInternalElement rDest = rfDest.getRoot();
		
		IRodinElement[] nesSource = rSource.getChildren();
		IRodinElement[] dests = new IRodinElement[nesSource.length];
		for (int i = 0; i < dests.length; i++) {
			dests[i] = rDest;
		}
		IRodinProject p = getRodinProject("P");
		dests[1] = p; //invalid destination
		dests[2]=  p;
		
		try {
			startDeltas();
			boolean e= false;
			try {
				rDest.getRodinDB().copy(nesSource, dests, null, null, false, null);
			} catch (RodinDBException rde) {
				assertTrue("Should be multistatus", rde.getStatus().isMultiStatus());
				assertTrue(
						"Should be an invalid destination",
						((IRodinDBStatus) rde.getStatus().getChildren()[0])
								.getCode() == IRodinDBStatusConstants.INVALID_DESTINATION);
				e = true;
			}
			assertTrue("Should have been an exception", e);
			
			assertDeltas(
					"Unexpected delta",
					"P[*]: {CHILDREN}\n" + 
					"	Y.test[*]: {CHILDREN}\n" + 
					"		Y[org.rodinp.core.tests.test][*]: {CHILDREN}\n" +
					"			foo[org.rodinp.core.tests.namedElement][+]: {}"
			);
			
			IRodinElement copy= generateHandle(nesSource[0], null, rDest);
			assertExists("Copy should exist", copy);
		} finally {
			stopDeltas();
		}
	}

	/**
	 * Ensures that an internal element can not be copied to an invalid
	 * top-level destination.
	 */
	@Test
	public void testCopyIntToTopInvalidChildType() throws CoreException {
		final IInternalElement rSource = createRodinFile("P/X.test").getRoot();
		final NamedElement ne = createNEPositive(rSource, "foo", null);

		final IInternalElement rDest = createRodinFile("P/Y.test2").getRoot();
		copyNegative(ne, rDest, null, null, false, INVALID_CHILD_TYPE);
	}

	/**
	 * Ensures that an internal element can not be copied to an invalid non
	 * top-level destination.
	 */
	@Test
	public void testCopyIntToIntInvalidChildType() throws CoreException {
		final IRodinFile rfSource = createRodinFile("P/X.test");
		final IInternalElement rSource = rfSource.getRoot();
		final NamedElement ne = createNEPositive(rSource, "foo", null);
		final NamedElement ne1 = createNEPositive(ne, "bar", null);

		final IRodinFile rfDest = createRodinFile("P/Y.test2");
		final IInternalElement rDest = rfDest.getRoot();
		final NamedElement2 neDest = createNE2Positive(rDest, "baz", null);
		copyNegative(ne1, neDest, null, null, false, INVALID_CHILD_TYPE);
	}

	/**
	 * Ensures that an internal element can not be copied to an invalid non
	 * top-level destination from a multiple element copy operation.
	 */
	@Test
	public void testCopyManyIntInvalidChildType() throws CoreException {
		final IInternalElement rSrc = createRodinFile("P/X.test").getRoot();
		final NamedElement ne = createNEPositive(rSrc, "foo", null);
		final NamedElement ne1 = createNEPositive(rSrc, "bar", null);

		final IInternalElement r2 = createRodinFile("P/Z.test2").getRoot();

		final IInternalElement rDest = createRodinFile("P/Y.test").getRoot();
		final IInternalElement[] toCopy = new IInternalElement[] { ne, r2, ne1 };
		final IInternalElement[] dest = new IInternalElement[] { rDest };
		copyNegative(toCopy, dest, null, null, false, INVALID_CHILD_TYPE);
		// Only two elements out of three were copied
		assertEquals(2, rDest.getChildren().length);
	}

	/**
	 * Ensures that a top-level internal element cannot be copied to an invalid destination.
	 */
	@Test
	public void testCopyTopWithInvalidDestination() throws CoreException {
		IRodinFile rfSource = createRodinFile("P/X.test");
		IInternalElement rSource = rfSource.getRoot();
		NamedElement neSource = createNEPositive(rSource, "foo", null);

		IRodinProject p = getRodinProject("P");
		
		copyNegative(neSource, p, null, null, false, IRodinDBStatusConstants.INVALID_DESTINATION);
	}

	/**
	 * Ensures that a top-level internal element cannot be copied to a different
	 * file with an invalid sibling used for positioning.
	 */
	@Test
	public void testCopyTopWithInvalidPositioning() throws CoreException {
		IRodinFile rfSource = createRodinFile("P/X.test");
		IInternalElement rSource = rfSource.getRoot();
		NamedElement neSource = createNEPositive(rSource, "foo", null);
		
		IRodinFile rfDest = createRodinFile("P/Y.test");
		IInternalElement rootDest = rfDest.getRoot();
		
		copyNegative(
				neSource, 
				rootDest, 
				rootDest.getInternalElement(NamedElement.ELEMENT_TYPE, "invalid"), 
				null, 
				false, 
				IRodinDBStatusConstants.INVALID_SIBLING);
	}
	
	/**
	 * Ensures that attempting to rename with an incorrect number of renamings fails
	 */
	@Test
	public void testCopyTopWithInvalidRenamings() throws CoreException {
		IRodinFile rfSource = createRodinFile("P/X.test");
		IInternalElement rSource = rfSource.getRoot();
		NamedElement neSource = createNEPositive(rSource, "foo", null);

		copyNegative(
				new IRodinElement[] {neSource}, 
				new IRodinElement[] {rfSource}, 
				null, 
				new String[] {"bar", "baz"}, 
				false, 
				IRodinDBStatusConstants.INVALID_RENAMING);
	}
	
	/**
	 * Ensures that an internal element can be moved to a different file.
	 */
	@Test
	public void testMoveInt() throws CoreException {
		IRodinFile rfSource = createRodinFile("P/X.test");
		IInternalElement rSource = rfSource.getRoot();
		NamedElement neParent = createNEPositive(rSource, "parent", null);
		NamedElement neSource = createNEPositive(neParent, "foo", null);
		
		IRodinFile rfDest = createRodinFile("P/Y.test");
		IInternalElement rDest = rfDest.getRoot();
		NamedElement neDest = createNEPositive(rDest, "target", null);
		
		movePositive(neSource, neDest, null, null, false);
	}
	
	/**
	 * Ensures that an internal element can be moved to a different
	 * file replacing an existing element.
	 */
	@Test
	public void testMoveIntForce() throws CoreException {
		IRodinFile rfSource = createRodinFile("P/X.test");
		IInternalElement rSource = rfSource.getRoot();
		NamedElement neParent = createNEPositive(rSource, "parent", null);
		NamedElement neSource = createNEPositive(neParent, "foo", null);

		IRodinFile rfDest = createRodinFile("P/Y.test");
		IInternalElement rDest = rfDest.getRoot();
		NamedElement neDest = createNEPositive(rDest, "target", null);
		createNEPositive(neDest, "foo", null);
		
		movePositive(neSource, neDest, null, null, true);
	}

	/**
	 * Ensures that moving an internal element to itself is a no-op.
	 */
	@Test
	public void testMoveIntNoop() throws CoreException {
		IRodinFile rfSource = createRodinFile("P/X.test");
		IInternalElement rSource = rfSource.getRoot();
		NamedElement neParent = createNEPositive(rSource, "parent", null);
		NamedElement neSource = createNEPositive(neParent, "foo", null);
		createNEPositive(neSource, "bar", null);

		moveNoop(neSource, null);
	}
	
	/**
	 * Ensures that an internal element can be moved to a different file,
	 * and renamed.
	 */
	@Test
	public void testMoveIntRename() throws CoreException {
		IRodinFile rfSource = createRodinFile("P/X.test");
		IInternalElement rSource = rfSource.getRoot();
		NamedElement neParent = createNEPositive(rSource, "parent", null);
		NamedElement neSource = createNEPositive(neParent, "foo", null);

		IRodinFile rfDest = createRodinFile("P/Y.test");
		IInternalElement rDest = rfDest.getRoot();
		NamedElement neDest = createNEPositive(rDest, "target", null);
		createNEPositive(neDest, "foo", null);

		movePositive(neSource, neDest, null, "bar", false);
	}
	
	/**
	 * Ensures that an internal element can be moved to a different file,
	 * and renamed, overwriting an existing element.
	 */
	@Test
	public void testMoveIntRenameForce() throws CoreException {
		IRodinFile rfSource = createRodinFile("P/X.test");
		IInternalElement rSource = rfSource.getRoot();
		NamedElement neParent = createNEPositive(rSource, "parent", null);
		NamedElement neSource = createNEPositive(neParent, "foo", null);
		
		IRodinFile rfDest = createRodinFile("P/Y.test");
		IInternalElement rDest = rfDest.getRoot();
		NamedElement neDest = createNEPositive(rDest, "target", null);
		createNEPositive(neDest, "bar", null);

		movePositive(neSource, neDest, null, "bar", true);
	}

	/**
	 * Ensures that an internal element can be moved in the same file.
	 */
	@Test
	public void testMoveIntSameParent() throws CoreException {
		IRodinFile rfSource = createRodinFile("P/X.test");
		IInternalElement rSource = rfSource.getRoot();
		NamedElement neParent = createNEPositive(rSource, "parent", null);
		NamedElement neSource = createNEPositive(neParent, "foo", null);
		
		movePositive(neSource, neParent, null, "bar", false);
	}
	
	/**
	 * Ensures that an internal element snapshot cannot be moved.
	 */
	@Test
	public void testMoveIntFromSnapshot() throws CoreException {
		IRodinFile rfSource = createRodinFile("P/X.test");
		IInternalElement rSource = rfSource.getRoot();
		NamedElement neParent = createNEPositive(rSource, "parent", null);
		NamedElement neSource = createNEPositive(neParent, "foo", null);
		rfSource.save(null, false);
		
		moveNegative(neSource.getSnapshot(), neParent, null, "bar", false,
				IRodinDBStatusConstants.READ_ONLY);
	}
	
	/**
	 * Ensures that an internal element cannot be moved to a snapshot.
	 */
	@Test
	public void testMoveIntToSnapshot() throws CoreException {
		IRodinFile rfSource = createRodinFile("P/X.test");
		IInternalElement rSource = rfSource.getRoot();
		NamedElement neParent = createNEPositive(rSource, "parent", null);
		NamedElement neSource = createNEPositive(neParent, "foo", null);
		rfSource.save(null, false);
		
		moveNegative(neSource, neParent.getSnapshot(), null, "bar", false,
				IRodinDBStatusConstants.READ_ONLY);
	}
	
	/**
	 * Ensures that an internal element cannot be moved to a different
	 * file replacing an existing element if no force.
	 */
	@Test
	public void testMoveIntWithCollision() throws CoreException {
		IRodinFile rfSource = createRodinFile("P/X.test");
		IInternalElement rSource = rfSource.getRoot();
		NamedElement neParent = createNEPositive(rSource, "parent", null);
		NamedElement neSource = createNEPositive(neParent, "foo", null);

		IRodinFile rfDest = createRodinFile("P/Y.test");
		IInternalElement rDest = rfDest.getRoot();
		NamedElement neDest = createNEPositive(rDest, "target", null);
		createNEPositive(neDest, "foo", null);

		moveNegative(neSource, neDest, null, null, false, IRodinDBStatusConstants.NAME_COLLISION);
	}
	
	/**
	 * Ensures that an internal element can be moved to a different
	 * file with positioning.
	 */
	@Test
	public void testMoveIntWithPositioning() throws CoreException {
		IRodinFile rfSource = createRodinFile("P/X.test");
		IInternalElement rSource = rfSource.getRoot();
		NamedElement neParent = createNEPositive(rSource, "parent", null);
		NamedElement neSource = createNEPositive(neParent, "foo", null);
		
		IRodinFile rfDest = createRodinFile("P/Y.test");
		IInternalElement rDest = rfDest.getRoot();
		NamedElement neDest = createNEPositive(rDest, "target", null);
		NamedElement neDestNext = createNEPositive(neDest, "bar", null);
		
		movePositive(neSource, neDest, neDestNext, null, false);
	}

	/**
	 * Ensures that a top-level internal element can be moved to a different file in a different project.
	 */
	@Test
	public void testMoveIntInDifferentProject() throws CoreException {
		try {
			IRodinFile rfSource = createRodinFile("P/X.test");
			IInternalElement rSource = rfSource.getRoot();
			NamedElement neSource = createNEPositive(rSource, "foo", null);

			createRodinProject("P2");
			final IRodinFile rfDest = createRodinFile("P2/Y.test");
			final IInternalElement rDest = rfDest.getRoot();

			movePositive(neSource, rDest, null, null, false);
		} finally {
			deleteProject("P2");
		}
	}
	
	/**
	 * Ensures that a multi status exception is generated when moveing top-level internal elements.
	 */
	@Test
	public void testMoveIntMultiStatus() throws CoreException {
		IRodinFile rfSource = createRodinFile("P/X.test");
		IInternalElement rSource = rfSource.getRoot();
		createNEPositive(rSource, "foo", null);
		createNEPositive(rSource, "bar", null);
		createNEPositive(rSource, "baz", null);

		IRodinFile rfDest = createRodinFile("P/Y.test");
		IInternalElement rDest = rfDest.getRoot();
		
		IRodinElement[] nesSource = rSource.getChildren();
		IRodinElement[] dests = new IRodinElement[nesSource.length];
		for (int i = 0; i < dests.length; i++) {
			dests[i] = rDest;
		}
		IRodinProject p = getRodinProject("P");
		dests[1] = p; //invalid destination
		dests[2]=  p;
		
		try {
			startDeltas();
			boolean e= false;
			try {
				rDest.getRodinDB().move(nesSource, dests, null, null, false, null);
			} catch (RodinDBException jme) {
				assertTrue("Should be multistatus", jme.getStatus().isMultiStatus());
				assertTrue(
						"Should be an invalid destination",
						((IRodinDBStatus) jme.getStatus().getChildren()[0])
								.getCode() == IRodinDBStatusConstants.INVALID_DESTINATION);
				e = true;
			}
			assertTrue("Should have been an exception", e);
			
			assertDeltas(
					"Unexpected delta",
					"P[*]: {CHILDREN}\n" + 
					"	X.test[*]: {CHILDREN}\n" + 
					"		X[org.rodinp.core.tests.test][*]: {CHILDREN}\n" + 
					"			foo[org.rodinp.core.tests.namedElement][-]: " +
					"{MOVED_TO(foo[org.rodinp.core.tests.namedElement] [in Y[org.rodinp.core.tests.test] [in Y.test [in P]]])}\n" +
					"	Y.test[*]: {CHILDREN}\n" + 
					"		Y[org.rodinp.core.tests.test][*]: {CHILDREN}\n" + 
					"			foo[org.rodinp.core.tests.namedElement][+]: " +
					"{MOVED_FROM(foo[org.rodinp.core.tests.namedElement] [in X[org.rodinp.core.tests.test] [in X.test [in P]]])}"
			);
			
			IRodinElement move= generateHandle(nesSource[0], null, rDest);
			assertExists("Move should exist", move);
		} finally {
			stopDeltas();
		}
	}

	/**
	 * Ensures that an element which type is not allowed by a top-level
	 * destination parent, can not be moved to this parent.
	 */
	@Test
	public void testMoveToTopInvalidChildType() throws CoreException {
		final IInternalElement rSource = createRodinFile("P/X.test").getRoot();
		final NamedElement ne = createNEPositive(rSource, "foo", null);

		final IInternalElement rDest = createRodinFile("P/Y.test2").getRoot();
		moveNegative(ne, rDest, null, null, false, INVALID_CHILD_TYPE);
	}

	/**
	 * Ensures that an element which type is not allowed by a non top-level
	 * destination parent, can not be moved to this parent.
	 */
	@Test
	public void testMoveToIntInvalidChildType() throws CoreException {
		final IInternalElement rSource = createRodinFile("P/X.test").getRoot();
		final NamedElement ne = createNEPositive(rSource, "foo", null);

		final IInternalElement rDest = createRodinFile("P/Y.test2").getRoot();
		final NamedElement2 ne2 = createNE2Positive(rDest, "bar", null);
		moveNegative(ne, ne2, null, null, false, INVALID_CHILD_TYPE);
	}

	/**
	 * Ensures that an internal element can not be moved to an invalid non
	 * top-level destination from a multiple element move operation.
	 */
	@Test
	public void testMoveManyInvalidChildType() throws CoreException {
		final IRodinFile rfSrc = createRodinFile("P/X.test");
		final IInternalElement rSrc = rfSrc.getRoot();
		final RodinTestRoot r2 = rSrc.createChild(RodinTestRoot.ELEMENT_TYPE,
				null, null);

		final NamedElement foo = createNEPositive(rSrc, "foo", null);
		final NamedElement bar = createNEPositive(rSrc, "bar", null);
		final NamedElement bar2 = createNEPositive(rSrc, "bar2", null);

		final IInternalElement[] toMove = new IInternalElement[] { bar, r2,
				bar2 };
		final IInternalElement[] dest = new IInternalElement[] { foo };
		moveNegative(toMove, dest, null, null, false, INVALID_CHILD_TYPE);
		// Only two elements out of three were moved
		assertEquals(2, rSrc.getChildren().length);
		assertEquals(2, foo.getChildren().length);
	}

	/**
	 * Ensures that a top-level internal element cannot be moved to an invalid destination.
	 */
	@Test
	public void testMoveTopWithInvalidDestination() throws CoreException {
		IRodinFile rfSource = createRodinFile("P/X.test");
		IInternalElement rSource = rfSource.getRoot();
		NamedElement neSource = createNEPositive(rSource, "foo", null);

		IRodinProject p = getRodinProject("P");
		
		moveNegative(neSource, p, null, null, false, IRodinDBStatusConstants.INVALID_DESTINATION);
	}

	/**
	 * Ensures that a top-level internal element cannot be moved to a different
	 * file with an invalid sibling used for positioning.
	 */
	@Test
	public void testMoveTopWithInvalidPositioning() throws CoreException {
		IRodinFile rfSource = createRodinFile("P/X.test");
		IInternalElement rSource = rfSource.getRoot();
		NamedElement neSource = createNEPositive(rSource, "foo", null);
		
		IRodinFile rfDest = createRodinFile("P/Y.test");
		IInternalElement rootDest = rfDest.getRoot();
		
		moveNegative(
				neSource, 
				rootDest, 
				rootDest.getInternalElement(NamedElement.ELEMENT_TYPE, "invalid"), 
				null, 
				false, 
				IRodinDBStatusConstants.INVALID_SIBLING);
	}
	
	/**
	 * Ensures that attempting to rename with an incorrect number of renamings fails
	 */
	@Test
	public void testMoveTopWithInvalidRenamings() throws CoreException {
		IRodinFile rfSource = createRodinFile("P/X.test");
		IInternalElement rSource = rfSource.getRoot();
		NamedElement neSource = createNEPositive(rSource, "foo", null);

		moveNegative(
				new IRodinElement[] {neSource}, 
				new IRodinElement[] {rfSource}, 
				null, 
				new String[] {"bar", "baz"}, 
				false, 
				IRodinDBStatusConstants.INVALID_RENAMING);
	}
	
	/**
	 * Ensures that an internal element can be renamed.
	 */
	@Test
	public void testRenameInt() throws CoreException {
		IRodinFile rfSource = createRodinFile("P/X.test");
		IInternalElement rSource = rfSource.getRoot();
		NamedElement neParent = createNEPositive(rSource, "parent", null);
		NamedElement neSource = createNEPositive(neParent, "foo", null);
		
		renamePositive(neSource, "bar", false);
	}

	/**
	 * Ensures that an internal element can be renamed, replacing an existing element.
	 */
	@Test
	public void testRenameIntForce() throws CoreException {
		IRodinFile rfSource = createRodinFile("P/X.test");
		IInternalElement rSource = rfSource.getRoot();
		NamedElement neParent = createNEPositive(rSource, "parent", null);
		NamedElement neSource = createNEPositive(neParent, "foo", null);
		NamedElement neDest = createNEPositive(neParent, "bar", null);
		
		renamePositive(neSource, neDest.getElementName(), true);
	}
	
	/**
	 * Ensures that renaming an internal element to itself is a no-op.
	 */
	@Test
	public void testRenameIntNoop() throws CoreException {
		IRodinFile rfSource = createRodinFile("P/X.test");
		IInternalElement rSource = rfSource.getRoot();
		NamedElement neParent = createNEPositive(rSource, "parent", null);
		NamedElement neSource = createNEPositive(neParent, "foo", null);
		createNEPositive(neSource, "bar", null);

		renameNoop(neSource);
	}
	
	/**
	 * Ensures that an internal element snapshot cannot be renamed.
	 */
	@Test
	public void testRenameIntSnapshot() throws CoreException {
		IRodinFile rfSource = createRodinFile("P/X.test");
		IInternalElement rSource = rfSource.getRoot();
		NamedElement neParent = createNEPositive(rSource, "parent", null);
		NamedElement neSource = createNEPositive(neParent, "foo", null);
		rfSource.save(null, false);
		
		renameNegative(neSource.getSnapshot(), "bar", false,
				IRodinDBStatusConstants.READ_ONLY);
	}
	
	/**
	 * Ensures that an internal element cannot be renamed,
	 * replacing an existing element if no force.
	 */
	@Test
	public void testRenameIntWithCollision() throws CoreException {
		IRodinFile rfSource = createRodinFile("P/X.test");
		IInternalElement rSource = rfSource.getRoot();
		NamedElement neParent = createNEPositive(rSource, "parent", null);
		NamedElement neSource = createNEPositive(neParent, "foo", null);
		NamedElement neDest = createNEPositive(neParent, "bar", null);

		renameNegative(neSource, neDest.getElementName(), false, IRodinDBStatusConstants.NAME_COLLISION);
	}

	/**
	 * Ensures that attempting to rename with an incorrect number of renamings fails
	 */
	@Test
	public void testRenameIntWithInvalidNames() throws CoreException {
		IRodinFile rfSource = createRodinFile("P/X.test");
		IInternalElement rSource = rfSource.getRoot();
		NamedElement neSource = createNEPositive(rSource, "foo", null);

		renameNegative(
				new IRodinElement[] {neSource}, 
				new String[] {"bar", "baz"}, 
				false, 
				IRodinDBStatusConstants.INVALID_RENAMING);
	}

	/**
	 * Ensures that an internal element snapshot cannot be reordered.
	 */
	@Test
	public void testReorderIntSnapshot() throws CoreException {
		IRodinFile rfSource = createRodinFile("P/X.test");
		IInternalElement rSource = rfSource.getRoot();
		NamedElement neParent = createNEPositive(rSource, "parent", null);
		NamedElement ne1 = createNEPositive(neParent, "foo", null);
		NamedElement ne2 = createNEPositive(neParent, "bar", null);
		rfSource.save(null, false);
		
		reorderNegative(ne2.getSnapshot(), ne1.getSnapshot(),
				IRodinDBStatusConstants.READ_ONLY);
	}
	
	/**
	 * Ensures that an internal element can be reordered.
	 */
	@Test
	public void testReorderInt() throws CoreException {
		IRodinFile rfSource = createRodinFile("P/X.test");
		IInternalElement rSource = rfSource.getRoot();
		NamedElement foo = createNEPositive(rSource, "foo", null);
		NamedElement bar = createNEPositive(rSource, "bar", null);
		NamedElement baz = createNEPositive(rSource, "baz", null);
		assertElementDescendants("Unexpected children",
				"X[org.rodinp.core.tests.test]\n" + 
				"  foo[org.rodinp.core.tests.namedElement]\n" + 
				"  bar[org.rodinp.core.tests.namedElement]\n" + 
				"  baz[org.rodinp.core.tests.namedElement]",
				rSource
		);

		// move first in first pos.
		reorderPositive(foo, bar, false);
		assertElementDescendants("Unexpected children",
				"X[org.rodinp.core.tests.test]\n" + 
				"  foo[org.rodinp.core.tests.namedElement]\n" + 
				"  bar[org.rodinp.core.tests.namedElement]\n" + 
				"  baz[org.rodinp.core.tests.namedElement]",
				rSource
		);
		
		// move first in second pos.
		reorderPositive(foo, baz, true);
		assertElementDescendants("Unexpected children",
				"X[org.rodinp.core.tests.test]\n" + 
				"  bar[org.rodinp.core.tests.namedElement]\n" + 
				"  foo[org.rodinp.core.tests.namedElement]\n" + 
				"  baz[org.rodinp.core.tests.namedElement]",
				rSource
		);
		
		// move first in last pos.
		reorderPositive(bar, null, true); 
		assertElementDescendants("Unexpected children",
				"X[org.rodinp.core.tests.test]\n" + 
				"  foo[org.rodinp.core.tests.namedElement]\n" + 
				"  baz[org.rodinp.core.tests.namedElement]\n" + 
				"  bar[org.rodinp.core.tests.namedElement]",
				rSource
		);
		
		// move second in first pos.
		reorderPositive(baz, foo, true);
		assertElementDescendants("Unexpected children",
				"X[org.rodinp.core.tests.test]\n" + 
				"  baz[org.rodinp.core.tests.namedElement]\n" + 
				"  foo[org.rodinp.core.tests.namedElement]\n" + 
				"  bar[org.rodinp.core.tests.namedElement]",
				rSource
		);
		
		// move second in second pos.
		reorderPositive(foo, bar, false);  
		assertElementDescendants("Unexpected children",
				"X[org.rodinp.core.tests.test]\n" + 
				"  baz[org.rodinp.core.tests.namedElement]\n" + 
				"  foo[org.rodinp.core.tests.namedElement]\n" + 
				"  bar[org.rodinp.core.tests.namedElement]",
				rSource
		);
		
		// move second in last pos.
		reorderPositive(foo, null, true);  
		assertElementDescendants("Unexpected children",
				"X[org.rodinp.core.tests.test]\n" + 
				"  baz[org.rodinp.core.tests.namedElement]\n" + 
				"  bar[org.rodinp.core.tests.namedElement]\n" + 
				"  foo[org.rodinp.core.tests.namedElement]",
				rSource
		);
		
		// move last in first pos.
		reorderPositive(foo, baz, true);
		assertElementDescendants("Unexpected children",
				"X[org.rodinp.core.tests.test]\n" + 
				"  foo[org.rodinp.core.tests.namedElement]\n" + 
				"  baz[org.rodinp.core.tests.namedElement]\n" + 
				"  bar[org.rodinp.core.tests.namedElement]",
				rSource
		);
		
		// move last in second pos.
		reorderPositive(bar, baz, true);
		assertElementDescendants("Unexpected children",
				"X[org.rodinp.core.tests.test]\n" + 
				"  foo[org.rodinp.core.tests.namedElement]\n" + 
				"  bar[org.rodinp.core.tests.namedElement]\n" + 
				"  baz[org.rodinp.core.tests.namedElement]",
				rSource
		);
		
		// move last in last pos.
		reorderPositive(baz, null, false);
		assertElementDescendants("Unexpected children",
				"X[org.rodinp.core.tests.test]\n" + 
				"  foo[org.rodinp.core.tests.namedElement]\n" + 
				"  bar[org.rodinp.core.tests.namedElement]\n" + 
				"  baz[org.rodinp.core.tests.namedElement]",
				rSource
		);
	}
	
}

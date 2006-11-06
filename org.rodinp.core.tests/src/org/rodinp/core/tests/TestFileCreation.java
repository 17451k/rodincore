package org.rodinp.core.tests;

import java.io.ByteArrayInputStream;
import java.io.UnsupportedEncodingException;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.runtime.CoreException;
import org.rodinp.core.IRodinElement;
import org.rodinp.core.IRodinFile;
import org.rodinp.core.IRodinProject;
import org.rodinp.core.RodinCore;
import org.rodinp.core.RodinDBException;
import org.xml.sax.SAXParseException;

public class TestFileCreation extends AbstractRodinDBTests {

	public TestFileCreation() {
		super("org.rodinp.core.tests.TestFileCreation");
	}

	private IRodinProject rodinProject;
	
	@Override
	protected void setUp() throws Exception {
		super.setUp();
		rodinProject = createRodinProject("foo");
	}

	@Override
	protected void tearDown() throws Exception {
		super.tearDown();
		rodinProject.getProject().delete(true, true, null);
		rodinProject.getRodinDB().close();
	}
	
	private void assertNotOpenable(IRodinFile rodinFile) {
		try {
			rodinFile.getChildren();
			fail("File should not be openable");
		} catch (RodinDBException rde) {
			assertTrue(rde.getException() instanceof SAXParseException);
		}
	}

	// Test creation of a Rodin file through the IResource API
	public void testCreateRodinFile1() throws Exception {
		// Check project is empty
		assertEquals("Empty project", 0, rodinProject.getChildren().length);
		assertFalse(rodinProject.hasChildren());
		// 1 because of the ".project" file
		assertEquals("Empty project", 1, rodinProject.getNonRodinResources().length);
		assertEquals("Empty project", 0, rodinProject.getRodinFiles().length);
		
		// Create one Rodin file handle
		IRodinFile rodinFile = rodinProject.getRodinFile("toto.test");
		assertNotExists("File should not exist", rodinFile);
		
		// Actually create the file
		IFile file = (IFile) rodinFile.getResource();
		
		String contents = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n"
			+ "<org.rodinp.core.tests.test/>\n";
		file.create(new ByteArrayInputStream(contents.getBytes("UTF-8")), true, null);
		assertExists("File should exist", rodinFile);
		
		// Test a memento of the file
		String memento = rodinFile.getHandleIdentifier();
		assertEquals("/foo/toto.test", memento);
		IRodinElement element = RodinCore.valueOf(memento);
		assertEquals(rodinFile, element);
		
		// Then delete it
		rodinFile.getResource().delete(true, null);
		assertNotExists("File should not exist", rodinFile);
		assertEquals("Empty project", 1, rodinProject.getNonRodinResources().length);
		assertEquals("Empty project", 0, rodinProject.getRodinFiles().length);
	}

	// Test creation of a Rodin file through the IResource API, with unnecessary
	// whitespace in the XML contents
	public void testCreateRodinFile2() throws Exception {
		assertExists("Project should exist", rodinProject);
		
		// Check project is empty
		assertEquals("Empty project", 0, rodinProject.getChildren().length);
		assertFalse(rodinProject.hasChildren());
		
		// Create one Rodin file handle
		IRodinFile rodinFile = rodinProject.getRodinFile("toto.test");
		assertNotExists("File should not exist", rodinFile);
		
		// Actually create the file
		IFile file = (IFile) rodinFile.getResource();
		
		String contents = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n"
			+ "<org.rodinp.core.tests.test>\n"
			+ "  \t\n\r  "
			+ "</org.rodinp.core.tests.test>\n";
		file.create(new ByteArrayInputStream(contents.getBytes("UTF-8")), true, null);
		assertExists("File should exist", rodinFile);

		// Check the file is empty
		assertEquals("File should be empty", 0, rodinFile.getChildren().length);
		
		// Then delete it
		rodinFile.getResource().delete(true, null);
		assertNotExists("File should not exist", rodinFile);
		assertEquals("Empty project", 1, rodinProject.getNonRodinResources().length);
		assertEquals("Empty project", 0, rodinProject.getRodinFiles().length);
	}

	// Test creation of an empty Rodin file through the IResource API
	public void testCreateEmptyRodinFile() throws Exception {
		// Check project is empty
		assertEquals("Empty project", 0, rodinProject.getChildren().length);
		assertFalse(rodinProject.hasChildren());
		// 1 because of the ".project" file
		assertEquals("Empty project", 1, rodinProject.getNonRodinResources().length);
		assertEquals("Empty project", 0, rodinProject.getRodinFiles().length);
		
		// Create one Rodin file handle
		IRodinFile rodinFile = rodinProject.getRodinFile("toto.test");
		assertNotExists("File should not exist", rodinFile);
		
		// Actually create the file
		IFile file = rodinFile.getResource();
		file.create(new ByteArrayInputStream(new byte[0]), true, null);
		assertExists("File should exist", rodinFile);
		// As the file is not an XML file, it can't be opened
		assertNotOpenable(rodinFile);
		
		// Then delete the file
		file.delete(true, null);
		assertNotExists("File should not exist", rodinFile);
		assertEquals("Empty project", 1, rodinProject.getNonRodinResources().length);
		assertEquals("Empty project", 0, rodinProject.getRodinFiles().length);
	}

	// Test creation of a Rodin file through the Rodin API
	public void testCreateRodinFile3() throws CoreException, RodinDBException{
		// Check project is empty
		assertEquals("Empty project", 0, rodinProject.getChildren().length);
		// 1 because of the ".project" file
		assertEquals("Empty project", 1, rodinProject.getNonRodinResources().length);
		assertEquals("Empty project", 0, rodinProject.getRodinFiles().length);
		
		// Create one Rodin file handle
		IRodinFile rodinFile = rodinProject.createRodinFile("toto.test", true, null);
		assertExists("File should exist", rodinFile);
		
		// Test a memento of the file
		String memento = rodinFile.getHandleIdentifier();
		assertEquals("/foo/toto.test", memento);
		IRodinElement element = RodinCore.valueOf(memento);
		assertEquals(rodinFile, element);
		
		// Then delete it
		rodinFile.getResource().delete(true, null);
		assertNotExists("File should not exist", rodinFile);
		assertEquals("Empty project", 1, rodinProject.getNonRodinResources().length);
		assertEquals("Empty project", 0, rodinProject.getRodinFiles().length);
	}

	// Test creation of a Rodin file through the Rodin API, when the file exists already
	public void testCreateExistingRodinFile() throws CoreException, RodinDBException{
		// Check project is empty
		assertEquals("Empty project", 0, rodinProject.getChildren().length);
		
		// Create one Rodin file handle
		IRodinFile rodinFile = rodinProject.createRodinFile("toto.test", true, null);
		assertExists("File should exist", rodinFile);
		
		// Create the same Rodin file again
		IRodinFile rodinFile2 = rodinProject.createRodinFile("toto.test", true, null);
		assertExists("File should exist", rodinFile2);
		assertEquals(rodinFile, rodinFile2);
		
		// Then delete it
		rodinFile.getResource().delete(true, null);
		assertNotExists("File should not exist", rodinFile);
		assertEquals("Empty project", 1, rodinProject.getNonRodinResources().length);
		assertEquals("Empty project", 0, rodinProject.getRodinFiles().length);
	}

	// Test creation of a non-Rodin file
	public void testCreateNonRodinFile() throws CoreException, RodinDBException{
		// Check project is empty
		assertEquals("Empty project", 0, rodinProject.getChildren().length);
		assertFalse(rodinProject.hasChildren());
		// 1 because of the ".project" file
		assertEquals("Empty project", 1, rodinProject.getNonRodinResources().length);
		assertEquals("Empty project", 0, rodinProject.getRodinFiles().length);
		
		// Create one Non Rodin file handle
		IFile file = rodinProject.getProject().getFile("tagada");
		assertFalse(file.exists());
		
		// Actually create the file
		file.create(new ByteArrayInputStream(new byte[] {}), true, null);
		assertTrue(file.exists());
		assertEquals("Project with one non-Rodin file", 2, rodinProject.getNonRodinResources().length);
		assertEquals("Project with one non-Rodin file", 0, rodinProject.getRodinFiles().length);
		
		// Then delete it
		file.delete(true, null);
		assertFalse(file.exists());
		assertEquals("Empty project", 1, rodinProject.getNonRodinResources().length);
		assertEquals("Empty project", 0, rodinProject.getRodinFiles().length);
		
		// Remove project
		rodinProject.getProject().delete(true, true, null);
	}

	// Test creation of a folder
	public void testCreateFolder() throws CoreException, RodinDBException{
		// Check project is empty
		assertEquals("Empty project", 0, rodinProject.getChildren().length);
		assertFalse(rodinProject.hasChildren());
		// 1 because of the ".project" file
		assertEquals("Empty project", 1, rodinProject.getNonRodinResources().length);
		assertEquals("Empty project", 0, rodinProject.getRodinFiles().length);
		
		// Create one folder handle
		IFolder folder = rodinProject.getProject().getFolder("bar");
		assertFalse(folder.exists());
		
		// Actually create the file
		folder.create(true, true, null);
		assertTrue(folder.exists());
		assertEquals("Project with one non-Rodin file", 2, rodinProject.getNonRodinResources().length);
		assertEquals("Project with one non-Rodin file", 0, rodinProject.getRodinFiles().length);
		
		// Then delete it
		folder.delete(true, null);
		assertFalse(folder.exists());
		assertEquals("Empty project", 1, rodinProject.getNonRodinResources().length);
		assertEquals("Empty project", 0, rodinProject.getRodinFiles().length);
	}

	// Test creation of a malformed Rodin file through the IResource API
	public void testCreateMalformedRodinFile() throws Exception {
		// Create one Rodin file handle
		IRodinFile rodinFile = rodinProject.getRodinFile("toto.test");
		assertNotExists("File should not exist", rodinFile);
		
		// Actually create the file
		IFile file = rodinFile.getResource();
		String contents = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n"
			+ "<org.rodinp.core.tests.test>\n"
			+ "  <  "
			+ "</org.rodinp.core.tests.test>\n";
		file.create(new ByteArrayInputStream(contents.getBytes("UTF-8")), true, null);
		assertExists("File should exist", rodinFile);
		
		// Check that the XML error is reported
		assertNotOpenable(rodinFile);
		
		// Then delete the file
		file.delete(true, null);
	}

	/**
	 * Ensures that a file which content type is unknown to Eclipse is
	 * considered as a non-Rodin resource (bug fix).
	 */
	public void testCreateFileOfUnknownType() throws CoreException, RodinDBException, UnsupportedEncodingException{
		// Create one non-Rodin file with unknown content-type
		IFile file = rodinProject.getProject().getFile("toto.xyzt");
		String contents = "Some arbitrary contents.";
		file.create(new ByteArrayInputStream(contents.getBytes("UTF-8")), false, null);

		// Checks the content-type is unknown
		assertNull(file.getContentDescription());

		// Check project contains the file as a non-Rodin resource
		assertEquals("Project with one non-Rodin file", 0, rodinProject.getChildren().length);
		assertFalse(rodinProject.hasChildren());
		// 2 because of the ".project" file
		assertEquals("Project with one non-Rodin file", 2, rodinProject.getNonRodinResources().length);
		assertEquals("Project with one non-Rodin file", 0, rodinProject.getRodinFiles().length);
	}

}

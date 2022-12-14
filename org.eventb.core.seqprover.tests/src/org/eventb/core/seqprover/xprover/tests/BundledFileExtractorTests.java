/*******************************************************************************
 * Copyright (c) 2007, 2013 ETH Zurich and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     ETH Zurich - initial API and implementation
 *     Systerel - now checks that the execution right can be given
 *******************************************************************************/
package org.eventb.core.seqprover.xprover.tests;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.io.File;

import org.eclipse.core.filesystem.EFS;
import org.eclipse.core.filesystem.IFileSystem;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Platform;
import org.eventb.core.seqprover.xprover.BundledFileExtractor;
import org.junit.Test;
import org.osgi.framework.Bundle;


/**
 * Unit tests for class BundledFileExtractor.
 * 
 * @author Laurent Voisin
 */
public class BundledFileExtractorTests {

	private static String BUNDLE_NAME = "org.eventb.core.seqprover.tests";
	private static Bundle bundle = Platform.getBundle(BUNDLE_NAME);

	@Test
	public void checkFSAttributes() {
		final IFileSystem fs = EFS.getLocalFileSystem();
		assertTrue("The local file system should manage execution permission.",
				(fs.attributes() & EFS.ATTRIBUTE_EXECUTABLE) != 0);
	}

	@Test
	public void extractData() throws Exception {
		IPath localPath = new Path("lib/data.txt");
		IPath osPath = BundledFileExtractor.extractFile(bundle, localPath, false);
		assertNotNull("File should have been extracted", osPath);

		File file = osPath.toFile();
		assertTrue(file.exists());
	}
	
	@Test
	public void extractDataFailure() throws Exception {
		IPath localPath = new Path("lib/inexistent-file");
		IPath osPath = BundledFileExtractor.extractFile(bundle, localPath, false);
		assertNull("File shouldn't have been extracted", osPath);
	}
	
	@Test
	public void extractExec() throws Exception {
		IPath localPath = new Path("$os$/simple");
		IPath osPath = BundledFileExtractor.extractFile(bundle, localPath, true);
		assertNotNull("File should have been extracted", osPath);

		File file = osPath.toFile();
		assertTrue(file.exists());
	}

	@Test
	public void extractExecFailure() throws Exception {
		IPath localPath = new Path("$os$/inexistent-file");
		IPath osPath = BundledFileExtractor.extractFile(bundle, localPath, true);
		assertNull("File shouldn't have been extracted", osPath);
	}
	
}

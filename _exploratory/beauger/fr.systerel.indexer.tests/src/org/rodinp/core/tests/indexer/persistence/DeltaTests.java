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
package org.rodinp.core.tests.indexer.persistence;

import static org.rodinp.core.tests.indexer.IndexTestsUtil.*;
import static org.rodinp.internal.core.indexer.persistence.xml.XMLUtils.*;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.IPath;
import org.rodinp.core.ElementChangedEvent;
import org.rodinp.core.IElementChangedListener;
import org.rodinp.core.IOpenable;
import org.rodinp.core.IRodinElement;
import org.rodinp.core.IRodinElementDelta;
import org.rodinp.core.IRodinFile;
import org.rodinp.core.IRodinProject;
import org.rodinp.core.RodinCore;
import org.rodinp.core.tests.basis.NamedElement;
import org.rodinp.core.tests.indexer.IndexTests;
import org.rodinp.internal.core.indexer.IndexManager;

/**
 * @author Nicolas Beauger
 * 
 */
public class DeltaTests extends IndexTests {

	private static IRodinProject project;

	public DeltaTests(String name) {
		super(name, true);
	}

	@Override
	protected void setUp() throws Exception {
		super.setUp();
		project = createRodinProject("P");
	}

	static void addAffectedFiles(IRodinElementDelta delta, List<IRodinFile> list) {
		final IRodinElement element = delta.getElement();
		if (!(element instanceof IOpenable)) {
			// No chance to find a file below
			return;
		}
		if (element instanceof IRodinFile) {
			final IRodinFile file = (IRodinFile) element;
			list.add(file);
			return;
		}
		for (IRodinElementDelta childDelta : delta.getAffectedChildren()) {
			addAffectedFiles(childDelta, list);
		}
	}

	@Override
	protected void tearDown() throws Exception {
		deleteProject("P");
		IndexManager.getDefault().clear();
		super.tearDown();
	}

	private static class FakeListener implements IElementChangedListener {
		private final List<IRodinFile> list;

		public FakeListener() {
			list = new ArrayList<IRodinFile>();
		}

		public void elementChanged(ElementChangedEvent event) {
			addAffectedFiles(event.getDelta(), list);
		}

		public List<IRodinFile> getList() {
			return list;
		}
	}

	/**
	 * Verify that, when a project is closed, if it is modified outside Rodin
	 * platform, then opened again, a delta will be generated when performing a
	 * refresh.
	 * 
	 * @throws Exception
	 */
	public void testModifOutsideWhenProjectClosed() throws Exception {

		final IRodinFile file = createRodinFile(project, "delta2.test");
		NamedElement elt = getNamedElement(file.getRoot(), "elt");
		elt.create(null, null);
		file.save(null, true);

		final IPath location = file.getResource().getLocation();

		final List<IRodinFile> expected = Arrays.asList(file);

		project.getProject().close(null);

		final File resource = location.toFile();

		final String xml =
				"<?xml version=\"1.0\" encoding=\"UTF-8\"?>"
						+ "<org.rodinp.core.tests.test/>";
		Thread.sleep(1000); // Ensure file timestamp is at least 1 second after
		write(resource, xml);

		project.getProject().open(null);

		final FakeListener listener = new FakeListener();
		RodinCore.addElementChangedListener(listener);

		project.getProject().refreshLocal(IResource.DEPTH_INFINITE, null);

		RodinCore.removeElementChangedListener(listener);

		assertSameElements(expected, listener.getList(), "delta file");
	}

}

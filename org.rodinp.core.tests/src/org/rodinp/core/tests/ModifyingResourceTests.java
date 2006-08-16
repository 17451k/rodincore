/*******************************************************************************
 * Copyright (c) 2005 ETH Zurich.
 * Strongly inspired by org.eclipse.jdt.core.tests.model.ModifyingResourceTests.java which is
 * 
 * Copyright (c) 2000, 2005 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.rodinp.core.tests;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.util.Arrays;
import java.util.Comparator;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IWorkspaceRunnable;
import org.eclipse.core.resources.ResourceAttributes;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.Path;
import org.rodinp.core.IInternalElement;
import org.rodinp.core.IInternalParent;
import org.rodinp.core.IParent;
import org.rodinp.core.IRodinDBStatus;
import org.rodinp.core.IRodinDBStatusConstants;
import org.rodinp.core.IRodinElement;
import org.rodinp.core.IRodinElementDelta;
import org.rodinp.core.IRodinFile;
import org.rodinp.core.RodinDBException;
import org.rodinp.core.basis.RodinElement;
import org.rodinp.core.tests.basis.NamedElement;
import org.rodinp.core.tests.util.Util;

/*
 * Tests that modify resources in the workspace.
 */
public abstract class ModifyingResourceTests extends AbstractRodinDBTests {
	
	static final String emptyContents = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n"
		+ "<org.rodinp.core.tests.test/>\n";
	static byte[] emptyBytes = emptyContents.getBytes();

	public ModifyingResourceTests(String name) {
		super(name);
	}
	
	protected void assertElementDescendants(String message,  String expected, IRodinElement element) throws CoreException {
		String actual = expandAll(element);
		if (!expected.equals(actual)){
			System.out.println(Util.displayString(actual, 4));
		}
		assertEquals(
				message,
				expected,
				actual);
	}
	
	protected IRodinFile createRodinFile(String path) throws CoreException {
		createFile(path, emptyContents);
		return getRodinFile(path);
	}
	
	protected IFile createFile(String path, InputStream content) throws CoreException {
		IFile file = getFile(path);
		file.create(content, true, null);
		return file;
	}
	
	protected IFile createFile(String path, byte[] content) throws CoreException {
		return createFile(path, new ByteArrayInputStream(content));
	}
	
	protected IFile createFile(String path, String content) throws CoreException {
		return createFile(path, content.getBytes());
	}

	protected IFile createFile(String path, String content, String charsetName) throws CoreException, UnsupportedEncodingException {
		return createFile(path, content.getBytes(charsetName));
	}
	
	protected IFolder createFolder(String path) throws CoreException {
		return createFolder(new Path(path));
	}
	
	protected NamedElement createNamedElement(IInternalParent parent, String name,
			IInternalElement nextSibling) throws RodinDBException {
		
		final String type = NamedElement.ELEMENT_TYPE;
		final IInternalElement element = parent.getInternalElement(type, name);
		if (element.exists()) {
			try {
				parent.createInternalElement(type, name, nextSibling, null);
				fail("Should have raised an exception");
			} catch (RodinDBException e) {
				final IRodinDBStatus status = e.getRodinDBStatus();
				assertEquals("Status should be an error",
						IRodinDBStatus.ERROR,
						status.getSeverity());
				assertEquals("Status code should be a name collision", 
						IRodinDBStatusConstants.NAME_COLLISION, 
						status.getCode());
				IRodinElement[] elements = status.getElements(); 
				assertEquals("Status should be related to the given element", 
						1, 
						elements.length);
				assertEquals("Status should be related to the given element", 
						element, 
						elements[0]);
			}
			assertTrue("Conflicting element should still exist", element.exists());
			return null;
		}
		IRodinElement result = 
				parent.createInternalElement(type, name, nextSibling, null);
		assertTrue("Created element should exist", element.exists());
		return (NamedElement) result;
	}
	
	protected void deleteFile(String filePath) throws CoreException {
		deleteResource(this.getFile(filePath));
	}
	
	protected void deleteFolder(String folderPath) throws CoreException {
		deleteFolder(new Path(folderPath));
	}
	
	protected IFile editFile(String path, String content) throws CoreException {
		IFile file = this.getFile(path);
		InputStream input = new ByteArrayInputStream(content.getBytes());
		file.setContents(input, IResource.FORCE, null);
		return file;
	}
	
	/* 
	 * Expands (i.e. open) the given element and returns a toString() representation
	 * of the tree.
	 */
	protected String expandAll(IRodinElement element) throws CoreException {
		StringBuilder buffer = new StringBuilder();
		this.expandAll(element, 0, buffer);
		return buffer.toString();
	}
	
	private void expandAll(IRodinElement element, int tab, StringBuilder buffer) throws CoreException {
		IRodinElement[] children = null;
		// force opening of element by getting its children
		if (element instanceof IParent) {
			IParent parent = (IParent)element;
			children = parent.getChildren();
		}
		((RodinElement)element).toStringInfo(tab, buffer);
		if (children != null) {
			for (int i = 0, length = children.length; i < length; i++) {
				buffer.append("\n");
				this.expandAll(children[i], tab+1, buffer);
			}
		}
	}
	
	protected void renameProject(String project, String newName) throws CoreException {
		this.getProject(project).move(new Path(newName), true, null);
	}

	protected IFolder getFolder(String path) {
		return getFolder(new Path(path));
	}
	
	protected String getSortedByProjectDeltas() {
		StringBuffer buffer = new StringBuffer();
		for (int i=0, length = this.deltaListener.deltas.length; i<length; i++) {
			IRodinElementDelta[] projects = this.deltaListener.deltas[i].getAffectedChildren();
			int projectsLength = projects.length;
			
			// sort by project
			IRodinElementDelta[] sorted = new IRodinElementDelta[projectsLength];
			System.arraycopy(projects, 0, sorted, 0, projectsLength);
			Arrays.sort(
					sorted, 
					new  Comparator<IRodinElementDelta>() {
						public int compare(IRodinElementDelta a, IRodinElementDelta b) {
							return a.toString().compareTo(b.toString());
						}
					});
			
			for (int j=0; j<projectsLength; j++) {
				buffer.append(sorted[j]);
				if (j != projectsLength-1) {
					buffer.append("\n");
				}
			}
			if (i != length-1) {
				buffer.append("\n\n");
			}
		}
		return buffer.toString();
	}
	
	protected void moveFile(String sourcePath, String destPath) throws CoreException {
		this.getFile(sourcePath).move(this.getFile(destPath).getFullPath(), false, null);
	}
	
	protected void moveFolder(String sourcePath, String destPath) throws CoreException {
		this.getFolder(sourcePath).move(this.getFolder(destPath).getFullPath(), false, null);
	}
	
	protected void swapFiles(String firstPath, String secondPath) throws CoreException {
		final IFile first = this.getFile(firstPath);
		final IFile second = this.getFile(secondPath);
		IWorkspaceRunnable runnable = new IWorkspaceRunnable(	) {
			public void run(IProgressMonitor monitor) throws CoreException {
				IPath tempPath = first.getParent().getFullPath().append("swappingFile.temp");
				first.move(tempPath, false, monitor);
				second.move(first.getFullPath(), false, monitor);
				getWorkspaceRoot().getFile(tempPath).move(second.getFullPath(), false, monitor);
			}
		};
		getWorkspace().run(runnable, null);
	}

	public void setReadOnly(IResource resource, boolean readOnly) throws CoreException {
		ResourceAttributes resourceAttributes = resource.getResourceAttributes();
		if (resourceAttributes != null) {
			resourceAttributes.setReadOnly(readOnly);
			resource.setResourceAttributes(resourceAttributes);
		}		
	}
	
	public boolean isReadOnly(IResource resource) throws CoreException {
		ResourceAttributes resourceAttributes = resource.getResourceAttributes();
		if (resourceAttributes != null) {
			return resourceAttributes.isReadOnly();
		}
		return false;
	}
}

/*******************************************************************************
 * Copyright (c) 2005 ETH Zurich.
 * Strongly inspired by org.eclipse.jdt.internal.core.JavaModel.java which is
 * 
 * Copyright (c) 2000, 2005 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.rodinp.internal.core;

import java.util.ArrayList;
import java.util.Map;

import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.Path;
import org.rodinp.core.IOpenable;
import org.rodinp.core.IRodinDB;
import org.rodinp.core.IRodinElement;
import org.rodinp.core.IRodinProject;
import org.rodinp.core.RodinDBException;
import org.rodinp.core.basis.Openable;
import org.rodinp.core.basis.RodinElement;
import org.rodinp.internal.core.util.MementoTokenizer;
import org.rodinp.internal.core.util.Messages;

/**
 * Implementation of
 * <code>IRodinDB<code>. The Rodin database maintains a cache of
 * active <code>IRodinProject</code>s in a workspace. A Rodin database is specific to a
 * workspace. To retrieve a workspace's database, use the
 * <code>#getRodinDB(IWorkspace)</code> method.
 *
 * @see IRodinDB
 */
public class RodinDB extends Openable implements IRodinDB {

	/**
	 * Constructs a new Rodin database on the given workspace. Note that only
	 * one instance of RodinDB handle should ever be created. One should only
	 * indirect through RodinDBManager#getRodinDB() to get access to it.
	 * 
	 * @exception Error
	 *                if called more than once
	 */
	protected RodinDB() throws Error {
		super(null);
	}

	@Override
	protected boolean buildStructure(OpenableElementInfo info,
			IProgressMonitor pm,
			Map<IRodinElement, RodinElementInfo> newElements,
			IResource underlyingResource) {

		// determine my children
		IProject[] projects = ResourcesPlugin.getWorkspace().getRoot()
				.getProjects();
		for (IProject project : projects) {
			if (RodinProject.hasRodinNature(project)) {
				info.addChild(getRodinProject(project));
			}
		}
		newElements.put(this, info);
		return true;
	}

	/*
	 * @see IRodinDB
	 */
	public boolean contains(IResource resource) {
		switch (resource.getType()) {
		case IResource.ROOT:
		case IResource.PROJECT:
			return true;
		}
		// file or folder
		IRodinProject[] projects;
		try {
			projects = this.getRodinProjects();
		} catch (RodinDBException e) {
			return false;
		}
		for (IRodinProject project: projects) {
			if (((RodinProject) project).contains(resource)) {
				return true;
			}
		}
		return false;
	}

	/*
	 * @see IRodinDB
	 */
	public void copy(IRodinElement[] elements, IRodinElement[] containers,
			IRodinElement[] siblings, String[] renamings, boolean force,
			IProgressMonitor monitor) throws RodinDBException {
		
		if (elements != null && elements.length > 0 && elements[0] != null
				&& elements[0] instanceof IOpenable) {
			runOperation(new CopyResourceElementsOperation(elements,
					containers, force), elements, siblings, renamings, monitor);
		} else {
			runOperation(
					new CopyElementsOperation(elements, containers, force),
					elements, siblings, renamings, monitor);
		}
	}

	/**
	 * Returns a new element info for this element.
	 */
	@Override
	protected RodinElementInfo createElementInfo() {
		return new RodinDBInfo();
	}

	/*
	 * @see IRodinDB
	 */
	public void delete(IRodinElement[] elements, boolean force,
			IProgressMonitor monitor) throws RodinDBException {
		
		if (elements != null && elements.length > 0 && elements[0] instanceof IOpenable) {
			new DeleteResourceElementsOperation(elements, force).runOperation(monitor);
		} else {
			new DeleteElementsOperation(elements, force).runOperation(monitor);
		}
	}

	@Override
	public boolean equals(Object o) {
		if (!(o instanceof RodinDB))
			return false;
		return super.equals(o);
	}

	/**
	 * Finds the given project in the list of the Rodin database's children.
	 * Returns null if not found.
	 */
	public IRodinProject findOldRodinProject(IProject project) {
		try {
			for (IRodinProject rodinProject: getOldRodinProjects()) {
				if (project.equals(rodinProject.getProject())) {
					return rodinProject;
				}
			}
		} catch (RodinDBException e) {
			// Rodin database doesn't exist: cannot find any project
		}
		return null;
	}

	/**
	 * @see IRodinElement
	 */
	@Override
	public String getElementType() {
		return RODIN_DATABASE;
	}

	/*
	 * @see RodinElement
	 */
	@Override
	public IRodinElement getHandleFromMemento(String token,
			MementoTokenizer memento) {
		switch (token.charAt(0)) {
		case REM_EXTERNAL:
			if (!memento.hasMoreTokens())
				return this;
			String projectName = memento.nextToken();
			RodinElement project = (RodinElement) getRodinProject(projectName);
			if (project == null) {
				return null;
			}
			return project.getHandleFromMemento(memento);
		}
		return null;
	}

	@Override
	protected void getHandleMemento(StringBuilder buff) {
		// The database is implicit in the memento.
	}

	/**
	 * Returns the <code>char</code> that marks the start of this handles
	 * contribution to a memento.
	 */
	@Override
	protected char getHandleMementoDelimiter() {
		assert false : "Should not be called"; //$NON-NLS-1$
		return 0;
	}

	/**
	 * Returns the list of Rodin projects before resource delta processing
	 * has started.
	 */
	public IRodinProject[] getOldRodinProjects() throws RodinDBException {
		RodinDBManager manager = RodinDBManager.getRodinDBManager();
		return manager.deltaState.dbProjectsCache == null ? 
				this.getRodinProjects() : 
				manager.deltaState.dbProjectsCache; 
	}
	
	/*
	 * @see IRodinDB
	 */
	public IRodinProject getRodinProject(String projectName) {
		return new RodinProject(ResourcesPlugin.getWorkspace().getRoot()
				.getProject(projectName), this);
	}

	/**
	 * Returns the active Rodin project associated with the specified resource,
	 * or <code>null</code> if no Rodin project yet exists for the resource.
	 * 
	 * @exception IllegalArgumentException
	 *                if the given resource is not one of an IProject, IFolder,
	 *                or IFile.
	 */
	public RodinProject getRodinProject(IResource resource) {
		switch (resource.getType()) {
		case IResource.FOLDER:
			return new RodinProject(((IFolder) resource).getProject(), this);
		case IResource.FILE:
			return new RodinProject(((IFile) resource).getProject(), this);
		case IResource.PROJECT:
			return new RodinProject((IProject) resource, this);
		default:
			throw new IllegalArgumentException(
					Messages.element_invalidResourceForProject);
		}
	}

	/*
	 * @see IRodinDB
	 */
	public IRodinProject[] getRodinProjects() throws RodinDBException {
		ArrayList<IRodinElement> list = getFilteredChildrenList(RODIN_PROJECT);
		return list.toArray(new IRodinProject[list.size()]);

	}

	/*
	 * @see IRodinDB
	 */
	public IResource[] getNonRodinResources() throws RodinDBException {
		return ((RodinDBInfo) getElementInfo()).getNonRodinResources();
	}

	/*
	 * @see IRodinElement
	 */
	public IPath getPath() {
		return Path.ROOT;
	}

	/*
	 * @see IRodinElement
	 */
	public IResource getResource() {
		return ResourcesPlugin.getWorkspace().getRoot();
	}

	/*
	 * @see IOpenable
	 */
	public IWorkspaceRoot getUnderlyingResource() {
		return ResourcesPlugin.getWorkspace().getRoot();
	}

	/**
	 * Returns the workspace associated with this object.
	 */
	public IWorkspace getWorkspace() {
		return ResourcesPlugin.getWorkspace();
	}

	/**
	 * Configures and runs the <code>MultiOperation</code>.
	 */
	public void runOperation(MultiOperation op, IRodinElement[] elements,
			IRodinElement[] siblings, String[] renamings,
			IProgressMonitor monitor) throws RodinDBException {
		op.setRenamings(renamings);
		if (siblings != null) {
			for (int i = 0; i < elements.length; i++) {
				op.setInsertBefore(elements[i], siblings[i]);
			}
		}
		op.runOperation(monitor);
	}

	/*
	 * @private Debugging purposes
	 */
	@Override
	protected void toStringInfo(int tab, StringBuilder buffer, RodinElementInfo info) {
		buffer.append(this.tabString(tab));
		buffer.append("Rodin database"); //$NON-NLS-1$
		if (info == null) {
			buffer.append(" (not open)"); //$NON-NLS-1$
		}
	}

	/**
	 * Helper method - returns the targeted item (IResource), or null if unbound
	 * Items must be referred to using container relative paths.
	 */
	public static Object getTarget(IContainer container, IPath path,
			boolean checkResourceExistence) {

		if (path == null)
			return null;

		// lookup - inside the container
		if (path.getDevice() == null) { // container relative paths should not
			// contain a device
			IResource resource = container.findMember(path);
			if (resource != null) {
				if (!checkResourceExistence || resource.exists())
					return resource;
				return null;
			}
		}
		return null;
	}

	@Override
	public String getElementName() {
		return "";
	}

	/*
	 * @see IRodinDB
	 */
	public void move(IRodinElement[] elements, IRodinElement[] containers,
			IRodinElement[] siblings, String[] renamings, boolean force,
			IProgressMonitor monitor) throws RodinDBException {
		
		if (elements != null && elements.length > 0 && elements[0] != null
				&& elements[0] instanceof IOpenable) {
			runOperation(new MoveResourceElementsOperation(elements,
					containers, force), elements, siblings, renamings, monitor);
		} else {
			runOperation(
					new MoveElementsOperation(elements, containers, force),
					elements, siblings, renamings, monitor);
		}
	}

	/*
	 * @see IRodinDB
	 */
	public void rename(IRodinElement[] elements, String[] names, boolean replace, IProgressMonitor monitor) throws RodinDBException {
		MultiOperation op;
		if (elements != null && elements.length > 0 && elements[0] != null
				&& elements[0] instanceof IOpenable) {
			op = new RenameResourceElementsOperation(elements, names, replace);
		} else {
			op = new RenameElementsOperation(elements, names, replace);
		}
		op.runOperation(monitor);
	}

}

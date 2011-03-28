/*******************************************************************************
 * Copyright (c) 2011 Systerel and others.
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 * 
 * Contributors:
 *     Southampton - Initial API and implementation
 *     Systerel - Adaptation from the persistence class RodinResource
 *******************************************************************************/
package org.rodinp.core.emf.lightcore;

import java.io.IOException;
import java.util.Map;


import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IWorkspaceRunnable;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.resource.impl.ResourceImpl;
import org.rodinp.core.IInternalElement;
import org.rodinp.core.IRodinFile;
import org.rodinp.core.IRodinProject;
import org.rodinp.core.RodinCore;
import org.rodinp.core.RodinDBException;
import org.rodinp.core.emf.lightcore.sync.SynchroManager;

/**
 * 
 * This is the serialisation of Event-B models from EMF into the Rodin database
 * We overload save and load directly as we are not interested in input or
 * output streams (because we load/save through the Rodin API)
 * 
 * We extend XMIResourceImpl (rather than ResourceImpl). This allows clients to
 * call the I/O stream versions of save and load to obtain the model content in
 * EMF's default XMI stream. For example, EMF compare uses this.
 * 
 * If file extension is "xmb", default xmi serialisation is used.
 * 
 * @author cfs/ff
 * 
 */
public class RodinResource extends ResourceImpl {

	private IRodinFile rodinFile;
	private IRodinProject rodinProject;
	private IFile file;
	private IProject project;

	@Override
	public void setURI(final URI uri) {
		String projectName;
		super.setURI(uri);
		final int segmentCount = uri.segmentCount();
		if ("platform".equals(uri.scheme())) {
			projectName = URI.decode(uri.segment(segmentCount - 2));
			final String fileName = URI.decode(uri.segment(segmentCount - 1));
			rodinProject = RodinCore.getRodinDB().getRodinProject(projectName);
			project = (IProject) rodinProject.getCorrespondingResource();
			rodinFile = rodinProject.getRodinFile(fileName);
			file = rodinFile.getResource();
		} else if (null == uri.scheme()) {
			projectName = null;
			final String fileName = URI.decode(uri.segment(segmentCount - 1));
			rodinFile = rodinProject.getRodinFile(fileName);
			file = rodinFile.getResource();
		}

	}

	@Override
	public void load(final Map<?, ?> options) throws IOException {
		try {
			isLoading = true;
			// does file already exist? -> load
			if (exists()) {
				try {
					this.getContents().add(
							SynchroManager.getDefault().getModelForRoot(
									rodinFile.getRoot()));
				} catch (Exception e) {
					e.printStackTrace();
				}
				// success
				setTimeStamp(System.currentTimeMillis());
				setLoaded(true);
			}
			// otherwise throw exception
			else {
				throw new IOException("Resource does not exist");
			}
		} finally {
			isLoading = false;
		}
	}

	@Override
	public void save(final Map<?, ?> options) throws IOException {
		saveAsRodin(options);
	}

	private void saveAsRodin(final Map<?, ?> options) throws IOException {
		if (!isLoaded || isLoading) // || !isModified )
			return;
		try {

			if (!exists()) {
				// create new RodinFile
				try {
					rodinFile.create(true, null);
					// success
					setTimeStamp(System.currentTimeMillis());

				} catch (final RodinDBException e) {
					throw new IOException("Error while creating rodin file: "
							+ e.getLocalizedMessage());
				}
			}

			try {
				RodinCore.run(new IWorkspaceRunnable() {
					public void run(final IProgressMonitor monitor)
							throws CoreException {
						for (EObject content : getContents()) {
							if (content instanceof IInternalElement) {
								SynchroManager.getDefault().saveModelFromRoot(
										(IInternalElement) content);
							}
						}
						rodinFile.save(null, true);
					}
				}, null);

			} catch (RodinDBException e) {
				throw new IOException("Error while saving rodin file: "
						+ e.getLocalizedMessage());
			}
			// success
			setTimeStamp(System.currentTimeMillis());
			isModified = false;

		} finally {
		}
	}

	public IRodinFile getRodinFile() {
		return rodinFile;
	}

	public IResource getUnderlyingResource() {
		if (file == null) {
			return project;
		} else
			return file;
	}

	/**
	 * Returns whether this resource exists.
	 * 
	 * @exception IOException
	 *                if the resource is not properly defined.
	 */
	private boolean exists() throws IOException {
		// valid project?
		if (rodinProject == null && project == null) {
			throw new IOException("Invalid project name: "
					+ uri.segment(uri.segmentCount() - 2));
		}
		// valid file for RodinFile?
		if (rodinFile == null && file == null) {
			throw new IOException("Invalid file name: "
					+ uri.segment(uri.segmentCount() - 1));
		}
		// does file exist?
		return rodinFile == null ? file.exists() : rodinFile.exists();
	}
}
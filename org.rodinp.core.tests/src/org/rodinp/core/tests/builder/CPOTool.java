/*******************************************************************************
 * Copyright (c) 2006 ETH Zurich.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.rodinp.core.tests.builder;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.rodinp.core.builder.IGraph;

/**
 * @author Stefan Hallerstede
 *
 */
public class CPOTool extends POTool {

	private static final String CPO = "CPO";
	// Id of this tool
	private static String SC_ID = "org.rodinp.core.tests.testCPO";
	
	public void clean(IFile file, IProgressMonitor monitor) throws CoreException {
		clean(file, monitor, CPO);
	}

	public void extract(IFile file, IGraph graph, IProgressMonitor monitor) throws CoreException {
		extract(file, graph, CPO, SC_ID, monitor);
	}

	public boolean run(IFile file, IProgressMonitor monitor) throws CoreException {
		run(file, monitor, CPO);
		return true;
	}
	
	public void remove(IFile file, IFile origin, IProgressMonitor monitor) throws CoreException {
		remove(file, origin, monitor, CPO);
	}

}

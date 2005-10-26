/*******************************************************************************
 * Copyright (c) 2005 ETH Zurich.
 * Strongly inspired by org.eclipse.jdt.internal.core.BatchOperation.java which is
 * 
 * Copyright (c) 2000, 2004 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.rodinp.internal.core;

import org.eclipse.core.resources.IResourceStatus;
import org.eclipse.core.resources.IWorkspaceRunnable;
import org.eclipse.core.runtime.CoreException;
import org.rodinp.core.IRodinDBStatus;
import org.rodinp.core.RodinDBException;

/**
 * An operation created as a result of a call to RodinCore.run(IWorkspaceRunnable, IProgressMonitor)
 * that encapsulates a user defined IWorkspaceRunnable.
 */
public class BatchOperation extends RodinDBOperation {

	protected IWorkspaceRunnable runnable;

	public BatchOperation(IWorkspaceRunnable runnable) {
		this.runnable = runnable;
	}

	@Override
	protected void executeOperation() throws RodinDBException {
		try {
			this.runnable.run(this.progressMonitor);
		} catch (RodinDBException re) {
			throw re;
		} catch (CoreException ce) {
			if (ce.getStatus().getCode() == IResourceStatus.OPERATION_FAILED) {
				Throwable e = ce.getStatus().getException();
				if (e instanceof RodinDBException) {
					throw (RodinDBException) e;
				}
			}
			throw new RodinDBException(ce);
		}
	}
	
	@Override
	protected IRodinDBStatus verify() {
		// cannot verify user defined operation
		return RodinDBStatus.VERIFIED_OK;
	}
	
}

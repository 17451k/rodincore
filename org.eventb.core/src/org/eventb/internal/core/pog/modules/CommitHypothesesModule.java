/*******************************************************************************
 * Copyright (c) 2006 ETH Zurich.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.eventb.internal.core.pog.modules;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eventb.core.pog.ProcessorModule;
import org.eventb.core.pog.state.IHypothesisManager;
import org.eventb.core.pog.state.IStateRepository;
import org.rodinp.core.IRodinElement;

/**
 * @author Stefan Hallerstede
 *
 */
public abstract class CommitHypothesesModule extends ProcessorModule {

	IHypothesisManager hypothesisManager;
	
	/* (non-Javadoc)
	 * @see org.eventb.core.pog.IModule#process(org.rodinp.core.IRodinElement, org.eventb.core.IPOFile, org.eventb.core.state.IStateRepository, org.eclipse.core.runtime.IProgressMonitor)
	 */
	public void process(IRodinElement element, IStateRepository repository,
			IProgressMonitor monitor)
			throws CoreException {
		
		hypothesisManager.makeImmutable();

	}

	@Override
	public void initModule(
			IRodinElement element, 
			IStateRepository repository, 
			IProgressMonitor monitor) throws CoreException {
		super.initModule(element, repository, monitor);
		hypothesisManager = getHypothesisManager(repository);
	}
	
	protected abstract IHypothesisManager getHypothesisManager(
			IStateRepository repository) throws CoreException;

	@Override
	public void endModule(
			IRodinElement element, 
			IStateRepository repository, 
			IProgressMonitor monitor) throws CoreException {
		hypothesisManager = null;
		super.endModule(element, repository, monitor);
	}
	
}

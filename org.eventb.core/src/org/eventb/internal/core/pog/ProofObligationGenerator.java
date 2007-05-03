/*******************************************************************************
 * Copyright (c) 2006 ETH Zurich.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.eventb.internal.core.pog;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eventb.core.EventBPlugin;
import org.eventb.core.IPOFile;
import org.eventb.core.pog.IPOGProcessorModule;
import org.eventb.core.pog.state.IPOGStateRepository;
import org.eventb.internal.core.tool.IModuleFactory;
import org.rodinp.core.IRodinFile;
import org.rodinp.core.builder.IAutomaticTool;
import org.rodinp.core.builder.IExtractor;

/**
 * @author Stefan Hallerstede
 *
 */
public abstract class ProofObligationGenerator implements IAutomaticTool, IExtractor {

	public static String PRD_NAME_PREFIX = "PRD";
	
	public static boolean DEBUG = false;
	public static boolean DEBUG_STATE = false;
	public static boolean DEBUG_MODULECONF = false;
	
	protected static final String DEFAULT_CONFIG = EventBPlugin.PLUGIN_ID + ".fwd";
	
	protected final IPOGStateRepository createRepository(
			IPOFile target, 
			IProgressMonitor monitor) throws CoreException {
		
		final POGStateRepository repository = new POGStateRepository(target);
		
		if (DEBUG_STATE)
			repository.debug();
		
		return repository;
	}
	
	/* (non-Javadoc)
	 * @see org.rodinp.core.builder.IAutomaticTool#clean(org.eclipse.core.resources.IFile, org.eclipse.core.runtime.IProgressMonitor)
	 */
	public void clean(IFile source, IFile file, IProgressMonitor monitor)
			throws CoreException {
		
		try {
		
			monitor.beginTask(Messages.bind(Messages.build_cleaning, file.getName()), 1);
			
			if (file.exists())
				file.delete(true, monitor);
			
			monitor.worked(1);
			
		} finally {
			monitor.done();
		}

	}

	protected void runModules(
			IPOGProcessorModule rootModule,
			IRodinFile file, 
			IPOGStateRepository repository, 
			IProgressMonitor monitor) throws CoreException {
		
		rootModule.initModule(file, repository, monitor);
		rootModule.process(file, repository, monitor);
		rootModule.endModule(file, repository, monitor);
		
	}

	protected void printModuleTree(IRodinFile file, IModuleFactory moduleFactory) {
		if (DEBUG_MODULECONF) {
			System.out.println("+++ PROOF OBLIGATION GENERATOR MODULES +++");
			System.out.println("INPUT " + file.getPath());
			System.out.println("      " + file.getElementType());
			System.out.println("CONFIG " + DEFAULT_CONFIG);
			System.out.print(moduleFactory
					.printModuleTree(file.getElementType()));
			System.out.println("++++++++++++++++++++++++++++++++++++++");
		}
	}

}

/*******************************************************************************
 * Copyright (c) 2005 ETH Zurich.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.rodinp.internal.core.builder;

import java.util.HashSet;
import java.util.Map;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IResourceDelta;
import org.eclipse.core.resources.IResourceDeltaVisitor;
import org.eclipse.core.resources.IResourceVisitor;
import org.eclipse.core.resources.IncrementalProjectBuilder;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.OperationCanceledException;
import org.eclipse.core.runtime.SubProgressMonitor;
import org.rodinp.core.IFileElementType;
import org.rodinp.internal.core.ElementTypeManager;
import org.rodinp.internal.core.util.Util;

/**
 * @author Stefan Hallerstede
 *
 */
public class RodinBuilder extends IncrementalProjectBuilder {
	
	public static boolean DEBUG = false;
	
	public static boolean DEBUG_RUN = false;
	
	public static boolean DEBUG_GRAPH = false;
	
	BuildState state;
	
	ElementTypeManager elementTypeManager;
	
	private static final HashSet<String> ignoredFiles;
	static { 
		ignoredFiles = new HashSet<String>(3);
		ignoredFiles.add(".project");
	}
	
	@Override
	protected void startupOnInitialize() {
        // add builder init logic here
		
		state = null;
		
		elementTypeManager = ElementTypeManager.getInstance();
     }
	
	class RodinBuilderDeltaVisitor implements IResourceDeltaVisitor {

		private final ProgressManager manager;
		
		/*
		 * (non-Javadoc)
		 * 
		 * @see org.eclipse.core.resources.IResourceDeltaVisitor#visit(org.eclipse.core.resources.IResourceDelta)
		 */
		public boolean visit(IResourceDelta delta) throws CoreException {
			IResource resource = delta.getResource();
			switch (delta.getKind()) {
			case IResourceDelta.ADDED:
				// handle added resource
				
				if (resource instanceof IFile) {
				
					markNodeDated(resource, true, manager);
					
				}
				else if (resource instanceof IProject) {
					if (delta.getAffectedChildren().length == 0) {
						
						createGraph(manager);
						
					}
				}
				break;
			case IResourceDelta.REMOVED:
				// handle removed resource
				
				Node node = state.graph.getNode(resource.getFullPath());
				if(node == null)
					break;
				state.graph.builderRemoveNodeFromGraph(node, manager);
				break;
			case IResourceDelta.CHANGED:
				// handle changed resource
				if (resource instanceof IFile) {
					
					markNodeDated(resource, true, manager);
					
				}
				else if (resource instanceof IProject) {
					if (delta.getAffectedChildren().length == 0) {
					
						createGraph(manager);
						
					}
				}
				break;
			}
			//return true to continue visiting children.
			return true;
		}
		
		public RodinBuilderDeltaVisitor(ProgressManager manager) {
			this.manager = manager;
		}
	}
	
	Node createNode(IResource resource) {
		assert resource instanceof IFile;
		IFile file = (IFile) resource;
		IFileElementType<?> elementType = 
			elementTypeManager.getFileElementType(file);
		if(elementType == null)
			return null;
		Node node = state.graph.getNode(resource.getFullPath());
		
		if(node == null) {
			node = state.graph.builderAddNodeToGraph(resource.getFullPath());
		}
		return node;
	}

	class RodinBuilderResourceVisitor implements IResourceVisitor {
		
		final ProgressManager manager;
		
		public boolean visit(IResource resource) {
			if (resource instanceof IFile) {
				markNodeDated(resource, false, manager);
			}
			//return true to continue visiting children.
			return true;
		}

		public RodinBuilderResourceVisitor(ProgressManager manager) {
			this.manager = manager;
		}
	}

	@Override
	@SuppressWarnings("unchecked")
	protected IProject[] build(int kind, Map args, IProgressMonitor monitor)
			throws CoreException {
		
		if (monitor == null)
			monitor = new NullProgressMonitor();
		
		ProgressManager progressManager = 
			new ProgressManager(monitor, this);
	
		try {
		
			if (DEBUG) {
				String kindImage = 
					kind == FULL_BUILD ? "full" :
						kind == AUTO_BUILD ? "auto" :
							kind == INCREMENTAL_BUILD ? "incremental" :
								"unknown";
				System.out.println("##############################################");
				System.out.println("BUILDER: Starting " + kindImage + " build.");
			}
		
			if (state == null)
				state = 
					BuildState.getBuildState(
						getProject(), 
						progressManager.getZeroProgressMonitor());
			
			if (kind == FULL_BUILD) {
				fullBuild(progressManager);
			} else {
				IResourceDelta delta = getDelta(getProject());
				if (delta == null) {
					fullBuild(progressManager);
				} else {
					incrementalBuild(delta, progressManager);
				}
			}
		} finally {
			progressManager.done(); 
			if (DEBUG) {
				System.out.println("BUILDER: Finished build.");
				System.out.println("##############################################");
			}
		}
		return null;
	}
	
	IProgressMonitor makeProgressMonitor(IProgressMonitor monitor) {
		return new SubProgressMonitor(makeBuilderProgressMonitor(monitor), 1);
	}

	IProgressMonitor makeBuilderProgressMonitor(IProgressMonitor monitor) {
		return new BuilderProgressMonitor(monitor, this);
	}

	/**
	 * Hook allowing to reset some static state after a complete build iteration.
	 * This hook is invoked during POST_AUTO_BUILD notification
	 */
	public static void buildFinished() {
		// build has finished
    }
    
	private void buildGraph(ProgressManager manager) throws CoreException {
		state.graph.builderBuildGraph(manager);
	}
	
	/**
	 * Hook allowing to initialize some static state before a complete build iteration.
	 * This hook is invoked during PRE_AUTO_BUILD notification
	 */
	public static void buildStarting() {
		// build is about to start
	}

	private void cleanGraph(ProgressManager manager, boolean onlyClean) throws CoreException {
		state.graph.builderCleanGraph(getProject(), onlyClean, manager);
		state.graph = new Graph();
	}
	
	void markNodeDated(IResource resource, boolean userOp, ProgressManager manager) {
		assert resource instanceof IFile;
		if (ignoredFiles.contains(resource.getName()))
			return;
		Node node = state.graph.getNode(resource.getFullPath());
		if(node == null) {
			if(DEBUG)
				System.out.println(getClass().getName() + ": Node not in dependency graph " + resource.getName()); //$NON-NLS-1$
			node = createNode(resource);
		}
		if(node != null) {
			if (userOp) {
				state.graph.builderSetPreferredNode(node);
				node.setToolId(null);
				node.setPhantom(false);
			}
			try {
				state.graph.builderExtractNode(node, manager);
			} catch (CoreException e) {
				Util.log(e, "during extraction after change");
			}
		} else if(DEBUG)
			System.out.println(getClass().getName() + ": Cannot create node " + resource.getName()); //$NON-NLS-1$
	}

	protected void fullBuild(final ProgressManager manager) throws CoreException {
		createGraph(manager);
		
		manager.makeSlices(state.graph);
		
		try {
			buildGraph(manager);
		} catch (OperationCanceledException e) {
			if(isInterrupted())
				return;
			else throw e;
		} catch (CoreException e) {
			Util.log(e, "during builder full build");
		}
	}

	protected void createGraph(final ProgressManager manager) {
		try {
			cleanGraph(manager, false);
			getProject().accept(new RodinBuilderResourceVisitor(manager));
			state.graph.builderMarkDerivedNodesDated();
		} catch (OperationCanceledException e) {
			if(isInterrupted())
				return;
			else throw e;
		} catch (CoreException e) {
			Util.log(e, "during builder graph creation");
		}
	}
	
	@Override
	protected void clean(IProgressMonitor monitor) {
		
		if (monitor == null)
			monitor = new NullProgressMonitor();
		
		ProgressManager progressManager = 
			new ProgressManager(monitor, this);
	
		if (state == null)
			state = 
				BuildState.getBuildState(
					getProject(), 
					progressManager.getZeroProgressMonitor());
		
		progressManager.makeSlices(state.graph);
		
      try {
			if (DEBUG) {
				System.out.println("BUILDER: Starting cleaning");
			}
        	cleanGraph(progressManager, true);
        	MarkerHelper.deleteAllProblemMarkers(getProject());
        } catch (CoreException e) {
			Util.log(e, "during builder clean");
		} catch (OperationCanceledException e) {
			if (isInterrupted())
				return;
			throw e;
		} finally {
			progressManager.done();
			if (DEBUG) {
				System.out.println("BUILDER: Finished cleaning");
			}
		}
     }

	protected void incrementalBuild(IResourceDelta delta, ProgressManager manager) throws CoreException {

		// the visitor does the work.
		delta.accept(new RodinBuilderDeltaVisitor(manager));
		
		manager.makeSlices(state.graph);
		
		try {
			buildGraph(manager);
		} catch (OperationCanceledException e) {
			if(isInterrupted())
				return;
			else throw e;
		} catch (CoreException e) {
			Util.log(e, "during builder incremental build");
		}
	}

}

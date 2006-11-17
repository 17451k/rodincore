/*******************************************************************************
 * Copyright (c) 2005-2006 ETH Zurich.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.eventb.internal.core.pom;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.OperationCanceledException;
import org.eclipse.core.runtime.SubProgressMonitor;
import org.eventb.core.EventBPlugin;
import org.eventb.core.IPOFile;
import org.eventb.core.IPOSequent;
import org.eventb.core.IPRFile;
import org.eventb.core.IPRProof;
import org.eventb.core.IPSFile;
import org.eventb.core.IPSStatus;
import org.eventb.core.ast.FormulaFactory;
import org.eventb.core.seqprover.IConfidence;
import org.eventb.core.seqprover.IProofDependencies;
import org.eventb.core.seqprover.IProverSequent;
import org.eventb.core.seqprover.ProverLib;
import org.eventb.internal.core.Util;
import org.rodinp.core.RodinCore;
import org.rodinp.core.RodinDBException;
import org.rodinp.core.builder.IAutomaticTool;
import org.rodinp.core.builder.IExtractor;
import org.rodinp.core.builder.IGraph;

/**
 * @author Farhad Mehta
 *
 */
public class AutoPOM implements IAutomaticTool, IExtractor {

	public static boolean DEBUG = false;

	public void remove(IFile file, IFile origin, IProgressMonitor monitor) throws CoreException {
		try {
			
			monitor.beginTask("Removing " + file.getName(), 1);
			
			String s = EventBPlugin.getComponentName(file.getName());
			String t = EventBPlugin.getComponentName(origin.getName());
			if (s.equals(t)) {
				RodinCore.valueOf(file).delete(true, monitor);
			}			
		} finally {
			monitor.done();
		}
	}

	public boolean run(IFile file, IProgressMonitor monitor) throws CoreException {
		
		IPSFile psFile = (IPSFile) RodinCore.valueOf(file).getMutableCopy();
		IPRFile prFile = (IPRFile) psFile.getPRFile().getMutableCopy();
		IPOFile poFile = (IPOFile) psFile.getPOFile().getSnapshot();
		
		final String componentName = 
			EventBPlugin.getComponentName(prFile.getElementName());
		
		final IPOSequent[] pos = poFile.getSequents(monitor);
		final int noOfPOs = pos.length;
		final int workUnits = 3 + 2 + noOfPOs * 2;
		// 3 : creating fresh PR file
		// 1x : updating eash proof status
		// 2 : saving PR file
		// 1x : autoproving each proof obligation
		
		try {
			monitor.beginTask("Proving " + componentName, workUnits);
			
			// remove old proof status
			monitor.subTask("cleaning up");
			makeFresh(prFile,psFile,null);
			monitor.worked(3);
			checkCancellation(monitor, prFile, psFile);
			
			// create new proof status
			for (int i = 0; i < pos.length; i++) {
				
				final String name = pos[i].getElementName();
				monitor.subTask("updating status for " + name);
				
				final IPRProof prProof = prFile.getProof(name);
				
				if (! prProof.exists()) {
					prProof.create(null, null);
					prProof.initialize(null);
				}
				
				
				final IPSStatus oldStatus = ((IPSFile) psFile.getSnapshot()).getStatus(name);
				IPSStatus status = psFile.getStatus(name);
				// TODO : for some reason, oldStatus never exists
				if (oldStatus.exists())
				{
					oldStatus.copy(psFile, null, null, true, null);
				}
				else
				{
					status.create(null, monitor);					
				}
				updateStatus(status,monitor);
				
				monitor.worked(1);
				checkCancellation(monitor, prFile, psFile);
			}
			
			monitor.subTask("saving");
			prFile.save(new SubProgressMonitor(monitor, 1), true);
			psFile.save(new SubProgressMonitor(monitor, 1), true);
			
			checkCancellation(monitor, prFile, psFile);

			SubProgressMonitor spm = new SubProgressMonitor(monitor, noOfPOs,
					SubProgressMonitor.PREPEND_MAIN_LABEL_TO_SUBTASK					
			);
			AutoProver.run(prFile, psFile, spm);
			return true;
		} finally {
			monitor.done();
		}
	}

	
	private void checkCancellation(IProgressMonitor monitor, IPRFile prFile, IPSFile psFile) {
//		 TODO harmonize cleanup after cancellation
		if (monitor.isCanceled()) {
			// Cleanup PR & PS files (may have unsaved changes).
			try {
				prFile.makeConsistent(null);
				psFile.makeConsistent(null);
			} catch (RodinDBException e) {
				Util.log(e, "when reverting changes to proof and status files for"
						+ prFile.getElementName());
			}
			throw new OperationCanceledException();
		}
	}
	
	public void clean(IFile file, IProgressMonitor monitor) throws CoreException {
		
		IPSFile psFile = (IPSFile) RodinCore.valueOf(file);
		psFile.delete(true, null);
		
//		IPRFile prFile = psFile.getPRFile();
//		prFile.delete(true, null);
		
//		TODO : something for the PR file maybe
//		IPSFile psFile = (IPSFile) RodinCore.valueOf(file).getMutableCopy();
//			
//		try {
//		
//			final IRodinElement[] children = psFile.getChildren();
//			
//			monitor.beginTask("Cleaning " + file.getName(), children.length + 5);
//			
//			for (IRodinElement child : children){
//				// do not delete interactive proofs !
//				if ((! child.getElementType().equals(IPRProofTree.ELEMENT_TYPE)) ||
//						((PRProofTree)child).isAutomaticallyGenerated())
//					((InternalElement)child).delete(true,null);
//				monitor.worked(1);
//			}
//			
//			psFile.save(new SubProgressMonitor(monitor,5),true);
//			
//		} finally {
//			monitor.done();
//		}
		
	}

	public void extract(IFile file, IGraph graph, IProgressMonitor monitor) throws CoreException {	
		
		try {
			
			monitor.beginTask("Extracting " + file.getName(), 1);
		
			IPOFile source = (IPOFile) RodinCore.valueOf(file);
			// IPRFile target = source.getPRFile();
			IPSFile target = source.getPSFile();
		
			graph.openGraph();
			graph.addNode(target.getResource(), POMCore.AUTO_POM_TOOL_ID);
			graph.putToolDependency(
					source.getResource(), 
					target.getResource(), POMCore.AUTO_POM_TOOL_ID, true);
			graph.closeGraph();
			
		} finally {
			monitor.done();
		}
	}

	private void makeFresh(IPRFile prFile, IPSFile psFile,
			IProgressMonitor monitor) throws RodinDBException {
		
		// IRodinProject project = prFile.getRodinProject();

		// Create a new PR file if none exists
		if (! prFile.exists())
			prFile.create(true, monitor);
			//project.createRodinFile(prFile.getElementName(), true, monitor);
		
		// Create a fresh PS file
		// TODO : modify once signatures are implemented
		if (! psFile.exists()) 
			psFile.create(true, monitor);
//			project.createRodinFile(psFile.getElementName(), true, monitor);
		else
		{
			IPSStatus[] statuses = psFile.getStatuses();
			for (IPSStatus status : statuses) {
				status.delete(true, monitor);
			}
			// psFile.getRodinDB().delete(psFile.getChildren(),true,null);
		}
	}
	
	
//	 lock po & pr files before calling this method
// TODO : a version with Proof tree from seqProver
	public static void updateStatus(IPSStatus status, IProgressMonitor monitor) throws RodinDBException {
		IProverSequent seq =  POLoader.readPO(status.getPOSequent());
		final IPRProof prProof = status.getProof();
		if (!prProof.exists()) {
			status.setProofConfidence(IConfidence.UNATTEMPTED, null);
			status.setProofValidAttribute(true, null);
			return;
		}
		IProofDependencies deps = prProof.getProofDependencies(FormulaFactory.getDefault(), null);
		boolean valid = ProverLib.proofReusable(deps,seq);
		status.setProofConfidence(prProof.getConfidence(null), null);
		status.setProofValidAttribute(valid, null);
	}
	
}

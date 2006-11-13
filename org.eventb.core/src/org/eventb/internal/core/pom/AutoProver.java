/*******************************************************************************
 * Copyright (c) 2005-2006 ETH Zurich.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.eventb.internal.core.pom;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.OperationCanceledException;
import org.eclipse.core.runtime.SubProgressMonitor;
import org.eventb.core.EventBAttributes;
import org.eventb.core.IPOSequent;
import org.eventb.core.IPRFile;
import org.eventb.core.IPRProofTree;
import org.eventb.core.IPSFile;
import org.eventb.core.IPSstatus;
import org.eventb.core.seqprover.IConfidence;
import org.eventb.core.seqprover.IProofTree;
import org.eventb.core.seqprover.ITactic;
import org.eventb.core.seqprover.ProverFactory;
import org.eventb.core.seqprover.eventbExtensions.Tactics;
import org.eventb.core.seqprover.tactics.BasicTactics;
import org.eventb.internal.core.ProofMonitor;
import org.rodinp.core.RodinDBException;

import com.b4free.rodin.core.B4freeCore;

public class AutoProver {
	
	private static boolean enabled = true;
	
	// Default delay for automatic proofs: 2 seconds
	private static long timeOutDelay = 2 * 1000;
	
	public static void disable() {
		enabled = false;
	}
	
	public static void enable() {
		enabled = true;
	}

	public static boolean isEnabled() {
		return enabled;
	}

	public static void setTimeOutDelay(long value) {
		timeOutDelay = value;
	}

	private AutoProver() {
		// Nothing to do.
	}
	
	protected static void run(IPRFile prFile, IPSFile psFile, IProgressMonitor monitor) throws CoreException {
		if (! enabled)
			return;
		final IPSstatus[] pos = psFile.getStatus();
		boolean dirty = false;
		try {
			monitor.beginTask("auto-proving", pos.length);
			for (IPSstatus status : pos) {
				if (monitor.isCanceled()) {
					prFile.makeConsistent(null);
					psFile.makeConsistent(null);
					throw new OperationCanceledException();
				}
				IProgressMonitor subMonitor = new SubProgressMonitor(
						monitor, 
						1, 
						SubProgressMonitor.PREPEND_MAIN_LABEL_TO_SUBTASK
				);
				dirty |= processPo(prFile, status, subMonitor);
			}
			// monitor.worked(1);
			dirty = true;
			if (dirty) prFile.save(null, false);
			if (dirty) psFile.save(null, false);
		} finally {
			monitor.done();
		}
	}

	private static boolean processPo(IPRFile prFile, IPSstatus status,
			IProgressMonitor pm) throws RodinDBException {
		
		try {
			pm.beginTask(status.getName() + ":", 3);
			
			pm.subTask("loading");
			IPRProofTree prProofTree = status.getProofTree();
//			if (proofTree == null)
//				proofTree = prFile.createProofTree(status.getName());
			pm.worked(1);
			
			if ((!status.isProofValid()) || 
					(status.getProofConfidence(null) <= IConfidence.PENDING)) {
				final IPOSequent poSequent = status.getPOSequent();
				IProofTree autoProofTree = ProverFactory.makeProofTree(
						POLoader.readPO(poSequent),
						poSequent
				);

				pm.subTask("proving");
				autoTactic().apply(autoProofTree.getRoot(), new ProofMonitor(pm));
				pm.worked(1);
				
				pm.subTask("saving");
				// Update the tree if it was discharged
				if (autoProofTree.isClosed()) {
					prProofTree.setProofTree(autoProofTree, null);
					status.updateStatus();
					setAutoProven(true,status);
					prFile.save(null, false);
					return true;
				}
				// If the auto prover made 'some' progress, and no
				// proof was previously attempted update the proof
				if (autoProofTree.getRoot().hasChildren() && 
						(
								// ( status.getProofConfidence() > IConfidence.UNATTEMPTED) || 
								(status.isAutoProven() && !(status.getProofConfidence(null) > IConfidence.PENDING))
						))	
					
				{
					prProofTree.setProofTree(autoProofTree, null);
					status.updateStatus();
					setAutoProven(true,status);
					// in this case no need to save immediately.
					return true;
				}
			}
			setAutoProven(false,status);
			return false;
		} finally {
			pm.done();
		}
	}
	
	public static ITactic autoTactic(){
		final int MLforces = 
			B4freeCore.ML_FORCE_0 |
			B4freeCore.ML_FORCE_1;
		return BasicTactics.compose(
				Tactics.lasoo(),
				BasicTactics.onAllPending(Tactics.norm()),
				BasicTactics.onAllPending(
						B4freeCore.externalML(MLforces, timeOutDelay)), // ML
				BasicTactics.onAllPending(
						B4freeCore.externalPP(true, timeOutDelay)), // P1
				BasicTactics.onAllPending(
						B4freeCore.externalPP(false, timeOutDelay)) // PP
				);
	}
	
	private static void setAutoProven(boolean autoProven, IPSstatus status) throws RodinDBException {
		status.setBooleanAttribute(EventBAttributes.AUTO_PROOF_ATTRIBUTE, autoProven, null);
	}


}

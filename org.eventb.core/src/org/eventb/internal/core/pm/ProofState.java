/*******************************************************************************
 * Copyright (c) 2005-2006 ETH Zurich.
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Rodin @ ETH Zurich
 ******************************************************************************/

package org.eventb.internal.core.pm;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eventb.core.EventBPlugin;
import org.eventb.core.IPRProof;
import org.eventb.core.IPSStatus;
import org.eventb.core.IPSWrapper;
import org.eventb.core.ast.Predicate;
import org.eventb.core.pm.IProofState;
import org.eventb.core.pm.IUserSupportInformation;
import org.eventb.core.pm.IUserSupportManager;
import org.eventb.core.seqprover.IConfidence;
import org.eventb.core.seqprover.IProofMonitor;
import org.eventb.core.seqprover.IProofSkeleton;
import org.eventb.core.seqprover.IProofTree;
import org.eventb.core.seqprover.IProofTreeDelta;
import org.eventb.core.seqprover.IProofTreeNode;
import org.eventb.core.seqprover.IProverSequent;
import org.eventb.core.seqprover.ITactic;
import org.eventb.core.seqprover.ProverLib;
import org.eventb.core.seqprover.eventbExtensions.Tactics;
import org.eventb.core.seqprover.tactics.BasicTactics;
import org.eventb.internal.core.ProofMonitor;
import org.eventb.internal.core.pom.POLoader;
import org.rodinp.core.RodinDBException;

/**
 * @author htson
 *         <p>
 *         This class keep the proof state for one proof obligation including
 *         the proof tree, the current proof node, the set of cached and
 *         searched hypotheses.
 */
public class ProofState implements IProofState {

	// The PR sequent associated with this proof obligation.
	IPSStatus status;

	// The current proof tree, this might be different from the proof tree in
	// the disk, can be null when it is not initialised.
	IProofTree pt;

	// The current proof node, can be null when the proof tree is uninitialised.
	IProofTreeNode current;

	// The set of cached hypotheses.
	private Collection<Predicate> cached;

	// The set of searched hypotheses.
	private Collection<Predicate> searched;

	// The dirty flag to indicate if there are some unsaved changes with this
	// proof obligation.
	private boolean dirty;

	DeltaProcessor deltaProcessor;

	UserSupport userSupport;

	public ProofState(UserSupport userSupport, IPSStatus ps) {
		this.userSupport = userSupport;
		this.status = ps;
		cached = new ArrayList<Predicate>();
		searched = new ArrayList<Predicate>();
		deltaProcessor = ((UserSupportManager) UserSupportManager.getDefault())
				.getDeltaProcessor();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eventb.core.pm.IProofState#loadProofTree(org.eclipse.core.runtime.IProgressMonitor)
	 */
	public void loadProofTree(final IProgressMonitor monitor)
			throws RodinDBException {
		EventBPlugin.getDefault().getUserSupportManager().run(new Runnable() {

			public void run() {
				if (pt != null)
					pt.removeChangeListener(ProofState.this);

				IPSWrapper psWrapper = userSupport.getPSWrapper();

				// Construct the proof tree from the PO file.
				try {
					pt = psWrapper.getFreshProofTree(status);
				} catch (RodinDBException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
					return;
				}

				// Get the proof skeleton and rebuild the tree
				IProofSkeleton proofSkeleton;
				try {
					proofSkeleton = psWrapper.getProofSkeleton(status, monitor);
					if (proofSkeleton != null) {
						// ProofBuilder.rebuild(pt.getRoot(), proofSkeleton);
						BasicTactics.rebuildTac(proofSkeleton).apply(
								pt.getRoot(),
								new ProofMonitor(monitor));
					}
				} catch (RodinDBException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				pt.addChangeListener(ProofState.this);
				ProofState.this.newProofTree();
				ProofState.this.setDirty(false);
				
				if (!pt.getRoot().isClosed()) {
					// Run Post tactic at the root of the tree
					IUserSupportManager usManager = EventBPlugin.getDefault()
							.getUserSupportManager();
					ITactic postTactic = usManager.getProvingMode()
							.getPostTactic();
					postTactic.apply(pt.getRoot(), new ProofMonitor(monitor));
				}
				// Current node is the next pending subgoal or the root of the
				// proof tree if there are no pending subgoal.
				IProofTreeNode node = getNextPendingSubgoal();

				if (node == null) {
					node = pt.getRoot();
				}
				try {
					setCurrentNode(node);
				} catch (RodinDBException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

				// if the proof tree was previously broken then the rebuild
				// would fix the proof, marking it dirty.
				try {
					if (status.isBroken())
						ProofState.this.setDirty(true);
				} catch (RodinDBException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

				ProofState.this.setCached(new HashSet<Predicate>());
				ProofState.this.setSearched(new HashSet<Predicate>());
				deltaProcessor.informationChanged(userSupport,
						new UserSupportInformation("Proof Tree is reloaded",
								IUserSupportInformation.MAX_PRIORITY));
			}

		});
	}

	protected void newProofTree() {
		deltaProcessor.newProofTree(userSupport, this);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eventb.core.pm.IProofState#isClosed()
	 */
	public boolean isClosed() throws RodinDBException {
		if (pt != null)
			return pt.isClosed();

		return isSequentDischarged();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eventb.core.pm.IProofState#getPRSequent()
	 */
	public IPSStatus getPSStatus() {
		return status;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eventb.core.pm.IProofState#getProofTree()
	 */
	public IProofTree getProofTree() {
		return pt;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eventb.core.pm.IProofState#getCurrentNode()
	 */
	public IProofTreeNode getCurrentNode() {
		return current;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eventb.core.pm.IProofState#setCurrentNode(org.eventb.core.seqprover.IProofTreeNode)
	 */
	public void setCurrentNode(final IProofTreeNode newNode)
			throws RodinDBException {
		UserSupportManager.getDefault().run(new Runnable() {
			public void run() {
				if (current != newNode) {
					current = newNode;
					// Fire delta
					deltaProcessor.setNewCurrentNode(userSupport,
							ProofState.this);
					deltaProcessor.informationChanged(userSupport,
							new UserSupportInformation(
									"Select a new proof node",
									IUserSupportInformation.MIN_PRIORITY));
				} else {
					deltaProcessor.informationChanged(userSupport,
							new UserSupportInformation("Not a new proof node",
									IUserSupportInformation.MIN_PRIORITY));
				}
			}
		});

		return;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eventb.core.pm.IProofState#getNextPendingSubgoal(org.eventb.core.seqprover.IProofTreeNode)
	 */
	public IProofTreeNode getNextPendingSubgoal(IProofTreeNode node) {
		if (node.getProofTree() != pt) {
			// Node has been detached from this proof tree
			return pt.getRoot().getFirstOpenDescendant();
		}
		final IProofTreeNode next = node.getNextOpenNode();
		if (next != null) {
			return next;
		}
		return pt.getRoot().getFirstOpenDescendant();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eventb.core.pm.IProofState#getNextPendingSubgoal()
	 */
	IProofTreeNode getNextPendingSubgoal() {
		return pt.getRoot().getFirstOpenDescendant();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eventb.core.pm.IProofState#addAllToCached(java.util.Collection)
	 */
	public void addAllToCached(Collection<Predicate> hyps) {
		cached.addAll(hyps);
		deltaProcessor.cacheChanged(userSupport, this);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eventb.core.pm.IProofState#setSearched(java.util.Collection)
	 */
	protected void setCached(Collection<Predicate> cached) {
		this.cached = cached;
		deltaProcessor.cacheChanged(userSupport, this);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eventb.core.pm.IProofState#removeAllFromCached(java.util.Collection)
	 */
	public void removeAllFromCached(Collection<Predicate> hyps) {
		cached.removeAll(hyps);
		deltaProcessor.cacheChanged(userSupport, this);
		deltaProcessor.informationChanged(userSupport,
				new UserSupportInformation(
						"Removed hypotheses from cache",
						IUserSupportInformation.MAX_PRIORITY));
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eventb.core.pm.IProofState#getCached()
	 */
	public Collection<Predicate> getCached() {
		return cached;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eventb.core.pm.IProofState#removeAllFromSearched(java.util.Collection)
	 */
	public void removeAllFromSearched(Collection<Predicate> hyps) {
		searched.removeAll(hyps);
		deltaProcessor.searchChanged(userSupport, this);
		deltaProcessor.informationChanged(userSupport,
				new UserSupportInformation(
						"Removed hypotheses from search",
						IUserSupportInformation.MAX_PRIORITY));
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eventb.core.pm.IProofState#getSearched()
	 */
	public Collection<Predicate> getSearched() {
		return searched;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eventb.core.pm.IProofState#setSearched(java.util.Collection)
	 */
	public void setSearched(Collection<Predicate> searched) {
		this.searched = searched;
		deltaProcessor.searchChanged(userSupport, this);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eventb.core.pm.IProofState#isDirty()
	 */
	public boolean isDirty() {
		return dirty;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eventb.core.pm.IProofState#doSave(org.eclipse.core.runtime.IProgressMonitor)
	 */
	public void setProofTree(IProgressMonitor monitor) throws CoreException {
		if (UserSupportUtils.DEBUG)
			UserSupportUtils.debug("Saving: " + status.getElementName());

		userSupport.getPSWrapper().setProofTree(status, pt, monitor);
		setDirty(false);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eventb.core.pm.IProofState#setDirty(boolean)
	 */
	public void setDirty(boolean dirty) {
		if (this.dirty != dirty)
			this.dirty = dirty;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eventb.core.pm.IProofState#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj) {
		if (!(obj instanceof ProofState))
			return false;
		else {
			IProofState proofState = (IProofState) obj;
			return proofState.getPSStatus().equals(status);
		}

	}

	// Pre: Must be initalised and not currently saving.
	/*
	 * (non-Javadoc)
	 * 
	 * 
	 * @see org.eventb.core.pm.IProofState#proofReuse(org.eventb.core.seqprover.IProofMonitor)
	 * 
	 * Not currently used : Used before for supporting copy&paste.
	 * This is now supported using proofSkeletons & rebuildTac()
	 * TODO : See if this method is really needed, and if so, remove the call to
	 * the deprecated method.
	 */
	public void proofReuse(IProofMonitor monitor) throws RodinDBException {
		IProofTree newTree = userSupport.getPSWrapper().getFreshProofTree(
				status);
		
		IProofSkeleton proofSkeleton = pt.getRoot().copyProofSkeleton();
		ITactic reuseTac = BasicTactics.reuseTac(proofSkeleton);
		reuseTac.apply(newTree.getRoot(), monitor);
		pt.removeChangeListener(this);
		
		pt = newTree;
		newTree.addChangeListener(this);
		IProofTreeNode newNode = getNextPendingSubgoal();
		if (newNode == null) {
			newNode = pt.getRoot();
		}
		setCurrentNode(newNode);
		deltaProcessor.newProofTree(userSupport, this);
	}

	/* (non-Javadoc)
	 * @see org.eventb.core.pm.IProofState#proofRebuilt(org.eventb.internal.core.ProofMonitor)
	 */
	public void proofRebuilt(ProofMonitor monitor) throws RodinDBException {
		IProofTree newTree = userSupport.getPSWrapper().getFreshProofTree(
				status);
		
		IProofSkeleton proofSkeleton = pt.getRoot().copyProofSkeleton();
		ITactic rebuiltTac = BasicTactics.rebuildTac(proofSkeleton);
		rebuiltTac.apply(newTree.getRoot(), monitor);
		
		pt.removeChangeListener(this);

		pt = newTree;
		newTree.addChangeListener(this);
		current = getNextPendingSubgoal();
		if (current == null) {
			current = pt.getRoot();
		}
		deltaProcessor.newProofTree(userSupport, this);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eventb.core.pm.IProofState#isUninitialised()
	 */
	public boolean isUninitialised() {
		return (pt == null);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eventb.core.pm.IProofState#isSequentDischarged()
	 */
	public boolean isSequentDischarged() throws RodinDBException {
		final IPRProof prProof = status.getProof();
		return (!status.isBroken() && prProof.exists() && (prProof
				.getConfidence() > IConfidence.PENDING));
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eventb.core.pm.IProofState#isProofReusable()
	 */
	public boolean isProofReusable() throws RodinDBException {
		IProverSequent seq = POLoader.readPO(status.getPOSequent());
		return ProverLib.proofReusable(pt.getProofDependencies(), seq);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eventb.core.pm.IProofState#reloadProofTree()
	 */
	@Deprecated
	public void reloadProofTree() throws RodinDBException {

		// Construct the proof tree from the file.
		if (pt != null)
			pt.removeChangeListener(this);
		pt = userSupport.getPSWrapper().getFreshProofTree(status);
		pt.addChangeListener(this);

		// Current node is the next pending subgoal or the root of the proof
		// tree if there are no pending subgoal.
		current = getNextPendingSubgoal();
		if (current == null) {
			current = pt.getRoot();
		}

		// if the proof tree was previously broken then the rebuild would
		// fix the proof, making it dirty.
		dirty = status.isBroken();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eventb.core.pm.IProofState#unloadProofTree()
	 */
	public void unloadProofTree() {
		pt = null;
		current = null;
	}

	@Override
	public String toString() {
		StringBuffer buffer = new StringBuffer("****** Proof Status for: ");
		buffer.append(status + " ******\n");
		buffer.append("Is dirty? " + dirty + "\n");
		buffer.append("** Proof Tree **\n");
		buffer.append(pt);
		buffer.append("\n");
		buffer.append("** Cached **\n");
		for (Predicate hyp : cached) {
			buffer.append(hyp);
			buffer.append("\n");			
		}
		buffer.append("** Searched **\n");
		for (Predicate hyp : searched) {
			buffer.append(hyp);
			buffer.append("\n");			
		}
		buffer.append("Current node: ");
		buffer.append(current);
		buffer.append("\n");
		buffer.append("****************************");
		return buffer.toString();
	}

	public void applyTactic(final ITactic t, final IProofTreeNode node,
			final IProgressMonitor monitor) throws RodinDBException {
		UserSupportManager.getDefault().run(new Runnable() {

			public void run() {
				if (internalApplyTactic(t, node, new ProofMonitor(monitor))) {
					selectNextPendingSubGoal(node);					
				}
			}

		});

	}

	public void applyTacticToHypotheses(final ITactic t,
			final IProofTreeNode node, final Set<Predicate> hyps,
			final IProgressMonitor monitor) throws RodinDBException {
		UserSupportManager.getDefault().run(new Runnable() {

			public void run() {
				ProofState.this.addAllToCached(hyps);
				if (internalApplyTactic(t, node, new ProofMonitor(monitor))) {
					selectNextPendingSubGoal(node);
				}
			}

		});

	}

	protected void selectNextPendingSubGoal(IProofTreeNode node) {
		final IProofTreeNode newNode = this.getNextPendingSubgoal(node);
		if (newNode != null) {
			try {
				setCurrentNode(newNode);
			} catch (RodinDBException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	protected boolean internalApplyTactic(ITactic t, IProofTreeNode node,
			IProofMonitor pm) {
		Object info = t.apply(node, pm);
		if (info == null) {
			info = "Tactic applied successfully";
			if (!t.equals(Tactics.prune())) {
				IUserSupportManager usManager = EventBPlugin.getDefault()
						.getUserSupportManager();
				ITactic postTactic = usManager.getProvingMode().getPostTactic();
				postTactic.apply(node, pm);
			}
			deltaProcessor.informationChanged(userSupport,
					new UserSupportInformation(info,
							IUserSupportInformation.MAX_PRIORITY));
			return true;
		} else {
			deltaProcessor.informationChanged(userSupport,
					new UserSupportInformation(info,
							IUserSupportInformation.MAX_PRIORITY));
			return false;
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eventb.core.pm.IUserSupport#proofTreeChanged(org.eventb.core.seqprover.IProofTreeDelta)
	 */
	public void proofTreeChanged(IProofTreeDelta proofTreeDelta) {
		if (UserSupportUtils.DEBUG)
			UserSupportUtils.debug("UserSupport - Proof Tree Changed: "
					+ proofTreeDelta);
		deltaProcessor.proofTreeChanged(userSupport, this, proofTreeDelta);
		setDirty(true);
	}

	public void back(IProofTreeNode node, final IProgressMonitor monitor)
			throws RodinDBException {
		if (node == null)
			return;

		final IProofTreeNode parent = node.getParent();
		if (node.isOpen() && parent != null) {
			UserSupportManager.getDefault().run(new Runnable() {

				public void run() {
					try {
						applyTactic(Tactics.prune(), parent, monitor);
					} catch (RodinDBException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}

				}

			});
		}
	}

	public void setComment(final String text, final IProofTreeNode node)
			throws RodinDBException {
		node.setComment(text);
	}

}

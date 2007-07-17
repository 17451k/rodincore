package org.eventb.core.pm;

import java.util.Collection;
import java.util.Set;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eventb.core.IPSStatus;
import org.eventb.core.ast.Predicate;
import org.eventb.core.seqprover.IProofMonitor;
import org.eventb.core.seqprover.IProofTree;
import org.eventb.core.seqprover.IProofTreeChangedListener;
import org.eventb.core.seqprover.IProofTreeNode;
import org.eventb.core.seqprover.ITactic;
import org.eventb.internal.core.ProofMonitor;
import org.rodinp.core.RodinDBException;

public interface IProofState extends IProofTreeChangedListener {

	public abstract void loadProofTree(IProgressMonitor monitor)
			throws RodinDBException;

	public abstract boolean isClosed() throws RodinDBException;

	public abstract IPSStatus getPSStatus();

	public abstract IProofTree getProofTree();

	public abstract IProofTreeNode getCurrentNode();

	public abstract void setCurrentNode(IProofTreeNode newNode)
			throws RodinDBException;

	public abstract IProofTreeNode getNextPendingSubgoal(IProofTreeNode node);

	public abstract void addAllToCached(Collection<Predicate> hyps);

	public abstract void removeAllFromCached(Collection<Predicate> hyps);

	public abstract Collection<Predicate> getCached();

	public abstract void removeAllFromSearched(Collection<Predicate> hyps);

	public abstract Collection<Predicate> getSearched();

	public abstract void setSearched(Collection<Predicate> searched);

	public abstract boolean isDirty();

	public abstract void setProofTree(IProgressMonitor monitor) throws RodinDBException;

	public abstract void setDirty(boolean dirty);

	public abstract boolean equals(Object obj);

	// Must be initalised and not currently saving.
	// TODO: Check if this method is reallly needed.
	public abstract void proofReuse(IProofMonitor monitor)
			throws RodinDBException;

	public abstract void proofRebuilt(ProofMonitor monitor)
			throws RodinDBException;

	public abstract boolean isUninitialised();

	public abstract boolean isSequentDischarged() throws RodinDBException;

	public abstract boolean isProofReusable() throws RodinDBException;

	@Deprecated
	public abstract void reloadProofTree() throws RodinDBException;

	public abstract void unloadProofTree();


	/**
	 * Apply a tactic at a given proof tree node
	 * <p>
	 * 
	 * @param t
	 *            a tactic
	 * @param node
	 *            a proof tree node
	 * @param monitor
	 *            a proof monitor
	 * @throws RodinDBException
	 *             when there are some problem in applying the tactic.
	 * @deprecated use method from {@link IUserSupport} instead
	 */
	@Deprecated
	public abstract void applyTactic(ITactic t, IProofTreeNode node,
			IProgressMonitor monitor) throws RodinDBException;


	/**
	 * Apply a tactic to a set of hypotheses at a given proof tree node.
	 * <p>
	 * 
	 * @param t
	 *            a tactic
	 * @param node
	 *            a proof tree node
	 * @param hyps
	 *            a set of predicates (hypotheses)
	 * @param monitor
	 *            a proof monitor
	 * @throws RodinDBException
	 *             when there are some problem in applying the tactic.
	 * @deprecated use method from {@link IUserSupport} instead
	 */
	@Deprecated
	public abstract void applyTacticToHypotheses(ITactic t,
			IProofTreeNode node, Set<Predicate> hyps, IProgressMonitor monitor)
			throws RodinDBException;

	public abstract void back(IProofTreeNode currentNode,
			IProgressMonitor monitor) throws RodinDBException;

	public abstract void setComment(String text, IProofTreeNode node) throws RodinDBException;

}
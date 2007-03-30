package org.eventb.core.seqprover;

import org.eventb.core.seqprover.proofBuilder.ProofBuilder;
import org.eventb.internal.core.seqprover.ProofTreeNode;

/**
 * Common protocol for a proof skeleton node.
 * 
 * <p>
 * A proof skeleton node is a restricted version of a proof tree node 
 * ({@link ProofTreeNode} inherits from it) that does not contain sequents and 
 * only contains the information that is needed to build (or re-build) the proof 
 * tree for different initial sequents.
 * </p>
 *
 * <p>
 * This interface is intended to be implemented by clients that wish to persist
 * or copy proofs or subproofs.
 * </p>
 * 
 * @see ProofTreeNode
 * @see ProofBuilder
 * 
 * @author Farhad Mehta
 */
public interface IProofSkeleton {

	/**
	 * Returns the children of this node.
	 * <p>
	 * This method always returns an array, even if this node is a leaf node
	 * (that is with no rule applied to it). It never returns <code>null</code>.
	 * </p>
	 * 
	 * @return an array of the children of this node
	 */
	IProofSkeleton[] getChildNodes();

	/**
	 * Returns the rule applied to this node.
	 * 
	 * @return the rule applied to this node or <code>null</code> is this node
	 *         is a leaf node
	 */
	IProofRule getRule();

	/**
	 * Returns the comment field of this node.
	 * 
	 * @return the comment associated to this node
	 */
	String getComment();
	
}
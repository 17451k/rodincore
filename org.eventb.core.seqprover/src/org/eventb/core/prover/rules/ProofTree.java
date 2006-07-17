/*******************************************************************************
 * Copyright (c) 2006 ETH Zurich.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/

package org.eventb.core.prover.rules;

import java.util.HashSet;
import java.util.Set;

import org.eventb.core.ast.FreeIdentifier;
import org.eventb.core.ast.ITypeEnvironment;
import org.eventb.core.prover.IProofTree;
import org.eventb.core.prover.IProofTreeChangedListener;
import org.eventb.core.prover.IProofTreeNode;
import org.eventb.core.prover.sequent.Hypothesis;
import org.eventb.core.prover.sequent.IProverSequent;

/**
 * Implementation of a proof tree, with observer design pattern.
 * 
 * @author Laurent Voisin
 */
public final class ProofTree implements IProofTree {

	// Delta processor for this tree
	final DeltaProcessor deltaProcessor;
	
	// Root node
	final ProofTreeNode root;

	/**
	 * Creates a new proof tree for the given sequent.
	 * 
	 * Clients must not call this constructor, but rather the factory method in
	 * {@link org.eventb.core.prover.SequentProver}.
	 */
	public ProofTree(IProverSequent sequent) {
		root = new ProofTreeNode(this, sequent);
		deltaProcessor = new DeltaProcessor(this);
	}
	
	/**
	 * Creates a new proof tree for the given (disconnected) IProofTreeNode.
	 * 
	 * Clients must not call this constructor, but rather the factory method in
	 * {@link org.eventb.core.prover.SequentProver}.
	 */
	protected ProofTree(ProofTreeNode node) {
		node.setProofTree(this);
		root = node;
		deltaProcessor = new DeltaProcessor(this);
	}

	public void addChangeListener(IProofTreeChangedListener listener) {
		deltaProcessor.addChangeListener(listener);
	}

	public ProofTreeNode getRoot() {
		return root;
	}

	public IProverSequent getSequent() {
		return getRoot().getSequent();
	}

	public boolean isClosed() {
		return getRoot().isClosed();
	}

	public void removeChangeListener(IProofTreeChangedListener listener) {
		deltaProcessor.removeChangeListener(listener);
	}

	//	TODO : Replace with a more sophisticated implementation
	//  once Rule and ReasoningStep have been merged.
	public Set<Hypothesis> getUsedHypotheses() {
		Set<Hypothesis> usedHyps = new HashSet<Hypothesis>();
		collectNeededHypotheses(usedHyps,root);
		usedHyps.retainAll(getSequent().hypotheses());
		return usedHyps;
	}
	
	private static void collectNeededHypotheses(Set<Hypothesis> neededHyps,IProofTreeNode node){
		neededHyps.addAll(node.getNeededHypotheses());
		IProofTreeNode[] children = node.getChildren();
		for (int i = 0; i < children.length; i++) {
			neededHyps.addAll(children[i].getNeededHypotheses());
			collectNeededHypotheses(neededHyps,children[i]);
		}
	}

	public Set<FreeIdentifier> getFreeIdents() {
		Set<FreeIdentifier> freeIdents = new HashSet<FreeIdentifier>();
		collectFreeIdentifiers(freeIdents,root);
		return freeIdents;
	}

	
	private static void collectFreeIdentifiers(Set<FreeIdentifier> freeIdents, IProofTreeNode node) {
		node.addFreeIdents(freeIdents);
		IProofTreeNode[] children = node.getChildren();
		for (int i = 0; i < children.length; i++) {
			children[i].addFreeIdents(freeIdents);
			collectFreeIdentifiers(freeIdents,children[i]);
		}
		
	}
	
	// TODO : return both used and introduced idents somehow
	public Set<FreeIdentifier> getUsedFreeIdents() {
		Set<FreeIdentifier> freeIdents = getFreeIdents();
		Set<FreeIdentifier> usedIdents = new HashSet<FreeIdentifier>();
		Set<FreeIdentifier> introducedIdents = new HashSet<FreeIdentifier>();
		ITypeEnvironment typeEnv = root.getSequent().typeEnvironment();
		for (FreeIdentifier freeIdent : freeIdents) {
			// Check if the type environment contains the freeIdent
			if (typeEnv.contains(freeIdent.getName()) && 
					typeEnv.getType(freeIdent.getName()).equals(freeIdent.getType()))
				usedIdents.add(freeIdent);
			else 
			{
				introducedIdents.add(freeIdent);
			}
		}
		return usedIdents;
	}

	/* (non-Javadoc)
	 * @see org.eventb.core.prover.IProofTree#getConfidence()
	 */
	public int getConfidence() {
		return getRoot().getConfidence();
	}

	/* (non-Javadoc)
	 * @see org.eventb.core.prover.IProofTree#proofAttempted()
	 */
	public boolean proofAttempted() {
		return !(root.isOpen() && root.getComment().length() == 0);
	}

}
/*******************************************************************************
 * Copyright (c) 2006, 2014 ETH Zurich and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     ETH Zurich - initial API and implementation
 *******************************************************************************/
package org.eventb.internal.core.seqprover;

import org.eventb.core.ast.FormulaFactory;
import org.eventb.core.seqprover.IConfidence;
import org.eventb.core.seqprover.IProofDependencies;
import org.eventb.core.seqprover.IProofTree;
import org.eventb.core.seqprover.IProofTreeChangedListener;
import org.eventb.core.seqprover.IProverSequent;

/**
 * Implementation of a proof tree, with observer design pattern.
 * 
 * @author Laurent Voisin
 */
public final class ProofTree implements IProofTree {


	/**
	 * The delta processor for this tree
	 */
	final DeltaProcessor deltaProcessor;
	
	/**
	 * The origin of the root sequent
	 */
	final Object origin;

	/**
	 * The root Proof Tree Node
	 */
	final ProofTreeNode root;

	/**
	 * Creates a new proof tree for the given sequent.
	 * 
	 * Clients must not call this constructor, but rather the factory method in
	 * {@link org.eventb.core.seqprover.SequentProver}.
	 */
	public ProofTree(IProverSequent sequent, Object origin) {
		this.deltaProcessor = new DeltaProcessor(this);
		this.origin = origin;
		this.root = new ProofTreeNode(this, sequent);
	}
	
	/**
	 * Creates a new proof tree for the given (disconnected) IProofTreeNode.
	 * 
	 * Clients must not call this constructor, but rather the factory method in
	 * {@link org.eventb.core.seqprover.SequentProver}.
	 */
	protected ProofTree(ProofTreeNode node) {
		deltaProcessor = new DeltaProcessor(this);
		origin = null;
		root = node;
		node.setProofTree(this);
	}

	@Override
	public void addChangeListener(IProofTreeChangedListener listener) {
		deltaProcessor.addChangeListener(listener);
	}

	@Override
	public Object getOrigin() {
		return origin;
	}

	@Override
	public ProofTreeNode getRoot() {
		return root;
	}

	@Override
	public FormulaFactory getFormulaFactory() {
		return getRoot().getFormulaFactory();
	}

	@Override
	public IProverSequent getSequent() {
		return getRoot().getSequent();
	}

	@Override
	public boolean isClosed() {
		return getConfidence() > IConfidence.PENDING;
	}

	@Override
	public void removeChangeListener(IProofTreeChangedListener listener) {
		deltaProcessor.removeChangeListener(listener);
	}

	@Override
	public void run(Runnable op) {
		boolean wasEnable = deltaProcessor.isEnable();
		try {
			if (wasEnable)
				deltaProcessor.setEnable(false);
			op.run();
		}
		finally {
			if (wasEnable)
				deltaProcessor.setEnable(true);
		}
		deltaProcessor.fireDeltas();
	}
	
	@Override
	public int getConfidence() {
		if (getRoot().isOpen() && 
				getRoot().getComment().length() == 0)
			return IConfidence.UNATTEMPTED;
		return getRoot().getConfidence();
	}

	@Override
	public boolean proofAttempted() {
		return !(root.isOpen() && root.getComment().length() == 0);
	}
	
	@Override
	public IProofDependencies getProofDependencies() {
		return getRoot().computeProofDeps().finished(getFormulaFactory());
	}
	
	@Override
	public String toString(){
		return root.toString();
	}

}

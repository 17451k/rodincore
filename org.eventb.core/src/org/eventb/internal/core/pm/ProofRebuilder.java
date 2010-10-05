/*******************************************************************************
 * Copyright (c) 2010 Systerel and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     Systerel - initial API and implementation
 *******************************************************************************/
package org.eventb.internal.core.pm;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eventb.core.EventBPlugin;
import org.eventb.core.IPRProof;
import org.eventb.core.pm.IProofAttempt;
import org.eventb.core.seqprover.IProofSkeleton;
import org.eventb.core.seqprover.IProofTree;
import org.eventb.core.seqprover.ITactic;
import org.eventb.core.seqprover.autoTacticPreference.IAutoTacticPreference;
import org.eventb.core.seqprover.tactics.BasicTactics;
import org.eventb.internal.core.ProofMonitor;

/**
 * This class implements the operation to rebuild of a proof.
 * 
 * @author Nicolas Beauger
 * @since 1.3
 */
public class ProofRebuilder extends ProofModifier {

	private static final String REBUILDER = "Rebuilder"; //$NON-NLS-1$
	private final boolean applyPostTactics;

	public ProofRebuilder(IPRProof proof, boolean applyPostTactics) {
		super(proof, REBUILDER);
		this.applyPostTactics = applyPostTactics;
	}

	@Override
	protected boolean makeNewProof(IProofAttempt pa,
			IProofSkeleton originalSkeleton, IProgressMonitor monitor) {
		final IProofTree pt = pa.getProofTree();
		final Object result = BasicTactics.rebuildTac(originalSkeleton).apply(
				pt.getRoot(), new ProofMonitor(monitor));

		final boolean success = (result == null);
		if (success && applyPostTactics && !pt.isClosed()) {
			applyPostTacticsIfEnabled(pt, monitor);
		}
		return success;
	}

	private static void applyPostTacticsIfEnabled(IProofTree pt,
			IProgressMonitor monitor) {
		final IAutoTacticPreference postTacticPreference = EventBPlugin
				.getPostTacticPreference();
		if (postTacticPreference.isEnabled()) {
			final ITactic postTactic = postTacticPreference
					.getSelectedComposedTactic();
			postTactic.apply(pt.getRoot(), new ProofMonitor(monitor));

		}
	}
	
}

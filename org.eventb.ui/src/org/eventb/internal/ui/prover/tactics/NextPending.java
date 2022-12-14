/*******************************************************************************
 * Copyright (c) 2007, 2014 ETH Zurich and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     ETH Zurich - initial API and implementation
 *******************************************************************************/
package org.eventb.internal.ui.prover.tactics;

import static org.eventb.core.seqprover.ProverLib.isUncertain;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eventb.core.ast.Predicate;
import org.eventb.core.pm.IProofState;
import org.eventb.core.pm.IUserSupport;
import org.eventb.core.seqprover.IProofTree;
import org.eventb.core.seqprover.IProofTreeNode;
import org.eventb.core.seqprover.IProofTreeNodeFilter;
import org.eventb.internal.ui.EventBUIExceptionHandler;
import org.eventb.ui.prover.IProofCommand;
import org.rodinp.core.RodinDBException;

public class NextPending implements IProofCommand {

	@Override
	public void apply(IUserSupport us, Predicate hyp, String[] inputs,
			IProgressMonitor monitor) throws RodinDBException {
		us.selectNextSubgoal(false, new IProofTreeNodeFilter() {

			@Override
			public boolean select(IProofTreeNode node) {
				return node.isOpen() || isUncertain(node.getRuleConfidence());
			}
			
		});
	}

	@Override
	public boolean isApplicable(IUserSupport us, Predicate hyp, String input) {
		final IProofState currentPO = us.getCurrentPO();
		try {
			if (currentPO == null) {
				return false;
			}
			if (!currentPO.isClosed()) {
				return true;
			}
			final IProofTree pt = currentPO.getProofTree();
			return pt != null && isUncertain(pt.getConfidence());
		} catch (RodinDBException e) {
			EventBUIExceptionHandler.handleGetAttributeException(e);
		}
		return false;
	}

}

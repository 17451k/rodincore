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

package org.eventb.ui.prover;

import org.eventb.core.seqprover.IProofTreeNode;
import org.eventb.core.seqprover.ITactic;

/**
 * @author htson
 *         <p>
 *         This is the common interface for global proof tactics.
 */
public interface IGoalTactic {

	/**
	 * Check if the tactic is enable (applicable).
	 * <p>
	 * 
	 * @param node
	 *            the current proof tree node
	 * @param input
	 *            the optional string input
	 * @return <code>true</code> if the tactic is applicable and
	 *         <code>false</code> otherwise
	 */
	public boolean isApplicable(IProofTreeNode node);

	/**
	 * Apply the tactic.
	 * <p>
	 * 
	 * @param userSupport
	 *            the current user support
	 * @param input
	 *            the (optional) string input
	 * @throws RodinDBException
	 *             exceptions can be throws when applying tactics.
	 */
	public ITactic getTactic(IProofTreeNode node,  String [] inputs);
			
}

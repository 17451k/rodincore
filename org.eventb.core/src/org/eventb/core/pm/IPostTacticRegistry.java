/*******************************************************************************
 * Copyright (c) 2007 ETH Zurich.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributor
 *        Rodin @ ETH-Zurich
 *******************************************************************************/

package org.eventb.core.pm;

import org.eventb.core.seqprover.autoTacticPreference.IAutoTacticPreference;

/**
 * @author htson
 *         <p>
 *         This defines the interface for the post-tactic registry
 *         @deprecated use {@link IAutoTacticPreference} instead.
 */
@Deprecated
public interface IPostTacticRegistry {

	/**
	 * Check if a tactic is declared to be used as a post-tactic or not.
	 * <p>
	 * 
	 * @param tacticID
	 *            a string (a tactic ID).
	 * @return <code>true</code> if the tactic can be used as a post-tactic.
	 *         Return <code>false</code> otherwise.
	 */
	public abstract boolean isDeclared(String tacticID);

	/**
	 * Return the set of tactic IDs that declared to be used as post-tactics.
	 * <p>
	 * 
	 * @return an array of strings (tactic IDs).
	 */
	public abstract String[] getTacticIDs();

}
/*******************************************************************************
 * Copyright (c) 2010, 2013 Systerel and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Systerel - initial API and implementation
 *******************************************************************************/
package org.eventb.core.preferences.autotactics;

import org.eventb.core.EventBPlugin;
import org.eventb.core.IEventBRoot;
import org.eventb.core.seqprover.ITactic;
import org.eventb.core.seqprover.autoTacticPreference.IAutoTacticPreference;

/**
 * Facade for the management of auto and post tactics.
 * <p>
 * An instance can be obtained by calling
 * {@link EventBPlugin#getAutoPostTacticManager()}.
 * </p>
 * @since 2.1
 * @noextend This interface is not intended to be extended by clients.
 * @noimplement This interface is not intended to be implemented by clients.
 */
public interface IAutoPostTacticManager {

	/**
	 * Returns if the auto-tactics are activated, the composed tactic of the
	 * selected tactic profile for the given root if there is one, or of the
	 * workspace profile.
	 * 
	 * @param root
	 *            the root for which the auto-tactic profile is retrieved
	 * @return the composed tactic of selected auto-tactics for the given root.
	 */
	ITactic getSelectedAutoTactics(IEventBRoot root);

	/**
	 * Returns if the interactive-tactics are activated, the composed tactic
	 * of the selected tactic profile for the given root if there is one, or
	 * of the workspace profile.
	 * 
	 * @param root
	 *            the root for which the auto-tactic profile is retrieved
	 * @return the composed tactic of selected interactive-tactics for the
	 *            given root.
	 */
	ITactic getSelectedInterTactics(IEventBRoot root);

	/**
	 * Returns if the post-tactics are activated, the composed tactic of the
	 * selected tactic profile for the given root if there is one, or of the
	 * workspace profile.
	 * 
	 * @param root
	 *            the root for which the post-tactic profile is retrieved
	 * @return the composed tactic of selected post-tactics for the given root.
	 */
	ITactic getSelectedPostTactics(IEventBRoot root);

	/**
	 * @return the workspace auto-tactic preference
	 */
	IAutoTacticPreference getAutoTacticPreference();

	/**
	 * @return the workspace interactive-tactic preference
	 */
	IAutoTacticPreference getInterTacticPreference();

	/**
	 * @return the workspace post-tactic preference
	 */
	IAutoTacticPreference getPostTacticPreference();

}
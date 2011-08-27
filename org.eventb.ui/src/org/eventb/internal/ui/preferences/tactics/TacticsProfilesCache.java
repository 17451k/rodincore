/*******************************************************************************
 * Copyright (c) 2010, 2011 Systerel and others.
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 * 
 * Contributors:
 *     Systerel - Initial API and implementation
 *******************************************************************************/
package org.eventb.internal.ui.preferences.tactics;

import static org.eventb.core.preferences.autotactics.TacticPreferenceConstants.P_TACTICSPROFILES;
import static org.eventb.core.preferences.autotactics.TacticPreferenceFactory.makeTacticRefMaker;
import static org.eventb.core.preferences.autotactics.TacticPreferenceFactory.makeTacticXMLSerializer;

import org.eclipse.jface.preference.IPreferenceStore;
import org.eventb.core.seqprover.IAutoTacticRegistry.ITacticDescriptor;

public class TacticsProfilesCache extends
		StorablePreferenceMap<ITacticDescriptor> {

	public TacticsProfilesCache(IPreferenceStore store) {
		super(store, P_TACTICSPROFILES, makeTacticXMLSerializer(),
				makeTacticRefMaker());
	}

}

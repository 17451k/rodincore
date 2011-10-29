/*******************************************************************************
 * Copyright (c) 2005, 2011 ETH Zurich and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     ETH Zurich - initial API and implementation
 *     Systerel - refactored to use ITacticProvider2 and ITacticApplication
 *******************************************************************************/
package org.eventb.internal.ui.prover.registry;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;

import org.eclipse.core.runtime.IConfigurationElement;
import org.eventb.internal.ui.prover.ProverUIUtils;

public class DropdownInfo {

	// FIXME remove variable (should be computed earlier)
	private final Map<String, TacticUIInfo> globalRegistry;

	private final String id;
	private final String toolbar;

	private volatile Collection<String> tactics; // FIXME Should be final

	public DropdownInfo(Map<String, TacticUIInfo> globalRegistry, String id,
			IConfigurationElement configuration) {
		this.globalRegistry = globalRegistry;
		this.id = id;
		// FIXME what if not present?
		this.toolbar = configuration.getAttribute("toolbar");
	}

	public String getID() {
		return id;
	}

	public String getToolbar() {
		return toolbar;
	}

	// FIXME this method is not thread safe
	public Collection<String> getTactics() {
		assert globalRegistry != null;

		if (tactics == null) {
			tactics = new ArrayList<String>();
			for (String tacticID : globalRegistry.keySet()) {
				final TacticUIInfo info = globalRegistry.get(tacticID);
				if (id.equals(info.getDropdown())) {
					tactics.add(info.getID());
					if (ProverUIUtils.DEBUG)
						ProverUIUtils.debug("Attached tactic " + tacticID
								+ " to dropdown " + id);
				}
			}
		}

		return tactics;
	}

}
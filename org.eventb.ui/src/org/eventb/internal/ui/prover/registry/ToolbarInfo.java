/*******************************************************************************
 * Copyright (c) 2005, 2013 ETH Zurich and others.
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
import java.util.List;
import java.util.Map;

import org.eventb.internal.ui.prover.ProverUIUtils;

public class ToolbarInfo extends AbstractInfo {

	// FIXME remove both variables (should be computed earlier)
	private final Map<String, TacticUIInfo> globalRegistry;
	private final Map<String, DropdownInfo> dropdownRegistry;
	private final Map<String, DynamicDropdownInfo> dynDropdownRegistry;
	
	private volatile List<DropdownInfo> dropdowns; // FIXME Should be final
	
	private volatile List<DynamicDropdownInfo> dynDropdowns; // FIXME Should be final
	
	private volatile List<TacticUIInfo> tactics; // FIXME Should be final

	public ToolbarInfo(Map<String, TacticUIInfo> globalRegistry,
			Map<String, DropdownInfo> dropdownRegistry,
			Map<String, DynamicDropdownInfo> dynDropdownRegistry, String id) {
		super(id);
		this.globalRegistry = globalRegistry;
		this.dropdownRegistry = dropdownRegistry;
		this.dynDropdownRegistry = dynDropdownRegistry;
	}

	public List<DropdownInfo> getDropdowns() {
		assert dropdownRegistry != null;

		if (dropdowns == null) {
			dropdowns = new ArrayList<DropdownInfo>();
			for (final DropdownInfo info : dropdownRegistry.values()) {
				if (id.equals(info.getToolbar())) {
					final String dropdownID = info.getID();
					dropdowns.add(info);
					if (ProverUIUtils.DEBUG)
						ProverUIUtils.debug("Attached dropdown " + dropdownID
								+ " to toolbar " + id);
				}
			}
		}

		return dropdowns;
	}

	public List<TacticUIInfo> getTactics() {
		assert globalRegistry != null;

		if (tactics == null) {
			tactics = new ArrayList<TacticUIInfo>();
			for (final TacticUIInfo info : globalRegistry.values()) {
				if (id.equals(info.getToolbar())) {
					final String tacticID = info.getID();
					tactics.add(info);
					if (ProverUIUtils.DEBUG)
						ProverUIUtils.debug("Attached tactic " + tacticID
								+ " to toolbar " + id);
				}
			}
		}

		return tactics;
	}

	// This method is not thread safe, but called only in UI thread, so OK.
	public List<DynamicDropdownInfo> getDynamicDropdowns() {
		assert dynDropdownRegistry != null;

		if (dynDropdowns == null) {
			dynDropdowns = new ArrayList<DynamicDropdownInfo>();
			for (final DynamicDropdownInfo info : dynDropdownRegistry.values()) {
				if (id.equals(info.getToolbar())) {
					final String dropdownID = info.getID();
					dynDropdowns.add(info);
					if (ProverUIUtils.DEBUG)
						ProverUIUtils.debug("Attached dynamic dropdown " + dropdownID
								+ " to toolbar " + id);
				}
			}
		}

		return dynDropdowns;
	}
}
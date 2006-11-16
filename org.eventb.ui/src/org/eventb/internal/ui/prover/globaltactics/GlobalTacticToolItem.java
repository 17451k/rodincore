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

package org.eventb.internal.ui.prover.globaltactics;

import org.eclipse.swt.widgets.ToolItem;
import org.eventb.core.pm.IProofState;
import org.eventb.core.pm.IUserSupport;
import org.eventb.core.seqprover.IProofTreeNode;
import org.eventb.internal.ui.prover.TacticUIRegistry;
import org.eventb.ui.prover.IProofCommand;
import org.eventb.ui.prover.ITacticProvider;

/**
 * @author htson
 *         <p>
 *         This class implement the Tool Items in the Proof Control Page (Global
 *         Tactics).
 */
public class GlobalTacticToolItem {

	ToolItem item;

	String tacticID;

	boolean interrupt;

	/**
	 * Constructor.
	 * <p>
	 * 
	 * @param item
	 *            The Tool Item in the Proof Control Page
	 * @param tacticID
	 *            The actual Global Tactic
	 */
	public GlobalTacticToolItem(ToolItem item, String tacticID,
			boolean interrupt) {
		this.item = item;
		this.tacticID = tacticID;
		this.interrupt = interrupt;
	}

	/**
	 * Get the actual Tool Item
	 * <p>
	 * 
	 * @return The Tool Item associated with this.
	 */
	public ToolItem getToolItem() {
		return item;
	}

	/**
	 * Update the status of the tool item according to the current proof tree
	 * node and the optional string input.
	 * <p>
	 * 
	 * @param input
	 *            the (optional) string input
	 */
	public void updateStatus(IUserSupport us, String input) {
		ITacticProvider provider = TacticUIRegistry.getDefault()
				.getTacticProvider(tacticID);
		if (provider != null) {
			
			IProofState currentPO = us.getCurrentPO();
			if (currentPO == null) {
				item.setEnabled(false);
				return;
			}
			IProofTreeNode node = currentPO.getCurrentNode();
			item.setEnabled(provider.isApplicable(node, null, input));
			return;
		} else {
			IProofCommand command = TacticUIRegistry.getDefault()
					.getProofCommand(tacticID, TacticUIRegistry.TARGET_GLOBAL);
			if (command != null) {
				item.setEnabled(command.isApplicable(us, null, input));
				return;
			}
		}
		item.setEnabled(false);
	}

	/**
	 * Get the actual global tactic.
	 * <p>
	 * 
	 * @return the global tactic associated with this.
	 */
	public String getTactic() {
		return tacticID;
	}

	public boolean isInterruptable() {
		return interrupt;
	}

}

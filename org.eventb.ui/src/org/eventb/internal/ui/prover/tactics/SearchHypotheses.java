/*******************************************************************************
 * Copyright (c) 2005, 2009 ETH Zurich and others.
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     ETH Zurich - initial API and implementation
 *     ETH Zurich - adapted to org.rodinp.keyboard
 ******************************************************************************/
package org.eventb.internal.ui.prover.tactics;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eventb.core.ast.Predicate;
import org.eventb.core.pm.IUserSupport;
import org.eventb.internal.ui.UIUtils;
import org.eventb.internal.ui.searchhypothesis.SearchHypothesis;
import org.eventb.ui.prover.IProofCommand;
import org.rodinp.core.RodinDBException;
import org.rodinp.keyboard.RodinKeyboardPlugin;

public class SearchHypotheses implements IProofCommand {

	public void apply(IUserSupport us, Predicate hyp, String[] inputs,
			IProgressMonitor monitor) throws RodinDBException {
		// TODO should have been fixed at the root (the text widget) rather than here
		final String input = RodinKeyboardPlugin.getDefault().translate(
				inputs[0]);
		us.searchHyps(input);
		// Trying to show the Search View
		UIUtils.showView(SearchHypothesis.VIEW_ID);
	}

	public boolean isApplicable(IUserSupport us, Predicate hyp, String input) {
		return (us.getCurrentPO() != null && us.getCurrentPO().getCurrentNode() != null);
	}

}

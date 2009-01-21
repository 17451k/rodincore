/*******************************************************************************
 * Copyright (c) 2008 Systerel and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     Systerel - initial API and implementation
 *******************************************************************************/
package org.eventb.internal.ui.proofskeleton;

import org.eclipse.jface.action.IAction;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.ui.IViewActionDelegate;
import org.eclipse.ui.IViewPart;

/**
 * ExpandAll action. Expands the master part tree viewer of the
 * ProofSkeletonView.
 * 
 * @author Nicolas Beauger
 * 
 */
public class ProofSkeletonExpandAllAction implements IViewActionDelegate {

	private ProofSkeletonView view;
	
	public void init(IViewPart view) {
		assert view instanceof ProofSkeletonView;
		this.view = (ProofSkeletonView) view;
	}

	public void run(IAction action) {
		view.changeExpansionState(true);
	}

	public void selectionChanged(IAction action, ISelection selection) {
		// Do nothing
	}

}

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
package org.eventb.ui.proofskeleton;

import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.Viewer;
import org.eventb.core.seqprover.IProofTree;
import org.eventb.core.seqprover.IProofTreeNode;
import org.eventb.ui.proofskeleton.PrfSklMasterDetailsBlock.DefaultMasterInput;

/**
 * Content provider for the proof skeleton viewer.
 * 
 * @author Nicolas Beauger
 *
 */
public class PrfSklContentProvider implements ITreeContentProvider {

	private static final Object[] NO_OBJECTS = new Object[0];
	
	public PrfSklContentProvider() {
		// Do nothing
	}
	
	public Object[] getChildren(Object parentElement) {
		if (parentElement instanceof IProofTreeNode) {
			return ((IProofTreeNode) parentElement).getChildNodes();
		}
		return NO_OBJECTS;
	}

	public Object getParent(Object element) {
		if (element instanceof IProofTreeNode) {
			return ((IProofTreeNode) element).getParent();
		}
		return null;
	}

	public boolean hasChildren(Object element) {
		if (element instanceof IProofTreeNode) {
			return ((IProofTreeNode) element).hasChildren();
		}
		return false;
	}

	public Object[] getElements(Object inputElement) {
		if (inputElement instanceof IProofTree) {
			final Object[] result = {((IProofTree) inputElement).getRoot()};
			return result;
		}
		if (inputElement instanceof IProofTreeNode) {
			final Object[] result = {((IProofTreeNode) inputElement).getRule()};
			return result;
		}
		if (inputElement instanceof DefaultMasterInput) {
			return new Object[] {inputElement};
		}
		return NO_OBJECTS;
	}

	public void dispose() {
	}

	public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
	}

}

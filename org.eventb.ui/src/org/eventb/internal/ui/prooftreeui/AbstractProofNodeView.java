/*******************************************************************************
 * Copyright (c) 2011, 2016 Systerel and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Systerel - initial API and implementation
 *******************************************************************************/
package org.eventb.internal.ui.prooftreeui;

import static org.rodinp.keyboard.ui.preferences.PreferenceConstants.RODIN_MATH_FONT;

import org.eclipse.jface.resource.JFaceResources;
import org.eclipse.jface.util.IPropertyChangeListener;
import org.eclipse.jface.util.PropertyChangeEvent;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.ISelectionListener;
import org.eclipse.ui.ISelectionService;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.part.ViewPart;
import org.eventb.core.seqprover.IProofTreeNode;

/**
 * Abstract class for views that display information about currently selected
 * proof tree node.
 * 
 * @author Nicolas Beauger
 * 
 */
public abstract class AbstractProofNodeView extends ViewPart implements
		ISelectionListener, IPropertyChangeListener {

	private Composite parentComp = null;
	private IProofTreeNode currentNode = null;
	private Font currentFont = null;

	/**
	 * Creates the initial control of the view.
	 * 
	 * @param parent
	 *            the parent control
	 * @param font
	 *            the mathematical font to use
	 */
	protected abstract void initializeControl(final Composite parent, Font font);

	/**
	 * Refreshes the control because a new proof tree node has been selected.
	 * 
	 * @param node
	 *            the new proof tree node to use for the display
	 */
	protected abstract void refreshContents(IProofTreeNode node);

	/**
	 * Refreshes the control with a new mathematical font.
	 * 
	 * @param font
	 *            the mathematical font to use
	 */
	protected abstract void fontChanged(Font font);
	
	@Override
	public void createPartControl(final Composite parent) {
		this.parentComp = parent;
		// add myself as a global selection listener
		getSelectionService().addSelectionListener(this);
		currentFont = JFaceResources.getFont(RODIN_MATH_FONT);

		initializeControl(parent, currentFont);

		JFaceResources.getFontRegistry().addListener(this);
		// prime the selection to display contents
		refreshOnSelectionChanged(getSite().getPage().getSelection());
	}

	@Override
	public void setFocus() {
		// Nothing to do.
	}

	private void refreshOnSelectionChanged(ISelection selection) {
		if (parentComp == null || parentComp.isDisposed()) {
			return;
		}
		if (selection instanceof IStructuredSelection) {
			final IStructuredSelection ssel = ((IStructuredSelection) selection);
			final Object element = ssel.getFirstElement();
			if (currentNode == element) {
				return;
			}
			if (element instanceof IProofTreeNode) {
				currentNode = (IProofTreeNode) element;
				refreshContents(currentNode);
			}
		}
	}

	@Override
	public void selectionChanged(IWorkbenchPart part, ISelection selection) {
		refreshOnSelectionChanged(selection);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.ui.part.WorkbenchPart#dispose()
	 */
	@Override
	public void dispose() {
		getSelectionService().removeSelectionListener(this);
		JFaceResources.getFontRegistry().removeListener(this);
		super.dispose();
	}

	@Override
	public void propertyChange(PropertyChangeEvent event) {
		if (parentComp == null || parentComp.isDisposed()) {
			return;
		}
		if (event.getProperty().equals(RODIN_MATH_FONT)) {
			currentFont = JFaceResources.getFont(RODIN_MATH_FONT);
			if (currentNode != null) {
				fontChanged(currentFont);
			}
		}
	}

	private ISelectionService getSelectionService() {
		return getSite().getWorkbenchWindow().getSelectionService();
	}

}

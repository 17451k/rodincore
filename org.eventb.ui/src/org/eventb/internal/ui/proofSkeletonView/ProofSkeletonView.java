/*******************************************************************************
 * Copyright (c) 2008, 2010 Systerel and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     Systerel - initial API and implementation
 *     Systerel - added contextual menu to copy proof trees
 *******************************************************************************/
package org.eventb.internal.ui.proofSkeletonView;

import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.ui.forms.IManagedForm;
import org.eclipse.ui.forms.ManagedForm;
import org.eclipse.ui.part.ViewPart;

/**
 * ViewPart for displaying proof skeletons.
 * 
 * @author Nicolas Beauger
 * 
 */
public class ProofSkeletonView extends ViewPart {

	public static boolean DEBUG;
	
	protected PrfSklMasterDetailsBlock masterDetailsBlock;
	private InputManager selManager;
	private IManagedForm managedForm;
	
	@Override
	public void createPartControl(Composite parent) {
		parent.setLayout(new FillLayout());
		managedForm = new ManagedForm(parent);
		masterDetailsBlock = new PrfSklMasterDetailsBlock();
		masterDetailsBlock.createContent(managedForm);

		selManager = new InputManager(this);
		selManager.register();
		
		addContextMenu();
	}

	private void addContextMenu() {
		final Viewer viewer = masterDetailsBlock.getViewer();
		final Control control = viewer.getControl();
		final MenuManager menuManager = new MenuManager();
		final Menu menu = menuManager.createContextMenu(control);
		control.setMenu(menu);
		getSite().registerContextMenu(menuManager, viewer);
		getSite().setSelectionProvider(viewer);
	}

	@Override
	public void setFocus() {
		// Do nothing
	}

	@Override
	public void dispose() {
		getSite().setSelectionProvider(null);
		selManager.unregister();
		super.dispose();
	}

	/**
	 * Expand or collapse the master part tree viewer.
	 * 
	 * @param expand
	 *            expands all when <code>true</code>; collapses all when
	 *            <code>false</code>.
	 */
	public void changeExpansionState(boolean expand) {
		if (expand) {
			masterDetailsBlock.getViewer().expandAll();
		} else {
			masterDetailsBlock.getViewer().collapseAll();
		}
	}

	public void switchOrientation() {
		masterDetailsBlock.switchOrientation();
	}

	public void setInput(IViewerInput input) {
		if (managedForm != null) {
			if(!managedForm.getForm().isDisposed()) {
				managedForm.setInput(input);
			}
		}
		setTitleToolTip(input.getTitleTooltip());
	}

}

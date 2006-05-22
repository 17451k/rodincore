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

package org.eventb.internal.ui.eventbeditor;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.dialogs.InputDialog;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.ui.IActionBars;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.ISelectionService;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.actions.ActionFactory;
import org.eclipse.ui.actions.RefreshAction;
import org.eclipse.ui.part.MultiPageEditorActionBarContributor;
import org.eventb.internal.ui.EventBImage;
import org.eventb.internal.ui.EventBUIPlugin;
import org.rodinp.core.IInternalElement;
import org.rodinp.core.IRodinElement;
import org.rodinp.core.RodinDBException;

/**
 * @author htson
 *         <p>
 *         Manages the installation/deinstallation of global actions for
 *         multi-page editors. Responsible for the redirection of global actions
 *         to the active editor. Multi-page contributor replaces the
 *         contributors for the individual editors in the multi-page editor.
 */
public class EventBEditorContributor extends
		MultiPageEditorActionBarContributor {
	public static RefreshAction sampleAction;

	public static Action rename;

	/**
	 * Creates a multi-page contributor.
	 */
	public EventBEditorContributor() {
		super();
		createActions();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.ui.part.MultiPageEditorActionBarContributor#setActivePage(org.eclipse.ui.IEditorPart)
	 */
	public void setActivePage(IEditorPart part) {

		IActionBars actionBars = getActionBars();
		if (actionBars != null) {
			actionBars.setGlobalActionHandler(ActionFactory.RENAME.getId(),
					rename);
			// actionBars.setGlobalActionHandler(
			// ActionFactory.UNDO.getId(),
			// getAction(editor, ITextEditorActionConstants.UNDO));
			// actionBars.setGlobalActionHandler(
			// ActionFactory.REDO.getId(),
			// getAction(editor, ITextEditorActionConstants.REDO));
			// actionBars.setGlobalActionHandler(
			// ActionFactory.CUT.getId(),
			// getAction(editor, ITextEditorActionConstants.CUT));
			// actionBars.setGlobalActionHandler(
			// ActionFactory.COPY.getId(),
			// getAction(editor, ITextEditorActionConstants.COPY));
			// actionBars.setGlobalActionHandler(
			// ActionFactory.PASTE.getId(),
			// getAction(editor, ITextEditorActionConstants.PASTE));
			// actionBars.setGlobalActionHandler(
			// ActionFactory.SELECT_ALL.getId(),
			// getAction(editor, ITextEditorActionConstants.SELECT_ALL));
			// actionBars.setGlobalActionHandler(
			// ActionFactory.FIND.getId(),
			// getAction(editor, ITextEditorActionConstants.FIND));
			// actionBars.setGlobalActionHandler(
			// IDEActionFactory.BOOKMARK.getId(),
			// getAction(editor, IDEActionFactory.BOOKMARK.getId()));
			actionBars.updateActionBars();
		}
	}

	/**
	 * Create the actions. Sub-class should override and call this super method
	 * in order to create their own actions.
	 */
	protected void createActions() {
		sampleAction = new RefreshAction(EventBUIPlugin
				.getActiveWorkbenchShell());

		sampleAction.setToolTipText("Refresh the component");
		sampleAction.setImageDescriptor(PlatformUI.getWorkbench()
				.getSharedImages().getImageDescriptor(
						ActionFactory.REFRESH.getId()));

		rename = new Action() {
			public void run() {
				IEditorPart part = EventBEditorContributor.this.getPage()
						.getActiveEditor();
				ISelectionService selService = part.getSite()
						.getWorkbenchWindow().getSelectionService();
				ISelection sel = selService.getSelection();
				if (sel instanceof IStructuredSelection) {
					IStructuredSelection ssel = (IStructuredSelection) sel;
					if (ssel.size() == 1) {
						if (ssel.getFirstElement() instanceof IRodinElement) {
							IRodinElement element = (IRodinElement) ssel
									.getFirstElement();
							InputDialog dialog = new InputDialog(part.getSite()
									.getShell(), "Rename", "Rename element",
									element.getElementName(), null);
							dialog.open();
							try {
								String text = dialog.getValue();
								if (text != null) {
									if (!element.getElementName().equals(text)) {
										((IInternalElement) element).rename(
												text, false, null);
									}
								}
							} catch (RodinDBException e) {
								e.printStackTrace();
							}
						}
					}
				}
			}
		};
		rename.setText("New Variables");
		rename.setToolTipText("Create new variables for the component");
		rename.setImageDescriptor(EventBImage
				.getImageDescriptor(EventBImage.IMG_NEW_VARIABLES_PATH));

	}

}

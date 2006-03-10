/*******************************************************************************
 * Copyright (c) 2005 ETH-Zurich
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     ETH RODIN Group
 *******************************************************************************/

package org.eventb.internal.ui.eventbeditor;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.ui.IActionBars;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IWorkbenchActionConstants;
import org.eclipse.ui.part.MultiPageEditorActionBarContributor;
import org.eclipse.ui.texteditor.ITextEditor;
import org.eventb.internal.ui.EventBImage;
import org.eventb.internal.ui.EventBUIPlugin;
import org.eventb.internal.ui.UIUtils;
import org.rodinp.core.IRodinFile;

/**
 * @author htson
 * Manages the installation/deinstallation of global actions for multi-page editors.
 * Responsible for the redirection of global actions to the active editor.
 * Multi-page contributor replaces the contributors for the individual editors in the multi-page editor.
 */
public class EventBContextEditorContributor
	extends MultiPageEditorActionBarContributor
{
	private IEditorPart activeEditorPart;
	
	private static Action newCarrierSets;
	private static Action newConstants;
	private static Action newTheorems;
	private static Action newAxioms;
	
	/**
	 * Creates a multi-page contributor.
	 */
	public EventBContextEditorContributor() {
		super();
		createActions();
	}
	/**
	 * Returns the action registed with the given text editor.
	 * @return IAction or null if editor is null.
	 */
	protected IAction getAction(ITextEditor editor, String actionID) {
		return (editor == null ? null : editor.getAction(actionID));
	}
	/* (non-JavaDoc)
	 * Method declared in AbstractMultiPageEditorActionBarContributor.
	 */

	public void setActivePage(IEditorPart part) {
		if (activeEditorPart == part)
			return;

		activeEditorPart = part;

		IActionBars actionBars = getActionBars();
//		if (actionBars != null) {
//
//			ITextEditor editor = (part instanceof ITextEditor) ? (ITextEditor) part : null;
//
//			actionBars.setGlobalActionHandler(
//				ActionFactory.DELETE.getId(),
//				getAction(editor, ITextEditorActionConstants.DELETE));
//			actionBars.setGlobalActionHandler(
//				ActionFactory.UNDO.getId(),
//				getAction(editor, ITextEditorActionConstants.UNDO));
//			actionBars.setGlobalActionHandler(
//				ActionFactory.REDO.getId(),
//				getAction(editor, ITextEditorActionConstants.REDO));
//			actionBars.setGlobalActionHandler(
//				ActionFactory.CUT.getId(),
//				getAction(editor, ITextEditorActionConstants.CUT));
//			actionBars.setGlobalActionHandler(
//				ActionFactory.COPY.getId(),
//				getAction(editor, ITextEditorActionConstants.COPY));
//			actionBars.setGlobalActionHandler(
//				ActionFactory.PASTE.getId(),
//				getAction(editor, ITextEditorActionConstants.PASTE));
//			actionBars.setGlobalActionHandler(
//				ActionFactory.SELECT_ALL.getId(),
//				getAction(editor, ITextEditorActionConstants.SELECT_ALL));
//			actionBars.setGlobalActionHandler(
//				ActionFactory.FIND.getId(),
//				getAction(editor, ITextEditorActionConstants.FIND));
//			actionBars.setGlobalActionHandler(
//				IDEActionFactory.BOOKMARK.getId(),
//				getAction(editor, IDEActionFactory.BOOKMARK.getId()));
//			actionBars.updateActionBars();
//		}
//		else {
			IToolBarManager manager = actionBars.getToolBarManager();
			manager.add(newTheorems);
			actionBars.updateActionBars();
			
//		}
	}
	private void createActions() {
		newCarrierSets = new Action() {
			public void run() {
				IEditorPart part = EventBUIPlugin.getActivePage().getActiveEditor();
				if (part instanceof EventBEditor) {
					EventBEditor editor = (EventBEditor) part;
					IRodinFile rodinFile = editor.getRodinInput();
					UIUtils.newCarrierSets(rodinFile);
					editor.setActivePage(CarrierSetsPage.PAGE_ID);
				}
			}
		};
		newCarrierSets.setText("New Carrier Sets");
		newCarrierSets.setToolTipText("Create new carrier sets for the component");
		newCarrierSets.setImageDescriptor(EventBImage.getImageDescriptor(EventBImage.IMG_NEW_CARRIER_SETS_PATH));

		
		newConstants = new Action() {
			public void run() {
				IEditorPart part = EventBUIPlugin.getActivePage().getActiveEditor();
				if (part instanceof EventBEditor) {
					EventBEditor editor = (EventBEditor) part;
					IRodinFile rodinFile = editor.getRodinInput();
					UIUtils.newConstants(rodinFile);
					editor.setActivePage(ConstantsPage.PAGE_ID);
				}
			}
		};
		newConstants.setText("New Constants");
		newConstants.setToolTipText("Create new cosntants for the component");
		newConstants.setImageDescriptor(EventBImage.getImageDescriptor(EventBImage.IMG_NEW_CONSTANTS_PATH));

		
		newTheorems = new Action() {
			public void run() {
				IEditorPart part = EventBUIPlugin.getActivePage().getActiveEditor();
				if (part instanceof EventBEditor) {
					EventBEditor editor = (EventBEditor) part;
					IRodinFile rodinFile = editor.getRodinInput();
					UIUtils.newTheorems(rodinFile);
					editor.setActivePage(TheoremsPage.PAGE_ID);				}
			}
		};
		newTheorems.setText("New Theorems");
		newTheorems.setToolTipText("Create new theorems for the component");
		newTheorems.setImageDescriptor(EventBImage.getImageDescriptor(EventBImage.IMG_NEW_THEOREMS_PATH));


		newAxioms = new Action() {
			public void run() {
				IEditorPart part = EventBUIPlugin.getActivePage().getActiveEditor();
				if (part instanceof EventBEditor) {
					EventBEditor editor = (EventBEditor) part;
					IRodinFile rodinFile = editor.getRodinInput();
					UIUtils.newAxioms(rodinFile);
					editor.setActivePage(AxiomsPage.PAGE_ID);
				}
			}
		};
		newAxioms.setText("New Axioms");
		newAxioms.setToolTipText("Create new axioms for the component");
		newAxioms.setImageDescriptor(EventBImage.getImageDescriptor(EventBImage.IMG_NEW_AXIOMS_PATH));
	}
	
	
	public void contributeToMenu(IMenuManager manager) {
		IMenuManager menu = new MenuManager("Event-B");
		menu.add(newCarrierSets);
		menu.add(newConstants);
		menu.add(newTheorems);
		menu.add(newAxioms);
		manager.prependToGroup(IWorkbenchActionConstants.MB_ADDITIONS, menu);
	}

	public void contributeToToolBar(IToolBarManager manager) {
		manager.add(new Separator());
		manager.add(newCarrierSets);
		manager.add(newConstants);
		manager.add(newTheorems);
		manager.add(newAxioms);
	}
}

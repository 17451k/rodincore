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
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.ui.ISharedImages;
import org.eclipse.ui.IWorkbenchActionConstants;
import org.eclipse.ui.PlatformUI;
import org.eventb.core.EventBPlugin;
import org.eventb.core.IAction;
import org.eventb.core.IEvent;
import org.eventb.core.IGuard;
import org.eventb.core.IMachineFile;
import org.eventb.core.IRefinesMachine;
import org.eventb.core.IVariable;
import org.eventb.internal.ui.EventBImage;
import org.eventb.internal.ui.UIUtils;
import org.eventb.ui.IEventBSharedImages;
import org.eventb.ui.eventbeditor.IEventBEditor;
import org.rodinp.core.IInternalElement;
import org.rodinp.core.IRodinElement;
import org.rodinp.core.IRodinProject;
import org.rodinp.core.RodinDBException;

/**
 * @author htson
 *         <p>
 *         This class provides the actions that will be used with the Synthetic
 *         Machine Editable Tree Viewer.
 */
public class SyntheticMachineMasterSectionActionGroup extends
		MasterSectionActionGroup {

	// Some actions
	protected Action addVariable;

	protected Action addInvariant;

	protected Action addTheorem;

	protected Action addEvent;

	protected Action addLocalVariable;

	protected Action addGuard;

	protected Action addAction;

	protected Action delete;

	protected Action handleUp;

	protected Action handleDown;

	protected Action showAbstraction;

	/**
	 * Constructor: Create the actions.
	 * <p>
	 * 
	 * @param eventBEditor
	 *            The Event-B Editor
	 * @param treeViewer
	 *            The tree viewer associated with this action group
	 */
	public SyntheticMachineMasterSectionActionGroup(IEventBEditor eventBEditor,
			TreeViewer treeViewer) {
		super(eventBEditor, treeViewer);

		// Add a variable.
		addVariable = new Action() {
			@Override
			public void run() {
				EventBEditorUtils.addVariable(editor, viewer);
			}
		};
		addVariable.setText("New &Variable");
		addVariable.setToolTipText("Create a new variable");
		addVariable
				.setImageDescriptor(EventBImage
						.getImageDescriptor(IEventBSharedImages.IMG_NEW_VARIABLES_PATH));

		// Add an invariant.
		addInvariant = new Action() {
			@Override
			public void run() {
				EventBEditorUtils.addInvariant(editor, viewer);
			}
		};
		addInvariant.setText("New &Invariant");
		addInvariant.setToolTipText("Create a new invariant");
		addInvariant
				.setImageDescriptor(EventBImage
						.getImageDescriptor(IEventBSharedImages.IMG_NEW_INVARIANTS_PATH));

		// Add a theorem.
		addTheorem = new Action() {
			@Override
			public void run() {
				EventBEditorUtils.addTheorem(editor, viewer);
			}
		};
		addTheorem.setText("New &Theorem");
		addTheorem.setToolTipText("Create a new theorem");
		addTheorem.setImageDescriptor(EventBImage
				.getImageDescriptor(IEventBSharedImages.IMG_NEW_THEOREMS_PATH));

		// Add an event.
		addEvent = new Action() {
			@Override
			public void run() {
				EventBEditorUtils.addEvent(editor, viewer);
			}
		};
		addEvent.setText("New &Event");
		addEvent.setToolTipText("Create a new event");
		addEvent.setImageDescriptor(EventBImage
				.getImageDescriptor(IEventBSharedImages.IMG_NEW_EVENT_PATH));

		// Add a local variable.
		addLocalVariable = new Action() {
			@Override
			public void run() {
				EventBEditorUtils.addLocalVariable(editor, viewer);
			}
		};
		addLocalVariable.setText("New &Local Variable");
		addLocalVariable.setToolTipText("Create a new (local) variable");
		addLocalVariable
				.setImageDescriptor(EventBImage
						.getImageDescriptor(IEventBSharedImages.IMG_NEW_VARIABLES_PATH));

		// Add a guard.
		addGuard = new Action() {
			@Override
			public void run() {
				EventBEditorUtils.addGuard(editor, viewer);
			}
		};
		addGuard.setText("New &Guard");
		addGuard.setToolTipText("Create a new guard");
		addGuard.setImageDescriptor(EventBImage
				.getImageDescriptor(IEventBSharedImages.IMG_NEW_GUARD_PATH));

		// Add an action.
		addAction = new Action() {
			@Override
			public void run() {
				EventBEditorUtils.addAction(editor, viewer);
			}
		};
		addAction.setText("New &Action");
		addAction.setToolTipText("Create a new action");
		addAction.setImageDescriptor(EventBImage
				.getImageDescriptor(IEventBSharedImages.IMG_NEW_ACTION_PATH));

		// Delete the current selected element in the tree viewer.
		delete = new Action() {
			@Override
			public void run() {
				EventBEditorUtils.deleteElements(viewer);
			}
		};
		delete.setText("&Delete");
		delete.setToolTipText("Delete selected element");
		delete.setImageDescriptor(PlatformUI.getWorkbench().getSharedImages()
				.getImageDescriptor(ISharedImages.IMG_TOOL_DELETE));

		// Handle the up action.
		handleUp = new Action() {
			@Override
			public void run() {
				EventBEditorUtils.handleUp(viewer);
			}
		};
		handleUp.setText("&Up");
		handleUp.setToolTipText("Move the element up");
		handleUp.setImageDescriptor(EventBImage
				.getImageDescriptor(IEventBSharedImages.IMG_UP_PATH));

		// Handle the down action.
		handleDown = new Action() {
			@Override
			public void run() {
				EventBEditorUtils.handleDown(viewer);
			}
		};
		handleDown.setText("D&own");
		handleDown.setToolTipText("Move the element down");
		handleDown.setImageDescriptor(EventBImage
				.getImageDescriptor(IEventBSharedImages.IMG_DOWN_PATH));

		// Handle ShowAbstraction action.
		showAbstraction = new Action() {
			@Override
			public void run() {
				IStructuredSelection ssel = (IStructuredSelection) viewer
						.getSelection();
				if (ssel.size() == 1) {
					Object obj = ssel.getFirstElement();
					IInternalElement event = TreeSupports.getEvent(obj);

					IMachineFile file = (IMachineFile) editor.getRodinInput();
					try {
						IRodinElement[] refines = file
								.getChildrenOfType(IRefinesMachine.ELEMENT_TYPE);
						if (refines.length == 1) {
							IRefinesMachine refine = (IRefinesMachine) refines[0];
							String name = refine.getAbstractMachineName();
							IRodinProject prj = file.getRodinProject();
							IMachineFile refinedFile = (IMachineFile) prj
									.getRodinFile(EventBPlugin
											.getMachineFileName(name));
							if (EventBEditorUtils.DEBUG)
								EventBEditorUtils.debug("Refined: "
										+ refinedFile.getElementName());
							IInternalElement abs_evt = refinedFile
									.getInternalElement(event.getElementType(),
											event.getElementName());
							UIUtils.linkToEventBEditor(abs_evt);

							// if (refinedFile.exists()) {
							// IWorkbenchPage activePage = EventBUIPlugin
							// .getActivePage();
							// IEditorReference[] editors = activePage
							// .getEditorReferences();
							//
							// for (IEditorReference editor : editors) {
							// IEditorPart part = editor.getEditor(true);
							// if (activePage.isPartVisible(part)) {
							// if (part instanceof EventBMachineEditor) {
							// activePage.openEditor();
							// }
							// }
							//								
							// IRodinFile rodinInput = ((EventBMachineEditor)
							// part)
							// .getRodinInput();
							// UIUtils.debugEventBEditor("Trying: "
							// + rodinInput.getElementName());
							// if (rodinInput.equals(refinedFile)) {
							// UIUtils.debugEventBEditor("Focus");
							// if (activePage.isPartVisible(part)) {
							// IStructuredSelection ssel =
							// (IStructuredSelection) event
							// .getSelection();
							// if (ssel.size() == 1) {
							// IInternalElement obj = (IInternalElement) ssel
							// .getFirstElement();
							// IInternalElement element = refinedFile
							// .getInternalElement(
							// obj
							// .getElementType(),
							// obj
							// .getElementName());
							// if (element != null)
							// ((EventBEditor) part)
							// .setSelection(element);
							// }
							// }
							// }
							// }
							// }
							// }
						}
					} catch (RodinDBException e) {
						e.printStackTrace();
					}

				}
			}
		};
		showAbstraction.setText("Abstraction");
		showAbstraction.setToolTipText("Show the corresponding abstract event");
		showAbstraction.setImageDescriptor(EventBImage
				.getImageDescriptor(IEventBSharedImages.IMG_REFINES_PATH));
	}

	/**
	 * Fill the context menu with the actions create initially.
	 * <p>
	 * 
	 * @see org.eclipse.ui.actions.ActionGroup#fillContextMenu(org.eclipse.jface.action.IMenuManager)
	 */
	@Override
	public void fillContextMenu(IMenuManager menu) {
		super.fillContextMenu(menu);
		ISelection sel = getContext().getSelection();
		if (sel instanceof IStructuredSelection) {
			IStructuredSelection ssel = (IStructuredSelection) sel;
			if (ssel.size() == 1) {
				Object obj = ssel.getFirstElement();

				if (obj instanceof IEvent) {
					menu.add(addLocalVariable);
					menu.add(addGuard);
					menu.add(addAction);
				}
			}

			menu.add(new Separator());
			menu.add(addVariable);
			menu.add(addInvariant);
			menu.add(addTheorem);
			menu.add(addEvent);
			if (ssel.size() == 1) {
				Object obj = ssel.getFirstElement();
				if ((obj instanceof IEvent) || (obj instanceof IGuard)
						|| (obj instanceof IAction)
						|| (obj instanceof IVariable)
						&& ((IVariable) obj).getParent() instanceof IEvent) {

					IRodinElement[] refines;
					IMachineFile file = (IMachineFile) editor.getRodinInput();
					try {
						refines = file
								.getChildrenOfType(IRefinesMachine.ELEMENT_TYPE);
						if (refines.length == 1)
							menu.add(showAbstraction);
					} catch (RodinDBException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			}
			if (!sel.isEmpty()) {
				menu.add(new Separator());
				menu.add(delete);
			}
		}

		// IStructuredSelection selection = (IStructuredSelection) getContext()
		// .getSelection();

		// boolean anyResourceSelected = !selection.isEmpty()
		// && ResourceSelectionUtil.allResourcesAreOfType(selection,
		// IResource.PROJECT | IResource.FOLDER | IResource.FILE);
		//

		// Other plug-ins can contribute there actions here
		// These actions are added by extending the extension point
		// org.eventb.ui.popup
		menu.add(new Separator(IWorkbenchActionConstants.MB_ADDITIONS));
	}

}

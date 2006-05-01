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
import org.eclipse.jface.action.IMenuListener;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ISelectionProvider;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.StructuredViewer;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerFilter;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.TreeItem;
import org.eclipse.ui.actions.ActionContext;
import org.eclipse.ui.actions.ActionGroup;
import org.eclipse.ui.forms.IManagedForm;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.ScrolledForm;
import org.eventb.core.IAction;
import org.eventb.core.IEvent;
import org.eventb.core.IGuard;
import org.eventb.core.IVariable;
import org.eventb.internal.ui.UIUtils;
import org.rodinp.core.ElementChangedEvent;
import org.rodinp.core.IInternalElement;
import org.rodinp.core.IRodinElement;
import org.rodinp.core.RodinDBException;

/**
 * @author htson
 * <p>
 * An implementation of the Event-B Table part with buttons
 * for displaying constants (used as master section in Master-Detail block).
 */
public class EventMasterSection 
	extends EventBTreePartWithButtons
{
	// The indexes for different buttons.
	private static final int ADD_EVT_INDEX = 0;
	private static final int ADD_VAR_INDEX = 1;
	private static final int ADD_GRD_INDEX = 2;
	private static final int ADD_ACT_INDEX = 3;
	private static final int UP_INDEX = 4;
	private static final int DOWN_INDEX = 5;

	private static final String [] buttonLabels =
		{"Add Event", "Add Var.", "Add Guard", "Add Action", "Up", "Down"};

	// Title and description of the section.
	private final static String SECTION_TITLE = "Events";
	private final static String SECTION_DESCRIPTION = "The list contains events from the model whose details are editable on the right";
	
	// The group of actions for the tree part.
	private ActionGroup groupActionSet;
	
	private ViewerFilter varFilter;
	private ViewerFilter grdFilter;

	/**
	 * Contructor.
	 * <p>
	 * @param managedForm The form to create this master section
	 * @param parent The composite parent
	 * @param toolkit The Form Toolkit used to create this master section
	 * @param style The style
	 * @param block The master detail block which this master section belong to
	 */
	public EventMasterSection(IManagedForm managedForm, Composite parent, FormToolkit toolkit, 
			int style, EventBEditor editor) {
		super(managedForm, parent, toolkit, style, editor, buttonLabels, SECTION_TITLE, SECTION_DESCRIPTION);
		
		makeActions();
		hookContextMenu();
		createToolBarActions(managedForm);
	}
	
	/**
	 * Create the Toolbar actions
	 */
	protected void createToolBarActions(IManagedForm managedForm) {
		final ScrolledForm form = managedForm.getForm();
		varFilter = new ViewerFilter() {

			/* (non-Javadoc)
			 * @see org.eclipse.jface.viewers.ViewerFilter#select(org.eclipse.jface.viewers.Viewer, java.lang.Object, java.lang.Object)
			 */
			@Override
			public boolean select(Viewer viewer, Object parentElement, Object element) {
				IRodinElement rodinElement = ((Leaf) element).getElement();
				
				if (rodinElement instanceof IVariable) return false;
				else return true;
			}
			
		};
		
		grdFilter = new ViewerFilter() {

			/* (non-Javadoc)
			 * @see org.eclipse.jface.viewers.ViewerFilter#select(org.eclipse.jface.viewers.Viewer, java.lang.Object, java.lang.Object)
			 */
			@Override
			public boolean select(Viewer viewer, Object parentElement, Object element) {
				IRodinElement rodinElement = ((Leaf) element).getElement();
				if (rodinElement instanceof IGuard) return false;
				else return true;
			}
			
		};

		Action filterVarAction = new Action("var", Action.AS_CHECK_BOX) {
			public void run() {
				TreeViewer viewer = ((TreeViewer) EventMasterSection.this.getViewer());
				if (isChecked()) viewer.addFilter(varFilter);
				else viewer.removeFilter(varFilter);
			}
		};
		filterVarAction.setChecked(false);
		filterVarAction.setToolTipText("Filter variable elements");
		Action filterGrdAtion = new Action("grd", Action.AS_CHECK_BOX) {
			public void run() {
				TreeViewer viewer = ((TreeViewer) EventMasterSection.this.getViewer());
				if (isChecked()) viewer.addFilter(grdFilter);
				else viewer.removeFilter(grdFilter);
			}
		};
		filterGrdAtion.setChecked(false);
		filterGrdAtion.setToolTipText("Filter guard elements");
		form.getToolBarManager().add(filterVarAction);
		form.getToolBarManager().add(filterGrdAtion);
		form.updateToolBar();
	}
	
	/*
	 * Create the actions that can be used in the tree.
	 */
	private void makeActions() {
		groupActionSet = new EventMasterSectionActionGroup(editor, (TreeViewer) this.getViewer());
	}
	
	
	/**
	 * Hook the actions to the menu
	 */
	private void hookContextMenu() {
		MenuManager menuMgr = new MenuManager("#PopupMenu");
		menuMgr.setRemoveAllWhenShown(true);
		menuMgr.addMenuListener(new IMenuListener() {
			public void menuAboutToShow(IMenuManager manager) {
				groupActionSet.setContext(new ActionContext(((StructuredViewer) getViewer()).getSelection()));
				groupActionSet.fillContextMenu(manager);
				groupActionSet.setContext(null);
			}
		});
		Viewer viewer = getViewer();
		Menu menu = menuMgr.createContextMenu(((Viewer) viewer).getControl());
		((Viewer) viewer).getControl().setMenu(menu);
		this.editor.getSite().registerContextMenu(menuMgr, (ISelectionProvider) viewer);
	}

	
	/*
	 * Handle add (new element) action.
	 */
	private void handleAddEvent() {
		UIUtils.newEvent(editor.getRodinInput());
		editor.editorDirtyStateChanged();
	}
	

	/*
	 * Handle up action.
	 */
	private void handleUp() {
		UIUtils.debug("Up: To be implemented");
		return;
	}
	
	
	/*
	 * Handle down action. 
	 */
	private void handleDown() {
		UIUtils.debug("Down: To be implemented");
		return;
	}
	
	
	/**
	 * Update the expanded of buttons.
	 */
	protected void updateButtons() {
		ISelection sel = ((ISelectionProvider) getViewer()).getSelection();
		Object [] selections = ((IStructuredSelection) sel).toArray();
		
		boolean hasOneSelection = selections.length == 1;
		
		boolean initSelected = false;
		
		if (hasOneSelection) {
			IRodinElement event;
			if (selections[0] instanceof Leaf) {
				IRodinElement element = ((Leaf) selections[0]).getElement();
				if (element instanceof IEvent) {
					event = (IRodinElement) element;
				}
				else if (element instanceof IInternalElement) {
					event = ((IInternalElement) element).getParent();
				}
				else { // Should not happen
					event = null; 
				}
				initSelected = (event.getElementName().equals("INITIALISATION")) ? true : false;
			}
			
		}

		setButtonEnabled(ADD_EVT_INDEX, true);
		setButtonEnabled(ADD_VAR_INDEX, hasOneSelection && !initSelected);
		setButtonEnabled(ADD_GRD_INDEX, hasOneSelection && !initSelected);
		setButtonEnabled(ADD_ACT_INDEX, hasOneSelection);
		setButtonEnabled(UP_INDEX, hasOneSelection);
		setButtonEnabled(DOWN_INDEX, hasOneSelection);
	}
	

	/**
	 * Method to response to button selection.
	 * <p>
	 * @param index The index of selected button
	 */
	protected void buttonSelected(int index) {
		switch (index) {
			case ADD_EVT_INDEX:
				handleAddEvent();
				break;
			case ADD_VAR_INDEX:
				handleAddVar();
				break;
			case ADD_GRD_INDEX:
				handleAddGuard();
				break;
			case ADD_ACT_INDEX:
				handleAddAction();
				break;
			case UP_INDEX:
				handleUp();
				break;
			case DOWN_INDEX:
				handleDown();
				break;
		}
	}
	

	private void handleAddVar() {
		try {
			TreeViewer viewer = (TreeViewer) this.getViewer();
			IStructuredSelection ssel = (IStructuredSelection) viewer.getSelection();
			if (ssel.size() == 1) {
				Object obj = ssel.getFirstElement();
				IInternalElement event = getEvent(obj);
				int counter = ((IInternalElement) event).getChildrenOfType(IVariable.ELEMENT_TYPE).length;
				IInternalElement element = event.createInternalElement(IVariable.ELEMENT_TYPE, "var"+(counter+1), null, null);
//				Leaf leaf = new Leaf(event);
				viewer.setExpandedState(findItem(event).getData(), true);
				select(element, 0);
			}
		}
		catch (RodinDBException e) {
			e.printStackTrace();
		}
	}

	private void handleAddGuard() {
		try {
			TreeViewer viewer = (TreeViewer) this.getViewer();
			IStructuredSelection ssel = (IStructuredSelection) viewer.getSelection();
			if (ssel.size() == 1) {
				Object obj = ssel.getFirstElement();
				IInternalElement event = getEvent(obj);
				int counter = ((IInternalElement) event).getChildrenOfType(IGuard.ELEMENT_TYPE).length;
				IInternalElement element = event.createInternalElement(IGuard.ELEMENT_TYPE, "grd"+(counter+1), null, null);
//				Leaf leaf = new Leaf(event);
				viewer.setExpandedState(findItem(event).getData(), true);
				select(element, 1);
			}
		}
		catch (RodinDBException e) {
			e.printStackTrace();
		}
	}

	private void handleAddAction() {
		try {
			TreeViewer viewer = (TreeViewer) this.getViewer();
			IStructuredSelection ssel = (IStructuredSelection) viewer.getSelection();
			if (ssel.size() == 1) {
				Object obj = ssel.getFirstElement();
				IInternalElement event = getEvent(obj);
				int counter = ((IInternalElement) event).getChildrenOfType(IAction.ELEMENT_TYPE).length;
				IInternalElement element = event.createInternalElement(IAction.ELEMENT_TYPE, null, null, null);
				element.setContents("act"+(counter+1));
//				Leaf leaf = new Leaf(event);
				viewer.setExpandedState(findItem(event).getData(), true);
				/* TODO Should use the previous findItem to avoid searching again */
				select(element, 1);
			}
		}
		catch (RodinDBException e) {
			e.printStackTrace();
		}
	}
	
	private IInternalElement getEvent(Object obj) {
		IRodinElement rodinElement = ((Leaf) obj).getElement();
		if (rodinElement instanceof IEvent) {
			return (IEvent) rodinElement;
		}
		else if (rodinElement instanceof IInternalElement) {
			return (IInternalElement) ((IInternalElement) rodinElement).getParent();
		}
		else return null; // should not happen
	}
	
	private TreeItem findItem(IRodinElement element) {
		Tree tree = ((TreeViewer) this.getViewer()).getTree();
		TreeItem [] items = tree.getItems();
		for (TreeItem item : items) {
			TreeItem temp = findItem(item, element);
			if (temp != null) return temp;
		}
		return null;
	}
	
	private TreeItem findItem(TreeItem item, IRodinElement element) {
		UIUtils.debug("From " + item);
		Leaf leaf = (Leaf) item.getData();
		if (leaf == null) return null;
		if (leaf.getElement().equals(element)) {
			UIUtils.debug("Found");
			return item;
		}
		else {
//			UIUtils.debug("Recursively ...");
			TreeItem [] items = item.getItems();
			for (TreeItem i : items) {
				TreeItem temp = findItem(i, element);
				if (temp != null) return temp;
			}
		}
//		UIUtils.debug("... Not found");
		return null;
	}
	
	
	private void select(Object obj, int column) throws RodinDBException {
//		UIUtils.debug("Element: " + obj);
//		if (obj instanceof IAction) {
//			UIUtils.debug("Action: " + ((IAction) obj).getContents());
//		}
		TreeViewer viewer = (TreeViewer) this.getViewer();
		TreeItem item = findItem((IRodinElement) obj);
		viewer.reveal(item.getData());

		((EventBEditableTreeViewer) viewer).selectItem(item, column); // try to select the second column to edit name
	}
	
	/*
	 * Create the tree view part.
	 * <p>
	 * @param managedForm The Form used to create the viewer.
	 * @param toolkit The Form Toolkit used to create the viewer
	 * @param parent The composite parent
	 */
	protected EventBEditableTreeViewer createTreeViewer(IManagedForm managedForm, FormToolkit toolkit, Composite parent) {
		return new EventEditableTreeViewer(parent, SWT.MULTI | SWT.H_SCROLL | SWT.V_SCROLL | SWT.FULL_SELECTION);
	}
	
	
	/**
	 * Set the selection in the tree viewer.
	 * <p>
	 * @param element A Rodin element
	 */
	public void setSelection(IRodinElement element) {
		TreeViewer viewer = (TreeViewer) this.getViewer();
		viewer.setSelection(new StructuredSelection(element));
	}
	
	/* (non-Javadoc)
	 * @see org.rodinp.core.IElementChangedListener#elementChanged(org.rodinp.core.ElementChangedEvent)
	 */
	public void elementChanged(ElementChangedEvent event) {
		((EventEditableTreeViewer) this.getViewer()).elementChanged(event);
		updateButtons();
	}
	
	/* (non-Javadoc)
	 * @see org.eventb.internal.ui.eventbeditor.EventBPartWithButtons#edit(org.rodinp.core.IRodinElement)
	 */
	@Override
	protected void edit(IRodinElement element) {
		TreeViewer viewer = (TreeViewer) this.getViewer();
		viewer.reveal(element);
		TreeItem item  = findItem(element);
		selectItem(item, 1);
	}

}

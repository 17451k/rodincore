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
import org.eclipse.jface.viewers.ISelectionProvider;
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
import org.eclipse.ui.forms.IManagedForm;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.ScrolledForm;
import org.eventb.core.IEvent;
import org.eventb.core.IGuard;
import org.eventb.core.IVariable;
import org.rodinp.core.ElementChangedEvent;
import org.rodinp.core.IInternalElement;
import org.rodinp.core.IRodinElement;
import org.rodinp.core.IUnnamedInternalElement;

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
	
	private ViewerFilter varFilter;
	private ViewerFilter grdFilter;

	/**
	 * Constructor.
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
				Object [] objects = viewer.getExpandedElements();
				if (isChecked()) viewer.addFilter(varFilter);
				else viewer.removeFilter(varFilter);
				// This only work for tree with 2 layers
				for (Object object : objects) {
					TreeItem item = TreeSupports.findItem(viewer.getTree(), ((Leaf) object).getElement());
					viewer.setExpandedState(item.getData(), true);
				}
			}
		};
		filterVarAction.setChecked(false);
		filterVarAction.setToolTipText("Filter variable elements");
		Action filterGrdAtion = new Action("grd", Action.AS_CHECK_BOX) {
			public void run() {
				TreeViewer viewer = ((TreeViewer) EventMasterSection.this.getViewer());
				Object [] objects = viewer.getExpandedElements();
//				for (Object object : objects) {
//				UIUtils.debug("Object: " + object + " type: " + object.getClass());
//			}
				if (isChecked()) viewer.addFilter(grdFilter);
				else viewer.removeFilter(grdFilter);
				// This only work for tree with 2 layers
				for (Object object : objects) {
					TreeItem item = TreeSupports.findItem(viewer.getTree(), ((Leaf) object).getElement());
					viewer.setExpandedState(item.getData(), true);
				}
			}
		};
		filterGrdAtion.setChecked(false);
		filterGrdAtion.setToolTipText("Filter guard elements");
		form.getToolBarManager().add(filterVarAction);
		form.getToolBarManager().add(filterGrdAtion);
		form.updateToolBar();
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
	
	/**
	 * Update the expanded of buttons.
	 */
	protected void updateButtons() {
		Tree tree = ((TreeViewer) getViewer()).getTree();
		TreeItem [] items = tree.getSelection();
		
		boolean hasOneSelection = items.length == 1;
		boolean initSelected = false;
		boolean canMoveUp = false;
		boolean canMoveDown = false;
		
		if (hasOneSelection) {
			IRodinElement event;
			if (items[0].getData() instanceof Leaf) {
				IRodinElement element = ((Leaf) items[0].getData()).getElement();
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

		if (hasOneSelection) {
			TreeItem item = items[0];
			IRodinElement element = ((Leaf) item.getData()).getElement();
			TreeItem prev = TreeSupports.findPrevItem(tree, item);
			if (prev != null) {
				Leaf leaf = (Leaf) prev.getData();
				if (element.getElementType() == leaf.getElement().getElementType())
					canMoveUp = true;
			}
			TreeItem next = TreeSupports.findNextItem(tree, item);
			if (next != null) {
				Leaf leaf = (Leaf) next.getData();
				if (element.getElementType() == leaf.getElement().getElementType())
					canMoveDown = true;
			}
		}
        setButtonEnabled(
			UP_INDEX,
			hasOneSelection && canMoveUp);
		setButtonEnabled(
			DOWN_INDEX,
			hasOneSelection && canMoveDown);
		
		setButtonEnabled(ADD_EVT_INDEX, true);
		setButtonEnabled(ADD_VAR_INDEX, hasOneSelection && !initSelected);
		setButtonEnabled(ADD_GRD_INDEX, hasOneSelection && !initSelected);
		setButtonEnabled(ADD_ACT_INDEX, hasOneSelection);
	}
	

	/**
	 * Method to response to button selection.
	 * <p>
	 * @param index The index of selected button
	 */
	protected void buttonSelected(int index) {
		switch (index) {
			case ADD_EVT_INDEX:
				groupActionSet.addEvent.run();
				break;
			case ADD_VAR_INDEX:
				groupActionSet.addLocalVariable.run();
				break;
			case ADD_GRD_INDEX:
				groupActionSet.addGuard.run();
				break;
			case ADD_ACT_INDEX:
				groupActionSet.addAction.run();
				break;
			case UP_INDEX:
				groupActionSet.handleUp.run();
				break;
			case DOWN_INDEX:
				groupActionSet.handleDown.run();
				break;
		}
	}
	
	/*
	 * Create the tree view part.
	 * <p>
	 * @param managedForm The Form used to create the viewer.
	 * @param toolkit The Form Toolkit used to create the viewer
	 * @param parent The composite parent
	 */
	protected EventBEditableTreeViewer createTreeViewer(IManagedForm managedForm, FormToolkit toolkit, Composite parent) {
		return new EventEditableTreeViewer(editor, parent, SWT.MULTI | SWT.H_SCROLL | SWT.V_SCROLL | SWT.FULL_SELECTION);
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
		TreeItem item  = TreeSupports.findItem(viewer.getTree(), element);
		if (element instanceof IUnnamedInternalElement) selectItem(item, 1);
		else if (element instanceof IVariable) selectItem(item, 0);
		else if (element instanceof IEvent) selectItem(item, 0);
		else selectItem(item, 1);
	}


	/* (non-Javadoc)
	 * @see org.eclipse.ui.forms.AbstractFormPart#dispose()
	 */
	@Override
	public void dispose() {
		editor.removeStatusListener(this);
		super.dispose();
	}
	
}

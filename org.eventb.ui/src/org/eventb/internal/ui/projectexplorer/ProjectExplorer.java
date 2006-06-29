/*******************************************************************************
 * Copyright (c) 2005 ETH Zurich.
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Rodin @ ETH Zurich
 ******************************************************************************/

package org.eventb.internal.ui.projectexplorer;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IMenuListener;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.IStatusLineManager;
import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.jface.viewers.DoubleClickEvent;
import org.eclipse.jface.viewers.IDoubleClickListener;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerSorter;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.ui.IActionBars;
import org.eclipse.ui.actions.ActionContext;
import org.eclipse.ui.part.ViewPart;
import org.eventb.core.EventBPlugin;
import org.eventb.core.IAction;
import org.eventb.core.IGuard;
import org.eventb.core.IVariable;
import org.eventb.internal.ui.EventBUIPlugin;
import org.eventb.internal.ui.UIUtils;
import org.rodinp.core.IRodinElement;
import org.rodinp.core.IRodinFile;
import org.rodinp.core.IRodinProject;

/**
 * @author htson
 *         <p>
 *         The view shows data obtained from the model. The view gets a list of
 *         models from the RodinDB. The view is connected to the model using a
 *         content provider.
 */
public class ProjectExplorer extends ViewPart {

	/**
	 * The plug-in identifier of the Project Explorer (value
	 * <code>"org.eventb.internal.ui.views.ProjectExplorer"</code>).
	 */
	public static final String VIEW_ID = EventBUIPlugin.PLUGIN_ID
			+ ".views.ProjectExplorer";

	// Debug flag
	public static boolean DEBUG = false;
	
	// The tree viewer to display the structure of projects, components, etc.
	private TreeViewer viewer;

	// Action when double clicking.
	private Action doubleClickAction;

	// Group of action that is used.
	private ProjectExplorerActionGroup groupActionSet;

	private IRodinProject currentProject;

	/**
	 * The constructor.
	 */
	public ProjectExplorer() {
	}

	/**
	 * Get the tree viewer
	 * 
	 * @return the contained tree viewer
	 */
	public TreeViewer getTreeViewer() {
		return viewer;
	}

	/**
	 * @author htson
	 *         <p>
	 *         This class sorts the projects by name and internal elements by
	 *         types
	 */
	private class ProjectsSorter extends ViewerSorter {

		/*
		 * (non-Javadoc)
		 * 
		 * @see org.eclipse.jface.viewers.ViewerSorter#compare(org.eclipse.jface.viewers.Viewer,
		 *      java.lang.Object, java.lang.Object)
		 */
		public int compare(Viewer viewer, Object e1, Object e2) {
			if (e1 instanceof IRodinProject) {
				return super.compare(viewer, e1, e2);
			} else {
				int cat1 = category(e1);
				int cat2 = category(e2);
				return cat1 - cat2;
			}
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see org.eclipse.jface.viewers.ViewerSorter#category(java.lang.Object)
		 */
		public int category(Object element) {
			if (element instanceof IVariable)
				return 1;
			if (element instanceof IGuard)
				return 2;
			if (element instanceof IAction)
				return 3;

			return 0;
		}
	}

	/**
	 * @author htson
	 *         <p>
	 *         Providing the label for object in the tree.
	 */
	private class ViewLabelProvider extends LabelProvider {
		public String getText(Object obj) {
			if (obj instanceof IRodinFile) {
				String name = ((IRodinFile) obj).getElementName();
				return EventBPlugin.getComponentName(name);
			}
			if (obj instanceof IRodinElement)
				return ((IRodinElement) obj).getElementName();
			return obj.toString();
		}

		public Image getImage(Object obj) {
			return UIUtils.getImage(obj);
		}
	}

	/**
	 * This is a callback that will allow us to create the viewer and initialize
	 * it.
	 * <p>
	 * 
	 * @see org.eclipse.ui.IWorkbenchPart#createPartControl(org.eclipse.swt.widgets.Composite)
	 */
	public void createPartControl(Composite parent) {
		viewer = new TreeViewer(parent, SWT.MULTI | SWT.H_SCROLL | SWT.V_SCROLL);
		viewer.setContentProvider(new ProjectExplorerContentProvider(this));
		viewer.setLabelProvider(new ViewLabelProvider());
		viewer.setSorter(new ProjectsSorter());
		viewer.setInput(EventBUIPlugin.getRodinDatabase());
		makeActions();
		hookContextMenu();
		hookDoubleClickAction();
		contributeToActionBars();
		IStatusLineManager slManager = getViewSite().getActionBars()
				.getStatusLineManager();
		viewer.addSelectionChangedListener(new StatusBarUpdater(slManager));
		this.getSite().setSelectionProvider(viewer);
	}

	/**
	 * @author htson
	 *         <p>
	 *         This class update the status bar of the Project Explorer to show
	 *         the current project.
	 */
	private class StatusBarUpdater implements ISelectionChangedListener {
		private IStatusLineManager slManager;

		StatusBarUpdater(IStatusLineManager slManager) {
			this.slManager = slManager;
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see org.eclipse.jface.viewers.ISelectionChangedListener#selectionChanged(org.eclipse.jface.viewers.SelectionChangedEvent)
		 */
		public void selectionChanged(SelectionChangedEvent event) {
			slManager.setMessage(event.getSelection().toString());
		}

	}

	/**
	 * Hook the actions to the context menu.
	 */
	private void hookContextMenu() {
		MenuManager menuMgr = new MenuManager("#PopupMenu");
		menuMgr.setRemoveAllWhenShown(true);
		menuMgr.addMenuListener(new IMenuListener() {
			public void menuAboutToShow(IMenuManager manager) {
				ActionContext context = new ActionContext(viewer.getSelection());
				context.setInput(getCurrentProject());
				groupActionSet.setContext(context);
				groupActionSet.fillContextMenu(manager);
				groupActionSet.setContext(null);
			}
		});
		Menu menu = menuMgr.createContextMenu(viewer.getControl());
		viewer.getControl().setMenu(menu);
		getSite().registerContextMenu(menuMgr, viewer);
	}

	/**
	 * Put the actions to to the pull down menu and toolbar.
	 */
	private void contributeToActionBars() {
		IActionBars bars = getViewSite().getActionBars();
		fillLocalPullDown(bars.getMenuManager());
		fillLocalToolBar(bars.getToolBarManager());
	}

	/**
	 * Fill the local pull down menu with actions.
	 * <p>
	 * 
	 * @param manager
	 *            A Menu manager
	 *            <p>
	 * @param manager
	 *            a menu manager
	 */
	private void fillLocalPullDown(IMenuManager manager) {
		MenuManager newMenu = new MenuManager("&New");
		newMenu.add(ProjectExplorerActionGroup.newProjectAction);
		newMenu.add(ProjectExplorerActionGroup.newComponentAction);
		manager.add(newMenu);
		manager.add(new Separator());
		manager.add(ProjectExplorerActionGroup.deleteAction);
	}

	/**
	 * Fill the toolbar with actions.
	 * <p>
	 * 
	 * @param manager
	 *            a menu manager
	 */
	private void fillLocalToolBar(IToolBarManager manager) {

		manager.add(ProjectExplorerActionGroup.newProjectAction);
		manager.add(ProjectExplorerActionGroup.newComponentAction);
		manager.add(ProjectExplorerActionGroup.deleteAction);
		manager.add(new Separator());
		ProjectExplorerActionGroup.drillDownAdapter
				.addNavigationActions(manager);

	}

	/**
	 * Creat the actions.
	 */
	private void makeActions() {
		groupActionSet = new ProjectExplorerActionGroup(this);

		// Double click to link with editor
		doubleClickAction = new Action() {
			public void run() {
				ISelection selection = viewer.getSelection();
				Object obj = ((IStructuredSelection) selection)
						.getFirstElement();

				if (!(obj instanceof IRodinProject)) {
					UIUtils.linkToEventBEditor(obj);
				}
			}
		};
	}

	/**
	 * Associate the double click action.
	 */
	private void hookDoubleClickAction() {
		viewer.addDoubleClickListener(new IDoubleClickListener() {
			public void doubleClick(DoubleClickEvent event) {
				doubleClickAction.run();
			}
		});
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.ui.IWorkbenchPart#setFocus()
	 */
	public void setFocus() {
		viewer.getControl().setFocus();
	}

	/**
	 * Getting the top level project from the current root of tree showing in
	 * the Project Explorer.
	 * <p>
	 * 
	 * @param root
	 *            the root of the tree
	 */
	public void setRoot(Object root) {
		IRodinElement curr;
		if (root instanceof IRodinElement) {
			curr = (IRodinElement) root;
		} else if (root instanceof TreeNode) {
			curr = (IRodinElement) ((TreeNode) root).getParent();
		} else
			curr = null;
		while (!(curr instanceof IRodinProject || curr == null)) {
			curr = curr.getParent();
		}
		currentProject = (IRodinProject) curr;

		if (currentProject != null) {
			setContentDescription(currentProject.getElementName());
		} else {
			setContentDescription("");
		}
	}

	/**
	 * Get the current project
	 * <p>
	 * 
	 * @return the current project
	 */
	public IRodinProject getCurrentProject() {
		return currentProject;
	}

}
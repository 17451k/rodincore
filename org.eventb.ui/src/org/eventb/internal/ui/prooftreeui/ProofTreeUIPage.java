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

package org.eventb.internal.ui.prooftreeui;

import org.eclipse.jface.action.IMenuListener;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.jface.resource.JFaceResources;
import org.eclipse.jface.util.IPropertyChangeListener;
import org.eclipse.jface.util.PropertyChangeEvent;
import org.eclipse.jface.viewers.DoubleClickEvent;
import org.eclipse.jface.viewers.IDoubleClickListener;
import org.eclipse.jface.viewers.IFontProvider;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.ui.IActionBars;
import org.eclipse.ui.actions.ActionContext;
import org.eclipse.ui.part.IPageSite;
import org.eclipse.ui.part.Page;
import org.eclipse.ui.part.PageBook;
import org.eventb.core.EventBPlugin;
import org.eventb.core.pm.IProofState;
import org.eventb.core.pm.IProofStateDelta;
import org.eventb.core.pm.IUserSupport;
import org.eventb.core.pm.IUserSupportDelta;
import org.eventb.core.pm.IUserSupportInformation;
import org.eventb.core.pm.IUserSupportManagerChangedListener;
import org.eventb.core.pm.IUserSupportManagerDelta;
import org.eventb.core.seqprover.IProofTree;
import org.eventb.core.seqprover.IProofTreeNode;
import org.eventb.eventBKeyboard.preferences.PreferenceConstants;
import org.eventb.internal.ui.EventBImage;
import org.eventb.internal.ui.prover.ProofStatusLineManager;
import org.eventb.internal.ui.prover.ProverUIUtils;
import org.rodinp.core.RodinDBException;

/**
 * @author htson
 *         <p>
 *         This class is an implementation of a Proof Tree UI 'page'.
 */
public class ProofTreeUIPage extends Page implements IProofTreeUIPage,
		ISelectionChangedListener, IUserSupportManagerChangedListener {
	// The contained tree viewer.
	TreeViewer viewer;

	// The invisible root of the tree.
	private IProofTree invisibleRoot = null;

	private IProofTreeNode root = null;

	// TODO Change to Rule class?
	private Object[] filters = {}; // Default filters

	// The current editting element.
	private Object fInput;

	// The associated user support.
	IUserSupport userSupport;

	// Group of action that is used.
	ProofTreeUIActionGroup groupActionSet;

	PageBook pageBook;
	
	private ProofStatusLineManager statusManager;

	/**
	 * @author htson
	 *         <p>
	 *         This class provides the labels for elements in the tree viewer.
	 */
	private class ProofTreeLabelProvider extends LabelProvider implements
			IFontProvider, IPropertyChangeListener {

		public ProofTreeLabelProvider() {
			JFaceResources.getFontRegistry().addListener(this);
		}

		public Font getFont(Object element) {
			return JFaceResources.getFont(PreferenceConstants.EVENTB_MATH_FONT);
		}

		public void propertyChange(PropertyChangeEvent event) {
			if (event.getProperty()
					.equals(PreferenceConstants.EVENTB_MATH_FONT)) {
				if (event.getProperty().equals(
						PreferenceConstants.EVENTB_MATH_FONT)) {
					viewer.refresh();
				}
			}
		}

		@Override
		public void dispose() {
			JFaceResources.getFontRegistry().removeListener(this);
			super.dispose();
		}

		@Override
		public Image getImage(Object element) {
			if (element instanceof IProofTreeNode) {
				return EventBImage
						.getProofTreeNodeImage((IProofTreeNode) element);
			}
			return super.getImage(element);
		}

		@Override
		public String getText(Object element) {
			if (element instanceof IProofTreeNode) {
				IProofTreeNode proofTree = (IProofTreeNode) element;

				if (!proofTree.isOpen()) {
					if (ProofTreeUI.showGoal)
						return proofTree.getRule().getDisplayName() + " : "
								+ proofTree.getSequent().goal();
					else 
						return proofTree.getRule().getDisplayName();
				} else {
					return proofTree.getSequent().goal().toString();
				}
			}
			return super.getText(element);
		}
	}

	/**
	 * Creates a content outline page using the given editor. Register as a
	 * change listener for the Rodin Database.
	 * <p>
	 * 
	 * @param userSupport
	 *            the User Support
	 */
	public ProofTreeUIPage(IUserSupport userSupport) {
		super();
		this.userSupport = userSupport;
		EventBPlugin.getDefault().getUserSupportManager().addChangeListener(this);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.ui.part.IPageBookViewPage#init(org.eclipse.ui.part.IPageSite)
	 */
	@Override
	public void init(IPageSite pageSite) {
		super.init(pageSite);
		pageSite.setSelectionProvider(this);
	}

	@Override
	public void createControl(Composite parent) {
		assert parent instanceof PageBook;
		pageBook = (PageBook) parent;

		viewer = new TreeViewer(pageBook, SWT.SINGLE | SWT.H_SCROLL | SWT.V_SCROLL);
		viewer.setContentProvider(new ProofTreeUIContentProvider(this));
		viewer.setLabelProvider(new ProofTreeLabelProvider());
		viewer.addSelectionChangedListener(this);
		Tree tree = viewer.getTree();
		tree.setHeaderVisible(false);
		GridData gd = new GridData(GridData.FILL_BOTH);
		gd.horizontalSpan = 1;
		tree.setLayoutData(gd);

		ProofTreeUIToolTip handler = new ProofTreeUIToolTip(viewer.getControl()
				.getShell(), userSupport);
		handler.activateHoverHelp(viewer.getControl());

		makeActions();
		hookContextMenu();
		hookDoubleClickAction();
		contributeToActionBars();

		if (fInput != null)
			update();
		
		this.getSite().setSelectionProvider(viewer);
	}

	/**
	 * Sets the input of the outline page
	 * <p>
	 * 
	 * @param input
	 *            the input of this outline page
	 */
	public void setInput(Object input) {
		fInput = input;
		update();
	}

	/**
	 * Updates the outline page. Remember the previous expand states.
	 */
	private void update() {
		if (viewer != null) {
			Control control = viewer.getControl();
			if (control != null && !control.isDisposed()) {
				control.setRedraw(false);

				// Saving the expanded elements
				Object[] elements = viewer.getExpandedElements();
				viewer.setInput(fInput);
				if (fInput != null) {
					viewer.setExpandedElements(elements);
					viewer.refresh();

					IProofState currentPO = userSupport.getCurrentPO();
					if (currentPO != null)
						selectCurrentNode(currentPO.getCurrentNode());
				}
				control.setRedraw(true);
			}
		}
	}

	@Override
	public void dispose() {
		EventBPlugin.getDefault().getUserSupportManager().removeChangeListener(
				this);
		super.dispose();
	}

	/**
	 * Setup the context menu.
	 */
	private void hookContextMenu() {
		MenuManager menuMgr = new MenuManager("#PopupMenu");
		menuMgr.setRemoveAllWhenShown(true);
		menuMgr.addMenuListener(new IMenuListener() {
			public void menuAboutToShow(IMenuManager manager) {
				groupActionSet.setContext(new ActionContext(viewer
						.getSelection()));
				groupActionSet.fillContextMenu(manager);
				groupActionSet.setContext(null);
			}
		});
		Menu menu = menuMgr.createContextMenu(viewer.getControl());
		viewer.getControl().setMenu(menu);
		this.getSite().registerContextMenu("Proof tree " + fInput, menuMgr, viewer);
	}

	/**
	 * Setup the action bar.
	 */
	private void contributeToActionBars() {
		IActionBars bars = this.getSite().getActionBars();
		fillLocalPullDown(bars.getMenuManager());
		fillLocalToolBar(bars.getToolBarManager());
	}

	/**
	 * Setup the local pull down.
	 * <p>
	 * 
	 * @param manager
	 *            the menu manager
	 */
	private void fillLocalPullDown(IMenuManager manager) {
		manager.add(groupActionSet.filterAction);
		manager.add(new Separator());
		manager.add(groupActionSet.nextPOAction);
		manager.add(groupActionSet.prevPOAction);
	}

	/**
	 * Setup the local tool bar.
	 * <p>
	 * 
	 * @param manager
	 *            the menu manager
	 */
	private void fillLocalToolBar(IToolBarManager manager) {
		groupActionSet.drillDownAdapter.addNavigationActions(manager);
		manager.update(true);
	}

	/**
	 * Create various actions.
	 */
	private void makeActions() {
		groupActionSet = new ProofTreeUIActionGroup(this);
	}

	/**
	 * Hook the double click action.
	 */
	private void hookDoubleClickAction() {
		viewer.addDoubleClickListener(new IDoubleClickListener() {
			public void doubleClick(DoubleClickEvent event) {
				// Do nothing
			}
		});
	}

	/**
	 * Refresh the tree viewer from the proof tree node.
	 * <p>
	 * 
	 * @param pt
	 *            a proof tree node
	 */
	protected void refresh(IProofTreeNode pt) {
		// TODO Refresh the parent of this proof tree
		Object[] expands = viewer.getExpandedElements();
		viewer.refresh(true);
		viewer.setExpandedElements(expands);
		return;
	}

	/**
	 * Refresh the whole tree viewer.
	 */
	protected void refresh() {
		viewer.refresh(true);
		return;
	}

	/**
	 * Passing the focus request to the viewer's control.
	 * <p>
	 * 
	 * @see org.eclipse.ui.part.IPage#setFocus()
	 */
	@Override
	public void setFocus() {
		viewer.getControl().setFocus();
	}

	/**
	 * Getting the viewer.
	 * <p>
	 * 
	 * @return the tree viewer contains in the page
	 */
	protected TreeViewer getViewer() {
		return viewer;
	}

	/**
	 * Set the list of filters.
	 * <p>
	 * 
	 * @param newFilters
	 *            a list of filters
	 */
	protected void setFilters(Object[] newFilters) {
		this.filters = newFilters;
		viewer.refresh();
		viewer.expandAll();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.ui.part.Page#getControl()
	 */
	@Override
	public Control getControl() {
		if (viewer == null)
			return null;
		return viewer.getControl();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.jface.viewers.ISelectionProvider#addSelectionChangedListener(org.eclipse.jface.viewers.ISelectionChangedListener)
	 */
	public void addSelectionChangedListener(ISelectionChangedListener listener) {
		viewer.addSelectionChangedListener(listener);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.jface.viewers.ISelectionProvider#getSelection()
	 */
	public ISelection getSelection() {
		if (viewer == null)
			return StructuredSelection.EMPTY;
		return viewer.getSelection();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.jface.viewers.ISelectionProvider#removeSelectionChangedListener(org.eclipse.jface.viewers.ISelectionChangedListener)
	 */
	public void removeSelectionChangedListener(
			ISelectionChangedListener listener) {
		viewer.removeSelectionChangedListener(listener);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.jface.viewers.ISelectionProvider#setSelection(org.eclipse.jface.viewers.ISelection)
	 */
	public void setSelection(ISelection selection) {
		if (viewer != null)
			viewer.setSelection(selection);
	}

	/**
	 * This is called when there is a selection change in the tree. This
	 * responses by selecting the first element of the selection in the editor.
	 * <p>
	 * 
	 * @param event
	 *            the selection event
	 * 
	 * @see org.eclipse.jface.viewers.ISelectionChangedListener#selectionChanged(org.eclipse.jface.viewers.SelectionChangedEvent)
	 */
	public void selectionChanged(SelectionChangedEvent event) {
		if (ProofTreeUIUtils.DEBUG)
			ProofTreeUIUtils.debug("Selection Changed 1");
		ISelection sel = event.getSelection();
		if (ProofTreeUIUtils.DEBUG)
			ProofTreeUIUtils.debug("Selection Changed 2: " + sel);

		if (sel instanceof IStructuredSelection) {
			if (ProofTreeUIUtils.DEBUG)
				ProofTreeUIUtils.debug("Selection Changed 3");
			IStructuredSelection ssel = (IStructuredSelection) sel;
			if (ProofTreeUIUtils.DEBUG)
				ProofTreeUIUtils.debug("Selection Changed 4");
			if (!ssel.isEmpty()) {
				if (ProofTreeUIUtils.DEBUG)
					ProofTreeUIUtils.debug("Selection Changed 5");
				Object obj = ssel.getFirstElement();
				if (ProofTreeUIUtils.DEBUG)
					ProofTreeUIUtils.debug("Selection Changed 6: " + obj);
				if (obj instanceof IProofTreeNode) {
					try {
						if (ProofTreeUIUtils.DEBUG)
							ProofTreeUIUtils.debug("Selection Changed 7");
						userSupport.selectNode((IProofTreeNode) obj);
						if (ProofTreeUIUtils.DEBUG)
							ProofTreeUIUtils.debug("Selection Changed 8");
					} catch (RodinDBException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			} else { // Do nothing when there is no selection
				if (ProofTreeUIUtils.DEBUG)
					ProofTreeUIUtils.debug("Selection Changed 4.1");
			}
			if (ProofTreeUIUtils.DEBUG)
				ProofTreeUIUtils.debug("Selection Changed 9");
		}
		if (ProofTreeUIUtils.DEBUG)
			ProofTreeUIUtils.debug("Selection Changed 10");

	}

	/**
	 * Set the invisible root of the tree viewer.
	 * <p>
	 * 
	 * @param pt
	 *            a Proof Tree
	 */
	public void setInvisibleRoot(IProofTree pt) {
		this.invisibleRoot = pt;
	}

	/**
	 * Return the invisible root of the tree viewer.
	 * <p>
	 * 
	 * @return a Proof Tree which is the invisible root of the tree viewer
	 */
	public IProofTree getInvisibleRoot() {
		return invisibleRoot;
	}

	/**
	 * Setting the root of the tree viewer.
	 * <p>
	 * 
	 * @param pt
	 *            a Proof Tree Node
	 */
	public void setRoot(IProofTreeNode pt) {
		this.root = pt;
	}

	/**
	 * Return the roof of the tree viewer.
	 * <p>
	 * 
	 * @return the Proof Tree Node which is the root of the tree viewer
	 */
	public IProofTreeNode getRoot() {
		return root;
	}

	/**
	 * Return the associated UserSupport.
	 * 
	 * @return the associated UserSupport
	 */
	public IUserSupport getUserSupport() {
		return userSupport;
	}

	public void userSupportManagerChanged(final IUserSupportManagerDelta delta) {
		if (ProofTreeUIUtils.DEBUG)
			ProofTreeUIUtils.debug("Begin User Support Manager Changed");
		
		final Control control = viewer.getControl();
		// Do nothing if the control has been disposed
		if (control.isDisposed())
			return;
		
		if (ProofTreeUIUtils.DEBUG)
			ProofTreeUIUtils.debug("Proof Tree UI for "
					+ ProofTreeUIPage.this.userSupport.getInput()
							.getElementName() + ": State Changed: "
					+ delta.toString());

		// Trying to get the changes for the current user support.
		final IUserSupportDelta affectedUserSupport = ProverUIUtils
				.getUserSupportDelta(delta, userSupport);

		// Do nothing if there is no change for this current user support.
		if (affectedUserSupport == null)
			return;

		// If the user support has been removed, do nothing. This will be handle
		// by the main proof editor.
		final int kind = affectedUserSupport.getKind();
		if (kind == IUserSupportDelta.REMOVED) {
			return; // Do nothing
		}

		// This case should NOT happened.
		if (kind == IUserSupportDelta.ADDED) {
			if (ProofTreeUIUtils.DEBUG)
				ProofTreeUIUtils
						.debug("Error: Delta said that the user Support is added");
			return; // Do nothing
		}
		Display display = control.getDisplay();
		display.syncExec(new Runnable() {
			public void run() {

				// Do nothing if the control has been disposed
				if (control.isDisposed())
					return;

				// Handle the case where the user support has changed.
				if (kind == IUserSupportDelta.CHANGED) {
					int flags = affectedUserSupport.getFlags();
					
					// Set the information if it has been changed.
					if ((flags & IUserSupportDelta.F_INFORMATION) != 0) {
						setInformation(affectedUserSupport.getInformation());
					}

					if ((flags & IUserSupportDelta.F_CURRENT) != 0) {
						// The current proof state is changed.
						IProofState ps = userSupport.getCurrentPO();
						if (ps != null) {
							// The current proof state is not null, set the
							// input to the proof tree, select the current node
							// and expand all.
							ProofTreeUIPage page = ProofTreeUIPage.this;
							page.setInput(ps.getProofTree());
						} else {
							// The new proof state is null, set the input to
							// empty.
							ProofTreeUIPage.this.setInput(null);
						}
					} else if ((flags & IUserSupportDelta.F_STATE) != 0) {
						// If the changes occurs in some proof states.
						IProofState proofState = userSupport.getCurrentPO();
						// Trying to get the change for the current proof state. 
						final IProofStateDelta affectedProofState = ProverUIUtils
								.getProofStateDelta(affectedUserSupport,
										proofState);
						if (affectedProofState != null) {
						
							// If there are some changes
							int psKind = affectedProofState.getKind();

							if (psKind == IProofStateDelta.ADDED) {
								// This case should not happened
								if (ProofTreeUIUtils.DEBUG)
									ProofTreeUIUtils
											.debug("Error: Delta said that the proof state is added");
								return;
							}

							if (psKind == IProofStateDelta.REMOVED) {
								// Do nothing in this case, this will be handled
								// by the main proof editor.
								return;
							}
							
							if (psKind == IProofStateDelta.CHANGED) {
								// If there are some changes to the proof state.
								int psFlags = affectedProofState.getFlags();
								if ((psFlags & IProofStateDelta.F_PROOFTREE) != 0) {
									if (affectedProofState.getProofTreeDelta() != null) {
										// Refresh if the proof tree has changed.
										viewer.refresh();
									}
									else {
										// This is the case where the proof tree is completely change
										// i.e. reload or rebuild
										setInput(userSupport.getCurrentPO()
												.getProofTree());
									}
								}
								if ((psFlags & IProofStateDelta.F_NODE) != 0) {
									// If the current node has been changed
									IProofTreeNode node = proofState.getCurrentNode();
									
									selectCurrentNode(node);						
								}
							}
						}
						
					}
				}
			}
		});
	
		if (ProofTreeUIUtils.DEBUG)
			ProofTreeUIUtils.debug("End User Support Manager Changed");

	}

	void selectCurrentNode(IProofTreeNode node) {
		ISelection selection = viewer.getSelection();
		if (node != null) {
			// Select the new current node if not null.
			if (selection.isEmpty()) {
				viewer.setSelection(new StructuredSelection(node),
						true);
				return;
			}
			if (selection instanceof IStructuredSelection) {
				IStructuredSelection ssel = (IStructuredSelection) selection;
				Object firstElement = ssel.getFirstElement();
				if (!firstElement.equals(node))
					viewer.setSelection(new StructuredSelection(node),
							true);
			}
		}									
		else {
			// Set the selection to empty otherwise.
			if (!selection.isEmpty())
				viewer.setSelection(new StructuredSelection(),
					true);
		}					
	}
	
	public Object[] getFilters() {
		return filters;
	}

	void setInformation(final IUserSupportInformation[] information) {
		if (statusManager == null) {
			statusManager = new ProofStatusLineManager(this.getSite()
					.getActionBars());
		}
		statusManager.setProofInformation(information);
	}

}
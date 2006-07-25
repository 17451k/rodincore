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

package org.eventb.internal.ui.obligationexplorer;

import java.util.Collection;

import org.eclipse.jface.action.IMenuListener;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.jface.resource.ImageRegistry;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerFilter;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.CoolBar;
import org.eclipse.swt.widgets.CoolItem;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.widgets.ToolBar;
import org.eclipse.swt.widgets.ToolItem;
import org.eclipse.ui.IActionBars;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.actions.ActionContext;
import org.eclipse.ui.part.ViewPart;
import org.eventb.core.EventBPlugin;
import org.eventb.core.IPRFile;
import org.eventb.core.IPRSequent;
import org.eventb.core.basis.PRSequent;
import org.eventb.core.pm.IProofStateChangedListener;
import org.eventb.core.pm.IProofStateDelta;
import org.eventb.core.pm.IUSManagerListener;
import org.eventb.core.pm.ProofState;
import org.eventb.core.pm.UserSupport;
import org.eventb.core.pm.UserSupportManager;
import org.eventb.core.prover.IConfidence;
import org.eventb.core.prover.IProofTree;
import org.eventb.core.prover.IProofTreeDelta;
import org.eventb.internal.ui.EventBImage;
import org.eventb.internal.ui.EventBUIPlugin;
import org.eventb.internal.ui.TimerText;
import org.eventb.internal.ui.UIUtils;
import org.eventb.internal.ui.prover.ProofControl;
import org.eventb.internal.ui.prover.ProofTreeUI;
import org.eventb.internal.ui.prover.ProverUI;
import org.rodinp.core.IRodinFile;
import org.rodinp.core.IRodinProject;
import org.rodinp.core.RodinDBException;

/**
 * @author htson
 *         <p>
 *         The view shows data obtained from the model. The view gets a list of
 *         models and obligations from the RodinDB. The view is connected to the
 *         model using a content provider.
 */
public class ObligationExplorer extends ViewPart implements
		ISelectionChangedListener, IUSManagerListener,
		IProofStateChangedListener {

	/**
	 * The plug-in identifier of the Obligation Explorer (value
	 * <code>"org.eventb.ui.views.ObligationExplorer"</code>).
	 */
	public static final String VIEW_ID = EventBUIPlugin.PLUGIN_ID
			+ ".views.ObligationExplorer";

	// Debug flag
	public static boolean DEBUG = false;

	// The tree viewer to display the structure of projects, components, etc.
	private TreeViewer viewer;

	// Group of action that is used.
	private ObligationExplorerActionGroup groupActionSet;

	// A flag to indicate if the selection is made externally.
	private boolean byExternal;

	private ToolItem exclude;
	
	private ToolItem discharge;

	private Text filterText;

	private static int NULL = 0;
	
	private static int UNATTEMPTED = 1;
	
	private static int PENDING_BROKEN = 2;
	
	private static int PENDING = 3;
	
	private static int REVIEWED_BROKEN = 4;
	
	private static int REVIEWED = 5;
	
	private static int DISCHARGED_BROKEN = 6;
	
	private static int DISCHARGED;
	
	private class ObligationTextFilter extends ViewerFilter {

		@Override
		public boolean select(Viewer viewer, Object parentElement,
				Object element) {

			boolean selection = exclude.getSelection();
			if (element instanceof PRSequent) {
				PRSequent sequent = (PRSequent) element;
				
				if (sequent.getName().indexOf(filterText.getText()) == -1)
					return selection;
				else
					return !selection;
			} else {
				return true;
			}
		}

	}

	private class DischargedFilter extends ViewerFilter {

		@Override
		public boolean select(Viewer viewer, Object parentElement, Object element) {
			boolean selection = discharge.getSelection();
			if (element instanceof IPRSequent) {
				IPRSequent sequent = (IPRSequent) element;
				int status = NULL;
				try {
					status = getStatus(sequent);
				} catch (RodinDBException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
				
				if (status == DISCHARGED)
					return !selection;
				else
					return true;
			} else {
				return true;
			}
			
		}
		
	}
	
	private int getStatus(IPRSequent sequent) throws RodinDBException {
		// Try to synchronize with the proof tree in memory
		Collection<UserSupport> userSupports = UserSupportManager
				.getUserSupports();
		for (UserSupport userSupport : userSupports) {
			// UIUtils.debugObligationExplorer("Get US: "
			// + userSupport);
			Collection<ProofState> proofStates = userSupport
					.getPOs();
			for (ProofState proofState : proofStates) {
				if (proofState.getPRSequent().equals(sequent)) {
					IProofTree tree = proofState.getProofTree();

					if (!tree.proofAttempted())
						return UNATTEMPTED;

					int confidence = tree.getConfidence();

					if (confidence == IConfidence.PENDING) {
						if (false && sequent.isProofBroken())
							return PENDING_BROKEN;
						else
							return PENDING;
					}
					if (confidence <= IConfidence.REVIEWED_MAX) {
						if (false && sequent.isProofBroken())
							return REVIEWED_BROKEN;
						else
							return REVIEWED;
					}
					if (confidence <= IConfidence.DISCHARGED_MAX) {
						if (false && sequent.isProofBroken())
							return DISCHARGED_BROKEN;
						else
							return DISCHARGED;
					}
					return NULL;
				}
			}
		}

		// Otherwise, setting the label accordingly.
		if (!sequent.proofAttempted())
			return UNATTEMPTED;

		int confidence = sequent.getProofTree().getConfidence();
		if (sequent.isProofBroken()) {
			
			if (confidence == IConfidence.PENDING)
				return PENDING_BROKEN;
			if (confidence <= IConfidence.REVIEWED_MAX)
				return REVIEWED_BROKEN;
			if (confidence <= IConfidence.DISCHARGED_MAX)
				return DISCHARGED_BROKEN;

		} else {
	
			if (confidence == IConfidence.PENDING)
				return PENDING;
			if (confidence <= IConfidence.REVIEWED_MAX)
				return REVIEWED;
			if (confidence <= IConfidence.DISCHARGED_MAX)
				return DISCHARGED;

		}

		return NULL;

		// Previous code:
		// IProof status = ps.getProof();
		// if (status.getContents().equals("PENDING"))
		// return registry.get(EventBImage.IMG_PENDING);
		// else if (status.getContents().equals("DISCHARGED"))
		// return registry.get(EventBImage.IMG_DISCHARGED);
	}

	/**
	 * The constructor.
	 */
	public ObligationExplorer() {
		byExternal = false;
		UserSupportManager.addUSManagerListener(this);
		registerUserSupports();
	}

	private void registerUserSupports() {
		Collection<UserSupport> userSupports = UserSupportManager
				.getUserSupports();
		for (UserSupport userSupport : userSupports) {
			userSupport.addStateChangedListeners(this);
		}
	}

	/**
	 * Get the contained tree viewer
	 * <p>
	 * 
	 * @return a tree viewer
	 */
	public TreeViewer getTreeViewer() {
		return viewer;
	}

	/**
	 * @author htson
	 *         <p>
	 *         This class provides the label for object in the tree.
	 */
	private class ObligationLabelProvider extends LabelProvider {

		/*
		 * (non-Javadoc)
		 * 
		 * @see org.eclipse.jface.viewers.ILabelProvider#getText(java.lang.Object)
		 */
		public String getText(Object obj) {
			if (obj instanceof IRodinProject)
				return ((IRodinProject) obj).getElementName();
			if (obj instanceof IRodinFile) {
				String name = ((IRodinFile) obj).getElementName();
				return EventBPlugin.getComponentName(name);
			}
			if (obj instanceof IPRSequent) {
				// UIUtils.debugObligationExplorer("Label for: " + obj);

				// Find the label in the list of UserSupport.
				Collection<UserSupport> userSupports = UserSupportManager
						.getUserSupports();
				for (UserSupport userSupport : userSupports) {
					// UIUtils.debugObligationExplorer("Get US: " +
					// userSupport);
					Collection<ProofState> proofStates = userSupport.getPOs();
					for (ProofState proofState : proofStates) {
						if (proofState.getPRSequent().equals(obj)) {
							if (proofState.isDirty())
								return "* " + ((IPRSequent) obj).getName();
							else
								return ((IPRSequent) obj).getName();
						}
					}
				}
				return ((IPRSequent) obj).getName();
			}

			return obj.toString();
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see org.eclipse.jface.viewers.ILabelProvider#getImage(java.lang.Object)
		 */
		public Image getImage(Object obj) {
			ImageRegistry registry = EventBUIPlugin.getDefault()
					.getImageRegistry();
			if (obj instanceof IPRSequent) {
				IPRSequent prSequent = (IPRSequent) obj;
				try {
					
					// Try to synchronize with the proof tree in memory
					Collection<UserSupport> userSupports = UserSupportManager
							.getUserSupports();
					for (UserSupport userSupport : userSupports) {
						// UIUtils.debugObligationExplorer("Get US: "
						// + userSupport);
						Collection<ProofState> proofStates = userSupport
								.getPOs();
						for (ProofState proofState : proofStates) {
							if (proofState.getPRSequent().equals(obj)) {
								IProofTree tree = proofState.getProofTree();

								if (!tree.proofAttempted())
									return registry
											.get(EventBImage.IMG_UNATTEMPTED);

								int confidence = tree.getConfidence();

								if (confidence == IConfidence.PENDING) {
									if (false && prSequent.isProofBroken())
										return registry
												.get(EventBImage.IMG_PENDING_BROKEN);
									else
										return registry
												.get(EventBImage.IMG_PENDING);
								}
								if (confidence <= IConfidence.REVIEWED_MAX) {
									if (false && prSequent.isProofBroken())
										return registry
												.get(EventBImage.IMG_REVIEWED_BROKEN);
									else
										return registry
												.get(EventBImage.IMG_REVIEWED);
								}
								if (confidence <= IConfidence.DISCHARGED_MAX) {
									if (false && prSequent.isProofBroken())
										return registry
												.get(EventBImage.IMG_DISCHARGED_BROKEN);
									else
										return registry
												.get(EventBImage.IMG_DISCHARGED);
								}
								return registry.get(EventBImage.IMG_DEFAULT);
							}
						}
					}

					// Otherwise, setting the label accordingly.
					if (!prSequent.proofAttempted())
						return registry.get(EventBImage.IMG_UNATTEMPTED);

					int confidence = prSequent.getProofTree().getConfidence();
					if (prSequent.isProofBroken()) {
						
						if (confidence == IConfidence.PENDING)
							return registry.get(EventBImage.IMG_PENDING_BROKEN);
						if (confidence <= IConfidence.REVIEWED_MAX)
							return registry
									.get(EventBImage.IMG_REVIEWED_BROKEN);
						if (confidence <= IConfidence.DISCHARGED_MAX)
							return registry
									.get(EventBImage.IMG_DISCHARGED_BROKEN);

					} else {
				
						if (confidence == IConfidence.PENDING)
							return registry.get(EventBImage.IMG_PENDING);
						if (confidence <= IConfidence.REVIEWED_MAX)
							return registry.get(EventBImage.IMG_REVIEWED);
						if (confidence <= IConfidence.DISCHARGED_MAX)
							return registry.get(EventBImage.IMG_DISCHARGED);

					}

					return registry.get(EventBImage.IMG_DEFAULT);

					// Previous code:
					// IProof status = ps.getProof();
					// if (status.getContents().equals("PENDING"))
					// return registry.get(EventBImage.IMG_PENDING);
					// else if (status.getContents().equals("DISCHARGED"))
					// return registry.get(EventBImage.IMG_DISCHARGED);
				} catch (RodinDBException e) {
					e.printStackTrace();
				}
			}
			if (obj instanceof IPRFile) {
				IPRFile prFile = (IPRFile) obj;
				if (prFile.getMachine().exists())
					return registry.get(EventBImage.IMG_MACHINE);
				else if (prFile.getContext().exists())
					return registry.get(EventBImage.IMG_CONTEXT);
			}
			return UIUtils.getImage(obj);
		}
	}

	CoolItem createToolItem(CoolBar coolBar) {
		ToolBar toolBar = new ToolBar(coolBar, SWT.FLAT);
		discharge = new ToolItem(toolBar, SWT.CHECK);
		ImageRegistry registry = EventBUIPlugin.getDefault().getImageRegistry();
		discharge.setImage(registry.get(EventBImage.IMG_DISCHARGED));
		discharge.addSelectionListener(new SelectionListener() {

			public void widgetSelected(SelectionEvent e) {
				UIUtils.debugObligationExplorer("Event " + e);
				UIUtils.debugObligationExplorer("Status "
						+ exclude.getSelection());
				viewer.refresh();
			}

			public void widgetDefaultSelected(SelectionEvent e) {
				widgetSelected(e);
			}			
		});
		
		exclude = new ToolItem(toolBar, SWT.CHECK);
		exclude.setText("ex");
		exclude.addSelectionListener(new SelectionListener() {

			public void widgetSelected(SelectionEvent e) {
				UIUtils.debugObligationExplorer("Event " + e);
				UIUtils.debugObligationExplorer("Status "
						+ exclude.getSelection());
				viewer.refresh();
			}

			public void widgetDefaultSelected(SelectionEvent e) {
				widgetSelected(e);
			}

		});

		toolBar.pack();
		Point size = toolBar.getSize();
		CoolItem item = new CoolItem(coolBar, SWT.NONE);
		item.setControl(toolBar);
		Point preferred = item.computeSize(size.x, size.y);
		item.setPreferredSize(preferred);
		return item;
	}

	CoolItem createText(CoolBar coolBar) {
		filterText = new Text(coolBar, SWT.SINGLE | SWT.BORDER);
		new TimerText(filterText, 1000) {

			@Override
			protected void response() {
				viewer.refresh();
			}

		};
		filterText.pack();
		Point size = filterText.getSize();
		CoolItem item = new CoolItem(coolBar, SWT.NONE);
		item.setControl(filterText);
		Point preferred = item.computeSize(size.x, size.y);
		item.setPreferredSize(preferred);
		return item;
	}

	/**
	 * This is a callback that will allow us to create the viewer and initialize
	 * it.
	 * <p>
	 * 
	 * @see org.eclipse.ui.IWorkbenchPart#createPartControl(org.eclipse.swt.widgets.Composite)
	 */
	public void createPartControl(Composite parent) {
		FormLayout layout = new FormLayout();
		parent.setLayout(layout);
		CoolBar coolBar = new CoolBar(parent, SWT.FLAT);
		FormData coolData = new FormData();
		coolData.left = new FormAttachment(0);
		coolData.right = new FormAttachment(100);
		coolData.top = new FormAttachment(0);
		coolBar.setLayoutData(coolData);

		createToolItem(coolBar);
		createText(coolBar);

		viewer = new TreeViewer(parent, SWT.SINGLE | SWT.H_SCROLL
				| SWT.V_SCROLL);
		viewer.setContentProvider(new ObligationExplorerContentProvider(this));
		viewer.setLabelProvider(new ObligationLabelProvider());
		// viewer.setSorter(new ProjectsSorter());
		viewer.setInput(EventBUIPlugin.getRodinDatabase());
		viewer.addFilter(new ObligationTextFilter());
		viewer.addFilter(new DischargedFilter());
		FormData textData = new FormData();
		textData.left = new FormAttachment(0);
		textData.right = new FormAttachment(100);
		textData.top = new FormAttachment(coolBar);
		textData.bottom = new FormAttachment(100);
		viewer.getControl().setLayoutData(textData);

		// Sync with the current active ProverUI
		IWorkbenchPage activePage = EventBUIPlugin.getActivePage();
		if (activePage != null) {
			IEditorPart editor = activePage.getActiveEditor();
			if (editor instanceof ProverUI) {
				IPRSequent prSequent = ((ProverUI) editor)
						.getCurrentProverSequent();
				if (prSequent != null) {
					viewer.setSelection(new StructuredSelection(prSequent));
					viewer.reveal(prSequent);
				} else {
					IRodinFile prFile = ((ProverUI) editor).getRodinInput();
					viewer.setSelection(new StructuredSelection(prFile));
					viewer.reveal(prFile);
				}
			}
		}
		viewer.addSelectionChangedListener(this);
		makeActions();
		hookContextMenu();
		contributeToActionBars();
	}

	/**
	 * Hook the actions to the context menu.
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
	 *            a menu manager
	 */
	private void fillLocalPullDown(IMenuManager manager) {
		MenuManager newMenu = new MenuManager("&New");
		manager.add(newMenu);
		manager.add(new Separator());
		manager.add(ObligationExplorerActionGroup.refreshAction);
	}

	/**
	 * Fill the toolbar with actions.
	 * <p>
	 * 
	 * @param manager
	 *            a menu manager
	 */
	private void fillLocalToolBar(IToolBarManager manager) {
		manager.add(new Separator());
		ObligationExplorerActionGroup.drillDownAdapter
				.addNavigationActions(manager);
	}

	/**
	 * Creat the actions.
	 */
	private void makeActions() {
		groupActionSet = new ObligationExplorerActionGroup(this);
	}

	/**
	 * Passing the focus request to the viewer's control.
	 * <p>
	 * 
	 * @see org.eclipse.ui.IWorkbenchPart#setFocus()
	 */
	public void setFocus() {
		viewer.getControl().setFocus();
	}

	/**
	 * Refersh the view by refreshing the tree viewer.
	 */
	public void refresh() {
		viewer.refresh();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.jface.viewers.ISelectionChangedListener#selectionChanged(org.eclipse.jface.viewers.SelectionChangedEvent)
	 */
	public void selectionChanged(SelectionChangedEvent event) {
		if (byExternal)
			return;

		UIUtils.debugObligationExplorer("Selection changed: ");
		ISelection sel = event.getSelection();

		if (sel instanceof IStructuredSelection) {
			IStructuredSelection ssel = (IStructuredSelection) sel;

			if (!ssel.isEmpty()) {
				// UIUtils.debugObligationExplorer("Activate UI "
				// + ssel.toString());
				ISelection selection = viewer.getSelection();
				Object obj = ((IStructuredSelection) selection)
						.getFirstElement();

				if (obj instanceof IPRSequent) {
					IPRSequent ps = (IPRSequent) obj;

					selectPO(ps);
				}
			} else {
				UIUtils.debugObligationExplorer("De-selected");
				// Do nothing when there is no selection
				// editor.getUserSupport().selectNode(null);
			}
		}

	}

	private void selectPO(IPRSequent ps) {
		UIUtils.linkToProverUI(ps);
		UIUtils.activateView(ProofControl.VIEW_ID);
		UIUtils.activateView(ProofTreeUI.VIEW_ID);
	}

	/**
	 * External object need to call this methods to set the selection in the
	 * viewer.
	 * <p>
	 * 
	 * @param obj
	 *            the object will be selected
	 */
	public void externalSetSelection(Object obj) {
		byExternal = true;
		if (!((IStructuredSelection) viewer.getSelection()).toList().contains(
				obj)) {
			UIUtils.debugObligationExplorer("Set new Selection");
			viewer.getControl().setRedraw(false);
			viewer.setSelection(new StructuredSelection(obj));
			viewer.getControl().setRedraw(true);
		}
		byExternal = false;
	}

	public void USManagerChanged(final UserSupport userSupport, final int status) {
		Display display = viewer.getControl().getDisplay();
		display.syncExec(new Runnable() {
			public void run() {
				UIUtils.debugObligationExplorer("Obligation Explorer: "
						+ userSupport + " : " + status);
				if (status == UserSupportManager.ADDED) {
					userSupport
							.addStateChangedListeners(ObligationExplorer.this);
				}
				if (status == UserSupportManager.REMOVED) {
					userSupport
							.removeStateChangedListeners(ObligationExplorer.this);
				}
				viewer.refresh(userSupport.getInput());
			}
		});
	}

	public void proofStateChanged(final IProofStateDelta delta) {
		Display display = viewer.getControl().getDisplay();
		display.syncExec(new Runnable() {
			public void run() {
				UIUtils
						.debugObligationExplorer("Obligation Exprlorer: Proof Changed "
								+ delta);
				final ProofState ps = delta.getNewProofState();

				final UserSupport userSupport = delta.getSource();
				if (userSupport.isOutOfDate()) {
					viewer.refresh(userSupport.getInput(), true);
					return;
				}
				if (ps != null) {
					externalSetSelection(ps.getPRSequent());
				} else {
					IProofTreeDelta proofTreeDelta = delta.getProofTreeDelta();
					if (proofTreeDelta != null) {
						ProofState state = userSupport.getCurrentPO();
						final IPRSequent prSequent = state.getPRSequent();
						viewer.refresh(prSequent, true);
					}
				}
			}
		});
	}

	@Override
	public void dispose() {
		Collection<UserSupport> userSupports = UserSupportManager
				.getUserSupports();
		for (UserSupport userSupport : userSupports) {
			userSupport.removeStateChangedListeners(this);
		}
		UserSupportManager.removeUSManagerListener(this);
		viewer.removeSelectionChangedListener(this);
		super.dispose();
	}

}
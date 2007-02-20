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
import org.eclipse.jface.resource.JFaceResources;
import org.eclipse.jface.util.IPropertyChangeListener;
import org.eclipse.jface.util.PropertyChangeEvent;
import org.eclipse.jface.viewers.IColorProvider;
import org.eclipse.jface.viewers.IFontProvider;
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
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.CoolBar;
import org.eclipse.swt.widgets.CoolItem;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.widgets.ToolBar;
import org.eclipse.swt.widgets.ToolItem;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.ui.IActionBars;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.actions.ActionContext;
import org.eclipse.ui.part.ViewPart;
import org.eventb.core.EventBPlugin;
import org.eventb.core.IPRProof;
import org.eventb.core.IPSFile;
import org.eventb.core.IPSStatus;
import org.eventb.core.basis.PSStatus;
import org.eventb.core.pm.IProofState;
import org.eventb.core.pm.IUserSupport;
import org.eventb.core.pm.IUserSupportDelta;
import org.eventb.core.pm.IUserSupportManagerChangedListener;
import org.eventb.core.pm.IUserSupportManagerDelta;
import org.eventb.core.seqprover.IConfidence;
import org.eventb.core.seqprover.IProofTree;
import org.eventb.eventBKeyboard.preferences.PreferenceConstants;
import org.eventb.internal.ui.EventBImage;
import org.eventb.internal.ui.TimerText;
import org.eventb.internal.ui.UIUtils;
import org.eventb.internal.ui.proofcontrol.ProofControl;
import org.eventb.internal.ui.prooftreeui.ProofTreeUI;
import org.eventb.internal.ui.prover.ProverUI;
import org.eventb.ui.ElementSorter;
import org.eventb.ui.EventBUIPlugin;
import org.eventb.ui.IEventBSharedImages;
import org.rodinp.core.IRodinElement;
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
		ISelectionChangedListener, IUserSupportManagerChangedListener {
//	private TreeColumn column;

	/**
	 * The plug-in identifier of the Obligation Explorer (value
	 * <code>"org.eventb.ui.views.ObligationExplorer"</code>).
	 */
	public static final String VIEW_ID = EventBUIPlugin.PLUGIN_ID
			+ ".views.ObligationExplorer";
//
//	private static final int MAX_WIDTH = 500;

	// The tree viewer to display the structure of projects, components, etc.
	TreeViewer fViewer;

	// Group of action that is used.
	ObligationExplorerActionGroup groupActionSet;

	// A flag to indicate if the selection is made externally.
	private boolean byExternal;

	ToolItem exclude;

	ToolItem discharge;

	Text filterText;

	static int NULL = 0;

	private static int UNATTEMPTED = 1;

	private static int PENDING_BROKEN = 2;

	private static int PENDING = 3;

	private static int REVIEWED_BROKEN = 4;

	private static int REVIEWED = 5;

	private static int DISCHARGED_BROKEN = 6;

	static int DISCHARGED;

	/**
	 * Implements filtering based on proof obligation names.
	 */
	class ObligationTextFilter extends ViewerFilter {

		@Override
		public boolean select(Viewer viewer, Object parentElement,
				Object element) {

			if (element instanceof PSStatus) {
				final String filterString = filterText.getText();
				final boolean excluding = exclude.getSelection();
				if (filterString.length() == 0) {
					// This filter always match the PO name
					return !excluding;
				}
				final PSStatus sequent = (PSStatus) element;
				if (sequent.getElementName().contains(filterString))
					return !excluding;
				else
					return excluding;
			}
			return true;
		}

	}

	/**
	 * Implements filtering of discharged proof obligations.
	 */
	class DischargedFilter extends ViewerFilter {

		@Override
		public boolean select(Viewer viewer, Object parentElement,
				Object element) {
			
			if (element instanceof IPSStatus) {
				if (! discharge.getSelection()) {
					// No filtering on discharged POs
					return true;
				}

				try {
					return getStatus((IPSStatus) element) != DISCHARGED;
				} catch (RodinDBException e) {
					// Ignore case where database is not up to date
				}
			}
			return true;
		}

	}

	int getStatus(IPSStatus status) throws RodinDBException {
		// Try to synchronize with the proof tree in memory
		Collection<IUserSupport> userSupports = EventBPlugin.getDefault().getUserSupportManager()
				.getUserSupports();
		final boolean proofBroken = status.isBroken();
		for (IUserSupport userSupport : userSupports) {
			// UIUtils.debugObligationExplorer("Get US: "
			// + userSupport);
			IProofState [] proofStates = userSupport.getPOs();
			for (IProofState proofState : proofStates) {
				if (proofState.getPSStatus().equals(status)) {
					IProofTree tree = proofState.getProofTree();
					if (!proofState.isDirty() || tree == null)
						break;

					if (!tree.proofAttempted())
						return UNATTEMPTED;

					int confidence = tree.getConfidence();

					if (confidence == IConfidence.PENDING) {
						if (false && proofBroken)
							return PENDING_BROKEN;
						else
							return PENDING;
					}
					if (confidence <= IConfidence.REVIEWED_MAX) {
						if (false && proofBroken)
							return REVIEWED_BROKEN;
						else
							return REVIEWED;
					}
					if (confidence <= IConfidence.DISCHARGED_MAX) {
						if (false && proofBroken)
							return DISCHARGED_BROKEN;
						else
							return DISCHARGED;
					}
					return NULL; // Should not happen
				}
			}
		}

		// Otherwise, setting the label accordingly.

		final IPRProof prProof = status.getProof();

		// TODO : confidence now expresses unattempted as well
		if ((!prProof.exists()) || (prProof.getConfidence() <= IConfidence.UNATTEMPTED))
			return UNATTEMPTED;

		int confidence = prProof.getConfidence();
		if (proofBroken) {

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
	}

	/**
	 * Get the contained tree viewer
	 * <p>
	 * 
	 * @return a tree viewer
	 */
	public TreeViewer getTreeViewer() {
		return fViewer;
	}

	/**
	 * @author htson
	 *         <p>
	 *         This class provides the label for object in the tree.
	 */
	private class ObligationLabelProvider extends LabelProvider implements
			IFontProvider, IColorProvider, IPropertyChangeListener {

		public ObligationLabelProvider() {
			JFaceResources.getFontRegistry().addListener(this);
		}

		@Override
		public Image getImage(Object element) {
			ImageRegistry registry = EventBUIPlugin.getDefault()
					.getImageRegistry();
			if (element instanceof IPSStatus) {
				IPSStatus status = (IPSStatus) element;
				try {

					// Try to synchronize with the proof tree in memory
					Collection<IUserSupport> userSupports = EventBPlugin.getDefault().getUserSupportManager()
							.getUserSupports();
					for (IUserSupport userSupport : userSupports) {
						// UIUtils.debugObligationExplorer("Get US: "
						// + userSupport);
						IProofState [] proofStates = userSupport
								.getPOs();
						for (IProofState proofState : proofStates) {
							if (proofState.getPSStatus().equals(element)) {
								IProofTree tree = proofState.getProofTree();

								if (tree != null && proofState.isDirty()) {
									if (!tree.proofAttempted())
										return registry
												.get(IEventBSharedImages.IMG_UNATTEMPTED);

									int confidence = tree.getConfidence();

									final boolean proofBroken = status.isBroken();
									if (confidence == IConfidence.PENDING) {
										if (false && proofBroken)
											return registry
													.get(IEventBSharedImages.IMG_PENDING_BROKEN);
										else
											return registry
													.get(IEventBSharedImages.IMG_PENDING);
									}
									if (confidence <= IConfidence.REVIEWED_MAX) {
										if (false && proofBroken)
											return registry
													.get(IEventBSharedImages.IMG_REVIEWED_BROKEN);
										else
											return registry
													.get(IEventBSharedImages.IMG_REVIEWED);
									}
									if (confidence <= IConfidence.DISCHARGED_MAX) {
										if (false && proofBroken)
											return registry
													.get(IEventBSharedImages.IMG_DISCHARGED_BROKEN);
										else
											return registry
													.get(IEventBSharedImages.IMG_DISCHARGED);
									}
									return registry
											.get(IEventBSharedImages.IMG_DEFAULT);
								}
							}
						}
					}

					// Otherwise, setting the label accordingly.
					return EventBImage.getPRSequentImage(status);
				} catch (RodinDBException e) {
					e.printStackTrace();
				}
			}
			if (element instanceof IPSFile) {
				IPSFile prFile = (IPSFile) element;
				if (prFile.getMachineFile().exists())
					return registry.get(IEventBSharedImages.IMG_MACHINE);
				else if (prFile.getContextFile().exists())
					return registry.get(IEventBSharedImages.IMG_CONTEXT);
			}
			if (element instanceof IRodinElement)
				return EventBImage.getRodinImage((IRodinElement) element);
			return null;
		}

		@Override
		public String getText(Object obj) {
			if (ObligationExplorerUtils.DEBUG)
				ObligationExplorerUtils.debug("Label for: " + obj);
			if (obj instanceof IRodinProject) {
				if (ObligationExplorerUtils.DEBUG)
					ObligationExplorerUtils.debug("Project: "
							+ ((IRodinProject) obj).getElementName());
				return ((IRodinProject) obj).getElementName();
			} else if (obj instanceof IRodinFile) {
				return ((IRodinFile) obj).getBareName();
			} else if (obj instanceof IPSStatus) {

				// Find the label in the list of UserSupport.
				Collection<IUserSupport> userSupports = EventBPlugin.getDefault().getUserSupportManager()
						.getUserSupports();
				for (IUserSupport userSupport : userSupports) {
					// UIUtils.debugObligationExplorer("Get US: " +
					// userSupport);
					IProofState [] proofStates = userSupport.getPOs();
					for (IProofState proofState : proofStates) {
						if (proofState.getPSStatus().equals(obj)) {
							if (proofState.isDirty())
								return "* " + ((IPSStatus) obj).getElementName();
							else
								return ((IPSStatus) obj).getElementName();
						}
					}
				}
				return ((IPSStatus) obj).getElementName();
			}

			return obj.toString();
		}

		@Override
		public void dispose() {
			JFaceResources.getFontRegistry().removeListener(this);
			super.dispose();
		}

		public Font getFont(Object element) {
			return JFaceResources.getFont(PreferenceConstants.EVENTB_MATH_FONT);
		}

		public Color getForeground(Object element) {
			Display display = Display.getCurrent();
			return display.getSystemColor(SWT.COLOR_BLACK);
		}

		public Color getBackground(Object element) {
			Display display = Display.getCurrent();
			Color white = display.getSystemColor(SWT.COLOR_WHITE);
			Color yellow = display.getSystemColor(SWT.COLOR_YELLOW);
			if (element instanceof IRodinProject)
				return white;
			if (element instanceof IRodinFile) {
				return white;
			}
			if (element instanceof IPSStatus) {
				// UIUtils.debugObligationExplorer("Label for: " + obj);

				// Find the label in the list of UserSupport.
				Collection<IUserSupport> userSupports = EventBPlugin.getDefault().getUserSupportManager()
						.getUserSupports();
				for (IUserSupport userSupport : userSupports) {
					// UIUtils.debugObligationExplorer("Get US: " +
					// userSupport);
					IProofState [] proofStates = userSupport.getPOs();
					for (IProofState proofState : proofStates) {
						if (proofState.getPSStatus().equals(element)) {
							if (proofState.isDirty())
								return yellow;
							else
								return white;
						}
					}
				}
				return white;
			}

			return white;
		}

		public void propertyChange(PropertyChangeEvent event) {
			if (event.getProperty()
					.equals(PreferenceConstants.EVENTB_MATH_FONT)) {
				if (event.getProperty().equals(
						PreferenceConstants.EVENTB_MATH_FONT)) {
					fViewer.refresh();
				}
			}
		}
		
	}

	CoolItem createToolItem(CoolBar coolBar) {
		ToolBar toolBar = new ToolBar(coolBar, SWT.FLAT);
		discharge = new ToolItem(toolBar, SWT.CHECK);
		ImageRegistry registry = EventBUIPlugin.getDefault().getImageRegistry();
		discharge.setImage(registry.get(IEventBSharedImages.IMG_DISCHARGED));
		discharge.addSelectionListener(new SelectionListener() {

			public void widgetSelected(SelectionEvent e) {
				if (ObligationExplorerUtils.DEBUG) {
					ObligationExplorerUtils.debug("Event " + e);
					ObligationExplorerUtils.debug("Status "
							+ exclude.getSelection());
				}
				fViewer.refresh();
//				column.pack();
//				column.setWidth(MAX_WIDTH);
			}

			public void widgetDefaultSelected(SelectionEvent e) {
				widgetSelected(e);
			}
		});

		exclude = new ToolItem(toolBar, SWT.CHECK);
		exclude.setText("ex");
		exclude.addSelectionListener(new SelectionListener() {

			public void widgetSelected(SelectionEvent e) {
				if (ObligationExplorerUtils.DEBUG) {
					ObligationExplorerUtils.debug("Event " + e);
					ObligationExplorerUtils.debug("Status "
							+ exclude.getSelection());
				}
				fViewer.refresh();
//				column.pack();
//				column.setWidth(MAX_WIDTH);
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
				fViewer.refresh();
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
	@Override
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

		fViewer = new TreeViewer(parent, SWT.SINGLE | SWT.H_SCROLL
				| SWT.V_SCROLL);
		fViewer.setContentProvider(new ObligationExplorerContentProvider(this));
		fViewer.setLabelProvider(new ObligationLabelProvider());
		fViewer.setSorter(new ElementSorter());
		fViewer.addFilter(new ObligationTextFilter());
		fViewer.addFilter(new DischargedFilter());
		FormData textData = new FormData();
		textData.left = new FormAttachment(0);
		textData.right = new FormAttachment(100);
		textData.top = new FormAttachment(coolBar);
		textData.bottom = new FormAttachment(100);
		fViewer.getControl().setLayoutData(textData);
		Tree tree = fViewer.getTree();
		tree.setHeaderVisible(false);
//		column = new TreeColumn(tree, SWT.LEFT);
		fViewer.setInput(EventBUIPlugin.getRodinDatabase());
//		column.pack();
//		column.setWidth(MAX_WIDTH);

		// Sync with the current active ProverUI
		IWorkbenchPage activePage = EventBUIPlugin.getActivePage();
		if (activePage != null) {
			IEditorPart editor = activePage.getActiveEditor();
			if (editor instanceof ProverUI) {
				IPSStatus prSequent = ((ProverUI) editor)
						.getCurrentProverSequent();
				if (prSequent != null) {
					fViewer.setSelection(new StructuredSelection(prSequent));
					fViewer.reveal(prSequent);
				} else {
					IRodinFile prFile = ((ProverUI) editor).getRodinInput();
					fViewer.setSelection(new StructuredSelection(prFile));
					fViewer.reveal(prFile);
				}
			}
		}
		fViewer.addSelectionChangedListener(this);
		makeActions();
		hookContextMenu();
		contributeToActionBars();
		EventBPlugin.getDefault().getUserSupportManager().addChangeListener(this);
	}

	/**
	 * Hook the actions to the context menu.
	 */
	private void hookContextMenu() {
		MenuManager menuMgr = new MenuManager("#PopupMenu");
		menuMgr.setRemoveAllWhenShown(true);
		menuMgr.addMenuListener(new IMenuListener() {
			public void menuAboutToShow(IMenuManager manager) {
				groupActionSet.setContext(new ActionContext(fViewer
						.getSelection()));
				groupActionSet.fillContextMenu(manager);
				groupActionSet.setContext(null);
			}
		});
		Menu menu = menuMgr.createContextMenu(fViewer.getControl());
		fViewer.getControl().setMenu(menu);
		getSite().registerContextMenu(menuMgr, fViewer);
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
	@Override
	public void setFocus() {
		fViewer.getControl().setFocus();
	}

	/**
	 * Refersh the view by refreshing the tree viewer.
	 */
	public void refresh() {
		fViewer.refresh();
//		column.pack();
//		column.setWidth(MAX_WIDTH);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.jface.viewers.ISelectionChangedListener#selectionChanged(org.eclipse.jface.viewers.SelectionChangedEvent)
	 */
	public void selectionChanged(SelectionChangedEvent event) {
		if (byExternal)
			return;

		if (ObligationExplorerUtils.DEBUG)
			ObligationExplorerUtils.debug("Selection changed: ");
		ISelection sel = event.getSelection();

		if (sel instanceof IStructuredSelection) {
			IStructuredSelection ssel = (IStructuredSelection) sel;

			if (!ssel.isEmpty()) {
				// UIUtils.debugObligationExplorer("Activate UI "
				// + ssel.toString());
				ISelection selection = fViewer.getSelection();
				Object obj = ((IStructuredSelection) selection)
						.getFirstElement();

				if (obj instanceof IPSStatus) {
					IPSStatus ps = (IPSStatus) obj;

					selectPO(ps);
				}
			} else {
				if (ObligationExplorerUtils.DEBUG)
					ObligationExplorerUtils.debug("De-selected");
				// Do nothing when there is no selection
				// editor.getUserSupport().selectNode(null);
			}
		}

	}

	private void selectPO(IPSStatus ps) {
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
		if (!((IStructuredSelection) fViewer.getSelection()).toList().contains(
				obj)) {
			if (ObligationExplorerUtils.DEBUG)
				ObligationExplorerUtils.debug("Set new Selection");
			fViewer.getControl().setRedraw(false);
			fViewer.setSelection(new StructuredSelection(obj));
			fViewer.getControl().setRedraw(true);
		}
		byExternal = false;
	}

	public void userSupportManagerChanged(final IUserSupportManagerDelta delta) {

		Control control = fViewer.getControl();
		if (control.isDisposed())
			return;

		
		Display display = control.getDisplay();
		display.syncExec(new Runnable() {
			public void run() {
				IUserSupportDelta[] affectedUserSupports = delta.getAffectedUserSupports();
				for (IUserSupportDelta affectedUserSupport : affectedUserSupports) {
					fViewer.refresh(affectedUserSupport.getUserSupport().getInput());
					int kind = affectedUserSupport.getKind();
					if (kind == IUserSupportDelta.CHANGED) {
						int flags = affectedUserSupport.getFlags();
						if ((flags | IUserSupportDelta.F_CURRENT) != 0) {
							IProofState ps = affectedUserSupport.getUserSupport().getCurrentPO();
							if (ps != null) {
								IPSStatus prSequent = ps.getPSStatus();
								externalSetSelection(prSequent);
							} else { // Empty selection
								clearSelection();
							}
							
						}
								
					}
				}
			}
		});
	}

	@Override
	public void dispose() {
		if (fViewer == null)
			return;
		EventBPlugin.getDefault().getUserSupportManager().removeChangeListener(this);
		fViewer.removeSelectionChangedListener(this);
		super.dispose();
	}

	void clearSelection() {
		fViewer.getControl().setRedraw(false);
		fViewer.setSelection(new StructuredSelection());
		fViewer.getControl().setRedraw(true);
	}

}
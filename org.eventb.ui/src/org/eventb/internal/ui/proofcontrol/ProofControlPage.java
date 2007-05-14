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

package org.eventb.internal.ui.proofcontrol;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Collection;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IMenuListener;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.IStatusLineManager;
import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.dialogs.ProgressMonitorDialog;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.util.IPropertyChangeListener;
import org.eclipse.jface.util.PropertyChangeEvent;
import org.eclipse.swt.SWT;
import org.eclipse.swt.dnd.DND;
import org.eclipse.swt.dnd.DropTarget;
import org.eclipse.swt.dnd.DropTargetEvent;
import org.eclipse.swt.dnd.DropTargetListener;
import org.eclipse.swt.dnd.FileTransfer;
import org.eclipse.swt.dnd.TextTransfer;
import org.eclipse.swt.dnd.Transfer;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.CoolBar;
import org.eclipse.swt.widgets.CoolItem;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.widgets.ToolBar;
import org.eclipse.swt.widgets.ToolItem;
import org.eclipse.ui.IActionBars;
import org.eclipse.ui.IWorkbenchActionConstants;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.ImageHyperlink;
import org.eclipse.ui.forms.widgets.ScrolledForm;
import org.eclipse.ui.part.Page;
import org.eventb.core.EventBPlugin;
import org.eventb.core.pm.IProofState;
import org.eventb.core.pm.IProofStateDelta;
import org.eventb.core.pm.IProvingMode;
import org.eventb.core.pm.IUserSupport;
import org.eventb.core.pm.IUserSupportDelta;
import org.eventb.core.pm.IUserSupportInformation;
import org.eventb.core.pm.IUserSupportManager;
import org.eventb.core.pm.IUserSupportManagerChangedListener;
import org.eventb.core.pm.IUserSupportManagerDelta;
import org.eventb.core.seqprover.IProofTreeNode;
import org.eventb.core.seqprover.ITactic;
import org.eventb.internal.ui.EventBControl;
import org.eventb.internal.ui.EventBImage;
import org.eventb.internal.ui.EventBMath;
import org.eventb.internal.ui.IEventBInputText;
import org.eventb.internal.ui.UIUtils;
import org.eventb.internal.ui.preferences.PreferenceConstants;
import org.eventb.internal.ui.prover.ProverUI;
import org.eventb.internal.ui.prover.ProverUIUtils;
import org.eventb.internal.ui.prover.TacticUIRegistry;
import org.eventb.ui.EventBUIPlugin;
import org.eventb.ui.IEventBSharedImages;
import org.eventb.ui.prover.IProofCommand;
import org.eventb.ui.prover.ITacticProvider;
import org.rodinp.core.RodinDBException;

/**
 * @author htson
 *         <p>
 *         This class is an implementation of a Proof Control 'page'.
 */
public class ProofControlPage extends Page implements IProofControlPage,
		IUserSupportManagerChangedListener, IPropertyChangeListener {

	boolean share;

	Action expertMode;

	IEventBInputText textInput;

	ProverUI editor;

//	private IEventBFormText formTextInformation;

	ScrolledForm scrolledForm;

	private Collection<GlobalTacticDropdownToolItem> dropdownItems;

	private Collection<GlobalTacticToolItem> toolItems;

	Combo historyCombo;

	private EventBControl history;

	private Composite pgComp;

	String currentInput = "";
	
	private Composite midComp;

	ImageHyperlink smiley; 
	
	/**
	 * Constructor
	 * <p>
	 * 
	 * @param editor
	 *            the Prover UI editor associated with this Proof Control page.
	 */
	public ProofControlPage(ProverUI editor) {
		this.editor = editor;
		EventBPlugin.getDefault().getUserSupportManager().addChangeListener(
				this);
		IPreferenceStore store = EventBUIPlugin.getDefault()
				.getPreferenceStore();
		store.addPropertyChangeListener(this);
	}

	/**
	 * Apply a tactic with a progress monitor (providing cancel button).
	 * <p>
	 * 
	 * @param op
	 *            a runnable with progress monitor.
	 */
	private static void applyTacticWithProgress(IRunnableWithProgress op) {
		final Shell shell = Display.getDefault().getActiveShell();
		ProgressMonitorDialog dialog = new ProgressMonitorDialog(shell);
		try {
			dialog.run(true, true, op);
		} catch (InterruptedException exception) {
			if (ProofControlUtils.DEBUG)
				ProofControlUtils.debug("Interrupt");
			return;
		} catch (InvocationTargetException exception) {
			final Throwable realException = exception.getTargetException();
			if (ProofControlUtils.DEBUG)
				ProofControlUtils.debug("Interrupt");
			realException.printStackTrace();
			final String message = realException.getMessage();
			MessageDialog.openError(shell, "Unexpected Error", message);
			return;
		}
	}

	@Override
	public void dispose() {
		// Deregister with the UserSupport
		EventBPlugin.getDefault().getUserSupportManager().removeChangeListener(
				this);
		IPreferenceStore store = EventBUIPlugin.getDefault()
				.getPreferenceStore();
		store.removePropertyChangeListener(this);
//		formTextInformation.dispose();
		textInput.dispose();
		history.dispose();
		scrolledForm.dispose();
		super.dispose();
	}

	CoolItem createItem(CoolBar coolBar, String toolbarID) {
		final ToolBar toolBar = new ToolBar(coolBar, SWT.FLAT);

		final TacticUIRegistry registry = TacticUIRegistry.getDefault();
		Collection<String> dropdownIDs = registry
				.getToolbarDropdowns(toolbarID);

		for (String dropdownID : dropdownIDs) {
			Collection<String> tacticIDs = registry
					.getDropdownTactics(dropdownID);

			if (tacticIDs.size() != 0) {
				ToolItem item = new ToolItem(toolBar, SWT.DROP_DOWN);
				// item.setText(itemCount++ + "");

				GlobalTacticDropdownToolItem dropdownItem = new GlobalTacticDropdownToolItem(
						item, dropdownID) {
					@Override
					public void apply(final String tacticID) {
						try {
							if (ProofControlUtils.DEBUG)
								ProofControlUtils.debug("File "
										+ ProofControlPage.this.editor
												.getRodinInput()
												.getElementName());
							Text textWidget = textInput.getTextWidget();
							final IUserSupport userSupport = editor
									.getUserSupport();
							boolean interruptable = registry.isInterruptable(
									tacticID, TacticUIRegistry.TARGET_GLOBAL);
							ITacticProvider provider = registry
									.getTacticProvider(tacticID);
							if (provider != null) {
								applyTacticProvider(provider, userSupport,
										interruptable);
							} else {
								IProofCommand command = registry
										.getProofCommand(tacticID,
												TacticUIRegistry.TARGET_GLOBAL);
								if (command != null) {
									applyGlobalExpertTactic(command,
											userSupport, interruptable);
								} else {
									return;
								}
							}
							if (!currentInput.equals("")) {
								historyCombo.add(currentInput, 0);
							}
							if (textWidget.getText() != "") {
								textWidget.setText("");
							}
							currentInput = "";
						} catch (RodinDBException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}
				};

				dropdownItems.add(dropdownItem);

				for (String tactic : tacticIDs) {
					if (ProofControlUtils.DEBUG)
						ProofControlUtils.debug("Tactic: " + tactic);
					dropdownItem.addTactic(tactic);
				}
				if (ProofControlUtils.DEBUG)
					ProofControlUtils.debug("----------------------------");

			}

		}

		Collection<String> tacticIDs = registry.getToolbarTactics(toolbarID);

		for (final String tacticID : tacticIDs) {
			ToolItem item = new ToolItem(toolBar, SWT.PUSH);
			item.setImage(registry.getIcon(tacticID));
			item.setToolTipText(registry.getTip(tacticID));

			final GlobalTacticToolItem globalTacticToolItem = new GlobalTacticToolItem(
					item, tacticID, registry.isInterruptable(tacticID,
							TacticUIRegistry.TARGET_GLOBAL));

			item.addSelectionListener(new SelectionAdapter() {

				/*
				 * (non-Javadoc)
				 * 
				 * @see org.eclipse.swt.events.SelectionAdapter#widgetSelected(org.eclipse.swt.events.SelectionEvent)
				 */
				@Override
				public void widgetSelected(SelectionEvent e) {
					if (ProofControlUtils.DEBUG)
						ProofControlUtils.debug("File "
								+ ProofControlPage.this.editor.getRodinInput()
										.getElementName());
					Text textWidget = textInput.getTextWidget();
					try {

						final IUserSupport userSupport = editor
								.getUserSupport();
						boolean interruptable = registry.isInterruptable(
								tacticID, TacticUIRegistry.TARGET_GLOBAL);
						ITacticProvider provider = TacticUIRegistry
								.getDefault().getTacticProvider(tacticID);
						if (provider != null) {
							applyTacticProvider(provider, userSupport,
									interruptable);
						} else {
							IProofCommand command = TacticUIRegistry
									.getDefault().getProofCommand(tacticID,
											TacticUIRegistry.TARGET_GLOBAL);
							if (command != null) {
								applyGlobalExpertTactic(command, userSupport,
										interruptable);
							} else {
								return;
							}
						}
					} catch (RodinDBException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}
					if (!currentInput.equals("")) {
						historyCombo.add(currentInput, 0);
					}
					if (textWidget.getText() != "") {
						textWidget.setText("");
					}
				}
			});
			toolItems.add(globalTacticToolItem);
		}

		toolBar.pack();
		Point size = toolBar.getSize();
		CoolItem item = new CoolItem(coolBar, SWT.NONE);
		item.setControl(toolBar);
		Point preferred = item.computeSize(size.x, size.y);
		item.setPreferredSize(preferred);
		// Allow data to be copied or moved to the drop target
		int operations = DND.DROP_MOVE | DND.DROP_COPY | DND.DROP_DEFAULT;
		DropTarget target = new DropTarget(toolBar, operations);

		final TextTransfer textTransfer = TextTransfer.getInstance();
		final FileTransfer fileTransfer = FileTransfer.getInstance();
		Transfer[] types = new Transfer[] { fileTransfer, textTransfer };
		target.setTransfer(types);

		target.addDropListener(new ToolBarDropTargetListener(toolBar,
				textTransfer, fileTransfer));

		return item;
	}

	// Applies a global tactic to the current proof tree node.
	void applyGlobalExpertTactic(final IProofCommand command,
			final IUserSupport userSupport, final boolean interruptable)
			throws RodinDBException {

		final String[] inputs = { currentInput };
		if (interruptable) {
			applyTacticWithProgress(new IRunnableWithProgress() {
				public void run(IProgressMonitor pm)
						throws InvocationTargetException {
					try {
						pm.beginTask("Proving", IProgressMonitor.UNKNOWN);
						command.apply(userSupport, null, inputs, pm);
					} catch (RodinDBException e) {
						e.printStackTrace();
						UIUtils.log(e, "Error applying proof command");
					} finally {
						pm.done();
					}
				}
			});

		} else {
			command.apply(userSupport, null, inputs, null);
		}
	}

	// Applies a global tactic to the current proof tree node.
	void applyTacticProvider(ITacticProvider provider,
			final IUserSupport userSupport, boolean interruptable) {

		IProofTreeNode node = userSupport.getCurrentPO().getCurrentNode();
		String[] inputs = { currentInput };
		final ITactic tactic = provider.getTactic(node, null, null, inputs);
		if (interruptable) {
			applyTacticWithProgress(new IRunnableWithProgress() {
				public void run(IProgressMonitor pm)
						throws InvocationTargetException {
					try {
						pm.beginTask("Proving", IProgressMonitor.UNKNOWN);
						try {
							userSupport.applyTactic(tactic, true, pm);
						} catch (RodinDBException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					} finally {
						pm.done();
					}
				}
			});
		} else {
			try {
				userSupport.applyTactic(tactic, true, null);
			} catch (RodinDBException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	private class ToolBarDropTargetListener implements DropTargetListener {

		ToolBar toolBar;

		TextTransfer textTransfer;

		FileTransfer fileTransfer;

		ToolItem currentItem;

		public ToolBarDropTargetListener(ToolBar toolBar,
				TextTransfer textTransfer, FileTransfer fileTransfer) {
			this.toolBar = toolBar;
			this.textTransfer = textTransfer;
			this.fileTransfer = fileTransfer;
		}

		private ToolItem findCurrentItem(Point pt) {
			if (ProofControlUtils.DEBUG)
				ProofControlUtils.debug("Drop target location " + pt.x + ", "
						+ pt.y);

			Point loc = toolBar.getLocation();

			Composite parent = toolBar.getParent();
			while (parent != null) {
				Point point = parent.getLocation();
				loc.x = loc.x + point.x;
				loc.y = loc.y + point.y;
				parent = parent.getParent();
			}
			if (ProofControlUtils.DEBUG)
				ProofControlUtils.debug("Location Toolbar " + loc);

			ToolItem[] children = toolBar.getItems();
			ToolItem found = null;
			for (ToolItem child : children) {
				Rectangle rec = child.getBounds();
				// Rectangle area = toolBar.getLocation();
				if (ProofControlUtils.DEBUG) {
					ProofControlUtils.debug("Tool Item " + child);
					ProofControlUtils.debug("Rec: " + rec);
				}
				// ProofControl.debug("Area: " + area);
				if (loc.x + rec.x <= pt.x && pt.x <= loc.x + rec.x + rec.width) {
					found = child;
					break;
				}
			}

			return found;
		}

		public void dragEnter(DropTargetEvent event) {

			Point pt = new Point(event.x, event.y);
			ToolItem item = findCurrentItem(pt);

			if (item != currentItem) {
				currentItem = item;
			}
			if (item != null && !item.isEnabled())
				item.setEnabled(true);

			if (event.detail == DND.DROP_DEFAULT) {
				if ((event.operations & DND.DROP_COPY) != 0) {
					event.detail = DND.DROP_COPY;
				} else {
					event.detail = DND.DROP_NONE;
				}
			}
			// will accept text but prefer to have files dropped
			for (int i = 0; i < event.dataTypes.length; i++) {
				if (fileTransfer.isSupportedType(event.dataTypes[i])) {
					event.currentDataType = event.dataTypes[i];
					// files should only be copied
					if (event.detail != DND.DROP_COPY) {
						event.detail = DND.DROP_NONE;
					}
					break;
				}
			}
		}

		public void dragOver(DropTargetEvent event) {
			// Determine which button are there

			Point pt = new Point(event.x, event.y);
			ToolItem item = findCurrentItem(pt);

			if (item != currentItem) {
				currentItem = item;
			}
			if (item != null && !item.isEnabled())
				item.setEnabled(true);

			event.feedback = DND.FEEDBACK_SELECT | DND.FEEDBACK_SCROLL;
			if (textTransfer.isSupportedType(event.currentDataType)) {
				// NOTE: on unsupported platforms this will return null
				Object o = textTransfer.nativeToJava(event.currentDataType);
				String t = (String) o;
				if (t != null)
					if (ProofControlUtils.DEBUG)
						ProofControlUtils.debug(t);
			}

		}

		public void dragOperationChanged(DropTargetEvent event) {
			if (event.detail == DND.DROP_DEFAULT) {
				if ((event.operations & DND.DROP_COPY) != 0) {
					event.detail = DND.DROP_COPY;
				} else {
					event.detail = DND.DROP_NONE;
				}
			}
			// allow text to be moved but files should only be copied
			if (fileTransfer.isSupportedType(event.currentDataType)) {
				if (event.detail != DND.DROP_COPY) {
					event.detail = DND.DROP_NONE;
				}
			}
		}

		public void dragLeave(DropTargetEvent event) {
			updateToolItems(editor.getUserSupport());
		}

		public void dropAccept(DropTargetEvent event) {
			// Do nothing
		}

		public void drop(DropTargetEvent event) {
			if (textTransfer.isSupportedType(event.currentDataType)) {
				if (ProofControlUtils.DEBUG)
					ProofControlUtils.debug("Drop Text: " + event.data);
				currentInput = (String) event.data;
				currentItem.notifyListeners(SWT.Selection, new Event());
				// String text = (String) event.data;
				// TableItem item = new TableItem(dropTable, SWT.NONE);
				// item.setText(text);
			}
			if (fileTransfer.isSupportedType(event.currentDataType)) {
				String[] files = (String[]) event.data;
				for (int i = 0; i < files.length; i++) {
					// TableItem item = new TableItem(dropTable, SWT.NONE);
					// item.setText(files[i]);
				}
			}
		}
	}

	/**
	 * This is a callback that will allow us to create the viewer and initialize
	 * it.
	 * <p>
	 * 
	 * 
	 * @see org.eclipse.ui.part.IPage#createControl(org.eclipse.swt.widgets.Composite)
	 */
	@Override
	public void createControl(Composite parent) {
		FormToolkit toolkit = new FormToolkit(parent.getDisplay());

		pgComp = toolkit.createComposite(parent, SWT.NULL);
		pgComp.setLayout(new FormLayout());

		CoolBar coolBar = new CoolBar(pgComp, SWT.FLAT);

		FormData coolData = new FormData();
		coolData.left = new FormAttachment(0);
		coolData.right = new FormAttachment(100);
		coolData.top = new FormAttachment(0);
		coolBar.setLayoutData(coolData);

		scrolledForm = toolkit.createScrolledForm(pgComp);
		FormData scrolledData = new FormData();
		scrolledData.left = new FormAttachment(0);
		scrolledData.right = new FormAttachment(100);
		scrolledData.top = new FormAttachment(coolBar);
		scrolledData.bottom = new FormAttachment(100);
		scrolledForm.setLayoutData(scrolledData);

		Composite body = scrolledForm.getBody();
		GridLayout gl = new GridLayout();
		gl.numColumns = 1;
		body.setLayout(gl);

		// Create toolbars
		dropdownItems = new ArrayList<GlobalTacticDropdownToolItem>();
		toolItems = new ArrayList<GlobalTacticToolItem>();

		Collection<String> toolbars = TacticUIRegistry.getDefault()
				.getToolbars();

		for (String toolbar : toolbars) {
			createItem(coolBar, toolbar);
		}

		midComp = toolkit.createComposite(body, SWT.NULL);
		midComp.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
		midComp.setBackground(scrolledForm.getBackground());
		gl = new GridLayout();
		gl.numColumns = 2;
		midComp.setLayout(gl);
		
		smiley = new ImageHyperlink(midComp, SWT.BOTTOM);
		smiley.setLayoutData(new GridData(SWT.FILL, SWT.FILL, false, false));
		smiley.setEnabled(false);
		smiley.setBackground(scrolledForm.getBackground());

		// A text field
		textInput = new EventBMath(toolkit.createText(midComp, "", SWT.MULTI));

		textInput.getTextWidget().addModifyListener(new ModifyListener() {

			public void modifyText(ModifyEvent e) {
				if (ProofControlUtils.DEBUG)
					ProofControlUtils.debug("File: "
							+ ProofControlPage.this.editor.getRodinInput()
									.getElementName());
			}

		});

		GridData gd = new GridData(SWT.FILL, SWT.FILL, true, true);
		gd.heightHint = textInput.getTextWidget().getLineHeight() * 2;
		gd.widthHint = 200;
		textInput.getTextWidget().setLayoutData(gd);
		textInput.getTextWidget().addModifyListener(new ModifyListener() {
			public void modifyText(ModifyEvent e) {
				currentInput = textInput.getTextWidget().getText();
				updateToolItems(editor.getUserSupport());
			}
		});

		toolkit.paintBordersFor(midComp);
		
		historyCombo = new Combo(body, SWT.DROP_DOWN | SWT.READ_ONLY);
		historyCombo.addSelectionListener(new SelectionListener() {

			public void widgetSelected(SelectionEvent e) {
				textInput.getTextWidget().setText(historyCombo.getText());
			}

			public void widgetDefaultSelected(SelectionEvent e) {
				widgetSelected(e);
			}

		});
		gd = new GridData(SWT.FILL, SWT.FILL, true, false);
		historyCombo.setLayoutData(gd);
		history = new EventBControl(historyCombo);

//		formTextInformation = new EventBFormText(toolkit.createFormText(body,
//				true));
//		gd = new GridData();
//		gd.horizontalSpan = 2;
//		gd.minimumHeight = 20;
//		gd.heightHint = 20;
//		formTextInformation.getFormText().setLayoutData(gd);
//		setFormTextInformation("");

		scrolledForm.reflow(true);

		makeActions();
		hookContextMenu();
		contributeToActionBars();

		updateToolItems(editor.getUserSupport());
		updateSmiley();
		coolBar.pack();
		pgComp.setVisible(false);
	}

	void updateSmiley() {
		Image image;
		IProofState currentPO = editor.getUserSupport().getCurrentPO();
		try {
			if (currentPO == null || currentPO.isClosed()) {
				image = EventBImage.getImage(EventBImage.IMG_DISCHARGED_SMILEY);
			}
			else {
				image = EventBImage.getImage(EventBImage.IMG_PENDING_SMILEY);
			}
		} catch (RodinDBException e1) {
			image = EventBImage.getImage(EventBImage.IMG_PENDING_SMILEY);
		}
		smiley.setImage(image);
		midComp.pack();
	}

	/**
	 * Set the information (in the bottom of the page).
	 * <p>
	 * 
	 * @param information
	 *            the string (information from the UserSupport).
	 */
	void setStatusInformation(String information) {
		IStatusLineManager slManager = getSite().getActionBars()
				.getStatusLineManager();
		slManager.setMessage(information);
	}

	/**
	 * Setup the context menu.
	 */
	private void hookContextMenu() {
		MenuManager menuMgr = new MenuManager("#PopupMenu");
		menuMgr.setRemoveAllWhenShown(true);
		menuMgr.addMenuListener(new IMenuListener() {
			public void menuAboutToShow(IMenuManager manager) {
				ProofControlPage.this.fillContextMenu(manager);
			}
		});
		Menu menu = menuMgr.createContextMenu(this.getControl());
		this.getControl().setMenu(menu);
	}

	/**
	 * Setup the action bars
	 */
	private void contributeToActionBars() {
		IActionBars bars = getSite().getActionBars();
		fillLocalPullDown(bars.getMenuManager());
		fillLocalToolBar(bars.getToolBarManager());
	}

	/**
	 * Fill the local pull down.
	 * <p>
	 * 
	 * @param manager
	 *            the menu manager
	 */
	private void fillLocalPullDown(IMenuManager manager) {
		manager.add(expertMode);
		manager.add(new Separator());
	}

	/**
	 * Fill the context menu.
	 * <p>
	 * 
	 * @param manager
	 *            the menu manager
	 */
	void fillContextMenu(IMenuManager manager) {
		manager.add(expertMode);
		// Other plug-ins can contribute there actions here
		manager.add(new Separator(IWorkbenchActionConstants.MB_ADDITIONS));
	}

	/**
	 * Fill the local toolbar.
	 * <p>
	 * 
	 * @param manager
	 *            the toolbar manager
	 */
	private void fillLocalToolBar(IToolBarManager manager) {
		manager.add(expertMode);
	}

	/**
	 * Creat the actions used in this page.
	 */
	private void makeActions() {
		final IPreferenceStore store = EventBUIPlugin.getDefault()
				.getPreferenceStore();

		expertMode = new Action("Expert mode switch", SWT.CHECK) {
			@Override
			public void run() {
				boolean checked = expertMode.isChecked();
				store.setValue(PreferenceConstants.P_EXPERTMODE,
						checked);
			}
		};
		boolean b = store
						.getBoolean(PreferenceConstants.P_EXPERTMODE);
		expertMode.setChecked(b);
		IUserSupportManager usManager = EventBPlugin.getDefault()
				.getUserSupportManager();
		IProvingMode provingMode = usManager.getProvingMode();
		provingMode.setExpertMode(b);
		expertMode.setToolTipText("Expert mode switch");

		expertMode.setImageDescriptor(EventBImage
				.getImageDescriptor(IEventBSharedImages.IMG_EXPERT_MODE_PATH));
	}

	/**
	 * Passing the focus request to the button bar.
	 * <p>
	 * 
	 * @see org.eclipse.ui.part.IPage#setFocus()
	 */
	@Override
	public void setFocus() {
		pgComp.setFocus();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.ui.part.Page#getControl()
	 */
	@Override
	public Control getControl() {
		return pgComp;
	}

	/**
	 * Update the status of the toolbar items.
	 */
	void updateToolItems(IUserSupport userSupport) {
		for (GlobalTacticDropdownToolItem item : dropdownItems) {
			item.updateStatus(userSupport, textInput.getTextWidget().getText());
		}

		for (GlobalTacticToolItem item : toolItems) {
			item.updateStatus(userSupport, textInput.getTextWidget().getText());
		}

		return;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eventb.core.pm.IProofStateChangedListener#proofStateChanged(org.eventb.core.pm.IProofStateDelta)
	 */
	public void userSupportManagerChanged(final IUserSupportManagerDelta delta) {

		// Do nothing if the form is disposed.
		if (scrolledForm.isDisposed())
			return;

		final IUserSupport userSupport = this.editor.getUserSupport();

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
			if (ProofControlUtils.DEBUG)
				ProofControlUtils
						.debug("Error: Delta said that the user Support is added");
			return; // Do nothing
		}

		Display display = EventBUIPlugin.getDefault().getWorkbench()
				.getDisplay();

		display.syncExec(new Runnable() {
			public void run() {
				// Handle the case where the user support has changed.
				if (kind == IUserSupportDelta.CHANGED) {
					int flags = affectedUserSupport.getFlags();

					// Set the information if it has been changed.
					if ((flags | IUserSupportDelta.F_INFORMATION) != 0) {
						setInformation(affectedUserSupport.getInformation());
					}

					if ((flags | IUserSupportDelta.F_CURRENT) != 0) {
						// The current proof state is changed, update the tool
						// items.
						updateToolItems(editor.getUserSupport());
						updateSmiley();
						scrolledForm.reflow(true);
					} else if ((flags | IUserSupportDelta.F_STATE) != 0) {
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
								if (ProofControlUtils.DEBUG)
									ProofControlUtils
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

								if ((psFlags | IProofStateDelta.F_NODE) != 0) {
									// Update the items if the current node has
									// been changed.
									updateToolItems(editor.getUserSupport());
									if ((psFlags | IProofStateDelta.F_PROOFTREE) != 0) {
										updateSmiley();									
									}
								}
								else if ((psFlags | IProofStateDelta.F_PROOFTREE) != 0) {
									// Update the items if the current node has
									// been changed.
									updateToolItems(editor.getUserSupport());
									updateSmiley();
								}
								scrolledForm.reflow(true);
							}
						}
					}
				}

				scrolledForm.reflow(true);
			}
		});

	}

	void setInformation(final IUserSupportInformation[] information) {
		if (ProofControlUtils.DEBUG) {
			ProofControlUtils.debug("********** MESSAGE *********");
			for (IUserSupportInformation info : information) {
				ProofControlUtils.debug(info.toString());
			}
			ProofControlUtils.debug("****************************");
		}

		int size = information.length;
		if (size == 0) {
			setStatusInformation("");
			return;
		}

		// Trying to print the latest message with highest priority.
		for (int priority = IUserSupportInformation.MAX_PRIORITY; IUserSupportInformation.MIN_PRIORITY <= priority; --priority) {
			for (int i = information.length - 1; 0 <= i; --i) {
				if (information[i].getPriority() == priority) {
					setStatusInformation(information[i].getInformation().toString());
					return;
				}
			}
		}
		setStatusInformation("");
	}

	public void propertyChange(PropertyChangeEvent event) {
		if (event.getProperty().equals(
				PreferenceConstants.P_EXPERTMODE)) {
			Object newValue = event.getNewValue();
			assert newValue instanceof Boolean || newValue instanceof String;
			if (newValue instanceof String) {
				boolean b = ((String) newValue)
										.compareToIgnoreCase("true") == 0;
				expertMode.setChecked(b);
				IUserSupportManager usManager = EventBPlugin.getDefault()
				.getUserSupportManager();
				IProvingMode provingMode = usManager.getProvingMode();
				provingMode.setExpertMode(b);
			} else {
				Boolean b = (Boolean) newValue;
				expertMode.setChecked(b);
				IUserSupportManager usManager = EventBPlugin.getDefault()
					.getUserSupportManager();
				IProvingMode provingMode = usManager.getProvingMode();
				provingMode.setExpertMode(b);
			}
		}

	}
}

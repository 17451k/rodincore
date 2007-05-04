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

import java.util.ArrayList;
import java.util.Collection;

import org.eclipse.core.resources.IWorkspaceRunnable;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.eventb.core.IAxiom;
import org.eventb.core.IConstant;
import org.eventb.eventBKeyboard.Text2EventBMathTranslator;
import org.eventb.internal.ui.EventBMath;
import org.eventb.internal.ui.EventBText;
import org.eventb.internal.ui.IEventBInputText;
import org.eventb.internal.ui.Pair;
import org.eventb.internal.ui.UIUtils;
import org.eventb.internal.ui.eventbeditor.actions.PrefixAxmName;
import org.eventb.internal.ui.eventbeditor.actions.PrefixCstName;
import org.eventb.ui.eventbeditor.IEventBEditor;
import org.rodinp.core.RodinCore;
import org.rodinp.core.RodinDBException;

/**
 * @author htson
 *         <p>
 *         This class extends the Dialog class and provides an input dialog for
 *         creating a new variable along with its type axiom
 */
public class IntelligentNewConstantInputDialog extends EventBInputDialog {

	private int axmIndex;

	String identifier;

	private IEventBInputText identifierText;

	Collection<String> axmLabels;
	
	Collection<String> axmSubs;
	
	private Collection<Pair> axiomPairTexts;

	IEventBEditor editor;

	private Composite composite;
	
	/**
	 * Constructor.
	 * <p>
	 * 
	 * @param parentShell
	 *            the parent shell of the dialog
	 * @param title
	 *            the title of the dialog
	 */
	public IntelligentNewConstantInputDialog(IEventBEditor editor,
			Shell parentShell, String title) {
		super(parentShell, title);
		this.editor = editor;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.jface.dialogs.Dialog#createButtonsForButtonBar(org.eclipse.swt.widgets.Composite)
	 */
	@Override
	protected void createButtonsForButtonBar(Composite parent) {
		createButton(parent, IDialogConstants.RETRY_ID, "&Add", false);

		createButton(parent, IDialogConstants.YES_ID, "&More Axm.", false);

		createButton(parent, IDialogConstants.OK_ID, IDialogConstants.OK_LABEL,
				true);

		createButton(parent, IDialogConstants.CANCEL_ID,
				IDialogConstants.CANCEL_LABEL, false);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.jface.dialogs.Dialog#createDialogArea(org.eclipse.swt.widgets.Composite)
	 */
	@Override
	protected void createContents() {
		Composite body = scrolledForm.getBody();
		body.setLayout(new FillLayout());
		createDialogContents(body);

	}

	private void createDialogContents(Composite parent) {
		composite = toolkit.createComposite(parent);
		if (EventBEditorUtils.DEBUG)
			composite.setBackground(composite.getDisplay().getSystemColor(
					SWT.COLOR_CYAN));

		axiomPairTexts = new ArrayList<Pair>();
		GridLayout layout = new GridLayout();
		layout.numColumns = 3;
		layout.verticalSpacing = 10;
		layout.horizontalSpacing = 10;
		composite.setLayout(layout);
		GridData gd = new GridData(SWT.FILL, SWT.FILL, true, true);
		scrolledForm.setLayoutData(gd);

		Label label = toolkit.createLabel(composite, "Name");
		label.setLayoutData(new GridData(SWT.FILL, SWT.FILL, false, false));

		identifierText = new EventBText(toolkit.createText(composite, ""));
		gd = new GridData(SWT.FILL, SWT.NONE, true, false);
		gd.horizontalSpan = 2;
		gd.widthHint = 200;
		Text textWidget = identifierText.getTextWidget();
		textWidget.setLayoutData(gd);
		textWidget.addModifyListener(new DirtyStateListener());

		label = toolkit.createLabel(composite, "Axiom");
		label.setLayoutData(new GridData(SWT.FILL, SWT.FILL, false, false));

		String axmPrefix = UIUtils.getPrefix(editor,
				IAxiom.ELEMENT_TYPE, PrefixAxmName.DEFAULT_PREFIX);
		try {
			axmIndex = UIUtils.getFreeElementLabelIndex(editor, editor.getRodinInput(),
					IAxiom.ELEMENT_TYPE, axmPrefix);
		} catch (RodinDBException e1) {
			// TODO Auto-generated catch block
			axmIndex = 1;
			e1.printStackTrace();
		}

		IEventBInputText axiomNameText = new EventBText(toolkit.createText(
				composite, axmPrefix + axmIndex));

		gd = new GridData(SWT.FILL, SWT.NONE, false, false);
		gd.widthHint = 50;
		axiomNameText.getTextWidget().setLayoutData(gd);
		axiomNameText.getTextWidget().addModifyListener(
				new DirtyStateListener());

		IEventBInputText axiomPredicateText = new EventBMath(toolkit
				.createText(composite, ""));
		gd = new GridData(SWT.FILL, SWT.NONE, true, false);
		gd.widthHint = 150;
		axiomPredicateText.getTextWidget().setLayoutData(gd);
		axiomPredicateText.getTextWidget().addModifyListener(
				new DirtyStateListener());
		textWidget.addModifyListener(
				new GuardListener(axiomPredicateText.getTextWidget()));

		axiomPairTexts.add(new Pair<IEventBInputText, IEventBInputText>(
				axiomNameText, axiomPredicateText));

		String cstLabel = "defaultLabel";
		try {
			cstLabel = UIUtils.getFreeElementIdentifier(editor,
					editor.getRodinInput(), IConstant.ELEMENT_TYPE,
					PrefixCstName.DEFAULT_PREFIX);
		} catch (RodinDBException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		textWidget.setText(cstLabel);
		textWidget.selectAll();
		textWidget.setFocus();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.jface.dialogs.Dialog#buttonPressed(int)
	 */
	@Override
	protected void buttonPressed(int buttonId) {
		if (buttonId == IDialogConstants.CANCEL_ID) {
			identifier = null;
			axmLabels = null;
			axmSubs = null;
		} else if (buttonId == IDialogConstants.YES_ID) {
			Label label = toolkit.createLabel(composite, "Axiom");
			GridData gd = new GridData(SWT.FILL, SWT.FILL, false, false);
			label.setLayoutData(gd);
			String axmPrefix = UIUtils.getPrefix(editor,
					IAxiom.ELEMENT_TYPE,
					PrefixAxmName.DEFAULT_PREFIX);

			try {
				axmIndex = UIUtils.getFreeElementLabelIndex(editor, editor
						.getRodinInput(), IAxiom.ELEMENT_TYPE, axmPrefix,
						axmIndex + 1);
			} catch (RodinDBException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			IEventBInputText axiomNameText = new EventBText(toolkit.createText(
					composite, axmPrefix + axmIndex));
			gd = new GridData(SWT.FILL, SWT.NONE, false, false);
			axiomNameText.getTextWidget().setLayoutData(gd);
			axiomNameText.getTextWidget().addModifyListener(
					new DirtyStateListener());

			IEventBInputText axiomPredicateText = new EventBMath(toolkit
					.createText(composite, ""));
			gd = new GridData(SWT.FILL, SWT.NONE, true, false);
			axiomPredicateText.getTextWidget().setLayoutData(gd);
			axiomPredicateText.getTextWidget().addModifyListener(
					new DirtyStateListener());

			axiomPairTexts.add(new Pair<IEventBInputText, IEventBInputText>(
					axiomNameText, axiomPredicateText));

			updateSize();
		} else if (buttonId == IDialogConstants.OK_ID) {
			setFieldValues();
		} else if (buttonId == IDialogConstants.RETRY_ID) {
			setFieldValues();
			addValues();
			initialise();
		}
		super.buttonPressed(buttonId);
	}

	private void addValues() {
		try {
			RodinCore.run(new IWorkspaceRunnable() {

				public void run(IProgressMonitor monitor) throws CoreException {
					EventBEditorUtils
							.createNewConstant(editor, identifier, monitor);

					EventBEditorUtils.createNewAxioms(editor, axmLabels
							.toArray(new String[axmLabels.size()]), axmSubs
							.toArray(new String[axmSubs.size()]), monitor);
				}

			}, new NullProgressMonitor());
		} catch (CoreException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private void initialise() {
		clearDirtyTexts();
		composite.dispose();
		createDialogContents(scrolledForm.getBody());
		scrolledForm.reflow(true);
	}

	private void setFieldValues() {
		identifier = identifierText.getTextWidget().getText();

		axmLabels = new ArrayList<String>();
		axmSubs = new ArrayList<String>();
		for (Pair pair : axiomPairTexts) {
			IEventBInputText axiomPredicateText = (IEventBInputText) pair
					.getSecond();
			IEventBInputText axiomNameText = (IEventBInputText) pair
					.getFirst();
			if (dirtyTexts.contains(axiomPredicateText.getTextWidget())) {
				String axmName = axiomNameText.getTextWidget().getText();
				String sub = Text2EventBMathTranslator
						.translate(axiomPredicateText.getTextWidget()
								.getText());
				axmLabels.add(axmName);
				axmSubs.add(sub);
			}
		}
	}

	/**
	 * Get the variable name.
	 * <p>
	 * 
	 * @return the variable name as input by the user
	 */
	public String getIdentifier() {
		return identifier;
	}

	@Override
	public boolean close() {
		identifierText.dispose();
		for (Pair pair : axiomPairTexts) {
			IEventBInputText axiomPredicateText = (IEventBInputText) pair
					.getSecond();
			IEventBInputText axiomNameText = (IEventBInputText) pair.getFirst();
			axiomNameText.dispose();
			axiomPredicateText.dispose();
		}
		return super.close();
	}

	public String[] getAxiomNames() {
		// TODO Auto-generated method stub
		return null;
	}

	public String[] getAxiomSubtitutions() {
		// TODO Auto-generated method stub
		return null;
	}

}

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

import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eventb.core.IInvariant;
import org.eventb.internal.ui.EventBMath;
import org.eventb.internal.ui.EventBText;
import org.eventb.internal.ui.IEventBInputText;
import org.eventb.internal.ui.UIUtils;
import org.eventb.ui.eventbeditor.IEventBEditor;
import org.rodinp.core.RodinDBException;

/**
 * @author htson
 *         <p>
 *         This class extends the Dialog class and provides an input dialog for
 *         creating a new variable along with its type invariant and
 *         initilisation.
 */
public class IntelligentNewVariableInputDialog extends EventBInputDialog {

	private String defaultName;

	private String invPrefix;

	private int invIndex;

	private String defaultInitName;

	private String name;

	private Collection<Pair> invariants;

	private String initName;

	private String initSubstitution;

	private IEventBInputText nameText;

	private Collection<Pair> invariantPairTexts;

	private IEventBInputText initNameText;

	private IEventBInputText initSubstitutionText;

	private IEventBEditor editor;

	/**
	 * Constructor.
	 * <p>
	 * 
	 * @param parentShell
	 *            the parent shell of the dialog
	 * @param title
	 *            the title of the dialog
	 * @param defaultName
	 *            the default variable name
	 * @param defaultInitName
	 *            the default init name
	 */
	public IntelligentNewVariableInputDialog(IEventBEditor editor,
			Shell parentShell, String title, String defaultName,
			String invPrefix, int invIndex, String defaultInitName) {
		super(parentShell, title);
		this.editor = editor;
		this.defaultName = defaultName;
		this.invIndex = invIndex;
		this.invPrefix = invPrefix;
		this.defaultInitName = defaultInitName;
		invariantPairTexts = new ArrayList<Pair>();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.jface.dialogs.Dialog#createButtonsForButtonBar(org.eclipse.swt.widgets.Composite)
	 */
	@Override
	protected void createButtonsForButtonBar(Composite parent) {
		createButton(parent, IDialogConstants.YES_ID, "&More Inv.", true);

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

		GridLayout layout = new GridLayout();
		layout.numColumns = 3;
		layout.verticalSpacing = 10;
		layout.horizontalSpacing = 10;
		body.setLayout(layout);
		GridData gd = new GridData(SWT.FILL, SWT.FILL, true, true);
		scrolledForm.setLayoutData(gd);

		Label label = toolkit.createLabel(body, "Name");
		label.setLayoutData(new GridData(SWT.FILL, SWT.FILL, false, false));

		nameText = new EventBText(toolkit.createText(body, ""));
		gd = new GridData(SWT.FILL, SWT.NONE, true, false);
		gd.horizontalSpan = 2;
		gd.widthHint = 200;
		nameText.getTextWidget().setLayoutData(gd);
		nameText.getTextWidget().addModifyListener(new DirtyStateListener());

		label = toolkit.createLabel(body, "Initialisation");
		label.setLayoutData(new GridData(SWT.FILL, SWT.FILL, false, false));

		initNameText = new EventBText(toolkit.createText(body, defaultInitName));
		gd = new GridData(SWT.FILL, SWT.NONE, false, false);
		gd.widthHint = 50;
		initNameText.getTextWidget().setLayoutData(gd);
		initNameText.getTextWidget().addModifyListener(new DirtyStateListener());

		initSubstitutionText = new EventBMath(toolkit.createText(body, ""));
		gd = new GridData(SWT.FILL, SWT.NONE, true, false);
		gd.widthHint = 150;
		initSubstitutionText.getTextWidget().setLayoutData(gd);
		initSubstitutionText.getTextWidget().addModifyListener(
				new DirtyStateListener());
		nameText.getTextWidget().addModifyListener(
				new ActionListener(initSubstitutionText.getTextWidget()));

		label = toolkit.createLabel(body, "Invariant");
		label.setLayoutData(new GridData(SWT.FILL, SWT.FILL, false, false));

		IEventBInputText invariantNameText = new EventBText(toolkit.createText(
				body, invPrefix + invIndex));

		gd = new GridData(SWT.FILL, SWT.NONE, false, false);
		gd.widthHint = 50;
		invariantNameText.getTextWidget().setLayoutData(gd);
		invariantNameText.getTextWidget().addModifyListener(
				new DirtyStateListener());

		IEventBInputText invariantPredicateText = new EventBMath(toolkit
				.createText(body, ""));
		gd = new GridData(SWT.FILL, SWT.NONE, true, false);
		gd.widthHint = 150;
		invariantPredicateText.getTextWidget().setLayoutData(gd);
		invariantPredicateText.getTextWidget().addModifyListener(
				new DirtyStateListener());
		nameText.getTextWidget().addModifyListener(
				new GuardListener(invariantPredicateText.getTextWidget()));

		invariantPairTexts.add(new Pair(invariantNameText,
				invariantPredicateText));

		nameText.getTextWidget().setText(defaultName);

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.jface.dialogs.Dialog#buttonPressed(int)
	 */
	@Override
	protected void buttonPressed(int buttonId) {
		if (buttonId == IDialogConstants.CANCEL_ID) {
			name = null;
			invariants = null;
			initName = null;
			initSubstitution = null;
		} else if (buttonId == IDialogConstants.YES_ID) {
			Composite body = scrolledForm.getBody();
			Label label = toolkit.createLabel(body, "Invariant");
			GridData gd = new GridData(SWT.FILL, SWT.FILL, false, false);
			label.setLayoutData(gd);

			try {
				invIndex = UIUtils.getFreeElementLabelIndex(editor, editor
						.getRodinInput(), IInvariant.ELEMENT_TYPE, invPrefix,
						invIndex + 1);
			} catch (RodinDBException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			IEventBInputText invariantNameText = new EventBText(toolkit
					.createText(body, invPrefix + invIndex));

			gd = new GridData(SWT.FILL, SWT.NONE, false, false);
			invariantNameText.getTextWidget().setLayoutData(gd);
			invariantNameText.getTextWidget().addModifyListener(
					new DirtyStateListener());

			IEventBInputText invariantPredicateText = new EventBMath(toolkit
					.createText(body, ""));
			gd = new GridData(SWT.FILL, SWT.NONE, true, false);
			invariantPredicateText.getTextWidget().setLayoutData(gd);
			invariantPredicateText.getTextWidget().addModifyListener(
					new DirtyStateListener());

			invariantPairTexts.add(new Pair(invariantNameText,
					invariantPredicateText));

			updateSize();
		} else if (buttonId == IDialogConstants.OK_ID) {
			if (dirtyTexts.contains(nameText.getTextWidget()))
				name = nameText.getTextWidget().getText();
			else
				name = null;
			invariants = new ArrayList<Pair>();
			for (Pair pair : invariantPairTexts) {
				IEventBInputText invariantPredicateText = (IEventBInputText) pair
						.getSecond();
				IEventBInputText invariantNameText = (IEventBInputText) pair
						.getFirst();
				if (dirtyTexts.contains(invariantPredicateText.getTextWidget())) {
					String invName = invariantNameText.getTextWidget().getText();
					String pred = invariantPredicateText.getTextWidget().getText();
					invariants.add(new Pair(invName, pred));
				}
			}
			if (dirtyTexts.contains(initSubstitutionText.getTextWidget())) {
				initName = initNameText.getTextWidget().getText();
				initSubstitution = initSubstitutionText.getTextWidget().getText();
			} else {
				initName = null;
				initSubstitution = null;
			}
		}
		super.buttonPressed(buttonId);
	}

	/**
	 * Get the variable name.
	 * <p>
	 * 
	 * @return the variable name as input by the user
	 */
	public String getName() {
		return name;
	}

	/**
	 * Get the invariant name.
	 * <p>
	 * 
	 * @return the invariant name as input by the user
	 */
	public Collection<Pair> getInvariants() {
		return invariants;
	}

	/**
	 * Get the initialisation action.
	 * <p>
	 * 
	 * @return the initialisation action as input by the user
	 */
	public String getInitSubstitution() {
		return initSubstitution;
	}

	public String getInitName() {
		return initName;
	}

	@Override
	public boolean close() {
		nameText.dispose();
		for (Pair pair : invariantPairTexts) {
			IEventBInputText invariantPredicateText = (IEventBInputText) pair
					.getSecond();
			IEventBInputText invariantNameText = (IEventBInputText) pair
					.getFirst();
			invariantNameText.dispose();
			invariantPredicateText.dispose();
		}
		initSubstitutionText.dispose();
		return super.close();
	}

}

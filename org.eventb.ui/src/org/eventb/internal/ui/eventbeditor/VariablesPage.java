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

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.forms.IManagedForm;
import org.eclipse.ui.forms.editor.FormEditor;
import org.eclipse.ui.forms.widgets.ExpandableComposite;
import org.eclipse.ui.forms.widgets.Section;
import org.rodinp.core.IRodinFile;

/**
 * @author htson
 * <p>
 * An implementation of the Event-B Form Page
 * for editing Variables (Rodin elements).
 */
public class VariablesPage
	extends NewEventBFormPage 
{
	
	// Title, tab title and ID of the page.
	public static final String PAGE_ID = "Variables"; //$NON-NLS-1$
	public static final String PAGE_TITLE = "Variables";
	public static final String PAGE_TAB_TITLE = "Variables";

	/**
	 * Contructor.
	 * <p>
	 * @param editor The form editor that holds the page 
	 */
	public VariablesPage(FormEditor editor) {
		super(editor, PAGE_ID, PAGE_TITLE, PAGE_TAB_TITLE);  //$NON-NLS-1$
	}

	/* (non-Javadoc)
	 * @see org.eventb.internal.ui.eventbeditor.NewEventBFormPage#createMasterSection(org.eclipse.ui.forms.IManagedForm, org.eclipse.swt.widgets.Composite, int, org.eventb.internal.ui.eventbeditor.EventBEditor)
	 */
	@Override
	protected NewEventBTablePartWithButtons createMasterSection(IManagedForm managedForm, Composite parent, int style, EventBEditor editor) {
		VariableMasterSection part = new VariableMasterSection(managedForm, parent, managedForm.getToolkit(), Section.TITLE_BAR | Section.DESCRIPTION, (EventBEditor) this.getEditor());
		GridData gd = new GridData(SWT.FILL, SWT.FILL, true, true);
		gd.heightHint = 200;
		gd.minimumHeight = 150;
		gd.widthHint = 150;
		part.getSection().setLayoutData(gd);
		return part;
	}



	/**
	 * Creating the Mirror sections within the page.
	 */
	protected void createMirrorSections(Composite body, IManagedForm managedForm) {
		IRodinFile rodinFile = ((EventBEditor) this.getEditor()).getRodinInput();
		
		GridData gd = new GridData(SWT.FILL, SWT.FILL, true, true);
		Composite composite = managedForm.getToolkit().createComposite(body);
		composite.setLayoutData(gd);
		GridLayout layout = new GridLayout();
		layout.numColumns = 2;
		composite.setLayout(layout);

		// Invariant mirror section.
		InvariantMirrorSection invariantMirrorSection = new InvariantMirrorSection(this, composite, ExpandableComposite.TITLE_BAR |Section.EXPANDED, rodinFile);
		managedForm.addPart(invariantMirrorSection);
		gd = new GridData(SWT.FILL, SWT.FILL, true, true);
		gd.heightHint = 200;
		gd.minimumHeight = 150;
		gd.widthHint = 150;
		invariantMirrorSection.getSection().setLayoutData(gd);
		
		// Theorem mirror section.
		TheoremMirrorSection theoremMirrorSection = new TheoremMirrorSection(this, composite, ExpandableComposite.TITLE_BAR |Section.EXPANDED, rodinFile);
		managedForm.addPart(theoremMirrorSection);
		gd = new GridData(SWT.FILL, SWT.FILL, true, true);
		gd.heightHint = 200;
		gd.minimumHeight = 150;
		gd.widthHint = 150;
		theoremMirrorSection.getSection().setLayoutData(gd);
	}

}
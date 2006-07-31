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

package org.eventb.internal.ui.prover;

import java.util.Collection;
import java.util.HashSet;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.forms.SectionPart;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.ScrolledForm;
import org.eclipse.ui.forms.widgets.Section;
import org.eventb.core.prover.sequent.Hypothesis;
import org.eventb.internal.ui.IEventBFormText;
import org.eventb.internal.ui.UIUtils;

/**
 * @author htson
 *         <p>
 *         This is the based class for creating different hypothesis sections
 *         (cached, searched, selected).
 */
public abstract class HypothesesSection extends SectionPart {

	// The page contains the section.
	protected ProofsPage page;

	private Composite comp;

	private ScrolledForm scrolledForm;

	// Title and description
	private String title;

	private String description;

	protected Collection<HypothesisRow> rows;

	protected IEventBFormText formText;
	
	private boolean compact;
	
	/**
	 * Constructor.
	 * <p>
	 * 
	 * @param page
	 *            page contains the section
	 * @param parent
	 *            composite parent of the section
	 * @param style
	 *            style to creat the section
	 * @param title
	 *            title of the section
	 * @param description
	 *            description of the section
	 */
	public HypothesesSection(ProofsPage page, Composite parent, int style,
			String title, String description) {
		super(parent, page.getManagedForm().getToolkit(), style);
		compact = (style & Section.COMPACT) != 0 ? true : false;
		this.page = page;
		this.title = title;
		this.description = description;
		FormToolkit toolkit = page.getManagedForm().getToolkit();
		createClient(getSection(), toolkit);
		rows = new HashSet<HypothesisRow>();
	}

	/**
	 * Create the top FormText (such as ds,sl hyperlinks).
	 * <p>
	 * 
	 * @param toolkit
	 *            FormToolkit used to create the FormText
	 * @param comp
	 *            the composite parent of the FormText
	 */
//	protected abstract void createTopFormText(FormToolkit toolkit,
//			Composite comp);

	/**
	 * Create the client of the section.
	 * <p>
	 * 
	 * @param section
	 *            the section
	 * @param toolkit
	 *            the FormToolkit used to create the client.
	 */
	public void createClient(Section section, FormToolkit toolkit) {
		section.setText(title);
		section.setDescription(description);

		Composite composite = toolkit.createComposite(section);
		GridLayout layout = new GridLayout();
		layout.numColumns = 1;
		composite.setLayout(layout);
//		createTopFormText(toolkit, composite);

		scrolledForm = toolkit.createScrolledForm(composite);
		GridData gd = new GridData(SWT.FILL, SWT.FILL, true, true);
		scrolledForm.setLayoutData(gd);

		comp = scrolledForm.getBody();
		layout = new GridLayout();
		layout.numColumns = 3;
		layout.verticalSpacing = 5;
		comp.setLayout(layout);

		section.setClient(composite);
		
		createTextClient(section, toolkit);
	}

	protected void createTextClient(Section section, FormToolkit toolkit) {
		// TODO Auto-generated method stub
		
	}

	public void init(Collection<Hypothesis> hyps) {
		// Remove everything
		for (HypothesisRow row : rows) {
			row.dispose();
		}
		rows.clear();

		// Add new hyps
		for (Hypothesis hyp : hyps) {
			UIUtils.debugEventBEditor("Add to " + this.title + " hyp: "
					+ hyp.getPredicate());
			HypothesisRow row = new HypothesisRow(this.getManagedForm()
					.getToolkit(), comp, hyp, ((ProverUI) page.getEditor())
					.getUserSupport());
			rows.add(row);
		}
		
		scrolledForm.reflow(true);
	}

	@Override
	public void dispose() {
		if (formText != null) formText.dispose();
		super.dispose();
	}

	@Override
	protected void expansionStateChanging(boolean expanding) {
		if (expanding) compact = false;
		else compact = true;
		page.layout();
	}

	public boolean isCompact() {return compact;}
	
}
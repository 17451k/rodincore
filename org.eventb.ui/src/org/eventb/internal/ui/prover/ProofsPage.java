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

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.forms.IManagedForm;
import org.eclipse.ui.forms.editor.FormPage;
import org.eclipse.ui.forms.widgets.ExpandableComposite;
import org.eclipse.ui.forms.widgets.ScrolledForm;
import org.eclipse.ui.forms.widgets.Section;
import org.eventb.core.pm.IProofStateChangedListener;
import org.eventb.core.pm.IProofStateDelta;
import org.eventb.core.pm.ProofState;
import org.eventb.core.pm.UserSupport;
import org.eventb.core.prover.IProofTreeNode;
import org.eventb.core.prover.sequent.Hypothesis;

/**
 * @author htson
 *         <p>
 *         This is the implementation of the Proof Page in the Prover UI Editor.
 */
public class ProofsPage extends FormPage implements IProofStateChangedListener {

	// ID, title and the tab-title
	public static final String PAGE_ID = "Proof State"; //$NON-NLS-1$

	public static final String PAGE_TITLE = "Proof State";

	public static final String PAGE_TAB_TITLE = "Proof State";

	// Different sections
	private GoalSection goalSection;

	private HypothesesSection selectedSection;

	private HypothesesSection cachedSection;

	private HypothesesSection searchedSection;

	// private Collection<Hypothesis> selected;
	//
	// private Collection<Hypothesis> cached;
	//
	// private Collection<Hypothesis> searched;

	private UserSupport userSupport;

	/**
	 * Constructor.
	 * <p>
	 * 
	 * @param editor
	 *            the Prover UI Editor contains this page.
	 */
	public ProofsPage(ProverUI editor) {
		super(editor, PAGE_ID, PAGE_TAB_TITLE); //$NON-NLS-1$
		userSupport = editor.getUserSupport();
		userSupport.addStateChangedListeners(this);
		// selected = new HashSet<Hypothesis>();
		// cached = new HashSet<Hypothesis>();
		// searched = new HashSet<Hypothesis>();
	}
	
	

	@Override
	public void dispose() {
		userSupport.removeStateChangedListeners(this);
		super.dispose();
	}



	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.ui.forms.editor.FormPage#createFormContent(org.eclipse.ui.forms.IManagedForm)
	 */
	protected void createFormContent(IManagedForm managedForm) {
		super.createFormContent(managedForm);
		ScrolledForm form = managedForm.getForm();
		form.setText(PAGE_TITLE); //$NON-NLS-1$
		Composite body = form.getBody();
		GridLayout layout = new GridLayout();
		layout.numColumns = 1;
		layout.marginWidth = 10;
		layout.verticalSpacing = 20;
		layout.horizontalSpacing = 10;
		body.setLayout(layout);

		GridData gd = new GridData(SWT.FILL, SWT.FILL, true, true);
		body.setLayoutData(gd);

		searchedSection = new SearchHypothesesSection(this, body,
				ExpandableComposite.TITLE_BAR | ExpandableComposite.TWISTIE
						| Section.COMPACT);
		managedForm.addPart(searchedSection);

		gd = new GridData(SWT.FILL, SWT.FILL, true, true);
		// gd.heightHint = 0;
		gd.widthHint = 200;
		searchedSection.getSection().setLayoutData(gd);

		cachedSection = new CacheHypothesesSection(this, body,
				ExpandableComposite.TITLE_BAR | ExpandableComposite.TWISTIE
						| Section.EXPANDED);
		managedForm.addPart(cachedSection);

		gd = new GridData(SWT.FILL, SWT.FILL, true, true);
		gd.heightHint = 100;
		gd.minimumHeight = 100;
		gd.widthHint = 200;
		cachedSection.getSection().setLayoutData(gd);

		selectedSection = new SelectedHypothesesSection(this, body,
				ExpandableComposite.TITLE_BAR | ExpandableComposite.TWISTIE
						| Section.EXPANDED);
		managedForm.addPart(selectedSection);

		gd = new GridData(SWT.FILL, SWT.FILL, true, true);
		gd.heightHint = 150;
		gd.minimumHeight = 100;
		gd.widthHint = 200;
		selectedSection.getSection().setLayoutData(gd);

		initHypothesisSections();

		goalSection = new GoalSection(this, body, ExpandableComposite.TITLE_BAR
				| ExpandableComposite.TWISTIE | Section.EXPANDED);
		managedForm.addPart(goalSection);

		gd = new GridData(SWT.FILL, SWT.FILL, true, true);
		gd.heightHint = 75;
		gd.minimumHeight = 50;
		gd.widthHint = 200;
		goalSection.getSection().setLayoutData(gd);

	}

	private void initHypothesisSections() {
		ProofState ps = userSupport.getCurrentPO();
		Set<Hypothesis> selected = new HashSet<Hypothesis>();
		ArrayList<Hypothesis> cached = new ArrayList<Hypothesis>();
		ArrayList<Hypothesis> searched = new ArrayList<Hypothesis>();

		if (ps != null) {
			IProofTreeNode node = ps.getCurrentNode();
			if (node != null) {
				selected = node.getSequent().selectedHypotheses();
			}
			Collection<Hypothesis> currentCached = ps.getCached();
			for (Iterator<Hypothesis> i = currentCached.iterator(); i.hasNext();) {
				Hypothesis hyp = i.next();
				if (node != null) 
					if (!node.getSequent().hypotheses().contains(hyp))
						continue;
				if (!selected.contains(hyp))
					cached.add(hyp);
			}
			Collection<Hypothesis> currentSearched = ps.getSearched();
			for (Iterator<Hypothesis> i = currentSearched.iterator(); i
					.hasNext();) {
				Hypothesis hyp = i.next();
				if (node != null) 
					if (!node.getSequent().hypotheses().contains(hyp))
						continue;
				if (!selected.contains(hyp) && !cached.contains(hyp))
					searched.add(hyp);
			}
		}
		selectedSection.init(selected);
		cachedSection.init(cached);
		searchedSection.init(searched);

	}

	public void proofStateChanged(final IProofStateDelta delta) {
		Display display = this.getEditorSite().getWorkbenchWindow()
				.getWorkbench().getDisplay();

		final ProofState ps = delta.getNewProofState();
		boolean refresh = false;
		if (ps != null) { // Reload everything
			refresh = true;
			display.syncExec(new Runnable() {
				public void run() {
					goalSection.setGoal(ps.getCurrentNode());
				}
			});
		} else {
			final IProofTreeNode node = delta.getNewProofTreeNode();
			if (node != null) {
				refresh = true;
				display.syncExec(new Runnable() {
					public void run() {
						goalSection.setGoal(node);
					}
				});
			} else {
				display.syncExec(new Runnable() {
					public void run() {
						if (delta.getNewCache() || delta.getNewSearch()) {
							initCacheAndSearch();
						}
					}
				});
			}
		}

		if (refresh) {
			display.syncExec(new Runnable() {
				public void run() {
					initHypothesisSections();
				}
			});
		}
	}

	private void initCacheAndSearch() {
		ProofState ps = userSupport.getCurrentPO();
		Set<Hypothesis> selected = new HashSet<Hypothesis>();
		ArrayList<Hypothesis> cached = new ArrayList<Hypothesis>();
		ArrayList<Hypothesis> searched = new ArrayList<Hypothesis>();

		if (ps != null) {
			IProofTreeNode node = ps.getCurrentNode();
			if (node != null) {
				selected = node.getSequent().selectedHypotheses();
			}
			Collection<Hypothesis> currentCached = ps.getCached();
			for (Iterator<Hypothesis> i = currentCached.iterator(); i.hasNext();) {
				Hypothesis hyp = i.next();
				if (node != null) 
					if (!node.getSequent().hypotheses().contains(hyp))
						continue;
				if (!selected.contains(hyp))
					cached.add(hyp);
			}
			
			Collection<Hypothesis> currentSearched = ps.getSearched();
			for (Iterator<Hypothesis> i = currentSearched.iterator(); i
					.hasNext();) {
				Hypothesis hyp = i.next();
				if (node != null) 
					if (!node.getSequent().hypotheses().contains(hyp))
						continue;
				if (!selected.contains(hyp) && !cached.contains(hyp))
					searched.add(hyp);
			}
		}
		cachedSection.init(cached);
		searchedSection.init(searched);

	}
}
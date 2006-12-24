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

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.jface.resource.ImageRegistry;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.forms.events.HyperlinkAdapter;
import org.eclipse.ui.forms.events.HyperlinkEvent;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.ImageHyperlink;
import org.eclipse.ui.forms.widgets.Section;
import org.eventb.core.ast.Predicate;
import org.eventb.core.seqprover.ITactic;
import org.eventb.core.seqprover.ProverFactory;
import org.eventb.core.seqprover.eventbExtensions.Tactics;
import org.eventb.internal.ui.HypothesisRow;
import org.eventb.ui.EventBUIPlugin;
import org.eventb.ui.IEventBSharedImages;
import org.rodinp.core.RodinDBException;

/**
 * @author htson
 *         <p>
 *         This class is an sub-class of Hypotheses Section to show the set of
 *         selected hypotheses in Prover UI editor.
 */
public class SelectedHypothesesSection extends HypothesesSection {

	// Title and description
	private static final String SECTION_TITLE = "Selected Hypotheses";

	private static final String SECTION_DESCRIPTION = "The set of selected hypotheses";

	private ImageHyperlink ds;

	/**
	 * @author htson
	 *         <p>
	 *         This class extends HyperlinkAdapter and provide response actions
	 *         when a hyperlink is activated.
	 */
	class SelectedHyperlinkAdapter extends HyperlinkAdapter {

		/*
		 * (non-Javadoc)
		 * 
		 * @see org.eclipse.ui.forms.events.IHyperlinkListener#linkActivated(org.eclipse.ui.forms.events.HyperlinkEvent)
		 */
		@Override
		public void linkActivated(HyperlinkEvent e) {
			Set<Predicate> deselected = new HashSet<Predicate>();
			for (Iterator<HypothesisRow> it = rows.iterator(); it.hasNext();) {
				HypothesisRow hr = it.next();
				if (hr.isSelected()) {
					deselected.add(hr.getHypothesis());
				}
			}

			if (deselected.isEmpty())
				return;

			ProverUI editor = (ProverUI) page.getEditor();
			ITactic t = Tactics.mngHyp(ProverFactory.makeDeselectHypAction(deselected));
			try {
				editor.getUserSupport().applyTacticToHypotheses(t, deselected, new NullProgressMonitor());
			} catch (RodinDBException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			// TreeViewer viewer = editor.getProofTreeUI().getViewer();
			//
			// ISelection selection = viewer.getSelection();
			// Object obj = ((IStructuredSelection)
			// selection).getFirstElement();
			// if (obj instanceof IProofTreeNode) {
			// IProofTreeNode proofTree = (IProofTreeNode) obj;
			// if (!proofTree.isClosed()) {
			// // ITactic t =
			// // Tactics.mngHyp(HypothesesManagement.ActionType.DESELECT,
			// // deselected);
			// // t.apply(proofTree);
			// editor.getProofTreeUI().refresh(proofTree);
			// // Expand the node
			// viewer.expandToLevel(proofTree,
			// AbstractTreeViewer.ALL_LEVELS);
			// // viewer.setExpandedState(proofTree, true);
			//
			// // Select the "next" pending "subgoal"
			// ProofState ps = editor.getUserSupport().getCurrentPO();
			// IProofTreeNode pt = ps.getNextPendingSubgoal(proofTree);
			// if (pt != null)
			// editor.getProofTreeUI().getViewer().setSelection(
			// new StructuredSelection(pt));
			// }
			// }
		}

	}

	/**
	 * Constructor
	 * <p>
	 * 
	 * @param page
	 *            The page that contain this section
	 * @param parent
	 *            the composite parent of the section
	 * @param style
	 *            style to create this section
	 */
	public SelectedHypothesesSection(ProofsPage page, Composite parent,
			int style) {
		super(page, parent, style, SECTION_TITLE, SECTION_DESCRIPTION);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.ui.forms.SectionPart#expansionStateChanging(boolean)
	 */
	// @Override
	// protected void expansionStateChanging(boolean expanding) {
	// if (expanding) {
	// GridData gd = new GridData(SWT.FILL, SWT.FILL, true, true);
	// gd.heightHint = 150;
	// gd.minimumHeight = 100;
	// gd.widthHint = 200;
	// this.getSection().setLayoutData(gd);
	// } else {
	// GridData gd = new GridData(GridData.VERTICAL_ALIGN_BEGINNING);
	// gd.widthHint = 200;
	// this.getSection().setLayoutData(gd);
	// }
	// super.expansionStateChanging(expanding);
	// }
	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eventb.internal.ui.prover.HypothesesSection#createTopFormText()
	 */
	// @Override
	// protected void createTopFormText(FormToolkit toolkit, Composite comp) {
	// GridData gd;
	// formText = new EventBFormText(toolkit.createFormText(comp, true));
	// gd = new GridData();
	// gd.widthHint = 50;
	// gd.horizontalAlignment = SWT.LEFT;
	// FormText ft = formText.getFormText();
	// ft.setLayoutData(gd);
	// ft.addHyperlinkListener(new SelectedHyperlinkAdapter());
	// String string = "<form><li style=\"text\" value=\"\" bindent=\"-20\"><a
	// href=\"ds\">ds</a></li></form>";
	// ft.setText(string, true, false);
	// }
	@Override
	protected void createTextClient(Section section, FormToolkit toolkit) {
		Composite composite = new Composite(section, SWT.NONE);

		GridLayout layout = new GridLayout();
		layout.numColumns = 1;
		layout.horizontalSpacing = 0;
		layout.verticalSpacing = 0;
		composite.setLayout(layout);

		toolkit.adapt(composite, true, true);
		composite.setBackground(section.getTitleBarGradientBackground());

		ds = new ImageHyperlink(composite, SWT.CENTER);
		toolkit.adapt(ds, true, true);
		ImageRegistry registry = EventBUIPlugin.getDefault().getImageRegistry();
		ds.setImage(registry.get(IEventBSharedImages.IMG_PENDING));
		ds.addHyperlinkListener(new SelectedHyperlinkAdapter());
		ds.setBackground(section.getTitleBarGradientBackground());
		ds.setToolTipText("Deselect checked hypotheses");
		composite.pack();

		section.setTextClient(composite);
	}

	@Override
	protected void updateTextClientStatus(boolean enable) {
		ds.setEnabled(enable);
	}

}
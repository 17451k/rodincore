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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.forms.SectionPart;
import org.eclipse.ui.forms.editor.FormPage;
import org.eclipse.ui.forms.events.HyperlinkEvent;
import org.eclipse.ui.forms.events.IHyperlinkListener;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.ImageHyperlink;
import org.eclipse.ui.forms.widgets.ScrolledForm;
import org.eclipse.ui.forms.widgets.Section;
import org.eventb.core.ast.BoundIdentDecl;
import org.eventb.core.ast.Formula;
import org.eventb.core.ast.FormulaFactory;
import org.eventb.core.ast.IParseResult;
import org.eventb.core.ast.IPosition;
import org.eventb.core.ast.Predicate;
import org.eventb.core.ast.QuantifiedPredicate;
import org.eventb.core.ast.SourceLocation;
import org.eventb.core.pm.IProofState;
import org.eventb.core.pm.IUserSupport;
import org.eventb.core.seqprover.IProofTreeNode;
import org.eventb.internal.ui.EventBImage;
import org.eventb.internal.ui.TacticPositionUI;
import org.eventb.ui.IEventBSharedImages;
import org.eventb.ui.prover.IProofCommand;
import org.eventb.ui.prover.ITacticProvider;
import org.rodinp.core.RodinDBException;

/**
 * @author htson
 *         <p>
 *         This class implements the goal section in the Prover UI Editor.
 */
public class GoalSection extends SectionPart {

	// Title and description.
	private static final String SECTION_TITLE = "Goal";

	private static final String SECTION_DESCRIPTION = "The current goal";

	private static final FormulaFactory ff = FormulaFactory.getDefault();

	FormPage page;

	private FormToolkit toolkit;

	IUserSupport us;

	private ScrolledForm scrolledForm;

	private Composite buttonComposite;

	private ScrolledForm goalComposite;

	EventBPredicateText goalText;

	private Predicate parsedPred;

	private String actualString;

	private int max_length = 30;

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
	public GoalSection(FormPage page, Composite parent, int style) {
		super(parent, page.getManagedForm().getToolkit(), style);
		this.page = page;
		toolkit = page.getManagedForm().getToolkit();
		us = ((ProverUI) ((ProofsPage) this.page).getEditor()).getUserSupport();
		createClient(getSection());
	}

	/**
	 * Creating the client of the section.
	 * <p>
	 * 
	 * @param section
	 *            the section that used as the parent of the client
	 */
	public void createClient(Section section) {
		section.setText(SECTION_TITLE);
		section.setDescription(SECTION_DESCRIPTION);
		scrolledForm = toolkit.createScrolledForm(section);

		Composite comp = scrolledForm.getBody();
		GridLayout layout = new GridLayout();
		layout.numColumns = 2;
		layout.verticalSpacing = 5;
		comp.setLayout(layout);
		section.setClient(scrolledForm);
		toolkit.paintBordersFor(scrolledForm);

		IProofState ps = us.getCurrentPO();
		if (ps != null) {
			setGoal(ps.getCurrentNode());
		} else
			setGoal(null);
	}

	/**
	 * Set the current goal
	 * <p>
	 * 
	 * @param node
	 *            the current proof tree node.
	 */
	public void setGoal(IProofTreeNode node) {
		if (buttonComposite != null)
			buttonComposite.dispose();
		if (goalComposite != null)
			goalComposite.dispose();

		Composite comp = scrolledForm.getBody();

		buttonComposite = toolkit.createComposite(comp);
		GridLayout layout = new GridLayout();
		layout.makeColumnsEqualWidth = true;
		layout.numColumns = 3;

		buttonComposite.setLayout(layout);
		buttonComposite.setLayoutData(new GridData(SWT.FILL, SWT.FILL, false,
				false));

		goalComposite = toolkit.createScrolledForm(comp);
		GridData gd = new GridData(SWT.FILL, SWT.FILL, false, false);
		goalComposite.setLayoutData(gd);
		goalComposite.getBody().setLayout(new FillLayout());

		if (node == null)
			createNullHyperlinks();
		else if (node.isOpen())
			createImageHyperlinks(true);
		else
			createImageHyperlinks(false);

		createGoalText(node);

		scrolledForm.reflow(true);

		return;
	}

	public void createGoalText(final IProofTreeNode node) {
		Color color = Display.getCurrent().getSystemColor(SWT.COLOR_GRAY);
		if (goalText != null)
			goalText.dispose();
		goalText = new EventBPredicateText(toolkit, goalComposite);
		final StyledText styledText = goalText.getMainTextWidget();
		// styledText.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true,
		// true));

		// int borderWidth = styledText.getBorderWidth();
		// styledText.setText(" ");
		// goalComposite.pack(true);
		// int textWidth = styledText.getSize().x;
		//
		// Rectangle rec = goalComposite.getBounds();
		// Point size = goalComposite.getSize();
		// int compositeWidth = goalComposite.getClientArea().width;
		// if (textWidth != 0) {
		// max_length = (compositeWidth - borderWidth) / textWidth;
		// } else
		// max_length = 30;

		if (node == null) {
			goalText.setText("No current goal", us, node.getSequent().goal(),
					null, null);
			styledText.setBackground(color);
		} else {
			Predicate goal = node.getSequent().goal();
			String tmpString = goal.toString();
			IParseResult parseResult = ff.parsePredicate(tmpString);
			assert parseResult.isSuccess();
			Predicate tmpPred = parseResult.getParsedPredicate();

			Collection<Point> indexes = new ArrayList<Point>();

			if (node.isOpen() && tmpPred instanceof QuantifiedPredicate
					&& tmpPred.getTag() == Formula.EXISTS) {
				indexes = getIndexesString(tmpPred, tmpString);
			} else {
				actualString = PredicateUtil.prettyPrint(max_length, tmpString,
						tmpPred);
			}
			IParseResult parsedResult = ff.parsePredicate(actualString);
			assert parsedResult.isSuccess();
			parsedPred = parsedResult.getParsedPredicate();

			Map<Point, TacticPositionUI> links = new HashMap<Point, TacticPositionUI>();
			if (node.isOpen()) {
				links = getHyperlinks();
			}
			goalText.setText(actualString, us, node.getSequent().goal(),
					indexes, links);

			if (!node.isOpen()) {
				styledText.setBackground(color);
			}

		}
		toolkit.paintBordersFor(goalComposite);

		// DragSource source = new DragSource(styledText, DND.DROP_COPY
		// | DND.DROP_MOVE);
		// source.setTransfer(new Transfer[] { TextTransfer.getInstance() });
		// source.addDragListener(new DragSourceAdapter() {
		// Point selection;
		//
		// public void dragStart(DragSourceEvent e) {
		// selection = goalText.getMainTextWidget().getSelection();
		// e.doit = selection.x != selection.y;
		// }
		//
		// public void dragSetData(DragSourceEvent e) {
		// e.data = goalText.getMainTextWidget().getText(selection.x,
		// selection.y - 1);
		// }
		//
		// public void dragFinished(DragSourceEvent e) {
		// if (e.detail == DND.DROP_MOVE) {
		// goalText.getMainTextWidget().replaceTextRange(selection.x,
		// selection.y - selection.x, "");
		// }
		// selection = null;
		// }
		// });

		// styledText.addListener(SWT.MouseDown, new Listener() {
		// public void handleEvent(Event e) {
		// Point location = new Point(e.x, e.y);
		// Point maxLocation = styledText.getLocationAtOffset(styledText
		// .getCharCount());
		// int maxOffset = styledText.getCharCount();
		// if (location.y >= maxLocation.y + styledText.getLineHeight()) {
		// styledText.setCaretOffset(maxOffset);
		// return;
		// }
		// int startLineOffset = styledText.getOffsetAtLocation(new Point(0,
		// location.y));
		// int line = styledText.getLineAtOffset(startLineOffset);
		// Point pt = styledText.getSelection();
		// ProverUIUtils.debugProverUI("Selection: " + pt.x + ", " + pt.y);
		// if (line == styledText.getLineCount() - 1) {
		// if (location.x > maxLocation.x) {
		// styledText.setCaretOffset(maxOffset);
		// } else {
		// int offset = styledText.getOffsetAtLocation(location);
		// // styledText.setCaretOffset(offset);
		// if (pt.x <= offset && offset <= pt.y) {
		// ProverUIUtils.debugProverUI("Drag: " + offset);
		// }
		// else {
		// ProverUIUtils.debugProverUI("Select " + offset);
		// }
		// }
		// return;
		// }
		//				
		//				
		//				
		// int startNextLineOffset = styledText.getOffsetAtLine(line + 1);
		// Point lineEnd = styledText
		// .getLocationAtOffset(startNextLineOffset - 1);
		// if (location.x > lineEnd.x) {
		// // styledText.setCaretOffset(startNextLineOffset - 1);
		// } else {
		// int offset = styledText.getOffsetAtLocation(location);
		// // styledText.setCaretOffset(offset);
		// if (pt.x <= offset && offset <= pt.y) {
		// ProverUIUtils.debugProverUI("Drag: " + offset);
		// }
		// else {
		// ProverUIUtils.debugProverUI("Select " + offset);
		// }
		// }
		// }
		// });

		// source.addDragListener(new DragSourceListener() {
		// Point selection;
		//
		// public void dragStart(DragSourceEvent event) {
		// ProverUIUtils.debugProverUI("Start dragging: ");
		// selection = styledText.getSelection();
		// event.doit = selection.x != selection.y;
		// }
		//
		// public void dragSetData(DragSourceEvent event) {
		// ProverUIUtils.debugProverUI("Set Data: ");
		// event.data = styledText.getText(selection.x, selection.y - 1);
		//
		// }
		//
		// public void dragFinished(DragSourceEvent event) {
		// ProverUIUtils.debugProverUI("Finish dragging ");
		//
		// }
		//
		// });

	}

	private Map<Point, TacticPositionUI> getHyperlinks() {
		Map<Point, TacticPositionUI> links = new HashMap<Point, TacticPositionUI>();

		final TacticUIRegistry tacticUIRegistry = TacticUIRegistry.getDefault();

		String[] tactics = tacticUIRegistry.getApplicableToGoal(us);

		for (final String tacticID : tactics) {
			List<IPosition> positions = tacticUIRegistry
					.getApplicableToGoalPositions(tacticID, us);
			if (positions.size() == 0)
				continue;
			for (final IPosition position : positions) {
				Point pt = tacticUIRegistry.getOperatorPosition(tacticID,
						parsedPred, actualString, position);
				TacticPositionUI tacticPositionUI = links.get(pt);
				if (tacticPositionUI == null) {
					tacticPositionUI = new TacticPositionUI();
					links.put(pt, tacticPositionUI);
				}
				tacticPositionUI.addTacticPosition(tacticID, position);
			}
		}
		return links;
	}

	private Collection<Point> getIndexesString(Predicate pred,
			String sourceString) {
		QuantifiedPredicate qpred = (QuantifiedPredicate) pred;
		Collection<Point> indexes = new ArrayList<Point>();

		actualString = "\u2203 ";
		BoundIdentDecl[] idents = qpred.getBoundIdentDecls();

		int i = 0;
		for (BoundIdentDecl ident : idents) {
			SourceLocation loc = ident.getSourceLocation();
			String image = sourceString.substring(loc.getStart(),
					loc.getEnd() + 1);
			// ProverUIUtils.debugProverUI("Ident: " + image);
			actualString += " " + image + " ";
			int x = actualString.length();
			actualString += "      ";
			int y = actualString.length();
			indexes.add(new Point(x, y));

			if (++i == idents.length) {
				actualString += "\u00b7\n";
			} else {
				actualString += ", ";
			}
		}
		actualString += PredicateUtil.prettyPrint(max_length, sourceString,
				qpred.getPredicate());
		return indexes;
	}

	@Override
	public void dispose() {
		goalText.dispose();
		super.dispose();
	}

	private void createNullHyperlinks() {
		if (ProverUIUtils.DEBUG)
			ProverUIUtils.debug("Create Null Image");
		ImageHyperlink hyperlink = new ImageHyperlink(buttonComposite,
				SWT.CENTER);
		hyperlink.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));

		toolkit.adapt(hyperlink, true, true);
		hyperlink.setImage(EventBImage.getImage(IEventBSharedImages.IMG_NULL));
		hyperlink.setEnabled(false);
		return;
	}

	/**
	 * Utility methods to create hyperlinks for applicable tactics.
	 * <p>
	 * 
	 */
	private void createImageHyperlinks(boolean enable) {

		final TacticUIRegistry tacticUIRegistry = TacticUIRegistry.getDefault();
		String[] tactics = tacticUIRegistry.getApplicableToGoal(us);

		if (tactics.length == 0) {
			createNullHyperlinks();
		}

		for (final String tacticID : tactics) {

			List<IPosition> positions = tacticUIRegistry
					.getApplicableToGoalPositions(tacticID, us);

			if (positions.size() != 0)
				continue;

			ImageHyperlink hyperlink = new ImageHyperlink(buttonComposite,
					SWT.CENTER);
			hyperlink.setLayoutData(new GridData(SWT.FILL, SWT.FILL, false,
					false));
			toolkit.adapt(hyperlink, true, true);
			hyperlink.setImage(tacticUIRegistry.getIcon(tacticID));

			hyperlink.addHyperlinkListener(new IHyperlinkListener() {

				public void linkEntered(HyperlinkEvent e) {
					return;
				}

				public void linkExited(HyperlinkEvent e) {
					return;
				}

				public void linkActivated(HyperlinkEvent e) {
					IProofTreeNode node = us.getCurrentPO().getCurrentNode();
					applyTactic(tacticID, node, null);
				}

			});
			hyperlink.setToolTipText(tacticUIRegistry.getTip(tacticID));
			hyperlink.setEnabled(enable);
		}

		return;
	}

	void applyTactic(String tacticID, IProofTreeNode node, IPosition position) {
		TacticUIRegistry tacticUIRegistry = TacticUIRegistry.getDefault();
		String[] inputs = goalText.getResults();
		if (ProverUIUtils.DEBUG)
			for (String input : inputs)
				ProverUIUtils.debug("Input: \"" + input + "\"");

		ITacticProvider provider = tacticUIRegistry.getTacticProvider(tacticID);
		if (provider != null)
			try {
				us.applyTactic(
						provider.getTactic(node, null, position, inputs),
						new NullProgressMonitor());
			} catch (RodinDBException e2) {
				// TODO Auto-generated catch block
				e2.printStackTrace();
			}
		else {
			IProofCommand command = tacticUIRegistry.getProofCommand(tacticID,
					TacticUIRegistry.TARGET_HYPOTHESIS);
			if (command != null) {
				try {
					command.apply(us, null, inputs, new NullProgressMonitor());
				} catch (RodinDBException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
			}
		}
	}

}
/*******************************************************************************
 * Copyright (c) 2008 Systerel and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License  v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Systerel - initial API and implementation
  *******************************************************************************/

package fr.systerel.editor.editors;

import org.eclipse.jface.text.Document;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.ITextViewer;
import org.eclipse.jface.text.Position;
import org.eclipse.jface.text.TextViewer;
import org.eclipse.jface.text.contentassist.ContentAssistant;
import org.eclipse.jface.text.contentassist.IContentAssistant;
import org.eclipse.jface.text.source.AnnotationModelEvent;
import org.eclipse.jface.text.source.IAnnotationModel;
import org.eclipse.jface.text.source.IAnnotationModelListener;
import org.eclipse.jface.text.source.IAnnotationModelListenerExtension;
import org.eclipse.jface.text.source.ISourceViewer;
import org.eclipse.jface.text.source.projection.ProjectionViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.ExtendedModifyEvent;
import org.eclipse.swt.custom.ExtendedModifyListener;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.custom.VerifyKeyListener;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.KeyListener;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.VerifyEvent;
import org.eclipse.swt.events.VerifyListener;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.RGB;
import org.eventb.core.IAssignmentElement;
import org.eventb.core.ICommentedElement;
import org.eventb.core.IIdentifierElement;
import org.eventb.core.ILabeledElement;
import org.eventb.core.IPredicateElement;
import org.rodinp.core.IRodinElement;
import org.rodinp.core.RodinDBException;

/**
 *
 */
public class OverlayEditor implements IAnnotationModelListener,
						IAnnotationModelListenerExtension, ExtendedModifyListener, VerifyKeyListener{
	private StyledText editorText;
	private DocumentMapper mapper;
	private StyledText parent;
	private ProjectionViewer viewer;
	private Interval interval;
	private static final int DEFAULT_WIDTH = 300;
	private ITextViewer textViewer;
	private IContentAssistant contentAssistant;

	public OverlayEditor(StyledText parent, DocumentMapper mapper, ProjectionViewer viewer) {
		this.viewer = viewer;
		this.mapper = mapper;
		this.parent = parent;
		
		textViewer = new TextViewer(parent, SWT.BORDER |SWT.V_SCROLL);
		contentAssistant = getContentAssistant();
		contentAssistant.install(textViewer);
		
		editorText = textViewer.getTextWidget();
		editorText.addVerifyKeyListener(this);
		createEditorText();
	}


	private void createEditorText() {
//		editorText = new StyledText(parent, SWT.BORDER | SWT.V_SCROLL);
		editorText.setFont(parent.getFont());
		Point oldsize = parent.getSize();
		parent.pack();
		parent.setSize(oldsize);
		editorText.setVisible(false);
		editorText.addExtendedModifyListener(this);
	}

	
	public void showAtOffset(int offset){
		if (!editorText.isVisible()) {
			interval = mapper.findEditableInterval(viewer.widgetOffset2ModelOffset(offset));
			String text = "test";
			if (interval != null) {
				int start = viewer.modelOffset2WidgetOffset(interval.getOffset());
				if (interval.getLength() > 0) {
					text = parent.getText(start, start +interval.getLength()-1);
				} else {
					text = "";
				}
				Point location = (parent.getLocationAtOffset(start));
				
				Point endPoint = new Point(findMaxWidth(start, start +interval.getLength()), parent.getLocationAtOffset(start +interval.getLength()).y);
				Point size = new Point(endPoint.x - location.x, endPoint.y - (location.y) +parent.getLineHeight());
				
				textViewer.setDocument(createDocument(text));
//				editorText.setText(text);
				resizeTo(size.x, size.y);
				editorText.setFont(parent.getFont());
				setToLocation(location.x, location.y);
				editorText.setVisible(true);
				editorText.setFocus();
			}
			
		}
	}
	
	public void setToLocation(int x, int y) {
		editorText.setLocation(x, y-2);
	}

	public void resizeTo(int width, int height) {
		int w = Math.max(width +5 +editorText.getVerticalBar().getSize().x, DEFAULT_WIDTH);
		int h = Math.max(height, editorText.getLineHeight() ) +4;
		editorText.setSize(w, h);
	}
	
	public void abortEditing() {
		editorText.setVisible(false);
		interval = null;
		
	}

	
	public void addChangeToDatabase() {
		IRodinElement element = interval.getElement();
		String contentType= interval.getContentType();
		String text = removeWhiteSpaces(editorText.getText());
		if (element instanceof IIdentifierElement && contentType.equals(RodinConfiguration.IDENTIFIER_TYPE)) {
			try {
				((IIdentifierElement) element).setIdentifierString(text, null);
			} catch (RodinDBException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		if (element instanceof ILabeledElement && contentType.equals(RodinConfiguration.IDENTIFIER_TYPE)) {
			try {
				((ILabeledElement) element).setLabel(text, null);
			} catch (RodinDBException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		if (element instanceof IPredicateElement && contentType.equals(RodinConfiguration.CONTENT_TYPE)) {
			try {
				((IPredicateElement) element).setPredicateString(text, null);
			} catch (RodinDBException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		if (element instanceof IAssignmentElement && contentType.equals(RodinConfiguration.CONTENT_TYPE)) {
			try {
				((IAssignmentElement) element).setAssignmentString(text, null);
			} catch (RodinDBException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		if (element instanceof ICommentedElement && contentType.equals(RodinConfiguration.COMMENT_TYPE)) {
			try {
				((ICommentedElement) element).setComment(text, null);
			} catch (RodinDBException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
	
	/**
	 * Removes whitespaces at beginning and end of a text.
	 * 
	 * @return the text with the whitespaces removed
	 */
	public String removeWhiteSpaces(String text) {
		
		int first_pos = -1;
		int last_pos = -1;
		int i = 0;
		for (char ch : text.toCharArray()) {
			if (first_pos == -1 && !isWhitespace(ch)) {
				first_pos = i;
			}
			if (last_pos == -1 && isWhitespace(ch)){
				last_pos = i;
			}
			if (last_pos != -1 && !isWhitespace(ch)){
				last_pos = -1;
			}
			i++;
		}
		first_pos = Math.max(first_pos, 0);
		last_pos = (last_pos == -1 ) ? (text.length()) : last_pos;
		return (text.substring(first_pos, last_pos));
	}
	
	protected boolean isWhitespace(char c) {
		return (c == ' ' || c == '\t' || c == '\n' || c == '\r');
	}

	
	/**
	 * finds the maximum with of the the text in the parent widget within a
	 * given range.
	 * 
	 * @param start
	 * @param end
	 * @return the maximum with in pixels.
	 */
	protected int findMaxWidth(int start, int end){
		int firstLine = parent.getLineAtOffset(start);
		int lastLine = parent.getLineAtOffset(end);
		int max = 0;
		for (int i = firstLine; i <= lastLine; i++) {
			max = Math.max(parent.getLocationAtOffset(parent.getOffsetAtLine(i+1)-1).x, max);
		}
		
		return max;
	}
	
	protected IDocument createDocument(String text) {
		IDocument doc = new Document();
		doc.set(text);
		return doc;
	}




	@Override
	public void modelChanged(IAnnotationModel model) {
		// do nothing
	}


	@Override
	public void modelChanged(AnnotationModelEvent event) {
		// react to folding of the editor
		if (event.getChangedAnnotations().length >0 && editorText.isVisible()) {
			//adjust the location of the editor
			if (viewer.modelOffset2WidgetOffset(interval.getOffset()) > 0) {
				setToLocation(editorText.getLocation().x, parent.getLocationAtOffset(viewer.modelOffset2WidgetOffset(interval.getOffset())).y);
			} else {
				// if the interval that is currently being edited is hidden from view
				// abort the editing
				abortEditing();
			}
		}
	}



	@Override
	public void modifyText(ExtendedModifyEvent event) {
		int max = 0;
		for (int i = 0; i < editorText.getLineCount()-1; i++) {
			int offset = editorText.getOffsetAtLine(i +1)-1;
			max = Math.max(max, editorText.getLocationAtOffset(offset).x);
		}
		//last line
		max = Math.max(max, editorText.getLocationAtOffset(editorText.getCharCount()).x);
		int height = editorText.getLineCount() * editorText.getLineHeight();
		resizeTo(max, height);
		// TODO Auto-generated method stub
		
	}
	
	public IContentAssistant getContentAssistant() {

		ContentAssistant assistant= new ContentAssistant();
//		assistant.setDocumentPartitioning(getConfiguredDocumentPartitioning(sourceViewer));
		assistant.setContentAssistProcessor(new RodinContentAssistProcessor(mapper, this), Document.DEFAULT_CONTENT_TYPE);

		assistant.enableAutoActivation(true);
		assistant.setAutoActivationDelay(500);
		assistant.setProposalPopupOrientation(IContentAssistant.PROPOSAL_OVERLAY);
		assistant.setContextInformationPopupOrientation(IContentAssistant.CONTEXT_INFO_ABOVE);

		return assistant;
	}


	@Override
	public void verifyKey(VerifyEvent event) {
		if ((event.stateMask & SWT.CTRL) != 0 && event.character == '\r') {
			//do not add the return to the text
			event.doit = false;
			addChangeToDatabase();
			editorText.setVisible(false);
			interval = null;
		}
		if (event.character == SWT.ESC) {
			abortEditing();
		}
		
		if ((event.stateMask & SWT.CTRL) != 0 && event.character == '\u0020') {
			contentAssistant.showPossibleCompletions();
		}
			
		
	}

	/**
	 *
	 * @return the interval that this editor is currently editing
	 * or <code>null</code>, if there editor is not visible currently.
	 */
	public Interval getInterval() {
		return interval;
	}
	
	
}
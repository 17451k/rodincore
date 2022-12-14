/*******************************************************************************
 * Copyright (c) 2011, 2015 Systerel and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Systerel - initial API and implementation
 *******************************************************************************/
package fr.systerel.editor.internal.documentModel;

import static fr.systerel.editor.internal.documentModel.RodinTextStream.LINESEPARATOR;
import static fr.systerel.editor.internal.editors.EditPos.newPosOffLen;
import static fr.systerel.editor.internal.presentation.RodinConfiguration.COMMENT_TYPE;
import static fr.systerel.editor.internal.presentation.RodinConfiguration.IMPLICIT_COMMENT_TYPE;

import org.eventb.ui.manipulation.IAttributeManipulation;
import org.rodinp.core.emf.api.itf.ILElement;

import fr.systerel.editor.internal.editors.EditPos;
import fr.systerel.editor.internal.presentation.RodinConfiguration.ContentType;

/**
 * Material needed to build an interval in the Rodin Editor document.
 */
public class EditorRegion {

	private final boolean addWhitespace;
	private final EditPos pos;
	private final int level;
	private final String elementText;
	private final ILElement element;
	private final ContentType type;
	private final IAttributeManipulation manipulation;
	private final boolean multiline;
	private final String alignmentStr;

	public EditorRegion(int startOffset, int level, String text,
			ILElement element, ContentType type,
			IAttributeManipulation manipulation, boolean multiline,
			String alignmentStr) {
		this.addWhitespace = (COMMENT_TYPE.equals(type.getName()) || //
		IMPLICIT_COMMENT_TYPE.equals(type.getName()));
		this.level = level;
		this.elementText = RodinTextStream.processMulti(multiline,
				alignmentStr, addWhitespace, text);
		this.pos = getRegionLength(startOffset);
		this.element = element;
		this.type = type;
		this.manipulation = manipulation;
		this.multiline = multiline;
		this.alignmentStr = alignmentStr;
	}

	/**
	 * Ignoring length of line separators
	 */
	private EditPos getRegionLength(int startOffset) {
		final int length = (!elementText.equals(LINESEPARATOR)) ? elementText
				.length() : 0;
		return newPosOffLen(startOffset, length);
	}
	
	public EditPos getPos() {
		return pos;
	}
	
	public String getText() {
		return elementText;
	}

	public ILElement getElement() {
		return element;
	}

	public ContentType getType() {
		return type;
	}

	public IAttributeManipulation getManipulation() {
		return manipulation;
	}

	public Boolean getMultiline() {
		return multiline;
	}

	public int getAdditionalTabs() {
		return level;
	}
	
	public String getAlignement() {
		return alignmentStr;
	}
	
	public boolean isAddWhitespace() {
		return addWhitespace;
	}
	
}

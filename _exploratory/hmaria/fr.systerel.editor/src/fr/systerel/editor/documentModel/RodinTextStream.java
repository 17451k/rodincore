/*******************************************************************************
 * Copyright (c) 2011 Systerel and others.
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 * 
 * Contributors:
 *     Systerel - Initial API and implementation
 *******************************************************************************/
package fr.systerel.editor.documentModel;

import static fr.systerel.editor.presentation.RodinConfiguration.COMMENT_HEADER_TYPE;
import static fr.systerel.editor.presentation.RodinConfiguration.KEYWORD_TYPE;
import static fr.systerel.editor.presentation.RodinConfiguration.LABEL_TYPE;
import static fr.systerel.editor.presentation.RodinConfiguration.LEFT_PRESENTATION_TYPE;
import static fr.systerel.editor.presentation.RodinConfiguration.PRESENTATION_TYPE;
import static fr.systerel.editor.presentation.RodinConfiguration.getAttributeContentType;

import org.eventb.internal.ui.eventbeditor.manipulation.IAttributeManipulation;
import org.rodinp.core.IAttributeType;
import org.rodinp.core.IInternalElementType;
import org.rodinp.core.emf.api.itf.ILElement;

import fr.systerel.editor.presentation.RodinConfiguration;
import fr.systerel.editor.presentation.RodinConfiguration.ContentType;

/**
 * A class holding a string builder and the documentMapper used by the text
 * generator in order to process both the text representation of a model, and
 * the interval mapping of the model. 
 */
public class RodinTextStream {

	public static int MIN_LEVEL = 1;
	private static final String COMMENT_HEADER_DELIMITER = "§";
	private static final Character TAB = '\u0009';
	private static final int NO_TABS = 0;
	private static final String WHITESPACE = " ";
	private static final Object LINESEPARATOR = System
			.getProperty("line.separator");
	
	private final StringBuilder builder;
	private final DocumentMapper mapper;
	private int level = MIN_LEVEL;
	
	public static String processMulti(boolean multiLine, int level,
			boolean addWhiteSpace, String text) {
		if (!multiLine || text == null)
			return text;
		final String regex = "(\r\n)|(\r)|(\n)";
		if (addWhiteSpace)
			return text.replaceAll(regex, "$0" + getTabs(level) + WHITESPACE);
		return text.replaceAll(regex, "$0" + getTabs(level));
	}

	public static String deprocessMulti(int level, boolean multiLine,
			boolean tabbed, String text) {
		if (!multiLine)
			return text;
		return deprocessMulti(level, tabbed, text);
	}

	public static String deprocessMulti(int level, boolean addWhitespace,
			String text) {
		final String commonPatternStart = "((\r\n)|(\r)|(\n))(";
		// Tells that it should take into account one (only) matching pattern
		final String commonPatternEnd = "){1}";
		if (addWhitespace) {
			return text.replaceAll(commonPatternStart + getTabs(level)
					+ WHITESPACE + commonPatternEnd, "$1");
		}
		return text.replaceAll(commonPatternStart + getTabs(level)
				+ commonPatternEnd, "$1");
	}

	public RodinTextStream(DocumentMapper mapper) {
		this.builder = new StringBuilder();
		this.mapper = mapper;
		mapper.resetPrevious();
	}

	protected void addEditorSection(IInternalElementType<?> type,
			int folding_start, int folding_length) {
		mapper.addEditorSection(type, folding_start, folding_length);
	}

	protected void addElementRegion(String text, ILElement element,
			ContentType contentType, boolean multiLine) {
		addElementRegion(text, element, contentType, null, multiLine, NO_TABS);
	}

	protected void addElementRegion(String text, ILElement element,
			ContentType contentType, boolean multiLine, int additionalTabs) {
		addElementRegion(text, element, contentType, null, multiLine,
				additionalTabs);
	}

	protected void addElementRegion(String text, ILElement element,
			ContentType contentType, IAttributeManipulation manipulation,
			boolean multiLine, int additionalTabs) {
		final int start = builder.length();
		final boolean addWhiteSpace = contentType == RodinConfiguration.COMMENT_TYPE
				|| contentType == RodinConfiguration.IMPLICIT_COMMENT_TYPE;
		final String multilined = processMulti(multiLine, getLevel()
				+ additionalTabs, addWhiteSpace, text);
		builder.append(multilined);
		final int length = builder.length() - start;
		mapper.processInterval(start, length, element, contentType,
				manipulation, multiLine, getLevel() + additionalTabs,
				addWhiteSpace);
	}

	protected void addAttributeRegion(String text, ILElement element,
			IAttributeManipulation manipulation, IAttributeType attributeType) {
		addElementRegion(text, element, getAttributeContentType(attributeType),
				manipulation, false, NO_TABS);
	}

	protected void addLabelRegion(String text, ILElement element) {
		addElementRegion(text, element, LABEL_TYPE, false);
		addPresentationRegion((String) LINESEPARATOR, element);
	}

	protected void addLeftPresentationRegion(String text, ILElement element) {
		addElementRegion(text, element, LEFT_PRESENTATION_TYPE, false);
	}
	
	protected void addPresentationRegion(String text, ILElement element) {
		addElementRegion(text, element, PRESENTATION_TYPE, false);
	}

	protected void addCommentHeaderRegion(ILElement element, boolean appendTabs) {
		if (appendTabs)
			addPresentationRegion(String.valueOf(TAB), element);
		addElementRegion(COMMENT_HEADER_DELIMITER, element,
				COMMENT_HEADER_TYPE, false);
	}

	protected void addKeywordRegion(String title) {
		appendPresentationTabs(null);
		addElementRegion(title, null, KEYWORD_TYPE, false);
		appendLineSeparator();
	}

	protected void addSectionRegion(String title) {
		if (level > 0)
			appendPresentationTabs(null);
		addElementRegion(title, null, KEYWORD_TYPE, false);
		appendLineSeparator();
	}

	public static String getTabs(int number) {
		StringBuilder tabs = new StringBuilder();
		for (int i = 0; i < number; i++) {
			tabs.append(TAB);
		}
		return tabs.toString();
	}

	public void incrementIndentation() {
		level++;
	}
	
	public void decrementIndentation() {
		level--;
	}
	
	public void appendLineSeparator() {
		addPresentationRegion((String) LINESEPARATOR, null);
	}
	
	public int getLevel() {
		return level;
	}
	
	public int getLength() {
		return builder.length();
	}
	
	public String getTabsForCurrentLevel() {
		return getTabs(level);
	}
	
	public String getText() {
		return builder.toString();
	}

	public void appendLeftPresentationTabs(ILElement e) {
		addLeftPresentationRegion(getTabs(level), e);
	}

	public void appendPresentationTabs(ILElement e, int indentation) {
		addPresentationRegion(getTabs(indentation), e);
	}
	
	public void appendPresentationTabs(ILElement e) {
		addPresentationRegion(getTabs(level), null);
	}

	public void incrementIndentation(int i) {
		level += i;
	}
	
	public void decrementIndentation(int i) {
		level -= i;
	}
	
}

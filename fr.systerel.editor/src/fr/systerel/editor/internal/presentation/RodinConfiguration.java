/*******************************************************************************
 * Copyright (c) 2008, 2013 Systerel and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License  v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Systerel - initial API and implementation
 *******************************************************************************/
package fr.systerel.editor.internal.presentation;

import static fr.systerel.editor.internal.presentation.IRodinColorConstant.ATTRIBUTE;
import static fr.systerel.editor.internal.presentation.IRodinColorConstant.COMMENT;
import static fr.systerel.editor.internal.presentation.IRodinColorConstant.COMMENT_DEBUG_BG;
import static fr.systerel.editor.internal.presentation.IRodinColorConstant.COMMENT_HEADER;
import static fr.systerel.editor.internal.presentation.IRodinColorConstant.COMMENT_HEADER_DEBUG_BG;
import static fr.systerel.editor.internal.presentation.IRodinColorConstant.CONTENT;
import static fr.systerel.editor.internal.presentation.IRodinColorConstant.CONTENT_DEBUG_BG;
import static fr.systerel.editor.internal.presentation.IRodinColorConstant.HANDLE;
import static fr.systerel.editor.internal.presentation.IRodinColorConstant.HANDLE_DEBUG_BG;
import static fr.systerel.editor.internal.presentation.IRodinColorConstant.IDENTIFIER;
import static fr.systerel.editor.internal.presentation.IRodinColorConstant.IDENTIFIER_DEBUG_BG;
import static fr.systerel.editor.internal.presentation.IRodinColorConstant.IMPLICIT_ATTRIBUTE;
import static fr.systerel.editor.internal.presentation.IRodinColorConstant.IMPLICIT_COMMENT;
import static fr.systerel.editor.internal.presentation.IRodinColorConstant.IMPLICIT_CONTENT;
import static fr.systerel.editor.internal.presentation.IRodinColorConstant.IMPLICIT_HANDLE;
import static fr.systerel.editor.internal.presentation.IRodinColorConstant.IMPLICIT_IDENTIFIER;
import static fr.systerel.editor.internal.presentation.IRodinColorConstant.IMPLICIT_LABEL;
import static fr.systerel.editor.internal.presentation.IRodinColorConstant.KEYWORD;
import static fr.systerel.editor.internal.presentation.IRodinColorConstant.KEYWORD_DEBUG_BG;
import static fr.systerel.editor.internal.presentation.IRodinColorConstant.LABEL;
import static fr.systerel.editor.internal.presentation.IRodinColorConstant.LABEL_DEBUG_BG;
import static fr.systerel.editor.internal.presentation.IRodinColorConstant.PRESENTATION_DEBUG_BG;
import static fr.systerel.editor.internal.presentation.IRodinColorConstant.SECTION;
import static fr.systerel.editor.internal.presentation.IRodinColorConstant.SECTION_DEBUG_BG;
import static org.eventb.core.EventBAttributes.COMMENT_ATTRIBUTE;
import static org.eventb.core.EventBAttributes.IDENTIFIER_ATTRIBUTE;
import static org.eventb.core.EventBAttributes.LABEL_ATTRIBUTE;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.eclipse.jface.text.ITextHover;
import org.eclipse.jface.text.TextAttribute;
import org.eclipse.jface.text.presentation.IPresentationReconciler;
import org.eclipse.jface.text.presentation.PresentationReconciler;
import org.eclipse.jface.text.source.IAnnotationHover;
import org.eclipse.jface.text.source.ISourceViewer;
import org.eclipse.jface.text.source.SourceViewerConfiguration;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.RGB;
import org.rodinp.core.IAttributeType;

import fr.systerel.editor.internal.editors.RodinEditor;

/**
 * A customization of the SourceViewerConfiguration
 */
public class RodinConfiguration extends SourceViewerConfiguration {

	public static class ContentType {

		private final String name;
		private final boolean isEditable;
		private final boolean isImplicit;
		private final RGB color;
		private final RGB debugBackground;
		private final int styles;

		public ContentType(String contentName, boolean isEditable,
				boolean isImplicit, RGB color, RGB debugBackground) {
			this(contentName, isEditable, isImplicit, color, debugBackground,
					SWT.NONE);
		}

		public ContentType(String contentName, boolean isEditable,
				boolean isImplicit, RGB color, RGB debugBackground, int styles) {
			this.name = contentName;
			this.isEditable = isEditable;
			this.isImplicit = isImplicit;
			this.color = color;
			this.debugBackground = debugBackground;
			this.styles = styles;
		}

		public String getName() {
			return name;
		}

		public boolean isEditable() {
			return isEditable;
		}

		public RGB getColor() {
			return color;
		}

		public boolean isImplicit() {
			return isImplicit;
		}

		public boolean isKindOfEditable() {
			return false;
		}

		public int getStyles() {
			return styles;
		}

		public RGB getDebugBackgroundColor() {
			return debugBackground;
		}

	}

	public static class AttributeContentType extends ContentType {

		// note: the attribute type may be null when unknown
		private final IAttributeType attributeType;

		public AttributeContentType(String contentName, boolean isImplicit,
				RGB color, RGB debugBackground, IAttributeType attributeType,
				int styles) {
			super(contentName, !isImplicit, isImplicit, color, debugBackground,
					styles);
			this.attributeType = attributeType;
		}

		public AttributeContentType(String contentName, boolean isImplicit,
				RGB color, RGB debugBackground, IAttributeType attributeType) {
			this(contentName, isImplicit, color, debugBackground,
					attributeType, SWT.NONE);
		}

		/**
		 * Returns the attribute type, or <code>null</code> when it is unknown.
		 * 
		 * @return an attribute type, or <code>null</code>
		 */
		public IAttributeType getAttributeType() {
			return attributeType;
		}

		@Override
		public boolean isKindOfEditable() {
			return true;
		}

	}

	/**
	 * Debug option to activate the visualization of content types using colored
	 * backgrounds
	 */
	public static boolean DEBUG = false;

	// FIXME take care about attribute type extensions
	// TODO make contributions out of the following constants
	public static final ContentType LEFT_PRESENTATION_TYPE = new ContentType(
			"__left_presentation_", false, false, CONTENT,
			PRESENTATION_DEBUG_BG);

	public static final ContentType PRESENTATION_TYPE = new ContentType(
			"__presentation_", false, false, CONTENT, PRESENTATION_DEBUG_BG);

	public static final ContentType HANDLE_TYPE = new ContentType("__handle",
			false, false, HANDLE, HANDLE_DEBUG_BG);

	public static final ContentType IMPLICIT_HANDLE_TYPE = new ContentType(
			"__implicit_handle", false, false, IMPLICIT_HANDLE, HANDLE_DEBUG_BG);

	public static final ContentType IDENTIFIER_TYPE = new AttributeContentType(
			"__identifier", false, IDENTIFIER, IDENTIFIER_DEBUG_BG,
			IDENTIFIER_ATTRIBUTE);
	public static final ContentType IMPLICIT_IDENTIFIER_TYPE = new AttributeContentType(
			"__implicit_identifier", true, IMPLICIT_IDENTIFIER,
			IDENTIFIER_DEBUG_BG, IDENTIFIER_ATTRIBUTE);

	public static final ContentType COMMENT_TYPE = new AttributeContentType(
			"__comment", false, COMMENT, COMMENT_DEBUG_BG, COMMENT_ATTRIBUTE);
	public static final ContentType IMPLICIT_COMMENT_TYPE = new AttributeContentType(
			"__implicit_comment", true, IMPLICIT_COMMENT, COMMENT_DEBUG_BG,
			COMMENT_ATTRIBUTE);

	public static final ContentType LABEL_TYPE = new AttributeContentType(
			"__label", false, LABEL, LABEL_DEBUG_BG, LABEL_ATTRIBUTE);
	public static final ContentType IMPLICIT_LABEL_TYPE = new AttributeContentType(
			"__implicit_label", true, IMPLICIT_LABEL, LABEL_DEBUG_BG,
			LABEL_ATTRIBUTE);

	public static final ContentType BOLD_LABEL_TYPE = new AttributeContentType(
			"__bold_label", false, LABEL, LABEL_DEBUG_BG, LABEL_ATTRIBUTE,
			SWT.BOLD);
	public static final ContentType BOLD_IMPLICIT_LABEL_TYPE = new AttributeContentType(
			"__bold_implicit_label", true, IMPLICIT_LABEL, LABEL_DEBUG_BG,
			LABEL_ATTRIBUTE, SWT.BOLD);

	public static final ContentType KEYWORD_TYPE = new ContentType("__keyword",
			false, false, KEYWORD, KEYWORD_DEBUG_BG);
	public static final ContentType SECTION_TYPE = new ContentType("__section",
			false, false, SECTION_DEBUG_BG, SECTION);
	public static final ContentType COMMENT_HEADER_TYPE = new ContentType(
			"__comment_header", false, false, COMMENT_HEADER,
			COMMENT_HEADER_DEBUG_BG);

	private static ContentType[] contentTypes = new ContentType[] {
			LEFT_PRESENTATION_TYPE, PRESENTATION_TYPE, HANDLE_TYPE,
			IMPLICIT_HANDLE_TYPE, IDENTIFIER_TYPE, IMPLICIT_IDENTIFIER_TYPE,
			COMMENT_TYPE, IMPLICIT_COMMENT_TYPE, LABEL_TYPE,
			IMPLICIT_LABEL_TYPE, BOLD_LABEL_TYPE, BOLD_IMPLICIT_LABEL_TYPE,
			KEYWORD_TYPE, SECTION_TYPE, COMMENT_HEADER_TYPE, };

	//--------------------------------------------------------------------------
	// The following contents are there only to populate damage repairers
	// They are not supposed to be part of <code>contentTypes</code>
	public static final ContentType FORMULA_TYPE = new AttributeContentType(
			"__formula", false, CONTENT, CONTENT_DEBUG_BG, null);
	public static final ContentType IMPLICIT_FORMULA_TYPE = new AttributeContentType(
			"__implicit_formula", true, IMPLICIT_CONTENT, CONTENT_DEBUG_BG,
			null);
	public static final ContentType ATTRIBUTE_TYPE = new AttributeContentType(
			"__attribute", false, ATTRIBUTE, CONTENT_DEBUG_BG, null);
	public static final ContentType IMPLICIT_ATTRIBUTE_TYPE = new AttributeContentType(
			"__implicit_attribute", true, IMPLICIT_ATTRIBUTE, CONTENT_DEBUG_BG,
			null);

	private static ContentType[] defaultTypes = new ContentType[] {
			FORMULA_TYPE, IMPLICIT_FORMULA_TYPE, ATTRIBUTE_TYPE,
			IMPLICIT_ATTRIBUTE_TYPE };
	// -------------------------------------------------------------------------
	
	private static Map<String, ContentType> typesByName = new HashMap<String, ContentType>();
	static {
		for (ContentType contentType : contentTypes) {
			typesByName.put(contentType.getName(), contentType);
		}
	}

	public static ContentType getAttributeContentType(IAttributeType type,
			boolean implicit) {
			final RGB rgb = (implicit) ? IMPLICIT_ATTRIBUTE : ATTRIBUTE;
			final String id = (implicit) ? "__implicit_attribute" : "__attribute";
			return new AttributeContentType(id, false, rgb,
					CONTENT_DEBUG_BG, type);
	}

	public static ContentType getFormulaContentType(IAttributeType attrType,
			boolean implicit) {
		final RGB rgb = (implicit) ? IMPLICIT_CONTENT : CONTENT;
		final String id = (implicit) ? "__implicit_formula" : "__formula";
		return new AttributeContentType(id, false, rgb, CONTENT_DEBUG_BG,
				attrType);
	}

	/**
	 *  TODO warning this method is unsafe and shall not be used
	 * 	DO NOT USE THIS METHOD
	 */
	public static ContentType getContentType(String name) {
		final ContentType type = typesByName.get(name);
		if (type != null) {
			return type;
		}
		if (name.equals(FORMULA_TYPE.getName()))
			return FORMULA_TYPE;
		if (name.equals(IMPLICIT_FORMULA_TYPE.getName()))
			return IMPLICIT_FORMULA_TYPE;
		if (name.equals(IMPLICIT_ATTRIBUTE_TYPE)) {
			return IMPLICIT_ATTRIBUTE_TYPE;
		}
		return ATTRIBUTE_TYPE;
	}

	private RodinEditor editor;
	private IAnnotationHover annotationHover;
	private ITextHover textHover;

	public RodinConfiguration(RodinEditor editor) {
		this.editor = editor;
	}

	@Override
	public String[] getConfiguredContentTypes(ISourceViewer sourceViewer) {
		return getContentTypeNames();
	}

	@Override
	public IPresentationReconciler getPresentationReconciler(
			ISourceViewer sourceViewer) {
		final PresentationReconciler reconciler = new PresentationReconciler();
		RodinDamagerRepairer rdr;
		for (ContentType contentType : typesByName.values()) {
			rdr = new RodinDamagerRepairer(editor, createTextAttribute(contentType));
			reconciler.setDamager(rdr, contentType.getName());
			reconciler.setRepairer(rdr, contentType.getName());
		}
		for (ContentType contentType : defaultTypes) {
			rdr = new RodinDamagerRepairer(editor, createTextAttribute(contentType));
			reconciler.setDamager(rdr, contentType.getName());
			reconciler.setRepairer(rdr, contentType.getName());
		}
		return reconciler;
	}

	private static TextAttribute createTextAttribute(ContentType type) {
		return new TextAttribute(ColorManager.getDefault().getColor(
				type.getColor()), getBackgroundColor(type), type.getStyles());
	}

	private static Color getBackgroundColor(ContentType type) {
		return (DEBUG) ? ColorManager.getDefault().getColor(
				type.getDebugBackgroundColor()) : null;
	}

	@Override
	public IAnnotationHover getAnnotationHover(ISourceViewer sourceViewer) {
		if (annotationHover == null)
			annotationHover = new RodinProblemAnnotationHover();
		return annotationHover;
	}

	@Override
	public ITextHover getTextHover(ISourceViewer sourceViewer,
			String contentType) {
		if (textHover == null)
			textHover = new RodinProblemTextHover(sourceViewer);
		return textHover;
	}

	public static String[] getContentTypeNames() {
		final Set<String> result = new HashSet<String>();
		result.addAll(typesByName.keySet());
		for (ContentType t : defaultTypes) {
			result.add(t.getName());
		}
		return result.toArray(new String[result.size()]);
	}

}

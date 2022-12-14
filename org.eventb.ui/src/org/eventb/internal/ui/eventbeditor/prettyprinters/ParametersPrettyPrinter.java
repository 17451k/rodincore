/*******************************************************************************
 * Copyright (c) 2006, 2014 ETH Zurich and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     ETH Zurich - initial API and implementation
 *     Systerel - replaced inherited by extended, event variable by parameter
 *     Systerel - separation of file and root element
 *     Systerel - added implicit children for events
 *     Systerel - added theorem attribute of IDerivedPredicateElement
 *     Systerel - fixed bug #2884774 : display guards marked as theorems
 *     Systerel - fixed bug #2936324 : Extends clauses in pretty print
 *     Systerel - Extracted and refactored from AstConverter
 *******************************************************************************/
package org.eventb.internal.ui.eventbeditor.prettyprinters;

import static org.eventb.internal.ui.eventbeditor.htmlpage.CorePrettyPrintUtils.getDirectOrImplicitChildString;
import static org.eventb.ui.prettyprint.PrettyPrintUtils.getHTMLBeginForCSSClass;
import static org.eventb.ui.prettyprint.PrettyPrintUtils.getHTMLEndForCSSClass;
import static org.eventb.ui.prettyprint.PrettyPrintUtils.wrapString;

import org.eventb.core.IEvent;
import org.eventb.core.IParameter;
import org.eventb.internal.ui.eventbeditor.EventBEditorUtils;
import org.eventb.ui.prettyprint.DefaultPrettyPrinter;
import org.eventb.ui.prettyprint.IPrettyPrintStream;
import org.eventb.ui.prettyprint.PrettyPrintAlignments.HorizontalAlignment;
import org.eventb.ui.prettyprint.PrettyPrintAlignments.VerticalAlignement;
import org.rodinp.core.IInternalElement;
import org.rodinp.core.RodinDBException;

public class ParametersPrettyPrinter extends DefaultPrettyPrinter {
	private static final String PARAMETER_IDENTIFIER = "parameterIdentifier";
	private static final String IMPLICIT_PARAMETER_IDENTIFIER = "implicitParameterIdentifier";

	private static final String PARAMETER_IDENTIFIER_SEPARATOR_BEGIN = null;
	private static final String PARAMETER_IDENTIFIER_SEPARATOR_END = null;

	@Override
	public void prettyPrint(IInternalElement elt, IInternalElement parent,
			IPrettyPrintStream ps) {
		if (elt instanceof IParameter) {
			final IParameter param = (IParameter) elt;
			final IEvent ancestor = (IEvent) parent;
			try {
				appendParameterIdentifier(ps, param, ancestor);
			} catch (RodinDBException e) {
				EventBEditorUtils.debugAndLogError(e,
						"Cannot get the identifier string for parameter "
								+ param.getElementName());
			}
		}
	}

	private static void appendParameterIdentifier(IPrettyPrintStream ps,
			IParameter prm, IEvent evt) throws RodinDBException {
		final String identifier = wrapString(prm.getIdentifierString());
		final String bpi = getBeginParameterIdentifier(prm, evt);
		final String epi = getEndParameterIdentifier(prm, evt);
		ps.appendString(identifier, //
				bpi, //
				epi, //
				PARAMETER_IDENTIFIER_SEPARATOR_BEGIN,//
				PARAMETER_IDENTIFIER_SEPARATOR_END);
	}

	private static String getBeginParameterIdentifier(IParameter prm, IEvent evt) {
		return getDirectOrImplicitChildString(evt, prm, //
				getHTMLBeginForCSSClass(PARAMETER_IDENTIFIER,//
						HorizontalAlignment.LEFT, //
						VerticalAlignement.MIDDLE), //
				getHTMLBeginForCSSClass(IMPLICIT_PARAMETER_IDENTIFIER, //
						HorizontalAlignment.LEFT, //
						VerticalAlignement.MIDDLE));
	}

	private static String getEndParameterIdentifier(IParameter prm, IEvent evt) {
		return getDirectOrImplicitChildString(evt, prm, //
				getHTMLEndForCSSClass(PARAMETER_IDENTIFIER, //
						HorizontalAlignment.LEFT, //
						VerticalAlignement.MIDDLE), //
				getHTMLEndForCSSClass(IMPLICIT_PARAMETER_IDENTIFIER, //
						HorizontalAlignment.LEFT, //
						VerticalAlignement.MIDDLE));
	}

}

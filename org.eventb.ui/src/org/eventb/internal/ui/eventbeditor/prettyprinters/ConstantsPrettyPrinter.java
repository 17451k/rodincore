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

import static org.eventb.ui.prettyprint.PrettyPrintUtils.getHTMLBeginForCSSClass;
import static org.eventb.ui.prettyprint.PrettyPrintUtils.getHTMLEndForCSSClass;
import static org.eventb.ui.prettyprint.PrettyPrintUtils.wrapString;

import org.eventb.core.IConstant;
import org.eventb.internal.ui.eventbeditor.EventBEditorUtils;
import org.eventb.ui.prettyprint.DefaultPrettyPrinter;
import org.eventb.ui.prettyprint.IPrettyPrintStream;
import org.eventb.ui.prettyprint.PrettyPrintAlignments.HorizontalAlignment;
import org.eventb.ui.prettyprint.PrettyPrintAlignments.VerticalAlignement;
import org.rodinp.core.IInternalElement;
import org.rodinp.core.RodinDBException;

public class ConstantsPrettyPrinter extends DefaultPrettyPrinter {

	private static String CONSTANT_IDENTIFIER = "constantIdentifier";
	private static final String BEGIN_CONSTANT_IDENTIFIER_SEPARATOR = null;
	private static final String END_CONSTANT_IDENTIFIER_SEPARATOR = null;

	@Override
	public void prettyPrint(IInternalElement elt, IInternalElement parent,
			IPrettyPrintStream ps) {
		if (elt instanceof IConstant) {
			final IConstant cst = (IConstant) elt;
			try {
				appendConstantIdentifier(ps, wrapString(cst
						.getIdentifierString()));
			} catch (RodinDBException e) {
				EventBEditorUtils.debugAndLogError(e,
						"Cannot get the identifier string for constant "
								+ cst.getElementName());
			}
		}
	}

	private void appendConstantIdentifier(IPrettyPrintStream ps,
			String identifier) {
		ps.appendString(
				identifier, //
				getHTMLBeginForCSSClass(CONSTANT_IDENTIFIER, //
						HorizontalAlignment.LEFT, //
						VerticalAlignement.MIDDLE), //
				getHTMLEndForCSSClass(CONSTANT_IDENTIFIER, //
						HorizontalAlignment.LEFT, //
						VerticalAlignement.MIDDLE), //
				BEGIN_CONSTANT_IDENTIFIER_SEPARATOR,
				END_CONSTANT_IDENTIFIER_SEPARATOR);
	}

}

/*******************************************************************************
 * Copyright (c) 2009, 2012 Systerel and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Systerel - initial API and implementation
 *******************************************************************************/
package org.eventb.ui.prover;

import org.eclipse.swt.graphics.Point;
import org.eventb.core.ast.Predicate;

/**
 * A tactic application located inside a formula.
 * <p>
 * Implementors of this interface will be applied through a hyperlink in a
 * hypothesis or goal predicate text.
 * </p>
 * 
 * @author Nicolas Beauger
 * @since 1.1
 */
public interface IPositionApplication extends ITacticApplication {

	/**
	 * Returns the coordinates in the given predicate String where the tactic
	 * hyperlink is to be placed.
	 * <p>
	 * Returned point represents a valid range inside the given predicate
	 * string.
	 * </p>
	 * 
	 * @param parsedString
	 *            the actual String displayed by the prover ui.
	 * @param parsedPredicate
	 *            the result of the parsing of the String, source locations are
	 *            correct but the predicate may not be type-checked
	 * @return a Point with x (inclusive) and y (exclusive) as hyperlink bounds
	 */
	Point getHyperlinkBounds(String parsedString, Predicate parsedPredicate);

	/**
	 * Returns the label associated with this tactic application.
	 * <p>
	 * Defaults to the tooltip provided in the extension point if
	 * <code>null</code>.
	 * </p>
	 * 
	 * @return a label String or <code>null</code>
	 */
	String getHyperlinkLabel();

}

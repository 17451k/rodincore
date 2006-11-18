/*******************************************************************************
 * Copyright (c) 2006 ETH Zurich.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/

package org.eventb.core;

import org.eclipse.core.runtime.IProgressMonitor;
import org.rodinp.core.IInternalElement;
import org.rodinp.core.RodinDBException;

/**
 * Common protocol for Event-B elements that contain an expression.
 * <p>
 * The expression is manipulated as a bare string of characters, as there is no
 * guarantee that it is parseable.
 * </p>
 * <p>
 * This interface is not intended to be implemented by clients.
 * </p>
 * 
 * @author Stefan Hallerstede
 */
public interface IExpressionElement extends IInternalElement {

	/**
	 * Returns the string representation of the expression contained in this
	 * element.
	 * 
	 * @return the expression of this element as a string
	 * @throws RodinDBException
	 *             if there was a problem accessing the database
	 */
	String getExpressionString() throws RodinDBException;

	/**
	 * This method is deprecated; use <code>setExpressionString(IProgressMonitor)</code> instead.
	 * 
	 * Sets the string representation of the expression contained in this
	 * element.
	 * 
	 * @param expression
	 *            the string representation of the expression
	 * @throws RodinDBException
	 *             if there was a problem accessing the database
	 */
	@Deprecated
	void setExpressionString(String expression) throws RodinDBException;

	/**
	 * Sets the string representation of the expression contained in this
	 * element.
	 * 
	 * @param expression
	 *            the string representation of the expression
	 * @param monitor
	 *            a progress monitor, or <code>null</code> if progress
	 *            reporting is not desired
	 * @throws RodinDBException
	 *             if there was a problem accessing the database
	 */
	void setExpressionString(String expression, IProgressMonitor monitor) throws RodinDBException;

}

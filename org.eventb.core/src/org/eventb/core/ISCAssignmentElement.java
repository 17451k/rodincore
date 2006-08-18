/*******************************************************************************
 * Copyright (c) 2006 ETH Zurich.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/

package org.eventb.core;

import org.eventb.core.ast.Assignment;
import org.eventb.core.ast.FormulaFactory;
import org.eventb.core.ast.ITypeEnvironment;
import org.rodinp.core.IInternalElement;
import org.rodinp.core.RodinDBException;

/**
 * Common protocol for Event-B SC elements that contain an assignment.
 * <p>
 * As this element has been statically checked, the contained assignment parses
 * and type-checks. Thus, it can be manipulated directly as an AST.
 * </p>
 * <p>
 * This interface is not intended to be implemented by clients.
 * </p>
 * 
 * @author Stefan Hallerstede
 */
public interface ISCAssignmentElement extends IInternalElement {

	/**
	 * Returns the untyped assignment contained in this element.
	 * 
	 * @param factory
	 *            the formula factory to use for building the result
	 * 
	 * @return the assignment of this element
	 * @throws RodinDBException
	 *             if there was a problem accessing the database
	 */
	Assignment getAssignment(FormulaFactory factory)
			throws RodinDBException;

	/**
	 * Returns the typed assignment contained in this element.
	 * 
	 * @param factory
	 *            the formula factory to use for building the result
	 * 
	 * @param typenv
	 *            the type environment to use for building the result
	 * 
	 * @return the assignment of this element
	 * @throws RodinDBException
	 *             if there was a problem accessing the database
	 */
	Assignment getAssignment(FormulaFactory factory, ITypeEnvironment typenv)
			throws RodinDBException;

	/**
	 * Sets the assignment contained in this element.
	 * 
	 * @param assignment
	 *            the assignment to set (must be type-checked)
	 * @throws RodinDBException
	 *             if there was a problem accessing the database
	 */
	void setAssignment(Assignment assignment) throws RodinDBException;

}

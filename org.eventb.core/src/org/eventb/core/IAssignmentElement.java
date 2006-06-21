/*******************************************************************************
 * Copyright (c) 2006 ETH Zurich.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/

package org.eventb.core;

import org.rodinp.core.IInternalElement;
import org.rodinp.core.RodinDBException;

/**
 * Common protocol for Event-B elements that contain an assignment.
 * <p>
 * The assignment is manipulated as a bare string of characters, as there is no
 * guarantee that it is parseable.
 * </p>
 * <p>
 * This interface is not intended to be implemented by clients.
 * </p>
 * 
 * @author Stefan Hallerstede
 */
public interface IAssignmentElement extends IInternalElement {

	/**
	 * Returns the string representation of the assignment contained in this
	 * element.
	 * 
	 * @return the assignment of this element as a string
	 * @throws RodinDBException
	 *             if there was a problem accessing the database
	 */
	String getAssignmentString() throws RodinDBException;

	/**
	 * Sets the string representation of the assignment contained in this
	 * element.
	 * 
	 * @param assignment
	 *            the string representation of the assignment
	 * @throws RodinDBException
	 *             if there was a problem accessing the database
	 */
	void setAssignmentString(String assignment) throws RodinDBException;

}

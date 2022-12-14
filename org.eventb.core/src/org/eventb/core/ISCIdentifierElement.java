/*******************************************************************************
 * Copyright (c) 2006, 2013 ETH Zurich and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     ETH Zurich - initial API and implementation
 *******************************************************************************/
package org.eventb.core;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eventb.core.ast.FormulaFactory;
import org.eventb.core.ast.FreeIdentifier;
import org.eventb.core.ast.Type;
import org.rodinp.core.IInternalElement;
import org.rodinp.core.RodinDBException;

/**
 * Common protocol for Event-B statically checked identifiers.
 * <p>
 * A checked identifier is guaranteed to parse and has a type associated to it.
 * Contrary to <code>IIdentifierElement</code> the name of an SC identifier is stored
 * by way of the unique element name and cannot be modified once the SC identifier
 * has been created.
 * </p>
 *
 * @author Stefan Hallerstede
 * @since 1.0
 * @noimplement This interface is not intended to be implemented by clients.
 */
public interface ISCIdentifierElement extends IInternalElement {

	/**
	 * Returns the type of this identifier.
	 * 
	 * @param factory
	 *            the formula factory to use for building the result
	 * 
	 * @return the type of this identifier
	 * @throws CoreException
	 *             if there was a problem accessing the database, or if the type
	 *             could not be parsed
	 */
	Type getType(FormulaFactory factory) throws CoreException;

	/**
	 * Sets the type of this element to a new value.
	 * 
	 * @param type
	 *            the type to give to this identifier
	 * @param monitor
	 *            a progress monitor, or <code>null</code> if progress
	 *            reporting is not desired
	 * @throws RodinDBException
	 *             if there was a problem accessing the database
	 */
	void setType(Type type, IProgressMonitor monitor) throws RodinDBException;

	/**
	 * Returns the AST corresponding to this element (a typed free identifier).
	 * 
	 * @param factory
	 *            the formula factory to use for building the result
	 * @return the AST representation of this element
	 * @throws CoreException
	 *             if there was a problem accessing the database, or if the
	 *             identifier is invalid
	 */
	FreeIdentifier getIdentifier(FormulaFactory factory) throws CoreException;
	
	/**
	 * Returns the name of the identifier as a character string.
	 * 
	 * @return name of the identifier as a character string
	 * @throws RodinDBException if there was a problem accessing the database
	 */
	String getIdentifierString() throws RodinDBException;

}

/*******************************************************************************
 * Copyright (c) 2005, 2008 ETH Zurich and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     ETH Zurich - initial API and implementation
 *     Systerel - separation of file and root element
 *******************************************************************************/
package org.eventb.core;

import org.rodinp.core.IFileElementType;
import org.rodinp.core.IRodinElement;
import org.rodinp.core.RodinCore;
import org.rodinp.core.RodinDBException;

/**
 * Common protocol for Event-B (unchecked) contexts.
 * <p>
 * A context has a name that is returned by
 * {@link IRodinElement#getElementName()}.
 * </p>
 * <p>
 * The elements contained in an event-B context are:
 * <ul>
 * <li>extends clauses (<code>IExtendsContext</code>)</li>
 * <li>carrier sets (<code>ICarrierSet</code>)</li>
 * <li>constants (<code>IConstant</code>)</li>
 * <li>axioms (<code>IAxiom</code>)</li>
 * <li>theorems (<code>ITheorem</code>)</li>
 * </ul>
 * </p>
 * <p>
 * In addition to access methods for children elements, also access methods for
 * related file handles are provided.
 * </p>
 * <p>
 * This interface is not intended to be implemented by clients.
 * </p>
 * 
 * @see IRodinElement#getElementName()
 * 
 * @author Laurent Voisin
 */
@Deprecated
public interface IContextFile extends IEventBFile, ICommentedElement, IConfigurationElement {

	IFileElementType ELEMENT_TYPE = 
		RodinCore.getFileElementType(EventBPlugin.PLUGIN_ID + ".contextFileF"); //$NON-NLS-1$

	/**
	 * Returns a handle to a child extends clause with the given element name.
	 * <p>
	 * This is a handle-only method. The child element may or may not be
	 * present.
	 * </p>
	 * 
	 * @param elementName
	 *            element name of the extends clause
	 * @return a handle to a child extends clause with the given element name
	 */
	IExtendsContext getExtendsClause(String elementName);

	/**
	 * Returns an array of all extends clauses of this context.
	 * @return an array of extends clauses
	 * @throws RodinDBException
	 *             if there was a problem accessing the database
	 */
	IExtendsContext[] getExtendsClauses() throws RodinDBException;

	/**
	 * Returns a handle to a child carrier set with the given element name.
	 * <p>
	 * This is a handle-only method. The child element may or may not be
	 * present.
	 * </p>
	 * 
	 * @param elementName
	 *            element name of the carrier set
	 * @return a handle to a child carrier set with the given element name
	 */
	ICarrierSet getCarrierSet(String elementName);

	/**
	 * Returns an array containing all carrier sets of this context.
	 * @return an array of carrier sets
	 * @throws RodinDBException
	 *             if there was a problem accessing the database
	 */
	ICarrierSet[] getCarrierSets() throws RodinDBException;

	/**
	 * Returns a handle to a child constant with the given element name.
	 * <p>
	 * This is a handle-only method. The child element may or may not be
	 * present.
	 * </p>
	 * 
	 * @param elementName
	 *            element name of the constant
	 * @return a handle to a child constant with the given element name
	 */
	IConstant getConstant(String elementName);

	/**
	 * Returns an array containing all constants of this context.
	 * @return an array of constants
	 * @throws RodinDBException
	 *             if there was a problem accessing the database
	 */
	IConstant[] getConstants() throws RodinDBException;

	/**
	 * Returns a handle to a child axiom with the given element name.
	 * <p>
	 * This is a handle-only method. The child element may or may not be
	 * present.
	 * </p>
	 * 
	 * @param elementName
	 *            element name of the axiom
	 * @return a handle to a child axiom with the given element name
	 */
	IAxiom getAxiom(String elementName);

	/**
	 * Returns an array containing all axioms of this context.
	 * @return an array of axioms
	 * @throws RodinDBException
	 *             if there was a problem accessing the database
	 */
	IAxiom[] getAxioms() throws RodinDBException;

	/**
	 * Returns a handle to a child theorem with the given element name.
	 * <p>
	 * This is a handle-only method. The child element may or may not be
	 * present.
	 * </p>
	 * 
	 * @param elementName
	 *            element name of the theorem
	 * @return a handle to a child theorem with the given element name
	 */
	ITheorem getTheorem(String elementName);

	/**
	 * Returns an array containing all theorems of this context.
	 * @return an array of theorems
	 * @throws RodinDBException
	 *             if there was a problem accessing the database
	 */
	ITheorem[] getTheorems() throws RodinDBException;

}

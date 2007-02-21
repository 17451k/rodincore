/*******************************************************************************
 * Copyright (c) 2005-2007 ETH Zurich.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/

package org.eventb.core;

import org.eventb.core.ast.FormulaFactory;
import org.eventb.core.ast.ITypeEnvironment;
import org.rodinp.core.IFileElementType;
import org.rodinp.core.RodinCore;
import org.rodinp.core.RodinDBException;

/**
 * Common protocol for Event-B statically checked (SC) machine files.
 * <p>
 * An SC machine file has a name that is returned by
 * {@link org.rodinp.core.IRodinElement#getElementName()}.
 * </p>
 * The elements contained in an event-B SC machine file are:
 * <ul>
 * <li>at most one refines clause (<code>ISCRefinesMachine</code>)</li>
 * <li>sees clauses (<code>ISCSeesContext</code>)</li>
 * <li>internal contexts (<code>ISCInternalContext</code>)</li>
 * <li>variables (<code>ISCVariable</code>)</li>
 * <li>invariants (<code>ISCInvariant</code>)</li>
 * <li>theorems (<code>ISCTheorem</code>)</li>
 * <li>events (<code>ISCEvent</code>)</li>
 * <li>at most one variant (<code>ISCVariant</code>)</li>
 * </ul>
 * </p>
 * <p>
 * The internal contexts are a local copy of the contents of the contexts seen,
 * directly or indirectly, by this machine. The other child elements of this
 * machine are the SC versions of the elements of the unchecked version of this
 * machine.
 * In addition, access methods for related file handles are provided.
 * </p>
 * <p>
 * This interface is not intended to be implemented by clients.
 * </p>
 * 
 * @see org.rodinp.core.IRodinElement#getElementName()
 * 
 * @author Stefan Hallerstede
 */
public interface ISCMachineFile extends IEventBFile {

	IFileElementType<ISCMachineFile> ELEMENT_TYPE = 
		RodinCore.getFileElementType(EventBPlugin.PLUGIN_ID + ".scMachineFile"); //$NON-NLS-1$

	/**
	 * Returns a handle to the statically checked version of the abstraction of
	 * this machine or <code>null</code> if there is no abstraction (case of a
	 * top level machine).
	 * 
	 * @return a handle to the statically checked version of the abstraction, or
	 *         <code>null</code>
	 * @throws RodinDBException
	 *             if there was a problem accessing the database
	 * @deprecated use <code>getAbstractSCMachines(IProgressMonitor)</code> instead
	 */
	@Deprecated
	ISCMachineFile getAbstractSCMachine() throws RodinDBException;

	/**
	 * Returns a handle to the statically checked versions of the abstractions of
	 * this SC machine.
	 * 
	 * @return a array of handles to the statically checked versions of the abstractions
	 * @throws RodinDBException
	 *             if there was a problem accessing the database
	 */
	ISCMachineFile[] getAbstractSCMachines() throws RodinDBException;

	/**
	 * Returns a handle to a child SC refines clause with the given element name.
	 * <p>
	 * This is a handle-only method. The child element may or may not be
	 * present.
	 * </p>
	 * 
	 * @param elementName
	 *            element name of the SC refines clause
	 * @return a handle to a child SC refines clause with the given element name
	 */
	ISCRefinesMachine getSCRefinesClause(String elementName);

	/**
	 * Returns an array of all SC refines clauses of this SC machine.
	 * @return an array of SC refines clauses
	 * @throws RodinDBException
	 *             if there was a problem accessing the database
	 */
	ISCRefinesMachine[] getSCRefinesClauses() throws RodinDBException;

	/**
	 * Returns a handle to a child SC internal context with the given element name.
	 * <p>
	 * This is a handle-only method. The child element may or may not be
	 * present.
	 * </p>
	 * 
	 * @param elementName
	 *            element name of the SC internal context
	 * @return a handle to a child SC internal context with the given element name
	 */
	ISCInternalContext getSCSeenContext(String elementName);

	/**
	 * Returns the internal SC contexts that are (transitively) seen by this SC
	 * machine.
	 * 
	 * @return an array of all internal contexts
	 * @throws RodinDBException
	 *             if there was a problem accessing the database
	 */
	ISCInternalContext[] getSCSeenContexts() 
	throws RodinDBException;

	/**
	 * Returns a handle to a child SC sees clause with the given element name.
	 * <p>
	 * This is a handle-only method. The child element may or may not be
	 * present.
	 * </p>
	 * 
	 * @param elementName
	 *            element name of the SC sees clause
	 * @return a handle to a child SC sees clause with the given element name
	 */
	ISCSeesContext getSCSeesClause(String elementName);

	/**
	 * Returns an array of all SC sees clauses of this SC machine.
	 * @return an array of SC sees clauses
	 * @throws RodinDBException
	 *             if there was a problem accessing the database
	 */
	ISCSeesContext[] getSCSeesClauses() throws RodinDBException;

	/**
	 * Returns a handle to a child SC variable with the given element name.
	 * <p>
	 * This is a handle-only method. The child element may or may not be
	 * present.
	 * </p>
	 * 
	 * @param elementName
	 *            element name of the SC variable
	 * @return a handle to a child SC variable with the given element name
	 */
	ISCVariable getSCVariable(String elementName);

	/**
	 * Returns an array containing all SC variables of this SC machine.
	 * 
	 * @return an array of all SC variables
	 * @throws RodinDBException
	 *             if there was a problem accessing the database
	 */
	ISCVariable[] getSCVariables() throws RodinDBException;

	/**
	 * Returns a handle to a child SC invariant with the given element name.
	 * <p>
	 * This is a handle-only method. The child element may or may not be
	 * present.
	 * </p>
	 * 
	 * @param elementName
	 *            element name of the SC invariant
	 * @return a handle to a child SC invariant with the given element name
	 */
	ISCInvariant getSCInvariant(String elementName);

	/**
	 * Returns an array containing all SC invariants of this SC machine.
	 * 
	 * @return an array of all SC invariants
	 * @throws RodinDBException
	 *             if there was a problem accessing the database
	 */
	ISCInvariant[] getSCInvariants() throws RodinDBException;

	/**
	 * Returns a handle to a child SC theorem with the given element name.
	 * <p>
	 * This is a handle-only method. The child element may or may not be
	 * present.
	 * </p>
	 * 
	 * @param elementName
	 *            element name of the SC theorem
	 * @return a handle to a child SC theorem with the given element name
	 */
	ISCTheorem getSCTheorem(String elementName);

	/**
	 * Returns an array containing all SC theorems of this SC machine.
	 * 
	 * @return an array of all SC theorems
	 * @throws RodinDBException
	 *             if there was a problem accessing the database
	 */
	ISCTheorem[] getSCTheorems() throws RodinDBException;

	/**
	 * Returns a handle to a child SC event with the given element name.
	 * <p>
	 * This is a handle-only method. The child element may or may not be
	 * present.
	 * </p>
	 * 
	 * @param elementName
	 *            element name of the SC event
	 * @return a handle to a child SC event with the given element name
	 */
	ISCEvent getSCEvent(String elementName);

	/**
	 * Returns the array containing all SC events of this SC machine.
	 * 
	 * @return the array of all SC events
	 * @throws RodinDBException
	 *             if there was a problem accessing the database
	 */
	ISCEvent[] getSCEvents() throws RodinDBException;

	/**
	 * Returns a handle to a child SC variant with the given element name.
	 * <p>
	 * This is a handle-only method. The child element may or may not be
	 * present.
	 * </p>
	 * 
	 * @param elementName
	 *            element name of the SC variant
	 * @return a handle to a child SC variant with the given element name
	 */
	ISCVariant getSCVariant(String elementName);
	
	/**
	 * Returns the handle of the SC variant of this SC machine.
	 * 
	 * @return the handle of the SC variant
	 * @throws RodinDBException
	 *             if there was a problem accessing the database
	 * @deprecated use <code>getSCVariants(IProgressMonitor)</code> instead
	 */
	@Deprecated
	ISCVariant getSCVariant() throws RodinDBException;
	
	/**
	 * Returns the array containing all SC variants of this SC machine.
	 * 
	 * @return the array of all SC variants
	 * @throws RodinDBException
	 *             if there was a problem accessing the database
	 */
	ISCVariant[] getSCVariants() throws RodinDBException;
	
	/**
	 * Returns the handle of the refines clause of this SC machine.
	 * 
	 * @return the handle of the refines clause
	 * @throws RodinDBException if there was a problem accessing the database
	 * @deprecated use <code>getSCRefinesClause(IProgressMonitor)</code> instead
	 */
	@Deprecated
	ISCRefinesMachine getRefinesClause() throws RodinDBException;

	/**
	 * Returns the type environment defined by this machine file. The returned
	 * type environment is made of all carrier sets and constants defined in the
	 * seen contexts and their abstractions, together with the variables
	 * declared in this machine and its abstractions.
	 * 
	 * <p>
	 * It can be used subsequently to type-check the invariants and theorems of
	 * this machine. It can also be used as a basis for building the type
	 * environment of an event of this machine.
	 * </p>
	 * 
	 * @param factory
	 *            formula factory to use for building the result
	 * @return the type environment of this machine
	 * @throws RodinDBException
	 *             if there was a problem accessing the database
	 */
	ITypeEnvironment getTypeEnvironment(FormulaFactory factory)
			throws RodinDBException;

}

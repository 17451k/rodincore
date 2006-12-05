/*******************************************************************************
 * Copyright (c) 2005, 2006 ETH Zurich.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/

package org.eventb.core;

import org.eclipse.core.runtime.IProgressMonitor;
import org.rodinp.core.IInternalElementType;
import org.rodinp.core.RodinCore;
import org.rodinp.core.RodinDBException;

/**
 * Common protocol for events in Event-B statically checked (SC) files.
 * <p>
 * An SC event has a label that is accessed and manipulated via
 * {@link ILabeledElement}.
 * </p>
 * <p>
 * SC events are similar to events ({@link org.eventb.core.IEvent}) except
 * that they contain statically checked elements:
 * <ul>
 * <li>refines clauses (<code>ISCRefinesEvent</code>)</li>
 * <li>local variables (<code>ISCVariable</code>)</li>
 * <li>witnesses (<code>ISCWitness</code>)</li>
 * <li>guards (<code>ISCGuard</code>)</li>
 * <li>actions (<code>ISCAction</code>)</li>
 * </ul>
 * </p>
 * <p>
 * This interface is not intended to be implemented by clients.
 * </p>
 * 
 * @see ILabeledElement#getLabel()
 * @see ILabeledElement#setLabel(String, IProgressMonitor)
 * 
 * @author Stefan Hallerstede
 */
public interface ISCEvent extends ITraceableElement, ILabeledElement, IConvergenceElement {

	IInternalElementType ELEMENT_TYPE =
		RodinCore.getInternalElementType(EventBPlugin.PLUGIN_ID + ".scEvent"); //$NON-NLS-1$

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
	ISCRefinesEvent getSCRefinesClause(String elementName);

	/**
	 * Returns an array of all SC refines clauses of this SC event.
	 * 
	 * @return an array of all SC refines clauses
	 * @throws RodinDBException
	 *             if there was a problem accessing the database
	 */
	ISCRefinesEvent[] getSCRefinesClauses() throws RodinDBException;

	/**
	 * Returns an array of all SC events refined by this SC event.
	 * <p>
	 * This is a convenience method. It fetches all refines clauses of this
	 * events and gets the handles of the abstract events from there.
	 * </p>
	 * 
	 * @return an array of all abstract SC events
	 * @throws RodinDBException
	 *             if there was a problem accessing the database
	 */
	ISCEvent[] getAbstractSCEvents() throws RodinDBException;

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
	 * Returns an array containing all SC (local) variables of this SC event.
	 * 
	 * @return an array of all SC variables
	 * @throws RodinDBException
	 *             if there was a problem accessing the database
	 */
	ISCVariable[] getSCVariables() throws RodinDBException;

	/**
	 * Returns a handle to a child SC witness with the given element name.
	 * <p>
	 * This is a handle-only method. The child element may or may not be
	 * present.
	 * </p>
	 * 
	 * @param elementName
	 *            element name of the SC witness
	 * @return a handle to a child SC witness with the given element name
	 */
	ISCWitness getSCWitness(String elementName);

	/**
	 * Returns an array of all SC witnesses of this SC event.
	 * 
	 * @return an array of all SC witnesses
	 * @throws RodinDBException
	 *             if there was a problem accessing the database
	 */
	ISCWitness[] getSCWitnesses() throws RodinDBException;

	/**
	 * Returns a handle to a child SC guard with the given element name.
	 * <p>
	 * This is a handle-only method. The child element may or may not be
	 * present.
	 * </p>
	 * 
	 * @param elementName
	 *            element name of the SC guard
	 * @return a handle to a child SC guard with the given element name
	 */
	ISCGuard getSCGuard(String elementName);

	/**
	 * Returns an array containing all SC guards of this SC event.
	 * 
	 * @return an array of all SC guards
	 * @throws RodinDBException
	 *             if there was a problem accessing the database
	 */
	ISCGuard[] getSCGuards() throws RodinDBException;

	/**
	 * Returns a handle to a child SC action with the given element name.
	 * <p>
	 * This is a handle-only method. The child element may or may not be
	 * present.
	 * </p>
	 * 
	 * @param elementName
	 *            element name of the SC action
	 * @return a handle to a child SC action with the given element name
	 */
	ISCAction getSCAction(String elementName);

	/**
	 * Returns an array containing all SC actions of this SC event.
	 * 
	 * @return an array of all SC actions
	 * @throws RodinDBException
	 *             if there was a problem accessing the database
	 */
	ISCAction[] getSCActions() throws RodinDBException;
	
	/**
	 * An event label that has been used in an abstraction but not in some refinement
	 * cannot be used again. It is "forbidden".
	 * 
	 * @param value the "forbidden" status of the event label
	 * @deprecated no longer supported
	 */
	@Deprecated
	void setForbidden(boolean value) throws RodinDBException;
	
	/**
	 * An event label that has been used in an abstraction but not in some refinement
	 * cannot be used again. It is "forbidden".
	 * 
	 * @param value the "forbidden" status of the event label
	 * @param monitor
	 *            a progress monitor, or <code>null</code> if progress
	 *            reporting is not desired
	 * @throws RodinDBException
	 *             if there was a problem accessing the database
	 * @deprecated no longer supported
	 */
	@Deprecated
	void setForbidden(boolean value, IProgressMonitor monitor) throws RodinDBException;
	
	/**
	 * Returns whether the event label is forbidden or not.
	 * 
	 * @return whether the event label is forbidden or not
	 * @throws RodinDBException
	 *             if there was a problem accessing the database
	 * @deprecated no longer supported
	 */
	@Deprecated
	boolean isForbidden() throws RodinDBException;

}

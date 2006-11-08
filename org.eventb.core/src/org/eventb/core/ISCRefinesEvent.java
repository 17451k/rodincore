/*******************************************************************************
 * Copyright (c) 2006 ETH Zurich.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/

package org.eventb.core;

import org.rodinp.core.IInternalElement;
import org.rodinp.core.IInternalElementType;
import org.rodinp.core.RodinCore;
import org.rodinp.core.RodinDBException;

/**
 * Common protocol for a refines clause of an event.
 * <p>
 * A refines element has a name that is returned by
 * {@link org.rodinp.core.IRodinElement#getElementName()}.
 * </p>
 * <p>
 * This interface describes a statically checked version of
 * {@link org.eventb.core.IRefinesEvent}. The value stored in an
 * <code>ISCRefinesEvent</code> is a handle of the abstract SC event.
 * </p>
 * <p>
 * This interface is not intended to be implemented by clients.
 * </p>
 * 
 * @see org.rodinp.core.IRodinElement#getElementName()
 * 
 * @author Stefan Hallerstede
 */
public interface ISCRefinesEvent extends ITraceableElement, IInternalElement {

	IInternalElementType ELEMENT_TYPE =
		RodinCore.getInternalElementType(EventBPlugin.PLUGIN_ID + ".scRefinesEvent"); //$NON-NLS-1$

	/**
	 * Returns the abstract event introduced by this refines clause.
	 * 
	 * @return the abstract SC event
	 * @throws RodinDBException
	 *             if there was a problem accessing the database
	 */
	ISCEvent getAbstractSCEvent() throws RodinDBException;

	/**
	 * Sets the abstract event introduced by this refines clause.
	 * 
	 * @param abstractSCEvent
	 *            the abstract event
	 * @throws RodinDBException
	 *             if there was a problem accessing the database
	 */
	void setAbstractSCEvent(ISCEvent abstractSCEvent) throws RodinDBException;

}

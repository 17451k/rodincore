/*******************************************************************************
 * Copyright (c) 2006 ETH Zurich.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/

package org.eventb.core.basis;

import org.eventb.core.ISCEvent;
import org.eventb.core.ISCRefinesEvent;
import org.eventb.internal.core.Messages;
import org.eventb.internal.core.Util;
import org.rodinp.core.IInternalElementType;
import org.rodinp.core.IRodinElement;
import org.rodinp.core.RodinCore;
import org.rodinp.core.RodinDBException;

/**
 * Implementation of Event-B SC refines clauses for events, as an extension of
 * the Rodin database.
 * <p>
 * This class is intended to be subclassed by clients that want to extend this
 * internal element type.
 * </p>
 * <p>
 * This class should not be used in any other way than subclassing it in a
 * database extension. In particular, clients should not use it, but rather use
 * its associated interface <code>ISCRefinesEvent</code>.
 * </p>
 * 
 * @author Stefan Hallerstede
 */
public class SCRefinesEvent extends SCTraceableLabeledElement implements ISCRefinesEvent {

	/**
	 *  Constructor used by the Rodin database. 
	 */
	public SCRefinesEvent(String name, IRodinElement parent) {
		super(name, parent);
	}

	@Override
	public IInternalElementType getElementType() {
		return ELEMENT_TYPE;
	}

	public ISCEvent getAbstractSCEvent() throws RodinDBException {
		String contents = getContents();
		IRodinElement target = RodinCore.valueOf(contents);
		if (! (target instanceof ISCEvent)) {
			throw Util.newRodinDBException(
					Messages.database_SCRefinesEventTypeFailure,
					this);
		}
		return (ISCEvent) target;
	}

	public void setAbstractSCEvent(ISCEvent abstractSCEvent) throws RodinDBException {
		setContents(abstractSCEvent.getHandleIdentifier());
	}

}

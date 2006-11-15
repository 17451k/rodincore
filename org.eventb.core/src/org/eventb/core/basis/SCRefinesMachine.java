/*******************************************************************************
 * Copyright (c) 2006 ETH Zurich.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/

package org.eventb.core.basis;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eventb.core.EventBAttributes;
import org.eventb.core.ISCMachineFile;
import org.eventb.core.ISCRefinesMachine;
import org.eventb.internal.core.Messages;
import org.eventb.internal.core.Util;
import org.rodinp.core.IInternalElementType;
import org.rodinp.core.IRodinElement;
import org.rodinp.core.RodinDBException;

/**
 * Implementation of Event-B SC refines clause for a machine, as an extension of
 * the Rodin database.
 * <p>
 * This class is intended to be subclassed by clients that want to extend this
 * internal element type.
 * </p>
 * <p>
 * This class should not be used in any other way than subclassing it in a
 * database extension. In particular, clients should not use it, but rather use
 * its associated interface <code>ISCRefinesMachine</code>.
 * </p>
 * 
 * @author Stefan Hallerstede
 */
public class SCRefinesMachine extends EventBElement implements
		ISCRefinesMachine {

	/**
	 *  Constructor used by the Rodin database. 
	 */
	public SCRefinesMachine(String name, IRodinElement parent) {
		super(name, parent);
	}

	@Override
	public IInternalElementType getElementType() {
		return ELEMENT_TYPE;
	}

	private IRodinElement getAbstractSCMachineHandle(IProgressMonitor monitor) throws RodinDBException {
		return getAttributeValue(EventBAttributes.SCTARGET_ATTRIBUTE, monitor);
	}

	public ISCMachineFile getAbstractSCMachine(IProgressMonitor monitor) throws RodinDBException {
		IRodinElement target = getAbstractSCMachineHandle(monitor);
		if (! (target instanceof ISCMachineFile)) {
			throw Util.newRodinDBException(
					Messages.database_SCRefinesMachineTypeFailure,
					this);
		}
		return (ISCMachineFile) target;
	}

	public void setAbstractSCMachine(ISCMachineFile abstractSCMachine, IProgressMonitor monitor) 
	throws RodinDBException {
		setAttributeValue(EventBAttributes.SCTARGET_ATTRIBUTE, abstractSCMachine, monitor);
	}

	@Deprecated
	public ISCMachineFile getAbstractSCMachine() throws RodinDBException {
		return getAbstractSCMachine(null);
	}

	@Deprecated
	public void setAbstractSCMachine(ISCMachineFile abstractSCMachine) throws RodinDBException {
		setAbstractSCMachine(abstractSCMachine, null);
	}
}

/*******************************************************************************
 * Copyright (c) 2006, 2013 ETH Zurich and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     ETH Zurich - initial API and implementation
 *     Systerel - separation of file and root element
 *******************************************************************************/
package org.eventb.core.basis;

import static org.eventb.internal.core.Util.newCoreException;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eventb.core.EventBAttributes;
import org.eventb.core.ISCMachineRoot;
import org.eventb.core.ISCRefinesMachine;
import org.eventb.internal.core.Messages;
import org.rodinp.core.IInternalElementType;
import org.rodinp.core.IRodinElement;
import org.rodinp.core.IRodinFile;
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
 * @since 1.0
 * @noinstantiate This class is not intended to be instantiated by clients.
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
	public IInternalElementType<ISCRefinesMachine> getElementType() {
		return ELEMENT_TYPE;
	}

	private IRodinElement getAbstractSCMachineHandle() throws RodinDBException {
		return getAttributeValue(EventBAttributes.SCTARGET_ATTRIBUTE);
	}

	@Override
	public IRodinFile getAbstractSCMachine() throws CoreException {
		IRodinElement target = getAbstractSCMachineHandle();
		if (target instanceof IRodinFile) {
			IRodinFile rf = (IRodinFile) target;
			if (rf.getRoot() instanceof ISCMachineRoot) {
				return rf;
			}
		}
		throw newCoreException(
				Messages.database_SCRefinesMachineTypeFailure, this);
	}

	@Override
	public void setAbstractSCMachine(IRodinFile abstractSCMachine, IProgressMonitor monitor) 
	throws RodinDBException {
		setAttributeValue(EventBAttributes.SCTARGET_ATTRIBUTE, abstractSCMachine, monitor);
	}
}

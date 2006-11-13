/*******************************************************************************
 * Copyright (c) 2006 ETH Zurich.
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
 * Common protocol for a refines clause of a machine.
 * <p>
 * A refines element has a name that is returned by
 * {@link org.rodinp.core.IRodinElement#getElementName()}.
 * </p>
 * <p>
 * This interface describes a statically checked version of
 * {@link org.eventb.core.IRefinesMachine}. The value stored in an
 * <code>ISCRefinesMachine</code> is a handle of the abstract SC machine.
 * </p>
 * <p>
 * This interface is not intended to be implemented by clients.
 * </p>
 * 
 * @see org.rodinp.core.IRodinElement#getElementName()
 * 
 * @author Stefan Hallerstede
 */
public interface ISCRefinesMachine extends ITraceableElement {
	
	IInternalElementType ELEMENT_TYPE =
		RodinCore.getInternalElementType(EventBPlugin.PLUGIN_ID + ".scRefinesMachine"); //$NON-NLS-1$

	/**
	 * Returns the abstract SC machine file introduced by this refines clause.
	 * 
	 * @return the abstract SC machine file
	 * @throws RodinDBException
	 *             if there was a problem accessing the database
	 * @deprecated use <code>getAbstractSCMachine(IProgressMonitor)</code> instead
	 */
	@Deprecated
	ISCMachineFile getAbstractSCMachine() throws RodinDBException;

	/**
	 * Returns the abstract SC machine file introduced by this refines clause.
	 * 
	 * @param monitor
	 *            a progress monitor, or <code>null</code> if progress
	 *            reporting is not desired
	 * @return the abstract SC machine file
	 * @throws RodinDBException
	 *             if there was a problem accessing the database
	 */
	ISCMachineFile getAbstractSCMachine(IProgressMonitor monitor) throws RodinDBException;

	/**
	 * Sets the abstract SC machine file introduced by this refines clause.
	 * 
	 * @param abstractSCMachine
	 *            the abstract machine file
	 * @throws RodinDBException
	 *             if there was a problem accessing the database
	 * @deprecated use <code>setAbstractSCMachine(ISCMachineFile,IProgressMonitor)</code> instead
	 */
	@Deprecated
	void setAbstractSCMachine(ISCMachineFile abstractSCMachine) throws RodinDBException;

	/**
	 * Sets the abstract SC machine file introduced by this refines clause.
	 * 
	 * @param abstractSCMachine
	 *            the abstract machine file
	 * @param monitor
	 *            a progress monitor, or <code>null</code> if progress
	 *            reporting is not desired
	 * @throws RodinDBException
	 *             if there was a problem accessing the database
	 */
	void setAbstractSCMachine(ISCMachineFile abstractSCMachine, IProgressMonitor monitor) throws RodinDBException;

}

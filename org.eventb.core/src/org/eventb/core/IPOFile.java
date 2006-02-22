/*******************************************************************************
 * Copyright (c) 2005 ETH Zurich.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/

package org.eventb.core;

import org.rodinp.core.IRodinFile;
import org.rodinp.core.RodinDBException;

/**
 * Common protocol for Event-B Proof Obligation (PO) files.
 * <p>
 * This interface is not intended to be implemented by clients.
 * </p>
 *
 * @author Stefan Hallerstede
 *
 */
public interface IPOFile extends IRodinFile {

	public String ELEMENT_TYPE = EventBPlugin.PLUGIN_ID + ".poFile"; //$NON-NLS-1$
	
	/**
	 * Returns a handle to the checked version of the context for which this
	 * proof obligation file has been generated.
	 * <p>
	 * This is a handle-only operation.
	 * </p>
	 * 
	 * @return a handle to the checked version of the corresponding context
	 */
	public ISCContext getCheckedContext();

	/**
	 * Returns a handle to the checked version of the machine for which this
	 * proof obligation file has been generated.
	 * <p>
	 * This is a handle-only operation.
	 * </p>
	 * 
	 * @return a handle to the checked version of the corresponding machine
	 */
	public ISCMachine getCheckedMachine();

	/**
	 * Returns a handle to the file containing proofs for this component.
	 * <p>
	 * This is a handle-only operation.
	 * </p>
	 * 
	 * @return a handle to the proof file of this component
	 */
	public IPRFile getPRFile();

	public IPOPredicateSet getPredicateSet(String name) throws RodinDBException;
	public IPOIdentifier[] getIdentifiers() throws RodinDBException;
	public IPOSequent[] getSequents() throws RodinDBException;
}

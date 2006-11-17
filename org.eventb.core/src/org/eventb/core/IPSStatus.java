/*******************************************************************************
 * Copyright (c) 2005 ETH Zurich.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/

package org.eventb.core;


import org.eclipse.core.runtime.IProgressMonitor;
import org.rodinp.core.IInternalElement;
import org.rodinp.core.IInternalElementType;
import org.rodinp.core.RodinCore;
import org.rodinp.core.RodinDBException;

/**
 * Common protocol for Proof Status elements in Event-B Proof Status (PS) files.
 *
 * <p>
 * The convention used for associating proof obligations (IPOSequent) in the PO file to 
 * proof status elements (IPSStatus) in the PS file, and proofs (in the PR file) is that 
 * they all have the identical element name.
 * </p>
 * 
 * <p>
 * This interface is not intended to be implemented by clients.
 * </p>
 * 
 * @see #getElementName() , IPOSequent, IPRProof 
 * 
 * @author Farhad Mehta
 *
 */
public interface IPSStatus extends IInternalElement {
	
	IInternalElementType ELEMENT_TYPE =
		RodinCore.getInternalElementType(EventBPlugin.PLUGIN_ID + ".psStatus"); //$NON-NLS-1$
	
	/**
	 * Returns the proof associated to this proof obligation from the
	 * RODIN database.
	 * <p>
	 * This is a handle-only method. The proof element may or may not be
	 * present.
	 * </p>
	 * 
	 * @return a handle to the proof associated to this status element
	 * 
	 * @throws RodinDBException
	 */
	IPRProof getProof() throws RodinDBException;
	
	/**
	 * Returns the proof obligation associated to this status element from the
	 * RODIN database.
	 * <p>
	 * This is a handle-only method. The returned element may or may not be
	 * present.
	 * </p>
	 *  
	 * @return the IPOSequent associated to this proof obligation from the
	 * RODIN database.
	 *
	 * @throws RodinDBException
	 */
	IPOSequent getPOSequent() throws RodinDBException;
	
	/**
	 * Returns the value stored in the proof validity attribute of this 
	 * proof status element.
	 * <p>
	 * When consistent, the proof validity attribute is <code>true</code>
	 * iff the proof stored in the associated proof element is valid (or 
	 * applicable) for the associated proof obligation.
	 * </p>
	 *
	 * @param monitor
	 *            a progress monitor, or <code>null</code> if progress
	 *            reporting is not desired
	 * @return The value stored in the proof validity attribute of this 
	 * proof status element.
	 * 
	 * @throws RodinDBException
	 */
	boolean getProofValidAttribute(IProgressMonitor monitor) throws RodinDBException;
	
	/**
	 * Sets the value stored in the proof validity attribute of this 
	 * proof status element.
	 * <p>
	 * When consistent, the proof validity attribute is <code>true</code>
	 * iff the proof stored in the associated proof element is valid (or 
	 * applicable) for the associated proof obligation.
	 * </p>
	 *
	 * @param valid
	 * 			The value to set to
	 * @param monitor
	 *            a progress monitor, or <code>null</code> if progress
	 *            reporting is not desired
	 * 
	 * @throws RodinDBException
	 */
	void setProofValidAttribute(boolean valid, IProgressMonitor monitor) throws RodinDBException;
	
	/**
	 * Returns the value stored in the confidence attribute of this 
	 * proof status element.
	 * <p>
	 * When consistent, the confidence attribute is identical to the
	 * confidence attribute stored in the associated proof element.
	 * </p>
	 *
	 * @param monitor
	 *            a progress monitor, or <code>null</code> if progress
	 *            reporting is not desired
	 * @return The value stored in the confidence attribute of this 
	 * proof status element.
	 * 
	 * @throws RodinDBException
	 */
	int getProofConfidence(IProgressMonitor monitor) throws RodinDBException;

	/**
	 * Sets value stored in the confidence attribute of this 
	 * proof status element.
	 * <p>
	 * When consistent, the confidence attribute is identical to the
	 * confidence attribute stored in the associated proof element.
	 * </p>
	 *
	 * @param confidence
	 * 			The confidence value to set
	 *
	 * @param monitor
	 *            a progress monitor, or <code>null</code> if progress
	 *            reporting is not desired
	 * 
	 * @throws RodinDBException
	 */
	void setProofConfidence(int confidence, IProgressMonitor monitor) throws RodinDBException;

	
	/**
	 * Returns whether an auto proof attribute is present in this 
	 * proof status element.
	 * <p>
	 * The presence of an auto proof attribute means that the auto prover
	 * has been attemptd on the associated proof obligation
	 * </p>
	 *
	 * @param monitor
	 *            a progress monitor, or <code>null</code> if progress
	 *            reporting is not desired
	 * @return <code>true</code> iff an auto proof attribute is present in this 
	 * proof status element.
	 * @throws RodinDBException
	 */
	boolean hasAutoProofAttribute(IProgressMonitor monitor) throws RodinDBException;
	
	/**
	 * Returns the value stored in the auto proof attribute of this 
	 * proof status element.
	 * <p>
	 * This attribute may be absent if the auto prover has not been attempted.
	 * In this case a check with {@link #hasAutoProofAttribute(IProgressMonitor)} 
	 * is needed to avoid a RodinDBException.
	 * </p>
	 * <p>
	 * The value of this attribute is <code>true</code> iff the associated proof 
	 * element was generated by the auto prover.
	 * </p>
	 *
	 * @param monitor
	 *            a progress monitor, or <code>null</code> if progress
	 *            reporting is not desired
	 * @return The value stored in the auto proof attribute of this 
	 * proof status element.
	 * 
	 * @throws RodinDBException
	 */
	boolean getAutoProofAttribute(IProgressMonitor monitor) throws RodinDBException;
	
	/**
	 * Sets the value stored in the auto proof attribute of this 
	 * proof status element.
	 * <p>
	 * The value of this attribute is <code>true</code> iff the associated proof 
	 * element was generated by the auto prover.
	 * </p>
	 *
	 * @param autoProof
	 * 			The value to set to
	 * @param monitor
	 *            a progress monitor, or <code>null</code> if progress
	 *            reporting is not desired
	 * 
	 * @throws RodinDBException
	 */
	void setAutoProofAttribute(boolean autoProof, IProgressMonitor monitor) throws RodinDBException;
	
}

/*******************************************************************************
 * Copyright (c) 2005 ETH Zurich.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/

package org.eventb.core;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eventb.core.basis.POSequent;
import org.eventb.core.basis.PRProof;
import org.eventb.core.seqprover.IConfidence;
import org.rodinp.core.IInternalElement;
import org.rodinp.core.IInternalElementType;
import org.rodinp.core.RodinCore;
import org.rodinp.core.RodinDBException;

/**
 * Common protocol for Proof Status elements in Event-B Proof Status (PS) files.
 * 
 * <p>
 * The convention used for associating proof obligations (IPOSequent) in the PO
 * file to proof status elements (IPSStatus) in the PS file, and proofs (in the
 * PR file) is that they all have the identical element name.
 * </p>
 * 
 * <p>
 * Some attributes in this internal element (i.e. confidence and hasManualProof) 
 * are cached versions of the corresponding proof in case it exists. Clients writing
 * into the PSFile must make sure that they are identical to the attributes stored in 
 * the corresponding proof. 
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
public interface IPSStatus extends IInternalElement, IPRProofInfoElement, IPOStampedElement, IPRStampedElement {

	IInternalElementType<IPSStatus> ELEMENT_TYPE = RodinCore
			.getInternalElementType(EventBPlugin.PLUGIN_ID + ".psStatus"); //$NON-NLS-1$

	/**
	 * Returns the proof associated to this proof obligation from the RODIN
	 * database.
	 * <p>
	 * This is a handle-only method. The proof element may or may not be
	 * present.
	 * </p>
	 * 
	 * @return the proof associated to this status element
	 */
	IPRProof getProof();

	/**
	 * Returns the proof obligation associated to this status element from the
	 * RODIN database.
	 * <p>
	 * This is a handle-only method. The returned element may or may not be
	 * present.
	 * </p>
	 * 
	 * @return the sequent associated to this proof obligation
	 */
	IPOSequent getPOSequent();

	/**
	 * Returns whether this proof obligation has a broken proof, that is a proof
	 * that does not match its sequent, and thus can't be used to discharge the
	 * proof obligation.
	 * <p>
	 * The returned value is <code>true</code> iff the corresponding attribute
	 * contains <code>true</code>. Hence, if the attribute is absent,
	 * <code>false</code> is returned.
	 * </p>
	 * 
	 * @return <code>true</code> if the associated proof is broken
	 * 
	 * @throws RodinDBException
	 * @see #setBroken(boolean, IProgressMonitor)
	 */
	boolean isBroken() throws RodinDBException;

	/**
	 * Sets whether this proof obligation has a broken proof.
	 * 
	 * @param value
	 *            The value to set to
	 * @param monitor
	 *            a progress monitor, or <code>null</code> if progress
	 *            reporting is not desired
	 * 
	 * @throws RodinDBException
	 * @see #isBroken()
	 */
	void setBroken(boolean value, IProgressMonitor monitor)
			throws RodinDBException;

	/**
	 * Returns the confidence associated to this proof obligation. The
	 * confidence is stored in an attribute which contains a local copy of the
	 * confidence attribute of the associated proof. If there is no associated
	 * proof yet, then the confidence attribute is not set and this method
	 * returns {@link IConfidence#UNATTEMPTED}.
	 * 
	 * <p>
	 * This is a cached attribute in case there is an associated proof.
	 * </p>
	 * 
	 * @return the confidence associated to this proof obligation
	 * 
	 * @throws RodinDBException
	 * @see #getProof()
	 * @see #setProofConfidence(IProgressMonitor)
	 */
	int getConfidence() throws RodinDBException;

	/**
	 * Returns whether this proof obligation has been discharged manually. A
	 * proof obligation is considered as manually discharged if the end user
	 * entered manually its associated proof (even partially).
	 * <p>
	 * The returned value is <code>true</code> iff the corresponding attribute
	 * contains <code>true</code>. Hence, if the attribute is absent,
	 * <code>false</code> is returned.
	 * </p>
	 * <p>
	 * This is a cached attribute in case there is an associated proof.
	 * </p>
	 * 
	 * @return <code>true</code> if the user contributed to the proof of this
	 *         proof obligation
	 * 
	 * @throws RodinDBException
	 */
	boolean getHasManualProof() throws RodinDBException;
	
	/**
	 * Copies the cached proof information attributes described in {@link IPRProofInfoElement}
	 * from the associated proof in case it exists.
	 *
 	 * <p>
	 * In case the associated proof does not exist, all proof information attributes are removed.
	 * </p>
	 *
	 * 
	 * @param monitor
	 *            a progress monitor, or <code>null</code> if progress
	 *            reporting is not desired
	 * 
	 * @throws RodinDBException
	 * @see IPRProofInfoElement
	 */
	void copyProofInfo(IProgressMonitor monitor) throws RodinDBException;

	/**
	 * Copies the PO and PR stamps for the associated {@link POSequent} and
	 * {@link PRProof} so that they can be used later for delta checking.
	 * 
	 * <p>
	 * In case the associated elements do not exist, both stamp attributes are removed.
	 * </p>
	 * 
	 * @param monitor
	 *            a progress monitor, or <code>null</code> if progress
	 *            reporting is not desired
	 * 
	 * @throws RodinDBException
	 * @see IPRProofInfoElement
	 */
	void copyStamps(IProgressMonitor monitor) throws RodinDBException;
	
	/**
	 * Returns whether this proof obligation has been discharged manually. A
	 * proof obligation is considered as manually discharged if the end user
	 * entered manually its associated proof (even partially).
	 * <p>
	 * The returned value is <code>true</code> iff the corresponding attribute
	 * contains <code>true</code>. Hence, if the attribute is absent,
	 * <code>false</code> is returned.
	 * </p>
	 * <p>
	 * This is a cached attribute in case there is an associated proof.
	 * </p>
	 * 
	 * @return <code>true</code> if the user contributed to the proof of this
	 *         proof obligation
	 * 
	 * @throws RodinDBException
	 * 
	 * @Deprecated use {@link #getHasManualProof()} instead
	 * 
	 */
	@Deprecated
	boolean hasManualProof() throws RodinDBException;

	
	/**
	 * Sets the confidence associated to this proof obligation.
	 * <p>
	 * The confidence is copied from the associated proof. If the latter doesn't
	 * exist, then the confidence attribute is removed from this element.
	 * </p>
	 * <p>
	 * This is a cached attribute in case there is an associated proof.
	 * </p>
	 * 
	 * @param monitor
	 *            a progress monitor, or <code>null</code> if progress
	 *            reporting is not desired
	 * 
	 * @throws RodinDBException
	 * @see #getProof()
	 * @see #getConfidence()
	 * 
	 * @deprecated use {@link #copyProofInfo(IProgressMonitor)} instead
	 */
	@Deprecated
	void setProofConfidence(IProgressMonitor monitor) throws RodinDBException;
	
	/**
	 * Sets whether this proof obligation has been discharged manually.
	 * 
	 * @param value
	 *            The value to set to
	 * @param monitor
	 *            a progress monitor, or <code>null</code> if progress
	 *            reporting is not desired
	 * 
	 * <p>
	 * This is a cached attribute in case there is an associated proof.
	 * </p>
	 * 
	 * @throws RodinDBException
	 * @see #hasManualProof()
	 * 
	 * @deprecated set the manual proof in {@link IPRProof} and then use {@link #copyProofInfo(IProgressMonitor)}
	 */
	@Deprecated
	void setManualProof(boolean value, IProgressMonitor monitor)
			throws RodinDBException;
	
	/**
	 * Returns the confidence associated to this proof obligation. The
	 * confidence is stored in an attribute which contains a local copy of the
	 * confidence attribute of the associated proof. If there is no associated
	 * proof yet, then the confidence attribute is not set and this method
	 * returns {@link IConfidence#UNATTEMPTED}.
	 * 
	 * <p>
	 * This is a cached attribute in case there is an associated proof.
	 * </p>
	 * 
	 * @return the confidence associated to this proof obligation
	 * 
	 * @throws RodinDBException
	 * @see #getProof()
	 * @see #setProofConfidence(IProgressMonitor)
	 * 
	 * @deprecated use {@link #getConfidence()} instead
	 */
	@Deprecated
	int getProofConfidence() throws RodinDBException;

}

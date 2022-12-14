/*******************************************************************************
 * Copyright (c) 2006, 2012 ETH Zurich and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     ETH Zurich - initial API and implementation
 *******************************************************************************/
package org.eventb.core.seqprover;

import org.eventb.core.seqprover.proofBuilder.ReplayHints;

/**
 * Common protocol for reasoner inputs.
 * 
 * <p>
 * This interface is intended to be implemented by clients. Typically each reasoner
 * implements its own input class, although they may be shared in case the inputs are
 * the same.
 * </p>
 * 
 * 
 * @see IReasoner
 * @see IReasonerInputReader
 * @see IReasonerInputWriter
 * 
 * @author Farhad Mehta
 * @since 1.0
 */
public interface IReasonerInput {
	

	/**
	 * Returns <code>true</code> iff there was an error while constructing the
	 * reasoner input.
	 * 
	 * @return
	 * 		<code>true</code> iff there was an error while constructing the
	 * 		reasoner input.
	 */
	public boolean hasError();
	
	
	/**
	 * Returns the cause of the error in constructing the reasoner input.
	 * 
	 * @return
	 * 		the cause of the error, or <code>null</code> in case there was no
	 * 		error in constructing the reasoner input.
	 */
	public String getError();
	
	
	/**
	 * Applies the given free variable renaming to the reasoner input.
	 * 
	 * @param renaming
	 * 		the renaming to apply.
	 */
	public void applyHints(ReplayHints renaming);

}

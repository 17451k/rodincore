/*******************************************************************************
 * Copyright (c) 2006 ETH Zurich.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.eventb.core.pog;

import org.eventb.core.ast.Predicate;
import org.rodinp.core.IRodinElement;
import org.rodinp.core.RodinDBException;

/**
 * Representation of a predicate as handled by the proof obligation generator.
 * <p>
 * This interface is not intended to be implemented by clients.
 * </p>
 * 
 * @author Stefan Hallerstede
 *
 */
public interface IPOGPredicate {
	/**
	 * Returns the source reference for the predicate.
	 * 
	 * @return the source reference for the predicate
	 * @throws RodinDBException if there was a problem accessing the source reference
	 */
	public IRodinElement getSource() throws RodinDBException;
	
	/**
	 * Returns the predicate.
	 * 
	 * @return the predicate
	 */
	public Predicate getPredicate();

}

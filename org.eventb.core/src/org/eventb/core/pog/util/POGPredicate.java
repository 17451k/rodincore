/*******************************************************************************
 * Copyright (c) 2006 ETH Zurich.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.eventb.core.pog.util;

import org.eventb.core.IPOPredicate;
import org.eventb.core.ast.Predicate;
import org.rodinp.core.IRodinElement;
import org.rodinp.core.RodinDBException;

/**
 * All predicates stored in a PO file have an associated source reference.
 * <p>
 * There is also support for indirect source references used in statically 
 * checked files: {@link POGTraceablePredicate}.
 * </p>
 * @see IPOPredicate
 * @see POGTraceablePredicate
 * 
 * @author Stefan Hallerstede
 *
 */
public class POGPredicate {
	
	private final IRodinElement source;
	private final Predicate predicate;
	
	/**
	 * Creates a predicate with an associated source reference to be stored in a PO file.
	 * @param predicate a predicate
	 * @param source an associated source
	 */
	public POGPredicate(Predicate predicate, IRodinElement source) {
		this.source = source;
		this.predicate = predicate;
	}
	
	/**
	 * Returns the source reference for the predicate.
	 * 
	 * @return the source reference for the predicate
	 * @throws RodinDBException if there was a problem accessing the source reference
	 */
	public IRodinElement getSource() throws RodinDBException {
		return source;
	}
	
	/**
	 * Returns the predicate.
	 * 
	 * @return the predicate
	 */
	public Predicate getPredicate() {
		return predicate;
	}

}

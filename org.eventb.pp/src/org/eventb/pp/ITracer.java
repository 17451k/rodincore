/*******************************************************************************
 * Copyright (c) 2006 ETH Zurich.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/

package org.eventb.pp;

import java.util.List;

import org.eventb.core.ast.Predicate;

/**
 * A tracer that keeps track of what formulas are needed to get 
 * to the proof.
 *
 * @author François Terrier
 *
 */
public interface ITracer {

	/**
	 * Returns the hypotheses that where needed for the proof.
	 * 
	 * @return the hypotheses that where needed for the proof
	 */
	public List<Predicate> getNeededHypotheses();

	/**
	 * Returns <code>true</code> if the goal was needed to get 
	 * to the proof, <code>false</code> otherwise.
	 * 
	 * @return <code>true</code> if the goal was needed to get 
	 * to the proof, <code>false</code> otherwise
	 */
	public boolean isGoalNeeded();
	
}
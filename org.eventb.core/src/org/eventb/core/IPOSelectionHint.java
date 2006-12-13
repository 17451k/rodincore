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
 * A selection hint points a prover at hypotheses that are most likely useful in proving
 * a particular proof obligation. A selection hint can describe either a single predicate
 * or an interval of predicates (determined by segment path of the tree of predicate sets).
 * <p>
 * If <code>getEnd()</code> evaluates to <code>null</code>, then a selection hint is a 
 * predicate selection hint, and <code>getPredicate()</code> yields the predicate.
 * </p>
 * <p>
 * If <code>getEnd()</code> does not evaluate to <code>null</code>, then a selection is an
 * interval selection hint, where <code>getEnd()</code> denotes the last predicate set contained
 * in the interval and <code>getStart()</code> denotes the predecessor of the first predicate set
 * contained in the interval.
 * </p>
 * The following Java template shows how a selection can be read:
 * <pre>
 *	IPOPredicateSet endP = selectionHint.getEnd();
 *	if (endP == null) {
 *		IPOPredicate pred = selectionHint.getPredicate();
 *		... // do something with pred
 *	} else {
 *		IPOPredicateSet startP = selectionHint.getStart();
 *		while (!endP.equals(startP)) {
 *			IPOPredicate[] preds = endP.getPredicates();
 *			for (IPOPredicate pred : preds) {
 *				... // do something with pred
 *			}
 *			endP = endP.getParentPredicateSet();
 *		}
 *	}
 *</pre>
 * 
 * @author Stefan Hallerstede
 *
 */
public interface IPOSelectionHint extends IPOHint {

	IInternalElementType ELEMENT_TYPE =
		RodinCore.getInternalElementType(EventBPlugin.PLUGIN_ID + ".poSelHint"); //$NON-NLS-1$
	
	/**
	 * Returns the predicate handle in case the hint is a predicate selection hint.
	 * 
	 * @return the predicate handle
	 * @throws RodinDBException if there was a problem accessing the database, or
	 * 		this is not a predicate selection hint
	 */
	IPOPredicate getPredicate() throws RodinDBException;
	
	/**
	 * Makes this a predicate selection hint, and sets the predicate.
	 * 
	 * @param predicate the predicate of the hint
	 * @param monitor
	 *            a progress monitor, or <code>null</code> if progress
	 *            reporting is not desired
	 * @throws RodinDBException if there was a problem accessing the database
	 */
	void setPredicate(IPOPredicate predicate, IProgressMonitor monitor) throws RodinDBException;
	
	/**
	 * Returns the predecessor of the first predicate set contained in the interval.
	 * 
	 * @return the predecessor of the first predicate set contained in the interval
	 * @throws RodinDBException if there was a problem accessing the database, or
	 * 		this is not an interval selection hint
	 */
	IPOPredicateSet getStart() throws RodinDBException;
	
	/**
	 * Returns the last predicate set contained in the interval or <code>null</code>.
	 * If the returned value is <code>null</code>, then this is a predicate selection hint.
	 * <p>
	 * Use this method to determine whether this is a predicate or an interval selection hint.
	 * </p>
	 * 
	 * @return the last predicate set contained in the interval
	 * @throws RodinDBException if there was a problem accessing the database
	 */
	IPOPredicateSet getEnd() throws RodinDBException;
	
	/**
	 * Makes this an interval selection hint, and sets the interval.
	 * 
	 * @param start the predecessor of the first predicate set contained in the interval
	 * @param end the last predicate set contained in the interval
	 * @param monitor
	 *            a progress monitor, or <code>null</code> if progress
	 *            reporting is not desired
	 * @throws RodinDBException if there was a problem accessing the database
	 */
	void setInterval(IPOPredicateSet start, IPOPredicateSet end, IProgressMonitor monitor) 
	throws RodinDBException;
	
}

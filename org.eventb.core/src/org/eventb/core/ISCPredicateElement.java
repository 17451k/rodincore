/*******************************************************************************
 * Copyright (c) 2006, 2013 ETH Zurich and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     ETH Zurich - initial API and implementation
 *     Systerel - added origin of predicates in proof
 *******************************************************************************/
package org.eventb.core;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eventb.core.ast.ITypeEnvironment;
import org.eventb.core.ast.Predicate;
import org.rodinp.core.RodinDBException;

/**
 * Common protocol for Event-B SC elements that contain a predicate.
 * <p>
 * As this element has been statically checked, the contained predicate parses
 * and type-checks. Thus, it can be manipulated directly as an AST.
 * </p>
 * 
 * @author Stefan Hallerstede
 * @since 1.0
 * @noimplement This interface is not intended to be implemented by clients.
 */
public interface ISCPredicateElement extends IPredicateElement {

	/**
	 * Returns the predicate string contained in this element.
	 * 
	 * @return the string representation of the predicate of this element
	 * @throws RodinDBException
	 *             if there was a problem accessing the database
	 */
	@Override
	String getPredicateString()
			throws RodinDBException;

	/**
	 * Returns the typed predicate contained in this element.
	 * <p>
	 * If a {@link EventBAttributes#SOURCE_ATTRIBUTE} is present, it is
	 * considered the predicate's origin.
	 * </p>
	 * 
	 * @param typenv
	 *            the type environment to use for building the result
	 * @return the predicate of this element
	 * @throws CoreException
	 *             if there was a problem accessing the database, or if the
	 *             predicate does not parse or type-check
	 * 
	 * @see ISCContextRoot#getTypeEnvironment()
	 * @see ISCMachineRoot#getTypeEnvironment()
	 * @see ISCEvent#getTypeEnvironment(ITypeEnvironment)
	 * @since 3.0
	 */
	Predicate getPredicate(ITypeEnvironment typenv) throws CoreException;

	/**
	 * Sets the predicate contained in this element.
	 * 
	 * @param predicate
	 *            the predicate to set (must be type-checked)
	 * @param monitor
	 *            a progress monitor, or <code>null</code> if progress
	 *            reporting is not desired
	 * @throws RodinDBException
	 *             if there was a problem accessing the database
	 */
	void setPredicate(Predicate predicate, IProgressMonitor monitor) throws RodinDBException;

}

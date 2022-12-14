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
package org.eventb.internal.core.basis;

import org.eclipse.core.runtime.CoreException;
import org.eventb.core.ISCCarrierSet;
import org.eventb.core.ISCConstant;
import org.eventb.core.ISCContext;
import org.eventb.core.ast.FormulaFactory;
import org.eventb.core.ast.ITypeEnvironmentBuilder;
import org.rodinp.core.RodinDBException;

/**
 * This class contains some utility methods for SC contexts.
 * 
 * @author Laurent Voisin
 */
public class SCContextUtil {

	private SCContextUtil() {
		// Disabled constructor
	}

	/**
	 * Adds the carrier sets and constants of the given context to the given
	 * type environment. Operates by side-effect on the given type environment.
	 * 
	 * @param ctx
	 *            the contributing context (internal or file)
	 * @param typenv
	 *            the type environment to enrich
	 * @param factory
	 *            the formula factory to use
	 * @throws RodinDBException
	 *             if there was a problem accessing the database
	 */
	public static void augmentTypeEnvironment(ISCContext ctx,
			ITypeEnvironmentBuilder typenv, FormulaFactory factory)
			throws CoreException {

		for (ISCCarrierSet set : ctx.getSCCarrierSets()) {
			typenv.add(set.getIdentifier(factory));
		}
		for (ISCConstant cst : ctx.getSCConstants()) {
			typenv.add(cst.getIdentifier(factory));
		}
	}

}

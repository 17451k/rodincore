/*******************************************************************************
 * Copyright (c) 2006, 2014 ETH Zurich and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     ETH Zurich - initial API and implementation
 *******************************************************************************/
package org.eventb.core.sc;

import org.eventb.core.tool.IModule;
import org.eventb.internal.core.sc.SCUtil;
import org.eventb.internal.core.tool.Module;
import org.rodinp.core.IAttributeType;
import org.rodinp.core.IInternalElement;
import org.rodinp.core.IRodinElement;
import org.rodinp.core.IRodinProblem;
import org.rodinp.core.RodinDBException;

/**
 * 
 * Default implementation for static checker modules.
 * It provides an implementation for creating problem markers
 * in the input file implementing {@link IMarkerDisplay}.
 * 
 * @see IModule
 * @see IMarkerDisplay
 * 
 * @author Stefan Hallerstede
 *
 * @since 1.0
 */
public abstract class SCModule extends Module implements IMarkerDisplay {
	
	public static boolean DEBUG_MODULE = false;
	
	@Override
	public void createProblemMarker(
			IRodinElement element, 
			IRodinProblem problem, 
			Object... args)
		throws RodinDBException {
		SCUtil.createProblemMarker(element, problem, args);
	}
	
	@Override
	public void createProblemMarker(IInternalElement element,
			IAttributeType attributeType, IRodinProblem problem,
			Object... args) throws RodinDBException {
		if (SCUtil.DEBUG_MARKERS)
			SCUtil.traceMarker(element, problem.getLocalizedMessage(args));

		element.createProblemMarker(attributeType, problem, args);
	}

	@Override
	public void createProblemMarker(IInternalElement element,
			IAttributeType.String attributeType, int charStart, int charEnd,
			IRodinProblem problem, Object... args) throws RodinDBException {
		if (SCUtil.DEBUG_MARKERS)
			SCUtil.traceMarker(element, problem.getLocalizedMessage(args));

		element.createProblemMarker(attributeType, charStart, charEnd+1, problem,
				args);
	}
	
	@Override
	public String toString() {
		return getClass().getSimpleName();
	}

}

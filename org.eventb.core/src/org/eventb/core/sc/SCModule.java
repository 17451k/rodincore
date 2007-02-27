/*******************************************************************************
 * Copyright (c) 2006 ETH Zurich.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.eventb.core.sc;

import org.eventb.core.IEventBFile;
import org.eventb.core.IIdentifierElement;
import org.eventb.core.ILabeledElement;
import org.eventb.core.tool.IFilterModule;
import org.eventb.core.tool.IModule;
import org.eventb.core.tool.IProcessorModule;
import org.eventb.internal.core.sc.StaticChecker;
import org.eventb.internal.core.tool.Module;
import org.rodinp.core.IAttributeType;
import org.rodinp.core.IInternalElement;
import org.rodinp.core.IRodinElement;
import org.rodinp.core.IRodinProblem;
import org.rodinp.core.RodinDBException;

/**
 * 
 * Default implementation for modules.
 * 
 * @see org.eventb.core.tool.IModule
 * 
 * @author Stefan Hallerstede
 *
 */
public abstract class SCModule extends Module implements IModule, IMarkerDisplay {
	
	private void traceMarker(IRodinElement element, String message) {
		
		String name = element.getElementName();
		
		try {
			if (element instanceof ILabeledElement)
				name = ((ILabeledElement) element).getLabel();
			else if (element instanceof IIdentifierElement)
				name = ((IIdentifierElement) element).getIdentifierString();
			else if (element instanceof IEventBFile)
				name = ((IEventBFile) element).getBareName();
		} catch (RodinDBException e) {
			// ignore
		} finally {
		
			System.out.println("SC MARKER: " + name + ": " + message);
		}
	}
	
	@Override
	protected IFilterModule[] getFilterModules() {
		IFilterModule[] filterModules = super.getFilterModules();
		traceModules(filterModules);
		return filterModules;
	}

	@Override
	protected IProcessorModule[] getProcessorModules() {
		IProcessorModule[] processorModules = super.getProcessorModules();
		traceModules(processorModules);
		return processorModules;
	}

	private <M extends IModule> void traceModules(M[] modules) {
		if (DEBUG_MODULE) {
			for (IModule module : modules) {
				System.out.println("SC ACCESS: " + module.getModuleType());
			}
		}
	}

	public static boolean DEBUG_MODULE = false;
	public void createProblemMarker(
			IRodinElement element, 
			IRodinProblem problem, 
			Object... args)
		throws RodinDBException {
		if (StaticChecker.DEBUG_MARKERS)
			traceMarker(element, problem.getLocalizedMessage(args));

		element.createProblemMarker(problem, args);
	}
	
	public void createProblemMarker(IInternalElement element,
			IAttributeType attributeType, IRodinProblem problem,
			Object... args) throws RodinDBException {
		if (StaticChecker.DEBUG_MARKERS)
			traceMarker(element, problem.getLocalizedMessage(args));

		element.createProblemMarker(attributeType, problem, args);
	}

	public void createProblemMarker(IInternalElement element,
			IAttributeType.String attributeType, int charStart, int charEnd,
			IRodinProblem problem, Object... args) throws RodinDBException {
		if (StaticChecker.DEBUG_MARKERS)
			traceMarker(element, problem.getLocalizedMessage(args));

		element.createProblemMarker(attributeType, charStart, charEnd+1, problem,
				args);
	}
	
	@Override
	public String toString() {
		return getClass().getSimpleName();
	}

}

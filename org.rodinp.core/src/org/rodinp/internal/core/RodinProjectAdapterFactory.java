/*******************************************************************************
 * Copyright (c) 2006 ETH Zurich.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.rodinp.internal.core;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.IAdapterFactory;
import org.rodinp.core.IRodinProject;

/**
 * Adapter from IRodinProject to IProject.
 * 
 * @author Laurent Voisin
 */
public class RodinProjectAdapterFactory implements IAdapterFactory {

	private static final Class[] ADAPTERS = new Class[] {
		IProject.class,
	};
	
	public Object getAdapter(Object adaptableObject, Class adapterType) {
		if (IProject.class.equals(adapterType)) {
			return ((IRodinProject) adaptableObject).getProject();
		}
		return null;
	}

	public Class[] getAdapterList() {
		return ADAPTERS;
	}

}

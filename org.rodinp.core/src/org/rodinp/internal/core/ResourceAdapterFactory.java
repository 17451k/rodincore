/*******************************************************************************
 * Copyright (c) 2006, 2017 ETH Zurich and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     ETH Zurich - initial API and implementation
 *     INP Toulouse - use of generics for adapters
 *******************************************************************************/
package org.rodinp.internal.core;

import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.IAdapterFactory;
import org.rodinp.core.IRodinElement;
import org.rodinp.core.RodinCore;

/**
 * Adapter from IResource to IRodinElement.
 * 
 * @author Laurent Voisin
 */
public class ResourceAdapterFactory implements IAdapterFactory {

	private static final Class<?>[] ADAPTERS = new Class[] {
		IRodinElement.class,
	};
	
	@Override
	public <T> T getAdapter(Object adaptableObject, Class<T> adapterType) {
		if (!(adaptableObject instanceof IResource)) {
			return null;
		}
		if (IRodinElement.class.equals(adapterType)) {
			return adapterType.cast(
					RodinCore.valueOf((IResource) adaptableObject));
		}
		return null;
	}

	@Override
	public Class<?>[] getAdapterList() {
		return ADAPTERS;
	}

}

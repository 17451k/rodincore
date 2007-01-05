/*******************************************************************************
 * Copyright (c) 2005 ETH Zurich.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/

package org.rodinp.internal.core;

import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.Platform;
import org.osgi.framework.Bundle;
import org.rodinp.core.IInternalElement;
import org.rodinp.core.IInternalElementType;
import org.rodinp.core.IInternalParent;
import org.rodinp.core.IRodinElement;
import org.rodinp.core.basis.InternalElement;

/**
 * @author lvoisin
 *
 */
public class InternalElementType<T extends IInternalElement> extends
		ContributedElementType<T> implements IInternalElementType<T> {

	private final boolean named;
	
	public InternalElementType(IConfigurationElement configurationElement) {
		super(configurationElement);
		
		String value = configurationElement.getAttribute("named"); 
		this.named = value == null || value.equalsIgnoreCase(Boolean.TRUE.toString());
	}

	@Override
	@SuppressWarnings("unchecked")
	protected void computeClass() {
		Bundle bundle = Platform.getBundle(getBundleName());
		try {
			Class<?> clazz = bundle.loadClass(getClassName());
			classObject = (Class<? extends T>) clazz.asSubclass(InternalElement.class);
		} catch (Exception e) {
			throw new IllegalStateException(
					"Can't find constructor for element type " + getId(), e);
		}
	}

	@Override
	protected void computeConstructor() {
		if (classObject == null) {
			computeClass();
		}
		try {
			if (this.named)
				constructor = classObject.getConstructor(String.class, IRodinElement.class);
			else
				constructor = classObject.getConstructor(IRodinElement.class);
		} catch (Exception e) {
			throw new IllegalStateException(
					"Can't find constructor for element type " + getId(), e);
		}
	}

	public boolean isNamed() {
		return named;
	}

	/**
	 * Creates a new internal element handle.
	 * 
	 * @param elementName
	 *            the name of the element to create. Ignored for an unnamed
	 *            element.
	 * @param parent
	 *            the new element's parent
	 * @return a handle on the internal element or <code>null</code> if the
	 *         element type is unknown
	 */
	public T createInstance(String elementName, IInternalParent parent) {
		if (constructor == null) {
			computeConstructor();
		}
		if (constructor == null) {
			return null;
		}
		try {
			if (isNamed()) {
				return constructor.newInstance(elementName, parent);
			} else {
				return constructor.newInstance(parent);
			}
		} catch (Exception e) {
			throw new IllegalStateException(
					"Can't create an element of type " + getId(), e);
		}
	}
	
}
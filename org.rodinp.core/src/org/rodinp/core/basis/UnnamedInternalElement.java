/*******************************************************************************
 * Copyright (c) 2005 ETH Zurich.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.rodinp.core.basis;

import org.rodinp.core.IRodinElement;
import org.rodinp.core.IUnnamedInternalElement;
import org.rodinp.core.RodinCore;

/**
 * This abstract class is intended to be implemented by clients that contribute
 * to the <code>org.rodinp.core.internalElementTypes</code> extension point.
 * <p>
 * This abstract class should not be used in any other way than subclassing it
 * in database extensions. In particular, database clients should not use it,
 * but rather use its associated interface <code>IUnnamedInternalElement</code>.
 * </p>
 * 
 * @see IUnnamedInternalElement
 */
public class UnnamedInternalElement extends InternalElement implements IUnnamedInternalElement {

	/* Type of this unnamed internal element. */ 
	private String type;

	public UnnamedInternalElement(String type, IRodinElement parent) {
		super(RodinCore.getRodinCore().intern(REM_TYPE_PREFIX + type),
				parent);
		this.type = type;
	}

	@Override
	public final String getElementType() {
		return type;
	}

	@Override
	protected void getHandleMemento(StringBuffer buff) {
		getParent().getHandleMemento(buff);
		buff.append(getHandleMementoDelimiter());
		buff.append(getElementName());
	}

}

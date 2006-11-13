/*******************************************************************************
 * Copyright (c) 2006 ETH Zurich.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/

package org.eventb.core.basis;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eventb.core.EventBAttributes;
import org.eventb.core.IPOSource;
import org.rodinp.core.IInternalElementType;
import org.rodinp.core.IRodinElement;
import org.rodinp.core.RodinDBException;

/**
 * @author halstefa
 *
 */
public class POSource extends EventBElement implements IPOSource {
	
	public POSource(String name, IRodinElement parent) {
		super(name, parent);
	}

	/* (non-Javadoc)
	 * @see org.rodinp.core.basis.RodinElement#getElementType()
	 */
	@Override
	public IInternalElementType getElementType() {
		return ELEMENT_TYPE;
	}
	public String getRole(IProgressMonitor monitor) throws RodinDBException {
		return getStringAttribute(EventBAttributes.POROLE_ATTRIBUTE, monitor);
	}
	
	@Deprecated
	public String getSourceHandleIdentifier() throws RodinDBException {
		return getContents();
	}

	public void setRole(String role, IProgressMonitor monitor) throws RodinDBException {
		setStringAttribute(EventBAttributes.POROLE_ATTRIBUTE, role, monitor);
	}

}

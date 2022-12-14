/*******************************************************************************
 * Copyright (c) 2006, 2013 ETH Zurich and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     ETH Zurich - initial API and implementation
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
 * @since 1.0
 * @noinstantiate This class is not intended to be instantiated by clients.
 */
public class POSource extends EventBElement implements IPOSource {
	
	public POSource(String name, IRodinElement parent) {
		super(name, parent);
	}

	/* (non-Javadoc)
	 * @see org.rodinp.core.basis.RodinElement#getElementType()
	 */
	@Override
	public IInternalElementType<IPOSource> getElementType() {
		return ELEMENT_TYPE;
	}
	@Override
	public String getRole() throws RodinDBException {
		return getAttributeValue(EventBAttributes.POROLE_ATTRIBUTE);
	}
	
	@Override
	public void setRole(String role, IProgressMonitor monitor) throws RodinDBException {
		setAttributeValue(EventBAttributes.POROLE_ATTRIBUTE, role, monitor);
	}

}

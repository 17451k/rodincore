/*******************************************************************************
 * Copyright (c) 2006 ETH Zurich.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.rodinp.core.tests.version.conf;

import org.eclipse.core.runtime.InvalidRegistryObjectException;

/**
 * @author Stefan Hallerstede
 *
 */
public class RenameAttribute extends Operation {

	private final String id;
	private final String newId;
	
	public RenameAttribute(String id, String newId) {
		this.id = id;
		this.newId = newId;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.core.runtime.IConfigurationElement#getAttribute(java.lang.String)
	 */
	public String getAttribute(String name)
			throws InvalidRegistryObjectException {
		if (name.equals("id"))
			return id;
		else if (name.equals("newId"))
			return newId;
		else
			return null;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.core.runtime.IConfigurationElement#getName()
	 */
	public String getName() throws InvalidRegistryObjectException {
		return "renameAttribute";
	}

}

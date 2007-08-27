/*******************************************************************************
 * Copyright (c) 2006 ETH Zurich.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.rodinp.core.tests.version.conf;

import org.eclipse.core.runtime.IContributor;
import org.eclipse.core.runtime.InvalidRegistryObjectException;

/**
 * @author Stefan Hallerstede
 *
 */
public abstract class BundleConfElement extends ConfElement {
	
	final Contributor contributor;
	
	protected BundleConfElement(String bundle) {
		this.contributor =new Contributor(bundle);
	}

	/* (non-Javadoc)
	 * @see org.eclipse.core.runtime.IConfigurationElement#getContributor()
	 */
	public IContributor getContributor() throws InvalidRegistryObjectException {
		return contributor;
	}

}

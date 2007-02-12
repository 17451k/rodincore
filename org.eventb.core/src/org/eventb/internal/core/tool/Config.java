/*******************************************************************************
 * Copyright (c) 2006 ETH Zurich.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.eventb.internal.core.tool;

import org.eclipse.core.runtime.IConfigurationElement;

/**
 * @author Stefan Hallerstede
 *
 */
public class Config extends BasicDesc {

	// Unique ids of modules that are required to be executed before this module 
	private final String[] included;

	/**
	 * Creates a new module decription.
	 * 
	 * @param configElement
	 *            description of this module in the Eclipse registry
	 */
	public Config(IConfigurationElement configElement) {
		super(configElement);
		
		IConfigurationElement[] includedElements = configElement.getChildren("config");
		included = new String[includedElements.length];
		for (int i=0; i<includedElements.length; i++) {
			included[i] = includedElements[i].getAttribute("id");
		}
		
	}
	
	public String[] getIncluded() {
		return included;
	}
	
	
		
}

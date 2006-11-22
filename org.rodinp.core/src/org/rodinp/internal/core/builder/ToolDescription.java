/*******************************************************************************
 * Copyright (c) 2006 ETH Zurich.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/

package org.rodinp.internal.core.builder;

import org.eclipse.core.runtime.IConfigurationElement;
import org.rodinp.core.builder.IAutomaticTool;

/**
 * Description of a tool registered with the tool manager.
 * <p>
 * Implements lazy class loading for the tool.
 * </p>
 * 
 * @author Laurent Voisin
 */
public class ToolDescription extends ExecutableExtensionDesc {

	// Unique identifier of this executable extension
	private final String id;

	/**
	 * Creates a new tool decription.
	 * 
	 * @param configElement
	 *            description of this tool in the Eclipse registry
	 */
	public ToolDescription(IConfigurationElement configElement) {
		super(configElement);
		this.id = this.bundleName + "." + configElement.getAttribute("id");
	}

	@Override
	public String getId() {
		return id;
	}

	/**
	 * Returns an instance of this tool.
	 * 
	 * @return Returns an instance of this tool.
	 */
	public IAutomaticTool getTool() {
		return (IAutomaticTool) super.getExecutableExtension();
	}

	@Override
	public String toString() {
		return name + "(id=" + id + ", class=" + className + ")";
	}

}

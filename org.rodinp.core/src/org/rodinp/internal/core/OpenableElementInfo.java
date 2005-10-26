/*******************************************************************************
 * Copyright (c) 2005 ETH Zurich.
 * Strongly inspired by org.eclipse.jdt.internal.core.OpenableElementInfo.java which is
 * 
 * Copyright (c) 2000, 2004 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.rodinp.internal.core;


/** Element info for IOpenable elements. */
public abstract class OpenableElementInfo extends RodinElementInfo {
	
	private boolean knownStructure = false;

	public boolean isStructureKnown() {
		return knownStructure;
	}

	public void setIsStructureKnown(boolean isStructureKnown) {
		knownStructure = isStructureKnown;
	}
	
}

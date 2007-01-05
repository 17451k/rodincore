/*******************************************************************************
 * Copyright (c) 2006 ETH Zurich.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.rodinp.core.tests.builder;

import org.rodinp.core.IFileElementType;
import org.rodinp.core.IRodinFile;
import org.rodinp.core.RodinCore;

/**
 * @author Stefan Hallerstede
 *
 */
public interface IPOFile extends IRodinFile {

	public static final IFileElementType<IPOFile> ELEMENT_TYPE = 
		RodinCore.getFileElementType("org.rodinp.core.tests.poFile");
	
	ISCContext getCheckedContext();
	
	ISCMachine getCheckedMachine();
}

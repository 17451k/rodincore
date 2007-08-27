/*******************************************************************************
 * Copyright (c) 2006 ETH Zurich.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.rodinp.core.tests.version.db;

import org.rodinp.core.IInternalElement;
import org.rodinp.core.IInternalElementType;
import org.rodinp.core.RodinCore;

/**
 * @author Stefan Hallerstede
 *
 */
public interface IVersionEA extends IInternalElement {

	public static final IInternalElementType<IVersionEA> ELEMENT_TYPE = 
		RodinCore.getInternalElementType("org.rodinp.core.tests.versionEA");

}

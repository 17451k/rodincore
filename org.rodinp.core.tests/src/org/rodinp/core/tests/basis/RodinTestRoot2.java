/*******************************************************************************
 * Copyright (c) 2006, 2012 ETH Zurich and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     ETH Zurich - initial API and implementation
 *     Systerel - separation of file and root element
 *******************************************************************************/
package org.rodinp.core.tests.basis;

import static org.rodinp.core.tests.AbstractRodinDBTests.PLUGIN_ID;

import org.rodinp.core.IInternalElementType;
import org.rodinp.core.IRodinElement;
import org.rodinp.core.RodinCore;
import org.rodinp.core.basis.InternalElement;

public class RodinTestRoot2 extends InternalElement {

	public static final IInternalElementType<RodinTestRoot2> ELEMENT_TYPE =
		RodinCore.getInternalElementType(PLUGIN_ID + ".test2");

	public RodinTestRoot2(String name, IRodinElement parent) {
		super(name, parent);
	}

	@Override
	public IInternalElementType<RodinTestRoot2> getElementType() {
		return ELEMENT_TYPE;
	}

}

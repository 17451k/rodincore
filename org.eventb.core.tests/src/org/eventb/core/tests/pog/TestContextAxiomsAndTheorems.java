/*******************************************************************************
 * Copyright (c) 2006, 2009 ETH Zurich and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     ETH Zurich - initial API and implementation
 *     Systerel - separation of file and root element
 *******************************************************************************/
package org.eventb.core.tests.pog;

import org.eventb.core.IContextRoot;

/**
 * @author Stefan Hallerstede
 *
 */
public class TestContextAxiomsAndTheorems extends GenericPredicateTest<IContextRoot> {

	@Override
	protected IGenericPOTest<IContextRoot> newGeneric() {
		return new GenericContextPOTest(this);
	}

	@Override
	public String getTHMPOName(IContextRoot elem, String predName) {
		return predName + "/THM";
	}

	@Override
	public String getWDPOName(IContextRoot elem, String predName) {
		return predName + "/WD";
	}

	@Override
	public boolean isCumulative() {
		return true;
	}

}

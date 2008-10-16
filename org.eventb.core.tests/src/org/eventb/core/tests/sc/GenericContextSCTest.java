/*******************************************************************************
 * Copyright (c) 2006, 2008 ETH Zurich and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     ETH Zurich - initial API and implementation
 *     Systerel - separation of file and root element
 *******************************************************************************/
package org.eventb.core.tests.sc;

import org.eclipse.core.runtime.CoreException;
import org.eventb.core.IContextRoot;
import org.eventb.core.ISCContextRoot;
import org.eventb.core.ast.ITypeEnvironment;
import org.eventb.core.tests.GenericContextTest;
import org.rodinp.core.IRodinElement;
import org.rodinp.core.RodinDBException;

/**
 * @author Stefan Hallerstede
 *
 */
public class GenericContextSCTest extends GenericContextTest<BasicSCTest> implements
		IGenericSCTest<IContextRoot, ISCContextRoot> {

	public GenericContextSCTest(BasicSCTest test) {
		super(test);
	}

	public void containsIdents(ISCContextRoot element, String... strings) throws RodinDBException {
		test.containsConstants(element, strings);
	}

	public void containsNonTheorems(ISCContextRoot element, ITypeEnvironment environment, String[] labels, String[] strings) throws RodinDBException {
		test.containsAxioms(element, environment, labels, strings);
	}

	public void containsTheorems(ISCContextRoot element, ITypeEnvironment environment, String[] labels, String[] strings) throws RodinDBException {
		test.containsTheorems(element, environment, labels, strings);
	}

	public ISCContextRoot getSCElement(IContextRoot element) throws RodinDBException {
		return element.getSCContextRoot();
	}

	public void save(IContextRoot element) throws RodinDBException {
		element.getRodinFile().save(null, true);
	}

	public void containsMarkers(IContextRoot element, boolean yes) throws CoreException {
		test.containsMarkers(element.getRodinFile(), yes);
	}

	public IRodinElement[] getIdents(IContextRoot element) throws RodinDBException {
		return element.getConstants();
	}

	public IRodinElement[] getNonTheorems(IContextRoot element) throws RodinDBException {
		return element.getAxioms();
	}

	public IRodinElement[] getTheorems(IContextRoot element) throws RodinDBException {
		return element.getTheorems();
	}

}

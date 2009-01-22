/*******************************************************************************
 * Copyright (c) 2007, 2008 ETH Zurich and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     ETH Zurich - initial API and implementation
 *     Systerel - added history support
 *     Systerel - made IAttributeFactory generic
 *******************************************************************************/
package org.eventb.internal.ui.eventbeditor.manipulation;

import static org.eventb.core.EventBAttributes.IDENTIFIER_ATTRIBUTE;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eventb.core.IIdentifierElement;
import org.eventb.internal.ui.UIUtils;
import org.rodinp.core.IInternalElement;
import org.rodinp.core.IRodinElement;
import org.rodinp.core.RodinDBException;

public class IdentifierAttributeManipulation extends
		AbstractAttributeManipulation implements IAttributeManipulation {

	public void setDefaultValue(IRodinElement element, IProgressMonitor monitor)
			throws RodinDBException {
		final IIdentifierElement identifierElement = getIdentifier(element);
		final String identifier = UIUtils.getFreeElementIdentifier(
				(IInternalElement) identifierElement.getParent(),
				identifierElement.getElementType());
		identifierElement.setIdentifierString(identifier, monitor);
	}

	public void setValue(IRodinElement element, String newValue,
			IProgressMonitor monitor) throws RodinDBException {
		getIdentifier(element).setIdentifierString(newValue,
				new NullProgressMonitor());
	}

	public String getValue(IRodinElement element, IProgressMonitor monitor)
			throws RodinDBException {
		return getIdentifier(element).getIdentifierString();
	}

	public void removeAttribute(IRodinElement element, IProgressMonitor monitor)
			throws RodinDBException {
		logCantRemove(IDENTIFIER_ATTRIBUTE);
	}

	public String[] getPossibleValues(IRodinElement element,
			IProgressMonitor monitor) {
		// Not applicable to Identifier Elements
		logCantGetPossibleValues(IDENTIFIER_ATTRIBUTE);
		return null;
	}

	public boolean hasValue(IRodinElement element, IProgressMonitor monitor)
			throws RodinDBException {
		return getIdentifier(element).hasIdentifierString();
	}

	public IIdentifierElement getIdentifier(IRodinElement element) {
		assert element instanceof IIdentifierElement;
		return (IIdentifierElement) element;
	}
}

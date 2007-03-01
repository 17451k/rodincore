/*******************************************************************************
 * Copyright (c) 2006 ETH Zurich.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.eventb.internal.core.sc.symbolTable;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eventb.core.ISCConstant;
import org.eventb.core.ISCContextFile;
import org.eventb.core.ISCIdentifierElement;
import org.rodinp.core.IAttributeType;
import org.rodinp.core.IInternalElement;
import org.rodinp.core.IInternalParent;

/**
 * @author Stefan Hallerstede
 *
 */
public class ConcreteConstantSymbolInfo extends ConstantSymbolInfo {

	public ConcreteConstantSymbolInfo(
			String symbol, 
			IInternalElement element, 
			IAttributeType.String attribute, 
			String component) {
		super(symbol, false, element, attribute, component);
	}
	
	public ISCIdentifierElement createSCElement(
			IInternalParent parent, 
			IProgressMonitor monitor) throws CoreException {
		ISCConstant constant = ((ISCContextFile) parent).getSCConstant(getSymbol());
		constant.create(null, monitor);
		constant.setType(getType(), null);
		constant.setSource(getReferenceElement(), monitor);
		return constant;
	}

}

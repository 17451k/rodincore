/*******************************************************************************
 * Copyright (c) 2006 ETH Zurich.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.eventb.internal.core.sc.symbolTable;

import org.eventb.core.sc.GraphProblem;
import org.eventb.core.sc.symbolTable.ICarrierSetSymbolInfo;
import org.rodinp.core.IAttributeType;
import org.rodinp.core.IInternalElement;
import org.rodinp.core.IRodinProblem;

/**
 * @author Stefan Hallerstede
 *
 */
public abstract class CarrierSetSymbolInfo 
	extends IdentifierSymbolInfo
	implements ICarrierSetSymbolInfo {

	public CarrierSetSymbolInfo(
			String symbol, 
			String link, 
			IInternalElement element, 
			IAttributeType.String attribute, 
			String component) {
		super(symbol, link, element, attribute, component);
	}

	@Override
	public IRodinProblem getConflictWarning() {
		if (isImported())
			return GraphProblem.CarrierSetNameImportConflictWarning;
		else
			return GraphProblem.CarrierSetNameConflictWarning;
	}

	@Override
	public IRodinProblem getConflictError() {
		if (isImported())
			return GraphProblem.CarrierSetNameImportConflictError;
		else
			return GraphProblem.CarrierSetNameConflictError;
	}

	@Override
	public IRodinProblem getUntypedError() {
		return GraphProblem.UntypedCarrierSetError;
	}

}

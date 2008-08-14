/*******************************************************************************
 * Copyright (c) 2006 ETH Zurich.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.eventb.internal.core.sc.symbolTable;

import org.eclipse.core.runtime.CoreException;
import org.eventb.core.sc.GraphProblem;
import org.eventb.core.sc.IMarkerDisplay;
import org.eventb.core.sc.state.IConcreteEventInfo;
import org.eventb.core.sc.symbolTable.IEventSymbolInfo;
import org.rodinp.core.IAttributeType;
import org.rodinp.core.IInternalElement;
import org.rodinp.core.RodinDBException;

/**
 * @author Stefan Hallerstede
 *
 */
public class EventSymbolInfo extends LabelSymbolInfo implements IEventSymbolInfo {
	
	public EventSymbolInfo(
			String symbol, 
			IInternalElement element, 
			IAttributeType.String attribute, 
			String component) {
		super(symbol, element, attribute, component);
		inherited = false;
	}
	
	private boolean inherited;
	
	@Deprecated
	private IConcreteEventInfo refinesInfo;
	
	@Deprecated
	public void setRefinesInfo(IConcreteEventInfo refinesInfo) {
		this.refinesInfo = refinesInfo;
	}

	@Deprecated
	public IConcreteEventInfo getRefinesInfo() {
		return refinesInfo;
	}

	public boolean isExtended() {
		return inherited;
	}

	public void setInherited() throws CoreException {
		inherited = true;
	}

	@Override
	protected void createConflictError(IMarkerDisplay markerDisplay) throws RodinDBException {
		markerDisplay.createProblemMarker(
				getElement(), 
				getSourceAttributeType(), 
				GraphProblem.EventLabelConflictError, 
				getSymbol());
	}

	@Override
	protected void createConflictWarning(IMarkerDisplay markerDisplay) throws RodinDBException {
		markerDisplay.createProblemMarker(
				getElement(), 
				getSourceAttributeType(), 
				GraphProblem.EventLabelConflictWarning, 
				getSymbol());
	}
	
}

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
import org.eventb.core.sc.IEventRefinesInfo;
import org.eventb.core.sc.symbolTable.IEventSymbolInfo;
import org.eventb.internal.core.Util;
import org.eventb.internal.core.sc.Messages;
import org.rodinp.core.IRodinElement;
import org.rodinp.core.IRodinProblem;

/**
 * @author Stefan Hallerstede
 *
 */
public class EventSymbolInfo extends LabelSymbolInfo implements
		IEventSymbolInfo {
	
	public EventSymbolInfo(
			String symbol, 
			IRodinElement element, 
			String component) {
		super(symbol, element, component);
		inherited = false;
	}
	
	private boolean forbidden;
	
	private boolean disappearing;
	
	private boolean inherited;
	
	private IEventRefinesInfo refinesInfo;
	
	public boolean isForbidden() {
		return forbidden;
	}
	
	public void setForbidden() throws CoreException {
		if (!isMutable())
			throw Util.newCoreException(Messages.symtab_ImmutableSymbolViolation);
		this.forbidden = true;
	}

	public boolean isDisappearing() {
		return disappearing;
	}

	public void setDisappearing() throws CoreException {
		this.disappearing = true;
	}

	public void setRefinesInfo(IEventRefinesInfo refinesInfo) {
		this.refinesInfo = refinesInfo;
	}

	public IEventRefinesInfo getRefinesInfo() {
		return hasError() ? null : refinesInfo;
	}

	public boolean isInherited() {
		return inherited;
	}

	public void setInherited() throws CoreException {
		inherited = true;
	}

	@Override
	public IRodinProblem getConflictWarning() {
		return GraphProblem.EventLabelConflictWarning;
	}

	@Override
	public IRodinProblem getConflictError() {
		return GraphProblem.EventLabelConflictError;
	}
	
}

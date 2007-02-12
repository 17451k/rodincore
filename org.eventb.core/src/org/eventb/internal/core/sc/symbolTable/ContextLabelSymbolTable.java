/*******************************************************************************
 * Copyright (c) 2006 ETH Zurich.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.eventb.internal.core.sc.symbolTable;

import org.eventb.core.sc.state.IContextLabelSymbolTable;
import org.eventb.core.sc.symbolTable.ILabelSymbolInfo;
import org.eventb.core.tool.state.IStateType;

/**
 * @author Stefan Hallerstede
 *
 */
public class ContextLabelSymbolTable extends SymbolTable<ILabelSymbolInfo> implements
		IContextLabelSymbolTable {

	public ContextLabelSymbolTable(int size) {
		super(size);
	}

	/* (non-Javadoc)
	 * @see org.eventb.core.sc.IState#getStateType()
	 */
	public IStateType<?> getStateType() {
		return STATE_TYPE;
	}

}

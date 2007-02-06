/*******************************************************************************
 * Copyright (c) 2006 ETH Zurich.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.eventb.internal.core.pog;

import org.eventb.core.ISCTheorem;
import org.eventb.core.ast.FormulaFactory;
import org.eventb.core.ast.ITypeEnvironment;
import org.eventb.core.pog.state.IMachineTheoremTable;
import org.eventb.core.tool.state.IToolStateType;
import org.rodinp.core.RodinDBException;

/**
 * @author Stefan Hallerstede
 *
 */
public class MachineTheoremTable extends PredicateTable<ISCTheorem> implements IMachineTheoremTable {

	public MachineTheoremTable(
			ISCTheorem[] elements, 
			ITypeEnvironment typeEnvironment, 
			FormulaFactory factory) throws RodinDBException {
		super(elements, typeEnvironment, factory);
	}

	/* (non-Javadoc)
	 * @see org.eventb.core.sc.IState#getStateType()
	 */
	public IToolStateType<?> getStateType() {
		return STATE_TYPE;
	}

}

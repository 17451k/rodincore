/*******************************************************************************
 * Copyright (c) 2006 ETH Zurich.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.eventb.internal.core.pog;

import org.eclipse.core.runtime.CoreException;
import org.eventb.core.ISCMachineFile;
import org.eventb.core.ISCPredicateElement;
import org.eventb.core.pog.state.IMachineHypothesisManager;
import org.rodinp.core.IRodinElement;

/**
 * @author Stefan Hallerstede
 *
 */
public class MachineHypothesisManager extends HypothesisManager implements IMachineHypothesisManager {
	
	public static final String CTX_HYP_NAME = "CTXHYP";
	
	public static final String ABS_HYP_NAME = "ABSHYP";
	
	public static final String HYP_PREFIX = "HYP";
	
	public static final String ALLHYP_NAME = "ALLHYP";
	
	private static final int IDENTIFIER_TABLE_SIZE = 213;
	
	private final ISCMachineFile abstractMachine;
	
	public MachineHypothesisManager(
			IRodinElement parentElement, 
			ISCPredicateElement[] predicateTable) throws CoreException {
		super(parentElement, predicateTable, ABS_HYP_NAME, HYP_PREFIX, ALLHYP_NAME, IDENTIFIER_TABLE_SIZE);
		
		abstractMachine = ((ISCMachineFile) parentElement).getAbstractSCMachine(null);
	}

	public String getStateType() {
		return STATE_TYPE;
	}
	
	public String getContextHypothesisName() {
		return CTX_HYP_NAME;
	}
	
	public boolean isInitialMachine() {
		return abstractMachine == null;
	}
}

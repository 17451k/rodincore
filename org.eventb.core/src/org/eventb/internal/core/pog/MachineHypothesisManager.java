/*******************************************************************************
 * Copyright (c) 2006 ETH Zurich.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.eventb.internal.core.pog;

import org.eclipse.core.runtime.CoreException;
import org.eventb.core.IPOFile;
import org.eventb.core.IPOPredicateSet;
import org.eventb.core.ISCMachineFile;
import org.eventb.core.ISCPredicateElement;
import org.eventb.core.ISCRefinesMachine;
import org.eventb.core.pog.state.IMachineHypothesisManager;
import org.eventb.core.tool.state.IStateType;
import org.eventb.internal.core.Util;
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
	
	public static final String IDENT_HYP_NAME = "IDENT";
	
	private static final int IDENTIFIER_TABLE_SIZE = 213;
	
	private final ISCMachineFile abstractMachine;
	
	public MachineHypothesisManager(
			IRodinElement parentElement, 
			IPOFile target,
			ISCPredicateElement[] predicateTable) throws CoreException {
		super(parentElement, target, predicateTable, 
				ABS_HYP_NAME, HYP_PREFIX, ALLHYP_NAME, IDENT_HYP_NAME, 
				IDENTIFIER_TABLE_SIZE);
		
		ISCRefinesMachine[] refinesMachines = ((ISCMachineFile) parentElement).getSCRefinesClauses();
		
		if (refinesMachines.length == 0)
			abstractMachine = null;
		else {
			abstractMachine = refinesMachines[0].getAbstractSCMachine();
			
			if (refinesMachines.length > 1) {
				throw Util.newCoreException(Messages.pog_multipleRefinementError);
			}
		}
	}

	public IStateType<?> getStateType() {
		return STATE_TYPE;
	}
	
	public IPOPredicateSet getContextHypothesis() {
		return target.getPredicateSet(CTX_HYP_NAME);
	}
	
	public boolean isInitialMachine() {
		return abstractMachine == null;
	}
}

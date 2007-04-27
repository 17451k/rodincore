/*******************************************************************************
 * Copyright (c) 2006 ETH Zurich.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.eventb.core.sc.state;

import org.eventb.core.EventBPlugin;
import org.eventb.core.IRefinesMachine;
import org.eventb.core.ISCMachineFile;
import org.eventb.core.sc.SCCore;
import org.eventb.core.tool.state.IStateType;

/**
 * State component providing information about an abstract machine of
 * a machine.
 * <p>
 * This interface is not intended to be implemented by clients.
 * </p>
 * 
 * @author Stefan Hallerstede
 *
 */
public interface IAbstractMachineInfo extends ISCState {

	final static IStateType<IAbstractMachineInfo> STATE_TYPE = 
		SCCore.getToolStateType(EventBPlugin.PLUGIN_ID + ".abstractMachineInfo");
	
	/**
	 * Returns a handle to the abstract machine if there is one, and 
	 * <code>null</code> otherwise.
	 * <p>
	 * If this method does not return <code>null</code>, then <code>getRefinesClause()</code>
	 * does not return <code>null</code> either.
	 * </p>
	 * 
	 * @return a handle to the abstract machine if there is one, and 
	 * <code>null</code> otherwise
	 */
	ISCMachineFile getAbstractMachine();
	
	/**
	 * Returns a handle to the refines clause from which the abstract
	 * machine was extracted, or <code>null</code> if none.
	 * <p>
	 * If this method returns <code>null</code>, then <code>getAbstractMachine()</code> 
	 * returns <code>null</code> too.
	 * </p>
	 * 
	 * @return a handle to the refines clause from which the abstract
	 * machine was extracted, or <code>null</code> if none
	 */
	IRefinesMachine getRefinesClause();

}

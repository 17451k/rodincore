/*******************************************************************************
 * Copyright (c) 2006 ETH Zurich.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.eventb.core.sc.state;

import java.util.List;

import org.eventb.core.EventBPlugin;
import org.eventb.core.sc.SCCore;
import org.eventb.core.tool.state.IStateType;

/**
 * State component for accessing information about events of the abstract machine.
 * 
 * <p>
 * This interface is not intended to be implemented by clients.
 * </p>
 * @author Stefan Hallerstede
 *
 */
public interface IAbstractEventTable extends ISCState {
	
	final static IStateType<IAbstractEventTable> STATE_TYPE = 
		SCCore.getToolStateType(EventBPlugin.PLUGIN_ID + ".abstractEventTable");
	
	/**
	 * Returns the array of all abstract event infos for the abstract machine
	 * 
	 * @return the array of all abstract event infos
	 */
	List<? extends IAbstractEventInfo> getAbstractEventInfos();
	
	/**
	 * Returns the abstract event info for the specified label, or <code>null</code> if this
	 * table does not contain an abstract event info for that label.
	 * 
	 * @param label the label of the abstract event info sought
	 * @return the abstract event info for the specified label, or <code>null</code> if this
	 * table does not contain an abstract event info for that label
	 */
	IAbstractEventInfo getAbstractEventInfo(String label);
	
	/**
	 * Returns whether any of the abstract events has a local variable with the specified name.
	 * 
	 * @param name the name to check
	 * @return whether any of the abstract events has a local variable with the specified name
	 */
	boolean isLocalVariable(String name);
	
}

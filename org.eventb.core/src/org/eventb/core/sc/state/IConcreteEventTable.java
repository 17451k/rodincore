/*******************************************************************************
 * Copyright (c) 2008, 2018 University of Southampton and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     University of Southampton - initial API and implementation
 *     Systerel - added machineRoot information
 *******************************************************************************/
package org.eventb.core.sc.state;

import org.eclipse.core.runtime.CoreException;
import org.eventb.core.EventBPlugin;
import org.eventb.core.IMachineRoot;
import org.eventb.core.sc.SCCore;
import org.eventb.core.tool.IStateType;

/**
 * This state component stores a table with information about refinement relationships of concrete events.
 * 
 * @author Stefan Hallerstede
 * @since 1.0
 * @noextend This interface is not intended to be extended by clients.
 * @noimplement This interface is not intended to be implemented by clients.
 */
public interface IConcreteEventTable extends ISCState, Iterable<IConcreteEventInfo> {
	
	final static IStateType<IConcreteEventTable> STATE_TYPE = 
		SCCore.getToolStateType(EventBPlugin.PLUGIN_ID + ".concreteEventTable");

	IConcreteEventInfo getConcreteEventInfo(String symbol) throws CoreException;
	
	void addConcreteEventInfo(IConcreteEventInfo info) throws CoreException;

	/**
	 * Returns the machine root associated with this event table.
	 * 
	 * @return the machine root associated with this event table
	 * @since 2.0
	 */
	IMachineRoot getMachineRoot();
	
	
}

/*******************************************************************************
 * Copyright (c) 2006 ETH Zurich.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.eventb.core.sc.state;

import org.eventb.core.EventBPlugin;
import org.eventb.core.IEvent;
import org.eventb.core.sc.SCCore;
import org.eventb.core.sc.ISCFilterModule;
import org.eventb.core.sc.symbolTable.IEventSymbolInfo;
import org.eventb.core.tool.state.IStateType;

/**
 * The static checker protocol does not permit to pass the current event as
 * a parameter to filter modules {@link ISCFilterModule} for elements contained in an event.
 * This state component can be used to access the current event instead.
 * 
 * @author Stefan Hallerstede
 *
 */
public interface ICurrentEvent extends ISCState, IConvergenceInfo {
	
	final static IStateType<ICurrentEvent> STATE_TYPE = 
		SCCore.getToolStateType(EventBPlugin.PLUGIN_ID + ".currentEvent");

	/**
	 * Returns whether the current event is the initialisation.
	 * 
	 * @return whether the current event is the initialisation
	 */
	boolean isInitialisation();
	
	/**
	 * Returns the current event.
	 * 
	 * @return the current event
	 */
	IEvent getCurrentEvent();
	
	/**
	 * Returns the symbol info for the event, or <code>null</code> if no symbol info
	 * has been generated for the event.
	 * 
	 * @return the symbol info for the event, or <code>null</code> if no symbol info
	 * has been generated for the event
	 */
	IEventSymbolInfo getCurrentEventSymbolInfo();

}

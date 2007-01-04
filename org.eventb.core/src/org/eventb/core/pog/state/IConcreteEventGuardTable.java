/*******************************************************************************
 * Copyright (c) 2006 ETH Zurich.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.eventb.core.pog.state;

import org.eventb.core.EventBPlugin;
import org.eventb.core.ISCGuard;

/**
 * Common protocol for accessing the guards of the concrete event.
 * There is always exacly one concrete event. In case of a split refinement
 * each split event is treated separately.
 * 
 * <p>
 * This interface is not intended to be implemented by clients.
 * </p>
 * 
 * @author Stefan Hallerstede
 *
 */
public interface IConcreteEventGuardTable extends IPredicateTable<ISCGuard> {
	
	final static String STATE_TYPE = EventBPlugin.PLUGIN_ID + ".concreteEventGuardTable";

}

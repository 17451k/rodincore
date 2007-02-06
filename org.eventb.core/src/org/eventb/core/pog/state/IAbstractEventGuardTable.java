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
import org.eventb.core.pog.POGCore;
import org.eventb.core.tool.state.IToolStateType;

/**
 * Common protocol for accessing the guards of an abstract event.
 * 
 * <p>
 * This interface is not intended to be implemented by clients.
 * </p>
 * 
 * @author Stefan Hallerstede
 *
 */
public interface IAbstractEventGuardTable extends IPredicateTable<ISCGuard>, ICorrespondence {

	final static IToolStateType<IAbstractEventGuardTable> STATE_TYPE = 
		POGCore.getToolStateType(EventBPlugin.PLUGIN_ID + ".abstractEventGuardTable");

}

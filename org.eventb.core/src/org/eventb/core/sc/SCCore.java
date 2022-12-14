/*******************************************************************************
 * Copyright (c) 2006, 2014 ETH Zurich and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     ETH Zurich - initial API and implementation
 *******************************************************************************/
package org.eventb.core.sc;

import org.eventb.core.tool.IModule;
import org.eventb.core.tool.IModuleType;
import org.eventb.core.tool.IState;
import org.eventb.core.tool.IStateType;
import org.eventb.internal.core.tool.SCModuleManager;
import org.eventb.internal.core.tool.state.SCStateTypeManager;

/**
 * @author Stefan Hallerstede
 *
 * @since 1.0
 */
public final class SCCore {
	
	/**
	 * Returns the tool state type with the given id.
	 * 
	 * @param id
	 *            unique identifier of the tool state type
	 * @return the tool state type with the given id
	 * @throws IllegalArgumentException
	 *             if no such tool state type has been contributed
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public static <T extends IState> IStateType<T> getToolStateType(
			String id) {
		final SCStateTypeManager manager = SCStateTypeManager.getInstance();
		final IStateType result = manager.getStateType(id);
		if (result != null) {
			return result;
		}
		throw new IllegalArgumentException("Unknown SC tool state type: " + id);
	}
	
	/**
	 * Returns the module type with the given id.
	 * 
	 * @param id
	 *            unique identifier of the module type
	 * @return the module type with the given id
	 * @throws IllegalArgumentException
	 *             if no such module type has been contributed
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public static <T extends IModule> IModuleType<T> getModuleType(
			String id) {
		final SCModuleManager manager = SCModuleManager.getInstance();
		final IModuleType result = manager.getModuleDesc(id);
		if (result != null) {
			return result;
		}
		throw new IllegalArgumentException("Unknown SC module type: " + id);
	}

}

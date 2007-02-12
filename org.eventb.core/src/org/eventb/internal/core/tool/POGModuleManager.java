/*******************************************************************************
 * Copyright (c) 2006 ETH Zurich.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.eventb.internal.core.tool;

import java.util.List;

import org.eventb.core.pog.IPOGFilterModule;
import org.eventb.core.pog.IPOGProcessorModule;
import org.eventb.core.tool.IModule;
import org.eventb.internal.core.tool.graph.ModuleGraph;

/**
 * @author Stefan Hallerstede
 *
 */
public class POGModuleManager extends ModuleManager<IPOGFilterModule, IPOGProcessorModule> {

	private static final String POG_MODULES_ID = "pogModules";
	private static final POGModuleManager MANAGER = new POGModuleManager();
	
	private static final POGConfigManager POG_CONFIG_MANAGER = POGConfigManager.getInstance();
	
	public static POGModuleManager getInstance() {
		return MANAGER;
	}
	
	private POGModuleManager() {
		super(POG_MODULES_ID);
	}

	@Override
	protected List<ModuleDesc<? extends IModule>> getModuleListForConfig(String configId) {
		return POG_CONFIG_MANAGER.getConfigClosure(configId);
	}

	@Override
	protected ModuleFactory<IPOGFilterModule, IPOGProcessorModule> computeModuleFactory(
			ModuleGraph moduleGraph) {
		return new POGModuleFactory(moduleGraph, this);
	}

}

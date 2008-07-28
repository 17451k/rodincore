/*******************************************************************************
 * Copyright (c) 2006 ETH Zurich.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.eventb.internal.core.tool;

import org.eventb.core.tool.IModuleType;
import org.eventb.internal.core.tool.types.IFilterModule;
import org.eventb.internal.core.tool.types.IModule;
import org.eventb.internal.core.tool.types.IProcessorModule;
import org.rodinp.core.IFileElementType;
import org.rodinp.core.IRodinFile;

/**
 * @author Stefan Hallerstede
 *
 */
public interface IModuleFactory {
	
	IFilterModule[] getFilterModules(IModuleType<? extends IModule> parentType);
	
	IProcessorModule[] getProcessorModules(IModuleType<? extends IModule> parentType);
	
	IProcessorModule getRootModule(IFileElementType<? extends IRodinFile> type);
	
	public String printModuleTree(IFileElementType<? extends IRodinFile> type);
}

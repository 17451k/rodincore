/*******************************************************************************
 * Copyright (c) 2006 ETH Zurich.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.eventb.core.pog;

import org.eventb.core.tool.IModule;
import org.eventb.internal.core.tool.Module;

/**
 * This is the base class of all proof obligation generator modules.
 * 
 * @see IModule
 * @see IPOGFilterModule
 * @see IPOGProcessorModule
 * 
 * @author Stefan Hallerstede
 *
 */
public abstract class POGModule extends Module {
	
	public static boolean DEBUG_MODULE = false;

}

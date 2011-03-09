/*******************************************************************************
 * Copyright (c) 2006 ETH Zurich.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.eventb.internal.core.tool.types;


/**
 * Basic type for processor modules. A processor module is one of the two
 * kinds of modules ({@link IModule}) used by a core tool.
 * <p>
 * Processor modules are intended to process a list of elements. 
 * It is implemented by the static checker and the proof obligation generator.
 * </p>
 * <p>
 * A processor module may have filter and processor child modules.
 * The filter child nodes are executed before the processor child modules.
 * </p>
 * 
 * @see IModule
 * @see ISCProcessorModule
 * @see IPOGProcessorModule
 * 
 * @author Stefan Hallerstede
 *
 */
public interface IProcessorModule extends IModule {

	// basic type for processor modules 

}

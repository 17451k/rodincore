/*******************************************************************************
 * Copyright (c) 2006 ETH Zurich.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.eventb.internal.core.tool;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IExtensionRegistry;
import org.eclipse.core.runtime.Platform;
import org.eventb.core.EventBPlugin;
import org.eventb.core.tool.IFilterModule;
import org.eventb.core.tool.IModule;
import org.eventb.core.tool.IProcessorModule;
import org.eventb.internal.core.tool.graph.ModuleGraph;
import org.eventb.internal.core.tool.graph.ParentGraph;

/**
 * @author Stefan Hallerstede
 *
 */
public abstract class ModuleManager extends SortingUtil {
	
	private static final String FILTER_TYPE = "filterType";

	private static final String PROCESSOR_TYPE = "processorType";

	// Debug flag set from tracing options 
	public static boolean VERBOSE = false;

	// Local id of the state types extension point of this plugin
	private final String modules_id;
	
	// Access to modules using their unique id
	// This table contains all filter and processor modules
	private HashMap<String, ModuleDesc<? extends IModule>> moduleIds;

	private void register(String id, ModuleDesc<? extends IModule> type) {
		final ModuleDesc<? extends IModule> oldType = moduleIds.put(id, type);
		if (oldType != null) {
			moduleIds.put(id, oldType);
			throw new IllegalStateException(
					"Attempt to create twice module type " + id);
		}
	}
	
	private void computeModules() {
		moduleIds =
			new HashMap<String, ModuleDesc<? extends IModule>>();
		
		// Read the extension point extensions.
		IExtensionRegistry registry = Platform.getExtensionRegistry();
		IConfigurationElement[] elements = 
			registry.getConfigurationElementsFor(EventBPlugin.PLUGIN_ID, modules_id);
		
		loadModules(elements);
//		for (IConfigurationElement element : elements) {
//			loadFilterModules(element);
//			loadProcessorModules(element);
//		}
		
		
		if (VERBOSE) {
			System.out.println("---------------------------------------------------");
			System.out.println(modules_id + " registered:");
			for (String id: getSortedIds(moduleIds)) {
				ModuleDesc<? extends IModule> type = moduleIds.get(id);
				System.out.println("  " + type.getId());
				System.out.println("    name: " + type.getName());
				System.out.println("    class: " + type.getClassName());
			}
			System.out.println("---------------------------------------------------");
		}
	}
	
	protected abstract List<ModuleDesc<? extends IModule>> getModuleListForConfig(String configId);

	protected abstract void verifyProcessor(ProcessorModuleDesc<? extends IProcessorModule> moduleDesc);
	
	protected abstract void verifyFilter(FilterModuleDesc<? extends IFilterModule> moduleDesc);
	
	protected abstract String getName();

	private void loadModules(IConfigurationElement[] elements) {
		
		for (IConfigurationElement element: elements) {
			String name = element.getName();
			if (name.equals(FILTER_TYPE)) {
				
				FilterModuleDesc<? extends IFilterModule> filter = 
					new FilterModuleDesc<IFilterModule>(element);
				verifyFilter(filter);
				register(filter.getId(), filter);
				
			} else if (name.equals(PROCESSOR_TYPE)) {
				
				ProcessorModuleDesc<? extends IProcessorModule> processor = 
					new ProcessorModuleDesc<IProcessorModule>(element);
				verifyProcessor(processor);
				register(processor.getId(), processor);
				
			} else {
				throw new IllegalStateException("Unknown module declaration: " + name);
			}
			
		}
	}

	/**
	 * Returns the module desc with the given id.
	 * 
	 * @param id
	 *            the id of the module desc to retrieve
	 * @return the module desc or <code>null</code> if this
	 *         module desc id is unknown.
	 */
	public ModuleDesc<? extends IModule> getModuleDesc(String id) {

		if (moduleIds == null) {
			computeModules();
		}
		return moduleIds.get(id);
	}

	protected ModuleManager(final String modules_id) {
		this.modules_id = modules_id;
		factoryMap = new HashMap<String, ModuleFactory>();
	}
	
	Map<String, ModuleFactory> factoryMap;

	public IModuleFactory getModuleFactory(String configId) {
		
		ModuleFactory factory = factoryMap.get(configId);
		if (factory != null)
			return factory;
		
		List<ModuleDesc<? extends IModule>> moduleList = getModuleListForConfig(configId);
		ModuleGraph moduleGraph = sortModules(moduleList, getName());
		
		factory = computeModuleFactory(moduleGraph);
		factoryMap.put(configId, factory);
		return factory;
	}

	// this method is part of the testing interface 
	public static ModuleGraph sortModules(List<ModuleDesc<? extends IModule>> moduleList, String creator) {
		ParentGraph parentGraph = new ParentGraph(creator);
		ModuleGraph moduleGraph = new ModuleGraph(creator);
		for (ModuleDesc<? extends IModule> moduleDesc : moduleList) {
			parentGraph.add(moduleDesc);
			moduleGraph.add(moduleDesc);
		}
		parentGraph.analyse();
		moduleGraph.analyse(parentGraph);
		return moduleGraph;
	}

	protected ModuleFactory computeModuleFactory(ModuleGraph moduleGraph) {
		return new ModuleFactory(moduleGraph, this);
	}
}

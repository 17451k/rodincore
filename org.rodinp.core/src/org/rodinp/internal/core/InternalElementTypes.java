/*******************************************************************************
 * Copyright (c) 2005, 2012 ETH Zurich and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     ETH Zurich - initial API and implementation
 *     Systerel - code extracted from class ElementTypeManager
 *******************************************************************************/
package org.rodinp.internal.core;

import static org.rodinp.internal.core.ItemTypeUtils.debug;
import static org.rodinp.internal.core.ItemTypeUtils.getSortedIds;

import java.util.HashMap;

import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IExtensionRegistry;
import org.eclipse.core.runtime.Platform;
import org.rodinp.core.IInternalElement;
import org.rodinp.core.IInternalElementType;
import org.rodinp.core.RodinCore;

/**
 * Stores a map between ids and internal element types, as defined by the
 * extension point <code>internalElementTypes</code>.
 * <p>
 * Instances of this class are immutable and therefore thread-safe.
 * </p>
 * 
 * @author Laurent Voisin
 */
public class InternalElementTypes {

	// Local id of the internalElementTypes extension point of this plug-in
	protected static final String INTERNAL_ELEMENT_TYPES_ID = "internalElementTypes";

	private final HashMap<String, InternalElementType<?>> map//
	= new HashMap<String, InternalElementType<?>>();

	public InternalElementTypes() {
		computeInternalElementTypes();
	}

	public InternalElementType<?> get(String id) {
		return map.get(id);
	}

	public IInternalElementType<?> getElement(String elementId) {
		return map.get(elementId);
	}

	protected void computeInternalElementTypes() {
		final IConfigurationElement[] elements = readExtensions();
		for (final IConfigurationElement element : elements) {
			final InternalElementType<?> type = new InternalElementType<IInternalElement>(
					element);
			map.put(type.getId(), type);
		}

		if (ElementTypeManager.VERBOSE) {
			debug("---------------------------------------------------");
			debug("Internal element types known to the Rodin database:");
			for (final String id : getSortedIds(map)) {
				final InternalElementType<?> type = get(id);
				debug("  " + type.getId());
				debug("    name: " + type.getName());
				debug("    class: " + type.getClassName());
			}
			debug("---------------------------------------------------");
		}
	}

	protected IConfigurationElement[] readExtensions() {
		final IExtensionRegistry registry = Platform.getExtensionRegistry();
		return registry.getConfigurationElementsFor(RodinCore.PLUGIN_ID,
				INTERNAL_ELEMENT_TYPES_ID);
	}

}
/*******************************************************************************
 * Copyright (c) 2006, 2012 ETH Zurich and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     ETH Zurich - initial API and implementation
 *******************************************************************************/
package org.eventb.internal.core.tool.graph;

import org.eventb.internal.core.tool.ModuleDesc;
import org.eventb.internal.core.tool.types.IModule;

/**
 * @author Stefan Hallerstede
 *
 */
public class FilterModuleNode extends ModuleNode {

	public FilterModuleNode(
			ModuleDesc<? extends IModule> object, String id, String[] predecs, ModuleGraph graph) {
		super(object, id, predecs, graph);
	}
	
	@Override
	public void storeFilterInParent(ModuleNode node) {
		assert getObject().getParent().equals(node.getId());
		node.addChildFilter(this);
	}

	@Override
	public boolean canBeParent() {
		return false;
	}
	
}

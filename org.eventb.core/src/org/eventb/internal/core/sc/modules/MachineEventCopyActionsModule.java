/*******************************************************************************
 * Copyright (c) 2008 University of Southampton.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.eventb.internal.core.sc.modules;

import org.eventb.core.EventBPlugin;
import org.eventb.core.ILabeledElement;
import org.eventb.core.IRefinesEvent;
import org.eventb.core.ISCEvent;
import org.eventb.core.sc.SCCore;
import org.eventb.core.sc.state.ILabelSymbolInfo;
import org.eventb.core.tool.IModuleType;
import org.eventb.internal.core.sc.symbolTable.SymbolFactory;
import org.rodinp.core.RodinDBException;

/**
 * @author Stefan Hallerstede
 * 
 */
public class MachineEventCopyActionsModule extends
		MachineEventCopyLabeledElementsModule {

	public static final IModuleType<MachineEventModule> MODULE_TYPE = SCCore
			.getModuleType(EventBPlugin.PLUGIN_ID
					+ ".machineEventCopyActionsModule"); //$NON-NLS-1$

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eventb.internal.core.sc.modules.MachineEventCopyLabeledElementsModule
	 * #getSCElements(org.eventb.core.ISCEvent)
	 */
	@Override
	protected ILabeledElement[] getSCElements(ISCEvent scEvent)
			throws RodinDBException {
		return scEvent.getSCActions();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eventb.internal.core.tool.types.IModule#getModuleType()
	 */
	public IModuleType<?> getModuleType() {
		return MODULE_TYPE;
	}

	@Override
	protected ILabelSymbolInfo makeLabelSymbolInfo(String label,
			IRefinesEvent refinesEvent, String component) {
		return SymbolFactory.getInstance().makeAction(label, false,
				refinesEvent, component);
	}

}

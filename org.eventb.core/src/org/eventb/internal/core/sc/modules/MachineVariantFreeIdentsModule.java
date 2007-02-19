/*******************************************************************************
 * Copyright (c) 2006 ETH Zurich.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.eventb.internal.core.sc.modules;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eventb.core.EventBAttributes;
import org.eventb.core.EventBPlugin;
import org.eventb.core.ast.FreeIdentifier;
import org.eventb.core.sc.SCCore;
import org.eventb.core.sc.symbolTable.IIdentifierSymbolInfo;
import org.eventb.core.sc.symbolTable.IVariableSymbolInfo;
import org.eventb.core.sc.util.GraphProblem;
import org.eventb.core.tool.IModuleType;
import org.rodinp.core.IAttributeType;
import org.rodinp.core.IInternalElement;
import org.rodinp.core.IRodinProblem;

/**
 * @author Stefan Hallerstede
 *
 */
public class MachineVariantFreeIdentsModule extends MachineFormulaFreeIdentsModule {
	
	public static final IModuleType<MachineVariantFreeIdentsModule> MODULE_TYPE = 
		SCCore.getModuleType(EventBPlugin.PLUGIN_ID + ".machineVariantFreeIdentsModule"); //$NON-NLS-1$
	
	public IModuleType<?> getModuleType() {
		return MODULE_TYPE;
	}

	@Override
	protected IRodinProblem declaredFreeIdentifierError() {
		return GraphProblem.VariantFreeIdentifierError;
	}

	/* (non-Javadoc)
	 * @see org.eventb.internal.core.sc.modules.PredicateFreeIdentsModule#getSymbolInfo(org.eventb.core.ast.FreeIdentifier)
	 */
	@Override
	protected IIdentifierSymbolInfo getSymbolInfo(
			IInternalElement element, 
			FreeIdentifier freeIdentifier,
			IProgressMonitor monitor) throws CoreException {
		IIdentifierSymbolInfo symbolInfo = super.getSymbolInfo(element, freeIdentifier, monitor);
		if (symbolInfo != null && symbolInfo instanceof IVariableSymbolInfo) {
			IVariableSymbolInfo variableSymbolInfo = 
				(IVariableSymbolInfo) symbolInfo;
			if (!variableSymbolInfo.isConcrete())
				return null;
		}
		return symbolInfo;
	}

	@Override
	protected IAttributeType.String getAttributeType() {
		return EventBAttributes.EXPRESSION_ATTRIBUTE;
	}

}

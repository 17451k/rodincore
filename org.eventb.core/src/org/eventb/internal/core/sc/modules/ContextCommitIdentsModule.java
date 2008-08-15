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
import org.eventb.core.EventBPlugin;
import org.eventb.core.ast.Type;
import org.eventb.core.sc.SCCore;
import org.eventb.core.sc.SCProcessorModule;
import org.eventb.core.sc.state.IIdentifierSymbolTable;
import org.eventb.core.sc.state.ISCStateRepository;
import org.eventb.core.sc.symbolTable.IIdentifierSymbolInfo;
import org.eventb.core.tool.IModuleType;
import org.rodinp.core.IInternalParent;
import org.rodinp.core.IRodinElement;

/**
 * @author Stefan Hallerstede
 * 
 */
public class ContextCommitIdentsModule extends SCProcessorModule {

	public static final IModuleType<ContextCommitIdentsModule> MODULE_TYPE = SCCore
			.getModuleType(EventBPlugin.PLUGIN_ID
					+ ".contextCommitIdentsModule"); //$NON-NLS-1$

	public IModuleType<?> getModuleType() {
		return MODULE_TYPE;
	}

	public void process(IRodinElement element, IInternalParent target,
			ISCStateRepository repository, IProgressMonitor monitor)
			throws CoreException {

		IIdentifierSymbolTable identifierSymbolTable = (IIdentifierSymbolTable) repository
				.getState(IIdentifierSymbolTable.STATE_TYPE);

		for (IIdentifierSymbolInfo symbolInfo : identifierSymbolTable
				.getSymbolInfosFromTop()) {

			if (symbolInfo.isPersistent()) {

				Type type = symbolInfo.getType();

				if (type == null) { // identifier could not be typed

					symbolInfo.createUntypedErrorMarker(this);

					symbolInfo.setError();

				} else if (!symbolInfo.hasError()) {

					symbolInfo.createSCElement(target, null);
				}

				symbolInfo.makeImmutable();
			}
		}
	}

}

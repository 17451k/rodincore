/*******************************************************************************
 * Copyright (c) 2006, 2009 ETH Zurich and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     ETH Zurich - initial API and implementation
 *     Systerel - separation of file and root element
 *******************************************************************************/
package org.eventb.internal.core.sc.modules;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eventb.core.EventBPlugin;
import org.eventb.core.ISCVariable;
import org.eventb.core.ast.Type;
import org.eventb.core.sc.SCCore;
import org.eventb.core.sc.SCProcessorModule;
import org.eventb.core.sc.state.IIdentifierSymbolInfo;
import org.eventb.core.sc.state.IIdentifierSymbolTable;
import org.eventb.core.sc.state.ISCStateRepository;
import org.eventb.core.tool.IModuleType;
import org.rodinp.core.IInternalElement;
import org.rodinp.core.IRodinElement;

/**
 * @author Stefan Hallerstede
 * 
 */
public class MachineCommitIdentsModule extends SCProcessorModule {

	public static final IModuleType<MachineCommitIdentsModule> MODULE_TYPE = SCCore
			.getModuleType(EventBPlugin.PLUGIN_ID
					+ ".machineCommitIdentsModule"); //$NON-NLS-1$

	public IModuleType<?> getModuleType() {
		return MODULE_TYPE;
	}

	public void process(IRodinElement element, IInternalElement target,
			ISCStateRepository repository, IProgressMonitor monitor)
			throws CoreException {

		IIdentifierSymbolTable identifierSymbolTable = (IIdentifierSymbolTable) repository
				.getState(IIdentifierSymbolTable.STATE_TYPE);

		for (IIdentifierSymbolInfo symbolInfo : identifierSymbolTable
				.getSymbolInfosFromTop()) {

			if (symbolInfo.getSymbolType() == ISCVariable.ELEMENT_TYPE
					&& symbolInfo.isPersistent()) {

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

/*******************************************************************************
 * Copyright (c) 2006-2007 ETH Zurich.
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
import org.eventb.core.IMachineFile;
import org.eventb.core.ISCContextFile;
import org.eventb.core.ISCInternalContext;
import org.eventb.core.ISCMachineFile;
import org.eventb.core.ISCSeesContext;
import org.eventb.core.ISeesContext;
import org.eventb.core.sc.GraphProblem;
import org.eventb.core.sc.SCCore;
import org.eventb.core.sc.state.IContextPointerArray;
import org.eventb.core.sc.state.IMachineAccuracyInfo;
import org.eventb.core.sc.state.ISCStateRepository;
import org.eventb.core.tool.IModuleType;
import org.eventb.internal.core.sc.ContextPointerArray;
import org.eventb.internal.core.sc.Messages;
import org.rodinp.core.IInternalElement;
import org.rodinp.core.IInternalParent;
import org.rodinp.core.IRodinElement;
import org.rodinp.core.IRodinProblem;
import org.rodinp.core.RodinDBException;

/**
 * @author Stefan Hallerstede
 * 
 */
public class MachineSeesContextModule extends ContextPointerModule {

	private ContextPointerArray contextPointerArray;

	private IMachineAccuracyInfo accuracyInfo;

	@Override
	public void initModule(IRodinElement element,
			ISCStateRepository repository, IProgressMonitor monitor)
			throws CoreException {
		super.initModule(element, repository, monitor);

		IMachineFile machineFile = (IMachineFile) element;

		ISeesContext[] seesContexts = machineFile.getSeesClauses();

		ISCContextFile[] contextFiles = new ISCContextFile[seesContexts.length];

		for (int i = 0; i < seesContexts.length; i++) {
			if (seesContexts[i].hasSeenContextName()) {
				contextFiles[i] = seesContexts[i].getSeenSCContext();
				if (!contextFiles[i].exists()) {
					createProblemMarker(seesContexts[i],
							EventBAttributes.TARGET_ATTRIBUTE,
							GraphProblem.SeenContextNotFoundError,
							seesContexts[i].getSeenContextName());
					contextFiles[i] = null;
				} else if (!contextFiles[i].hasConfiguration()) {
					createProblemMarker(seesContexts[i],
							EventBAttributes.TARGET_ATTRIBUTE,
							GraphProblem.SeenContextWithoutConfigurationError,
							seesContexts[i].getSeenContextName());
					contextFiles[i] = null;
				}
			} else {
				createProblemMarker(seesContexts[i],
						EventBAttributes.TARGET_ATTRIBUTE,
						GraphProblem.SeenContextNameUndefError);
			}
		}

		contextPointerArray = new ContextPointerArray(
				IContextPointerArray.PointerType.SEES_POINTER, seesContexts,
				contextFiles);
		repository.setState(contextPointerArray);

		accuracyInfo = (IMachineAccuracyInfo) repository
				.getState(IMachineAccuracyInfo.STATE_TYPE);

	}

	@Override
	public void endModule(IRodinElement element, ISCStateRepository repository,
			IProgressMonitor monitor) throws CoreException {
		super.endModule(element, repository, monitor);
		contextPointerArray = null;
		accuracyInfo = null;
	}

	public static final IModuleType<MachineSeesContextModule> MODULE_TYPE = SCCore
			.getModuleType(EventBPlugin.PLUGIN_ID + ".machineSeesContextModule"); //$NON-NLS-1$

	public static final String SEES_NAME_PREFIX = "SEES";

	public IModuleType<?> getModuleType() {
		return MODULE_TYPE;
	}

	public void process(IRodinElement element, IInternalParent target,
			ISCStateRepository repository, IProgressMonitor monitor)
			throws CoreException {

		// we need to do everything up to this point
		// produce a define repository state

		if (contextPointerArray.size() == 0) {
			contextPointerArray.makeImmutable();
			return; // nothing to do
		}

		monitor.subTask(Messages.bind(Messages.progress_MachineSees));

		boolean accurate = fetchSCContexts(contextPointerArray, monitor);

		contextPointerArray.makeImmutable();

		accurate &= createSeesClauses((ISCMachineFile) target, null);

		if (!accurate)
			accuracyInfo.setNotAccurate();

		createInternalContexts(target, contextPointerArray.getValidContexts(),
				repository, null);

	}

	@Override
	protected ISCInternalContext getSCInternalContext(IInternalParent target,
			String elementName) {
		return ((ISCMachineFile) target).getSCSeenContext(elementName);
	}

	private boolean createSeesClauses(ISCMachineFile target,
			IProgressMonitor monitor) throws RodinDBException {

		boolean accurate = true;

		int count = 0;
		final int size = contextPointerArray.size();
		for (int i = 0; i < size; ++i) {
			final ISCContextFile scSeenContext = contextPointerArray
					.getSCContextFile(i);
			if (scSeenContext == null || contextPointerArray.hasError(i)) {
				accurate = false;
			} else {
				final ISCSeesContext scSees = target
						.getSCSeesClause(SEES_NAME_PREFIX + count++);
				scSees.create(null, monitor);

				scSees.setSeenSCContext(scSeenContext, monitor);

				final IInternalElement source = contextPointerArray
						.getContextPointer(i);
				scSees.setSource(source, null);
			}
		}
		return accurate;
	}

	@Override
	protected IRodinProblem getRedundantContextWarning() {
		return GraphProblem.SeenContextRedundantWarning;
	}

}

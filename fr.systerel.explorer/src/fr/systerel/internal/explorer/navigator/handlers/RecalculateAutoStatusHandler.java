/*******************************************************************************
 * Copyright (c) 2010, 2021 Systerel and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Systerel - initial API and implementation
 *     ISP RAS - parallelize code
 *******************************************************************************/
package fr.systerel.internal.explorer.navigator.handlers;

import static org.eventb.core.EventBPlugin.recalculateAutoStatus;

import java.util.Set;

import org.eclipse.core.resources.WorkspaceJob;
import org.eclipse.core.runtime.SubMonitor;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eventb.core.IPSStatus;
import org.rodinp.core.RodinDBException;

import fr.systerel.internal.explorer.navigator.actionProviders.Messages;

/**
 * Handler for the 'Recalculate Auto Status' command.
 */
public class RecalculateAutoStatusHandler extends AbstractJobHandler {

	@Override
	protected WorkspaceJob getWorkspaceJob(IStructuredSelection sel) {
		return new ParallelProofStatusJob(Messages.dialogs_recalculatingAutoStatus,
				false, sel) {

			@Override
			protected void perform(Set<IPSStatus> statuses,
					SubMonitor subMonitor) throws RodinDBException,
					InterruptedException {
				recalculateAutoStatus(statuses, subMonitor);
			}

		};
	}

}

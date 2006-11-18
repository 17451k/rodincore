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
import org.eventb.core.IEvent;
import org.eventb.core.ILabeledElement;
import org.eventb.core.sc.FilterModule;
import org.eventb.core.sc.GraphProblem;
import org.eventb.core.sc.state.IAbstractEventInfo;
import org.eventb.core.sc.state.IAbstractEventTable;
import org.eventb.core.sc.state.IStateSC;
import org.eventb.core.state.IStateRepository;
import org.rodinp.core.IRodinElement;

/**
 * @author Stefan Hallerstede
 *
 */
public class MachinePreviousEventLabelModule extends FilterModule {
	
	private IAbstractEventTable abstractEventTable;

	@Override
	public void initModule(
			IStateRepository<IStateSC> repository, 
			IProgressMonitor monitor) throws CoreException {
		abstractEventTable = (IAbstractEventTable)
			repository.getState(IAbstractEventTable.STATE_TYPE);
	}

	/* (non-Javadoc)
	 * @see org.eventb.core.sc.IAcceptorModule#accept(org.rodinp.core.IRodinElement, org.rodinp.core.IInternalParent, org.eventb.core.sc.IStateRepository, org.eclipse.core.runtime.IProgressMonitor)
	 */
	public boolean accept(
			IRodinElement element,
			IStateRepository<IStateSC> repository,
			IProgressMonitor monitor) throws CoreException {
		ILabeledElement labeledElement = (ILabeledElement) element;
		String label = labeledElement.getLabel();
		IAbstractEventInfo abstractEventInfo = 
			abstractEventTable.getAbstractEventInfo(label);
		if (abstractEventInfo != null) {
			if (element instanceof IEvent) {
				if (abstractEventInfo.isForbidden()) {
					createProblemMarker(
							labeledElement,
							EventBAttributes.LABEL_ATTRIBUTE,
							GraphProblem.ObsoleteEventLabelWarning,
							label);
				}
			} else {
				createProblemMarker(
						labeledElement,
						EventBAttributes.LABEL_ATTRIBUTE,
						GraphProblem.WasAbstractEventLabelWarning,
						label);
			}
		}
		
		return true;
	}

	/* (non-Javadoc)
	 * @see org.eventb.core.sc.Module#endModule(org.eventb.core.sc.IStateRepository, org.eclipse.core.runtime.IProgressMonitor)
	 */
	@Override
	public void endModule(
			IStateRepository<IStateSC> repository, 
			IProgressMonitor monitor) throws CoreException {
		abstractEventTable = null;
	}

}

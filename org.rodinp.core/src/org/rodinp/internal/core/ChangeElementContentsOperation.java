/*******************************************************************************
 * Copyright (c) 2005 ETH Zurich.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.rodinp.internal.core;

import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.runtime.jobs.ISchedulingRule;
import org.rodinp.core.IRodinDBStatus;
import org.rodinp.core.IRodinDBStatusConstants;
import org.rodinp.core.IRodinElement;
import org.rodinp.core.IRodinElementDelta;
import org.rodinp.core.RodinDBException;
import org.rodinp.core.basis.InternalElement;
import org.rodinp.core.basis.RodinFile;
import org.rodinp.internal.core.util.Messages;

public class ChangeElementContentsOperation extends RodinDBOperation{

	private InternalElement element;
	private String newContents;
	
	public ChangeElementContentsOperation(InternalElement element, String newContents) {
		super(new IRodinElement[] { element });
		this.element = element;
		this.newContents = newContents;
	}

	@Override
	protected void executeOperation() throws RodinDBException {
		RodinElementDelta delta = newRodinElementDelta();

		try {
			beginTask(Messages.operation_changeElementContentsProgress, 2);
			RodinFile file = element.getOpenableParent();
			RodinFileElementInfo fileInfo = (RodinFileElementInfo) file.getElementInfo(getSubProgressMonitor(1));
			fileInfo.changeDescendantContents(element, newContents);
			delta.changed(element, IRodinElementDelta.F_CONTENT);
			addDelta(delta);
			worked(1);
		} finally {
			done();
		}
	}

	@Override
	protected ISchedulingRule getSchedulingRule() {
		IResource resource = element.getOpenableParent().getResource();
		IWorkspace workspace = resource.getWorkspace();
		if (resource.exists()) {
			return workspace.getRuleFactory().modifyRule(resource);
		} else {
			return super.getSchedulingRule();
		}
	}

	/**
	 * Possible failures:
	 * <ul>
	 * <li>NO_ELEMENTS_TO_PROCESS - the element supplied to the operation is
	 * <code>null</code>.
	 * <li>ELEMENT_DOES_NOT_EXIST - the element supplied to the operation
	 * doesn't exist yet.
	 * <li>NULL_STRING - the contents supplied to the operation is
	 * <code>null</code>.
	 * </ul>
	 */
	@Override
	public IRodinDBStatus verify() {
		super.verify();
		if (! element.exists()) {
			return new RodinDBStatus(
					IRodinDBStatusConstants.ELEMENT_DOES_NOT_EXIST,
					element);
		}
		if (newContents == null) {
			return new RodinDBStatus(IRodinDBStatusConstants.NULL_STRING);
		}
		return RodinDBStatus.VERIFIED_OK;
	}
}

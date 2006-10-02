/*******************************************************************************
 * Copyright (c) 2006 ETH Zurich.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.rodinp.internal.core;

import org.eclipse.core.runtime.jobs.ISchedulingRule;
import org.rodinp.core.IRodinDBStatus;
import org.rodinp.core.IRodinDBStatusConstants;
import org.rodinp.core.IRodinElement;
import org.rodinp.core.IRodinElementDelta;
import org.rodinp.core.RodinDBException;
import org.rodinp.core.basis.InternalElement;
import org.rodinp.core.basis.RodinFile;
import org.rodinp.internal.core.util.Messages;

public class RemoveElementAttributeOperation extends RodinDBOperation{

	private InternalElement element;
	private String attrName;
	
	public RemoveElementAttributeOperation(InternalElement element,
			String attrName) {
		super(new IRodinElement[] { element });
		this.element = element;
		this.attrName = attrName;
	}

	@Override
	protected void executeOperation() throws RodinDBException {
		try {
			beginTask(Messages.operation_removeElementAttributeProgress, 2);
			RodinFile file = element.getRodinFile();
			RodinFileElementInfo fileInfo = (RodinFileElementInfo)
					file.getElementInfo(getSubProgressMonitor(1));
			if (fileInfo.removeAttribute(element, attrName)) {
				RodinElementDelta delta = newRodinElementDelta();
				delta.changed(element, IRodinElementDelta.F_ATTRIBUTE);
				addDelta(delta);
			}
			worked(1);
		} finally {
			done();
		}
	}

	@Override
	protected ISchedulingRule getSchedulingRule() {
		assert false;
		return null;
	}

	/**
	 * Possible failures:
	 * <ul>
	 * <li>NO_ELEMENTS_TO_PROCESS - the element supplied to the operation is
	 * <code>null</code>.
	 * <li>ELEMENT_DOES_NOT_EXIST - the element supplied to the operation
	 * doesn't exist yet.
	 * <li>READ_ONLY - the element supplied to the operation is read only.
	 * </ul>
	 */
	@Override
	public IRodinDBStatus verify() {
		super.verify();
		if (! element.exists()) {
			return new RodinDBStatus(
					IRodinDBStatusConstants.ELEMENT_DOES_NOT_EXIST,
					element
			);
		}
		if (element.isReadOnly()) {
			return new RodinDBStatus(
					IRodinDBStatusConstants.READ_ONLY,
					element
			);
		}
		return RodinDBStatus.VERIFIED_OK;
	}
}

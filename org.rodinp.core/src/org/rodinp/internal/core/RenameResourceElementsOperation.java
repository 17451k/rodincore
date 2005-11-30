/*******************************************************************************
 * Copyright (c) 2005 ETH Zurich.
 * Strongly inspired by org.eclipse.jdt.internal.core.RenameResourceElementsOperation.java which is
 *
 * Copyright (c) 2000, 2004 IBM Corporation and others. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.rodinp.internal.core;

import org.rodinp.core.IRodinDBStatusConstants;
import org.rodinp.core.IRodinElement;
import org.rodinp.core.IRodinFile;
import org.rodinp.core.RodinDBException;
import org.rodinp.core.basis.RodinFile;
import org.rodinp.internal.core.util.Messages;

/**
 * This operation renames resources (Rodin files).
 */
public class RenameResourceElementsOperation extends
		MoveResourceElementsOperation {
	/**
	 * When executed, this operation will rename the specified elements with the
	 * given names in the corresponding destinations.
	 */
	public RenameResourceElementsOperation(IRodinElement[] elements,
			String[] newNames, boolean force) {
		super(elements, force);
		setRenamings(newNames);
	}

	public RenameResourceElementsOperation(RodinFile elementToProcess, String newName, boolean replace) {
		super(elementToProcess, replace);
		setRenamings(new String[] {newName});
	}

	@Override
	protected String getMainTaskName() {
		return Messages.operation_renameResourceProgress;
	}

	@Override
	protected boolean isRename() {
		return true;
	}

	@Override
	protected void verify(IRodinElement element) throws RodinDBException {
		super.verify(element);

		if (!(element instanceof IRodinFile)) {
			error(IRodinDBStatusConstants.INVALID_ELEMENT_TYPES, element);
		}
		// check here for primary working copy when they're introduced.
		verifyRenaming(element);
	}
}

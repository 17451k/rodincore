/*******************************************************************************
 * Copyright (c) 2013 Systerel and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Systerel - initial API and implementation
 *******************************************************************************/
package org.eventb.internal.ui.eventbeditor.rename.handlers;

import org.eventb.core.IAction;
import org.rodinp.core.IInternalElementType;

/**
 * Rename handlers.
 */
public class AutoRenameHandlers {

	private AutoRenameHandlers() {
		// no instantiation
	}

	/**
	 * The handler class for automatic action label renaming.
	 */
	public static class AutoActionRenameHandler extends
			AbstractAutoRenameHandler {

		@Override
		protected IInternalElementType<?> getType() {
			return IAction.ELEMENT_TYPE;
		}

	}

}

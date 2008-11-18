/*******************************************************************************
 * Copyright (c) 2008 Systerel and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License  v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Systerel - initial API and implementation
  *******************************************************************************/

package fr.systerel.editor.handlers;

import org.eclipse.swt.custom.ST;

/**
 * A handler that invokes a copy action on a styled text.
 *
 */
public class CopyHandler extends EditHandler {

	public CopyHandler() {
		action = ST.COPY;
	}
	

}

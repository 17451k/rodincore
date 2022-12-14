/*******************************************************************************
 * Copyright (c) 2006, 2013 ETH Zurich and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     ETH Zurich - initial API and implementation
 *     Systerel - separation of file and root element
 *     Systerel - introduced read only elements through new super class
 *     Systerel - refactored using NewInvariantsWizard
 *     Systerel - refactored using AbstractCreationWizardHandler
 *******************************************************************************/
package org.eventb.internal.ui.eventbeditor.handlers.create;

import org.eventb.internal.ui.eventbeditor.wizards.EventBCreationWizards;

public class NewInvariantHandler extends AbstractCreationWizardHandler {

	@Override
	public void openCreationWizard() {
		new EventBCreationWizards.NewInvariantsWizard().openDialog(editor);
	}

}

/*******************************************************************************
 * Copyright (c) 2009, 2011 Systerel and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Systerel - initial API and implementation
 *******************************************************************************/
package org.eventb.internal.ui.autocompletion;

import org.eclipse.jface.fieldassist.ContentProposalAdapter;
import org.eclipse.jface.fieldassist.IContentProposalProvider;
import org.eclipse.jface.fieldassist.IControlContentAdapter;
import org.eclipse.swt.widgets.Control;
import org.eclipse.ui.fieldassist.ContentAssistCommandAdapter;
import org.eventb.ui.autocompletion.IEventBContentProposalAdapter;

/**
 * Encapsulates a <code>ContentProposalAdapter</code> while tracking whether the
 * proposal pop-up is currently open or closed.
 * 
 * @author Aurélien Gilles
 */
public class EventBContentProposalAdapter implements IEventBContentProposalAdapter {

	private boolean proposalPopupOpen;
	private final ContentProposalAdapter adaptee;
	
	public EventBContentProposalAdapter(Control control,
			IControlContentAdapter controlContentAdapter,
			IContentProposalProvider proposalProvider) {
		proposalPopupOpen = false;
		adaptee = new ContentAssistCommandAdapter(control,
				controlContentAdapter, proposalProvider, null, null, true);
		adaptee.addContentProposalListener(this);
	}

	@Override
	public boolean isProposalPopupOpen() {
		return proposalPopupOpen;
	}

	@Override
	public void proposalPopupClosed(ContentProposalAdapter a) {
		proposalPopupOpen = false;
	}

	@Override
	public void proposalPopupOpened(ContentProposalAdapter a) {
		proposalPopupOpen = true;
	}

}

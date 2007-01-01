/*******************************************************************************
 * Copyright (c) 2005-2006 ETH Zurich.
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Rodin @ ETH Zurich
 ******************************************************************************/
package org.eventb.internal.ui.eventbeditor;

import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.part.IPage;
import org.eclipse.ui.part.IPageBookViewPage;
import org.eclipse.ui.part.MessagePage;
import org.eclipse.ui.part.PageBook;
import org.eventb.ui.EventBUIPlugin;

/**
 * @author htson
 *         <p>
 *         An implementation of Event-B Mirror to shows the information about
 *         carrier sets of the editting construct.
 */
public class CarrierSetMirror extends EventBMirror {
	/**
	 * The plug-in identifier of the Carrier Set Mirror View (value
	 * <code>"org.eventb.ui.views.CarrierSetMirror"</code>).
	 */
	public static final String VIEW_ID = EventBUIPlugin.PLUGIN_ID
			+ ".views.CarrierSetMirror";

	private String defaultText = "Information about carrier sets is not available";

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.ui.part.PageBookView#createDefaultPage(org.eclipse.ui.part.PageBook)
	 */
	@Override
	protected IPage createDefaultPage(PageBook book) {
		MessagePage page = new MessagePage();
		initPage(page);
		page.createControl(book);
		page.setMessage(defaultText);
		return page;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.ui.part.PageBookView#doCreatePage(org.eclipse.ui.IWorkbenchPart)
	 */
	@Override
	protected PageRec doCreatePage(IWorkbenchPart part) {
		// Try to get a carrier set mirror page.
		Object obj = part.getAdapter(ICarrierSetMirrorPage.class);
		if (obj instanceof ICarrierSetMirrorPage) {
			ICarrierSetMirrorPage page = (ICarrierSetMirrorPage) obj;
			if (page instanceof IPageBookViewPage)
				initPage((IPageBookViewPage) page);
			page.createControl(getPageBook());
			return new PageRec(part, page);
		}
		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.ui.part.PageBookView#getBootstrapPart()
	 */
	@Override
	protected IWorkbenchPart getBootstrapPart() {
		IWorkbenchPage page = getSite().getPage();
		if (page != null)
			if (page.getActiveEditor() instanceof EventBContextEditor)
				return page.getActiveEditor();

		return null;
	}

}
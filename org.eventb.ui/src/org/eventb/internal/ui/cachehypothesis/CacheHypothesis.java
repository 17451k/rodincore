/*******************************************************************************
 * Copyright (c) 2007 ETH Zurich.
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Rodin @ ETH Zurich
 ******************************************************************************/

package org.eventb.internal.ui.cachehypothesis;

import org.eclipse.ui.IWorkbenchPart;
import org.eventb.internal.ui.prover.ProverContentOutline;
import org.eventb.ui.EventBUIPlugin;

/**
 * @author htson
 *         <p>
 *         Implementation of the Cache Hypothesis View.
 */
public class CacheHypothesis extends ProverContentOutline {

	/**
	 * The identifier of the Cache Hypothesis View (value
	 * <code>"org.eventb.ui.views.CacheHypothesis"</code>).
	 */
	public static final String VIEW_ID = EventBUIPlugin.PLUGIN_ID
			+ ".views.CacheHypothesis";

	public CacheHypothesis() {
		super("Cached Hypothesis is not available");
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.ui.part.PageBookView#doCreatePage(org.eclipse.ui.IWorkbenchPart)
	 */
	@Override
	protected PageRec doCreatePage(IWorkbenchPart part) {
		// Try to get a Search Hypothesis Page.
		Object obj = part.getAdapter(ICacheHypothesisPage.class);
		if (obj instanceof ICacheHypothesisPage) {
			ICacheHypothesisPage page = (ICacheHypothesisPage) obj;
			initPage(page);
			page.createControl(getPageBook());
			return new PageRec(part, page);
		}
		// There is no content outline
		return null;
	}

}
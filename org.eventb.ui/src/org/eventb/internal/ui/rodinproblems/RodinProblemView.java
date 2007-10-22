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
package org.eventb.internal.ui.rodinproblems;

import static org.eventb.eventBKeyboard.preferences.PreferenceConstants.EVENTB_MATH_FONT;

import org.eclipse.core.resources.IMarker;
import org.eclipse.jface.resource.JFaceResources;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.ui.views.markers.internal.ProblemView;

/**
 * Overriding of the regular Problems view for Rodin.
 * <p>
 * The problem with the Problems view of <code>org.eclipse.ide</code> is that
 * it doesn't allow clients to set the font used to display problem description.
 * However, the Rodin tools sometimes create messages that contain mathematical
 * symbols, which are not displayed properly with the default font, most notably
 * on Windows computers.
 * </p>
 * <p>
 * That's why this view has been created, although it violates Eclipse rules, by
 * extending an internal class.
 * </p>
 * 
 * @author Laurent Voisin
 */
@SuppressWarnings("restriction")
public class RodinProblemView extends ProblemView {
	
	public static final String VIEW_ID = "org.eventb.ui.views.RodinProblemView"; 

	private final static String[] ROOT_TYPES = { IMarker.PROBLEM };

	@Override
	public void createPartControl(Composite parent) {
		super.createPartControl(parent);
		final Tree tree = getViewer().getTree();
		final Font font = JFaceResources.getFont(EVENTB_MATH_FONT);
		tree.setFont(font);
	}

	@Override
	protected String[] getRootTypes() {
		return ROOT_TYPES;
	}

}

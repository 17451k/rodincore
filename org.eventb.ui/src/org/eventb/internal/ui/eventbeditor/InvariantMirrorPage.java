/*******************************************************************************
 * Copyright (c) 2005 ETH-Zurich
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     ETH RODIN Group
 *******************************************************************************/

package org.eventb.internal.ui.eventbeditor;

import org.eclipse.ui.forms.events.HyperlinkAdapter;
import org.eclipse.ui.forms.events.HyperlinkEvent;
import org.eventb.core.IInvariant;
import org.eventb.internal.ui.UIUtils;
import org.rodinp.core.IInternalElement;
import org.rodinp.core.IRodinElement;
import org.rodinp.core.RodinDBException;


/**
 * This sample class demonstrates how to plug-in a new
 * workbench view. The view shows data obtained from the
 * model. The sample creates a dummy model on the fly,
 * but a real implementation would connect to the model
 * available either in this or another plug-in (e.g. the workspace).
 * The view is connected to the model using a content provider.
 * <p>
 * The view uses a label provider to define how model
 * objects should be presented in the view. Each
 * view can present the same model objects using
 * different labels and icons, if needed. Alternatively,
 * a single label provider can be shared between views
 * in order to ensure that objects of the same type are
 * presented in the same way everywhere.
 * <p>
 */

public class InvariantMirrorPage
	extends EventBMirrorPage
	implements	IInvariantMirrorPage
{

	public InvariantMirrorPage(EventBEditor editor) {
		super(editor);
	}   
    
	/**
	 * Return the form (XML formatted) string that represents the information 
	 * of the constants.
	 */
	protected String getFormString() {
		String formString = "<form>";
		try {
			IRodinElement [] invariants = editor.getRodinInput().getChildrenOfType(IInvariant.ELEMENT_TYPE);
			for (int i = 0; i < invariants.length; i++) {
				formString = formString + "<li style=\"bullet\">" + UIUtils.makeHyperlink(invariants[i].getElementName()) + ": ";
				formString = formString + UIUtils.XMLWrapUp(((IInternalElement) invariants[i]).getContents()); 
				formString = formString + "</li>";
			}
		}
		catch (RodinDBException e) {
			// TODO Exception handle
			e.printStackTrace();
		}
		formString = formString + "</form>";

		return formString;
	}
	
	/**
	 * Return the hyperlink listener which enable the navigation on the form. 
	 */
	protected HyperlinkAdapter createHyperlinkListener() {
		return (new HyperlinkAdapter() {
			public void linkActivated(HyperlinkEvent e) {
				try {
					UIUtils.debug("Event: " + e.toString());
					IRodinElement [] invariants = editor.getRodinInput().getChildrenOfType(IInvariant.ELEMENT_TYPE);
					for (int i = 0; i < invariants.length; i++) {
						if (e.getHref().equals(invariants[i].getElementName())) {
							editor.setSelection(invariants[i]);
							return;
						}
					}
				}
				catch (RodinDBException exception) {
					// TODO Exception handle
					exception.printStackTrace();
				}
			}
		});
	}

}
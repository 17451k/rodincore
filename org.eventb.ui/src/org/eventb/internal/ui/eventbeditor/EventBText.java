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

import org.eclipse.jface.resource.JFaceResources;
import org.eclipse.jface.util.PropertyChangeEvent;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.widgets.Text;
import org.eventb.eventBKeyboard.preferences.PreferenceConstants;

/**
 * @author htson
 * This is the class that hold a Text that used Event-B Text Font.
 */
public class EventBText 
	implements IEventBInputText
{
	// The actual Text.
	Text text;
	
	
	/**
	 * Constructor.
	 * <p>
	 * @param parent the Composite parent of this
	 * @param toolkit the FormToolkit used to creat the Text
	 * @param style the style used to create the Text
	 */
	public EventBText(Text text) {
		this.text = text;
		// TODO Create a new font for Event-B Text
		Font font = JFaceResources.getFont(PreferenceConstants.EVENTB_MATH_FONT);
		text.setFont(font);

		JFaceResources.getFontRegistry().addListener(this);
	}

	
	/**
	 * This call back is used when the font is change in the Preferences.
	 */
	public void propertyChange(PropertyChangeEvent event) {
		if (event.getProperty().equals(PreferenceConstants.EVENTB_MATH_FONT)) {
			Font font = JFaceResources.getFont(PreferenceConstants.EVENTB_MATH_FONT);
			text.setFont(font);
		}
	}
	
	
	/**
	 * Setting the focus to the contained Text.
	 */
	public void setFocus() {text.setFocus(); text.selectAll();}
	

	/* (non-Javadoc)
	 * @see org.eventb.internal.ui.eventbeditor.IEventBInputText#getTextWidget()
	 */
	public Text getTextWidget() {
		return text;
	}
	
}

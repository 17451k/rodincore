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

package org.eventb.ui.editors;

import org.eclipse.jface.resource.JFaceResources;
import org.eclipse.jface.util.PropertyChangeEvent;
import org.eclipse.swt.events.FocusListener;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eventb.eventBKeyboard.preferences.PreferenceConstants;
import org.eventb.eventBKeyboard.translators.EventBTextModifyListener;
import org.eventb.ui.editors.IEventBInputText;

/**
 * @author htson
 * <p>
 * This is the class that hold a Text using to display and get expressions which are
 * in the mathematical language of Event-B.
 */
public class EventBMath
	implements IEventBInputText
{

	// The actual Text.
	private Text text;
	
	
	/**
	 * Contructor.
	 * <p>
	 * @param parent the Composite parent of this
	 * @param toolkit the FormToolkit used to creat the Text
	 * @param style the style used to create the Text
	 */
	public EventBMath(Composite parent, FormToolkit toolkit, int style) {
		text = toolkit.createText(parent, "", style);
		Font font = JFaceResources.getFont(PreferenceConstants.EVENTB_MATH_FONT);
		text.setFont(font);
		text.addModifyListener(new EventBTextModifyListener());
			
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
	 * @return the content of the Text inside.
	 */
	public String getText() {return text.getText();}
	
	
	/**
	 * Setting the layout data of the Text.
	 * <p>
	 * @param gd Any grid data
	 */
	public void setLayoutData(GridData gd) {text.setLayoutData(gd);}
	
	
	/**
	 * Setting the focus to the contained Text.
	 */
	public void setFocus() {text.setFocus(); text.selectAll();}
	
	
	/**
	 * Setting the content of the Text.
	 * <p>
	 * @param str Any string
	 */
	public void setText(String string) {text.setText(string);}
	
	
	/**
	 * Adding a listener for when the focus is changed.
	 * <p>
	 * @param listener a FocusListener
	 */
	public void addFocusListener(FocusListener listener) {text.addFocusListener(listener);}
	
	
	/**
	 * Adding a listener for any modification of the Text.
	 * <p>
	 * @param listener a ModifyListener
	 */
	public void addModifyListener(ModifyListener listener) {text.addModifyListener(listener);}

}
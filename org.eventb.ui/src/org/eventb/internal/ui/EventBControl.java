/*******************************************************************************
 * Copyright (c) 2005 ETH Zurich.
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Rodin @ ETH Zurich
 ******************************************************************************/

package org.eventb.internal.ui;

import org.eclipse.jface.resource.JFaceResources;
import org.eclipse.jface.util.IPropertyChangeListener;
import org.eclipse.jface.util.PropertyChangeEvent;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.widgets.Control;
import org.eventb.eventBKeyboard.preferences.PreferenceConstants;

/**
 * @author htson
 *         <p>
 *         This decorator class implement the
 *         <code>org.eventb.ui.IEventBControl</code> which add the default
 *         Event-B font to the control.
 */
public class EventBControl implements IEventBControl, IPropertyChangeListener {
	// The actual control widget
	Control control;

	/**
	 * Constructor which stored the widget, set the font then set itself as a
	 * listener to the changed in the font registry
	 * <p>
	 * 
	 * @param control
	 *            any control widget
	 */
	public EventBControl(Control control) {
		this.control = control;
		Font font = JFaceResources
				.getFont(PreferenceConstants.EVENTB_MATH_FONT);
		control.setFont(font);

		JFaceResources.getFontRegistry().addListener(this);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.jface.util.IPropertyChangeListener#propertyChange(org.eclipse.jface.util.PropertyChangeEvent)
	 */
	public void propertyChange(PropertyChangeEvent event) {
		if (event.getProperty().equals(PreferenceConstants.EVENTB_MATH_FONT)) {
			Font font = JFaceResources
					.getFont(PreferenceConstants.EVENTB_MATH_FONT);
			control.setFont(font);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eventb.internal.ui.IEventBControl#getControl()
	 */
	public Control getControl() {
		return control;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eventb.internal.ui.IEventBControl#dispose()
	 */
	public void dispose() {
		JFaceResources.getFontRegistry().removeListener(this);
		control.dispose();
	}

}

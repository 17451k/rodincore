/*******************************************************************************
 * Copyright (c) 2005, 2009 ETH Zurich and others.
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     ETH Zurich - initial API and implementation
 *     Systerel - changed double click behavior
 *     Systerel - added class Translator
 *     ETH Zurich - adapted to org.rodinp.keyboard
 ******************************************************************************/
package org.eventb.internal.ui;

import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.events.FocusAdapter;
import org.eclipse.swt.events.FocusEvent;
import org.eclipse.swt.events.FocusListener;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.widgets.Text;
import org.eventb.eventBKeyboard.EventBStyledTextModifyListener;
import org.rodinp.keyboard.RodinKeyboardPlugin;

/**
 * @author htson
 *         <p>
 *         This is the class that holds a StyledText to display and to retrieve
 *         expressions which are in the mathematical language of Event-B.
 */
public class EventBStyledText extends EventBControl implements IEventBInputText {

	private final StyledText text;

	/**
	 * Constructor.
	 * <p>
	 * 
	 * @param text
	 *            a Text widget
	 */
	public EventBStyledText(final StyledText text, final boolean isMath) {
		super(text);
		this.text = text;
		text.addMouseListener(new DoubleClickStyledTextListener(text));
		if (isMath) {
			final Translator translator = new Translator(text);
			text.addModifyListener(translator);
			text.addFocusListener(new FocusListener() {
				public void focusGained(FocusEvent e) {
					if (translate()) {
						commit();
					}
				}
				public void focusLost(FocusEvent e) {
					translate();
					commit();
				}
			});
		} else {
			text.addMouseListener(new DoubleClickStyledTextListener(text));
			text.addFocusListener(new FocusAdapter() {
				@Override
				public void focusLost(FocusEvent e) {
					commit();
				}
			});
		}
	}

	// Translates the StyledText contents into Event-B Mathematical Language.
	// Returns true if something changed
	protected boolean translate(){
		if (!text.getEditable()) {
			return false;
		}
		final String original = text.getText();
		final String translated = RodinKeyboardPlugin.getDefault().translate(original);
		if (original.equals(translated)) {
			return false;
		}
		text.setText(translated);
		return true;
	}
	
	protected void commit() {
		// Do nothing. Client should override this method in order to implement
		// the intended behaviour.
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eventb.internal.ui.IEventBInputText#getTextWidget()
	 */
	public Text getTextWidget() {
		return (Text) getControl();
	}

	class Translator extends EventBStyledTextModifyListener {
		private final StyledText widget;

		public Translator(StyledText widget) {
			this.widget = widget;
		}

		@Override
		public void modifyText(ModifyEvent e) {
			if (!widget.isFocusControl()) {
				return;
			}
			super.modifyText(e);
		}
	}
	
}
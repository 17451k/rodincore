/*******************************************************************************
* Copyright (c) 2008 Systerel and others.
* All rights reserved. This program and the accompanying materials
* are made available under the terms of the Eclipse Public License v1.0
* which accompanies this distribution, and is available at
* http://www.eclipse.org/legal/epl-v10.html
*
* Contributors:
*     Systerel - initial API and implementation
*******************************************************************************/
package org.eventb.internal.ui;

import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.MouseListener;
import org.eclipse.swt.graphics.Point;

public abstract class AbstractDoubleClickListener implements MouseListener {

	interface ITextWidget {
		public abstract String getText();

		public abstract Point getSelection();

		public abstract void setSelection(Point p);
	}

	public abstract ITextWidget getWidget();

	public void mouseDoubleClick(MouseEvent e) {

		try {
			final Point selection = getWidget().getSelection();
			final String text = getWidget().getText();
			getWidget().setSelection(wordPosition(text, selection));
		} catch (IllegalArgumentException exception) {
			// the point is not null, so there is no offset. It's
			// occurred when position is end of widget
		}
	}

	private boolean isIdentifierChar(char c) {
		if(c == '\u03bb')
			return false;
		return Character.isJavaIdentifierPart(c);
	}

	private int offsetFirstNoId(String text, int offset, boolean increase) {
		final int step = (increase) ? 1 : -1;
		int i;
		for (i = offset; 0 <= i && i < text.length(); i = i + step) {
			if (!isIdentifierChar(text.charAt(i)))
				return i;
		}
		return -1;
	}

	private Point wordPosition(String text, Point selection) {
		final int textSize = text.length();
		final int lastOffset = offsetFirstNoId(text, selection.y, true);
		final int endOfVar = (lastOffset != -1 && lastOffset < textSize) ? lastOffset
				: textSize;

		final int firstOffset = offsetFirstNoId(text, selection.x, false);
		final int beginOfVar = (firstOffset >= 0) ? firstOffset + 1 : 0;
		return new Point(beginOfVar, endOfVar);
	}

	public void mouseDown(MouseEvent e) {
		// do nothing
	}

	public void mouseUp(MouseEvent e) {
		// do nothing
	}
}

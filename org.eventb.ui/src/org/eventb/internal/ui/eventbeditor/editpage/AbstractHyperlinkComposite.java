/*******************************************************************************
 * Copyright (c) 2007, 2013 ETH Zurich and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     ETH Zurich - initial API and implementation
 *     Systerel - used EventBSharedColor
 *     Systerel - separation of file and root element
 *     Systerel - introduced read only elements
 *     Systerel - fixed Hyperlink.setImage() calls
 *******************************************************************************/
package org.eventb.internal.ui.eventbeditor.editpage;

import static org.eventb.internal.ui.EventBUtils.setHyperlinkImage;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.forms.events.HyperlinkAdapter;
import org.eclipse.ui.forms.events.HyperlinkEvent;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.ImageHyperlink;
import org.eventb.internal.ui.EventBImage;
import org.eventb.internal.ui.EventBSharedColor;
import org.eventb.internal.ui.eventbeditor.EventBEditorUtils;
import org.eventb.ui.IEventBSharedImages;
import org.rodinp.core.IInternalElement;
import org.rodinp.core.IInternalElementType;

public abstract class AbstractHyperlinkComposite {

	Composite composite;

	EditPage page;

	IInternalElement parent;

	boolean initialised;
	
	IInternalElementType<?> type;

	
	public AbstractHyperlinkComposite(EditPage page, IInternalElement parent,
			IInternalElementType<?> type,
			FormToolkit toolkit, Composite compParent) {
		this.page = page;
		this.parent = parent;
		this.type = type;
		initialised = false;
		createComposite(toolkit, compParent);
	}

	private void createComposite(FormToolkit toolkit, Composite compParent) {
		composite = toolkit.createComposite(compParent);
		GridData gridData = new GridData(GridData.FILL_HORIZONTAL);
		composite.setLayoutData(gridData);
		GridLayout gridLayout = new GridLayout();
		gridLayout.numColumns = 2;
		gridLayout.marginWidth = 0;
		gridLayout.marginHeight = 0;
		composite.setLayout(gridLayout);
		if (EventBEditorUtils.DEBUG) {
			composite.setBackground(EventBSharedColor
					.getSystemColor(SWT.COLOR_DARK_GRAY));
		}
	}

	abstract void createContent(FormToolkit toolkit, int level);
	
	public boolean isInitialised() {
		return initialised;
	}
	
	protected void setInitialised(boolean init) {
		initialised=init;
	}
	
	public void setHeightHint(int heightHint) {
		GridData gridData = (GridData) composite.getLayoutData();
		gridData.heightHint = heightHint;
	}

	protected boolean checkAndShowReadOnly() {
		return EventBEditorUtils.checkAndShowReadOnly(parent);
	}

}

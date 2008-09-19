/*******************************************************************************
 * Copyright (c) 2007, 2008 ETH Zurich and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     ETH Zurich - initial API and implementation
 *     Systerel - used EventBSharedColor
 *******************************************************************************/
package org.eventb.internal.ui.eventbeditor.editpage;

import org.eclipse.core.resources.IMarker;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CCombo;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Control;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eventb.internal.ui.EventBSharedColor;
import org.eventb.internal.ui.EventBUIExceptionHandler;
import org.eventb.internal.ui.UIUtils;
import org.eventb.internal.ui.EventBUIExceptionHandler.UserAwareness;
import org.eventb.internal.ui.markers.MarkerUIRegistry;
import org.eventb.ui.eventbeditor.IEventBEditor;
import org.rodinp.core.RodinDBException;

public class CComboEditComposite extends AbstractEditComposite {

	protected final String UNDEFINED = "--undef--";
	
	protected CCombo combo;
	protected Button undefinedButton;

	public CComboEditComposite(IAttributeUISpec uiSpec) {
		super(uiSpec);
	}
	
	@Override
	public void initialise(boolean refreshMarkers) {
		try {
			String value = uiSpec.getAttributeFactory().getValue(element,
					new NullProgressMonitor());
			createCombo();
			combo.setText(value);
			if (refreshMarkers) 
				displayMarkers();
		} catch (RodinDBException e) {
			setUndefinedValue();
		}
	}

	private void displayMarkers() {
		Color WHITE = EventBSharedColor.getSystemColor(SWT.COLOR_WHITE);
		Color BLACK = EventBSharedColor.getSystemColor(SWT.COLOR_BLACK);
		Color RED = EventBSharedColor.getSystemColor(SWT.COLOR_RED);
		Color YELLOW = EventBSharedColor.getSystemColor(SWT.COLOR_YELLOW);
		try {
			int maxSeverity = MarkerUIRegistry.getDefault()
				.getMaxMarkerSeverity(element, uiSpec.getAttributeType());
			if (maxSeverity == IMarker.SEVERITY_ERROR) {
				combo.setBackground(RED);
				combo.setForeground(YELLOW);
				return;
			}
			else if (maxSeverity == IMarker.SEVERITY_WARNING) {
				combo.setBackground(YELLOW);
				combo.setForeground(RED);
				return;
			}
		} catch (CoreException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		combo.setBackground(WHITE);
		combo.setForeground(BLACK);
	}
	@Override
	public void setSelected(boolean selection) {
		Control control = combo == null ? undefinedButton : combo;
		if (selection)
			control.setBackground(EventBSharedColor.getSystemColor(
					SWT.COLOR_GRAY));
		else {
			control.setBackground(EventBSharedColor.getSystemColor(
					SWT.COLOR_WHITE));
		}
		super.setSelected(selection);
	}

	/**
	 * Set undefined value for the element.
	 */
	private void setUndefinedValue() {
		if (combo == null)
			createCombo();
		
		combo.setText(UNDEFINED);
	}

	private void createCombo() {
		if (combo != null)
			combo.removeAll();
		else {
			combo = new CCombo(composite, SWT.BORDER | SWT.FLAT | SWT.READ_ONLY);
			combo.addSelectionListener(new SelectionListener() {

				public void widgetDefaultSelected(SelectionEvent e) {
					widgetSelected(e);
				}

				public void widgetSelected(SelectionEvent e) {
					if (combo.getText().equals(UNDEFINED)) {
							UIUtils.setStringAttribute(element, uiSpec
								.getAttributeFactory(), null,
								new NullProgressMonitor());
					} else {
						UIUtils.setStringAttribute(element, uiSpec
								.getAttributeFactory(), combo.getText(),
								new NullProgressMonitor());
					}
				}
			});
			this.getFormToolkit().paintBordersFor(composite);
		}
		
		combo.add(UNDEFINED);
		try {
			String[] values;
			values = uiSpec.getAttributeFactory().getPossibleValues(element,
					new NullProgressMonitor());
			for (String value : values) {
				combo.add(value);
			}
		} catch (RodinDBException e) {
			EventBUIExceptionHandler.handleRodinException(e,
					UserAwareness.IGNORE);
		}
	}

	public void setDefaultValue(IEventBEditor<?> editor) {
		try {
			uiSpec.getAttributeFactory().setDefaultValue(editor, element,
					new NullProgressMonitor());
			if (combo != null)
				combo.setFocus();
		} catch (RodinDBException e) {
			EventBUIExceptionHandler.handleSetAttributeException(e);
		}
	}

	public void edit(int charStart, int charEnd) {
		combo.setFocus();
		if (charStart != -1 && charEnd != -1)
			combo.setSelection(new Point(charStart, charEnd));
		else {
			String text = combo.getText();
			combo.setSelection(new Point(0, text.length()));
		}
			
		FormToolkit.ensureVisible(combo);
	}

}

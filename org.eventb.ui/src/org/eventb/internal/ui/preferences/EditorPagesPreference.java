/*******************************************************************************
 * Copyright (c) 2008 ETH Zurich.
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Rodin @ ETH Zurich
 ******************************************************************************/

package org.eventb.internal.ui.preferences;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.util.IPropertyChangeListener;
import org.eclipse.jface.util.PropertyChangeEvent;
import org.eventb.internal.ui.UIUtils;
import org.eventb.internal.ui.eventbeditor.EditorPagesRegistry;
import org.eventb.internal.ui.eventbeditor.IEditorPagesRegistry;
import org.eventb.ui.EventBUIPlugin;
import org.eventb.ui.eventbeditor.EventBEditorPage;

/**
 * @author htson
 *         <p>
 *         An abstract implementation for Editor Pages Preference
 *         {@link IEditorPagesPreference}.
 */
public abstract class EditorPagesPreference implements IEditorPagesPreference,
		IPropertyChangeListener {

	/**
	 * The preference store for this preference page. 
	 */
	private IPreferenceStore pStore = EventBUIPlugin.getDefault()
			.getPreferenceStore();

	/**
	 * The editor pages registry.
	 */
	private IEditorPagesRegistry registry = EditorPagesRegistry.getDefault();

	/**
	 * The list of valid page IDs.
	 */
	private List<String> validPageIDs = null;

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eventb.internal.ui.preferences.IEditorPagesPreference#createPages()
	 */
	public synchronized EventBEditorPage[] createPages() {
		if (validPageIDs == null)
			validPageIDs = getSelectedPageIDs();
		assert validPageIDs != null;

		List<EventBEditorPage> list = new ArrayList<EventBEditorPage>(
				validPageIDs.size());
		String editorID = getEditorID();
		for (String pageID : validPageIDs) {
			list.add(registry.createPage(editorID, pageID));
		}
		return list.toArray(new EventBEditorPage[list.size()]);
	}
 
	/**
	 * Utility method to get the selected page IDs.
	 * 
	 * @return the selected page IDs.
	 */
	private List<String> getSelectedPageIDs() {
		String editorID = getEditorID();
		String pName = getPreferenceName();
		if (pStore.contains(pName)) {
			String s = pStore.getString(pName);
			String[] pageIDs = UIUtils.parseString(s);
			List<String> list = new ArrayList<String>(pageIDs.length);
			for (String pageID : pageIDs) {
				if (registry.isValid(editorID, pageID)) {
					list.add(pageID);
				}
			}
			if (list.size() != 0)
				return list;
			else {
				return registry.getAllPageIDs(editorID);
			}
		} else {
			return registry.getAllPageIDs(editorID);
		}
	}

	protected abstract String getPreferenceName();

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.jface.util.IPropertyChangeListener#propertyChange(org.eclipse.jface.util.PropertyChangeEvent)
	 */
	public void propertyChange(PropertyChangeEvent event) {
		String property = event.getProperty();
		if (property.equals(getPreferenceName())) {
			validPageIDs = null;
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eventb.internal.ui.preferences.IEditorPagesPreference#setDefault()
	 */
	public void setDefault() {
		IPreferenceStore store = EventBUIPlugin.getDefault()
				.getPreferenceStore();
		List<String> defaultMachineEditorPages = EditorPagesRegistry
				.getDefault().getDefaultPageIDs(getEditorID());
		ArrayList<Object> machinePages = new ArrayList<Object>(
				defaultMachineEditorPages.size());
		for (String page : defaultMachineEditorPages) {
			machinePages.add(page);
		}
		store.setDefault(getPreferenceName(), UIUtils
				.toCommaSeparatedList(machinePages));
	}
		
}

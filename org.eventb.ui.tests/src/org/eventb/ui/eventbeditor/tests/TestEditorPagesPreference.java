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

package org.eventb.ui.eventbeditor.tests;

import java.util.List;

import junit.framework.TestCase;

import org.eclipse.jface.preference.IPreferenceStore;
import org.eventb.internal.ui.UIUtils;
import org.eventb.internal.ui.eventbeditor.EditorPagesRegistry;
import org.eventb.internal.ui.eventbeditor.EventBContextEditor;
import org.eventb.internal.ui.eventbeditor.EventBMachineEditor;
import org.eventb.internal.ui.eventbeditor.IEditorPagesRegistry;
import org.eventb.internal.ui.preferences.ContextEditorPagesPreference;
import org.eventb.internal.ui.preferences.EditorPagesPreference;
import org.eventb.internal.ui.preferences.IEditorPagesPreference;
import org.eventb.internal.ui.preferences.MachineEditorPagesPreference;
import org.eventb.internal.ui.preferences.PreferenceConstants;
import org.eventb.ui.EventBUIPlugin;
import org.eventb.ui.eventbeditor.EventBEditorPage;
import org.eventb.ui.tests.EventBUITestsPlugin;
import org.junit.Test;

/**
 * @author htson
 *         <p>
 *         This is the sets of JUnit tests for the editor pages preferences
 *         {@link EditorPagesPreference}
 */
public class TestEditorPagesPreference extends TestCase {

	// The registry under test.
	private IEditorPagesRegistry registry;

	// The test registry.
	private final static String EDITOR_PAGE_REGISTRY_TEST_ID = EventBUITestsPlugin.PLUGIN_ID
			+ ".editorPages";

	// Some pre-defined IDs and names.
	private String htmlPageID = "org.eventb.ui.htmlpage";

	private String dependenciesPageID = "org.eventb.ui.dependencypage";

	private String editPageID = "org.eventb.ui.edit";
	
	// The preference store: The test will modify the preference value through
	// this store.
	private IPreferenceStore pStore; 

	// The preference for machine editor.
	private IEditorPagesPreference machinePreference;

	// The preference for context editor.	
	private IEditorPagesPreference contextPreference;
	
	@Override
	protected void setUp() throws Exception {
		super.setUp();
		registry = EditorPagesRegistry.getDefault();
		((EditorPagesRegistry) registry)
				.setEditorPageRegistryID(EDITOR_PAGE_REGISTRY_TEST_ID);
		// Try to reset the default values for the two preferences.
		machinePreference = MachineEditorPagesPreference.getDefault();
		machinePreference.setDefault();
		contextPreference = ContextEditorPagesPreference.getDefault();
		contextPreference.setDefault();
		pStore = EventBUIPlugin.getDefault().getPreferenceStore();
	}

	/**
	 * Tests for getting the editor ID corresponding to the preferences.
	 */
	@Test
	public void testGetEditorID() {
		IEditorPagesPreference pref = MachineEditorPagesPreference.getDefault();
		String editorID = pref.getEditorID();
		assertEquals("Incorrect editor ID for Machine Editor Pages Preference",
				EventBMachineEditor.EDITOR_ID, editorID);
		
		pref = ContextEditorPagesPreference.getDefault();
		editorID = pref.getEditorID();
		assertEquals("Incorrect editor ID for Context Editor Pages Preference",
				EventBContextEditor.EDITOR_ID, editorID);
	}

	/**
	 * Tests for getting the pages from the preferences.
	 */
	@Test
	public void testGetPages() {
		// The pages should be the default pages now
		EventBEditorPage[] pages = machinePreference.createPages();
		List<String> defaultMachinePages = registry
			.getDefaultPageIDs(EventBMachineEditor.EDITOR_ID);
		int i = 0;
		for (String pageID : defaultMachinePages) {
			assertEquals("Incorrect page ID for page " + i
					+ " for machine editor ", pageID, pages[i++].getId()); 
		}

		// The pages should be the default pages now
		pages = contextPreference.createPages();
		List<String> defaultContextPages = registry
			.getDefaultPageIDs(EventBContextEditor.EDITOR_ID);
		i = 0;
		for (String pageID : defaultContextPages) {
			assertEquals("Incorrect page ID for page " + i
					+ " for context editor ", pageID, pages[i++].getId()); 
		}

		
		String [] pageIDs = new String [] {editPageID, htmlPageID};
		pStore.setValue(PreferenceConstants.P_MACHINE_EDITOR_PAGE, UIUtils
				.toCommaSeparatedList(pageIDs));

		pages = machinePreference.createPages();
		assertEquals("Incorrect number of pages for machine editor ",
				pageIDs.length, pages.length);
		for (i = 0; i < pages.length; i++) {
			assertEquals("Incorrect page ID for page " + i
					+ " for machine editor ", pageIDs[i], pages[i].getId());
		}


		pageIDs = new String [] {dependenciesPageID};
		pStore.setValue(PreferenceConstants.P_CONTEXT_EDITOR_PAGE, UIUtils
				.toCommaSeparatedList(pageIDs));

		pages = contextPreference.createPages();
		assertEquals("Incorrect number of pages for context editor ",
				pageIDs.length, pages.length);
		for (i = 0; i < pages.length; i++) {
			assertEquals("Incorrect page ID for page " + i
					+ " for context editor ", pageIDs[i], pages[i].getId());
		}

		
		pStore.setToDefault(PreferenceConstants.P_MACHINE_EDITOR_PAGE);
		// The pages should be the default pages now
		pages = machinePreference.createPages();
		i = 0;
		for (String pageID : defaultMachinePages) {
			assertEquals("Incorrect page ID for page " + i
					+ " for machine editor ", pageID, pages[i++].getId()); 
		}

		
		pStore.setToDefault(PreferenceConstants.P_CONTEXT_EDITOR_PAGE);
		// The pages should be the default pages now
		pages = contextPreference.createPages();
		i = 0;
		for (String pageID : defaultContextPages) {
			assertEquals("Incorrect page ID for page " + i
					+ " for context editor ", pageID, pages[i++].getId()); 
		}
	}

}

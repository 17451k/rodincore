/*******************************************************************************
 * Copyright (c) 2005, 2008 ETH Zurich and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     ETH Zurich - initial API and implementation
 *     Systerel - refactored getRodinDatabase()
 *     Systerel - added ColorManager
 *     Systerel - used EventBPreferenceStore
 *******************************************************************************/
package org.eventb.ui;

import org.eclipse.core.runtime.Assert;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Platform;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.resource.ImageRegistry;
import org.eclipse.swt.graphics.FontData;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.eventb.core.EventBPlugin;
import org.eventb.core.ast.Formula;
import org.eventb.core.ast.FormulaFactory;
import org.eventb.core.seqprover.autoTacticPreference.IAutoTacticPreference;
import org.eventb.internal.ui.BundledFileExtractor;
import org.eventb.internal.ui.ColorManager;
import org.eventb.internal.ui.EventBImage;
import org.eventb.internal.ui.UIUtils;
import org.eventb.internal.ui.cachehypothesis.CacheHypothesisUtils;
import org.eventb.internal.ui.eventbeditor.EventBEditorUtils;
import org.eventb.internal.ui.goal.GoalUtils;
import org.eventb.internal.ui.preferences.EventBPreferenceStore;
import org.eventb.internal.ui.preferences.PreferenceConstants;
import org.eventb.internal.ui.proofcontrol.ProofControlUtils;
import org.eventb.internal.ui.proofinformation.ProofInformationUtils;
import org.eventb.internal.ui.prooftreeui.ProofTreeUIUtils;
import org.eventb.internal.ui.prover.ProverUIUtils;
import org.eventb.internal.ui.searchhypothesis.SearchHypothesisUtils;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.rodinp.core.IRodinDB;
import org.rodinp.core.RodinCore;

/**
 * @author htson
 *         <p>
 *         The main plugin class for Event-B UI.
 */
public class EventBUIPlugin extends AbstractUIPlugin {

	/**
	 * The identifier of the Event-B UI plug-in (value
	 * <code>"org.eventb.ui"</code>).
	 */
	public static final String PLUGIN_ID = "org.eventb.ui"; //$NON-NLS-1$

	/**
	 * The identifier of the Proof Tree UI View (value
	 * <code>"org.eventb.ui.views.ProofTreeUI"</code>).
	 */
	public static final String PROOF_TREE_VIEW_ID = PLUGIN_ID
			+ ".views.ProofTreeUI";

	/**
	 * The identifier of the Proof Control View (value
	 * <code>"org.eventb.ui.views.ProofControl"</code>).
	 */
	public static final String PROOF_CONTROL_VIEW_ID = PLUGIN_ID + ".views.ProofControl";
	
	/**
	 * The identifier of the Rodin Problem View (value
	 * <code>"org.eventb.ui.views.RodinProblemView"</code>).
	 */
	public static final String RODIN_PROBLEM_VIEW_ID = PLUGIN_ID
			+ ".views.RodinProblemView"; 		
	
	public static final String PROVING_PERSPECTIVE_ID = "org.eventb.ui.perspective.eventb";

	public static final String NAVIGATOR_VIEW_ID = "fr.systerel.explorer.navigator.view";

	// Trace Options
	private static final String GLOBAL_TRACE = PLUGIN_ID + "/debug"; //$NON-NLS-1$

	private static final String EVENTBEDITOR_TRACE = PLUGIN_ID
			+ "/debug/eventbeditor"; //$NON-NLS-1$

	private static final String PROVERUI_TRACE = PLUGIN_ID + "/debug/proverui"; //$NON-NLS-1$

	private static final String PROOFCONTROL_TRACE = PLUGIN_ID
			+ "/debug/proofcontrol"; //$NON-NLS-1$

	private static final String PROOFTREEUI_TRACE = PLUGIN_ID
			+ "/debug/prooftreeui"; //$NON-NLS-1$

	private static final String PROOFINFORMATION_TRACE = PLUGIN_ID
			+ "/debug/proofinformation"; //$NON-NLS-1$

	private static final String SEARCHHYPOTHESIS_TRACE = PLUGIN_ID
			+ "/debug/searchhypothesis"; //$NON-NLS-1$

	private static final String CACHEDHYPOTHESIS_TRACE = PLUGIN_ID
			+ "/debug/cachedhypothesis"; //$NON-NLS-1$

	private static final String GOAL_TRACE = PLUGIN_ID
			+ "/debug/goal"; //$NON-NLS-1$

	/**
	 * Default values for creating RODIN Elements
	 */
	public static final String PRD_DEFAULT = FormulaFactory.getDefault()
			.makeLiteralPredicate(Formula.BTRUE, null).toString();

	public static final String INV_DEFAULT = PRD_DEFAULT;

	public static final String AXM_DEFAULT = PRD_DEFAULT;

	public static final String THM_DEFAULT = PRD_DEFAULT;

	public static final String GRD_DEFAULT = PRD_DEFAULT;

	public static final String SUB_DEFAULT = "";

	// The shared instance.
	private static EventBUIPlugin plugin;

	/**
	 * The constructor, also store the database instance of the current
	 * Workspace.
	 */
	public EventBUIPlugin() {
		super();
		plugin = this;
	}

	/**
	 * Returns the Rodin database element.
	 * 
	 * @return the Rodin database
	 */
	public static IRodinDB getRodinDatabase() {
		return RodinCore.getRodinDB();
	}

	@Override
	public void start(BundleContext context) throws Exception {
		super.start(context);

		configureDebugOptions();
		
		initializePreferences();
		
		loadFont();
	}

	/**
	 * Utility method which try to load the necessary font if it is not
	 * currently available.
	 */
	private void loadFont() {
		Display display = this.getWorkbench().getDisplay();
		FontData[] fontList = display.getFontList("Brave Sans Mono", true);
		if (fontList.length == 0) {
			// The font is not available, try to load the font
			Bundle bundle = EventBUIPlugin.getDefault().getBundle();
			IPath path = new Path("fonts/bravesansmono_roman.ttf");
			IPath absolutePath = BundledFileExtractor.extractFile(bundle, path);
			Assert.isNotNull(absolutePath, "The Brave Sans Mono font should be included with the distribution");
			display.loadFont(absolutePath.toString());
		}
	}

	/**
	 * Reads the value of the preferences and initialize various components
	 */
	private void initializePreferences() {
		final IPreferenceStore store = getPreferenceStore();

		// Initialize the post-tactics
		String s = store.getString(PreferenceConstants.P_POSTTACTICS);
		String[] postTacticIDs = UIUtils.parseString(s);
		
		IAutoTacticPreference postTacticPreference = EventBPlugin
				.getPostTacticPreference();
		postTacticPreference
				.setSelectedDescriptors(ProverUIUtils
						.stringsToTacticDescriptors(postTacticPreference,
								postTacticIDs));
		boolean b = EventBPreferenceStore
				.getBooleanPreference(PreferenceConstants.P_POSTTACTIC_ENABLE);
		postTacticPreference.setEnabled(b);

		// Initialize the auto-tactics
		s = store.getString(PreferenceConstants.P_AUTOTACTICS);
		String[] autoTacticIDs = UIUtils.parseString(s);
		IAutoTacticPreference autoTacticPreference = EventBPlugin
				.getAutoTacticPreference();
		autoTacticPreference
				.setSelectedDescriptors(ProverUIUtils
						.stringsToTacticDescriptors(autoTacticPreference,
								autoTacticIDs));
		b = EventBPreferenceStore
				.getBooleanPreference(PreferenceConstants.P_AUTOTACTIC_ENABLE);
		autoTacticPreference.setEnabled(b);
	}

	/**
	 * Process debugging/tracing options coming from Eclipse.
	 */
	private void configureDebugOptions() {
		if (isDebugging()) {
			String option = Platform.getDebugOption(GLOBAL_TRACE);
			if (option != null)
				UIUtils.DEBUG = option.equalsIgnoreCase("true"); //$NON-NLS-1$

			option = Platform.getDebugOption(EVENTBEDITOR_TRACE);
			if (option != null)
				EventBEditorUtils.DEBUG = option.equalsIgnoreCase("true"); //$NON-NLS-1$

			option = Platform.getDebugOption(PROVERUI_TRACE);
			if (option != null)
				ProverUIUtils.DEBUG = option.equalsIgnoreCase("true"); //$NON-NLS-1$

			option = Platform.getDebugOption(PROOFCONTROL_TRACE);
			if (option != null)
				ProofControlUtils.DEBUG = option.equalsIgnoreCase("true"); //$NON-NLS-1$

			option = Platform.getDebugOption(PROOFTREEUI_TRACE);
			if (option != null)
				ProofTreeUIUtils.DEBUG = option.equalsIgnoreCase("true"); //$NON-NLS-1$

			option = Platform.getDebugOption(PROOFINFORMATION_TRACE);
			if (option != null)
				ProofInformationUtils.DEBUG = option.equalsIgnoreCase("true"); //$NON-NLS-1$

			option = Platform.getDebugOption(SEARCHHYPOTHESIS_TRACE);
			if (option != null)
				SearchHypothesisUtils.DEBUG = option.equalsIgnoreCase("true"); //$NON-NLS-1$

			option = Platform.getDebugOption(CACHEDHYPOTHESIS_TRACE);
			if (option != null)
				CacheHypothesisUtils.DEBUG = option.equalsIgnoreCase("true"); //$NON-NLS-1$

			option = Platform.getDebugOption(GOAL_TRACE);
			if (option != null)
				GoalUtils.DEBUG = option.equalsIgnoreCase("true"); //$NON-NLS-1$
		}
	}

	@Override
	public void stop(BundleContext context) throws Exception {
		super.stop(context);
		ColorManager.getDefault().dispose();
		plugin = null;
	}

	/**
	 * Returns the shared instance of this plug-in.
	 * 
	 * @returns the shared instance of this plug-in.
	 */
	public static EventBUIPlugin getDefault() {
		return plugin;
	}

	@Override
	protected void initializeImageRegistry(ImageRegistry reg) {
		EventBImage.initializeImageRegistry(reg);
		super.initializeImageRegistry(reg);
	}

	/**
	 * Get the active workbench page.
	 * <p>
	 * 
	 * @return current active workbench page
	 */
	public static IWorkbenchPage getActivePage() {
		return getDefault().internalGetActivePage();
	}

	/**
	 * Getting the current active page from the active workbench window.
	 * <p>
	 * 
	 * @return current active workbench page
	 */
	private IWorkbenchPage internalGetActivePage() {
		return getWorkbench().getActiveWorkbenchWindow().getActivePage();
	}

	/**
	 * Getting the workbench shell
	 * <p>
	 * 
	 * @return the shell associated with the active workbench window or null if
	 *         there is no active workbench window
	 */
	public static Shell getActiveWorkbenchShell() {
		IWorkbenchWindow window = getActiveWorkbenchWindow();
		if (window != null) {
			return window.getShell();
		}
		return null;
	}

	/**
	 * Return the active workbench window
	 * <p>
	 * 
	 * @return the active workbench window
	 */
	public static IWorkbenchWindow getActiveWorkbenchWindow() {
		return getDefault().getWorkbench().getActiveWorkbenchWindow();
	}
}

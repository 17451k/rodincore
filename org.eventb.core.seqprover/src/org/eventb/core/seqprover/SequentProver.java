/*******************************************************************************
 * Copyright (c) 2006, 2020 ETH Zurich and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     ETH Zurich - initial API and implementation
 *******************************************************************************/
package org.eventb.core.seqprover;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.MultiStatus;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.Plugin;
import org.eventb.core.seqprover.xprover.AbstractXProverReasoner;
import org.eventb.internal.core.seqprover.AutoTacticChecker;
import org.eventb.internal.core.seqprover.AutoTacticRegistry;
import org.eventb.internal.core.seqprover.ProverChecks;
import org.eventb.internal.core.seqprover.ProverSequent;
import org.eventb.internal.core.seqprover.ReasonerRegistry;
import org.eventb.internal.core.seqprover.eventbExtensions.mbGoal.MembershipGoal;
import org.eventb.internal.core.seqprover.eventbExtensions.rewriters.AutoRewriterImpl;
import org.osgi.framework.BundleContext;

/**
 * The main plugin class to be used in the desktop.
 * @since 1.0
 * @noextend This class is not intended to be subclassed by clients.
 * @noinstantiate This class is not intended to be instantiated by clients.
 */
public class SequentProver extends Plugin {

	public static final String PLUGIN_ID = "org.eventb.core.seqprover"; //$NON-NLS-1$

	/**
	 * debugging/tracing option names
	 */
	private static final String SEQPROVER_TRACE = PLUGIN_ID
			+ "/debug/seqProver"; //$NON-NLS-1$
	private static final String PROVER_SEQUENT_TRACE = PLUGIN_ID
			+ "/debug/proverSequent"; //$NON-NLS-1$
	private static final String PROVER_CHECKS_TRACE = PLUGIN_ID
			+ "/debug/proverChecks"; //$NON-NLS-1$
	private static final String REASONER_REGISTRY_TRACE = PLUGIN_ID
			+ "/debug/reasonerRegistry"; //$NON-NLS-1$	
	private static final String TACTIC_REGISTRY_TRACE = PLUGIN_ID
			+ "/debug/tacticRegistry"; //$NON-NLS-1$
	private static final String XPROVER_TRACE = PLUGIN_ID
			+ "/debug/xProver"; //$NON-NLS-1$
	private static final String AUTO_REWRITER_TRACE = PLUGIN_ID
			+ "/debug/autoRewriter"; //$NON-NLS-1$
	private static final String MEMBERSHIP_GOAL_TRACE = PLUGIN_ID
			+ "/debug/mbGoal"; //$NON-NLS-1$
	private static final String AUTO_TACTIC_CHECKER_TRACE = PLUGIN_ID
			+ "/debug/autoTacticChecker"; //$NON-NLS-1$
	
	/**
	 * The shared instance.
	 */
	private static SequentProver plugin;

	/**
	 * Debug flag for <code>SEQPROVER_TRACE</code>
	 */
	private static boolean DEBUG;

	/**
	 * This method is called upon plug-in activation
	 */
	@Override
	public void start(BundleContext context) throws Exception {
		super.start(context);
		plugin = this;
		enableAssertions();
		if (isDebugging())
			configureDebugOptions();
	}

	/**
	 * Enable Java assertion checks for this plug-in.
	 */
	private void enableAssertions() {
		getClass().getClassLoader().setDefaultAssertionStatus(true);
	}

	/**
	 * Process debugging/tracing options coming from Eclipse.
	 */
	private void configureDebugOptions() {
		SequentProver.DEBUG = parseOption(SEQPROVER_TRACE);
		ProverSequent.DEBUG = parseOption(PROVER_SEQUENT_TRACE);
		ProverChecks.DEBUG = parseOption(PROVER_CHECKS_TRACE);
		ReasonerRegistry.DEBUG = parseOption(REASONER_REGISTRY_TRACE);
		AutoTacticRegistry.DEBUG = parseOption(TACTIC_REGISTRY_TRACE);
		AbstractXProverReasoner.DEBUG = parseOption(XPROVER_TRACE);
		AutoRewriterImpl.DEBUG = parseOption(AUTO_REWRITER_TRACE);
		MembershipGoal.DEBUG = parseOption(MEMBERSHIP_GOAL_TRACE);
		AutoTacticChecker.DEBUG = parseOption(AUTO_TACTIC_CHECKER_TRACE);
	}

	private static boolean parseOption(String key) {
		final String option = Platform.getDebugOption(key);
		return "true".equalsIgnoreCase(option); //$NON-NLS-1$
	}

	/**
	 * This method is called when the plug-in is stopped
	 */
	@Override
	public void stop(BundleContext context) throws Exception {
		super.stop(context);
		plugin = null;
	}

	/**
	 * Returns the shared instance.
	 */
	public static SequentProver getDefault() {
		return plugin;
	}

	/**
	 * Returns the Reasoner registry managed by the sequent prover
	 * 
	 * @see IReasonerRegistry
	 * 
	 * @return the Reasoner registry managed by the sequent prover
	 */
	public static IReasonerRegistry getReasonerRegistry() {
		return ReasonerRegistry.getReasonerRegistry();
	}

	/**
	 * Returns the AutoTactic registry managed by the sequent prover
	 * 
	 * @see IAutoTacticRegistry
	 * 
	 * @return the AutoTactic registry managed by the sequent prover
	 */
	public static IAutoTacticRegistry getAutoTacticRegistry() {
		return AutoTacticRegistry.getTacticRegistry();
	}

	/**
	 * Checks that all externally provided auto tactics seem to work by running them
	 * on a trivial sequent. The result of the last run is persisted across platform
	 * restart, so that the check is not performed again for an external tactic that
	 * has already succeeded in the past.
	 * 
	 * The result of the checks is provided as a {@link MultiStatus}, with one
	 * entry for each external auto tactic.
	 * 
	 * @param force   ignore the cache and check all tactics again
	 * @param monitor a progress monitor, or {@code null} if progress reporting and
	 *                cancellation are not desired.
	 * 
	 * @return the results of the checks as a {@link MultiStatus}
	 * @since 3.3
	 */
	public static IStatus checkAutoTactics(boolean force, IProgressMonitor monitor) {
		return AutoTacticChecker.checkAutoTactics(force, monitor);
	}

	/**
	 * Prints the given message to the console in case the debug flag is
	 * switched on
	 * 
	 * @param message
	 *            The message to print out to the console
	 */
	public static void debugOut(String message) {
		if (DEBUG)
			System.out.println(message);
	}

}

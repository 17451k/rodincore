/*******************************************************************************
 * Copyright (c) 2006, 2008 ETH Zurich and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     ETH Zurich - initial API and implementation
 *     Systerel - refactored enableAutoProver
 *                added post-tactic manipulation
 *******************************************************************************/
package org.eventb.core.tests.pm;

import java.util.Arrays;
import java.util.List;

import junit.framework.TestCase;

import org.eclipse.core.resources.IMarker;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IProjectDescription;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.IWorkspaceDescription;
import org.eclipse.core.resources.IncrementalProjectBuilder;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eventb.core.EventBPlugin;
import org.eventb.core.IPOSequent;
import org.eventb.core.IPSFile;
import org.eventb.core.IPSStatus;
import org.eventb.core.ast.FormulaFactory;
import org.eventb.core.seqprover.IAutoTacticRegistry;
import org.eventb.core.seqprover.SequentProver;
import org.eventb.core.seqprover.IAutoTacticRegistry.ITacticDescriptor;
import org.eventb.core.seqprover.autoTacticPreference.IAutoTacticPreference;
import org.rodinp.core.IRodinFile;
import org.rodinp.core.IRodinProject;
import org.rodinp.core.RodinCore;
import org.rodinp.core.RodinDBException;
import org.rodinp.core.RodinMarkerUtil;

/**
 * Abstract class for builder tests.
 * 
 * @author Laurent Voisin
 */
public abstract class BasicTest extends TestCase {
	
	protected static FormulaFactory factory = FormulaFactory.getDefault();

	protected IWorkspace workspace = ResourcesPlugin.getWorkspace();
	
	public BasicTest() {
		super();
	}

	public BasicTest(String name) {
		super(name);
	}

	protected IRodinProject rodinProject;

	protected void runBuilder() throws CoreException {
		final IProject project = rodinProject.getProject();
		project.build(IncrementalProjectBuilder.INCREMENTAL_BUILD, null);
		IMarker[] buildPbs= project.findMarkers(
				RodinMarkerUtil.BUILDPATH_PROBLEM_MARKER,
				true,
				IResource.DEPTH_INFINITE
		);
		if (buildPbs.length != 0) {
			for (IMarker marker: buildPbs) {
				System.out.println("Build problem for " + marker.getResource());
				System.out.println("  " + marker.getAttribute(IMarker.MESSAGE));
			}
			fail("Build produced build problems, see console");
		}
		checkPSFiles();
	}

	private void checkPSFiles() throws RodinDBException {
		IRodinFile[] files = rodinProject.getRodinFiles(); 
		for (IRodinFile file: files) {
			if (file instanceof IPSFile) {
				checkPSFile((IPSFile) file);
			}
		}
	}

	private void checkPSFile(IPSFile file) throws RodinDBException {
		for (IPSStatus psStatus: file.getStatuses()) {
			final IPOSequent poSequent = psStatus.getPOSequent();
			assertEquals("PS file not in sync with PO file",
					poSequent.getPOStamp(), psStatus.getPOStamp());
		}
	}
	
	protected static void enableAutoProver() {
		enableAutoProver(false);
	}

	private static final String[] autoTacticIds = new String[] {
		"org.eventb.core.seqprover.trueGoalTac",
		"org.eventb.core.seqprover.trueGoalTac",
		"org.eventb.core.seqprover.falseHypTac",
		"org.eventb.core.seqprover.goalInHypTac",
		"org.eventb.core.seqprover.funGoalTac",
		"org.eventb.core.seqprover.autoRewriteTac",
		"org.eventb.core.seqprover.typeRewriteTac",
		"org.eventb.core.seqprover.findContrHypsTac",
		"org.eventb.core.seqprover.eqHypTac",
		"org.eventb.core.seqprover.shrinkImpHypTac",
		"org.eventb.core.seqprover.clarifyGoalTac",
	};
	
	protected static void enableAutoProver(boolean limited) {
		final IAutoTacticPreference p = EventBPlugin.getAutoTacticPreference();
		final List<ITacticDescriptor> ds;
		if (limited) {
			ds = Arrays.asList(getTacticDescriptors(autoTacticIds));
		} else {
			ds = p.getDefaultDescriptors();
		}
		enablePreference(p, ds);
	}

	protected static void enableAutoProver(String... ids) {
		final IAutoTacticPreference p = EventBPlugin.getAutoTacticPreference();
		final ITacticDescriptor[] ds = getTacticDescriptors(ids);
		enablePreference(p, Arrays.asList(ds));
	}

	protected static void enablePostTactic(String... ids) {
		final IAutoTacticPreference p = EventBPlugin.getPostTacticPreference();
		final ITacticDescriptor[] ds = getTacticDescriptors(ids);
		enablePreference(p, Arrays.asList(ds));
	}

	protected static void disablePostTactic() {
		final IAutoTacticPreference p = EventBPlugin.getPostTacticPreference();
		p.setEnabled(false);
	}

	private static ITacticDescriptor[] getTacticDescriptors(String... ids) {
		final IAutoTacticRegistry reg = SequentProver.getAutoTacticRegistry();
		final ITacticDescriptor[] ds = new ITacticDescriptor[ids.length];
		for (int i = 0; i < ids.length; i++) {
			ds[i] = reg.getTacticDescriptor(ids[i]);
		}
		return ds;
	}

	private static void enablePreference(IAutoTacticPreference pref,
			List<ITacticDescriptor> ds) {
		pref.setSelectedDescriptors(ds);
		pref.setEnabled(true);
	}

	protected static void disableAutoProver() {
		EventBPlugin.getAutoTacticPreference().setEnabled(false);
	}

	@Override
	protected void setUp() throws Exception {
		super.setUp();
		
		// ensure autobuilding is turned off
		IWorkspaceDescription wsDescription = workspace.getDescription();
		if (wsDescription.isAutoBuilding()) {
			wsDescription.setAutoBuilding(false);
			workspace.setDescription(wsDescription);
		}
		
		// Create a new project
		IProject project = workspace.getRoot().getProject("P");
		project.create(null);
		project.open(null);
		IProjectDescription pDescription = project.getDescription();
		pDescription.setNatureIds(new String[] {RodinCore.NATURE_ID});
		project.setDescription(pDescription, null);
		rodinProject = RodinCore.valueOf(project);
		
		disableAutoProver();
	}
	
	@Override
	protected void tearDown() throws Exception {
		rodinProject.getProject().delete(true, true, null);
		super.tearDown();
	}


}

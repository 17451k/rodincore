/*******************************************************************************
 * Copyright (c) 2006, 2008 ETH Zurich and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     ETH Zurich - initial API and implementation
 *     Systerel - separation of file and root element
 *******************************************************************************/
package org.eventb.core.tests;

import java.util.ArrayList;
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
import org.eventb.core.IContextRoot;
import org.eventb.core.IMachineRoot;
import org.eventb.core.IPORoot;
import org.eventb.core.ISCContextRoot;
import org.eventb.core.ISCMachineRoot;
import org.eventb.core.ast.FormulaFactory;
import org.eventb.core.seqprover.IAutoTacticRegistry;
import org.eventb.core.seqprover.SequentProver;
import org.eventb.core.seqprover.IAutoTacticRegistry.ITacticDescriptor;
import org.eventb.core.seqprover.autoTacticPreference.IAutoTacticPreference;
import org.rodinp.core.IRodinDB;
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
public abstract class BuilderTest extends TestCase {
	
	protected static FormulaFactory factory = FormulaFactory.getDefault();

	protected IRodinProject rodinProject;

	protected IWorkspace workspace = ResourcesPlugin.getWorkspace();

	public BuilderTest() {
		super();
	}

	public BuilderTest(String name) {
		super(name);
	}
	
	protected IContextRoot createContext(String bareName) throws RodinDBException {
		final String fileName = EventBPlugin.getContextFileName(bareName);
		IRodinFile result = rodinProject.getRodinFile(fileName);
		result.create(true, null);
		return (IContextRoot) result.getRoot();
	}

	protected IMachineRoot createMachine(String bareName) throws RodinDBException {
		final String fileName = EventBPlugin.getMachineFileName(bareName);
		IRodinFile result = rodinProject.getRodinFile(fileName);
		result.create(true, null);
		return (IMachineRoot) result.getRoot();
	}

	protected IPORoot createPOFile(String bareName) throws RodinDBException {
		final String fileName = EventBPlugin.getPOFileName(bareName);
		IRodinFile result = rodinProject.getRodinFile(fileName);
		result.create(true, null);
		return (IPORoot) result;
	}

	protected ISCContextRoot createSCContext(String bareName) throws RodinDBException {
		final String fileName = EventBPlugin.getSCContextFileName(bareName);
		IRodinFile result = rodinProject.getRodinFile(fileName);
		result.create(true, null);
		return (ISCContextRoot) result.getRoot();
	}

	protected ISCMachineRoot createSCMachine(String bareName) throws RodinDBException {
		final String fileName = EventBPlugin.getSCMachineFileName(bareName);
		IRodinFile result = rodinProject.getRodinFile(fileName);
		result.create(true, null);
		return (ISCMachineRoot) result;
	}
	
	protected void runBuilder() throws CoreException {
		runBuilder(rodinProject);
	}

	protected void runBuilder(IRodinProject rp) throws CoreException {
		final IProject project = rp.getProject();
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
	
	protected static void enableAutoProver() {
		final IAutoTacticPreference autoPref = EventBPlugin
				.getAutoTacticPreference();
		final List<ITacticDescriptor> descrs = new ArrayList<ITacticDescriptor>(
				autoTacticIds.length);
		final IAutoTacticRegistry reg = SequentProver.getAutoTacticRegistry();
		for (String id : autoTacticIds) {
			descrs.add(reg.getTacticDescriptor(id));
		}
		autoPref.setSelectedDescriptors(descrs);
		autoPref.setEnabled(true);
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
		
		rodinProject = createRodinProject("P");
		
		disableAutoProver();
	}

	protected IRodinProject createRodinProject(String projectName)
			throws CoreException {
		IProject project = workspace.getRoot().getProject(projectName);
		project.create(null);
		project.open(null);
		IProjectDescription pDescription = project.getDescription();
		pDescription.setNatureIds(new String[] {RodinCore.NATURE_ID});
		project.setDescription(pDescription, null);
		IRodinProject result = RodinCore.valueOf(project);
		return result;
	}
	
	@Override
	protected void tearDown() throws Exception {
		// Delete all Rodin projects
		final IRodinDB rodinDB = RodinCore.getRodinDB();
		for (IRodinProject rp: rodinDB.getRodinProjects()) {
			rp.getProject().delete(true, true, null);
		}
		
		super.tearDown();
	}

}

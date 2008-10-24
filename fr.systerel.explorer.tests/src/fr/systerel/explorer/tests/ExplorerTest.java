/*******************************************************************************
 * Copyright (c) 2008 Systerel and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License  v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Systerel - initial API and implementation
  *******************************************************************************/

package fr.systerel.explorer.tests;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertFalse;
import static junit.framework.Assert.assertNotNull;

import java.util.Arrays;
import java.util.Comparator;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IProjectDescription;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.IWorkspaceRunnable;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eventb.core.EventBPlugin;
import org.eventb.core.IAction;
import org.eventb.core.IAxiom;
import org.eventb.core.ICarrierSet;
import org.eventb.core.IConstant;
import org.eventb.core.IContextRoot;
import org.eventb.core.IEvent;
import org.eventb.core.IExtendsContext;
import org.eventb.core.IGuard;
import org.eventb.core.IInvariant;
import org.eventb.core.IMachineRoot;
import org.eventb.core.IPORoot;
import org.eventb.core.IPOSequent;
import org.eventb.core.IPOSource;
import org.eventb.core.IPSRoot;
import org.eventb.core.IPSStatus;
import org.eventb.core.IRefinesMachine;
import org.eventb.core.ISeesContext;
import org.eventb.core.ITheorem;
import org.eventb.core.IVariable;
import org.eventb.core.IWitness;
import org.junit.After;
import org.junit.Before;
import org.rodinp.core.IInternalElement;
import org.rodinp.core.IInternalElementType;
import org.rodinp.core.IInternalParent;
import org.rodinp.core.IRodinElement;
import org.rodinp.core.IRodinFile;
import org.rodinp.core.IRodinProject;
import org.rodinp.core.RodinCore;
import org.rodinp.core.RodinDBException;

import fr.systerel.explorer.model.IModelElement;
import fr.systerel.explorer.model.ModelController;
import fr.systerel.explorer.model.ModelProofObligation;

/**
 *
 *
 */
public class ExplorerTest {


	/**
	 * The pointer to the test Rodin project.
	 */
	protected IRodinProject rodinProject;

	/**
	 * The testing workspace. 
	 */
	protected static IWorkspace workspace = ResourcesPlugin.getWorkspace();
	
	protected static Comparator<Object> comparator = new Comparator<Object> () {

		@Override
		public int compare(Object arg0, Object arg1) {
			return arg0.toString().compareTo(arg1.toString());
		}
		
	};

	/**
	 * Utility method to create a context with the given bare name. The context
	 * is created as a child of the test Rodin project ({@link #rodinProject}).
	 * 
	 * @param bareName
	 *            the bare name (without the extension .ctx) of the context
	 * @return the newly created context.
	 * @throws RodinDBException
	 *             if some problems occur.
	 */
	protected IContextRoot createContext(String bareName) throws RodinDBException {
		final String fileName = EventBPlugin.getContextFileName(bareName);
		IRodinFile result = rodinProject.getRodinFile(fileName);
		result.create(true, null);
		return (IContextRoot) result.getRoot();
	}

	protected IContextRoot createContext(String bareName, IRodinProject parent) throws RodinDBException {
		final String fileName = EventBPlugin.getContextFileName(bareName);
		IRodinFile result = parent.getRodinFile(fileName);
		result.create(true, null);
		return (IContextRoot) result.getRoot();
	}
	
	/**
	 * Utility method to create a machine with the given bare name. The machine
	 * is created as a child of the test Rodin project ({@link #rodinProject}).
	 * 
	 * @param bareName
	 *            the bare name (without the extension .mch) of the machine
	 * @return the newly created machine.
	 * @throws RodinDBException
	 *             if some problems occur.
	 */
	protected IMachineRoot createMachine(String bareName) throws RodinDBException {
		final String fileName = EventBPlugin.getMachineFileName(bareName);
		IRodinFile result = rodinProject.getRodinFile(fileName);
		result.create(true, null);
		return (IMachineRoot) result.getRoot();
	}

	protected IMachineRoot createMachine(String bareName, IRodinProject parent) throws RodinDBException {
		final String fileName = EventBPlugin.getMachineFileName(bareName);
		IRodinFile result = parent.getRodinFile(fileName);
		result.create(true, null);
		return (IMachineRoot) result.getRoot();
	}
	
	protected IPORoot createIPORoot(String bareName) throws RodinDBException {
		final String fileName = EventBPlugin.getPOFileName(bareName);
		IRodinFile result = rodinProject.getRodinFile(fileName);
		result.create(true, null);
		return (IPORoot) result.getRoot();
	}

	protected IPORoot createIPORoot(String bareName, IRodinProject parent) throws RodinDBException {
		final String fileName = EventBPlugin.getPOFileName(bareName);
		IRodinFile result = parent.getRodinFile(fileName);
		result.create(true, null);
		return (IPORoot) result.getRoot();
	}
	
	protected IPSRoot createIPSRoot(String bareName) throws RodinDBException {
		final String fileName = EventBPlugin.getPSFileName(bareName);
		IRodinFile result = rodinProject.getRodinFile(fileName);
		result.create(true, null);
		return (IPSRoot) result.getRoot();
	}
	
	protected IPSRoot createIPSRoot(String bareName, IRodinProject parent) throws RodinDBException {
		final String fileName = EventBPlugin.getPSFileName(bareName);
		IRodinFile result = parent.getRodinFile(fileName);
		result.create(true, null);
		return (IPSRoot) result.getRoot();
	}
	
	/**
	 * Method to create an internal element
	 * 
	 * @param <T>		the type of internal element to create
	 * @param parent	the parent of the element to create
	 * @param childType	the type of the child to create
	 * @param childName	the name of the element to create
	 * @return			the created element
	 * @throws RodinDBException
	 */
	protected <T extends IInternalElement> T createInternalElement(
			IInternalParent parent, IInternalElementType<T> childType,
			String childName) throws RodinDBException {

		T element = parent.getInternalElement(childType, childName);
		element.create(null, null);
		return element;
	}

	protected IAxiom createAxiom(IInternalParent parent,
			String name) throws RodinDBException {
			IAxiom axiom =  createInternalElement(parent,
					IAxiom.ELEMENT_TYPE, name);
			axiom.setLabel(name, null);
			return axiom;
		}

	protected ITheorem createTheorem(IInternalParent parent,
			String name) throws RodinDBException {
			ITheorem theorem =  createInternalElement(parent,
					ITheorem.ELEMENT_TYPE, name);
			theorem.setLabel(name, null);
			return theorem;
		}
	
	protected IVariable createVariable(IInternalParent parent,
		String name) throws RodinDBException {
		IVariable variable =  createInternalElement(parent,
				IVariable.ELEMENT_TYPE, name);
		return variable;
	}
	
	
	protected IEvent createEvent(IInternalParent parent,
			String name) throws RodinDBException {
			IEvent event =  createInternalElement(parent,
					IEvent.ELEMENT_TYPE, name);
			event.setLabel(name, null);
			return event;
	}
	
	protected IInvariant createInvariant(IInternalParent parent,
			String name) throws RodinDBException {
			IInvariant invariant =  createInternalElement(parent,
					IInvariant.ELEMENT_TYPE, name);
			invariant.setLabel(name, null);
			return invariant;
	}
	

	protected IConstant createConstant(IInternalParent parent,
			String name) throws RodinDBException {
		IConstant constant =  createInternalElement(parent,
				IConstant.ELEMENT_TYPE, name);
			return constant;
	}
	
	protected ICarrierSet createCarrierSet(IInternalParent parent,
			String name) throws RodinDBException {
		ICarrierSet carrier =  createInternalElement(parent,
				ICarrierSet.ELEMENT_TYPE, name);
			return carrier;
	}

	protected IPOSequent createSequent(IInternalParent parent,
			String name) throws RodinDBException {
		IPOSequent sequent =  createInternalElement(parent,
				IPOSequent.ELEMENT_TYPE, name);
		return sequent;
	}

	protected IPSStatus createPSStatus(IInternalParent parent,
			String name) throws RodinDBException {
		IPSStatus status =  createInternalElement(parent,
				IPSStatus.ELEMENT_TYPE, name);
		return status;
	}
	
	protected IPOSource createPOSource(IInternalParent parent,
			String name) throws RodinDBException {
		IPOSource source =  createInternalElement(parent,
				IPOSource.ELEMENT_TYPE, name);
		return source;
	}

	protected IWitness createWitness(IInternalParent parent,
			String name) throws RodinDBException {
		IWitness witness =  createInternalElement(parent,
				IWitness.ELEMENT_TYPE, name);
		return witness;
	}

	protected IAction createAction(IInternalParent parent,
			String name) throws RodinDBException {
		IAction action =  createInternalElement(parent,
				IAction.ELEMENT_TYPE, name);
		return action;
	}

	protected IGuard createGuard(IInternalParent parent,
			String name) throws RodinDBException {
		IGuard guard =  createInternalElement(parent,
				IGuard.ELEMENT_TYPE, name);
		return guard;
	}
	
	@Before
	public void setUp() throws Exception {
		rodinProject = createRodinProject("P");
	}
	
	@After
	public void tearDown() throws Exception {
		deleteProject("P");
		ModelController.removeProject(rodinProject);
		
	}
	
	protected static IRodinProject createRodinProject(final String projectName) throws CoreException {
		IWorkspaceRunnable create = new IWorkspaceRunnable() {
			public void run(IProgressMonitor monitor) throws CoreException {
				// create project
				createProject(projectName);
				
				// set Rodin nature
				addRodinNature(projectName);
			}
		};
		workspace.run(create, null);	
		return RodinCore.getRodinDB().getRodinProject(projectName);
	}

	/*
	 * Create simple project.
	 */
	protected static IProject createProject(final String projectName) throws CoreException {
		final IProject project = workspace.getRoot().getProject(projectName);
		IWorkspaceRunnable create = new IWorkspaceRunnable() {
			public void run(IProgressMonitor monitor) throws CoreException {
				project.create(null);
				project.open(null);
			}
		};
		workspace.run(create, null);	
		return project;
	}

	protected static void addRodinNature(String projectName) throws CoreException {
		IProject project = workspace.getRoot().getProject(projectName);
		IProjectDescription description = project.getDescription();
		description.setNatureIds(new String[] {RodinCore.NATURE_ID});
		project.setDescription(description, null);
	}
	
	protected static void deleteProject(String projectName) throws CoreException {
		IProject project = workspace.getRoot().getProject(projectName);
//		if (project.exists() && !project.isOpen()) { // force opening so that project can be deleted without logging (see bug 23629)
//			project.open(null);
//		}
		project.delete(true, null);
	}
	
	/**
	 * Utility method to create a new refines machine clause for a machine.
	 * 
	 * @param machine
	 *            the input machine {@link IMachineFile}.
	 * @param abstractMachineName
	 *            the name of the abstract machine.
	 * @return the newly created refines machine clause
	 * @throws RodinDBException
	 *             if some problems occur.
	 */
	protected IRefinesMachine createRefinesMachineClause(IMachineRoot machine,
			IMachineRoot abstractMachine, String clauseName) throws RodinDBException {
		IRefinesMachine refinesClause = machine.getRefinesClause(clauseName);
		refinesClause.create(null, null);
		refinesClause.setAbstractMachineName(abstractMachine.getElementName(),
				null);
		return refinesClause;
	}
	

	protected ISeesContext createSeesContextClause(IMachineRoot machine,
			IContextRoot seenContext, String clauseName) throws RodinDBException {
		ISeesContext seesContext = machine.getSeesClause(clauseName);
		seesContext.create(null, null);
		seesContext.setSeenContextName(seenContext.getElementName(), null);
		return seesContext;
	}
	

	protected IExtendsContext createExtendsContextClause(IContextRoot context, IContextRoot abstractContext, String clauseName) throws RodinDBException {
		IExtendsContext extendsContext = context.getExtendsClause(clauseName);
		extendsContext.create(null, null);
		extendsContext.setAbstractContextName(abstractContext.getElementName(), null);
		return extendsContext;
	}
	
	public static <T> void assertArray(T[] actual, T... expected) {
		//sort the array, the order doesn't matter
		Arrays.sort(actual, comparator );
		Arrays.sort(expected, comparator );
		assertEquals(Arrays.asList(expected), Arrays.asList(actual));
		
	}

	
	/**
	 * Asserts that the given rodin project was processed by the model controller.
	 * @param project
	 */
	public static void assertProcessed(IRodinProject project) {
		assertNotNull("The project should exist in the model", ModelController.getProject(project));
		assertFalse(ModelController.getProject(project).needsProcessing);
	}

	/**
	 * Asserts that a given ModelElement is based on a given RodinElement
	 */
	public static void assertModel(IRodinElement expected, IModelElement actual) {
		assertEquals(expected, actual.getInternalElement());
	}

	/**
	 * Asserts that a given ModelProofObligation is based on a given sequent
	 */
	public static void assertModelPOSequent(ModelProofObligation[] actual, IPOSequent... expected ) {
		IPOSequent[] actualSeq =  new IPOSequent[actual.length];
		int i = 0;
		for (ModelProofObligation po : actual) {
			actualSeq[i] = po.getIPOSequent();
			i++;
		}
		assertArray(actualSeq,  expected);
	}

	/**
	 * Asserts that a given set of ModelProofObligations is based on a given set of statuses
	 */
	public static void assertModelPSStatus(ModelProofObligation[] actual, IPSStatus... expected ) {
		IPSStatus[] actualStat =  new IPSStatus[actual.length];
		int i = 0;
		for (ModelProofObligation po : actual) {
			actualStat[i] = po.getIPSStatus();
			i++;
		}
		assertArray(actualStat,  expected);
	}
	
	
}

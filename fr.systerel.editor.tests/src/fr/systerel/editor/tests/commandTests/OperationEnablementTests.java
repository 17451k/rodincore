/*******************************************************************************
 * Copyright (c) 2013, 2017 Systerel and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Systerel - initial API and implementation
 *******************************************************************************/
package fr.systerel.editor.tests.commandTests;

import static fr.systerel.editor.tests.TestUtils.copyTestFileInProject;
import static org.eclipse.ui.IWorkbenchCommandConstants.EDIT_COPY;
import static org.eclipse.ui.IWorkbenchCommandConstants.EDIT_CUT;
import static org.eclipse.ui.IWorkbenchCommandConstants.EDIT_DELETE;
import static org.eclipse.ui.IWorkbenchCommandConstants.EDIT_PASTE;
import static org.eclipse.ui.IWorkbenchCommandConstants.EDIT_REDO;
import static org.eclipse.ui.IWorkbenchCommandConstants.EDIT_SELECT_ALL;
import static org.eclipse.ui.IWorkbenchCommandConstants.EDIT_UNDO;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.List;

import org.eclipse.core.commands.Command;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.commands.ICommandService;
import org.eventb.core.EventBAttributes;
import org.eventb.core.IAxiom;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;
import org.rodinp.core.IRodinProject;
import org.rodinp.core.emf.api.itf.ILElement;

import fr.systerel.editor.internal.documentModel.EditorElement;
import fr.systerel.editor.internal.documentModel.Interval;
import fr.systerel.editor.internal.editors.OverlayEditor;
import fr.systerel.editor.tests.TestUtils;

/**
 * Tests which verify the enablement of redirected text operation actions such
 * as copy, paste and delete.
 */
@Ignore("These tests are too fragile:"
		+ "They do not work if the workbench window does not have the focus.")
public class OperationEnablementTests {

	private static final IWorkbench WORKBENCH = PlatformUI.getWorkbench();

	private static final String contextName = "context.buc";

	private static IRodinProject rodinProject;

	private static IProject project;

	private static OperationTestHelper helper;

	@BeforeClass
	public static void beforeClass() throws Exception {
		rodinProject = TestUtils.createRodinProject("P");
		project = rodinProject.getProject();
		copyTestFileInProject(contextName, project);
	}

	@Before
	public void setUp() throws Exception {
		final IFile testFile = project.getFile(contextName);
		helper = new OperationTestHelper(testFile);
		helper.clearClipboard();
	}

	@After
	public void tearDown() throws Exception {
		helper.closeRodinEditor();
	}
	
	@AfterClass
	public static void afterClass() throws CoreException {
		TestUtils.deleteProject("P");
	}

	/**
	 * Checks that cut, copy, paste, and delete are correctly enabled or
	 * disabled, when the Rodin element selection changes.
	 */
	@Test
	public void SelectionDependentEnablementTest() throws Exception {
		// there is only one axiom in the file at the beginning
		final ILElement axiom = getFirstAxiom(helper);
		helper.setSelection(new ILElement[] { axiom });
		assertEnabled(EDIT_COPY);
		assertEnabled(EDIT_DELETE);
		assertDisabled(EDIT_CUT);
		assertDisabled(EDIT_PASTE);
	}

	/**
	 * Checks that the paste command becomes enabled after the copy of a Rodni
	 * element has been performed.
	 */
	@Test
	public void TestPasteEnablement() throws Exception {
		// there is only one axiom in the file at the beginning
		final ILElement axiom = getFirstAxiom(helper);
		helper.setSelection(new ILElement[] { axiom });
		helper.executeOperation(EDIT_COPY);
		assertEnabled(EDIT_PASTE);
		assertEnabled(EDIT_COPY);
		assertEnabled(EDIT_DELETE);
		assertDisabled(EDIT_CUT);
		// perform paste
		helper.executeOperation(EDIT_PASTE);
		assertEnabled(EDIT_PASTE);
		assertEnabled(EDIT_COPY);
		assertEnabled(EDIT_DELETE);
		assertDisabled(EDIT_CUT);

		final ILElement root = helper.getRoot();
		final List<ILElement> axioms2 = root
				.getChildrenOfType(IAxiom.ELEMENT_TYPE);
		assertTrue(axioms2.size() == 2);
	}

	/**
	 * Checks that cut and copy actions are disabled when overlay is active and
	 * there is no selection. Paste and delete actions are always possible when
	 * overlay is active.
	 */
	@Test
	public void TestOverlayDefaultEnablement() throws Exception {
		enterAxiomPredicateEdition(helper);
		assertDisabled(EDIT_CUT);
		assertDisabled(EDIT_COPY);
		assertEnabled(EDIT_DELETE);
		assertEnabled(EDIT_PASTE);
	}

	/**
	 * Checks that copy action is enabled when overlay is inactive but some text
	 * is selected in the Rodin editor. Cut, paste and delete actions are
	 * disabled.
	 */
	@Test
	public void TestTextCopyEnablement() throws Exception {
		helper.setSelection(0, 8);
		assertEnabled(EDIT_COPY);
		assertDisabled(EDIT_CUT);
		assertDisabled(EDIT_DELETE);
		assertDisabled(EDIT_PASTE);
	}
	
	/**
	 * Checks that undo/redo actions are enabled in the Rodin editor when some
	 * delete operation is respectively performed and undone.
	 */
	@Test
	public void TestUndoRedoEnablement() throws Exception {
		assertDisabled(EDIT_UNDO);
		assertDisabled(EDIT_REDO);
		final ILElement axiom = getFirstAxiom(helper);
		helper.setSelection(new ILElement[] { axiom });
		helper.executeOperation(EDIT_DELETE);
		assertEnabled(EDIT_UNDO);
		assertDisabled(EDIT_REDO);
		helper.executeOperation(EDIT_UNDO);
		assertDisabled(EDIT_UNDO);
		assertEnabled(EDIT_REDO);
	}
	
	/**
	 * Checks that selectAll action is always enabled both in the Rodin editor
	 * and overlay editor.
	 */
	@Test
	public void TestSelectAllEnablement() throws Exception {
		assertEnabled(EDIT_SELECT_ALL);
		enterAxiomPredicateEdition(helper);
		assertEnabled(EDIT_SELECT_ALL);
	}

	public void assertEnablement(String commandName, String message,
			boolean shouldBeEnabled) {
		final ICommandService commandService = WORKBENCH.getService(ICommandService.class);
		final Command command = commandService.getCommand(commandName);
		assertEquals(command + message, shouldBeEnabled, command.isEnabled());
	}

	public void assertEnabled(String commandName) {
		assertEnablement(commandName, " should be enabled", true);
	}

	public void assertDisabled(String commandName) {
		assertEnablement(commandName, " should be not enabled", false);
	}

	private static void enterAxiomPredicateEdition(OperationTestHelper helper) {
		final ILElement axiom = getFirstAxiom(helper);
		final EditorElement axiomPos = helper.getEditor().getDocumentMapper()
				.findEditorElement(axiom);
		final Interval interval = axiomPos
				.getInterval(EventBAttributes.PREDICATE_ATTRIBUTE);
		final OverlayEditor overlay = helper.getOverlay();
		overlay.showAtOffset(interval.getOffset());
	}

	private static ILElement getFirstAxiom(OperationTestHelper helper) {
		final ILElement root = helper.getRoot();
		final List<ILElement> axioms = root
				.getChildrenOfType(IAxiom.ELEMENT_TYPE);
		final ILElement axiom = axioms.get(0);
		return axiom;
	}

}

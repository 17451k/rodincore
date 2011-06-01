/*******************************************************************************
 * Copyright (c) 2011 Systerel and others.
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 * 
 * Contributors:
 *     Systerel - Initial API and implementation
 *******************************************************************************/
package fr.systerel.editor.handlers;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.jface.text.TextSelection;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.handlers.HandlerUtil;

import fr.systerel.editor.EditorPlugin;
import fr.systerel.editor.editors.RodinEditor;

/**
 * Abstract class for the editor handlers such as add, remove, copy, etc.
 */
public abstract class AbstractEditorHandler extends AbstractHandler {

	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException {
		final IEditorPart activeEditor = EditorPlugin.getActivePage()
				.getActiveEditor();
		if (!(activeEditor instanceof RodinEditor)) {
			return null;
		}
		final RodinEditor editor = (RodinEditor) activeEditor;
		final ISelection curSel = HandlerUtil.getActiveMenuSelection(event);
		final int offset;
		if (curSel instanceof TextSelection) {
			offset = ((TextSelection) curSel).getOffset();
		} else {
			offset = editor.getCurrentOffset();
		}
		handleSelection(editor, offset);
		return null;
	}

	protected abstract void handleSelection(RodinEditor editor, int offset);

	@Override
	public boolean isEnabled() {
		final IWorkbenchWindow ww = PlatformUI.getWorkbench()
				.getActiveWorkbenchWindow();
		if (ww == null)
			return false;
		final IWorkbenchPage activePage = ww.getActivePage();
		if (activePage == null)
			return false;
		final IWorkbenchPart editor = activePage.getActivePart();
		if (!(editor instanceof RodinEditor))
			return false;
		final RodinEditor rEditor = (RodinEditor) editor;
		return checkEnablement(rEditor, rEditor.getCurrentOffset());
	}

	/**
	 * Implementors should perform enablement checkings using the given
	 * RodinEditor. Default implementation returns <code>true</code>.
	 * 
	 * @param editor
	 *            the current Rodin Editor
	 * @param caretOffset
	 * @return <code>true</code> is the command should be enabled,
	 *         <code>false</code> otherwise
	 */
	protected boolean checkEnablement(RodinEditor editor, int caretOffset) {
		return true;
	}

}

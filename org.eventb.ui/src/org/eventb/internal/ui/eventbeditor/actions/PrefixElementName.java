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
package org.eventb.internal.ui.eventbeditor.actions;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.QualifiedName;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.dialogs.InputDialog;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.ui.IEditorActionDelegate;
import org.eclipse.ui.IEditorPart;
import org.eventb.internal.ui.EventBUIExceptionHandler;
import org.eventb.internal.ui.UIUtils;
import org.eventb.internal.ui.eventbeditor.editpage.AttributeRelUISpecRegistry;
import org.eventb.ui.eventbeditor.IEventBEditor;
import org.rodinp.core.IInternalElementType;
import org.rodinp.core.IRodinFile;

public abstract class PrefixElementName implements IEditorActionDelegate {

	IEventBEditor<?> editor;

	public void setActiveEditor(IAction action, IEditorPart targetEditor) {
		editor = (IEventBEditor<?>) targetEditor;
	}

	public void setPrefix(String attributeID, String dialogTitle, String message) {
		IInternalElementType<?> type = AttributeRelUISpecRegistry.getDefault()
				.getType(attributeID);
		QualifiedName qualifiedName = UIUtils.getQualifiedName(type);
		IRodinFile inputFile = editor.getRodinInput().getRodinFile();
		String prefix = null;
		try {
			prefix = inputFile.getResource().getPersistentProperty(
					qualifiedName);
		} catch (CoreException e) {
			EventBUIExceptionHandler.handleGetPersistentPropertyException(e);
		}

		if (prefix == null)
			prefix = AttributeRelUISpecRegistry.getDefault().getDefaultPrefix(
					attributeID);
		InputDialog dialog = new InputDialog(editor.getSite().getShell(),
				dialogTitle, message, prefix, null);
		dialog.open();
		prefix = dialog.getValue();

		try {
			if (prefix != null)
				inputFile.getResource().setPersistentProperty(qualifiedName,
						prefix);
		} catch (CoreException e) {
			EventBUIExceptionHandler.handleSetPersistentPropertyException(e);
		}
	}

	public void selectionChanged(IAction action, ISelection selection) {
		// Do nothing
	}

}

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
package org.eventb.internal.ui.projectexplorer.actions;

import org.eclipse.core.resources.IWorkspaceRunnable;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.dialogs.InputDialog;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.ui.IActionDelegate;
import org.eclipse.ui.IObjectActionDelegate;
import org.eclipse.ui.IWorkbenchPart;
import org.eventb.core.EventBPlugin;
import org.eventb.core.IContextRoot;
import org.eventb.core.IMachineRoot;
import org.rodinp.core.IInternalElement;
import org.rodinp.core.IRodinFile;
import org.rodinp.core.IRodinProject;
import org.rodinp.core.RodinCore;
import org.rodinp.core.RodinDBException;

public class Renames implements IObjectActionDelegate {

	private ISelection selection;

	private IWorkbenchPart part;

	/**
	 * Constructor.
	 */
	public Renames() {
		super();
	}

	/**
	 * @see IObjectActionDelegate#setActivePart(IAction, IWorkbenchPart)
	 */
	public void setActivePart(IAction action, IWorkbenchPart targetPart) {
		part = targetPart;
	}

	/**
	 * @see IActionDelegate#run(IAction)
	 */
	public void run(IAction action) {
		if (selection instanceof IStructuredSelection) {
			IStructuredSelection ssel = (IStructuredSelection) selection;
			if (ssel.size() == 1) {
				Object obj = ssel.getFirstElement();
				if(!(obj instanceof IInternalElement))
					return;
				final IInternalElement root = (IInternalElement) obj;
				if (!(root.getParent() instanceof IRodinFile))
					return;
				final IRodinFile file = root.getRodinFile();
				final IRodinProject prj = file.getRodinProject();

				InputDialog dialog = new InputDialog(part.getSite().getShell(),
						"Rename Component",
						"Please enter the new name of for the component", "m0",
						new RodinFileInputValidator(prj));

				dialog.open();

				final String bareName = dialog.getValue();
				
				if (dialog.getReturnCode() == InputDialog.CANCEL)
					return; // Cancel
				
				assert bareName != null;
				
				try {
					RodinCore.run(new IWorkspaceRunnable() {

						public void run(IProgressMonitor monitor)
								throws RodinDBException {
							String newName = null;
							if (root instanceof IContextRoot)
								newName = EventBPlugin
										.getContextFileName(bareName);
							else if (root instanceof IMachineRoot)
								newName = EventBPlugin
										.getMachineFileName(bareName);

							if (newName != null)
								file.rename(newName, false, monitor);
						}

					}, null);
				} catch (RodinDBException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

			}
		}

	}

	/**
	 * @see IActionDelegate#selectionChanged(IAction, ISelection)
	 */
	public void selectionChanged(IAction action, ISelection sel) {
		this.selection = sel;
	}
}

/*******************************************************************************
 * Copyright (c) 2005 ETH Zurich.
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Rodin @ ETH Zurich
 ******************************************************************************/

package org.eventb.internal.ui.wizards;

import java.lang.reflect.InvocationTargetException;

import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.IWorkspaceRunnable;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.ui.INewWizard;
import org.eclipse.ui.IWorkbench;
import org.eventb.core.IEvent;
import org.eventb.core.IMachineFile;
import org.eventb.internal.ui.UIUtils;
import org.eventb.internal.ui.eventbeditor.actions.PrefixEvtName;
import org.eventb.ui.EventBUIPlugin;
import org.rodinp.core.IRodinDB;
import org.rodinp.core.IRodinFile;
import org.rodinp.core.IRodinProject;
import org.rodinp.core.RodinCore;

/**
 * @author htson
 *         <p>
 *         This is the new wizard for creating new Event-B components (machine,
 *         context, etc.) resource in the provided project. If the project
 *         resource (a folder or a project) is selected in the workspace when
 *         the wizard is opened, it will accept it as the target project. The
 *         wizard creates one file with the extension "bum" / "buc" (for
 *         machine/context respectively). An instance of the Event-B Editor will
 *         be opened for editting the new component.
 */
public class NewComponentWizard extends Wizard implements INewWizard {

	/**
	 * The identifier of the new component wizard (value
	 * <code>"org.eventb.ui.wizards.NewComponent"</code>).
	 */
	public static final String WIZARD_ID = EventBUIPlugin.PLUGIN_ID
			+ ".wizards.NewComponent";

	// The wizard page.
	private NewComponentWizardPage page;

	// The selection when the wizard is launched.
	private ISelection selection;

	/**
	 * Constructor: This wizard needs a progress monitor.
	 */
	public NewComponentWizard() {
		super();
		setNeedsProgressMonitor(true);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.jface.wizard.IWizard#addPages()
	 */
	public void addPages() {
		page = new NewComponentWizardPage(selection);
		addPage(page);
	}

	/**
	 * This method is called when 'Finish' button is pressed in the wizard. We
	 * will create an operation and run it using wizard as execution context.
	 * <p>
	 * 
	 * @see org.eclipse.jface.wizard.IWizard#performFinish()
	 */
	public boolean performFinish() {
		final String projectName = page.getContainerName();
		final String fileName = page.getComponentName() + "." + page.getType();

		IRunnableWithProgress op = new IRunnableWithProgress() {
			public void run(IProgressMonitor monitor)
					throws InvocationTargetException {
				try {
					doFinish(projectName, fileName, monitor);
				} catch (CoreException e) {
					throw new InvocationTargetException(e);
				} finally {
					monitor.done();
				}
			}
		};
		try {
			getContainer().run(true, false, op);
		} catch (InterruptedException e) {
			return false;
		} catch (InvocationTargetException e) {
			Throwable realException = e.getTargetException();
			MessageDialog.openError(getShell(), "Error", realException
					.getMessage());
			return false;
		}
		return true;
	}

	/**
	 * The worker method. It will find the project, create the file, and open
	 * the editor on the newly created file.
	 * <p>
	 * 
	 * @param projectName
	 *            the name of the project
	 * @param fileName
	 *            the name of the file
	 * @param monitor
	 *            a progress monitor
	 * @throws CoreException
	 *             a core exception when creating the new file
	 */
	private void doFinish(String projectName, final String fileName,
			IProgressMonitor monitor) throws CoreException {

		monitor.beginTask("Creating " + fileName, 2);
		IWorkspaceRoot root = ResourcesPlugin.getWorkspace().getRoot();
		IResource resource = root.findMember(new Path(projectName));
		if (!resource.exists() || !(resource instanceof IContainer)) {
			throwCoreException("Project \"" + projectName
					+ "\" does not exist.");
		}

		IRodinDB db = EventBUIPlugin.getRodinDatabase();
		// Creating a project handle
		final IRodinProject rodinProject = db.getRodinProject(projectName);

		RodinCore.run(new IWorkspaceRunnable() {

			public void run(IProgressMonitor monitor) throws CoreException {
				final IRodinFile rodinFile = rodinProject.createRodinFile(fileName,
						false, null);
				if (rodinFile instanceof IMachineFile) {
					IEvent init = (IEvent) rodinFile.createInternalElement(IEvent.ELEMENT_TYPE,
							"internal_" + PrefixEvtName.DEFAULT_PREFIX + 1, null, monitor);
					init.setLabel(IEvent.INITIALISATION, monitor);
					init.setConvergence(IEvent.ORDINARY, monitor);
					init.setInherited(false, monitor);
				}
				rodinFile.save(null, true);
			}
			
		}, monitor);
		

		monitor.worked(1);
		
		monitor.setTaskName("Opening file for editing...");
		getShell().getDisplay().asyncExec(new Runnable() {
			public void run() {
				UIUtils.linkToEventBEditor(rodinProject.getRodinFile(fileName));
			}
		});
		monitor.worked(1);
	}

	/**
	 * Throw a Core exception.
	 * <p>
	 * 
	 * @param message
	 *            The message for displaying
	 * @throws CoreException
	 *             a Core exception with the status contains the input message
	 */
	private void throwCoreException(String message) throws CoreException {
		IStatus status = new Status(IStatus.ERROR, "org.eventb.internal.ui",
				IStatus.OK, message, null);
		throw new CoreException(status);
	}

	/**
	 * We will accept the selection in the workbench to see if we can initialize
	 * from it.
	 * <p>
	 * 
	 * @see org.eclipse.ui.IWorkbenchWizard#init(org.eclipse.ui.IWorkbench,
	 *      org.eclipse.jface.viewers.IStructuredSelection)
	 */
	public void init(IWorkbench workbench, IStructuredSelection selection) {
		this.selection = selection;
	}

}
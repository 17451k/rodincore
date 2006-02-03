/*******************************************************************************
 * Copyright (c) 2005 ETH-Zurich
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     ETH RODIN Group
 *******************************************************************************/

package org.eventb.ui.views;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.swt.custom.BusyIndicator;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IEditorReference;
import org.eclipse.ui.ISharedImages;
import org.eclipse.ui.IWorkbenchActionConstants;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.actions.ActionGroup;
import org.eclipse.ui.part.DrillDownAdapter;
import org.eclipse.ui.part.FileEditorInput;
import org.eventb.ui.EventBImageDescriptor;
import org.eventb.ui.EventBUIPlugin;
import org.eventb.ui.prover.ProverUI;
import org.eventb.ui.wizards.NewConstructWizard;
import org.eventb.ui.wizards.NewProjectWizard;
import org.rodinp.core.IRodinDB;
import org.rodinp.core.IRodinElement;
import org.rodinp.core.IRodinFile;
import org.rodinp.core.IRodinProject;
import org.rodinp.core.RodinDBException;

/**
 * @author htson
 * <p>
 * This class provides the actions that will be used with the tree viewer 
 * for the Project Explorer.
 */
public class ProjectExplorerActionGroup 
	extends ActionGroup 
{

	// The project explorer.
	private ProjectExplorer explorer;
	
	// Some actions and the drill down adapter
	public static DrillDownAdapter drillDownAdapter;
	public static Action newProjectAction;
	public static Action newConstructAction;
	public static Action deleteAction;
	public static Action proveAction;
	
	/**
	 * Constructor: Create the actions.
	 * @param projectExplorer The project explorer
	 */
	public ProjectExplorerActionGroup(ProjectExplorer projectExplorer) {
		this.explorer = projectExplorer;
		drillDownAdapter = new DrillDownAdapter(explorer.getTreeViewer());
		
		// Creating the public action
		newProjectAction = new Action() {
			public void run() {
				BusyIndicator.showWhile(explorer.getTreeViewer().getTree().getDisplay(),
						new Runnable() {
							public void run() {
								NewProjectWizard wizard = new NewProjectWizard();
								WizardDialog dialog = new WizardDialog(EventBUIPlugin.getActiveWorkbenchShell(), wizard);
								dialog.create();
								dialog.open();
							}
						});
			}
		};
		newProjectAction.setText("&Project");
		newProjectAction.setToolTipText("Create new project");
		newProjectAction.setImageDescriptor(new EventBImageDescriptor(EventBUIPlugin.IMG_NEW_PROJECT));

		newConstructAction = new Action() {
			public void run() {
				BusyIndicator.showWhile(explorer.getTreeViewer().getTree().getDisplay(),
					new Runnable() {
						public void run() {
							IStructuredSelection sel = (IStructuredSelection) explorer.getTreeViewer().getSelection();
							NewConstructWizard wizard = new NewConstructWizard();
							wizard.init(EventBUIPlugin.getDefault().getWorkbench(), sel);
							WizardDialog dialog = new WizardDialog(EventBUIPlugin.getActiveWorkbenchShell(), wizard);
							dialog.create();
							//SWTUtil.setDialogSize(dialog, 500, 500);
							dialog.open();
						}
					});
			}
		};
		newConstructAction.setText("&Construct");
		newConstructAction.setToolTipText("Create new construct");
		newConstructAction.setImageDescriptor(new EventBImageDescriptor(EventBUIPlugin.IMG_NEW_CONSTRUCT));
	
		deleteAction = new Action() {
			public void run() {
				ISelection selection = explorer.getTreeViewer().getSelection();
				if (!(selection.isEmpty())) {
					IStructuredSelection ssel = (IStructuredSelection) selection;
					Object [] slist = ssel.toArray();
				
					for (int i = 0; i < slist.length; i++) {
						System.out.println(slist[i].toString() + " : " + slist[i].getClass().toString());
						if (slist[i] instanceof IRodinProject) {
							IRodinProject rodinProject = (IRodinProject) slist[i];
							IProject project = rodinProject.getProject();
							
							try {
								IRodinElement [] files =  rodinProject.getChildren();
								for (int j = 0; j < files.length; j++) {
									if (files[j] instanceof IRodinFile) 
										closeOpenedEditor((IRodinFile) files[j]);
								}
							
								project.delete(true, true, null);
							}
							catch (PartInitException e) {
								e.printStackTrace();
							}
							catch (RodinDBException e) {
								e.printStackTrace();
							}
							catch (CoreException e) {
								e.printStackTrace();
							}
						}
					
						else if (slist[i] instanceof IRodinFile) {
							try {
								closeOpenedEditor((IRodinFile) slist[i]);
								((IRodinFile) slist[i]).delete(true, new NullProgressMonitor());
							}
							catch (PartInitException e) {
								e.printStackTrace();
							}
							catch (RodinDBException e) {
								e.printStackTrace();
							}
						}
					}
				//viewer.refresh();
				}
			}
		};
		deleteAction.setText("Delete");
		deleteAction.setToolTipText("Delete selected elements");
		deleteAction.setImageDescriptor(new EventBImageDescriptor(EventBUIPlugin.IMG_DELETE));
		
		proveAction = new Action() {
			public void run() {
				linkToProverUI(null);
			}
		};
		proveAction.setText("Prove");
		proveAction.setToolTipText("Start the prover");
		proveAction.setImageDescriptor(PlatformUI.getWorkbench().getSharedImages().
			getImageDescriptor(ISharedImages.IMG_OBJS_INFO_TSK));
		
	}

	
	/*
	 * Link to the prover UI.
	 * <p>
	 * @param obj The selected object 
	 */
	private void linkToProverUI(Object obj) {
		// TODO To be removed
		
		String editorId = ProverUI.EDITOR_ID;
		IRodinFile construct;
//		
		// open the dummy IRodinFile file
		IRodinDB db = EventBUIPlugin.getRodinDatabase();
		IRodinProject prj = db.getRodinProject("Marriage");
		construct = prj.getRodinFile("m0.bum");
//		
//		//if (!(obj instanceof IRodinProject)) {
//			//construct = (IRodinFile) TreeNode.getOpenable(obj); 
//			// System.out.println("Top construct: " + construct.toString());
//			
			try {
				// Getting the reference to the editor that opens the construct
//				IEditorReference editorReference = isOpen(construct);
				//ProverUI editor;
////			
////				if (editorReference != null) {
////					editor = (EventBEditor) editorReference.getEditor(true);
////					EventBUIPlugin.getActivePage().activate(editor);
////				}
////				else {
				
				IEditorInput fileInput = new FileEditorInput(construct.getResource());
				EventBUIPlugin.getActivePage().openEditor(fileInput, editorId);
//				}
//				//if (obj instanceof Leaf) editor.getContentOutlinePage().setTreeSelection(new StructuredSelection(obj));
//				//editor.setSelection(obj);
			} catch (PartInitException e) {
				MessageDialog.openError(null, null, "Error open the editor");
				e.printStackTrace();
				// TODO EventBUIPlugin.logException(e);
			}
//		}
		return;
	}
	
	
	/*
	 * Close the open editor for a particular Rodin File 
	 * @param file A Rodin File
	 * @throws PartInitException Exception when closing the editor
	 */
	private void closeOpenedEditor(IRodinFile file) throws PartInitException {
		IEditorReference [] editorReferences = EventBUIPlugin.getActivePage().getEditorReferences();
		for (int j = 0; j < editorReferences.length; j++) {
			IFile inputFile = (IFile) editorReferences[j].getEditorInput().getAdapter(IFile.class);
	
			if (file.getResource().equals(inputFile)) {
				IEditorPart editor = editorReferences[j].getEditor(true);
				IWorkbenchPage page = EventBUIPlugin.getActivePage(); 
				page.closeEditor(editor, false);
			}
		}
	}

	
	/* 
	 * Dynamically fill the context menu (depends on the selection).
	 * <p> 
	 * @see org.eclipse.ui.actions.ActionGroup#fillContextMenu(org.eclipse.jface.action.IMenuManager)
	 */
	public void fillContextMenu(IMenuManager menu) {
		ISelection sel = getContext().getSelection();
		if (sel instanceof IStructuredSelection) {
			MenuManager newMenu = new MenuManager("&New");
			
			IStructuredSelection ssel = (IStructuredSelection) sel;
			newMenu.add(newProjectAction);
			newMenu.add(new Separator());
			if (ssel.size() == 1) {
				Object obj = ssel.getFirstElement();
				if (obj instanceof IRodinProject) {
					newMenu.add(newConstructAction);
				}
			}
			menu.add(newMenu);
			menu.add(deleteAction);
			if ((ssel.size() == 1) && (ssel.getFirstElement() instanceof IRodinFile)) menu.add(proveAction);
			menu.add(new Separator());
			drillDownAdapter.addNavigationActions(menu);
			
			// Other plug-ins can contribute there actions here
			menu.add(new Separator(IWorkbenchActionConstants.MB_ADDITIONS));
			super.fillContextMenu(menu);
		}
	}

}

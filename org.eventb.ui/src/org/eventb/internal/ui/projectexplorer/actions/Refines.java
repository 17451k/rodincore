package org.eventb.internal.ui.projectexplorer.actions;

import org.eclipse.core.resources.IWorkspaceRunnable;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.dialogs.InputDialog;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.ui.IActionDelegate;
import org.eclipse.ui.IObjectActionDelegate;
import org.eclipse.ui.IWorkbenchPart;
import org.eventb.core.EventBPlugin;
import org.eventb.core.IEvent;
import org.eventb.core.IMachineFile;
import org.eventb.core.IRefinesEvent;
import org.eventb.core.IRefinesMachine;
import org.eventb.core.ISeesContext;
import org.eventb.core.IVariable;
import org.eventb.core.IWitness;
import org.eventb.internal.ui.UIUtils;
import org.eventb.internal.ui.eventbeditor.actions.PrefixRefinesEventName;
import org.eventb.internal.ui.eventbeditor.actions.PrefixRefinesMachineName;
import org.rodinp.core.IInternalElement;
import org.rodinp.core.IInternalElementType;
import org.rodinp.core.IRodinDB;
import org.rodinp.core.IRodinElement;
import org.rodinp.core.IRodinFile;
import org.rodinp.core.IRodinProject;
import org.rodinp.core.RodinCore;
import org.rodinp.core.RodinDBException;

public class Refines implements IObjectActionDelegate {

	private ISelection selection;

	private IWorkbenchPart part;

	IRodinFile newFile;

	/**
	 * Constructor for Action1.
	 */
	public Refines() {
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
				if (!(obj instanceof IMachineFile))
					return;
				final IMachineFile machine = (IMachineFile) obj;
				final IRodinProject prj = machine.getRodinProject();

				InputDialog dialog = new InputDialog(part.getSite().getShell(),
						"New REFINES Clause",
						"Please enter the name of the new machine", "m0",
						new RodinFileInputValidator(prj));

				dialog.open();

				final String abstractMachineName = machine.getComponentName();
				final String bareName = dialog.getValue();
				if (bareName == null)
					return;

				try {
					RodinCore.run(new IWorkspaceRunnable() {

						public void run(IProgressMonitor monitor)
								throws CoreException {
							newFile = prj.getRodinFile(EventBPlugin
									.getMachineFileName(bareName));
							newFile.create(false, monitor);

							IRefinesMachine refined = newFile
									.getInternalElement(
											IRefinesMachine.ELEMENT_TYPE,
											"internal_"
													+ PrefixRefinesMachineName.DEFAULT_PREFIX
													+ 1);
							refined.create(null, monitor);
							refined.setAbstractMachineName(abstractMachineName,
									null);

							copyChildrenOfType(newFile, machine,
									ISeesContext.ELEMENT_TYPE, monitor);
							copyChildrenOfType(newFile, machine,
									IVariable.ELEMENT_TYPE, monitor);
							copyChildrenOfType(newFile, machine,
									IEvent.ELEMENT_TYPE, monitor);

							IRodinElement[] elements = machine
									.getChildrenOfType(IEvent.ELEMENT_TYPE);

							for (IRodinElement element : elements) {
								String name = ((IEvent) element)
										.getElementName();
								String label = ((IEvent) element).getLabel();
								IInternalElement newElement = newFile
										.getInternalElement(
												IEvent.ELEMENT_TYPE, name);
								// Need to remove the existing IRefinesEvent
								// elements
								IRodinElement[] refinesEvents = newElement
										.getChildrenOfType(IRefinesEvent.ELEMENT_TYPE);
								for (IRodinElement refinesEvent : refinesEvents)
									((IRefinesEvent) refinesEvent).delete(true,
											monitor);

								// Need to remove the existing Witness elements
								IRodinElement[] witnesses = newElement
										.getChildrenOfType(IWitness.ELEMENT_TYPE);
								for (IRodinElement witness : witnesses)
									((IWitness) witness).delete(true, monitor);

								// INITILISATION does not have RefineEvents
								// Element
								if (!label.equals(IEvent.INITIALISATION)) {
									IRefinesEvent refinesEvent = newElement
											.getInternalElement(
													IRefinesEvent.ELEMENT_TYPE,
													"internal_"
															+ PrefixRefinesEventName.DEFAULT_PREFIX
															+ 1);
									refinesEvent.create(null, monitor);
									refinesEvent.setAbstractEventLabel(label,
											null);
								}
							}
							newFile.save(null, true);
						}

					}, null);
				} catch (CoreException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
					newFile = null;
				}
				if (newFile != null)
					UIUtils.linkToEventBEditor(newFile);

			}
		}

	}

	/**
	 * @see IActionDelegate#selectionChanged(IAction, ISelection)
	 */
	public void selectionChanged(IAction action, ISelection sel) {
		this.selection = sel;
	}

	<T extends IInternalElement> void copyChildrenOfType(
			IRodinFile destination, IRodinFile original,
			IInternalElementType<T> type, IProgressMonitor monitor)
			throws RodinDBException {

		final T[] elements = original.getChildrenOfType(type);
		final IRodinFile[] containers = new IRodinFile[] {destination};
		final IRodinDB rodinDB = destination.getRodinDB();
		rodinDB.copy(elements, containers, null, null, false, monitor);
	}

}

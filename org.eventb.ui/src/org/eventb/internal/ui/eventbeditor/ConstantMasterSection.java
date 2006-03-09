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

package org.eventb.internal.ui.eventbeditor;

import java.util.Collection;
import java.util.Iterator;

import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.forms.IManagedForm;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eventb.core.IConstant;
import org.eventb.core.IContext;
import org.eventb.internal.ui.UIUtils;
import org.eventb.internal.ui.UIUtils.ElementLabelProvider;
import org.rodinp.core.ElementChangedEvent;
import org.rodinp.core.IRodinElement;
import org.rodinp.core.IRodinElementDelta;
import org.rodinp.core.IRodinFile;
import org.rodinp.core.RodinDBException;

/**
 * @author htson
 * <p>
 * An implementation of the Event-B Table part with buttons
 * for displaying constants (used as master section in Master-Detail block).
 */
public class ConstantMasterSection
	extends EventBTablePartWithButtons 
{

	/**
	 * The content provider class. 
	 */
	class MasterContentProvider
	implements IStructuredContentProvider {
		public Object[] getElements(Object parent) {
			if (parent instanceof IContext)
				try {
					return ((IContext) parent).getConstants();
				}
				catch (RodinDBException e) {
					// TODO Exception handle
					e.printStackTrace();
				}
			return new Object[0];
		}
    	
    	public void dispose() {return;}

    	public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
    		return;
    	}
    }
	

	/**
	 * Contructor.
	 * <p>
	 * @param managedForm The form to create this master section
	 * @param parent The composite parent
	 * @param toolkit The Form Toolkit used to create this master section
	 * @param style The style
	 * @param block The master detail block which this master section belong to
	 */
	public ConstantMasterSection(IManagedForm managedForm, Composite parent, FormToolkit toolkit, 
			int style, EventBMasterDetailsBlock block) {
		super(managedForm, parent, toolkit, style, block);
	}
	

	/**
	 * Handle the adding (new Axiom) action
	 */
	protected void handleAdd() {
		ElementAtributeInputDialog dialog = new ElementAtributeInputDialog(this.getSection().getShell(), this.getManagedForm().getToolkit(), "New Constants", "Name of the new constant", "cst" + (1));
		dialog.open();
		Collection<String> names = dialog.getAttributes();
		try {
			for (Iterator<String> it = names.iterator(); it.hasNext();) {
				String name = it.next();
				rodinFile.createInternalElement(IConstant.ELEMENT_TYPE, name, null, null);
			}
		}
		catch (RodinDBException e) {
			e.printStackTrace();
		}
		this.getViewer().setInput(rodinFile);
		this.markDirty();
		updateButtons();
	}
	

	/**
	 * Setting the (tree) viewer input of this master section 
	 */
	protected void setViewerInput() {
		// TODO Move this to the super class
		TableViewer viewer = this.getViewer();
		viewer.setContentProvider(new MasterContentProvider());
		viewer.setLabelProvider(new ElementLabelProvider());
		rodinFile = ((EventBEditor) this.getBlock().getPage().getEditor()).getRodinInput();
		viewer.setInput(rodinFile);
	}

	/* (non-Javadoc)
	 * @see org.rodinp.core.IElementChangedListener#elementChanged(org.rodinp.core.ElementChangedEvent)
	 */
	public void elementChanged(ElementChangedEvent event) {
		IRodinElementDelta delta = event.getDelta();
		processDelta(delta);
	}
	
	private void processDelta(IRodinElementDelta delta) {
		IRodinElement element= delta.getElement();
		if (element instanceof IRodinFile) {
			IRodinElementDelta [] deltas = delta.getAffectedChildren();
			for (int i = 0; i < deltas.length; i++) {
				processDelta(deltas[i]);
			}

			return;
		}
		if (element instanceof IConstant) {
			UIUtils.postRunnable(new Runnable() {
				public void run() {
					getViewer().setInput(rodinFile);
					markDirty();
					updateButtons();
				}
			}, this.getSection().getClient());
		}
		else {
			return;
		}
	}
}

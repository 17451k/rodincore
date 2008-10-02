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
package fr.systerel.explorer.navigator.contentProviders;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.Viewer;
import org.eventb.core.IContextFile;
import org.eventb.core.IMachineFile;
import org.rodinp.core.IRodinProject;
import org.rodinp.core.RodinCore;
import org.rodinp.core.RodinDBException;

import fr.systerel.explorer.model.ModelController;

/**
 * The content provider for Context elements.
 *
 */
public class ContextContentProvider implements ITreeContentProvider {

	public Object[] getChildren(Object element) {
	       if (element instanceof IProject) {
	    	   IProject project = (IProject) element;
	    	   if (project.isAccessible()) {
					try {
						//if it is a RodinProject return the IRodinProject from the DB.
						if (project.hasNature(RodinCore.NATURE_ID)) {
							IRodinProject proj = (RodinCore.getRodinDB().getRodinProject(project.getName()));
							if (proj != null) {
				            	ModelController.processProject(proj);
								return proj.getChildrenOfType(IContextFile.ELEMENT_TYPE);
							}
						} 
					} catch (CoreException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
	       	}
	        return new Object[0];
	}

	public Object getParent(Object element) {
        if (element instanceof IMachineFile) {
			return ((IMachineFile) element).getParent();
		}
        if (element instanceof IContextFile) {
			return ((IContextFile) element).getParent();
		}
        return null;
	}

	public boolean hasChildren(Object element) {
		if (element instanceof IRodinProject) {
        	IRodinProject project = (IRodinProject) element;
        	try {
				return project.hasChildren();
			} catch (RodinDBException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
		}
        return getChildren(element).length > 0;
	}

	public Object[] getElements(Object inputElement) {
		return getChildren(inputElement);
	}

	public void dispose() {
    	// Do nothing

	}

	public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
	   	// Do nothing
		 
	}

}

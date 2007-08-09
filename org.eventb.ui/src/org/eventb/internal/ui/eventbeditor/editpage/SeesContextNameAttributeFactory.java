package org.eventb.internal.ui.eventbeditor.editpage;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eventb.core.EventBAttributes;
import org.eventb.core.IContextFile;
import org.eventb.core.ISeesContext;
import org.eventb.ui.eventbeditor.IEventBEditor;
import org.rodinp.core.IAttributedElement;
import org.rodinp.core.IRodinProject;
import org.rodinp.core.RodinDBException;

public class SeesContextNameAttributeFactory implements IAttributeFactory {

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eventb.internal.ui.eventbeditor.editpage.IAttributeFactory#setDefaultValue(org.rodinp.core.IAttributedElement,
	 *      org.eclipse.core.runtime.IProgressMonitor)
	 */
	public void setDefaultValue(IEventBEditor<?> editor,
			IAttributedElement element, IProgressMonitor monitor)
			throws RodinDBException {
		ISeesContext seesContext = (ISeesContext) element;
		String name = "context";
		seesContext.setSeenContextName(name, monitor);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eventb.internal.ui.eventbeditor.editpage.IAttributeFactory#getValue(org.rodinp.core.IAttributedElement,
	 *      org.eclipse.core.runtime.IProgressMonitor)
	 */
	public String getValue(IAttributedElement element, IProgressMonitor monitor)
			throws RodinDBException {
		ISeesContext seesContext = (ISeesContext) element;
		return seesContext.getSeenContextName();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eventb.internal.ui.eventbeditor.editpage.IAttributeFactory#setValue(org.rodinp.core.IAttributedElement,
	 *      java.lang.String, org.eclipse.core.runtime.IProgressMonitor)
	 */
	public void setValue(IAttributedElement element, String newValue,
			IProgressMonitor monitor) throws RodinDBException {
		assert element instanceof ISeesContext;

		ISeesContext seesContext = (ISeesContext) element;

		String value;
		try {
			value = getValue(element, monitor);
		} catch (RodinDBException e) {
			value = null;
		}
		if (value == null || !value.equals(newValue)) {
			seesContext.setSeenContextName(newValue, monitor);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eventb.internal.ui.eventbeditor.editpage.IAttributeFactory#getPossibleValues(org.rodinp.core.IAttributedElement,
	 *      org.eclipse.core.runtime.IProgressMonitor)
	 */
	public String[] getPossibleValues(IAttributedElement element,
			IProgressMonitor monitor) throws RodinDBException {
		List<String> results = new ArrayList<String>();
		ISeesContext seesContext = (ISeesContext) element;
		IRodinProject rodinProject = seesContext.getRodinProject();
		IContextFile[] contextFiles = rodinProject
				.getChildrenOfType(IContextFile.ELEMENT_TYPE);
		for (IContextFile contextFile : contextFiles) {
			String bareName = contextFile.getBareName();
			results.add(bareName);
		}
		return results.toArray(new String[results.size()]);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eventb.internal.ui.eventbeditor.editpage.IAttributeFactory#removeAttribute(org.rodinp.core.IAttributedElement,
	 *      org.eclipse.core.runtime.IProgressMonitor)
	 */
	public void removeAttribute(IAttributedElement element,
			IProgressMonitor monitor) throws RodinDBException {
		element.removeAttribute(EventBAttributes.TARGET_ATTRIBUTE, monitor);
	}

}

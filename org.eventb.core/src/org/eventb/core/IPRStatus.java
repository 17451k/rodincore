package org.eventb.core;

import org.rodinp.core.IUnnamedInternalElement;
import org.rodinp.core.RodinDBException;


/**
 * @author Stefan Hallerstede
 * @author Farhad Mehta
 *
 */

public interface IPRStatus extends IUnnamedInternalElement {
		public String ELEMENT_TYPE = EventBPlugin.PLUGIN_ID + ".prStatus"; //$NON-NLS-1$

		public enum Status {PENDING, DISCHARGED};
		
		public Status getStatus() throws RodinDBException;	
		// public String getType() throws RodinDBException;
}

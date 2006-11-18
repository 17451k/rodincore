package org.eventb.core;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eventb.core.ast.Predicate;
import org.rodinp.core.IInternalElement;
import org.rodinp.core.IInternalElementType;
import org.rodinp.core.RodinCore;
import org.rodinp.core.RodinDBException;

public interface IPRPredRef extends IInternalElement {

	IInternalElementType ELEMENT_TYPE =
		RodinCore.getInternalElementType(EventBPlugin.PLUGIN_ID + ".prPredRef"); //$NON-NLS-1$

	Predicate getPredicate(IProofStoreReader store) throws RodinDBException;
	void setPredicate(Predicate pred, IProofStoreCollector store, IProgressMonitor monitor) throws RodinDBException;		
}

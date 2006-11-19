/*******************************************************************************
 * Copyright (c) 2005 ETH Zurich.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.eventb.core.basis;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eventb.core.EventBAttributes;
import org.eventb.core.IPOHint;
import org.eventb.core.IPOIdentifier;
import org.eventb.core.IPOPredicate;
import org.eventb.core.IPOPredicateSet;
import org.eventb.core.IPOSequent;
import org.eventb.core.IPOSource;
import org.eventb.internal.core.Messages;
import org.rodinp.core.IInternalElementType;
import org.rodinp.core.IRodinElement;
import org.rodinp.core.RodinDBException;

/**
 * Implementation of Event-B PO proof obligation as an extension of the Rodin database.
 * <p>
 * This class is intended to be implemented by clients that want to extend this
 * internal element type.
 * </p>
 * <p>
 * This class should not be used in any other way than subclassing it
 * in a database extension. In particular, clients should not use it,
 * but rather use its associated interface <code>IPOSequent</code>.
 * </p>
 *
 * @author Stefan Hallerstede
 *
 */
public class POSequent extends EventBElement implements IPOSequent {

	public POSequent(String name, IRodinElement parent) {
		super(name, parent);
	}

	/* (non-Javadoc)
	 * @see org.rodinp.core.RodinElement#getElementType()
	 */
	@Override
	public IInternalElementType getElementType() {
		return ELEMENT_TYPE;
	}
	
	@Deprecated
	public String getName() {
		return getElementName();
	}
	
	@Deprecated
	public IPOIdentifier[] getIdentifiers() throws RodinDBException {
		return (IPOIdentifier[]) getChildrenOfType(IPOIdentifier.ELEMENT_TYPE);
	}
	
	@Deprecated
	public IPOPredicateSet getHypothesis() throws RodinDBException {
		
		return (IPOPredicateSet) getSingletonChild(
				IPOPredicateSet.ELEMENT_TYPE, Messages.database_SequentMultipleHypothesisFailure);
		
	}
	
	@Deprecated
	public IPOPredicate getGoal() throws RodinDBException {
		
		return (IPOPredicate) getSingletonChild(
				IPOPredicate.ELEMENT_TYPE, Messages.database_SequentMultipleGoalFailure);
		
	}
	
	/* (non-Javadoc)
	 * @see org.eventb.core.IPODescription#getName()
	 */
	public String getDescription() throws RodinDBException {
		return getAttributeValue(EventBAttributes.PODESC_ATTRIBUTE);
	}

	/* (non-Javadoc)
	 * @see org.eventb.core.IPODescription#getSources()
	 */
	public IPOSource[] getSources() throws RodinDBException {
		return (IPOSource[]) getChildrenOfType(IPOSource.ELEMENT_TYPE); 
	}

	/* (non-Javadoc)
	 * @see org.eventb.core.IPODescription#getHints()
	 */
	public IPOHint[] getHints() throws RodinDBException {
		return (IPOHint[]) getChildrenOfType(IPOHint.ELEMENT_TYPE); 
	}

	/* (non-Javadoc)
	 * @see org.eventb.core.IPOSequent#setDescriptionName(java.lang.String, org.eclipse.core.runtime.IProgressMonitor)
	 */
	public void setDescription(String description, IProgressMonitor monitor) throws RodinDBException {
		setAttributeValue(EventBAttributes.PODESC_ATTRIBUTE, description, monitor);
	}

	public IPOPredicate[] getGoals() throws RodinDBException {
		return (IPOPredicate[]) getChildrenOfType(IPOPredicate.ELEMENT_TYPE); 
	}

	public IPOPredicateSet[] getHypotheses() throws RodinDBException {
		return (IPOPredicateSet[]) getChildrenOfType(IPOPredicateSet.ELEMENT_TYPE); 
	}

	public IPOPredicate getGoal(String elementName) {
		return (IPOPredicate) getInternalElement(IPOPredicate.ELEMENT_TYPE, elementName);
	}

	public IPOHint getHint(String elementName) {
		return (IPOHint) getInternalElement(IPOHint.ELEMENT_TYPE, elementName);
	}

	public IPOPredicateSet getHypothesis(String elementName) {
		return (IPOPredicateSet) getInternalElement(IPOPredicateSet.ELEMENT_TYPE, elementName);
	}

	public IPOSource getSource(String elementName) {
		return (IPOSource) getInternalElement(IPOSource.ELEMENT_TYPE, elementName);
	}

}

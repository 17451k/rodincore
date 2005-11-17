/*******************************************************************************
 * Copyright (c) 2005 ETH Zurich.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.eventb.core.basis;

import java.util.ArrayList;

import org.eventb.core.IPOPredicate;
import org.eventb.core.IPOPredicateSet;
import org.rodinp.core.IRodinElement;
import org.rodinp.core.RodinDBException;
import org.rodinp.core.basis.InternalElement;

/**
 * @author halstefa
 * 
 * A predicate set consists of other predicate sets and some predicates.
 * Note, that predicates can be identified by their NAME attributes.
 *
 */
public class POPredicateSet extends InternalElement implements IPOPredicateSet {

	/**
	 * @param name
	 * @param parent
	 */
	public POPredicateSet(String name, IRodinElement parent) {
		super(name, parent);
		// TODO Auto-generated constructor stub
	}

	/* (non-Javadoc)
	 * @see org.rodinp.core.RodinElement#getElementType()
	 */
	@Override
	public String getElementType() {
		return ELEMENT_TYPE;
	}
	
	public IPOPredicate[] getPredicates() throws RodinDBException {
		ArrayList<IRodinElement> list = getChildrenOfType(IPOPredicate.ELEMENT_TYPE);
		IPOPredicate[] predicates = new IPOPredicate[list.size()];
		list.toArray(predicates);
		return predicates;
	}
	
	public IPOPredicateSet[] getPredicateSets() throws RodinDBException {
		ArrayList<IRodinElement> list = getChildrenOfType(IPOPredicateSet.ELEMENT_TYPE);
		IPOPredicateSet[] predicateSets = new IPOPredicateSet[list.size()];
		list.toArray(predicateSets);
		return predicateSets;
	}

}

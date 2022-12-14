/*******************************************************************************
 * Copyright (c) 2006, 2013 ETH Zurich and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     ETH Zurich - initial API and implementation
 *******************************************************************************/
package org.eventb.internal.core.pog;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.eclipse.core.runtime.CoreException;
import org.eventb.core.ISCPredicateElement;
import org.eventb.core.ast.FormulaFactory;
import org.eventb.core.ast.ITypeEnvironment;
import org.eventb.core.ast.Predicate;
import org.eventb.core.pog.state.IPredicateTable;
import org.eventb.internal.core.tool.state.State;

/**
 * @author Stefan Hallerstede
 *
 */
public abstract class PredicateTable<PE extends ISCPredicateElement> 
extends State implements IPredicateTable<PE> {

	@Override
	public String toString() {
		return predicates.toString();
	}

	protected final List<PE> predicateElements;
	protected final List<Predicate> predicates;

	public PredicateTable(
			PE[] elements, 
			ITypeEnvironment typeEnvironment, 
			FormulaFactory factory) throws CoreException {
		predicateElements = Arrays.asList(elements);
		predicates = new ArrayList<Predicate>(elements.length);
		
		for (PE element : elements) {
			predicates.add(element.getPredicate(typeEnvironment));
		}
	}

	/* (non-Javadoc)
	 * @see org.eventb.core.pog.IPredicateTable#getElements()
	 */
	@Override
	public List<PE> getElements() {
		return predicateElements;
	}

	/* (non-Javadoc)
	 * @see org.eventb.core.pog.IPredicateTable#getPredicates()
	 */
	@Override
	public List<Predicate> getPredicates() {
		return predicates;
	}

	@Override
	public int indexOfPredicate(Predicate predicate) {
		return predicates.indexOf(predicate);
	}
	
}

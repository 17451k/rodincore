/*******************************************************************************
 * Copyright (c) 2006 ETH Zurich.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/

package org.eventb.internal.pp.core.elements.terms;

import java.util.Map;

import org.eventb.internal.pp.core.elements.Sort;

/**
 * Abstract base class for simple terms.
 *
 * @author François Terrier
 *
 */
public abstract class SimpleTerm extends Term {

	protected SimpleTerm(Sort sort, int priority, int hashCode, int hashCodeWithDifferentVariables) {
		super(sort, priority, hashCode, hashCodeWithDifferentVariables);
	}
	
	@Override
	protected <S extends Term> Term substitute(Map<SimpleTerm, S> map) {
		return map.containsKey(this)?map.get(this):this;
	}
	
	@Override
	public boolean contains(SimpleTerm variable) {
		return variable.equals(this);
	}
	
}

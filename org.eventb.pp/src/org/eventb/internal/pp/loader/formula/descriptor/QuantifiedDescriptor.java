/*******************************************************************************
 * Copyright (c) 2006, 2012 ETH Zurich and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     ETH Zurich - initial API and implementation
 *******************************************************************************/
package org.eventb.internal.pp.loader.formula.descriptor;

import org.eventb.internal.pp.loader.predicate.IContext;

public class QuantifiedDescriptor extends IndexedDescriptor {


	public QuantifiedDescriptor(IContext context, int index /* , List<TermSignature> definingTerms */) {
		super(context, index);
	}
	
	@Override
	public String toString() {
		return "Q"+index;
	}
	
}

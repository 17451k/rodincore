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

import java.util.List;

import org.eventb.internal.pp.loader.predicate.IContext;
import org.eventb.internal.pp.loader.predicate.IIntermediateResult;

public abstract class IndexedDescriptor extends LiteralDescriptor {

	protected int index;
	
	public int getIndex() {
		return index;
	}

	public IndexedDescriptor(IContext context, int index) {
		super(context);
		this.index = index;
	}

	public IndexedDescriptor(IContext context, List<IIntermediateResult> termList, int index) {
		super(context, termList);
		this.index = index;
	}
	
}

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
package org.eventb.core.basis;

import org.eventb.core.ISCWitness;
import org.rodinp.core.IInternalElementType;
import org.rodinp.core.IRodinElement;

/**
 * Implementation of Event-B SC witnesses as an extension of the Rodin database.
 * <p>
 * This class is intended to be subclassed by clients that want to extend this
 * internal element type.
 * </p>
 * <p>
 * This class should not be used in any other way than subclassing it in a
 * database extension. In particular, clients should not use it, but rather use
 * its associated interface <code>ISCWitness</code>.
 * </p>
 * 
 * @author Stefan Hallerstede
 *
 * @since 1.0
 * @noinstantiate This class is not intended to be instantiated by clients.
 */
public class SCWitness extends SCPredicateElement implements ISCWitness {

	/**
	 *  Constructor used by the Rodin database. 
	 */
	public SCWitness(String name, IRodinElement parent) {
		super(name, parent);
	}

	/* (non-Javadoc)
	 * @see org.rodinp.core.IRodinElement#getElementType()
	 */
	@Override
	public IInternalElementType<ISCWitness> getElementType() {
		return ELEMENT_TYPE;
	}

}

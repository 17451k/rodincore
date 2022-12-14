/*******************************************************************************
 * Copyright (c) 2011, 2013 Systerel and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Systerel - initial API and implementation
 *******************************************************************************/
package org.rodinp.core.emf.api.itf;

import org.rodinp.core.IInternalElement;
import org.rodinp.core.IRodinElement;
import org.rodinp.core.RodinDBException;
import org.rodinp.core.emf.lightcore.LightElement;
import org.rodinp.core.emf.lightcore.sync.SynchroUtils;

/**
 * A set of utility methods for 'IL' elements.
 * 
 * @author "Thomas Muller"
 * @noextend This class is not intended to be subclassed by clients.
 * @noinstantiate This class is not intended to be instantiated by clients.
 */
public class ILUtils {
	
	private ILUtils() {
		//no instance
	}
	
	public static IInternalElement getNextSibling(ILElement parent,
			IInternalElement element) throws RodinDBException {
		return SynchroUtils.getNextSibling((LightElement) parent, element);
	}
	
	public static ILElement findElement(IRodinElement toFind, ILElement root) {
		return SynchroUtils.findElement(toFind, root);
	}

}

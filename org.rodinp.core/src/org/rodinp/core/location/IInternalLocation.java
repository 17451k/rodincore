/*******************************************************************************
 * Copyright (c) 2008, 2009 Systerel and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     Systerel - initial API and implementation
 *******************************************************************************/
package org.rodinp.core.location;

import org.rodinp.core.IInternalElement;

/**
 * Common protocol for specifying a location in a {@link IInternalElement} in
 * the Rodin database.
 * <p>
 * This interface is not intended to be implemented by clients.
 * </p>
 * 
 * @see IRodinLocation
 * @see IAttributeLocation
 * @see IAttributeSubstringLocation
 * 
 * @author Nicolas Beauger
 * @author Laurent Voisin
 */
public interface IInternalLocation extends IRodinLocation {

	/**
	 * Returns the element containing this location.
	 * 
	 * @return the element containing this location
	 */
	IInternalElement getElement();

}

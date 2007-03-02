/*******************************************************************************
 * Copyright (c) 2006 ETH Zurich.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.eventb.core.pog;

import org.rodinp.core.IRodinElement;

/**
 * Common protocol for source elements associated with proof obligations.
 * <p>
 * This interface is not intended to be implemented by clients.
 * </p> 
 * 
 * @author Stefan Hallerstede
 *
 */
public interface IPOGSource {
	
	public String getRole();
	
	public IRodinElement getSource();

}

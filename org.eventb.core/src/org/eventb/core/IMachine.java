/*******************************************************************************
 * Copyright (c) 2005 ETH Zurich.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.eventb.core;

import org.rodinp.core.IRodinFile;
import org.rodinp.core.RodinDBException;

/**
 * Common protocol for Event-B (unchecked) machines.
 * <p>
 * This interface is not intended to be implemented by clients.
 * </p>
 * 
 * @author Laurent Voisin
 */
public interface IMachine extends IRodinFile {
	public String ELEMENT_TYPE = EventBPlugin.PLUGIN_ID + ".machine"; //$NON-NLS-1$

	public IVariable[] getVariables() throws RodinDBException;
	public ITheorem[] getTheorems() throws RodinDBException;
	public IInvariant[] getInvariants() throws RodinDBException;
	public IEvent[] getEvents() throws RodinDBException;
	public ISees[] getSees() throws RodinDBException;
}

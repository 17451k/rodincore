/*******************************************************************************
 * Copyright (c) 2006 ETH Zurich.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.eventb.core.sc.symbolTable;

import org.eclipse.core.runtime.CoreException;

/**
 * <code>IVariableSymbolInfo</code> is an {@link org.eventb.core.sc.symbolTable.IIdentifierSymbolInfo} that
 * corresponds to an Event-B variable.
 * <p>
 * This interface is not intended to be implemented by clients.
 * </p>
 * @author Stefan Hallerstede
 *
 */
public interface IVariableSymbolInfo extends IIdentifierSymbolInfo {

	/**
	 * Marks the variable as "forbidden".
	 * 
	 * @throws CoreException if the symbol info is immutable
	 */
	public void setForbidden() throws CoreException;
	
	/**
	 * Returns whether the variable is forbidden.
	 * 
	 * @return whether the variable is forbidden
	 */
	boolean isForbidden();
	
	/**
	 * Marks the variable as concrete. This method can be called even
	 * when the symbol info is immutable.
	 */
	public void setConcrete();
	
	/**
	 * Returns whether this is a variable of the current machine.
	 * 
	 * @return whether this is a variable of the current machine
	 */
	boolean isConcrete();
	
	/**
	 * Returns whether the variable symbol is "local".
	 * @return whether the variable symbol is "local"
	 */
	boolean isLocal();
	
	/**
	 * Mark this the variable as "fresh", i.e. its name was not used before.
	 * 
	 * @throws CoreException if this symbol is not mutable
	 */
	void setFresh() throws CoreException;
	
	/**
	 * Returns whether the variable symbol is "fresh".
	 * @return whether the variable symbol is "fresh"
	 */
	boolean isFresh();
	
}

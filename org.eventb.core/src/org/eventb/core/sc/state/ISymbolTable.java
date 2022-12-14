/*******************************************************************************
 * Copyright (c) 2008, 2014 University of Southampton and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     University of Southampton - initial API and implementation
 *     Systerel - add tryPutSymbolInfo
 *******************************************************************************/
package org.eventb.core.sc.state;

import org.eclipse.core.runtime.CoreException;
import org.rodinp.core.IInternalElement;
import org.rodinp.core.IInternalElementType;

/**
 * Symbol table template used in various forms in the static checker.
 *
 * @see ILabelSymbolTable
 * @see IIdentifierSymbolTable
 * @author Stefan Hallerstede
 * @since 1.1
 * @noimplement This interface is not intended to be implemented by clients.
 */
public interface ISymbolTable<E extends IInternalElement, T extends IInternalElementType<? extends E>, I extends ISymbolInfo<E, T>> {

	/**
	 * Returns whether a symbol is contained in the symbol table.
	 * 
	 * @param symbol
	 *            the symbol to be looked up.
	 * @return <code>true</code> if the symbol is present, <code>false</code>
	 *         otherwise
	 */
	boolean containsKey(String symbol);

	/**
	 * Returns the symbol info for a symbol. If the symbol is not present
	 * <code>null</code> is returned. If this is stacked symbol table, it is
	 * searched from top to bottom.
	 * 
	 * @param symbol
	 *            the symbol corresponding to the symbol info
	 * @return the symbol info
	 */
	I getSymbolInfo(String symbol);

	/**
	 * Inserts a symbol info into the symbol table. Multiple insertions are not
	 * allowed. The key of the corresponding symbol is
	 * <code>symbolInfo.getSymbol()</code>. If the symbol table is stacked, the
	 * symbol is inserted in the top level symbol table.
	 * <p>
	 * This method should be used only when no exception should be raised.
	 * Otherwise, use the alternative method
	 * {@link #tryPutSymbolInfo(ISymbolInfo)}.
	 * </p>
	 * 
	 * @param symbolInfo
	 *            the symbol info for the symbol
	 * @throws CoreException
	 *             if the symbol is already present in the symbol table
	 * 
	 * @see ISymbolInfo#getSymbol()
	 */
	void putSymbolInfo(I symbolInfo) throws CoreException;

	/**
	 * Attempts to insert a symbol info into the symbol table and tells if it
	 * succeeded. Multiple insertions are not allowed. The key of the
	 * corresponding symbol is <code>symbolInfo.getSymbol()</code>. If the
	 * symbol table is stacked, the symbol is inserted in the top level symbol
	 * table.
	 * <p>
	 * This is similar to {@link #putSymbolInfo(ISymbolInfo)} but
	 * returning a Boolean instead of throwing an exception.
	 * </p>
	 * 
	 * @param symbolInfo
	 *            the symbol info for the symbol
	 * @return <code>true</code> if the symbol has been added to the table,
	 *         <code>false</code> in case of collision
	 * @see ISymbolInfo#getSymbol()
	 * @since 3.1
	 */
	boolean tryPutSymbolInfo(I symbolInfo);

	/**
	 * Turns all symbols of the symbol table immutable
	 */
	void makeImmutable();
}

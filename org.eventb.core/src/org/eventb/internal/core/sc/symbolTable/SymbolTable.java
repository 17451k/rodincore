/*******************************************************************************
 * Copyright (c) 2006 ETH Zurich.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.eventb.internal.core.sc.symbolTable;

import java.util.Collection;
import java.util.Collections;
import java.util.Hashtable;
import java.util.Set;
import java.util.TreeSet;

import org.eclipse.core.runtime.CoreException;
import org.eventb.core.sc.symbolTable.ISymbolInfo;
import org.eventb.core.sc.symbolTable.ISymbolTable;
import org.eventb.internal.core.Util;
import org.eventb.internal.core.tool.state.State;

/**
 * @author Stefan Hallerstede
 *
 */
public abstract class SymbolTable<I extends ISymbolInfo> extends State implements ISymbolTable<I> {

	private final Hashtable<String, I> table;
	
	// the tableValues variable is a cache that holds the value of table.values()
	private final Set<I> tableValues;
	
	public SymbolTable(int size) {
		table = new Hashtable<String, I>(size);
		tableValues = new TreeSet<I>();
	}
	
	public boolean containsKey(String symbol) {
		return table.containsKey(symbol);
	}
	
	public I getSymbolInfo(String symbol) {
		return table.get(symbol);
	}
	
	protected void throwSymbolConflict() throws CoreException {
		throw Util.newCoreException("Attempt to insert symbol into symbol table more than once");
	}
	
	public void putSymbolInfo(I symbolInfo) throws CoreException {
		
		String key = symbolInfo.getSymbol();
		
		I ocell = table.put(key, symbolInfo);
		if (ocell != null) {
			// revert to old symbol table and throw exception
			table.put(key, ocell);
			throwSymbolConflict();
		}
		tableValues.add(symbolInfo);
	}

	@Override
	public void makeImmutable() {
		for(I info : tableValues) {
			info.makeImmutable();
		}
		super.makeImmutable();
	}

	public int size() {
		return tableValues.size();
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return table.toString();
	}
	
	public I getSymbolInfoFromTop(String symbol) {
		return getSymbolInfo(symbol);
	}

	public ISymbolTable<I> getParentTable() {
		return null;
	}

	public Collection<I> getSymbolInfosFromTop() {
		return Collections.unmodifiableSet(tableValues);
	}

}

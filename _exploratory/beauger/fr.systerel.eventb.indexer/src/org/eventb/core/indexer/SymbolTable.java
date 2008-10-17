/*******************************************************************************
 * Copyright (c) 2008 Systerel and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     Systerel - initial API and implementation
 *******************************************************************************/
package org.eventb.core.indexer;

import java.util.HashMap;
import java.util.Map;

import org.eventb.core.ast.FormulaFactory;
import org.eventb.core.ast.FreeIdentifier;
import org.rodinp.core.index.IDeclaration;

public class SymbolTable {

	private final Map<String, IDeclaration> table;
	private final SymbolTable prev;

	SymbolTable(SymbolTable prev) {
		this.table = new HashMap<String, IDeclaration>();
		this.prev = prev;
	}

	public IDeclaration lookup(String symbol) {
		final IDeclaration declaration = table.get(symbol);
		if (declaration == null && prev != null) {
			return prev.lookup(symbol);
		}
		return declaration;
	}

	/**
	 * Puts the given declaration in this SymbolTable, at the closest level.
	 * <p>
	 * It is possible that a declaration with the same name already exists at
	 * the same level of the table. In this case the declaration is not put and
	 * the previous one is removed.
	 * </p>
	 * 
	 * @param declaration
	 *            the declaration to add
	 */
	// TODO maybe return a boolean false if an association already exists
	public void put(IDeclaration declaration) {
		final String name = declaration.getName();

		final IDeclaration previousDecl = table.put(name, declaration);

		if (previousDecl != null) {
			table.remove(name);
		}
	}

	public void clear() {
		table.clear();
	}

	public IDeclaration lookUpper(String symbol) {
		if (prev == null) {
			return null;
		}
		return prev.lookup(symbol);
	}

	/**
	 * Extracts an IdentTable containing only the given FreeIdentifiers, when
	 * they are found.
	 * 
	 * @param idents
	 * @param identTable
	 */
	public void addToIdentTable(FreeIdentifier[] idents, IdentTable identTable) {
		for (FreeIdentifier ident : idents) {
			if (ident.isPrimed()) {
				ident = ident.withoutPrime(FormulaFactory.getDefault());
			}
			final IDeclaration declaration = lookup(ident.getName());
			if (declaration != null) {
				identTable.put(ident, declaration);
			}
		}
	}

	/**
	 * @param abstSymbolTable
	 */
	public void putAll(SymbolTable abstSymbolTable) {
		for (IDeclaration declaration : abstSymbolTable.table.values()) {
			this.put(declaration);
		}
		if (abstSymbolTable.prev != null) {
			putAll(abstSymbolTable.prev);
		}
	}
}

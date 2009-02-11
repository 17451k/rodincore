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
package org.rodinp.internal.core.indexer.tables;

import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.rodinp.core.IRodinFile;
import org.rodinp.core.indexer.IDeclaration;

public class ExportTable implements IExportTable {

	Map<IRodinFile, Set<IDeclaration>> table;

	public ExportTable() {
		table = new HashMap<IRodinFile, Set<IDeclaration>>();
	}

	public Set<IDeclaration> get(IRodinFile file) {
		final Set<IDeclaration> declarations = table.get(file);
		if (declarations == null) {
			return Collections.emptySet();
		}
		return new HashSet<IDeclaration>(declarations);
	}

	// Overwrites any previous mapping from the given file to the element,
	// and from the given element to the name.
	public void add(IRodinFile file, IDeclaration declaration) {
		Set<IDeclaration> declarations = table.get(file);
		if (declarations == null) {
			declarations = new HashSet<IDeclaration>();
			table.put(file, declarations);
		}
		declarations.add(declaration);
	}

	public void remove(IRodinFile file) {
		table.remove(file);
	}

	public void clear() {
		table.clear();
	}

	public Set<IRodinFile> files() {
		return new HashSet<IRodinFile>(table.keySet());
	}
}

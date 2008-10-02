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
package org.rodinp.internal.core.index;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.rodinp.core.IRodinFile;
import org.rodinp.core.IRodinProject;
import org.rodinp.core.index.IDeclaration;
import org.rodinp.internal.core.index.tables.ExportTable;
import org.rodinp.internal.core.index.tables.FileTable;
import org.rodinp.internal.core.index.tables.NameTable;
import org.rodinp.internal.core.index.tables.RodinIndex;
import org.rodinp.internal.core.index.tables.TotalOrder;

public class ProjectIndexManager {

	private final IRodinProject project;

	private final FileIndexingManager fim;

	private final IndexersRegistry indexersManager;

	private final RodinIndex index;

	private final FileTable fileTable;

	private final NameTable nameTable;

	private final ExportTable exportTable;

	private final TotalOrder<IRodinFile> order;

	public ProjectIndexManager(IRodinProject project, FileIndexingManager fim,
			IndexersRegistry indManager) {
		this.project = project;
		this.fim = fim;
		this.indexersManager = indManager;
		this.index = new RodinIndex();
		this.fileTable = new FileTable();
		this.nameTable = new NameTable();
		this.exportTable = new ExportTable();
		this.order = new TotalOrder<IRodinFile>();
	}

	public void launchIndexing() {

		while (order.hasNext()) {
			final IRodinFile file = order.next();

			final Set<IDeclaration> fileImports = computeImports(file);
			final IndexingToolkit indexingToolkit = new IndexingToolkit(file,
					index, fileTable, nameTable, exportTable, fileImports);

			if (file.exists()) {
				fim.doIndexing(file, indexingToolkit);

				if (indexingToolkit.mustReindexDependents()) {
					order.setToIterSuccessors();
				}
			} else {
				order.setToIterSuccessors();
				order.remove();
				indexingToolkit.clean();
			}
		}
		order.end();
	}

	/**
	 * Returns true if the file was actually set to index, false it the file was
	 * not indexable.
	 * 
	 * @param file
	 * @return whether the file was actually set to index.
	 */
	public boolean setToIndex(IRodinFile file) {
		if (!file.getRodinProject().equals(project)) {
			throw new IllegalArgumentException(file
					+ " should be indexed in project " + project);
		}

		if (!indexersManager.isIndexable(file.getElementType())) {
			return false;
		}
		final IRodinFile[] dependFiles = fim.getDependencies(file);

		order.setPredecessors(file, dependFiles);
		order.setToIter(file);

		return true;
	}

	public FileTable getFileTable() {
		return fileTable;
	}

	public NameTable getNameTable() {
		return nameTable;
	}

	public ExportTable getExportTable() {
		return exportTable;
	}

	public RodinIndex getIndex() {
		return index;
	}

	private Set<IDeclaration> computeImports(IRodinFile file) {
		final Set<IDeclaration> result = new HashSet<IDeclaration>();
		final List<IRodinFile> fileDeps = order.getPredecessors(file);

		for (IRodinFile f : fileDeps) {
			result.addAll(exportTable.get(f));
		}
		return result;
	}

}

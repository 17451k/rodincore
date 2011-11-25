/*******************************************************************************
 * Copyright (c) 2011 Systerel and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Systerel - initial API and implementation
 *******************************************************************************/
package org.eventb.internal.ui.prooftreeui;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jface.viewers.ILabelProviderListener;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerComparator;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eventb.core.ast.ITypeEnvironment;
import org.eventb.core.ast.ITypeEnvironment.IIterator;
import org.eventb.core.seqprover.IProofTreeNode;

/**
 * A view that displays the type environment of the currently selected proof
 * tree node.
 * 
 * @author Nicolas Beauger
 * 
 */
public class TypeEnvView extends AbstractProofNodeView {

	private static class Ident {

		public final String name;
		public final String type;

		public Ident(String name, String type) {
			super();
			this.name = name;
			this.type = type;
		}

	}

	private static class TypeEnvLabelProvider implements ITableLabelProvider {

		public TypeEnvLabelProvider() {
			// avoid synthetic access
		}

		@Override
		public void addListener(ILabelProviderListener listener) {
			// nothing to do
		}

		@Override
		public void dispose() {
			// nothing to do
		}

		@Override
		public boolean isLabelProperty(Object element, String property) {
			return false;
		}

		@Override
		public void removeListener(ILabelProviderListener listener) {
			// nothing to do
		}

		@Override
		public Image getColumnImage(Object element, int columnIndex) {
			return null;
		}

		@Override
		public String getColumnText(Object element, int columnIndex) {
			if (!(element instanceof Ident)) {
				return null;
			}
			final Ident ident = (Ident) element;
			if (columnIndex == 0) {
				return ident.name;
			} else {
				return ident.type;
			}

		}

	}

	private static class TypeEnvContentProvider implements
			IStructuredContentProvider {

		public TypeEnvContentProvider() {
			// avoid synthetic access
		}

		@Override
		public void dispose() {
			// nothing to do
		}

		@Override
		public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
			// nothing to do
		}

		@Override
		public Object[] getElements(Object inputElement) {
			if (!(inputElement instanceof ITypeEnvironment)) {
				return null;
			}
			final ITypeEnvironment tEnv = (ITypeEnvironment) inputElement;
			final List<Ident> result = new ArrayList<Ident>();

			final IIterator iter = tEnv.getIterator();
			while (iter.hasNext()) {
				iter.advance();
				result.add(new Ident(iter.getName(), iter.getType().toString()));
			}
			return result.toArray(new Ident[result.size()]);
		}

	}

	private static class ColumnComparator extends ViewerComparator {
		
		private int column = 0;
		private boolean ascending = true;

		public ColumnComparator() {
			// avoid synthetic access
		}

		public int getColumn() {
			return column;
		}
		
		public void setColumn(int column) {
			this.column = column;
		}
		
		public boolean isAscending() {
			return ascending;
		}
		
		public void setAscending(boolean ascending) {
			this.ascending = ascending;
		}
		
		@Override
		public int compare(Viewer viewer, Object e1, Object e2) {
			final Ident ident1 = (Ident) e1;
			final Ident ident2 = (Ident) e2;
			final int ascendCompare;
			if (column == 0) {
				ascendCompare = ident1.name.compareTo(ident2.name);
			} else {
				ascendCompare = ident1.type.compareTo(ident2.type);
			}
			if (ascending) {
				return ascendCompare;
			} else {
				return -ascendCompare;
			}
		}
	}
	
	private static final String[] COLUMN_NAMES = new String[] { "Identifier",
			"Type" };

	TableViewer tableViewer;

	@Override
	protected void initializeControl(Composite parent, Font font) {
		tableViewer = new TableViewer(parent, SWT.FULL_SELECTION | SWT.FILL
				| SWT.H_SCROLL | SWT.V_SCROLL | SWT.BORDER);
		final ColumnComparator comparator = new ColumnComparator();
		tableViewer.setComparator(comparator);
		tableViewer.setLabelProvider(new TypeEnvLabelProvider());
		tableViewer.setContentProvider(new TypeEnvContentProvider());
		createColumns(font);
		initColumnSorting(comparator);
	}

	private void createColumns(Font font) {
		final Table table = tableViewer.getTable();
		table.setFont(font);
		table.setHeaderVisible(true);
		table.setLinesVisible(true);

		
		for (int i = 0; i < COLUMN_NAMES.length; i++) {
			final String columnName = COLUMN_NAMES[i];
			final TableColumn col = new TableColumn(table, SWT.WRAP);
			col.setText(columnName);
			col.pack();
		}
	}

	private void initColumnSorting(final ColumnComparator comparator) {
		final TableColumn[] columns = tableViewer.getTable().getColumns();
		
		for (int i = 0; i < columns.length; i++) {
			final int columnNumber = i;
			columns[i].addSelectionListener(new SelectionAdapter() {
				
				@Override
				public void widgetSelected(SelectionEvent e) {
					if (comparator.getColumn() == columnNumber) {
						// already sorting with this column's comparator: toggle
						// ascending.
						comparator.setAscending(!comparator.isAscending());
					} else {
						comparator.setColumn(columnNumber);
						comparator.setAscending(true);
					}
					tableViewer.refresh(false);
				}
			});
		}
	}

	@Override
	protected void refreshContents(IProofTreeNode node, Font font) {
		tableViewer.getTable().setFont(font);
		final ITypeEnvironment typeEnv = node.getSequent().typeEnvironment();
		tableViewer.setInput(typeEnv);
	}

}

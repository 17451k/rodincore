/*******************************************************************************
 * Copyright (c) 2008 Systerel and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License  v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Systerel - initial API and implementation
  *******************************************************************************/


package fr.systerel.internal.explorer.statistics;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;

public abstract class StatisticsColumn {

	private TableColumn column;
	
	
	protected void createTableColumn(Table table, String header) {
		column = new TableColumn(table, SWT.NONE);
		column.setText(header);
		column.pack();
	
	}
	
	public abstract String getLabel(IStatistics statistics);


	public int getIndex() {
		return column.getParent().indexOf(column);
	}

	public TableColumn getColumn() {
		return column;
	}


	
	public static class NameColumn extends StatisticsColumn {
		
		public NameColumn(Table table){
			createTableColumn(table, "Name");
		}

		@Override
		public String getLabel(IStatistics statistics) {
			return (statistics.getParentLabel());
		}
	}

	public static class TotalColumn extends StatisticsColumn {
		
		public TotalColumn(Table table){
			createTableColumn(table, "Total");
		}

		@Override
		public String getLabel(IStatistics statistics) {
			return Integer.toString(statistics.getTotal());
		}
	}

	public static class AutoColumn extends StatisticsColumn {
		
		public AutoColumn(Table table){
			createTableColumn(table, "Auto.");
		}

		@Override
		public String getLabel(IStatistics statistics) {
			return Integer.toString(statistics.getAuto());
		}
	}

	public static class ManualColumn extends StatisticsColumn {
		
		public ManualColumn(Table table){
			createTableColumn(table, "Manual.");
		}

		@Override
		public String getLabel(IStatistics statistics) {
			return Integer.toString(statistics.getManual());
		}
	}

	public static class ReviewedColumn extends StatisticsColumn {
		
		public ReviewedColumn(Table table){
			createTableColumn(table, "Reviewed");
		}

		@Override
		public String getLabel(IStatistics statistics) {
			return Integer.toString(statistics.getReviewed());
		}
	}

	public static class UndischargedColumn extends StatisticsColumn {
		
		public UndischargedColumn(Table table){
			createTableColumn(table, "Undischarged");
		}

		@Override
		public String getLabel(IStatistics statistics) {
			return Integer.toString(statistics.getUndischargedRest());
		}
	}
	
}

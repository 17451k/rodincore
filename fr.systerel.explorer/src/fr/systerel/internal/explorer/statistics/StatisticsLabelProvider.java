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

import org.eclipse.jface.viewers.ILabelProviderListener;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.swt.graphics.Image;


/**
 * This is a LabelProvider for the IStatistics is used for the overview
 * in the statistics tab. It doesn't show a label (name) for each line.
 *
 */
public class StatisticsLabelProvider implements ITableLabelProvider {

	private StatisticsView view;
	
	public StatisticsLabelProvider(StatisticsView view){
		this.view = view;
	}
	
	public Image getColumnImage(Object element, int columnIndex) {
		return null;
		
   	}

	public String getColumnText(Object element, int columnIndex) {
		if (element instanceof IStatistics) {
			IStatistics stats = (IStatistics) element;
			StatisticsColumn column = view.getOverviewColumn(columnIndex);
			if (column != null) {
				return column.getLabel(stats);
			}
		}
		return null;
	}

	public void addListener(ILabelProviderListener listener) {
		// do nothing
	}

	public void dispose() {
		// do nothing
	}

	public boolean isLabelProperty(Object element, String property) {
		// do nothing
		return false;
	}

	public void removeListener(ILabelProviderListener listener) {
		// do nothing
	}

}

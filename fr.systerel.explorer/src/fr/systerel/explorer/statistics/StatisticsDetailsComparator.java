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

package fr.systerel.explorer.statistics;

import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerComparator;

/**
 * This comparator can be used for sorting statistics by various criteria.
 * 
 */
public abstract class StatisticsDetailsComparator extends ViewerComparator {
	
	public static final boolean ASCENDING = true;
	protected boolean order = ASCENDING;
	
	/**
	 * Constructor argument values that indicate to sort items by name, total,
	 * manual., auto., reviewed or undischarged.
	 */
	public final static StatisticsDetailsComparator NAME = new StatisticsDetailsComparator() {

		@Override
		public int compare(IStatistics stats1, IStatistics stats2) {
			int ascending = stats1.getParentLabel().compareTo(stats2.getParentLabel());
			if (order == ASCENDING) {
				return ascending;
			} else return -ascending;
		}

	};

	public final static StatisticsDetailsComparator TOTAL = new StatisticsDetailsComparator() {

		@Override
		public int compare(IStatistics stats1, IStatistics stats2) {
			int ascending = stats1.getTotal() - stats2.getTotal();
			if (order == ASCENDING) {
				return ascending;
			} else return -ascending;
		}

	};

	public final static StatisticsDetailsComparator AUTO = new StatisticsDetailsComparator() {

		@Override
		public int compare(IStatistics stats1, IStatistics stats2) {
			int ascending = stats1.getAuto() - stats2.getAuto();
			if (order == ASCENDING) {
				return ascending;
			} else return -ascending;
		}

	};

	public final static StatisticsDetailsComparator MANUAL = new StatisticsDetailsComparator() {

		@Override
		public int compare(IStatistics stats1, IStatistics stats2) {
			int ascending =  stats1.getManual() - stats2.getManual();
			if (order == ASCENDING) {
				return ascending;
			} else return -ascending;
		}

	};

	public final static StatisticsDetailsComparator REVIEWED = new StatisticsDetailsComparator() {

		@Override
		public int compare(IStatistics stats1, IStatistics stats2) {
			int ascending = stats1.getReviewed() - stats2.getReviewed();
			if (order == ASCENDING) {
				return ascending;
			} else return -ascending;
		}

	};

	public final static StatisticsDetailsComparator UNDISCHARGED = new StatisticsDetailsComparator() {

		@Override
		public int compare(IStatistics stats1, IStatistics stats2) {
			int ascending = stats1.getUndischargedRest() - stats2.getUndischargedRest();
			if (order == ASCENDING) {
				return ascending;
			} else return -ascending;
		}

	};

	@Override
	public int compare(Viewer viewer, Object o1, Object o2) {
		if (o1 instanceof Statistics && o2 instanceof Statistics) {
			Statistics stats1 = (Statistics) o1;
			Statistics stats2 = (Statistics) o2;
			return compare(stats1, stats2);
		}
		return super.compare(viewer, o1, o2);

	}

	public abstract int compare(IStatistics stats1, IStatistics stats2);
	

	public void setOrder(boolean order) {
		this.order = order;
	}

	public boolean getOrder() {
		return order;
	}
	
}

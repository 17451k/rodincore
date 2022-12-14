/*******************************************************************************
 * Copyright (c) 2006, 2012 ETH Zurich and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     ETH Zurich - initial API and implementation
 *     Systerel - added cancellation tests
 *******************************************************************************/
package org.eventb.internal.pp.loader.clause;

import java.util.LinkedHashSet;

import org.eventb.internal.pp.CancellationChecker;
import org.eventb.internal.pp.loader.formula.AbstractLabelizableFormula;

/**
 * Label manager that stores all formula that must be labelized and
 * permits to retrieve them.
 *
 * @author François Terrier
 *
 */
public class LabelManager {

	private final CancellationChecker cancellation;
	
	private LinkedHashSet<AbstractLabelizableFormula<?>> toLabelizeNeg = new LinkedHashSet<AbstractLabelizableFormula<?>>();
	private LinkedHashSet<AbstractLabelizableFormula<?>> toLabelizePos = new LinkedHashSet<AbstractLabelizableFormula<?>>();
	
	public LabelManager(CancellationChecker cancellation) {
		this.cancellation = cancellation;
	}
	
	public void addLabel(AbstractLabelizableFormula<?> formula, boolean pos) {
		cancellation.check();
		if (pos) addLabel(formula, toLabelizePos);
		else addLabel(formula, toLabelizeNeg);
	}
	
	private void addLabel(AbstractLabelizableFormula<?> formula, LinkedHashSet<AbstractLabelizableFormula<?>> set) {
		cancellation.check();
		if (!set.contains(formula)) {
			if (ClauseBuilder.DEBUG) ClauseBuilder.debug("Adding "+formula+" to list of clauses that must be labelized");
			set.add(formula);
		}
	}
	
	public boolean hasLabel(AbstractLabelizableFormula<?> formula) {
		cancellation.check();
		return toLabelizePos.contains(formula) 
			|| toLabelizeNeg.contains(formula);
	}

	private int currentIndexPos = 0;
	private int currentIndexNeg = 0;
	
	private AbstractLabelizableFormula<?> nextFormula;
	private boolean isNextPositive;
	public void nextLabelizableFormula() {
		cancellation.check();
		nextFormula = null;
		if (currentIndexPos != toLabelizePos.size()) {
			nextFormula = getLabelizableFormula(toLabelizePos, currentIndexPos);
			currentIndexPos++;
			isNextPositive = true;
		}
		else if (currentIndexNeg != toLabelizeNeg.size()) {
			nextFormula = getLabelizableFormula(toLabelizeNeg, currentIndexNeg);
			currentIndexNeg++;
			isNextPositive = false;
		}
	}
	
	public AbstractLabelizableFormula<?> getNextFormula() {
		return nextFormula;
	}
	
	public boolean isNextPositive() {
		return isNextPositive;
	}
	
	// TODO optimize
	private AbstractLabelizableFormula<?> getLabelizableFormula(
			LinkedHashSet<AbstractLabelizableFormula<?>> set, int index) {
		cancellation.check();
		return set.toArray(new AbstractLabelizableFormula[set.size()])[index];
	}
	
}

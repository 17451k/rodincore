/*******************************************************************************
 * Copyright (c) 2006, 2012 ETH Zurich and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     ETH Zurich - initial API and implementation
 *     Systerel - cleanup
 *     Systerel - adapted to XProver v2 API
 *******************************************************************************/
package org.eventb.internal.pp.sequent;

import static java.util.Collections.emptyList;
import static java.util.Collections.singletonList;

import java.util.List;

import org.eventb.core.ast.Predicate;
import org.eventb.core.seqprover.transformer.ITrackedPredicate;
import org.eventb.pp.ITracer;

/**
 * Simple tracer for a sequent which is found to be trivially true during the
 * loading phase.
 */
public class SimpleTracer implements ITracer {

	private final List<Predicate> hypotheses;
	private final boolean goalNeeded;

	public SimpleTracer(ITrackedPredicate predicate) {
		if (predicate.isHypothesis()) {
			this.hypotheses = singletonList(predicate.getOriginal());
			this.goalNeeded = false;
		} else {
			this.hypotheses = emptyList();
			this.goalNeeded = true;
		}
	}

	@Override
	public List<Predicate> getNeededHypotheses() {
		return hypotheses;
	}

	@Override
	public boolean isGoalNeeded() {
		return goalNeeded;
	}

}
/*******************************************************************************
 * Copyright (c) 2006 ETH Zurich.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/

package org.eventb.internal.pp;

import java.util.HashSet;
import java.util.Set;

import org.eventb.core.ast.Predicate;
import org.eventb.core.seqprover.IProofMonitor;
import org.eventb.core.seqprover.xprover.XProverCall;
import org.eventb.core.seqprover.xprover.XProverInput;
import org.eventb.pp.IPPMonitor;
import org.eventb.pp.PPProof;
import org.eventb.pp.PPResult;
import org.eventb.pp.PPResult.Result;

/**
 * Implementation of {@link XProverCall} for PP.
 * 
 * @author François Terrier
 * 
 */
public class PPProverCall extends XProverCall implements IPPMonitor {

	private final int maxSteps;
	private PPResult result;

	public PPProverCall(XProverInput input, Iterable<Predicate> hypothesis,
			Predicate goal, IProofMonitor pm) {
		super(hypothesis, goal, pm);
		maxSteps = ((PPInput) input).getMaxSteps();
	}

	@Override
	public void cleanup() {
		// Run the garbage collector
		System.gc();
	}

	@Override
	public String displayMessage() {
		return "Predicate Prover";
	}

	@Override
	public boolean isValid() {
		if (result == null) {
			throw new IllegalStateException("isValid() called before run().");
		}
		return result.getResult() == Result.valid;
	}

	@Override
	public void run() {
		final PPProof prover = new PPProof(hypotheses, goal);
		prover.translate();
		prover.load();
		prover.prove(maxSteps, this);
		result = prover.getResult();
	}

	@Override
	public boolean isGoalNeeded() {
		if (result == null) {
			throw new IllegalStateException(
					"isGoalNeeded() called before run().");
		}
		return result.getTracer().isGoalNeeded();
	}

	@Override
	public Set<Predicate> neededHypotheses() {
		if (result == null) {
			throw new IllegalStateException(
					"neededHypotheses() called before run().");
		}
		return new HashSet<Predicate>(result.getTracer().getNeededHypotheses());
	}

	public boolean isCanceled() {
		return isCancelled();
	}

}

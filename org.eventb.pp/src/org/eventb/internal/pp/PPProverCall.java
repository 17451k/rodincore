package org.eventb.internal.pp;

import java.util.HashSet;
import java.util.Set;
import java.util.Timer;
import java.util.TimerTask;

import org.eventb.core.ast.Predicate;
import org.eventb.core.seqprover.IProofMonitor;
import org.eventb.core.seqprover.xprover.XProverCall;
import org.eventb.core.seqprover.xprover.XProverInput;
import org.eventb.pp.PPProof;
import org.eventb.pp.PPResult.Result;

public class PPProverCall extends XProverCall {

	private PPProof prover; 
	private int maxSteps;
	
	public PPProverCall(XProverInput input, Iterable<Predicate> hypothesis, Predicate goal, IProofMonitor pm) {
		super(hypothesis,goal,pm);
		
		maxSteps = ((PPInput)input).getMaxSteps();
		this.prover = new PPProof(hypotheses,this.goal);
	}
	
	@Override
	public void cleanup() {
//		prover = null;
	}

	@Override
	public String displayMessage() {
		return "Predicate Prover";
	}

	@Override
	public boolean isValid() {
		return prover.getResult().getResult()==Result.valid;
	}

	private static final long DEFAULT_PERIOD = 317;

	@Override
	public void run() {
		final Thread thread = Thread.currentThread();
		
		Timer timer = new Timer(true);
		timer.schedule(new TimerTask() {
			@Override
			public void run() {
				if (isCancelled()) {
					thread.interrupt();
					cancel();
				}
			}
		}, DEFAULT_PERIOD, DEFAULT_PERIOD);
		
		prover.translate();
		prover.load();
		prover.prove(maxSteps);
	}
	
	@Override
	public boolean isGoalNeeded() {
		return prover.getResult().getTracer().isGoalNeeded();
	}

	@Override
	public Set<Predicate> neededHypotheses() {
		return new HashSet<Predicate>(prover.getResult().getTracer().getOriginalPredicates());
	}

}

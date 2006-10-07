package org.eventb.core.seqprover.reasonerExtentionTests;

import org.eventb.core.seqprover.IProofMonitor;
import org.eventb.core.seqprover.IProofRule;
import org.eventb.core.seqprover.IProverSequent;
import org.eventb.core.seqprover.IReasonerInput;
import org.eventb.core.seqprover.IReasonerOutput;
import org.eventb.core.seqprover.Lib;
import org.eventb.core.seqprover.ProverFactory;
import org.eventb.core.seqprover.IProofRule.IAnticident;
import org.eventb.core.seqprover.tactics.BasicTactics;
import org.eventb.core.seqprover.tactics.ITactic;
import org.eventb.core.seqprover.tests.SequentProverTests;

public class TrueGoal extends EmptyInputReasoner{
	
	public static String REASONER_ID = SequentProverTests.PLUGIN_ID + ".trueGoal";
	
	public String getReasonerID() {
		return REASONER_ID;
	}
	
	public IReasonerOutput apply(IProverSequent seq, IReasonerInput input,
			IProofMonitor pm) {
	
		if (! (seq.goal().equals(Lib.True)))
			return ProverFactory.reasonerFailure(this,input,"Goal is not a tautology");
		
		IProofRule reasonerOutput = ProverFactory.makeProofRule(
				this,input,
				seq.goal(),"⊤ goal",
				new IAnticident[0]);
		
		return reasonerOutput;
	}
	
	public ITactic asTactic(){
		return BasicTactics.reasonerTac(this,new EmptyInput());
	}

}

package org.eventb.core.seqprover.reasonerExtentionTests;

import org.eventb.core.seqprover.IReasonerInput;
//import org.eventb.core.seqprover.ITactic;
import org.eventb.core.seqprover.reasonerInputs.EmptyInput;
import org.eventb.core.seqprover.tests.TestLib;

//import com.b4free.rodin.core.B4freeCore;

public class HypOrTests extends AbstractReasonerTests {

	private static final IReasonerInput input = new EmptyInput();

	@Override
	public String getReasonerID() {
		return "org.eventb.core.seqprover.hypOr";
	}

	@Override
	public SuccessfullReasonerApplication[] getSuccessfulReasonerApplications() {
		return new SuccessfullReasonerApplication[] {
				new SuccessfullReasonerApplication(TestLib
						.genSeq(" x = 1 |- x = 2 ∨ x = 1 ∨ x = 3 "), input),
				new SuccessfullReasonerApplication(TestLib
						.genSeq(" x = 1 |- x = 2 ∨ x = 1 ∨ x = 3 "), input,
						"[]"),
				new SuccessfullReasonerApplication(TestLib
						.genSeq(" x = 1 |- x = 1 ∨ x = 2 ∨ x = 3 "), input),
				new SuccessfullReasonerApplication(TestLib
						.genSeq(" x = 1 |- x = 1 ∨ x = 2 ∨ x = 3 "), input,
						"[]"),
				new SuccessfullReasonerApplication(TestLib
						.genSeq(" x = 1 |- x = 2 ∨ x = 3 ∨ x = 1 "), input),
				new SuccessfullReasonerApplication(TestLib
						.genSeq(" x = 1 |- x = 2 ∨ x = 3 ∨ x = 1 "), input,
						"[]")				
		};
	}

	@Override
	public UnsuccessfullReasonerApplication[] getUnsuccessfullReasonerApplications() {
		return new UnsuccessfullReasonerApplication[] {
				new UnsuccessfullReasonerApplication(TestLib
						.genSeq(" x = 1 |- x = 2"), input),
				new UnsuccessfullReasonerApplication(TestLib
						.genSeq(" x = 1 |- x = 2 "), input,
						"Goal is not a disjunctive predicate"),			
				new UnsuccessfullReasonerApplication(TestLib
						.genSeq(" x = 1 |- x = 2 ∨ x = 4 ∨ x = 3"), input),
				new UnsuccessfullReasonerApplication(TestLib
						.genSeq(" x = 1 |- x = 2 ∨ x = 4 ∨ x = 3 "), input,
						"Hypotheses contain no disjunct in goal")
		};
	}

//	@Override
//	public ITactic getJustDischTactic() {
//		return B4freeCore.externalPP(false);
//	}

}

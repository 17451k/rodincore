package org.eventb.core.seqprover.reasonerExtentionTests;

import org.eventb.core.seqprover.IReasonerInput;
import org.eventb.core.seqprover.ITactic;
import org.eventb.core.seqprover.eventbExtensions.AutoTactics;
import org.eventb.core.seqprover.reasonerInputs.EmptyInput;
import org.eventb.core.seqprover.tests.TestLib;

/**
 * This class demonstrates how to extend {@link AbstractReasonerTests} to test a particular 
 * registered reasoner implementation (in this case, {@link TrueGoal}).
 * 
 * @author Farhad Mehta
 *
 */
public class ConcreteReasonerTestsExample extends AbstractReasonerTests {

	private static final IReasonerInput input = new EmptyInput();

	@Override
	public String getReasonerID() {
		return "org.eventb.core.seqprover.tests.trueGoal";
	}
	
	@Override
	public ITactic getJustDischTactic() {
		return new AutoTactics.TrueGoalTac();
	}

	@Override
	public SuccessfullReasonerApplication[] getSuccessfulReasonerApplications() {
		return new SuccessfullReasonerApplication[]{
				new SuccessfullReasonerApplication(TestLib.genSeq(" ⊤ |- ⊤ "), input),
				new SuccessfullReasonerApplication(TestLib.genSeq(" ⊤ |- ⊤ "), input, "[]")
		};
	}

	@Override
	public UnsuccessfullReasonerApplication[] getUnsuccessfullReasonerApplications() {
		return new UnsuccessfullReasonerApplication[]{
				new UnsuccessfullReasonerApplication(TestLib.genSeq(" ⊤ |- ⊥ "), input),
				new UnsuccessfullReasonerApplication(TestLib.genSeq(" ⊤ |- ⊥ "), input, "Goal is not a tautology")
		};
	}

}

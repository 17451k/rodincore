package org.eventb.pp;

import org.eventb.core.seqprover.IReasonerInput;
import org.eventb.core.seqprover.reasonerExtentionTests.AbstractReasonerTests;
import org.eventb.core.seqprover.tests.TestLib;
import org.eventb.internal.pp.PPInput;

//import com.b4free.rodin.core.B4freeCore;

public class ReasonerExtensionTests extends AbstractReasonerTests {

	private static final IReasonerInput input = new PPInput(true,3000,1000);

	@Override
	public String getReasonerID() {
		return "org.eventb.pp.pp";
	}

	@Override
	public SuccessfullReasonerApplication[] getSuccessfulReasonerApplications() {
		return new SuccessfullReasonerApplication[] {
				new SuccessfullReasonerApplication(TestLib
						.genSeq(" x = 1 |- x = 1 "), input,
						"[]"),
				new SuccessfullReasonerApplication(TestLib
						.genSeq(" 1∈P |- 1∈P "), input,
						"[]")				
		};
	}

	@Override
	public UnsuccessfullReasonerApplication[] getUnsuccessfullReasonerApplications() {
		return new UnsuccessfullReasonerApplication[] {
				new UnsuccessfullReasonerApplication(TestLib
						.genSeq(" x = 1 |- x = 2"), input,"Failed"),
				new UnsuccessfullReasonerApplication(TestLib
						.genSeq(" 1∈P |- 2∈P "), input,"Failed")
		};
	}

//	@Override
//	public ITactic getJustDischTactic() {
//		return B4freeCore.externalPP(false);
//	}

}

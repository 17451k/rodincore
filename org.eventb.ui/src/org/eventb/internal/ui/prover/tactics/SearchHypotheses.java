package org.eventb.internal.ui.prover.tactics;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eventb.core.ast.Predicate;
import org.eventb.core.pm.IUserSupport;
import org.eventb.ui.prover.IProofCommand;
import org.rodinp.core.RodinDBException;

public class SearchHypotheses implements IProofCommand {

	public void apply(IUserSupport us, Predicate hyp, String [] inputs, IProgressMonitor monitor)
			throws RodinDBException {
		us.searchHyps(inputs[0]);
	}

	public boolean isApplicable(IUserSupport us, Predicate hyp, String input) {
		return (us.getCurrentPO() != null && us.getCurrentPO().getCurrentNode() != null);
	}

}

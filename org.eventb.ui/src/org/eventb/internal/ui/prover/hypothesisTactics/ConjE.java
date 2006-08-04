package org.eventb.internal.ui.prover.hypothesisTactics;

import org.eventb.core.prover.IProofTreeNode;
import org.eventb.core.prover.sequent.Hypothesis;
import org.eventb.core.prover.tactics.ITactic;
import org.eventb.core.prover.tactics.Tactics;
import org.eventb.ui.prover.IHypothesisTactic;

public class ConjE implements IHypothesisTactic {

	public ITactic getTactic(IProofTreeNode node, Hypothesis hyp, String[] inputs) {
		return Tactics.conjD(hyp);
	}

	public boolean isApplicable(IProofTreeNode node, Hypothesis hyp) {
		return Tactics.conjD_applicable(hyp);
	}
	

}

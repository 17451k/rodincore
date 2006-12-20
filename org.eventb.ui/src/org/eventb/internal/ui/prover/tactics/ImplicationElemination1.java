package org.eventb.internal.ui.prover.tactics;

import org.eventb.core.ast.Predicate;
import org.eventb.core.seqprover.IProofTreeNode;
import org.eventb.core.seqprover.ITactic;
import org.eventb.core.seqprover.eventbExtensions.Tactics;
import org.eventb.ui.prover.ITacticProvider;

public class ImplicationElemination1 implements ITacticProvider {

	public ITactic getTactic(IProofTreeNode node, Predicate hyp,
			String[] inputs) {
		return Tactics.impE(hyp);
	}

	public boolean isApplicable(IProofTreeNode node, Predicate hyp,
			String input) {
		return Tactics.impE_applicable(hyp);
	}

}

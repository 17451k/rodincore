package org.eventb.internal.ui.prover.tactics;

import java.util.ArrayList;
import java.util.List;

import org.eventb.core.ast.IPosition;
import org.eventb.core.ast.Predicate;
import org.eventb.core.seqprover.IProofTreeNode;
import org.eventb.core.seqprover.ITactic;
import org.eventb.ui.prover.DefaultTacticProvider;

public class AutoProver extends DefaultTacticProvider {

	@Override
	@Deprecated
	public ITactic getTactic(IProofTreeNode node, Predicate hyp,
			IPosition position, String[] inputs) {
		return org.eventb.internal.core.pom.AutoProver.autoTactic();
	}

	@Override
	public List<IPosition> getApplicablePositions(IProofTreeNode node,
			Predicate hyp, String input) {
		if (node != null && node.isOpen())
			return new ArrayList<IPosition>();
		return null;
	}

}

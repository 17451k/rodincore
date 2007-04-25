package org.eventb.internal.ui.prover.tactics;

import java.util.ArrayList;
import java.util.List;

import org.eventb.core.ast.IPosition;
import org.eventb.core.ast.Predicate;
import org.eventb.core.seqprover.IProofTreeNode;
import org.eventb.core.seqprover.ITactic;
import org.eventb.core.seqprover.eventbExtensions.Tactics;
import org.eventb.ui.prover.DefaultTacticProvider;

public class ImplicationElemination1 extends DefaultTacticProvider {

	@Override
	public ITactic getTactic(IProofTreeNode node, Predicate hyp,
			IPosition position, String[] inputs) {
		return Tactics.impE(hyp);
	}

	@Override
	public List<IPosition> getApplicablePositions(IProofTreeNode node,
			Predicate hyp, String input) {
		if (Tactics.impE_applicable(hyp)) {
			List<IPosition> positions = new ArrayList<IPosition>();
			positions.add(IPosition.ROOT);
			return positions;
		}
		return null;
	}

}

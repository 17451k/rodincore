package org.eventb.internal.ui.prover.tactics;

import java.util.List;

import org.eventb.core.ast.IPosition;
import org.eventb.core.ast.Predicate;
import org.eventb.core.seqprover.IProofTreeNode;
import org.eventb.core.seqprover.ITactic;
import org.eventb.core.seqprover.eventbExtensions.Tactics;
import org.eventb.ui.prover.DefaultTacticProvider;
import org.eventb.ui.prover.ITacticProvider;

public class RemoveInclusionHyp extends DefaultTacticProvider implements
		ITacticProvider {

	@Override
	public ITactic getTactic(IProofTreeNode node, Predicate hyp,
			IPosition position, String[] inputs) {
		return Tactics.removeInclusion(hyp, position);
	}

	@Override
	public List<IPosition> getApplicablePositions(IProofTreeNode node,
			Predicate hyp, String input) {
		if (node != null) {
			List<IPosition> positions = Tactics.riGetPositions(hyp);
			if (positions.size() == 0)
				return null;
			return positions;
		}
		return null;
	}

}

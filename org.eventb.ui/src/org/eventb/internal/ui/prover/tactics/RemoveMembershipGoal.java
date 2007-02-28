package org.eventb.internal.ui.prover.tactics;

import java.util.List;

import org.eventb.core.ast.IPosition;
import org.eventb.core.ast.Predicate;
import org.eventb.core.seqprover.IProofTreeNode;
import org.eventb.core.seqprover.ITactic;
import org.eventb.core.seqprover.eventbExtensions.Tactics;
import org.eventb.ui.prover.DefaultTacticProvider;
import org.eventb.ui.prover.ITacticProvider;

public class RemoveMembershipGoal extends DefaultTacticProvider implements
		ITacticProvider {

	@Override
	public ITactic getTactic(IProofTreeNode node, Predicate hyp,
			IPosition position, String[] inputs) {
		return Tactics.removeMembership(null, position);
	}

	@Override
	public List<IPosition> getApplicablePositions(IProofTreeNode node,
			Predicate hyp, String input) {
		if (node != null) {
			List<IPosition> positions = Tactics.rmGetPositions(node
					.getSequent().goal());
			if (positions.size() == 0)
				return null;
			return positions;
		}
		return null;
	}

}

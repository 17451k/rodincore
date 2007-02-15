package org.eventb.internal.ui.prover.tactics;

import java.util.ArrayList;
import java.util.List;

import org.eventb.core.ast.IPosition;
import org.eventb.core.ast.Predicate;
import org.eventb.core.seqprover.IProofTreeNode;
import org.eventb.core.seqprover.ITactic;
import org.eventb.core.seqprover.eventbExtensions.Lib;
import org.eventb.core.seqprover.eventbExtensions.Tactics;
import org.eventb.ui.prover.DefaultTacticProvider;

public class ForallInstantiationHyp extends DefaultTacticProvider {

	List<IPosition> positions;

	@Override
	public ITactic getTactic(IProofTreeNode node, Predicate hyp,
			IPosition position, String[] inputs) {
		return Tactics.allD(hyp, inputs);
	}

	@Override
	public List<IPosition> getApplicablePositions(IProofTreeNode node,
			Predicate hyp, String input) {
		if (Tactics.allD_applicable(hyp)) {
			internalGetPositions(hyp);
			return positions;
		}
		return null;
	}

	private void internalGetPositions(Predicate hyp) {
		positions = new ArrayList<IPosition>();
		if (Lib.isUnivQuant(hyp))
			positions.add(IPosition.ROOT);
	}

}

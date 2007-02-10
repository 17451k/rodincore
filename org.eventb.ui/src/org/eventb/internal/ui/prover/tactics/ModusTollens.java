package org.eventb.internal.ui.prover.tactics;

import java.util.ArrayList;
import java.util.List;

import org.eventb.core.ast.FormulaFactory;
import org.eventb.core.ast.IPosition;
import org.eventb.core.ast.Predicate;
import org.eventb.core.seqprover.IProofTreeNode;
import org.eventb.core.seqprover.ITactic;
import org.eventb.core.seqprover.eventbExtensions.Lib;
import org.eventb.core.seqprover.eventbExtensions.Tactics;
import org.eventb.ui.prover.DefaultTacticProvider;

public class ModusTollens extends DefaultTacticProvider {

	private List<IPosition> positions;

	@Override
	public ITactic getTactic(IProofTreeNode node, Predicate hyp,
			IPosition position, String[] inputs) {
		return Tactics.modusTollens(hyp);
	}

	@Override
	public List<IPosition> getApplicablePositions(IProofTreeNode node,
			Predicate hyp, String input) {
		if (node != null && Lib.isImp(hyp)) {
			internalGetPositions(hyp);
			return positions;
		}
		return null;
	}

	private void internalGetPositions(Predicate hyp) {
		positions = new ArrayList<IPosition>();
		positions.add(FormulaFactory.getDefault().makePosition(""));
//		positions.add(hyp.getPosition(hyp.getSourceLocation()));
	}

}

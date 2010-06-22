package org.eventb.internal.ui.prover.tactics;

import java.util.List;

import org.eclipse.swt.graphics.Point;
import org.eventb.core.ast.AssociativePredicate;
import org.eventb.core.ast.IPosition;
import org.eventb.core.ast.Predicate;
import org.eventb.core.seqprover.IProofTreeNode;
import org.eventb.core.seqprover.ITactic;
import org.eventb.core.seqprover.eventbExtensions.Lib;
import org.eventb.core.seqprover.eventbExtensions.Tactics;
import org.eventb.ui.prover.DefaultTacticProvider;
import org.eventb.ui.prover.ITacticProvider;

public class DisjToImplHyp extends DefaultTacticProvider implements
		ITacticProvider {

	@Override
	@Deprecated
	public ITactic getTactic(IProofTreeNode node, Predicate hyp,
			IPosition position, String[] inputs) {
		return Tactics.disjToImpl(hyp, position);
	}

	@Override
	public List<IPosition> getApplicablePositions(IProofTreeNode node,
			Predicate hyp, String input) {
		if (node != null) {
			List<IPosition> positions = Tactics.disjToImplGetPositions(hyp);
			if (positions.size() == 0)
				return null;
			return positions;
		}
		return null;
	}

	@Override
	public Point getOperatorPosition(Predicate predicate, String predStr,
			IPosition position) {
		assert Lib.isDisj((Predicate) predicate.getSubFormula(position));
		AssociativePredicate subFormula = (AssociativePredicate) predicate
				.getSubFormula(position);
		Predicate[] children = subFormula.getChildren();
		assert children.length >= 2;
		Predicate first = children[0];
		Predicate second = children[1];
		// Return the operator between the first and second child
		return getOperatorPosition(predStr,
				first.getSourceLocation().getEnd() + 1, second
						.getSourceLocation().getStart());
	}
}

package org.eventb.core.seqprover;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Set;

import org.eventb.core.ast.FreeIdentifier;
import org.eventb.core.ast.ITypeEnvironment;
import org.eventb.core.ast.Predicate;
import org.eventb.core.seqprover.IHypAction.ISelectionHypAction;
import org.eventb.core.seqprover.IProofRule.IAntecedent;
import org.eventb.internal.core.seqprover.ProofRule;
import org.eventb.internal.core.seqprover.ProofTree;
import org.eventb.internal.core.seqprover.ProverSequent;
import org.eventb.internal.core.seqprover.ReasonerFailure;
import org.eventb.internal.core.seqprover.SelectionHypAction;
import org.eventb.internal.core.seqprover.ProofRule.Antecedent;

/**
 * Static class with factory methods required to construct various data structures
 * used in the sequent prover
 * 
 * @author Farhad Mehta
 *
 */
public final class ProverFactory {

	private ProverFactory() {
	}
	
	public static IReasonerFailure reasonerFailure(
			IReasoner generatedBy,
			IReasonerInput generatedUsing,
			String reason){
		return new ReasonerFailure(generatedBy,generatedUsing,reason);
	}
	
	public static IProofRule makeProofRule (
			IReasoner generatedBy,
			IReasonerInput generatedUsing,
			Predicate goal,
			Set<Predicate> neededHyps,
			Integer confidence,
			String display,
			IAntecedent... anticidents) {
		
		assert goal != null;
		assert anticidents != null;
		
		ProofRule proofRule = new ProofRule(generatedBy,generatedUsing,goal,neededHyps,confidence,display,anticidents);
		
		return proofRule;
	}
	
	public static IProofRule makeProofRule (
			IReasoner generatedBy,
			IReasonerInput generatedUsing,
			Predicate goal,
			Predicate neededHyp,
			String display,
			IAntecedent... anticidents) {
		return makeProofRule(generatedBy,generatedUsing,goal,Collections.singleton(neededHyp),null, display,anticidents);
	}
	
	public static IProofRule makeProofRule (
			IReasoner generatedBy,
			IReasonerInput generatedUsing,
			Predicate goal,
			String display,
			IAntecedent... anticidents) {
		return makeProofRule(generatedBy,generatedUsing,goal,null,null,display,anticidents);
	}
	
	public static IAntecedent makeAntecedent(
			Predicate goal,
			Set<Predicate> addedHyps,
			FreeIdentifier[] addedFreeIdents,
			List<IHypAction> hypAction){
		
		assert goal != null;
		
		IAntecedent antecedent = new Antecedent(goal, addedHyps, addedFreeIdents, hypAction);
		
		return antecedent;
		
	}

	public static IAntecedent makeAntecedent(
			Predicate goal,
			Set<Predicate> addedHyps,
			IHypAction hypAction) {
		
		if (hypAction != null){
			ArrayList<IHypAction> hypActions = new ArrayList<IHypAction>(1);
			hypActions.add(hypAction);
			return makeAntecedent(goal,addedHyps,null,hypActions);
		}
		return makeAntecedent(goal,addedHyps,null,null);
	}
	
	public static IAntecedent makeAntecedent(Predicate goal) {
		return makeAntecedent(goal,null,null,null);
	}

	public static IProverSequent makeSequent(ITypeEnvironment typeEnvironment,Set<Predicate> hyps,Predicate goal){
		return new ProverSequent(typeEnvironment,hyps,goal);
	}
	
	public static IProverSequent makeSequent(ITypeEnvironment typeEnvironment,Set<Predicate> hyps,Set<Predicate> selHyps,Predicate goal){
		return new ProverSequent(typeEnvironment,hyps,selHyps,goal);
	}

	/**
	 * Creates a new proof tree for the given sequent.
	 * 
	 * @param sequent
	 *            the sequent to prove
	 * @param origin
	 *            an object describing the origin of the sequent, might be
	 *            <code>null</code>
	 * @return a new proof tree for the given sequent
	 */
	public static IProofTree makeProofTree(IProverSequent sequent, Object origin) {
		return new ProofTree(sequent, origin);
	}

	
	public static IHypAction makeSelectHypAction(Collection<Predicate> toSelect){
		return new SelectionHypAction(ISelectionHypAction.SELECT_ACTION_TYPE,toSelect);
	}
	
	public static IHypAction makeDeselectHypAction(Collection<Predicate> toDeselect){
		return new SelectionHypAction(ISelectionHypAction.DESELECT_ACTION_TYPE,toDeselect);
	}

	public static IHypAction makeHideHypAction(Collection<Predicate> toHide){
		return new SelectionHypAction(ISelectionHypAction.HIDE_ACTION_TYPE,toHide);
	}

	public static IHypAction makeShowHypAction(Collection<Predicate> toShow){
		return new SelectionHypAction(ISelectionHypAction.SHOW_ACTION_TYPE,toShow);
	}

}

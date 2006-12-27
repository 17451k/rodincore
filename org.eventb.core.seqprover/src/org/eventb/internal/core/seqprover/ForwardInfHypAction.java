package org.eventb.internal.core.seqprover;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;

import org.eventb.core.ast.FreeIdentifier;
import org.eventb.core.ast.ITypeEnvironment;
import org.eventb.core.ast.Predicate;
import org.eventb.core.seqprover.IHypAction.IForwardInfHypAction;


public class ForwardInfHypAction implements IInternalHypAction, IForwardInfHypAction {

	private final Collection<Predicate> hyps;
	private final FreeIdentifier[] addedIdents;
	private final Collection<Predicate> inferredHyps;

	// Internal field to enforce that only forward inferences that were not skipped 
	// while building a proof are used to process proof dependencies.
	private boolean skipped = true;
	

	public ForwardInfHypAction(final Collection<Predicate> hyps, final FreeIdentifier[] addedIdents, final Collection<Predicate> inferredHyps) {
		super();
		this.hyps = hyps;
		this.addedIdents = addedIdents;
		this.inferredHyps = inferredHyps;
	}
		
	public Collection<Predicate> getHyps() {
		return hyps;
	}

	public String getActionType() {
		return IForwardInfHypAction.ACTION_TYPE;
	}

	public FreeIdentifier[] getAddedFreeIdents() {
		return addedIdents;
	}

	public Collection<Predicate> getInferredHyps() {
		return inferredHyps;
	}	

	public IInternalProverSequent perform(IInternalProverSequent seq) {
		if (seq.containsHypotheses(hyps) &&
				allFresh(seq.typeEnvironment(), addedIdents) &&
				(! seq.containsHypotheses(inferredHyps)))
		{
			IInternalProverSequent result = seq.addHyps(inferredHyps, nonDestructiveAdd(seq.typeEnvironment(), addedIdents));
			skipped = false;
			return result;
		}
		skipped = true;
		return seq;
	}

	public void processDependencies(ProofDependenciesBuilder proofDeps) {
		if (skipped) return;
		if ((! Collections.disjoint(proofDeps.getUsedHypotheses(),inferredHyps)) ||
				(! Collections.disjoint(proofDeps.getUsedFreeIdents(),Arrays.asList(addedIdents))))
		{
			// This forward inference was actually used
			proofDeps.getUsedHypotheses().removeAll(inferredHyps);
			proofDeps.getUsedHypotheses().addAll(hyps);

			for (Predicate hyp : hyps)
				proofDeps.getUsedFreeIdents().addAll(Arrays.asList(hyp.getFreeIdentifiers()));
			for (Predicate infHyp : inferredHyps)
				proofDeps.getUsedFreeIdents().addAll(Arrays.asList(infHyp.getFreeIdentifiers()));
			
			for (FreeIdentifier addedIdent : addedIdents)
			{
				proofDeps.getUsedFreeIdents().remove(addedIdent);
				proofDeps.getIntroducedFreeIdents().add(addedIdent.getName());
			}
		}
	}
	
	private boolean allFresh(ITypeEnvironment typEnv, FreeIdentifier[] freeIdents){
		for (FreeIdentifier freeIdent : freeIdents)
		{
			if (typEnv.contains(freeIdent.getName())) return false;
		}
		return true;
	}
	
	private ITypeEnvironment nonDestructiveAdd(ITypeEnvironment typeEnv, FreeIdentifier[] freeIdents){
		if (freeIdents.length == 0) return typeEnv;
		ITypeEnvironment newTypEnv = typeEnv.clone();
		newTypEnv.addAll(freeIdents);
		return newTypEnv;
	}
	
}

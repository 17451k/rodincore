package org.eventb.core.prover;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.eventb.core.ast.FreeIdentifier;
import org.eventb.core.ast.ITypeEnvironment;
import org.eventb.core.ast.Predicate;
import org.eventb.core.prover.sequent.HypothesesManagement;
import org.eventb.core.prover.sequent.Hypothesis;
import org.eventb.core.prover.sequent.IProverSequent;
import org.eventb.core.prover.sequent.HypothesesManagement.Action;

public class ReasonerOutputSucc extends ReasonerOutput{
	
	public static class Anticident{
		
		public FreeIdentifier[] addedFreeIdentifiers;
		public Set<Predicate> addedHypotheses;
		public List <Action> hypAction;
		public Predicate subGoal;
		
		public Anticident(){
			addedFreeIdentifiers = new FreeIdentifier[0];
			addedHypotheses = new HashSet<Predicate>();
			hypAction = new ArrayList<Action>();
			subGoal = null;
		}
		
		public IProverSequent genSequent(IProverSequent seq){
			ITypeEnvironment newTypeEnv;
			if (addedFreeIdentifiers.length == 0)
				newTypeEnv = seq.typeEnvironment();
			else
			{
				newTypeEnv = seq.typeEnvironment().clone();
				for (FreeIdentifier freeIdent : addedFreeIdentifiers) {
					// check for variable name clash
					if (newTypeEnv.contains(freeIdent.getName()))
					{
						// name clash
						return null;
					}
					newTypeEnv.addName(freeIdent.getName(),freeIdent.getType());
				}
				// Check of variable name clashes
//				if (! Collections.disjoint(
//						seq.typeEnvironment().getNames(),
//						addedFreeIdentifiers.getNames()))
//					// This is the place to add name refactoring code.
//					return null;
//				newTypeEnv = seq.typeEnvironment().clone();
//				newTypeEnv.addAll(addedFreeIdentifiers);
			}
			IProverSequent result = seq.replaceGoal(subGoal,newTypeEnv);
			if (result == null) return null;
			Set<Hypothesis> hypsToAdd = Hypothesis.Hypotheses(addedHypotheses);
			result = result.addHyps(hypsToAdd,null);
			if (result == null) return null;
			result = result.selectHypotheses(hypsToAdd);
			result = HypothesesManagement.perform(hypAction,result);
			return result;
		}

		public void addFreeIdents(Set<FreeIdentifier> freeIdents) {
			assert subGoal != null;
			freeIdents.addAll(Arrays.asList(subGoal.getFreeIdentifiers()));
			for(Predicate hyp: addedHypotheses){
				freeIdents.addAll(
						Arrays.asList(hyp.getFreeIdentifiers()));
			}
			// This is not strictly needed. Just to be safe..
			freeIdents.addAll(Arrays.asList(addedFreeIdentifiers));
		}
		
//		public Set<FreeIdentifier> getNeededFreeIdents() {
//			Set<FreeIdentifier> neededFreeIdents = new HashSet<FreeIdentifier>();
//			assert subGoal != null;
//			neededFreeIdents.addAll(Arrays.asList(subGoal.getFreeIdentifiers()));
//			for(Predicate hyp: addedHypotheses){
//				neededFreeIdents.addAll(
//						Arrays.asList(hyp.getFreeIdentifiers()));
//			}
//			neededFreeIdents.removeAll(Arrays.asList(addedFreeIdentifiers));
//			return neededFreeIdents;
//		}
		
	}
	
	public String display;
	public Anticident[] anticidents;
	public Set<Hypothesis> neededHypotheses;
	public Predicate goal;
	public int reasonerConfidence;
	
	public ReasonerOutputSucc(Reasoner generatedBy, ReasonerInput generatedUsing){
		super(generatedBy,generatedUsing);
		display = generatedBy.getReasonerID();
		anticidents = null;
		neededHypotheses = new HashSet<Hypothesis>();
		goal = null;
		reasonerConfidence = IConfidence.DISCHARGED_MAX;
	}

	public void addFreeIdents(Set<FreeIdentifier> freeIdents) {
		for(Anticident anticident : anticidents){
			anticident.addFreeIdents(freeIdents);
		}
		
		freeIdents.addAll(Arrays.asList(goal.getFreeIdentifiers()));
		for(Hypothesis hyp: neededHypotheses){
			freeIdents.addAll(
					Arrays.asList(hyp.getPredicate().getFreeIdentifiers()));
		}
	}

}

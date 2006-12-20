package org.eventb.internal.core.seqprover;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.eventb.core.ast.FreeIdentifier;
import org.eventb.core.ast.ITypeEnvironment;
import org.eventb.core.ast.Predicate;
import org.eventb.core.seqprover.IConfidence;
import org.eventb.core.seqprover.IHypAction;
import org.eventb.core.seqprover.IProofRule;
import org.eventb.core.seqprover.IProverSequent;
import org.eventb.core.seqprover.IReasoner;
import org.eventb.core.seqprover.IReasonerInput;

public class ProofRule extends ReasonerOutput implements IProofRule{
	
	public static class Antecedent implements IAntecedent{
		
		private FreeIdentifier[] addedFreeIdentifiers;
		private Set <Predicate> addedHypotheses;
		private List <IHypAction> hypAction;
		private Predicate goal;
		
		public Antecedent(Predicate goal){
			addedFreeIdentifiers = new FreeIdentifier[0];
			addedHypotheses = new HashSet<Predicate>();
			hypAction = new ArrayList<IHypAction>();
			this.goal = goal;
		}
		
		public Antecedent(Predicate goal, Set<Predicate> addedHyps, FreeIdentifier[] addedFreeIdents, List<IHypAction> hypAction) {
			assert goal != null;
			this.goal = goal;
			this.addedHypotheses = addedHyps == null ? new HashSet<Predicate>() : addedHyps;
			this.addedFreeIdentifiers = addedFreeIdents == null ? new FreeIdentifier[0] : addedFreeIdents;
			this.hypAction = hypAction == null ? new ArrayList<IHypAction>() : hypAction;
		}
		
		public void addToAddedHyps(Predicate pred){
			addedHypotheses.add(pred);
		}
		
		public void addToAddedHyps(Collection<Predicate> preds){
			addedHypotheses.addAll(preds);
		}
		
		
		/**
		 * @return Returns the addedFreeIdentifiers.
		 */
		public final FreeIdentifier[] getAddedFreeIdents() {
			return addedFreeIdentifiers;
		}

		/**
		 * @param addedFreeIdentifiers The addedFreeIdentifiers to set.
		 */
		public final void setAddedFreeIdentifiers(FreeIdentifier[] addedFreeIdentifiers) {
			this.addedFreeIdentifiers = addedFreeIdentifiers;
		}

		/**
		 * @return Returns the hypAction.
		 */
		public final List<IHypAction> getHypAction() {
			return hypAction;
		}

		public final void addHypAction(IHypAction hypAction) {
			this.hypAction.add(hypAction);
		}

		/**
		 * @return Returns the addedHypotheses.
		 */
		public final Set<Predicate> getAddedHyps() {
			return Collections.unmodifiableSet(addedHypotheses);
		}

		/**
		 * @return Returns the goal.
		 */
		public final Predicate getGoal() {
			return goal;
		}
		
		
		
		private IProverSequent genSequent(IProverSequent seq){
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
			IProverSequent result = seq.replaceGoal(goal,newTypeEnv);
			if (result == null) return null;
//			Set<Predicate> hypsToAdd = HashSet<Predicate>(addedHypotheses);
			result = result.addHyps(addedHypotheses,null);
			if (result == null) return null;
			result = result.selectHypotheses(addedHypotheses);
			result = ProofRule.perform(hypAction,result);
			return result;
		}

		public void addFreeIdents(ITypeEnvironment typeEnv) {
			assert goal != null;
			typeEnv.addAll(goal.getFreeIdentifiers());
			for(Predicate hyp: addedHypotheses){
				typeEnv.addAll(
						hyp.getFreeIdentifiers());
			}
			// This is not strictly needed. Just to be safe..
			typeEnv.addAll(addedFreeIdentifiers);
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
	
	private String display;
	private IAntecedent[] antecedents;
	private Set<Predicate> neededHypotheses;
	private Predicate goal;
	private int reasonerConfidence;
	
	public ProofRule(IReasoner generatedBy, IReasonerInput generatedUsing){
		super(generatedBy,generatedUsing);
		display = generatedBy.getReasonerID();
		antecedents = null;
		neededHypotheses = new HashSet<Predicate>();
		goal = null;
		reasonerConfidence = IConfidence.DISCHARGED_MAX;
	}
	
	public ProofRule(IReasoner generatedBy, IReasonerInput generatedUsing, Predicate goal, IAntecedent[] anticidents){
		super(generatedBy,generatedUsing);
		display = generatedBy.getReasonerID();
		this.antecedents = anticidents;
		neededHypotheses = new HashSet<Predicate>();
		this.goal = goal;
		reasonerConfidence = IConfidence.DISCHARGED_MAX;
	}

	public ProofRule(IReasoner generatedBy, IReasonerInput generatedUsing, Predicate goal, Set<Predicate> neededHyps, Integer confidence, String display, IAntecedent[] anticidents) {
		super(generatedBy,generatedUsing);
		
		assert goal != null;
		assert anticidents != null;
		
		this.goal = goal;
		this.antecedents = anticidents;
		this.neededHypotheses = neededHyps == null ? new HashSet<Predicate>() : neededHyps;
		this.reasonerConfidence = confidence == null ? IConfidence.DISCHARGED_MAX : confidence;
		this.display = display == null ? generatedBy.getReasonerID() : display;		
	}

	protected void addFreeIdents(ITypeEnvironment typeEnv) {
		for(IAntecedent antecedent : antecedents){
			((Antecedent) antecedent).addFreeIdents(typeEnv);
		}
		
		typeEnv.addAll(goal.getFreeIdentifiers());
		for(Predicate hyp: neededHypotheses){
			typeEnv.addAll(
					hyp.getFreeIdentifiers());
		}
	}

	public String getDisplayName() {
		return display;
	}

	public String getRuleID() {
		return generatedBy.getReasonerID();
	}

	public int getConfidence() {
		return reasonerConfidence;
	}

	protected IProverSequent[] apply(IProverSequent seq) {
		ProofRule reasonerOutput = this;
		// Check if all the needed hyps are there
		if (! seq.containsHypotheses(reasonerOutput.neededHypotheses))
			return null;
		// Check if the goal is the same
		if (! reasonerOutput.goal.equals(seq.goal())) return null;
		
		// Generate new antecedents
		// Antecedent[] antecedents = reasonerOutput.anticidents;
		IProverSequent[] anticidents 
			= new IProverSequent[reasonerOutput.antecedents.length];
		for (int i = 0; i < anticidents.length; i++) {
			anticidents[i] = ((Antecedent) reasonerOutput.antecedents[i]).genSequent(seq);
			if (anticidents[i] == null)
				// most probably a name clash occured
				// or an invalid type env.
				// add renaming/refactoring code here
				return null;
		}
		
		return anticidents;
	}

	public Set<Predicate> getNeededHyps() {
		return neededHypotheses;
	}

	public Predicate getGoal() {
		return goal;
	}

	public IAntecedent[] getAntecedents() {
		return antecedents;
	}

	
	public static IProverSequent perform(List<IHypAction> hypActions,IProverSequent seq){
		if (hypActions == null) return seq;
		IProverSequent result = seq;
		for(IHypAction action : hypActions){
			result = ((IInternalHypAction) action).perform(result);
		}
		return result;
	}

}

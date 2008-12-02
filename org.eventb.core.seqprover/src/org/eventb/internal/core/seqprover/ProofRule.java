package org.eventb.internal.core.seqprover;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import org.eventb.core.ast.FreeIdentifier;
import org.eventb.core.ast.Predicate;
import org.eventb.core.seqprover.IConfidence;
import org.eventb.core.seqprover.IHypAction;
import org.eventb.core.seqprover.IProofRule;
import org.eventb.core.seqprover.IProverSequent;
import org.eventb.core.seqprover.IReasoner;
import org.eventb.core.seqprover.IReasonerInput;

// TODO : preserve order of hypotheses stored by using a LinkedHashSet inplementation
public class ProofRule extends ReasonerOutput implements IProofRule{
	
	private static final Set<Predicate> NO_HYPS = Collections.emptySet();
	private static final IAntecedent[] NO_ANTECEDENTS = new IAntecedent[0];

	public static class Antecedent implements IAntecedent{
		
		private final FreeIdentifier[] addedFreeIdentifiers;
		private final Set <Predicate> addedHypotheses;
		private final List <IHypAction> hypActions;
		private final Predicate goal;
		
		private static final FreeIdentifier[] NO_FREE_IDENTS = new FreeIdentifier[0];
		private static final ArrayList<IHypAction> NO_HYP_ACTIONS = new ArrayList<IHypAction>();
		
		public Antecedent(Predicate goal, Set<Predicate> addedHyps, FreeIdentifier[] addedFreeIdents, List<IHypAction> hypAction) {
			this.goal = goal;
			this.addedHypotheses = addedHyps == null ? NO_HYPS : new LinkedHashSet<Predicate>(addedHyps);
			this.addedFreeIdentifiers = addedFreeIdents == null ? NO_FREE_IDENTS : addedFreeIdents.clone();
			this.hypActions = hypAction == null ? NO_HYP_ACTIONS : new ArrayList<IHypAction>(hypAction);
		}
		
		/**
		 * @return Returns the addedFreeIdentifiers.
		 */
		public final FreeIdentifier[] getAddedFreeIdents() {
			return addedFreeIdentifiers;
		}

		/**
		 * @return Returns the hypAction.
		 */
		public final List<IHypAction> getHypActions() {
			return hypActions;
		}

		/**
		 * @return Returns the hypAction.
		 */
		public final List<IHypAction> getHypAction() {
			return hypActions;
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
		
		private IProverSequent genSequent(IProverSequent seq, Predicate goalInstantiation){
			
			// newGoal not required
			Predicate newGoal;
			if (goal == null)
			{
				// Check for ill formed rule
				if (goalInstantiation == null) return null;
				newGoal = goalInstantiation;
			}
			else
			{
				newGoal = goal;
			}
			
			IInternalProverSequent result = ((IInternalProverSequent) seq).modify(addedFreeIdentifiers, addedHypotheses, newGoal);
			// not strictly needed
			if (result == null) return null;
			result = ProofRule.performHypActions(hypActions,result);
			// no change if seq == result
			return result;
			
		}
		
	}
	
	private final String display;
	private final IAntecedent[] antecedents;
	private final Set<Predicate> neededHypotheses;
	private final Predicate goal;
	private final int reasonerConfidence;
	
	public ProofRule(IReasoner generatedBy, IReasonerInput generatedUsing, Predicate goal, Set<Predicate> neededHyps, Integer confidence, String display, IAntecedent[] antecedents) {
		super(generatedBy,generatedUsing);
		
		this.goal = goal;
		this.antecedents = antecedents == null ? NO_ANTECEDENTS : antecedents.clone();
		this.neededHypotheses = neededHyps == null ? NO_HYPS : new LinkedHashSet<Predicate>(neededHyps);
		this.reasonerConfidence = confidence == null ? IConfidence.DISCHARGED_MAX : confidence;
		this.display = display == null ? generatedBy.getReasonerID() : display;		
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

	public IProverSequent[] apply(IProverSequent seq) {
		// Check if all the needed hyps are there
		if (! seq.containsHypotheses(neededHypotheses))
			return null;
		// Check if the goal null, or identical to the sequent.
		if ( goal!=null && ! goal.equals(seq.goal()) ) return null;
		
		// in case the goal is null, keep track of the sequent goal.
		Predicate goalInstantiation = null;
		if (goal == null)
			goalInstantiation = seq.goal();
		
		// Generate new antecedents
		IProverSequent[] anticidents 
			= new IProverSequent[antecedents.length];
		for (int i = 0; i < anticidents.length; i++) {
			anticidents[i] = ((Antecedent) antecedents[i]).genSequent(seq, goalInstantiation);
			if (anticidents[i] == null)
				// most probably a name clash occured
				// or the rule is ill formed
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

	
	public ProofDependenciesBuilder processDeps(ProofDependenciesBuilder[] subProofsDeps){
		assert antecedents.length == subProofsDeps.length;

		ProofDependenciesBuilder proofDeps = new ProofDependenciesBuilder();
		
		// the singular goal dependency
		Predicate depGoal = null;
		
		// process each antecedent
		for (int i = 0; i < antecedents.length; i++) {

			final IAntecedent antecedent = antecedents[i];
			final ProofDependenciesBuilder subProofDeps = subProofsDeps[i];
			
			// Process the antecedent
			processHypActionDeps(antecedent.getHypActions(), subProofDeps);
			
			subProofDeps.getUsedHypotheses().removeAll(antecedent.getAddedHyps());
			if (antecedent.getGoal()!=null)
				subProofDeps.getUsedFreeIdents().addAll(Arrays.asList(antecedent.getGoal().getFreeIdentifiers()));
			for (Predicate hyp : antecedent.getAddedHyps())
				subProofDeps.getUsedFreeIdents().addAll(Arrays.asList(hyp.getFreeIdentifiers()));
			for (FreeIdentifier freeIdent : antecedent.getAddedFreeIdents()){
				subProofDeps.getUsedFreeIdents().remove(freeIdent);
				subProofDeps.getIntroducedFreeIdents().add(freeIdent.getName());			
			}
						
			// Combine this information
			proofDeps.getUsedHypotheses().addAll(subProofDeps.getUsedHypotheses());
			proofDeps.getUsedFreeIdents().addAll(subProofDeps.getUsedFreeIdents());
			proofDeps.getIntroducedFreeIdents().addAll(subProofDeps.getIntroducedFreeIdents());
			
			// update depGoal
			// in case the antecedent is the variable goal, and the proof above it has
			// an instantiation for it, make it the new depGoal
			if (antecedent.getGoal() == null && subProofDeps.getGoal() != null){
				// Check for non-equal instantiations of the goal
				assert (depGoal == null || depGoal.equals(subProofDeps.getGoal()));
				depGoal = subProofDeps.getGoal();
			}

		}
		
		if (goal != null){	
			// goal is explicitly stated
			depGoal = goal;
		}
			
		proofDeps.setGoal(depGoal);
		proofDeps.getUsedHypotheses().addAll(neededHypotheses);	
		if (depGoal!=null) proofDeps.getUsedFreeIdents().addAll(Arrays.asList(depGoal.getFreeIdentifiers()));
		for (Predicate hyp : neededHypotheses)
			proofDeps.getUsedFreeIdents().addAll(Arrays.asList(hyp.getFreeIdentifiers()));
		
		return proofDeps;
	}
	
	
	private static IInternalProverSequent performHypActions(List<IHypAction> hypActions,IInternalProverSequent seq){
		if (hypActions == null) return seq;
		IInternalProverSequent result = seq;
		for(IHypAction action : hypActions){
			result = ((IInternalHypAction) action).perform(result);
			if (result == null)
				return null;
		}
		return result;
	}
	
	private static void processHypActionDeps(List<IHypAction> hypActions,ProofDependenciesBuilder proofDeps){
		int length = hypActions.size();
		for (int i = length-1; i >= 0; i--) {
			((IInternalHypAction)hypActions.get(i)).processDependencies(proofDeps);
		}
	}

}

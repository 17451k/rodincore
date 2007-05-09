package org.eventb.core.seqprover.reasonerInputs;

import java.util.Set;

import org.eventb.core.ast.Predicate;
import org.eventb.core.seqprover.IProofMonitor;
import org.eventb.core.seqprover.IProofRule;
import org.eventb.core.seqprover.IProverSequent;
import org.eventb.core.seqprover.IReasoner;
import org.eventb.core.seqprover.IReasonerInput;
import org.eventb.core.seqprover.IReasonerInputReader;
import org.eventb.core.seqprover.IReasonerInputWriter;
import org.eventb.core.seqprover.IReasonerOutput;
import org.eventb.core.seqprover.ProverFactory;
import org.eventb.core.seqprover.SerializeException;
import org.eventb.core.seqprover.IProofRule.IAntecedent;
import org.eventb.core.seqprover.proofBuilder.ReplayHints;
import org.eventb.internal.core.seqprover.ReasonerFailure;

/**
 * Common implementation for reasoners that work on at most one hypothesis and
 * mark it as the sole needed hypothesis in their generated rule.
 * 
 * @author Laurent Voisin
 * @author Farhad Mehta
 */
public abstract class HypothesisReasoner implements IReasoner {
	
	public static final class Input implements IReasonerInput {

		Predicate pred;

		public Input(Predicate pred) {
			this.pred = pred;
		}

		public void applyHints(ReplayHints hints) {
			if (pred != null) {
				pred = hints.applyHints(pred);
			}
		}

		public String getError() {
			return null;
		}

		public boolean hasError() {
			return false;
		}

	}
	
	public final void serializeInput(IReasonerInput rInput,
			IReasonerInputWriter writer) throws SerializeException {
		
		// Nothing to do
	}

	public final Input deserializeInput(IReasonerInputReader reader)
			throws SerializeException {

		Set<Predicate> neededHyps = reader.getNeededHyps();
		final int length = neededHyps.size();
		if (length == 0) {
			return new Input(null);
		}
		if (length != 1) {
			throw new SerializeException(new IllegalStateException(
					"Expected at most one needed hypothesis!"));
		}
		for (Predicate hyp: neededHyps) {
			return new Input(hyp);
		}
		assert false;
		return null;
	}

	public final IReasonerOutput apply(IProverSequent seq, IReasonerInput rInput,
			IProofMonitor pm) {
		
		final Input input = (Input) rInput;
		final Predicate pred = input.pred;
		
		final Predicate hyp;
		if (pred == null) {
			hyp = null;
		} else {
			hyp = pred;
			if (!seq.containsHypothesis(hyp)) {
				return ProverFactory.reasonerFailure(this, input,
						"Nonexistent hypothesis: " + hyp);
			}
		}
		
		
		final String display = getDisplay(pred);
		final IAntecedent[] antecedents; 
		try {
			antecedents = getAntecedents(seq, pred);
		} catch (IllegalArgumentException e) {
			return new ReasonerFailure(this, input, e.getMessage());
		}
		
		final Predicate goal;
		if (isGoalDependent(seq, pred)){
			goal = seq.goal();
		} else
		{
			goal = null;
		}
		
		return ProverFactory.makeProofRule(this, input, goal, hyp, display,
				antecedents);
	}

	/**
	 * Return the antecedents to put in the generated rule, or throw an
	 * <code>IllegalArgumentException</code> in case of reasoner failure. In
	 * the latter case, the message associated to the exception will be returned
	 * in the reasoner failure.
	 * <p>
	 * When this method is called, it has already been checked that the given
	 * predicate is indeed an hypothesis of the given sequent. Hence, clients do
	 * not need to check it again here.
	 * </p>
	 * 
	 * <p>
	 * Antecedents returned by this method may contain <code>null</code> goals only in 
	 * case the method {@link #isGoalDependent(IProverSequent, Predicate)} returns <code>false</code>.
	 * </p>
	 * 
	 * @param sequent
	 *            the goal of the current sequent
	 * @param pred
	 *            the predicate of the hypothesis, or <code>null</code> if
	 *            none
	 * @return the antecedents of the generated rule
	 * @throws IllegalArgumentException
	 *             if the given predicate doesn't fulfill the reasoner
	 *             preconditions.
	 */
	protected abstract IAntecedent[] getAntecedents(IProverSequent sequent,
			Predicate pred) throws IllegalArgumentException;

	/**
	 * Return the display string to associate to the generated rule
	 * 
	 * @param pred
	 *            the predicate of the hypothesis, or <code>null</code> if none
	 * @return the display string for the generated rule
	 */
	protected abstract String getDisplay(Predicate pred);
	
	/**
	 * Returns whether the generated rule should be goal dependent.
	 * 
	 * <p>
	 * When this method is called, it has already been checked that {@link #getAntecedents(IProverSequent, Predicate)}
	 * has not returned an {@link IllegalArgumentException}, and also that the given
	 * predicate is indeed an hypothesis of the given sequent. Hence, clients do
	 * not need to check it again here.
	 * </p>
	 * 
	 * <p>
	 * By default, this method returns <code>true</code>. Subclasses may override this method to provide different
	 * behaviour. Antecedents returned by {@link #getAntecedents(IProverSequent, Predicate)}
	 * may contain <code>null</code> goals only in case this method returns <code>false</code>.
	 * </p>
	 * 
	 * @see IProofRule
	 * 
	 * @param sequent
	 *            the goal of the current sequent
	 * @param pred
	 *            the predicate of the hypothesis, or <code>null</code> if
	 *            none
	 * @return <code>true</code> iff the generated rule should be goal dependent.
	 */
	protected boolean isGoalDependent(IProverSequent sequent, Predicate pred){
		return true;
	}


}

package org.eventb.core.seqprover;

import java.util.List;
import java.util.Set;

import org.eventb.core.ast.FreeIdentifier;
import org.eventb.core.ast.Predicate;


/**
 * Common protocol for a proof rule for the sequent prover.
 * <p>
 * A proof rule contains a goal, needed hypotheses, and a possibly empty array of
 * anticidents.
 * </p>
 * 
 * <p>
 * The goal and needed hypotheses are used to determine if the rule is applicable
 * to a sequent. The antecedents are used to calculate the result of applying the
 * rule to a sequent. All three are used to calculate proof dependencies.
 * </p>
 * 
 * <p>
 * From the point of view of a proof tree, rules are applied in a top-down (i.e. from
 * root to leaves) manner, and proof dependencies are calculated in a bottom-up
 * (i.e. from leaves to root) manner.
 * </p>
 * 
 * <p>
 * Note that in addition to the set of hypotheses explicitly stated in a sequent,
 * the well-definedness predicates of each hypothesis, and that of the goal may be assumed.
 * If this is the case, the actual hypotheses or goal need to be stated explicity
 * in the needed hypotheses or the goal of the rule respectively. 
 * </p>
 * 
 * <p>
 * This interface is not intended to be implemented by clients. Objects of this type 
 * are typically generated inside reasoners by calling a factory method in {@link ProverFactory}.
 * </p>
 * @see IAntecedent
 * @see org.eventb.core.seqprover.IReasoner
 * @see org.eventb.core.seqprover.ProverFactory
 * 
 * @author Farhad Mehta
 */
public interface IProofRule extends IReasonerOutput{

	
	/**
	 * Returns the goal of this proof rule as returned by the reasoner.
	 * <p>
	 * The goal may be <code>null</code> in the case when the rule is 
	 * applicable to a sequent with any goal. In this case it is permitted that
	 * the antecedents also have a <code>null</code> goal. The <code>null</code>
	 * value then serves as a wildcard that can be instantiated upon rule application
	 * for any predicate occuring as the goal of the sequent on which the rule is 
	 * applied.
	 * </p>
	 * @return the goal {@link Predicate} of this proof rule, or <code>null</code>
	 *  if the rule is does not depend on the goal.
	 */
	Predicate getGoal();

	
	/**
	 * Returns the needed hypotheses of this proof rule as returned by the reasoner.
	 * 
	 * <p>
	 * Note : The returned object must not be modified in any way.
	 * </p>
	 * 
	 * @return the needed hypotheses of this proof rule
	 */
	Set<Predicate> getNeededHyps();

	/**
	 * Returns the confidence of this proof rule as returned by the reasoner.
	 * 
	 * @return the confidence of this proof rule (see {@see IConfidence})
	 */
	int getConfidence();
	
	/**
	 * Returns the name of this proof rule this should be used for display.
	 * 
	 * <p>
	 * Note : The returned object must not be modified in any way.
	 * </p>
	 * 
	 * @return the display name of this proof rule
	 */
	String getDisplayName();
	
	/**
	 * Returns the anticidents of this proof rule as returned by the reasoner.
	 * 
	 * <p>
	 * Note : The returned object must not be modified in any way.
	 * </p>
	 * 
	 * @return the anticidents of this proof rule (see {@see IAntecedent})
	 */
	IAntecedent[] getAntecedents();
	

	/**
	 * Common protocol for an anticident for a proof rule.
	 * <p>
	 * An anticident contains a goal, added hypotheses, added free identifiers, and hypothesis
	 * selection information.
	 * </p>
	 * <p>
	 * Typically, an andicident records the changes made by a reasoner on the sequent
	 * to be proven. 
	 * </p>
	 * <p>
	 * This interface is not intended to be implemented by clients. Objects of this type 
	 * are typically generated inside reasoners by calling a factory method.
	 * </p>
	 * @see org.eventb.core.seqprover.IReasoner
	 * @see org.eventb.core.seqprover.ProverFactory
	 * 
	 * @author Farhad Mehta
	 */
	public interface IAntecedent {
		
		/**
		 * Returns the goal of this anticident.
		 * <p> 
		 * In case the goal is <code>null</code>, the goal of this antecedent 
		 * is intended to be identical to the goal of the sequent on which the
		 * rule is applied. The goal of an antecedent may only be <code>null</code>
		 * if the goal of the rule is also <code>null</code>. If this is not the case,
		 * the rule is ill formed and will not be applicable for any sequent.
		 * </p>
		 * 
		 * @see IProofRule.getGoal()
		 * 
		 * @return the goal {@link Predicate} of this antecedent, or <code>null</code>
		 * 		if the goal is intended to be identical to the goal of the sequent on which the
		 *     	rule is applied.
		 */
		Predicate getGoal();
		
		/**
		 * Returns the added hypotheses of this anticident.
		 * <p>
		 * Added hyps are by default selected.
		 * </p>
		 * 
		 * <p>
		 * The returned object must not be modified in any way.
		 * </p>
		 * 
		 * @return the added hypotheses of this anticident
		 */
		Set<Predicate> getAddedHyps();
		
		/**
		 * Returns the added free identifiers of this anticident.
		 * 
		 * <p>
		 * The returned object must not be modified in any way.
		 * </p>
		 *
		 * @return the added free identifiers of this anticident
		 */
		FreeIdentifier[] getAddedFreeIdents();
		
		/**
		 * Returns hypotheses selection information for this anticident.
		 * <p>
		 * Added hyps are by default selected. The hypAction should not contain 
		 * added hypotheses. (simlifier constraint)
		 * </p>
		 * 
		 * <p>
		 * The returned object must not be modified in any way.
		 * </p>
		 * 
		 * @return the hypotheses selection information for this anticident
		 */
		List<IHypAction> getHypActions();
		
		/**
		 * Returns hypotheses selection information for this anticident.
		 * <p>
		 * Added hyps are by default selected. The hypAction should not contain 
		 * added hypotheses. (simlifier constraint)
		 * </p>
		 * @return the hypotheses selection information for this anticident
		 * 
		 * <p>
		 * The returned object must not be modified in any way.
		 * </p>
		 * 
		 * @deprecated use {@link #getHypActions()} instead
		 */
		@Deprecated
		List<IHypAction> getHypAction();
	}
	
	/**
	 * Applies this rule to the given sequent and returns the sequents resulting
	 * from this rule application, or <code>null</code> in case this rule was not
	 * applicable to the given sequent.
	 * 
	 * <p>
	 * Note that during normal operation, this method is meant to be used only
	 * internally within the sequent prover. It has been made public so that people
	 * implementing their own rules have a way of testing them independantly of the
	 * proof tree data structure.
	 * </p>
	 * 
	 * @param seq
	 * 			Sequent on which this rule should be applied
	 * @return
	 * 		The sequents resulting from this rule application, or <code>null</code>
	 * 		 in case this rule was not applicable to the given sequent.
	 */
	IProverSequent[] apply(IProverSequent seq);

}
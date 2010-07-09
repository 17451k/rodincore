/*******************************************************************************
 * Copyright (c) 2007, 2010 ETH Zurich and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     ETH Zurich - initial API and implementation
 *     Systerel - added FunOvrGoalTac and FunOvrHypTac tactics
 *     Systerel - added PartitionRewriteTac tactic (math V2)
 *     Systerel - added FiniteHypBoundedGoalTac and OnePoint*Tac
 *     Systerel - modified FindContrHypsTac to use ContrHyps (discharge)
 *     Systerel - added FunImgSimpTac tactic (simplify)
 ******************************************************************************/
package org.eventb.core.seqprover.eventbExtensions;

import static org.eventb.core.seqprover.tactics.BasicTactics.composeOnAllPending;
import static org.eventb.core.seqprover.tactics.BasicTactics.loopOnAllPending;

import java.util.Arrays;
import java.util.List;

import org.eventb.core.ast.BinaryPredicate;
import org.eventb.core.ast.Expression;
import org.eventb.core.ast.IPosition;
import org.eventb.core.ast.Predicate;
import org.eventb.core.ast.RelationalPredicate;
import org.eventb.core.ast.UnaryPredicate;
import org.eventb.core.seqprover.IProofMonitor;
import org.eventb.core.seqprover.IProofTreeNode;
import org.eventb.core.seqprover.IProverSequent;
import org.eventb.core.seqprover.ITactic;
import org.eventb.core.seqprover.reasonerInputs.EmptyInput;
import org.eventb.core.seqprover.reasonerInputs.MultiplePredInput;
import org.eventb.core.seqprover.reasoners.Hyp;
import org.eventb.core.seqprover.tactics.BasicTactics;
import org.eventb.internal.core.seqprover.eventbExtensions.AllI;
import org.eventb.internal.core.seqprover.eventbExtensions.AutoImpF;
import org.eventb.internal.core.seqprover.eventbExtensions.Conj;
import org.eventb.internal.core.seqprover.eventbExtensions.FalseHyp;
import org.eventb.internal.core.seqprover.eventbExtensions.FiniteHypBoundedGoal;
import org.eventb.internal.core.seqprover.eventbExtensions.HypOr;
import org.eventb.internal.core.seqprover.eventbExtensions.ImpI;
import org.eventb.internal.core.seqprover.eventbExtensions.IsFunGoal;
import org.eventb.internal.core.seqprover.eventbExtensions.NegEnum;
import org.eventb.internal.core.seqprover.eventbExtensions.TrueGoal;
import org.eventb.internal.core.seqprover.eventbExtensions.rewriters.AutoRewrites;
import org.eventb.internal.core.seqprover.eventbExtensions.rewriters.TypeRewrites;


/**
 * This class contains static internal classes that implement automatic tactics.
 * 
 * <p>
 * Auto tactics are tactics that require no user input and are used to either discharge, simplify, or split the proof tree
 * nodes to which they are applied.
 * </p>
 * 
 * <p>
 * They typically extend the auto and post tactic extension points.
 * </p>
 * 
 * @author Farhad Mehta
 * 
 * @since 1.0
 */
public class AutoTactics {

	private static final EmptyInput EMPTY_INPUT = new EmptyInput();
		

	/**
	 * This class is not meant to be instantiated
	 */
	private AutoTactics()
	{
		
	}
	
	
	//*************************************************
	//
	//				Discharging Auto tactics
	//
	//*************************************************
	
	
	/**
	 * Discharges any sequent whose goal is 'true'.
	 * 
	 * @author Farhad Mehta
	 *
	 */
	public static class TrueGoalTac extends AbsractLazilyConstrTactic{

		@Override
		protected ITactic getSingInstance() {
			return BasicTactics.reasonerTac(new TrueGoal(), EMPTY_INPUT);
		}
	}

	/**
	 * Discharges any sequent containing a 'false' hypothesis.
	 * 
	 * @author Farhad Mehta
	 *
	 */
	public static class FalseHypTac extends AbsractLazilyConstrTactic{

		@Override
		protected ITactic getSingInstance() {
			return BasicTactics.reasonerTac(new FalseHyp(), EMPTY_INPUT);
		}
	}

	
	/**
	 * Discharges any sequent whose goal is present in its hypotheses.
	 * 
	 * @author Farhad Mehta
	 *
	 */
	public static class GoalInHypTac extends AbsractLazilyConstrTactic{

		@Override
		protected ITactic getSingInstance() {
			return BasicTactics.reasonerTac(new Hyp(), EMPTY_INPUT);
		}
	}

	/**
	 * Discharges any sequent whose goal is a disjunction and one of whose disjuncts 
	 * is present in the hypotheses.
	 * 
	 * @author Farhad Mehta
	 *
	 */
	public static class GoalDisjInHypTac extends AbsractLazilyConstrTactic{

		@Override
		protected ITactic getSingInstance() {
			return BasicTactics.reasonerTac(new HypOr(), EMPTY_INPUT);
		}
	}

	
	/**
	 * Discharges a sequent whose goal states that an expression is a
	 * function (i.e. 'E : T1 -/-> T2', where T1 and T2 are type expressions).
	 * 
	 * @author Farhad Mehta
	 *
	 */
	public static class FunGoalTac extends AbsractLazilyConstrTactic{

		@Override
		protected ITactic getSingInstance() {
			return BasicTactics.reasonerTac(new IsFunGoal(), EMPTY_INPUT);
		}
	}

	/**
	 * Discharges a sequent whose goal states that an expression E has a lower
	 * or a upper bound (e.g. '∃n·(∀x·x ∈ S ⇒ x ≤ n)'), when there is an
	 * hypothesis that states the finiteness of E (i.e. 'finite(S)').
	 * 
	 * @author Nicolas Beauger
	 * @since 1.1
	 * 
	 */
	public static class FiniteHypBoundedGoalTac extends AbsractLazilyConstrTactic {

		@Override
		protected ITactic getSingInstance() {
			return BasicTactics.reasonerTac(new FiniteHypBoundedGoal(), EMPTY_INPUT);
		}
	}

	/**
	 * Discharges a sequent by finding contradictory hypotheses.
	 * This tactic tries to find a contradiction using each selected hypothesis that is a negation.
	 * 
	 * @author Farhad Mehta
	 *
	 */
	public static class FindContrHypsTac implements ITactic{

		public Object apply(IProofTreeNode ptNode, IProofMonitor pm) {
			for (Predicate shyp : ptNode.getSequent().selectedHypIterable()) {
				if (Lib.isNeg(shyp) &&
						ptNode.getSequent().containsHypotheses(Lib.breakPossibleConjunct(Lib.negPred(shyp)))){
					return Tactics.contrHyps(shyp).apply(ptNode, pm);
				}
			}
			return "Selected hypotheses contain no contradicting negations";
		};
	}

	//*************************************************
	//
	//				Simplifying Auto tactics
	//
	//*************************************************
	
	/**
	 * Tries to simplify all predicates in a sequent using pre-defined simplification rewritings.
	 * 
	 * @author Farhad Mehta
	 *
	 */
	public static class AutoRewriteTac  extends AbsractLazilyConstrTactic{

		@Override
		protected ITactic getSingInstance() {
			return BasicTactics.reasonerTac(new AutoRewrites(),EMPTY_INPUT);
		}
	}
	
	/**
	 * Tries to simplify predicates related to types using pre-defined simplification rewritings.
	 * 
	 * @author htson
	 *
	 */
	public static class TypeRewriteTac  extends AbsractLazilyConstrTactic{

		@Override
		protected ITactic getSingInstance() {
			return BasicTactics.reasonerTac(new TypeRewrites(),EMPTY_INPUT);
		}
	}
	
	/**
	 * Simplifies any sequent with an implicative goal by adding the left hand side of the implication to the hypotheses and making its 
	 * right hand side the new goal.
	 * 
	 * @author Farhad Mehta
	 *
	 */
	public static class ImpGoalTac extends AbsractLazilyConstrTactic{

		@Override
		protected ITactic getSingInstance() {
			return BasicTactics.reasonerTac(new ImpI(), EMPTY_INPUT);
		}
	}
	
	
	/**
	 * Simplifies any sequent with a universally quantified goal by freeing all its bound variables.
	 * 
	 * @author Farhad Mehta
	 *
	 */
	public static class ForallGoalTac extends AbsractLazilyConstrTactic{

		@Override
		protected ITactic getSingInstance() {
			return BasicTactics.reasonerTac(new AllI(), EMPTY_INPUT);
		}
	}
	
	/**
	 * Simplifies a sequent containing (selected) existentially quantified hypotheses by freeing their bound variables.
	 * 
	 * @author Farhad Mehta
	 *
	 */
	public static class ExistsHypTac implements ITactic{
	
		public Object apply(IProofTreeNode ptNode, IProofMonitor pm) {
			for (Predicate shyp : ptNode.getSequent().selectedHypIterable()) {
				if (Tactics.exF_applicable(shyp)){
					return Tactics.exF(shyp).apply(ptNode, pm);
				}
			}
			return "Selected hyps contain no existential hyps";
		}
		
	}

	/**
	 * Simplifies a sequent containing (selected) conjunctive hypotheses by separating them.
	 * 
	 * @author Farhad Mehta
	 *
	 */
	public static class ConjHypTac implements ITactic{
	
		public Object apply(IProofTreeNode ptNode, IProofMonitor pm) {
			for (Predicate shyp : ptNode.getSequent().selectedHypIterable()) {
				if (Tactics.conjF_applicable(shyp)){
					return Tactics.conjF(shyp).apply(ptNode, pm);
				}
			}
			return "Selected hypotheses contain no conjunctions";
		}
		
	}
	
	/**
	 * Simplifies a sequent by rewriting all selected hypotheses and the goal using a (selected) hypothesis that is an equality
	 * between a free variable and an expression that does not contain the free variable. The used equality remains in the
	 * selected hypotheses to be used again.
	 * 
	 * @author Farhad Mehta
	 *
	 */
	public static class EqHypTac implements ITactic{

		public Object apply(IProofTreeNode ptNode, IProofMonitor pm) {
			for (Predicate shyp : ptNode.getSequent().selectedHypIterable()) {
				if (Lib.isEq(shyp)){
					if (Lib.isFreeIdent(Lib.eqLeft(shyp)) &&
							! Arrays.asList(Lib.eqRight(shyp).getFreeIdentifiers()).contains(Lib.eqLeft(shyp))){
						// Try eq and return only if the tactic actually did something.
						if (Tactics.eqE(shyp).apply(ptNode, pm) == null) return null;
					} else if (Lib.isFreeIdent(Lib.eqRight(shyp)) &&
							! Arrays.asList(Lib.eqLeft(shyp).getFreeIdentifiers()).contains(Lib.eqRight(shyp))){
						// Try he and return only if the tactic actually did something.
						if (Tactics.he(shyp).apply(ptNode, pm) == null) return null;
					}
				}

			}
			return "Selected hyps contain no appropriate equalities";
		}
	}
	
	
	/**
	 * Simplifies the (visible) implicative hypotheses in a sequent by removing predicates from their left hand sides that are
	 * (selected) hypotheses. 
	 * 
	 * @author Farhad Mehta
	 *
	 */
	public static class ShrinkImpHypTac extends AbsractLazilyConstrTactic{

		@Override
		protected ITactic getSingInstance() {
			return BasicTactics.reasonerTac(new AutoImpF(), EMPTY_INPUT);
		}
	}
	
	/**
	 * Simplifies (selected) hypotheses of the form 'E={a,b,c}' to 'E={a,c}' after finding the hypothesis 'not(E=b)'.
	 * 
	 * 
	 * @author htson, Farhad Mehta
	 *
	 * TODO : do search in reasoner instead of tactic
	 */
	public static class ShrinkEnumHypTac implements ITactic  {

		public Object apply(IProofTreeNode ptNode, IProofMonitor pm) {
			for (Predicate shyp : ptNode.getSequent().selectedHypIterable()) {
				// Search for E : {a, ... ,c}
				if (Lib.isInclusion(shyp)) {
					Expression right = ((RelationalPredicate) shyp)
							.getRight();
					if (Lib.isSetExtension(right)) {
						// Looking for not(E = b)
						for (Predicate hyp : ptNode.getSequent()
								.selectedHypIterable()) {
							if (Lib.isNeg(hyp)) {
								Predicate child = ((UnaryPredicate) hyp)
										.getChild();
								if (Lib.isEq(child)) {
									if (negEnum(shyp, hyp)
											.apply(ptNode, pm) == null)
										return null;
								}

							}
						}
					}
				}
			}

			return "Selected hyps contain no appropriate hypotheses";
		}
		
		private static ITactic negEnum(Predicate shyp, Predicate hyp) {
			return BasicTactics.reasonerTac(new NegEnum(), new MultiplePredInput(
					new Predicate[] { shyp, hyp }));
		}
	
	}	
	
	/**
	 * Simplifies all (selected) hypotheses of the form 'P => Q /\ R' into multiple
	 * implications 'P => Q' , 'P => R'.
	 * 
	 * @author htson, Farhad Mehta
	 */
	public static class SplitRightConjImpHypTac implements ITactic {

		public Object apply(IProofTreeNode ptNode, IProofMonitor pm) {
			for (Predicate shyp : ptNode.getSequent().selectedHypIterable()) {
				// Search for (P => Q /\ ... /\ R)
				if (Lib.isImp(shyp)) {
					Predicate right = ((BinaryPredicate) shyp)
							.getRight();
					if (Lib.isConj(right)) {
						if (Tactics.impAndRewrites(shyp, IPosition.ROOT).apply(
								ptNode, pm) == null)
							return null;
					}
				}
			}
			return "Selected hyps contain no appropriate hypotheses";
		}
	}

	/**
	 * Simplifies all (selected) hypotheses of the form 'P \/ Q => R' into multiple
	 * implications 'P => R' , 'Q => R'.
	 * 
	 * @author htson, Farhad Mehta
	 */
	public static class SplitLeftDisjImpHypTac implements ITactic {

		public Object apply(IProofTreeNode ptNode, IProofMonitor pm) {
			for (Predicate shyp : ptNode.getSequent().selectedHypIterable()) {
				// Search for (P \/ ... \/ Q => R)
				if (Lib.isImp(shyp)) {
					Predicate left = ((BinaryPredicate) shyp)
							.getLeft();
					if (Lib.isDisj(left)) {
						if (Tactics.impOrRewrites(shyp, IPosition.ROOT).apply(
								ptNode, pm) == null)
							return null;
					}
				}
			}
			return "Selected hyps contain no appropriate hypotheses";
		}
	}

	/**
	 * Simplifies all predicates of the form 'partition(S, ...)' into their
	 * expanded form in the goal and all visible hypotheses .
	 * 
	 * @author Nicolas Beauger
	 */
	public static class PartitionRewriteTac implements ITactic {

		public Object apply(IProofTreeNode ptNode, IProofMonitor pm) {
			boolean success = false;
			for (Predicate shyp : ptNode.getSequent().visibleHypIterable()) {
				success |= applyPartitionRewrites(ptNode, shyp, pm);
			}
			success |= applyPartitionRewrites(ptNode, null, pm);
			
			if (success)
				return null;
			else
				return "Tactic unapplicable";
		}

		private boolean applyPartitionRewrites(IProofTreeNode ptNode,
				Predicate hyp, IProofMonitor pm) {
			boolean success = false;
			final Predicate pred;
			if (hyp == null) {
				pred = ptNode.getSequent().goal();
			} else {
				pred = hyp;
			}
			final List<IPosition> positions = Tactics
					.partitionGetPositions(pred);
			for (IPosition position : positions) {
				final ITactic partitionRewrites = Tactics
						.partitionRewrites(hyp, position);
				success |= (partitionRewrites.apply(ptNode, pm) == null);
			}
			return success;
		}
	}

	/**
	 * Simplifies expressions of form '(A <<| f)(C)' and similar, where f is
	 * known as a partial function.
	 * 
	 * @author Thomas Muller
	 * @since 1.3
	 */
	public static class FunImgSimpTac implements ITactic {

		public Object apply(IProofTreeNode ptNode, IProofMonitor pm) {
			for (Predicate shyp : ptNode.getSequent().visibleHypIterable()) {
				if (applyFunImgSimplifies(ptNode, shyp, pm)) {
					return null;
				}
			}
			if (applyFunImgSimplifies(ptNode, null, pm))
				return null;
			return "Tactic unapplicable";
		}

		private boolean applyFunImgSimplifies(IProofTreeNode ptNode,
				Predicate hyp, IProofMonitor pm) {
			final IProverSequent sequent = ptNode.getSequent();
			final List<IPosition> positions = Tactics.funImgSimpGetPositions(
					hyp, sequent);
			for (IPosition position : positions) {
				final ITactic t = Tactics.funImgSimplifies(hyp, position);
				if (t.apply(ptNode, pm) == null) {
					return true;
				}
			}
			return false;
		}
	}
	
	
	//*************************************************
	//
	//				Splitting Auto tactics
	//
	//*************************************************


	/**
	 * Splits a sequent with a conjunctive goal into multiple subgoals.
	 * 
	 * @author Farhad Mehta
	 *
	 */
	public static class ConjGoalTac extends AbsractLazilyConstrTactic{

		@Override
		protected ITactic getSingInstance() {
			return BasicTactics.reasonerTac(new Conj(), new Conj.Input(null));
		}
	}

	/**
	 * Applies automatically the <code>funOvrGoal</code> tactic to the first
	 * applicable position in the goal.
	 * 
	 * @author Laurent Voisin
	 */
	private static class FunOvrGoalOnceTac implements ITactic {

		public Object apply(IProofTreeNode ptNode, IProofMonitor pm) {
			if (pm != null && pm.isCanceled()) {
				return "Canceled";
			}
			final Predicate goal = ptNode.getSequent().goal();
			final List<IPosition> pos = Tactics.funOvrGetPositions(goal);
			if (pos.size() == 0) {
				return "Tactic unapplicable";
			}
			if (pm != null && pm.isCanceled()) {
				return "Canceled";
			}
			return Tactics.funOvr(null, pos.get(0)).apply(ptNode, pm);
		}

	}

	/**
	 * Applies automatically, repeatedly and recursively the
	 * <code>FunOvrGoalOnceTac</code> to the proof subtree rooted at the given
	 * node.
	 * 
	 * @author Laurent Voisin
	 */
	public static class FunOvrGoalTac extends AbsractLazilyConstrTactic {

		@Override
		protected ITactic getSingInstance() {
			return loopOnAllPending(new FunOvrGoalOnceTac(), new FunImgSimpTac());
		}

	}
	
	/**
	 * Applies automatically the <code>funOvrHyp</code> tactic to the first
	 * applicable position in the selected hypotheses.
	 * 
	 * @author Laurent Voisin
	 */
	private static class FunOvrHypOnceTac implements ITactic {

		public Object apply(IProofTreeNode ptNode, IProofMonitor pm) {
			if (pm != null && pm.isCanceled()) {
				return "Canceled";
			}
			for (Predicate shyp : ptNode.getSequent().selectedHypIterable()) {
				final List<IPosition> pos = Tactics.funOvrGetPositions(shyp);
				if (pm != null && pm.isCanceled()) {
					return "Canceled";
				}
				if (pos.size() != 0) {
					return Tactics.funOvr(shyp, pos.get(0)).apply(ptNode, pm);
				}
			}
			return "Tactic unapplicable";
		}

	}

	/**
	 * Applies automatically, repeatedly and recursively the
	 * <code>FunOvrHypOnceTac</code> to the proof subtree rooted at the given
	 * node.
	 * 
	 * @author Laurent Voisin
	 */
	public static class FunOvrHypTac extends AbsractLazilyConstrTactic {

		@Override
		protected ITactic getSingInstance() {
			return loopOnAllPending(new FunOvrHypOnceTac(), new FunImgSimpTac());
		}

	}
	
	/**
	 * Applies automatically the <code>OnePointGoal</code> tactic to the goal.
	 * 
	 * @author Nicolas Beauger
	 * @since 1.1
	 */
	public static class OnePointGoalTac implements ITactic {

		public Object apply(IProofTreeNode ptNode, IProofMonitor pm) {
			if (pm != null && pm.isCanceled()) {
				return "Canceled";
			}
			final Predicate goal = ptNode.getSequent().goal();
			if (!Tactics.isOnePointApplicable(goal)) {
				return "Tactic unapplicable";
			}
			if (pm != null && pm.isCanceled()) {
				return "Canceled";
			}
			return Tactics.onePointGoal().apply(ptNode, pm);
		}

	}

	/**
	 * Applies automatically the <code>OnePointHyp</code> tactic to the selected
	 * hypotheses.
	 * 
	 * @author Nicolas Beauger
	 * @since 1.1
	 */
	public static class OnePointHypTac implements ITactic {

		public Object apply(IProofTreeNode ptNode, IProofMonitor pm) {
			if (pm != null && pm.isCanceled()) {
				return "Canceled";
			}
			for (Predicate shyp : ptNode.getSequent().selectedHypIterable()) {
				if (Tactics.isOnePointApplicable(shyp)) {
					return Tactics.onePointHyp(shyp).apply(ptNode, pm);
				}
				if (pm != null && pm.isCanceled()) {
					return "Canceled";
				}
			}
			return "Tactic unapplicable";
		}

	}
	
	//*************************************************
	//
	//				Mixed
	//
	//*************************************************


	/**
	 * Clarifies the goal of the sequent by repeatedly :
	 * - splitting conjunctions
	 * - simplifying implications and universal quantifiers
	 * - discharging sequents with a true goal, a false hypothesis, and where the goal is contained in the hypotheses
	 * 
	 * @author Farhad Mehta
	 *
	 */
	public static class ClarifyGoalTac extends AbsractLazilyConstrTactic{

		@Override
		protected ITactic getSingInstance() {
			ITactic innerLoop = 
				composeOnAllPending(
					new ConjGoalTac(),
					new ImpGoalTac(),
					new ForallGoalTac());
			ITactic outerLoop =
				loopOnAllPending(
						new TrueGoalTac(),
						new FalseHypTac(),
						innerLoop);
			return outerLoop;
		}
	}

	
	//*************************************************
	//
	//				Helper code
	//
	//*************************************************
	
	
	/**
	 * An abstract class that lazily constructs a tactic and avoids reconstructing it
	 * every time it is applied.
	 * 
	 * <p>
	 * This is particularly useful for tactics that are constructed using the tactic constructors such as 
	 * {@link BasicTactics#compose(ITactic...)} and {@link BasicTactics#repeat(ITactic)}.
	 * </p>
	 * 
	 * @author Farhad Mehta
	 *
	 */
	public static abstract class AbsractLazilyConstrTactic implements ITactic{
		
		private ITactic instance = null;
				
		abstract protected ITactic getSingInstance();
		
		public final Object apply(IProofTreeNode ptNode, IProofMonitor pm){
			if (instance == null) 
			{
				instance = getSingInstance(); 
			}
			
			return instance.apply(ptNode, pm);
		}
	}

}

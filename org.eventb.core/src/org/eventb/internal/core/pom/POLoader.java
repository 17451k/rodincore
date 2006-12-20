package org.eventb.internal.core.pom;

import java.util.HashSet;
import java.util.Set;

import org.eventb.core.IPOIdentifier;
import org.eventb.core.IPOPredicate;
import org.eventb.core.IPOPredicateSet;
import org.eventb.core.IPOSelectionHint;
import org.eventb.core.IPOSequent;
import org.eventb.core.ast.FormulaFactory;
import org.eventb.core.ast.ITypeEnvironment;
import org.eventb.core.ast.Predicate;
import org.eventb.core.seqprover.IProverSequent;
import org.eventb.core.seqprover.ProverFactory;
import org.eventb.internal.core.Util;
import org.rodinp.core.RodinDBException;

/**
 * This class handles loading and generation of Prover Sequents from PO sequents
 * stored in the database.
 * 
 * @author Laurent Voisin
 * @author Farhad Mehta
 *
 */
public final class POLoader {

	private static final FormulaFactory factory = FormulaFactory.getDefault();

	private POLoader() {
		super();
	}

	/**
	 * Returns the sequent associated to the given proof obligation.
	 * <p>
	 * The PO file containing the proof obligation to read should be locked
	 * before running this method, so that the PO doesn't change while reading
	 * it.
	 * </p>
	 * 
	 * @param poSeq
	 *            the proof obligation to read
	 * @return the sequent of the given proof obligation
	 * @throws RodinDBException
	 */
	public static IProverSequent readPO(IPOSequent poSeq) throws RodinDBException {
		final ITypeEnvironment typeEnv = factory.makeTypeEnvironment();
		final Set<Predicate> hypotheses = new HashSet<Predicate>();
		final Set<Predicate> selHyps = new HashSet<Predicate>();
		final SelectionHints selHints = new SelectionHints(poSeq);
		loadHypotheses(poSeq, selHints, hypotheses, selHyps, typeEnv);
		final Predicate goal = readGoal(poSeq, typeEnv);
		return ProverFactory.makeSequent(typeEnv,hypotheses,selHyps,goal);
	}

	/**
	 * Loads the hypotheses of the given PO and appends them to the given set of
	 * hypotheses. The given type environment is enriched with the types of the
	 * free identifiers that occur in the loaded hypotheses.
	 * 
	 * @param poSeq
	 *            PO to read
	 * @param selHints 
	 * 			  information regarding hypotheses selection
	 * @param hypotheses
	 *            set of hypotheses where to store the loaded hypotheses
	 * @param selHyps 
	 *            set of hypotheses where to store the selected loaded hypotheses
	 * @param typeEnv
	 *            type environment to enrich at the same time
	 * @throws RodinDBException
	 */
	private static void loadHypotheses(IPOSequent poSeq,
			SelectionHints selHints, Set<Predicate> hypotheses, Set<Predicate> selHyps, ITypeEnvironment typeEnv)
			throws RodinDBException {

		IPOPredicateSet[] dbHyps = poSeq.getHypotheses();
		if (dbHyps.length == 0) {
			Util.log(null, "No predicate set in PO " + poSeq);
			return;
		}
		if (dbHyps.length != 1) {
			Util.log(null, "More than one predicate set in PO " + poSeq);
		}
		loadPredicateSet(dbHyps[0], selHints, hypotheses, selHyps, typeEnv);
	}
	
	private static void loadPredicateSet(IPOPredicateSet poPredSet,
			SelectionHints selHints, Set<Predicate> hypotheses, Set<Predicate> selHyps, ITypeEnvironment typeEnv)
			throws RodinDBException {

		final IPOPredicateSet parentSet = poPredSet.getParentPredicateSet();
		if (parentSet != null) {
			loadPredicateSet(parentSet, selHints, hypotheses, selHyps, typeEnv);
		}
		for (final IPOIdentifier poIdent: poPredSet.getIdentifiers()) {
			typeEnv.add(poIdent.getIdentifier(factory));
		}
		
		boolean selected = selHints.contains(poPredSet);
		for (final IPOPredicate poPred : poPredSet.getPredicates()) {
			final Predicate predicate = poPred.getPredicate(factory, typeEnv);
			final Predicate hypothesis = predicate;
			if (!selected && selHints.contains(poPred)) selHyps.add(hypothesis);
			hypotheses.add(hypothesis);
		}
		if (selected) selHyps.addAll(hypotheses);
	}
	
	/**
	 * Reads the goal of the given proof obligation.
	 * 
	 * @param poSeq
	 *            PO to read
	 * @param typeEnv
	 *            type environment to use
	 * @return the goal of the given PO
	 * @throws RodinDBException
	 */
	private static Predicate readGoal(IPOSequent poSeq, ITypeEnvironment typeEnv)
			throws RodinDBException {
		
		IPOPredicate[] dbGoals = poSeq.getGoals();
		if (dbGoals.length == 0) {
			Util.log(null, "No goal for PO " + poSeq);
			return null;
		}
		if (dbGoals.length != 1) {
			Util.log(null, "More than one goal for PO " + poSeq);
		}
		return dbGoals[0].getPredicate(factory, typeEnv);
	}
	
	
	/**
	 * This is a private class that collects selection hints from a PO Sequent.
	 * The collected hints can then be used for querying whether a predicate or
	 * predicate set should be selected.
	 * <p>
	 * The selection hints are collected as handles to PO Predicates, and handles 
	 * to PO Predicate Sets that are contained in the selection hints for a 
	 * PO Sequent.
	 * </p>
	 * 
	 * @author Farhad Mehta
	 *
	 */
	private static class SelectionHints {
		
		private Set<IPOPredicate> preds;
		private Set<IPOPredicateSet> predSets;
		
		
		/**
		 * Collects Selection hint information from a PO sequent
		 * 
		 * @param poSeq
		 * 			The PO sequent to collect hints from
		 * @throws RodinDBException
		 */
		protected SelectionHints(IPOSequent poSeq) throws RodinDBException {
			preds = new HashSet<IPOPredicate>();
			predSets = new HashSet<IPOPredicateSet>();
			IPOSelectionHint[] dbSelHints = poSeq.getSelectionHints();
			for (IPOSelectionHint hint : dbSelHints) {
				IPOPredicateSet end = hint.getEnd();
				if (end == null)
				{
					preds.add(hint.getPredicate());
				}
				else
				{
					IPOPredicateSet start = hint.getStart(); 
					while (end != null && end.getParentPredicateSet() != start)
					{
						predSets.add(end);
						end = end.getParentPredicateSet();
					}
				}
			}
		}
		
		
		/**
		 * A query on the collected selection hints.
		 * 
		 * @param dbPred
		 * 		the poPredicate handle to query for
		 * @return <code>true</code> iff the given poPredicate handle is contained
		 * 			in the collected selection hints.
		 */
		protected boolean contains(IPOPredicate dbPred){
			return preds.contains(dbPred);
		}
		
		/**
		 * A query on the collected selection hints.
		 * 
		 * @param dbPredSet
		 * 		the poPredicateSet handle to query for
		 * @return <code>true</code> iff the given poPredicateSet handle is contained
		 * 			in the collected selection hints.
		 */	
		protected boolean contains(IPOPredicateSet dbPredSet){
			return predSets.contains(dbPredSet);
		}
		
	}

}

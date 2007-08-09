/*******************************************************************************
 * Copyright (c) 2006 ETH Zurich.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/

package org.eventb.internal.pp.core.provers.equality;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;

import org.eventb.internal.pp.core.ClauseSimplifier;
import org.eventb.internal.pp.core.Dumper;
import org.eventb.internal.pp.core.IProver;
import org.eventb.internal.pp.core.IVariableContext;
import org.eventb.internal.pp.core.Level;
import org.eventb.internal.pp.core.ProverResult;
import org.eventb.internal.pp.core.elements.Clause;
import org.eventb.internal.pp.core.elements.EqualityLiteral;
import org.eventb.internal.pp.core.elements.FalseClause;
import org.eventb.internal.pp.core.elements.terms.Constant;
import org.eventb.internal.pp.core.elements.terms.Variable;
import org.eventb.internal.pp.core.inferrers.EqualityInferrer;
import org.eventb.internal.pp.core.inferrers.EqualityInstantiationInferrer;
import org.eventb.internal.pp.core.tracing.ClauseOrigin;
import org.eventb.internal.pp.core.tracing.IOrigin;


public class EqualityProver implements IProver {

	/**
	 * Debug flag for <code>PROVER_EQUALITY_TRACE</code>
	 */
	public static boolean DEBUG = false;
	public static void debug(String message){
		if (DEBUG)
			System.out.println(message);
	}
	
	private IEquivalenceManager manager = new EquivalenceManager();
	
	private ClauseSimplifier simplifier;
	private EqualityInferrer inferrer;
	private EqualityInstantiationInferrer instantiationInferrer;
	
	private HashSet<Clause> equalityInstantiations;
	
	public EqualityProver(IVariableContext context) {
		this.inferrer = new EqualityInferrer(context);
		this.instantiationInferrer = new EqualityInstantiationInferrer(context);
		this.equalityInstantiations = new LinkedHashSet<Clause>();
	}
	
	public void contradiction(Level oldLevel, Level newLevel, Set<Level> dependencies) {
		manager.backtrack(newLevel);
		
		backtrackInstantiations(newLevel);
	}

	private void backtrackInstantiations(Level level) {
		for (Iterator<Clause> iterator = equalityInstantiations.iterator(); iterator.hasNext();) {
			Clause clause = iterator.next();
			if (level.isAncestorOf(clause.getLevel())) iterator.remove();
		}
	}
	
	public void initialize(ClauseSimplifier simplifier) {
		this.simplifier = simplifier;
	}

	public ProverResult next(boolean force) {
		// return equality instantiations here, if not, it loops
		if (equalityInstantiations.isEmpty()) return ProverResult.EMPTY_RESULT;
		Set<Clause> result = new HashSet<Clause>(equalityInstantiations);
		equalityInstantiations.clear();
		return new ProverResult(result, new HashSet<Clause>());
	}

	public void registerDumper(Dumper dumper) {
		dumper.addObject("EqualityFormula table", manager);
	}

	public ProverResult addClauseAndDetectContradiction(Clause clause) {
		Set<Clause> generatedClauses = new HashSet<Clause>();
		Set<Clause> subsumedClauses = new HashSet<Clause>();
		
		addClause(clause, generatedClauses, subsumedClauses);
//		if (origin != null) return new ProverResult(origin);
//		else {
//			if (generatedClauses.isEmpty() && subsumedClauses.isEmpty()) return null;
		return new ProverResult(generatedClauses, subsumedClauses);
//		}
	}
	
	public void removeClause(Clause clause) {
		if (clause.isUnit()) return;
		
		for (EqualityLiteral equality : clause.getEqualityLiterals()) {
			if (equality.isConstant()) manager.removeQueryEquality(equality, clause);
			else if (isInstantiationCandidate(equality)) manager.removeInstantiation(equality, clause);
		}
		for (EqualityLiteral equality : clause.getConditions()) {
			if (equality.isConstant()) manager.removeQueryEquality(equality, clause);
			else if (isInstantiationCandidate(equality)) manager.removeInstantiation(equality, clause);
		}
	}
	
	private boolean isInstantiationCandidate(EqualityLiteral equality) {
		if ((equality.getTerms().get(0) instanceof Variable && equality.getTerms().get(1) instanceof Constant)
				||	(equality.getTerms().get(1) instanceof Variable && equality.getTerms().get(0) instanceof Constant))
			return true;
		return false;
	}
	
	private void addClause(Clause clause, Set<Clause> generatedClauses, Set<Clause> subsumedClauses) {
		if (clause.isUnit() && (clause.getEqualityLiterals().size()>0 || clause.getConditions().size()>0)) {
			EqualityLiteral equality = null;
			if (clause.getConditions().size()==1) equality = clause.getConditions().get(0);
			else equality = clause.getEqualityLiterals().get(0);
			
			if (!equality.isConstant()) {
				// TODO handle this case, x = a or x = y
				return;
			}
			
			IFactResult result = manager.addFactEquality(equality, clause);
			handleFactResult(result, generatedClauses, subsumedClauses);
		}
		else if (clause.getEqualityLiterals().size()>0 || clause.getConditions().size()>0) {
			ArrayList<IQueryResult> queryResult = new ArrayList<IQueryResult>();
			ArrayList<IInstantiationResult> instantiationResult = new ArrayList<IInstantiationResult>();

			// if equivalence, then we do the standard instantiations
			// x=a -> x/a, x\=a -> x/a
			if (clause.isEquivalence())
				doTrivialInstantiations(clause, generatedClauses, subsumedClauses);
			
			handleEqualityList(clause.getEqualityLiterals(), clause,
					queryResult, instantiationResult, !clause.isEquivalence());
			handleEqualityList(clause.getConditions(), clause,
					queryResult, instantiationResult, true);
			handleQueryResult(queryResult, generatedClauses, subsumedClauses);
			handleInstantiationResult(instantiationResult, equalityInstantiations);
		}
	}

	private void handleEqualityList(List<EqualityLiteral> equalityList, Clause clause,
			List<IQueryResult> queryResult, List<IInstantiationResult> instantiationResult,
			boolean handleOnlyPositives) {
		for (EqualityLiteral equality : equalityList) {
			if (equality.isConstant()) {
				IQueryResult result = manager.addQueryEquality(equality, clause);
				if (result != null) queryResult.add(result);
			}
			else if (handleOnlyPositives?equality.isPositive():true) {
				if (isInstantiationCandidate(equality)) {
					List<? extends IInstantiationResult> result = manager.addInstantiationEquality(equality, clause);
					if (result != null) instantiationResult.addAll(result);
					
				}
			}
			// TODO handle other cases x = a or x = y or #x.x=y etc ...
		}
	}
	
	private void doTrivialInstantiations(Clause clause,
			Set<Clause> generatedClauses, Set<Clause> subsumedClauses) {
		for (EqualityLiteral equality : clause.getEqualityLiterals()) {
			if (isInstantiationCandidate(equality)) {
				Constant constant = null;
				if (equality.getTerms().get(0) instanceof Constant) constant = (Constant)equality.getTerms().get(0);
				else if (equality.getTerms().get(1) instanceof Constant) constant = (Constant)equality.getTerms().get(1);
				instantiationInferrer.addEqualityEqual(equality, constant);
				
				clause.infer(instantiationInferrer);
				Clause inferredClause = instantiationInferrer.getResult();
				
				inferredClause = simplifier.run(inferredClause);
//				if (inferredClause.isFalse()) {
//					return inferredClause.getOrigin();
//				}
//				if (!inferredClause.isTrue()) 
				generatedClauses.add(inferredClause);
			}
		}
	}

	private void handleFactResult(IFactResult result,
			Set<Clause> generatedClauses, Set<Clause> subsumedClauses) {
		if (result == null) return;
		if (result.hasContradiction()) {
			List<Clause> contradictionOrigin = result.getContradictionOrigin();
			IOrigin origin = new ClauseOrigin(contradictionOrigin);
			generatedClauses.add(new FalseClause(origin));
		}
		else {
			if (result.getSolvedQueries() != null) handleQueryResult(result.getSolvedQueries(), generatedClauses, subsumedClauses);
			if (result.getSolvedInstantiations() != null) handleInstantiationResult(result.getSolvedInstantiations(), equalityInstantiations);
		}
	}
	
	private <T> void addToList(Map<Clause, Set<T>> values, Clause clause, T equality) {
		if (!values.containsKey(clause)) {
			Set<T> equalities = new HashSet<T>();
			values.put(clause, equalities);
		}
		values.get(clause).add(equality);
	}
	
	private void handleInstantiationResult(List<? extends IInstantiationResult> result,
			Set<Clause> generatedClauses) {
		if (result == null) return;
		for (IInstantiationResult insRes : result) {
			for (Clause clause : insRes.getSolvedClauses()) {
				instantiationInferrer.addEqualityUnequal(insRes.getEquality(), insRes.getInstantiationValue());
				instantiationInferrer.addParentClauses(new ArrayList<Clause>(insRes.getSolvedValueOrigin()));
				clause.infer(instantiationInferrer);
				Clause inferredClause = instantiationInferrer.getResult();
				
				inferredClause = simplifier.run(inferredClause);
//				if (inferredClause.isFalse()) {
//					return inferredClause.getOrigin();
//				}
//				if (!inferredClause.isTrue()) 
				generatedClauses.add(inferredClause);
			}
		}
	}
	
//	private void handleInstantiationResult(List<? extends IInstantiationResult> result) {
//		if (result == null) return;
//		Map<Clause, Map<EqualityFormula, Constant>> values = new HashMap<Clause, Map<EqualityFormula,Constant>>();
//		Map<Clause, Set<Clause>> origins = new HashMap<Clause, Set<Clause>>();
//		
//		for (IInstantiationResult insRes : result) {
//			for (Clause clause : insRes.getSolvedClauses()) {
//				if (!values.containsKey(clause)) {
//					values.put(clause, new HashMap<EqualityFormula, Constant>());
//					origins.put(clause, new HashSet<Clause>());
//				}
//				Map<EqualityFormula, Constant> map = values.get(clause);
//				map.put(insRes.getEquality(), insRes.getInstantiationValue());
//				Set<Clause> origin = origins.get(clause);
//				origin.addAll(insRes.getSolvedClauses());
//			}
//		}
//		
//		for (Entry<Clause, Map<EqualityFormula, Constant>> entry : values.entrySet()) {
//			for (Entry<EqualityFormula, Constant> entry2 : entry.getValue().entrySet()) {
//				instantiationInferrer.addEquality(entry2.getKey(), entry2.getValue());
//			}
//			instantiationInferrer.addParentClauses(new ArrayList<Clause>(origins.get(entry.getKey())));
//			entry.getKey().infer(instantiationInferrer);
//			Clause inferredClause = instantiationInferrer.getResult();
//			
//			inferredClause = simplifier.run(inferredClause);
//			if (inferredClause.isFalse()) {
//				dispatcher.contradiction(inferredClause.getOrigin());
//				return;
//			}
//			if (!inferredClause.isTrue()) generatedClauses.appends(inferredClause);
//		}
//	}
	
	// takes a query result
	private void handleQueryResult(List<? extends IQueryResult> result,
			Set<Clause> generatedClauses, Set<Clause> subsumedClauses) {
		if (result == null) return;
		Map<Clause, Set<EqualityLiteral>> trueValues = new HashMap<Clause, Set<EqualityLiteral>>();
		Map<Clause, Set<EqualityLiteral>> falseValues = new HashMap<Clause, Set<EqualityLiteral>>();
		Map<Clause, Set<Clause>> clauses = new HashMap<Clause, Set<Clause>>();
		
		// take into account the level of the clause
		// -> done by the prover
		for (IQueryResult queryResult : result) {
			Map<Clause, Set<EqualityLiteral>> map = queryResult.getValue()?trueValues:falseValues;
			for (Clause clause : queryResult.getSolvedClauses()) {
				for (Clause originClause : queryResult.getSolvedValueOrigin()) {
					addToList(clauses, clause, originClause);
				}
				addToList(map, clause, queryResult.getEquality());
			}
		}
		
		for (Entry<Clause, Set<Clause>> entry : clauses.entrySet()) {
			if (trueValues.containsKey(entry.getKey())) {
				for (EqualityLiteral equality : trueValues.get(entry.getKey())) {
					inferrer.addEquality(equality, true);
				}
			}
			if (falseValues.containsKey(entry.getKey())) {
				for (EqualityLiteral equality : falseValues.get(entry.getKey())) {
					inferrer.addEquality(equality, false);
				}
			}
			inferrer.addParentClauses(new ArrayList<Clause>(entry.getValue()));
			entry.getKey().infer(inferrer);
			Clause inferredClause = inferrer.getResult();
			inferredClause = simplifier.run(inferredClause);
//			if (inferredClause.isFalse()) {
//				return inferredClause.getOrigin();
//			}
//			if (!inferredClause.isTrue()) 
			generatedClauses.add(inferredClause);
			if (inferredClause.getLevel().compareTo(entry.getKey().getLevel()) <= 0) 
				subsumedClauses.add(entry.getKey());
		}
	}

	@Override
	public String toString() {
		return "EqualityProver";
	}

}

/*******************************************************************************
 * Copyright (c) 2006 ETH Zurich.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/

package org.eventb.internal.pp.core.provers.predicate;


import java.util.HashSet;
import java.util.Set;

import org.eventb.internal.pp.core.ClauseSimplifier;
import org.eventb.internal.pp.core.Dumper;
import org.eventb.internal.pp.core.IProver;
import org.eventb.internal.pp.core.IVariableContext;
import org.eventb.internal.pp.core.Level;
import org.eventb.internal.pp.core.ProverResult;
import org.eventb.internal.pp.core.datastructure.DataStructureWrapper;
import org.eventb.internal.pp.core.elements.Clause;
import org.eventb.internal.pp.core.inferrers.InferrenceResult;
import org.eventb.internal.pp.core.inferrers.ResolutionInferrer;
import org.eventb.internal.pp.core.search.IterableHashSet;
import org.eventb.internal.pp.core.search.ResetIterator;
import org.eventb.internal.pp.core.tracing.AbstractInferrenceOrigin;
import org.eventb.internal.pp.core.tracing.IOrigin;

public class PredicateProver implements IProver {

	/**
	 * Debug flag for <code>PROVER_CASESPLIT_TRACE</code>
	 */
	public static boolean DEBUG = false;
	public static void debug(String message){
		System.out.println(message);
	}
	
	private DataStructureWrapper unitClausesWrapper;
	private IterableHashSet<Clause> unitClauses;
	private ResetIterator<Clause> unitClausesIterator;
	private IterableHashSet<Clause> nonUnitClauses;
	
	private ResolutionInferrer inferrer;
	private ResolutionResolver nonUnitResolver;
	private ResolutionResolver unitResolver;
	private ReverseResolutionResolver conditionResolver;
	
	private ClauseSimplifier simplifier;
	private UnitMatcher unitMatcher;
	
	public PredicateProver(IVariableContext context) {
		this.inferrer = new ResolutionInferrer(context);
		
		unitClauses = new IterableHashSet<Clause>();
		// TODO remove
		unitClausesWrapper = new DataStructureWrapper(unitClauses);
		nonUnitClauses = new IterableHashSet<Clause>();
		
		unitMatcher = new UnitMatcher(unitClausesWrapper);
		
		nonUnitResolver = new ResolutionResolver(inferrer, new IteratorMatchIterator(nonUnitClauses.iterator()));
		unitResolver = new ResolutionResolver(inferrer, new UnitMatchIterator(unitMatcher));
		conditionResolver = new ReverseResolutionResolver(inferrer, new UnitMatchIterator(unitMatcher));
		
		unitClausesIterator = unitClauses.iterator();
	}
	
	public void initialize(ClauseSimplifier simplifier) {
		this.simplifier = simplifier;
	}
	
	private int counter = 0;
	private boolean isNextAvailable() {
		if (counter > 0) {
			counter--;
			return true;
		}
		else {
			counter = unitClauses.size()*10;
			return false;
		}
	}
	
	public ProverResult next(boolean force) {
		// TODO refactor this 
		
		if (simplifier == null) throw new IllegalStateException();

		if (!force && !isNextAvailable()) return ProverResult.EMPTY_RESULT; 

		ProverResult result = null;
		if (!nonUnitResolver.isInitialized()) {
			Clause unit = nextUnit();
			if (unit == null) return ProverResult.EMPTY_RESULT;
			nonUnitResolver.initialize(unit);
		}
		InferrenceResult nextClause = nonUnitResolver.next(force);
		while (nextClause == null) {
			Clause unit = nextUnit();
			if (unit == null) return ProverResult.EMPTY_RESULT;
			else newClause(unit, unitResolver);
			nonUnitResolver.initialize(unit);
			nextClause = nonUnitResolver.next(force);
		}
		if (nextClause != null) {
			Clause clause = nextClause.getClause();
			clause = simplifier.run(clause);

			result = new ProverResult(clause, nextClause.getSubsumedClauses());
		}
		if (DEBUG) debug("PredicateProver, next clause: "+result);
		return result;
	}
	
	private Clause nextUnit() {
		if (unitClausesIterator.hasNext()) return unitClausesIterator.next();
		return null;
	}
	
	public ProverResult newClause(Clause clause, IResolver resolver) {
		Set<Clause> generatedClauses = new HashSet<Clause>();
		Set<Clause> subsumedClauses = new HashSet<Clause>();
		
		resolver.initialize(clause);
		InferrenceResult result = resolver.next(true);
		while (result != null) {
			subsumedClauses.addAll(result.getSubsumedClauses());
			
			Clause inferredClause = result.getClause();
			inferredClause = simplifier.run(inferredClause);
			generatedClauses.add(inferredClause);
			
			result = resolver.next(true);
		}
		return new ProverResult(generatedClauses, subsumedClauses);
	}
	
	
	private boolean isAcceptedUnitClause(Clause clause) {
		return 	clause.isUnit() 
				&& clause.getPredicateLiterals().size() > 0;
//				&& !clause.hasQuantifiedLiteral();
	}
	
	private boolean isAcceptedNonUnitClause(Clause clause) {
		 return	clause.getPredicateLiterals().size()>0 
				&& !clause.isUnit()
				&& !clause.isBlockedOnConditions();
//				&& !clause.hasQuantifiedLiteral();
	}
	
	
	public ProverResult addClauseAndDetectContradiction(Clause clause) {
		if (simplifier == null) throw new IllegalStateException();
		
		if (isAcceptedUnitClause(clause)) {
			unitClausesWrapper.add(clause);
			// we generate the clauses
			if (!clause.hasQuantifiedLiteral()) return newClause(clause, unitResolver);
		}
		else if (isAcceptedNonUnitClause(clause)) {
			nonUnitClauses.appends(clause);
			if (hadConditions(clause) /* || hadQuantifier(clause) */) return newClause(clause, conditionResolver);
		}
		return ProverResult.EMPTY_RESULT;
	}

	public void removeClause(Clause clause) {
		if (isAcceptedUnitClause(clause)) {
			unitClausesWrapper.remove(clause);
		}
		else if (isAcceptedNonUnitClause(clause)) {
			nonUnitClauses.remove(clause);
		}
		nonUnitResolver.remove(clause);
		unitResolver.remove(clause);
	}
	
//	private boolean hadQuantifier(Clause clause) {
//		IOrigin origin = clause.getOrigin();
//		if (origin instanceof AbstractInferrenceOrigin) {
//			AbstractInferrenceOrigin tmp = (AbstractInferrenceOrigin)origin;
//			for (Clause parent : tmp.getClauses()) {
//				if (parent.hasQuantifiedLiteral()) return true;
//			}
//		}
//		return false;
//	}

	// TODO dirty
	private boolean hadConditions(Clause clause) {
		IOrigin origin = clause.getOrigin();
		if (origin instanceof AbstractInferrenceOrigin) {
			AbstractInferrenceOrigin tmp = (AbstractInferrenceOrigin)origin;
			for (Clause parent : tmp.getClauses()) {
				if (parent.isBlockedOnConditions()) return true;
			}
		}
		return false;
	}

	public void contradiction(Level oldLevel, Level newLevel, Set<Level> dependencies) {
		// do nothing
	}

	public void registerDumper(Dumper dumper) {
		dumper.addDataStructure("PredicateFormula unit clauses", unitClauses.iterator());
		dumper.addObject("Current unit clause", new Object(){
			@SuppressWarnings("synthetic-access")
			@Override
			public String toString() {
				return unitClausesIterator.current()==null?"no current unit clause":unitClausesIterator.current().toString();
			}
		});
		dumper.addDataStructure("PredicateFormula non-unit clauses", nonUnitClauses.iterator());
	}
	
	@Override
	public String toString() {
		return "PredicateProver";
	}
	
	
}



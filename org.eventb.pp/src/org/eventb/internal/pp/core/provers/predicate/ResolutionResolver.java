/*******************************************************************************
 * Copyright (c) 2006 ETH Zurich.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/

package org.eventb.internal.pp.core.provers.predicate;

import java.util.Iterator;

import org.eventb.internal.pp.core.ClauseDispatcher;
import org.eventb.internal.pp.core.elements.Clause;
import org.eventb.internal.pp.core.elements.PredicateLiteral;
import org.eventb.internal.pp.core.inferrers.ResolutionInferrer;
import org.eventb.internal.pp.core.provers.predicate.iterators.IMatchIterator;

/**
 * This class is responsible for applying the resolution rule between one
 * unit and one non-unit clause. It is initialized with a new pair of clauses
 * and then, each call of {@link #nextMatch(ClauseDispatcher)} returns a new inferred clause,
 * until no more matches are available.
 *
 * @author François Terrier
 *
 */
public class ResolutionResolver implements IResolver {

	private ResolutionInferrer inferrer;
	private IMatchIterator matchedClauses;
	
	public ResolutionResolver(ResolutionInferrer inferrer, IMatchIterator matchedClauses) {
		this.inferrer = inferrer;
		this.matchedClauses = matchedClauses;
	}
	
	private Clause currentMatcher;
	private Clause currentMatched;
	private Iterator<Clause> currentMatchedIterator;
	private int currentPosition;

	public ResolutionResult next() {
		if (!isInitialized()) throw new IllegalStateException();
		
		if (nextPosition()) return doMatch();
		while (nextMatchedClause()) {
			if (nextPosition()) {
				return doMatch();
			}
		}
		return null;
	}
	
	public boolean isInitialized() {
		return currentMatchedIterator != null;
	}
	
	public void initialize(Clause matcher) {
		assert matcher.isUnit();
		
		currentMatcher = matcher;
		currentMatched = null;
		currentPosition = -1;
		initMatchedIterator();
	}
	
	private void initMatchedIterator() {
		PredicateLiteral predicate = currentMatcher.getPredicateLiteral(0);
		currentMatchedIterator = matchedClauses.iterator(predicate.getDescriptor(), predicate.isPositive());
	}
	
	public void remove(Clause clause) {
		if (currentMatched != null && clause.equalsWithLevel(currentMatched)) {
			currentMatched = null;
			currentPosition = -1;
		}
		if (currentMatcher != null && clause.equalsWithLevel(currentMatcher)) {
			currentMatchedIterator = null;
			currentMatched = null;
			currentMatcher = null;
			currentPosition = -1;
		}
	}
	
	private ResolutionResult doMatch() {
		inferrer.setPosition(currentPosition);
		inferrer.setUnitClause(currentMatcher);
		currentMatched.infer(inferrer);
		ResolutionResult result = new ResolutionResult(inferrer.getDerivedClause(), inferrer.getSubsumedClause());
		if (PredicateProver.DEBUG) PredicateProver.debug("Inferred clause: "+currentMatcher+" + "+currentMatched+" -> "+result.getDerivedClause());
		return result;
	}
	
	private boolean nextMatchedClause() {
		if (!currentMatchedIterator.hasNext()) return false;
		currentMatched = currentMatchedIterator.next();
		currentPosition = -1;
		return true;
	}
	
	private boolean nextPosition() {
		if (currentMatched == null) return false;
		for (int i = currentPosition+1; i < currentMatched.getPredicateLiteralsSize(); i++) {
			PredicateLiteral matcherPredicate = currentMatcher.getPredicateLiteral(0);
			if (currentMatched.matchesAtPosition(matcherPredicate.getDescriptor(), matcherPredicate.isPositive(), i)) {
				currentPosition = i;
				return true;
			}
		}
		return false;
	}
	
}

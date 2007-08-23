/*******************************************************************************
 * Copyright (c) 2006 ETH Zurich.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/

package org.eventb.internal.pp.core.elements;

import java.util.ArrayList;

import org.eventb.internal.pp.core.inferrers.IInferrer;
import org.eventb.internal.pp.core.simplifiers.ISimplifier;
import org.eventb.internal.pp.core.tracing.IOrigin;

/**
 * Concrete implementation of {@link Clause} for ⊥.
 *
 * @author François Terrier
 *
 */
public final class FalseClause extends Clause {

	private static final int BASE_HASHCODE = 11;
	
	FalseClause(IOrigin origin) {
		super(origin, new ArrayList<PredicateLiteral>(), new ArrayList<EqualityLiteral>(), new ArrayList<ArithmeticLiteral>(), BASE_HASHCODE);
	}

	@Override
	protected void computeBitSets() {
		// nothing
	}

	@Override
	public void infer(IInferrer inferrer) {
		// nothing
	}

	@Override
	public Clause simplify(ISimplifier simplifier) {
		return this;
	}

	@Override
	public boolean isFalse() {
		return true;
	}

	@Override
	public boolean isTrue() {
		return false;
	}

	@Override
	public String toString() {
		return "FALSE";
	}

	@Override
	public boolean isEquivalence() {
		return false;
	}

	@Override
	public boolean matches(PredicateLiteralDescriptor predicate, boolean isPositive) {
		return false;
	}

	@Override
	public boolean matchesAtPosition(PredicateLiteralDescriptor predicate, boolean isPositive, int position) {
		return false;
	}
	
	@Override
	public boolean equals(Object obj) {
		return obj == this;
	}

	@Override
	public int hashCode() {
		return System.identityHashCode(this);
	}

}

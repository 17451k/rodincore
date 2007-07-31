package org.eventb.internal.pp.core.elements;

import java.util.ArrayList;

import org.eventb.internal.pp.core.inferrers.IInferrer;
import org.eventb.internal.pp.core.simplifiers.ISimplifier;
import org.eventb.internal.pp.core.tracing.IOrigin;

public class TrueClause extends Clause {

	private static final int BASE_HASHCODE = 13;
	
	public TrueClause(IOrigin origin) {
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
		return false;
	}

	@Override
	public boolean isTrue() {
		return true;
	}

	@Override
	public String toString() {
		return "TRUE";
	}

	@Override
	public boolean isEquivalence() {
		return false;
	}

	@Override
	public boolean matches(PredicateDescriptor predicate) {
		return false;
	}

	@Override
	public boolean matchesAtPosition(PredicateDescriptor predicate, int position) {
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

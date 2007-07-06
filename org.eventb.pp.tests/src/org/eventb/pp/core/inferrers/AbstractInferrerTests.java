package org.eventb.pp.core.inferrers;

import java.util.HashSet;
import java.util.Set;

import org.eventb.internal.pp.core.elements.Clause;
import org.eventb.internal.pp.core.elements.EqualityLiteral;
import org.eventb.internal.pp.core.elements.PredicateLiteral;
import org.eventb.internal.pp.core.elements.terms.Term;
import org.eventb.internal.pp.core.elements.terms.Variable;
import org.eventb.pp.AbstractPPTest;

public abstract class AbstractInferrerTests extends AbstractPPTest {

	protected void disjointVariables(Clause clause1, Clause clause2) {
		Set<Variable> vars1 = collectVariables(clause1);
		Set<Variable> vars2 = collectVariables(clause2);
		for (Variable variable : vars2) {
			assertFalse("Clause1: "+clause1.toString()+", clause2: "+clause2.toString(),
					vars1.contains(variable));
		}
		for (Variable variable : vars1) {
			assertFalse("Clause1: "+clause1.toString()+", clause2: "+clause2.toString(),
					vars2.contains(variable));
		}
	}

	private Set<Variable> collectVariables(Clause clause) {
		Set<Variable> vars = new HashSet<Variable>();
		for (PredicateLiteral literal : clause.getPredicateLiterals()) {
			for (Term term : literal.getTerms()) {
				term.collectVariables(vars);
			}
		}
		for (EqualityLiteral equality : clause.getEqualityLiterals()) {
			for (Term term : equality.getTerms()) {
				term.collectVariables(vars);
			}
		}
		return vars;
	}
}

package org.eventb.pp.core.simplifiers;

import static org.eventb.internal.pp.core.elements.terms.Util.cClause;
import static org.eventb.internal.pp.core.elements.terms.Util.cEqClause;
import static org.eventb.internal.pp.core.elements.terms.Util.cEqual;
import static org.eventb.internal.pp.core.elements.terms.Util.cNEqual;
import static org.eventb.internal.pp.core.elements.terms.Util.cNotPred;
import static org.eventb.internal.pp.core.elements.terms.Util.cPred;
import static org.eventb.internal.pp.core.elements.terms.Util.mList;

import java.util.ArrayList;

import org.eventb.internal.pp.core.elements.Clause;
import org.eventb.internal.pp.core.elements.Literal;
import org.eventb.internal.pp.core.elements.terms.AbstractPPTest;
import org.eventb.internal.pp.core.elements.terms.VariableContext;
import org.eventb.internal.pp.core.simplifiers.EqualitySimplifier;

@SuppressWarnings("unchecked")
public class TestEqualitySimplifier extends AbstractPPTest {

	
	private class TestPair {
		Clause input, output;
		
		TestPair(Clause input, Clause output) {
			this.input = input;
			this.output = output;
		}
	}
	
	TestPair[] tests = new TestPair[] {
			new TestPair(
					cClause(cNEqual(a,a)),
					FALSE
			),
			// simple equality
			new TestPair(
					cClause(cEqual(a,a)),
					TRUE
			),
			// simple inequality with variables
			new TestPair(
					cClause(cNEqual(var0,var0)),
					FALSE
			),
			// simple equality with variables
			new TestPair(
					cClause(cEqual(var0,var0)),
					TRUE
			),
			// simple inequality with variables
			new TestPair(
					cClause(cNEqual(evar0,evar0)),
					FALSE
			),
			
			// do nothing
			new TestPair(
					cClause(cNEqual(a,b)),
					cClause(cNEqual(a,b))
			),
			// do nothing
			new TestPair(
					cClause(cNEqual(var0,var1)),
					cClause(cNEqual(var0,var1))
			),
			// do nothing
			new TestPair(
					cClause(cNEqual(evar0,evar1)),
					cClause(cNEqual(evar0,evar1))
			),
			
			// more than one literal
			new TestPair(
					cClause(cNEqual(a,a),cPred(0,a)),
					cClause(cPred(0,a))
			),
			new TestPair(
					cClause(cNEqual(a,a),cNEqual(var0,var0),cPred(0,a)),
					cClause(cPred(0,a))
			),
			new TestPair(
					cClause(cNEqual(a,a),cEqual(a,a)),
					TRUE
			),
			new TestPair(
					cClause(cPred(0,a),cEqual(a,a)),
					TRUE
			),
			
			// EQUIVALENCE
			new TestPair(
					cEqClause(cNEqual(a, a),cPred(0,a)),
					cClause(cNotPred(0,a))
			),
			new TestPair(
					cEqClause(cNEqual(a,a),cEqual(a,a)),
					FALSE
			),
			new TestPair(
					cEqClause(cNEqual(a,a),cEqual(a,a),cPred(0,a)),
					cClause(cNotPred(0,a))
			),
			new TestPair(
					cEqClause(cNEqual(a,a),cNEqual(a,a),cPred(0,a)),
					cClause(cPred(0,a))
			),
			new TestPair(
					cEqClause(cEqual(a,a),cEqual(a,a),cPred(0,a)),
					cClause(cPred(0,a))
			),
			new TestPair(
					cEqClause(cEqual(a,a),cEqual(a,a)),
					TRUE
			),
			
			// EQUIVALENCE and conditions
			new TestPair(
					cEqClause(mList(cPred(0,a),cPred(1,a)),cNEqual(a,a)),
					cEqClause(cPred(0,a),cPred(1,a))
			),
			new TestPair(
					cEqClause(mList(cPred(0,a),cPred(1,a)),cNEqual(a,a),cNEqual(b,b)),
					cEqClause(cPred(0,a),cPred(1,a))
			),
			new TestPair(
					cEqClause(mList(cPred(0,a),cNEqual(a,a)),cNEqual(a,a)),
					cClause(cNotPred(0,a))
			),
			
			// DISJUNCTIVE with conditions
			new TestPair(
					cClause(mList(cPred(0,a),cPred(1,a)),cNEqual(a,a)),
					cClause(cPred(0,a),cPred(1,a))
			),
			new TestPair(
					cClause(mList(cPred(0,a),cPred(1,a)),cNEqual(a,a),cNEqual(b,b)),
					cClause(cPred(0,a),cPred(1,a))
			),
			new TestPair(
					cClause(mList(cPred(0,a),cNEqual(a,a))),
					cClause(cPred(0,a))
			),
			new TestPair(
					cClause(new ArrayList<Literal<?,?>>(),cNEqual(a,a)),
					FALSE
			),
			
	};
	
	private VariableContext variableContext() {
		return new VariableContext();
	}
	
	
	public void testEquality() {
		for (TestPair test : tests) {
			EqualitySimplifier rule = new EqualitySimplifier(variableContext());
			
			
			assertTrue(rule.canSimplify(test.input));
			Clause actual = test.input.simplify(rule);
			
			if (actual.isFalse()) assertTrue(test.output.isFalse());
			else if (actual.isTrue()) assertTrue(test.output.isTrue());
			else assertEquals(test.input.toString(),test.output,actual);
		}
	}
}

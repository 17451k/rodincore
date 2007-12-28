package org.eventb.pp.loader;

import junit.framework.TestCase;

import org.eventb.core.ast.FormulaFactory;
import org.eventb.core.ast.ITypeEnvironment;
import org.eventb.core.ast.Predicate;
import org.eventb.core.ast.Type;
import org.eventb.internal.pp.loader.formula.AbstractFormula;
import org.eventb.internal.pp.loader.formula.SignedFormula;
import org.eventb.internal.pp.loader.formula.descriptor.LiteralDescriptor;
import org.eventb.internal.pp.loader.predicate.PredicateLoader;

/**
 * This class tests that two predicates having the same terms as arguments 
 * with the same corresponding types are constructed with the same name. It
 * also tests that two different predicates are not constructed with the
 * same name. 
 *
 * @author François Terrier
 *
 */
public class TestSamePredicate extends TestCase {
	static FormulaFactory ff = FormulaFactory.getDefault();
	
	static ITypeEnvironment env = ff.makeTypeEnvironment();
	static {
		Type S = ff.makeGivenType("S");
		Type T = ff.makeGivenType("T");
		
		env.addName("a", S);
		env.addName("b", S);
		env.addName("S1", ff.makePowerSetType(S));
		env.addName("S2", ff.makePowerSetType(S));
		
		env.addName("c", T);
		env.addName("d", T);
		env.addName("T1", ff.makePowerSetType(T));
		env.addName("T2", ff.makePowerSetType(T));
		
		env.addName("e", ff.makeBooleanType());
		env.addName("f", ff.makeBooleanType());
		
		env.addName("SS1", ff.makePowerSetType(ff.makeProductType(S,S)));
		env.addName("SS2", ff.makePowerSetType(ff.makeProductType(S,S)));
		
		env.addName("TT1", ff.makePowerSetType(ff.makeProductType(T,T)));
		env.addName("TT2", ff.makePowerSetType(ff.makeProductType(T,T)));
		
		env.addName("k", ff.makeIntegerType());
		env.addName("l", ff.makeIntegerType());
		
		env.addName("NS", ff.makePowerSetType(ff.makeProductType(ff.makeIntegerType(), S)));
		env.addName("NN", ff.makePowerSetType(ff.makeProductType(ff.makeIntegerType(), ff.makeIntegerType())));
		env.addName("SN", ff.makePowerSetType(ff.makeProductType(S, ff.makeIntegerType())));
		env.addName("STN", ff.makePowerSetType(ff.makeProductType(ff.makeProductType(S, T), ff.makeIntegerType())));
		
	}
	
	String[][] test1 = new String[][]{
			new String[]{
				"a ∈ S1", "b ∈ S1", "a ∈ S2", "b ∈ S2"
			},
			new String[]{
				"c ∈ T1", "d ∈ T1", "c ∈ T2", "d ∈ T2"
			},
			new String[]{
				"a ↦ b ∈ SS1", "b ↦ a ∈ SS1", "a ↦ b ∈ SS2", "b ↦ a ∈ SS2"
			},
			new String[]{
				"c ↦ d ∈ TT1", "d ↦ c ∈ TT1", "c ↦ d ∈ TT2", "d ↦ c ∈ TT2"
			},
//			new String[]{
//				"k ↦ a ∈ NS", "k ↦ b ∈ NS", "k + 1 ↦ b ∈ NS", "k ∗ 1 ↦ b ∈ NS"
//			},
//			new String[]{
//				"k ↦ k ∈ NN", "k ↦ k + 1 ∈ NN"
//			},
//			new String[]{
//				"a ↦ k ∈ SN", "b ↦ k ∈ SN", "b ↦ k + 1 ∈ SN", "b ↦ k ∗ 1 ∈ SN"
//			},
//			new String[]{
//				"(a ↦ c) ↦ 1 ∈ STN", "(b ↦ d) ↦ k ∈ STN", "(a ↦ d) ↦ 1 ∈ STN", "(b ↦ c) ↦ k + 1 ∈ STN"
//			},

			new String[]{
				"e = f", "e = TRUE", "f = TRUE", "¬(e = TRUE)", "¬(f = TRUE)"
			},
			new String[]{
				"a = b", "b = a"
			},
			new String[]{
				"c = d", "d = c"
			},
			// TODO decide if it is the same literal
//			new String[]{
//				"k = 1", "k = 2", "k = 2 + k"
//			},
				
			// TODO clauses !
	};
	
	private SignedFormula<?> build(PredicateLoader builder, String test) {
		Predicate expr = ff.parsePredicate(test).getParsedPredicate();
		expr.typeCheck(env);
		builder.build(expr,false);
		return builder.getContext().getResults().get(0).getSignature();
	}
	
	public void testSamePredicate() {
		for (String[] tests : test1) {
			PredicateLoader builder = new PredicateLoader();
			LiteralDescriptor desc = null;
			for (String test : tests) {
				AbstractFormula<?> pp = ((SignedFormula<?>)build(builder, test)).getFormula();
				if (desc == null) desc = pp.getLiteralDescriptor();
				else assertEquals(desc, pp.getLiteralDescriptor());
			}
		}
	}
	
//	public void testDifferentPredicate() {
//		PredicateBuilder builder = new PredicateBuilder();
//		List<LiteralDescriptor> desc = new ArrayList<LiteralDescriptor>();
//		for (String[] tests : test1) {
//			String test = tests[0];
//			AbstractFormula<?> pp = ((SignedFormula<?>)build(builder, test)).getFormula();
//			// TODO: document while a loop below?
//			for (LiteralDescriptor lit : desc) {
//				assertNotSame(lit.toString() + " " + pp.getLiteralDescriptor(), lit, pp.getLiteralDescriptor());
//			}
//			desc.add(pp.getLiteralDescriptor());
//		}
//	}
	
}

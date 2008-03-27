package org.eventb.internal.pp.core.elements.terms;

import static org.eventb.internal.pp.core.elements.terms.Util.cCons;
import static org.eventb.internal.pp.core.elements.terms.Util.cELocVar;
import static org.eventb.internal.pp.core.elements.terms.Util.cFLocVar;
import static org.eventb.internal.pp.core.elements.terms.Util.cIntCons;
import static org.eventb.internal.pp.core.elements.terms.Util.cVar;

import java.util.ArrayList;
import java.util.List;

import junit.framework.TestCase;

import org.eventb.core.ast.BooleanType;
import org.eventb.core.ast.FormulaFactory;
import org.eventb.core.ast.GivenType;
import org.eventb.core.ast.ITypeEnvironment;
import org.eventb.core.ast.IntegerType;
import org.eventb.core.ast.Type;
import org.eventb.internal.pp.core.Level;
import org.eventb.internal.pp.core.ProverResult;
import org.eventb.internal.pp.core.elements.Clause;
import org.eventb.internal.pp.core.elements.EqualityLiteral;
import org.eventb.internal.pp.core.elements.Sort;

public abstract class AbstractPPTest extends TestCase {

	public List<EqualityLiteral> EMPTY = new ArrayList<EqualityLiteral>(); 

	public static FormulaFactory ff = FormulaFactory.getDefault();

	// Types used in these tests
	public static IntegerType INT = ff.makeIntegerType();
	public static BooleanType ty_BOOL = ff.makeBooleanType();

	public static GivenType ty_A = ff.makeGivenType("A");
	public static GivenType ty_B = ff.makeGivenType("B");
	public static GivenType ty_C = ff.makeGivenType("C");
	public static GivenType ty_D = ff.makeGivenType("D");
	public static GivenType ty_S = ff.makeGivenType("S");
	public static GivenType ty_T = ff.makeGivenType("T");
	public static GivenType ty_U = ff.makeGivenType("U");
	public static GivenType ty_V = ff.makeGivenType("V");
	public static GivenType ty_M = ff.makeGivenType("M");

	public static Type POW(Type base) {
		return ff.makePowerSetType(base);
	}

	public static Type CPROD(Type left, Type right) {
		return ff.makeProductType(left, right);
	}
	
	public static Type REL(Type left, Type right) {
		return ff.makeRelationalType(left, right);
	}
	
	public static ITypeEnvironment mTypeEnvironment(Object...objs) {
		assert (objs.length & 1) == 0;
		ITypeEnvironment result = ff.makeTypeEnvironment();
		for (int i = 0; i < objs.length; i += 2) {
			result.addName((String) objs[i], (Type) objs[i+1]);
		}
		return result;
	}
	
	public static Level BASE = Level.BASE;
	public static Level ONE = BASE.getLeftBranch();
	public static Level TWO = BASE.getRightBranch();
	public static Level THREE = ONE.getLeftBranch();
	public static Level FOUR = ONE.getRightBranch();
	public static Level FIVE = TWO.getLeftBranch();
	public static Level SIX = TWO.getRightBranch();
	public static Level SEVEN = THREE.getLeftBranch();
	public static Level EIGHT = THREE.getRightBranch();
	public static Level NINE = FOUR.getLeftBranch();
	public static Level TEN = FOUR.getRightBranch();
	public static Level ELEVEN = FIVE.getLeftBranch();
	public static Level NINETEEN = NINE.getLeftBranch();
	public static Level TWENTY = NINE.getRightBranch();
	
	public static Sort A = Util.A;
	public static Sort B = Util.mSort(ty_B);
	public static Sort C = Util.mSort(ty_C);
	public static Sort D = Util.mSort(ty_D);
	public static Sort S = Util.mSort(ty_S);
	public static Sort T = Util.mSort(ty_T);
	public static Sort U = Util.mSort(ty_U);
	public static Sort V = Util.mSort(ty_V);
	public static Sort PA = Util.mSort(POW(ty_A));
	public static Sort PB = Util.mSort(POW(ty_B));
	public static Sort PC = Util.mSort(POW(ty_C));
	public static Sort PD = Util.mSort(POW(ty_D));
	public static Sort PS = Util.mSort(POW(ty_S));
	public static Sort PSS = Util.mSort(REL(ty_S, ty_S));
	public static Sort PAB = Util.mSort(REL(ty_A,ty_B));
	public static Sort PAA = Util.mSort(REL(ty_A,ty_A));
	public static Sort PAC = Util.mSort(REL(ty_A,ty_C));
	public static Sort PBC = Util.mSort(REL(ty_B,ty_C));
	public static Sort NAT = Sort.NATURAL;
	public static Sort BOOL = Sort.BOOLEAN;
	
	public static Variable x = cVar(1, A);
	public static Variable y = cVar(2, A);
	public static Variable z = cVar(3, A);
	
	public static IntegerConstant zero = cIntCons(0);
	public static IntegerConstant one = cIntCons(1);
	
	public static Constant a = cCons("a", A);
	public static Constant b = cCons("b", A);
	public static Constant c = cCons("c", A);
	public static Constant d = cCons("d", A);
	public static Constant e = cCons("e", A);
	public static Constant f = cCons("f", A);
	
	public static Variable var00 = cVar(2, A);
	public static Variable var11 = cVar(3, A);
	public static Variable var0 = cVar(4, A);
	public static Variable var1 = cVar(5, A);
	public static Variable var2 = cVar(6, A);
	public static Variable var3 = cVar(7, A);
	public static Variable var4 = cVar(8, A);
	
	public static LocalVariable evar0 = cELocVar(0, A);
	public static LocalVariable evar1 = cELocVar(1, A);
	public static LocalVariable evar2 = cELocVar(2, A);
	public static LocalVariable fvar0 = cFLocVar(0, A);
	public static LocalVariable fvar1 = cFLocVar(1, A);
	public static LocalVariable fvar2 = cFLocVar(2, A);
	
	public static EqualityLiteral ab = Util.cEqual(a,b);
	public static EqualityLiteral ac = Util.cEqual(a,c);
	public static EqualityLiteral nab = Util.cNEqual(a,b);
	public static EqualityLiteral bc = Util.cEqual(b,c);
	public static EqualityLiteral nbc = Util.cNEqual(b,c);
	public static EqualityLiteral cd = Util.cEqual(c,d);
	public static EqualityLiteral ncd = Util.cNEqual(c,d);
	public static EqualityLiteral nbd = Util.cNEqual(b,d);
	public static EqualityLiteral nac = Util.cNEqual(a,c);
	
	public static EqualityLiteral xa = Util.cEqual(x, a);
	public static EqualityLiteral xb = Util.cEqual(x, b);
	public static EqualityLiteral yb = Util.cEqual(y, b);
	public static EqualityLiteral nxa = Util.cNEqual(x, a);
	public static EqualityLiteral nxb = Util.cNEqual(x, b);
	public static EqualityLiteral xc = Util.cEqual(x, c);
	public static EqualityLiteral xd = Util.cEqual(x, d);
	
	public static Clause TRUE = Util.TRUE(Level.BASE);
	public static Clause FALSE = Util.FALSE(Level.BASE);
	
	public static void assertFalse(ProverResult result) {
		assertEquals(result.getGeneratedClauses().size(), 1);
		assertTrue(result.getGeneratedClauses().iterator().next().isFalse());
	}
	
	public static void assertTrue(ProverResult result) {
		assertEquals(result.getGeneratedClauses().size(), 1);
		assertTrue(result.getGeneratedClauses().iterator().next().isTrue());
	}

}

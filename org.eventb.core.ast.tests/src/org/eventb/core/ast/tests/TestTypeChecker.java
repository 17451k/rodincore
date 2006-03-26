package org.eventb.core.ast.tests;

import static org.eventb.core.ast.tests.FastFactory.mList;
import static org.eventb.core.ast.tests.FastFactory.mTypeEnvironment;
import junit.framework.TestCase;

import org.eventb.core.ast.Assignment;
import org.eventb.core.ast.BooleanType;
import org.eventb.core.ast.FormulaFactory;
import org.eventb.core.ast.GivenType;
import org.eventb.core.ast.IParseResult;
import org.eventb.core.ast.ITypeCheckResult;
import org.eventb.core.ast.ITypeEnvironment;
import org.eventb.core.ast.IntegerType;
import org.eventb.core.ast.Predicate;
import org.eventb.core.ast.Type;

/**
 * Unit test of the mathematical formula Type-Checker.
 * 
 * @author franz
 */
public class TestTypeChecker extends TestCase {
	private static FormulaFactory ff = FormulaFactory.getDefault();
	
	private static class TestItem {
		public final String formula;
		public final ITypeEnvironment initialEnv;
		public final boolean result;
		public final ITypeEnvironment inferredEnv;
		
		TestItem(String formula, ITypeEnvironment initialEnv, ITypeEnvironment finalEnv) {
			this.formula = formula;
			this.initialEnv = initialEnv;
			this.result = finalEnv != null;
			this.inferredEnv = finalEnv;
		}
	}
	
	@Override
	public void setUp() throws Exception {
		super.setUp();
	}
	
	private static IntegerType INTEGER = ff.makeIntegerType();
	private static BooleanType BOOL = ff.makeBooleanType();

	private static GivenType ty_L = ff.makeGivenType("L");
	private static GivenType ty_N = ff.makeGivenType("N");
	private static GivenType ty_S = ff.makeGivenType("S");
	private static GivenType ty_T = ff.makeGivenType("T");
	private static GivenType ty_U = ff.makeGivenType("U");
	private static GivenType ty_V = ff.makeGivenType("V");

	// Construction of a given type with a name "S" that is not canonic (not gone through intern())
	private static GivenType ty_S2 = ff.makeGivenType(new String(new char[]{'S'}));
	

	private static Type POW(Type base) {
		return ff.makePowerSetType(base);
	}

	private static Type CPROD(Type left, Type right) {
		return ff.makeProductType(left, right);
	}
	
	private static Type REL(Type left, Type right) {
		return POW(CPROD(left, right));
	}
	
	private TestItem[] testItems = new TestItem[] {
			new TestItem(
					"x\u2208\u2124\u22271\u2264x",
					mTypeEnvironment(),
					mTypeEnvironment(mList("x"), mList(INTEGER))
			),
			
			new TestItem("x\u2286S\u2227\u2205\u2282x",
					mTypeEnvironment(mList("S"), mList(POW(ty_S))),
					mTypeEnvironment(mList("x"), mList(POW(ty_S)))
			),
			
			new TestItem("\u2205=\u2205",
					mTypeEnvironment(),
					null
			),
			
			new TestItem("x=TRUE",
					mTypeEnvironment(mList("x"), mList(INTEGER)),
					null
			),
			
			new TestItem("x=TRUE",
					mTypeEnvironment(mList("x"), mList(BOOL)),
					mTypeEnvironment()
			),
			
			new TestItem("M = {A \u2223 A \u2209 A}",
					mTypeEnvironment(),
					null
			),
			new TestItem("x>x",
					mTypeEnvironment(),
					mTypeEnvironment(mList("x"), mList(INTEGER))
			),
			new TestItem("x\u2208y\u2227y\u2208x",
					mTypeEnvironment(),
					null
			),
			new TestItem("x\u2208\u2119(y)\u2227y\u2208\u2119(x)",
					mTypeEnvironment(mList("x"), mList(POW(BOOL))),
					mTypeEnvironment(mList("y"), mList(POW(BOOL)))
			),
			
			new TestItem("\u22a5", 
					mTypeEnvironment(),
					mTypeEnvironment()
			),
			new TestItem("\u22a4",
					mTypeEnvironment(),
					mTypeEnvironment()
			),
			new TestItem("finite(x)",
					mTypeEnvironment(),
					null
			),
			new TestItem("finite(x)",
					mTypeEnvironment(mList("x"), mList(POW(INTEGER))),
					mTypeEnvironment()
			),
			new TestItem("x=x",
					mTypeEnvironment(),
					null
			),
			new TestItem("x\u2260x",
					mTypeEnvironment(),
					null
			),
			new TestItem("x<x",
					mTypeEnvironment(),
					mTypeEnvironment(mList("x"), mList(INTEGER))
			),
			new TestItem("x≤x",
					mTypeEnvironment(),
					mTypeEnvironment(mList("x"), mList(INTEGER))
			),
			new TestItem("x>x",
					mTypeEnvironment(mList("x"), mList(BOOL)),
					null
			),
			new TestItem("x≥x",
					mTypeEnvironment(),
					mTypeEnvironment(mList("x"), mList(INTEGER))
			),
			new TestItem("x\u2208S",
					mTypeEnvironment(),
					null
			),
			new TestItem("x\u2208S",
					mTypeEnvironment(mList("x"), mList(INTEGER)),
					mTypeEnvironment(mList("S"), mList(POW(INTEGER)))
			),
			new TestItem("x\u2208S",
					mTypeEnvironment(mList("x"), mList(ty_S)),
					mTypeEnvironment(mList("S"), mList(POW(ty_S)))
			),
			new TestItem("x\u2209S",
					mTypeEnvironment(mList("x"), mList(ty_S)),
					mTypeEnvironment(mList("S"), mList(POW(ty_S)))
			),
			new TestItem("x\u2282S",
					mTypeEnvironment(),
					null
			),
			new TestItem("x\u2282S",
					mTypeEnvironment(mList("x"), mList(POW(ty_S))),
					mTypeEnvironment(mList("S"), mList(POW(ty_S)))
			),
			new TestItem("x\u2284S",
					mTypeEnvironment(mList("x"), mList(POW(ty_S))),
					mTypeEnvironment(mList("S"), mList(POW(ty_S)))
			),
			new TestItem("x\u2286S",
					mTypeEnvironment(mList("x"), mList(POW(ty_S))),
					mTypeEnvironment(mList("S"), mList(POW(ty_S)))
			),
			new TestItem("x\u2288S",
					mTypeEnvironment(mList("x"), mList(POW(ty_S))),
					mTypeEnvironment(mList("S"), mList(POW(ty_S)))
			),
			// LiteralPredicate
			new TestItem("\u00ac\u22a5",
					mTypeEnvironment(),
					mTypeEnvironment()
			),
			// SimplePredicate
			new TestItem("\u22a5\u2227\u22a5",
					mTypeEnvironment(),
					mTypeEnvironment()
			),
			new TestItem("\u22a5\u2228\u22a5",
					mTypeEnvironment(),
					mTypeEnvironment()
			),
			new TestItem("\u22a5\u2227\u22a5\u2227\u22a5",
					mTypeEnvironment(),
					mTypeEnvironment()
			),
			new TestItem("\u22a5\u2228\u22a5\u2228\u22a5",
					mTypeEnvironment(),
					mTypeEnvironment()
			),
			// UnquantifiedPredicate
			new TestItem("\u22a5\u21d2\u22a5",
					mTypeEnvironment(),
					mTypeEnvironment()
			),
			new TestItem("\u22a5\u21d4\u22a5",
					mTypeEnvironment(),
					mTypeEnvironment()
			),
			// Predicate + IdentList + Quantifier
			new TestItem("\u2200x\u00b7\u22a5",
					mTypeEnvironment(),
					null
			),
			// Bound variable "x" has a different type from free variable "x"
			new TestItem("\u2200 x \u00b7 x \u2208 \u2124",
					mTypeEnvironment(mList("x"), mList(BOOL)),
					mTypeEnvironment()
			),
			new TestItem("\u2203 x \u00b7 x \u2208 \u2124",
					mTypeEnvironment(mList("x"), mList(BOOL)),
					mTypeEnvironment()
			),
			new TestItem("\u2200 x,y,z \u00b7 \u22a5",
					mTypeEnvironment(mList("x", "y", "z"), mList(BOOL, BOOL, BOOL)),
					null
			),
			new TestItem("\u2200 x,y \u00b7 x ∈ y ∧ y ⊆ ℤ",
					mTypeEnvironment(mList("x"), mList(BOOL)),  // Not used.
					mTypeEnvironment()
			),
			new TestItem("\u2203 x,y,z \u00b7 x ∈ y ∧ x ∈ z ∧ z ⊆ S",
					mTypeEnvironment(mList("S"), mList(POW(ty_S))),
					mTypeEnvironment()
			),
			new TestItem("\u2200 x,y \u00b7 \u2200 s,t \u00b7 x ∈ s ∧ y ∈ t ∧ s ∩ t ⊆ S",
					mTypeEnvironment(mList("S"), mList(POW(ty_S))),
					mTypeEnvironment()
			),
			// SimpleExpression
			new TestItem("bool(\u22a5)=y",
					mTypeEnvironment(),
					mTypeEnvironment(mList("y"), mList(BOOL))
			),
			new TestItem("card(x)=y",
					mTypeEnvironment(),
					null
			),
			new TestItem("card(x)=y",
					mTypeEnvironment(mList("x"), mList(ty_S)),
					null
			),
			new TestItem("card(x)=y",
					mTypeEnvironment(mList("x"), mList(POW(ty_S))),
					mTypeEnvironment(mList("y"), mList(INTEGER))
			),
			new TestItem("\u2119(x)=y",
					mTypeEnvironment(),
					null
			),
			new TestItem("\u2119(x)=y",
					mTypeEnvironment(mList("y"), mList(POW(POW(INTEGER)))),
					mTypeEnvironment(mList("x"), mList(POW(INTEGER)))
			),
			new TestItem("\u21191(x)=y",
					mTypeEnvironment(mList("y"), mList(POW(POW(INTEGER)))),
					mTypeEnvironment(mList("x"), mList(POW(INTEGER)))
			),
			new TestItem("union(x)=y",
					mTypeEnvironment(),
					null
			),
			new TestItem("union(x)=y",
					mTypeEnvironment(mList("y"), mList(POW(ty_S))),
					mTypeEnvironment(mList("x"), mList(POW(POW(ty_S))))
			),
			new TestItem("inter(x)=y",
					mTypeEnvironment(),
					null
			),
			new TestItem("inter(x)=y",
					mTypeEnvironment(mList("y"), mList(POW(ty_S))),
					mTypeEnvironment(mList("x"), mList(POW(POW(ty_S))))
			),
			
			new TestItem("dom(x)=y",
					mTypeEnvironment(),
					null
			),
			new TestItem("dom(x)=y",
					mTypeEnvironment(mList("x"), mList(POW(CPROD(INTEGER,ty_S)))),
					mTypeEnvironment(mList("y"), mList(POW(INTEGER)))
			),
			new TestItem("ran(x)=y",
					mTypeEnvironment(mList("x"), mList(POW(CPROD(INTEGER,ty_S)))),
					mTypeEnvironment(mList("y"), mList(POW(ty_S)))
			),
			new TestItem("prj1(x)=y",
					mTypeEnvironment(),
					null
			),
			new TestItem("prj1(x)=y",
					mTypeEnvironment(mList("x"), mList(POW(CPROD(INTEGER,BOOL)))),
					mTypeEnvironment(mList("y"), mList(POW(CPROD(CPROD(INTEGER,BOOL),INTEGER))))
			),
			new TestItem("prj2(x)=y",
					mTypeEnvironment(mList("x"), mList(POW(CPROD(INTEGER,BOOL)))),
					mTypeEnvironment(mList("y"), mList(POW(CPROD(CPROD(INTEGER,BOOL),BOOL))))
			),
			new TestItem("id(x)=y",
					mTypeEnvironment(mList("x"), mList(POW(ty_S))),
					mTypeEnvironment(mList("y"), mList(POW(CPROD(ty_S,ty_S))))
			),
			new TestItem("{x,y\u00b7\u22a5\u2223z}=a",
					mTypeEnvironment(),
					null
			),
			new TestItem("{x,y\u00b7\u22a5\u2223z}=a",
					mTypeEnvironment(mList("z"), mList(INTEGER)),
					null
			),
			new TestItem("{x \u00b7 x ∈ z \u2223 z}=a",
					mTypeEnvironment(mList("a"), mList(POW(POW(BOOL)))),
					mTypeEnvironment(mList("z"), mList(POW(BOOL)))
			),
			new TestItem("{x \u00b7 \u22a5 \u2223 x}=a",
					mTypeEnvironment(mList("a"), mList(POW(INTEGER))),
					mTypeEnvironment()
			),
			new TestItem("{x+y\u2223\u22a5}=a",
					mTypeEnvironment(),
					mTypeEnvironment(mList("a"), mList(POW(INTEGER)))
			),
			new TestItem("{}={}",
					mTypeEnvironment(),
					null
			),
			new TestItem("a=∅",
					mTypeEnvironment(mList("a"), mList(POW(ty_N))),
					mTypeEnvironment()
			),
			new TestItem("a=∅",
					mTypeEnvironment(mList("a"), mList(POW(CPROD(ty_N,ty_N)))),
					mTypeEnvironment()
			),
			new TestItem("∅=a",
					mTypeEnvironment(mList("a"), mList(POW(ty_N))),
					mTypeEnvironment()
			),
			new TestItem("∅=a",
					mTypeEnvironment(mList("a"), mList(POW(CPROD(ty_N,ty_N)))),
					mTypeEnvironment()
			),
			new TestItem("{x}=a",
					mTypeEnvironment(mList("x"), mList(INTEGER)),
					mTypeEnvironment(mList("a"), mList(POW(INTEGER)))
			),
			new TestItem("{x,y,z}=a",
					mTypeEnvironment(mList("x"), mList(INTEGER)),
					mTypeEnvironment(mList("y","z","a"), mList(INTEGER,INTEGER,POW(INTEGER)))
			),
			new TestItem("x\u2208\u2124",
					mTypeEnvironment(),
					mTypeEnvironment(mList("x"), mList(INTEGER))
			),
			new TestItem("x\u2208\u2115",
					mTypeEnvironment(),
					mTypeEnvironment(mList("x"), mList(INTEGER))
			),
			new TestItem("x\u2208\u21151",
					mTypeEnvironment(),
					mTypeEnvironment(mList("x"), mList(INTEGER))
			),
			new TestItem("x\u2208BOOL",
					mTypeEnvironment(),
					mTypeEnvironment(mList("x"), mList(BOOL))
			),
			new TestItem("x=FALSE",
					mTypeEnvironment(),
					mTypeEnvironment(mList("x"), mList(BOOL))
			),
			new TestItem("x=pred",
					mTypeEnvironment(),
					mTypeEnvironment(mList("x"), mList(REL(INTEGER, INTEGER)))
			),
			new TestItem("x=succ",
					mTypeEnvironment(),
					mTypeEnvironment(mList("x"), mList(REL(INTEGER, INTEGER)))
			),
			new TestItem("x=2",
					mTypeEnvironment(),
					mTypeEnvironment(mList("x"), mList(INTEGER))
			),
			// Primary
			new TestItem("x\u223c=y",
					mTypeEnvironment(mList("x"), mList(POW(CPROD(INTEGER,BOOL)))),
					mTypeEnvironment(mList("y"), mList(POW(CPROD(BOOL,INTEGER))))
			),
			// Image
			new TestItem("f(x)=a",
					mTypeEnvironment(mList("f"), mList(POW(CPROD(INTEGER,BOOL)))),
					mTypeEnvironment(mList("x","a"), mList(INTEGER,BOOL))
			),
			new TestItem("f[x]=a",
					mTypeEnvironment(mList("f"), mList(POW(CPROD(INTEGER,BOOL)))),
					mTypeEnvironment(mList("x","a"), mList(POW(INTEGER),POW(BOOL)))
			),
			new TestItem("f[x](y)=a",
					mTypeEnvironment(),
					null
			),
//			new TestItem("f[x](y)=a",
//			mTypeEnvironment(),
//			true,
//			null
//			),
			
//			"f(x)[y]=a",
//			
//			"f(x)(y)=a",
//			
//			"f[x][y]=a",
//			
//			
//			// Factor
			new TestItem("x^y=a",
					mTypeEnvironment(),
					mTypeEnvironment(
							mList("a",    "x",    "y"),
							mList(INTEGER, INTEGER, INTEGER)
					)
			),
			
			// Term
			new TestItem("x\u2217x=a",
					mTypeEnvironment(),
					mTypeEnvironment(
							mList("a",    "x"),
							mList(INTEGER, INTEGER)
					)
			),			
			new TestItem("x\u2217x\u2217x=a",
					mTypeEnvironment(),
					mTypeEnvironment(
							mList("a", 	   "x"	),
							mList(INTEGER, INTEGER)
					)
			),
			new TestItem("x÷x=a",
					mTypeEnvironment(),
					mTypeEnvironment(
							mList("a",    "x"),
							mList(INTEGER, INTEGER)
					)
			),
			new TestItem("x mod x=a",
					mTypeEnvironment(),
					mTypeEnvironment(
							mList("a",	   "x"),
							mList(INTEGER, INTEGER)
					)
			),
			// ArithmeticExpr
			new TestItem("x+y=a",
					mTypeEnvironment(),
					mTypeEnvironment(
							mList("a",    "x",    "y"),
							mList(INTEGER, INTEGER, INTEGER)
					)
			),
			new TestItem("x+y+x=a",
					mTypeEnvironment(),
					mTypeEnvironment(
							mList("a",    "x",    "y"),
							mList(INTEGER, INTEGER, INTEGER)
					)
			),
			new TestItem("−x+y+z=a",
					mTypeEnvironment(),
					mTypeEnvironment(
							mList("a",    "x",    "y",    "z"),
							mList(INTEGER, INTEGER, INTEGER, INTEGER)
					)
			),
			new TestItem("x−y=a",
					mTypeEnvironment(),
					mTypeEnvironment(
							mList("a",    "x",    "y"),
							mList(INTEGER, INTEGER, INTEGER)
					)
			),
			new TestItem("x−y−z=a",
					mTypeEnvironment(),
					mTypeEnvironment(
							mList("a",    "x",    "y",    "z"),
							mList(INTEGER, INTEGER, INTEGER, INTEGER)
					)
			),
			new TestItem("−x−y=a",
					mTypeEnvironment(),
					mTypeEnvironment(
							mList("a",    "x",    "y"),
							mList(INTEGER, INTEGER, INTEGER)
					)
			),
			new TestItem("x−y+z−x=a",
					mTypeEnvironment(),
					mTypeEnvironment(
							mList("a",    "x",    "y",    "z"),
							mList(INTEGER, INTEGER, INTEGER, INTEGER)
					)
			),
			new TestItem("−x−y+z−x=a",
					mTypeEnvironment(),
					mTypeEnvironment(
							mList("a",    "x",    "y",    "z"),
							mList(INTEGER, INTEGER, INTEGER, INTEGER)
					)
			),
			new TestItem("x+y−z+x=a",
					mTypeEnvironment(),
					mTypeEnvironment(
							mList("a",    "x",    "y",    "z"),
							mList(INTEGER, INTEGER, INTEGER, INTEGER)
					)
			),
			new TestItem("−x+y−z+x=a",
					mTypeEnvironment(),
					mTypeEnvironment(
							mList("a",    "x",    "y",    "z"),
							mList(INTEGER, INTEGER, INTEGER, INTEGER)
					)
			),
			// IntervalExpr
			new TestItem("x\u2025y=a",
					mTypeEnvironment(),
					mTypeEnvironment(
							mList("a",         "x",    "y"),
							mList(POW(INTEGER), INTEGER, INTEGER)
					)
			),
			// RelationExpr
			new TestItem("x\u2297y=a",
					mTypeEnvironment(
							mList("x",                  "y"),
							mList(POW(CPROD(ty_S,ty_T)), POW(CPROD(ty_S,ty_U)))
					),
					mTypeEnvironment(mList("a"), mList(POW(CPROD(ty_S,CPROD(ty_T,ty_U)))))
			),
			new TestItem("x;y=a",
					mTypeEnvironment(
							mList("a",                  "x"),
							mList(POW(CPROD(ty_S,ty_T)), POW(CPROD(ty_S,ty_U)))
					),
					mTypeEnvironment(mList("y"), mList(POW(CPROD(ty_U,ty_T))))
			),
//			new TestItem("x;y;z=a",
//			mTypeEnvironment(),
//			true,
////			
			new TestItem("x\u25b7y=a",
					mTypeEnvironment(
							mList("x"),
							mList(POW(CPROD(ty_S,ty_T)))
					),
					mTypeEnvironment(
							mList("y",      "a"),
							mList(POW(ty_T), POW(CPROD(ty_S,ty_T)))
					)
			),
			new TestItem("x\u2a65y=a",
					mTypeEnvironment(
							mList("x"),
							mList(POW(CPROD(ty_S,ty_T)))
					),
					mTypeEnvironment(
							mList("y",      "a"),
							mList(POW(ty_T), POW(CPROD(ty_S,ty_T)))
					)
			),
			new TestItem("x\u2229y=a",
					mTypeEnvironment(
							mList("x"),
							mList(POW(ty_T))
					),
					mTypeEnvironment(
							mList("y",      "a"),
							mList(POW(ty_T), POW(ty_T))
					)
			),
			
			new TestItem("x\u2229y\u2229z=a",
					mTypeEnvironment(
							mList("x"),
							mList(POW(ty_T))
					),
					mTypeEnvironment(
							mList("y",      "z",      "a"),
							mList(POW(ty_T), POW(ty_T), POW(ty_T))
					)
			),
			new TestItem("x\u2216y=a",
					mTypeEnvironment(
							mList("x"),
							mList(POW(ty_T))
					),
					mTypeEnvironment(
							mList("y",      "a"),
							mList(POW(ty_T), POW(ty_T))
					)
			),
//			new TestItem("x;y\u2a65z=a",
//			
//			new TestItem("x\u2229y\u2a65z=a",
//			
//			new TestItem("x\u2229y\\z=a",
//			
//			
//			// SetExpr
			new TestItem("x\u222ay=a",
					mTypeEnvironment(
							mList("x"),
							mList(POW(ty_T))
					),
					mTypeEnvironment(
							mList("y",      "a"),
							mList(POW(ty_T), POW(ty_T))
					)
			),
			new TestItem("x\u222ay\u222az=a",
					mTypeEnvironment(
							mList("x"),
							mList(POW(ty_T))
					),
					mTypeEnvironment(
							mList("y",      "z",      "a"),
							mList(POW(ty_T), POW(ty_T), POW(ty_T))
					)
			),
			new TestItem("x\u00d7y=a",
					mTypeEnvironment(
							mList("a"),
							mList(POW(CPROD(ty_S,ty_T)))
					),
					mTypeEnvironment(
							mList("x",      "y"),
							mList(POW(ty_S), POW(ty_T))
					)
			),
			new TestItem("x\u00d7y\u00d7z=a",
					mTypeEnvironment(
							mList("a"),
							mList(POW(CPROD(CPROD(ty_S,ty_T),ty_U)))
					),
					mTypeEnvironment(
							mList("x",      "y",      "z"),
							mList(POW(ty_S), POW(ty_T), POW(ty_U))
					)
			),
			new TestItem("x\ue103y=a",
					mTypeEnvironment(
							mList("a"),
							mList(POW(CPROD(ty_S,ty_T)))
					),
					mTypeEnvironment(
							mList("x",                  "y"),
							mList(POW(CPROD(ty_S,ty_T)), POW(CPROD(ty_S,ty_T)))
					)
			),
			new TestItem("x\ue103y\ue103z=a",
					mTypeEnvironment(
							mList("a"),
							mList(POW(CPROD(ty_S,ty_T)))
					),
					mTypeEnvironment(
							mList(
									"x",
									"y",
									"z"
							),
							mList(
									POW(CPROD(ty_S,ty_T)),
									POW(CPROD(ty_S,ty_T)),
									POW(CPROD(ty_S,ty_T))
							)
					)
			),
			
			new TestItem("f \u2218 g = a",
					mTypeEnvironment(
							mList("f",                  "a"),
							mList(POW(CPROD(ty_T,ty_U)), POW(CPROD(ty_S,ty_U)))
					),
					mTypeEnvironment(
							mList("g"),
							mList(POW(CPROD(ty_S,ty_T)))
					)
			),
			
			new TestItem("f \u2218 g \u2218 h = a",
					mTypeEnvironment(
							mList("f",                  "h"),
							mList(POW(CPROD(ty_U,ty_V)), POW(CPROD(ty_S,ty_T)))
					),
					mTypeEnvironment(
							mList(
									"a",
									"g"
							),
							mList(
									POW(CPROD(ty_S,ty_V)),
									POW(CPROD(ty_T,ty_U))
							)
					)
			),
			new TestItem("x\u2225y=a",
					mTypeEnvironment(),
					null
			),
			new TestItem("x\u2225y=a",
					mTypeEnvironment(
							mList("x",                  "y"),
							mList(POW(CPROD(ty_S,ty_U)), POW(CPROD(ty_T,ty_V)))
					),
					mTypeEnvironment(
							mList("a"),
							mList(POW(CPROD(CPROD(ty_S,ty_T),CPROD(ty_U,ty_V))))
					)
			),
			new TestItem("x\u25c1y=a",
					mTypeEnvironment(
							mList("y"),
							mList(POW(CPROD(ty_S,ty_T)))
					),
					mTypeEnvironment(
							mList("x",      "a"),
							mList(POW(ty_S), POW(CPROD(ty_S,ty_T)))
					)
			),
			new TestItem("x\u2a64y=a",
					mTypeEnvironment(
							mList("y"),
							mList(POW(CPROD(ty_S,ty_T)))
					),
					mTypeEnvironment(
							mList("x",      "a"),
							mList(POW(ty_S), POW(CPROD(ty_S,ty_T)))
					)
			),
			// RelationalSetExpr
			new TestItem("x\ue100y=a",
					mTypeEnvironment(
							mList("a"),
							mList(POW(POW(CPROD(ty_S,ty_T))))
					),
					mTypeEnvironment(
							mList("y",      "x"),
							mList(POW(ty_T), POW(ty_S))
					)
			),
			new TestItem("x\ue100y\ue100z=a",
					mTypeEnvironment(
							mList("a"),
							mList(POW(POW(CPROD(POW(CPROD(ty_S,ty_T)),ty_U))))
					),
					mTypeEnvironment(
							mList("z",      "y",      "x"),
							mList(POW(ty_U), POW(ty_T), POW(ty_S))
					)
			),
			new TestItem("x\ue101y=a",
					mTypeEnvironment(
							mList("a"),
							mList(POW(POW(CPROD(ty_S,ty_T))))
					),
					mTypeEnvironment(
							mList("y",      "x"),
							mList(POW(ty_T), POW(ty_S))
					)
			),
			new TestItem("x\ue101y\ue101z=a",
					mTypeEnvironment(
							mList("a"),
							mList(POW(POW(CPROD(POW(CPROD(ty_S,ty_T)),ty_U))))
					),
					mTypeEnvironment(
							mList("z",      "y",      "x"),
							mList(POW(ty_U), POW(ty_T), POW(ty_S))
					)
			),
			new TestItem("x\ue102y=a",
					mTypeEnvironment(
							mList("a"),
							mList(POW(POW(CPROD(ty_S,ty_T))))
					),
					mTypeEnvironment(
							mList("y",      "x"),
							mList(POW(ty_T), POW(ty_S))
					)
			),
			new TestItem("x\ue102y\ue102z=a",
					mTypeEnvironment(
							mList("a"),
							mList(POW(POW(CPROD(POW(CPROD(ty_S,ty_T)),ty_U))))
					),
					mTypeEnvironment(
							mList("z",      "y",      "x"),
							mList(POW(ty_U), POW(ty_T), POW(ty_S))
					)
			),
			new TestItem("x\u2900y=a",
					mTypeEnvironment(
							mList("a"),
							mList(POW(POW(CPROD(ty_S,ty_T))))
					),
					mTypeEnvironment(
							mList("y",      "x"),
							mList(POW(ty_T), POW(ty_S))
					)
			),
			new TestItem("x\u2900y\u2900z=a",
					mTypeEnvironment(
							mList("a"),
							mList(POW(POW(CPROD(POW(CPROD(ty_S,ty_T)),ty_U))))
					),
					mTypeEnvironment(
							mList("z",      "y",      "x"),
							mList(POW(ty_U), POW(ty_T), POW(ty_S))
					)
			),
			new TestItem("x\u2914y=a",
					mTypeEnvironment(
							mList("a"),
							mList(POW(POW(CPROD(ty_S,ty_T))))
					),
					mTypeEnvironment(
							mList("y",      "x"),
							mList(POW(ty_T), POW(ty_S))
					)
			),
			new TestItem("x\u2914y\u2914z=a",
					mTypeEnvironment(
							mList("a"),
							mList(POW(POW(CPROD(POW(CPROD(ty_S,ty_T)),ty_U))))
					),
					mTypeEnvironment(
							mList("z",      "y",      "x"),
							mList(POW(ty_U), POW(ty_T), POW(ty_S))
					)
			),
			new TestItem("x\u2916y=a",
					mTypeEnvironment(
							mList("a"),
							mList(POW(POW(CPROD(ty_S,ty_T))))
					),
					mTypeEnvironment(
							mList("y",      "x"),
							mList(POW(ty_T), POW(ty_S))
					)
			),
			new TestItem("x\u2916y\u2916z=a",
					mTypeEnvironment(
							mList("a"),
							mList(POW(POW(CPROD(POW(CPROD(ty_S,ty_T)),ty_U))))
					),
					mTypeEnvironment(
							mList("z",      "y",      "x"),
							mList(POW(ty_U), POW(ty_T), POW(ty_S))
					)
			),
			new TestItem("x\u2192y=a",
					mTypeEnvironment(
							mList("a"),
							mList(POW(POW(CPROD(ty_S,ty_T))))
					),
					mTypeEnvironment(
							mList("y",      "x"),
							mList(POW(ty_T), POW(ty_S))
					)
			),
			new TestItem("x\u2192y\u2192z=a",
					mTypeEnvironment(
							mList("a"),
							mList(POW(POW(CPROD(POW(CPROD(ty_S,ty_T)),ty_U))))
					),
					mTypeEnvironment(
							mList("z",      "y",      "x"),
							mList(POW(ty_U), POW(ty_T), POW(ty_S))
					)
			),
			new TestItem("x\u2194y=a",
					mTypeEnvironment(
							mList("a"),
							mList(POW(POW(CPROD(ty_S,ty_T))))
					),
					mTypeEnvironment(
							mList("y",      "x"),
							mList(POW(ty_T), POW(ty_S))
					)
			),
			new TestItem("x\u2194y\u2194z=a",
					mTypeEnvironment(
							mList("a"),
							mList(POW(POW(CPROD(POW(CPROD(ty_S,ty_T)),ty_U))))
					),
					mTypeEnvironment(
							mList("z",      "y",      "x"),
							mList(POW(ty_U), POW(ty_T), POW(ty_S))
					)
			),
			new TestItem("x\u21a0y=a",
					mTypeEnvironment(
							mList("a"),
							mList(POW(POW(CPROD(ty_S,ty_T))))
					),
					mTypeEnvironment(
							mList("y",      "x"),
							mList(POW(ty_T), POW(ty_S))
					)
			),
			new TestItem("x\u21a0y\u21a0z=a",
					mTypeEnvironment(
							mList("a"),
							mList(POW(POW(CPROD(POW(CPROD(ty_S,ty_T)),ty_U))))
					),
					mTypeEnvironment(
							mList("z",      "y",      "x"),
							mList(POW(ty_U), POW(ty_T), POW(ty_S))
					)
			),
			new TestItem("x\u21a3y=a",
					mTypeEnvironment(
							mList("a"),
							mList(POW(POW(CPROD(ty_S,ty_T))))
					),
					mTypeEnvironment(
							mList("y",      "x"),
							mList(POW(ty_T), POW(ty_S))
					)
			),
			new TestItem("x\u21a3y\u21a3z=a",
					mTypeEnvironment(
							mList("a"),
							mList(POW(POW(CPROD(POW(CPROD(ty_S,ty_T)),ty_U))))
					),
					mTypeEnvironment(
							mList("z",      "y",      "x"),
							mList(POW(ty_U), POW(ty_T), POW(ty_S))
					)
			),
			new TestItem("x\u21f8y=a",
					mTypeEnvironment(
							mList("a"),
							mList(POW(POW(CPROD(ty_S,ty_T))))
					),
					mTypeEnvironment(
							mList("y",      "x"),
							mList(POW(ty_T), POW(ty_S))
					)
			),
			new TestItem("x\u21f8y\u21f8z=a",
					mTypeEnvironment(
							mList("a"),
							mList(POW(POW(CPROD(POW(CPROD(ty_S,ty_T)),ty_U))))
					),
					mTypeEnvironment(
							mList("z",      "y",      "x"),
							mList(POW(ty_U), POW(ty_T), POW(ty_S))
					)
			),
			// PairExpr
			new TestItem("x\u21a6y=a",
					mTypeEnvironment(
							mList("a"),
							mList(CPROD(ty_S,ty_T))
					),
					mTypeEnvironment(
							mList("x", "y"),
							mList(ty_S, ty_T)
					)
			),
			new TestItem("a=x\u21a6y",
					mTypeEnvironment(
							mList("a"),
							mList(CPROD(ty_S,ty_T))
					),
					mTypeEnvironment(
							mList("x", "y"),
							mList(ty_S, ty_T)
					)
			),
//			// QuantifiedExpr & IdentPattern
//			// UnBound
//			new TestItem("finite(\u03bb x\u00b7\u22a5\u2223z)",
//			
//			new TestItem("finite(\u03bb x\u21a6y\u00b7\u22a5\u2223z)",
//			
//			new TestItem("finite(\u03bb x\u21a6y\u21a6s\u00b7\u22a5\u2223z)",
//			
//			new TestItem("finite(\u03bb x\u21a6(y\u21a6s)\u00b7\u22a5\u2223z)",
//			
//			
//			// Bound
//			new TestItem("finite(\u03bb x\u00b7\u22a5\u2223x)",
//			
//			new TestItem("finite(\u03bb x\u21a6y\u00b7\u22a5\u2223y)",
//			
//			new TestItem("finite(\u03bb x\u21a6y\u21a6s\u00b7\u22a5\u2223s)",
//			
//			new TestItem("finite(\u03bb x\u21a6(y\u21a6s)\u00b7\u22a5\u2223s)",
//			
//			
			// UnBound
//			new TestItem("finite(\u22c3x\u00b7\u22a5\u2223z)"
//			),
//			
//			new TestItem("finite(\u22c3y,x\u00b7\u22a5\u2223z)",
//			
//			new TestItem("finite(\u22c3s,y,x\u00b7\u22a5\u2223z)",
//			
//			
//			// Bound
			new TestItem("(\u22c3 x \u00b7 \u22a5 \u2223 x) = a",
					mTypeEnvironment(mList("a"), mList(POW(INTEGER))),
					mTypeEnvironment()
			),
//			
//			new TestItem("finite(\u22c3y,x\u00b7\u22a5\u2223y \u25b7 x)",
//			mTypeEnvironment(new FreeIdentifier[]{formulaFactory.makeFreeIdentifier("x",Formula.FREE_IDENT)}, mList(formulaFactory.makeUnaryExpression(formulaFactory.makeAtomicExpression(Formula.INTEGER),Formula.POW))),
//			true,
//			mTypeEnvironment(new FreeIdentifier[]{formulaFactory.makeFreeIdentifier("x",Formula.FREE_IDENT)}, mList(formulaFactory.makeUnaryExpression(formulaFactory.makeAtomicExpression(Formula.INTEGER),Formula.POW)))
//			),
//			
//			new TestItem("finite(\u22c3s,y,x\u00b7\u22a5\u2223s)",
//			
//			
//			// UnBound
//			new TestItem("finite(\u22c3x\u2223\u22a5)",
//			
//			new TestItem("finite(\u22c3y−x\u2223\u22a5)",
//			
//			
//			// UnBound
//			new TestItem("finite(\u22c2x\u00b7\u22a5\u2223z)",
//			
//			new TestItem("finite(\u22c2y,x\u00b7\u22a5\u2223z)",
//			
//			new TestItem("finite(\u22c2s,y,x\u00b7\u22a5\u2223z)",
//			
//			
//			// Bound
//			new TestItem("finite(\u22c2x\u00b7\u22a5\u2223x)",
//			
//			new TestItem("finite(\u22c2y,x\u00b7\u22a5\u2223y)",
//			
//			new TestItem("finite(\u22c2s,y,x\u00b7\u22a5\u2223s)",
//			
//			
//			// UnBound
//			new TestItem("finite(\u22c2x\u2223\u22a5)",
//			
//			new TestItem("finite(\u22c2y−x\u2223\u22a5)",
//			
			
			// Special formulas
			new TestItem("∀ s \u00b7 id ( N ) ⊆ s ∧ s ; r ⊆ s ⇒ c ⊆ s",
					mTypeEnvironment(mList("N"), mList(POW(ty_N))),
					mTypeEnvironment(
							mList("r",                  "c"),
							mList(POW(CPROD(ty_N,ty_N)), POW(CPROD(ty_N,ty_N)))
					)
			),
			
			new TestItem("(λ x ↦ y ↦ z \u00b7 x < y ∧ z ∈ ℤ∣ H ) ( f ( 1 ) ) ∈ ℙ ( ℤ )",
					mTypeEnvironment(),
					mTypeEnvironment(
							mList("H",         "f"),
							mList(POW(INTEGER), POW(CPROD(INTEGER,CPROD(CPROD(INTEGER,INTEGER),INTEGER))))
					)
			),
			new TestItem(
					" ultraf = { " +
					" f ∣ f ∈ filter ∧ " +
					" (∀ g \u00b7 g ∈ filter ∧ f ⊆ g ⇒ f = g) " +
					" } " +
					" ∧ filter = { " +
					" h ∣ h ∈ ℙ ( ℙ ( S ) ) ∧ " +
					" S ∈ h ∧" +
					" ∅ ∉ h ∧" +
					" ( ∀ a, b \u00b7 a ∈ h ∧ a ⊆ b ⇒ b ∈ h ) ∧ " +
					" ( ∀ c, d \u00b7 c ∈ h ∧ d ∈ h ⇒ c ∩ d ∈ h )" +
					" } ",
					mTypeEnvironment(mList("S"), mList(POW(ty_S))),
					mTypeEnvironment(
							mList("filter",           "ultraf"),
							mList(POW(POW(POW(ty_S))), POW(POW(POW(ty_S))))
					)
			),
			new TestItem(
					" filter = { " +
					" h ∣ h ∈ ℙ ( ℙ ( S ) ) ∧ " +
					" S ∈ h ∧" +
					" ∅ ∉ h ∧" +
					" ( ∀ a, b \u00b7 a ∈ h ∧ a ⊆ b ⇒ b ∈ h ) ∧ " +
					" ( ∀ c, d \u00b7 c ∈ h ∧ d ∈ h ⇒ c ∩ d ∈ h )" +
					" } ∧ " +				
					" ultraf = { " +
					" f ∣ f ∈ filter ∧ " +
					" (∀ g \u00b7 g ∈ filter ∧ f ⊆ g ⇒ f = g) " +
					" } ",
					mTypeEnvironment(mList("S"), mList(POW(ty_S))),
					mTypeEnvironment(
							mList("filter",           "ultraf"),
							mList(POW(POW(POW(ty_S))), POW(POW(POW(ty_S))))
					)
			),
			new TestItem("id(N) ∩ g = ∅",
					mTypeEnvironment(mList("N"), mList(POW(ty_N))),
					mTypeEnvironment(
							mList("g"),
							mList(POW(CPROD(ty_N,ty_N)))
					)
			),
			new TestItem(
					" g = g~ ∧ " +
					" id(N) ∩ g = ∅ ∧ " +
					" dom(g) = N ∧ " +
					" h ∈ N ↔ ( N ⤀ N ) ∧ " +
					" (∀n,f\u00b7" +
					"    n ∈ N ∧ " +
					"    f ∈ N ⤀ N" +
					"    ⇒" +
					"    (n ↦ f ∈ h" + 
					"     ⇔" + 
					"     (f ∈ N ∖ {n} ↠ N ∧ " +
					"      f ⊆ g ∧ " +
					"      (∀ S \u00b7 n ∈ S ∧ f~[S] ⊆ S ⇒ N ⊆ S)" +
					"     )" +
					"    )" +
					" )",
					mTypeEnvironment(mList("N"), mList(POW(ty_N))),
					mTypeEnvironment(
							mList(
									"g",
									"h"
							),
							mList(
									POW(CPROD(ty_N,ty_N)),
									POW(CPROD(ty_N,POW(CPROD(ty_N,ty_N))))
							)
					)
			),
			new TestItem(
					" com ∩ id(L) = ∅ ∧ " +
					" exit ∈ L ∖ {outside} ↠ L ∧ " +
					" exit ⊆ com ∧ " +
					" ( ∀ s \u00b7 s ⊆ exit~[s] ⇒ s = ∅ ) ∧ " +
					" aut ⩥ {outside} ⊆ (aut ; exit~) ∧ " +
					" ( ∃ l \u00b7 l ∈ L ∖ {outside} ∧ outside ↦ l ∈ com ∧ L×{l} ⊆ aut )",
					mTypeEnvironment(mList("L"), mList(POW(ty_L))),
					mTypeEnvironment(
							mList(
									"aut",
									"com",
									"outside",
									"exit"
							),
							mList(
									POW(CPROD(ty_L,ty_L)),
									POW(CPROD(ty_L,ty_L)),
									ty_L,
									POW(CPROD(ty_L,ty_L))
							)
					)
			),
			
			new TestItem(
					" f ∈ ℙ(S) ↠ ℙ(S) ∧ " +
					" (∀ a, b \u00b7 a ⊆ b ⇒ f(a) ⊆ f(b)) ∧ " +
					" fix = inter({s \u2223 f(s) ⊆ s}) ∧ " +
					" (∀ s \u00b7 f(s) ⊆ s ⇒ fix ⊆ s) ∧ " +
					" (∀ v \u00b7 (∀ w \u00b7 f(w) ⊆ w ⇒ v ⊆ w) ⇒ v ⊆ fix) ∧ " +
					" f(fix) = fix ",
					mTypeEnvironment(
							mList("S"),	
							mList(POW(ty_S))
					),
					mTypeEnvironment(
							mList("fix",    "f"),	
							mList(POW(ty_S), POW(CPROD(POW(ty_S),POW(ty_S))))
					)
			),
			new TestItem(
					"  x ∈ S " +
					"∧ (∀x\u00b7x ∈ T) " +
					"∧ (∀x\u00b7x ∈ U) ",
					mTypeEnvironment(
							mList("S",      "T",      "U"),	
							mList(POW(ty_S), POW(ty_T), POW(ty_U))
					),
					mTypeEnvironment(
							mList("x"),	
							mList(ty_S)
					)
			),
			new TestItem(
					"  x ∈ S " +
					"∧ (∀x\u00b7x ∈ T ∧ (∀x\u00b7x ∈ U)) ",
					mTypeEnvironment(
							mList("S",      "T",      "U"),	
							mList(POW(ty_S), POW(ty_T), POW(ty_U))
					),
					mTypeEnvironment(
							mList("x"),	
							mList(ty_S)
					)
			),
			
			// Example from Christophe.
			new TestItem(
					"x ∈ y",
					mTypeEnvironment(
							mList("x", "y"),
							mList(ty_S, POW(ty_S2))
					),
					mTypeEnvironment()
			),

			// Test with typed empty set
			new TestItem(
					"(∅⦂ℙ(S×ℤ)) ∈ (∅⦂ℙ(S)) → ℤ",
					mTypeEnvironment(),
					mTypeEnvironment()
			),
	};

	private TestItem[] assignItems = new TestItem[] {
			new TestItem(
					"x ≔ E",
					mTypeEnvironment(mList("x"), mList(ty_S)),
					mTypeEnvironment(mList("E"), mList(ty_S))
			),
			new TestItem(
					"x ≔ 2",
					mTypeEnvironment(),
					mTypeEnvironment(mList("x"), mList(INTEGER))
			),
			new TestItem(
					"x ≔ 2",
					mTypeEnvironment(mList("x"), mList(ty_S)),
					null
			),
			new TestItem(
					"x,y ≔ E,F",
					mTypeEnvironment(mList("x", "F"), mList(ty_S, ty_T)),
					mTypeEnvironment(mList("E", "y"), mList(ty_S, ty_T))
			),
			new TestItem(
					"x,y ≔ E,F",
					mTypeEnvironment(mList("x", "y", "E"), mList(ty_S, ty_T, ty_T)),
					null
			),
			new TestItem(
					"x,y ≔ E,F",
					mTypeEnvironment(mList("x", "y", "F"), mList(ty_S, ty_T, ty_S)),
					null
			),
			new TestItem(
					"x,y,z ≔ ∅,∅,∅",
					mTypeEnvironment(
							mList("x",       "y",       "z"), 
							mList(POW(ty_S), POW(ty_T), POW(ty_U))),
					mTypeEnvironment()
			),
			new TestItem(
					"x,y,z ≔ E,F,G",
					mTypeEnvironment(
							mList("x",  "y",  "z",  "E"), 
							mList(ty_S, ty_T, ty_U, ty_T)),
					null
			),
			new TestItem(
					"x,y,z ≔ E,F,G",
					mTypeEnvironment(
							mList("x",  "y",  "z",  "F"), 
							mList(ty_S, ty_T, ty_U, ty_U)),
					null
			),
			new TestItem(
					"x,y,z ≔ E,F,G",
					mTypeEnvironment(
							mList("x",  "y",  "z",  "G"), 
							mList(ty_S, ty_T, ty_U, ty_S)),
					null
			),
			new TestItem(
					"x :∈ S",
					mTypeEnvironment(mList("S"), mList(POW(ty_S))),
					mTypeEnvironment(mList("x"), mList(ty_S))
			),
			new TestItem(
					"x :∈ ∅",
					mTypeEnvironment(mList("x"), mList(POW(ty_S))),
					mTypeEnvironment()
			),
			new TestItem(
					"x :∈ 1",
					mTypeEnvironment(mList("x"), mList(ty_S)),
					null
			),
			new TestItem(
					"x :∈ 1",
					mTypeEnvironment(mList("x"), mList(INTEGER)),
					null
			),
			new TestItem(
					"x :\u2223 x' < 0",
					mTypeEnvironment(),
					mTypeEnvironment(mList("x"), mList(INTEGER))
			),
			new TestItem(
					"x,y :\u2223 x' < 0 ∧ y' = bool(x' = 5)",
					mTypeEnvironment(),
					mTypeEnvironment(mList("x", "y"), mList(INTEGER, BOOL))
			),
	};
	
	/**
	 * Main test routine for predicates.
	 */
	public void testTypeChecker () {
		for (TestItem item: testItems) {
			IParseResult parseResult = ff.parsePredicate(item.formula);
			assertTrue("Couldn't parse " + item.formula, parseResult.isSuccess());
			
			Predicate formula = parseResult.getParsedPredicate();
			ITypeCheckResult result = formula.typeCheck(item.initialEnv);
			
			assertEquals("\nTest failed on: " + item.formula
					+ "\nParser result: " + formula.toString()
					+ "\nType check results:\n" + result.toString()
					+ "\nInitial type environment:\n" + result.getInitialTypeEnvironment() + "\n",
					item.result, result.isSuccess());
			assertEquals("\nResult typenv differ for: " + item.formula + "\n",
						item.inferredEnv, result.getInferredEnvironment());
			
			if (result.isSuccess()) {
				assertTrue(formula.isTypeChecked());
			}
		}
	}
	
	public void testAssignmentTypeChecker () {
		for (TestItem item: assignItems) {
			IParseResult parseResult = ff.parseAssignment(item.formula);
			assertTrue(parseResult.isSuccess());
			
			Assignment formula = parseResult.getParsedAssignment();
			ITypeCheckResult result = formula.typeCheck(item.initialEnv);
			
			assertEquals("\nTest failed on: " + item.formula
					+ "\nParser result: " + formula.toString()
					+ "\nType check results:\n" + result.toString()
					+ "\nInitial type environment:\n" + result.getInitialTypeEnvironment() + "\n",
					item.result, result.isSuccess());
			assertEquals("\nResult typenv differ for: " + item.formula + "\n",
						item.inferredEnv, result.getInferredEnvironment());
			
			if (result.isSuccess()) {
				assertTrue(formula.isTypeChecked());
			}
		}
	}
}

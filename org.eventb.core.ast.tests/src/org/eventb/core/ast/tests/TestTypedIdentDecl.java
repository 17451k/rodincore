package org.eventb.core.ast.tests;

import static org.eventb.core.ast.Formula.BTRUE;
import static org.eventb.core.ast.Formula.CSET;
import static org.eventb.core.ast.Formula.EXISTS;
import static org.eventb.core.ast.Formula.FORALL;
import static org.eventb.core.ast.Formula.QINTER;
import static org.eventb.core.ast.Formula.QUNION;
import static org.eventb.core.ast.Formula.SETMINUS;
import static org.eventb.core.ast.QuantifiedExpression.Form.Explicit;
import static org.eventb.core.ast.QuantifiedExpression.Form.Implicit;
import static org.eventb.core.ast.QuantifiedExpression.Form.Lambda;
import static org.eventb.core.ast.tests.FastFactory.mBinaryExpression;
import static org.eventb.core.ast.tests.FastFactory.mBoundIdentDecl;
import static org.eventb.core.ast.tests.FastFactory.mBoundIdentifier;
import static org.eventb.core.ast.tests.FastFactory.mList;
import static org.eventb.core.ast.tests.FastFactory.mLiteralPredicate;
import static org.eventb.core.ast.tests.FastFactory.mMaplet;
import static org.eventb.core.ast.tests.FastFactory.mQuantifiedExpression;
import static org.eventb.core.ast.tests.FastFactory.mQuantifiedPredicate;
import static org.eventb.core.ast.tests.FastFactory.mTypeEnvironment;
import junit.framework.TestCase;

import org.eventb.core.ast.BoundIdentDecl;
import org.eventb.core.ast.BoundIdentifier;
import org.eventb.core.ast.Expression;
import org.eventb.core.ast.FormulaFactory;
import org.eventb.core.ast.GivenType;
import org.eventb.core.ast.IParseResult;
import org.eventb.core.ast.ITypeCheckResult;
import org.eventb.core.ast.ITypeEnvironment;
import org.eventb.core.ast.Predicate;
import org.eventb.core.ast.Type;

public class TestTypedIdentDecl extends TestCase {

	private static FormulaFactory ff = FormulaFactory.getDefault();

	// Types used in these tests
	private static GivenType ty_S = ff.makeGivenType("S");
	private static GivenType ty_T = ff.makeGivenType("T");
	private static GivenType ty_U = ff.makeGivenType("U");

	private static Type POW(Type base) {
		return ff.makePowerSetType(base);
	}

	private static Type CPROD(Type left, Type right) {
		return ff.makeProductType(left, right);
	}
	
	private static Type REL(Type left, Type right) {
		return ff.makeRelationalType(left, right);
	}
	
	private static ITypeEnvironment env = mTypeEnvironment();
	
	private static BoundIdentDecl bxS = mBoundIdentDecl("x", ty_S);
	private static BoundIdentDecl bxPS = mBoundIdentDecl("x", POW(ty_S));
	private static BoundIdentDecl byT = mBoundIdentDecl("y", ty_T);
	private static BoundIdentDecl byPS = mBoundIdentDecl("y", POW(ty_S));
	private static BoundIdentDecl bzU = mBoundIdentDecl("z", ty_U);
	
	private static BoundIdentifier b0S = mBoundIdentifier(0, ty_S);
	private static BoundIdentifier b0T = mBoundIdentifier(0, ty_T);
	private static BoundIdentifier b0U = mBoundIdentifier(0, ty_U);
	private static BoundIdentifier b0PS = mBoundIdentifier(0, POW(ty_S));
	private static BoundIdentifier b1S = mBoundIdentifier(1, ty_S);
	private static BoundIdentifier b1T = mBoundIdentifier(1, ty_T);
	private static BoundIdentifier b1PS = mBoundIdentifier(1, POW(ty_S));
	private static BoundIdentifier b2S = mBoundIdentifier(2, ty_S);

	/**
	 * Main test routine for expressions containing bound identifier declarations.
	 */
	public void testExpressions () {

		// Comprehension set and lambda abstraction
		doTest(mQuantifiedExpression(CSET, Lambda,
				mList(bxS),
				mLiteralPredicate(BTRUE),
				mMaplet(b0S, b0S)),
				REL(ty_S, ty_S)
		);
		doTest(mQuantifiedExpression(CSET, Lambda,
				mList(bxS, byT),
				mLiteralPredicate(BTRUE),
				mMaplet(mMaplet(b1S, b0T), b1S)),
				REL(CPROD(ty_S, ty_T), ty_S)
		);
		doTest(mQuantifiedExpression(CSET, Implicit,
				mList(bxS),
				mLiteralPredicate(BTRUE),
				b0S),
				POW(ty_S)
		);
		doTest(mQuantifiedExpression(CSET, Implicit,
				mList(bxS, byT),
				mLiteralPredicate(BTRUE),
				mMaplet(b1S, b0T)),
				POW(CPROD(ty_S, ty_T))
		);
		doTest(mQuantifiedExpression(CSET, Explicit,
				mList(bxS),
				mLiteralPredicate(BTRUE),
				b0S),
				POW(ty_S)
		);
		doTest(mQuantifiedExpression(CSET, Explicit,
				mList(bxS, byT),
				mLiteralPredicate(BTRUE),
				mMaplet(b1S, b0T)),
				POW(CPROD(ty_S, ty_T))
		);

		// Quantified union and intersection
		doTest(mQuantifiedExpression(QUNION, Implicit,
				mList(bxPS),
				mLiteralPredicate(BTRUE),
				b0PS),
				POW(ty_S)
		);
		doTest(mQuantifiedExpression(QUNION, Implicit,
				mList(bxPS, byPS),
				mLiteralPredicate(BTRUE),
				mBinaryExpression(SETMINUS, b1PS, b0PS)),
				POW(ty_S)
		);
		doTest(mQuantifiedExpression(QINTER, Implicit,
				mList(bxPS),
				mLiteralPredicate(BTRUE),
				b0PS),
				POW(ty_S)
		);
		doTest(mQuantifiedExpression(QINTER, Implicit,
				mList(bxPS, byPS),
				mLiteralPredicate(BTRUE),
				mBinaryExpression(SETMINUS, b1PS, b0PS)),
				POW(ty_S)
		);
		doTest(mQuantifiedExpression(QUNION, Explicit,
				mList(bxPS),
				mLiteralPredicate(BTRUE),
				b0PS),
				POW(ty_S)
		);
		doTest(mQuantifiedExpression(QUNION, Explicit,
				mList(bxPS, byPS),
				mLiteralPredicate(BTRUE),
				mBinaryExpression(SETMINUS, b1PS, b0PS)),
				POW(ty_S)
		);
		doTest(mQuantifiedExpression(QINTER, Explicit,
				mList(bxPS),
				mLiteralPredicate(BTRUE),
				b0PS),
				POW(ty_S)
		);
		doTest(mQuantifiedExpression(QINTER, Explicit,
				mList(bxPS, byPS),
				mLiteralPredicate(BTRUE),
				mBinaryExpression(SETMINUS, b1PS, b0PS)),
				POW(ty_S)
		);

		// Lambda abstraction with complex patterns
		doTest(mQuantifiedExpression(CSET, Lambda,
				mList(bxS, byT, bzU),
				mLiteralPredicate(BTRUE),
				mMaplet(mMaplet(mMaplet(b2S, b1T), b0U), b2S)),
				REL(CPROD(CPROD(ty_S, ty_T), ty_U), ty_S)
		);
		doTest(mQuantifiedExpression(CSET, Lambda,
				mList(bxS, byT, bzU),
				mLiteralPredicate(BTRUE),
				mMaplet(mMaplet(b2S, mMaplet(b1T, b0U)), b2S)),
				REL(CPROD(ty_S, CPROD(ty_T, ty_U)), ty_S)
		);
		
	}
	
	private void doTest(Expression expr, Type expected) {
		assertTrue("Input is not typed", expr.isTypeChecked());
		assertEquals("Bad type", expected, expr.getType());
		final String image = expr.toStringWithTypes();
		final IParseResult pResult = ff.parseExpression(image);
		assertTrue("Typed string didn't parse", pResult.isSuccess());
		Expression actual = pResult.getParsedExpression();
		final ITypeCheckResult result = actual.typeCheck(env);
		assertTrue("Expression didn't typecheck", result.isSuccess());
		assertEquals("Typed string is a different expression", expr, actual);
	}

	/**
	 * Main test routine for predicates.
	 */
	public void testPredicates () {
		doTest(mQuantifiedPredicate(FORALL,
				mList(bxS),
				mLiteralPredicate(BTRUE)
		));
		doTest(mQuantifiedPredicate(FORALL,
				mList(bxS, byT),
				mLiteralPredicate(BTRUE)
		));
		doTest(mQuantifiedPredicate(EXISTS,
				mList(bxS),
				mLiteralPredicate(BTRUE)
		));
		doTest(mQuantifiedPredicate(EXISTS,
				mList(bxS, byT),
				mLiteralPredicate(BTRUE)
		));
	}
	
	private void doTest(Predicate pred) {
		assertTrue("Input is not typed", pred.isTypeChecked());
		final String image = pred.toStringWithTypes();
		final IParseResult pResult = ff.parsePredicate(image);
		assertTrue("Typed string didn't parse", pResult.isSuccess());
		final Predicate actual = pResult.getParsedPredicate();
		final ITypeCheckResult result = actual.typeCheck(env);
		assertTrue("Predicate didn't typecheck", result.isSuccess());
		assertEquals("Typed string is a different predicate", pred, actual);
	}

}

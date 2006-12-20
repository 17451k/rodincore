package org.eventb.core.ast.tests;

import static org.eventb.core.ast.Formula.BFALSE;
import static org.eventb.core.ast.Formula.BTRUE;
import static org.eventb.core.ast.Formula.CSET;
import static org.eventb.core.ast.Formula.EQUAL;
import static org.eventb.core.ast.Formula.FORALL;
import static org.eventb.core.ast.Formula.LAND;
import static org.eventb.core.ast.Formula.LIMP;
import static org.eventb.core.ast.Formula.MINUS;
import static org.eventb.core.ast.Formula.NOT;
import static org.eventb.core.ast.Formula.PLUS;
import static org.eventb.core.ast.Formula.QUNION;
import static org.eventb.core.ast.Formula.UNMINUS;
import static org.eventb.core.ast.QuantifiedExpression.Form.Explicit;
import static org.eventb.core.ast.QuantifiedExpression.Form.Implicit;
import static org.eventb.core.ast.QuantifiedExpression.Form.Lambda;
import static org.eventb.core.ast.tests.FastFactory.ff;
import static org.eventb.core.ast.tests.FastFactory.mAssociativeExpression;
import static org.eventb.core.ast.tests.FastFactory.mAssociativePredicate;
import static org.eventb.core.ast.tests.FastFactory.mAtomicExpression;
import static org.eventb.core.ast.tests.FastFactory.mBinaryExpression;
import static org.eventb.core.ast.tests.FastFactory.mBinaryPredicate;
import static org.eventb.core.ast.tests.FastFactory.mBoolExpression;
import static org.eventb.core.ast.tests.FastFactory.mBoundIdentDecl;
import static org.eventb.core.ast.tests.FastFactory.mBoundIdentifier;
import static org.eventb.core.ast.tests.FastFactory.mFreeIdentifier;
import static org.eventb.core.ast.tests.FastFactory.mIntegerLiteral;
import static org.eventb.core.ast.tests.FastFactory.mList;
import static org.eventb.core.ast.tests.FastFactory.mLiteralPredicate;
import static org.eventb.core.ast.tests.FastFactory.mMaplet;
import static org.eventb.core.ast.tests.FastFactory.mQuantifiedExpression;
import static org.eventb.core.ast.tests.FastFactory.mQuantifiedPredicate;
import static org.eventb.core.ast.tests.FastFactory.mRelationalPredicate;
import static org.eventb.core.ast.tests.FastFactory.mSetExtension;
import static org.eventb.core.ast.tests.FastFactory.mSimplePredicate;
import static org.eventb.core.ast.tests.FastFactory.mUnaryExpression;
import static org.eventb.core.ast.tests.FastFactory.mUnaryPredicate;

import java.util.List;

import junit.framework.TestCase;

import org.eventb.core.ast.AssociativeExpression;
import org.eventb.core.ast.AssociativePredicate;
import org.eventb.core.ast.AtomicExpression;
import org.eventb.core.ast.BinaryExpression;
import org.eventb.core.ast.BinaryPredicate;
import org.eventb.core.ast.BoolExpression;
import org.eventb.core.ast.BoundIdentDecl;
import org.eventb.core.ast.BoundIdentifier;
import org.eventb.core.ast.Expression;
import org.eventb.core.ast.Formula;
import org.eventb.core.ast.FreeIdentifier;
import org.eventb.core.ast.IFormulaFilter;
import org.eventb.core.ast.IPosition;
import org.eventb.core.ast.IntegerLiteral;
import org.eventb.core.ast.LiteralPredicate;
import org.eventb.core.ast.Predicate;
import org.eventb.core.ast.QuantifiedExpression;
import org.eventb.core.ast.QuantifiedPredicate;
import org.eventb.core.ast.RelationalPredicate;
import org.eventb.core.ast.SetExtension;
import org.eventb.core.ast.SimplePredicate;
import org.eventb.core.ast.Type;
import org.eventb.core.ast.UnaryExpression;
import org.eventb.core.ast.UnaryPredicate;
import org.eventb.core.ast.QuantifiedExpression.Form;

public class TestSubFormulas extends TestCase {

	private static class FixedFilter implements IFormulaFilter {
		
		final Formula searched;
		final Formula replacement;
		
		public FixedFilter(Formula searched, Formula replacement) {
			this.searched = searched;
			this.replacement = replacement;
		}

		public boolean retainAssociativeExpression(AssociativeExpression expression) {
			return searched.equals(expression);
		}

		public boolean retainAssociativePredicate(AssociativePredicate predicate) {
			return searched.equals(predicate);
		}

		public boolean retainAtomicExpression(AtomicExpression expression) {
			return searched.equals(expression);
		}

		public boolean retainBinaryExpression(BinaryExpression expression) {
			return searched.equals(expression);
		}

		public boolean retainBinaryPredicate(BinaryPredicate predicate) {
			return searched.equals(predicate);
		}

		public boolean retainBoolExpression(BoolExpression expression) {
			return searched.equals(expression);
		}

		public boolean retainBoundIdentDecl(BoundIdentDecl decl) {
			return searched.equals(decl);
		}

		public boolean retainBoundIdentifier(BoundIdentifier identifier) {
			return searched.equals(identifier);
		}

		public boolean retainFreeIdentifier(FreeIdentifier identifier) {
			return searched.equals(identifier);
		}

		public boolean retainIntegerLiteral(IntegerLiteral literal) {
			return searched.equals(literal);
		}

		public boolean retainLiteralPredicate(LiteralPredicate predicate) {
			return searched.equals(predicate);
		}

		public boolean retainQuantifiedExpression(QuantifiedExpression expression) {
			return searched.equals(expression);
		}

		public boolean retainQuantifiedPredicate(QuantifiedPredicate predicate) {
			return searched.equals(predicate);
		}

		public boolean retainRelationalPredicate(RelationalPredicate predicate) {
			return searched.equals(predicate);
		}

		public boolean retainSetExtension(SetExtension expression) {
			return searched.equals(expression);
		}

		public boolean retainSimplePredicate(SimplePredicate predicate) {
			return searched.equals(predicate);
		}

		public boolean retainUnaryExpression(UnaryExpression expression) {
			return searched.equals(expression);
		}

		public boolean retainUnaryPredicate(UnaryPredicate predicate) {
			return searched.equals(predicate);
		}
		
	}

	private Type INT = ff.makeIntegerType();
	
	private Type POW(Type base) {
		return ff.makePowerSetType(base);
	}

	private Predicate btrue = mLiteralPredicate(BTRUE);
	
	private BoundIdentDecl bd_x = mBoundIdentDecl("x", INT);
	private BoundIdentDecl bd_X = mBoundIdentDecl("X", INT);
	private BoundIdentDecl bd_y = mBoundIdentDecl("y", INT);
	private BoundIdentDecl bd_z = mBoundIdentDecl("z", INT);
	
	private FreeIdentifier id_x = mFreeIdentifier("x", INT);
	private FreeIdentifier id_X = mFreeIdentifier("X", INT);
	private FreeIdentifier id_y = mFreeIdentifier("y", INT);
	private FreeIdentifier id_S = mFreeIdentifier("S", POW(INT));
	private FreeIdentifier id_T = mFreeIdentifier("T", POW(INT));
	
	private Expression b0 = mBoundIdentifier(0, INT);
	private Expression b1 = mBoundIdentifier(1, INT);

	private Expression m0x = mMaplet(b0, id_x);
	private Expression m0X = mMaplet(b0, id_X);
	private Expression m01x = mMaplet(mMaplet(b0, b1), id_x);
	private Expression m01X = mMaplet(mMaplet(b0, b1), id_X);
	private Expression m0y = mMaplet(b0, id_y);
	
	private RelationalPredicate equals = mRelationalPredicate(EQUAL, id_x, id_x);
	private RelationalPredicate equalsX = mRelationalPredicate(EQUAL, id_X, id_X);

	private FixedFilter bdFilter = new FixedFilter(bd_x, bd_X);
	private FixedFilter idFilter = new FixedFilter(id_x, id_X);
	private FixedFilter equalsFilter = new FixedFilter(equals, equalsX);

	private <T extends Formula<T>> void checkPositions(FixedFilter filter,
			Formula<T> formula, final Object... args) {
		
		assertTrue(formula.isTypeChecked());
		assertEquals(0, args.length & 1);
		final List<IPosition> actualPositions = formula.getPositions(filter);
		final int length = args.length;
		assertEquals("wrong number of positions retrieved",
				length / 2, actualPositions.size());
		for (int i = 0; i < length; i += 2) {
			String expectedPos = (String) args[i];
			Formula expRewrite = (Formula) args[i+1];
			final IPosition actualPos = actualPositions.get(i/2);
			assertEquals("Unexpected position",
					expectedPos, actualPos.toString());
			assertEquals("Unexpected sub-formula",
					filter.searched, formula.getSubFormula(actualPos));
			assertEquals("Unexpected rewrite", expRewrite,
					formula.rewriteSubFormula(actualPos, filter.replacement, ff));
		}
	}
	
	private void checkBdFilterQExpr(int tag, Form form) {
		checkPositions(
				bdFilter,
				mQuantifiedExpression(tag, form, mList(bd_y), btrue, id_S));
		checkPositions(
				bdFilter,
				mQuantifiedExpression(tag, form, mList(bd_x), btrue, id_S),
				"0",
				mQuantifiedExpression(tag, form, mList(bd_X), btrue, id_S));
		checkPositions(
				bdFilter,
				mQuantifiedExpression(tag, form, mList(bd_x, bd_y), btrue, id_S),
				"0",
				mQuantifiedExpression(tag, form, mList(bd_X, bd_y), btrue, id_S));
		checkPositions(
				bdFilter,
				mQuantifiedExpression(tag, form, mList(bd_x, bd_y, bd_z), btrue, id_S),
				"0",
				mQuantifiedExpression(tag, form, mList(bd_X, bd_y, bd_z), btrue, id_S));
		checkPositions(
				bdFilter,
				mQuantifiedExpression(tag, form, mList(bd_y, bd_x), btrue, id_S),
				"1",
				mQuantifiedExpression(tag, form, mList(bd_y, bd_X), btrue, id_S));
		checkPositions(
				bdFilter,
				mQuantifiedExpression(tag, form, mList(bd_z, bd_y, bd_x), btrue, id_S),
				"2",
				mQuantifiedExpression(tag, form, mList(bd_z, bd_y, bd_X), btrue, id_S));
		checkPositions(
				bdFilter,
				mQuantifiedExpression(tag, form, mList(bd_z, bd_x, bd_x), btrue, id_S),
				"1",
				mQuantifiedExpression(tag, form, mList(bd_z, bd_X, bd_x), btrue, id_S),
				"2",
				mQuantifiedExpression(tag, form, mList(bd_z, bd_x, bd_X), btrue, id_S));
	}

	/**
	 * Ensures that the position of a bound identifier declaration can be
	 * retrieved or not retrieved from all places where a declaration can occur.
	 */
	public void testBdFilter() throws Exception {
		checkPositions(bdFilter, bd_y);
		checkPositions(bdFilter, bd_x, "", bd_X);
		
		checkBdFilterQExpr(QUNION, Implicit);
		checkBdFilterQExpr(QUNION, Explicit);
		checkBdFilterQExpr(CSET, Implicit);
		checkBdFilterQExpr(CSET, Explicit);
		checkPositions(bdFilter,
				mQuantifiedExpression(CSET, Lambda, mList(bd_y), btrue, m0x));
		checkPositions(bdFilter,
				mQuantifiedExpression(CSET, Lambda, mList(bd_x), btrue, m0x),
				"0",
				mQuantifiedExpression(CSET, Lambda, mList(bd_X), btrue, m0x));
		checkPositions(bdFilter,
				mQuantifiedExpression(CSET, Lambda, mList(bd_x, bd_y), btrue, m01x),
				"0",
				mQuantifiedExpression(CSET, Lambda, mList(bd_X, bd_y), btrue, m01x));
		checkPositions(bdFilter,
				mQuantifiedExpression(CSET, Lambda, mList(bd_y, bd_x), btrue, m01x),
				"1",
				mQuantifiedExpression(CSET, Lambda, mList(bd_y, bd_X), btrue, m01x));
		
		checkPositions(bdFilter,
				mQuantifiedPredicate(mList(bd_x), btrue),
				"0",
				mQuantifiedPredicate(mList(bd_X), btrue));
		checkPositions(bdFilter,
				mQuantifiedPredicate(mList(bd_y), btrue));
		checkPositions(bdFilter, 
				mQuantifiedPredicate(mList(bd_x, bd_y), btrue),
				"0",
				mQuantifiedPredicate(mList(bd_X, bd_y), btrue));
		checkPositions(bdFilter, 
				mQuantifiedPredicate(mList(bd_x, bd_y, bd_z), btrue),
				"0",
				mQuantifiedPredicate(mList(bd_X, bd_y, bd_z), btrue));
		checkPositions(bdFilter,
				mQuantifiedPredicate(mList(bd_y, bd_x), btrue),
				"1",
				mQuantifiedPredicate(mList(bd_y, bd_X), btrue));
		checkPositions(bdFilter,
				mQuantifiedPredicate(mList(bd_z, bd_y, bd_x), btrue),
				"2",
				mQuantifiedPredicate(mList(bd_z, bd_y, bd_X), btrue));
		checkPositions(bdFilter,
				mQuantifiedPredicate(mList(bd_x, bd_y, bd_x), btrue),
				"0",
				mQuantifiedPredicate(mList(bd_X, bd_y, bd_x), btrue),
				"2",
				mQuantifiedPredicate(mList(bd_x, bd_y, bd_X), btrue));
	}
	
	/**
	 * Ensures that the position of an expression can be retrieved or not
	 * retrieved from all places where an expression can occur.
	 */
	public void testIdFilter() throws Exception {
		checkPositions(idFilter,
				mAssociativeExpression(PLUS, id_y, id_y));
		checkPositions(idFilter,
				mAssociativeExpression(PLUS, id_x, id_y),
				"0",
				mAssociativeExpression(PLUS, id_X, id_y));
		checkPositions(idFilter,
				mAssociativeExpression(PLUS, id_y, id_x),
				"1",
				mAssociativeExpression(PLUS, id_y, id_X));
		checkPositions(idFilter,
				mAssociativeExpression(PLUS, id_x, id_y, id_x),
				"0",
				mAssociativeExpression(PLUS, id_X, id_y, id_x),
				"2",
				mAssociativeExpression(PLUS, id_x, id_y, id_X));
		
		checkPositions(idFilter,
				mAtomicExpression());
		
		checkPositions(idFilter,
				mBinaryExpression(MINUS, id_x, id_x),
				"0",
				mBinaryExpression(MINUS, id_X, id_x),
				"1",
				mBinaryExpression(MINUS, id_x, id_X));
		checkPositions(idFilter,
				mBinaryExpression(MINUS, id_x, id_y),
				"0",
				mBinaryExpression(MINUS, id_X, id_y));
		checkPositions(idFilter,
				mBinaryExpression(MINUS, id_y, id_x),
				"1",
				mBinaryExpression(MINUS, id_y, id_X));
		checkPositions(idFilter,
				mBinaryExpression(MINUS, id_y, id_y));
		
		checkPositions(idFilter, b0);

		checkPositions(idFilter, id_y);
		checkPositions(idFilter, id_x, "", id_X);
		
		checkPositions(idFilter, mIntegerLiteral());
		
		checkPositions(idFilter,
				mQuantifiedExpression(CSET, Implicit, mList(bd_x), btrue, id_y));
		checkPositions(idFilter,
				mQuantifiedExpression(CSET, Implicit, mList(bd_x), btrue, id_x),
				"2",
				mQuantifiedExpression(CSET, Implicit, mList(bd_x), btrue, id_X));
		checkPositions(idFilter,
				mQuantifiedExpression(CSET, Explicit, mList(bd_x), btrue, id_y));
		checkPositions(idFilter,
				mQuantifiedExpression(CSET, Explicit, mList(bd_x), btrue, id_x),
				"2",
				mQuantifiedExpression(CSET, Explicit, mList(bd_x), btrue, id_X));
		checkPositions(idFilter,
				mQuantifiedExpression(CSET, Lambda, mList(bd_x), btrue, m0y));
		checkPositions(idFilter,
				mQuantifiedExpression(CSET, Lambda, mList(bd_x), btrue, m0x),
				"2.1",
				mQuantifiedExpression(CSET, Lambda, mList(bd_x), btrue, m0X));
		checkPositions(idFilter,
				mQuantifiedExpression(CSET, Lambda, mList(bd_x, bd_y), btrue, m01x),
				"3.1",
				mQuantifiedExpression(CSET, Lambda, mList(bd_x, bd_y), btrue, m01X));
		
		checkPositions(idFilter,
				mRelationalPredicate(EQUAL, id_x, id_x),
				"0",
				mRelationalPredicate(EQUAL, id_X, id_x),
				"1",
				mRelationalPredicate(EQUAL, id_x, id_X));
		checkPositions(idFilter,
				mRelationalPredicate(EQUAL, id_x, id_y),
				"0",
				mRelationalPredicate(EQUAL, id_X, id_y));
		checkPositions(idFilter,
				mRelationalPredicate(EQUAL, id_y, id_x),
				"1",
				mRelationalPredicate(EQUAL, id_y, id_X));
		checkPositions(idFilter,
				mRelationalPredicate(EQUAL, id_y, id_y));

		checkPositions(idFilter,
				mSetExtension(id_x, id_y),
				"0",
				mSetExtension(id_X, id_y));
		checkPositions(idFilter,
				mSetExtension(id_y, id_x),
				"1",
				mSetExtension(id_y, id_X));
		checkPositions(idFilter,
				mSetExtension(id_x, id_y, id_x),
				"0",
				mSetExtension(id_X, id_y, id_x),
				"2",
				mSetExtension(id_x, id_y, id_X));
		
		checkPositions(idFilter, mSimplePredicate(id_S));
		checkPositions(new FixedFilter(id_S, id_T),
				mSimplePredicate(id_S),
				"0",
				mSimplePredicate(id_T));

		checkPositions(idFilter,
				mUnaryExpression(UNMINUS, id_x),
				"0",
				mUnaryExpression(UNMINUS, id_X));
	}
	
	/**
	 * Ensures that the position of a predicate can be retrieved from all
	 * contexts.
	 */
	public void testEqualsFilter() throws Exception {
		checkPositions(equalsFilter,
				mAssociativePredicate(LAND, equals, btrue),
				"0",
				mAssociativePredicate(LAND, equalsX, btrue));
		checkPositions(equalsFilter,
				mAssociativePredicate(LAND, btrue, equals),
				"1",
				mAssociativePredicate(LAND, btrue, equalsX));
		checkPositions(equalsFilter, 
				mAssociativePredicate(LAND, equals, btrue, equals),
				"0", 
				mAssociativePredicate(LAND, equalsX, btrue, equals),
				"2", 
				mAssociativePredicate(LAND, equals, btrue, equalsX));
		
		checkPositions(equalsFilter,
				mBinaryPredicate(LIMP, btrue, btrue));
		checkPositions(equalsFilter,
				mBinaryPredicate(LIMP, equals, btrue),
				"0",
				mBinaryPredicate(LIMP, equalsX, btrue));
		checkPositions(equalsFilter,
				mBinaryPredicate(LIMP, btrue, equals),
				"1",
				mBinaryPredicate(LIMP, btrue, equalsX));
		checkPositions(equalsFilter,
				mBinaryPredicate(LIMP, equals, equals),
				"0",
				mBinaryPredicate(LIMP, equalsX, equals),
				"1",
				mBinaryPredicate(LIMP, equals, equalsX));
		
		checkPositions(equalsFilter,
				mBoolExpression(btrue));
		checkPositions(equalsFilter,
				mBoolExpression(equals),
				"0",
				mBoolExpression(equalsX));
		
		checkPositions(equalsFilter,
				mLiteralPredicate());

		checkPositions(equalsFilter,
				mQuantifiedExpression(CSET, Implicit, mList(bd_x), btrue, id_x));
		checkPositions(equalsFilter,
				mQuantifiedExpression(CSET, Implicit, mList(bd_x), equals, id_x),
				"1",
				mQuantifiedExpression(CSET, Implicit, mList(bd_x), equalsX, id_x));
		checkPositions(equalsFilter,
				mQuantifiedExpression(CSET, Implicit, mList(bd_x, bd_y), equals, id_x),
				"2",
				mQuantifiedExpression(CSET, Implicit, mList(bd_x, bd_y), equalsX, id_x));
		checkPositions(equalsFilter,
				mQuantifiedExpression(CSET, Explicit, mList(bd_x), btrue, id_x));
		checkPositions(equalsFilter,
				mQuantifiedExpression(CSET, Explicit, mList(bd_x), equals, id_x),
				"1",
				mQuantifiedExpression(CSET, Explicit, mList(bd_x), equalsX, id_x));
		checkPositions(equalsFilter,
				mQuantifiedExpression(CSET, Explicit, mList(bd_x, bd_y), equals, id_x),
				"2",
				mQuantifiedExpression(CSET, Explicit, mList(bd_x, bd_y), equalsX, id_x));
		checkPositions(equalsFilter,
				mQuantifiedExpression(CSET, Lambda, mList(bd_x), btrue, m0x));
		checkPositions(equalsFilter,
				mQuantifiedExpression(CSET, Lambda, mList(bd_x), equals, m0x),
				"1",
				mQuantifiedExpression(CSET, Lambda, mList(bd_x), equalsX, m0x));
		checkPositions(equalsFilter,
				mQuantifiedExpression(CSET, Lambda, mList(bd_x, bd_y), equals, m01x),
				"2",
				mQuantifiedExpression(CSET, Lambda, mList(bd_x, bd_y), equalsX, m01x));
		
		checkPositions(equalsFilter,
				mQuantifiedPredicate(FORALL, mList(bd_x), btrue));
		checkPositions(equalsFilter,
				mQuantifiedPredicate(FORALL, mList(bd_x), equals),
				"1",
				mQuantifiedPredicate(FORALL, mList(bd_x), equalsX));
		checkPositions(equalsFilter,
				mQuantifiedPredicate(FORALL, mList(bd_x, bd_y), equals),
				"2",
				mQuantifiedPredicate(FORALL, mList(bd_x, bd_y), equalsX));
		
		checkPositions(equalsFilter,
				mUnaryPredicate(NOT, btrue));
		checkPositions(equalsFilter,
				mUnaryPredicate(NOT, equals),
				"0",
				mUnaryPredicate(NOT, equalsX));
	}
	
	private <T extends Formula<T>> void checkRootPosition(Formula<T> f1,
			Formula<T> f2) {
		assertEquals(f1.getClass(), f2.getClass());
		assertFalse(f1.equals(f2));
		final FixedFilter filter = new FixedFilter(f1, f2);
		checkPositions(filter, f2);
		checkPositions(filter, f1, "", f2);
	}
	
	/**
	 * Ensures that filtering is implemented for all kinds of formulas.  Also
	 * ensures that one can rewrite the root of any formula.
	 */
	public void testAllClasses() throws Exception {
		checkRootPosition(
				mAssociativeExpression(PLUS, id_x, id_x),
				mAssociativeExpression(PLUS, id_x, id_y)
		);
		checkRootPosition(
				mAssociativePredicate(LAND, btrue, equals),
				mAssociativePredicate(LAND, btrue, btrue)
		);
		checkRootPosition(
				mBinaryExpression(MINUS, id_x, id_x),
				mBinaryExpression(MINUS, id_x, id_y)
		);
		checkRootPosition(
				mBinaryPredicate(LIMP, btrue, equals),
				mBinaryPredicate(LIMP, btrue, btrue)
		);
		checkRootPosition(
				mBoolExpression(equals),
				mBoolExpression(btrue)
		);
		checkRootPosition(
				mBoundIdentDecl("x", INT),
				mBoundIdentDecl("y", INT)
		);
		checkRootPosition(
				mBoundIdentifier(0, INT),
				mBoundIdentifier(1, INT)
		);
		checkRootPosition(
				mFreeIdentifier("x", INT),
				mFreeIdentifier("y", INT)
		);
		checkRootPosition(
				mIntegerLiteral(0),
				mIntegerLiteral(1)
		);
		checkRootPosition(
				mLiteralPredicate(BTRUE),
				mLiteralPredicate(BFALSE)
		);
		checkRootPosition(
				mQuantifiedExpression(CSET, Implicit, mList(bd_x), btrue, id_x),
				mQuantifiedExpression(CSET, Implicit, mList(bd_x), btrue, id_y)
		);
		checkRootPosition(
				mQuantifiedPredicate(FORALL, mList(bd_x), equals),
				mQuantifiedPredicate(FORALL, mList(bd_x), btrue)
		);
		checkRootPosition(
				mRelationalPredicate(EQUAL, id_x, id_x),
				mRelationalPredicate(EQUAL, id_x, id_y)
		);
		checkRootPosition(
				mSetExtension(id_x),
				mSetExtension(id_y)
		);
		checkRootPosition(
				mSimplePredicate(id_S),
				mSimplePredicate(id_T)
		);
		checkRootPosition(
				mUnaryExpression(UNMINUS, id_x),
				mUnaryExpression(UNMINUS, id_y)
		);
		checkRootPosition(
				mUnaryPredicate(NOT, equals),
				mUnaryPredicate(NOT, btrue)
		);
	}
	
	/**
	 * Ensures that a sub-expression that occurs deeply in a formula can be
	 * retrieved.
	 */
	public void testDeep() {
		checkPositions(idFilter,
				mAssociativePredicate(
						mRelationalPredicate(EQUAL, id_x, id_y),
						mRelationalPredicate(EQUAL, id_y,
								mBinaryExpression(MINUS, id_x, id_y))
				),
				"0.0",
				mAssociativePredicate(
						mRelationalPredicate(EQUAL, id_X, id_y),
						mRelationalPredicate(EQUAL, id_y,
								mBinaryExpression(MINUS, id_x, id_y))
				),
				"1.1.0",
				mAssociativePredicate(
						mRelationalPredicate(EQUAL, id_x, id_y),
						mRelationalPredicate(EQUAL, id_y,
								mBinaryExpression(MINUS, id_X, id_y))
				)
		);
		
	}
	
}

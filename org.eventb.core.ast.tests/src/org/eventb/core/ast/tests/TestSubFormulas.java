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
import static org.eventb.core.ast.Formula.SETMINUS;
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
import static org.eventb.core.ast.tests.FastFactory.mEmptySet;
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
import org.eventb.core.ast.DefaultFilter;
import org.eventb.core.ast.DefaultRewriter;
import org.eventb.core.ast.Expression;
import org.eventb.core.ast.Formula;
import org.eventb.core.ast.FreeIdentifier;
import org.eventb.core.ast.IFormulaFilter;
import org.eventb.core.ast.IFormulaRewriter;
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

		public boolean select(AssociativeExpression expression) {
			return searched.equals(expression);
		}

		public boolean select(AssociativePredicate predicate) {
			return searched.equals(predicate);
		}

		public boolean select(AtomicExpression expression) {
			return searched.equals(expression);
		}

		public boolean select(BinaryExpression expression) {
			return searched.equals(expression);
		}

		public boolean select(BinaryPredicate predicate) {
			return searched.equals(predicate);
		}

		public boolean select(BoolExpression expression) {
			return searched.equals(expression);
		}

		public boolean select(BoundIdentDecl decl) {
			return searched.equals(decl);
		}

		public boolean select(BoundIdentifier identifier) {
			return searched.equals(identifier);
		}

		public boolean select(FreeIdentifier identifier) {
			return searched.equals(identifier);
		}

		public boolean select(IntegerLiteral literal) {
			return searched.equals(literal);
		}

		public boolean select(LiteralPredicate predicate) {
			return searched.equals(predicate);
		}

		public boolean select(QuantifiedExpression expression) {
			return searched.equals(expression);
		}

		public boolean select(QuantifiedPredicate predicate) {
			return searched.equals(predicate);
		}

		public boolean select(RelationalPredicate predicate) {
			return searched.equals(predicate);
		}

		public boolean select(SetExtension expression) {
			return searched.equals(expression);
		}

		public boolean select(SimplePredicate predicate) {
			return searched.equals(predicate);
		}

		public boolean select(UnaryExpression expression) {
			return searched.equals(expression);
		}

		public boolean select(UnaryPredicate predicate) {
			return searched.equals(predicate);
		}
		
	}

	private static class FixedRewriter extends DefaultRewriter {
		final Formula from;
		final Formula to;
		
		public FixedRewriter(Formula from, Formula to) {
			super(false, FastFactory.ff);
			this.from = from;
			this.to = to;
		}

		private Formula doRewrite(Formula expr) {
			if (expr.equals(from)) {
				return to;
			}
			return expr;
		}
		
		@Override
		public Expression rewrite(AssociativeExpression expression) {
			return (Expression) doRewrite(expression);
		}

		@Override
		public Predicate rewrite(AssociativePredicate predicate) {
			return (Predicate) doRewrite(predicate);
		}

		@Override
		public Expression rewrite(AtomicExpression expression) {
			return (Expression) doRewrite(expression);
		}

		@Override
		public Expression rewrite(BinaryExpression expression) {
			return (Expression) doRewrite(expression);
		}

		@Override
		public Predicate rewrite(BinaryPredicate predicate) {
			return (Predicate) doRewrite(predicate);
		}

		@Override
		public Expression rewrite(BoolExpression expression) {
			return (Expression) doRewrite(expression);
		}

		@Override
		public Expression rewrite(BoundIdentifier identifier) {
			return (Expression) doRewrite(identifier);
		}

		@Override
		public Expression rewrite(FreeIdentifier identifier) {
			return (Expression) doRewrite(identifier);
		}

		@Override
		public Expression rewrite(IntegerLiteral literal) {
			return (Expression) doRewrite(literal);
		}

		@Override
		public Predicate rewrite(LiteralPredicate predicate) {
			return (Predicate) doRewrite(predicate);
		}

		@Override
		public Expression rewrite(QuantifiedExpression expression) {
			return (Expression) doRewrite(expression);
		}

		@Override
		public Predicate rewrite(QuantifiedPredicate predicate) {
			return (Predicate) doRewrite(predicate);
		}

		@Override
		public Predicate rewrite(RelationalPredicate predicate) {
			return (Predicate) doRewrite(predicate);
		}

		@Override
		public Expression rewrite(SetExtension expression) {
			return (Expression) doRewrite(expression);
		}

		@Override
		public Predicate rewrite(SimplePredicate predicate) {
			return (Predicate) doRewrite(predicate);
		}

		@Override
		public Expression rewrite(UnaryExpression expression) {
			return (Expression) doRewrite(expression);
		}

		@Override
		public Predicate rewrite(UnaryPredicate predicate) {
			return (Predicate) doRewrite(predicate);
		}
	}

	private static Type INT = ff.makeIntegerType();
	
	private static Type POW(Type base) {
		return ff.makePowerSetType(base);
	}

	private static Predicate btrue = mLiteralPredicate(BTRUE);
	
	private static BoundIdentDecl bd_x = mBoundIdentDecl("x", INT);
	private static BoundIdentDecl bd_X = mBoundIdentDecl("X", INT);
	private static BoundIdentDecl bd_y = mBoundIdentDecl("y", INT);
	private static BoundIdentDecl bd_z = mBoundIdentDecl("z", INT);
	
	private static FreeIdentifier id_x = mFreeIdentifier("x", INT);
	private static FreeIdentifier id_X = mFreeIdentifier("X", INT);
	private static FreeIdentifier id_y = mFreeIdentifier("y", INT);
	private static FreeIdentifier id_S = mFreeIdentifier("S", POW(INT));
	private static FreeIdentifier id_T = mFreeIdentifier("T", POW(INT));
	
	private static Expression b0 = mBoundIdentifier(0, INT);
	private static Expression b1 = mBoundIdentifier(1, INT);

	private static Expression m0x = mMaplet(b0, id_x);
	private static Expression m0X = mMaplet(b0, id_X);
	private static Expression m01x = mMaplet(mMaplet(b0, b1), id_x);
	private static Expression m01X = mMaplet(mMaplet(b0, b1), id_X);
	private static Expression m0y = mMaplet(b0, id_y);
	
	private static RelationalPredicate equals =
		mRelationalPredicate(EQUAL, id_x, id_x);
	private static RelationalPredicate equalsX =
		mRelationalPredicate(EQUAL, id_X, id_X);

	private static FixedFilter bdFilter = new FixedFilter(bd_x, bd_X);
	private static FixedFilter idFilter = new FixedFilter(id_x, id_X);
	private static FixedFilter equalsFilter = new FixedFilter(equals, equalsX);

	private static final IFormulaFilter defaultFilter = new DefaultFilter();
	
	private <T extends Formula<T>> void checkDefaultFilter(Formula<T> f) {
		final List<IPosition> actualPositions = f.getPositions(defaultFilter);
		assertEquals("Default filter should not select any position",
				0, actualPositions.size());
	}

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
		
		// Additional transversal test with the default filter
		checkDefaultFilter(formula);
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
		
		checkAllPositions(f1, IPosition.ROOT);
	}
	
	// Traverse the formula asking for all sub-formulas after the given position
	// (in preorder)
	private <T extends Formula<T>> void checkAllPositions(Formula<T> f,
			IPosition p) {
		Formula s = f.getSubFormula(p);
		if (s != null) {
			checkAllPositions(f, p.getFirstChild());
			if (! p.isRoot()) {
				checkAllPositions(f, p.getNextSibling());
			}
		}
	}

	/**
	 * Ensures that filtering is implemented for all kinds of formulas.  Also
	 * ensures that one can rewrite the root of any formula.
	 */
	public void testPositionAllClasses() throws Exception {
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
	public void testDeepPositions() {
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
	
	private final IFormulaRewriter identity = new DefaultRewriter(false, ff);

	private void checkIdentityRewriting(Formula formula) {
		assertSame(formula, formula.rewrite(identity));
	}

	private void checkRewriting(Formula from, Formula to,
			Formula before, Formula after) {

		// Actual rewriting
		FixedRewriter rewriter = new FixedRewriter(from, to);
		Formula actual = before.rewrite(rewriter);
		assertEquals("Unexpected rewritten formula", after, actual);
		
		// Identity rewriting returns an identical formula
		checkIdentityRewriting(before);
		checkIdentityRewriting(after);
	}

	private void checkRootRewriting(Formula from, Formula to) {

		FixedRewriter rewriter = new FixedRewriter(from, to);
		Formula actual = from.rewrite(rewriter);
		assertEquals("Unexpected rewritten formula", to, actual);
		
		// Identity rewriting returns an identical formula
		checkIdentityRewriting(from);
		checkIdentityRewriting(to);
	}

	/**
	 * Ensures that multiple expression rewriting can be performed in all places
	 * where a rewritable sub-expression can occur.
	 */
	public void testExpressionRewriting() {
		final Expression zero = mIntegerLiteral(0);
		final Expression i1 = mBinaryExpression(MINUS, id_x, zero);
		final Expression i2 = zero;

		final Expression empty = mEmptySet(POW(INT));
		final Expression s1 = mBinaryExpression(SETMINUS, id_S, empty);
		final Expression s2 = id_S;
		
		checkRewriting(i1, i2,
				mAssociativeExpression(PLUS, i1, id_y),
				mAssociativeExpression(PLUS, i2, id_y)
		);
		checkRewriting(i1, i2,
				mAssociativeExpression(PLUS, id_y, i1),
				mAssociativeExpression(PLUS, id_y, i2));
		checkRewriting(i1, i2,
				mAssociativeExpression(PLUS, i1, id_y, i1),
				mAssociativeExpression(PLUS, i2, id_y, i2));

		checkRewriting(i1, i2,
				mAtomicExpression(),
				mAtomicExpression());

		checkRewriting(i1, i2,
				mBinaryExpression(MINUS, i1, i1),
				mBinaryExpression(MINUS, i2, i2));
		checkRewriting(i1, i2,
				mBinaryExpression(MINUS, i1, id_y),
				mBinaryExpression(MINUS, i2, id_y));
		checkRewriting(i1, i2,
				mBinaryExpression(MINUS, id_y, i1),
				mBinaryExpression(MINUS, id_y, i2));
		checkRewriting(i1, i2,
				mBinaryExpression(MINUS, id_y, id_y),
				mBinaryExpression(MINUS, id_y, id_y));

		checkRewriting(i1, i2, b0, b0);

		checkRewriting(i1, i2, id_x, id_x);

		checkRewriting(i1, i2, zero, zero);

		checkRewriting(i1, i2,
				mQuantifiedExpression(CSET, Implicit, mList(bd_x), btrue, id_y),
				mQuantifiedExpression(CSET, Implicit, mList(bd_x), btrue, id_y));
		checkRewriting(i1, i2,
				mQuantifiedExpression(CSET, Implicit, mList(bd_x), btrue, i1),
				mQuantifiedExpression(CSET, Implicit, mList(bd_x), btrue, i2));
		checkRewriting(i1, i2,
				mQuantifiedExpression(CSET, Explicit, mList(bd_x), btrue, id_y),
				mQuantifiedExpression(CSET, Explicit, mList(bd_x), btrue, id_y));
		checkRewriting(i1, i2,
				mQuantifiedExpression(CSET, Explicit, mList(bd_x), btrue, i1),
				mQuantifiedExpression(CSET, Explicit, mList(bd_x), btrue, i2));
		checkRewriting(i1, i2,
				mQuantifiedExpression(CSET, Lambda, mList(bd_x), btrue,
						mMaplet(b0, i1)
				),
				mQuantifiedExpression(CSET, Lambda, mList(bd_x), btrue,
						mMaplet(b0, i2)
				));
		checkRewriting(i1, i2,
				mQuantifiedExpression(CSET, Lambda, mList(bd_x, bd_y), btrue,
						mMaplet(mMaplet(b0, b1), i1)
				),
				mQuantifiedExpression(CSET, Lambda, mList(bd_x, bd_y), btrue,
						mMaplet(mMaplet(b0, b1), i2)
				));
		checkRewriting(i1, i2,
				mRelationalPredicate(EQUAL, i1, i1),
				mRelationalPredicate(EQUAL, i2, i2));
		checkRewriting(i1, i2,
				mRelationalPredicate(EQUAL, i1, id_y),
				mRelationalPredicate(EQUAL, i2, id_y));
		checkRewriting(i1, i2,
				mRelationalPredicate(EQUAL, id_y, i1),
				mRelationalPredicate(EQUAL, id_y, i2));
		checkRewriting(i1, i2,
				mRelationalPredicate(EQUAL, id_y, id_y),
				mRelationalPredicate(EQUAL, id_y, id_y));

		checkRewriting(i1, i2,
				mSetExtension(i1, id_y),
				mSetExtension(i2, id_y));
		checkRewriting(i1, i2,
				mSetExtension(id_y, i1),
				mSetExtension(id_y, i2));
		checkRewriting(i1, i2,
				mSetExtension(i1, id_y, i1),
				mSetExtension(i2, id_y, i2));

		checkRewriting(s1, s2,
				mSimplePredicate(s1),
				mSimplePredicate(s2));

		checkRewriting(i1, i2,
				mUnaryExpression(UNMINUS, i1),
				mUnaryExpression(UNMINUS, i2));	
	}

	/**
	 * Ensures that a predicate can be rewritten in all contexts.
	 */
	public void testPredicateRewriting() throws Exception {
		Predicate p1 = equals;
		Predicate p2 = btrue;
		
		checkRewriting(p1, p2,
				mAssociativePredicate(LAND, p1, btrue),
				mAssociativePredicate(LAND, p2, btrue));
		checkRewriting(p1, p2,
				mAssociativePredicate(LAND, btrue, p1),
				mAssociativePredicate(LAND, btrue, p2));
		checkRewriting(p1, p2, 
				mAssociativePredicate(LAND, p1, btrue, p1),
				mAssociativePredicate(LAND, p2, btrue, p2));
		
		checkRewriting(p1, p2,
				mBinaryPredicate(LIMP, p1, btrue),
				mBinaryPredicate(LIMP, p2, btrue));
		checkRewriting(p1, p2,
				mBinaryPredicate(LIMP, btrue, p1),
				mBinaryPredicate(LIMP, btrue, p2));
		checkRewriting(p1, p2,
				mBinaryPredicate(LIMP, p1, p1),
				mBinaryPredicate(LIMP, p2, p2));
		
		checkRewriting(p1, p2,
				mBoolExpression(btrue),
				mBoolExpression(btrue));
		checkRewriting(p1, p2,
				mBoolExpression(p1),
				mBoolExpression(p2));
		
		checkRewriting(p1, p2,
				mLiteralPredicate(),
				mLiteralPredicate());

		checkRewriting(p1, p2,
				mQuantifiedExpression(CSET, Implicit, mList(bd_x), p1, id_x),
				mQuantifiedExpression(CSET, Implicit, mList(bd_x), p2, id_x));
		checkRewriting(p1, p2,
				mQuantifiedExpression(CSET, Implicit, mList(bd_x, bd_y), p1, id_x),
				mQuantifiedExpression(CSET, Implicit, mList(bd_x, bd_y), p2, id_x));
		checkRewriting(p1, p2,
				mQuantifiedExpression(CSET, Explicit, mList(bd_x), p1, id_x),
				mQuantifiedExpression(CSET, Explicit, mList(bd_x), p2, id_x));
		checkRewriting(p1, p2,
				mQuantifiedExpression(CSET, Explicit, mList(bd_x, bd_y), p1, id_x),
				mQuantifiedExpression(CSET, Explicit, mList(bd_x, bd_y), p2, id_x));
		checkRewriting(p1, p2,
				mQuantifiedExpression(CSET, Lambda, mList(bd_x), p1, m0x),
				mQuantifiedExpression(CSET, Lambda, mList(bd_x), p2, m0x));
		checkRewriting(p1, p2,
				mQuantifiedExpression(CSET, Lambda, mList(bd_x, bd_y), p1, m01x),
				mQuantifiedExpression(CSET, Lambda, mList(bd_x, bd_y), p2, m01x));
		
		checkRewriting(p1, p2,
				mQuantifiedPredicate(FORALL, mList(bd_x), p1),
				mQuantifiedPredicate(FORALL, mList(bd_x), p2));
		checkRewriting(p1, p2,
				mQuantifiedPredicate(FORALL, mList(bd_x, bd_y), p1),
				mQuantifiedPredicate(FORALL, mList(bd_x, bd_y), p2));
		
		checkRewriting(p1, p2,
				mUnaryPredicate(NOT, p1),
				mUnaryPredicate(NOT, p2));
	}

	/**
	 * Ensures that rewriting is implemented for all kinds of formulas.
	 */
	public void testRewritingAllClasses() throws Exception {
		checkRootRewriting(
				mAssociativeExpression(PLUS, id_x, id_x),
				mAssociativeExpression(PLUS, id_x, id_y)
		);
		checkRootRewriting(
				mAssociativePredicate(LAND, btrue, equals),
				mAssociativePredicate(LAND, btrue, btrue)
		);
		checkRootRewriting(
				mBinaryExpression(MINUS, id_x, id_x),
				mBinaryExpression(MINUS, id_x, id_y)
		);
		checkRootRewriting(
				mBinaryPredicate(LIMP, btrue, equals),
				mBinaryPredicate(LIMP, btrue, btrue)
		);
		checkRootRewriting(
				mBoolExpression(equals),
				mBoolExpression(btrue)
		);
		checkRootRewriting(
				mBoundIdentifier(0, INT),
				mBoundIdentifier(1, INT)
		);
		checkRootRewriting(
				mFreeIdentifier("x", INT),
				mFreeIdentifier("y", INT)
		);
		checkRootRewriting(
				mIntegerLiteral(0),
				mIntegerLiteral(1)
		);
		checkRootRewriting(
				mLiteralPredicate(BTRUE),
				mLiteralPredicate(BFALSE)
		);
		checkRootRewriting(
				mQuantifiedExpression(CSET, Implicit, mList(bd_x), btrue, id_x),
				mQuantifiedExpression(CSET, Implicit, mList(bd_x), btrue, id_y)
		);
		checkRootRewriting(
				mQuantifiedPredicate(FORALL, mList(bd_x), equals),
				mQuantifiedPredicate(FORALL, mList(bd_x), btrue)
		);
		checkRootRewriting(
				mRelationalPredicate(EQUAL, id_x, id_x),
				mRelationalPredicate(EQUAL, id_x, id_y)
		);
		checkRootRewriting(
				mSetExtension(id_x),
				mSetExtension(id_y)
		);
		checkRootRewriting(
				mSimplePredicate(id_S),
				mSimplePredicate(id_T)
		);
		checkRootRewriting(
				mUnaryExpression(UNMINUS, id_x),
				mUnaryExpression(UNMINUS, id_y)
		);
		checkRootRewriting(
				mUnaryPredicate(NOT, equals),
				mUnaryPredicate(NOT, btrue)
		);
	}
}

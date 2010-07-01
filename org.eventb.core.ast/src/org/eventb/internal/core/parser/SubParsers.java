/*******************************************************************************
 * Copyright (c) 2010 Systerel and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Systerel - initial API and implementation
 *******************************************************************************/
package org.eventb.internal.core.parser;

import static java.util.Arrays.asList;
import static org.eventb.core.ast.Formula.BOUND_IDENT;
import static org.eventb.core.ast.Formula.BOUND_IDENT_DECL;
import static org.eventb.core.ast.Formula.CONVERSE;
import static org.eventb.core.ast.Formula.CSET;
import static org.eventb.core.ast.Formula.EMPTYSET;
import static org.eventb.core.ast.Formula.FREE_IDENT;
import static org.eventb.core.ast.Formula.INTLIT;
import static org.eventb.core.ast.Formula.KBOOL;
import static org.eventb.core.ast.Formula.KFINITE;
import static org.eventb.core.ast.Formula.KID_GEN;
import static org.eventb.core.ast.Formula.KPARTITION;
import static org.eventb.core.ast.Formula.KPRJ1_GEN;
import static org.eventb.core.ast.Formula.KPRJ2_GEN;
import static org.eventb.core.ast.Formula.MAPSTO;
import static org.eventb.core.ast.Formula.NOT;
import static org.eventb.core.ast.Formula.NO_TAG;
import static org.eventb.core.ast.Formula.PREDICATE_VARIABLE;
import static org.eventb.core.ast.Formula.SETEXT;
import static org.eventb.core.ast.Formula.UNMINUS;
import static org.eventb.internal.core.parser.AbstractGrammar._RPAR;
import static org.eventb.internal.core.parser.BMath.B_MATH;
import static org.eventb.internal.core.parser.BMath._DOT;
import static org.eventb.internal.core.parser.BMath._MID;
import static org.eventb.internal.core.parser.BMath._RBRACE;
import static org.eventb.internal.core.parser.BMath._TYPING;
import static org.eventb.internal.core.parser.MainParsers.BOUND_IDENT_DECL_LIST_PARSER;
import static org.eventb.internal.core.parser.MainParsers.EXPR_LIST_PARSER;
import static org.eventb.internal.core.parser.MainParsers.EXPR_PARSER;
import static org.eventb.internal.core.parser.MainParsers.PRED_PARSER;
import static org.eventb.internal.core.parser.MainParsers.TYPE_PARSER;
import static org.eventb.internal.core.parser.MainParsers.asExpression;
import static org.eventb.internal.core.parser.MainParsers.asPredicate;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.eventb.core.ast.ASTProblem;
import org.eventb.core.ast.AssociativeExpression;
import org.eventb.core.ast.AssociativePredicate;
import org.eventb.core.ast.AtomicExpression;
import org.eventb.core.ast.BinaryExpression;
import org.eventb.core.ast.BinaryPredicate;
import org.eventb.core.ast.BoolExpression;
import org.eventb.core.ast.BoundIdentDecl;
import org.eventb.core.ast.BoundIdentifier;
import org.eventb.core.ast.Expression;
import org.eventb.core.ast.ExtendedExpression;
import org.eventb.core.ast.Formula;
import org.eventb.core.ast.FormulaFactory;
import org.eventb.core.ast.FreeIdentifier;
import org.eventb.core.ast.Identifier;
import org.eventb.core.ast.IntegerLiteral;
import org.eventb.core.ast.LiteralPredicate;
import org.eventb.core.ast.MultiplePredicate;
import org.eventb.core.ast.PowerSetType;
import org.eventb.core.ast.Predicate;
import org.eventb.core.ast.PredicateVariable;
import org.eventb.core.ast.ProblemKind;
import org.eventb.core.ast.ProblemSeverities;
import org.eventb.core.ast.ProductType;
import org.eventb.core.ast.QuantifiedExpression;
import org.eventb.core.ast.QuantifiedPredicate;
import org.eventb.core.ast.RelationalPredicate;
import org.eventb.core.ast.SetExtension;
import org.eventb.core.ast.SimplePredicate;
import org.eventb.core.ast.SourceLocation;
import org.eventb.core.ast.Type;
import org.eventb.core.ast.UnaryExpression;
import org.eventb.core.ast.UnaryPredicate;
import org.eventb.core.ast.QuantifiedExpression.Form;
import org.eventb.core.ast.extension.IExpressionExtension;
import org.eventb.internal.core.ast.extension.IToStringMediator;
import org.eventb.internal.core.parser.GenParser.ParserContext;
import org.eventb.internal.core.parser.GenParser.SyntaxError;
import org.eventb.internal.core.parser.MainParsers.PatternParser;

/**
 * Sub-parsers are specialized parsers; they are usually bound to an operator.
 * 
 * @author Nicolas Beauger
 * 
 */
public class SubParsers {

	private static final Predicate[] NO_PRED = new Predicate[0];
	static final BoundIdentDecl[] NO_DECL = new BoundIdentDecl[0];

	static abstract class AbstractSubParser<T> {

		protected final int[] tags;

		protected AbstractSubParser(int tag, int... others) {
			this.tags = new int[1 + others.length];
			this.tags[0] = tag;
			for (int i = 1; i < tags.length; i++) {
				tags[i] = others[i - 1];
			}
		}

		public int[] getTags() {
			return tags;
		}
	}

	static abstract class AbstractNudParser<T> extends AbstractSubParser<T> implements INudParser<T> {

		protected AbstractNudParser(int tag, int... others) {
			super(tag, others);
		}

	}

	private static abstract class AbstractLedParser<T> extends AbstractSubParser<T> implements ILedParser<T> {

		protected AbstractLedParser(int tag, int... others) {
			super(tag, others);
		}

	}

	
	private static abstract class PrefixNudParser<T> extends AbstractNudParser<T> {

		protected PrefixNudParser(int tag) {
			super(tag);
		}
		
		public final T nud(ParserContext pc) throws SyntaxError {
			pc.progress();
			return parseRight(pc);
		}
		
		/**
		 * Current token is the one that immediately follows the one on which
		 * nud() applies.
		 * 
		 * @param pc
		 *            the parser context
		 * @return the value to be returned by nud()
		 * @throws SyntaxError
		 */
		protected abstract T parseRight(ParserContext pc) throws SyntaxError;
		
		public void toString(IToStringMediator mediator, T toPrint) {
			mediator.appendOperator();
		}
	}

	private static abstract class ParenNudParser<T, U> extends PrefixNudParser<T> {

		private final INudParser<U> childParser;
		
		protected ParenNudParser(int tag, INudParser<U> childParser) {
			super(tag);
			this.childParser = childParser;
		}

		@Override
		protected final T parseRight(ParserContext pc) throws SyntaxError {
			// FIXME parsing this way prevents priority and compatibility checks
			// with operators that follow the closing parenthesis
			pc.progressOpenParen();
			final U child = pc.subParse(childParser);
			pc.progressCloseParen();
			return makeValue(pc.factory, child, pc.getSourceLocation());
		}
		
		protected abstract U getChild(T t);
		
		@Override
		public void toString(IToStringMediator mediator, T toPrint) {
			super.toString(mediator, toPrint);
			mediator.append("(");
			// FIXME should forbid direct calls to toString,
			// replace with subPrint to have the correct tag
			childParser.toString(mediator, getChild(toPrint));
			mediator.append(")");
		}
		
		protected abstract T makeValue(FormulaFactory factory, U child, SourceLocation loc) throws SyntaxError;
	}
	
	private static abstract class ValuedNudParser<T> extends AbstractNudParser<T> {

		protected ValuedNudParser(int tag, int... others) {
			super(tag, others);
		}
		
		public final T nud(ParserContext pc) throws SyntaxError {
			final String tokenVal = pc.t.val;
			pc.progress(getKind());
			final SourceLocation loc = pc.getSourceLocation();
			return makeValue(pc, tokenVal, loc);
		}

		protected abstract int getKind();
		
		/**
		 * Makes the value to be returned by nud().
		 * <p>
		 * Current token is the one that immediately follows the given token
		 * value.
		 * </p>
		 * 
		 * @param pc
		 *            a parser context
		 * @param tokenVal
		 *            the value of the token on which nud() applies
		 * @param loc
		 *            the location of the token on which nud() applies
		 * @return the value to be returned by nud().
		 * @throws SyntaxError 
		 */
		protected abstract T makeValue(ParserContext pc, String tokenVal, SourceLocation loc) throws SyntaxError;

	}

	// TODO use the possibility to have Left different from Right to make
	// assignment parser extend this class
	private static abstract class BinaryLedParser<T, Child extends Formula<Child>> extends AbstractSubParser<T> implements ILedParser<T> {

		private final INudParser<Child> childParser;
		
		protected BinaryLedParser(int tag, INudParser<Child> rightParser) {
			super(tag);
			this.childParser = rightParser;
		}
		
		protected Child parseRight(ParserContext pc) throws SyntaxError {
			return pc.subParse(childParser);
		}

		/**
		 * Returns the left child (or first child) of the given formula node.
		 * 
		 * @param t
		 *            a formula node
		 * @return a left node
		 */
		protected abstract Child getLeft(T t);

		/**
		 * Return the right child of the given node, or <code>null</code> if
		 * none.
		 * 
		 * @param t
		 *            a formula node
		 * @return a right node or <code>null</code>
		 */
		protected abstract Child getRight(T t);
		
		public final T led(Formula<?> left, ParserContext pc) throws SyntaxError {
			pc.progress();
			final Child typedLeft = asLeftType(left);
			final Child right = parseRight(pc);
			return makeValue(pc.factory, typedLeft, right, pc.getSourceLocation());
		}

		public void toString(IToStringMediator mediator, T toPrint) {
			final Child left = getLeft(toPrint);
			mediator.subPrint(left, false);
			mediator.appendOperator();
			final Child right = getRight(toPrint);
			if (right != null) {
				mediator.subPrint(right, false);
			}
		}

		protected abstract Child asLeftType(Formula<?> left) throws SyntaxError;
		
		protected abstract T makeValue(FormulaFactory factory, Child left,
				Child right, SourceLocation loc) throws SyntaxError;
	}
	
	private static abstract class DefaultLedExprParser<T> extends BinaryLedParser<T, Expression> {

		protected DefaultLedExprParser(int tag) {
			super(tag, EXPR_PARSER);
		}
		
		@Override
		protected final Expression asLeftType(Formula<?> left) throws SyntaxError {
			return asExpression(left);
		}
		
	}
	
	private static abstract class DefaultLedPredParser<T> extends BinaryLedParser<T, Predicate> {

		protected DefaultLedPredParser(int tag) {
			super(tag, PRED_PARSER);
		}
		
		@Override
		protected Predicate asLeftType(Formula<?> left) throws SyntaxError {
			return asPredicate(left);
		}

	}
	
	private static abstract class AssociativeLedParser<T, Child extends Formula<?>> extends AbstractSubParser<T> implements ILedParser<T> {

		private final INudParser<Child> childParser;
		
		protected AssociativeLedParser(int tag, INudParser<Child> childParser) {
			super(tag);
			this.childParser = childParser;
		}

		public T led(Formula<?> left, ParserContext pc) throws SyntaxError {
			final Child typedLeft = asChildType(left);
			
			final List<Child> children = new ArrayList<Child>();
			children.add(typedLeft);
			
			final int kind = pc.t.kind;
			do {
				pc.progress();
				final Child next = pc.subParse(childParser);
				children.add(next);
			} while (pc.t.kind == kind);
			
			return makeResult(pc.factory, children, pc.getSourceLocation());
		}
		
		protected abstract Child[] getChildren(T t);
		
		public void toString(IToStringMediator mediator, T toPrint) {
			final Child[] children = getChildren(toPrint);
			// TODO remove all calls to parser.toString, use subPrint instead
			mediator.subPrint(children[0], false, NO_DECL, childParser);
			for (int i = 1; i < children.length; i++) {
				mediator.appendOperator();
				mediator.subPrint(children[i], true, NO_DECL, childParser);
			}
		}
		
		protected abstract Child asChildType(Formula<?> left) throws SyntaxError;
		
		protected abstract T makeResult(FormulaFactory factory,
				List<Child> children, SourceLocation loc) throws SyntaxError;

	}

	// TODO move ident parsers to MainParsers as they are imported there
	// Takes care of the bindings.
	static final INudParser<Identifier> IDENT_SUBPARSER = new ValuedNudParser<Identifier>(FREE_IDENT, BOUND_IDENT) {

		@Override
		protected Identifier makeValue(ParserContext pc, String tokenVal,
				SourceLocation loc) {
			if (pc.isParsingType()) { // make a type expression
				final Type type = pc.factory.makePowerSetType(pc.factory.makeGivenType(tokenVal));
				return pc.factory.makeFreeIdentifier(tokenVal, loc, type);
			}
			final int index = pc.getBoundIndex(tokenVal);
			if (index == -1) { // free identifier
				return pc.factory.makeFreeIdentifier(tokenVal, loc);
			} else { // bound identifier
				return pc.factory.makeBoundIdentifier(index, loc);
			}
		}

		@Override
		protected int getKind() {
			return B_MATH.getIDENT();
		}

		public void toString(IToStringMediator mediator, Identifier toPrint) {
			switch(toPrint.getTag()) {
			case FREE_IDENT:
				FREE_IDENT_SUBPARSER.toString(mediator, (FreeIdentifier) toPrint);
				break;
			case BOUND_IDENT:
				final BoundIdentifier boundIdent = (BoundIdentifier) toPrint;
				mediator.appendBoundIdent(boundIdent.getBoundIndex());
			}
		}
		

	};
	
	static final INudParser<FreeIdentifier> FREE_IDENT_SUBPARSER = new AbstractNudParser<FreeIdentifier>(FREE_IDENT) {

		public FreeIdentifier nud(ParserContext pc) throws SyntaxError {
			final Identifier ident = IDENT_SUBPARSER.nud(pc);
			if (!(ident instanceof FreeIdentifier)) {
				throw new SyntaxError(new ASTProblem(ident.getSourceLocation(),
						ProblemKind.FreeIdentifierExpected,
						ProblemSeverities.Error));
			}
			return (FreeIdentifier) ident;
		}

		public void toString(IToStringMediator mediator, FreeIdentifier toPrint) {
			mediator.append(toPrint.getName());
		}

	};

	static final INudParser<BoundIdentDecl> BOUND_IDENT_DECL_SUBPARSER = new ValuedNudParser<BoundIdentDecl>(BOUND_IDENT_DECL) {

		@Override
		protected BoundIdentDecl makeValue(ParserContext pc, String tokenVal,
				SourceLocation loc) throws SyntaxError {
			Type type = null;
			if (pc.t.kind == _TYPING) {
				pc.pushParentKind();
				pc.progress();
				try {
					type = pc.subParse(TYPE_PARSER);
				} finally {
					pc.popParentKind();
				}
			}
			return pc.factory.makeBoundIdentDecl(tokenVal, pc.getSourceLocation(), type);
		}

		@Override
		protected int getKind() {
			return B_MATH.getIDENT();
		}

		public void toString(IToStringMediator mediator, BoundIdentDecl toPrint) {
			mediator.append(toPrint.getName());
			if (mediator.isWithTypes()) {
				mediator.append("\u2982");
				TYPE_PARSER.toString(mediator, toPrint.getType());
			}
		}
	};

	static final INudParser<IntegerLiteral> INTLIT_SUBPARSER = new ValuedNudParser<IntegerLiteral>(INTLIT) {
	
		@Override
		protected IntegerLiteral makeValue(ParserContext pc, String tokenVal,
				SourceLocation loc) throws SyntaxError {
			try {
				final BigInteger value = BigInteger.valueOf((Integer
						.valueOf(tokenVal)));
				return pc.factory.makeIntegerLiteral(value, loc);
			} catch (NumberFormatException e) {
				// FIXME this is rather a problem with the lexer: it should
				// never have returned a _INTLIT token kind
				throw new SyntaxError(new ASTProblem(loc,
						ProblemKind.IntegerLiteralExpected,
						ProblemSeverities.Error));
			}
		}

		@Override
		protected int getKind() {
			return B_MATH.getINTLIT();
		}

		// Change the minus sign if any, so that it conforms to the mathematical
		// language: \u2212 (minus sign) instead of \u002d (hyphen-minus).
		public void toString(IToStringMediator mediator, IntegerLiteral toPrint) {
			final String image = toPrint.getValue().toString();
			if (image.charAt(0) == '-') {
				mediator.append("\u2212");
				mediator.append(image.substring(1));
			} else {
				mediator.append(image);
			}
		}
		
	};

	static final INudParser<Predicate> PRED_VAR_SUBPARSER = new ValuedNudParser<Predicate>(
			PREDICATE_VARIABLE) {

		@Override
		protected Predicate makeValue(ParserContext pc,
				String tokenVal, SourceLocation loc) throws SyntaxError {
			if (!pc.withPredVar) {
				pc.result.addProblem(new ASTProblem(loc,
						ProblemKind.PredicateVariableNotAllowed,
						ProblemSeverities.Error, tokenVal));
				return pc.factory.makeLiteralPredicate(Formula.BTRUE, loc);
			}
			return pc.factory.makePredicateVariable(tokenVal, loc);
		}

		@Override
		protected int getKind() {
			return B_MATH.getPREDVAR();
		}

		public void toString(IToStringMediator mediator, Predicate toPrint) {
			final String name = ((PredicateVariable) toPrint).getName();
			mediator.append(name);
		}
	};

	/**
	 * Parses expressions outside bound identifier declarations. Always returns
	 * an expression with the same tag as left.
	 */
	static final ILedParser<Expression> OFTYPE = new AbstractLedParser<Expression>(NO_TAG) {
		
		private static final String POW_ALPHA = "\u2119(alpha)";
		private static final String POW_ALPHA_ALPHA = "\u2119(alpha \u00d7 alpha)";
		private static final String POW_ALPHA_BETA_ALPHA = "\u2119(alpha \u00d7 beta \u00d7 alpha)";
		private static final String POW_ALPHA_BETA_BETA = "\u2119(alpha \u00d7 beta \u00d7 beta)";
		
		public Expression led(Formula<?> left, ParserContext pc) throws SyntaxError {
			final int tag = left.getTag();
			if (!pc.isParenthesized()) {
				throw newMissingParenError(pc);
			}
			if (!isTypedGeneric(tag)) {
				throw newUnexpectedOftype(pc);
			}
			pc.progress();
			
			Type type = pc.subParse(TYPE_PARSER);
			final SourceLocation typeLoc = pc.getSourceLocation();
			if (pc.t.kind != _RPAR) {
				throw newMissingParenError(pc);
			}
			if (!checkValidTypedGeneric(tag, type, typeLoc, pc.result)) {
				type = null;
			}
			return pc.factory.makeAtomicExpression(tag, pc
					.getEnclosingSourceLocation(), type);
		}

		private boolean isTypedGeneric(int tag) {
			switch (tag) {
			case Formula.EMPTYSET:
			case Formula.KID_GEN:
			case Formula.KPRJ1_GEN:
			case Formula.KPRJ2_GEN:
				return true;
			default:
				return false;
			}
		}
		
		private SyntaxError newUnexpectedOftype(ParserContext pc) {
			return new SyntaxError(new ASTProblem(pc.makeSourceLocation(pc.t),
					ProblemKind.UnexpectedOftype, ProblemSeverities.Error));
		}
		
		private SyntaxError newMissingParenError(ParserContext pc) {
			return new SyntaxError(new ASTProblem(pc.makeSourceLocation(pc.t),
					ProblemKind.OftypeMissingParentheses,
					ProblemSeverities.Error));
		}

		
		private boolean checkValidTypedGeneric(int tag, Type type,
				SourceLocation typeLoc, ParseResult result) throws SyntaxError {
			switch (tag) {
			case Formula.EMPTYSET:
				if (!(type instanceof PowerSetType)) {
					result.addProblem(newInvalidGenType(typeLoc, POW_ALPHA));
					return false;
				}
				break;
			case Formula.KID_GEN:
				final Type source = type.getSource();
				if (!(source != null && source.equals(type.getTarget()))) {
					result.addProblem(newInvalidGenType(typeLoc, POW_ALPHA_ALPHA));
					return false;
				}
				break;
			case Formula.KPRJ1_GEN:
				if (!isValidPrjType(type, true)) {
					result.addProblem(newInvalidGenType(typeLoc, POW_ALPHA_BETA_ALPHA));
					return false;
				}
				break;
			case Formula.KPRJ2_GEN:
				if (!isValidPrjType(type, false)) {
					result.addProblem(newInvalidGenType(typeLoc, POW_ALPHA_BETA_BETA));
					return false;
				}
				break;
			default:
				// tag has already been checked
				assert false;
			}
			return true;
		}

		private ASTProblem newInvalidGenType(SourceLocation loc, String expected) {
			return new ASTProblem(loc,
					ProblemKind.InvalidGenericType,
					ProblemSeverities.Error,
					expected);
		}
		
		private boolean isValidPrjType(Type type, boolean left) {
			final Type source = type.getSource();
			final Type target = type.getTarget();
			if (!(source instanceof ProductType)) {
				return false;
			}

			final ProductType prodSource = (ProductType) source;
			final Type child;
			if (left) {
				child = prodSource.getLeft();
			} else {
				child = prodSource.getRight();
			}
			return target.equals(child);
		}

		public void toString(IToStringMediator mediator, Expression toPrint) {
			// FIXME parentheses might not always be needed
			mediator.append("(");
			mediator.forward(toPrint, false);
			mediator.append("\u2982");
			TYPE_PARSER.toString(mediator, toPrint.getType());
			mediator.append(")");
		}

	};
	
	static class AtomicExpressionParser extends PrefixNudParser<AtomicExpression> {
	
		protected AtomicExpressionParser(int tag) {
			super(tag);
		}
	
		@Override
		protected AtomicExpression parseRight(ParserContext pc)
				throws SyntaxError {
			return pc.factory.makeAtomicExpression(tags[0], pc.getSourceLocation());
		}

		@Override
		public void toString(IToStringMediator mediator,
				AtomicExpression toPrint) {
			if (mustPrintTypes(mediator, toPrint)) {
				OFTYPE.toString(mediator, toPrint);
			} else {
				super.toString(mediator, toPrint);
			}
			
		}

	}

	static boolean mustPrintTypes(IToStringMediator mediator, Expression toPrint) {
		switch (toPrint.getTag()) {
		case EMPTYSET:
		case KID_GEN:
		case KPRJ1_GEN:
		case KPRJ2_GEN:
		case BOUND_IDENT_DECL:
			return mediator.isWithTypes();
		default:
			return false;
		}
	}

	static class ExtendedAtomicExpressionParser extends PrefixNudParser<ExtendedExpression> {
		
		protected ExtendedAtomicExpressionParser(int tag) {
			super(tag);
		}
	
		@Override
		protected ExtendedExpression parseRight(ParserContext pc)
				throws SyntaxError {
			return checkAndMakeExtendedExpr(pc.factory, tags[0], Collections
					.<Expression> emptyList(), pc.getSourceLocation());
		}

	}

	static class BinaryExpressionInfix extends DefaultLedExprParser<BinaryExpression> {

		public BinaryExpressionInfix(int tag) {
			super(tag);
		}
		
		@Override
		protected BinaryExpression makeValue(FormulaFactory factory, Expression left,
				Expression right, SourceLocation loc) throws SyntaxError {
			return factory.makeBinaryExpression(tags[0], left, right, loc);
		}
		
		@Override
		protected Expression getLeft(BinaryExpression t) {
			return t.getLeft();
		}

		@Override
		protected Expression getRight(BinaryExpression t) {
			return t.getRight();
		}

	}
	
	static class ExtendedBinaryExpressionInfix extends DefaultLedExprParser<ExtendedExpression> {

		public ExtendedBinaryExpressionInfix(int tag) {
			super(tag);
		}

		@Override
		protected ExtendedExpression makeValue(FormulaFactory factory,
				Expression left, Expression right, SourceLocation loc) throws SyntaxError {
			return checkAndMakeExtendedExpr(factory, tags[0], asList(left,
					right), loc);
		}
		
		@Override
		protected Expression getLeft(ExtendedExpression t) {
			return t.getChildExpressions()[0];
		}

		@Override
		protected Expression getRight(ExtendedExpression t) {
			return t.getChildExpressions()[1];
		}

	}

	static class AssociativeExpressionInfix extends AssociativeLedParser<AssociativeExpression, Expression> {


		protected AssociativeExpressionInfix(int tag) {
			super(tag, EXPR_PARSER);
		}

		@Override
		protected AssociativeExpression makeResult(FormulaFactory factory,
				List<Expression> children, SourceLocation loc) throws SyntaxError {
			return factory.makeAssociativeExpression(tags[0], children, loc);
		}

		@Override
		protected Expression asChildType(Formula<?> left) throws SyntaxError {
			return asExpression(left);
		}

		@Override
		protected Expression[] getChildren(AssociativeExpression t) {
			return t.getChildren();
		}
		
	}

	static class ExtendedAssociativeExpressionInfix extends AssociativeLedParser<ExtendedExpression, Expression> {

		public ExtendedAssociativeExpressionInfix(int tag) {
			super(tag, EXPR_PARSER);
		}
		
		@Override
		protected ExtendedExpression makeResult(FormulaFactory factory,
				List<Expression> children, SourceLocation loc) throws SyntaxError {
			return checkAndMakeExtendedExpr(factory, tags[0], children, loc);
		}

		@Override
		protected Expression asChildType(Formula<?> left) throws SyntaxError {
			return asExpression(left);
		}

		@Override
		protected Expression[] getChildren(ExtendedExpression t) {
			return t.getChildExpressions();
		}
	}
	
	static class AssociativePredicateInfix extends AssociativeLedParser<AssociativePredicate, Predicate> {

		public AssociativePredicateInfix(int tag) {
			super(tag, PRED_PARSER);
		}

		@Override
		protected Predicate asChildType(Formula<?> left) throws SyntaxError {
			return asPredicate(left);
		}

		@Override
		protected AssociativePredicate makeResult(FormulaFactory factory,
				List<Predicate> children, SourceLocation loc)
				throws SyntaxError {
			return factory.makeAssociativePredicate(tags[0], children, loc);
		}

		@Override
		protected Predicate[] getChildren(AssociativePredicate t) {
			return t.getChildren();
		}
	}

	static class RelationalPredicateInfix extends DefaultLedExprParser<RelationalPredicate> {

		public RelationalPredicateInfix(int tag) {
			super(tag);
		}

		@Override
		protected RelationalPredicate makeValue(FormulaFactory factory,
				Expression left, Expression right, SourceLocation loc) throws SyntaxError {
			return factory.makeRelationalPredicate(tags[0], left, right, loc);
		}
		
		@Override
		protected Expression getLeft(RelationalPredicate t) {
			return t.getLeft();
		}

		@Override
		protected Expression getRight(RelationalPredicate t) {
			return t.getRight();
		}
	}

	static class LedImage extends DefaultLedExprParser<BinaryExpression> {

		private final int closeKind;
		
		protected LedImage(int tag, int closeKind) {
			super(tag);
			this.closeKind = closeKind;
		}

		@Override
		protected Expression parseRight(ParserContext pc) throws SyntaxError {
			// FIXME parsing this way prevents priority and compatibility checks
			// with operators that follow the closing parenthesis
			final Expression right = super.parseRight(pc);
			pc.progress(closeKind);
			return right;
		}
		
		@Override
		protected BinaryExpression makeValue(FormulaFactory factory, Expression left,
				Expression right, SourceLocation loc) throws SyntaxError {
			return factory.makeBinaryExpression(tags[0], left, right, loc);
		}

		@Override
		protected Expression getRight(BinaryExpression t) {
			return t.getRight();
		}

		@Override
		protected Expression getLeft(BinaryExpression t) {
			return t.getLeft();
		}
		
	}

	static class LiteralPredicateParser extends PrefixNudParser<LiteralPredicate> {

		public LiteralPredicateParser(int tag) {
			super(tag);
		}

		@Override
		protected LiteralPredicate parseRight(ParserContext pc)
				throws SyntaxError {
			return pc.factory.makeLiteralPredicate(tags[0], pc.getSourceLocation());
		}

	}

	static final INudParser<UnaryPredicate> NOT_PARSER = new PrefixNudParser<UnaryPredicate>(NOT) {

		@Override
		protected UnaryPredicate parseRight(ParserContext pc)
				throws SyntaxError {
			final Predicate pred = pc.subParse(PRED_PARSER);
			return pc.factory.makeUnaryPredicate(tags[0], pred, pc.getSourceLocation());
		}

		@Override
		public void toString(IToStringMediator mediator, UnaryPredicate toPrint) {
			super.toString(mediator, toPrint);
			final Predicate child = toPrint.getChild();
			mediator.subPrint(child, true);
		}
	};

	static class BinaryPredicateParser extends DefaultLedPredParser<BinaryPredicate> {

		public BinaryPredicateParser(int tag) {
			super(tag);
		}

		@Override
		protected BinaryPredicate makeValue(FormulaFactory factory, Predicate left,
				Predicate right, SourceLocation loc) throws SyntaxError {
			return factory.makeBinaryPredicate(tags[0], left, right, loc);
		}
		
		@Override
		protected Predicate getLeft(BinaryPredicate t) {
			return t.getLeft();
		}

		@Override
		protected Predicate getRight(BinaryPredicate t) {
			return t.getRight();
		}
	}

	static class QuantifiedPredicateParser extends PrefixNudParser<QuantifiedPredicate> {

		public QuantifiedPredicateParser(int tag) {
			super(tag);
		}

		@Override
		public QuantifiedPredicate parseRight(ParserContext pc) throws SyntaxError {
			final List<BoundIdentDecl> boundIdentifiers = pc.subParseNoBinding(BOUND_IDENT_DECL_LIST_PARSER);
			pc.progress(_DOT);
			final Predicate pred = pc.subParse(PRED_PARSER, boundIdentifiers);

			return pc.factory.makeQuantifiedPredicate(tags[0], boundIdentifiers,
					pred, pc.getSourceLocation());
		}

		@Override
		public void toString(IToStringMediator mediator,
				QuantifiedPredicate toPrint) {
			super.toString(mediator, toPrint);
			final BoundIdentDecl[] boundDecls = toPrint.getBoundIdentDecls();
			BOUND_IDENT_DECL_LIST_PARSER.toString(mediator, asList(boundDecls));
			mediator.appendImage(_DOT);
			mediator.subPrint(toPrint.getPredicate(), false, boundDecls);
		}
	}

	static class UnaryExpressionParser extends ParenNudParser<UnaryExpression, Expression> {

		public UnaryExpressionParser(int tag) {
			super(tag, EXPR_PARSER);
		}

		@Override
		protected UnaryExpression makeValue(FormulaFactory factory, Expression child,
				SourceLocation loc) {
			return factory.makeUnaryExpression(tags[0], child, loc);
		}

		@Override
		protected Expression getChild(UnaryExpression t) {
			return t.getChild();
		}

	}

	static class GenExpressionParser extends AbstractNudParser<Expression> {
		
		private final INudParser<UnaryExpression> parserV1;
		private final INudParser<AtomicExpression> parserV2;
		
		public GenExpressionParser(int unaryTagV1, int atomicTagV2) {
			super(unaryTagV1, atomicTagV2);
			this.parserV1 = new UnaryExpressionParser(unaryTagV1);
			this.parserV2 = new AtomicExpressionParser(atomicTagV2);
		}
		
		public Expression nud(ParserContext pc) throws SyntaxError {
			switch (pc.version) {
			case V1:
				return parserV1.nud(pc);
			case V2:
				return parserV2.nud(pc);
			default:
				throw new IllegalArgumentException(
						"Unsupported language version: " + pc.version);
			}
		}

		public void toString(IToStringMediator mediator, Expression toPrint) {
			if (mustPrintTypes(mediator, toPrint)) {
				OFTYPE.toString(mediator, toPrint);
				return;
			}
			if (toPrint instanceof UnaryExpression) {
				parserV1.toString(mediator, (UnaryExpression) toPrint);
			} else if (toPrint instanceof AtomicExpression) {
				parserV2.toString(mediator, (AtomicExpression) toPrint);
			} else {
				// should never happen
				assert false;
			}
		}

	}
	
	static final ILedParser<UnaryExpression> CONVERSE_PARSER = new DefaultLedExprParser<UnaryExpression>(CONVERSE) {

		@Override
		protected UnaryExpression makeValue(FormulaFactory factory, Expression left,
				Expression right, SourceLocation loc) throws SyntaxError {
			return factory.makeUnaryExpression(tags[0], left, loc);
		}
		
		@Override
		protected Expression parseRight(ParserContext pc) throws SyntaxError {
			// nothing to parse at right
			return null;
		}
		
		@Override
		protected Expression getLeft(UnaryExpression t) {
			return t.getChild();
		}

		@Override
		protected Expression getRight(UnaryExpression t) {
			return null;
		}
	};
	
	static final INudParser<BoolExpression> KBOOL_PARSER = new ParenNudParser<BoolExpression, Predicate>(KBOOL, PRED_PARSER) {

		@Override
		protected BoolExpression makeValue(FormulaFactory factory, Predicate child,
				SourceLocation loc) {
			return factory.makeBoolExpression(child, loc);
		}

		@Override
		protected Predicate getChild(BoolExpression t) {
			return t.getPredicate();
		}

	};

	static final INudParser<SetExtension> SETEXT_PARSER = new PrefixNudParser<SetExtension>(SETEXT) {
		
		@Override
		public SetExtension parseRight(ParserContext pc) throws SyntaxError {
			final List<Expression> exprs;
			if (pc.t.kind == _RBRACE) { // only place where a list may be empty
				exprs = Collections.emptyList();
			} else {
				exprs = pc.subParse(EXPR_LIST_PARSER);
			}
			pc.progress(_RBRACE);
			return pc.factory.makeSetExtension(exprs, pc.getSourceLocation());
		}

		@Override
		public void toString(IToStringMediator mediator, SetExtension toPrint) {
			super.toString(mediator, toPrint);
			final Expression[] members = toPrint.getMembers();
			if (members.length > 0) {
				EXPR_LIST_PARSER.toString(mediator, asList(members));
			}
			mediator.appendImage(_RBRACE);
		}
	};
	
	static class ExplicitQuantExpr extends PrefixNudParser<QuantifiedExpression> {
		
		protected ExplicitQuantExpr(int tag) {
			super(tag);
		}

		@Override
		protected QuantifiedExpression parseRight(ParserContext pc) throws SyntaxError {
			final List<BoundIdentDecl> boundIdents = pc.subParseNoBinding(BOUND_IDENT_DECL_LIST_PARSER);
			pc.progress(_DOT);
			final Predicate pred = pc.subParseNoParent(PRED_PARSER, boundIdents);
			pc.progress(_MID);
			final Expression expr = pc.subParse(EXPR_PARSER, boundIdents);
			progressClose(pc);

			return pc.factory.makeQuantifiedExpression(tags[0], boundIdents, pred,
					expr, pc.getSourceLocation(), Form.Explicit);
		}
	
		protected void progressClose(ParserContext pc) throws SyntaxError {
			// do nothing by default
		}

		@Override
		public void toString(IToStringMediator mediator,
				QuantifiedExpression toPrint) {
			super.toString(mediator, toPrint);
			final BoundIdentDecl[] boundDecls = toPrint.getBoundIdentDecls();
			BOUND_IDENT_DECL_LIST_PARSER.toString(mediator, asList(boundDecls));
			mediator.appendImage(_DOT);
			mediator.subPrint(toPrint.getPredicate(), false, boundDecls);
			mediator.appendImage(_MID);
			mediator.subPrint(toPrint.getExpression(), false, boundDecls);
		}
	}
	
	static final ExplicitQuantExpr CSET_EXPLICIT = new ExplicitQuantExpr(CSET) {
		
		@Override
		protected void progressClose(ParserContext pc) throws SyntaxError {
			pc.progress(_RBRACE);
		}
		
		@Override
		public void toString(IToStringMediator mediator, QuantifiedExpression toPrint) {
			super.toString(mediator, toPrint);
			mediator.appendImage(_RBRACE);
		}
	};
	
	static class ImplicitQuantExpr extends PrefixNudParser<QuantifiedExpression> {
		
		protected ImplicitQuantExpr(int tag) {
			super(tag);
		}

		@Override
		protected final QuantifiedExpression parseRight(ParserContext pc)
		throws SyntaxError {
			final Expression expr = pc.subParseNoBinding(EXPR_PARSER);
			pc.progress(_MID);
			final List<BoundIdentDecl> boundIdents = new ArrayList<BoundIdentDecl>();
			final Expression boundExpr = expr.bindAllFreeIdents(boundIdents, pc.factory);

			final Predicate pred = pc.subParseNoParent(PRED_PARSER, boundIdents);
			progressClose(pc);

			return pc.factory.makeQuantifiedExpression(tags[0], boundIdents, pred,
					boundExpr, pc.getSourceLocation(), Form.Implicit);
		}
		
		protected void progressClose(ParserContext pc) throws SyntaxError {
			// do nothing by default
		}

		@Override
		public void toString(IToStringMediator mediator,
				QuantifiedExpression toPrint) {
			super.toString(mediator, toPrint);
			final BoundIdentDecl[] boundDecls = toPrint.getBoundIdentDecls();
			BOUND_IDENT_DECL_LIST_PARSER.toString(mediator, asList(boundDecls));
			mediator.subPrint(toPrint.getExpression(), false, boundDecls);
			mediator.appendImage(_MID);
			mediator.subPrint(toPrint.getPredicate(), false, boundDecls);
		}
		
	}
	
	static final ImplicitQuantExpr CSET_IMPLICIT = new ImplicitQuantExpr(CSET) {

		@Override
		protected void progressClose(ParserContext pc) throws SyntaxError {
			pc.progress(_RBRACE);
		}
		
		@Override
		public void toString(IToStringMediator mediator, QuantifiedExpression toPrint) {
			super.toString(mediator, toPrint);
			mediator.appendImage(_RBRACE);
		}
	};
	
	static final INudParser<QuantifiedExpression> CSET_LAMBDA = new PrefixNudParser<QuantifiedExpression>(CSET) {
		
		@Override
		public QuantifiedExpression parseRight(ParserContext pc) throws SyntaxError {
			final PatternParser pattParser = new PatternParser(pc.result);
			final Pattern pattern = pc.subParseNoBinding(pattParser);
			pc.progress(_DOT);
			final List<BoundIdentDecl> boundDecls = pattern.getDecls();
			final Predicate pred = pc.subParseNoParent(PRED_PARSER, boundDecls);
			pc.progress(_MID);
			final Expression expr = pc.subParse(EXPR_PARSER, boundDecls);
			
			final Expression pair = pc.factory.makeBinaryExpression(MAPSTO,
					pattern.getPattern(), expr, null);
			return pc.factory.makeQuantifiedExpression(tags[0], boundDecls, pred,
					pair, pc.getSourceLocation(), Form.Lambda);
		}

		@Override
		public void toString(IToStringMediator mediator,
				QuantifiedExpression toPrint) {
			super.toString(mediator, toPrint);
			final Expression chile = toPrint.getExpression();
			assert chile.getTag() == MAPSTO;
			final BinaryExpression pair = (BinaryExpression)chile;
			final Expression pattern = pair.getLeft();
			final BoundIdentDecl[] boundDecls = toPrint.getBoundIdentDecls();
			mediator.subPrint(pattern, false, boundDecls);
			mediator.appendImage(_DOT);
			mediator.subPrint(toPrint.getPredicate(), false, boundDecls);
			mediator.appendImage(_MID);
			mediator.subPrint(pair.getRight(), false, boundDecls);
		}
	};

	// TODO extract from above code for printing quantified expressions
//	@Override
//	protected void toStringFullyParenthesized(StringBuilder builder, String[] existingBoundIdents) {
//		toStringHelper(builder, existingBoundIdents, true, false);
//	}
//	
//	/*
//	 * avoid having to write almost twice the same for methods 
//	 * toString and method toStringFully parenthesized
//	 */ 
//	private void toStringHelper(StringBuilder builder, String[] boundNames,
//			boolean parenthesized, boolean withTypes) {
//
//		// Collect names used in subformulas and not locally bound
//		HashSet<String> usedNames = new HashSet<String>();
//		expr.collectNamesAbove(usedNames, boundNames,
//				quantifiedIdentifiers.length);
//		boolean exprIsClosed = usedNames.size() == 0;
//		pred.collectNamesAbove(usedNames, boundNames,
//				quantifiedIdentifiers.length);
//
//		String[] localNames = resolveIdents(quantifiedIdentifiers, usedNames);
//		String[] newBoundNames = catenateBoundIdentLists(boundNames, localNames);
//
//		switch (form) {
//		case Lambda:
//			toStringLambda(builder, parenthesized, newBoundNames, withTypes);
//			break;
//		case Implicit:
//			if (exprIsClosed && ! withTypes) {
//				// Still OK to use implicit form.
//				toStringImplicit(builder, parenthesized, localNames,
//						newBoundNames, withTypes);
//			} else {
//				toStringExplicit(builder, parenthesized, localNames,
//						newBoundNames, withTypes);
//			}
//			break;
//		case Explicit:
//			toStringExplicit(builder, parenthesized, localNames, newBoundNames,
//					withTypes);
//			break;
//		default:
//			assert false;
//			break;
//		}
//	}
//
//	private void toStringLambda(StringBuilder builder, boolean parenthesized,
//			String[] boundNames, boolean withTypes) {
//
//		// Extract left and right subexpressions
//		assert expr.getTag() == MAPSTO;
//		final BinaryExpression binExpr = (BinaryExpression) this.expr;
//		final Expression leftExpr = binExpr.getLeft();
//		final Expression rightExpr = binExpr.getRight();
//
//		builder.append("\u03bb");
//		if (parenthesized) {
//			leftExpr.toStringFullyParenthesized(builder, boundNames);
//		} else if (withTypes) {
//			appendTypedPattern(builder, leftExpr, boundNames);
//		} else {
////			leftExpr.toString(builder, false, MAPSTO, boundNames, withTypes);
//		}
//		builder.append("\u00b7");
//		appendPredString(builder, parenthesized, boundNames, withTypes);
//		builder.append(" \u2223 ");
//		if (parenthesized) {
//			rightExpr.toStringFullyParenthesized(builder, boundNames);
//		} else {
////			rightExpr.toString(builder, true, MAPSTO, boundNames, withTypes);
//		}
//	}
//
//	private void appendTypedPattern(StringBuilder builder, Expression pattern,
//			String[] boundNames) {
//		
//		switch (pattern.getTag()) {
//		case MAPSTO:
//			final BinaryExpression maplet = (BinaryExpression) pattern;
//			final Expression left = maplet.getLeft();
//			final Expression right = maplet.getRight();
//			appendTypedPattern(builder, left, boundNames);
//			builder.append("\u21a6");
//			final boolean needsParen = right.getTag() == MAPSTO;
//			if (needsParen) builder.append("(");
//			appendTypedPattern(builder, right, boundNames);
//			if (needsParen) builder.append(")");
//			break;
//		case BOUND_IDENT:
//			final BoundIdentifier ident = (BoundIdentifier) pattern;
//			ident.toStringFullyParenthesized(builder, boundNames);
//			builder.append("\u2982");
//			final int length = quantifiedIdentifiers.length;
//			final int idx = length - ident.getBoundIndex() - 1;
//			builder.append(quantifiedIdentifiers[idx].getType());
//			break;
//		default:
//			assert false;
//			break;
//		}
//	}
//
//	private void toStringImplicit(StringBuilder builder, boolean parenthesized,
//			String[] localNames, String[] boundNames, boolean withTypes) {
//
//		if (getTag() == Formula.CSET) {
//			builder.append("{");
//		}
//		else {
//			builder.append(tags[getTag()-firstTag]);
//		}
//		appendExprString(builder, parenthesized, boundNames, withTypes);
//		builder.append(" \u2223 ");
//		appendPredString(builder, parenthesized, boundNames, withTypes);
//		if (getTag() == Formula.CSET) {
//			builder.append("}");
//		}
//	}
//
//	private void toStringExplicit(StringBuilder builder, boolean parenthesized,
//			String[] localNames, String[] boundNames, boolean withTypes) {
//		
//		if (getTag() == Formula.CSET) { 
//			builder.append("{");
//		}
//		else {
//			builder.append(tags[getTag()-firstTag]);
//		}
//		appendBoundIdentifiersString(builder, localNames,
//				quantifiedIdentifiers, withTypes);
//		builder.append("\u00b7");
//		appendPredString(builder, parenthesized, boundNames, withTypes);
//		builder.append(" \u2223 ");
//		appendExprString(builder, parenthesized, boundNames, withTypes);
//		if (getTag() == Formula.CSET) {
//			builder.append("}");
//		}
//	}
//
//	private void appendPredString(StringBuilder builder, boolean parenthesized,
//			String[] boundNames, boolean withTypes) {
//
//		if (parenthesized) {
//			builder.append('(');
//			pred.toStringFullyParenthesized(builder, boundNames);
//			builder.append(')');
//		} else {
////			pred.toString(builder, false, getTag(), boundNames, withTypes);
//		}
//	}
//
//	private void appendExprString(StringBuilder builder, boolean parenthesized,
//			String[] boundNames, boolean withTypes) {
//
//		if (parenthesized) {
//			builder.append('(');
//			expr.toStringFullyParenthesized(builder, boundNames);
//			builder.append(')');
//		} else {
////			expr.toString(builder, false, getTag(), boundNames, withTypes);
//		}
//	}
//	

	static final INudParser<MultiplePredicate> MULTIPLE_PREDICATE_PARSER = new ParenNudParser<MultiplePredicate, List<Expression>>(KPARTITION, EXPR_LIST_PARSER) {

		@Override
		protected MultiplePredicate makeValue(FormulaFactory factory,
				List<Expression> child, SourceLocation loc) {
			return factory.makeMultiplePredicate(tags[0], child, loc);
		}

		@Override
		protected List<Expression> getChild(MultiplePredicate t) {
			return Arrays.asList(t.getChildren());
		}

	};
	
	static final INudParser<? extends Formula<?>> PARTITION_PARSER = new AbstractNudParser<Formula<?>>(KPARTITION) {

		public Formula<?> nud(ParserContext pc) throws SyntaxError {
			switch(pc.version) {
			case V1:
				return IDENT_SUBPARSER.nud(pc);
			case V2:
				return MULTIPLE_PREDICATE_PARSER.nud(pc);
			default:
				assert false;
				return null;
			}
		}

		public void toString(IToStringMediator mediator, Formula<?> toPrint) {
			assert toPrint.getTag() == KPARTITION;
			MULTIPLE_PREDICATE_PARSER.toString(mediator, (MultiplePredicate) toPrint);
		}

	};

	static final INudParser<SimplePredicate> FINITE_PARSER = new ParenNudParser<SimplePredicate, Expression>(KFINITE, EXPR_PARSER) {

		@Override
		protected SimplePredicate makeValue(FormulaFactory factory,
				Expression child, SourceLocation loc) {
			return factory.makeSimplePredicate(tags[0], child, loc);
		}

		@Override
		protected Expression getChild(SimplePredicate t) {
			return t.getExpression();
		}

	};
	
	static final INudParser<Expression> UNMINUS_PARSER = new AbstractNudParser<Expression>(UNMINUS) {

		public Expression nud(ParserContext pc) throws SyntaxError {
			final int minusPos = pc.t.pos;
			pc.progress();
			final Expression expr = pc.subParse(EXPR_PARSER);
			final SourceLocation loc = pc.getSourceLocation();
	        if (expr instanceof IntegerLiteral
	        		&& expr.getSourceLocation().getStart() == minusPos + 1) {
				// A unary minus followed by an integer literal, glued together,
				// this is a negative integer literal
	        	final IntegerLiteral lit = (IntegerLiteral) expr;
	        	return pc.factory.makeIntegerLiteral(lit.getValue().negate(), loc);
	        }
	  		return pc.factory.makeUnaryExpression(UNMINUS, expr, loc);
		}

		public void toString(IToStringMediator mediator, Expression toPrint) {
			switch(toPrint.getTag()) {
			case INTLIT:
				INTLIT_SUBPARSER.toString(mediator, (IntegerLiteral) toPrint);
				break;
			case UNMINUS: 
				mediator.appendOperator();
				final Expression child = ((UnaryExpression) toPrint).getChild();
				mediator.subPrint(child, false);
				break;
			default:
				assert false;
			}
		}

	};
	
	static class ExtendedExprParen extends ParenNudParser<ExtendedExpression, List<Expression>> {

		protected ExtendedExprParen(int tag) {
			super(tag, EXPR_LIST_PARSER);
		}

		@Override
		protected ExtendedExpression makeValue(FormulaFactory factory,
				List<Expression> children, SourceLocation loc) throws SyntaxError {
			return checkAndMakeExtendedExpr(factory, tags[0], children, loc);
		}

		@Override
		protected List<Expression> getChild(ExtendedExpression t) {
			return Arrays.asList(t.getChildExpressions());
		}

	}
	
	static ExtendedExpression checkAndMakeExtendedExpr(FormulaFactory factory, int tag,
			List<Expression> children, SourceLocation loc) throws SyntaxError {
		final IExpressionExtension extension = (IExpressionExtension) factory
				.getExtension(tag);
		if (!extension.getKind().checkPreconditions(
				children.toArray(new Expression[children.size()]), NO_PRED)) {
			throw new SyntaxError(new ASTProblem(loc,
					ProblemKind.ExtensionPreconditionError,
					ProblemSeverities.Error));
		}
		return factory.makeExtendedExpression(extension, children, Collections
				.<Predicate> emptyList(), loc);
	}
}

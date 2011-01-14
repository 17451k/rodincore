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
import static org.eventb.core.ast.Formula.CONVERSE;
import static org.eventb.core.ast.Formula.CSET;
import static org.eventb.core.ast.Formula.FREE_IDENT;
import static org.eventb.core.ast.Formula.INTLIT;
import static org.eventb.core.ast.Formula.KBOOL;
import static org.eventb.core.ast.Formula.KFINITE;
import static org.eventb.core.ast.Formula.KPARTITION;
import static org.eventb.core.ast.Formula.MAPSTO;
import static org.eventb.core.ast.Formula.SETEXT;
import static org.eventb.core.ast.Formula.UNMINUS;
import static org.eventb.internal.core.parser.MainParsers.BOUND_IDENT_DECL_LIST_PARSER;
import static org.eventb.internal.core.parser.MainParsers.EXPR_LIST_PARSER;
import static org.eventb.internal.core.parser.MainParsers.EXPR_PARSER;
import static org.eventb.internal.core.parser.MainParsers.FORMULA_LIST_PARSER;
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
import org.eventb.core.ast.ExtendedPredicate;
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
import org.eventb.core.ast.QuantifiedExpression.Form;
import org.eventb.core.ast.QuantifiedPredicate;
import org.eventb.core.ast.RelationalPredicate;
import org.eventb.core.ast.SetExtension;
import org.eventb.core.ast.SimplePredicate;
import org.eventb.core.ast.SourceLocation;
import org.eventb.core.ast.Type;
import org.eventb.core.ast.UnaryExpression;
import org.eventb.core.ast.UnaryPredicate;
import org.eventb.core.ast.extension.IExpressionExtension;
import org.eventb.core.ast.extension.IExtendedFormula;
import org.eventb.core.ast.extension.IFormulaExtension;
import org.eventb.core.ast.extension.IPredicateExtension;
import org.eventb.core.ast.extension.ITypeDistribution;
import org.eventb.internal.core.ast.extension.IToStringMediator;
import org.eventb.internal.core.parser.GenParser.ParserContext;
import org.eventb.internal.core.parser.GenParser.SyntaxError;
import org.eventb.internal.core.parser.MainParsers.BoundIdentDeclListParser;
import org.eventb.internal.core.parser.MainParsers.PatternParser;

/**
 * Sub-parsers are specialized parsers; they are usually bound to an operator.
 * <p>
 * Type parameters of sub parsers are conventionally named R and C. R is the
 * result type of the (sub)parsing, i.e the type of the parsed object. C is the
 * type of the children of the parsed object.
 * </p>
 * 
 * @author Nicolas Beauger
 * 
 */
public class SubParsers {

	static final String SPACE = " ";
	static final Predicate[] NO_PRED = new Predicate[0];
	static final String[] NO_DECL = new String[0];

	static abstract class AbstractSubParser {

		protected final int kind;
		protected final int tag;

		protected AbstractSubParser(int kind, int tag) {
			this.kind = kind;
			this.tag = tag;
		}

		public final int getKind() {
			return kind;
		}
	}

	static abstract class AbstractNudParser<R> extends AbstractSubParser implements INudParser<R> {

		protected AbstractNudParser(int kind, int tag) {
			super(kind, tag);
		}

	}

	private static abstract class AbstractLedParser<R> extends AbstractSubParser implements ILedParser<R> {

		protected AbstractLedParser(int kind, int tag) {
			super(kind, tag);
		}

	}

	
	private static abstract class PrefixNudParser<R> extends AbstractNudParser<R> {
		
		protected PrefixNudParser(int kind, int tag) {
			super(kind, tag);
		}
		
		@Override
		public final SubParseResult<R> nud(ParserContext pc) throws SyntaxError {
			pc.accept(kind);
			final R right = parseRight(pc);
			return new SubParseResult<R>(right, kind);
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
		protected abstract R parseRight(ParserContext pc) throws SyntaxError;
		
		@Override
		public void toString(IToStringMediator mediator, R toPrint) {
			mediator.appendImage(kind);
		}
	}

	private static abstract class ParenNudParser<R, C> extends PrefixNudParser<R> {

		private final INudParser<C> childParser;
		
		protected ParenNudParser(int kind, int tag, INudParser<C> childParser) {
			super(kind, tag);
			this.childParser = childParser;
		}

		@Override
		protected final R parseRight(ParserContext pc) throws SyntaxError {
			// FIXME parsing this way prevents priority and compatibility checks
			// with operators that follow the closing parenthesis
			pc.acceptOpenParen();
			final C child = pc.subParseNoCheck(childParser);
			pc.acceptCloseParen();
			return makeValue(pc.factory, child, pc.getSourceLocation());
		}
		
		protected abstract C getChild(R parent);
		
		@Override
		public void toString(IToStringMediator mediator, R toPrint) {
			super.toString(mediator, toPrint);
			mediator.append("(");
			// FIXME should forbid direct calls to toString,
			// replace with subPrint
			C child = getChild(toPrint);
			printChild(mediator, child);
			mediator.append(")");
		}

		protected void printChild(IToStringMediator mediator, C child) {
			childParser.toString(mediator, child);
		}
		
		protected abstract R makeValue(FormulaFactory factory, C child, SourceLocation loc) throws SyntaxError;
	}
	
	private static abstract class ParenNudFormulaChildParser<R, C extends Formula<?>> extends ParenNudParser<R, C> {

		protected ParenNudFormulaChildParser(int kind, int tag,
				INudParser<C> childParser) {
			super(kind, tag, childParser);
		}
		
		@Override
		protected void printChild(IToStringMediator mediator, C child) {
			mediator.subPrintNoPar(child, true, NO_DECL);
		}
	}
	
	private static abstract class ValuedNudParser<R> implements INudParser<R> {

		public ValuedNudParser() {
			// avoid synthetic accessor
		}

		@Override
		public final SubParseResult<R> nud(ParserContext pc) throws SyntaxError {
			final String tokenVal = pc.t.val;
			final int kind = getKind(pc.getGrammar());
			pc.accept(kind);
			final SourceLocation loc = pc.getSourceLocation();
			final R value = makeValue(pc, tokenVal, loc);
			return new SubParseResult<R>(value, kind);
		}

		protected abstract int getKind(AbstractGrammar grammar);
		
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
		protected abstract R makeValue(ParserContext pc, String tokenVal, SourceLocation loc) throws SyntaxError;

	}

	// TODO use the possibility to have Left different from Right to make
	// assignment parser extend this class
	private static abstract class BinaryLedParser<R, C extends Formula<?>>
			extends AbstractLedParser<R> {

		protected final INudParser<C> childParser;
		
		protected BinaryLedParser(int kind, int tag, INudParser<C> rightParser) {
			super(kind, tag);
			this.childParser = rightParser;
		}
		
		protected C parseRight(ParserContext pc) throws SyntaxError {
			return pc.subParse(childParser, true);
		}

		/**
		 * Returns the left child (or first child) of the given formula node.
		 * 
		 * @param parent
		 *            a formula node
		 * @return a left node
		 */
		protected abstract C getLeft(R parent);

		/**
		 * Return the right child of the given node, or <code>null</code> if
		 * none.
		 * 
		 * @param parent
		 *            a formula node
		 * @return a right node or <code>null</code>
		 */
		protected abstract C getRight(R parent);
		
		@Override
		public final SubParseResult<R> led(Formula<?> left, ParserContext pc) throws SyntaxError {
			pc.accept(kind);
			final C typedLeft = asLeftType(left);
			final C right = parseRight(pc);
			final R value = makeValue(pc.factory, typedLeft, right, pc.getSourceLocation());
			return new SubParseResult<R>(value, kind);
		}

		@Override
		public void toString(IToStringMediator mediator, R toPrint) {
			final C left = getLeft(toPrint);
			mediator.subPrint(left, false);
			mediator.appendImage(kind);
			final C right = getRight(toPrint);
			if (right != null) {
				subPrintRight(mediator, right);
			}
		}

		protected void subPrintRight(IToStringMediator mediator, C right) {
			mediator.subPrint(right, true);
		}
		
		protected abstract C asLeftType(Formula<?> left) throws SyntaxError;
		
		protected abstract R makeValue(FormulaFactory factory, C left,
				C right, SourceLocation loc) throws SyntaxError;
	}
	
	private static abstract class BinaryLedExprParser<R> extends BinaryLedParser<R, Expression> {

		protected BinaryLedExprParser(int kind, int tag) {
			super(kind, tag, EXPR_PARSER);
		}
		
		@Override
		protected final Expression asLeftType(Formula<?> left) throws SyntaxError {
			return asExpression(left);
		}
		
	}
	
	private static abstract class BinaryLedPredParser<R> extends BinaryLedParser<R, Predicate> {

		protected BinaryLedPredParser(int kind, int tag) {
			super(kind, tag, PRED_PARSER);
		}
		
		@Override
		protected Predicate asLeftType(Formula<?> left) throws SyntaxError {
			return asPredicate(left);
		}

	}
	
	private static abstract class AssociativeLedParser<R, C extends Formula<?>> extends AbstractLedParser<R> {

		private final INudParser<C> childParser;
		
		protected AssociativeLedParser(int kind, int tag, INudParser<C> childParser) {
			super(kind, tag);
			this.childParser = childParser;
		}

		@Override
		public SubParseResult<R> led(Formula<?> left, ParserContext pc) throws SyntaxError {
			final C typedLeft = asChildType(left);
			
			final List<C> children = new ArrayList<C>();
			children.add(typedLeft);
			
			do {
				pc.accept(kind);
				final C next = pc.subParse(childParser, true);
				children.add(next);
			} while (pc.t.kind == kind);
			
			final R result = makeResult(pc.factory, children, pc.getSourceLocation());
			return new SubParseResult<R>(result, kind);
		}
		
		protected abstract C[] getChildren(R parent);
		
		@Override
		public void toString(IToStringMediator mediator, R toPrint) {
			final C[] children = getChildren(toPrint);
			mediator.subPrint(children[0], false);
			for (int i = 1; i < children.length; i++) {
				mediator.appendImage(kind);
				mediator.subPrint(children[i], true);
			}
		}
		
		protected abstract C asChildType(Formula<?> left) throws SyntaxError;
		
		protected abstract R makeResult(FormulaFactory factory,
				List<C> children, SourceLocation loc) throws SyntaxError;

	}

	// TODO move ident parsers to MainParsers as they are imported there
	// Takes care of the bindings.
	public static final INudParser<Identifier> IDENT_SUBPARSER = new ValuedNudParser<Identifier>() {

		@Override
		protected int getKind(AbstractGrammar grammar) {
			return grammar.getIDENT();
		}
		
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
	
	static final INudParser<FreeIdentifier> FREE_IDENT_SUBPARSER = new INudParser<FreeIdentifier>() {

		@Override
		public SubParseResult<FreeIdentifier> nud(ParserContext pc) throws SyntaxError {
			final Identifier ident = pc.subParse(IDENT_SUBPARSER, false);
			if (!(ident instanceof FreeIdentifier)) {
				throw new SyntaxError(new ASTProblem(ident.getSourceLocation(),
						ProblemKind.FreeIdentifierExpected,
						ProblemSeverities.Error));
			}
			final FreeIdentifier freeIdent = (FreeIdentifier) ident;
			return new SubParseResult<FreeIdentifier>(freeIdent, pc.getGrammar().getIDENT());
		}

		@Override
		public void toString(IToStringMediator mediator, FreeIdentifier toPrint) {
			mediator.append(toPrint.getName());
		}

	};

	public static class BoundIdentDeclSubParser extends ValuedNudParser<BoundIdentDecl> {

		@Override
		protected int getKind(AbstractGrammar grammar) {
			return grammar.getIDENT();
		}
		
		@Override
		protected BoundIdentDecl makeValue(ParserContext pc, String tokenVal,
				SourceLocation loc) throws SyntaxError {
			Type type = null;
			final int oftype = pc.getGrammar().getOFTYPE();
			if (pc.t.kind == oftype) {
				pc.pushParentKind();
				pc.accept(oftype);
				try {
					type = pc.subParse(TYPE_PARSER, true);
				} finally {
					pc.popParentKind();
				}
			}
			return pc.factory.makeBoundIdentDecl(tokenVal, pc.getSourceLocation(), type);
		}

		@Override
		public void toString(IToStringMediator mediator, BoundIdentDecl toPrint) {
			// bound name renaming has to be performed with knowledge of
			// bound predicate where this bound declaration occurs;
			// hence the printing has to be performed from upper containers,
			// using the static method below;
			// this method only manages the case where the printed declaration
			// is the root formula (practically, concerns tests only).
			mediator.append(toPrint.getName());
			if (mediator.isWithTypes() && toPrint.isTypeChecked()) {
				OftypeParser.appendOftype(mediator, toPrint.getType(), false);
			}
		}
		
		public static void printIdent(IToStringMediator mediator,
				final BoundIdentDecl[] decls, final String[] resolvedIdents,
				int index) {
			mediator.append(resolvedIdents[index]);
			if (mediator.isWithTypes() && decls[index].isTypeChecked()) {
				OftypeParser.appendOftype(mediator, decls[index].getType(), false);
			}
		}
		
	}
	
	public static final BoundIdentDeclSubParser BOUND_IDENT_DECL_SUBPARSER = new BoundIdentDeclSubParser();

	public static class IntLitSubParser extends ValuedNudParser<IntegerLiteral> {

		@Override
		protected int getKind(AbstractGrammar grammar) {
			return grammar.getINTLIT();
		}
		
		@Override
		protected IntegerLiteral makeValue(ParserContext pc, String tokenVal,
				SourceLocation loc) throws SyntaxError {
			try {
				final BigInteger value = new BigInteger(tokenVal);
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
		public void toString(IToStringMediator mediator, IntegerLiteral toPrint) {
			final BigInteger literal = toPrint.getValue();			
			
			toStringInternal(mediator, literal);
			
		}
		
		// Change the minus sign if any, so that it conforms to the mathematical
		// language: \u2212 (minus sign) instead of \u002d (hyphen-minus).
		private void toStringInternal(IToStringMediator mediator, BigInteger literal) {
			final String image = literal.toString();
			if (image.charAt(0) == '-') {
				mediator.append("\u2212");
				mediator.append(image.substring(1));
			} else {
				mediator.append(image);
			}
		}
		
	}
	
	public static final IntLitSubParser INTLIT_SUBPARSER = new IntLitSubParser();
	
	public static final INudParser<Predicate> PRED_VAR_SUBPARSER = new ValuedNudParser<Predicate>() {

		@Override
		protected int getKind(AbstractGrammar grammar) {
			if (grammar instanceof BMath) {
				return ((BMath) grammar).getPREDVAR();
			}
			return grammar.getIDENT();
		}
		
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
		public void toString(IToStringMediator mediator, Predicate toPrint) {
			final String name = ((PredicateVariable) toPrint).getName();
			mediator.append(name);
		}
	};

	public static class OftypeParser implements ILedParser<Expression> {

		private static final String POW_ALPHA = "\u2119(alpha)";
		private static final String POW_ALPHA_ALPHA = "\u2119(alpha \u00d7 alpha)";
		private static final String POW_ALPHA_BETA_ALPHA = "\u2119(alpha \u00d7 beta \u00d7 alpha)";
		private static final String POW_ALPHA_BETA_BETA = "\u2119(alpha \u00d7 beta \u00d7 beta)";
		private static final String EXTENSION_TYPE = "[see operator definition]";
		
		@Override
		public SubParseResult<Expression> led(Formula<?> left, ParserContext pc) throws SyntaxError {
			if (!isTypedGeneric(left)) {
				throw newUnexpectedOftype(pc);
			}
			final int oftype = pc.getGrammar().getOFTYPE();
			pc.accept(oftype);
			
			Type type = pc.subParse(TYPE_PARSER, true);
			final SourceLocation typeLoc = pc.getSourceLocation();
			if (!checkValidTypedGeneric(left, type, typeLoc, pc.result)) {
				type = null;
			}
			final SourceLocation sourceLoc = pc.getEnclosingSourceLocation();
			final Expression result;
			if (left instanceof ExtendedExpression) {
				final ExtendedExpression extExpr = (ExtendedExpression) left;
				// TODO ExtendedExpression.getExtension()
				final IExpressionExtension extension = (IExpressionExtension) pc.factory
						.getExtension(left.getTag());
				result = pc.factory.makeExtendedExpression(extension,
						extExpr.getChildExpressions(),
						extExpr.getChildPredicates(), sourceLoc, type);
			} else {
				result = pc.factory.makeAtomicExpression(left.getTag(),
						sourceLoc, type);
			}
			
			return new SubParseResult<Expression>(result, oftype);
		}

		private static boolean isTypedGeneric(Formula<?> formula) {
			switch (formula.getTag()) {
			case Formula.EMPTYSET:
			case Formula.KID_GEN:
			case Formula.KPRJ1_GEN:
			case Formula.KPRJ2_GEN:
				return true;
			}
			if (formula instanceof ExtendedExpression) {
				return ((ExtendedExpression) formula).isAtomic();
			}
			return false;
		}

		private SyntaxError newUnexpectedOftype(ParserContext pc) {
			return new SyntaxError(new ASTProblem(pc.makeSourceLocation(pc.t),
					ProblemKind.UnexpectedOftype, ProblemSeverities.Error));
		}
		
		// FIXME duplicate checks with AtomicExpression => factorize
		private static boolean checkValidTypedGeneric(Formula<?> formula, Type type,
				SourceLocation typeLoc, ParseResult result) throws SyntaxError {
			switch (formula.getTag()) {
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
			}
			if (formula instanceof ExtendedExpression) {
				final ExtendedExpression extExpr = (ExtendedExpression) formula;
				if (!extExpr.isValidType(type)) {
					result.addProblem(newInvalidGenType(typeLoc, EXTENSION_TYPE));
					return false;
				}
			}
			return true;
		}

		private static ASTProblem newInvalidGenType(SourceLocation loc, String expected) {
			return new ASTProblem(loc,
					ProblemKind.InvalidGenericType,
					ProblemSeverities.Error,
					expected);
		}
		
		private static boolean isValidPrjType(Type type, boolean left) {
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

		@Override
		public void toString(IToStringMediator mediator, Expression toPrint) {
			mediator.subPrint(toPrint, false, NO_DECL, false);
			appendOftype(mediator, toPrint.getType(), true);
		}

		public static void appendOftype(IToStringMediator mediator, Type type, boolean withSpaces) {
			final int oftype = mediator.getFactory().getGrammar().getOFTYPE();
			mediator.appendImage(oftype, withSpaces);
			mediator.append(type.toString());
		}

	}
	
	/**
	 * Parses expressions outside bound identifier declarations. Always returns
	 * an expression with the same tag as left.
	 */
	public static final ILedParser<Expression> OFTYPE = new OftypeParser();
	
	public static class AtomicExpressionParser extends PrefixNudParser<AtomicExpression> {
	
		public AtomicExpressionParser(int kind, int tag) {
			super(kind, tag);
		}
	
		@Override
		protected AtomicExpression parseRight(ParserContext pc)
				throws SyntaxError {
			return pc.factory.makeAtomicExpression(tag, pc.getSourceLocation());
		}

	}

	public static class ExtendedAtomicExpressionParser extends PrefixNudParser<ExtendedExpression> {
		
		public ExtendedAtomicExpressionParser(int kind, int tag) {
			super(kind, tag);
		}
	
		@Override
		protected ExtendedExpression parseRight(ParserContext pc)
				throws SyntaxError {
			return EXTENDED_EXPR.checkAndMake(pc.factory, tag, Collections
					.<Expression> emptyList(), pc.getSourceLocation());
		}

	}

	public static class BinaryExpressionInfix extends BinaryLedExprParser<BinaryExpression> {

		public BinaryExpressionInfix(int kind, int tag) {
			super(kind, tag);
		}
		
		@Override
		protected BinaryExpression makeValue(FormulaFactory factory, Expression left,
				Expression right, SourceLocation loc) throws SyntaxError {
			return factory.makeBinaryExpression(tag, left, right, loc);
		}
		
		@Override
		protected Expression getLeft(BinaryExpression parent) {
			return parent.getLeft();
		}

		@Override
		protected Expression getRight(BinaryExpression parent) {
			return parent.getRight();
		}

	}
	
	public static class ExtendedBinaryExpressionInfix extends BinaryLedExprParser<ExtendedExpression> {

		public ExtendedBinaryExpressionInfix(int kind, int tag) {
			super(kind, tag);
		}

		@Override
		protected ExtendedExpression makeValue(FormulaFactory factory,
				Expression left, Expression right, SourceLocation loc) throws SyntaxError {
			return EXTENDED_EXPR.checkAndMake(factory, tag, asList(left,
					right), loc);
		}
		
		@Override
		protected Expression getLeft(ExtendedExpression parent) {
			return parent.getChildExpressions()[0];
		}

		@Override
		protected Expression getRight(ExtendedExpression parent) {
			return parent.getChildExpressions()[1];
		}

	}

	public static class AssociativeExpressionInfix extends AssociativeLedParser<AssociativeExpression, Expression> {


		public AssociativeExpressionInfix(int kind, int tag) {
			super(kind, tag, EXPR_PARSER);
		}

		@Override
		protected AssociativeExpression makeResult(FormulaFactory factory,
				List<Expression> children, SourceLocation loc) throws SyntaxError {
			return factory.makeAssociativeExpression(tag, children, loc);
		}

		@Override
		protected Expression asChildType(Formula<?> left) throws SyntaxError {
			return asExpression(left);
		}

		@Override
		protected Expression[] getChildren(AssociativeExpression parent) {
			return parent.getChildren();
		}
		
	}

	public static class ExtendedAssociativeExpressionInfix extends AssociativeLedParser<ExtendedExpression, Expression> {

		public ExtendedAssociativeExpressionInfix(int kind, int tag) {
			super(kind, tag, EXPR_PARSER);
		}
		
		@Override
		protected ExtendedExpression makeResult(FormulaFactory factory,
				List<Expression> children, SourceLocation loc) throws SyntaxError {
			return EXTENDED_EXPR.checkAndMake(factory, tag, children, loc);
		}

		@Override
		protected Expression asChildType(Formula<?> left) throws SyntaxError {
			return asExpression(left);
		}

		@Override
		protected Expression[] getChildren(ExtendedExpression parent) {
			return parent.getChildExpressions();
		}
		
	}
	
	public static class AssociativePredicateInfix extends AssociativeLedParser<AssociativePredicate, Predicate> {

		public AssociativePredicateInfix(int kind, int tag) {
			super(kind, tag, PRED_PARSER);
		}

		@Override
		protected Predicate asChildType(Formula<?> left) throws SyntaxError {
			return asPredicate(left);
		}

		@Override
		protected AssociativePredicate makeResult(FormulaFactory factory,
				List<Predicate> children, SourceLocation loc)
				throws SyntaxError {
			return factory.makeAssociativePredicate(tag, children, loc);
		}

		@Override
		protected Predicate[] getChildren(AssociativePredicate parent) {
			return parent.getChildren();
		}
	}

	public static class RelationalPredicateInfix extends BinaryLedExprParser<RelationalPredicate> {

		public RelationalPredicateInfix(int kind, int tag) {
			super(kind, tag);
		}

		@Override
		protected RelationalPredicate makeValue(FormulaFactory factory,
				Expression left, Expression right, SourceLocation loc) throws SyntaxError {
			return factory.makeRelationalPredicate(tag, left, right, loc);
		}
		
		@Override
		protected Expression getLeft(RelationalPredicate parent) {
			return parent.getLeft();
		}

		@Override
		protected Expression getRight(RelationalPredicate parent) {
			return parent.getRight();
		}
	}

	public static abstract class LedImage extends BinaryLedExprParser<BinaryExpression> {

		public LedImage(int kind, int tag) {
			super(kind, tag);
		}

		protected abstract int getCloseKind(AbstractGrammar grammar);
		
		@Override
		protected Expression parseRight(ParserContext pc) throws SyntaxError {
			// FIXME parsing this way prevents priority and compatibility checks
			// with operators that follow the closing parenthesis
			final Expression right = pc.subParseNoCheck(childParser);
			final int closeKind = getCloseKind(pc.getGrammar());
			pc.accept(closeKind);
			return right;
		}
		
		@Override
		protected BinaryExpression makeValue(FormulaFactory factory, Expression left,
				Expression right, SourceLocation loc) throws SyntaxError {
			return factory.makeBinaryExpression(tag, left, right, loc);
		}

		@Override
		protected Expression getRight(BinaryExpression parent) {
			return parent.getRight();
		}

		@Override
		protected Expression getLeft(BinaryExpression parent) {
			return parent.getLeft();
		}
		
		@Override
		protected void subPrintRight(IToStringMediator mediator,
				Expression right) {
			mediator.subPrintNoPar(right, false, NO_DECL);
		}
		
		@Override
		public void toString(IToStringMediator mediator,
				BinaryExpression toPrint) {
			super.toString(mediator, toPrint);
			final int closeKind = getCloseKind(mediator.getFactory()
					.getGrammar());
			mediator.appendImage(closeKind);
		}
	}

	public static class LiteralPredicateParser extends PrefixNudParser<LiteralPredicate> {

		public LiteralPredicateParser(int kind, int tag) {
			super(kind, tag);
		}

		@Override
		protected LiteralPredicate parseRight(ParserContext pc)
				throws SyntaxError {
			return pc.factory.makeLiteralPredicate(tag, pc.getSourceLocation());
		}

	}

	public static class UnaryPredicateParser extends PrefixNudParser<UnaryPredicate> {

		public UnaryPredicateParser(int kind, int tag) {
			super(kind, tag);
		}

		@Override
		protected UnaryPredicate parseRight(ParserContext pc)
				throws SyntaxError {
			final Predicate pred = pc.subParse(PRED_PARSER, false);
			return pc.factory.makeUnaryPredicate(tag, pred, pc.getSourceLocation());
		}

		@Override
		public void toString(IToStringMediator mediator, UnaryPredicate toPrint) {
			super.toString(mediator, toPrint);
			final Predicate child = toPrint.getChild();
			mediator.subPrint(child, false);
		}
	}

	public static class BinaryPredicateParser extends BinaryLedPredParser<BinaryPredicate> {

		public BinaryPredicateParser(int kind, int tag) {
			super(kind, tag);
		}

		@Override
		protected BinaryPredicate makeValue(FormulaFactory factory, Predicate left,
				Predicate right, SourceLocation loc) throws SyntaxError {
			return factory.makeBinaryPredicate(tag, left, right, loc);
		}
		
		@Override
		protected Predicate getLeft(BinaryPredicate parent) {
			return parent.getLeft();
		}

		@Override
		protected Predicate getRight(BinaryPredicate parent) {
			return parent.getRight();
		}
	}

	public static class QuantifiedPredicateParser extends QuantifiedParser<QuantifiedPredicate> {

		public QuantifiedPredicateParser(int kind, int tag) {
			super(kind, tag);
		}

		@Override
		public QuantifiedPredicate parseRight(ParserContext pc) throws SyntaxError {
			final List<BoundIdentDecl> boundIdentifiers = pc.subParseNoBindingNoCheck(BOUND_IDENT_DECL_LIST_PARSER);
			final int dot = pc.getGrammar().getDOT();
			pc.accept(dot);
			final Predicate pred = pc.subParseNoCheck(PRED_PARSER, boundIdentifiers);

			return pc.factory.makeQuantifiedPredicate(tag, boundIdentifiers,
					pred, pc.getSourceLocation());
		}

		@Override
		public void toString(IToStringMediator mediator,
				QuantifiedPredicate toPrint) {
			super.toString(mediator, toPrint);
			final BoundIdentDecl[] boundDecls = toPrint.getBoundIdentDecls();
			printBoundIdentDecls(mediator, boundDecls);
			final int dot = mediator.getFactory().getGrammar().getDOT();
			mediator.appendImage(dot);
			mediator.subPrintNoPar(toPrint.getPredicate(), false, getLocalNames());
		}
	}

	public static class UnaryExpressionParser extends ParenNudFormulaChildParser<UnaryExpression, Expression> {

		public UnaryExpressionParser(int kind, int tag) {
			super(kind, tag, EXPR_PARSER);
		}

		@Override
		protected UnaryExpression makeValue(FormulaFactory factory, Expression child,
				SourceLocation loc) {
			return factory.makeUnaryExpression(tag, child, loc);
		}

		@Override
		protected Expression getChild(UnaryExpression parent) {
			return parent.getChild();
		}

	}
	
	public static class ConverseParser extends BinaryLedExprParser<UnaryExpression> {

		public ConverseParser(int kind) {
			super(kind, CONVERSE);
		}
		
		@Override
		protected UnaryExpression makeValue(FormulaFactory factory, Expression left,
				Expression right, SourceLocation loc) throws SyntaxError {
			return factory.makeUnaryExpression(tag, left, loc);
		}
		
		@Override
		protected Expression parseRight(ParserContext pc) throws SyntaxError {
			// nothing to parse at right
			return null;
		}
		
		@Override
		protected Expression getLeft(UnaryExpression parent) {
			return parent.getChild();
		}

		@Override
		protected Expression getRight(UnaryExpression parent) {
			return null;
		}
	}
	
	public static class KBoolParser extends ParenNudFormulaChildParser<BoolExpression, Predicate> {

		public KBoolParser(int kind) {
			super(kind, KBOOL, PRED_PARSER);
		}

		@Override
		protected BoolExpression makeValue(FormulaFactory factory, Predicate child,
				SourceLocation loc) {
			return factory.makeBoolExpression(child, loc);
		}

		@Override
		protected Predicate getChild(BoolExpression parent) {
			return parent.getPredicate();
		}

	}

	public static final class SetExtParser extends PrefixNudParser<SetExtension> {
		
		public SetExtParser(int kind) {
			super(kind, SETEXT);
		}

		@Override
		public SetExtension parseRight(ParserContext pc) throws SyntaxError {
			final List<Expression> exprs;
			final int rbrace = pc.getGrammar().getRBRACE();
			if (pc.t.kind == rbrace) { // only place where a list may be empty
				exprs = Collections.emptyList();
			} else {
				exprs = pc.subParseNoCheck(EXPR_LIST_PARSER);
			}
			pc.accept(rbrace);
			return pc.factory.makeSetExtension(exprs, pc.getSourceLocation());
		}

		@Override
		public void toString(IToStringMediator mediator, SetExtension toPrint) {
			super.toString(mediator, toPrint);
			final Expression[] members = toPrint.getMembers();
			if (members.length > 0) {
				EXPR_LIST_PARSER.toString(mediator, asList(members));
			}
			final int rbrace = mediator.getFactory().getGrammar().getRBRACE();
			mediator.appendImage(rbrace);
		}
	}
	
	static void printMid(IToStringMediator mediator) {
		mediator.append(SPACE);
		final int mid = mediator.getFactory().getGrammar().getMID();
		mediator.appendImage(mid);
		mediator.append(SPACE);
	}

	public static interface IQuantifiedParser<R> extends INudParser<R> {
		void setLocalNames(String[] localNames);
	}
	
	static abstract class QuantifiedParser<R> extends PrefixNudParser<R> implements IQuantifiedParser<R> {
		protected QuantifiedParser(int kind, int tag) {
			super(kind, tag);
		}

		private String[] localNames = null;

		@Override
		public void setLocalNames(String[] localNames) {
			this.localNames = localNames;
		}

		protected String[] getLocalNames() {
			assert localNames != null;
			return localNames;
		}
		
		protected void printBoundIdentDecls(IToStringMediator mediator, BoundIdentDecl[] boundDecls) {
			BoundIdentDeclListParser.toString(mediator, boundDecls, getLocalNames());
		}
	}
	
	public static class ExplicitQuantExpr extends QuantifiedParser<QuantifiedExpression> {
		
		public ExplicitQuantExpr(int kind, int tag) {
			super(kind, tag);
		}

		@Override
		protected QuantifiedExpression parseRight(ParserContext pc) throws SyntaxError {
			final List<BoundIdentDecl> boundIdents = pc.subParseNoBindingNoCheck(BOUND_IDENT_DECL_LIST_PARSER);
			final int dot = pc.getGrammar().getDOT();
			pc.accept(dot);
			final Predicate pred = pc.subParseNoParentNoCheck(PRED_PARSER, boundIdents);
			final int mid = pc.getGrammar().getMID();
			pc.accept(mid);
			final Expression expr = pc.subParseNoCheck(EXPR_PARSER, boundIdents);
			acceptClose(pc);

			return pc.factory.makeQuantifiedExpression(tag, boundIdents, pred,
					expr, pc.getSourceLocation(), Form.Explicit);
		}
	
		protected void acceptClose(ParserContext pc) throws SyntaxError {
			// do nothing by default
		}

		@Override
		public void toString(IToStringMediator mediator,
				QuantifiedExpression toPrint) {
			super.toString(mediator, toPrint);
			final BoundIdentDecl[] boundDecls = toPrint.getBoundIdentDecls();
			printBoundIdentDecls(mediator, boundDecls);
			final int dot = mediator.getFactory().getGrammar().getDOT();
			mediator.appendImage(dot);
			mediator.subPrintNoPar(toPrint.getPredicate(), false, getLocalNames());
			printMid(mediator);
			mediator.subPrintNoPar(toPrint.getExpression(), false, getLocalNames());
		}
	}
	
	public static class CSetExplicit extends ExplicitQuantExpr {
		
		public CSetExplicit(int kind) {
			super(kind, CSET);
		}

		@Override
		protected void acceptClose(ParserContext pc) throws SyntaxError {
			final int rbrace = pc.getGrammar().getRBRACE();
			pc.accept(rbrace);
		}
		
		@Override
		public void toString(IToStringMediator mediator, QuantifiedExpression toPrint) {
			super.toString(mediator, toPrint);
			final int rbrace = mediator.getFactory().getGrammar().getRBRACE();
			mediator.appendImage(rbrace);
		}
	}
	
	public static class ImplicitQuantExpr extends QuantifiedParser<QuantifiedExpression> {
		
		public ImplicitQuantExpr(int kind, int tag) {
			super(kind, tag);
		}

		@Override
		protected final QuantifiedExpression parseRight(ParserContext pc)
		throws SyntaxError {
			final Expression expr = pc.subParseNoBindingNoCheck(EXPR_PARSER);
			final int mid = pc.getGrammar().getMID();
			pc.accept(mid);
			final List<BoundIdentDecl> boundIdents = new ArrayList<BoundIdentDecl>();
			final Expression boundExpr = expr.bindAllFreeIdents(boundIdents, pc.factory);

			final Predicate pred = pc.subParseNoParentNoCheck(PRED_PARSER, boundIdents);
			acceptClose(pc);

			return pc.factory.makeQuantifiedExpression(tag, boundIdents, pred,
					boundExpr, pc.getSourceLocation(), Form.Implicit);
		}
		
		protected void acceptClose(ParserContext pc) throws SyntaxError {
			// do nothing by default
		}

		@Override
		public void toString(IToStringMediator mediator,
				QuantifiedExpression toPrint) {
			super.toString(mediator, toPrint);
			mediator.subPrintNoPar(toPrint.getExpression(), false, getLocalNames());
			printMid(mediator);
			mediator.subPrintNoPar(toPrint.getPredicate(), false, getLocalNames());
		}
		
	}
	
	public static class CSetImplicit extends ImplicitQuantExpr {

		public CSetImplicit(int kind) {
			super(kind, CSET);
		}

		@Override
		protected void acceptClose(ParserContext pc) throws SyntaxError {
			final int rbrace = pc.getGrammar().getRBRACE();
			pc.accept(rbrace);
		}
		
		@Override
		public void toString(IToStringMediator mediator, QuantifiedExpression toPrint) {
			super.toString(mediator, toPrint);
			final int rbrace = mediator.getFactory().getGrammar().getRBRACE();
			mediator.appendImage(rbrace);
		}
	}
	
	public static class CSetLambda extends QuantifiedParser<QuantifiedExpression> {
		
		public CSetLambda(int kind) {
			super(kind, CSET);
		}

		@Override
		public QuantifiedExpression parseRight(ParserContext pc) throws SyntaxError {
			final PatternParser pattParser = new PatternParser(pc.result);
			final Pattern pattern = pc.subParseNoBindingNoCheck(pattParser);
			final int dot = pc.getGrammar().getDOT();
			pc.accept(dot);
			final List<BoundIdentDecl> boundDecls = pattern.getDecls();
			final Predicate pred = pc.subParseNoParentNoCheck(PRED_PARSER, boundDecls);
			final int mid = pc.getGrammar().getMID();
			pc.accept(mid);
			final Expression expr = pc.subParseNoCheck(EXPR_PARSER, boundDecls);
			
			final Expression pair = pc.factory.makeBinaryExpression(MAPSTO,
					pattern.getPattern(), expr, null);
			return pc.factory.makeQuantifiedExpression(tag, boundDecls, pred,
					pair, pc.getSourceLocation(), Form.Lambda);
		}

		@Override
		public void toString(IToStringMediator mediator,
				QuantifiedExpression toPrint) {
			super.toString(mediator, toPrint);
			final Expression child = toPrint.getExpression();
			assert child.getTag() == MAPSTO;
			final BinaryExpression pair = (BinaryExpression) child;
			final Expression pattern = pair.getLeft();
			
			PatternParser.appendPattern(mediator, pattern, toPrint.getBoundIdentDecls(), getLocalNames());
			
			final int dot = mediator.getFactory().getGrammar().getDOT();
			mediator.appendImage(dot);
			mediator.subPrintNoPar(toPrint.getPredicate(), false, getLocalNames());
			printMid(mediator);
			mediator.subPrintNoPar(pair.getRight(), false, getLocalNames());
		}
	}

	// TODO extract from above code for printing quantified expressions
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

	public static class MultiplePredicateParser extends ParenNudParser<MultiplePredicate, List<Expression>> {

		public MultiplePredicateParser(int kind) {
			super(kind, KPARTITION, EXPR_LIST_PARSER);
		}

		@Override
		protected MultiplePredicate makeValue(FormulaFactory factory,
				List<Expression> child, SourceLocation loc) {
			return factory.makeMultiplePredicate(tag, child, loc);
		}

		@Override
		protected List<Expression> getChild(MultiplePredicate parent) {
			return Arrays.asList(parent.getChildren());
		}

	}

	public static class FiniteParser extends ParenNudFormulaChildParser<SimplePredicate, Expression> {

		public FiniteParser(int kind) {
			super(kind, KFINITE, EXPR_PARSER);
		}

		@Override
		protected SimplePredicate makeValue(FormulaFactory factory,
				Expression child, SourceLocation loc) {
			return factory.makeSimplePredicate(tag, child, loc);
		}

		@Override
		protected Expression getChild(SimplePredicate parent) {
			return parent.getExpression();
		}

	}
	
	public static class UnminusParser extends AbstractNudParser<Expression> {

		public UnminusParser(int kind) {
			super(kind, UNMINUS);
		}

		@Override
		public SubParseResult<Expression> nud(ParserContext pc) throws SyntaxError {
			final int minusPos = pc.t.pos;
			pc.accept(kind);
			final Expression expr = pc.subParse(EXPR_PARSER, true);
			final SourceLocation loc = pc.getSourceLocation();
	        if (expr instanceof IntegerLiteral
	        		&& expr.getSourceLocation().getStart() == minusPos + 1) {
				// A unary minus followed by an integer literal, glued together,
				// this is a negative integer literal
	        	final IntegerLiteral lit = (IntegerLiteral) expr;
	        	final IntegerLiteral result = pc.factory.makeIntegerLiteral(lit.getValue().negate(), loc);
				return new SubParseResult<Expression>(result, pc.getGrammar().getINTLIT());
	        }
	  		final UnaryExpression result = pc.factory.makeUnaryExpression(UNMINUS, expr, loc);
			return new SubParseResult<Expression>(result, kind);
		}

		@Override
		public void toString(IToStringMediator mediator, Expression toPrint) {
			mediator.appendImage(kind, false);
			final Expression child = ((UnaryExpression) toPrint).getChild();
			final boolean parenthesize = child.getTag() == INTLIT;
			if (parenthesize) {
				mediator.subPrintWithPar(child);
			} else {
				mediator.subPrint(child, true);
			}
		}
	}
	
	private static class AbstractExtendedParen<R extends IExtendedFormula> extends ParenNudParser<R, List<Formula<?>>> {
		
		private final ExtensionCheckMaker<R> extCheckMaker;
		
		public AbstractExtendedParen(int kind, int tag, ExtensionCheckMaker<R> extCheckMaker) {
			super(kind, tag, FORMULA_LIST_PARSER);
			this.extCheckMaker = extCheckMaker;
		}

		@Override
		protected R makeValue(FormulaFactory factory,
				List<Formula<?>> children, SourceLocation loc) throws SyntaxError {
			return extCheckMaker.checkAndMake(factory, tag, children, loc);
		}

		@Override
		protected List<Formula<?>> getChild(R parent) {
			final ITypeDistribution childTypes = parent.getExtension()
					.getKind().getProperties().getChildTypes();
			return childTypes.makeList(parent.getChildExpressions(),
					parent.getChildPredicates());
		}

	}
	
	public static class ExtendedExprParen extends AbstractExtendedParen<ExtendedExpression> {

		public ExtendedExprParen(int kind, int tag) {
			super(kind, tag, EXTENDED_EXPR);
		}

	}
	
	public static class ExtendedPredParen extends AbstractExtendedParen<ExtendedPredicate> {

		public ExtendedPredParen(int kind, int tag) {
			super(kind, tag, EXTENDED_PRED);
		}

	}
	
	private static abstract class ExtensionCheckMaker<T extends IExtendedFormula> {

		public ExtensionCheckMaker() {
			// avoid synthetic accessor methods
		}
		
		public final T checkAndMake(FormulaFactory factory, int tag,
				List<? extends Formula<?>> children, SourceLocation loc)
				throws SyntaxError {
			final IFormulaExtension extension = factory.getExtension(tag);
			final ITypeDistribution childTypes = extension.getKind()
					.getProperties().getChildTypes();
			if (!childTypes.check(children)) {
				throw new SyntaxError(new ASTProblem(loc,
						ProblemKind.ExtensionPreconditionError,
						ProblemSeverities.Error));
			}
			final List<Expression> childExprs = new ArrayList<Expression>();
			final List<Predicate> childPreds = new ArrayList<Predicate>();
			splitExprPred(children, childExprs, childPreds);
			
			return make(factory, extension, childExprs, childPreds, loc);
		}

		private static void splitExprPred(List<? extends Formula<?>> children,
				List<Expression> childExprs, List<Predicate> childPreds) {
			for (Formula<?> child : children) {
				if (child instanceof Expression) {
					childExprs.add((Expression) child);
				} else {
					childPreds.add((Predicate) child);
				}
			}
		}

		protected abstract T make(FormulaFactory factory,
				final IFormulaExtension extension, List<Expression> childExprs,
				List<Predicate> childPreds, SourceLocation loc);

	}
	
	static final ExtensionCheckMaker<ExtendedExpression> EXTENDED_EXPR = new ExtensionCheckMaker<ExtendedExpression>() {

		@Override
		protected ExtendedExpression make(FormulaFactory factory,
				IFormulaExtension extension, List<Expression> childExprs,
				List<Predicate> childPreds, SourceLocation loc) {
			return factory.makeExtendedExpression(
					(IExpressionExtension) extension, childExprs,
					childPreds, loc);
		}

	};

	static final ExtensionCheckMaker<ExtendedPredicate> EXTENDED_PRED = new ExtensionCheckMaker<ExtendedPredicate>() {

		@Override
		protected ExtendedPredicate make(FormulaFactory factory,
				IFormulaExtension extension, List<Expression> childExprs,
				List<Predicate> childPreds, SourceLocation loc) {
			return factory.makeExtendedPredicate(
					(IPredicateExtension) extension, childExprs, childPreds,
					loc);
		}

	};
	
	
}

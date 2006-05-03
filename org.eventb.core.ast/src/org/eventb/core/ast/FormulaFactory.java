/*
 * Created on 27-may-2005
 *
 */
package org.eventb.core.ast;

import java.math.BigInteger;
import java.util.List;

import org.eventb.internal.core.parser.Lexer;
import org.eventb.internal.core.parser.ParseResult;
import org.eventb.internal.core.parser.Parser;
import org.eventb.internal.core.parser.Scanner;
import org.eventb.internal.core.typecheck.TypeEnvironment;


/**
 * This class is the factory class for all the ast nodes of an event-B formula.
 * <p>
 * Use this class to instantiate all types of ast nodes used in an event-B
 * formula. Tags used in this method are the constants defined in class {@link Formula}.
 * </p>
 *  
 * @author François Terrier
 */

public class FormulaFactory {

	static FormulaFactory DEFAULT_INSTANCE = new FormulaFactory();

	/**
	 * Returns the default instance of the type factory.
	 * 
	 * @return the single instance of this class.
	 */
	public static FormulaFactory getDefault() {
		return DEFAULT_INSTANCE;
	}

	private FormulaFactory() {
		// Nothing to do
	}

	/**
	 * Returns a new associative expression
	 * <p>
	 * {BUNION, BINTER, BCOMP, FCOMP, OVR, PLUS, MUL}
	 * @param tag
	 *            the tag of the associative expression
	 * @param children
	 *            the children of the associative expression
	 * @param location
	 *            the location of the assocative expression
	 * 
	 * @return a new associative expression
	 */
	public AssociativeExpression makeAssociativeExpression(
			int tag, Expression[] children, SourceLocation location) {
		return new AssociativeExpression(children, tag, location, this);
	}

	/**
	 * Returns a new associative expression
	 * <p>
	 * {BUNION, BINTER, BCOMP, FCOMP, OVR, PLUS, MUL}
	 * </p>
	 * @param tag
	 *            the tag of the associative expression
	 * @param children
	 *            the children of the associative expression
	 * @param location
	 *            the location of the assocative expression
	 * 
	 * @return a new associative expression
	 */
	public AssociativeExpression makeAssociativeExpression(
			int tag, List<Expression> children, SourceLocation location) {
		return new AssociativeExpression(children, tag, location, this);
	}
	
	/**
	 * Returns a new associative predicate
	 * <p>
	 * {LAND, LOR, LEQV}
	 * </p>
	 * @param tag
	 *            the tag of the associative predicate
	 * @param predicates
	 *            the children of the asswociative predicate
	 * @param location
	 *            the location of the associative predicate
	 * 
	 * @return a new associative predicate
	 */
	public AssociativePredicate makeAssociativePredicate(
			int tag, List<Predicate> predicates, SourceLocation location) {
		return new AssociativePredicate(predicates, tag, location, this);
	}

	/**
	 * Returns a new associative predicate
	 * <p>
	 * {LAND, LOR, LEQV}
	 * </p>
	 * @param tag
	 *            the tag of the associative predicate
	 * @param predicates
	 *            the children of the asswociative predicate
	 * @param location
	 *            the location of the associative predicate
	 * 
	 * @return a new associative predicate
	 */
	public AssociativePredicate makeAssociativePredicate(
			int tag, Predicate[] predicates, SourceLocation location) {
		return new AssociativePredicate(predicates, tag, location, this);
	}

	/**
	 * Returns a new atomic expression
	 * <p>
	 * {INTEGER, NATURAL, NATURAL1, BOOL, TRUE, FALSE, EMPTYSET, KPRED, KSUCC}
	 * 
	 * @param tag
	 *            the tag of the atomic expression
	 * @param location
	 *            the location of the atomic expression
	 * @return a new atomic expression
	 */
	public AtomicExpression makeAtomicExpression(int tag,
			SourceLocation location) {
		return new AtomicExpression(tag, location, null, this);
	}

	/**
	 * Returns a new empty set expression.
	 * @param type
	 *            the type of the empty set (must be a power set)
	 * @param location
	 *            the location of the empty set
	 * 
	 * @return a new empty set expression
	 */
	public AtomicExpression makeEmptySet(Type type, SourceLocation location) {
		return new AtomicExpression(Formula.EMPTYSET, location, type, this);
	}

	/**
	 * Returns a new "becomes equal to" assignment.
	 * <p>
	 * 
	 * @param ident
	 *            identifier which is assigned to (left-hand side)
	 * @param value
	 *            after-value for this identifier (right-hand side)
	 * @param location
	 *            the location of the assignment
	 * 
	 * @return a new "becomes equal to" assignment
	 */
	public BecomesEqualTo makeBecomesEqualTo(FreeIdentifier ident,
			Expression value, SourceLocation location) {
		return new BecomesEqualTo(ident, value, location, this);
	}

	/**
	 * Returns a new "becomes equal to" assignment.
	 * <p>
	 * @param idents
	 *            array of identifier which are assigned to (left-hand side)
	 * @param values
	 *            array of after-values for these identifiers (right-hand side)
	 * @param location
	 *            the location of the assignment
	 * 
	 * @return a new "becomes equal to" assignment
	 */
	public BecomesEqualTo makeBecomesEqualTo(FreeIdentifier[] idents,
			Expression[] values, SourceLocation location) {
		return new BecomesEqualTo(idents, values, location, this);
	}

	/**
	 * Returns a new "becomes equal to" assignment.
	 * <p>
	 * @param idents
	 *            list of identifier which are assigned to (left-hand side)
	 * @param values
	 *            list of after-values for these identifiers (right-hand side)
	 * @param location
	 *            the location of the assignment
	 * 
	 * @return a new "becomes equal to" assignment
	 */
	public BecomesEqualTo makeBecomesEqualTo(List<FreeIdentifier> idents,
			List<Expression> values, SourceLocation location) {
		return new BecomesEqualTo(idents, values, location, this);
	}

	/**
	 * Returns a new "becomes member of" assignment.
	 * <p>
	 * @param ident
	 *            identifier which is assigned to (left-hand side)
	 * @param setExpr
	 *            set to which the after-value belongs (right-hand side)
	 * @param location
	 *            the location of the assignment
	 * 
	 * @return a new "becomes member of" assignment
	 */
	public BecomesMemberOf makeBecomesMemberOf(FreeIdentifier ident,
			Expression setExpr, SourceLocation location) {
		return new BecomesMemberOf(ident, setExpr, location, this);
	}

	/**
	 * Returns a new "becomes such that" assignment.
	 * <p>
	 * @param ident
	 *            identifier which is assigned to (left-hand side)
	 * @param primedIdent
	 *            bound identifier declaration for primed identifier (after values)
	 * @param condition
	 *            condition on before and after values of this identifier (right-hand side)
	 * @param location
	 *            the location of the assignment
	 * 
	 * @return a new "becomes such that" assignment
	 */
	public BecomesSuchThat makeBecomesSuchThat(FreeIdentifier ident,
			BoundIdentDecl primedIdent, Predicate condition,
			SourceLocation location) {
		return new BecomesSuchThat(ident, primedIdent, condition, location, this);
	}

	/**
	 * Returns a new "becomes such that" assignment.
	 * <p>
	 * @param idents
	 *            array of identifiers that are assigned to (left-hand side)
	 * @param primedIdents
	 *            array of bound identifier declarations for primed identifiers (after values)
	 * @param condition
	 *            condition on before and after values of these identifiers (right-hand side)
	 * @param location
	 *            the location of the assignment
	 * 
	 * @return a new "becomes such that" assignment
	 */
	public BecomesSuchThat makeBecomesSuchThat(FreeIdentifier[] idents,
			BoundIdentDecl[] primedIdents, Predicate condition,
			SourceLocation location) {
		return new BecomesSuchThat(idents, primedIdents, condition, location, this);
	}

	/**
	 * Returns a new "becomes such that" assignment.
	 * <p>
	 * @param idents
	 *            list of identifiers that are assigned to (left-hand side)
	 * @param primedIdents
	 *            list of bound identifier declarations for primed identifiers (after values)
	 * @param condition
	 *            condition on before and after values of these identifiers (right-hand side)
	 * @param location
	 *            the location of the assignment
	 * 
	 * @return a new "becomes such that" assignment
	 */
	public BecomesSuchThat makeBecomesSuchThat(List<FreeIdentifier> idents,
			List<BoundIdentDecl> primedIdents, Predicate condition,
			SourceLocation location) {
		return new BecomesSuchThat(idents, primedIdents, condition, location, this);
	}

	/**
	 * Returns a new binary expression
	 * <p>
	 * {MAPSTO, REL, TREL, SREL, STREL, PFUN, TFUN, PINJ, TINJ, PSUR, TSUR,
	 * TBIJ, SETMINUS, CPROD, DPROD, PPROD, DOMRES, DOMSUB, RANRES, RANSUB,
	 * UPTO, MINUS, DIV, MOD, EXPN, FUNIMAGE, RELIMAGE}
	 * @param tag
	 *            the tag of the binary expression
	 * @param left
	 *            the left child of the binary expression
	 * @param right
	 *            the right child of the binary expression
	 * @param location
	 *            the location of the binary expression
	 * 
	 * @return a new binary expression
	 */
	public BinaryExpression makeBinaryExpression(int tag,
			Expression left, Expression right, SourceLocation location) {
		return new BinaryExpression(left, right, tag, location, this);
	}

	/**
	 * Returns a new binary predicate
	 * <p>
	 * {LIMP}
	 * @param tag
	 *            the tag of the binary predicate
	 * @param left
	 *            the left child of the binary predicate
	 * @param right
	 *            the right child of the binary predicate
	 * @param location
	 *            the location of the binary predicate
	 * 
	 * @return a new binary predicate
	 */
	public BinaryPredicate makeBinaryPredicate(int tag,
			Predicate left, Predicate right, SourceLocation location) {
		return new BinaryPredicate(left, right, tag, location, this);
	}

	/**
	 * Returns a new "bool" expression.
	 * <p>
	 * {KBOOL}
	 * 
	 * @param child
	 *            the child of the bool expression
	 * @param location
	 *            the location of the bool expression
	 * @return a new bool expression
	 */
	public BoolExpression makeBoolExpression(Predicate child, SourceLocation location) {
		return new BoolExpression(child, Formula.KBOOL, location, this);
	}

	/**
	 * Creates a new node representing a declaration of a bound identifier,
	 * using as model a free occurrence of the same identifier.
	 * 
	 * @param ident
	 *            a free identifier occurrence
	 * @return a bound identifier declaration
	 */
	public BoundIdentDecl makeBoundIdentDecl(FreeIdentifier ident) {
		return new BoundIdentDecl(ident.getName(), Formula.BOUND_IDENT_DECL,
				ident.getSourceLocation(), ident.getType());
	}

	/**
	 * Creates a new node representing a declaration of a bound identifier.
	 * 
	 * @param name
	 *            the name of the identifier. Must not be null or an empty string.
	 * @param location
	 *            the source location of this identifer declaration
	 * @return a bound identifier declaration
	 * 
	 * @see #makeFreeIdentifier(String, SourceLocation)
	 * @see #makeBoundIdentifier(int, SourceLocation)
	 */
	public BoundIdentDecl makeBoundIdentDecl(String name,
			SourceLocation location) {
		return new BoundIdentDecl(name, Formula.BOUND_IDENT_DECL, location, null);
	}

	/**
	 * Creates a new node representing a declaration of a bound identifier.
	 * 
	 * @param name
	 *            the name of the identifier. Must not be null or an empty string.
	 * @param location
	 *            the source location of this identifer declaration
	 * @param type
	 *            the type of this identifier. Can be <code>null</code>.
	 * @return a bound identifier declaration
	 * 
	 * @see #makeFreeIdentifier(String, SourceLocation)
	 * @see #makeBoundIdentifier(int, SourceLocation)
	 */
	public BoundIdentDecl makeBoundIdentDecl(String name,
			SourceLocation location, Type type) {
		return new BoundIdentDecl(name, Formula.BOUND_IDENT_DECL, location, type);
	}

	/**
	 * Returns a new bound occurrence of an identifier.
	 * 
	 * @param index
	 *            the index in the De Bruijn notation. Must be non-negative.
	 * @param location
	 *            the source location of this identifier occurence
	 * @return a bound identifier occurrence
	 * 
	 * @see #makeBoundIdentDecl(String, SourceLocation)
	 * @see #makeFreeIdentifier(String, SourceLocation)
	 */
	public BoundIdentifier makeBoundIdentifier(int index,
			SourceLocation location) {
		return new BoundIdentifier(index, Formula.BOUND_IDENT, location, null, this);
	}

	/**
	 * Returns a new bound occurrence of an identifier.
	 * 
	 * @param index
	 *            the index in the De Bruijn notation. Must be non-negative.
	 * @param location
	 *            the source location of this identifier occurence
	 * @param type
	 *            the type of this identifier. Can be <code>null</code>.
	 * @return a bound identifier occurrence
	 * 
	 * @see #makeBoundIdentDecl(String, SourceLocation)
	 * @see #makeFreeIdentifier(String, SourceLocation)
	 */
	public BoundIdentifier makeBoundIdentifier(int index,
			SourceLocation location, Type type) {
		return new BoundIdentifier(index, Formula.BOUND_IDENT, location, type, this);
	}

	/**
	 * Creates a new node representing a free occurence of an identifier.
	 * 
	 * @param name
	 *            the name of the identifier. Must not be null or an empty string.
	 * @param location
	 *            the source location of this identifer occurence
	 * @return a free identifier
	 * 
	 * @see #makeBoundIdentDecl(String, SourceLocation)
	 * @see #makeBoundIdentifier(int, SourceLocation)
	 */
	public FreeIdentifier makeFreeIdentifier(String name,
			SourceLocation location) {
		return new FreeIdentifier(name, Formula.FREE_IDENT, location, null, this);
	}
	
	/**
	 * Creates a new node representing a free occurence of an identifier.
	 * 
	 * @param name
	 *            the name of the identifier. Must not be null or an empty string.
	 * @param location
	 *            the source location of this identifer occurence
	 * @param type
	 *            the type of this identifier. Can be <code>null</code>.
	 * @return a free identifier
	 * 
	 * @see #makeBoundIdentDecl(String, SourceLocation)
	 * @see #makeBoundIdentifier(int, SourceLocation)
	 */
	public FreeIdentifier makeFreeIdentifier(String name,
			SourceLocation location, Type type) {
		return new FreeIdentifier(name, Formula.FREE_IDENT, location, type, this);
	}
	
	/**
	 * Creates a node that contains a primed free identifier.
	 * 
	 * @param identifier An unprimed identifier that is the template for the primed one.
	 * An exception is thrown if it is already primed.
	 * @return The identifier passed as parameter with a prime appended.
	 */
	public FreeIdentifier makePrimedFreeIdentifier(FreeIdentifier identifier) {
		
		String name = identifier.getName();
		assert name.charAt(name.length()-1) != '\'';
		
		FreeIdentifier primedIdentifier = makeFreeIdentifier(
				name + "'",
				identifier.getSourceLocation(),
				identifier.getType());
		
		return primedIdentifier;
	}

	/**
	 * Returns a new integer literal.
	 * 
	 * @param literal
	 *            the integer value for this literal
	 * @param location
	 *            the source location of the literal
	 * @return a new integer literal
	 */
	public IntegerLiteral makeIntegerLiteral(BigInteger literal,
			SourceLocation location) {
		return new IntegerLiteral(literal, Formula.INTLIT, location, this);
	}

	/**
	 * Returns a new literal predicate
	 * <p>
	 * {BTRUE, BFALSE}
	 * 
	 * @param tag
	 *            the tag of the predicate
	 * @param location
	 *            the location of the predicate
	 * @return a new literal predicate
	 */
	public LiteralPredicate makeLiteralPredicate(int tag,
			SourceLocation location) {
		return new LiteralPredicate(tag, location, this);
	}

	/**
	 * Returns a new quantified expression
	 * <p>
	 * {LAMBDA, QUNION, QINTER, CSET}
	 * </p>
	 * @param tag
	 *            the tag of the quantified expression
	 * @param boundIdentifiers
	 *            a list of the quantifiers
	 * @param pred
	 *            the corresponding predicate
	 * @param expr
	 *            the corresponding expression
	 * @param location
	 *            the location of the quantified expression
	 * @param form
	 *            the written form of the quantified expression
	 * @return a new quantified expression
	 */
	// TODO: maybe make different creators for every form
	public QuantifiedExpression makeQuantifiedExpression(int tag,
			BoundIdentDecl[] boundIdentifiers, Predicate pred, Expression expr,
			SourceLocation location, QuantifiedExpression.Form form) {
		return new QuantifiedExpression(expr, pred, boundIdentifiers, tag,
				location, form, this);
	}

	/**
	 * Returns a new quantified expression
	 * <p>
	 * {LAMBDA, QUNION, QINTER, CSET}
	 * </p>
	 * @param tag
	 *            the tag of the quantified expression
	 * @param boundIdentifiers
	 *            a list of the quantifiers
	 * @param pred
	 *            the corresponding predicate
	 * @param expr
	 *            the corresponding expression
	 * @param location
	 *            the location of the quantified expression
	 * @param form
	 *            the written form of the quantified expression
	 * @return a new quantified expression
	 */
	public QuantifiedExpression makeQuantifiedExpression(int tag,
			List<BoundIdentDecl> boundIdentifiers, Predicate pred, Expression expr,
			SourceLocation location, QuantifiedExpression.Form form) {
		return new QuantifiedExpression(expr, pred, boundIdentifiers, tag,
				location, form, this);
	}

	/**
	 * Returns a new quantified predicate
	 * <p>
	 * {FORALL, EXISTS}
	 * @param tag
	 *            the tag of the quantified predicate
	 * @param boundIdentifiers
	 *            a list of the quantifiers
	 * @param pred
	 *            the corresponding predicate
	 * @param location
	 *            the location of the quantified predicate
	 * @return a new quantified predicate
	 */
	public QuantifiedPredicate makeQuantifiedPredicate(int tag,
			BoundIdentDecl[] boundIdentifiers, Predicate pred, SourceLocation location) {
		return new QuantifiedPredicate(pred, boundIdentifiers, tag, location, this);
	}

	/**
	 * Returns a new quantified predicate
	 * <p>
	 * {FORALL, EXISTS}
	 * @param tag
	 *            the tag of the quantified predicate
	 * @param boundIdentifiers
	 *            a list of the quantifiers
	 * @param pred
	 *            the corresponding predicate
	 * @param location
	 *            the location of the quantified predicate
	 * @return a new quantified predicate
	 */
	public QuantifiedPredicate makeQuantifiedPredicate(int tag,
			List<BoundIdentDecl> boundIdentifiers, Predicate pred, SourceLocation location) {
		return new QuantifiedPredicate(pred, boundIdentifiers, tag, location, this);
	}
	
	/**
	 * Returns a new relational predicate
	 * <p>
	 * {EQUAL, NOTEQUAL, LT, LE, GT, GE, IN, NOTIN, SUBSET, NOTSUBSET, SUBSETEQ,
	 * NOTSUBSETEQ}
	 * @param tag
	 *            the tag of the relational predicate
	 * @param left
	 *            the left child of the relational predicate
	 * @param right
	 *            the right child of the relational predicate
	 * @param location
	 *            the location of the relational predicate
	 * 
	 * @return a new relational predicate
	 */
	public RelationalPredicate makeRelationalPredicate(int tag,
			Expression left, Expression right, SourceLocation location) {
		return new RelationalPredicate(left, right, tag, location, this);
	}

	/**
	 * Returns a new singleton set.
	 * <p>
	 * {SETEXT}
	 * </p>
	 * @param expression
	 *            the unique member of this singleton set
	 * @param location
	 *            the location of the set extension
	 * @return a new set extension
	 */
	public SetExtension makeSetExtension(Expression expression, SourceLocation location) {
		return new SetExtension(expression, location, this);
	}

	/**
	 * Returns a new set extension
	 * <p>
	 * {SETEXT}
	 * </p>
	 * @param expressions
	 *            the children of the set extension
	 * @param location
	 *            the location of the set extension
	 * @return a new set extension
	 */
	public SetExtension makeSetExtension(Expression[] expressions, SourceLocation location) {
		return new SetExtension(expressions, location, this);
	}

	/**
	 * Returns a new set extension
	 * <p>
	 * {SETEXT}
	 * </p>
	 * 
	 * @param expressions
	 *            the children of the set extension
	 * @param location
	 *            the location of the set extension
	 * @return a new set extension
	 */
	public SetExtension makeSetExtension(List<Expression> expressions, SourceLocation location) {
		return new SetExtension(expressions, location, this);
	}

	/**
	 * Returns a new simple predicate
	 * <p>
	 * {KFINITE}
	 * @param tag
	 *            the tag of the simple predicate
	 * @param child
	 *            the child of the simple predicate
	 * @param location
	 *            the location of the simple predicate
	 * 
	 * @return a new simple predicate
	 */
	public SimplePredicate makeSimplePredicate(int tag, Expression child,
			SourceLocation location) {
		return new SimplePredicate(child, tag, location, this);
	}

	/**
	 * Returns a new empty type environment.
	 * 
	 * @return a new empty type environment
	 */
	public ITypeEnvironment makeTypeEnvironment() {
		return new TypeEnvironment(this);
	}

	/**
	 * Returns a new unary expression
	 * <p>
	 * {KCARD, POW, POW1, KUNION, KINTER, KDOM, KRAN, KPRJ1, KPRJ2, KID, KMIN,
	 * KMAX, CONVERSE, UNMINUS}
	 * @param tag
	 *            the tag of the unary expression
	 * @param child
	 *            the child of the unary expression
	 * @param location
	 *            the location of the unary expression
	 * 
	 * @return a new unary expression
	 */
	public UnaryExpression makeUnaryExpression(int tag, Expression child,
			SourceLocation location) {
		return new UnaryExpression(child, tag, location, this);
	}

	/**
	 * Returns a new unary predicate
	 * <p>
	 * {NOT}
	 * @param tag
	 *            the tag of the unaty predicate
	 * @param child
	 *            the child of the unary predicate
	 * @param location
	 *            the location of the unary predicate
	 * 
	 * @return a new unary predicate
	 */
	public UnaryPredicate makeUnaryPredicate(int tag, Predicate child,
			SourceLocation location) {
		return new UnaryPredicate(child, tag, location, this);
	}
	
	/**
	 * Parses the specified formula and returns the corresponding result.
	 * 
	 * @param formula the formula to be parsed
	 * @return the result of the parse
	 */
	public IParseResult parseAssignment(String formula) {
		ParseResult result = new ParseResult(this);
		Scanner scanner = new Scanner(formula, result);
		Parser parser = new Parser(Assignment.class, scanner, result);
		parser.Parse();
		return parser.getResult();
	}
	
	/**
	 * Parses the specified formula and returns the corresponding result.
	 * 
	 * @param formula the formula to be parsed
	 * @return the result of the parse
	 */
	public IParseResult parseExpression(String formula) {
		ParseResult result = new ParseResult(this);
		Scanner scanner = new Scanner(formula, result);
		Parser parser = new Parser(Expression.class, scanner, result);
		parser.Parse();
		return parser.getResult();
	}
	
	/**
	 * Parses the specified predicate and returns the corresponding result.
	 * 
	 * @param formula the formula to be parsed
	 * @return the result of the parse
	 */
	public IParseResult parsePredicate(String formula) {
		ParseResult result = new ParseResult(this);
		Scanner scanner = new Scanner(formula, result);
		Parser parser = new Parser(Predicate.class, scanner, result);
		parser.Parse();
		return parser.getResult();
	}

	/**
	 * Parses the specified type and returns the corresponding result.
	 * 
	 * @param formula the formula to be parsed
	 * @return the result of the parse
	 */
	public IParseResult parseType(String formula) {
		ParseResult result = new ParseResult(this);
		Scanner scanner = new Scanner(formula, result);
		Parser parser = new Parser(Expression.class, scanner, result);
		parser.Parse();
		if (result.isSuccess()) {
			result.convertToType();
		}
		return result;
	}

	/**
	 * Returns the type which corresponds to the set of booleans.
	 * 
	 * @return the predefined boolean type
	 */
	public BooleanType makeBooleanType() {
		return new BooleanType();
	}

	/**
	 * Creates fresh identifiers corresponding to some bound identifier
	 * declarations and inserts them into the type environment.
	 * <p>
	 * For each bound identifier declaration, a free identifier is created. This
	 * new identifier has a name that does not occur in the given type
	 * environment and is based on the given declaration. It is guaranteed that
	 * not two free identifiers in the result bear the same name.
	 * </p>
	 * <p>
	 * The given bound identifier declarations must be typed. The types are then
	 * stored in the typing environment for the corresponding fresh free
	 * identifier created.
	 * </p>
	 * 
	 * @param boundIdents
	 *            array of bound identifier declarations for which corresponding
	 *            fresh free identifiers should be created. Each declaration
	 *            must be typed.
	 * @param environment
	 *            type environment relative to which fresh free identifiers are
	 *            created and into which they are inserted
	 * @return an array of fresh free identifiers
	 */
	public FreeIdentifier[] makeFreshIdentifiers(BoundIdentDecl[] boundIdents,
			ITypeEnvironment environment) {
		return QuantifiedUtil.resolveIdents(boundIdents, environment, this);
	}

	/**
	 * Returns the type which corresponds to the carrier-set with the given
	 * name.
	 * 
	 * @param name
	 *            name of the type
	 * @return a given type with the given name
	 */
	public GivenType makeGivenType(String name) {
		return new GivenType(name);
	}

	/**
	 * Returns the type which corresponds to the set of all integers.
	 * 
	 * @return the predefined integer type
	 */
	public IntegerType makeIntegerType() {
		return new IntegerType();
	}

	/**
	 * Returns the type corresponding to the power set of the given type.
	 * 
	 * @param base
	 *            the base type to build upon
	 * @return the power set type of the given type
	 */
	public PowerSetType makePowerSetType(Type base) {
		return new PowerSetType(base);
	}

	/**
	 * Returns the type corresponding to the cartesian product of the two given
	 * types.
	 * 
	 * @param left
	 *            the first component of the cartesian product
	 * @param right
	 *            the second component of the cartesian product
	 * @return the product type of the two given types
	 */
	public ProductType makeProductType(Type left, Type right) {
		return new ProductType(left, right);
	}

	/**
	 * Returns the type corresponding to the set of all relations between the
	 * two given types.
	 * <p>
	 * This is a handy method fully equivalent to the call
	 * 
	 * <pre>
	 * makePowerSetType(makeProductType(left, right));
	 * </pre>
	 * 
	 * </p>
	 * 
	 * @param left
	 *            the domain of the relations
	 * @param right
	 *            the range of the relations
	 * @return the relational type between the two given types
	 */
	public PowerSetType makeRelationalType(Type left, Type right) {
		return makePowerSetType(makeProductType(left, right));
	}
	
	/**
	 * Returns whether the given name is a valid identifier name (that is an
	 * identifier name which is not used as a keyword in event-B concrete
	 * syntax).
	 * 
	 * @param name
	 *            the name to test
	 * @return <code>true</code> if the given name is a valid name for an
	 *         identifier
	 */
	public boolean isValidIdentifierName(String name) {
		return Lexer.isValidIdentifierName(name);
	}
	
}

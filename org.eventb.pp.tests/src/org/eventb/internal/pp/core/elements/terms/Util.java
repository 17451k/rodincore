package org.eventb.internal.pp.core.elements.terms;

import static junit.framework.Assert.assertTrue;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.Stack;

import org.eventb.core.ast.BoundIdentDecl;
import org.eventb.core.ast.BoundIdentifier;
import org.eventb.core.ast.Expression;
import org.eventb.core.ast.Formula;
import org.eventb.core.ast.FormulaFactory;
import org.eventb.core.ast.FreeIdentifier;
import org.eventb.core.ast.ITypeCheckResult;
import org.eventb.core.ast.ITypeEnvironment;
import org.eventb.core.ast.Predicate;
import org.eventb.core.ast.RelationalPredicate;
import org.eventb.core.ast.Type;
import org.eventb.internal.pp.core.Level;
import org.eventb.internal.pp.core.Tracer;
import org.eventb.internal.pp.core.elements.ArithmeticLiteral;
import org.eventb.internal.pp.core.elements.AtomicPredicateLiteral;
import org.eventb.internal.pp.core.elements.Clause;
import org.eventb.internal.pp.core.elements.ClauseFactory;
import org.eventb.internal.pp.core.elements.ComplexPredicateLiteral;
import org.eventb.internal.pp.core.elements.EqualityLiteral;
import org.eventb.internal.pp.core.elements.Literal;
import org.eventb.internal.pp.core.elements.PredicateLiteral;
import org.eventb.internal.pp.core.elements.PredicateLiteralDescriptor;
import org.eventb.internal.pp.core.elements.PredicateTable;
import org.eventb.internal.pp.core.elements.Sort;
import org.eventb.internal.pp.core.elements.ArithmeticLiteral.AType;
import org.eventb.internal.pp.core.tracing.IOrigin;
import org.eventb.internal.pp.loader.clause.ClauseBuilder;
import org.eventb.internal.pp.loader.formula.terms.ConstantSignature;
import org.eventb.internal.pp.loader.formula.terms.DivideSignature;
import org.eventb.internal.pp.loader.formula.terms.ExpnSignature;
import org.eventb.internal.pp.loader.formula.terms.IntegerSignature;
import org.eventb.internal.pp.loader.formula.terms.MinusSignature;
import org.eventb.internal.pp.loader.formula.terms.ModSignature;
import org.eventb.internal.pp.loader.formula.terms.PlusSignature;
import org.eventb.internal.pp.loader.formula.terms.TermSignature;
import org.eventb.internal.pp.loader.formula.terms.TimesSignature;
import org.eventb.internal.pp.loader.formula.terms.UnaryMinusSignature;
import org.eventb.internal.pp.loader.formula.terms.VariableHolder;
import org.eventb.internal.pp.loader.formula.terms.VariableSignature;
import org.eventb.internal.pp.loader.predicate.AbstractContext;
import org.eventb.internal.pp.loader.predicate.IIntermediateResult;
import org.eventb.internal.pp.loader.predicate.IntermediateResult;
import org.eventb.internal.pp.loader.predicate.IntermediateResultList;


public class Util {

	private static FormulaFactory ff = FormulaFactory.getDefault();
	private static ClauseFactory cf = ClauseFactory.getDefault();
	
	///////////////
	// AST Rodin //
	///////////////
	public static Expression parseExpression(String expression) {
		return ff.parseExpression(expression).getParsedExpression();
	}
	
	public static Predicate parsePredicate(String predicate) {
		return ff.parsePredicate(predicate).getParsedPredicate();
	}
	
	public static Predicate parsePredicate(String predicate, ITypeEnvironment environment) {
		 Predicate pred = ff.parsePredicate(predicate).getParsedPredicate();
		 final ITypeCheckResult tcResult = pred.typeCheck(environment);
		 assertTrue("TypeCheck failed", tcResult.isSuccess());
		 return pred;
	}

	
	///////////////////////////
	public static ClauseBuilder doPhaseOneAndTwo(String predicate, ITypeEnvironment environment,
			VariableTable table) {
		final AbstractContext context = new AbstractContext();
		final Predicate pred = parsePredicate(predicate, environment);
		context.load(pred, false);
		final ClauseBuilder cBuilder = new ClauseBuilder();
		cBuilder.loadClausesFromContext(context, table);
		return cBuilder;
	}
	
	public static ClauseBuilder doPhaseOneAndTwo(String predicate) {
		final AbstractContext context = new AbstractContext();
		final Predicate pred = parsePredicate(predicate);
		context.load(pred, false);
		final ClauseBuilder cBuilder = new ClauseBuilder();
		cBuilder.loadClausesFromContext(context);
		return cBuilder;
	}
	
	public static VariableSignature mVariable(int uniqueIndex, int index) {
		return mVariable(uniqueIndex, index, new Sort(ff.makeGivenType("A")));
	}
	
	public static VariableSignature mVariable(int varIndex, int index, Sort sort) {
		return new VariableSignature(varIndex, index, sort);
	}
	
	public static ConstantSignature mConstant(String name) {
		return mConstant(name, new Sort(ff.makeGivenType("A")));
	}
	
	public static ConstantSignature mConstant(String name, Sort sort) {
		return new ConstantSignature(name, sort);
	}
	
	public static IntegerSignature mInteger(int value) {
		return new IntegerSignature(BigInteger.valueOf(value));
	}
	
	public static PlusSignature mPlus(TermSignature... children) {
		return new PlusSignature(Arrays.asList(children)); 
	}
	
	public static TimesSignature mTimes(TermSignature... children) {
		return new TimesSignature(Arrays.asList(children)); 
	}
	
	public static MinusSignature mMinus(TermSignature left, TermSignature right) {
		return new MinusSignature(left, right); 
	}
	
	public static DivideSignature mDivide(TermSignature left, TermSignature right) {
		return new DivideSignature(left, right); 
	}

	public static ModSignature mMod(TermSignature left, TermSignature right) {
		return new ModSignature(left, right); 
	}
	
	public static ExpnSignature mExpn(TermSignature left, TermSignature right) {
		return new ExpnSignature(left, right); 
	}
	
	public static UnaryMinusSignature mUnaryMinus(TermSignature child) {
		return new UnaryMinusSignature(child);
	}
	
	public static VariableHolder mVHolder() {
		return new VariableHolder(null);
	}

	public static IIntermediateResult mIR(TermSignature... elements) {
		IntermediateResult result = new IntermediateResult(Arrays.asList(elements));
		return result;
	}
	
	public static IIntermediateResult mIR(IIntermediateResult... elements) {
		IIntermediateResult result = new IntermediateResultList(Arrays.asList(elements));
		return result;
	}

	public static BoundIdentifier mBoundIdentifier(int index) {
		return ff.makeBoundIdentifier(index, null);
	}

	public static BoundIdentifier mBoundIdentifier(int index, Type type) {
		return ff.makeBoundIdentifier(index, null, type);
	}

	public static BoundIdentDecl mBoundIdentDecl(String name, Type type) {
		return ff.makeBoundIdentDecl(name, null, type);
	}

	public static FreeIdentifier mFreeIdentifier(String name) {
		return ff.makeFreeIdentifier(name, null);
	}

	public static FreeIdentifier mFreeIdentifier(String name, Type type) {
		return ff.makeFreeIdentifier(name, null, type);
	}
	
	public static RelationalPredicate mIn(Expression left, Expression right) {
		return ff.makeRelationalPredicate(Formula.IN, left, right, null);
	}
	
	
	private static class DummyOrigin implements IOrigin {
		private Level level;
		
		public DummyOrigin(Level level) {
			this.level = level;
		}
		
		public boolean dependsOnGoal() {
			return false;
		}

		public void getDependencies(Stack<Level> dependencies) {
			// skip
		}

		public boolean isDefinition() {
			return false;
		}

		public void trace(Tracer tracer) {
			// skip
		}

		public Level getLevel() {
			return level;
		}

		public void getDependencies(Set<Level> dependencies) {
			// skip
		}

		public int getDepth() {
			// TODO Auto-generated method stub
			return 0;
		}
	}

	public static Sort mSort(Type type) {
		return new Sort(type);
	}
	
	static Sort A = Util.mSort(ff.makeGivenType("A"));
	static Sort B = Util.mSort(ff.makeGivenType("B"));
	static Sort PA = Util.mSort(ff.makePowerSetType(ff.makeGivenType("A")));
	static Sort PAB = Util.mSort(ff.makePowerSetType(ff.makeProductType(ff
			.makeGivenType("A"), ff.makeGivenType("B"))));
	
	public static Constant cCons(String name, Sort sort) {
		return new Constant(name, sort);
	}
	
	public static IntegerConstant cIntCons(int value) {
		return new IntegerConstant(BigInteger.valueOf(value));
	}
	
	static Constant cCons(String name) {
		return new Constant(name, A);
	}
	
	public static Variable cVar(int index, Sort sort) {
		return new Variable(index, sort);
	}
	
	static Variable cVar(Sort sort) {
		return new Variable(1,sort);
	}
	
	static Variable cVar(int index) {
		return new Variable(index, A);
	}
	
	static Variable cVar() {
		return new Variable(1,A);
	}
	
	public static LocalVariable cFLocVar(int index, Sort sort) {
		return new LocalVariable(index, true, sort);
	}
	
	public static LocalVariable cELocVar(int index, Sort sort) {
		return new LocalVariable(index, false, sort);
	}
	
	static LocalVariable cFLocVar(int index) {
		return new LocalVariable(index, true, A);
	}
	
	static LocalVariable cELocVar(int index) {
		return new LocalVariable(index, false, A);
	}
	
	public static Plus cPlus(Term... terms) {
		return new Plus(Arrays.asList(terms));
	}
	
	public static Times cTimes(Term... terms) {
		return new Times(Arrays.asList(terms));
	}
	
	public static Minus cMinus(Term... terms) {
		return new Minus(Arrays.asList(terms));
	}
	
	public static Divide cDiv(Term... terms) {
		return new Divide(Arrays.asList(terms));
	}
	
	public static UnaryMinus cUnMin(Term... terms) {
		return new UnaryMinus(Arrays.asList(terms));
	}
	
	public static Mod cMod(Term... terms) {
		return new Mod(Arrays.asList(terms));
	}
	
	public static Expn cExpn(Term... terms) {
		return new Expn(Arrays.asList(terms));
	}

	private static final PredicateTable table = new PredicateTable();
	
	// Proposition descriptors
	public static final PredicateLiteralDescriptor d0 = descriptor(0);
	public static final PredicateLiteralDescriptor d1 = descriptor(1);
	public static final PredicateLiteralDescriptor d2 = descriptor(2);
	public static final PredicateLiteralDescriptor d3 = descriptor(3);
	public static final PredicateLiteralDescriptor d4 = descriptor(4);
	
	// Predicate descriptors
	public static final PredicateLiteralDescriptor d0A = descriptor(0, A);
	public static final PredicateLiteralDescriptor d0AA = descriptor(0, A, A);
	public static final PredicateLiteralDescriptor d0APA = descriptor(0, A, PA);
	public static final PredicateLiteralDescriptor d0PAPA = descriptor(0, PA, PA);
	public static final PredicateLiteralDescriptor d0AAA = descriptor(0, A, A, A);
	public static final PredicateLiteralDescriptor d1A = descriptor(1, A);
	public static final PredicateLiteralDescriptor d1AA = descriptor(1, A, A);
	public static final PredicateLiteralDescriptor d1APA = descriptor(1, A, PA);
	public static final PredicateLiteralDescriptor d1ABPAB = descriptor(1, A, B, PAB);
	public static final PredicateLiteralDescriptor d2A = descriptor(2, A);
	public static final PredicateLiteralDescriptor d2AA = descriptor(2, A, A);
	public static final PredicateLiteralDescriptor d3A = descriptor(3, A);

	public static PredicateLiteralDescriptor descriptor(int index, Sort... sorts) {
		final int arity = sorts.length;
		return table.newDescriptor(index, arity, arity, false, Arrays.asList(sorts));
	}

	public static ComplexPredicateLiteral cPred(PredicateLiteralDescriptor descriptor, SimpleTerm... terms) {
		return new ComplexPredicateLiteral(descriptor, true, Arrays.asList(terms));
	}
	
	public static ComplexPredicateLiteral cNotPred(PredicateLiteralDescriptor descriptor, SimpleTerm... terms) {
		return new ComplexPredicateLiteral(descriptor, false, Arrays.asList(terms));
	}
	
	public static PredicateLiteral cProp(int index) {
		return new AtomicPredicateLiteral(descriptor(index), true);
	}
	
	public static PredicateLiteral cNotProp(int index) {
		return new AtomicPredicateLiteral(descriptor(index), false);
	}
	
	public static EqualityLiteral cEqual(SimpleTerm term1, SimpleTerm term2) {
		return new EqualityLiteral(term1, term2, true);
	}
	
	public static EqualityLiteral cNEqual(SimpleTerm term1, SimpleTerm term2) {
		return new EqualityLiteral(term1, term2, false);
	}
	
	public static ArithmeticLiteral cAEqual(Term term1, Term term2) {
		return new ArithmeticLiteral(term1,term2,AType.EQUAL);
	}
	
	public static ArithmeticLiteral cANEqual(Term term1, Term term2) {
		return new ArithmeticLiteral(term1,term2,AType.UNEQUAL);
	}
	
	public static ArithmeticLiteral cLess(Term term1, Term term2) {
		return new ArithmeticLiteral(term1,term2,AType.LESS);
	}
	
	public static ArithmeticLiteral cLE(Term term1, Term term2) {
		return new ArithmeticLiteral(term1,term2,AType.LESS_EQUAL);
	}
	
	protected static Clause TRUE(Level level) {
		return cf.makeTRUE(new DummyOrigin(level));
	}
	
	protected static Clause FALSE(Level level) {
		return cf.makeFALSE(new DummyOrigin(level));
	}
	
	public static Clause newDisjClause(IOrigin origin, List<Literal<?,?>> literals) {
		List<PredicateLiteral> predicates = new ArrayList<PredicateLiteral>();
		List<EqualityLiteral> equalities = new ArrayList<EqualityLiteral>();
		List<ArithmeticLiteral> arithmetic = new ArrayList<ArithmeticLiteral>();
		for (Literal<?,?> literal : literals) {
			if (literal instanceof PredicateLiteral) predicates.add((PredicateLiteral)literal);
			else if (literal instanceof EqualityLiteral) equalities.add((EqualityLiteral)literal);
			else if (literal instanceof ArithmeticLiteral) arithmetic.add((ArithmeticLiteral)literal);
		}
		return cf.makeDisjunctiveClause(origin,predicates,equalities,arithmetic,new ArrayList<EqualityLiteral>());
	}
	
	public static Clause newEqClause(IOrigin origin, List<Literal<?,?>> literals) {
		assert literals.size() > 1;
		
		List<PredicateLiteral> predicates = new ArrayList<PredicateLiteral>();
		List<EqualityLiteral> equalities = new ArrayList<EqualityLiteral>();
		List<ArithmeticLiteral> arithmetic = new ArrayList<ArithmeticLiteral>();
		for (Literal<?,?> literal : literals) {
			if (literal instanceof PredicateLiteral) predicates.add((PredicateLiteral)literal);
			else if (literal instanceof EqualityLiteral) equalities.add((EqualityLiteral)literal);
			else if (literal instanceof ArithmeticLiteral) arithmetic.add((ArithmeticLiteral)literal);
		}
		return cf.makeEquivalenceClause(origin,predicates,equalities,arithmetic,new ArrayList<EqualityLiteral>());
	}
	
	public static Clause cClause(Literal<?,?>... literals) {
		Clause clause = newDisjClause(new DummyOrigin(Level.BASE), mList(literals));
		return clause;
	}
	
	public static Clause cClause(Level level, Literal<?,?>... literals) {
		Clause clause = newDisjClause(new DummyOrigin(level), mList(literals));
		return clause;
	}
	
	public static Clause cClause(List<? extends Literal<?,?>> literals, EqualityLiteral... conditions) {
		List<PredicateLiteral> predicates = new ArrayList<PredicateLiteral>();
		List<EqualityLiteral> equalities = new ArrayList<EqualityLiteral>();
		List<ArithmeticLiteral> arithmetic = new ArrayList<ArithmeticLiteral>();
		for (Literal<?,?> literal : literals) {
			if (literal instanceof PredicateLiteral) predicates.add((PredicateLiteral)literal);
			else if (literal instanceof EqualityLiteral) equalities.add((EqualityLiteral)literal);
			else if (literal instanceof ArithmeticLiteral) arithmetic.add((ArithmeticLiteral)literal);
		}
		return cf.makeDisjunctiveClause(new DummyOrigin(Level.BASE),predicates,equalities,arithmetic,mList(conditions));
	}
	
	public static Clause cEqClause(Literal<?,?>... literals) {
		Clause clause = newEqClause(new DummyOrigin(Level.BASE),mList(literals));
		return clause;
	}
	
	public static Clause cEqClause(Level level, Literal<?,?>... literals) {
		Clause clause = newEqClause(new DummyOrigin(level),mList(literals));
		return clause;
	}
	
	public static Clause cEqClause(List<? extends Literal<?,?>> literals, EqualityLiteral... conditions) {
		List<PredicateLiteral> predicates = new ArrayList<PredicateLiteral>();
		List<EqualityLiteral> equalities = new ArrayList<EqualityLiteral>();
		List<ArithmeticLiteral> arithmetic = new ArrayList<ArithmeticLiteral>();
		for (Literal<?,?> literal : literals) {
			if (literal instanceof PredicateLiteral) predicates.add((PredicateLiteral)literal);
			else if (literal instanceof EqualityLiteral) equalities.add((EqualityLiteral)literal);
			else if (literal instanceof ArithmeticLiteral) arithmetic.add((ArithmeticLiteral)literal);
		}
		return cf.makeEquivalenceClause(new DummyOrigin(Level.BASE),predicates,equalities,arithmetic,mList(conditions));
	}
	
	public static <T> Set<T> mSet(T... elements) {
		return new LinkedHashSet<T>(Arrays.asList(elements));
	}

	public static <T> List<T> mList(T... elements) {
		return new ArrayList<T>(Arrays.asList(elements));
	}
	
	public static <T> T[] mArray(T... elements) {
		return elements;
	}

}

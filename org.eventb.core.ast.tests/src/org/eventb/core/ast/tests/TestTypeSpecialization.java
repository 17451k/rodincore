/*******************************************************************************
 * Copyright (c) 2012, 2021 Systerel and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Systerel - initial API and implementation
 *******************************************************************************/
package org.eventb.core.ast.tests;

import static org.eventb.core.ast.Formula.REL;
import static org.eventb.core.ast.tests.FastFactory.mBinaryExpression;
import static org.eventb.core.ast.tests.FastFactory.mFreeIdentifier;
import static org.eventb.core.ast.tests.FastFactory.mIntegerLiteral;
import static org.eventb.core.ast.tests.FastFactory.mTypeEnvironment;
import static org.eventb.core.ast.tests.FastFactory.mTypeSpecialization;
import static org.eventb.core.ast.tests.datatype.TestDatatypes.MOULT_FAC;
import static org.eventb.core.ast.tests.extension.Extensions.EXTS_FAC;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.fail;

import java.util.Set;

import org.eventb.core.ast.Expression;
import org.eventb.core.ast.FormulaFactory;
import org.eventb.core.ast.GivenType;
import org.eventb.core.ast.ISpecialization;
import org.eventb.core.ast.ITypeEnvironment;
import org.eventb.core.ast.ITypeEnvironmentBuilder;
import org.eventb.core.ast.Type;
import org.eventb.core.ast.extension.IFormulaExtension;
import org.junit.Test;

/**
 * Unit tests for specialization of types. We check various combinations of
 * types and specialization to ensure that types are correctly substituted.
 * <p>
 * All tests are of the form of a triplet (original type, specialization, result
 * type).
 * </p>
 * 
 * @author Thomas Muller
 */
public class TestTypeSpecialization extends AbstractTests {

	/*
	 * Set of unused extensions to be used for constructing a different
	 * destination formula factory.
	 */
	private static final Set<IFormulaExtension> OTHER_EXTNS = EXTS_FAC
			.getExtensions();

	/**
	 * Ensures that a given type can be specialized in various ways.
	 */
	@Test 
	public void testGivenType() {
		assertSpecialization("S", "", "S");
		assertSpecialization("S", "S := S", "S");
		assertSpecialization("S", "T := U", "S");
		assertSpecialization("S", "S := T", "T");
		assertSpecialization("S", "S := ℤ", "ℤ");
		assertSpecialization("S", "S := BOOL", "BOOL");
		assertSpecialization("S", "S := ℙ(S)", "ℙ(S)");
	}

	/**
	 * Ensures that the boolean type is never specialized.
	 */
	@Test 
	public void testBooleanType() {
		assertSpecialization("BOOL", "S := T", "BOOL");
	}

	/**
	 * Ensures that the integer type is never specialized.
	 */
	@Test 
	public void testIntegerType() {
		assertSpecialization("ℤ", "S := T", "ℤ");
	}

	/**
	 * Ensures that a power set type can be specialized in various ways.
	 */
	@Test 
	public void testPowerSetType() {
		assertSpecialization("ℙ(S)", "", "ℙ(S)");
		assertSpecialization("ℙ(S)", "S := T", "ℙ(T)");
	}

	/**
	 * Ensures that a product type can be specialized in various ways.
	 */
	@Test 
	public void testProductType() {
		assertSpecialization("S×T", "", "S×T");
		assertSpecialization("S×T", "S := U", "U×T");
		assertSpecialization("S×T", "T := V", "S×V");
		assertSpecialization("S×T", "S := U || T := V", "U×V");
		assertSpecialization("S×T", "S := T", "T×T");
		assertSpecialization("S×T", "S := T || T := S", "T×S");
	}

	/**
	 * Ensures that parametric types can be specialized in various ways.
	 */
	@Test 
	public void testParametricType() {
		assertSpecialization("List(S)", "", "List(S)", LIST_FAC);
		assertSpecialization("List(S)", "S := T", "List(T)", LIST_FAC);

		assertSpecialization("Moult(S,T)", "", "Moult(S,T)", MOULT_FAC);
		assertSpecialization("Moult(S,T)", "S := U", "Moult(U,T)", MOULT_FAC);
		assertSpecialization("Moult(S,T)", "T := V", "Moult(S,V)", MOULT_FAC);
		assertSpecialization("Moult(S,T)", "S := U || T := V", "Moult(U,V)",
				MOULT_FAC);
		assertSpecialization("Moult(S,T)", "S := T", "Moult(T,T)", MOULT_FAC);
		assertSpecialization("Moult(S,T)", "S := T || T := S", "Moult(T,S)",
				MOULT_FAC);
	}

	/**
	 * Ensures that specializing a type remembers the types that are not
	 * substituted.
	 */
	@Test 
	public void bug727() {
		final Type src = parseType("List(S)", LIST_FAC);
		final ISpecialization spe = LIST_FAC.makeSpecialization();
		assertSame(src, src.specialize(spe));
		final GivenType s = LIST_FAC.makeGivenType("S");
		final Type z = LIST_FAC.makeIntegerType();
		try {
			spe.put(s, z);
			fail("Shall have raised an exception");
		} catch (IllegalArgumentException e) {
			// pass
		}

	}

	/**
	 * Ensures that specializing a type which has no substitution translates it
	 * to the factory of the specialization.
	 */
	@Test
	public void bug726() {
		final GivenType src = ff.makeGivenType("S");
		final ISpecialization spe = LIST_FAC.makeSpecialization();
		final Type actual = src.specialize(spe);
		assertEquals(src, actual);
		assertSame(LIST_FAC, actual.getFactory());
	}

	/**
	 * Ensures that specializing a given type which has no substitution prevents
	 * adding later a substitution on the same type.
	 */
	@Test
	public void typeBlocksType() {
		final GivenType src = ff.makeGivenType("S");
		final ISpecialization spe = ff.makeSpecialization();
		src.specialize(spe);

		try {
			spe.put(src, INT_TYPE);
			fail("Shall have raised an exception");
		} catch (IllegalArgumentException e) {
			// pass
		}

		SpecializationChecker.verify(spe, "S=ℙ(S)", "S := S", "S=ℙ(S)");
	}

	/**
	 * Ensures that specializing a given type which has no substitution prevents
	 * adding later a substitution on the identifier denoting that type.
	 */
	@Test
	public void typeBlocksIdent() {
		final GivenType src = ff.makeGivenType("S");
		final ISpecialization spe = ff.makeSpecialization();
		src.specialize(spe);

		try {
			spe.put(src.toExpression(), INT_TYPE.toExpression());
			fail("Shall have raised an exception");
		} catch (IllegalArgumentException e) {
			// pass
		}

		SpecializationChecker.verify(spe, "S=ℙ(S)", "S := S", "S=ℙ(S)");
	}

	/**
	 * Ensures that specializing a given type which has no substitution prevents
	 * adding later a substitution on the same identifier but with a different
	 * type.
	 */
	@Test
	public void typeBlocksIdentDifferentType() {
		final GivenType src = ff.makeGivenType("S");
		final ISpecialization spe = ff.makeSpecialization();
		src.specialize(spe);

		try {
			spe.put(mFreeIdentifier("S", INT_TYPE), mIntegerLiteral());
			fail("Shall have raised an exception");
		} catch (IllegalArgumentException e) {
			// pass
		}

		SpecializationChecker.verify(spe, "S=ℙ(S)", "S := S", "S=ℙ(S)");
	}

	/**
	 * Ensures that specializing a given type which has no substitution cannot
	 * conflict with an existing substitution.
	 */
	@Test
	public void identBlocksTypeSpecialization() {
		final ISpecialization spe = ff.makeSpecialization();
		spe.put(mFreeIdentifier("S", INT_TYPE), mIntegerLiteral(0));

		try {
			ff.makeGivenType("S").specialize(spe);
			fail("Shall have raised an exception");
		} catch (IllegalArgumentException e) {
			// pass
		}

		SpecializationChecker.verify(spe, "S=ℤ", "S := 0", "");
	}

	/**
	 * Ensures that specializing a given type which has no substitution prevents
	 * adding later a substitution using the same identifier in its replacement
	 * but with a different type.
	 */
	@Test
	public void typeBlocksDstIdent() {
		final GivenType src = ff.makeGivenType("S");
		final ISpecialization spe = ff.makeSpecialization();
		src.specialize(spe);

		try {
			spe.put(mFreeIdentifier("a", INT_TYPE),
					mFreeIdentifier("S", INT_TYPE));
			fail("Shall have raised an exception");
		} catch (IllegalArgumentException e) {
			// pass
		}

		SpecializationChecker.verify(spe, "S=ℙ(S)", "S := S", "S=ℙ(S)");
	}

	/**
	 * Ensures that specializing a given type which has no substitution raises
	 * an exception if the type name is already used with a different type in
	 * the destination type environment.
	 */
	@Test
	public void dstIdentBlocksType() {
		final ISpecialization spe = ff.makeSpecialization();
		spe.put(mFreeIdentifier("a", INT_TYPE), mFreeIdentifier("T", INT_TYPE));

		final Type src = parseType("S×T");
		try {
			src.specialize(spe);
			fail("Shall have raised an exception");
		} catch (IllegalArgumentException e) {
			// pass
		}

		SpecializationChecker.verify(spe, "a=ℤ", "a := T", "T=ℤ");
	}

	/**
	 * Ensures that specializing a given type which gets substituted works as
	 * expected, even if the type name is already used with a different type in
	 * an identifier substitution.
	 */
	@Test
	public void dstIdentDoesNotBlockType() {
		final ISpecialization spe = ff.makeSpecialization();
		final GivenType src = ff.makeGivenType("S");
		final GivenType dst = ff.makeGivenType("T");
		spe.put(src, dst);
		spe.put(mFreeIdentifier("a", INT_TYPE), mFreeIdentifier("S", INT_TYPE));

		assertEquals(dst, src.specialize(spe));

		SpecializationChecker.verify(spe, //
				"S=ℙ(S); a=ℤ", //
				"S := T || a := S", //
				"T=ℙ(T); S=ℤ");
	}

	/**
	 * Ensures that specializing a given type which gets substituted works as
	 * expected, even if the type name is already used with a different type in
	 * a predicate variable substitution.
	 */
	@Test
	public void dstPredVarDoesNotBlockType() {
		final ISpecialization spe = ff.makeSpecialization();
		final GivenType src = ff.makeGivenType("S");
		final GivenType dst = ff.makeGivenType("T");
		spe.put(src, dst);
		spe.put(ff.makePredicateVariable("$P", null),
				parsePredicate("S=0", mTypeEnvironment()));

		assertEquals(dst, src.specialize(spe));

		SpecializationChecker.verify(spe, //
				"S=ℙ(S)", //
				"S := T || $P := S=0", //
				"T=ℙ(T); S=ℤ");
	}

	/**
	 * Ensures that specializing a type with a relation works.
	 *
	 * The specificity of relations is that they can be expressed with two
	 * different, but equivalent, expressions: T ↔ U and ℙ(T × U). When these type
	 * expressions are converted in types, they both yield the type ℙ(T × U).
	 * Therefore, specializing a type S with an expression like T ↔ U requires
	 * special care to use T ↔ U in expressions and ℙ(T × U) in types.
	 */
	@Test
	public void testRelationType() {
		assertSpecialization("S", "S := T ↔ U", "T ↔ U");
	}

	/**
	 * Ensures that if a relation has been put, a powerset expression that
	 * represents the same type can't replace it.
	 */
	@Test
	public void testRelationTypeThenPowerset() {
		final ISpecialization spe = ff.makeSpecialization();
		final GivenType src = ff.makeGivenType("S");
		final Expression dst = mBinaryExpression(REL,
				ff.makeGivenType("T").toExpression(),
				ff.makeGivenType("U").toExpression());
		spe.put(src, dst);
		try {
			spe.put(src, parseType("ℙ(T × U)"));
			fail("Shall have raised an exception");
		} catch (IllegalArgumentException e) {
			// pass
		}
		SpecializationChecker.verify(spe, "S=ℙ(S)", "S := T ↔ U",
				"T=ℙ(T); U=ℙ(U)");
	}

	/**
	 * Ensures that a relation can't replace a powerset that represents the same
	 * type.
	 */
	@Test
	public void testPowersetTypeThenRelation() {
		final ISpecialization spe = ff.makeSpecialization();
		final GivenType src = ff.makeGivenType("S");
		spe.put(src, parseType("ℙ(T × U)"));
		try {
			final Expression dst = mBinaryExpression(REL,
					ff.makeGivenType("T").toExpression(),
					ff.makeGivenType("U").toExpression());
			spe.put(src, dst);
			fail("Shall have raised an exception");
		} catch (IllegalArgumentException e) {
			// pass
		}
		SpecializationChecker.verify(spe, "S=ℙ(S)", "S := ℙ(T × U)",
				"T=ℙ(T); U=ℙ(U)");
	}

	private static void assertSpecialization(String typeImage,
			String typeSpecializationImage, String expectedImage) {
		assertSpecialization(typeImage, typeSpecializationImage, expectedImage,
				ff);
	}

	private static void assertSpecialization(String typeImage,
			String typeSpecImage, String expectedImage, FormulaFactory fac) {
		final Type type = parseType(typeImage, fac);
		final ITypeEnvironmentBuilder te = fac.makeTypeEnvironment();
		addGivenSets(te, type);
		assertSpecialization(te, type, fac, typeSpecImage, expectedImage);
		final FormulaFactory otherFac = fac.withExtensions(OTHER_EXTNS);
		assertSpecialization(te, type, otherFac, typeSpecImage, expectedImage);
	}

	private static void assertSpecialization(ITypeEnvironment srcTypenv,
			Type srcType, FormulaFactory dstFac, String typeSpecImage,
			String expectedImage) {
		final ISpecialization spe = mTypeSpecialization(srcTypenv,
				typeSpecImage, dstFac);
		final Type expected = parseType(expectedImage, dstFac);
		final Type actual = srcType.specialize(spe);
		assertEquals(expected, actual);
		// If the specialization did not change the type nor the factory,
		// it should be the same object
		if (expected.equals(srcType)
				&& expected.getFactory() == srcType.getFactory()) {
			assertSame(srcType, actual);
		} else {
			assertSame(expected.getFactory(), actual.getFactory());
		}
	}

	private static void addGivenSets(ITypeEnvironmentBuilder te, Type type) {
		for (final GivenType gt : type.getGivenTypes()) {
			te.addGivenSet(gt.getName());
		}
	}

}

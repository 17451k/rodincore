/*******************************************************************************
 * Copyright (c) 2012 Systerel and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Systerel - initial API and implementation
 *******************************************************************************/
package org.eventb.core.ast;

import java.util.List;

/**
 * Common protocol for translating Event-B formulas containing datatype
 * extensions to plain set theory. Instances of this interface allow to
 * translate several Event-B formulas sharing a common type environment from an
 * instance of the Event-B mathematical language extended with some datatype
 * extensions to the plain Event-B mathematical language.
 * <p>
 * This interface should be used in the following manner:
 * <ul>
 * <li>From the common type environment, create an instance of this interface
 * with method {@link ITypeEnvironment#makeDatatypeTranslation()}.</li>
 * <li>Translate as many formulas as needed using this instance by calling
 * method {@link Formula#translateDatatype(IDatatypeTranslation)}.</li>
 * <li>Optionally, retrieve the formula factory of the translated formulas
 * and/or the axioms giving the properties of the fresh identifiers introduced
 * by the translation.</li>
 * </ul>
 * </p>
 * 
 * @noextend This interface is not intended to be extended by clients.
 * @noimplement This interface is not intended to be implemented by clients.
 * 
 * @since 2.7
 */
public interface IDatatypeTranslation {

	/**
	 * Returns the formula factory of the target language.
	 */
	FormulaFactory getTargetFormulaFactory();

	/**
	 * Returns the axioms that specify the properties of the fresh identifiers
	 * (i.e. translated datatypes) introduced by the translation so far.
	 * 
	 * Let the parameterized datatype DT for a given set of formal type
	 * parameters <tt>T1, ... Tn</tt> be defined as follows:
	 * 
	 * <pre>
	 *  DT(T1,...,Tn) ::=
	 *  		c1(d11: ??11,..., d1k1: ??1k1)
	 *          c2(d21: ??21,..., d2k2: ??2k2)
	 *          .
	 *          .
	 *          .
	 *          cm(dm1: ??m1,..., dmkm: ??mkm)
	 * </pre>
	 * 
	 * For each instance of parameterized datatype DT that is translated to a
	 * fresh given set ??, the following axioms are created to characterize it:
	 * 
	 * <ul>
	 * <li>The set constructor <code>DT</code> is translated to a fresh constant
	 * <code>??</code> and the following axiom is added:
	 * 
	 * <pre>
	 * (A) ?? ??? T1 ?? T2 ?? ??? ?? Tn ??? ??
	 * </pre>
	 * 
	 * </li>
	 * <li>each value constructor <code>ci</code> is translated to a fresh
	 * identifier <code>??i</code> and the following axiom is added:
	 * 
	 * <pre>
	 * (B) ??i ??? ??i1 ?? ... ?? ??iki ??? ??
	 * </pre>
	 * 
	 * </li>
	 * <li>the axiom telling that the datatype is partitioned by the
	 * constructors:
	 * 
	 * <pre>
	 * (C) partition(??, ran(??1), ..., ran(??m))
	 * </pre>
	 * 
	 * </li>
	 * <li>each destructor dij is translated to a fresh ??ij and the following
	 * typing axiom is created:
	 * 
	 * <pre>
	 * (D) ??ij ??? ran(??i) ??? ??ij
	 * </pre>
	 * 
	 * </li>
	 * <li>the typing of each value constructor is defined by the following
	 * added axiom:
	 * 
	 * <pre>
	 * (E) ??i1 ??? ... ??? ??iki = ??i???
	 * </pre>
	 * 
	 * </li>
	 * <li>the axiom telling that subsets of the datatype are partitioned by the
	 * constructors restricted to the set parameters:
	 * 
	 * <pre>
	 * (F) ??? t1, t2, ???, tn ?? partition(??[t1 ?? ??? ?? tn],
	 *                                 ??1[??11 ?? ??? ?? ??1k1],
	 *                                 ???,
	 *                                 ??n[??n1 ?? ??? ?? ??nkm])
	 * </pre>
	 * 
	 * where ??iki is obtained by substituting t1, t2, ???, tn for the formal type
	 * parameters T1, T2, ..., Tn in expressions ??iki.</li>
	 * </ul>
	 * 
	 * Moreover, there are three special cases where these predicates are
	 * slightly modified:
	 * <ul>
	 * <li>when there is no type parameter or no destructor, predicates (A) and
	 * (F) are not generated</li>
	 * <li>when a constructor ??i has no destructor, predicates (B), (D), and (E)
	 * are not generated and the singleton "{??i}" replaces "ran(??i)" in (C) and
	 * (F)</li>
	 * <li>when there is only one constructor, (B) becomes ??i ??? ??i1 ?? ... ?? ??iki
	 * ??? ?? and the predicate (D) becomes superfluous</li>
	 * </ul>
	 */
	List<Predicate> getAxioms();

}

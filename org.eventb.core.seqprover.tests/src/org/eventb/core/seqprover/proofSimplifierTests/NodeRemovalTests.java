/*******************************************************************************
 * Copyright (c) 2011, 2012 Systerel and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Systerel - initial API and implementation
 *******************************************************************************/
package org.eventb.core.seqprover.proofSimplifierTests;

import static org.eventb.core.seqprover.tactics.tests.TreeShape.*;
import static org.eventb.core.seqprover.tests.TestLib.genSeq;

import java.util.Arrays;
import java.util.List;

import org.eventb.core.seqprover.tactics.tests.TreeShape;
import org.junit.runners.Parameterized.Parameters;

/**
 * Simplify proof trees containing unneeded steps. No reordering is expected.
 * 
 * @author Nicolas Beauger
 */
public class NodeRemovalTests extends AbstractSimplificationTests {

	public NodeRemovalTests(String sequent, TreeShape initial,
			TreeShape expected) {
		super(sequent, initial, expected);
	}

	@Parameters
	public static List<Object[]> getTestCases() throws Exception {
		return Arrays.<Object[]> asList(
				
				//////////////////
				// 2 nodes test //
				//////////////////
				/**
				 * Proof tree:
				 * 0
				 * 1
				 * Dependencies:
				 * {}
				 * Expected:
				 * 1
				 */
				test("¬¬x=0|- ⊤",
						// initial
						rn(p("¬¬x=0"), "",
								trueGoal()),
						// expected		
						trueGoal()),
						
				/**
				 * Same as above, except for the review.
				 * The hyp is not needed in review input
				 * (as if deselected)
				 * 
				 * Proof tree:
				 * 0
				 * 1
				 * Dependencies:
				 * {}
				 * Expected:
				 * 1
				 */
				test("¬¬x=0|- ⊤",
						// initial
						rn(p("¬¬x=0"), "",
								review(genSeq("|- ⊤"))),
						// expected		
						review(genSeq("|- ⊤"))),
				
				///////////////////
				// 3 nodes tests //
				///////////////////
				
				/**
				 * Proof tree:
				 * 0
				 * 1
				 * 2
				 * Dependencies:
				 * {0->1}
				 * Expected:
				 * 2
				 */
				test("⊥ |- ¬¬x∈{0}",
						// initial
						rn("",
								rm("",
										falseHyp())),
						// expected		
						falseHyp()),
								
				/**
				 * Proof tree:
				 * 0
				 * 1
				 * 2
				 * Dependencies:
				 * {0->2}
				 * Expected:
				 * 0
				 * 2
				 */
				test("x=0 ;; ¬¬y=1|- ¬¬x=0",
						// initial
						rn("",
								rn(p("¬¬y=1"), "",
										hyp())),
						// expected		
						rn("",
								hyp())),
				
				/**
				 * Proof tree:
				 * 0
				 * 1
				 * 2
				 * Dependencies:
				 * {0->1, 0->2}
				 * Expected:
				 * 0
				 * 2
				 */
				test("|- ¬¬x=0 ⇒ ⊤",
						// initial
						impI(
								rn(p("¬¬x=0"), "",
										trueGoal())),
						// expected		
						impI(
								trueGoal())),

				/**
				 * Proof tree:
				 * 0
				 * 1
				 * 2
				 * Dependencies:
				 * {1->2}
				 * Expected:
				 * 1
				 * 2
				 */
				test("¬¬x=0 |- ⊤ ⇒ ⊤",
						// initial
						rn(p("¬¬x=0"), "",
								impI(
										trueGoal())),
						// expected		
						impI(
								trueGoal())),

				/**
				 * Choice of left subproof by default.
				 * 
				 * Proof tree:
				 *  0
				 * 1 2
				 * Dependencies:
				 * {}
				 * Expected:
				 * 1
				 */
				test("⊤ ∨ ⊤ ;; ⊥ |- ⊤",
						// initial
						disjE(p("⊤ ∨ ⊤"),
								trueGoal(),
								falseHyp()),
						// expected		
						trueGoal()),

				/**
				 * Proof tree:
				 *  0
				 * 1 2
				 * Dependencies:
				 * {0->1}
				 * Expected:
				 * 2
				 */
				test("⊥ ∨ ⊤ |- ⊤",
						// initial
						disjE(p("⊥ ∨ ⊤"),
								falseHyp(),
								trueGoal()),
						// expected		
						trueGoal()),

						
				///////////////////
				// 4 nodes tests //
				///////////////////

				/**
				 * Proof tree:
				 * 0
				 * 1
				 * 2
				 * 3
				 * Dependencies:
				 * {1->2}
				 * Expected:
				 * 0
				 * 3
				 */
				test(" ¬¬⊥ |- ¬¬x∈{0}",
						// initial
						rn(p("¬¬⊥"), "",
								rn("",
										rm("",
												falseHyp()))),
						// expected		
						rn(p("¬¬⊥"), "",
								falseHyp())),

				/**
				 * Choice of shortest subproof.
				 * 
				 * Proof tree:
				 *   0
				 *  1 3
				 * 2
				 * Dependencies:
				 * {1->2}
				 * Expected:
				 * 3
				 */
				test(" ⊤ ∨ ⊤ ;; ⊥ |- ⊤ ⇒ ⊤",
						// initial
						disjE(p("⊤ ∨ ⊤"),
								impI(
										trueGoal()),
								falseHyp()),
						// expected		
						falseHyp()),

				/**
				 * Proof tree:
				 *   0
				 *  1 2
				 *     3
				 * Dependencies:
				 * {0->1, 0->3}
				 * Expected:
				 *  0
				 * 1 3
				 */
				test(" ¬¬⊤ ;; x=0 |- ⊤ ∧ x=0",
						// initial
						conjI(
								trueGoal(),
								rn(p("¬¬⊤"), "",
										hyp())),
						// expected
						conjI(
								trueGoal(),
								hyp())),
						
				/**
				 * Proof tree:
				 *   0
				 *  1 2
				 *     3
				 * Dependencies:
				 * {0->1, 0->2, 0->3}
				 * Expected:
				 *  0
				 * 1 3
				 */
				test("⊤ ⇒ ¬¬⊤ ∧ ⊥ |- ⊥",
						// initial
						impE(p("⊤ ⇒ ¬¬⊤ ∧ ⊥"),
								trueGoal(),
								rn(p("¬¬⊤"), "",
										hyp())),
						// expected		
						impE(p("⊤ ⇒ ¬¬⊤ ∧ ⊥"),
								trueGoal(),
								hyp())),
								
				/**
				 * Proof tree:
				 *  0
				 *  1
				 * 2 3
				 * Dependencies:
				 * {0->3, 1->2}
				 * Expected:
				 * 0
				 * 3
				 */
				test("¬¬⊥ |- ⊤ ∧ ⊥",
						// initial
						rn(p("¬¬⊥"), "",
								conjI(
										trueGoal(),
										falseHyp())),
						// expected		
						rn(p("¬¬⊥"), "",
								falseHyp())),
								
				/**
				 * Proof tree:
				 *  0
				 *  1
				 * 2 3
				 * Dependencies:
				 * {0->2, 0->3}
				 * Expected:
				 * 0
				 * 2
				 */
				test("⊤ ∨ ⊤ |- ⊥ ⇒ ⊤",
						// initial
						impI(
								disjE(p("⊤ ∨ ⊤"),
										falseHyp(),
										trueGoal())),
						// expected		
						impI(
								falseHyp())),
								
				/**
				 * Proof tree:
				 *  0
				 *  1
				 * 2 3
				 * Dependencies:
				 * {}
				 * Expected:
				 * 2
				 */
				test("⊥ ;; ⊤ ∨ ⊤ ;; ¬¬⊤ |- ⊤",
						// initial
						rn(p("¬¬⊤"), "",
								disjE(p("⊤ ∨ ⊤"),
										falseHyp(),
										trueGoal())),
						// expected		
						falseHyp()),
				
						
				// TODO ah produces 3 antecedents, one of them remains
				
				///////////////////
				// 5 nodes tests //
				///////////////////

				/**
				 * Double branching: 0 gets deleted, then 2, 3, 4 as a
				 * consequence. 2 has no remaining child to stick.
				 * 
				 * Proof tree:
				 *   0
				 * 1   2
				 *    3 4
				 * Dependencies:
				 * {0->2, 2->3, 2->4}
				 * Expected:
				 * 1
				 */
				test("⊥ ;; x=0 |- ⊥ ∧ (⊤ ∧ x=0)",
						// initial
						conjI(
								falseHyp(),
								conjI(
										trueGoal(),
										hyp())),
						// expected		
						falseHyp())

				);
	}

}

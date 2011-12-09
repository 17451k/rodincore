/*******************************************************************************
 * Copyright (c) 2011 Systerel and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Systerel - initial API and implementation
 *******************************************************************************/
package org.eventb.core.seqprover.proofSimplifierTests;

import static org.eventb.core.seqprover.ProverFactory.makeProofTree;
import static org.eventb.core.seqprover.tests.TestLib.genPred;
import static org.eventb.core.seqprover.tests.TestLib.genSeq;

import org.eventb.core.ast.Predicate;
import org.eventb.core.seqprover.IProofTree;
import org.eventb.core.seqprover.IProverSequent;
import org.eventb.core.seqprover.tactics.tests.TreeShape;
import org.eventb.internal.core.seqprover.proofSimplifier.ProofTreeSimplifier;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

/**
 * Abstract class for tree shape based proof simplification tests.
 * 
 * @author Nicolas Beauger
 * 
 */
@RunWith(Parameterized.class)
public class AbstractSimplificationTests {

	private static IProofTree simplify(IProofTree pt) throws Exception {
		return new ProofTreeSimplifier().simplify(pt, null);
	}

	protected static Predicate p(String predicate) {
		return genPred(predicate);
	}

	protected static Predicate[] p(String... predicates) {
		final Predicate[] result = new Predicate[predicates.length];
		for (int i = 0; i < predicates.length; i++) {
			result[i] = genPred(predicates[i]);
		}
		return result;
	}

	private final String sequent;
	private final TreeShape initial;
	private final TreeShape expected;

	public AbstractSimplificationTests(String sequent, TreeShape initial,
			TreeShape expected) {
		this.sequent = sequent;
		this.initial = initial;
		this.expected = expected;
	}

	private IProofTree genProofTree() {
		final IProverSequent seq = genSeq(sequent);
		final IProofTree pt = makeProofTree(seq, null);
		initial.apply(pt.getRoot());
		return pt;
	}

	@Test
	public void identityTest() throws Exception {
		final IProofTree pt = genProofTree();
		final IProofTree simplified = simplify(pt);
		expected.check(simplified.getRoot());
	}

}

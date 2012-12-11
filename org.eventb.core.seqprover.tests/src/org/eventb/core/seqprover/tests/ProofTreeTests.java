/*******************************************************************************
 * Copyright (c) 2006, 2011 ETH Zurich and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     ETH Zurich - initial API and implementation
 *     Systerel - added used reasoners to proof dependencies
 *******************************************************************************/

package org.eventb.core.seqprover.tests;

import static java.util.Arrays.asList;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNotSame;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;

import java.util.Collections;
import java.util.HashSet;

import org.eventb.core.seqprover.IProofDependencies;
import org.eventb.core.seqprover.IProofTree;
import org.eventb.core.seqprover.IProofTreeNode;
import org.eventb.core.seqprover.IProverSequent;
import org.eventb.core.seqprover.IReasonerDesc;
import org.eventb.core.seqprover.ProverFactory;
import org.eventb.core.seqprover.ProverLib;
import org.eventb.core.seqprover.eventbExtensions.AutoTactics;
import org.eventb.core.seqprover.eventbExtensions.Tactics;
import org.eventb.core.seqprover.tactics.BasicTactics;
import org.eventb.internal.core.seqprover.ReasonerRegistry;
import org.junit.Test;

/**
 * Unit tests for classes ProofTree and ProofTreeNode: basic manipulations of
 * proof trees.
 * 
 * @author Laurent Voisin
 */
public class ProofTreeTests extends AbstractProofTreeTests {	
	
	/**
	 * Ensures that an initial proof tree is open.
	 */
	@Test
	public void testInitialTree() {
		IProverSequent sequent = makeSimpleSequent("⊥");
		IProofTree tree = ProverFactory.makeProofTree(sequent, null);
		
		assertNodeOpen(tree.getRoot());
	}

	/**
	 * Checks consistency of a discharged proof tree.
	 */
	@Test
	public void testDischargedTree() {
		IProverSequent sequent = makeSimpleSequent("⊤ ⇒ ⊤");
		IProofTree tree = ProverFactory.makeProofTree(sequent, null);
		IProofTreeNode root = tree.getRoot();

		Tactics.impI().apply(root, null);
		assertNodePending(root);
		assertNotEmpty(root.getChildNodes());

		IProofTreeNode imp = root.getChildNodes()[0];
		assertSingleton(imp, root.getChildNodes());
		assertNodeOpen(imp);
		
		Tactics.hyp().apply(imp, null);
		assertNodeClosed(imp);
		assertNodeClosed(root);
		assertTrue("Tree is not discharged", tree.isClosed());
		checkTree(tree, root, sequent);
	}

	/**
	 * Checks consistency of a pending proof tree.
	 */
	@Test
	public void testPendingTree() {
		IProverSequent sequent = makeSimpleSequent("⊤ ⇒ ⊥");
		IProofTree tree = ProverFactory.makeProofTree(sequent, null);
		IProofTreeNode root = tree.getRoot();

		Tactics.impI().apply(root, null);
		assertNotEmpty(root.getChildNodes());
		IProofTreeNode imp = root.getChildNodes()[0];
		assertSingleton(imp, root.getChildNodes());
		assertNodeOpen(imp);
		assertNodePending(root);
		
		assertFalse("Tree is discharged", tree.isClosed());
		checkTree(tree, root, sequent);
	}

	/**
	 * Checks consistency of a mixed pending proof tree.
	 */
	@Test
	public void testMixedTree() {
		IProverSequent sequent = makeSimpleSequent("⊤ ⇒ ⊤ ∧ ⊥");
		IProofTree tree = ProverFactory.makeProofTree(sequent, null);
		IProofTreeNode root = tree.getRoot();

		Tactics.impI().apply(root, null);
		assertNotEmpty(root.getChildNodes());
		IProofTreeNode conj = root.getChildNodes()[0];
		assertSingleton(conj, root.getChildNodes());
		assertNodeOpen(conj);
		assertNodePending(root);

		Tactics.conjI().apply(conj, null);
		assertEquals(2, conj.getChildNodes().length);
		IProofTreeNode left = conj.getChildNodes()[0];
		IProofTreeNode right = conj.getChildNodes()[1];
		assertNodeOpen(left);
		assertNodeOpen(right);
		assertNodePending(conj);
		assertNodePending(root);
		
		Tactics.hyp().apply(left, null);
		assertEmpty(left.getChildNodes());
		assertNodeDischarged(left);
		assertNodeOpen(right);
		assertNodePending(conj);
		assertNodePending(root);
		assertFalse("Tree is closed", tree.isClosed());
		
		Tactics.review(1).apply(right, null);
		assertNodeReviewed(right);
		assertNodeDischarged(left);
		assertNodeReviewed(conj);
		assertNodeReviewed(root);
		
		
		assertTrue("Tree is pending", tree.isClosed());
		checkTree(tree, root, sequent);
	}
	
	/**
	 * Checks consistency after applying a rule that fails.
	 */
	@Test
	public void testApplyRuleFailure() {
		IProverSequent sequent = makeSimpleSequent("⊥");
		IProofTree tree = ProverFactory.makeProofTree(sequent, null);
		IProofTreeNode root = tree.getRoot();
		
		Object error = (new AutoTactics.TrueGoalTac()).apply(root, null);
		assertNotNull(error);
		assertNodeOpen(root);
	}

	/**
	 * Checks consistency after pruning a subtree on a pending node.
	 */
	@Test
	public void testPrunePending() {
		IProverSequent sequent = makeSimpleSequent("⊤ ⇒ ⊤ ∧ ⊥");
		IProofTree tree = ProverFactory.makeProofTree(sequent, null);
		IProofTreeNode root = tree.getRoot();

		Tactics.impI().apply(root, null);
		assertEquals(1, root.getChildNodes().length);
		IProofTreeNode imp = root.getChildNodes()[0];

		Tactics.conjI().apply(imp, null);
		assertEquals(2, imp.getChildNodes().length);
		IProofTreeNode left = imp.getChildNodes()[0];
		IProofTreeNode right = imp.getChildNodes()[1];

		// the nodes to prune are part of the same proof tree.
		assertSame(imp.getProofTree(),tree);
		assertSame(left.getProofTree(),tree);
		assertSame(right.getProofTree(),tree);
		
		assertNodePending(root);
		IProofTree[] pruned = root.pruneChildren();
		assertEquals(1, pruned.length);
		assertNodeOpen(root);
		
		// the pruned node is the root of the pruned subtree.
		assertNull(imp.getParent());
		assertSame(imp.getProofTree(),pruned[0]);
		assertSame(left.getProofTree(),imp.getProofTree());
		assertSame(right.getProofTree(),imp.getProofTree());
		// the pruned nodes are not in the original tree
		assertNotSame(imp.getProofTree(),tree);
		assertNotSame(left.getProofTree(),tree);
		assertNotSame(right.getProofTree(),tree);
		
	}

	/**
	 * Checks consistency after pruning a subtree on a discharged node.
	 */
	@Test
	public void testPruneDischarged() {
		IProverSequent sequent = makeSimpleSequent("1=1 ∧ 2=2 ⇒ 1=1 ∧ 2=2");
		IProofTree tree = ProverFactory.makeProofTree(sequent, null);
		IProofTreeNode root = tree.getRoot();

		Tactics.impI().apply(root, null);
		assertEquals(1, root.getChildNodes().length);
		IProofTreeNode imp = root.getChildNodes()[0];

		Tactics.conjI().apply(imp, null);
		assertEquals(2, imp.getChildNodes().length);
		IProofTreeNode left = imp.getChildNodes()[0];
		IProofTreeNode right = imp.getChildNodes()[1];
		
		Tactics.hyp().apply(left, null);
		Tactics.hyp().apply(right, null);
		
		// the nodes to prune are part of the same proof tree.
		assertSame(left.getProofTree(),tree);
		assertSame(right.getProofTree(),tree);
		// their parent node is discharged
		assertNodeClosed(imp);

		IProofTree[] pruned = imp.pruneChildren();
		assertEquals(2, pruned.length);
		assertNodeOpen(imp);
		assertNodePending(root);
		// the pruned nodes are not part of the tree anymore.
		assertNotSame(left.getProofTree(),tree);
		assertNotSame(right.getProofTree(),tree);
		// Pruned nodes are part of some proof tree.
		assertNotNull(left.getProofTree());
		assertNotNull(right.getProofTree());
		
		// they are roots of their own proof trees.
		assertNull(left.getParent());
		assertNull(right.getParent());
		assertSame(left.getProofTree(),pruned[0]);
		assertSame(right.getProofTree(),pruned[1]);
	}
	
	/**
	 * Checks consistency after copying the subtree of a pending node.
	 */
	@Test
	public void testCopyPending() {
		IProverSequent sequent = makeSimpleSequent("⊤ ⇒ ⊤ ∧ ⊥");
		IProofTree tree = ProverFactory.makeProofTree(sequent, null);
		IProofTreeNode root = tree.getRoot();

		Tactics.impI().apply(root, null);
		assertEquals(1, root.getChildNodes().length);
		IProofTreeNode imp = root.getChildNodes()[0];

		Tactics.conjI().apply(imp, null);
		assertEquals(2, imp.getChildNodes().length);
		IProofTreeNode left = imp.getChildNodes()[0];
		IProofTreeNode right = imp.getChildNodes()[1];

		// the nodes to copy are part of the same proof tree.
		assertSame(imp.getProofTree(),tree);
		assertSame(left.getProofTree(),tree);
		assertSame(right.getProofTree(),tree);
		
		assertNodePending(root);
		IProofTree copied = root.copySubTree();
		assertNodePending(copied.getRoot());
		assertTrue(ProverLib.deepEquals(root,copied.getRoot()));
		
		// the copied nodes are not in the original tree
		assertNotSame(imp.getProofTree(),copied);
		assertNotSame(left.getProofTree(),copied);
		assertNotSame(right.getProofTree(),copied);
		
		// Pruning the copied node has no effect on the original tree.
		copied.getRoot().pruneChildren();
		assertFalse(ProverLib.deepEquals(root,copied.getRoot()));
		
	}

	
//	/**
//	 * Checks that grafting a tree with a un-identical sequent results in failure.
//	 */
//	public void testGraftFailure() {
//		IProverSequent sequent = makeSimpleSequent("⊥");
//		IProofTree tree = ProverFactory.makeProofTree(sequent, null);
//		IProofTreeNode treeRoot = tree.getRoot();
//
//		sequent = makeSimpleSequent("⊤");
//		IProofTree graft = ProverFactory.makeProofTree(sequent, null);
//		IProofTreeNode graftRoot = graft.getRoot();
//		
//		Tactics.tautology().apply(graftRoot, null);
//		assertNodeClosed(graftRoot);
//		
//		boolean success = treeRoot.graft(graft);
//		assertFalse(success);
//		
//		// Grafted tree is still discharged
//		assertNodeClosed(graftRoot);
//		
//		// Original tree is still open
//		assertNodeOpen(treeRoot);		
//	}
	
	
//	/**
//	 * Checks consistency after grafting a pending subtree on an open node.
//	 */
//	public void testGraftPending() {
//		IProverSequent sequent = makeSimpleSequent("⊤ ∧ ⊥");
//		IProofTree tree = ProverFactory.makeProofTree(sequent, null);
//		IProofTreeNode treeRoot = tree.getRoot();
//
//		sequent = makeSimpleSequent("⊤ ∧ ⊥");
//		IProofTree graft = ProverFactory.makeProofTree(sequent, null);
//		IProofTreeNode graftRoot = graft.getRoot();
//		
//		Tactics.conjI().apply(graftRoot, null);
//		assertEquals(2, graftRoot.getChildNodes().length);
//		IProofTreeNode ch1 = graftRoot.getChildNodes()[0];
//		IProofTreeNode ch2 = graftRoot.getChildNodes()[1];
//				
//		treeRoot.graft(graft);
//		
//		// Grafted tree is pruned
//		assertNodeOpen(graftRoot);
//		
//		// Children have been grafted
//		assertNotSame(ch1.getProofTree(),graft);
//		assertNotSame(ch2.getProofTree(),graft);
//		assertSame(ch1.getProofTree(),tree);
//		assertSame(ch2.getProofTree(),tree);
//		
//	}

//	/**
//	 * Checks consistency after grafting a discharged subtree on an open node.
//	 */
//	public void testGraftDischarged() {
//		IProverSequent sequent = makeSimpleSequent("⊤ ∧ ⊤");
//		IProofTree tree = ProverFactory.makeProofTree(sequent, null);
//		IProofTreeNode treeRoot = tree.getRoot();
//
//		sequent = makeSimpleSequent("⊤ ∧ ⊤");
//		IProofTree graft = ProverFactory.makeProofTree(sequent, null);
//		IProofTreeNode graftRoot = graft.getRoot();
//		
//		Tactics.conjI().apply(graftRoot, null);
//		assertEquals(2, graftRoot.getChildNodes().length);
//		IProofTreeNode ch1 = graftRoot.getChildNodes()[0];
//		IProofTreeNode ch2 = graftRoot.getChildNodes()[1];
//		Tactics.tautology().apply(ch1, null);
//		Tactics.tautology().apply(ch2, null);
//		assertNodeClosed(graftRoot);
//		
//		treeRoot.graft(graft);
//		
//		// Grafted tree is pruned
//		assertNodeOpen(graftRoot);
//		
//		// Children have been grafted
//		assertNotSame(ch1.getProofTree(),graft);
//		assertNotSame(ch2.getProofTree(),graft);
//		assertSame(ch1.getProofTree(),tree);
//		assertSame(ch2.getProofTree(),tree);
//		assertNodeClosed(treeRoot);
//	}


	// TODO split in four tests below
	/**
	 * Checks that proof dependency information has been properly generated.
	 */
	@Test
	public void testProofDependencies() {
		final IReasonerDesc hypDesc = getDesc("org.eventb.core.seqprover.hyp");
		final IReasonerDesc impIDesc = getDesc("org.eventb.core.seqprover.impI");
		final IReasonerDesc lemmaDesc = getDesc("org.eventb.core.seqprover.cut");
		final IReasonerDesc allIDesc = getDesc("org.eventb.core.seqprover.allI");
		final IReasonerDesc disjEDesc = getDesc("org.eventb.core.seqprover.disjE");
		
		IProverSequent sequent;
		IProofTree proofTree;
		IProofDependencies proofDependencies;
		
		// test getUsedHypotheses
		sequent = TestLib.genSeq("y=2;; x=1 |- x=1");
		proofTree = ProverFactory.makeProofTree(sequent, null);
		Tactics.hyp().apply(proofTree.getRoot(), null);
		proofDependencies = proofTree.getProofDependencies();
		assertTrue(ProverLib.proofReusable(proofDependencies,sequent));
		assertTrue(proofDependencies.getGoal().equals(TestLib.genPred("x=1")));
		assertTrue(proofDependencies.getUsedHypotheses().equals(TestLib.genPreds("x=1")));
		assertTrue(proofDependencies.getUsedFreeIdents().makeBuilder().equals(TestLib.genTypeEnv("x=ℤ")));
		assertTrue(proofDependencies.getIntroducedFreeIdents().size() == 0);
		assertEquals(Collections.singleton(hypDesc),
				proofDependencies.getUsedReasoners());
		
		// test getUsedHypotheses
		sequent = TestLib.genSeq("y=2 ;; x=1 |- x=1 ⇒ x=1");
		proofTree = ProverFactory.makeProofTree(sequent, null);
		Tactics.impI().apply(proofTree.getRoot(), null);
		Tactics.hyp().apply(proofTree.getRoot().getFirstOpenDescendant(), null);
		proofDependencies = proofTree.getProofDependencies();
		assertTrue(ProverLib.proofReusable(proofDependencies,sequent));
		assertTrue(proofDependencies.getGoal().equals(TestLib.genPred("x=1 ⇒ x=1")));
		assertFalse(proofDependencies.getUsedHypotheses().containsAll(TestLib.genPreds("x=1")));
		assertTrue(proofDependencies.getUsedHypotheses().equals(TestLib.genPreds()));
		assertTrue(proofDependencies.getUsedFreeIdents().makeBuilder().equals(TestLib.genTypeEnv("x=ℤ")));
		assertTrue(proofDependencies.getIntroducedFreeIdents().size() == 0);
		assertEquals(new HashSet<IReasonerDesc>(asList(impIDesc, hypDesc)),
				proofDependencies.getUsedReasoners());
		
		
		// test getUsedFreeIdents
		sequent = TestLib.genSeq("y=2;; 1=1 |- 1=1");
		proofTree = ProverFactory.makeProofTree(sequent, null);
		Tactics.lemma("y=2").apply(proofTree.getRoot(), null);
		proofDependencies = proofTree.getProofDependencies();
		assertTrue(ProverLib.proofReusable(proofDependencies,sequent));
		assertNull(proofDependencies.getGoal());
		assertTrue(proofDependencies.getUsedHypotheses().isEmpty());
		assertTrue(proofDependencies.getUsedFreeIdents().makeBuilder().equals(TestLib.genTypeEnv("y=ℤ")));
		assertTrue(proofDependencies.getIntroducedFreeIdents().size() == 0);
		assertEquals(Collections.singleton(lemmaDesc),
				proofDependencies.getUsedReasoners());
		
		//	 test getIntroducedFreeIdents
		sequent = TestLib.genSeq("y=2 |- ∀ x· x∈ℤ");
		proofTree = ProverFactory.makeProofTree(sequent, null);
		Tactics.allI().apply(proofTree.getRoot(), null);
		proofDependencies = proofTree.getProofDependencies();
		assertTrue(ProverLib.proofReusable(proofDependencies,sequent));
		assertTrue(proofDependencies.getGoal().equals(TestLib.genPred("∀ x· x∈ℤ")));
		assertTrue(proofDependencies.getUsedHypotheses().equals(TestLib.genPreds()));
		assertTrue(proofDependencies.getUsedFreeIdents().makeBuilder().equals(TestLib.genTypeEnv("")));
		assertTrue(proofDependencies.getIntroducedFreeIdents().contains("x"));
		assertEquals(Collections.singleton(allIDesc),
				proofDependencies.getUsedReasoners());
		
		// test getGoal
		sequent = TestLib.genSeq("1=2 ;; 3=3 ∨ 4=4 |- 1=2");
		proofTree = ProverFactory.makeProofTree(sequent, null);
		Tactics.disjE(TestLib.genPred("3=3 ∨ 4=4")).apply(proofTree.getRoot(),
				null);
		BasicTactics.onPending(0, Tactics.hyp()).apply(proofTree.getRoot(), null);
		
		proofDependencies = proofTree.getProofDependencies();
		assertTrue(ProverLib.proofReusable(proofDependencies,sequent));
		assertNotNull(proofDependencies.getGoal());
		assertEquals(TestLib.genPred("1=2"), proofDependencies.getGoal());
		assertEquals(TestLib.genPreds("1=2", "3=3 ∨ 4=4"),
				proofDependencies.getUsedHypotheses());
		assertTrue(proofDependencies.getUsedFreeIdents().isEmpty());
		assertTrue(proofDependencies.getIntroducedFreeIdents().isEmpty());
		assertEquals(new HashSet<IReasonerDesc>(asList(disjEDesc, hypDesc)),
				proofDependencies.getUsedReasoners());
	}

	private static IReasonerDesc getDesc(String id) {
		return ReasonerRegistry.getReasonerRegistry().getLiveReasonerDesc(id);
	}

	/**
	 * Ensures that the origin of a proof tree is tracked.
	 */
	@Test
	public void testOriginTracking() {
		final String origin = "here";
		final IProverSequent sequent = makeSimpleSequent("⊥");
		final IProofTree tree = ProverFactory.makeProofTree(sequent, origin);
		assertSame(origin, tree.getOrigin());
	}

	/**
	 * Ensures that the origin of a proof tree can be <code>null</code>.
	 */
	@Test
	public void testNullOriginTracking() {
		final IProverSequent sequent = makeSimpleSequent("⊥");
		final IProofTree tree = ProverFactory.makeProofTree(sequent, null);
		assertNull(tree.getOrigin());
	}

}

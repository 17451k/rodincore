package org.eventb.core.prover.tests;

import junit.framework.TestCase;

import org.eventb.core.ast.Predicate;
import org.eventb.core.prover.IExtReasonerOutput;
import org.eventb.core.prover.IExternalReasoner;
import org.eventb.core.prover.Lib;
import org.eventb.core.prover.UnSuccessfulExtReasonerOutput;
import org.eventb.core.prover.externalReasoners.LegacyProvers;
import org.eventb.core.prover.sequent.IProverSequent;

public class LegacyProversTest extends TestCase {
	IExternalReasoner legacyProvers = new LegacyProvers();

	IProverSequent[] success = {
			TestLib.genSeq("x∈ℕ|- x∈ℕ"),
			TestLib.genSeq("1=1 |- 1=1"),
			TestLib.genSeq("1=1 |- 2=2"),
			TestLib.genSeq("x∈ℕ|- x∈ℤ"),
			// TestLib.genSeq("x∈ℤ;; x>0 |- x≠0 "),
			TestLib.genSeq("(∀n·n∈ℕ ⇒ n∈A) |- (∃n·n∈ℕ ∧ n∈A) "),
			TestLib.genSeq("A∈ℙ(ℤ) ;; B∈ℙ(ℤ) ;; x∈A ;; x∈B |- x∈A∩B"),
			// The next sequent is provable by PP, but not ML
			TestLib.genSeq("0 ≤ a ∧ 1 < b |- a mod b < b"),
			TestLib.genSeq("x∈ℕ;; x=z ;; y=z |- x=z")};
	IProverSequent[] failure = {
			TestLib.genSeq("1=1 |- 2=1"),
			TestLib.genSeq("x∈ℤ|- x∈ℕ")
			};

	@Override
	public void setUp(){
	
	}
	
	public void testApply() {	

		Predicate newGoalPredicate;
		for (IProverSequent suceed : success){
			newGoalPredicate = TestLib.chkProofFormat_getNewGoalPred(suceed,legacyProvers,null);
			assertTrue(newGoalPredicate.equals(Lib.True));
		}
		
		IExtReasonerOutput extReaOut;
		for (IProverSequent fail : failure){
			extReaOut = legacyProvers.apply(fail,null);
			assertTrue (extReaOut instanceof UnSuccessfulExtReasonerOutput);
		}
//		
//		assertTrue(newGoalPredicate.equals(newgoal));
//		//System.out.println(newGoalPredicate);
//		
//		I = new Input(impHyp,true);
//		newGoalPredicate = TestLib.chkProofFormat_getNewGoalPred(impEseq,impE,I);
//		
//		assertTrue(newGoalPredicate.equals(newgoalContrap));
//		//System.out.println(newGoalPredicate);
//		
	}
	
}

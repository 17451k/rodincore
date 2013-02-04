/*******************************************************************************
 * Copyright (c) 2005, 2013 ETH Zurich and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     ETH Zurich - initial API and implementation
 *     Systerel - corrected: hid original hyp, added notImpRight hyp (V0)
 *     Systerel - visibility: deselected notImpRight hyp (V1)
 *     Systerel - back to original rule, but hiding the original predicate (V2)
 *     Systerel - factored out code common with ImpE
 *******************************************************************************/
package org.eventb.internal.core.seqprover.eventbExtensions;

import static org.eventb.core.seqprover.ProverFactory.makeAntecedent;

import java.util.Set;

import org.eventb.core.ast.Predicate;
import org.eventb.core.seqprover.IHypAction;
import org.eventb.core.seqprover.IProofRule.IAntecedent;
import org.eventb.core.seqprover.IVersionedReasoner;
import org.eventb.core.seqprover.ProverRule;
import org.eventb.core.seqprover.SequentProver;
import org.eventb.core.seqprover.eventbExtensions.DLib;
import org.eventb.core.seqprover.eventbExtensions.Lib;

/**
 * Generates a proof rule for modus tollens for a given implicative hypothesis.
 * <p>
 * Proof rules generated by this reasoner are not goal dependent.
 * </p>
 * <p>
 * Implementation is similar to the one in {@link ImpE}.
 * </p>
 * 
 * @author Farhad Mehta
 */
public class ModusTollens extends ImpHypothesisReasoner 
									implements IVersionedReasoner {

	public static final String REASONER_ID = SequentProver.PLUGIN_ID + ".mt";
	private static final int VERSION = 2;

	public String getReasonerID() {
		return REASONER_ID;
	}

	@ProverRule("HM")
	@Override
	protected IAntecedent[] getAntecedents(Predicate left, Predicate right,
			IHypAction hideHypAction) {
		

		final Predicate notRight = DLib.makeNeg(right);
		final Predicate notLeft = DLib.makeNeg(left);
		final Set<Predicate> addedHyps = Lib.breakPossibleConjunct(notLeft);
		return new IAntecedent[] {
				makeAntecedent(notRight, null, hideHypAction),
				makeAntecedent(null, addedHyps, hideHypAction) };
	}

	@Override
	protected String getDisplay(Predicate pred) {
		return "⇒ hyp mt (" + pred + ")";
	}

	public int getVersion() {
		return VERSION;
	}

}

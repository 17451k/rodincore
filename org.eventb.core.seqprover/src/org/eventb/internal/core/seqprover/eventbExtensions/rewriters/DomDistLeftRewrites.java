/*******************************************************************************
 * Copyright (c) 2006, 2010 ETH Zurich and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     ETH Zurich - initial API and implementation
 *     Systerel - fixed rules DISTRI_DOMSUB_BUNION_L and DISTRI_DOMSUB_BINTER_L
 *******************************************************************************/
package org.eventb.internal.core.seqprover.eventbExtensions.rewriters;

import java.util.Arrays;

import org.eventb.core.ast.BinaryExpression;
import org.eventb.core.ast.Formula;
import org.eventb.core.ast.FormulaFactory;
import org.eventb.core.ast.IFormulaRewriter;
import org.eventb.core.ast.IPosition;
import org.eventb.core.ast.Predicate;
import org.eventb.core.seqprover.IHypAction;
import org.eventb.core.seqprover.IVersionedReasoner;
import org.eventb.core.seqprover.ProverFactory;
import org.eventb.core.seqprover.SequentProver;

public class DomDistLeftRewrites extends AbstractManualRewrites implements IVersionedReasoner {

	private static final int VERSION = 0;
	
	public static final String REASONER_ID = SequentProver.PLUGIN_ID
			+ ".domDistLeftRewrites";

	public int getVersion() {
		return VERSION;
	}
	
	public String getReasonerID() {
		return REASONER_ID;
	}

	@Override
	protected String getDisplayName(Predicate pred, IPosition position) {
		if (pred == null)
			return "rewrites domain distribution left in goal";
		return "rewrites domain distribution left in hyp ("
				+ pred.getSubFormula(position) + ")";
	}

	@Override
	protected IHypAction getHypAction(Predicate pred, IPosition position) {
		if (pred == null) {
			return null;
		}
		return ProverFactory.makeHideHypAction(Arrays.asList(pred));
	}

	@Override
	protected Predicate rewrite(Predicate pred, IPosition position) {
		IFormulaRewriter rewriter = new DomDistLeftRewriterImpl();
		
		FormulaFactory ff = FormulaFactory.getDefault();
		Formula<?> subFormula = pred.getSubFormula(position);
		
		Formula<?> newSubFormula = null;
		if (subFormula instanceof BinaryExpression) {
			newSubFormula = rewriter
				.rewrite((BinaryExpression) subFormula);
		}
		if (newSubFormula == null)
			return null;
		
		if (newSubFormula == subFormula) // No rewrite occurs
			return null;

		return pred.rewriteSubFormula(position, newSubFormula, ff);
	}

}

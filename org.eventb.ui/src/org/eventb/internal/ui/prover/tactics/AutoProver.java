/*******************************************************************************
 * Copyright (c) 2006, 2010 ETH Zurich and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     ETH Zurich - initial API and implementation
 *******************************************************************************/
package org.eventb.internal.ui.prover.tactics;

import static java.util.Collections.emptyList;
import static java.util.Collections.singletonList;

import java.util.List;

import org.eventb.core.EventBPlugin;
import org.eventb.core.ast.Predicate;
import org.eventb.core.seqprover.IProofTreeNode;
import org.eventb.core.seqprover.ITactic;
import org.eventb.ui.prover.DefaultTacticProvider.DefaultPredicateApplication;
import org.eventb.ui.prover.ITacticApplication;

/**
 * Provider for the "auto-provers" tactic.
 * <ul>
 * <li>Provider ID : <code>org.eventb.ui.autoProver</code></li>
 * <li>Target : global (predicate)</li>
 * <ul>
 */
public class AutoProver extends AbstractHypGoalTacticProvider {

	public static class AutoProverApplication extends
			DefaultPredicateApplication {

		@Override
		public ITactic getTactic(String[] inputs, String globalInput) {
			return EventBPlugin.getAutoTacticPreference()
					.getSelectedComposedTactic();
		}

	}

	@Override
	protected List<ITacticApplication> getApplicationsOnPredicate(
			IProofTreeNode node, Predicate hyp, String globalInput,
			Predicate predicate) {
		if (node.isOpen()) { 
			final ITacticApplication appli = new AutoProverApplication();
			return singletonList(appli);
		}
		return emptyList();
	}

}

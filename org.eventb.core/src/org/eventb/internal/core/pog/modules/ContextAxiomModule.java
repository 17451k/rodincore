/*******************************************************************************
 * Copyright (c) 2006 ETH Zurich.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.eventb.internal.core.pog.modules;

import org.eclipse.core.runtime.CoreException;
import org.eventb.core.ISCAxiom;
import org.eventb.core.pog.state.IContextAxiomTable;
import org.eventb.core.pog.state.IContextHypothesisManager;
import org.eventb.core.pog.state.IHypothesisManager;
import org.eventb.core.pog.state.IPredicateTable;
import org.eventb.core.pog.state.IState;
import org.eventb.core.tool.state.IToolStateRepository;

/**
 * @author Stefan Hallerstede
 *
 */
public class ContextAxiomModule extends PredicateModule<ISCAxiom> {

	/* (non-Javadoc)
	 * @see org.eventb.internal.core.pog.modules.PredicateModule#getHypothesisManager(org.eventb.core.state.IStateRepository)
	 */
	@Override
	protected IHypothesisManager getHypothesisManager(
			IToolStateRepository<IState> repository) throws CoreException {
		return (IContextHypothesisManager) repository.getState(IContextHypothesisManager.STATE_TYPE);
	}

	/* (non-Javadoc)
	 * @see org.eventb.internal.core.pog.modules.PredicateModule#getPredicateTable(org.eventb.core.state.IStateRepository)
	 */
	@Override
	protected IPredicateTable<ISCAxiom> getPredicateTable(
			IToolStateRepository<IState> repository) throws CoreException {
		return (IContextAxiomTable) repository.getState(IContextAxiomTable.STATE_TYPE);
	}

	/* (non-Javadoc)
	 * @see org.eventb.internal.core.pog.modules.PredicateModule#getWDProofObligationDescription()
	 */
	@Override
	protected String getWDProofObligationDescription() {
		return "Well-definedness of Invariant";
	}

	/* (non-Javadoc)
	 * @see org.eventb.internal.core.pog.modules.PredicateModule#getWDProofObligationName(java.lang.String)
	 */
	@Override
	protected String getWDProofObligationName(String elementLabel) {
		return elementLabel + "/WD";
	}

}

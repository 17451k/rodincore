/*******************************************************************************
 * Copyright (c) 2006 ETH Zurich.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.eventb.internal.core.pog.modules;

import org.eventb.core.ISCTheorem;


/**
 * @author Stefan Hallerstede
 *
 */
public abstract class TheoremModule extends PredicateToProveModule<ISCTheorem> {

	@Override
	protected String getProofObligationDescription() {
		return "Theorem";
	}

	@Override
	protected String getProofObligationName(String elementLabel) {
		return elementLabel + "/THM";
	}

	@Override
	protected String getWDProofObligationDescription() {
		return "Well-definedness of Theorem";
	}

	@Override
	protected String getWDProofObligationName(String elementLabel) {
		return elementLabel + "/WD";
	}

}

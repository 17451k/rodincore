/*******************************************************************************
 * Copyright (c) 2006, 2018 ETH Zurich and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     ETH Zurich - initial API and implementation
 *     Systerel - lexicographic variants
 *******************************************************************************/
package org.eventb.internal.core.pog;

import org.eventb.core.ISCVariant;
import org.eventb.core.ast.Expression;
import org.eventb.core.pog.state.IMachineVariantInfo;
import org.eventb.core.tool.IStateType;
import org.eventb.internal.core.tool.state.State;

/**
 * @author Stefan Hallerstede
 *
 */
public class MachineVariantInfo extends State implements IMachineVariantInfo {

	@Override
	public String toString() {
		return varExpression == null ? "null" : varExpression.toString();
	}

	private final Expression varExpression;
	
	private final ISCVariant variant;
	
	@Override
	public Expression getExpression() {
		if (varExpression == null) {
			throw new IndexOutOfBoundsException();
		}
		return varExpression;
	}
	
	@Override
	public ISCVariant getVariant() {
		if (variant == null) {
			throw new IndexOutOfBoundsException();
		}
		return variant;
	}

	@Override
	public IStateType<?> getStateType() {
		return STATE_TYPE;
	}

	public MachineVariantInfo(final Expression expression, final ISCVariant variant) {
		this.varExpression = expression;
		this.variant = variant;
	}

	@Override
	public boolean machineHasVariant() {
		return varExpression != null;
	}

}

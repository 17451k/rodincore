/*******************************************************************************
 * Copyright (c) 2006, 2012 ETH Zurich and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     ETH Zurich - initial API and implementation
 *******************************************************************************/
package org.eventb.internal.core.sc.modules;

import org.eclipse.core.runtime.CoreException;
import org.eventb.core.IPredicateElement;
import org.eventb.core.ast.ITypeEnvironment;
import org.eventb.core.ast.ITypeEnvironmentBuilder;
import org.eventb.core.ast.Type;
import org.eventb.core.sc.state.IIdentifierSymbolInfo;
import org.rodinp.core.IInternalElement;

/**
 * @author Stefan Hallerstede
 * 
 */
public abstract class PredicateWithTypingModule<I extends IPredicateElement>
		extends PredicateModule<I> {

	@Override
	protected boolean updateIdentifierSymbolTable(
			IInternalElement formulaElement,
			ITypeEnvironment inferredEnvironment,
			ITypeEnvironmentBuilder typeEnvironment) throws CoreException {

		ITypeEnvironment.IIterator iterator = inferredEnvironment.getIterator();

		while (iterator.hasNext()) {
			iterator.advance();
			String name = iterator.getName();
			Type type = iterator.getType();

			IIdentifierSymbolInfo symbolInfo = identifierSymbolTable
					.getSymbolInfo(name);

			symbolInfo.setType(type);

		}

		typeEnvironment.addAll(inferredEnvironment);
		return true;
	}

}

/*******************************************************************************
 * Copyright (c) 2006, 2018 ETH Zurich and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     ETH Zurich - initial API and implementation
 *     Systerel - update documentation for label attribute
 *******************************************************************************/
package org.eventb.core;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eventb.core.ast.Expression;
import org.eventb.core.ast.ITypeEnvironment;
import org.rodinp.core.IInternalElementType;
import org.rodinp.core.RodinCore;

/**
 * Common protocol for Event-B SC variants.
 * <p>
 * An SC variant is a variant that has been statically checked. It contains a
 * label that is accessed and manipulated via {@link ILabeledElement} and an
 * expression that is accessed and manipulated via {@link ISCExpressionElement}.
 * This interface itself does not contribute any method.
 * </p>
 *
 * @see ILabeledElement#getLabel()
 * @see ILabeledElement#setLabel(String, IProgressMonitor)
 * @see ISCExpressionElement#getExpression(ITypeEnvironment)
 * @see ISCExpressionElement#setExpression(Expression, IProgressMonitor)
 * 
 * @author Stefan Hallerstede
 * 
 * @since 1.0
 * @noimplement This interface is not intended to be implemented by clients.
 */
public interface ISCVariant extends ILabeledElement, ITraceableElement, ISCExpressionElement {

	IInternalElementType<ISCVariant> ELEMENT_TYPE =
		RodinCore.getInternalElementType(EventBPlugin.PLUGIN_ID + ".scVariant"); //$NON-NLS-1$

}

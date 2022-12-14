/*******************************************************************************
 * Copyright (c) 2006, 2020 ETH Zurich and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     ETH Zurich - initial API and implementation
 *     Systerel - lexicographic variants
 *******************************************************************************/
package org.eventb.core.pog.state;

import org.eventb.core.EventBPlugin;
import org.eventb.core.ISCVariant;
import org.eventb.core.ast.Expression;
import org.eventb.core.pog.POGCore;
import org.eventb.core.tool.IStateType;

/**
 * Protocol for accessing the variants of a machine.
 *
 * @author Stefan Hallerstede
 * @since 1.0
 * @noimplement This interface is not intended to be implemented by clients.
 */
public interface IMachineVariantInfo extends IPOGState {

	final static IStateType<IMachineVariantInfo> STATE_TYPE = 
		POGCore.getToolStateType(EventBPlugin.PLUGIN_ID + ".machineVariantInfo");

	/**
	 * Returns the number of variants of the machine.
	 * 
	 * @return the number of variants of the machine
	 * @since 3.4
	 */
	int count();

	/**
	 * Returns the label of a variant.
	 * 
	 * @param index index of the variant
	 * @return the label of the variant
	 * @throws IndexOutOfBoundsException if index is less than zero or greater than
	 *                                   or equal to <code>count()</code>
	 * @since 3.4
	 */
	String getLabel(int index);
	
	/**
	 * Returns the parsed and type-checked variant expression.
	 * 
	 * This method has been retained for backward compatibility only. It will return
	 * a result only if the machine contains a single variant carrying the default
	 * label.
	 * 
	 * @return the parsed and type-checked variant expression
	 * @throws IndexOutOfBoundsException if there is not exactly one default variant
	 * @deprecated Use {@link #getExpression(int)}
	 */
	@Deprecated
	Expression getExpression();
 	
	/**
	 * Returns the parsed and type-checked variant expression.
	 * 
	 * @param index index of the variant
	 * @return the parsed and type-checked variant expression
	 * @throws IndexOutOfBoundsException if index is less than zero or greater than
	 *                                   or equal to <code>count()</code>
	 * @since 3.4
	 */
	Expression getExpression(int index);
	
 	/**
	 * Returns a handle to the variant.
	 * 
	 * This method has been retained for backward compatibility only. It will return
	 * a result only if the machine contains a single variant carrying the default
	 * label.
	 * 
	 * @return a handle to the variant
	 * @throws IndexOutOfBoundsException if there is no variant
	 * @deprecated Use {@link #getVariant(int)}
	 */
	@Deprecated
	ISCVariant getVariant();

	/**
	 * Returns a handle to the variant.
	 * 
	 * @param index index of the variant
	 * @return a handle to the variant
	 * @throws IndexOutOfBoundsException if index is less than zero or greater than
	 *                                   or equal to <code>count()</code>
	 * @since 3.4
	 */
	ISCVariant getVariant(int index);
	
	/**
	 * Returns the name for a variant PO.
	 * 
	 * @param index index of the variant
	 * @param prefix the prefix of the PO name
	 * @param suffix the suffix of the PO name
	 * @return the name of the PO
	 * @throws IndexOutOfBoundsException if index is less than zero or greater than
	 *                                   or equal to <code>count()</code>
	 * @since 3.4
	 */
	String getPOName(int index, String prefix, String suffix);
	
	/**
	 * Returns whether the machine has a variant. This is shortcut fully equivalent
	 * to <code>count() != 0</code>
	 * 
	 * @return whether the machine has a variant
	 */
	boolean machineHasVariant();

}

/*******************************************************************************
 * Copyright (c) 2006 ETH Zurich.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.eventb.core.pog.state;

import org.eventb.core.EventBPlugin;
import org.eventb.core.ISCVariant;
import org.eventb.core.ast.Expression;

/**
 * @author Stefan Hallerstede
 *
 */
public interface IMachineVariantInfo extends IStatePOG {

	final static String STATE_TYPE = EventBPlugin.PLUGIN_ID + ".machineVariantInfo";

	Expression getExpression();
	
	ISCVariant getVariant();
	
	boolean machineHasVariant();

}

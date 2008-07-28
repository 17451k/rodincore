/*******************************************************************************
 * Copyright (c) 2006 ETH Zurich.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.eventb.core.sc.state;

import org.eventb.internal.core.tool.types.IState;

/**
 * All static checker state components must inherit from this interface.
 * <p>
 * Clients that need to contribute to the SC state repository {@link ISCStateRepository}
 * must implement this interface for all contributed state components.
 * </p>
 * @author Stefan Hallerstede
 *
 */
public interface ISCState extends IState {
  // marker class for static checker state
}

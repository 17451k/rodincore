/*******************************************************************************
 * Copyright (c) 2006, 2014 ETH Zurich and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     ETH Zurich - initial API and implementation
 *******************************************************************************/
package org.eventb.internal.core.tool.state;

import org.eclipse.core.runtime.CoreException;
import org.eventb.core.tool.IState;
import org.eventb.internal.core.Messages;
import org.eventb.internal.core.Util;

/**
 * @author Stefan Hallerstede
 *
 */
public abstract class State implements IState {
	
	private boolean immutable;
	
	public State() {
		immutable = false;
	}
	
	protected final void assertImmutable() throws CoreException {
		if ( ! immutable)
			throw Util.newCoreException(
					Messages.bind(Messages.tool_MutableStateNotUnmodifiableFailure,
							getStateType()));
	}

	protected final void assertMutable() throws CoreException {
		if (immutable)
			throw Util.newCoreException(
					Messages.bind(Messages.tool_ImmutableStateModificationFailure,
							getStateType()));
	}

	/* (non-Javadoc)
	 * @see org.eventb.core.state.IState#isImmutable()
	 */
	@Override
	public final boolean isImmutable() {
		return immutable;
	}

	/* (non-Javadoc)
	 * @see org.eventb.core.state.IState#makeImmutable()
	 */
	@Override
	public void makeImmutable() {
		immutable = true;
	}

}

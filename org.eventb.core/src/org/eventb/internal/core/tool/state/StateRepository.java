/*******************************************************************************
 * Copyright (c) 2006 ETH Zurich.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.eventb.internal.core.tool.state;

import java.util.Hashtable;

import org.eclipse.core.runtime.CoreException;
import org.eventb.core.ast.FormulaFactory;
import org.eventb.core.ast.ITypeEnvironment;
import org.eventb.core.tool.state.IState;
import org.eventb.core.tool.state.IStateRepository;
import org.eventb.core.tool.state.IStateType;
import org.eventb.internal.core.Util;

/**
 * @author Stefan Hallerstede
 *
 */
public abstract class StateRepository<I extends IState> implements IStateRepository<I> {
	
	private CoreException exception;
	
	private boolean DEBUG = false;
	
	public void debug() {
		DEBUG = true;
	}

	public static final int REPOSITORY_SIZE = 117;
	
	private ITypeEnvironment environment;
	
	public StateRepository() {
		if (DEBUG)
			System.out.println("NEW STATE REPOSITORY ##################");
		environment = FormulaFactory.getDefault().makeTypeEnvironment();
		repository = new Hashtable<IStateType<?>, I>(REPOSITORY_SIZE);
		exception = null;
	}
	
	private final Hashtable<IStateType<?>, I> repository;
	
	/* (non-Javadoc)
	 * @see org.eventb.core.sc.IStateRepository#getState(java.lang.String)
	 */
	public final I getState(IStateType<? extends I> stateType) throws CoreException {
		if (exception != null)
			throw exception;
		I state = repository.get(stateType);
		if (DEBUG)
			System.out.print("GET STATE: " + stateType + 
					((state == null) ? " NONE" : 
						(state.isImmutable() ? " IMMUTABLE" : " MUTABLE")));
		if (state == null) {
			if (DEBUG)
				System.out.println(" FAILED");
			throwNewCoreException("Attempt to access uninitialized state in state repository");
		}
		if (DEBUG)
			System.out.println(" OK");
		return state;
	}

	public final ITypeEnvironment getTypeEnvironment() throws CoreException {
		if (exception != null)
			throw exception;
		return environment;
	}

	public final void setTypeEnvironment(ITypeEnvironment environment) throws CoreException {
		if (exception != null)
			throw exception;
		if (environment == null)
			throwNewCoreException("Attempt to create null typenv");
		this.environment = environment;
	}

	public final void setState(I state) throws CoreException {
		if (DEBUG)
			System.out.println("SET STATE: " + state.getStateType());
		if (exception != null)
			throw exception;
		if (state == null)
			throwNewCoreException("Attempt to create null state");
		repository.put(state.getStateType(), state);
	}

	public final void removeState(IStateType<? extends I> stateType) throws CoreException {
		if (exception != null)
			throw exception;
		
		repository.remove(stateType);
	}
	
	private void throwNewCoreException(String m) throws CoreException {
		exception = Util.newCoreException(m);
		throw exception;
	}

}

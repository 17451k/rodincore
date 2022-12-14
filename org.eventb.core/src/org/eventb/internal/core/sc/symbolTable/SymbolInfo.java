/*******************************************************************************
 * Copyright (c) 2006, 2012 ETH Zurich and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     ETH Zurich - initial API and implementation
 *     University of Southampton - redesign of symbol table
 *******************************************************************************/
package org.eventb.internal.core.sc.symbolTable;

import org.eclipse.core.runtime.CoreException;
import org.eventb.core.sc.IMarkerDisplay;
import org.eventb.core.sc.state.ISymbolInfo;
import org.eventb.internal.core.Util;
import org.rodinp.core.IAttributeType;
import org.rodinp.core.IInternalElement;
import org.rodinp.core.IInternalElementType;
import org.rodinp.core.RodinDBException;

/**
 * @author Stefan Hallerstede
 * 
 */
public abstract class SymbolInfo<E extends IInternalElement, T extends IInternalElementType<? extends E>, P extends ISymbolProblem>
		extends AttributedSymbol implements ISymbolInfo<E, T> {

	private final String symbol;

	private boolean error;

	private boolean mutable;

	private final boolean persistent;

	private IAttributeType problemAttributeType;

	private IInternalElement problemElement;

	private T elementType;

	protected final String component;

	private final P conflictProblem;

	public SymbolInfo(String symbol, T elementType, boolean persistent,
			IInternalElement problemElement,
			IAttributeType problemAttributeType, String component,
			P conflictProblem) {
		this.symbol = symbol;
		this.elementType = elementType;
		this.persistent = persistent;
		this.problemElement = problemElement;
		this.problemAttributeType = problemAttributeType;
		this.component = component;
		this.conflictProblem = conflictProblem;
		this.error = false;
		this.mutable = true;
	}

	protected P getConflictProblem() {
		return conflictProblem;
	}

	@Override
	public T getSymbolType() {
		return elementType;
	}

	@Override
	public boolean isPersistent() {
		return persistent;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eventb.core.sc.ISymbolInfo#hasError()
	 */
	@Override
	public final boolean hasError() {
		return error;
	}

	protected void assertMutable() throws CoreException {
		if (mutable)
			return;
		else
			throw Util.newCoreException("Attempt to modify immutable symbol");
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eventb.core.sc.ISymbolInfo#setError()
	 */
	@Override
	public final void setError() throws CoreException {
		assertMutable();
		error = true;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eventb.core.sc.ISymbolInfo#isMutable()
	 */
	@Override
	public final boolean isMutable() {
		return mutable;
	}

	/**
	 * Make this symbol info immutable. It cannot be made mutable again.
	 */
	@Override
	public final void makeImmutable() {
		mutable = false;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eventb.core.sc.ISymbolInfo#getSymbol()
	 */
	@Override
	public final String getSymbol() {
		return symbol;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Comparable#compareTo(T)
	 */
	@Override
	public final int compareTo(ISymbolInfo<?, ?> that) {
		return this.symbol.compareTo(that.getSymbol());
	}

	@Override
	public final boolean equals(Object obj) {
		return obj instanceof SymbolInfo<?, ?, ?>
				&& symbol.equals(((SymbolInfo<?, ?, ?>) obj).getSymbol());
	}

	@Override
	public final int hashCode() {
		return symbol.hashCode();
	}

	@Override
	public final String getComponentName() {
		return component;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return symbol;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eventb.core.sc.symbolTable.ISymbolInfo#getSourceElement()
	 */
	@Override
	public final IInternalElement getProblemElement() {
		return problemElement;
	}

	@Override
	public final void createConflictMarker(IMarkerDisplay markerDisplay)
			throws RodinDBException {
		if (isMutable())
			conflictProblem.createConflictError(this, markerDisplay);
		else
			conflictProblem.createConflictWarning(this, markerDisplay);
	}

	@Override
	public final IAttributeType getProblemAttributeType() {
		return problemAttributeType;
	}

	protected void checkPersistence() throws CoreException {
		if (persistent)
			return;
		else
			throw Util
					.newCoreException("Attempt to create non-persistent symbol");
	}

}

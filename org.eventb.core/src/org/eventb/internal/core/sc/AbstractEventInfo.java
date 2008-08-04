/*******************************************************************************
 * Copyright (c) 2006 ETH Zurich.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.eventb.internal.core.sc;

import java.util.Arrays;
import java.util.Collections;
import java.util.Hashtable;
import java.util.List;

import org.eclipse.core.runtime.CoreException;
import org.eventb.core.IConvergenceElement;
import org.eventb.core.ISCEvent;
import org.eventb.core.ast.Assignment;
import org.eventb.core.ast.FreeIdentifier;
import org.eventb.core.ast.Predicate;
import org.eventb.core.sc.state.IAbstractEventInfo;
import org.eventb.core.tool.IStateType;
import org.rodinp.core.RodinDBException;

/**
 * @author Stefan Hallerstede
 *
 */
public class AbstractEventInfo extends ConvergenceInfo implements IAbstractEventInfo {

	@Override
	public String toString() {
		return getEventLabel();
	}

	@Override
	public void makeImmutable() {
		super.makeImmutable();
		idents = Collections.unmodifiableList(idents);
		guards = Collections.unmodifiableList(guards);
		actions = Collections.unmodifiableList(actions);
	}

	private final String label;
	private Hashtable<String,FreeIdentifier> table;
	private final ISCEvent event;
	private List<FreeIdentifier> idents;
	private List<Predicate> guards;
	private List<Assignment> actions;
	
	/* (non-Javadoc)
	 * @see org.eventb.core.sc.IAbstractEventInfo#getEventLabel()
	 */
	public String getEventLabel() {
		return label;
	}

	/* (non-Javadoc)
	 * @see org.eventb.core.sc.IAbstractEventInfo#getIdentifier(java.lang.String)
	 */
	public FreeIdentifier getParameter(String name) throws CoreException {
		assertImmutable();
		if (table == null) {
			table = new Hashtable<String,FreeIdentifier>(idents.size() * 4 / 3 + 1);
			for (FreeIdentifier identifier : idents) {
				table.put(identifier.getName(), identifier);
			}
		}
		return table.get(name);
	}

	/* (non-Javadoc)
	 * @see org.eventb.core.sc.IAbstractEventInfo#getIdentifiers()
	 */
	public List<FreeIdentifier> getParameters() throws CoreException {
		assertImmutable();
		return idents;
	}

	/* (non-Javadoc)
	 * @see org.eventb.core.sc.IAbstractEventInfo#getGuards()
	 */
	public List<Predicate> getGuards() throws CoreException {
		assertImmutable();
		return guards;
	}

	/* (non-Javadoc)
	 * @see org.eventb.core.sc.IAbstractEventInfo#getActions()
	 */
	public List<Assignment> getActions() throws CoreException {
		assertImmutable();
		return actions;
	}

	public AbstractEventInfo(
			ISCEvent event,
			String label, 
			IConvergenceElement.Convergence convergence,
			FreeIdentifier[] idents, 
			Predicate[] guards, 
			Assignment[] actions) throws RodinDBException {
		super(convergence);
		this.event = event;
		this.label = label;
		this.idents = Arrays.asList(idents);
		this.guards = Arrays.asList(guards);
		this.actions = Arrays.asList(actions);
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		return label.hashCode();
	}

	public int compareTo(IAbstractEventInfo info) {
		return label.compareTo(info.getEventLabel());
	}
	
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		return obj instanceof IAbstractEventInfo 
			&& label.equals(((IAbstractEventInfo) obj).getEventLabel());
	}

	public ISCEvent getEvent() {
		return event;
	}

	public IStateType<?> getStateType() {
		return STATE_TYPE;
	}

}

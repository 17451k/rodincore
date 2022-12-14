/*******************************************************************************
 * Copyright (c) 2008, 2012 University of Southampton and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     University of Southampton - initial API and implementation
 *     Systerel - added machine root information
 *******************************************************************************/
package org.eventb.internal.core.sc;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.eclipse.core.runtime.CoreException;
import org.eventb.core.IMachineRoot;
import org.eventb.core.sc.state.IConcreteEventInfo;
import org.eventb.core.sc.state.IConcreteEventTable;
import org.eventb.core.tool.IStateType;
import org.eventb.internal.core.tool.state.State;

/**
 * @author Stefan Hallerstede
 *
 */
public class ConcreteEventTable extends State implements IConcreteEventTable {

	public ConcreteEventTable(IMachineRoot machineRoot) {
		table = new HashMap<String, IConcreteEventInfo>(31);
		this.machineRoot = machineRoot;
	}

	private Map<String, IConcreteEventInfo> table;
	private final IMachineRoot machineRoot;
	
	/* (non-Javadoc)
	 * @see org.eventb.internal.core.tool.types.IState#getStateType()
	 */
	@Override
	public IStateType<?> getStateType() {
		return STATE_TYPE;
	}

	@Override
	public IConcreteEventInfo getConcreteEventInfo(String symbol)
			throws CoreException {
		return table.get(symbol);
	}

	@Override
	public void addConcreteEventInfo(IConcreteEventInfo info)
			throws CoreException {
		assertMutable();
		assert table.get(info.getEventLabel()) == null;
		table.put(info.getEventLabel(), info);
	}

	@Override
	public Iterator<IConcreteEventInfo> iterator() {
		return table.values().iterator();
	}

	@Override
	public IMachineRoot getMachineRoot() {
		return machineRoot;
	}

}

/*******************************************************************************
 * Copyright (c) 2006, 2008 ETH Zurich and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     ETH Zurich - initial API and implementation
 *     Systerel - separation of file and root element
 *******************************************************************************/
package org.eventb.core.tests.sc;

import org.eventb.core.IContextRoot;
import org.eventb.core.IEvent;
import org.eventb.core.IMachineRoot;
import org.eventb.core.ISCEvent;
import org.eventb.core.ISCInternalContext;
import org.eventb.core.ISCMachineRoot;
import org.eventb.core.ast.ITypeEnvironment;

/**
 * @author Stefan Hallerstede
 *
 */
public class TestVariables extends GenericIdentTest<IMachineRoot, ISCMachineRoot> {
	
	
	
	/**
	 * check type propagation of carrier sets in seeing machine
	 */
	public void testVariables_01() throws Exception {
		IContextRoot con = createContext("con");

		addCarrierSets(con, makeSList("S1"));
		
		con.getRodinFile().save(null, true);
		
		runBuilder();
		
		IMachineRoot mac = createMachine("mac");
		
		addMachineSees(mac, "con");

		addVariables(mac, makeSList("V1"));
		addInvariants(mac, makeSList("I1"), makeSList("V1∈S1"));

		mac.getRodinFile().save(null, true);
		
		runBuilder();
		
		ITypeEnvironment environment = factory.makeTypeEnvironment();
		environment.addGivenSet("S1");
		environment.addName("V1", factory.makeGivenType("S1"));
		
		ISCMachineRoot file = mac.getSCMachineRoot();
		
		ISCInternalContext[] contexts = getInternalContexts(file, 1);
		
		containsCarrierSets(contexts[0], "S1");
		
		containsVariables(file, "V1");
		
		containsInvariants(file, environment, makeSList("I1"), makeSList("V1∈S1"));

		containsMarkers(mac.getRodinFile(), false);
	}
	
	/**
	 * name conflict of variable and seen constant: variable removed!
	 */
	public void testVariables_02() throws Exception {
		IContextRoot con = createContext("con");

		addConstants(con, makeSList("C1"));
		addAxioms(con, makeSList("A1"), makeSList("C1∈BOOL"));
		
		con.getRodinFile().save(null, true);
		
		runBuilder();

		IMachineRoot mac = createMachine("mac");
		
		addMachineSees(mac, "con");

		addVariables(mac, makeSList("C1"));
		addInvariants(mac, makeSList("I1", "I2"), makeSList("C1∈ℕ", "C1=TRUE"));

		mac.getRodinFile().save(null, true);
		
		runBuilder();
		
		ITypeEnvironment environment = factory.makeTypeEnvironment();
		environment.addName("C1", factory.makeBooleanType());
		
		ISCMachineRoot file = mac.getSCMachineRoot();
		
		ISCInternalContext[] contexts = getInternalContexts(file, 1);
		
		containsConstants(contexts[0], "C1");
		
		containsVariables(file);
		
		containsInvariants(file, environment, makeSList("I2"), makeSList("C1=TRUE"));

		hasMarker(mac.getVariables()[0]);
		hasMarker(mac.getInvariants()[0]);
	}
	
	/**
	 * variables and invariants are preserved in refinements
	 */
	public void testVariables_03() throws Exception {
		IMachineRoot abs = createMachine("abs");
		
		addVariables(abs, makeSList("V1"));
		addInvariants(abs, makeSList("I1"), makeSList("V1∈ℕ"));

		abs.getRodinFile().save(null, true);
		
		runBuilder();

		IMachineRoot mac = createMachine("mac");
		
		addMachineRefines(mac, "abs");

		addVariables(mac, makeSList("V1"));

		mac.getRodinFile().save(null, true);
		
		runBuilder();
		
		ITypeEnvironment environment = factory.makeTypeEnvironment();
		environment.addName("V1", factory.makeIntegerType());

		ISCMachineRoot file = mac.getSCMachineRoot();
				
		containsVariables(file, "V1");
		
		containsInvariants(file, environment, makeSList("I1"), makeSList("V1∈ℕ"));

		containsMarkers(mac.getRodinFile(), false);
	}

	/**
	 * A variable that has disappeared and reappears in a later refinement.
	 */
	public void testVariables_04() throws Exception {
		final IMachineRoot m0 = createMachine("m0");
		addVariables(m0, "v1");
		addInvariants(m0, makeSList("I1"), makeSList("v1 ∈ ℕ"));
		addInitialisation(m0, "v1");
		m0.getRodinFile().save(null, true);
		
		final IMachineRoot m1 = createMachine("m1");
		addMachineRefines(m1, "m0");
		addVariables(m1, "v2");
		addInvariants(m1, makeSList("I1"), makeSList("v2 ∈ ℕ"));
		addInitialisation(m1, "v2");
		m1.getRodinFile().save(null, true);

		final IMachineRoot m2 = createMachine("m2");
		addMachineRefines(m2, "m1");
		addVariables(m2, "v1");
		addInvariants(m2, makeSList("I1"), makeSList("v1 ∈ ℕ"));
		addInitialisation(m2, "v1");
		m2.getRodinFile().save(null, true);
		
		runBuilder();

		final ISCMachineRoot m0c = m0.getSCMachineRoot();
		containsVariables(m0c, "v1");
		containsMarkers(m0c.getRodinFile(), false);

		final ISCMachineRoot m1c = m1.getSCMachineRoot();
		containsVariables(m1c, "v1", "v2");
		forbiddenVariables(m1c, "v1");
		containsMarkers(m1c.getRodinFile(), false);
		
		final ISCMachineRoot m2c = m2.getSCMachineRoot();
		containsVariables(m2c, "v1", "v2");
		forbiddenVariables(m2c, "v1", "v2");
		containsMarkers(m2.getRodinFile(), true);
		
		ISCEvent[] events = getSCEvents(m2c, IEvent.INITIALISATION);
		containsActions(events[0], emptyEnv, makeSList(), makeSList());
		
		// TODO should also check that sc reports only that "v1" has disappeared
		// and can't be resurrected.
		hasMarker(m2.getVariables()[0]);
	}
	
	@Override
	protected IGenericSCTest<IMachineRoot, ISCMachineRoot> newGeneric() {
		return new GenericMachineSCTest(this);
	}

}

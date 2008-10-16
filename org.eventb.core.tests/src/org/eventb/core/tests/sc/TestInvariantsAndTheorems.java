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
import org.eventb.core.IMachineRoot;
import org.eventb.core.ISCMachineRoot;
import org.eventb.core.ast.ITypeEnvironment;

/**
 * @author Stefan Hallerstede
 *
 */
public class TestInvariantsAndTheorems extends GenericPredicateTest<IMachineRoot, ISCMachineRoot> {
		
	/**
	 * propagation of carrier set types, partial typing
	 */
	public void testInvariantsAndTheorems_05_carrierSetType() throws Exception {
		IContextRoot con =  createContext("con");
		addCarrierSets(con, "S1");
	
		con.getRodinFile().save(null, true);
		
		runBuilder();

		IMachineRoot mac = createMachine("mac");
		
		ITypeEnvironment typeEnvironment = factory.makeTypeEnvironment();
		typeEnvironment.addGivenSet("S1");

		addMachineSees(mac, "con");
		addVariables(mac, "V1");
		addInvariants(mac, makeSList("I1", "I2"), makeSList("V1∈ℕ∪S1", "V1∈S1"));
	
		mac.getRodinFile().save(null, true);
		
		runBuilder();
		
		ISCMachineRoot file = mac.getSCMachineRoot();
		
		containsInvariants(file, typeEnvironment, makeSList("I2"), makeSList("V1∈S1"));
		
		hasMarker(mac.getInvariants()[0]);
		
	}
	
	/**
	 * more on partial typing (more complex set up)
	 */
	public void testInvariantsAndTheorems_06_partialTyping() throws Exception {
		IContextRoot con =  createContext("con");
		addCarrierSets(con, "S1");
	
		con.getRodinFile().save(null, true);
		
		runBuilder();

		ITypeEnvironment typeEnvironment = factory.makeTypeEnvironment();
		typeEnvironment.addGivenSet("S1");
		typeEnvironment.addName("V1", factory.makeGivenType("S1"));
		
		IMachineRoot mac = createMachine("mac");
		addMachineSees(mac, "con");
		addVariables(mac, "V1");
		addInvariants(mac, 
				makeSList("I1", "I2", "I3", "I4"), 
				makeSList("V1=V1", "V1∈S1", "V1∈{V1}", "S1 ⊆ {V1}"));
	
		mac.getRodinFile().save(null, true);
		
		runBuilder();
		
		ISCMachineRoot file = mac.getSCMachineRoot();
		
		containsInvariants(file, typeEnvironment, 
				makeSList("I2", "I3", "I4"), 
				makeSList("V1∈S1", "V1∈{V1}", "S1 ⊆ {V1}"));
		
		hasMarker(mac.getInvariants()[0]);
	}

	@Override
	protected IGenericSCTest<IMachineRoot, ISCMachineRoot> newGeneric() {
		return new GenericMachineSCTest(this);
	}
	
}

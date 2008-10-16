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
package org.eventb.core.tests.pog;

import org.eventb.core.IEvent;
import org.eventb.core.IMachineRoot;
import org.eventb.core.IPORoot;
import org.eventb.core.IPOSequent;
import org.eventb.core.ast.ITypeEnvironment;

/**
 * @author Stefan Hallerstede
 *
 */
public class TestMachineInit extends EventBPOTest {
	
	private static String init = IEvent.INITIALISATION;

	public void testInit_00() throws Exception {
		IMachineRoot mac = createMachine("mac");

		addVariables(mac, "V1");
		addInvariants(mac, makeSList("I1"), makeSList("V1∈0‥4"));
		addEvent(mac, init, 
				makeSList(), 
				makeSList(), makeSList(), 
				makeSList("A1"), makeSList("V1≔1"));
		
		ITypeEnvironment typeEnvironment = factory.makeTypeEnvironment();
		typeEnvironment.addName("V1", intType);
		
		mac.getRodinFile().save(null, true);
		
		runBuilder();
		
		IPORoot po = mac.getPORoot();
		
		containsIdentifiers(po, "V1");
		
		IPOSequent sequent = getSequent(po, init + "/I1/INV");
		
		sequentHasIdentifiers(sequent, "V1'");
		sequentHasNoHypotheses(sequent);
		sequentHasGoal(sequent, typeEnvironment, "1∈0‥4");
		
	}
	
	public void testInit_01() throws Exception {
		IMachineRoot abs = createMachine("abs");

		addVariables(abs, "V1");
		addInvariants(abs, makeSList("I1"), makeSList("V1∈0‥4"));
		addEvent(abs, init, 
				makeSList(), 
				makeSList(), makeSList(), 
				makeSList("A1"), makeSList("V1≔1"));
		
		ITypeEnvironment typeEnvironment = factory.makeTypeEnvironment();
		typeEnvironment.addName("V1", intType);
		
		abs.getRodinFile().save(null, true);
		
		runBuilder();
		
		IMachineRoot mac = createMachine("mac");

		addMachineRefines(mac, "abs");
		addVariables(mac, "V1");
		addEvent(mac, init, 
				makeSList(), 
				makeSList(), makeSList(), 
				makeSList("A1"), makeSList("V1≔2"));
		
		mac.getRodinFile().save(null, true);
		
		runBuilder();
		
		IPORoot po = mac.getPORoot();
		
		containsIdentifiers(po, "V1");
		
		IPOSequent sequent = getSequent(po, init + "/A1/SIM");
		
		sequentHasIdentifiers(sequent, "V1'");
		sequentHasNoHypotheses(sequent);
		sequentHasGoal(sequent, typeEnvironment, "2=1");
		
	}
	
	/*
	 * Ensures that both abstract and concrete actions of the initialisation are
	 * applied to the glueing invariant.
	 */
	public void testInit_02() throws Exception {
		
		IMachineRoot abs = createMachine("abs");
		addVariables(abs, "x");
		addInvariants(abs, makeSList("I1"), makeSList("x ∈ ℤ"));
		addEvent(abs, init, 
				makeSList(), 
				makeSList(), makeSList(), 
				makeSList("A1"), makeSList("x≔0"));
		ITypeEnvironment typeEnvironment = factory.makeTypeEnvironment();
		typeEnvironment.addName("x", intType);
		abs.getRodinFile().save(null, true);
		runBuilder();
		
		IMachineRoot con = createMachine("con");
		addMachineRefines(con, "abs");
		addVariables(con, "y");
		addInvariants(con, makeSList("I1"), makeSList("y = x + 1"));
		addEvent(con, init,
				makeSList(), 
				makeSList(), makeSList(), 
				makeSList("A1"), makeSList("y ≔ 1"));
		con.getRodinFile().save(null, true);
		runBuilder();
		
		IPORoot po = con.getPORoot();
		IPOSequent sequent = getSequent(po, init + "/I1/INV");
		sequentHasNoHypotheses(sequent);
		sequentHasGoal(sequent, typeEnvironment, "1=0+1");
	}
	
}

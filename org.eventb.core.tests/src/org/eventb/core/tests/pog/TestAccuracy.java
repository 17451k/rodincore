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

import org.eventb.core.IContextRoot;
import org.eventb.core.IEvent;
import org.eventb.core.IMachineRoot;
import org.eventb.core.IPORoot;

/**
 * @author Stefan Hallerstede
 *
 */
public class TestAccuracy extends EventBPOTest {
	
	/**
	 * If a machine is inaccurate, then all invariant and theorem POs are inaccurate.
	 */
	public void testAcc_00() throws Exception {
		IMachineRoot mac = createMachine("mac");
		addInvariants(mac, makeSList("H", "I", "J"), makeSList("0÷9>0", "p>0", "∀x·x÷9>0"));
		addTheorems(mac, makeSList("T"), makeSList("0>0"));
		mac.getRodinFile().save(null, true);
		
		runBuilder();
		
		IPORoot po = mac.getPORoot();
		
		sequentIsNotAccurate(po, "H/WD");
		sequentIsNotAccurate(po, "J/WD");
		sequentIsNotAccurate(po, "T/THM");
	}
	
	/**
	 * If an event is inaccurate, then all POs generated from that event are inaccurate.
	 */
	public void testAcc_01() throws Exception {
		IMachineRoot mac = createMachine("mac");
		addVariables(mac, "m");
		addInvariants(mac, makeSList("H", "I", "J"), makeSList("m>0", "0>0", "∀x·x÷9>0"));
		addInitialisation(mac, "m");
		addEvent(mac, "evt", makeSList(), 
				makeSList("G"), makeSList("p<m"), 
				makeSList("A"), makeSList("m:∣ m'>m"));
		mac.getRodinFile().save(null, true);
		
		runBuilder();
		
		IPORoot po = mac.getPORoot();
		
		sequentIsAccurate(po, "J/WD");
		
		sequentIsNotAccurate(po, "evt/A/FIS");
	}	
	
	/**
	 * If an abstract event is inaccurate, 
	 * then all POs generated from the concrete event are inaccurate.
	 */
	public void testAcc_02() throws Exception {
		IMachineRoot abs = createMachine("abs");
		addEvent(abs, "evt", makeSList(), 
				makeSList("G"), makeSList("p<m"), 
				makeSList(), makeSList());
		abs.getRodinFile().save(null, true);
		
		IMachineRoot mac = createMachine("mac");
		addMachineRefines(mac, "abs");
		addVariables(mac, "m");
		addInvariants(mac, makeSList("H"), makeSList("m>0"));
		addTheorems(mac, makeSList("T"), makeSList("9>9"));
		addInitialisation(mac, "m");
		IEvent evt = addEvent(mac, "evt", makeSList(), 
				makeSList(), makeSList(), 
				makeSList("A"), makeSList("m:∣ m'>m"));
		addEventRefines(evt, "evt");
		mac.getRodinFile().save(null, true);
		
		runBuilder();
		
		IPORoot po = mac.getPORoot();
		
		sequentIsAccurate(po, "T/THM");
		
		sequentIsNotAccurate(po, "evt/A/FIS");
	}	
	
	/**
	 * If an abstract event in a merge is inaccurate, 
	 * then all POs generated from the concrete event are inaccurate.
	 */
	public void testAcc_03() throws Exception {
		IMachineRoot abs =  createMachine("abs");
		addEvent(abs, "evt", makeSList(), 
				makeSList("G"), makeSList("0<0"), 
				makeSList(), makeSList());
		addEvent(abs, "fvt", makeSList(), 
				makeSList("G"), makeSList("p<m"), 
				makeSList(), makeSList());
		abs.getRodinFile().save(null, true);
		
		IMachineRoot mac = createMachine("mac");
		addMachineRefines(mac, "abs");
		addVariables(mac, "m");
		addInvariants(mac, makeSList("H"), makeSList("m>0"));
		addTheorems(mac, makeSList("T"), makeSList("9>9"));
		addInitialisation(mac, "m");
		IEvent evt = addEvent(mac, "evt", makeSList(), 
				makeSList(), makeSList(), 
				makeSList("A"), makeSList("m:∣ m'>m"));
		addEventRefines(evt, "evt", "fvt");
		mac.getRodinFile().save(null, true);
		
		runBuilder();
		
		IPORoot po = mac.getPORoot();
		
		sequentIsAccurate(po, "T/THM");
		
		sequentIsNotAccurate(po, "evt/A/FIS");
	}	
	
	/**
	 * If a context is inaccurate, then all axiom and theorem POs are inaccurate.
	 */
	public void testAcc_04() throws Exception {
		IContextRoot ctx = createContext("ctx");
		addAxioms(ctx, makeSList("H", "I", "J"), makeSList("0÷9>0", "p>0", "∀x·x÷9>0"));
		addTheorems(ctx, makeSList("T"), makeSList("0>0"));
		ctx.getRodinFile().save(null, true);
		
		runBuilder();
		
		IPORoot po = ctx.getPORoot();
		
		sequentIsNotAccurate(po, "H/WD");
		sequentIsNotAccurate(po, "J/WD");
		sequentIsNotAccurate(po, "T/THM");
	}
	
	/**
	 * If an abstract event in a merge is inaccurate, 
	 * then all POs generated from the concrete event are inaccurate.
	 */
	public void testAcc_05() throws Exception {
		IMachineRoot aab = createMachine("aab");
		addEvent(aab, "evt", makeSList(), 
				makeSList("G"), makeSList("0<m"), 
				makeSList(), makeSList());
		aab.getRodinFile().save(null, true);
		
		IMachineRoot abs =  createMachine("abs");
		addMachineRefines(abs, "aab");
		IEvent eet = addEvent(abs, "evt", makeSList(), 
				makeSList("G"), makeSList("9÷9>0"), 
				makeSList(), makeSList());
		addEventRefines(eet, "evt");
		abs.getRodinFile().save(null, true);
		
		IMachineRoot mac =  createMachine("mac");
		addMachineRefines(mac, "abs");
		addVariables(mac, "m");
		addInvariants(mac, makeSList("H"), makeSList("m>0"));
		addTheorems(mac, makeSList("T"), makeSList("9>9"));
		addInitialisation(mac, "m");
		IEvent evt = addEvent(mac, "evt", makeSList(), 
				makeSList(), makeSList(), 
				makeSList("A"), makeSList("m:∣ m'>m"));
		addEventRefines(evt, "evt");
		mac.getRodinFile().save(null, true);
		
		runBuilder();
		
		IPORoot po = abs.getPORoot();
		
		sequentIsNotAccurate(po, "evt/G/WD");
		
		po = mac.getPORoot();
		
		sequentIsAccurate(po, "T/THM");
		
		sequentIsAccurate(po, "evt/A/FIS");
	}	

}

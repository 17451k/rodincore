/*******************************************************************************
 * Copyright (c) 2006, 2012 ETH Zurich and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     ETH Zurich - initial API and implementation
 *     Systerel - separation of file and root element
 *     Universitaet Duesseldorf - added theorem attribute
 *******************************************************************************/
package org.eventb.core.tests.pog;

import static org.eventb.core.tests.pom.POUtil.mTypeEnvironment;

import org.eventb.core.IEvent;
import org.eventb.core.IMachineRoot;
import org.eventb.core.IPORoot;
import org.eventb.core.IPOSequent;
import org.eventb.core.ast.ITypeEnvironmentBuilder;
import org.junit.Test;

/**
 * @author Stefan Hallerstede
 *
 */
public class TestMachineHints extends GenericHintTest<IMachineRoot> {
	
	/**
	 * guard well-definedness hints
	 */
	@Test
	public void testMachineHints_00() throws Exception {
		IMachineRoot mac = createMachine("mac");

		addVariables(mac, "x");
		addInvariants(mac, makeSList("I"), makeSList("x∈0‥4"), false);
		addEvent(mac, "evt", 
				makeSList("y"), 
				makeSList("G", "H"), makeSList("y>x÷x", "x÷x=0"), 
				makeSList(), makeSList());
		
		ITypeEnvironmentBuilder typeEnvironment = mTypeEnvironment("x=ℤ; y=ℤ",
				factory);
		
		saveRodinFileOf(mac);
		
		runBuilder();
		
		IPORoot po = mac.getPORoot();
		
		containsIdentifiers(po, "x");
		
		IPOSequent sequent = getSequent(po, "evt/G/WD");
		
		sequentHasIdentifiers(sequent, "y");
		sequentHasNotSelectionHints(sequent, typeEnvironment, "x∈0‥4");
		
		sequent = getSequent(po, "evt/H/WD");
		
		sequentHasIdentifiers(sequent, "y");
		sequentHasSelectionHints(sequent, typeEnvironment, "y>x÷x");
		sequentHasNotSelectionHints(sequent, typeEnvironment, "x∈0‥4");
	}
		
	/**
	 * action well-definedness and feasilibility hints
	 */
	@Test
	public void testMachineHints_01() throws Exception {
		IMachineRoot mac = createMachine("mac");

		addVariables(mac, "x", "z");
		addInvariants(mac, makeSList("I", "J"), makeSList("x∈0‥4", "z∈BOOL"), false, false);
		addEvent(mac, "evt", 
				makeSList("y"), 
				makeSList("G", "H"), makeSList("y>x", "x=0"), 
				makeSList("S", "T"), makeSList("x≔x÷y", "z:∣z'≠z"));
		
		ITypeEnvironmentBuilder typeEnvironment = mTypeEnvironment("x=ℤ; y=ℤ; z=BOOL",
				factory);
		
		saveRodinFileOf(mac);
		
		runBuilder();
		
		IPORoot po = mac.getPORoot();
		
		containsIdentifiers(po, "x", "z");
		
		IPOSequent sequent = getSequent(po, "evt/S/WD");
		
		sequentHasIdentifiers(sequent, "y", "x'", "z'");
		sequentHasSelectionHints(sequent, typeEnvironment, "y>x", "x=0");
		sequentHasNotSelectionHints(sequent, typeEnvironment, "x∈0‥4", "z∈BOOL");
		
		sequent = getSequent(po, "evt/T/FIS");
		
		sequentHasIdentifiers(sequent, "y", "x'", "z'");
		sequentHasSelectionHints(sequent, typeEnvironment, "y>x", "x=0");
		sequentHasNotSelectionHints(sequent, typeEnvironment, "x∈0‥4", "z∈BOOL");
	}
	
	/**
	 * invariant preservation hints
	 */
	@Test
	public void testMachineHints_02() throws Exception {
		IMachineRoot mac = createMachine("mac");

		addVariables(mac, "x", "z");
		addInvariants(mac, makeSList("I", "J"), makeSList("x∈0‥4", "z∈BOOL∖{TRUE}"), false, false);
		addEvent(mac, "evt", 
				makeSList("y"), 
				makeSList("G", "H"), makeSList("y>x", "x=0"), 
				makeSList("S", "T"), makeSList("x≔y", "z:∣z'≠z"));
		
		ITypeEnvironmentBuilder typeEnvironment = mTypeEnvironment("x=ℤ; y=ℤ; z=BOOL",
				factory);
		
		saveRodinFileOf(mac);
		
		runBuilder();
		
		IPORoot po = mac.getPORoot();
		
		containsIdentifiers(po, "x", "z");
		
		IPOSequent sequent = getSequent(po, "evt/I/INV");
		
		sequentHasIdentifiers(sequent, "y", "x'", "z'");
		sequentHasSelectionHints(sequent, typeEnvironment, "y>x", "x=0", "x∈0‥4");
		sequentHasNotSelectionHints(sequent, typeEnvironment, "z∈BOOL∖{TRUE}");
		
		sequent = getSequent(po, "evt/J/INV");
		
		sequentHasIdentifiers(sequent, "y", "x'", "z'");
		sequentHasSelectionHints(sequent, typeEnvironment, "y>x", "x=0", "z∈BOOL∖{TRUE}");
		sequentHasNotSelectionHints(sequent, typeEnvironment, "x∈0‥4");
	}
	
	/**
	 * guard strengthening hints in refinement
	 */
	@Test
	public void testMachineHints_03() throws Exception {
		IMachineRoot abs = createMachine("abs");
		
		
		addVariables(abs, "x", "z");
		addInvariants(abs, makeSList("I", "J"), makeSList("x∈0‥4", "z∈BOOL∖{TRUE}"), false, false);
		addEvent(abs, "evt", 
				makeSList("y"), 
				makeSList("G", "H"), makeSList("y>x", "x=0"), 
				makeSList(), makeSList());
		
		saveRodinFileOf(abs);
		
		IMachineRoot mac = createMachine("mac");
		addMachineRefines(mac, "abs");
		addVariables(mac, "x", "z");
		IEvent event = addEvent(mac, "evt", 
				makeSList("yc"), 
				makeSList("GC", "HC"), makeSList("yc>1", "x=1"), 
				makeSList(), makeSList());
		addEventRefines(event, "evt");
		addEventWitnesses(event, makeSList("y"), makeSList("yc=y"));
		
		ITypeEnvironmentBuilder typeEnvironment = mTypeEnvironment("x=ℤ; y=ℤ; z=BOOL",
				factory);
		
		saveRodinFileOf(mac);
		
		runBuilder();
		
		IPORoot po = mac.getPORoot();
		
		containsIdentifiers(po, "x", "z");
		
		IPOSequent sequent = getSequent(po, "evt/G/GRD");
		
		sequentHasIdentifiers(sequent, "y", "yc");
		sequentHasSelectionHints(sequent, typeEnvironment, "yc>1", "x=1", "yc=y");
		sequentHasNotSelectionHints(sequent, typeEnvironment, "x∈0‥4", "z∈BOOL∖{TRUE}");
		
		sequent = getSequent(po, "evt/H/GRD");
		
		sequentHasIdentifiers(sequent, "y", "yc");
		sequentHasSelectionHints(sequent, typeEnvironment, "yc>1", "x=1");
		sequentHasNotSelectionHints(sequent, typeEnvironment, "x∈0‥4", "z∈BOOL∖{TRUE}", "yc=y");
	}
	
	/**
	 * simulation hints in refinement
	 */
	@Test
	public void testMachineHints_04() throws Exception {
		IMachineRoot abs = createMachine("abs");
		
		addVariables(abs, "x", "z");
		addInvariants(abs, makeSList("I", "J"), makeSList("x∈0‥4", "z∈BOOL∖{TRUE}"), false, false);
		addEvent(abs, "evt", 
				makeSList("y"), 
				makeSList("G", "H"), makeSList("y>x", "x=0"), 
				makeSList("S", "T"), makeSList("x≔y", "z:∣z'≠z"));
		
		saveRodinFileOf(abs);
		
		IMachineRoot mac = createMachine("mac");
		addMachineRefines(mac, "abs");

		addVariables(mac, "x", "zc");
		addInvariants(mac, makeSList("JC"), makeSList("zc∈BOOL∖{TRUE}"), false);
		IEvent event = addEvent(mac, "evt", 
				makeSList("yc"), 
				makeSList("GC", "HC"), makeSList("yc>1", "x=1"), 
				makeSList("SC", "TC"), makeSList("x≔1", "zc≔TRUE"));
		addEventRefines(event, "evt");
		addEventWitnesses(event, makeSList("y", "z'"), makeSList("yc=y", "z'≠zc'"));
	
		ITypeEnvironmentBuilder typeEnvironment = mTypeEnvironment(
				"x=ℤ; y=ℤ; yc=ℤ; z=BOOL; zc=BOOL; z'=BOOL; zc'=BOOL", factory);
	
		saveRodinFileOf(mac);
		
		runBuilder();
		
		IPORoot po = mac.getPORoot();
		
		containsIdentifiers(po, "x", "z", "zc");
		
		IPOSequent sequent = getSequent(po, "evt/S/SIM");
		
		sequentHasIdentifiers(sequent, "y", "yc", "x'", "z'", "zc'");
		sequentHasSelectionHints(sequent, typeEnvironment, 
				"yc>1", "x=1", "yc=y");
		sequentHasNotSelectionHints(sequent, typeEnvironment, 
				"x∈0‥4", "z∈BOOL∖{TRUE}", "zc∈BOOL∖{TRUE}", "z'≠zc'");
		
		sequent = getSequent(po, "evt/T/SIM");
		
		sequentHasIdentifiers(sequent, "y", "yc", "x'", "z'", "zc'");
		sequentHasSelectionHints(sequent, typeEnvironment, 
				"yc>1", "x=1", "z'≠TRUE");
		sequentHasNotSelectionHints(sequent, typeEnvironment, 
				"x∈0‥4", "z∈BOOL∖{TRUE}", "zc∈BOOL∖{TRUE}", "yc=y");
	}
	
	/**
	 * invariant preservation hints in refinement
	 */
	@Test
	public void testMachineHints_05() throws Exception {
		IMachineRoot abs = createMachine("abs");
		
		addVariables(abs, "x", "z");
		addInvariants(abs, makeSList("I", "J"), makeSList("x∈0‥4", "z∈BOOL∖{TRUE}"), false, false);
		addEvent(abs, "evt", 
				makeSList("y"), 
				makeSList("G", "H"), makeSList("y>x", "x=0"), 
				makeSList("S", "T"), makeSList("x≔y", "z:∣z'≠z"));
		
		saveRodinFileOf(abs);
		
		IMachineRoot mac = createMachine("mac");
		addMachineRefines(mac, "abs");

		addVariables(mac, "x", "zc");
		addInvariants(mac, makeSList("JC", "JD"), makeSList("zc∈BOOL∖{TRUE}", "z≠zc"), false, false);
		IEvent event = addEvent(mac, "evt", 
				makeSList("yc"), 
				makeSList("GC", "HC"), makeSList("yc>1", "x=1"), 
				makeSList("SC", "TC"), makeSList("x≔1", "zc :∣ TRUE≠zc'"));
		addEventRefines(event, "evt");
		addEventWitnesses(event, makeSList("y", "z'"), makeSList("yc=y", "z'≠zc'"));

		ITypeEnvironmentBuilder typeEnvironment = mTypeEnvironment(
				"x=ℤ; y=ℤ; yc=ℤ; z=BOOL; zc=BOOL; z'=BOOL; zc'=BOOL", factory);
		
		saveRodinFileOf(mac);
		
		runBuilder();
		
		IPORoot po = mac.getPORoot();
		
		containsIdentifiers(po, "x", "z", "zc");
		
		IPOSequent sequent = getSequent(po, "evt/JC/INV");
		
		sequentHasIdentifiers(sequent, "y", "yc", "x'", "z'", "zc'");
		sequentHasSelectionHints(sequent, typeEnvironment, 
				"yc>1", "x=1", "TRUE≠zc'", "zc∈BOOL∖{TRUE}");
		sequentHasNotSelectionHints(sequent, typeEnvironment, 
				"x∈0‥4", "z∈BOOL∖{TRUE}", "yc=y", "z≠zc");
		
		sequent = getSequent(po, "evt/JD/INV");
		
		sequentHasIdentifiers(sequent, "y", "yc", "x'", "z'", "zc'");
		sequentHasSelectionHints(sequent, typeEnvironment, 
				"yc>1", "x=1", "z'≠zc'", "TRUE≠zc'", "z≠zc");
		sequentHasNotSelectionHints(sequent, typeEnvironment, 
				"x∈0‥4", "z∈BOOL∖{TRUE}", "zc∈BOOL∖{TRUE}", "yc=y");
	}
	
	/**
	 * invariant preservation hints
	 */
	@Test
	public void testMachineHints_06() throws Exception {
		IMachineRoot mac = createMachine("mac");

		addVariables(mac, "x", "z");
		addInvariants(mac, makeSList("I", "J", "K"), makeSList("x=min(0‥4)", "z∈ℕ→ℕ", "x≤z(x)"), false, false, false);
		addEvent(mac, "evt", 
				makeSList(), 
				makeSList(), makeSList(), 
				makeSList("S", "T"), makeSList("x≔x+1", "z(x)≔z(x)"));
		
		ITypeEnvironmentBuilder typeEnvironment = mTypeEnvironment(
				"x=ℤ; z=ℤ↔ℤ", factory);
		
		saveRodinFileOf(mac);
		
		runBuilder();
		
		IPORoot po = mac.getPORoot();
		
		containsIdentifiers(po, "x", "z");
		
		IPOSequent sequent = getSequent(po, "evt/I/INV");
		
		sequentHasIdentifiers(sequent, "x'", "z'");
		sequentHasSelectionHints(sequent, typeEnvironment, "x=min(0‥4)");
		sequentHasNotSelectionHints(sequent, typeEnvironment, "z∈ℕ→ℕ", "x≤z(x)");
		
		sequent = getSequent(po, "evt/J/INV");
		
		sequentHasIdentifiers(sequent, "x'", "z'");
		sequentHasSelectionHints(sequent, typeEnvironment, "z∈ℕ→ℕ");
		sequentHasNotSelectionHints(sequent, typeEnvironment, "x=min(0‥4)", "x≤z(x)");
		
		sequent = getSequent(po, "evt/K/INV");
		
		sequentHasIdentifiers(sequent, "x'", "z'");
		sequentHasSelectionHints(sequent, typeEnvironment, "x≤z(x)");
		sequentHasNotSelectionHints(sequent, typeEnvironment, "x=min(0‥4)", "z∈ℕ→ℕ");
	}
	
	/**
	 * variant hints for progress proof obligation
	 */
	@Test
	public void testMachineHints_07() throws Exception {
		IMachineRoot mac = createMachine("mac");

		addVariables(mac, "x");
		addInvariants(mac, makeSList("I"), makeSList("x∈0‥4"), false);
		IEvent evt = addEvent(mac, "evt", 
				makeSList("y"), 
				makeSList("G", "H", "K"), makeSList("y>1", "x÷x=0", "x+y=0"), 
				makeSList("A"), makeSList("x ≔ x−1"));
		setConvergent(evt);
		addVariant(mac, "x+1");
		
		ITypeEnvironmentBuilder typeEnvironment = mTypeEnvironment(
				"x=ℤ; y=ℤ", factory);
		
		saveRodinFileOf(mac);
		
		runBuilder();
		
		IPORoot po = mac.getPORoot();
		
		containsIdentifiers(po, "x");
		
		IPOSequent sequent = getSequent(po, "evt/VAR");
		
		sequentHasIdentifiers(sequent, "x'", "y");
		sequentHasSelectionHints(sequent, typeEnvironment, "y>1", "x÷x=0", "x+y=0");
		sequentHasNotSelectionHints(sequent, typeEnvironment, "x∈0‥4");
		
		sequent = getSequent(po, "evt/NAT");
		
		sequentHasIdentifiers(sequent, "x'", "y");
		sequentHasSelectionHints(sequent, typeEnvironment, "y>1", "x÷x=0", "x+y=0");
		sequentHasNotSelectionHints(sequent, typeEnvironment, "x∈0‥4");
	}

	/**
	 * guard theorem hints
	 */
	@Test
	public void testMachineHints_08() throws Exception {
		IMachineRoot mac = createMachine("mac");

		addVariables(mac, "x");
		addInvariants(mac, makeSList("I"), makeSList("x∈0‥4"), false);
		addEvent(mac, "evt", 
				makeSList("y"), 
				makeSList("G", "H"), makeSList("y>x÷x", "x÷x=0"), makeBList(false, true), 
				makeSList(), makeSList());
		
		ITypeEnvironmentBuilder typeEnvironment = mTypeEnvironment(
				"x=ℤ; y=ℤ", factory);
		
		saveRodinFileOf(mac);
		
		runBuilder();
		
		IPORoot po = mac.getPORoot();
		
		containsIdentifiers(po, "x");
		
		IPOSequent sequent = getSequent(po, "evt/H/THM");
		
		sequentHasIdentifiers(sequent, "y");
		sequentHasSelectionHints(sequent, typeEnvironment, "y>x÷x");
		sequentHasNotSelectionHints(sequent, typeEnvironment, "x∈0‥4");
	}

	@Override
	protected IGenericPOTest<IMachineRoot> newGeneric() {
		return new GenericMachinePOTest(this);
	}

}

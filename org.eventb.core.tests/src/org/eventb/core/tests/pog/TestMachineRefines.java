/*******************************************************************************
 * Copyright (c) 2006 ETH Zurich.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.eventb.core.tests.pog;

import org.eventb.core.IEvent;
import org.eventb.core.IMachineFile;
import org.eventb.core.IPOFile;
import org.eventb.core.IPOSequent;
import org.eventb.core.ast.ITypeEnvironment;

/**
 * @author Stefan Hallerstede
 *
 */
public class TestMachineRefines extends EventBPOTest {
	
	/**
	 * rewriting of deterministic action simulation POs
	 */
	public void testRefines_00() throws Exception {
		IMachineFile abs = createMachine("abs");

		addVariables(abs, "V1");
		addInvariants(abs, makeSList("I1"), makeSList("V1∈0‥4"));
		addEvent(abs, "evt", 
				makeSList(), 
				makeSList(), makeSList(), 
				makeSList("A1"), makeSList("V1≔V1+1"));
		
		ITypeEnvironment typeEnvironment = factory.makeTypeEnvironment();
		typeEnvironment.addName("V1", intType);
		
		abs.save(null, true);
		
		runBuilder();
		
		IMachineFile mac = createMachine("mac");

		addMachineRefines(mac, "abs");
		addVariables(mac, "V1");
		IEvent event = addEvent(mac, "evt", 
				makeSList(), 
				makeSList(), makeSList(), 
				makeSList("A1"), makeSList("V1≔V1+2"));
		addEventRefines(event, "evt");
		
		mac.save(null, true);
		
		runBuilder();
		
		IPOFile po = mac.getPOFile();
		
		containsIdentifiers(po, "V1");
		
		IPOSequent sequent = getSequent(po, "evt/A1/SIM");
		
		sequentHasIdentifiers(sequent, "V1'");
		sequentHasHypotheses(sequent, typeEnvironment, "V1∈0‥4");
		sequentHasGoal(sequent, typeEnvironment, "V1+2=V1+1");
		
	}
	
	/**
	 * rewriting of action frame simulation POs
	 */
	public void testRefines_01() throws Exception {
		IMachineFile abs = createMachine("abs");

		addVariables(abs, "V1");
		addInvariants(abs, makeSList("I1"), makeSList("V1∈0‥4"));
		addEvent(abs, "evt", 
				makeSList(), 
				makeSList(), makeSList(), 
				makeSList(), makeSList());
		
		ITypeEnvironment typeEnvironment = factory.makeTypeEnvironment();
		typeEnvironment.addName("V1", intType);
		
		abs.save(null, true);
		
		runBuilder();
		
		IMachineFile mac = createMachine("mac");

		addMachineRefines(mac, "abs");
		addVariables(mac, "V1");
		IEvent event = addEvent(mac, "evt", 
				makeSList(), 
				makeSList(), makeSList(), 
				makeSList("A1"), makeSList("V1≔V1+2"));
		addEventRefines(event, "evt");
		
		mac.save(null, true);
		
		runBuilder();
		
		IPOFile po = mac.getPOFile();
		
		containsIdentifiers(po, "V1");
		
		IPOSequent sequent = getSequent(po, "evt/V1/EQL");
		
		sequentHasIdentifiers(sequent, "V1'");
		sequentHasHypotheses(sequent, typeEnvironment, "V1∈0‥4");
		sequentHasGoal(sequent, typeEnvironment, "V1+2=V1");
		
	}
	
	/**
	 * simulation and invariant preservation using global witnesses
	 */
	public void testRefines_02() throws Exception {
		IMachineFile abs = createMachine("abs");

		addVariables(abs, "V1");
		addInvariants(abs, makeSList("I1"), makeSList("V1∈0‥4"));
		addEvent(abs, "evt", 
				makeSList(), 
				makeSList(), makeSList(), 
				makeSList("A1"), makeSList("V1:∈ℕ"));
		
		ITypeEnvironment typeEnvironment = factory.makeTypeEnvironment();
		typeEnvironment.addName("V1", intType);
		
		abs.save(null, true);
		
		runBuilder();
		
		IMachineFile mac = createMachine("mac");

		addMachineRefines(mac, "abs");
		addVariables(mac, "V2");
		addInvariants(mac, makeSList("I1", "I2"), makeSList("V2∈0‥5", "V2≥V1"));
		IEvent event = addEvent(mac, "evt", 
				makeSList(), 
				makeSList(), makeSList(), 
				makeSList("A1"), makeSList("V2≔V2+2"));
		addEventRefines(event, "evt");
		addEventWitnesses(event, makeSList("V1'"), makeSList("V1'≥V2'"));
		
		mac.save(null, true);
		
		runBuilder();
		
		IPOFile po = mac.getPOFile();
		
		containsIdentifiers(po, "V1", "V2");
		
		IPOSequent sequent = getSequent(po, "evt/I1/INV");
		
		sequentHasIdentifiers(sequent, "V1'", "V2'");
		sequentHasHypotheses(sequent, typeEnvironment, "V1∈0‥4", "V2∈0‥5", "V2≥V1");
		sequentHasGoal(sequent, typeEnvironment, "V2+2∈0‥5");

		sequent = getSequent(po, "evt/I2/INV");
		
		sequentHasIdentifiers(sequent, "V1'", "V2'");
		sequentHasHypotheses(sequent, typeEnvironment, "V1∈0‥4", "V2∈0‥5", "V2≥V1", "V1'≥V2+2");
		sequentHasGoal(sequent, typeEnvironment, "V2+2≥V1'");
		
		sequent = getSequent(po, "evt/A1/SIM");
		
		sequentHasIdentifiers(sequent, "V1'", "V2'");
		sequentHasHypotheses(sequent, typeEnvironment, "V1∈0‥4", "V2∈0‥5", "V2≥V1", "V1'≥V2+2");
		sequentHasGoal(sequent, typeEnvironment, "V1'∈ℕ");
		
	}
	
	/**
	 * simulation and invariant preservation using local witnesses
	 */
	public void testRefines_03() throws Exception {
		IMachineFile abs = createMachine("abs");

		addVariables(abs, "V1", "V2");
		addInvariants(abs, makeSList("I1", "I2"), makeSList("V1∈0‥4", "V2≥6"));
		addEvent(abs, "evt", 
				makeSList("L1"), 
				makeSList("G1"), makeSList("L1∈ℕ∖{0}"), 
				makeSList("A1", "A2"), makeSList("V1≔L1", "V2≔7"));
		
		abs.save(null, true);
		
		runBuilder();
		
		IMachineFile mac = createMachine("mac");

		addMachineRefines(mac, "abs");
		addVariables(mac, "V1X", "V2");
		addInvariants(mac, makeSList("I3"), makeSList("V1X=V1+1"));
		IEvent event = addEvent(mac, "evt", 
				makeSList("L2"), 
				makeSList("L2"), makeSList("L2∈ℕ"), 
				makeSList("A1"), makeSList("V1X≔L2"));
		addEventRefines(event, "evt");
		addEventWitnesses(event, makeSList("L1"), makeSList("L1=L2−1"));
		
		mac.save(null, true);
		
		runBuilder();
		
		IPOFile po = mac.getPOFile();
		
		ITypeEnvironment typeEnvironment = factory.makeTypeEnvironment();
		typeEnvironment.addName("V1", intType);
		typeEnvironment.addName("V2", intType);
		typeEnvironment.addName("V1X", intType);
		typeEnvironment.addName("L1", intType);
		typeEnvironment.addName("L2", intType);
		
		containsIdentifiers(po, "V1", "V1X", "V2");
		
		IPOSequent sequent = getSequent(po, "evt/I3/INV");
		
		sequentHasIdentifiers(sequent, "L1", "L2", "V1'", "V2'", "V1X'");
		sequentHasHypotheses(sequent, typeEnvironment, "V1∈0‥4", "V2≥6", "V1X=V1+1");
		sequentHasGoal(sequent, typeEnvironment, "L2=(L2−1)+1");

	}

	/*
	 * POG attempts to store twice the predicate set "ALLHYP", once for the
	 * well-definedness PO of the guard, and then once at the end of the
	 * machine.
	 */
	public void testRefines_04() throws Exception {
		IMachineFile abs = createMachine("abs");
		addEvent(abs, "evt", 
				makeSList(), 
				makeSList(), makeSList(), 
				makeSList(), makeSList());
		abs.save(null, true);
		
		IMachineFile ref = createMachine("ref");
		addMachineRefines(ref, "abs");
		IEvent event = addEvent(ref, "evt", 
				makeSList(), 
				makeSList("G"), makeSList("0 ≤ min({0})"), 
				makeSList(), makeSList());
		addEventRefines(event, "evt");
		ref.save(null, true);
		runBuilder();
		
		IPOFile po = ref.getPOFile();
		containsIdentifiers(po);
		getSequent(po, "evt/G/WD");
	}

	/*
	 * PO filter: the POG should not generate WD POs for guards when these
	 * conditions have already been proved for the abstract event
	 */
	public void testRefines_05() throws Exception {
		IMachineFile abs = createMachine("abs");
		addEvent(abs, "evt", 
				makeSList("x"), 
				makeSList("G1", "G2", "G3"), makeSList("x ÷ x > x", "1 ÷ x > 1", "2÷x = x"), 
				makeSList(), makeSList());
		abs.save(null, true);
		
		IMachineFile ref = createMachine("ref");
		addMachineRefines(ref, "abs");
		IEvent event = addEvent(ref, "evt", 
				makeSList("x"), 
				makeSList("G1", "G2", "G3", "G4"), makeSList("5 ÷ (x+x) = −1", "1 ÷ x > 1", "x ÷ x > x", "2÷x = x"), 
				makeSList(), makeSList());
		addEventRefines(event, "evt");
		ref.save(null, true);
		runBuilder();
		
		ITypeEnvironment environment = factory.makeTypeEnvironment();
		environment.addName("x", intType);
		
		IPOFile po = ref.getPOFile();
		containsIdentifiers(po);
		
		IPOSequent sequent;
		
		sequent= getSequent(po, "evt/G1/WD");
		sequentHasGoal(sequent, environment, "(x+x)≠0");
		
		sequent= getSequent(po, "evt/G2/WD");
		sequentHasGoal(sequent, environment, "x≠0");
		
		noSequent(po, "evt/G3/WD");
		
		noSequent(po, "evt/G4/WD");
	}

	/*
	 * PO filter: the POG should not generate guard strengthening POs if
	 * all abstract guards are syntactically (but normalised) contained
	 * in the concrete guards
	 */
	public void testRefines_06() throws Exception {
		IMachineFile abs = createMachine("abs");
		addEvent(abs, "evt", 
				makeSList("x"), 
				makeSList("GA1", "GA2", "GA3"), makeSList("x > x", "1 > 1", "x−1∈ℕ"), 
				makeSList(), makeSList());
		abs.save(null, true);
		
		IMachineFile ref = createMachine("ref");
		addMachineRefines(ref, "abs");
		IEvent event = addEvent(ref, "evt", 
				makeSList("x"), 
				makeSList("G1", "G2", "G3"), makeSList("1 > 1", "x > x", "x = x"), 
				makeSList(), makeSList());
		addEventRefines(event, "evt");
		ref.save(null, true);
		runBuilder();
		
		ITypeEnvironment environment = factory.makeTypeEnvironment();
		environment.addName("x", intType);
		
		IPOFile po = ref.getPOFile();
		containsIdentifiers(po);
		
		IPOSequent sequent;
		
		noSequent(po, "evt/GA1/GRD");
		
		noSequent(po, "evt/GA2/GRD");

		sequent= getSequent(po, "evt/GA3/GRD");
		sequentHasGoal(sequent, environment, "x−1∈ℕ");
	}

	/*
	 * PO filter: inherited events should only produce invariant preservation POs
	 */
	public void testRefines_07() throws Exception {
		IMachineFile abs = createMachine("abs");
		addVariables(abs, "A");
		addInvariants(abs, makeSList("I"), makeSList("A∈ℕ"));
		addEvent(abs, "evt", 
				makeSList("x"), 
				makeSList("G1", "G2"), makeSList("1 > x", "x−1∈ℕ"), 
				makeSList("S"), makeSList("A≔A+1"));
		abs.save(null, true);
		
		IMachineFile ref = createMachine("ref");
		addMachineRefines(ref, "abs");
		addVariables(ref, "A", "B");
		addInvariants(ref, makeSList("J"), makeSList("A=B"));
		addInheritedEvent(ref, "evt");
		ref.save(null, true);
		runBuilder();
		
		ITypeEnvironment environment = factory.makeTypeEnvironment();
		environment.addName("A", intType);
		environment.addName("B", intType);
		environment.addName("x", intType);
		
		IPOFile po = ref.getPOFile();
		containsIdentifiers(po, "A", "B");
		
		IPOSequent sequent;
		
		noSequent(po, "evt/G1/REF");
		noSequent(po, "evt/G2/REF");
		noSequent(po, "evt/S/SIM");
		
		sequent= getSequent(po, "evt/J/INV");
		sequentHasHypotheses(sequent, environment, "A∈ℕ", "1 > x", "x−1∈ℕ");
		sequentHasGoal(sequent, environment, "A+1=B");
	}

	/*
	 * PO filter: do not produce WD and FIS POs for repeated actions
	 */
	public void testRefines_08() throws Exception {
		IMachineFile abs = createMachine("abs");
		addVariables(abs, "A", "B");
		addInvariants(abs, makeSList("I1", "I2"), makeSList("A∈ℕ", "B∈ℕ"));
		addEvent(abs, "evt", 
				makeSList("x"), 
				makeSList("GA"), makeSList("x−1∈ℕ"), 
				makeSList("SA1", "SA2"), makeSList("A :∣ A'>x", "B ≔ x÷x"));
		abs.save(null, true);
		
		IMachineFile ref = createMachine("ref");
		addMachineRefines(ref, "abs");
		addVariables(ref, "A", "B", "C");
		addInvariants(ref, makeSList("J"), makeSList("C=B"));
		IEvent event = addEvent(ref, "evt", 
				makeSList("x"), 
				makeSList("GC"), makeSList("x>0"), 
				makeSList("SC1", "SC2", "SC3"), makeSList("A :∣ A'>x", "B ≔ x÷x", "C :∈ {1÷x}"));
		addEventRefines(event, "evt");
		ref.save(null, true);
		runBuilder();
		
		ITypeEnvironment environment = factory.makeTypeEnvironment();
		environment.addName("A", intType);
		environment.addName("B", intType);
		environment.addName("C", intType);
		environment.addName("x", intType);
		
		IPOFile po = ref.getPOFile();
		containsIdentifiers(po, "A", "B", "C");
		
		IPOSequent sequent;
		
		noSequent(po, "evt/SA1/SIM");
		noSequent(po, "evt/SA2/SIM");
		noSequent(po, "evt/SC1/FIS");
		noSequent(po, "evt/SC2/WD");
		
		sequent= getSequent(po, "evt/GA/GRD");
		sequentHasHypotheses(sequent, environment, "A∈ℕ", "B∈ℕ", "C=B", "x>0");
		sequentHasGoal(sequent, environment, "x−1∈ℕ");
		
		sequent= getSequent(po, "evt/SC3/WD");
		sequentHasHypotheses(sequent, environment, "A∈ℕ", "B∈ℕ", "C=B", "x>0");
		sequentHasGoal(sequent, environment, "x≠0");
		
		sequent= getSequent(po, "evt/SC3/FIS");
		sequentHasHypotheses(sequent, environment, "A∈ℕ", "B∈ℕ", "C=B", "x>0");
		sequentHasGoal(sequent, environment, "{1÷x}≠∅");
	}	
	
	/*
	 * create event merge POs (simple)
	 */
	public void testRefines_09() throws Exception {
		IMachineFile abs = createMachine("abs");
		addEvent(abs, "evt", 
				makeSList("x"), 
				makeSList("GA"), makeSList("x−1∈ℕ"), 
				makeSList(), makeSList());
		addEvent(abs, "fvt", 
				makeSList("x"), 
				makeSList("HA"), makeSList("x+1∈ℕ"), 
				makeSList(), makeSList());
		abs.save(null, true);
		
		IMachineFile ref = createMachine("ref");
		addMachineRefines(ref, "abs");
		IEvent event = addEvent(ref, "evt", 
				makeSList("x"), 
				makeSList("GC"), makeSList("x>0"), 
				makeSList(), makeSList());
		addEventRefines(event, "evt");
		addEventRefines(event, "fvt");
		ref.save(null, true);
		runBuilder();
		
		ITypeEnvironment environment = factory.makeTypeEnvironment();
		environment.addName("x", intType);
		
		IPOFile po = ref.getPOFile();
		containsIdentifiers(po);
		
		IPOSequent sequent;
		
		sequent= getSequent(po, "evt/MRG");
		sequentHasHypotheses(sequent, environment, "x>0");
		sequentHasGoal(sequent, environment, "x−1∈ℕ ∨ x+1∈ℕ");
	}

	/*
	 * create event merge POs (complicated)
	 */
	public void testRefines_10() throws Exception {
		IMachineFile abs = createMachine("abs");
		addVariables(abs, "A", "B");
		addInvariants(abs, makeSList("I1", "I2"), makeSList("A∈ℕ", "B∈ℕ"));
		addEvent(abs, "evt", 
				makeSList("x"), 
				makeSList("GA"), makeSList("x−1∈ℕ"), 
				makeSList("SA1", "SA2"), makeSList("A :∣ A'>x", "B ≔ x"));
		addEvent(abs, "fvt", 
				makeSList("x", "y"), 
				makeSList("HA1", "HA2"), makeSList("x+1∈ℕ", "x=y+y"), 
				makeSList("SA1", "SA2"), makeSList("A :∣ A'>x", "B ≔ x"));
		abs.save(null, true);
		
		IMachineFile ref = createMachine("ref");
		addMachineRefines(ref, "abs");
		addVariables(ref, "A", "B", "C");
		addInvariants(ref, makeSList("J"), makeSList("C=B"));
		IEvent event = addEvent(ref, "evt", 
				makeSList("x"), 
				makeSList("GC"), makeSList("x>0"), 
				makeSList("SC1", "SC2", "SC3"), makeSList("A :∣ A'>x", "B ≔ x+1", "C :∈ {x+1}"));
		addEventRefines(event, "evt");
		addEventRefines(event, "fvt");
		ref.save(null, true);
		runBuilder();
		
		ITypeEnvironment environment = factory.makeTypeEnvironment();
		environment.addName("A", intType);
		environment.addName("B", intType);
		environment.addName("C", intType);
		environment.addName("x", intType);
		environment.addName("y", intType);
		
		IPOFile po = ref.getPOFile();
		containsIdentifiers(po, "A", "B", "C");
		
		IPOSequent sequent;
		
		sequent= getSequent(po, "evt/MRG");
		sequentHasHypotheses(sequent, environment, "A∈ℕ", "B∈ℕ", "C=B", "x>0");
		sequentHasGoal(sequent, environment, "x−1∈ℕ ∨ (x+1∈ℕ ∧ x=y+y)");
		
		sequent= getSequent(po, "evt/SA2/SIM");
		sequentHasHypotheses(sequent, environment, "A∈ℕ", "B∈ℕ", "C=B", "x>0");
		sequentHasGoal(sequent, environment, "x+1=x");
	}

	/*
	 * filter repeated guards from event merge POs
	 */
	public void testRefines_11() throws Exception {
		IMachineFile abs = createMachine("abs");
		addVariables(abs, "A", "B");
		addInvariants(abs, makeSList("I1", "I2"), makeSList("A∈ℕ", "B∈ℕ"));
		addEvent(abs, "evt", 
				makeSList("x"), 
				makeSList("GA1", "GA2"), makeSList("x−1∈ℕ", "x=y+y"), 
				makeSList("SA1", "SA2"), makeSList("A :∣ A'>x", "B ≔ x"));
		addEvent(abs, "fvt", 
				makeSList("x", "y"), 
				makeSList("HA1", "HA2"), makeSList("x+1∈ℕ", "x=y+y"), 
				makeSList("SA1", "SA2"), makeSList("A :∣ A'>x", "B ≔ x"));
		addEvent(abs, "gvt", 
				makeSList("x", "y"), 
				makeSList("IA1", "IA2"), makeSList("x=y+y", "A>1"), 
				makeSList("SA1", "SA2"), makeSList("A :∣ A'>x", "B ≔ x"));
		abs.save(null, true);
		
		IMachineFile ref = createMachine("ref");
		addMachineRefines(ref, "abs");
		addVariables(ref, "A", "B");
		IEvent event = addEvent(ref, "evt", 
				makeSList("x", "y"), 
				makeSList("GC"), makeSList("x=y+y"), 
				makeSList("SC1"), makeSList("A :∣ A'>x"));
		addEventRefines(event, "evt");
		addEventRefines(event, "fvt");
		addEventRefines(event, "gvt");
		ref.save(null, true);
		runBuilder();
		
		ITypeEnvironment environment = factory.makeTypeEnvironment();
		environment.addName("A", intType);
		environment.addName("B", intType);
		environment.addName("C", intType);
		environment.addName("x", intType);
		environment.addName("y", intType);
		
		IPOFile po = ref.getPOFile();
		containsIdentifiers(po, "A", "B");
		
		IPOSequent sequent;
		
		sequent= getSequent(po, "evt/MRG");
		sequentHasHypotheses(sequent, environment, "A∈ℕ", "B∈ℕ", "x=y+y");
		sequentHasGoal(sequent, environment, "x−1∈ℕ ∨ x+1∈ℕ ∨ A>1");
		
		sequent= getSequent(po, "evt/SA2/SIM");
		sequentHasHypotheses(sequent, environment, "A∈ℕ", "B∈ℕ", "x=y+y");
		sequentHasGoal(sequent, environment, "B=x");
	}

	/*
	 * filter event merge POs entirely if one of the disjuncts is true
	 */
	public void testRefines_12() throws Exception {
		IMachineFile abs = createMachine("abs");
		addVariables(abs, "A", "B");
		addInvariants(abs, makeSList("I1", "I2"), makeSList("A∈ℕ", "B∈ℕ"));
		addEvent(abs, "evt", 
				makeSList("x"), 
				makeSList("GA1", "GA2"), makeSList("x−1∈ℕ", "x=y+y"), 
				makeSList("SA1", "SA2"), makeSList("A :∣ A'>x", "B ≔ x"));
		addEvent(abs, "fvt", 
				makeSList("x", "y"), 
				makeSList("HA1", "HA2"), makeSList("x=y+y"), 
				makeSList("SA1", "SA2"), makeSList("A :∣ A'>x", "B ≔ x"));
		abs.save(null, true);
		
		IMachineFile ref = createMachine("ref");
		addMachineRefines(ref, "abs");
		addVariables(ref, "A", "B");
		IEvent event = addEvent(ref, "evt", 
				makeSList("x", "y"), 
				makeSList("GC"), makeSList("x=y+y"), 
				makeSList("SA1", "SA2"), makeSList("A :∣ A'>x", "B ≔ x"));
		addEventRefines(event, "evt");
		addEventRefines(event, "fvt");
		addEventRefines(event, "gvt");
		ref.save(null, true);
		runBuilder();
		
		ITypeEnvironment environment = factory.makeTypeEnvironment();
		environment.addName("A", intType);
		environment.addName("B", intType);
		environment.addName("C", intType);
		environment.addName("x", intType);
		environment.addName("y", intType);
		
		IPOFile po = ref.getPOFile();
		containsIdentifiers(po, "A", "B");
		
		noSequent(po, "evt/MRG");
		
		noSequent(po, "evt/SA1/SIM");
		noSequent(po, "evt/SA2/SIM");
	}

	/*
	 * PO filter: do not produce any POs in identical refinements
	 */
	public void testRefines_13() throws Exception {
		IMachineFile abs = createMachine("abs");
		addVariables(abs, "A", "B");
		addInvariants(abs, makeSList("I1", "I2"), makeSList("A∈ℕ", "B∈ℕ"));
		addEvent(abs, "evt", 
				makeSList("x"), 
				makeSList("GA", "HA"), makeSList("x>0", "B÷x>0"), 
				makeSList("SA", "TA"), makeSList("A :∣ A'>x", "B ≔ x÷x"));
		abs.save(null, true);
		
		IMachineFile ref = createMachine("ref");
		addMachineRefines(ref, "abs");
		addVariables(ref, "A", "B");
		addInheritedEvent(ref, "evt");
		ref.save(null, true);
		runBuilder();
		
		IPOFile apo = abs.getPOFile();
		containsIdentifiers(apo, "A", "B");
		
		getSequent(apo, "evt/HA/WD");
		getSequent(apo, "evt/SA/FIS");
		getSequent(apo, "evt/TA/WD");
		getSequent(apo, "evt/I1/INV");
		getSequent(apo, "evt/I2/INV");
		
		IPOFile cpo = ref.getPOFile();
		containsIdentifiers(cpo, "A", "B");
		
		// no sequents!
		
		getSequents(cpo);
	}
	
	/*
	 * Generate PO for event action that modifies abstract preserved variable;
	 * two cases
	 * 	(1) the event is new
	 * 	(2) the abstract event is empty
	 * (POs should be identical!)
	 */
	public void testRefines_14() throws Exception {
		IMachineFile abs = createMachine("abs");
		addVariables(abs, "p");
		addInvariants(abs, makeSList("I"), makeSList("p∈BOOL"));
		addEvent(abs, "fvt");

		abs.save(null, true);
		
		IMachineFile ref = createMachine("ref");
		addMachineRefines(ref, "abs");
		addVariables(ref, "p");
		addEvent(ref, "evt", 
				makeSList(), 
				makeSList(), makeSList(), 
				makeSList("A"), makeSList("p≔TRUE"));
		IEvent fvt = addEvent(ref, "fvt", 
				makeSList(), 
				makeSList(), makeSList(), 
				makeSList("A"), makeSList("p≔TRUE"));
		addEventRefines(fvt, "fvt");
	
		ref.save(null, true);
		
		runBuilder();
		
		IPOFile po = ref.getPOFile();
		containsIdentifiers(po, "p");
		
		getSequent(po, "evt/p/EQL");
		getSequent(po, "fvt/p/EQL");
		
	}
	
	/*
	 * Proper naming in goals with nondeterministic witnesses
	 */
	public void testRefines_15() throws Exception {
		IMachineFile abs = createMachine("abs");
		addVariables(abs, "p");
		addInvariants(abs, makeSList("I"), makeSList("p∈BOOL"));
		addEvent(abs, "evt", 
				makeSList(), 
				makeSList(), makeSList(), 
				makeSList("A"), makeSList("p :∣ p'≠p"));

		abs.save(null, true);
		
		IMachineFile ref = createMachine("ref");
		addMachineRefines(ref, "abs");
		addVariables(ref, "q");
		addInvariants(ref, makeSList("J"), makeSList("q∈BOOL"));
		IEvent event = addEvent(ref, "evt", 
				makeSList(), 
				makeSList(), makeSList(), 
				makeSList("B"), makeSList("q≔q"));
		addEventRefines(event, "evt");
		addEventWitnesses(event, makeSList("p'"), makeSList("p'≠q'"));
	
		ref.save(null, true);
		
		runBuilder();
		
		ITypeEnvironment environment = factory.makeTypeEnvironment();
		environment.addName("p", boolType);
		environment.addName("q", boolType);
		environment.addName("p'", boolType);
		environment.addName("q'", boolType);

		IPOFile po = ref.getPOFile();
		containsIdentifiers(po, "p", "q");
		
		IPOSequent sequent = getSequent(po, "evt/A/SIM");
		sequentHasIdentifiers(sequent, "p'", "q'");
		sequentHasHypotheses(sequent, environment, "p∈BOOL", "q∈BOOL", "p'≠q");
		sequentHasGoal(sequent, environment, "p'≠p");
	}
	
	/*
	 * If the event variable witnesses do not refer to post state variable values,
	 * a more efficient way of POs can be generated:
	 * (1) the concrete non-determistic actions can be removed from the hypothesis of /GRD
	 * (2) and, as a consequence, the abstract non-determistic actions can be put in the
	 * hypothesis of the concrete /FIS.
	 */
	public void testRefines_16() throws Exception {
		IMachineFile abs = createMachine("abs");
		addVariables(abs, "p");
		addInvariants(abs, makeSList("I"), makeSList("p∈BOOL"));
		addEvent(abs, "evt", 
				makeSList("x"), 
				makeSList("G"), makeSList("x≠p"), 
				makeSList("A"), makeSList("p :∣ p'≠x"));

		abs.save(null, true);
		
		IMachineFile ref = createMachine("ref");
		addMachineRefines(ref, "abs");
		addVariables(ref, "p");
		
		IEvent evt = addEvent(ref, "evt", 
				makeSList(), 
				makeSList(), makeSList(), 
				makeSList("B"), makeSList("p :∣ p'≠p"));
		addEventRefines(evt, "evt");
		addEventWitnesses(evt, makeSList("x"), makeSList("p'=x"));
		
		IEvent fvt = addEvent(ref, "fvt", 
				makeSList("y"), 
				makeSList("H"), makeSList("y≠p"), 
				makeSList("B"), makeSList("p :∣ p'≠y"));
		addEventRefines(fvt, "evt");
		addEventWitnesses(fvt, makeSList("x"), makeSList("y=x"));
	
		ref.save(null, true);
		
		runBuilder();
		
		ITypeEnvironment environment = factory.makeTypeEnvironment();
		environment.addName("p", boolType);
		environment.addName("p'", boolType);
		environment.addName("x", boolType);
		environment.addName("y", boolType);

		IPOFile po = ref.getPOFile();
		containsIdentifiers(po, "p");
		
		IPOSequent sequent = getSequent(po, "evt/G/GRD");
		sequentHasIdentifiers(sequent, "p'", "x");
		sequentHasHypotheses(sequent, environment, "p∈BOOL", "p'=x", "p'≠p");
		sequentHasGoal(sequent, environment, "x≠p");
		
		sequent = getSequent(po, "evt/B/FIS");
		sequentHasIdentifiers(sequent, "p'", "x");
		sequentHasHypotheses(sequent, environment, "p∈BOOL");
		sequentHasNotHypotheses(sequent, environment, "x≠p", "p'≠x");
		sequentHasGoal(sequent, environment, "∃p'·p'≠p");
		
		sequent = getSequent(po, "fvt/G/GRD");
		sequentHasIdentifiers(sequent, "p'", "x", "y");
		sequentHasHypotheses(sequent, environment, "p∈BOOL", "y≠p", "y=x");
		sequentHasNotHypotheses(sequent, environment, "p'≠p");
		sequentHasGoal(sequent, environment, "x≠p");
		
		sequent = getSequent(po, "fvt/B/FIS");
		sequentHasIdentifiers(sequent, "p'", "x", "y");
		sequentHasHypotheses(sequent, environment, "p∈BOOL", "y≠p", "y=x", "p'≠x");
		sequentHasGoal(sequent, environment, "∃p'·p'≠y");
	}
	
	/*
	 * If the event variable witnesses do not refer to post state variable values of
	 * certain variables, then corresponding before-after predicates do not need to be
	 * added to the hypothesis of /GRD
	 */
	public void testRefines_17() throws Exception {
		IMachineFile abs = createMachine("abs");
		addVariables(abs, "p", "q");
		addInvariants(abs, makeSList("I", "J"), makeSList("p∈BOOL", "q∈BOOL"));
		addEvent(abs, "evt", 
				makeSList("x"), 
				makeSList("G"), makeSList("x≠p"), 
				makeSList("A", "B"), makeSList("p :∣ p'≠x", "q :∣ q'≠p"));

		abs.save(null, true);
		
		IMachineFile ref = createMachine("ref");
		addMachineRefines(ref, "abs");
		addVariables(ref, "p", "q");
		
		IEvent evt = addEvent(ref, "evt", 
				makeSList(), 
				makeSList(), makeSList(), 
				makeSList("A", "B"), makeSList("p :∣ p'≠p", "q :∣ q'≠q"));
		addEventRefines(evt, "evt");
		addEventWitnesses(evt, makeSList("x"), makeSList("p'=x"));
	
		ref.save(null, true);
		
		runBuilder();
		
		ITypeEnvironment environment = factory.makeTypeEnvironment();
		environment.addName("p", boolType);
		environment.addName("p'", boolType);
		environment.addName("x", boolType);
		environment.addName("y'", boolType);

		IPOFile po = ref.getPOFile();
		containsIdentifiers(po, "p", "q");
		
		IPOSequent sequent = getSequent(po, "evt/G/GRD");
		sequentHasIdentifiers(sequent, "p'", "q'", "x");
		sequentHasHypotheses(sequent, environment, "p∈BOOL", "p'=x", "p'≠p");
		sequentHasNotHypotheses(sequent, environment, "q'≠p");
		sequentHasGoal(sequent, environment, "x≠p");
	}
	
	/*
	 * Additional abstract before-after predicates in a /FIS proof obligation must
	 * be correctly rewritten using the witnesses (all witnesses!)
	 */
	public void testRefines_18() throws Exception {
		IMachineFile abs = createMachine("abs");
		addVariables(abs, "p");
		addInvariants(abs, makeSList("I"), makeSList("p∈BOOL"));
		addEvent(abs, "evt", 
				makeSList("x"), 
				makeSList("G"), makeSList("x≠p"), 
				makeSList("A"), makeSList("p :∣ p'≠x"));

		abs.save(null, true);
		
		IMachineFile ref = createMachine("ref");
		addMachineRefines(ref, "abs");
		addVariables(ref, "q");
		addInvariants(ref, makeSList("J"), makeSList("p∈{q}"));
	
		IEvent evt = addEvent(ref, "evt", 
				makeSList("y"), 
				makeSList("H"), makeSList("y∈{q}"), 
				makeSList("B"), makeSList("q :∣ q'≠q"));
		addEventRefines(evt, "evt");
		addEventWitnesses(evt, makeSList("x", "p'"), makeSList("y=x", "p'=y"));
		
		IEvent fvt = addEvent(ref, "fvt", 
				makeSList("y"), 
				makeSList("H"), makeSList("y≠q"), 
				makeSList("B"), makeSList("q :∣ q'≠y"));
		addEventRefines(fvt, "evt");
		addEventWitnesses(fvt, makeSList("x", "p'"), makeSList("x=y", "p'=y"));
		
		IEvent gvt = addEvent(ref, "gvt", 
				makeSList("y"), 
				makeSList("H"), makeSList("y≠q"), 
				makeSList("B"), makeSList("q :∣ q'≠y"));
		addEventRefines(gvt, "evt");
		addEventWitnesses(gvt, makeSList("x", "p'"), makeSList("y=x", "y=p'"));
		
		IEvent hvt = addEvent(ref, "hvt", 
				makeSList(), 
				makeSList("H"), makeSList("q∈{q}"), 
				makeSList("B"), makeSList("q :∣ q'≠q"));
		addEventRefines(hvt, "evt");
		addEventWitnesses(hvt, makeSList("x", "p'"), makeSList("q'=x", "q'=p'"));

		ref.save(null, true);
		
		runBuilder();
		
		ITypeEnvironment environment = factory.makeTypeEnvironment();
		environment.addName("p", boolType);
		environment.addName("p'", boolType);
		environment.addName("q", boolType);
		environment.addName("q'", boolType);
		environment.addName("x", boolType);
		environment.addName("y", boolType);

		IPOFile po = ref.getPOFile();
		containsIdentifiers(po, "p", "q");
		
		IPOSequent sequent = getSequent(po, "evt/G/GRD");
		sequentHasIdentifiers(sequent, "p'", "q'", "x", "y");
		sequentHasHypotheses(sequent, environment, "p∈BOOL", "p∈{q}", "p∈{q}", "y∈{q}", "y=x");
		sequentHasNotHypotheses(sequent, environment, "p'=y", "y≠x");
		sequentHasGoal(sequent, environment, "x≠p");
		
		sequent = getSequent(po, "evt/B/FIS");
		sequentHasIdentifiers(sequent, "p'", "q'", "x", "y");
		sequentHasHypotheses(sequent, environment, "p∈BOOL", "p∈{q}", "y≠x", "y=x", "y∈{q}");
		sequentHasNotHypotheses(sequent, environment, "p'=y");
		sequentHasGoal(sequent, environment, "∃q'·q'≠q");
		
		sequent = getSequent(po, "fvt/G/GRD");
		sequentHasIdentifiers(sequent, "p'", "q'", "x", "y");
		sequentHasHypotheses(sequent, environment, "p∈BOOL", "p∈{q}", "y≠q");
		sequentHasGoal(sequent, environment, "y≠p");
		
		sequent = getSequent(po, "fvt/B/FIS");
		sequentHasIdentifiers(sequent, "p'", "q'", "x", "y");
		sequentHasHypotheses(sequent, environment, "p∈BOOL", "p∈{q}", "p∈{q}", "y≠q", "y≠y");
		sequentHasGoal(sequent, environment, "∃q'·q'≠y");
		
		sequent = getSequent(po, "gvt/G/GRD");
		sequentHasIdentifiers(sequent, "p'", "q'", "x", "y");
		sequentHasHypotheses(sequent, environment, "p∈BOOL", "p∈{q}", "p∈{q}", "y≠q", "y=x");
		sequentHasNotHypotheses(sequent, environment, "y=p'");
		sequentHasGoal(sequent, environment, "x≠p");
		
		sequent = getSequent(po, "gvt/B/FIS");
		sequentHasIdentifiers(sequent, "p'", "q'", "x", "y");
		sequentHasHypotheses(sequent, environment, "p∈BOOL", "p∈{q}", "p∈{q}", "y=x", "y=p'", "p'≠x");
		sequentHasGoal(sequent, environment, "∃q'·q'≠y");
		
		sequent = getSequent(po, "hvt/G/GRD");
		sequentHasIdentifiers(sequent, "p'", "q'", "x");
		sequentHasHypotheses(sequent, environment, "p∈BOOL", "p∈{q}", "q∈{q}", "q'=x", "q'≠q");
		sequentHasGoal(sequent, environment, "x≠p");
		
		sequent = getSequent(po, "hvt/B/FIS");
		sequentHasIdentifiers(sequent, "p'", "q'", "x");
		sequentHasHypotheses(sequent, environment, "p∈BOOL", "p∈{q}", "q∈{q}");
		sequentHasNotHypotheses(sequent, environment, "q'=x", "q'=p'", "p'≠x");
		sequentHasGoal(sequent, environment, "∃q'·q'≠q");
	}
	
	/*
	 * Disappearing variables in deterministic actions must not be simulated,
	 * but the preserved variables must be simulated. This also holds for
	 * multiple assignments!
	 */
	public void testRefines_19() throws Exception {
		IMachineFile abs = createMachine("abs");
		addVariables(abs, "x", "y");
		addInvariants(abs, makeSList("I", "J"), makeSList("x∈ℤ", "y∈ℤ"));
		addEvent(abs, "evt", 
				makeSList(), 
				makeSList(), makeSList(), 
				makeSList("A"), makeSList("x,y ≔ y,x"));

		abs.save(null, true);
		
		IMachineFile ref = createMachine("ref");
		addMachineRefines(ref, "abs");
		addVariables(ref, "y", "z");
		addInvariants(ref, makeSList("K", "L"), makeSList("z∈ℤ", "y≤x"));
	
		IEvent evt = addEvent(ref, "evt", 
				makeSList(), 
				makeSList(), makeSList(), 
				makeSList("B"), makeSList("y,z ≔ z,y"));
		addEventRefines(evt, "evt");

		ref.save(null, true);
		
		runBuilder();
		
		ITypeEnvironment environment = factory.makeTypeEnvironment();
		environment.addName("x", intType);
		environment.addName("y", intType);
		environment.addName("z", intType);

		IPOFile po = ref.getPOFile();
		containsIdentifiers(po, "x", "y", "z");
		
		IPOSequent 
		sequent = getSequent(po, "evt/A/SIM");
		sequentHasIdentifiers(sequent, "x'", "y'", "z'");
		sequentHasHypotheses(sequent, environment, "x∈ℤ", "y∈ℤ", "z∈ℤ", "y≤x");
		sequentHasGoal(sequent, environment, "z=x");
		
		sequent = getSequent(po, "evt/L/INV");
		sequentHasIdentifiers(sequent, "x'", "y'", "z'");
		sequentHasHypotheses(sequent, environment, "x∈ℤ", "y∈ℤ", "z∈ℤ", "y≤x");
		sequentHasGoal(sequent, environment, "z≤y");
	}
	
	/*
	 * Check if types of local variables of abstract event are added to type environment
	 */
	public void testRefines_20() throws Exception {
		IMachineFile abs = createMachine("abs");
		addVariables(abs, "x", "y");
		addInvariants(abs, makeSList("I", "J"), makeSList("x∈ℤ", "y∈ℤ"));
		addEvent(abs, "evt", 
				makeSList("a", "b"), 
				makeSList("G", "H"), makeSList("a ∈ ℕ", "b ∈ {a}"), 
				makeSList("A"), makeSList("x,y ≔ a,b"));

		abs.save(null, true);
		
		IMachineFile ref = createMachine("ref");
		addMachineRefines(ref, "abs");
		addVariables(ref, "x", "y");
	
		IEvent evt = addEvent(ref, "evt", 
				makeSList("c"), 
				makeSList("GG"), makeSList("c ∈ ℕ"), 
				makeSList("B"), makeSList("x,y ≔ c,c"));
		addEventRefines(evt, "evt");

		ref.save(null, true);
		
		runBuilder();
		
		ITypeEnvironment environment = factory.makeTypeEnvironment();
		environment.addName("x", intType);
		environment.addName("y", intType);
		environment.addName("a", intType);
		environment.addName("b", intType);
		environment.addName("c", intType);

		IPOFile po = ref.getPOFile();
		containsIdentifiers(po, "x", "y");
		
		IPOSequent 
		sequent = getSequent(po, "evt/A/SIM");
		sequentHasIdentifiers(sequent, "x'", "y'", "a", "b", "c");
		sequentHasGoal(sequent, environment, "c=a ∧ c=b");
	}
	
	/*
	 * Check if invariant preservation PO is generated for event with
	 * empty list of actions (i.e. the concrete action is skip)
	 */
	public void testRefines_21() throws Exception {
		IMachineFile abs = createMachine("abs");
		addVariables(abs, "x");
		addInvariants(abs, makeSList("I"), makeSList("x∈ℤ"));
		addEvent(abs, "evt", 
				makeSList("a"), 
				makeSList("G"), makeSList("a ∈ ℕ"), 
				makeSList("A"), makeSList("x ≔ a"));

		abs.save(null, true);
		
		IMachineFile ref = createMachine("ref");
		addMachineRefines(ref, "abs");
		addVariables(ref, "y");
		addInvariants(ref, makeSList("J"), makeSList("x+y=2"));
	
		IEvent evt = addEvent(ref, "evt", 
				makeSList(), 
				makeSList("H"), makeSList("y=1"), 
				makeSList(), makeSList());
		addEventRefines(evt, "evt");
		addEventWitnesses(evt, makeSList("a"), makeSList("a=y'"));

		ref.save(null, true);
		
		runBuilder();
		
		ITypeEnvironment environment = factory.makeTypeEnvironment();
		environment.addName("x", intType);
		environment.addName("y", intType);
		environment.addName("a", intType);

		IPOFile po = ref.getPOFile();
		containsIdentifiers(po, "x", "y");
		
		IPOSequent 
		sequent = getSequent(po, "evt/J/INV");
		sequentHasIdentifiers(sequent, "x'", "a", "y'");
		sequentHasHypotheses(sequent, environment, "y=1", "x+y=2");
		sequentHasGoal(sequent, environment, "y+y=2");
	}
	
	/*
	 * Check that there are no witness-related POs (WFIS, WWD) for an inherited event.
	 */
	public void testRefines_22() throws Exception {
		IMachineFile abs = createMachine("abs");
		addVariables(abs, "x");
		addInvariants(abs, makeSList("I"), makeSList("x∈ℤ"));
		addInitialisation(abs, "x");
		addEvent(abs, "evt", 
				makeSList("a"), 
				makeSList("G"), makeSList("a ∈ ℕ"), 
				makeSList("A"), makeSList("x ≔ a"));

		abs.save(null, true);
		
		IMachineFile ref = createMachine("ref");
		addMachineRefines(ref, "abs");
		addVariables(ref, "y");
		addInvariants(ref, makeSList("J"), makeSList("x+y=2"));
	
		IEvent ini = addInitialisation(ref, "y");
		addEventWitnesses(ini, makeSList("x'"), makeSList("y'=x'÷1"));
		IEvent evt = addEvent(ref, "evt", 
				makeSList(), 
				makeSList("H"), makeSList("y=1"), 
				makeSList(), makeSList());
		addEventRefines(evt, "evt");
		addEventWitnesses(evt, makeSList("a"), makeSList("a÷1=y'"));

		ref.save(null, true);
		
		IMachineFile con = createMachine("con");
		addMachineRefines(con, "ref");
		addVariables(con, "y");
	
		addInheritedEvent(con, IEvent.INITIALISATION);
		addInheritedEvent(con, "evt");

		con.save(null, true);
		
		runBuilder();
		
		IPOFile po = ref.getPOFile();
		
		getSequent(po, IEvent.INITIALISATION + "/x'/WFIS");
		getSequent(po, IEvent.INITIALISATION + "/x'/WWD");
		getSequent(po, "evt/a/WFIS");
		getSequent(po, "evt/a/WWD");

		po = con.getPOFile();
		
		noSequent(po, IEvent.INITIALISATION + "/x'/WFIS");
		noSequent(po, IEvent.INITIALISATION + "/x'/WWD");
		noSequent(po, "evt/a/WFIS");
		noSequent(po, "evt/a/WWD");
	}

}

/*******************************************************************************
 * Copyright (c) 2006, 2009 ETH Zurich and others.
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

import org.eventb.core.IEvent;
import org.eventb.core.IMachineRoot;
import org.eventb.core.ISCEvent;
import org.eventb.core.ISCMachineRoot;
import org.eventb.core.ast.ITypeEnvironment;

/**
 * @author Stefan Hallerstede
 *
 */
public class TestEvents extends BasicSCTestWithFwdConfig {
	
	/*
	 * create an event
	 */
	public void testEvents_00_createEvent() throws Exception {
		IMachineRoot mac = createMachine("mac");
		
		addInitialisation(mac);

		addEvent(mac, "evt", makeSList(), makeSList(), makeSList(), makeSList(), makeSList());
		
		saveRodinFileOf(mac);
		
		runBuilder();
		
		ISCMachineRoot file = mac.getSCMachineRoot();
		
		containsEvents(file, IEvent.INITIALISATION, "evt");
		
		containsMarkers(mac, false);
	}

	/*
	 * create two events
	 */
	public void testEvents_01_createTwoEvents() throws Exception {
		IMachineRoot mac = createMachine("mac");

		addInitialisation(mac);
		addEvent(mac, "evt1", makeSList(), makeSList(), makeSList(), makeSList(), makeSList());
		addEvent(mac, "evt2", makeSList(), makeSList(), makeSList(), makeSList(), makeSList());
		
		saveRodinFileOf(mac);
		
		runBuilder();
		
		ISCMachineRoot file = mac.getSCMachineRoot();
		
		containsEvents(file, IEvent.INITIALISATION, "evt1", "evt2");
		
		containsMarkers(mac, false);
	}
	
	/*
	 * create two events with name conflict
	 */
	public void testEvents_02_createTwoEventsWithNameConflict() throws Exception {
		IMachineRoot mac = createMachine("mac");

		IEvent e = addEvent(mac, "evt1", makeSList(), makeSList(), makeSList(), makeSList(), makeSList());
		IEvent f = addEvent(mac, "evt1", makeSList(), makeSList(), makeSList(), makeSList(), makeSList());
		
		saveRodinFileOf(mac);
		
		runBuilder();
		
		ISCMachineRoot file = mac.getSCMachineRoot();
		
		containsEvents(file);
		
		hasMarker(e);
		hasMarker(f);
	}
	
	/*
	 * create guard
	 */
	public void testEvents_03_createGuard() throws Exception {
		IMachineRoot mac = createMachine("mac");

		addInitialisation(mac);
		addEvent(mac, "evt1", makeSList(), makeSList("G1"), makeSList("1∈ℕ"), makeSList(), makeSList());
		
		saveRodinFileOf(mac);
		
		runBuilder();
		
		ISCMachineRoot file = mac.getSCMachineRoot();
		
		ISCEvent[] scEvents = getSCEvents(file, IEvent.INITIALISATION, "evt1");
		
		containsGuards(scEvents[1], emptyEnv, makeSList("G1"), makeSList("1∈ℕ"));
		
		containsMarkers(mac, false);
	}

	/*
	 * create two guards
	 */
	public void testEvents_04_createTwoGuards() throws Exception {
		IMachineRoot mac = createMachine("mac");

		addInitialisation(mac);
		addEvent(mac, "evt1", makeSList(), makeSList("G1", "G2"), makeSList("1∈ℕ", "2∈ℕ"), makeSList(), makeSList());
		
		saveRodinFileOf(mac);
		
		runBuilder();
		
		ISCMachineRoot file = mac.getSCMachineRoot();
		
		ISCEvent[] scEvents = getSCEvents(file, IEvent.INITIALISATION, "evt1");
		
		containsGuards(scEvents[1], emptyEnv, makeSList("G1", "G2"), makeSList("1∈ℕ", "2∈ℕ"));
		
		containsMarkers(mac, false);
	}

	/*
	 * create two guards with label conflict (last one is filtered)
	 */
	public void testEvents_05_createTwoGuardsWithLabelConflict() throws Exception {
		IMachineRoot mac = createMachine("mac");

		IEvent e = 
			addEvent(mac, "evt1", 
					makeSList(), 
					makeSList("G1", "G1"), makeSList("1∈ℕ", "2∈ℕ"), makeSList(), makeSList());
		
		saveRodinFileOf(mac);
		
		runBuilder();
		
		ISCMachineRoot file = mac.getSCMachineRoot();
		
		ISCEvent[] scEvents = getSCEvents(file, "evt1");
		
		containsGuards(scEvents[0], emptyEnv, makeSList("G1"), makeSList("1∈ℕ"));
		
		hasMarker(e.getGuards()[0]);
		hasMarker(e.getGuards()[1]);
	}
	
	/*
	 * create local variable
	 */
	public void testEvents_06_parameter() throws Exception {
		IMachineRoot mac = createMachine("mac");

		ITypeEnvironment typeEnvironment = factory.makeTypeEnvironment();
		typeEnvironment.addName("L1", factory.makeIntegerType());

		addInitialisation(mac);
		addEvent(mac, "evt1", makeSList("L1"), makeSList("G1"), makeSList("L1∈ℕ"), makeSList(), makeSList());
		
		saveRodinFileOf(mac);
		
		runBuilder();
		
		ISCMachineRoot file = mac.getSCMachineRoot();
		
		ISCEvent[] scEvents = getSCEvents(file, IEvent.INITIALISATION, "evt1");
		
		containsParameters(scEvents[1], "L1");
		containsGuards(scEvents[1], typeEnvironment, makeSList("G1"), makeSList("L1∈ℕ"));
		
		containsMarkers(mac, false);
	}

	/*
	 * create global and local variable
	 */
	public void testEvents_07_variableAndParameter() throws Exception {
		IMachineRoot mac = createMachine("mac");

		ITypeEnvironment typeEnvironment = factory.makeTypeEnvironment();
		typeEnvironment.addName("L1", factory.makeIntegerType());

		addVariables(mac, "V1");
		addInvariants(mac, makeSList("I1"), makeSList("V1⊆ℕ"));
		addInitialisation(mac, "V1");
		addEvent(mac, "evt1", makeSList("L1"), makeSList("G1"), makeSList("L1∈V1"), makeSList(), makeSList());
		
		saveRodinFileOf(mac);
		
		runBuilder();
		
		ISCMachineRoot file = mac.getSCMachineRoot();
		
		containsVariables(file, "V1");
		
		ISCEvent[] scEvents = getSCEvents(file, IEvent.INITIALISATION, "evt1");
		
		containsParameters(scEvents[1], "L1");
		containsGuards(scEvents[1], typeEnvironment, makeSList("G1"), makeSList("L1∈V1"));
		
		containsMarkers(mac, false);
	}
	
	/*
	 * create global and local variable with name conflict (local variable is filtered)
	 */
	public void testEvents_08_variableAndParameterWithNameConflict() throws Exception {
		IMachineRoot mac = createMachine("mac");

		ITypeEnvironment typeEnvironment = factory.makeTypeEnvironment();
		typeEnvironment.addName("L1", factory.makeIntegerType());

		addVariables(mac, "L1");
		addInvariants(mac, makeSList("I1"), makeSList("L1⊆ℕ"));
		IEvent e = 
			addEvent(mac, "evt1", 
					makeSList("L1"), 
					makeSList("G1"), makeSList("L1∈ℕ"), makeSList(), makeSList());
		
		saveRodinFileOf(mac);
		
		runBuilder();
		
		ISCMachineRoot file = mac.getSCMachineRoot();
		
		containsVariables(file, "L1");
		
		ISCEvent[] scEvents = getSCEvents(file, "evt1");
		
		containsParameters(scEvents[0]);
		containsGuards(scEvents[0], typeEnvironment, makeSList(), makeSList());
		
		hasMarker(e.getParameters()[0]);
		hasMarker(e.getGuards()[0]);
	}
	
	/*
	 * create parameters with same name but different types in different events
	 */
	public void testEvents_09_parametersOfDifferentEvents() throws Exception {
		IMachineRoot mac = createMachine("mac");

		ITypeEnvironment typeEnvironment = factory.makeTypeEnvironment();
		typeEnvironment.addName("L1", factory.makeIntegerType());

		addVariables(mac, "L1");
		addInvariants(mac, makeSList("I1"), makeSList("L1⊆ℕ"));
		addInitialisation(mac, "L1");
		addEvent(mac, "evt1", makeSList("L2"), makeSList("G1"), makeSList("L2∈ℕ"), makeSList(), makeSList());
		addEvent(mac, "evt2", makeSList("L2"), makeSList("G1"), makeSList("L2⊆ℕ"), makeSList(), makeSList());
	
		saveRodinFileOf(mac);
		
		runBuilder();
		
		ISCMachineRoot file = mac.getSCMachineRoot();
		
		containsVariables(file, "L1");
		
		ISCEvent[] scEvents = getSCEvents(file, IEvent.INITIALISATION, "evt1", "evt2");
		
		containsParameters(scEvents[1], "L2");
		containsGuards(scEvents[1], typeEnvironment, makeSList("G1"), makeSList("L2∈ℕ"));
		
		containsParameters(scEvents[2], "L2");
		containsGuards(scEvents[2], typeEnvironment, makeSList("G1"), makeSList("L2⊆ℕ"));	
		
		containsMarkers(mac, false);
	}
	
	/*
	 * actions can assign to global variables
	 */
	public void testEvents_10_globalVariableInAction() throws Exception {
		IMachineRoot mac = createMachine("mac");

		ITypeEnvironment typeEnvironment = factory.makeTypeEnvironment();
		typeEnvironment.addName("L1", factory.makeIntegerType());

		addVariables(mac, "L1");
		addInvariants(mac, makeSList("I1"), makeSList("L1∈ℕ"));
		addInitialisation(mac, "L1");
		addEvent(mac, "evt1", makeSList(), makeSList(), makeSList(), makeSList("A1"), makeSList("L1≔1"));
	
		saveRodinFileOf(mac);
		
		runBuilder();
		
		ISCMachineRoot file = mac.getSCMachineRoot();
		
		containsVariables(file, "L1");
		
		ISCEvent[] scEvents = getSCEvents(file, IEvent.INITIALISATION, "evt1");
		
		containsActions(scEvents[1], typeEnvironment, makeSList("A1"), makeSList("L1≔1"));
		
		containsMarkers(mac, false);
		
	}
	
	/*
	 * error: two actions cannot assign to same variable
	 */
	public void testEvents_11_actionSameLHSConflict() throws Exception {
		IMachineRoot mac = createMachine("mac");

		ITypeEnvironment typeEnvironment = factory.makeTypeEnvironment();
		typeEnvironment.addName("L1", factory.makeIntegerType());

		addVariables(mac, "L1");
		addInvariants(mac, makeSList("I1"), makeSList("L1∈ℕ"));
		IEvent e = addEvent(mac, "evt1", makeSList(), makeSList(), makeSList(), 
				makeSList("A1","A1"), makeSList("L1≔1", "L1≔2"));
	
		saveRodinFileOf(mac);
		
		runBuilder();
		
		ISCMachineRoot file = mac.getSCMachineRoot();
		
		containsVariables(file, "L1");
		
		ISCEvent[] scEvents = getSCEvents(file, "evt1");
		
		containsActions(scEvents[0], typeEnvironment, makeSList("A1"), makeSList("L1≔1"));
		
		hasMarker(e.getActions()[0]);
		hasMarker(e.getActions()[1]);
	}
	
	/*
	 * action and gaurd labels in the same event must be different (actions filtered)
	 */
	public void testEvents_12_actionGuardLabelConflict() throws Exception {
		IMachineRoot mac = createMachine("mac");

		ITypeEnvironment typeEnvironment = factory.makeTypeEnvironment();
		typeEnvironment.addName("L1", factory.makeIntegerType());

		addVariables(mac, "L1");
		addInvariants(mac, makeSList("I1"), makeSList("L1∈ℕ"));
		IEvent e = addEvent(mac, "evt1", makeSList(), makeSList("A1"), makeSList("1∈ℕ"), 
				makeSList("A1"), makeSList("L1≔1"));
	
		saveRodinFileOf(mac);
		
		runBuilder();
		
		ISCMachineRoot file = mac.getSCMachineRoot();
		
		containsEvents(file, "evt1");
		
		containsVariables(file, "L1");
		
		ISCEvent[] scEvents = getSCEvents(file, "evt1");
		
		containsGuards(scEvents[0], typeEnvironment, makeSList("A1"), makeSList("1∈ℕ"));
		containsActions(scEvents[0], typeEnvironment, makeSList(), makeSList());
		
		hasMarker(e.getActions()[0]);
	}
	
	/*
	 * local variables may appear on the RHS of an assignment
	 */
	public void testEvents_13_actionAssignFromLocalVariableOK() throws Exception {
		IMachineRoot mac = createMachine("mac");

		ITypeEnvironment typeEnvironment = factory.makeTypeEnvironment();
		typeEnvironment.addName("V1", factory.makeIntegerType());

		addVariables(mac, "V1");
		addInvariants(mac, makeSList("I1"), makeSList("V1∈ℕ"));
		addInitialisation(mac, "V1");
		addEvent(mac, "evt1", makeSList("L1"), 
				makeSList("G1"), makeSList("L1∈ℕ"), 
				makeSList("A1"), makeSList("V1≔L1"));
	
		saveRodinFileOf(mac);
		
		runBuilder();
		
		ISCMachineRoot file = mac.getSCMachineRoot();
		
		containsEvents(file, IEvent.INITIALISATION, "evt1");
		
		containsVariables(file, "V1");
		
		ISCEvent[] scEvents = getSCEvents(file, IEvent.INITIALISATION, "evt1");
		
		containsParameters(scEvents[1], "L1");
		containsGuards(scEvents[1], typeEnvironment, makeSList("G1"), makeSList("L1∈ℕ"));
		containsActions(scEvents[1], typeEnvironment, makeSList("A1"), makeSList("V1≔L1"));
		
		containsMarkers(mac, false);
	}
	
	/*
	 * error: cannot assign to local variable
	 */
	public void testEvents_14_assignmentToParameterProblem() throws Exception {
		IMachineRoot mac = createMachine("mac");

		ITypeEnvironment typeEnvironment = factory.makeTypeEnvironment();
		typeEnvironment.addName("V1", factory.makeIntegerType());

		addVariables(mac, "V1");
		addInvariants(mac, makeSList("I1"), makeSList("V1∈ℕ"));
		IEvent e = addEvent(mac, "evt1", makeSList("L1"), 
				makeSList("G1"), makeSList("L1∈ℕ"), 
				makeSList("A1"), makeSList("L1≔V1"));
	
		saveRodinFileOf(mac);
		
		runBuilder();
		
		ISCMachineRoot file = mac.getSCMachineRoot();
		
		containsEvents(file, "evt1");
		
		containsVariables(file, "V1");
		
		ISCEvent[] scEvents = getSCEvents(file, "evt1");
		
		containsParameters(scEvents[0], "L1");
		containsGuards(scEvents[0], typeEnvironment, makeSList("G1"), makeSList("L1∈ℕ"));
		containsActions(scEvents[0], typeEnvironment, makeSList(), makeSList());
		
		hasMarker(e.getActions()[0]);
	}
	
	/*
	 * action with nondetermistic assignment
	 */
	public void testEvents_15_actionNondetAssignment() throws Exception {
		IMachineRoot mac = createMachine("mac");

		ITypeEnvironment typeEnvironment = factory.makeTypeEnvironment();
		typeEnvironment.addName("V1", factory.makeIntegerType());
		typeEnvironment.addName("V1'", factory.makeIntegerType());

		addVariables(mac, "V1");
		addInvariants(mac, makeSList("I1"), makeSList("V1∈ℕ"));
		addInitialisation(mac, "V1");
		addEvent(mac, "evt1", makeSList(), 
				makeSList(), makeSList(), 
				makeSList("A1"), makeSList("V1:∣V1'∈ℕ"));
	
		saveRodinFileOf(mac);
		
		runBuilder();
		
		ISCMachineRoot file = mac.getSCMachineRoot();
		
		containsEvents(file, IEvent.INITIALISATION, "evt1");
		
		containsVariables(file, "V1");
		
		ISCEvent[] scEvents = getSCEvents(file, IEvent.INITIALISATION, "evt1");
		
		containsParameters(scEvents[1]);
		containsGuards(scEvents[1], typeEnvironment, makeSList(), makeSList());
		containsActions(scEvents[1], typeEnvironment, makeSList("A1"), makeSList("V1:∣V1'∈ℕ"));
		
		containsMarkers(mac, false);
	}
	
	/*
	 * multiple assignment to same variables problem
	 */
	public void testEvents_16_actionMultipleLHSConflict() throws Exception {
		IMachineRoot mac = createMachine("mac");

		ITypeEnvironment typeEnvironment = factory.makeTypeEnvironment();
		typeEnvironment.addName("u", factory.makeIntegerType());
		typeEnvironment.addName("x", factory.makeIntegerType());

		addVariables(mac, "u", "v", "w", "x", "y", "z");
		addInvariants(mac, makeSList("I1"), makeSList("u∈ℕ ∧ v∈ℕ ∧ w∈ℕ ∧ x∈ℕ ∧ y∈ℕ ∧ z∈ℕ"));
		IEvent e = addEvent(mac, "evt", makeSList(), 
				makeSList(), makeSList(), 
				makeSList("A1", "A2", "A3", "A4", "A5"), 
				makeSList("u≔1", "v,w:∣⊤", "v≔1", "x≔u", "y,z,w≔v,w,1"));
	
		saveRodinFileOf(mac);
		
		runBuilder();
		
		ISCMachineRoot file = mac.getSCMachineRoot();
		
		containsVariables(file, "u", "v", "w", "x", "y", "z");
		
		ISCEvent[] scEvents = getSCEvents(file, "evt");
		
		containsActions(scEvents[0], typeEnvironment, makeSList("A1", "A4"), makeSList("u≔1", "x≔u"));
		
		hasMarker(e.getActions()[1]);
		hasMarker(e.getActions()[2]);
		hasMarker(e.getActions()[4]);
	}
	
	/*
	 * initialisation must not have local variables
	 */
	public void testEvents_17_initialisationParameterProblem() throws Exception {
		IMachineRoot mac = createMachine("mac");

		IEvent e = addEvent(mac, IEvent.INITIALISATION, makeSList("x"), 
				makeSList("G1"), makeSList("x∈ℕ"), 
				makeSList(), makeSList());
	
		saveRodinFileOf(mac);
		
		runBuilder();
		
		ISCMachineRoot file = mac.getSCMachineRoot();
		
		ISCEvent[] scEvents = getSCEvents(file, IEvent.INITIALISATION);
		
		containsParameters(scEvents[0]);
		containsGuards(scEvents[0], emptyEnv, makeSList(), makeSList());
		
		hasMarker(e.getParameters()[0]);
		hasMarker(e.getGuards()[0]);
	}
	
	/*
	 * initialisation must not have guards
	 */
	public void testEvents_18_initialisationGuardProblem() throws Exception {
		IMachineRoot mac = createMachine("mac");

		ITypeEnvironment typeEnvironment = factory.makeTypeEnvironment();
		typeEnvironment.addName("x", factory.makeIntegerType());

		addVariables(mac, "x");
		addInvariants(mac, makeSList("I1"), makeSList("x∈ℕ"));
		IEvent e = addEvent(mac, IEvent.INITIALISATION, makeSList(), 
				makeSList("G1", "G2"), makeSList("x∈ℕ", "⊤"), 
				makeSList(), makeSList());
	
		saveRodinFileOf(mac);
		
		runBuilder();
		
		ISCMachineRoot file = mac.getSCMachineRoot();
		
		ISCEvent[] scEvents = getSCEvents(file, IEvent.INITIALISATION);
		
		containsGuards(scEvents[0], emptyEnv, makeSList(), makeSList());
		
		hasMarker(e.getGuards()[0]);
		hasMarker(e.getGuards()[1]);
	}
	
	/*
	 * nondeterministic assignments can only refer to post-state of variables
	 * occurring on their left hand sides
	 */
	public void testEvents_19_actionFaultyNondetAssignment() throws Exception {
		IMachineRoot mac = createMachine("mac");

		ITypeEnvironment typeEnvironment = factory.makeTypeEnvironment();
		typeEnvironment.addName("x", factory.makeIntegerType());

		addVariables(mac, "x");
		addInvariants(mac, makeSList("I1"), makeSList("x∈ℕ"));
		IEvent e = addEvent(mac, "evt", makeSList(), 
				makeSList(), makeSList(), 
				makeSList("A1"), makeSList("x :∣ y'=x"));
	
		saveRodinFileOf(mac);
		
		runBuilder();
		
		ISCMachineRoot file = mac.getSCMachineRoot();
		
		ISCEvent[] scEvents = getSCEvents(file, "evt");
		
		containsActions(scEvents[0], emptyEnv, makeSList(), makeSList());
	
		hasMarker(e.getActions()[0]);
	}
	
	/*
	 * nondetermistic initialisation ok
	 */
	public void testEvents_20_initialisationNondetAssignmentOK() throws Exception {
		IMachineRoot mac = createMachine("mac");

		ITypeEnvironment typeEnvironment = factory.makeTypeEnvironment();
		typeEnvironment.addName("x", factory.makeIntegerType());

		addVariables(mac, "x");
		addInvariants(mac, makeSList("I1"), makeSList("x∈ℕ"));
		addEvent(mac, IEvent.INITIALISATION, makeSList(), 
				makeSList(), makeSList(), 
				makeSList("A1"), makeSList("x :∣ x'=1"));
	
		saveRodinFileOf(mac);
		
		runBuilder();
		
		ISCMachineRoot file = mac.getSCMachineRoot();
		
		ISCEvent[] scEvents = getSCEvents(file, IEvent.INITIALISATION);
		
		containsActions(scEvents[0], typeEnvironment, makeSList("A1"), makeSList("x :∣ x'=1"));
		
		containsMarkers(mac, false);
	}
	
	/*
	 * faulty initialisation replaced by default initialisation
	 */
	public void testEvents_21_initialisationNondetAssignmentReplacedByDefault() throws Exception {
		IMachineRoot mac = createMachine("mac");

		ITypeEnvironment typeEnvironment = factory.makeTypeEnvironment();
		typeEnvironment.addName("x", factory.makeIntegerType());

		addVariables(mac, "x");
		addInvariants(mac, makeSList("I1"), makeSList("x∈ℕ"));
		IEvent e = addEvent(mac, IEvent.INITIALISATION, makeSList(), 
				makeSList(), makeSList(), 
				makeSList("A1"), makeSList("x :∣ x=1"));
	
		saveRodinFileOf(mac);
		
		runBuilder();
		
		ISCMachineRoot file = mac.getSCMachineRoot();
		
		ISCEvent[] scEvents = getSCEvents(file, IEvent.INITIALISATION);
		
		containsActions(scEvents[0], typeEnvironment, makeSList("GEN"), makeSList("x :∣ ⊤"));
		
		hasMarker(e.getActions()[0]);
	}
	
	/*
	 * creation of default initialisation
	 */
	public void testEvents_22_initialisationDefaultAssignment() throws Exception {
		IMachineRoot mac = createMachine("mac");

		ITypeEnvironment typeEnvironment = factory.makeTypeEnvironment();
		typeEnvironment.addName("x", factory.makeIntegerType());

		addVariables(mac, "x");
		addInvariants(mac, makeSList("I1"), makeSList("x∈ℕ"));
		IEvent e = addEvent(mac, IEvent.INITIALISATION, makeSList(), 
				makeSList(), makeSList(), 
				makeSList(), makeSList());
	
		saveRodinFileOf(mac);
		
		runBuilder();
		
		ISCMachineRoot file = mac.getSCMachineRoot();
		
		ISCEvent[] scEvents = getSCEvents(file, IEvent.INITIALISATION);
		
		containsActions(scEvents[0], typeEnvironment, makeSList("GEN"), makeSList("x :∣ ⊤"));
		
		hasMarker(e);
	}
	
	/*
	 * creation of default witness for initialisation
	 */
	public void testEvents_23_initialisationDefaultWitness() throws Exception {
		IMachineRoot abs = createMachine("abs");

		addVariables(abs, "x");
		addInvariants(abs, makeSList("I1"), makeSList("x∈ℕ"));
		addEvent(abs, IEvent.INITIALISATION, makeSList(), 
				makeSList(), makeSList(), 
				makeSList("A1"), makeSList("x:∣x'=1"));
	
		saveRodinFileOf(abs);
		
		runBuilder();
		
		IMachineRoot mac = createMachine("mac");
		addMachineRefines(mac, "abs");

		ITypeEnvironment typeEnvironment = factory.makeTypeEnvironment();
		typeEnvironment.addName("x", factory.makeIntegerType());
		typeEnvironment.addName("x'", factory.makeIntegerType());
		typeEnvironment.addName("y", factory.makeIntegerType());

		addVariables(mac, "y");
		addInvariants(mac, makeSList("I1"), makeSList("y∈ℕ"));
		IEvent e = addEvent(mac, IEvent.INITIALISATION, makeSList(), 
				makeSList(), makeSList(), 
				makeSList("A1"), makeSList("x:∣x'=2"));
	
		saveRodinFileOf(mac);
		
		runBuilder();
		
		ISCMachineRoot file = mac.getSCMachineRoot();
		
		ISCEvent[] scEvents = getSCEvents(file, IEvent.INITIALISATION);
		
		containsWitnesses(scEvents[0], typeEnvironment, makeSList("x'"), makeSList("⊤"));
		
		hasMarker(e);
		
	}
	
	/*
	 * faulty witness in initialisation replaced by default witness
	 */
	public void testEvents_24_initialisationFaultyWitnessReplacedByDefault() throws Exception {
		IMachineRoot abs = createMachine("abs");

		addVariables(abs, "x");
		addInvariants(abs, makeSList("I1"), makeSList("x∈ℕ"));
		addEvent(abs, IEvent.INITIALISATION, makeSList(), 
				makeSList(), makeSList(), 
				makeSList("A1"), makeSList("x:∣x'∈{1}"));
	
		saveRodinFileOf(abs);
		
		runBuilder();
		
		IMachineRoot mac = createMachine("mac");
		addMachineRefines(mac, "abs");

		ITypeEnvironment typeEnvironment = factory.makeTypeEnvironment();
		typeEnvironment.addName("x", factory.makeIntegerType());
		typeEnvironment.addName("x'", factory.makeIntegerType());
		typeEnvironment.addName("y", factory.makeIntegerType());

		addVariables(mac, "y");
		addInvariants(mac, makeSList("I1"), makeSList("y∈ℕ"));
		IEvent evt = addEvent(mac, IEvent.INITIALISATION, makeSList(), 
				makeSList(), makeSList(), 
				makeSList("A1"), makeSList("y≔2"));
		addEventWitnesses(evt, makeSList("x'"), makeSList("x'=y−1"));
	
		saveRodinFileOf(mac);
		
		runBuilder();
		
		ISCMachineRoot file = mac.getSCMachineRoot();
		
		ISCEvent[] scEvents = getSCEvents(file, IEvent.INITIALISATION);
		
		containsWitnesses(scEvents[0], typeEnvironment, makeSList("x'"), makeSList("⊤"));
		
		hasMarker(evt.getWitnesses()[0]);
	}

}

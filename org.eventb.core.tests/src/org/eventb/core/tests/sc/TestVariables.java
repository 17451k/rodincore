/*******************************************************************************
 * Copyright (c) 2006, 2014 ETH Zurich and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     ETH Zurich - initial API and implementation
 *     Systerel - separation of file and root element
 *     Universitaet Duesseldorf - added theorem attribute
 *     Systerel - use marker matcher
 *******************************************************************************/
package org.eventb.core.tests.sc;

import static org.eventb.core.EventBAttributes.ASSIGNMENT_ATTRIBUTE;
import static org.eventb.core.EventBAttributes.IDENTIFIER_ATTRIBUTE;
import static org.eventb.core.EventBAttributes.PREDICATE_ATTRIBUTE;
import static org.eventb.core.EventBAttributes.TARGET_ATTRIBUTE;
import static org.eventb.core.sc.GraphProblem.CarrierSetNameImportConflictWarning;
import static org.eventb.core.sc.GraphProblem.ConstantNameImportConflictWarning;
import static org.eventb.core.sc.GraphProblem.ContextOnlyInAbstractMachineWarning;
import static org.eventb.core.sc.GraphProblem.DisappearedVariableRedeclaredError;
import static org.eventb.core.sc.GraphProblem.VariableHasDisappearedError;
import static org.eventb.core.sc.GraphProblem.VariableNameConflictError;
import static org.eventb.core.sc.ParseProblem.TypesDoNotMatchError;
import static org.eventb.core.tests.MarkerMatcher.marker;
import static org.eventb.core.tests.pom.POUtil.mTypeEnvironment;

import org.eventb.core.IContextRoot;
import org.eventb.core.IEvent;
import org.eventb.core.IMachineRoot;
import org.eventb.core.ISCEvent;
import org.eventb.core.ISCInternalContext;
import org.eventb.core.ISCMachineRoot;
import org.eventb.core.ast.ITypeEnvironmentBuilder;
import org.junit.Test;

/**
 * @author Stefan Hallerstede
 *
 */
public class TestVariables extends GenericIdentTest<IMachineRoot, ISCMachineRoot> {
	
	
	
	/**
	 * check type propagation of carrier sets in seeing machine
	 */
	@Test
	public void testVariables_01() throws Exception {
		IContextRoot con = createContext("ctx");

		addCarrierSets(con, makeSList("S1"));
		
		saveRodinFileOf(con);
		
		IMachineRoot mac = createMachine("mac");
		
		addMachineSees(mac, "ctx");

		addVariables(mac, makeSList("V1"));
		addInvariants(mac, makeSList("I1"), makeSList("V1???S1"), false);
		addInitialisation(mac, "V1");

		saveRodinFileOf(mac);
		
		runBuilderCheck();
		
		ITypeEnvironmentBuilder environment = mTypeEnvironment("S1=???(S1); V1=S1",
				factory);
		
		ISCMachineRoot file = mac.getSCMachineRoot();
		
		ISCInternalContext[] contexts = getInternalContexts(file, 1);
		
		containsCarrierSets(contexts[0], "S1");
		
		containsVariables(file, "V1");
		
		containsInvariants(file, environment, makeSList("I1"), makeSList("V1???S1"), false);
	}
	
	/**
	 * name conflict of variable and seen constant: variable removed!
	 */
	@Test
	public void testVariables_02() throws Exception {
		IContextRoot con = createContext("ctx");

		addConstants(con, makeSList("C1"));
		addAxioms(con, makeSList("A1"), makeSList("C1???BOOL"), false);
		
		saveRodinFileOf(con);
		
		IMachineRoot mac = createMachine("mac");
		
		addMachineSees(mac, "ctx");

		addVariables(mac, makeSList("C1"));
		addInvariants(mac, makeSList("I1", "I2"), makeSList("C1??????", "C1=TRUE"), false, false);
		addInitialisation(mac);

		saveRodinFileOf(mac);
		
		runBuilderCheck(
				marker(mac.getVariables()[0], IDENTIFIER_ATTRIBUTE,
						VariableNameConflictError, "C1"),
				marker(mac.getSeesClauses()[0], TARGET_ATTRIBUTE,
						ConstantNameImportConflictWarning, "C1", "ctx"),
				marker(mac.getInvariants()[0], PREDICATE_ATTRIBUTE, 0, 4,
						TypesDoNotMatchError, "???", "BOOL"));
		
		ITypeEnvironmentBuilder environment = mTypeEnvironment("C1=BOOL",
				factory);
		
		ISCMachineRoot file = mac.getSCMachineRoot();
		
		ISCInternalContext[] contexts = getInternalContexts(file, 1);
		
		containsConstants(contexts[0], "C1");
		
		containsVariables(file);
		
		containsInvariants(file, environment, makeSList("I2"), makeSList("C1=TRUE"), false);
	}
	
	/**
	 * variables and invariants are preserved in refinements
	 */
	@Test
	public void testVariables_03() throws Exception {
		IMachineRoot abs = createMachine("abs");
		
		addVariables(abs, makeSList("V1"));
		addInvariants(abs, makeSList("I1"), makeSList("V1??????"), false);
		addInitialisation(abs, "V1");

		saveRodinFileOf(abs);

		IMachineRoot mac = createMachine("mac");
		
		addMachineRefines(mac, "abs");

		addVariables(mac, makeSList("V1"));
		addInitialisation(mac, "V1");

		saveRodinFileOf(mac);
		
		runBuilderCheck();
		
		ITypeEnvironmentBuilder environment = mTypeEnvironment("V1=???",
				factory);

		ISCMachineRoot file = mac.getSCMachineRoot();
				
		containsVariables(file, "V1");
		
		containsInvariants(file, environment, makeSList("I1"), makeSList("V1??????"), false, false);
	}

	/**
	 * A variable that has disappeared and reappears in a later refinement.
	 */
	@Test
	public void testVariables_04() throws Exception {
		final IMachineRoot m0 = createMachine("m0");
		addVariables(m0, "v1");
		addInvariants(m0, makeSList("I1"), makeSList("v1 ??? ???"), true);
		addInitialisation(m0, makeSList("A1"), makeSList("v1 ??? 0"));
		saveRodinFileOf(m0);
		
		final IMachineRoot m1 = createMachine("m1");
		addMachineRefines(m1, "m0");
		addVariables(m1, "v2");
		addInvariants(m1, makeSList("I1"), makeSList("v2 ??? ???"), true);
		addInitialisation(m1, makeSList("A1"), makeSList("v2 ??? 0"));
		saveRodinFileOf(m1);

		final IMachineRoot m2 = createMachine("m2");
		addMachineRefines(m2, "m1");
		addVariables(m2, "v1");
		addInvariants(m2, makeSList("I1"), makeSList("v1 ??? ???"), true);
		addInitialisation(m2, makeSList("A1"), makeSList("v1 ??? 0"));
		saveRodinFileOf(m2);
		
		runBuilderCheck(
				marker(m2.getVariables()[0], IDENTIFIER_ATTRIBUTE,
						DisappearedVariableRedeclaredError, "v1"),
				marker(m2.getInvariants()[0], PREDICATE_ATTRIBUTE, 0, 2,
						VariableHasDisappearedError, "v1"),
				marker(m2.getEvents()[0].getActions()[0], ASSIGNMENT_ATTRIBUTE,
						VariableHasDisappearedError, "v1"));

		final ISCMachineRoot m0c = m0.getSCMachineRoot();
		containsVariables(m0c, "v1");

		final ISCMachineRoot m1c = m1.getSCMachineRoot();
		containsVariables(m1c, "v1", "v2");
		forbiddenVariables(m1c, "v1");
		
		final ISCMachineRoot m2c = m2.getSCMachineRoot();
		containsVariables(m2c, "v1", "v2");
		forbiddenVariables(m2c, "v1", "v2");
		
		ISCEvent[] events = getSCEvents(m2c, IEvent.INITIALISATION);
		containsActions(events[0], emptyEnv, makeSList(), makeSList());
	}
	
	/**
	 * name conflict of variable and seen carrier set (through abstraction):
	 * variable removed!
	 */
	@Test
	public void testVariables_05() throws Exception {
		final IContextRoot con = createContext("ctx");
		addCarrierSets(con, "x");
		saveRodinFileOf(con);

		final IMachineRoot abs = createMachine("abs");
		addMachineSees(abs, "ctx");
		addInitialisation(abs);
		saveRodinFileOf(abs);

		final IMachineRoot mac = createMachine("mac");
		addMachineRefines(mac, "abs");
		addVariables(mac, makeSList("x"));
		addInitialisation(mac);
		saveRodinFileOf(mac);

		runBuilderCheck(
				marker(mac.getRefinesClauses()[0], TARGET_ATTRIBUTE,
						ContextOnlyInAbstractMachineWarning, "ctx"),
				marker(mac.getRefinesClauses()[0], TARGET_ATTRIBUTE,
						CarrierSetNameImportConflictWarning, "x", "ctx"),
				marker(mac.getVariables()[0], IDENTIFIER_ATTRIBUTE,
						VariableNameConflictError, "x"));

		final ISCMachineRoot file = mac.getSCMachineRoot();
		containsContexts(file, "ctx");
		final ISCInternalContext[] contexts = getInternalContexts(file, 1);
		containsCarrierSets(contexts[0], "x");
		containsVariables(file);
		containsNoInvariant(file);
	}

	@Override
	protected IGenericSCTest<IMachineRoot, ISCMachineRoot> newGeneric() {
		return new GenericMachineSCTest(this);
	}

}

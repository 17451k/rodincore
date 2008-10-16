/*******************************************************************************
 * Copyright (c) 2007, 2008 ETH Zurich and others.
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
import org.eventb.core.ISCContextRoot;
import org.eventb.core.ISCEvent;
import org.eventb.core.ISCMachineRoot;
import org.eventb.core.ast.ITypeEnvironment;
import org.eventb.core.ast.Type;

/**
 * Tests for the <code>getTypeEnvironment()</code> methods of the Event-B
 * database.
 * 
 * @author Laurent Voisin
 */
public class TypeEnvironmentTest extends BasicSCTestWithFwdConfig {

	private static Type BOOL = factory.makeBooleanType();
	private static Type ty_S = factory.makeGivenType("S"); 
	private static Type ty_T = factory.makeGivenType("T"); 

	/**
	 * Ensures that the type environment of a single context is correctly
	 * retrieved.
	 */
	public void testContext() throws Exception {
		final IContextRoot ctx = createContext("ctx");
		addCarrierSets(ctx, makeSList("S"));
		addConstants(ctx, "s");
		addAxioms(ctx, makeSList("A"), makeSList("s ∈ S"));
		ctx.getRodinFile().save(null, true);

		runBuilder();
		final ISCContextRoot scCtxFile = ctx.getSCContextRoot();

		final ITypeEnvironment typenv = factory.makeTypeEnvironment();
		typenv.addGivenSet("S");
		typenv.addName("s", ty_S);
		assertEquals("Type environments differ",
				typenv, scCtxFile.getTypeEnvironment(factory));
	}
	
	/**
	 * Ensures that the type environment of a context with an abstraction is
	 * correctly retrieved.
	 */
	public void testContextWithAbstraction() throws Exception {
		final IContextRoot actx = createContext("actx");
		addCarrierSets(actx, makeSList("S"));
		addConstants(actx, "s");
		addAxioms(actx, makeSList("A"), makeSList("s ∈ S"));
		actx.getRodinFile().save(null, true);

		final IContextRoot cctx = createContext("cctx");
		addContextExtends(cctx, "actx");
		addCarrierSets(cctx, makeSList("T"));
		addConstants(cctx, "t");
		addAxioms(cctx, makeSList("A"), makeSList("t ∈ T"));
		cctx.getRodinFile().save(null, true);

		runBuilder();
		final ISCContextRoot scCtxFile = cctx.getSCContextRoot();

		final ITypeEnvironment typenv = factory.makeTypeEnvironment();
		typenv.addGivenSet("S");
		typenv.addName("s", ty_S);
		typenv.addGivenSet("T");
		typenv.addName("t", ty_T);
		assertEquals("Type environments differ",
				typenv, scCtxFile.getTypeEnvironment(factory));
	}
	
	/**
	 * Ensures that the type environment of a single machine is correctly
	 * retrieved.
	 */
	public void testMachine() throws Exception {
		final IMachineRoot mch = createMachine("mch");
		addVariables(mch, "v");
		addInvariants(mch, makeSList("I"), makeSList("v ∈ BOOL"));
		addInitialisation(mch, makeSList("A"), makeSList("v ≔ TRUE"));
		mch.getRodinFile().save(null, true);

		runBuilder();
		final ISCMachineRoot scMchFile = mch.getSCMachineRoot();

		final ITypeEnvironment typenv = factory.makeTypeEnvironment();
		typenv.addName("v", BOOL);
		assertEquals("Type environments differ",
				typenv, scMchFile.getTypeEnvironment(factory));
	}

	/**
	 * Ensures that the type environment of a machine with a sees clause is
	 * correctly retrieved.
	 */
	public void testMachineWithSees() throws Exception {
		final IContextRoot ctx = createContext("ctx");
		addCarrierSets(ctx, makeSList("S"));
		addConstants(ctx, "s");
		addAxioms(ctx, makeSList("A"), makeSList("s ∈ S"));
		ctx.getRodinFile().save(null, true);

		final IMachineRoot mch = createMachine("mch");
		addMachineSees(mch, "ctx");
		addVariables(mch, "v");
		addInvariants(mch, makeSList("I"), makeSList("v ∈ S"));
		addInitialisation(mch, makeSList("A"), makeSList("v ≔ s"));
		mch.getRodinFile().save(null, true);

		runBuilder();
		final ISCMachineRoot scMchFile = mch.getSCMachineRoot();

		final ITypeEnvironment typenv = factory.makeTypeEnvironment();
		typenv.addGivenSet("S");
		typenv.addName("s", ty_S);
		typenv.addName("v", ty_S);
		assertEquals("Type environments differ",
				typenv, scMchFile.getTypeEnvironment(factory));
	}

	/**
	 * Ensures that the type environment of a machine with a sees clause to an
	 * extending context is correctly retrieved.
	 */
	public void testMachineWithSeesExtends() throws Exception {
		final IContextRoot actx = createContext("actx");
		addCarrierSets(actx, makeSList("S"));
		addConstants(actx, "s");
		addAxioms(actx, makeSList("A"), makeSList("s ∈ S"));
		actx.getRodinFile().save(null, true);

		final IContextRoot cctx = createContext("cctx");
		addContextExtends(cctx, "actx");
		addCarrierSets(cctx, makeSList("T"));
		addConstants(cctx, "t");
		addAxioms(cctx, makeSList("A"), makeSList("t ∈ T"));
		cctx.getRodinFile().save(null, true);

		final IMachineRoot mch = createMachine("mch");
		addMachineSees(mch, "cctx");
		addVariables(mch, "v");
		addInvariants(mch, makeSList("I"), makeSList("v ∈ T"));
		addInitialisation(mch, makeSList("A"), makeSList("v ≔ t"));
		mch.getRodinFile().save(null, true);

		runBuilder();
		final ISCMachineRoot scMchFile = mch.getSCMachineRoot();

		final ITypeEnvironment typenv = factory.makeTypeEnvironment();
		typenv.addGivenSet("S");
		typenv.addName("s", ty_S);
		typenv.addGivenSet("T");
		typenv.addName("t", ty_T);
		typenv.addName("v", ty_T);
		assertEquals("Type environments differ",
				typenv, scMchFile.getTypeEnvironment(factory));
	}

	/**
	 * Ensures that the type environment of a machine with an abstraction is
	 * correctly retrieved.
	 */
	public void testMachineWithAbstraction() throws Exception {
		final IMachineRoot amch = createMachine("amch");
		addVariables(amch, "v");
		addInvariants(amch, makeSList("I"), makeSList("v ∈ BOOL"));
		addInitialisation(amch, makeSList("A"), makeSList("v ≔ TRUE"));
		amch.getRodinFile().save(null, true);

		final IMachineRoot cmch = createMachine("cmch");
		addMachineRefines(cmch, "amch");
		addVariables(cmch, "w");
		addInvariants(cmch, makeSList("I"), makeSList("w ∈ BOOL"));
		addInitialisation(cmch, makeSList("A"), makeSList("w ≔ TRUE"));
		cmch.getRodinFile().save(null, true);

		runBuilder();
		final ISCMachineRoot scMchFile = cmch.getSCMachineRoot();

		final ITypeEnvironment typenv = factory.makeTypeEnvironment();
		typenv.addName("v", BOOL);
		typenv.addName("w", BOOL);
		assertEquals("Type environments differ",
				typenv, scMchFile.getTypeEnvironment(factory));
	}

	/**
	 * Ensures that the type environment of a machine with an abstraction and a
	 * sees clause is correctly retrieved.
	 */
	public void testMachineWithSeesAbstraction() throws Exception {
		final IContextRoot actx = createContext("actx");
		addCarrierSets(actx, makeSList("S"));
		addConstants(actx, "s");
		addAxioms(actx, makeSList("A"), makeSList("s ∈ S"));
		actx.getRodinFile().save(null, true);

		final IMachineRoot amch = createMachine("amch");
		addMachineSees(amch, "actx");
		addVariables(amch, "v");
		addInvariants(amch, makeSList("I"), makeSList("v ∈ S"));
		addInitialisation(amch, makeSList("A"), makeSList("v ≔ s"));
		amch.getRodinFile().save(null, true);

		final IContextRoot cctx = createContext("cctx");
		addContextExtends(cctx, "actx");
		addCarrierSets(cctx, makeSList("T"));
		addConstants(cctx, "t");
		addAxioms(cctx, makeSList("A"), makeSList("t ∈ T"));
		cctx.getRodinFile().save(null, true);

		final IMachineRoot cmch = createMachine("cmch");
		addMachineRefines(cmch, "amch");
		addMachineSees(cmch, "cctx");
		addVariables(cmch, "w");
		addInvariants(cmch, makeSList("I"), makeSList("w ∈ T"));
		addInitialisation(cmch, makeSList("A"), makeSList("w ≔ t"));
		cmch.getRodinFile().save(null, true);

		runBuilder();
		final ISCMachineRoot scMchFile = cmch.getSCMachineRoot();

		final ITypeEnvironment typenv = factory.makeTypeEnvironment();
		typenv.addGivenSet("S");
		typenv.addName("s", ty_S);
		typenv.addName("v", ty_S);
		typenv.addGivenSet("T");
		typenv.addName("t", ty_T);
		typenv.addName("w", ty_T);
		assertEquals("Type environments differ",
				typenv, scMchFile.getTypeEnvironment(factory));
	}

	/**
	 * Ensures that the type environment of an event is correctly retrieved.
	 */
	public void testEvent() throws Exception {
		final IMachineRoot mch = createMachine("mch");
		addVariables(mch, "v");
		addInvariants(mch, makeSList("I"), makeSList("v ∈ BOOL"));
		addInitialisation(mch, makeSList("A"), makeSList("v ≔ TRUE"));
		addEvent(mch, "evt", makeSList(), makeSList(), makeSList(),
				makeSList(), makeSList());
		mch.getRodinFile().save(null, true);

		runBuilder();

		final ISCMachineRoot scMchFile = mch.getSCMachineRoot();
		final ITypeEnvironment mchTypenv = scMchFile.getTypeEnvironment(factory);

		final ISCEvent scEvent = getSCEvent(scMchFile, "evt");
		final ITypeEnvironment evtTypenv =
			scEvent.getTypeEnvironment(mchTypenv, factory);

		final ITypeEnvironment typenv = factory.makeTypeEnvironment();
		typenv.addName("v", BOOL);
		assertEquals("Type environments differ", typenv, evtTypenv);

		assertNotSame("The event typenv should be a copy", mchTypenv, evtTypenv);
	}
	
	/**
	 * Ensures that the type environment of an event with a local variable is
	 * correctly retrieved.
	 */
	public void testEventWithLocal() throws Exception {
		final IMachineRoot mch = createMachine("mch");
		addVariables(mch, "v");
		addInvariants(mch, makeSList("I"), makeSList("v ∈ BOOL"));
		addInitialisation(mch, makeSList("A"), makeSList("v ≔ TRUE"));
		addEvent(mch, "evt", makeSList("l"), makeSList("G"), makeSList("l ∈ BOOL"),
				makeSList(), makeSList());
		mch.getRodinFile().save(null, true);

		runBuilder();

		final ISCMachineRoot scMchFile = mch.getSCMachineRoot();
		final ITypeEnvironment mchTypenv = scMchFile.getTypeEnvironment(factory);

		final ISCEvent scEvent = getSCEvent(scMchFile, "evt");
		final ITypeEnvironment evtTypenv =
			scEvent.getTypeEnvironment(mchTypenv, factory);

		final ITypeEnvironment typenv = factory.makeTypeEnvironment();
		typenv.addName("v", BOOL);
		assertEquals("Type environments differ", typenv, mchTypenv);

		typenv.addName("l", BOOL);
		assertEquals("Type environments differ", typenv, evtTypenv);
	}

}

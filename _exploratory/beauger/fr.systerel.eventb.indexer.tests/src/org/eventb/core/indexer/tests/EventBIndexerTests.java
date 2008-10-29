/*******************************************************************************
 * Copyright (c) 2008 Systerel and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Systerel - initial API and implementation
 *******************************************************************************/
package org.eventb.core.indexer.tests;

import org.rodinp.core.IRodinProject;
import org.rodinp.core.index.RodinIndexer;
import org.rodinp.core.tests.AbstractRodinDBTests;

/**
 * @author Nicolas Beauger
 * 
 */
public abstract class EventBIndexerTests extends AbstractRodinDBTests {

	protected static final String EVT1 = "evt1";
	protected static final String IMPORTER = "importer";
	protected static final String EXPORTER = "exporter";
	protected static final String VAR1 = "var1";
	protected static final String PRM1 = "prm1";
	protected static final String CST1 = "cst1";

	protected static final String EMPTY_MACHINE =
			"<?xml version=\"1.0\" encoding=\"UTF-8\"?>"
					+ "<org.eventb.core.machineFile org.eventb.core.configuration=\"org.eventb.core.fwd\" version=\"3\"/>";

	public static final String EMPTY_CONTEXT =
			"<?xml version=\"1.0\" encoding=\"UTF-8\"?>"
					+ "<org.eventb.core.contextFile org.eventb.core.configuration=\"org.eventb.core.fwd\" version=\"1\"/>";

	protected static final String CST_1DECL =
			"<?xml version=\"1.0\" encoding=\"UTF-8\"?>"
					+ "<org.eventb.core.contextFile org.eventb.core.configuration=\"org.eventb.core.fwd\" version=\"1\">"
					+ "<org.eventb.core.constant"
					+ "		name=\"internal_element1\""
					+ "		org.eventb.core.identifier=\"cst1\"/>"
					+ "</org.eventb.core.contextFile>";

	protected static final String CST_1DECL_1REF_AXM =
			"<?xml version=\"1.0\" encoding=\"UTF-8\"?>"
					+ "<org.eventb.core.contextFile org.eventb.core.configuration=\"org.eventb.core.fwd\" version=\"1\">"
					+ "<org.eventb.core.constant"
					+ "		name=\"internal_element1\""
					+ "		org.eventb.core.identifier=\"cst2\"/>"
					+ "<org.eventb.core.axiom"
					+ "		name=\"internal_element1\""
					+ "		org.eventb.core.label=\"axm1\""
					+ "		org.eventb.core.predicate=\"cst2 = 2\"/>"
					+ "</org.eventb.core.contextFile>";

	protected static final String CST_1DECL_1REF_THM =
			"<?xml version=\"1.0\" encoding=\"UTF-8\"?>"
					+ "<org.eventb.core.contextFile org.eventb.core.configuration=\"org.eventb.core.fwd\" version=\"1\">"
					+ "<org.eventb.core.constant"
					+ "		name=\"internal_element1\""
					+ "		org.eventb.core.identifier=\"cst1\"/>"
					+ "<org.eventb.core.theorem"
					+ "		name=\"internal_element1\""
					+ "		org.eventb.core.label=\"thm1\""
					+ "		org.eventb.core.predicate=\"∀i·i∈ℕ ⇒ cst1 = i\"/>"
					+ "</org.eventb.core.contextFile>";

	protected static final String VAR_1DECL =
			"<?xml version=\"1.0\" encoding=\"UTF-8\"?>"
					+ "<org.eventb.core.machineFile org.eventb.core.configuration=\"org.eventb.core.fwd\" version=\"3\">"
					+ "<org.eventb.core.variable"
					+ "		name=\"internal_element1\""
					+ "		org.eventb.core.identifier=\"var1\"/>"
					+ "</org.eventb.core.machineFile>";

	protected static final String VAR_1DECL_1REF_INV =
			"<?xml version=\"1.0\" encoding=\"UTF-8\"?>"
					+ "<org.eventb.core.machineFile org.eventb.core.configuration=\"org.eventb.core.fwd\" version=\"3\">"
					+ "<org.eventb.core.variable"
					+ "		name=\"internal_element1\""
					+ "		org.eventb.core.identifier=\"var1\"/>"
					+ "<org.eventb.core.invariant"
					+ "		name=\"internal_element1\""
					+ "		org.eventb.core.label=\"inv1\""
					+ "		org.eventb.core.predicate=\"var1 = 1\"/>"
					+ "</org.eventb.core.machineFile>";

	protected static IRodinProject project;

	/**
	 * @param name
	 */
	public EventBIndexerTests(String name) {
		super(name);
		RodinIndexer.disableIndexing();
	}

	protected void setUp() throws Exception {
		super.setUp();
		project = createRodinProject("P");
	}

	protected void tearDown() throws Exception {
		deleteProject("P");
		super.tearDown();
	}

}

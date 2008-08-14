/*******************************************************************************
 * Copyright (c) 2008 University of Southampton.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.eventb.core.tests.versions;

import org.eventb.core.IMachineFile;
import org.eventb.core.IParameter;
import org.eventb.core.IVariable;

/**
 * Version 2 of machine database renames event variables into event parameters
 * 
 * @author Stefan Hallerstede
 *
 */
public class TestEventBVersion_002_M extends EventBVersionTest {
	
	/**
	 * machines of version 1 are updated to machines of version 2;
	 * machine variables are untouched
	 */
	public void testVersion_00_machineVariables() throws Exception {
		String contents = 
			"<?xml version=\"1.0\" encoding=\"UTF-8\"?>" +
			"<org.eventb.core.machineFile version=\"1\" org.eventb.core.configuration=\"org.eventb.core.fwd\">" +
			"<org.eventb.core.variable name=\"x3\" org.eventb.core.identifier=\"a\"/>" +
			"</org.eventb.core.machineFile>";
		String name = "mac.bum";
		createFile(name, contents);
		
		IMachineFile file = (IMachineFile) rodinProject.getRodinFile(name);
		
		convert(file);
		
		IVariable[] variables = file.getVariables();
		
		assertEquals("machine variables modified", 1, variables.length);
		
	}
	
	/**
	 * machines of version 1 are updated to machines of version 2;
	 * event variables are converted into event parameters
	 */
	public void testVersion_01_eventParameters() throws Exception {
		String contents = 
			"<?xml version=\"1.0\" encoding=\"UTF-8\"?>" +
			"<org.eventb.core.machineFile version=\"1\" org.eventb.core.configuration=\"org.eventb.core.fwd\">" +
			"<org.eventb.core.event name=\"x1\" org.eventb.core.convergence=\"0\" org.eventb.core.inherited=\"false\" org.eventb.core.label=\"INITIALISATION\"/>" +
			"<org.eventb.core.event name=\"x2\" org.eventb.core.convergence=\"0\" org.eventb.core.inherited=\"false\" org.eventb.core.label=\"e\">" +
			"<org.eventb.core.variable name=\"x3\" org.eventb.core.identifier=\"a\"/>" +
			"<org.eventb.core.guard name=\"x4\" org.eventb.core.label=\"G\" org.eventb.core.predicate=\"a∈ℤ\"/>" +
			"</org.eventb.core.event>" +
			"</org.eventb.core.machineFile>";
		String name = "mac.bum";
		createFile(name, contents);
		
		IMachineFile file = (IMachineFile) rodinProject.getRodinFile(name);
		
		convert(file);
		
		IParameter[] parameters = file.getEvents()[1].getParameters();
		
		assertEquals("no event parameters", 1, parameters.length);
		
	}
	
	/**
	 * machines of version 1 are updated to machines of version 2;
	 * machine variables are untouched and event variables are converted into event parameters
	 */
	public void testVersion_02_variablesAndParameters() throws Exception {
		String contents = 
			"<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"no\"?>" +
			"<org.eventb.core.machineFile org.eventb.core.configuration=\"org.eventb.core.fwd\" version=\"1\">" +
			"<org.eventb.core.variable name=\"x\" org.eventb.core.identifier=\"a\"/>" +
			"<org.eventb.core.event name=\"x0\" org.eventb.core.convergence=\"0\" org.eventb.core.inherited=\"false\" org.eventb.core.label=\"INITIALISATION\"/>" +
			"<org.eventb.core.event name=\"x1\" org.eventb.core.convergence=\"0\" org.eventb.core.inherited=\"false\" org.eventb.core.label=\"evt1\">" +
			"<org.eventb.core.variable name=\"x2\" org.eventb.core.identifier=\"L1\"/>" +
			"<org.eventb.core.variable name=\"x3\" org.eventb.core.identifier=\"L2\"/>" +
			"</org.eventb.core.event>" +
			"</org.eventb.core.machineFile>";
		String name = "mac.bum";
		createFile(name, contents);
		
		IMachineFile file = (IMachineFile) rodinProject.getRodinFile(name);
		
		convert(file);
		
		IVariable[] variables = file.getVariables();
		
		assertEquals("variables not preserved", 1, variables.length);
		
		IParameter[] parameters = file.getEvents()[1].getParameters();
		
		assertEquals("no event parameters", 2, parameters.length);
	}

}

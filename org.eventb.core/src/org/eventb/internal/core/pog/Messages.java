/*******************************************************************************
 * Copyright (c) 2006 ETH Zurich.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.eventb.internal.core.pog;

import java.text.MessageFormat;

import org.eclipse.osgi.util.NLS;

public final class Messages {

	private static final String BUNDLE_NAME = "org.eventb.internal.core.pog.messages";//$NON-NLS-1$

	// All messages below take one parameter: the handle to the element that
	// caused the error
	// build
	public static String build_cleaning;
	public static String build_runningMPO;
	public static String build_runningCPO;
	public static String build_extracting;
	
	// progress messages
	public static String progress_ContextAxioms;
	public static String progress_ContextTheorems;
	public static String progress_ContextExtends;
	
	public static String progress_MachineInvariants;
	public static String progress_MachineTheorems;
	public static String progress_MachineEvents;
	public static String progress_MachineVariant;
	
	static {
		NLS.initializeMessages(BUNDLE_NAME, Messages.class);
	}

	/**
	 * Bind the given message's substitution locations with the given string values.
	 * 
	 * @param message the message to be manipulated
	 * @param bindings An array of objects to be inserted into the message
	 * @return the manipulated String
	 */
	public static String bind(String message, Object... bindings) {
		return MessageFormat.format(message, bindings);
	}
	
	private Messages() {
		// Do not instantiate
	}
}
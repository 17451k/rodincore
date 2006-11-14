/*******************************************************************************
 * Copyright (c) 2006 ETH Zurich.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.eventb.core;

/**
 * This class lists all attribute names used by the Event-B core plugin.
 * 
 * @author Stefan Hallerstede
 * @author Farhad Mehta
 */
public final class EventBAttributes {

	public static String LABEL_ATTRIBUTE = EventBPlugin.PLUGIN_ID + ".label";
	public static String SOURCE_ATTRIBUTE = EventBPlugin.PLUGIN_ID + ".source";
	public static String COMMENT_ATTRIBUTE = EventBPlugin.PLUGIN_ID + ".comment";
	public static String INHERITED_ATTRIBUTE = EventBPlugin.PLUGIN_ID + ".inherited";
	public static String FORBIDDEN_ATTRIBUTE = EventBPlugin.PLUGIN_ID + ".forbidden";
	public static String PRESERVED_ATTRIBUTE = EventBPlugin.PLUGIN_ID + ".preserved";
	public static String CONVERGENCE_ATTRIBUTE = EventBPlugin.PLUGIN_ID + ".convergence";
	public static String PREDICATE_ATTRIBUTE = EventBPlugin.PLUGIN_ID + ".predicate";
	public static String EXPRESSION_ATTRIBUTE = EventBPlugin.PLUGIN_ID + ".expression";
	public static String ASSIGNMENT_ATTRIBUTE = EventBPlugin.PLUGIN_ID + ".assignment";
	public static String TYPE_ATTRIBUTE = EventBPlugin.PLUGIN_ID + ".type";
	public static String REFINES_ATTRIBUTE = EventBPlugin.PLUGIN_ID + ".refines";
	public static String EXTENDS_ATTRIBUTE = EventBPlugin.PLUGIN_ID + ".extends";
	public static String SEES_ATTRIBUTE = EventBPlugin.PLUGIN_ID + ".sees";
	public static String IDENTIFIER_ATTRIBUTE = EventBPlugin.PLUGIN_ID + ".identifier";

	public static String SCREFINES_ATTRIBUTE = EventBPlugin.PLUGIN_ID + ".scRefines";
	
	public static String PODESC_ATTRIBUTE = EventBPlugin.PLUGIN_ID + ".poDesc";
	public static String POROLE_ATTRIBUTE = EventBPlugin.PLUGIN_ID + ".poRole";
	public static String POSTAMP_ATTRIBUTE = EventBPlugin.PLUGIN_ID + ".poStamp";
	public static String POHINT_ATTRIBUTE = EventBPlugin.PLUGIN_ID + ".poHint";
	public static String PARENT_SET_ATTRIBUTE = EventBPlugin.PLUGIN_ID + ".parentSet";

	// Attributes related to the PR and PS files
	public static String CONFIDENCE_ATTRIBUTE = EventBPlugin.PLUGIN_ID + ".confidence";
	public static String PROOF_VALIDITY_ATTRIBUTE = EventBPlugin.PLUGIN_ID + ".proofValid";
	public static String AUTO_PROOF_ATTRIBUTE = EventBPlugin.PLUGIN_ID + ".autoProof";
	
	private EventBAttributes() {
		// Non-instantiable class
	}

}

/*******************************************************************************
 * Copyright (c) 2005 ETH Zurich.
 * Strongly inspired by org.eclipse.jdt.internal.core.util.Messages.java which is
 * 
 * Copyright (c) 2000, 2005 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.rodinp.internal.core.util;

import java.text.MessageFormat;

import org.eclipse.osgi.util.NLS;

public final class Messages {

	private static final String BUNDLE_NAME = "org.rodinp.internal.core.util.messages";//$NON-NLS-1$

	public static String build_cannotSaveState;
	public static String build_cannotSaveStates;
	public static String build_initializationError;
	public static String build_readStateProgress;
	public static String build_saveStateComplete;
	public static String build_saveStateProgress;
	public static String build_serializationError;
	public static String build_wrongFileFormat;
	public static String build_building;
	public static String build_cleaning;
	public static String build_removing;

	public static String cache_invalidLoadFactor;
	
	public static String element_doesNotExist;
	public static String element_invalidResourceForProject;
	
	public static String operation_cancelled;
	public static String operation_changeElementAttributeProgress;
	public static String operation_changeElementContentsProgress;
	public static String operation_copyElementProgress;
	public static String operation_copyResourceProgress;
	public static String operation_createFileProgress;
	public static String operation_createInternalElementProgress;
	public static String operation_deleteElementProgress;
	public static String operation_deleteResourceProgress;
	public static String operation_moveElementProgress;
	public static String operation_moveResourceProgress;
	public static String operation_needAbsolutePath;
	public static String operation_needElements;
	public static String operation_needName;
	public static String operation_needPath;
	public static String operation_needString;
	public static String operation_notSupported;
	public static String operation_nullContainer;
	public static String operation_pathOutsideProject;
	public static String operation_renameElementProgress;
	public static String operation_renameResourceProgress;
	public static String operation_saveFileProgress;
	
	public static String savedState_jobName;

	public static String status_cannotUseDeviceOnPath;
	public static String status_coreException;
	public static String status_invalidContents;
	public static String status_invalidDestination;
	public static String status_invalidName;
	public static String status_invalidPath;
	public static String status_invalidProject;
	public static String status_invalidResource;
	public static String status_invalidResourceType;
	public static String status_invalidSibling;
	public static String status_IOException;
	public static String status_nameCollision;
	public static String status_noLocalContents;
	public static String status_OK;
	public static String status_readOnly;
	public static String status_updateConflict;

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
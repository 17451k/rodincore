/*******************************************************************************
 * Copyright (c) 2005 ETH Zurich.
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Rodin @ ETH Zurich
 ******************************************************************************/

package org.eventb.internal.ui;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.jface.dialogs.MessageDialog;
import org.rodinp.core.IRodinDBStatus;
import org.rodinp.core.RodinDBException;

/**
 * @author htson
 *         <p>
 *         A main class for handle exceptions within the Event-B UI, given the
 *         intended awareness.
 *         </p>
 */
public class EventBUIExceptionHandler {

	/**
	 * The enumerated type <code>UserAwareness</code> specifies the awareness
	 * level of user in handling exceptions. The order of the awareness levels
	 * in the declaration is irrelevant.
	 */
	public enum UserAwareness {
		IGNORE(0), INFORM(1);

		private final int code;

		UserAwareness(int code) {
			this.code = code;
		}

		public int getCode() {
			return code;
		}

	}

	/**
	 * Utility method for handling exceptions in general. Depending on the types
	 * of the exceptions, call the appropriate utility methods.
	 * 
	 * @param e
	 *            the exception in consideration.
	 * @param msg
	 *            a string message.
	 * @param level
	 *            the level of awareness.
	 */
	private static void handleException(Exception e, String msg,
			UserAwareness level) {
		if (e instanceof RodinDBException) {
			handleRodinDBException((RodinDBException) e, msg, level);
			return;
		}

		if (e instanceof CoreException) {
			handleCoreException((CoreException) e, msg, level);
			return;
		}

		if (UIUtils.DEBUG) {
			System.out.println(msg);
			e.printStackTrace();
		}
		UIUtils.log(e, msg);
	}

	/**
	 * Utility method for handling Core Exceptions (except RodinDB Exceptions). 
	 * 
	 * @param exception
	 *            the exception in consideration.
	 * @param msg
	 *            a string message.
	 * @param level
	 *            the level of awareness.
	 */
	private static void handleCoreException(CoreException exception,
			String msg, UserAwareness level) {
		IStatus status = exception.getStatus();
		if (level == UserAwareness.INFORM) {
			int severity = status.getSeverity();
			switch (severity) {
			case IStatus.ERROR:
				MessageDialog.openError(null, msg, status.getMessage());
				break;
			case IStatus.WARNING:
				MessageDialog.openWarning(null, msg, status.getMessage());
				break;
			case IStatus.INFO:
				MessageDialog.openInformation(null, msg, status.getMessage());
				break;
			}
		}
		if (UIUtils.DEBUG) {
			System.out.println(msg);
			exception.printStackTrace();
		}
		UIUtils.log(exception, msg);
	}

	/**
	 * Utility method for handling exception when accessing Rodin Database.
	 * 
	 * @param exception
	 *            the exception in consideration.
	 * @param msg
	 *            a string message.
	 * @param level
	 *            the level of awareness.
	 */
	private static void handleRodinDBException(RodinDBException exception,
			String msg, UserAwareness level) {
		IRodinDBStatus rodinDBStatus = exception.getRodinDBStatus();
		if (level == UserAwareness.INFORM) {
			int severity = rodinDBStatus.getSeverity();
			switch (severity) {
			case IStatus.ERROR:
				MessageDialog.openError(null, msg, rodinDBStatus.getMessage());
				break;
			case IStatus.WARNING:
				MessageDialog
						.openWarning(null, msg, rodinDBStatus.getMessage());
				break;
			case IStatus.INFO:
				MessageDialog.openInformation(null, msg, rodinDBStatus
						.getMessage());
				break;
			}
			if (UIUtils.DEBUG) {
				System.out.println(msg);
				exception.printStackTrace();
			}
			UIUtils.log(exception, msg);
		}
	}

	/**
	 * Handle exception throws when creating an element with a default level of
	 * awareness as UserAwareness.#INFORM.
	 * 
	 * @param e
	 *            the exception in consideration.
	 */
	public static void handleCreateElementException(Exception e) {
		handleException(e, "Exception throws when creating a new element",
				EventBUIExceptionHandler.UserAwareness.INFORM);
	}

	/**
	 * Handle exception throws when deleting an element with a default level of
	 * awareness as UserAwareness.#INFORM.
	 * 
	 * @param e
	 *            the exception in consideration.
	 */
	public static void handleDeleteElementException(Exception e) {
		handleException(e, "Exception throws when deleting an element",
				EventBUIExceptionHandler.UserAwareness.INFORM);
	}

	/**
	 * Handle exception throws when setting an attribute of an element with a
	 * default level of awareness as UserAwareness.#INFORM.
	 * 
	 * @param e
	 *            the exception in consideration.
	 */
	public static void handleSetAttributeException(Exception e) {
		handleException(e, "Exception throws when setting an attribute",
				EventBUIExceptionHandler.UserAwareness.INFORM);
	}

	/**
	 * Handle exception throws when getting persistent property with a default
	 * level of awareness as UserAwareness.#IGNORE.
	 * 
	 * @param e
	 *            the exception in consideration.
	 */
	public static void handleGetPersistentPropertyException(Exception e) {
		handleException(e, "Exception throws when getting persistent property",
				EventBUIExceptionHandler.UserAwareness.IGNORE);
	}

	/**
	 * Handle exception throws when setting persistent property with a default
	 * level of awareness as UserAwareness.#IGNORE.
	 * 
	 * @param e
	 *            the exception in consideration.
	 */
	public static void handleSetPersistentPropertyException(Exception e) {
		handleException(e, "Exception throws when setting persistent property",
				EventBUIExceptionHandler.UserAwareness.IGNORE);
	}

	/**
	 * Handle exception throws when getting children of an element with a
	 * default level of awareness as UserAwareness.#INFORM.
	 * 
	 * @param e
	 *            the exception in consideration.
	 */
	public static void handleGetChildrenException(Exception e) {
		handleException(e, "Exception throws when getting child elements",
				EventBUIExceptionHandler.UserAwareness.INFORM);
	}

	/**
	 * Handle exception throws when removing an attribute of an element with a
	 * default level of awareness as UserAwareness.#INFORM.
	 * 
	 * @param e
	 *            the exception in consideration.
	 */
	public static void handleRemoveAttribteException(Exception e) {
		handleException(e, "Exception throws when removing element attribute",
				EventBUIExceptionHandler.UserAwareness.INFORM);
	}

	/**
	 * Handle exception throws when getting an attribute of an element with a
	 * default level of awareness as UserAwareness.#INFORM.
	 * 
	 * @param e
	 *            the exception in consideration.
	 */
	public static void handleGetAttributeException(RodinDBException e) {
		handleException(e,
				"Exception throws when getting the value of an attribute",
				EventBUIExceptionHandler.UserAwareness.INFORM);
	}

	/**
	 * Handle exception throws when getting an attribute of an element.
	 * 
	 * @param e
	 *            the exception in consideration.
	 * @param awareness
	 *            the level of awareness.
	 */
	public static void handleGetAttributeException(RodinDBException e,
			UserAwareness awareness) {
		handleException(e,
				"Exception throws when getting the value of an attribute",
				awareness);
	}

	/**
	 * Handle exception throws when apply a tactic with default level of
	 * awareness as UserAwareness.#INFORM.
	 * 
	 * @param e
	 *            the exepction in consideration.
	 */
	public static void handleApplyTacticException(Exception e) {
		handleException(e,
				"Exception throws when applying proof tactic",
				EventBUIExceptionHandler.UserAwareness.INFORM);
	}
	
	/**
	 * Handle exception throws when accessing Rodin DB (in general).
	 * 
	 * @param e
	 *            the exception in consideration.
	 * @param awareness
	 *            the level of awareness.
	 */
	public static void handleRodinException(RodinDBException e,
			UserAwareness awareness) {
		handleException(e, "Exception throws when accessing RodinDB", awareness);
	}

	/**
	 * Handle exception throws when getting children.
	 * 
	 * @param e
	 *            the exception in consideration.
	 * @param awareness
	 *            the level of awareness.
	 */
	public static void handleGetChildrenException(RodinDBException e,
			UserAwareness awareness) {
		handleException(e, "Exception throws when getting child elements",
				awareness);
	}

}

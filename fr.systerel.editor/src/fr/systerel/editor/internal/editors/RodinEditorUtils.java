/*******************************************************************************
 * Copyright (c) 2011, 2017 Systerel and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Systerel - initial API and implementation
 *******************************************************************************/
package fr.systerel.editor.internal.editors;

import static org.eclipse.jface.bindings.keys.SWTKeySupport.convertAcceleratorToKeyStroke;
import static org.eclipse.jface.bindings.keys.SWTKeySupport.convertEventToUnmodifiedAccelerator;
import static org.eventb.ui.EventBUIPlugin.PLUGIN_ID;
import static org.eventb.ui.manipulation.ElementManipulationFacade.getRodinFileUndoContext;

import org.eclipse.core.commands.operations.IOperationHistory;
import org.eclipse.core.commands.operations.IUndoContext;
import org.eclipse.core.expressions.IEvaluationContext;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.bindings.keys.KeyStroke;
import org.eclipse.jface.dialogs.ErrorDialog;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.events.VerifyEvent;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.ISources;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.services.IEvaluationService;
import org.eventb.core.IEventBRoot;
import org.eventb.ui.eventbeditor.IRodinHistory;
import org.eventb.ui.manipulation.ElementManipulationFacade;
import org.rodinp.core.RodinDBException;
import org.rodinp.core.emf.api.itf.ILElement;

import fr.systerel.editor.EditorPlugin;

/**
 * Utility methods for the Rodin Editor.
 */
public class RodinEditorUtils {

	private static final IOperationHistory MAIN_PLATFORM_HISTORY = //
	PlatformUI.getWorkbench().getOperationSupport().getOperationHistory();

	private static final IRodinHistory RODIN_HISTORY = //
	ElementManipulationFacade.getHistory();

	public static final IRodinHistory getRodinHistory() {
		return RODIN_HISTORY;
	}

	public static IOperationHistory getPlatformHistory() {
		return MAIN_PLATFORM_HISTORY;
	}

	public static void flushHistory(RodinEditor rodinEditor) {
		final ILElement root = rodinEditor.getResource().getRoot();
		final IEventBRoot ebRoot = (IEventBRoot) root.getElement();
		final IUndoContext rodinFileContext = getRodinFileUndoContext(ebRoot);
		MAIN_PLATFORM_HISTORY.dispose(rodinFileContext, true, true, false);
	}

	/**
	 * Converts the given SWT event into a JFace keystroke. This method allows
	 * to further use the JFace cross-platform layer to handle keyboard events.
	 * 
	 * @param event
	 *            the {@link VerifyEvent} to be converted as {@link KeyStroke}
	 * @return the keystroke corresponding to the given event
	 */
	public static KeyStroke convertEventToKeystroke(VerifyEvent event) {
		return convertAcceleratorToKeyStroke(convertEventToUnmodifiedAccelerator(event));
	}

	public static void debug(String message) {
		System.out.println(EditorPlugin.DEBUG_PREFIX + message);
	}

	/**
	 * Logs the given status to the Rodin editor plug-in log.
	 */
	public static void log(IStatus status) {
		EditorPlugin.getDefault().getLog().log(status);
	}

	public static void log(Throwable exc, String message) {
		if (exc instanceof RodinDBException) {
			final Throwable nestedExc = ((RodinDBException) exc).getException();
			if (nestedExc != null) {
				exc = nestedExc;
			}
		}
		if (message == null) {
			message = "Unknown context"; //$NON-NLS-1$
		}
		IStatus status = new Status(IStatus.ERROR, EditorPlugin.PLUGIN_ID,
				IStatus.ERROR, message, exc);
		EditorPlugin.getDefault().getLog().log(status);
	}
	

	/**
	 * Opens an information dialog to the user displaying the given message.
	 *  
	 * @param message The dialog message.
	 */
	public static void showInfo(final String message) {
		showInfo(null, message);
	}

	/**
	 * Opens an information dialog to the user displaying the given message.
	 * 
	 * @param title
	 *            The title of the dialog
	 * @param message
	 *            The dialog message
	 */
	public static void showInfo(final String title, final String message) {
		syncExec(new Runnable() {
			@Override
			public void run() {
				MessageDialog.openInformation(getShell(), title, message);
			}
		});

	}

	/**
	 * Opens an information dialog to the user displaying the given message.
	 * 
	 * @param message
	 *            The dialog message.
	 */
	public static boolean showQuestion(final String message) {
		class Question implements Runnable {
			private boolean response;

			@Override
			public void run() {
				response = MessageDialog
						.openQuestion(getShell(), null, message);
			}

			public boolean getResponse() {
				return response;
			}
		}
		final Question question = new Question();
		syncExec(question);
		return question.getResponse();
	}
	
	/**
	 * Opens an error dialog to the user displaying the given message.
	 * 
	 * @param message
	 *            The dialog message displayed
	 * @param title 
	 */
	public static void showError(final String title, final String message) {
		syncExec(new Runnable() {
			@Override
			public void run() {
				MessageDialog.openError(getShell(), title, message);
			}
		});
	}
	
	/**
	 * Opens a warning dialog to the user displaying the given message.
	 * 
	 * @param title
	 *            The title of the dialog window
	 * @param message
	 *            The dialog message displayed
	 * 
	 */
	public static void showWarning(final String title, final String message) {
		syncExec(new Runnable() {
			@Override
			public void run() {
				MessageDialog.openWarning(getShell(), title, message);
			}
		});
	}
	
	/**
	 * Opens an error dialog to the user showing the given unexpected error.
	 * 
	 * @param exc
	 *            The unexpected error.
	 * @param errorMessage
	 *            error message for logging
	 */
	public static void showUnexpectedError(final Throwable exc,
			final String errorMessage) {
		log(exc, errorMessage);
		final IStatus status;
		if (exc instanceof CoreException) {
			IStatus s = ((CoreException) exc).getStatus();
			status = new Status(s.getSeverity(), s.getPlugin(), s.getMessage()
					+ "\n" + errorMessage, s.getException());
		} else {
			final String msg = "Internal error " + errorMessage;
			status = new Status(IStatus.ERROR, PLUGIN_ID, msg, exc);
		}
		syncExec(new Runnable() {
			@Override
			public void run() {
				ErrorDialog.openError(getShell(), null,
						"Unexpected error. See log for details.", status);
			}
		});
	}

	public static Shell getShell() {
		return EditorPlugin.getActiveWorkbenchWindow().getShell();
	}


	public static void syncExec(Runnable runnable) {
		final Display display = PlatformUI.getWorkbench().getDisplay();
		display.syncExec(runnable);
	}


	public static void asyncExec(Runnable runnable) {
		final Display display = PlatformUI.getWorkbench().getDisplay();
		display.asyncExec(runnable);
	}

	public static IEvaluationContext getDefaultEvaluationContext(RodinEditor editor) {
		final IEvaluationService service = editor.getSite().getService(IEvaluationService.class);
		final IEvaluationContext currentState = service.getCurrentState();
		currentState.addVariable(ISources.ACTIVE_FOCUS_CONTROL_ID_NAME, RodinEditor.EDITOR_ID);
		return currentState;
	}

}

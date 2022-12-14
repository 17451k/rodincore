/*******************************************************************************
 * Copyright (c) 2007, 2017 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *     Systerel - adaptation to event-B UI
 *******************************************************************************/
package org.eventb.internal.ui.utils;

import java.util.Map;

import org.eclipse.core.commands.Command;
import org.eclipse.core.commands.CommandEvent;
import org.eclipse.core.commands.ICommandListener;
import org.eclipse.core.commands.ParameterizedCommand;
import org.eclipse.core.commands.common.NotDefinedException;
import org.eclipse.jface.action.Action;
import org.eclipse.swt.widgets.Event;
import org.eclipse.ui.IActionBars;
import org.eclipse.ui.commands.ICommandImageService;
import org.eclipse.ui.commands.ICommandService;
import org.eclipse.ui.handlers.IHandlerService;
import org.eclipse.ui.services.IServiceLocator;
import org.eventb.internal.ui.UIUtils;

/**
 * Instantiate an action that will execute the command.
 * <p>
 * This is a legacy bridge class, and should not be used outside of the
 * framework. Please use menu contributions to display a command in a menu or
 * toolbar.
 * </p>
 * <p>
 * <b>Note:</b> Clients my instantiate, but they must not subclass.
 * </p>
 * <p>
 * This class has been copied from class
 * <code>org.eclipse.ui.internal.actions.CommandAction</code> and then adapted
 * to the event-B UI plug-in for logging messages and fixing Java 5 warnings. It
 * was necessary to make a private copy of this class, because the original
 * class is ignored by
 * {@link IActionBars#setGlobalActionHandler(String, org.eclipse.jface.action.IAction)}
 * .
 * </p>
 * 
 * @since 3.3
 */
public class LegacyCommandAction extends Action {

	private IHandlerService handlerService = null;

	private ParameterizedCommand parameterizedCommand = null;

	private ICommandListener commandListener;

	protected LegacyCommandAction() {

	}

	/**
	 * Creates the action backed by a command. For commands that don't take
	 * parameters.
	 * 
	 * @param serviceLocator
	 *            The service locator that is closest in lifecycle to this
	 *            action.
	 * @param commandIdIn
	 *            the command id. Must not be <code>null</code>.
	 */
	public LegacyCommandAction(IServiceLocator serviceLocator, String commandIdIn) {
		this(serviceLocator, commandIdIn, null);
	}

	/**
	 * Creates the action backed by a parameterized command. The parameterMap
	 * must contain only all required parameters, and may contain the optional
	 * parameters.
	 * 
	 * @param serviceLocator
	 *            The service locator that is closest in lifecycle to this
	 *            action.
	 * @param commandIdIn
	 *            the command id. Must not be <code>null</code>.
	 * @param parameterMap
	 *            the parameter map. May be <code>null</code>.
	 */
	public LegacyCommandAction(IServiceLocator serviceLocator, String commandIdIn,
			Map<?, ?> parameterMap) {
		if (commandIdIn == null) {
			throw new NullPointerException("commandIdIn must not be null"); //$NON-NLS-1$
		}
		init(serviceLocator, commandIdIn, parameterMap);
	}

	protected ICommandListener getCommandListener() {
		if (commandListener == null) {
			commandListener = new ICommandListener() {
				@Override
				public void commandChanged(CommandEvent commandEvent) {
					if (commandEvent.isHandledChanged()
							|| commandEvent.isEnabledChanged()) {
						if (commandEvent.getCommand().isDefined()) {
							setEnabled(commandEvent.getCommand().isEnabled());
						}
					}
				}
			};
		}
		return commandListener;
	}

	/**
	 * Build a command from the executable extension information.
	 * 
	 * @param commandService
	 *            to get the Command object
	 * @param commandId
	 *            the command id for this action
	 * @param parameterMap
	 */
	private void createCommand(ICommandService commandService,
			String commandId, Map<?, ?> parameterMap) {
		Command cmd = commandService.getCommand(commandId);
		if (!cmd.isDefined()) {
			UIUtils.log(null, "Command " + commandId + " is undefined"); //$NON-NLS-1$//$NON-NLS-2$
			return;
		}

		if (parameterMap == null) {
			parameterizedCommand = new ParameterizedCommand(cmd, null);
			return;
		}

		parameterizedCommand = ParameterizedCommand.generateCommand(cmd,
				parameterMap);
	}

	public void dispose() {
		// not important for command ID, maybe for command though.
		handlerService = null;
		if (commandListener != null) {
			parameterizedCommand.getCommand().removeCommandListener(
					commandListener);
			commandListener = null;
		}
		parameterizedCommand = null;
	}

	@Override
	public void runWithEvent(Event event) {
		if (handlerService == null) {
			String commandId = (parameterizedCommand == null ? "unknownCommand" //$NON-NLS-1$
					: parameterizedCommand.getId());
			UIUtils.log(null, "Cannot run " + commandId //$NON-NLS-1$
					+ " before command action has been initialized"); //$NON-NLS-1$
			return;
		}
		try {
			if (parameterizedCommand != null) {
				handlerService.executeCommand(parameterizedCommand, event);
			}
		} catch (Exception e) {
			UIUtils.log(e, "when executing command " + parameterizedCommand);
		}
	}

	@Override
	public void run() {
		// hopefully this is never called
		runWithEvent(null);
	}

	protected void init(IServiceLocator serviceLocator, String commandIdIn,
			Map<?, ?> parameterMap) {
		if (handlerService != null) {
			// already initialized
			return;
		}
		handlerService = serviceLocator
				.getService(IHandlerService.class);
		ICommandService commandService = serviceLocator
				.getService(ICommandService.class);
		ICommandImageService commandImageService = serviceLocator
				.getService(ICommandImageService.class);

		createCommand(commandService, commandIdIn, parameterMap);
		if (parameterizedCommand != null) {
			setId(parameterizedCommand.getId());
			setActionDefinitionId(parameterizedCommand.getId());
			try {
				setText(parameterizedCommand.getName());
			} catch (NotDefinedException e) {
				// if we get this far it shouldn't be a problem
			}
			parameterizedCommand.getCommand().addCommandListener(
					getCommandListener());
			parameterizedCommand.getCommand().setEnabled(
					handlerService.getCurrentState());
			setEnabled(parameterizedCommand.getCommand().isEnabled());
			setImageDescriptor(commandImageService.getImageDescriptor(
					commandIdIn, ICommandImageService.TYPE_DEFAULT));
			setDisabledImageDescriptor(commandImageService.getImageDescriptor(
					commandIdIn, ICommandImageService.TYPE_DISABLED));
			setHoverImageDescriptor(commandImageService.getImageDescriptor(
					commandIdIn, ICommandImageService.TYPE_HOVER));
		}
	}

	protected ParameterizedCommand getParameterizedCommand() {
		return parameterizedCommand;
	}

	@Override
	public String getActionDefinitionId() {
		return super.getActionDefinitionId();
	}
}

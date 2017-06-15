/*******************************************************************************
 * Copyright (c) 2013, 2017 vgheorgh and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     vgheorgh - initial API and implementation
 *******************************************************************************/
package org.eventb.internal.ui.eventbeditor.handlers;

import static org.eclipse.ui.handlers.HandlerUtil.getCurrentSelectionChecked;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eventb.internal.ui.UIUtils;
import org.eventb.internal.ui.eventbeditor.EventBEditorUtils;
import org.eventb.internal.ui.eventbeditor.operations.AtomicOperation;
import org.eventb.internal.ui.eventbeditor.operations.History;
import org.eventb.internal.ui.eventbeditor.operations.OperationFactory;
import org.eventb.ui.EventBUIPlugin;
import org.rodinp.core.IInternalElement;
import org.rodinp.core.IInternalElementType;
import org.rodinp.core.RodinDBException;

/**
 * Adds a new element after the currently selected element in the active
 * EventBEditor.
 * 
 * @author vgheorgh
 */
public class CreateElementHandler extends AbstractHandler {

	/**
	 * @param selection
	 * @return a valid insertion point corresponding to the current selection,
	 *         or null
	 * 
	 *         The given selection must contain a single object.
	 * 
	 *         A valid insertion point is: - an IInternalElement - that has a
	 *         parent which is also an IInternalElement
	 * 
	 *         Note. This does not include the condition that the parent is not
	 *         read-only.
	 * 
	 */
	static public IInternalElement insertionPointForSelection(
			ISelection selection) {

		// If there is no selection or selection is empty then return null.
		if (selection == null || selection.isEmpty()) {
			return null;
		}


		final Object last = getLastSelected(selection);
		if (!(last instanceof IInternalElement)) {
			return null;
		}

		final IInternalElement insertionPoint = (IInternalElement) last;
		// parent must be an internal element
		if (!(insertionPoint.getParent() != null && insertionPoint.getParent() instanceof IInternalElement)) {
			return null;
		}

		return insertionPoint;
	}

	private static Object getLastSelected(ISelection selection) {
		if (selection instanceof IStructuredSelection) {
			final IStructuredSelection ssel = (IStructuredSelection) selection;
			if (ssel.size() > 0) {
				return ssel.toList().get(ssel.size() - 1);
			}
		}
		return null;
	}

	/**
	 * Create new element.
	 * 
	 * @param parent
	 *            Parent of the new element
	 * @param type
	 *            Type of the new element
	 * @param insertionPoint
	 *            An existing child of parent, after which the new element will
	 *            be added. May be null.
	 * 
	 * @throws RodinDBException
	 * 
	 * @pre If insertionPoint is not null, then it must have the same type as
	 *      the new element.
	 * 
	 */
	public static void doExecute(IInternalElement parent,
			IInternalElementType<? extends IInternalElement> type,
			IInternalElement insertionPoint) throws RodinDBException {

		// Check preconditions
		if (!(insertionPoint == null || insertionPoint.getElementType().equals(
				type))) {

			IStatus status = new Status(IStatus.ERROR,
					EventBUIPlugin.PLUGIN_ID,
					"o.e.i.u.eventbeditor.handlers.CreateElementHandler : invalid call");
			UIUtils.log(status);
			return;
		}

		// handle read-only model
		if (EventBEditorUtils.checkAndShowReadOnly(parent)) {
			return;
		}

		final IInternalElement sibling = insertionPoint == null ? null
				: insertionPoint.getNextSibling();

		// perform creation
		final AtomicOperation operation = OperationFactory
				.createElementGeneric(parent, type, sibling);

		History.getInstance().addOperation(operation);
	}

	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException {

		final ISelection selection = getCurrentSelectionChecked(event);

		final IInternalElement insertionPoint = insertionPointForSelection(selection);

		if (insertionPoint == null) {
			throw new ExecutionException("invalid selection");
		}

		final IInternalElement parent = (IInternalElement) insertionPoint
				.getParent();

		try {
			doExecute(parent, insertionPoint.getElementType(), insertionPoint);
		} catch (RodinDBException e) {
			throw new ExecutionException("internal error", e);
		}

		return null;
	}

}

/*******************************************************************************
 * Copyright (c) 2007, 2018 ETH Zurich and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     ETH Zurich - initial API and implementation
 *     Systerel - separation of file and root element
 *******************************************************************************/
package org.eventb.internal.ui.eventbeditor.handlers;

import static org.eclipse.ui.handlers.HandlerUtil.getCurrentSelectionChecked;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eventb.internal.ui.eventbeditor.EventBEditorUtils;
import org.eventb.internal.ui.eventbeditor.operations.AtomicOperation;
import org.eventb.internal.ui.eventbeditor.operations.History;
import org.eventb.internal.ui.eventbeditor.operations.OperationFactory;
import org.rodinp.core.IElementType;
import org.rodinp.core.IInternalElement;
import org.rodinp.core.IInternalElementType;
import org.rodinp.core.IParent;
import org.rodinp.core.IRodinElement;
import org.rodinp.core.RodinDBException;

public abstract class MoveHandler extends AbstractHandler {

	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException {
		final ISelection selection = getCurrentSelectionChecked(event);
		
		// If the selection is not a structured selection then do nothing.
		if (!(selection instanceof IStructuredSelection))
			return null;

		IStructuredSelection ssel = (IStructuredSelection) selection;

		IRodinElement [] elements = getRodinElements(ssel);
		if (elements == null) {
			// selection is not a valid list of RODIN elements
			throw new ExecutionException("Invalid selection");
		}

		// Now, the list of elements should have the same type and has the same
		// parent.
		
		final IInternalElement firstElement = (IInternalElement) elements[0];
		final IInternalElement lastElement = (IInternalElement) elements[elements.length - 1];
		final IInternalElementType<?> type = firstElement.getElementType();

		if ( ! (firstElement.getParent()!= null && firstElement.getParent() instanceof IInternalElement)) {
			throw new ExecutionException("selection has an invalid parent");
		}
		final IInternalElement parent = (IInternalElement)firstElement.getParent(); 
		
		// Enforce read-only model
		if(EventBEditorUtils.checkAndShowReadOnly(parent)) {
			return null;
		}
		
		try {
			IInternalElement[] children = parent.getChildrenOfType(type);
			assert (children.length > 0);
			IInternalElement prevElement = null;
			for (int i = 0; i < children.length; ++i) {
				if (children[i].equals(firstElement))
					break;
				prevElement = children[i];
			}
			IInternalElement nextElement = null;
			for (int i = children.length - 1; i >= 0; --i) {
				if (children[i].equals(lastElement))
					break;
				nextElement = children[i];
			}
			
			AtomicOperation operation = null;
			
			if (getDirection()) {
				if (prevElement != null) {					
					operation =	OperationFactory.move(firstElement.getRoot(),
							prevElement, parent, nextElement);
				}
			} else {
				if (nextElement != null) {
					operation =	OperationFactory.move(firstElement.getRoot(),
							nextElement, parent, firstElement);
				}
			}
			
			if(operation!=null) {
				History.getInstance().addOperation(operation);
			} else {
				// ignore
			}
			
		} catch (RodinDBException e) {
			throw new ExecutionException("Move Operation Failed", e);
		}
		
		return null;
	}

	/**
	 * @return true=up, false=down
	 * 
	 */
	protected abstract boolean getDirection();

	/**
	 * Get the list of Rodin elements contained in the selection. The elements
	 * must have the same parent and must be a consecutive children of the same
	 * type. If not, return <code>null</code>.
	 * 
	 * @param ssel
	 *            a structured selection.
	 * @return a collection of rodin elements or <code>null</code>.
	 */
	private IRodinElement[] getRodinElements(IStructuredSelection ssel) {
		Collection<IRodinElement> elements = new ArrayList<IRodinElement>();
		IParent parent = null;
		IElementType<?> type = null;
		for (Iterator<?> it = ssel.iterator(); it.hasNext();) {
			Object obj = it.next();
			if (!(obj instanceof IRodinElement))
				return null;
			IRodinElement element = (IRodinElement) obj;
			IParent newParent = (IParent) element.getParent();
			if (parent == null) {
				parent = newParent;
				type = element.getElementType();
				elements.add(element);
			} else {
				if (!newParent.equals(parent)) {
					return null;
				} else {
					elements.add(element);
				}
			}
		}
		
		// No elements
		if (parent == null)
			return null;
		
		IRodinElement[] array = elements.toArray(new IRodinElement[elements.size()]);
		try {
			IRodinElement [] children = parent.getChildrenOfType(type);
			int i = 0;
			for (i = 0; i < children.length; i++) {
				if (children[i].equals(array[0]))
					break;
			}
			if (i + array.length > children.length)
				return null;
			for (int j = 1; j < array.length; j++) {
				if (!array[j].equals(children[i+j])) {
					return null;
				}
			}
		} catch (RodinDBException e) {
			return null;
		}
		return array;
	}

}

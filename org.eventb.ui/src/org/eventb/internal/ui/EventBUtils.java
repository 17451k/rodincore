/*******************************************************************************
 * Copyright (c) 2008 ETH Zurich and others.
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     ETH Zurich - initial API and implementation
 *     Systerel - used getFreeIndex to factorize several methods
 *     Systerel - replaced inherited by extended
 *     Systerel - added getImplicitChildren(), refactored getAbstractEvent()
 *     Systerel - separation of file and root element
 *******************************************************************************/
package org.eventb.internal.ui;

import java.util.LinkedList;
import java.util.List;

import org.eventb.core.EventBAttributes;
import org.eventb.core.IAction;
import org.eventb.core.IEvent;
import org.eventb.core.IEventBProject;
import org.eventb.core.IGuard;
import org.eventb.core.ILabeledElement;
import org.eventb.core.IMachineRoot;
import org.eventb.core.IParameter;
import org.eventb.core.IRefinesEvent;
import org.eventb.core.IRefinesMachine;
import org.rodinp.core.IElementType;
import org.rodinp.core.IInternalElement;
import org.rodinp.core.IInternalElementType;
import org.rodinp.core.IInternalParent;
import org.rodinp.core.IParent;
import org.rodinp.core.IRodinElement;
import org.rodinp.core.RodinDBException;

/**
 * @author htson
 *         <p>
 *         This class contains utility (static) methods for manipulating Event-B
 *         elements.
 *         </p>
 */
public class EventBUtils {

	private static final IInternalElement[] NO_ELEMENTS = new IInternalElement[0];

	/**
	 * Gets the abstract machine of an event-B machine. This is done by checking
	 * the lists of refines machine clause of the input file. The input is
	 * assumed to be not <code>null</code>.
	 * 
	 * @param concrete
	 *            a Rodin File
	 * @return the abstract file corresponding to the input file. If there are
	 *         no refines machine or there are more than 1 refine machines then
	 *         <code>null</code> is returned. Otherwise, the handle to the
	 *         file corresponding the refine machine will be returned.
	 * @throws RodinDBException
	 *             if there are some problems in reading the refines machine
	 *             clause or in getting the abstract machine.
	 */
	public static IMachineRoot getAbstractMachine(IMachineRoot concrete)
			throws RodinDBException {
		assert concrete != null;
		IRodinElement[] refines = concrete
				.getChildrenOfType(IRefinesMachine.ELEMENT_TYPE);
		if (refines.length == 1) {
			IRefinesMachine refine = (IRefinesMachine) refines[0];
			String name = refine.getAbstractMachineName();
			IEventBProject prj = (IEventBProject) concrete.getRodinProject()
					.getAdapter(IEventBProject.class);
			return (IMachineRoot) prj.getMachineFile(name).getRoot();
		}
		return null;
	}

	/**
	 * Get the first child of an input parent having the specified type and
	 * label.
	 * 
	 * @param <T>
	 *            an internal element class (i.e. extends
	 *            {@link IInternalElement}.
	 * @param parent
	 *            the internal parent ({@link IInternalParent}).
	 * @param type
	 *            the type of the child ({@link IInternalElementType}).
	 * @param label
	 *            the label of the child that we are looking for.
	 * @return the child of the input parent with the input type and having the
	 *         label as the input label.
	 * @throws RodinDBException
	 *             if some problems occur in getting the list of child elements
	 *             or the label attributes of child elements.
	 */
	private static <T extends ILabeledElement> T getFirstChildOfTypeWithLabel(
			IParent parent, IInternalElementType<T> type, String label)
			throws RodinDBException {
		for (T child : parent.getChildrenOfType(type)) {
			if (child.hasAttribute(EventBAttributes.LABEL_ATTRIBUTE)
					&& label.equals(child.getLabel()))
				return child;
		}
		return null;
	}

	/**
	 * Gets the abstract event of an event. This is done by getting the abstract
	 * component and reading the refines event clause.
	 * 
	 * @param event
	 *            an input event
	 * @return the abstract event corresponding to the input event or
	 *         <code>null</code>. Returns <code>null</code> in the
	 *         following cases:
	 *         <ul>
	 *         <li>If the abstract machine does not exist.
	 *         <li>If there is no abstract machine corresponding to the file
	 *         contains the machine containing the input event.
	 *         <li>If there is no refines event child (except for INITIALISATION).
	 *         <li>If there are more than one refines event child.
	 *         <li>if there is no abstract event corresponding to the refines
	 *         event clause.
	 *         </ul>
	 * @see #getAbstractMachine(IMachineRoot)
	 * @throws RodinDBException
	 *             if some problems occur in getting the abstract file or
	 *             reading the refines event child.
	 */
	public static IEvent getAbstractEvent(IEvent event) throws RodinDBException {
		final IRodinElement parent = event.getParent();
		assert parent instanceof IMachineRoot;

		final IMachineRoot abs = getAbstractMachine((IMachineRoot) parent);
		if (abs == null || !abs.exists()) {
			return null;
		}

		final IInternalElementType<IEvent> type = IEvent.ELEMENT_TYPE;
		final String label = getAbstractEventLabel(event);
		if (label == null) {
			return null;
		}
		return getFirstChildOfTypeWithLabel(abs, type, label);
	}

	private static String getAbstractEventLabel(IEvent event)
			throws RodinDBException {
		if (event.getLabel().equals(IEvent.INITIALISATION)) {
			return IEvent.INITIALISATION;
		}
		final IRefinesEvent[] refinesClauses = event.getRefinesClauses();
		if (refinesClauses.length == 1) {
			return refinesClauses[0].getAbstractEventLabel();
		}
		return null;
	}

	/**
	 * Returns the children of the abstractions of the given event that are
	 * implicitly inherited through extension.
	 * <p>
	 * The children returned are sorted with the children of the most abstract
	 * event first. The order of children in each event is preserved.
	 * </p>
	 * 
	 * @param event
	 *            an event
	 * @return an array of all children that are implicitly inherited by the
	 *         given event through extension
	 * @throws RodinDBException
	 *             if some problems occurs
	 */
	public static IInternalElement[] getImplicitChildren(IEvent event)
			throws RodinDBException {
		final LinkedList<IRodinElement> result = new LinkedList<IRodinElement>();
		while (event.hasExtended() && event.isExtended()) {
			event = getAbstractEvent(event);
			if (event == null) {
				// No abstraction!
				break;
			}
			prependInheritedChildren(result, event);
		}
		final int size = result.size();
		if (size == 0) {
			return NO_ELEMENTS;
		}
		return result.toArray(new IInternalElement[size]);
	}

	private static void prependInheritedChildren(List<IRodinElement> result,
			IEvent event) throws RodinDBException {
		final IRodinElement[] children = event.getChildren();
		final int length = children.length;
		for (int i = length - 1; 0 <= i; --i) {
			final IRodinElement child = children[i];
			if (isInherited(child)) {
				result.add(0, child);
			}
		}
	}

	private static boolean isInherited(IRodinElement child) {
		IElementType<?> type = child.getElementType();
		return type == IParameter.ELEMENT_TYPE || type == IGuard.ELEMENT_TYPE
				|| type == IAction.ELEMENT_TYPE;
	}

	/**
	 * Get a free child name (internal name) for a new child element, given the
	 * parent element, the type of the child element and a proposed prefix for
	 * the name. A new unique name will be the prefix with a index appended to
	 * the end.
	 * 
	 * @param <T>
	 *            an internal element class (i.e. extends
	 *            {@link IInternalElement}.
	 * @param parent
	 *            the internal parent ({@link IInternalParent}).
	 * @param type
	 *            the type of the child ({@link IInternalElementType}).
	 * @param prefix
	 *            the proposed prefix for the child internal name.
	 * @return the new unique name for the child of the input parent which has
	 *         the input type.
	 * @throws RodinDBException
	 *             if some problems occur.
	 */
	public static <T extends IInternalElement> String getFreeChildName(
			IInternalParent parent, IInternalElementType<T> type, String prefix)
			throws RodinDBException {
		return prefix + getFreeChildNameIndex(parent, type, prefix);
	}



	/**
	 * Get a free index for a new child element, given the parent element, the
	 * type of the child element and a proposed prefix for the name. A new free
	 * index will be the index appended to so that the name by appending the
	 * index to the input prefix is also new. The index will be the smallest
	 * available index starting from the input beginIndex.
	 * 
	 * @param <T>
	 *            an internal element class (i.e. extends
	 *            {@link IInternalElement}.
	 * @param parent
	 *            the internal parent ({@link IInternalParent}).
	 * @param type
	 *            the type of the child ({@link IInternalElementType}).
	 * @param prefix
	 *            the proposed prefix for the child internal name.
	 * @return the new free index for the child of the input parent which has
	 *         the input type.
	 * @throws RodinDBException
	 *             if some problems occur.
	 */
	public static <T extends IInternalElement> String getFreeChildNameIndex(
			IInternalParent parent, IInternalElementType<T> type,
			String prefix) throws RodinDBException {
		return UIUtils.getFreePrefixIndex(parent, type, null, prefix);		
	}

}

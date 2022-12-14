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
package org.rodinp.core.emf.lightcore.sync;

import static org.rodinp.core.emf.lightcore.sync.SynchroManager.implicitLoad;
import static org.rodinp.core.emf.lightcore.sync.SynchroManager.loadInternalElementFor;

import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.eclipse.emf.common.notify.Adapter;
import org.eclipse.emf.common.notify.Notification;
import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.common.util.EMap;
import org.eclipse.emf.common.util.TreeIterator;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.InternalEObject;
import org.eclipse.emf.ecore.impl.ENotificationImpl;
import org.eclipse.emf.ecore.util.EContentAdapter;
import org.rodinp.core.IAttributeType;
import org.rodinp.core.IAttributeValue;
import org.rodinp.core.IInternalElement;
import org.rodinp.core.IInternalElementType;
import org.rodinp.core.IRodinElement;
import org.rodinp.core.RodinDBException;
import org.rodinp.core.emf.api.itf.ILElement;
import org.rodinp.core.emf.lightcore.Attribute;
import org.rodinp.core.emf.lightcore.InternalElement;
import org.rodinp.core.emf.lightcore.LightElement;
import org.rodinp.core.emf.lightcore.LightObject;
import org.rodinp.core.emf.lightcore.LightcoreFactory;
import org.rodinp.core.emf.lightcore.adapters.DeltaRootAdapter;
import org.rodinp.core.emf.lightcore.adapters.DeltaRootAdapterFactory;
import org.rodinp.core.emf.lightcore.adapters.ImplicitDeltaRootAdapterFactory;

/**
 * Utility class for EMF//Rodin synchronisation
 */
public class SynchroUtils {

	public static void loadAttributes(IInternalElement iElement,
			LightElement lElement, boolean silent) {
		final HashSet<IAttributeType> availableTypes;
		try {
			availableTypes = new HashSet<IAttributeType>(Arrays.asList(iElement
					.getAttributeTypes()));
			keepKnownAttributes(lElement, availableTypes);
			for (IAttributeType type : availableTypes) {
				final IAttributeValue value = iElement.getAttributeValue(type);
				final Attribute lAttribute = LightcoreFactory.eINSTANCE
						.createAttribute();
				try {
					if (silent)
						lAttribute.eSetDeliver(false);
					lAttribute.setEOwner(lElement);
					lAttribute.setType(type);
					lAttribute.setValue(value.getValue());
					lElement.getEAttributes().put(type.getId(), lAttribute);
					addParentEContentAdapter(lElement, lAttribute);
				} finally {
					lAttribute.eSetDeliver(true);
				}
			}
		} catch (RodinDBException e) {
			System.out.println("Could not load the attributes for the "
					+ "UI model from the element " + iElement.toString() + " "
					+ e.getMessage());
		}
	}
	
	public static void addParentEContentAdapter(ILElement parent, LightObject e) {
		for (Adapter ad : ((LightElement) parent).eAdapters()) {
			final EList<Adapter> adapters = e.eAdapters();
			if (ad instanceof EContentAdapter && !adapters.contains(ad)) {
				e.eAdapters().add(ad);
			}
		}
	}
	
	public static void reloadAttributes(IInternalElement iElement,
			LightElement lElement) {
		loadAttributes(iElement, lElement, false);
	}

	// Used in case of reloading, to remove the attributes which were suppressed
	// from the database from the light model.
	// For instance, the boolean attributes are generally suppressed when they
	// become false.
	private static void keepKnownAttributes(LightElement lElement,
			final Set<IAttributeType> availableTypes) {
		final Set<String> ids = new HashSet<String>();
		for (IAttributeType t : availableTypes) {
			ids.add(t.getId());
		}
		final EMap<String, Attribute> attrs = lElement.getEAttributes();
		final Set<String> toDelete = new HashSet<String>(attrs.keySet());
		toDelete.removeAll(ids);
		for (String key : toDelete) {
			attrs.removeKey(key);
		}
	}

	public static LightElement findElement(IRodinElement toFind,
			LightElement root) {
		if (toFind.equals(root.getElement()))
			return root;
		final TreeIterator<EObject> eAllContents = root.eAllContents();
		while (eAllContents.hasNext()) {
			final EObject next = eAllContents.next();
			if (next instanceof LightElement
					&& ((LightElement) next).getElement().equals(toFind)) {
				return (LightElement) next;
			}
		}
		return null;
	}

	public static ILElement findElement(IRodinElement toFind, ILElement root) {
		return findElement(toFind, (LightElement)root);
	}
	
	public static LightElement findChildElement(IRodinElement toFind,
			LightElement parent) {
		if (toFind != null)
			for (LightElement elem : parent.getEChildren()) {
				if (toFind.equals(elem.getERodinElement()))
					return elem;
			}
		return null;
	}
	
	/**
	 * Returns the position of the given element among the children of its type.
	 * @param parent
	 * 			the root element that shall contain the element
	 * @param element
	 * 			the element we search the position for
	 * @return
	 * 			the position of the given element among the children of its type
	 * or <code>-1</code> if the element has not been found
	 * @throws RodinDBException
	 */
	public static int getPositionOf(LightElement parent, IInternalElement element)
			throws RodinDBException {
		int pos = -1;
		if (element == null) {
			return pos;
		}
		LightElement found = null;
		for (LightElement e : parent.getEChildren()) {
			if (element.equals(e.getElement())) {
				found = e;
				break;
			}
		}
		if (found != null) {
			pos = parent.getEChildren().indexOf(found);
		}
		return pos;
	}

	/**
	 * Returns the position of the <code>element</code>'s next sibling in the
	 * list of all the children of the given <code>parent</code>
	 */
	public static int getPositionAmongSiblings(LightElement parent,
			IInternalElement element) throws RodinDBException {
		final IInternalElement nextSibling = getNextSibling(parent, element);
		if (nextSibling == null) {
			return -1;
		}
		return getPositionOf(parent, nextSibling);
	}

	/**
	 * Returns the next sibling of the given <code>element</code>.
	 */
	public static IInternalElement getNextSibling(LightElement parent,
			IInternalElement element) throws RodinDBException {
		final IInternalElementType<? extends IInternalElement> elementType = element
				.getElementType();
		final IInternalElement[] childrenOfType = parent.getElement()
				.getChildrenOfType(elementType);
		for (int i = 0; i < childrenOfType.length - 1; i++) {
			if (childrenOfType[i].equals(element)) {
				return childrenOfType[i + 1];
			}
		}
		return null;
	}
	
	public static int getPosFromNextSiblingPos(LightElement moved,
			LightElement parent) {
		try {
			final IInternalElement ns = getNextSibling(parent,
					moved.getElement());
			if (ns == null)
				return parent.getChildren().size() - 1;
			final LightElement lns = findElement(ns, parent);
			final int ins = parent.getEChildren().indexOf(lns);
			final int im = parent.getEChildren().indexOf(moved);
			if (im > ins) // if the next sibling is before the moved element
						  // we take the place of the next sibling
				return ins;
			else
				// otherwise we take the place just before
				return ins - 1;
		} catch (RodinDBException e) {
			return parent.getChildren().size() - 1;
		}
	}

	public static void adaptRootForDBChanges(LightElement e) {
		final DeltaRootAdapterFactory f = new DeltaRootAdapterFactory();
		if (e.isEIsRoot()) {
			f.adapt(e, DeltaRootAdapter.class);
		}
	}

	public static void adaptRootForImplicitChildren(LightElement e) {
		final ImplicitDeltaRootAdapterFactory f = new ImplicitDeltaRootAdapterFactory();
		if (e.isEIsRoot()) {
			f.adapt(e, DeltaRootAdapterFactory.class);
		}
	}

	public static Map<IInternalElement, LightElement> getImplicitChildren(
			LightElement parent) {
		final Map<IInternalElement, LightElement> result //
		= new HashMap<IInternalElement, LightElement>();
		for (final LightElement child : parent.getEChildren()) {
			if (child.isImplicit()) {
				result.put(child.getElement(), child);
			}
		}
		return result;
	}

	public static InternalElement loadModel(IInternalElement iParent,
			boolean adaptRoot, boolean silent) {
		final InternalElement parent = loadInternalElementFor(iParent, null);
		final boolean deliver = parent.eDeliver();
		final boolean shallDeliver = (deliver || !silent);
		parent.eSetDeliver(shallDeliver);
		implicitLoad(parent, iParent);
		if (iParent.isRoot() && adaptRoot) {
			adaptRootForDBChanges(parent);
		}
		try {
			for (IRodinElement ichild : iParent.getChildren()) {
				if (ichild instanceof IInternalElement) {
					final IInternalElement iInChild = (IInternalElement) ichild;
					recursiveLoad(parent, iParent, iInChild);
				}
			}
		} catch (RodinDBException e) {
			System.out.println("Could not create children of the UI"
					+ " model for the element " + iParent.toString() + " "
					+ e.getMessage());
		}
		parent.eSetDeliver(deliver);
		return parent;
	}

	public static void recursiveLoad(LightElement parent,
			IInternalElement iParent, IInternalElement child)
			throws RodinDBException {
		final LightElement eRoot = parent.getERoot();
		final InternalElement lChild = loadInternalElementFor(child, eRoot);
		parent.getEChildren().add(lChild);
		implicitLoad(lChild, child);
		for (IRodinElement ichild : child.getChildren()) {
			if (ichild instanceof IInternalElement)
				recursiveLoad(lChild, child, (IInternalElement) ichild);
		}
		addParentEContentAdapter(parent, lChild);
	}
	
	public static void reloadElement(LightElement parent) {
		final IInternalElement iParent = parent.getElement();
		final boolean deliver = parent.eDeliver();
		parent.eSetDeliver(false);
		parent.getEChildren().clear();
		try {
			for (IRodinElement child : iParent.getChildren()) {
				if (child instanceof IInternalElement) {
					final IInternalElement iChild = (IInternalElement) child;
					recursiveLoad(parent, iParent, iChild);
				}
			}
		} catch (RodinDBException e) {
			System.out.println("Could not reload the element"
					+ iParent.toString() + " " + e.getMessage());
		} finally {
			parent.eSetDeliver(deliver);
			parent.eNotify(new ENotificationImpl((InternalEObject) parent,
					Notification.REMOVE_MANY, null, null, null,
					Notification.NO_INDEX, true));
		}
	}

}

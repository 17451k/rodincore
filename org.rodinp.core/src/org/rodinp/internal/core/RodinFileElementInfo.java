/*******************************************************************************
 * Copyright (c) 2005, 2008 ETH Zurich.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     ETH Zurich - initial API and implementation
 *     Systerel - added clearChildren() method
 *     Systerel - removed deprecated methods (contents)
 *******************************************************************************/
package org.rodinp.internal.core;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.jobs.ISchedulingRule;
import org.rodinp.core.IAttributeType;
import org.rodinp.core.IInternalElementType;
import org.rodinp.core.IInternalParent;
import org.rodinp.core.IRodinDBStatusConstants;
import org.rodinp.core.IRodinElement;
import org.rodinp.core.RodinDBException;
import org.rodinp.core.basis.InternalElement;
import org.rodinp.core.basis.RodinElement;
import org.rodinp.core.basis.RodinFile;
import org.w3c.dom.Element;

public class RodinFileElementInfo extends OpenableElementInfo {
	
	public static boolean DEBUG = false;

	// Buffer associated to this Rodin file
	private Buffer buffer;
	
	// Tells whether the list of children is up to date
	private boolean childrenUpToDate;
	
	// Local cache of internal element informations
	private Map<InternalElement, InternalElementInfo> internalCache;
	
	// Map of internal elements inside this file (at any depth)
	// All accesses to this field must be synchronized.
	private Map<InternalElement, Element> internalElements;

	public RodinFileElementInfo() {
		super();
		this.internalElements = new HashMap<InternalElement, Element>();
		this.internalCache = new HashMap<InternalElement, InternalElementInfo>();
	}

	private void addToMap(InternalElement element, Element domElement) {
		internalElements.put(element, domElement);
	}
	
	public synchronized RodinElement[] clearChildren(IInternalParent element)
			throws RodinDBException {

		if (DEBUG) {
			System.out.println("--- CLEAR ---");
			System.out.println("Destination file "
					+ buffer.getOwner().getResource());
			if (element instanceof InternalElement) {
				printInternalElement("element: ", (InternalElement) element);
			} else {
				System.out.println("root element");
			}
			printCaches();
		}

		final Element domElement = getDOMElementCheckExists(element);
		final RodinElementInfo info;
		if (element instanceof InternalElement) {
			info = getElementInfo((InternalElement) element);
		} else {
			info = this;
		}
		final RodinElement[] children = info.getChildren();
		for (final RodinElement child : children) {
			removeFromMap((InternalElement) child);
		}
		buffer.deleteElementChildren(domElement);
		info.setChildren(RodinElement.NO_ELEMENTS);

		if (DEBUG) {
			printCaches();
			System.out.println("--- END OF CLEAR ---");
		}
		return children;
	}

	private void checkDOMElementForCollision(IInternalParent element)
			throws RodinDBException {
		
		Element domElement = getDOMElement(element);
		if (domElement != null) {
			throw ((RodinElement) element).newRodinDBException(
					new RodinDBStatus(
							IRodinDBStatusConstants.NAME_COLLISION, 
							element
					)		
			);
		}
	}
	
	private void computeChildren() {
		final RodinFile element = buffer.getOwner();
		final Element domElement = buffer.getDocumentElement();
		computeChildren(element, domElement, this);
		childrenUpToDate = true;
	}
	
	private void computeChildren(IInternalParent element,
			Element domElement, RodinElementInfo info) {
		
		LinkedHashMap<InternalElement, Element> childrenMap = 
			buffer.getChildren(element, domElement);
		internalElements.putAll(childrenMap);
		info.setChildren(childrenMap.keySet());
	}
	
	public synchronized long getVersion() throws RodinDBException {
		return buffer.getVersion();
	}

	public synchronized void setVersion(long version) throws RodinDBException {
		buffer.setVersion(version);
	}
	
	public synchronized boolean containsDescendant(InternalElement element) {
		return getDOMElement(element) != null;
	}
	
	// dest must be an element of the Rodin file associated to this info.
	// TODO check for sourceInfo parameter removal
	public synchronized void copy(InternalElement source,
			InternalElementInfo sourceInfo, InternalElement dest,
			InternalElement nextSibling) throws RodinDBException {
		
		assert source.getElementType() == dest.getElementType();

		if (DEBUG) {
			System.out.println("--- COPY ---");
			System.out.println("Destination file " + buffer.getOwner().getResource());
			printInternalElement("source: ", source);
			printInternalElement("dest: ", dest);
			printInternalElement("nextSibling: ", nextSibling);
			printCaches();
		}
		
		// TODO fix big mess below.  Should synchronize properly
		// and distinguish betweem two cases.
		RodinFile rfSource = source.getRodinFile();
		RodinFileElementInfo rfSourceInfo = 
			(RodinFileElementInfo) rfSource.getElementInfo(null);

		final Element domSource = rfSourceInfo.getDOMElement(source);
		final IInternalParent destParent = (IInternalParent) dest.getParent();
		final Element domDestParent = getDOMElementCheckExists(destParent);
		checkDOMElementForCollision(dest);
		final Element domNextSibling;
		if (nextSibling != null) {
			domNextSibling = getDOMElementCheckExists(nextSibling);
		} else {
			domNextSibling = null;
		}
		final String newName = dest.getElementName();
		final Element newDOMElement = 
			buffer.importNode(domSource, domDestParent, domNextSibling, newName);

		addToMap(dest, newDOMElement);
		addToParentInfo(dest, nextSibling);

		if (DEBUG) {
			printCaches();
			System.out.println("--- END OF COPY ---");
		}
	}

	public synchronized void create(InternalElement newElement,
			InternalElement nextSibling) throws RodinDBException {
		
		if (DEBUG) {
			System.out.println("--- CREATE ---");
			System.out.println("Destination file " + buffer.getOwner().getResource());
			printInternalElement("newElement: ", newElement);
			printInternalElement("nextSibling: ", nextSibling);
			printCaches();
		}

		IInternalParent parent = (IInternalParent) newElement.getParent();
		Element domParent = getDOMElementCheckExists(parent);
		checkDOMElementForCollision(newElement);
		Element domNextSibling = null;
		if (nextSibling != null) {
			domNextSibling = getDOMElementCheckExists(nextSibling);
		}		
		final IInternalElementType<?> type = newElement.getElementType();
		final String name = newElement.getElementName();
		final Element domNewElement =
			buffer.createElement(type, name, domParent, domNextSibling);
		
		addToMap(newElement, domNewElement);
		addToParentInfo(newElement, nextSibling);

		if (DEBUG) {
			printCaches();
			System.out.println("--- END OF CREATE ---");
		}
	}

	public synchronized void delete(InternalElement element)
			throws RodinDBException {
		
		if (DEBUG) {
			System.out.println("--- DELETE ---");
			System.out.println("Destination file " + buffer.getOwner().getResource());
			printInternalElement("element: ", element);
			printCaches();
		}

		final Element domElement = getDOMElementCheckExists(element);
		removeFromMap(element);
		buffer.deleteElement(domElement);
		removeFromParentInfo(element);

		if (DEBUG) {
			printCaches();
			System.out.println("--- END OF DELETE ---");
		}
	}

	public synchronized IAttributeType[] getAttributeTypes(
			IInternalParent element) throws RodinDBException {
		Element domElement = getDOMElementCheckExists(element);
		String[] rawAttrNames = buffer.getAttributeNames(domElement);
		ElementTypeManager manager = ElementTypeManager.getInstance();
		ArrayList<IAttributeType> result = new ArrayList<IAttributeType>(
				rawAttrNames.length);
		for (String attrName: rawAttrNames) {
			final AttributeType type = manager.getAttributeType(attrName);
			if (type != null) {
				result.add(type);
			}
		}
		return result.toArray(new IAttributeType[result.size()]);
	}

	public synchronized String getAttributeRawValue(IInternalParent element,
			String attrName) throws RodinDBException {
		Element domElement = getDOMElementCheckExists(element);
		String result = buffer.getAttributeRawValue(domElement, attrName);
		if (result == null) {
			throw new RodinDBException(
					new RodinDBStatus(
							IRodinDBStatusConstants.ATTRIBUTE_DOES_NOT_EXIST,
							element,
							attrName
					)
			);
		}
		return result;
	}

	@Override
	public synchronized RodinElement[] getChildren() {
		if (! childrenUpToDate) {
			computeChildren();
		}
		return super.getChildren();
	}

	/**
	 * Returns the DOM element corresponding to the given Rodin element.
	 * 
	 * @param element
	 *            a Rodin file or internal element
	 * @return the corresponding DOM element or <code>null</code> if
	 *         inexistent
	 */
	private Element getDOMElement(IInternalParent element) {
		if (element instanceof RodinFile) {
			return buffer.getDocumentElement();
		}
		Element result = internalElements.get(element);
		if (result != null) {
			assert result.getParentNode() != null;
			return result;
		}
		
		// Not found, force a cache update
		IRodinElement parent = element.getParent();
		if (parent instanceof InternalElement) {
			getElementInfo((InternalElement) parent);
		} else if (! childrenUpToDate) {
			computeChildren();
		}
		return internalElements.get(element);
	}

	private Element getDOMElementCheckExists(IInternalParent element)
			throws RodinDBException {
		
		Element domElement = getDOMElement(element);
		if (domElement == null) {
			throw ((RodinElement) element).newNotPresentException();
		}
		return domElement;
	}

	public synchronized InternalElementInfo getElementInfo(
			InternalElement element) {
		
		InternalElementInfo info = internalCache.get(element);
		if (info != null) {
			return info;
		}
		Element domElement = getDOMElement(element);
		if (domElement == null) {
			return null;
		}
		info = new InternalElementInfo();
		computeChildren(element, domElement, info);
		return info;
	}

	public synchronized boolean hasAttribute(IInternalParent element,
			IAttributeType type) throws RodinDBException {
		Element domElement = getDOMElementCheckExists(element);
		return buffer.hasAttribute(domElement, type.getId());
	}

	@Override
	public synchronized boolean hasUnsavedChanges() {
		return buffer.hasUnsavedChanges();
	}

	// Returns true if parse was successful
	public synchronized boolean parseFile(IProgressMonitor pm,
			RodinFile rodinFile) throws RodinDBException {
		
		final RodinDBManager rodinDBManager = RodinDBManager.getRodinDBManager();
		buffer = rodinDBManager.getBuffer(rodinFile);
		buffer.load(pm);
		return true;
	}
	
	private void printCaches() {
		System.out.println("Keys of internalCache:");
		printSet(internalCache.keySet());

		System.out.println("Keys of internalElements:");
		printSet(internalElements.keySet());
	}

	private void printSet(Set<InternalElement> entries) {
		for (InternalElement entry: entries) {
			printInternalElement("  ", entry);
		}
	}
	
	private void printInternalElement(String prefix, InternalElement elem) {
		System.out.println(prefix
				+ (elem == null ? "<null>" : elem.toStringWithAncestors()));
	}

	public synchronized boolean removeAttribute(IInternalParent element,
			IAttributeType attrType) throws RodinDBException {
		Element domElement = getDOMElementCheckExists(element);
		return buffer.removeAttribute(domElement, attrType.getId());
	}

	// Removes an element and all its descendants from the cache map.
	private void removeFromMap(InternalElement element) {
		final Element domElement = internalElements.remove(element);
		if (domElement == null) {
			// This element is not cached, nor its children
			return;
		}

		final InternalElementInfo info = internalCache.remove(element);
		if (info != null) {
			for (IRodinElement child: info.getChildren()) {
				removeFromMap((InternalElement) child);
			}
			return;
		}
		
		final LinkedHashMap<InternalElement, Element> childrenMap = buffer
				.getChildren(element, domElement);
		for (IRodinElement child: childrenMap.keySet()) {
			removeFromMap((InternalElement) child);
		}
	}

	/**
	 * Renames an element within this file.
	 * 
	 * @param source
	 *            the source element
	 * @param dest
	 *            the destination element
	 * @throws RodinDBException 
	 */
	public synchronized void rename(InternalElement source,
			InternalElement dest) throws RodinDBException {

		assert source.getParent().equals(dest.getParent());
		assert source.getClass() == dest.getClass();
		
		if (DEBUG) {
			System.out.println("--- RENAME ---");
			System.out.println("Destination file " + buffer.getOwner().getResource());
			printInternalElement("source: ", source);
			printInternalElement("dest: ", dest);
			printCaches();
		}

		Element domElement = getDOMElementCheckExists(source);
		checkDOMElementForCollision(dest);
		removeFromMap(source);
		buffer.renameElement(domElement, dest.getElementName());
		addToMap(dest, domElement);
		changeInParentInfo(source, dest);

		if (DEBUG) {
			printCaches();
			System.out.println("--- END OF RENAME ---");
		}
	}

	// Returns true iff a change was made to the order of the parent children.
	public synchronized boolean reorder(InternalElement source,
			InternalElement nextSibling) throws RodinDBException {
		
		assert nextSibling == null
				|| source.getParent().equals(nextSibling.getParent());
		
		if (DEBUG) {
			System.out.println("--- REORDER ---");
			System.out.println("Destination file " + buffer.getOwner().getResource());
			printInternalElement("source: ", source);
			printInternalElement("nextSibling: ", nextSibling);
			printCaches();
		}

		Element domSource = getDOMElementCheckExists(source);

		Element domNextSibling = null;
		if (nextSibling != null) {
			domNextSibling = getDOMElementCheckExists(nextSibling);
			assert domNextSibling.getParentNode().isSameNode(
					domSource.getParentNode());
		}
		boolean changed = buffer.reorderElement(domSource, domNextSibling);
		if (changed) {
			moveInParentInfo(source, nextSibling);
		}

		if (DEBUG) {
			printCaches();
			System.out.println("--- END OF REORDER ---");
		}

		return changed;
	}

	public synchronized void saveToFile(RodinFile rodinFile, boolean force,
			boolean keepHistory, ISchedulingRule rule, IProgressMonitor pm) throws RodinDBException {
		
		buffer.save(force, keepHistory, rule, pm);
	}

	public synchronized void setAttributeRawValue(IInternalParent element,
			String attrName, String newRawValue) throws RodinDBException {
		Element domElement = getDOMElementCheckExists(element);
		buffer.setAttributeRawValue(domElement, attrName, newRawValue);
	}

	// Parent info management methods.
	//
	// These methods update the parent information if it exists.
	
	/*
	 * Returns the element info associated to the parent of the given element
	 * is it has already been computed, otherwise null. 
	 */
	private RodinElementInfo peekParentInfo(InternalElement element) {
		final RodinElement parent = element.getParent();
		if (parent instanceof InternalElement) {
			return internalCache.get(parent);
		}
		if (childrenUpToDate) {
			return this;
		}
		return null;
	}

	private void addToParentInfo(InternalElement child, InternalElement next) {
		final RodinElementInfo parentInfo = peekParentInfo(child);
		if (parentInfo != null) {
			parentInfo.addChildBefore(child, next);
		}		
	}

	private void changeInParentInfo(InternalElement source, InternalElement dest) {
		internalCache.remove(source);
		final RodinElementInfo parentInfo = peekParentInfo(source);
		if (parentInfo != null) {
			parentInfo.changeChild(source, dest);
		}
	}

	private void moveInParentInfo(InternalElement child, InternalElement next) {
		final RodinElementInfo parentInfo = peekParentInfo(child);
		if (parentInfo != null) {
			parentInfo.moveChildBefore(child, next);
		}
	}

	private void removeFromParentInfo(InternalElement child) {
		internalCache.remove(child);
		final RodinElementInfo parentInfo = peekParentInfo(child);
		if (parentInfo != null) {
			parentInfo.removeChild(child);
		}
	}

}

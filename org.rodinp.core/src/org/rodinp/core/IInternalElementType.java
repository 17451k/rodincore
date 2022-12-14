/*******************************************************************************
 * Copyright (c) 2006, 2014 ETH Zurich and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     ETH Zurich - initial API and implementation
 *     Systerel - add database relations
 *******************************************************************************/
package org.rodinp.core;

/**
 * Common protocol for defining and traversing internal element types and
 * attribute types. Element types are the types associated to Rodin internal
 * elements. These types are contributed to the Rodin database through extension
 * point <code>org.rodinp.core.internalElementTypes</code>.
 * <p>
 * This interface also allows to retrieve the possible relationships between
 * internal elements (child-parent) and between elements and attributes. These
 * relationships are defined through the
 * <code>org.rodinp.core.itemRelations</code> extension point.
 * </p>
 * <p>
 * Element type instances are guaranteed to be unique. Hence, two element types
 * can be compared directly using identity (<code>==</code>). Instances can be
 * obtained using the static factory method
 * <code>RodinCore.getInternalElementType()</code>.
 * </p>
 * 
 * @author Laurent Voisin
 * @see RodinCore#getInternalElementType(String)
 * @since 1.0
 * @noimplement This interface is not intended to be implemented by clients.
 * @noextend This interface is not intended to be extended by clients.
 */
public interface IInternalElementType<T extends IInternalElement> extends
		IElementType<T> {

	/**
	 * Returns the types of the internal elements that can parent an element of
	 * this type.
	 * 
	 * @return the types of the internal elements that can parent an element of
	 *         this type
	 * @since 1.7
	 */
	IInternalElementType<?>[] getParentTypes();

	/**
	 * Returns the types of the internal elements that can occur as children of
	 * an internal element of this type.
	 * 
	 * @return the types of the internal elements that can occur as children of
	 *         an internal element of this type
	 * @since 1.7
	 */
	IInternalElementType<?>[] getChildTypes();

	/**
	 * Returns the types of the attributes that internal elements of this type
	 * can carry.
	 * 
	 * @return the types of the attributes that internal elements of this type
	 *         can carry
	 * @since 1.7
	 */
	IAttributeType[] getAttributeTypes();

	/**
	 * Tells whether an internal element of this type can parent an internal
	 * element of the given type.
	 * 
	 * @param childType
	 *            an internal element type
	 * @return <code>true</code> iff an internal element of this type can parent
	 *         an internal element of the given type
	 * @since 1.7
	 */
	boolean canParent(IInternalElementType<?> childType);

	/**
	 * Tells whether an internal element of this type can carry an attribute of
	 * the given type.
	 * 
	 * @param attributeType
	 *            an attribute type
	 * @return <code>true</code> iff an internal element of this type can carry
	 *         an attribute of the given type
	 * @since 1.7
	 */
	boolean canCarry(IAttributeType attributeType);

	/**
	 * Returns whether this element type is ubiquitous.
	 * <p>
	 * Ubiquitous element types can have any parent type.
	 * </p>
	 * 
	 * @return <code>true</code> if ubiquitous, <code>false</code> otherwise
	 * @since 1.7
	 */
	boolean isUbiquitous();

}

/*******************************************************************************
 * Copyright (c) 2005, 2018 ETH Zurich and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     ETH Zurich - initial API and implementation
 *     Systerel - generic attribute manipulation
 *     Systerel - add database relations
 *******************************************************************************/
package org.rodinp.core;

/**
 * Common protocol for all attribute types contributed by clients.
 * <p>
 * Attributes are attached to internal elements (that is elements that are
 * stored in a file element) and provide non-structural information pertaining
 * to that element (for instance the target of a refines clause).
 * </p>
 * <p>
 * Every attribute is associated with an attribute type, which contains the
 * following information:
 * <ul>
 * <li>the id of the attribute type (which is unique),</li>
 * <li>the human-readable name of the attribute type,</li>
 * <li>the kind of the attribute, that is the Java type of the associated
 * attribute values.</li>
 * </ul>
 * The correspondence between attribute values and kinds is the following:
 * <ul>
 * <li><code>boolean</code>: {@link IAttributeType.Boolean}</li>
 * <li><code>IRodinElement</code>: {@link IAttributeType.Handle}</li>
 * <li><code>int</code>: {@link IAttributeType.Integer}</li>
 * <li><code>long</code>: {@link IAttributeType.Long}</li>
 * <li><code>String</code>: {@link IAttributeType.String}</li>
 * </ul>
 * </p>
 * <p>
 * Attribute type instances are guaranteed to be unique. Hence, two attribute
 * types can be compared directly using identity (<code>==</code>). Instances
 * can be obtained using the static factory methods from <code>RodinCore</code>.
 * </p>
 * <p>
 * Moreover, this interface provides a protocol to retrieve and check the
 * relationships existing between attribute and element types.
 * </p>
 * 
 * @author Laurent Voisin
 * 
 * @see RodinCore#getAttributeType(java.lang.String)
 * @see RodinCore#getBooleanAttrType(java.lang.String)
 * @see RodinCore#getHandleAttrType(java.lang.String)
 * @see RodinCore#getIntegerAttrType(java.lang.String)
 * @see RodinCore#getLongAttrType(java.lang.String)
 * @see RodinCore#getStringAttrType(java.lang.String)
 * @since 1.0
 * @noextend This interface is not intended to be extended by clients.
 * @noimplement This interface is not intended to be implemented by clients.
 */
public interface IAttributeType {

	/**
	 * Common protocol for attribute types corresponding to attributes that
	 * carry a boolean value.
	 * <p>
	 * This interface is not intended to be implemented by clients.
	 * </p>
	 *
	 * @noextend This interface is not intended to be extended by clients.
	 * @noimplement This interface is not intended to be implemented by clients.
	 */
	interface Boolean extends IAttributeType {

		/**
		 * Returns a new attribute value for this attribute type with the given
		 * value.
		 * 
		 * @param value
		 *            a value
		 * @return a new attribute value for this type and the given value
		 */
		IAttributeValue.Boolean makeValue(boolean value);

	}

	/**
	 * Common protocol for attribute types corresponding to attributes that
	 * carry a Rodin element handle.
	 * <p>
	 * This interface is not intended to be implemented by clients.
	 * </p>
	 *
	 * @noextend This interface is not intended to be extended by clients.
	 * @noimplement This interface is not intended to be implemented by clients.
	 */
	interface Handle extends IAttributeType {

		/**
		 * Returns a new attribute value for this attribute type with the given
		 * value.
		 * 
		 * @param value
		 *            a value
		 * @return a new attribute value for this type and the given value
		 */
		IAttributeValue.Handle makeValue(IRodinElement value);

	}

	/**
	 * Common protocol for attribute types corresponding to attributes that
	 * carry an <code>int</code> value.
	 * <p>
	 * This interface is not intended to be implemented by clients.
	 * </p>
	 *
	 * @noextend This interface is not intended to be extended by clients.
	 * @noimplement This interface is not intended to be implemented by clients.
	 */
	interface Integer extends IAttributeType {

		/**
		 * Returns a new attribute value for this attribute type with the given
		 * value.
		 * 
		 * @param value
		 *            a value
		 * @return a new attribute value for this type and the given value
		 */
		IAttributeValue.Integer makeValue(int value);

	}

	/**
	 * Common protocol for attribute types corresponding to attributes that
	 * carry a <code>long</code> value.
	 * <p>
	 * This interface is not intended to be implemented by clients.
	 * </p>
	 *
	 * @noextend This interface is not intended to be extended by clients.
	 * @noimplement This interface is not intended to be implemented by clients.
	 */
	interface Long extends IAttributeType {

		/**
		 * Returns a new attribute value for this attribute type with the given
		 * value.
		 * 
		 * @param value
		 *            a value
		 * @return a new attribute value for this type and the given value
		 */
		IAttributeValue.Long makeValue(long value);

	}

	/**
	 * Common protocol for attribute types corresponding to attributes that
	 * carry a string value.
	 * <p>
	 * This interface is not intended to be implemented by clients.
	 * </p>
	 *
	 * @noextend This interface is not intended to be extended by clients.
	 * @noimplement This interface is not intended to be implemented by clients.
	 */
	interface String extends IAttributeType {

		/**
		 * Returns a new attribute value for this attribute type with the given
		 * value.
		 * 
		 * @param value
		 *            a value
		 * @return a new attribute value for this type and the given value
		 */
		IAttributeValue.String makeValue(java.lang.String value);

	}

	/**
	 * Returns the unique id of this attribute type.
	 * 
	 * @return the id of this attribute type
	 */
	java.lang.String getId();

	/**
	 * Returns the human-readable name of this attribute type.
	 * 
	 * @return the name of this attribute type
	 */
	java.lang.String getName();

	/**
	 * Returns the types of internal elements that can carry an attribute of
	 * this type.
	 * 
	 * @return the types of element that can carry an attribute of this type
	 * @since 1.7
	 */
	IInternalElementType<?>[] getElementTypes();

	/**
	 * Tells whether an attribute of this type can be carried by an element of
	 * the given type.
	 * 
	 * @param elementType
	 *            an internal element type
	 * @return <code>true</code> iff an element of the given type can carry an
	 *         attribute of this type
	 * @since 1.7
	 */
	boolean isAttributeOf(IInternalElementType<?> elementType);

	/**
	 * Returns whether this attribute type is ubiquitous.
	 * <p>
	 * Ubiquitous element types can have any parent type.
	 * </p>
	 * 
	 * @return <code>true</code> if ubiquitous, <code>false</code> otherwise
	 * @since 1.7
	 */
	boolean isUbiquitous();

}

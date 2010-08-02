/*******************************************************************************
 * Copyright (c) 2010 Systerel and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Systerel - initial API and implementation
 *******************************************************************************/
package org.eventb.core.ast.extension.datatype;

import java.util.List;

import org.eventb.core.ast.Type;
import org.eventb.core.ast.extension.ITypeMediator;

/**
 * Common protocol for constructor mediators.
 * <p>
 * Instances of this interface are passed as argument to
 * {@link IDatatypeExtension#addConstructors(IConstructorMediator)}. It provides
 * methods for declaring constructors and destructors of a datatype, based upon
 * the definition of their arguments.
 * </p>
 * 
 * @author Nicolas Beauger
 * @since 2.0
 * @noimplement This interface is not intended to be implemented by clients.
 * @noextend This interface is not intended to be extended by clients.
 */
public interface IConstructorMediator extends ITypeMediator {

	/**
	 * Returns the type parameter with the given name, or <code>null</code> if
	 * not found.
	 * 
	 * @param name
	 *            a String
	 * @return a type parameter or <code>null</code>
	 */
	ITypeParameter getTypeParameter(String name);

	/**
	 * Makes a new argument type whose type is the given type parameter.
	 * 
	 * @param type
	 *            a type parameter
	 * @return a new argument type
	 */
	IArgumentType newArgumentType(ITypeParameter type);

	/**
	 * Makes a new argument type whose type is the given type.
	 * 
	 * @param type
	 *            a type
	 * @return a new argument type
	 */
	IArgumentType newArgumentType(Type type);

	/**
	 * Makes a new argument with the given argument type and no associated
	 * destructor.
	 * 
	 * @param type
	 *            a new argument
	 * @return a new argument
	 */
	IArgument newArgument(IArgumentType type);

	/**
	 * Makes a new argument with the given argument type and an associated
	 * destructor of the given name (operator symbol).
	 * 
	 * @param destructorName
	 *            the name of the destructor
	 * @param type
	 *            a new argument
	 * @return a new argument
	 */
	IArgument newArgument(String destructorName, IArgumentType type);

	/**
	 * Makes a new argument type whose type is the powerset of the given
	 * argument type.
	 * 
	 * @param type
	 *            an argument type
	 * @return a new argument type
	 */
	IArgumentType makePowerSetType(IArgumentType type);

	/**
	 * Makes a new argument type whose type is the cartesian product of the
	 * given argument types.
	 * 
	 * @param left
	 *            an argument type
	 * @param right
	 *            an argument type
	 * 
	 * @return a new argument type
	 */
	IArgumentType makeProductType(IArgumentType left, IArgumentType right);

	/**
	 * Makes a new argument type whose type is the relation (powerset of the
	 * cartesian product) of the given argument types.
	 * 
	 * @param left
	 *            an argument type
	 * @param right
	 *            an argument type
	 * @return a new argument type
	 */
	IArgumentType makeRelationalType(IArgumentType left, IArgumentType right);

	/**
	 * @param typeParams
	 *            type parameters
	 * @return a new type constructor argument type
	 */
	IArgumentType newArgumentTypeConstr(List<IArgumentType> typeParams);

	/**
	 * Adds a new constructor with the given name, id but no arguments.
	 * <p>
	 * Same as calling {@link #addConstructor(String, String, List)} with an
	 * empty list.
	 * </p>
	 * 
	 * @param name
	 *            the name of the constructor
	 */
	void addConstructor(String name, String id);

	/**
	 * Adds a new constructor with the given name, id and arguments.
	 * <p>
	 * The number of arguments is determined from the size of the given list.
	 * </p>
	 * 
	 * @param name
	 *            the name of the constructor
	 * @param arguments
	 *            a list of arguments
	 */
	void addConstructor(String name, String id, List<IArgument> arguments);

}

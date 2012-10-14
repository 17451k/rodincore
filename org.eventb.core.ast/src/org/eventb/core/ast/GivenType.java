/*******************************************************************************
 * Copyright (c) 2005, 2012 ETH Zurich and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     ETH Zurich - initial API and implementation
 *     Systerel - implemented specialization
 *******************************************************************************/
package org.eventb.core.ast;

import java.util.Set;

import org.eventb.internal.core.ast.Specialization;
import org.eventb.internal.core.ast.extension.datatype.DatatypeTranslation;

/**
 * Denotes a type defined by the user. For instance, this type can correspond to
 * a carrier-set declared in a context.
 * 
 * @author Laurent Voisin
 * @since 1.0
 * @noextend This class is not intended to be subclassed by clients.
 */
public class GivenType extends Type {
	
	// Name of the carrier-set corresponding to this type.
	private String name;

	/**
	 * Creates a new instance of this type.
	 */
	protected GivenType(String name, FormulaFactory ff) {
		super(true);
		assert ff.isValidIdentifierName(name);
		this.name = name;
	}

	@Override
	protected void addGivenTypes(Set<GivenType> set) {
		set.add(this);
	}
	
	@Override
	protected Expression buildExpression(FormulaFactory factory) {
		return factory.makeFreeIdentifier(
				name, 
				null, 
				factory.makePowerSetType(this));
	}

	@Override
	protected void buildString(StringBuilder buffer) {
		buffer.append(name);
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (! (o instanceof GivenType)) return false;
		GivenType other = (GivenType) o;
		return name.equals(other.name);
	}
	
	/**
	 * Returns the name of the carrier-set corresponding to this type.
	 * 
	 * @return Returns the name of this type
	 */
	public String getName() {
		return name;
	}

	@Override
	public int hashCode() {
		return name.hashCode();
	}
	
	@Override
	public Type specialize(ISpecialization specialization) {
		final Type result = ((Specialization) specialization).get(this);
		if (this.equals(result))
			return this;
		return result;
	}

	/**
	 * Returns the free identifier that denotes the set corresponding to this
	 * given type.
	 * 
	 * @param factory
	 *            factory to use for building the result identifier
	 * @return the set corresponding to this type
	 * @since 2.6
	 */
	@Override
	public FreeIdentifier toExpression(FormulaFactory factory) {
		return (FreeIdentifier) super.toExpression(factory);
	}

	@Override
	public Type translateDatatype(DatatypeTranslation translation) {
		return this; // nothing to translate
	}

}

/*******************************************************************************
 * Copyright (c) 2007 ETH Zurich.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.eventb.core.ast;

/**
 * Implementation of a default filter that does not perform select any
 * sub-formula. Provides a basis for implementing simple filters by
 * sub-classing.
 * <p>
 * Clients may extend this class.
 * </p>
 * 
 * @author Laurent Voisin
 */
public class DefaultFilter implements IFormulaFilter {

	public boolean select(AssociativeExpression expression) {
		return false;
	}

	public boolean select(AssociativePredicate predicate) {
		return false;
	}

	public boolean select(AtomicExpression expression) {
		return false;
	}

	public boolean select(BinaryExpression expression) {
		return false;
	}

	public boolean select(BinaryPredicate predicate) {
		return false;
	}

	public boolean select(BoolExpression expression) {
		return false;
	}

	public boolean select(BoundIdentDecl decl) {
		return false;
	}

	public boolean select(BoundIdentifier identifier) {
		return false;
	}

	public boolean select(FreeIdentifier identifier) {
		return false;
	}

	public boolean select(IntegerLiteral literal) {
		return false;
	}

	public boolean select(LiteralPredicate predicate) {
		return false;
	}

	public boolean select(QuantifiedExpression expression) {
		return false;
	}

	public boolean select(QuantifiedPredicate predicate) {
		return false;
	}

	public boolean select(RelationalPredicate predicate) {
		return false;
	}

	public boolean select(SetExtension expression) {
		return false;
	}

	public boolean select(SimplePredicate predicate) {
		return false;
	}

	public boolean select(UnaryExpression expression) {
		return false;
	}

	public boolean select(UnaryPredicate predicate) {
		return false;
	}

}

/*******************************************************************************
 * Copyright (c) 2005, 2008 ETH Zurich and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     ETH Zurich - initial API and implementation
 *     Systerel - added origin
 *******************************************************************************/
package org.eventb.internal.core.parser;

import org.eventb.core.ast.Assignment;
import org.eventb.core.ast.Expression;
import org.eventb.core.ast.FormulaFactory;
import org.eventb.core.ast.IParseResult;
import org.eventb.core.ast.Predicate;
import org.eventb.core.ast.Type;
import org.eventb.internal.core.ast.AbstractResult;

/**
 * Result of a Parsed formula.
 * 
 * @author François Terrier
 * @author Laurent Voisin 
 */
public class ParseResult extends AbstractResult implements IParseResult {

	// Formula factory to use for building formulas
	protected final FormulaFactory factory;
	
	// Parsed assignment
	private Assignment assignment = null;

	// Parsed expression
	private Expression expression = null;

	// Parsed predicate
	private Predicate predicate = null;

	// Parsed type
	private Type type = null;

	// Origin
	private final Object origin;
	
	public ParseResult(FormulaFactory factory) {
		this.factory = factory;
		this.origin = null;
	}

	public ParseResult(FormulaFactory factory, Object origin) {
		this.factory = factory;
		this.origin = origin;
	}
	
	/*
	 * (non-Javadoc)
	 * @see org.eventb.internal.core.ast.IParseResult#getParsedAssignment()
	 */
	public Assignment getParsedAssignment() {
		if (!isSuccess())
			return null;
		return assignment;
	}

	/*
	 * (non-Javadoc)
	 * @see org.eventb.internal.core.ast.IParseResult#getParsedExpression()
	 */
	public Expression getParsedExpression() {
		if (!isSuccess())
			return null;
		return expression;
	}

	/*
	 * (non-Javadoc)
	 * @see org.eventb.internal.core.ast.IParseResult#getParsedPredicate()
	 */
	public Predicate getParsedPredicate() {
		if (!isSuccess())
			return null;
		return predicate;
	}

	/*
	 * (non-Javadoc)
	 * @see org.eventb.internal.core.ast.IParseResult#getParsedType()
	 */
	public Type getParsedType() {
		if (!isSuccess())
			return null;
		return type;
	}
	
	public Object getOrigin() {
		return origin;
	}

	/**
	 * Sets the parsed formula to null.
	 */
	public void resetParsedFormula() {
		this.predicate = null;
		this.expression = null;
		this.assignment = null;
	}

	/**
	 * Sets the parsed assignment.
	 * 
	 * @param formula
	 *            the assignment that has been parsed
	 */
	public void setParsedAssignment(Assignment formula) {
		this.assignment = formula;
	}

	/**
	 * Sets the parsed expression.
	 * 
	 * @param formula
	 *            the expression that has been parsed
	 */
	public void setParsedExpression(Expression formula) {
		this.expression = formula;
	}

	/**
	 * Sets the parsed predicate.
	 * 
	 * @param formula
	 *            the formula that has been parsed
	 */
	public void setParsedPredicate(Predicate formula) {
		this.predicate = formula;
	}

	/**
	 * Sets the parsed type.
	 * 
	 * @param type
	 *            the type that has been parsed
	 */
	public void setParsedType(Type type) {
		this.type = type;
	}

}

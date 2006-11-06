/*******************************************************************************
 * Copyright (c) 2006 ETH Zurich.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.eventb.core.basis;

import org.eventb.core.ISCAssignmentElement;
import org.eventb.core.ast.Assignment;
import org.eventb.core.ast.FormulaFactory;
import org.eventb.core.ast.IParseResult;
import org.eventb.core.ast.ITypeCheckResult;
import org.eventb.core.ast.ITypeEnvironment;
import org.eventb.internal.core.Messages;
import org.eventb.internal.core.Util;
import org.rodinp.core.IRodinElement;
import org.rodinp.core.RodinDBException;

/**
 * Common implementation of Event-B SC elements that contain an assignment, as
 * an extension of the Rodin database.
 * <p>
 * This class is intended to be subclassed by clients that want to extend this
 * internal element type.
 * </p>
 * <p>
 * This class should not be used in any other way than subclassing it in a
 * database extension. In particular, clients should not use it, but rather use
 * its associated interface <code>ISCAssignmentElement</code>.
 * </p>
 * 
 * @author Stefan Hallerstede
 */
public abstract class SCAssignmentElement extends SCTraceableLabeledElement
		implements ISCAssignmentElement {

	/**
	 *  Constructor used by the Rodin database. 
	 */
	public SCAssignmentElement(String name, IRodinElement parent) {
		super(name, parent);
	}

	public Assignment getAssignment(FormulaFactory factory) throws RodinDBException {
		
		String contents = getAssignmentString();
		IParseResult parserResult = factory.parseAssignment(contents);
		if (parserResult.getProblems().size() != 0) {
			throw Util.newRodinDBException(
					Messages.database_SCAssignmentParseFailure,
					this
			);
		}
		Assignment result = parserResult.getParsedAssignment();
		return result;
	}

	public Assignment getAssignment(
			FormulaFactory factory,
			ITypeEnvironment typenv) throws RodinDBException {
		
		Assignment result = getAssignment(factory);
		ITypeCheckResult tcResult = result.typeCheck(typenv);
		if (! tcResult.isSuccess())  {
			throw Util.newRodinDBException(
					Messages.database_SCAssignmentTCFailure,
					this
			);
		}
		assert result.isTypeChecked();
		return result;
	}

	public void setAssignment(Assignment assignment) throws RodinDBException {
		setContents(assignment.toStringWithTypes());
	}

}

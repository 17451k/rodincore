/*******************************************************************************
 * Copyright (c) 2006 ETH Zurich.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.eventb.core.basis;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eventb.core.ISCPredicateElement;
import org.eventb.core.ast.FormulaFactory;
import org.eventb.core.ast.IParseResult;
import org.eventb.core.ast.ITypeCheckResult;
import org.eventb.core.ast.ITypeEnvironment;
import org.eventb.core.ast.Predicate;
import org.eventb.internal.core.Messages;
import org.eventb.internal.core.Util;
import org.rodinp.core.IRodinElement;
import org.rodinp.core.RodinDBException;

/**
 * Common implementation of Event-B SC elements that contain a predicate, as
 * an extension of the Rodin database.
 * <p>
 * This class is intended to be subclassed by clients that want to extend this
 * internal element type.
 * </p>
 * <p>
 * This class should not be used in any other way than subclassing it in a
 * database extension. In particular, clients should not use it, but rather use
 * its associated interface <code>ISCPredicateElement</code>.
 * </p>
 * 
 * @author Stefan Hallerstede
 */
public abstract class SCPredicateElement extends EventBElement 
implements ISCPredicateElement {

	/**
	 *  Constructor used by the Rodin database. 
	 */
	public SCPredicateElement(String name, IRodinElement parent) {
		super(name, parent);
	}

	public Predicate getPredicate(FormulaFactory factory, IProgressMonitor monitor) 
	throws RodinDBException {
		String contents = getPredicateString(null);
		IParseResult parserResult = factory.parsePredicate(contents);
		if (parserResult.getProblems().size() != 0) {
			throw Util.newRodinDBException(
					Messages.database_SCPredicateParseFailure,
					this
			);
		}
		Predicate result = parserResult.getParsedPredicate();
		return result;
	}

	@Deprecated
	public Predicate getPredicate(FormulaFactory factory) 
	throws RodinDBException {
		return getPredicate(factory, (IProgressMonitor) null);
	}
	
	public Predicate getPredicate(
			FormulaFactory factory,
			ITypeEnvironment typenv, IProgressMonitor monitor) throws RodinDBException {
		
		Predicate result = getPredicate(factory, monitor);
		ITypeCheckResult tcResult = result.typeCheck(typenv);
		if (! tcResult.isSuccess())  {
			throw Util.newRodinDBException(
					Messages.database_SCPredicateTCFailure,
					this
			);
		}
		assert result.isTypeChecked();
		return result;
	}
	
	@Deprecated
	public Predicate getPredicate(
			FormulaFactory factory,
			ITypeEnvironment typenv) throws RodinDBException {
		return getPredicate(factory, typenv, null);
	}
	
	public void setPredicate(Predicate predicate, IProgressMonitor monitor) throws RodinDBException {
		setPredicateString(predicate.toStringWithTypes(), monitor);
	}
	
	@Deprecated
	public void setPredicate(Predicate predicate) throws RodinDBException {
		setPredicate(predicate, null);
	}

}


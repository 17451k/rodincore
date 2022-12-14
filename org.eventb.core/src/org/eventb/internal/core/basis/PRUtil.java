/*******************************************************************************
 * Copyright (c) 2009, 2013 Systerel and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Systerel - initial API and implementation
 *******************************************************************************/
package org.eventb.internal.core.basis;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eventb.core.IPRIdentifier;
import org.eventb.core.ast.Formula;
import org.eventb.core.ast.FormulaFactory;
import org.eventb.core.ast.FreeIdentifier;
import org.eventb.core.ast.ISealedTypeEnvironment;
import org.eventb.core.ast.ITypeEnvironment.IIterator;
import org.eventb.core.ast.ITypeEnvironmentBuilder;
import org.eventb.core.ast.Type;
import org.eventb.core.basis.PRStoredExpr;
import org.eventb.core.basis.PRStoredPred;
import org.rodinp.core.IInternalElement;
import org.rodinp.core.RodinCore;
import org.rodinp.core.RodinDBException;

/**
 * Utility methods shared by {@link PRStoredPred} and {@link PRStoredExpr}.
 * 
 * @author Laurent Voisin
 * @since 1.0
 */
public class PRUtil {

	/**
	 * Returns the type environment to use for reading a stored formula
	 * (predicate or expression).
	 * 
	 * @param ie
	 *            internal element containing the stored formula
	 * @param baseTypenv
	 *            common type environment for all stored formulas
	 * @return the type environment to use for parsing the formula stored in the
	 *         given Rodin element
	 * @throws RodinDBException
	 *             in case of problem accessing the Rodin database, or if a
	 *             component of the resulting type environment is invalid
	 * @since 3.0
	 */
	public static ISealedTypeEnvironment buildTypenv(IInternalElement ie,
			ISealedTypeEnvironment baseTypenv)
			throws CoreException {
		final FormulaFactory ff = baseTypenv.getFormulaFactory();
		final ITypeEnvironmentBuilder result = getLocalTypenv(ie, ff);
		mergeTypenv(result, baseTypenv);
		return result.makeSnapshot();
	}

	private static ITypeEnvironmentBuilder getLocalTypenv(IInternalElement ie,
			FormulaFactory ff) throws CoreException {
		final ITypeEnvironmentBuilder result = ff.makeTypeEnvironment();
		for (final IPRIdentifier child : getPRIdentifiers(ie)) {
			result.add(child.getIdentifier(ff));
		}
		return result;
	}

	private static void mergeTypenv(ITypeEnvironmentBuilder localTypenv,
			ISealedTypeEnvironment baseTypenv) {
		final IIterator it = baseTypenv.getIterator();
		while (it.hasNext()) {
			it.advance();
			final String name = it.getName();
			if (!localTypenv.contains(name)) {
				localTypenv.addName(name, it.getType());
			}
		}
	}

	/**
	 * Stores the local free identifier declarations for the given formula to be
	 * stored in the given Rodin element. This method will remove all children
	 * IPRIdentifiers and create as many IPRIdentifier children as free
	 * identifiers occurring in the given formula that do not belong to the
	 * given base type environment.
	 * 
	 * @param ie
	 *            internal element where the given formula will be stored
	 * @param formula
	 *            the formula to be stored
	 * @param baseTypenv
	 *            common type environment for all stored formulas
	 * @throws RodinDBException
	 *             in case of problem accessing the Rodin database
	 */
	public static void setPRIdentifiers(IInternalElement ie, Formula<?> formula,
			ISealedTypeEnvironment baseTypenv, IProgressMonitor monitor)
			throws RodinDBException {
		removePRIdentifiers(ie, monitor);
		addPRIdentifiers(ie, formula.getFreeIdentifiers(), baseTypenv, monitor);
	}

	private static void removePRIdentifiers(IInternalElement ie,
			IProgressMonitor monitor) throws RodinDBException {
		final IPRIdentifier[] ids = getPRIdentifiers(ie);
		if (ids.length != 0) {
			RodinCore.getRodinDB().delete(ids, true, monitor);
		}
	}

	private static void addPRIdentifiers(IInternalElement ie,
			FreeIdentifier[] idents, ISealedTypeEnvironment baseTypenv,
			IProgressMonitor monitor) throws RodinDBException {
		for (final FreeIdentifier ident : idents) {
			if (!baseTypenv.contains(ident)) {
				createPRIdentifier(ie, ident.getName(), ident.getType(),
						monitor);
			}
		}
	}

	private static void createPRIdentifier(IInternalElement ie, String name,
			Type type, IProgressMonitor monitor) throws RodinDBException {
		final IPRIdentifier prIdent = getPRIdentifier(ie, name);
		prIdent.create(null, monitor);
		prIdent.setType(type, monitor);
	}

	private static IPRIdentifier getPRIdentifier(IInternalElement ie,
			String name) {
		return ie.getInternalElement(IPRIdentifier.ELEMENT_TYPE, name);
	}

	private static IPRIdentifier[] getPRIdentifiers(IInternalElement ie)
			throws RodinDBException {
		return ie.getChildrenOfType(IPRIdentifier.ELEMENT_TYPE);
	}

}

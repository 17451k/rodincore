/*******************************************************************************
 * Copyright (c) 2006 ETH Zurich.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.eventb.internal.core.sc.modules;

import java.util.Collection;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eventb.core.EventBAttributes;
import org.eventb.core.IPredicateElement;
import org.eventb.core.ISCPredicateElement;
import org.eventb.core.ast.FormulaFactory;
import org.eventb.core.ast.FreeIdentifier;
import org.eventb.core.ast.IParseResult;
import org.eventb.core.ast.IResult;
import org.eventb.core.ast.Predicate;
import org.eventb.core.sc.GraphProblem;
import org.rodinp.core.IAttributeType;
import org.rodinp.core.IInternalParent;

/**
 * @author Stefan Hallerstede
 * 
 */
public abstract class PredicateModule<I extends IPredicateElement> extends
		LabeledFormulaModule<Predicate, I> {

	@Override
	protected Predicate[] allocateFormulas(int size) {
		return new Predicate[size];
	}

	@Override
	protected IAttributeType.String getFormulaAttributeType() {
		return EventBAttributes.PREDICATE_ATTRIBUTE;
	}

	@Override
	protected Predicate parseFormula(I formulaElement,
			Collection<FreeIdentifier> freeIdentifierContext,
			FormulaFactory factory) throws CoreException {

		if (!formulaElement.hasPredicateString()) {
			createProblemMarker(formulaElement,
					EventBAttributes.PREDICATE_ATTRIBUTE,
					GraphProblem.PredicateUndefError);
			return null;
		}
		String predicateString = formulaElement.getPredicateString();

		// parse the predicate

		IParseResult parseResult = factory.parsePredicate(predicateString);

		if (!parseResult.isSuccess()) {
			issueASTProblemMarkers(formulaElement, getFormulaAttributeType(),
					parseResult);

			return null;
		}
		Predicate predicate = parseResult.getParsedPredicate();

		// check legibility of the predicate
		// (this will only produce a warning on failure)

		IResult legibilityResult = predicate.isLegible(freeIdentifierContext);

		if (!legibilityResult.isSuccess()) {
			issueASTProblemMarkers(formulaElement, getFormulaAttributeType(),
					legibilityResult);
		}

		return predicate;
	}

	protected final void copySCPredicates(
			ISCPredicateElement[] predicateElements, IInternalParent target,
			IProgressMonitor monitor) throws CoreException {
		for (ISCPredicateElement predicate : predicateElements)
			predicate.copy(target, null, null, false, monitor);
	}

	protected final int createSCPredicates(IInternalParent target,
			String namePrefix, int index, IProgressMonitor monitor)
			throws CoreException {
		int k = index;

		for (int i = 0; i < formulaElements.length; i++) {
			if (formulas[i] == null)
				continue;
			ISCPredicateElement scPredElem = (ISCPredicateElement) symbolInfos[i]
					.createSCElement(target, namePrefix + k++, monitor);
			scPredElem.setPredicate(formulas[i], null);
		}
		return k;
	}

}

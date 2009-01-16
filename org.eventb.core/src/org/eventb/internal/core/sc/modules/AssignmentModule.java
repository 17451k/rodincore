/*******************************************************************************
 * Copyright (c) 2006, 2009 ETH Zurich and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     ETH Zurich - initial API and implementation
 *     Systerel - separation of file and root element
 *******************************************************************************/
package org.eventb.internal.core.sc.modules;

import java.util.Collection;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eventb.core.EventBAttributes;
import org.eventb.core.IAssignmentElement;
import org.eventb.core.ISCAssignmentElement;
import org.eventb.core.ast.Assignment;
import org.eventb.core.ast.FormulaFactory;
import org.eventb.core.ast.FreeIdentifier;
import org.eventb.core.ast.IParseResult;
import org.eventb.core.ast.IResult;
import org.eventb.core.sc.GraphProblem;
import org.rodinp.core.IAttributeType;
import org.rodinp.core.IInternalElement;

/**
 * @author Stefan Hallerstede
 * 
 */
public abstract class AssignmentModule<I extends IInternalElement> extends
		LabeledFormulaModule<Assignment, I> {

	@Override
	protected IAttributeType.String getFormulaAttributeType() {
		return EventBAttributes.ASSIGNMENT_ATTRIBUTE;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eventb.internal.core.sc.modules.LabeledFormulaModule#parseFormula
	 * (int, org.rodinp.core.IInternalElement[], org.eventb.core.ast.Formula[],
	 * java.util.Collection, org.eventb.core.ast.FormulaFactory)
	 */
	@Override
	protected Assignment parseFormula(I formulaElement,
			Collection<FreeIdentifier> freeIdentifierContext,
			FormulaFactory factory) throws CoreException {

		IAssignmentElement assignmentElement = (IAssignmentElement) formulaElement;

		if (!assignmentElement.hasAssignmentString()) {
			createProblemMarker(assignmentElement,
					EventBAttributes.ASSIGNMENT_ATTRIBUTE,
					GraphProblem.AssignmentUndefError);
			return null;
		}

		String assignmentString = assignmentElement.getAssignmentString();

		// parse the assignment

		IParseResult parseResult = factory.parseAssignment(assignmentString);

		if (!parseResult.isSuccess()) {
			issueASTProblemMarkers(assignmentElement,
					EventBAttributes.ASSIGNMENT_ATTRIBUTE, parseResult);

			return null;
		}

		Assignment assignment = parseResult.getParsedAssignment();

		// check legibility of the predicate
		// (this will only produce a warning on failure)

		IResult legibilityResult = assignment.isLegible(freeIdentifierContext);

		if (!legibilityResult.isSuccess()) {
			issueASTProblemMarkers(assignmentElement,
					EventBAttributes.ASSIGNMENT_ATTRIBUTE, legibilityResult);
		}

		return assignment;
	}

	@Override
	protected Assignment[] allocateFormulas(int size) {
		return new Assignment[size];
	}

	protected final int createSCAssignments(IInternalElement target,
			String namePrefix, int index, IProgressMonitor monitor)
			throws CoreException {
		int k = index;

		for (int i = 0; i < formulaElements.length; i++) {
			if (formulas[i] == null)
				continue;
			ISCAssignmentElement scAssnElem = (ISCAssignmentElement) symbolInfos[i]
					.createSCElement(target, namePrefix + k++, monitor);
			scAssnElem.setAssignment(formulas[i], null);
		}
		return k;
	}

}

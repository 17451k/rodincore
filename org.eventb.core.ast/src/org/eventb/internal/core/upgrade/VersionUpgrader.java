/*******************************************************************************
 * Copyright (c) 2009, 2012 Systerel and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Systerel - initial API and implementation
 *******************************************************************************/
package org.eventb.internal.core.upgrade;

import java.util.List;

import org.eventb.core.ast.ASTProblem;
import org.eventb.core.ast.Assignment;
import org.eventb.core.ast.Expression;
import org.eventb.core.ast.Formula;
import org.eventb.core.ast.FormulaFactory;
import org.eventb.core.ast.FreeIdentifier;
import org.eventb.core.ast.IParseResult;
import org.eventb.core.ast.IResult;
import org.eventb.core.ast.LanguageVersion;
import org.eventb.core.ast.Predicate;
import org.eventb.core.ast.ProblemKind;
import org.eventb.core.ast.ProblemSeverities;

/**
 * @author Nicolas Beauger
 * 
 */
public abstract class VersionUpgrader {

	public static boolean DEBUG;

	private final LanguageVersion sourceVersion;

	public VersionUpgrader(LanguageVersion sourceVer) {
		this.sourceVersion = sourceVer;
	}

	/**
	 * Returns the upgrade result for the given formula string.
	 * 
	 * @param input
	 *            the formula string of an assignment
	 * @param result
	 *            the result of the upgrade
	 */
	public void upgradeAssignment(String input, UpgradeResult<Assignment> result) {
		final FormulaFactory factory = result.getFactory();
		final IParseResult parseResult = factory.parseAssignment(input,
				sourceVersion, null);
		if (parseResult.hasProblem()) {
			copyProblems(parseResult, result);
			result.setUpgradeNeeded(true);
			return;
		}

		final Assignment assign = parseResult.getParsedAssignment();

		checkIdents(assign.getAssignedIdentifiers(), result);
		if (result.hasProblem()) {
			result.setUpgradeNeeded(true);
			return;
		}
		
		final AssignmentRHSUpgrader assignUpg = new AssignmentRHSUpgrader(this,
				input, factory, result);
		assign.accept(assignUpg);
	}

	private void checkIdents(FreeIdentifier[] identifiers,
			UpgradeResult<Assignment> result) {
		final List<String> reservedNames = getReservedKeywords();
		for (FreeIdentifier ident : identifiers) {
			if (reservedNames.contains(ident.getName())) {
				result.addProblem(new ASTProblem(ident.getSourceLocation(),
								ProblemKind.NotUpgradableError,
								ProblemSeverities.Error));
			}
		}
	}

	/**
	 * Returns the upgrade result for the given formula string.
	 * 
	 * @param input
	 *            the formula string of an expression
	 * @param result
	 *            the result of the upgrade
	 */
	public void upgradeExpression(String input, UpgradeResult<Expression> result) {
		final FormulaFactory factory = result.getFactory();
		final IParseResult parseResult = factory.parseExpression(input,
				sourceVersion, null);
		if (parseResult.hasProblem()) {
			copyProblems(parseResult, result);
			result.setUpgradeNeeded(true);
			return;
		}
		final Expression expr = parseResult.getParsedExpression();

		upgrade(input, expr, result);
	}

	/**
	 * Returns the upgrade result for of the given formula string.
	 * 
	 * @param input
	 *            the formula string of a predicate
	 * @param result
	 *            the result of the upgrade
	 */
	public void upgradePredicate(String input, UpgradeResult<Predicate> result) {
		final FormulaFactory factory = result.getFactory();
		final IParseResult parseResult = factory.parsePredicate(input,
				sourceVersion, null);
		if (parseResult.hasProblem()) {
			copyProblems(parseResult, result);
			result.setUpgradeNeeded(true);
			return;
		}
		final Predicate pred = parseResult.getParsedPredicate();
		upgrade(input, pred, result);
	}

	static void copyProblems(IResult result,
			UpgradeResult<?> upgradeResult) {
		for (ASTProblem problem : result.getProblems()) {
			upgradeResult.addProblem(problem);
		}
	}

	<T extends Formula<T>> void upgrade(String input, T formula,
			UpgradeResult<T> result) {
		checkUpgrade(input, formula, result);
		if (result.upgradeNeeded() && !result.hasProblem()) {
			upgrade(formula, result);
		}
	}

	protected abstract <T extends Formula<T>> void checkUpgrade(String formulaString,
			Formula<T> formula,	UpgradeResult<T> result);

	protected abstract <T extends Formula<T>> void upgrade(T formula,
			UpgradeResult<T> result);
	
	protected abstract List<String> getReservedKeywords();

}

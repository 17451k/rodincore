/*******************************************************************************
 * Copyright (c) 2007 ETH Zurich.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.eventb.internal.core.seqprover.eventbExtensions.rewriters;

import java.math.BigInteger;

import org.eventb.core.ast.AssociativeExpression;
import org.eventb.core.ast.AssociativePredicate;
import org.eventb.core.ast.AtomicExpression;
import org.eventb.core.ast.BinaryExpression;
import org.eventb.core.ast.BinaryPredicate;
import org.eventb.core.ast.BoolExpression;
import org.eventb.core.ast.BoundIdentDecl;
import org.eventb.core.ast.BoundIdentifier;
import org.eventb.core.ast.DefaultRewriter;
import org.eventb.core.ast.Expression;
import org.eventb.core.ast.Formula;
import org.eventb.core.ast.FormulaFactory;
import org.eventb.core.ast.FreeIdentifier;
import org.eventb.core.ast.Identifier;
import org.eventb.core.ast.IntegerLiteral;
import org.eventb.core.ast.LiteralPredicate;
import org.eventb.core.ast.Predicate;
import org.eventb.core.ast.QuantifiedExpression;
import org.eventb.core.ast.QuantifiedPredicate;
import org.eventb.core.ast.RelationalPredicate;
import org.eventb.core.ast.SetExtension;
import org.eventb.core.ast.SimplePredicate;
import org.eventb.core.ast.UnaryExpression;
import org.eventb.core.ast.UnaryPredicate;
import org.eventb.core.seqprover.eventbExtensions.Lib;

/**
 * Basic manual rewriter for the Event-B sequent prover.
 */
@SuppressWarnings("unused")
public class RemoveNegationRewriterImpl extends AutoRewriterImpl {

	public RemoveNegationRewriterImpl() {
		super();
	}

	%include {Formula.tom}
	
	@Override
	public Predicate rewrite(UnaryPredicate predicate) {
		Predicate newPredicate = super.rewrite(predicate);
		if (!newPredicate.equals(predicate))
			return newPredicate;
			
	    %match (Predicate predicate) {

			/**
			 * Negation: ¬(S = ∅) == ∃x·x ∈ S
			 */
			Not(Equal(S, EmptySet())) -> {
				return FormulaUnfold.makeExistantial(`S);
			}
			
			/**
			 * Negation: ¬(∅ = S) == ∃x·x ∈ S
			 */
			Not(Equal(EmptySet(), S)) -> {
				return FormulaUnfold.makeExistantial(`S);
			}
			
			/**
			 * Negation: ¬(P ∧ ... ∧ Q) == ¬P ⋁ ... ⋁ ¬Q
			 */
			Not(Land(children)) -> {
				return FormulaUnfold.deMorgan(Formula.LOR, `children);
			}
			
			/**
			 * Negation: ¬(P ⋁ ... ⋁ Q) == ¬P ∧ ... ∧ ¬Q
			 */
			Not(Lor(children)) -> {
				return FormulaUnfold.deMorgan(Formula.LAND, `children);
			}
			
			/**
			 * Negation: ¬(P ⇒ Q) == P ∧ ¬Q
			 */
			Not(Limp(P, Q)) -> {
				return FormulaUnfold.negImp(`P, `Q);
			}
			
			/**
			 * Negation: ¬(∀x·P) == ∃x·¬P
			 */
			Not(ForAll(idents, P)) -> {
				return FormulaUnfold.negQuant(Formula.EXISTS, `idents, `P);
			}
			
			/**
			 * Negation: ¬(∃x·P) == ∀x·¬P
			 */
			Not(Exists(idents, P)) -> {
				return FormulaUnfold.negQuant(Formula.FORALL, `idents, `P);
			}
	    }
	    return predicate;
	}

}

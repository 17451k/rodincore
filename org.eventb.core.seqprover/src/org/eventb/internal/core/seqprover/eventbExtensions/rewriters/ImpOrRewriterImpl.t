/*******************************************************************************
 * Copyright (c) 2006 ETH Zurich.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.eventb.internal.core.seqprover.eventbExtensions.rewriters;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Collection;

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
 * Basic automated rewriter for the Event-B sequent prover.
 */
@SuppressWarnings("unused")
public class ImpOrRewriterImpl extends DefaultRewriter {

	public ImpOrRewriterImpl() {
		super(true, FormulaFactory.getDefault());
	}
		
	%include {Formula.tom}
	
	@Override
	public Predicate rewrite(BinaryPredicate predicate) {
	    %match (Predicate predicate) {

			/**
	    	 * Implication : P ∨ ... ∨ Q ⇒ R  ==  P ⇒ R ∧ ... ∧ Q ⇒ R
	    	 */
			Limp(Lor(children), R) -> {
				Collection<Predicate> newChildren = new ArrayList<Predicate>(
						`children.length);
				FormulaFactory ff = FormulaFactory.getDefault();
				for (Predicate child : `children) {
					newChildren.add(ff.makeBinaryPredicate(Predicate.LIMP, child,
							`R, null));
				}
				return ff.makeAssociativePredicate(
						Predicate.LAND, newChildren, null);
			}
			
	    }
	    return predicate;
	}

}

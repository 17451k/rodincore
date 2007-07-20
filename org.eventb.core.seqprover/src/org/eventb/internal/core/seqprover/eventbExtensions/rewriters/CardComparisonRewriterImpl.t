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
import org.eventb.core.ast.PowerSetType;
import org.eventb.core.ast.Predicate;
import org.eventb.core.ast.ProductType;
import org.eventb.core.ast.QuantifiedExpression;
import org.eventb.core.ast.QuantifiedPredicate;
import org.eventb.core.ast.RelationalPredicate;
import org.eventb.core.ast.SetExtension;
import org.eventb.core.ast.SimplePredicate;
import org.eventb.core.ast.Type;
import org.eventb.core.ast.UnaryExpression;
import org.eventb.core.ast.UnaryPredicate;
import org.eventb.core.seqprover.eventbExtensions.Lib;

@SuppressWarnings("unused")
public class CardComparisonRewriterImpl extends DefaultRewriter {

	public CardComparisonRewriterImpl() {
		super(true, FormulaFactory.getDefault());
	}
		
	%include {Formula.tom}
	
	@Override
	public Predicate rewrite(RelationalPredicate predicate) {
	    %match (Predicate predicate) {
			
			/**
	    	 * Set Theory: card(S) ≤ card(T)
	    	 */
			Le(Card(S), Card(T)) -> {
				return ff.makeRelationalPredicate(Predicate.SUBSETEQ, `S, `T, null);
			}

			/**
	    	 * Set Theory: card(S) ≥ card(T)
	    	 */
			Ge(Card(S), Card(T)) -> {
				return ff.makeRelationalPredicate(Predicate.SUBSETEQ, `T, `S, null);
			}

			/**
	    	 * Set Theory: card(S) < card(T)
	    	 */
			Lt(Card(S), Card(T)) -> {
				return ff.makeRelationalPredicate(Predicate.SUBSET, `S, `T, null);
			}

			/**
	    	 * Set Theory: card(S) > card(T)
	    	 */
			Gt(Card(S), Card(T)) -> {
				return ff.makeRelationalPredicate(Predicate.SUBSET, `T, `S, null);
			}

			/**
	    	 * Set Theory: card(S) = card(T)
	    	 */
			Equal(Card(S), Card(T)) -> {
				return ff.makeRelationalPredicate(Predicate.EQUAL, `S, `T, null);
			}
	    }
	    return predicate;
	}
}

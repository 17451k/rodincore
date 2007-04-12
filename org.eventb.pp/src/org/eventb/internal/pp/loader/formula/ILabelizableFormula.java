/*******************************************************************************
 * Copyright (c) 2006 ETH Zurich.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/

package org.eventb.internal.pp.loader.formula;

import java.util.Collection;
import java.util.List;

import org.eventb.internal.pp.core.elements.ClauseFactory;
import org.eventb.internal.pp.core.elements.IClause;
import org.eventb.internal.pp.core.elements.ILiteral;
import org.eventb.internal.pp.loader.clause.BooleanEqualityTable;
import org.eventb.internal.pp.loader.clause.LabelManager;
import org.eventb.internal.pp.loader.clause.VariableTable;
import org.eventb.internal.pp.loader.formula.descriptor.LiteralDescriptor;
import org.eventb.internal.pp.loader.formula.terms.TermSignature;

/**
 * Basic interface for all formulas that are labelizable. It consists
 * in the quantified literals and the clauses, respectively the classes
 * {@link DisjunctiveClause} and {@link QuantifiedLiteral}.
 *
 * @author François Terrier
 */
public interface ILabelizableFormula<T extends LiteralDescriptor> extends ISubFormula<T> {

//	/* ***********************************/
//	/**
//	 * Returns a positive or a negative instance of the label 
//	 * for this formula, depending on the value of <code>isPositive</code>. 
//	 * 
//	 * @param isPositive 
//	 * @param table TODO
//	 * @return the corresponding label
//	 */
//	public ILiteral getLiteral(boolean isPositive, VariableTable table);
	
//	public List<List<ILiteral>> getDefinitionClauses(LabelManager manager,
//			boolean isPositive, VariableTable table);
//	/* ************************************/
//	
//	
//	/**
//	 * Helper method to {@link ILabelizableFormula#
//	 * getDefinitionClauses(List<Index>, LabelManager, List, boolean, boolean, VariableTable)},
//	 * 
//	 * @param termList
//	 * @param manager
//	 * @param prefix
//	 * @param flags TODO
//	 * @param table TODO
//	 * @return
//	 */
	public List<List<ILiteral>> getDefinitionClauses(
			List<TermSignature> termList, LabelManager manager,
			List<List<ILiteral>> prefix, TermVisitorContext flags,
			VariableTable table, BooleanEqualityTable bool);
	
	public List<TermSignature> getTerms();

    public String getStringDeps();
	
    public boolean isEquivalence();
    
//	 PUBLIC used by clausebuilder
	// -when called on a labelizable formula, this method uses unified term list
	// -when called on a signed formula, it uses terms recorded in the formula instance
	public void getFinalClauses(Collection<IClause> clauses, LabelManager manager, ClauseFactory factory, BooleanEqualityTable bool, VariableTable variableTable, boolean positive);
	
}

/*******************************************************************************
 * Copyright (c) 2006 ETH Zurich.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.eventb.internal.core.pog;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;

import org.eclipse.core.runtime.CoreException;
import org.eventb.core.ISCAction;
import org.eventb.core.ast.Assignment;
import org.eventb.core.ast.BecomesEqualTo;
import org.eventb.core.ast.FormulaFactory;
import org.eventb.core.ast.FreeIdentifier;
import org.eventb.core.ast.ITypeEnvironment;
import org.eventb.core.ast.Predicate;
import org.eventb.core.pog.state.IEventActionTable;
import org.eventb.internal.core.tool.state.State;

/**
 * @author Stefan Hallerstede
 *
 */
public abstract class EventActionTable extends State implements IEventActionTable {
	
	protected List<ISCAction> actions;
	protected List<Assignment> assignments;
	
	protected List<ISCAction> detActions;
	protected List<BecomesEqualTo> detAssn;
	protected List<BecomesEqualTo> primedDetAssn;

	protected List<ISCAction> nondetActions;
	protected List<Assignment> nondetAssn;
	protected List<Predicate> nondetPred;
	
	protected Collection<FreeIdentifier> assignedVars;

	public EventActionTable(
			ISCAction[] actions, 
			ITypeEnvironment typeEnvironment, 
			FormulaFactory factory) throws CoreException {
		nondetAssn = 
			new ArrayList<Assignment>(actions.length);
		nondetPred = 
			new ArrayList<Predicate>(actions.length);
		assignedVars = 
			new HashSet<FreeIdentifier>(actions.length * 15);
		assignments = new ArrayList<Assignment>(actions.length);		
		detAssn = 
			new ArrayList<BecomesEqualTo>(actions.length);
		primedDetAssn =
			new ArrayList<BecomesEqualTo>(actions.length);
		this.actions = Arrays.asList(actions);
		nondetActions = new ArrayList<ISCAction>(actions.length);
		detActions = new ArrayList<ISCAction>(actions.length);
		
		for (int i=0; i<actions.length; i++) {
			
			ISCAction action = actions[i];
			Assignment assignment = action.getAssignment(factory, typeEnvironment);
			
			assignments.add(assignment);
			
			fetchAssignedIdentifiers(assignedVars, assignment);
			
			if (assignment instanceof BecomesEqualTo) {
				detAssn.add((BecomesEqualTo) assignment);
				detActions.add(action);
			} else {
				nondetAssn.add(assignment);
				nondetPred.add(assignment.getBAPredicate(factory));
				nondetActions.add(action);
			}
		}
		makePrimedDetermist(factory);
	}
	
	@Override
	public void makeImmutable() {
		super.makeImmutable();
		
		actions = Collections.unmodifiableList(actions);
		assignments = Collections.unmodifiableList(assignments);
		assignedVars = Collections.unmodifiableCollection(assignedVars);
		detAssn =  Collections.unmodifiableList(detAssn);
		nondetAssn = Collections.unmodifiableList(nondetAssn);
		nondetPred = Collections.unmodifiableList(nondetPred);
		nondetActions = Collections.unmodifiableList(nondetActions);
		detActions = Collections.unmodifiableList(detActions);
		primedDetAssn = Collections.unmodifiableList(primedDetAssn);
	}

	public Collection<FreeIdentifier> getAssignedVariables() {
		return assignedVars;
	}

	public boolean containsAssignedVariable(FreeIdentifier variable) {
		return assignedVars.contains(variable);
	}

	public List<Assignment> getAssignments() {
		return assignments;
	}

	public List<BecomesEqualTo> getDetAssignments() {
		return detAssn;
	}

	private void makePrimedDetermist(FormulaFactory factory) {
		for (BecomesEqualTo becomesEqualTo : detAssn) {
			FreeIdentifier[] unprimedLeft = becomesEqualTo.getAssignedIdentifiers();
			FreeIdentifier[] primedLeft = new FreeIdentifier[unprimedLeft.length];
			for (int i=0; i<unprimedLeft.length; i++)
				primedLeft[i] = unprimedLeft[i].withPrime(factory);
			BecomesEqualTo primed = 
				factory.makeBecomesEqualTo(primedLeft, becomesEqualTo.getExpressions(), null);
			primedDetAssn.add(primed);
		}
	}

	/* (non-Javadoc)
	 * @see org.eventb.core.pog.IAssignmentTable#getPrimedDetAssignments()
	 */
	public List<BecomesEqualTo> getPrimedDetAssignments() {
		return primedDetAssn;
	}
	
	protected void fetchAssignedIdentifiers(
			Collection<FreeIdentifier> assignedIdents, 
			Assignment assignment) {
		FreeIdentifier[] freeIdentifiers = assignment.getAssignedIdentifiers();
		for (FreeIdentifier identifier : freeIdentifiers)
			assignedIdents.add(identifier);
	}

	public List<Assignment> getNondetAssignments() {
		return nondetAssn;
	}

	public List<Predicate> getNondetPredicates() {
		return nondetPred;
	}

	/* (non-Javadoc)
	 * @see org.eventb.core.pog.IEventActionTable#getActions()
	 */
	public List<ISCAction> getActions() {
		return actions;
	}
	
	public List<ISCAction> getNondetActions() {
		return nondetActions;
	}
	
	public List<ISCAction> getDetActions() {
		return detActions;
	}

}

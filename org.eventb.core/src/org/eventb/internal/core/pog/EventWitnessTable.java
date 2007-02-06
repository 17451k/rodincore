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
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eventb.core.ISCWitness;
import org.eventb.core.ast.BecomesEqualTo;
import org.eventb.core.ast.Expression;
import org.eventb.core.ast.Formula;
import org.eventb.core.ast.FormulaFactory;
import org.eventb.core.ast.FreeIdentifier;
import org.eventb.core.ast.ITypeEnvironment;
import org.eventb.core.ast.Predicate;
import org.eventb.core.ast.RelationalPredicate;
import org.eventb.core.pog.state.IEventWitnessTable;
import org.eventb.core.tool.state.IToolStateType;
import org.eventb.internal.core.tool.state.ToolState;

/**
 * @author Stefan Hallerstede
 *
 */
public class EventWitnessTable extends ToolState implements IEventWitnessTable {

	private List<ISCWitness> witnesses;
	private final boolean[] deterministic;
	private List<FreeIdentifier> witnessedVars;
	private List<Predicate> witnessPredicates;
	
	private final BecomesEqualTo primeSubstitution;
	
	private List<ISCWitness> machineDetWitnesses;
	private List<BecomesEqualTo> machineDetermist;
	private List<BecomesEqualTo> machinePrimedDetermist;
	private List<ISCWitness> eventDetWitnesses;
	private List<BecomesEqualTo> eventDetermist;
	
	private List<ISCWitness> nondetWitnesses;
	private List<FreeIdentifier> nondetIdentifiers;
	private List<Predicate> nondetPredicates;

	public EventWitnessTable(
			ISCWitness[] witnesses, 
			ITypeEnvironment typeEnvironment, 
			FormulaFactory factory, 
			IProgressMonitor monitor) throws CoreException {
		this.witnesses = Arrays.asList(witnesses);
		this.deterministic = new boolean[witnesses.length];
		machineDetWitnesses = new ArrayList<ISCWitness>(witnesses.length);
		machineDetermist = new ArrayList<BecomesEqualTo>(witnesses.length);
		machinePrimedDetermist = new ArrayList<BecomesEqualTo>(witnesses.length);
		eventDetWitnesses = new ArrayList<ISCWitness>(witnesses.length);
		eventDetermist = new ArrayList<BecomesEqualTo>(witnesses.length);
		nondetWitnesses = new ArrayList<ISCWitness>(witnesses.length);
		nondetPredicates = new ArrayList<Predicate>(witnesses.length);
		nondetIdentifiers = new ArrayList<FreeIdentifier>(witnesses.length);
		witnessedVars = new ArrayList<FreeIdentifier>(witnesses.length);
		witnessPredicates = new ArrayList<Predicate>(witnesses.length);
	
		final LinkedList<FreeIdentifier> left = new LinkedList<FreeIdentifier>();
		final LinkedList<Expression> right = new LinkedList<Expression>();
	
		for (int i=0; i<witnesses.length; i++) {
			final Predicate predicate = witnesses[i].getPredicate(factory, typeEnvironment);
			final String name = witnesses[i].getLabel();
			final FreeIdentifier identifier = factory.makeFreeIdentifier(name, null);
			identifier.typeCheck(typeEnvironment);
			final FreeIdentifier unprimed = 
				identifier.isPrimed() ? 
						identifier.withoutPrime(factory) : 
						identifier;
			witnessedVars.add(identifier);
			witnessPredicates.add(predicate);
			deterministic[i] = categorize(identifier, unprimed, predicate, witnesses[i], factory);
			if ( ! deterministic[i] && identifier != unprimed) {
				left.add(unprimed);
				right.add(identifier);
			}	
		}
		
		if (left.size() == 0) {
			primeSubstitution = null;
		} else {
			primeSubstitution = factory.makeBecomesEqualTo(left, right, null);
			primeSubstitution.typeCheck(typeEnvironment);
		}
	}
	
	@Override
	public void makeImmutable() {
		super.makeImmutable();
		
		witnesses = Collections.unmodifiableList(witnesses);
		machineDetWitnesses = Collections.unmodifiableList(machineDetWitnesses);
		machineDetermist = Collections.unmodifiableList(machineDetermist);
		machinePrimedDetermist = Collections.unmodifiableList(machinePrimedDetermist);
		eventDetWitnesses = Collections.unmodifiableList(eventDetWitnesses);
		eventDetermist = Collections.unmodifiableList(eventDetermist);
		nondetWitnesses = Collections.unmodifiableList(nondetWitnesses);
		nondetIdentifiers = Collections.unmodifiableList(nondetIdentifiers);
		nondetPredicates = Collections.unmodifiableList(nondetPredicates);
		witnessedVars = Collections.unmodifiableList(witnessedVars);
		witnessPredicates = Collections.unmodifiableList(witnessPredicates);
	}

	private boolean categorize(
			FreeIdentifier identifier, 
			FreeIdentifier unprimed, 
			Predicate predicate,
			ISCWitness witness,
			FormulaFactory factory) {
		
		// is it a deterministic witness?
		if (predicate instanceof RelationalPredicate) {
			RelationalPredicate relationalPredicate = (RelationalPredicate) predicate;
			if (relationalPredicate.getTag() == Formula.EQUAL)
				if (relationalPredicate.getLeft().equals(identifier) 
						&& !Arrays.asList(relationalPredicate.getRight().getFreeIdentifiers()).contains(identifier)) {
					final BecomesEqualTo becomesEqualTo =
						factory.makeBecomesEqualTo(unprimed, relationalPredicate.getRight(), null);
					if (identifier == unprimed) {
						eventDetermist.add(becomesEqualTo);
						eventDetWitnesses.add(witness);
					} else {
						machineDetermist.add(becomesEqualTo);
						machineDetWitnesses.add(witness);
						machinePrimedDetermist.add(
								factory.makeBecomesEqualTo(
										identifier, 
										relationalPredicate.getRight(), null));
					}
					// it's deterministic
					return true;
				}
		}

		// or a nondeterministic witness?
		nondetWitnesses.add(witness);
		nondetIdentifiers.add(identifier);
		nondetPredicates.add(predicate);
		
		// it's nondeterministic
		return false;
	}
	
	/* (non-Javadoc)
	 * @see org.eventb.core.sc.IState#getStateType()
	 */
	public IToolStateType<?> getStateType() {
		return STATE_TYPE;
	}

	public BecomesEqualTo getPrimeSubstitution() {
		return primeSubstitution;
	}

	public List<ISCWitness> getWitnesses() {
		return witnesses;
	}

	public List<ISCWitness> getMachineDetWitnesses() {
		return machineDetWitnesses;
	}

	public List<BecomesEqualTo> getMachineDetAssignments() {
		return machineDetermist;
	}

	public List<BecomesEqualTo> getMachinePrimedDetAssignments() {
		return machinePrimedDetermist;
	}

	public List<ISCWitness> getEventDetWitnesses() {
		return eventDetWitnesses;
	}

	public List<BecomesEqualTo> getEventDetAssignments() {
		return eventDetermist;
	}

	public List<Predicate> getNondetPredicates() {
		return nondetPredicates;
	}

	public List<FreeIdentifier> getVariables() {
		return witnessedVars;
	}

	public List<ISCWitness> getNondetWitnesses() {
		return nondetWitnesses;
	}

	public List<FreeIdentifier> getNondetVariables() {
		return nondetIdentifiers;
	}

	public List<Predicate> getPredicates() {
		return witnessPredicates;
	}

	public boolean isDeterministic(int index) {
		return deterministic[index];
	}

}

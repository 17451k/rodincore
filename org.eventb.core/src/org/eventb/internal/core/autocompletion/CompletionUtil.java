/*******************************************************************************
 * Copyright (c) 2008-2009 Systerel and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     Systerel - initial API and implementation
 *******************************************************************************/
package org.eventb.internal.core.autocompletion;

import static org.eventb.core.EventBPlugin.MODIFICATION;
import static org.eventb.core.EventBPlugin.REDECLARATION;

import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.Set;

import org.eventb.core.EventBPlugin;
import org.eventb.core.IAction;
import org.eventb.core.IEvent;
import org.eventb.core.IParameter;
import org.eventb.core.IVariable;
import org.eventb.core.ast.Assignment;
import org.eventb.core.ast.Formula;
import org.eventb.core.ast.FormulaFactory;
import org.eventb.core.ast.IParseResult;
import org.eventb.core.ast.LanguageVersion;
import org.rodinp.core.IInternalElement;
import org.rodinp.core.IRodinFile;
import org.rodinp.core.RodinCore;
import org.rodinp.core.RodinDBException;
import org.rodinp.core.indexer.IDeclaration;
import org.rodinp.core.indexer.IIndexQuery;
import org.rodinp.core.indexer.IOccurrence;
import org.rodinp.core.location.IInternalLocation;

/**
 * @author Nicolas Beauger
 * 
 */
public class CompletionUtil {

	// disappearing parameters
	public static Set<IDeclaration> getDisappearingParams(IEvent event) {
		final Set<IEvent> abstractEvents = getAbstractEvents(event);
		final Set<IDeclaration> absParams = getParameters(abstractEvents);
		final Set<IDeclaration> redeclared = getRedeclared(event);

		absParams.removeAll(redeclared);
		return absParams;
	}

	private static Set<IDeclaration> getParams(IEvent event) {
		final IIndexQuery query = RodinCore.makeIndexQuery();
		final Set<IDeclaration> declarations = query.getDeclarations(event.getRodinFile());
		new ParameterFilter(event).apply(declarations);
		return declarations;
	}

	private static Set<IDeclaration> getParameters(
			Set<IEvent> abstractEvents) {
		final Set<IDeclaration> abstractParams = new LinkedHashSet<IDeclaration>();
		for (IEvent event : abstractEvents) {
			abstractParams.addAll(getParams(event));
		}
		return abstractParams;
	}

	private static Set<IDeclaration> getRedeclared(IEvent event) {
		final IIndexQuery query = RodinCore.makeIndexQuery();
		final Set<IDeclaration> declarations = query.getDeclarations(event
				.getRodinFile());
		query.filterType(declarations, IParameter.ELEMENT_TYPE);
		final Set<IOccurrence> occurrences = query.getOccurrences(declarations);
		query.filterKind(occurrences, REDECLARATION);
		final IInternalLocation evtLoc = RodinCore.getInternalLocation(event);
		query.filterLocation(occurrences, evtLoc);
		return query.getDeclarations(occurrences);
	}
	
	public static Set<IEvent> getAbstractEvents(IEvent event) {
		final IIndexQuery query = RodinCore.makeIndexQuery();
		final Set<IDeclaration> declsFile = query.getDeclarations(event
				.getRodinFile());
		query.filterType(declsFile, IEvent.ELEMENT_TYPE);
		final Set<IOccurrence> occsEvents = query.getOccurrences(declsFile);
		query.filterKind(occsEvents, EventBPlugin.REDECLARATION);
		query.filterLocation(occsEvents, RodinCore.getInternalLocation(event));
		final Set<IDeclaration> declsAbsEvts = query
				.getDeclarations(occsEvents);
		
		return getEvents(declsAbsEvts);
	}

	private static Set<IEvent> getEvents(Set<IDeclaration> declsAbsEvts) {
		final Set<IEvent> result = new LinkedHashSet<IEvent>();
		for (IDeclaration declaration : declsAbsEvts) {
			final IInternalElement element = declaration.getElement();
			if (element instanceof IEvent)
				result.add((IEvent) element);
		}
		return result;
	}

	// disappearing variables
	public static Set<IDeclaration> getDisappearingVars(IRodinFile file) {
		final IIndexQuery query = RodinCore.makeIndexQuery();
		final Set<IDeclaration> vars = query.getVisibleDeclarations(file);
		query.filterType(vars, IVariable.ELEMENT_TYPE);
		final Iterator<IDeclaration> iter = vars.iterator();
		while(iter.hasNext()) {
			final IDeclaration var = iter.next();
			if (isInFile(var, file) || isRedeclaredInFile(var, file, query)) {
				iter.remove();
			}
		}
		return vars;
	}

	private static boolean isRedeclaredInFile(IDeclaration decl,
			IRodinFile file, final IIndexQuery query) {
		final Set<IOccurrence> occs = query.getOccurrences(decl);
		query.filterKind(occs, REDECLARATION);
		query.filterFile(occs, file);
		return !occs.isEmpty();
	}

	private static boolean isInFile(final IDeclaration var, IRodinFile file) {
		return var.getElement().getRodinFile().equals(file);
	}
	
	public static Set<IDeclaration> getDeterministicallyAssignedVars(IEvent event) {
		final IIndexQuery query = RodinCore.makeIndexQuery();
		final Set<IDeclaration> vars = query.getVisibleDeclarations(event
				.getRodinFile());
		query.filterType(vars, IVariable.ELEMENT_TYPE);
		final Set<IOccurrence> occs = query.getOccurrences(vars);
		query.filterKind(occs, MODIFICATION);
		query.filterLocation(occs, RodinCore.getInternalLocation(event));
		
		removeNonDeterministicallyAssigned(occs);
		final Set<IDeclaration> result = query.getDeclarations(occs);
		return result;
	}

	private static void removeNonDeterministicallyAssigned(
			Set<IOccurrence> occs) {
		final Iterator<IOccurrence> iter = occs.iterator();
		while(iter.hasNext()) {
			final IOccurrence occ = iter.next();
			final IInternalElement locElem = occ.getLocation().getElement();
			if (! (locElem instanceof IAction)) {
				continue;
			}
			try {
				final String assign = ((IAction) locElem).getAssignmentString();
				final IParseResult result = FormulaFactory.getDefault()
						.parseAssignment(assign, LanguageVersion.LATEST, assign);
				if (result.hasProblem()) {
					continue;
				}
				if (!isDeterministic(result.getParsedAssignment())) {
					iter.remove();
				}
			} catch (RodinDBException e) {
				// ignore this element
			}
		}
	}

	private static boolean isDeterministic(Assignment assignment) {
		return assignment.getTag() == Formula.BECOMES_EQUAL_TO;
	}
}

/*******************************************************************************
 * Copyright (c) 2006 ETH Zurich.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/

package org.eventb.internal.pp.core.tracing;

import java.util.Set;

import org.eventb.internal.pp.core.Level;
import org.eventb.internal.pp.core.Tracer;

/**
 * Implementation of {@link IOrigin} for definition clauses.
 *
 * @author François Terrier
 *
 */
public class DefinitionOrigin implements IOrigin {

	public void addDependenciesTo(Set<Level> dependencies) {
		if (!dependencies.contains(getLevel()))
			dependencies.add(getLevel());
	}

	public void trace(Tracer tracer) {
		// do nothing
	}

	public boolean dependsOnGoal() {
		return false;
	}

	public boolean isDefinition() {
		return true;
	}

	public Level getLevel() {
		return Level.BASE;
	}

	@Override
	public String toString() {
		return getLevel().toString();
	}

	public int getDepth() {
		return 0;
	}
}

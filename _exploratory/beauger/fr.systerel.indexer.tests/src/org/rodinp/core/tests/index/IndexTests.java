/*******************************************************************************
 * Copyright (c) 2008 Systerel and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     Systerel - initial API and implementation
 *******************************************************************************/
package org.rodinp.core.tests.index;

import org.rodinp.core.indexer.RodinIndexer;
import org.rodinp.core.tests.AbstractRodinDBTests;

public abstract class IndexTests extends AbstractRodinDBTests {

	public IndexTests(String name, boolean disableIndexing) {
		super(name);
		if (disableIndexing) {
			RodinIndexer.disableIndexing();
		}
	}

}
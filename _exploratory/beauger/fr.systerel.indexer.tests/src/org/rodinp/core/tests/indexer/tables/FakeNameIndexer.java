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
package org.rodinp.core.tests.indexer.tables;

import static org.rodinp.core.tests.indexer.IndexTestsUtil.*;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import junit.framework.TestCase;

import org.eclipse.core.runtime.CoreException;
import org.rodinp.core.IInternalElement;
import org.rodinp.core.IRodinFile;
import org.rodinp.core.indexer.IDeclaration;
import org.rodinp.core.indexer.IIndexer;
import org.rodinp.core.indexer.IIndexingBridge;
import org.rodinp.core.indexer.RodinIndexer;
import org.rodinp.core.location.IInternalLocation;
import org.rodinp.core.tests.basis.NamedElement;
import org.rodinp.core.tests.indexer.IndexTestsUtil;

public class FakeNameIndexer implements IIndexer {

	private static final boolean DEBUG = false;

	private static final String ID =
			"org.rodinp.core.tests.indexer.fakeNameIndexer";

	private final String[] names;
	private final int numberEach;
	private final Map<String, Set<IInternalElement>> indexedElements;

	public FakeNameIndexer(int numberEach, String... names) {
		TestCase.assertTrue("numberEach is not positive", numberEach > 0);

		this.numberEach = numberEach;
		this.names = names;
		indexedElements = new HashMap<String, Set<IInternalElement>>();
	}

	public boolean index(IIndexingBridge bridge) {
		indexedElements.clear();

		IRodinFile rodinFile = bridge.getRootToIndex().getRodinFile();
		try {
			rodinFile.clear(true, null);
			for (String name : names) {
				final NamedElement elt = createNamedElement(rodinFile, name);
				final IDeclaration declaration = bridge.declare(elt, name);
				final HashSet<IInternalElement> set =
						new HashSet<IInternalElement>();
				indexedElements.put(name, set);
				set.add(elt);
				for (int i = 0; i < numberEach; i++) {
					final NamedElement element =
							IndexTestsUtil.createNamedElement(rodinFile, name
									+ "_DB"
									+ i);
					final IInternalLocation loc =
							RodinIndexer.getInternalLocation(element);
					bridge.addOccurrence(declaration, TEST_KIND, loc);
					if (DEBUG) {
						System.out.println(name
								+ ": "
								+ element.getElementName());
					}
				}
			}
			return true;
		} catch (CoreException e) {
			e.printStackTrace();
			TestCase.fail("FakeNameIndexer unable to index "
					+ rodinFile.getBareName()
					+ "\nreason: "
					+ e.getLocalizedMessage());
			return false;
		}
	}

	public IInternalElement[] getIndexedElements(String name) {
		Set<IInternalElement> elements = indexedElements.get(name);
		if (elements == null || elements.size() == 0) {
			return new IInternalElement[0];
		}
		return elements.toArray(new IInternalElement[elements.size()]);
	}

	public IRodinFile[] getDependencies(IInternalElement root) {
		return new IRodinFile[0];
	}

	public String getId() {
		return ID;
	}

}

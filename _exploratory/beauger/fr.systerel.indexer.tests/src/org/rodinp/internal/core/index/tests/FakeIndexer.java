package org.rodinp.internal.core.index.tests;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.core.runtime.CoreException;
import org.rodinp.core.IInternalElement;
import org.rodinp.core.IRodinElement;
import org.rodinp.core.IRodinFile;
import org.rodinp.core.index.IIndexer;
import org.rodinp.core.index.IndexingFacade;
import org.rodinp.core.index.Occurrence;
import org.rodinp.core.tests.basis.NamedElement;

public class FakeIndexer implements IIndexer {

	public FakeIndexer() {
		// Nothing to do
	}

	public boolean canIndex(IRodinFile file) {
		return true; // TODO make our own test file type
	}

	/**
	 * Calls
	 * {@link IndexTestsUtil#generateOccurrencesTestSet(org.rodinp.core.IInternalElement, int)}
	 * for every children of type {@link NamedElement#ELEMENT_TYPE} generating 3
	 * occurrences of each kind.
	 */
	public void index(IRodinFile file, IndexingFacade index) {
		try {

			final IRodinElement[] fileElems = file
					.getChildrenOfType(NamedElement.ELEMENT_TYPE);

			for (IRodinElement element : fileElems) {
				NamedElement namedElt = (NamedElement) element;
				final String name = namedElt.getElementName();
				final Occurrence[] occurrences = IndexTestsUtil
						.generateOccurrencesTestSet(namedElt, 3);

				IndexTestsUtil.addOccurrences(namedElt, name, occurrences,
						index);
			}

		} catch (CoreException e) {
			e.printStackTrace();
			assert false;
		}
	}

	public IRodinFile[] getDependencies(IRodinFile file) {
		return new IRodinFile[0];
	}

	public Map<IInternalElement, String> getExports(IRodinFile file) {
		return new HashMap<IInternalElement, String>();
	}

}

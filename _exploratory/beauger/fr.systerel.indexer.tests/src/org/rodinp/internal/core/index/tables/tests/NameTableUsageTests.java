package org.rodinp.internal.core.index.tables.tests;

import static org.rodinp.internal.core.index.tests.IndexTestsUtil.assertSameElements;
import static org.rodinp.internal.core.index.tests.IndexTestsUtil.createRodinFile;

import org.rodinp.core.IInternalElement;
import org.rodinp.core.IRodinFile;
import org.rodinp.core.IRodinProject;
import org.rodinp.core.index.RodinIndexer;
import org.rodinp.core.tests.AbstractRodinDBTests;
import org.rodinp.internal.core.index.IndexManager;
import org.rodinp.internal.core.index.tables.NameTable;

public class NameTableUsageTests extends AbstractRodinDBTests {

	private static final boolean DEBUG = false;

	private static IRodinFile file;
	private static final String name1 = "NTUT_name1";
	private static final String name2 = "NTUT_name2";
	private static FakeNameIndexer indexer = new FakeNameIndexer(2, name1,
			name2);
	private static final IndexManager manager = IndexManager.getDefault();

	public NameTableUsageTests(String name) {
		super(name);
	}

	@Override
	protected void setUp() throws Exception {
		super.setUp();
		final IRodinProject rodinProject = createRodinProject("P");
		file = createRodinFile(rodinProject, "nameInd.test");
		RodinIndexer.register(indexer, file.getElementType());
	}

	@Override
	protected void tearDown() throws Exception {
		deleteProject("P");
		manager.clear();
		super.tearDown();
	}

	private void assertNameTable(IRodinFile rodinFile, String name,
			IInternalElement[] expectedElements, String message) {

		final NameTable table = manager.getNameTable(rodinFile
				.getRodinProject());
		IInternalElement[] actualElements = table.getElements(name);

		if (DEBUG && message != null) {
			System.out.println(getName() + ": " + message);
			System.out.println(table.toString());
		}
		assertSameElements(expectedElements, actualElements);
	}

	public void testNameTableFilling() throws Exception {
		manager.scheduleIndexing(file);
		IInternalElement[] expectedName1 = indexer.getIndexedElements(name1);
		IInternalElement[] expectedName2 = indexer.getIndexedElements(name2);

		assertNameTable(file, name1, expectedName1, "");
		assertNameTable(file, name2, expectedName2, null);
	}

	public void testNameTableUpdating() throws Exception {

		// first indexing with 2 elements for both name1 and name2
		manager.scheduleIndexing(file);
		IInternalElement[] expectedName1 = indexer.getIndexedElements(name1);
		IInternalElement[] expectedName2 = indexer.getIndexedElements(name2);

		assertNameTable(file, name1, expectedName1, "Before");
		assertNameTable(file, name2, expectedName2, null);

		// changing the indexer
		manager.clearIndexers();
		indexer = new FakeNameIndexer(1, name1);
		RodinIndexer.register(indexer, file.getElementType());

		// second indexing with 1 element for name1 only
		manager.scheduleIndexing(file);
		IInternalElement[] expectedName1Bis = indexer.getIndexedElements(name1);
		IInternalElement[] expectedName2Bis = indexer.getIndexedElements(name2);

		assertNameTable(file, name1, expectedName1Bis, "After");
		assertNameTable(file, name2, expectedName2Bis, null);
	}

}

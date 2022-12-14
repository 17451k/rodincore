/*******************************************************************************
 * Copyright (c) 2008, 2012 Systerel and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Systerel - initial API and implementation
 *******************************************************************************/
package org.eventb.core.tests.indexers;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import static org.eventb.core.tests.ResourceUtils.CTX_BARE_NAME;
import static org.eventb.core.tests.ResourceUtils.INTERNAL_ELEMENT1;
import static org.eventb.core.tests.ResourceUtils.MCH_BARE_NAME;
import static org.eventb.core.tests.indexers.OccUtils.makeDecl;
import static org.eventb.core.tests.indexers.OccUtils.makeRefPred;
import static org.eventb.core.tests.indexers.OccUtils.makeRefTarget;
import static org.eventb.core.tests.indexers.OccUtils.makeSelfDecl;
import static org.eventb.core.tests.indexers.OccUtils.newDecl;

import org.eventb.core.IAxiom;
import org.eventb.core.ICarrierSet;
import org.eventb.core.IConstant;
import org.eventb.core.IContextRoot;
import org.eventb.core.IExtendsContext;
import org.eventb.core.IMachineRoot;
import org.eventb.core.tests.ResourceUtils;
import org.eventb.internal.core.indexers.ContextIndexer;
import org.junit.Test;
import org.rodinp.core.RodinDBException;
import org.rodinp.core.indexer.IDeclaration;
import org.rodinp.core.indexer.IOccurrence;

/**
 * @author Nicolas Beauger
 * 
 */
public class ContextIndexerTests extends EventBIndexerTests {

	private static IDeclaration getDeclCst(IContextRoot context,
			String cstIntName, String cstName) throws RodinDBException {
		final IConstant cst = context.getConstant(cstIntName);

		return newDecl(cst, cstName);
	}

	private static IDeclaration getDeclSet(IContextRoot context,
			String setIntName, String setName) throws RodinDBException {
		final ICarrierSet set = context.getCarrierSet(setIntName);

		return newDecl(set, setName);
	}

	/**
	 * @throws Exception
	 */
	@Test
	public void testDeclaration() throws Exception {
		final IContextRoot context =
				ResourceUtils.createContext(rodinProject, CTX_BARE_NAME, CST_1DECL);

		final IDeclaration declCst1 =
				getDeclCst(context, INTERNAL_ELEMENT1, CST1);

		final BridgeStub tk = new BridgeStub(context);

		final ContextIndexer indexer = new ContextIndexer();

		assertTrue(indexer.index(tk));

		tk.assertDeclarationsOtherThanRoot(declCst1);
	}

	@Test
	public void testNoDeclarationEmptyName() throws Exception {
		final String CST_1DECL_EMPTY_NAME =
			"<?xml version=\"1.0\" encoding=\"UTF-8\"?>"
					+ "<org.eventb.core.contextFile org.eventb.core.configuration=\"org.eventb.core.fwd\" version=\"3\">"
					+ "<org.eventb.core.constant"
					+ "		name=\"internal_element1\""
					+ "		org.eventb.core.identifier=\"\"/>"
					+ "</org.eventb.core.contextFile>";
		
		final IContextRoot context = ResourceUtils.createContext(rodinProject,
				CTX_BARE_NAME, CST_1DECL_EMPTY_NAME);

		final BridgeStub tk = new BridgeStub(context);

		final ContextIndexer indexer = new ContextIndexer();

		assertTrue(indexer.index(tk));

		tk.assertDeclarationsOtherThanRoot();
	}

	/**
	 * @throws Exception
	 */
	@Test
	public void testRefDeclaration() throws Exception {
		final IContextRoot context =
				ResourceUtils.createContext(rodinProject, CTX_BARE_NAME, CST_1DECL);

		final IConstant cst1 = context.getConstant(INTERNAL_ELEMENT1);
		final IDeclaration declCst1 = newDecl(cst1, CST1);
		
		final IOccurrence occDecl = makeDecl(cst1, declCst1);


		final BridgeStub tk = new BridgeStub(context);

		final ContextIndexer indexer = new ContextIndexer();

		assertTrue(indexer.index(tk));

		tk.assertOccurrences(cst1, occDecl);

	}

	/**
	 * @throws Exception
	 */
	@Test
	public void testOccurrenceOtherThanDecl() throws Exception {
		final IContextRoot context =
				ResourceUtils.createContext(rodinProject, CTX_BARE_NAME, CST_1DECL_1REF_AXM);

		final IConstant cst1 = context.getConstant(INTERNAL_ELEMENT1);
		final IDeclaration declCst1 = newDecl(cst1, CST1);
	
		final IAxiom axiom = context.getAxiom(INTERNAL_ELEMENT1);
		final IOccurrence occRef = makeRefPred(axiom, 0, 4, declCst1);


		final BridgeStub tk = new BridgeStub(context);

		final ContextIndexer indexer = new ContextIndexer();

		assertTrue(indexer.index(tk));

		tk.assertOccurrencesOtherThanDecl(cst1, occRef);
	}

	/**
	 * @throws Exception
	 */
	@Test
	public void testDoubleOccurrenceSameElement() throws Exception {
		final String CST_1DECL_2OCC_SAME_AXM =
				"<?xml version=\"1.0\" encoding=\"UTF-8\"?>"
						+ "<org.eventb.core.contextFile"
						+ "		org.eventb.core.configuration=\"org.eventb.core.fwd\""
						+ "		version=\"3\">"
						+ "<org.eventb.core.constant"
						+ "		name=\"internal_element1\""
						+ "		org.eventb.core.identifier=\"cst1\"/>"
						+ "<org.eventb.core.axiom"
						+ "		name=\"internal_element1\""
						+ "		org.eventb.core.label=\"axm1\""
						+ "		org.eventb.core.predicate=\"cst1 = cst1\""
						+ " 	org.eventb.core.theorem=\"false\"/>"
						+ "</org.eventb.core.contextFile>";

		final IContextRoot context =
				ResourceUtils.createContext(rodinProject, CTX_BARE_NAME, CST_1DECL_2OCC_SAME_AXM);

		final IConstant cst1 = context.getConstant(INTERNAL_ELEMENT1);
		final IDeclaration declCst1 = newDecl(cst1, CST1);

		final IAxiom axiom = context.getAxiom(INTERNAL_ELEMENT1);
		final IOccurrence occRef1 = makeRefPred(axiom, 0, 4, declCst1);
		final IOccurrence occRef2 = makeRefPred(axiom, 7, 11, declCst1);


		final BridgeStub tk = new BridgeStub(context);

		final ContextIndexer indexer = new ContextIndexer();

		assertTrue(indexer.index(tk));

		tk.assertOccurrencesOtherThanDecl(cst1, occRef1, occRef2);
	}

	/**
	 * @throws Exception
	 */
	@Test
	public void testExportLocal() throws Exception {
		final IContextRoot context =
				ResourceUtils.createContext(rodinProject, CTX_BARE_NAME, CST_1DECL);

		final IDeclaration declCst1 =
				getDeclCst(context, INTERNAL_ELEMENT1, CST1);

		final BridgeStub tk = new BridgeStub(context);

		final ContextIndexer indexer = new ContextIndexer();

		assertTrue(indexer.index(tk));

		tk.assertExportsOtherThanRoot(declCst1);
	}

	/**
	 * @throws Exception
	 */
	@Test
	public void testExportImported() throws Exception {

		final IContextRoot exporter =
				ResourceUtils.createContext(rodinProject, "exporter", CST_1DECL);

		final IDeclaration declCst1 =
				getDeclCst(exporter, INTERNAL_ELEMENT1, CST1);

		final IContextRoot importer =
				ResourceUtils.createContext(rodinProject, "importer", EMPTY_CONTEXT);

		final BridgeStub tk = new BridgeStub(importer, declCst1);

		final ContextIndexer indexer = new ContextIndexer();

		assertTrue(indexer.index(tk));

		tk.assertExportsOtherThanRoot(declCst1);
	}

	private static final String CST_1REF_AXM =
			"<?xml version=\"1.0\" encoding=\"UTF-8\"?>"
					+ "<org.eventb.core.contextFile org.eventb.core.configuration=\"org.eventb.core.fwd\" version=\"3\">"
					+ "<org.eventb.core.axiom"
					+ "		name=\"internal_element1\""
					+ "		org.eventb.core.comment=\"\""
					+ "		org.eventb.core.label=\"axm1\""
					+ "		org.eventb.core.predicate=\"1 &lt; cst1\""
					+ " 	org.eventb.core.theorem=\"false\"/>"
					+ "</org.eventb.core.contextFile>";

	/**
	 * @throws Exception
	 */
	@Test
	public void testImportedOccurrence() throws Exception {
		final IContextRoot exporter =
				ResourceUtils.createContext(rodinProject, "exporter", CST_1DECL);

		final IDeclaration declCst1 =
				getDeclCst(exporter, INTERNAL_ELEMENT1, CST1);

		final IContextRoot importer =
				ResourceUtils.createContext(rodinProject, "importer", CST_1REF_AXM);

		final IAxiom axiom = importer.getAxiom(INTERNAL_ELEMENT1);
		final IOccurrence occCst1 = makeRefPred(axiom, 4, 8, declCst1);

		final BridgeStub tk = new BridgeStub(importer, declCst1);

		final ContextIndexer indexer = new ContextIndexer();

		assertTrue(indexer.index(tk));

		tk.assertOccurrences(declCst1.getElement(), occCst1);
	}

	/**
	 * @throws Exception
	 */
	@Test
	public void testUnknownElement() throws Exception {
		final IContextRoot independent =
				ResourceUtils.createContext(rodinProject, "independent", CST_1DECL);
		final IDeclaration declCst1 =
				getDeclCst(independent, INTERNAL_ELEMENT1, CST1);

		final IContextRoot context =
				ResourceUtils.createContext(rodinProject, CTX_BARE_NAME, CST_1REF_AXM);

		final BridgeStub tk = new BridgeStub(context);

		final ContextIndexer indexer = new ContextIndexer();

		assertTrue(indexer.index(tk));

		tk.assertEmptyOccurrences(declCst1.getElement());
	}

	/**
	 * @throws Exception
	 */
	@Test
	public void testTwoImportsSameName() throws Exception {
		final IContextRoot exporter1 =
				ResourceUtils.createContext(rodinProject, "exporter1", CST_1DECL);
		final IDeclaration declCstExp1 =
				getDeclCst(exporter1, INTERNAL_ELEMENT1, CST1);

		final IContextRoot exporter2 =
				ResourceUtils.createContext(rodinProject, "exporter2", CST_1DECL);
		final IDeclaration declCstExp2 =
				getDeclCst(exporter2, INTERNAL_ELEMENT1, CST1);

		final IContextRoot importer =
				ResourceUtils.createContext(rodinProject, "importer", CST_1REF_AXM);

		final BridgeStub tk =
				new BridgeStub(importer, declCstExp1, declCstExp2);

		final ContextIndexer indexer = new ContextIndexer();

		assertTrue(indexer.index(tk));

		tk.assertEmptyOccurrences(declCstExp1.getElement());
		tk.assertEmptyOccurrences(declCstExp2.getElement());
	}

	/**
	 * All other tests only check constants. This test checks a simple
	 * declaration of a carrier set. According to code structure, carrier sets
	 * are treated the same way as constants, so this test is sufficient to
	 * verify that carrier sets are also well treated.
	 * 
	 * @throws Exception
	 */
	@Test
	public void testDeclSet() throws Exception {
		final String SET_1DECL =
				"<?xml version=\"1.0\" encoding=\"UTF-8\"?>"
						+ "<org.eventb.core.contextFile org.eventb.core.configuration=\"org.eventb.core.fwd\" version=\"3\">"
						+ "<org.eventb.core.carrierSet"
						+ "		name=\"internal_element1\""
						+ "		org.eventb.core.identifier=\"set1\"/>"
						+ "</org.eventb.core.contextFile>";

		final String set1IntName = INTERNAL_ELEMENT1;
		final String set1Name = "set1";

		final IContextRoot context =
				ResourceUtils.createContext(rodinProject, CTX_BARE_NAME, SET_1DECL);

		final IDeclaration declSet1 =
				getDeclSet(context, set1IntName, set1Name);

		final BridgeStub tk = new BridgeStub(context);

		final ContextIndexer indexer = new ContextIndexer();

		assertTrue(indexer.index(tk));

		tk.assertDeclarationsOtherThanRoot(declSet1);
	}

	/**
	 * All other tests only check for occurrences in axioms. This test checks a
	 * simple occurrence in a theorem. According to code structure, theorems are
	 * treated the same way as axioms, so this test is sufficient to verify that
	 * theorems are also well treated.
	 * 
	 * @throws Exception
	 */
	@Test
	public void testOccThm() throws Exception {

		final IContextRoot context =
				ResourceUtils.createContext(rodinProject, CTX_BARE_NAME, CST_1DECL_1REF_THM);

		final IConstant cst1 = context.getConstant(INTERNAL_ELEMENT1);
		final IDeclaration declCst1 = newDecl(cst1, CST1);
		
		final IAxiom thm = context.getAxiom(INTERNAL_ELEMENT1);
		final IOccurrence occRef = makeRefPred(thm, 9, 13, declCst1);


		final BridgeStub tk = new BridgeStub(context);

		final ContextIndexer indexer = new ContextIndexer();

		assertTrue(indexer.index(tk));

		tk.assertOccurrencesOtherThanDecl(cst1, occRef);
	}

	@Test
	public void testBadFileType() throws Exception {
		final IMachineRoot machine =
			ResourceUtils.createMachine(rodinProject, MCH_BARE_NAME, VAR_1DECL_1REF_INV);

		final BridgeStub tk = new BridgeStub(machine);

		final ContextIndexer indexer = new ContextIndexer();

		try {
			indexer.index(tk);
			fail("IllegalArgumentException expected");
		} catch (IllegalArgumentException e) {
			// OK
		}
	}

	/**
	 * @throws Exception
	 */
	@Test
	public void testMalformedXML() throws Exception {
		// constant node is not closed with a /
		final String MALFORMED_CONTEXT =
				"<?xml version=\"1.0\" encoding=\"UTF-8\"?>"
						+ "<org.eventb.core.contextFile org.eventb.core.configuration=\"org.eventb.core.fwd\" version=\"3\">"
						+ "<org.eventb.core.constant"
						+ "		name=\"internal_element1\""
						+ "		org.eventb.core.identifier=\"cst1\">"
						+ "</org.eventb.core.contextFile>";

		final IContextRoot context =
				ResourceUtils.createContext(rodinProject, CTX_BARE_NAME, MALFORMED_CONTEXT);

		final BridgeStub tk = new BridgeStub(context);

		final ContextIndexer indexer = new ContextIndexer();

		// should not throw an exception
		assertFalse(indexer.index(tk));
	}

	/**
	 * @throws Exception
	 */
	@Test
	public void testMissingAttribute() throws Exception {
		final String CST_1DECL_1AXM_NO_PRED_ATT =
				"<?xml version=\"1.0\" encoding=\"UTF-8\"?>"
						+ "<org.eventb.core.contextFile org.eventb.core.configuration=\"org.eventb.core.fwd\" version=\"3\">"
						+ "<org.eventb.core.axiom"
						+ "		name=\"internal_element1\""
						+ "		org.eventb.core.label=\"axm1\""
						+ " 	org.eventb.core.theorem=\"false\"/>"
						+ "<org.eventb.core.constant"
						+ "		name=\"internal_element1\""
						+ "		org.eventb.core.comment=\"\""
						+ "		org.eventb.core.identifier=\"cst1\"/>"
						+ "</org.eventb.core.contextFile>";

		final IContextRoot context =
				ResourceUtils.createContext(rodinProject, CTX_BARE_NAME,
						CST_1DECL_1AXM_NO_PRED_ATT);

		final BridgeStub tk = new BridgeStub(context);

		final ContextIndexer indexer = new ContextIndexer();

		// should not throw an exception
		assertTrue(indexer.index(tk));
		// true because the axiom with missing attribute was ignored
	}

	/**
	 * @throws Exception
	 */
	@Test
	public void testDoesNotParse() throws Exception {
		final String CST_1DECL_1AXM_DOES_NOT_PARSE =
				"<?xml version=\"1.0\" encoding=\"UTF-8\"?>"
						+ "<org.eventb.core.contextFile org.eventb.core.configuration=\"org.eventb.core.fwd\" version=\"3\">"
						+ "<org.eventb.core.axiom"
						+ "		name=\"internal_element1\""
						+ "		org.eventb.core.label=\"axm1\""
						+ "		org.eventb.core.predicate=\"(1&lt;\""
						+ " 	org.eventb.core.theorem=\"false\"/>"
						+ "<org.eventb.core.constant"
						+ "		name=\"internal_element1\""
						+ "		org.eventb.core.identifier=\"cst1\"/>"
						+ "</org.eventb.core.contextFile>";

		final IContextRoot context =
				ResourceUtils.createContext(rodinProject, CTX_BARE_NAME,
						CST_1DECL_1AXM_DOES_NOT_PARSE);

		final BridgeStub tk = new BridgeStub(context);

		final ContextIndexer indexer = new ContextIndexer();

		// should not throw an exception
		assertTrue(indexer.index(tk));
		// true because the axiom that did not parse was ignored
	}

	@Test
	public void testRootDeclaration() throws Exception {
		final IContextRoot context = ResourceUtils.createContext(rodinProject,
				CTX_BARE_NAME, EMPTY_CONTEXT);

		final IDeclaration declRoot = newDecl(context, CTX_BARE_NAME);
		final IOccurrence occRoot = makeSelfDecl(declRoot);

		final BridgeStub bridge = new BridgeStub(context);

		final ContextIndexer indexer = new ContextIndexer();

		assertTrue(indexer.index(bridge));

		bridge.assertDeclarations(declRoot);
		bridge.assertOccurrences(context, occRoot);
	}
	
	@Test
	public void testRefExtends() throws Exception {
		final String c1Name = "c1";
		final IContextRoot extRoot = createContext(c1Name);

		final IDeclaration declC1Root = newDecl(extRoot, c1Name);

		final String EXTENDS_CONTEXT = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>"
				+ "<org.eventb.core.contextFile org.eventb.core.configuration=\"org.eventb.core.fwd\" version=\"3\">"
				+ "		<org.eventb.core.extendsContext"
				+ "			name=\"internal_element1\""
				+ "			org.eventb.core.target=\"" + c1Name + "\"/>"
				+ "</org.eventb.core.contextFile>";
		
		final IContextRoot extending = ResourceUtils.createContext(
				rodinProject, "extending", EXTENDS_CONTEXT);

		final IExtendsContext extendsClause = extending.getExtendsClause(INTERNAL_ELEMENT1);
		final IOccurrence occC1Root = makeRefTarget(extendsClause, declC1Root);

		final BridgeStub tk = new BridgeStub(extending, declC1Root);

		final ContextIndexer indexer = new ContextIndexer();

		assertTrue(indexer.index(tk));

		tk.assertOccurrences(declC1Root.getElement(), occC1Root);
	}

	// Verify that root name does not introduce conflicts in symbol table
	@Test
	public void testRootCstSameName() throws Exception {
		final IContextRoot context = ResourceUtils.createContext(rodinProject,
				CST1, CST_1DECL_1REF_THM);
		final IDeclaration declCtxRoot = newDecl(context, CST1);
		final IOccurrence occCtxRoot = makeSelfDecl(declCtxRoot);

		final IConstant cst1 = context.getConstant(INTERNAL_ELEMENT1);
		final IDeclaration declCst1 = newDecl(cst1, CST1);
		final IOccurrence occDeclCst1 = makeDecl(cst1, declCst1);

		final IAxiom thm = context.getAxiom(INTERNAL_ELEMENT1);
		final IOccurrence occRef = makeRefPred(thm, 9, 13, declCst1);

		final BridgeStub tk = new BridgeStub(context);

		final ContextIndexer indexer = new ContextIndexer();

		assertTrue(indexer.index(tk));

		tk.assertDeclarations(declCtxRoot, declCst1);
		tk.assertOccurrences(context, occCtxRoot);
		tk.assertOccurrences(cst1, occDeclCst1, occRef);

	}
}

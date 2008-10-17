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
package org.eventb.core.indexer.tests;

import static org.eventb.core.indexer.tests.OccUtils.*;
import static org.eventb.core.indexer.tests.ResourceUtils.*;

import org.eventb.core.IAxiom;
import org.eventb.core.ICarrierSet;
import org.eventb.core.IConstant;
import org.eventb.core.IContextRoot;
import org.eventb.core.IMachineRoot;
import org.eventb.core.ITheorem;
import org.eventb.core.indexer.ContextIndexer;
import org.rodinp.core.RodinDBException;
import org.rodinp.core.index.IDeclaration;
import org.rodinp.core.index.IOccurrence;

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
	 * @param name
	 * @throws Exception
	 */
	public ContextIndexerTests(String name) throws Exception {
		super(name);
	}

	/**
	 * @throws Exception
	 */
	public void testDeclaration() throws Exception {
		final IContextRoot context = createContext(project, CTX_BARE_NAME,
				CST_1DECL);

		final IDeclaration declCst1 = getDeclCst(context, INTERNAL_ELEMENT1,
				CST1);

		final ToolkitStub tk = new ToolkitStub(context);

		final ContextIndexer indexer = new ContextIndexer();

		indexer.index(tk);

		tk.assertDeclarations(declCst1);
	}

	/**
	 * @throws Exception
	 */
	public void testRefDeclaration() throws Exception {
		final IContextRoot context = createContext(project, CTX_BARE_NAME,
				CST_1DECL);

		final IOccurrence occDecl = makeDecl(context.getRodinFile());

		final IConstant cst1 = context.getConstant(INTERNAL_ELEMENT1);

		final ToolkitStub tk = new ToolkitStub(context);

		final ContextIndexer indexer = new ContextIndexer();

		indexer.index(tk);

		tk.assertOccurrences(cst1, occDecl);

	}

	/**
	 * @throws Exception
	 */
	public void testOccurrenceOtherThanDecl() throws Exception {
		final IContextRoot context = createContext(project, CTX_BARE_NAME,
				CST_1DECL_1REF_AXM);

		final IAxiom axiom = context.getAxiom(INTERNAL_ELEMENT1);
		final IOccurrence occRef = makeRefPred(axiom, 0, 4);

		final IConstant cst1 = context.getConstant(INTERNAL_ELEMENT1);

		final ToolkitStub tk = new ToolkitStub(context);

		final ContextIndexer indexer = new ContextIndexer();

		indexer.index(tk);

		tk.assertOccurrencesOtherThanDecl(cst1, occRef);
	}

	/**
	 * @throws Exception
	 */
	public void testDoubleOccurrenceSameElement() throws Exception {
		final String CST_1DECL_2OCC_SAME_AXM = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>"
				+ "<org.eventb.core.contextFile org.eventb.core.configuration=\"org.eventb.core.fwd\" version=\"1\">"
				+ "<org.eventb.core.constant name=\"internal_element1\" org.eventb.core.identifier=\"cst1\"/>"
				+ "<org.eventb.core.axiom name=\"internal_element1\" org.eventb.core.label=\"axm1\" org.eventb.core.predicate=\"cst1 = cst1\"/>"
				+ "</org.eventb.core.contextFile>";

		final IContextRoot context = createContext(project, CTX_BARE_NAME,
				CST_1DECL_2OCC_SAME_AXM);

		final IAxiom axiom = context.getAxiom(INTERNAL_ELEMENT1);
		final IOccurrence occRef1 = makeRefPred(axiom, 0, 4);
		final IOccurrence occRef2 = makeRefPred(axiom, 7, 11);

		final IConstant cst1 = context.getConstant(INTERNAL_ELEMENT1);

		final ToolkitStub tk = new ToolkitStub(context);

		final ContextIndexer indexer = new ContextIndexer();

		indexer.index(tk);

		tk.assertOccurrencesOtherThanDecl(cst1, occRef1, occRef2);
	}

	/**
	 * @throws Exception
	 */
	public void testExportLocal() throws Exception {
		final IContextRoot context = createContext(project, CTX_BARE_NAME,
				CST_1DECL);

		final IDeclaration declCst1 = getDeclCst(context, INTERNAL_ELEMENT1,
				CST1);

		final ToolkitStub tk = new ToolkitStub(context);

		final ContextIndexer indexer = new ContextIndexer();

		indexer.index(tk);

		tk.assertExports(declCst1);
	}

	/**
	 * @throws Exception
	 */
	public void testExportImported() throws Exception {

		final IContextRoot exporter = createContext(project, "exporter",
				CST_1DECL);

		final IDeclaration declCst1 = getDeclCst(exporter, INTERNAL_ELEMENT1,
				CST1);

		final IContextRoot importer = createContext(project, "importer",
				EMPTY_CONTEXT);

		final ToolkitStub tk = new ToolkitStub(importer, declCst1);

		final ContextIndexer indexer = new ContextIndexer();

		indexer.index(tk);

		tk.assertExports(declCst1);
	}

	private static final String CST_1REF_AXM = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>"
			+ "<org.eventb.core.contextFile org.eventb.core.configuration=\"org.eventb.core.fwd\" version=\"1\">"
			+ "<org.eventb.core.axiom name=\"internal_element1\" org.eventb.core.comment=\"\" org.eventb.core.label=\"axm1\" org.eventb.core.predicate=\"1 &lt; cst1\"/>"
			+ "</org.eventb.core.contextFile>";

	/**
	 * @throws Exception
	 */
	public void testImportedOccurrence() throws Exception {
		final IContextRoot exporter = createContext(project, "exporter",
				CST_1DECL);

		final IDeclaration declCst1 = getDeclCst(exporter, INTERNAL_ELEMENT1,
				CST1);

		final IContextRoot importer = createContext(project, "importer",
				CST_1REF_AXM);

		final IAxiom axiom = importer.getAxiom(INTERNAL_ELEMENT1);
		final IOccurrence occCst1 = makeRefPred(axiom, 4, 8);

		final ToolkitStub tk = new ToolkitStub(importer, declCst1);

		final ContextIndexer indexer = new ContextIndexer();

		indexer.index(tk);

		tk.assertOccurrences(declCst1.getElement(), occCst1);
	}

	/**
	 * @throws Exception
	 */
	public void testUnknownElement() throws Exception {
		final IContextRoot independent = createContext(project, "independent",
				CST_1DECL);
		final IDeclaration declCst1 = getDeclCst(independent,
				INTERNAL_ELEMENT1, CST1);

		final IContextRoot context = createContext(project, CTX_BARE_NAME,
				CST_1REF_AXM);

		final ToolkitStub tk = new ToolkitStub(context);

		final ContextIndexer indexer = new ContextIndexer();

		indexer.index(tk);

		tk.assertEmptyOccurrences(declCst1.getElement());
	}

	/**
	 * @throws Exception
	 */
	public void testTwoImportsSameName() throws Exception {
		final IContextRoot exporter1 = createContext(project, "exporter1",
				CST_1DECL);
		final IDeclaration declCstExp1 = getDeclCst(exporter1,
				INTERNAL_ELEMENT1, CST1);

		final IContextRoot exporter2 = createContext(project, "exporter2",
				CST_1DECL);
		final IDeclaration declCstExp2 = getDeclCst(exporter2,
				INTERNAL_ELEMENT1, CST1);

		final IContextRoot importer = createContext(project, "importer",
				CST_1REF_AXM);

		final ToolkitStub tk = new ToolkitStub(importer, declCstExp1,
				declCstExp2);

		final ContextIndexer indexer = new ContextIndexer();

		indexer.index(tk);

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
	public void testDeclSet() throws Exception {
		final String SET_1DECL = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>"
				+ "<org.eventb.core.contextFile org.eventb.core.configuration=\"org.eventb.core.fwd\" version=\"1\">"
				+ "<org.eventb.core.carrierSet name=\"internal_element1\" org.eventb.core.identifier=\"set1\"/>"
				+ "</org.eventb.core.contextFile>";

		final String set1IntName = INTERNAL_ELEMENT1;
		final String set1Name = "set1";

		final IContextRoot context = createContext(project, CTX_BARE_NAME,
				SET_1DECL);

		final IDeclaration declSet1 = getDeclSet(context, set1IntName, set1Name);

		final ToolkitStub tk = new ToolkitStub(context);

		final ContextIndexer indexer = new ContextIndexer();

		indexer.index(tk);

		tk.assertDeclarations(declSet1);
	}
	
	/**
	 * All other tests only check for occurrences in axioms. This test checks a
	 * simple occurrence in a theorem. According to code structure, theorems are
	 * treated the same way as axioms, so this test is sufficient to verify that
	 * theorems are also well treated.
	 * 
	 * @throws Exception
	 */
	public void testOccThm() throws Exception {

		final IContextRoot context = createContext(project, CTX_BARE_NAME,
				CST_1DECL_1REF_THM);

		final ITheorem thm = context.getTheorem(INTERNAL_ELEMENT1);
		final IOccurrence occRef = makeRefPred(thm, 9, 13);

		final IConstant cst1 = context.getConstant(INTERNAL_ELEMENT1);

		final ToolkitStub tk = new ToolkitStub(context);

		final ContextIndexer indexer = new ContextIndexer();

		indexer.index(tk);

		tk.assertOccurrencesOtherThanDecl(cst1, occRef);
	}

	public void testBadFileType() throws Exception {
		final IMachineRoot machine = createMachine(project, MCH_BARE_NAME,
				VAR_1DECL_1REF_INV);

		final ToolkitStub tk = new ToolkitStub(machine);

		final ContextIndexer indexer = new ContextIndexer();

		try {
			indexer.index(tk);
			fail("IllegalArgumentException expected");
		} catch(IllegalArgumentException e) {
			// OK
		}
	}

	/**
	 * @throws Exception
	 */
	public void testMalformedXML() throws Exception {
		// constant node is not closed with a /
		final String MALFORMED_CONTEXT = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>"
				+ "<org.eventb.core.contextFile org.eventb.core.configuration=\"org.eventb.core.fwd\" version=\"1\">"
				+ "<org.eventb.core.constant name=\"internal_element1\" org.eventb.core.identifier=\"cst1\">"
				+ "</org.eventb.core.contextFile>";

		final IContextRoot context = createContext(project, CTX_BARE_NAME,
				MALFORMED_CONTEXT);

		final ToolkitStub tk = new ToolkitStub(context);

		final ContextIndexer indexer = new ContextIndexer();

		// should not throw an exception
		indexer.index(tk);
	}

	/**
	 * @throws Exception
	 */
	public void testMissingAttribute() throws Exception {
		final String CST_1DECL_1AXM_NO_PRED_ATT = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>"
				+ "<org.eventb.core.contextFile org.eventb.core.configuration=\"org.eventb.core.fwd\" version=\"1\">"
				+ "<org.eventb.core.axiom name=\"internal_element1\" org.eventb.core.label=\"axm1\"/>"
				+ "<org.eventb.core.constant name=\"internal_element1\" org.eventb.core.comment=\"\" org.eventb.core.identifier=\"cst1\"/>"
				+ "</org.eventb.core.contextFile>";

		final IContextRoot context = createContext(project, CTX_BARE_NAME,
				CST_1DECL_1AXM_NO_PRED_ATT);

		final ToolkitStub tk = new ToolkitStub(context);

		final ContextIndexer indexer = new ContextIndexer();

		// should not throw an exception
		indexer.index(tk);
	}

	/**
	 * @throws Exception
	 */
	public void testDoesNotParse() throws Exception {
		final String CST_1DECL_1AXM_DOES_NOT_PARSE = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>"
				+ "<org.eventb.core.contextFile org.eventb.core.configuration=\"org.eventb.core.fwd\" version=\"1\">"
				+ "<org.eventb.core.axiom name=\"internal_element1\" org.eventb.core.label=\"axm1\" org.eventb.core.predicate=\"(1&lt;\"/>"
				+ "<org.eventb.core.constant name=\"internal_element1\" org.eventb.core.identifier=\"cst1\"/>"
				+ "</org.eventb.core.contextFile>";

		final IContextRoot context = createContext(project, CTX_BARE_NAME,
				CST_1DECL_1AXM_DOES_NOT_PARSE);

		final ToolkitStub tk = new ToolkitStub(context);

		final ContextIndexer indexer = new ContextIndexer();

		// should not throw an exception
		indexer.index(tk);
	}

}

/*******************************************************************************
 * Copyright (c) 2005 ETH-Zurich
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     ETH RODIN Group
 *******************************************************************************/

package org.eventb.eventBKeyboard.internal.tests;

import junit.framework.TestCase;

import org.eventb.eventBKeyboard.Text2EventBMathTranslation;

/**
 * @author htson
 * This class contains some simple test cases for Event-B Keyboard.
 * This test all the symbols separately.
 */
public class Text2EventBMathSimpleTestCase extends TestCase {

//	private Text formula;
//	private EventBTextModifyListener listener; 
	
//	protected void setUp() throws Exception {
//		super.setUp();
//
//		IWorkbenchPage page = EventBKeyboardPlugin.getActivePage();
//		
//		EventBKeyboardView view = (EventBKeyboardView) page.findView(EventBKeyboardPlugin.EventBKeyboardView_ID);
//		
//		if (view == null) 
//			view = (EventBKeyboardView) page.showView(EventBKeyboardPlugin.EventBKeyboardView_ID);
//		
//		formula = view.getFormula();
//		listener = view.getListener();
//		
//		// Remove the listener
//		// In order to simulate user's input, we have to manually setup the listener.
//		formula.removeModifyListener(listener); 
//	}

//	protected void tearDown() throws Exception {
//		super.tearDown();
//		formula.addModifyListener(listener);
//	}

	/**
	 *  We use this method to similate the action of typing a character into the
	 *  text area
	 */ 
//	private void insert(String s) {
//		formula.insert(s);
//		Event e = new Event();
//		e.widget = formula;
//		listener.modifyText(new ModifyEvent(e)); // register the listener for modifying
//		formula.removeModifyListener(listener); // then remove it again
//	}
	

	public void testNAT() {
		assertEquals("NAT", "\u2115", Text2EventBMathTranslation.translate("NAT"));
		assertEquals("NAT ", "\u2115 ", Text2EventBMathTranslation.translate("NAT "));
		assertEquals(" NAT", " \u2115", Text2EventBMathTranslation.translate(" NAT"));
		assertEquals(" NAT ", " \u2115 ", Text2EventBMathTranslation.translate(" NAT "));
	}	

	public void testNAT1() {
		assertEquals("NAT1", "\u2115\u0031", Text2EventBMathTranslation.translate("NAT1"));
		assertEquals("NAT1 ", "\u2115\u0031 ", Text2EventBMathTranslation.translate("NAT1 "));
		assertEquals(" NAT1", " \u2115\u0031", Text2EventBMathTranslation.translate(" NAT1"));
		assertEquals("NAT1 ", " \u2115\u0031 ", Text2EventBMathTranslation.translate(" NAT1 "));
	}
	
	public void testPOW() {
		assertEquals("POW", "\u2119", Text2EventBMathTranslation.translate("POW"));
		assertEquals("POW ", "\u2119 ", Text2EventBMathTranslation.translate("POW "));
		assertEquals(" POW", " \u2119", Text2EventBMathTranslation.translate(" POW"));
		assertEquals(" POW ", " \u2119 ", Text2EventBMathTranslation.translate(" POW "));
	}
	
	public void testPOW1() {
		assertEquals("POW1", "\u2119\u0031", Text2EventBMathTranslation.translate("POW1"));
		assertEquals("POW1 ", "\u2119\u0031 ", Text2EventBMathTranslation.translate("POW1 "));
		assertEquals(" POW1", " \u2119\u0031", Text2EventBMathTranslation.translate(" POW1"));
		assertEquals(" POW1 ", " \u2119\u0031 ", Text2EventBMathTranslation.translate(" POW1 "));
	}
	
	public void testINT() {
		assertEquals("INT", "\u2124", Text2EventBMathTranslation.translate("INT"));
		assertEquals("INT ", "\u2124 ", Text2EventBMathTranslation.translate("INT "));
		assertEquals(" INT", " \u2124", Text2EventBMathTranslation.translate(" INT"));
		assertEquals(" INT ", " \u2124 ", Text2EventBMathTranslation.translate(" INT "));
	}
	
	public void testLogicalEquivalent() {
		assertEquals("<=>", "\u21d4", Text2EventBMathTranslation.translate("<=>"));
	}
	
	public void testImply() {
		assertEquals("=>", "\u21d2", Text2EventBMathTranslation.translate("=>"));
	}

	public void testAnd() {
		assertEquals("&", "\u2227", Text2EventBMathTranslation.translate("&"));
	}
	
	public void testOr() {
		assertEquals("or", "\u2228", Text2EventBMathTranslation.translate("or"));
		assertEquals("or ", "\u2228 ", Text2EventBMathTranslation.translate("or "));
		assertEquals(" or", " \u2228", Text2EventBMathTranslation.translate(" or"));
		assertEquals(" or ", " \u2228 ", Text2EventBMathTranslation.translate(" or "));
	}
	
	public void testNot() {
		assertEquals("not", "\u00ac", Text2EventBMathTranslation.translate("not"));
		assertEquals("not ", "\u00ac ", Text2EventBMathTranslation.translate("not "));
		assertEquals(" not", " \u00ac", Text2EventBMathTranslation.translate(" not"));
		assertEquals(" not ", " \u00ac ", Text2EventBMathTranslation.translate(" not "));
	}
			
	public void testTrue() {
		assertEquals("true", "\u22a4", Text2EventBMathTranslation.translate("true"));
		assertEquals("true ", "\u22a4 ", Text2EventBMathTranslation.translate("true "));
		assertEquals(" true", " \u22a4", Text2EventBMathTranslation.translate(" true"));
		assertEquals(" true ", " \u22a4 ", Text2EventBMathTranslation.translate(" true "));
	}
	
	public void testFalse() {
		assertEquals("false", "\u22a5", Text2EventBMathTranslation.translate("false"));
		assertEquals("false ", "\u22a5 ", Text2EventBMathTranslation.translate("false "));
		assertEquals(" false", " \u22a5", Text2EventBMathTranslation.translate(" false"));
		assertEquals(" false ", " \u22a5 ", Text2EventBMathTranslation.translate(" false "));
	}
	
	public void testForall() {
		assertEquals("!", "\u2200", Text2EventBMathTranslation.translate("!"));
	}	
	
	public void testThereExists() {
		assertEquals("#", "\u2203", Text2EventBMathTranslation.translate("#"));
	}
	
	public void testMiddleDot() {
		assertEquals(".", "\u00b7", Text2EventBMathTranslation.translate("."));
	}
	
	public void testNotEqual() {
		assertEquals("/=", "\u2260", Text2EventBMathTranslation.translate("/="));
	}
	
	public void testLessThanEqual() {
		assertEquals("<=", "\u2264", Text2EventBMathTranslation.translate("<="));
	}
	
	public void testGreaterThanEqual() {
		assertEquals(">=", "\u2265", Text2EventBMathTranslation.translate(">="));
	}
	
	public void testElementOf() {
		assertEquals(":", "\u2208", Text2EventBMathTranslation.translate(":"));
	}
	
	public void testNotAnElementOf() {
		assertEquals("/:", "\u2209", Text2EventBMathTranslation.translate("/:"));
	}
	
	public void testSubsetOf() {
		assertEquals("<<:", "\u2282", Text2EventBMathTranslation.translate("<<:"));
	}
	
	public void testNotASubsetOf() {
		assertEquals("/<<:", "\u2284", Text2EventBMathTranslation.translate("/<<:"));
	}
	
	public void testSubsetOrEqualTo() {
		assertEquals("<:", "\u2286", Text2EventBMathTranslation.translate("<:"));
	}
	
	public void testNotASubsetOfNorEqualTo() {
		assertEquals("/<:", "\u2288", Text2EventBMathTranslation.translate("/<:"));
	}
	
	public void testRelation() {
		assertEquals("<->", "\u2194", Text2EventBMathTranslation.translate("<->"));
	}
	
	public void testTotalRelation() {
		assertEquals(":", "\ue100", Text2EventBMathTranslation.translate("<<->"));
	}
	
	public void testSurjectiveRelation() {
		assertEquals("<->>", "\ue101", Text2EventBMathTranslation.translate("<->>"));
	}
	
	public void testTotalSurjectiveRelation() {
		assertEquals("<<->>", "\ue102", Text2EventBMathTranslation.translate("<<->>"));
	}
	
	public void testPartialFunction() {
		assertEquals("+->", "\u21f8", Text2EventBMathTranslation.translate("+->"));
	}
	
	public void testTotalFunction() {
		assertEquals("-->", "\u2192", Text2EventBMathTranslation.translate("-->"));
	}
	
	public void testPartialInjectiveFunction() {
		assertEquals(">+>", "\u2914", Text2EventBMathTranslation.translate(">+>"));
	}
	
	public void testTotalInjectiveFunction() {
		assertEquals(">->", "\u21a3", Text2EventBMathTranslation.translate(">->"));
	}
	
	public void testPartialSurjectiveFunction() {
		assertEquals("+>>", "\u2900", Text2EventBMathTranslation.translate("+>>"));
	}
	
	public void testTotalSurjectiveFunction() {
		assertEquals("->>", "\u21a0", Text2EventBMathTranslation.translate("->>"));
	}
	
	public void testBijectiveFunction() {
		assertEquals(">->>", "\u2916", Text2EventBMathTranslation.translate(">->>"));
	}
	
	public void testMaplet() {
		assertEquals("|->", "\u21a6", Text2EventBMathTranslation.translate("|->"));
	}

	public void testEmptySet() {
		assertEquals("{}", "\u2205", Text2EventBMathTranslation.translate("{}"));
	}
	
	
	public void testIntersection() {
		assertEquals("/\\", "\u2229", Text2EventBMathTranslation.translate("/\\"));
	}
	
	public void testUnion() {
		assertEquals("\\/", "\u222a", Text2EventBMathTranslation.translate("\\/"));
	}

	public void testSetMinus() {
		assertEquals("\\", "\u2216", Text2EventBMathTranslation.translate("\\"));
	}

	public void testCartesianProduct() {
		assertEquals("**", "\u00d7", Text2EventBMathTranslation.translate("**"));
	}
	
	public void testRelationOverriding() {
		assertEquals("<+", "\ue103", Text2EventBMathTranslation.translate("<+"));
	}
	
	public void testBackwardComposition() {
		assertEquals("circ", "\u2218", Text2EventBMathTranslation.translate("circ"));		
		assertEquals("circ ", "\u2218 ", Text2EventBMathTranslation.translate("circ "));		
		assertEquals(" circ", " \u2218", Text2EventBMathTranslation.translate(" circ"));		
		assertEquals(" circ ", " \u2218 ", Text2EventBMathTranslation.translate(" circ "));		
	}
	
	public void testDirectProduct() {
		assertEquals("><", "\u2297", Text2EventBMathTranslation.translate("><"));		
	}
	
	public void testParallelProduct() {
		assertEquals("||", "\u2225", Text2EventBMathTranslation.translate("||"));		
	}
	
	public void testTildeOperator() {
		assertEquals("~", "\u223c", Text2EventBMathTranslation.translate("~"));		
	}
	
	public void testDomainRestriction() {
		assertEquals("<|", "\u25c1", Text2EventBMathTranslation.translate("<|"));		
	}	
	
	public void testDomainSubstraction() {
		assertEquals("<<|", "\u2a64", Text2EventBMathTranslation.translate("<<|"));		
	}	
	
	public void testRangeRestriction() {
		assertEquals("|>", "\u25b7", Text2EventBMathTranslation.translate("|>"));		
	}	
	
	public void testRangeSubstraction() {
		assertEquals("|>>", "\u2a65", Text2EventBMathTranslation.translate("|>>"));		
	}	
		
	public void testLambda() {
		assertEquals("%", "\u03bb", Text2EventBMathTranslation.translate("%"));		
	}
	
	public void testINTER() {
		assertEquals("INTER", "\u22c2", Text2EventBMathTranslation.translate("INTER"));		
		assertEquals("INTER ", "\u22c2 ", Text2EventBMathTranslation.translate("INTER "));		
		assertEquals(" INTER", " \u22c2", Text2EventBMathTranslation.translate(" INTER"));		
		assertEquals(" INTER ", " \u22c2 ", Text2EventBMathTranslation.translate(" INTER "));		
	}
	
	public void testUNION() {
		assertEquals("UNION", "\u22c3", Text2EventBMathTranslation.translate("UNION"));		
		assertEquals("UNION ", "\u22c3 ", Text2EventBMathTranslation.translate("UNION "));		
		assertEquals(" UNION", " \u22c3", Text2EventBMathTranslation.translate(" UNION"));		
		assertEquals(" UNION ", " \u22c3 ", Text2EventBMathTranslation.translate(" UNION "));		
	}
	
	public void testUptoOperator() {
		assertEquals("..", "\u2025", Text2EventBMathTranslation.translate(".."));		
	}

	public void testMinus() {
		assertEquals("-", "\u2212", Text2EventBMathTranslation.translate("-"));		
	}

	public void testAsterisk() {
		assertEquals("*", "\u2217", Text2EventBMathTranslation.translate("*"));		
	}

	public void testDivision() {
		assertEquals("/", "\u00f7", Text2EventBMathTranslation.translate("/"));		
	}

	public void testBecomesEqual() {
		assertEquals(":=", "\u2254", Text2EventBMathTranslation.translate(":="));		
	}
	
	public void testBecomesAnElementOf() {
		assertEquals("::", ":\u2208", Text2EventBMathTranslation.translate("::"));		
	}


	public void testBecomesSuchThat() {
		assertEquals(":|", ":\u2223", Text2EventBMathTranslation.translate(":|"));		
	}


	public void testMid() {
		assertEquals("|", "\u2223", Text2EventBMathTranslation.translate("|"));		
	}

}

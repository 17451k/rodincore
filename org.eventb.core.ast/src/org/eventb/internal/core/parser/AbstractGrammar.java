/*******************************************************************************
 * Copyright (c) 2010 Systerel and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Systerel - initial API and implementation
 *******************************************************************************/
package org.eventb.internal.core.parser;

import static org.eventb.internal.core.parser.OperatorRegistry.GROUP0;
import static org.eventb.internal.core.parser.OperatorRegistry.OperatorRelationship.INCOMPATIBLE;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eventb.core.ast.Formula;
import org.eventb.core.ast.LanguageVersion;
import org.eventb.core.ast.extension.CycleError;
import org.eventb.core.ast.extension.IOperatorProperties;
import org.eventb.internal.core.lexer.Token;
import org.eventb.internal.core.parser.GenParser.OverrideException;
import org.eventb.internal.core.parser.OperatorRegistry.OperatorRelationship;

/**
 * @author Nicolas Beauger
 *
 */
public abstract class AbstractGrammar {

	private static final String EMPTY_STRING = "";
	private static final String EOF_ID = "End of File";
	private static final String NOOP_ID = "No Operator";
	private static final String OPEN_ID = "Open";
	private static final String IDENT_IMAGE = "an identifier";
	private static final String INTLIT_IMAGE = "an integer literal";

	public static int _EOF;
	static int _NOOP;
	static int _OPEN;
	static int _LPAR;
	static int _RPAR;
	public static int _IDENT;
	public static int _INTLIT;
	static int _COMMA;

	protected final IndexedSet<String> tokens = new IndexedSet<String>();
	
	private final LexKindParserDB subParsers = new LexKindParserDB();
	
	protected final OperatorRegistry opRegistry = new OperatorRegistry();
	
	// used by extended grammar to fetch appropriate parser
	// and by extended formulae to fetch appropriate printers
	// TODO try to generalise to standard language operators
	private final PropertyParserDB propParsers = new PropertyParserDB();
	
	private final Map<Integer, Integer> closeOpenKinds = new HashMap<Integer, Integer>();
	
	private final AllInOnceMap<Integer, IParserPrinter<? extends Formula<?>>> tagParsers = new AllInOnceMap<Integer, IParserPrinter<? extends Formula<?>>>();
	private final AllInOnceMap<Integer, Integer> tagKind = new AllInOnceMap<Integer, Integer>();
	
	public boolean isOperator(int kind) {
		// TODO could be replaced by 'there exists a tag for the given kind'
		return opRegistry.hasGroup(kind) && !tokens.isReserved(kind);
	}
	
	public IndexedSet<String> getTokens() {
		return tokens;
	}

	/**
	 * Initialises tokens, parsers and operator relationships.
	 * <p>
	 * Subclasses are expected to override and call this method first.
	 * </p>
	 */
	// TODO split into several init methods, one for each data (?)
	public void init() {
		_EOF = tokens.reserved("End Of Formula");
		_NOOP = tokens.reserved("No Operator");
		_OPEN = tokens.reserved("Open");
		_LPAR = tokens.getOrAdd("(");
		_RPAR = tokens.getOrAdd(")");
		_COMMA = tokens.getOrAdd(",");
		
		opRegistry.addOperator(_EOF, EOF_ID, GROUP0);
		opRegistry.addOperator(_NOOP, NOOP_ID, GROUP0);
		opRegistry.addOperator(_OPEN, OPEN_ID, GROUP0);
		addOpenClose("(", ")");
		try {
			_INTLIT = addReservedSubParser(SubParsers.INTLIT_SUBPARSER, INTLIT_IMAGE);
			_IDENT = addReservedSubParser(SubParsers.IDENT_SUBPARSER, IDENT_IMAGE);
			addTagKindParser(SubParsers.FREE_IDENT_SUBPARSER, _IDENT);
			addTagKindParser(SubParsers.BOUND_IDENT_DECL_SUBPARSER, _IDENT);
			subParsers.addNud(_LPAR, MainParsers.CLOSED_SUGAR);
		} catch (OverrideException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	public List<INudParser<? extends Formula<?>>> getNudParsers(Token token) {
		return subParsers.getNudParsers(token);
	}
	
	public ILedParser<? extends Formula<?>> getLedParser(Token token) {
		return subParsers.getLedParser(token);
	}
	
	public IParserPrinter<? extends Formula<?>> getParser(IOperatorProperties operProps,
			int tag) {
		return propParsers.getParser(operProps, tag);
	}

	protected void addParser(IParserInfo<? extends Formula<?>> parserBuilder) throws OverrideException {
		propParsers.add(parserBuilder);
	}
	
	protected void addOperator(String token, String operatorId, String groupId,
			INudParser<? extends Formula<?>> subParser)
			throws OverrideException {
		final int kind = tokens.getOrAdd(token);
		opRegistry.addOperator(kind, operatorId, groupId);
		subParsers.addNud(kind, subParser);
		addTagKindParser(subParser, kind);
	}

	// FIXME remove method after correctly refactoring so as not to need it
	protected void addOperator(String token, int tag, String operatorId,
			String groupId, INudParser<? extends Formula<?>> subParser)
			throws OverrideException {
		final int kind = tokens.getOrAdd(token);
		opRegistry.addOperator(kind, operatorId, groupId);
		subParsers.addNud(kind, subParser);
		addTagKindParser(subParser, kind, tag);
	}

	protected void addOperator(String token, String operatorId, String groupId,
			ILedParser<? extends Formula<?>> subParser)
			throws OverrideException {
		final int kind = tokens.getOrAdd(token);
		opRegistry.addOperator(kind, operatorId, groupId);
		subParsers.addLed(kind, subParser);
		addTagKindParser(subParser, kind);
	}

	protected void addOperator(int kind, String operatorId, String groupId,
			INudParser<? extends Formula<?>> subParser)
			throws OverrideException {
		opRegistry.addOperator(kind, operatorId, groupId);
		subParsers.addNud(kind, subParser);
		addTagKindParser(subParser, kind);
	}

	protected void addOpenClose(String open, String close) {
		final int openKind = tokens.getOrAdd(open);
		final int closeKind = tokens.getOrAdd(close);
		closeOpenKinds.put(closeKind, openKind);
	}

	public boolean isOpen(int kind) {
		return closeOpenKinds.containsValue(kind);
	}

	public boolean isClose(int kind) {
		return closeOpenKinds.containsKey(kind);
	}

	private int addReservedSubParser(INudParser<? extends Formula<?>> subParser, String image)
			throws OverrideException {
		final int kind = tokens.reserved(image);
		subParsers.addNud(kind, subParser);
		addTagKindParser(subParser, kind);
		return kind;
	}
	
	protected void addGroupPrioritySequence(String... groupIds) throws CycleError {
		for (int i = 0; i < groupIds.length - 1; i++) {
			opRegistry.addGroupPriority(groupIds[i], groupIds[i+1]);
		}
	}
	
	public OperatorRelationship getOperatorRelationship(int leftKind,
			int rightKind, LanguageVersion version) {
		return opRegistry.getOperatorRelationship(leftKind, rightKind, version);
	}
	
	public int getEOF() {
		return _EOF;
	}
	
	public int getIDENT() {
		return _IDENT;
	}
	
	public int getINTLIT() {
		return _INTLIT;
	}
	
	public String getImage(int kind) {
		return tokens.getElem(kind);
	}

	public String getTagImage(int tag) {
		final Integer kind = tagKind.get(tag);
		if (kind == null) {
			return EMPTY_STRING;
		}
		return getImage(kind);
	}

	private void addTagKindParser(IParserPrinter<? extends Formula<?>> parser,
			int kind, int tag) {
		if (tag == Formula.NO_TAG) {
			return;
		}
		if (!tagKind.containsKey(tag)) {
			// FIXME should remove !tagKind.containsKey(tag) but
			// problem with Lambda (same tag for kinds 'lambda' and '{')
			tagKind.put(tag, kind);
		}
		if (!tagParsers.containsKey(tag)) {
			tagParsers.put(tag, parser);
		}
	}

	// FIXME should be private
	private void addTagKindParser(IParserPrinter<? extends Formula<?>> parser,
			int kind) {
		for (int tag : parser.getTags()) {
			addTagKindParser(parser, kind, tag);
		}
	}

	public <T extends Formula<?>> IParserPrinter<T> getParser(T formula) {
		return (IParserPrinter<T>) tagParsers.get(formula.getTag());
	}

	/**
	 * Returns whether parentheses are needed around a formula tag when it
	 * appears as a child of formula parentTag.
	 * 
	 * @param isRightChild
	 *            <code>true</code> if tag node is the right child parentTag,
	 *            <code>false</code> if it is the left child or a unique child
	 * @param childTag
	 * @param parentTag
	 * @param version
	 * @return <code>true</code> iff parentheses are needed
	 * @since 2.0
	 */
	public boolean needsParentheses(boolean isRightChild, int childTag, int parentTag, LanguageVersion version) {
		final Integer childKind = tagKind.getNoCheck(childTag);
		final Integer parentKind = tagKind.getNoCheck(parentTag);
		if (childKind == null || parentKind == null) { // EOF for instance
			return false;
		}
		if (!isOperator(parentKind) || !isOperator(childKind)) {
			return false; // IDENT for instance
		}
		final OperatorRelationship opRel = getOperatorRelationship(parentKind,
				childKind, version);
		
		return opRel == INCOMPATIBLE;
	}

}

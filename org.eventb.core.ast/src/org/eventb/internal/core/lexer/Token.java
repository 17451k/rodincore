/*******************************************************************************
 * Copyright (c) 2005, 2012 ETH Zurich and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     ETH Zurich - initial API and implementation
 *******************************************************************************/
package org.eventb.internal.core.lexer;

/**
 * Tokens of Event-B mathematical language.
 * 
 * These tokens are produced by the scanner and consumed by the parser.
 * 
 * @author François Terrier
 */
public class Token {
	public final int kind;   // token code
	public final String val; // token value
	public int pos;    // position in source stream
	
	public Token(int kind, String val, int pos) {
		this.kind = kind;
		this.val = val;
		this.pos = pos;
	}
	
	protected Token(int kind, String val) {
		this.val = val;
		this.kind = kind;
	}
	
	public int getEnd() {
		return pos + val.length() - 1;
	}
	
	@Override
	public String toString() {
		return "Kind: " + kind + " Pos: " + pos;
	}
}

/*
 * Created on 22-abr-2005
 *
 * To change the template for this generated file go to
 * Window - Preferences - Java - Code Generation - Code and Comments
 */
package org.eventb.internal.core.parser;

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
	protected int pos;    // position in source stream
	
	protected Token(int kind, String val, int pos) {
		this.kind = kind;
		this.val = val;
		this.pos = pos;
	}
	
	protected Token(int kind, String val) {
		this.val = val;
		this.kind = kind;
	}
	
	@Override
	public String toString() {
		return "Kind: " + kind + " Pos: " + pos;
	}
}

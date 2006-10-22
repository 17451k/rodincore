/*
 * Created on 11-may-2005
 *
 */
package org.eventb.core.ast;

import java.util.Set;




/**
 * This is the base class for all identifiers in an event-B formula.
 * 
 * @author François Terrier
 *
 */
public abstract class Identifier extends Expression {
	
	protected Identifier(int tag, SourceLocation location, int hashCode) {
		super(tag, location, hashCode);
	}

	@Override
	protected final void addGivenTypes(Set<GivenType> set) {
		// Already done at the global level, nothing to do locally
	}

}

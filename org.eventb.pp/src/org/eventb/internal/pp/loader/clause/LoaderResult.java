package org.eventb.internal.pp.loader.clause;

import java.util.List;

import org.eventb.internal.pp.core.elements.Clause;

/**
 * TODO comment
 *
 * @author François Terrier
 *
 */
public class LoaderResult {

	// TODO it is questionable whether we already separate this here
	// unit clauses are also clauses
	private List<Clause> clauses;
//	private Collection<Clause> unitClauses;
	
	protected LoaderResult(List<Clause> clauses) {
		this.clauses = clauses;
//		this.unitClauses = unitClauses;
	}
	
	/**
	 * Returns the non-unit clauses.
	 * 
	 * @return the non-unit clauses
	 */
	public List<Clause> getClauses() {
		return clauses;
	}
	
	@Override
	public String toString() {
		return clauses.toString();
	}
	
//	/**
//	 * Returns the unit clauses.
//	 * 
//	 * @return the unit clauses
//	 */
//	public Collection<Clause> getLiterals() {
//		return unitClauses;
//	}
}

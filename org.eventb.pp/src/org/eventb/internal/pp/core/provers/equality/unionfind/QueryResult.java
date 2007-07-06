package org.eventb.internal.pp.core.provers.equality.unionfind;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.eventb.internal.pp.core.elements.Clause;
import org.eventb.internal.pp.core.elements.EqualityLiteral;
import org.eventb.internal.pp.core.provers.equality.IQueryResult;
import org.eventb.internal.pp.core.provers.equality.unionfind.Source.FactSource;
import org.eventb.internal.pp.core.provers.equality.unionfind.Source.QuerySource;

public class QueryResult implements IQueryResult {

	private final QuerySource querySource;
	private final Set<FactSource> factSource;
	private final boolean value;
	
	public QueryResult(QuerySource querySource, Set<FactSource> factSource, boolean value) {
		this.querySource = querySource;
		this.factSource = factSource;
		this.value = value;
	}
	
//	public EqualityFormula getEquality() {
//		return querySource.getEquality();
//	}

	public List<Clause> getSolvedValueOrigin() {
		List<Clause> result = new ArrayList<Clause>();
		for (FactSource source : factSource) {
			result.add(source.getClause());
		}
		return result;
	}

	public Set<Clause> getSolvedClauses() {
		return querySource.getClauses();
	}
	
	public boolean getValue() {
		return value;
	}

	public Set<FactSource> getSolvedValueSource() {
		return factSource;
	}
	
	public QuerySource getQuerySource() {
		return querySource;
	}

	public EqualityLiteral getEquality() {
		return querySource.getEquality();
	}

}

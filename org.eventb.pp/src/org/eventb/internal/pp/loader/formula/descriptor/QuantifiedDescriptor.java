package org.eventb.internal.pp.loader.formula.descriptor;

import org.eventb.internal.pp.loader.predicate.IContext;

public class QuantifiedDescriptor extends IndexedDescriptor {


	public QuantifiedDescriptor(IContext context, int index /* , List<TermSignature> definingTerms */) {
		super(context, index);
//		this.definingTerms = definingTerms;
	}
	
	@Override
	public String toString() {
		return "Q"+index;
	}
	
}

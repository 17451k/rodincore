package org.eventb.core;

import org.eventb.core.result.type.TypeEnvironment;
import org.rodinp.core.IRodinElement;
import org.rodinp.core.RodinDBException;
import org.rodinp.core.RodinElement;
import org.rodinp.core.UnnamedInternalElement;

/**
 * @author halstefa
 *
 * A predicate form is a predicate to be substituted.
 * It is described by a pair (SUBST, PRED).
 * It consists of a substitution SUBST and
 * a predicate or predicate form PRED.
 * SUBST is stored in the contents and PRED is the only child.
 *
 */
public class POPredicateForm extends UnnamedInternalElement {

	public static final String ELEMENT_TYPE = EventBPlugin.PLUGIN_ID + ".popredicateform";
	
	private POAnyPredicate anyPredicate = null;
	
	public POPredicateForm(String type, RodinElement parent) {
		super(type, parent);
		// TODO Auto-generated constructor stub
	}

	/* (non-Javadoc)
	 * @see org.rodinp.core.RodinElement#getElementType()
	 */
	@Override
	public String getElementType() {
		return ELEMENT_TYPE;
	}
	
	public String getSubstitution(TypeEnvironment environment) throws RodinDBException {
		return getContents();
	}
	
	public POAnyPredicate getPredicate() throws RodinDBException {
		if(anyPredicate == null) {
			anyPredicate = (POAnyPredicate) getChildren()[0];
		}
		return anyPredicate;
	}

}

package org.eventb.internal.ui;

import org.eventb.core.IPredicateElement;
import org.eventb.ui.IElementModifier;
import org.rodinp.core.IRodinElement;
import org.rodinp.core.RodinDBException;

public class PredicateModifier implements IElementModifier {

	public void modify(IRodinElement element, String text)
			throws RodinDBException {
		if (element instanceof IPredicateElement) {
			((IPredicateElement) element).setPredicateString(text);
		}
		return;
	}

}

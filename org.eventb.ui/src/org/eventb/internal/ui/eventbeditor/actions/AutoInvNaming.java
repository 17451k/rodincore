package org.eventb.internal.ui.eventbeditor.actions;

import org.eclipse.jface.action.IAction;
import org.eventb.core.IInvariant;

public class AutoInvNaming extends AutoElementNaming {

	public void run(IAction action) {
		rename(IInvariant.ELEMENT_TYPE, "inv");
	}

}

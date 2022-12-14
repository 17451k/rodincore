/*******************************************************************************
 * Copyright (c) 2011, 2012 Systerel and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Systerel - initial API and implementation
 *******************************************************************************/
package org.rodinp.core.emf.lightcore.adapters;


import org.eclipse.emf.common.notify.impl.AdapterImpl;
import org.rodinp.core.ElementChangedEvent;
import org.rodinp.core.IElementChangedListener;
import org.rodinp.core.RodinCore;
import org.rodinp.core.emf.lightcore.LightElement;

/**
 * 
 * This is the adapter for light root elements which have to listen for Rodin
 * Database modifications, thus updating their light model accordingly.
 * 
 * @author Thomas Muller
 * 
 */
public class DeltaRootAdapter extends AdapterImpl implements
		IElementChangedListener {

	private final DeltaProcessor processor;

	public DeltaRootAdapter(LightElement root) {
		this.processor = new DeltaProcessor(this, root);
		RodinCore.addElementChangedListener(this);
	}

	@Override
	public void elementChanged(ElementChangedEvent event) {
		processor.processDelta(event.getDelta());
	}

	@Override
	public boolean isAdapterForType(Object type) {
		return DeltaRootAdapter.class == type;
	}

	public void finishListening() {
		RodinCore.removeElementChangedListener(this);
	}

}

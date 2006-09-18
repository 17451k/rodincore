/*******************************************************************************
 * Copyright (c) 2006 ETH Zurich.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/

package org.rodinp.internal.core;

import org.rodinp.core.basis.RodinFile;


/**
 * Implements a cache of Rodin file buffers.
 * 
 * 
 * @author Laurent Voisin
 */
public class BufferCache extends OverflowingLRUCache<RodinFile, Buffer> {

	public BufferCache(int size) {
		super(size);
	}

	public BufferCache(int size, int overflow) {
		super(size, overflow);
	}

	@Override
	protected boolean close(LRUCacheEntry<RodinFile, Buffer> entry) {
		Buffer buffer = entry._fValue;
		return ! buffer.hasUnsavedChanges();
	}

	@Override
	protected OverflowingLRUCache<RodinFile, Buffer> newInstance(int size,
			int overflow) {
		return null;
	}

}

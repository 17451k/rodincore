/*******************************************************************************
 * Copyright (c) 2005 ETH Zurich.
 * Strongly inspired by org.eclipse.jdt.internal.core.JavaModelCache.java which is
 * 
 * Copyright (c) 2000, 2004 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.rodinp.internal.core;

import static org.rodinp.core.IRodinElement.RODIN_DATABASE;
import static org.rodinp.core.IRodinElement.RODIN_PROJECT;

import java.text.NumberFormat;
import java.util.HashMap;

import org.rodinp.core.basis.Openable;
import org.rodinp.core.basis.RodinFile;

/**
 * The cache of Rodin elements to their respective info.
 */
public class RodinDBCache {

	private static final int BASE_VALUE = 20;

	// average 25552 bytes per project.
	private static final int DEFAULT_PROJECT_SIZE = 5;

	// average 6629 bytes per openable (includes members)
	// -> maximum size : 662900*BASE_VALUE bytes
	private static final int DEFAULT_OPENABLE_SIZE = BASE_VALUE * 100;

	private static final int DEFAULT_BUFFER_SIZE = DEFAULT_OPENABLE_SIZE;
	
	/**
	 * Active Rodin Model Info
	 */
	private RodinDBInfo modelInfo;

	/**
	 * Cache of open projects.
	 */
	private HashMap<RodinProject, RodinProjectElementInfo> projectCache;

	/**
	 * Cache of open Rodin files
	 */
	private OpenableCache openableCache;

	/**
	 * Cache of Rodin file buffers
	 */
	private BufferCache bufferCache;

	public RodinDBCache() {
		// NB: Don't use a LRUCache for projects as they are constantly reopened
		// (e.g. during delta processing)
		this.projectCache = new HashMap<RodinProject, RodinProjectElementInfo>(
				DEFAULT_PROJECT_SIZE);
		this.openableCache = new OpenableCache(DEFAULT_OPENABLE_SIZE);
		this.bufferCache = new BufferCache(DEFAULT_BUFFER_SIZE);
	}

	/**
	 * Returns the info for the element.
	 */
	public OpenableElementInfo getInfo(Openable element) {
		String elementType = element.getElementType();
		if (elementType == RODIN_DATABASE) {
			return this.modelInfo;
		}
		if (elementType == RODIN_PROJECT) {
			return this.projectCache.get(element);
		}
		return this.openableCache.get(element);
	}

	/**
	 * Returns the buffer for the given Rodin file.
	 */
	public Buffer getBuffer(RodinFile rodinFile) {
		return this.bufferCache.get(rodinFile);
	}

	/**
	 * Returns the info for this element without disturbing the cache ordering.
	 */
	public OpenableElementInfo peekAtInfo(Openable element) {
		String elementType = element.getElementType();
		if (elementType == RODIN_DATABASE) {
			return this.modelInfo;
		}
		if (elementType == RODIN_PROJECT) {
			return this.projectCache.get(element);
		}
		return this.openableCache.peek(element);
	}

	/**
	 * Remembers the buffer for the given Rodin file.
	 */
	public void putBuffer(RodinFile rodinFile, Buffer buffer) {
		this.bufferCache.put(rodinFile, buffer);
	}

	/**
	 * Remember the info for the element.
	 */
	public void putInfo(Openable element, OpenableElementInfo info) {
		String elementType = element.getElementType();
		if (elementType == RODIN_DATABASE) {
			this.modelInfo = (RodinDBInfo) info;
		} else if (elementType == RODIN_PROJECT) {
			this.projectCache.put(
					(RodinProject) element,
					(RodinProjectElementInfo) info
			);
		} else {
			this.openableCache.put(element, info);
		}
	}

	/**
	 * Removes the info of the element from the cache.
	 */
	public void removeInfo(Openable element) {
		String elementType = element.getElementType();
		if (elementType == RODIN_DATABASE) {
			this.modelInfo = null;
		} else if (elementType == RODIN_PROJECT) {
			this.projectCache.remove(element);
		} else {
			this.openableCache.remove(element);
		}
	}

	/**
	 * Removes the buffer for the given Rodin file from the cache. If
	 * <code>force</code> is <code>true</code>, always remove the buffer,
	 * otherwise remove the buffer only if it has not been modified yet.
	 */
	public void removeBuffer(RodinFile rodinFile, boolean force) {
		if (force) {
			this.bufferCache.remove(rodinFile);
		} else {
			Buffer buffer = this.bufferCache.peek(rodinFile);
			if (buffer != null && ! buffer.hasUnsavedChanges()) {
				this.bufferCache.remove(rodinFile);
			}
		}
	}

	public String toStringFillingRation(String prefix) {
		StringBuilder buffer = new StringBuilder();
		buffer.append(prefix);
		buffer.append("Project cache: "); //$NON-NLS-1$
		buffer.append(this.projectCache.size());
		buffer.append(" projects\n"); //$NON-NLS-1$
		buffer.append(prefix);
		buffer.append("Openable cache["); //$NON-NLS-1$
		buffer.append(this.openableCache.getSpaceLimit());
		buffer.append("]: "); //$NON-NLS-1$
		buffer.append(NumberFormat.getInstance().format(
				this.openableCache.fillingRatio()));
		buffer.append("%\n"); //$NON-NLS-1$
		buffer.append("Buffer cache["); //$NON-NLS-1$
		buffer.append(this.bufferCache.getSpaceLimit());
		buffer.append("]: "); //$NON-NLS-1$
		buffer.append(NumberFormat.getInstance().format(
				this.bufferCache.fillingRatio()));
		buffer.append("%\n"); //$NON-NLS-1$
		return buffer.toString();
	}
}

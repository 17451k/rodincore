/*******************************************************************************
 * Copyright (c) 2008 Systerel and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Systerel - initial API and implementation
 *******************************************************************************/
package org.eventb.ui.eventbeditor.operation.tests.utils;

import org.rodinp.core.IAttributeType;

public final class Attribute<T extends IAttributeType, V> {
	private final T type;
	private final V value;
	private final int hashCode;

	public Attribute(T type, V value) {
		this.type = type;
		this.value = value;
		hashCode = (type.hashCode() * 17 + value.hashCode()) * 17;
	}

	public T getType() {
		return type;
	}

	public V getValue() {
		return value;
	}

	@Override
	public boolean equals(Object obj) {
		if (obj instanceof Attribute) {
			Attribute<?, ?> att = (Attribute<?, ?>) obj;
			return getType().equals(att.getType())
					&& getValue().equals(att.getValue());
		} else {
			return false;
		}
	}

	@Override
	public String toString() {
		return value + "[" + type.getName() + "]";
	}

	@Override
	public int hashCode() {
		return hashCode;
	}
}

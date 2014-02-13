/*******************************************************************************
 * Copyright (c) 2014 Systerel and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Systerel - initial API and implementation
 *******************************************************************************/
package org.rodinp.internal.core.relations;


public class UbiquitousRelation extends ItemRelation {

	public UbiquitousRelation() {
		super(null);
	}

	@Override
	public boolean isValid() {
		return !childTypes.isEmpty() || !attributeTypes.isEmpty();
	}

	@Override
	protected void appendHeader(StringBuilder sb) {
		sb.append("Ubiquitous : ");
	}

}

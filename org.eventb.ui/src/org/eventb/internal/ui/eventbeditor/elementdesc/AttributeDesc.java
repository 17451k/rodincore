/*******************************************************************************
 * Copyright (c) 2009, 2015 Systerel and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Systerel - initial API and implementation
 *******************************************************************************/
package org.eventb.internal.ui.eventbeditor.elementdesc;

import org.eventb.internal.ui.eventbeditor.editpage.IEditComposite;
import org.eventb.ui.itemdescription.IAttributeDesc;
import org.eventb.ui.manipulation.IAttributeManipulation;
import org.rodinp.core.IAttributeType;

public abstract class AttributeDesc extends ItemDesc implements IAttributeDesc {

	public AttributeDesc(IAttributeManipulation factory, String prefix,
			String suffix, boolean isHorizontalExpand, IAttributeType attrType) {
		super(prefix);
		this.factory = factory;
		this.isHorizontalExpand = isHorizontalExpand;
		this.suffix = suffix;
		this.attrType = attrType;
	}

	private final IAttributeManipulation factory;
	private final boolean isHorizontalExpand;
	private final String suffix;
	private final IAttributeType attrType;

	@Override
	public String getSuffix() {
		return suffix;
	}

	public abstract IEditComposite createWidget();

	/**
	 * This indicates that the editing area should expand horizontally.
	 */
	@Override
	public boolean isHorizontalExpand() {
		return isHorizontalExpand;
	}

	@Override
	public IAttributeManipulation getManipulation() {
		return factory;
	}

	@Override
	public IAttributeType getAttributeType() {
		return attrType;
	}

	@Override
	public boolean isChoiceAttribute() {
		return false;
	}

	@Override
	public boolean isToggleAttribute() {
		return false;
	}

	@Override
	public boolean isTextAttribute() {
		return false;
	}

	@Override
	public boolean isMultiLine() {
		return false;
	}

	@Override
	public String toString() {
		return factory.getClass().toString() + ", "
				+ (isHorizontalExpand ? "horizontal" : "not horizontal") + ", "
				+ prefix + ", " + suffix;
	}
	
	@Override
	public boolean isMath() {
		return false;
	}

}

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
package org.eventb.core.indexer;

import org.eventb.core.ast.SourceLocation;
import org.rodinp.core.IAttributeType;
import org.rodinp.core.IInternalElement;
import org.rodinp.core.indexer.IOccurrenceKind;
import org.rodinp.core.indexer.RodinIndexer;
import org.rodinp.core.location.IAttributeSubstringLocation;
import org.rodinp.core.location.IInternalLocation;

public class EventBIndexUtil {

	public static final IOccurrenceKind DECLARATION = RodinIndexer
			.getOccurrenceKind("fr.systerel.eventb.indexer.declaration");

	public static final IOccurrenceKind REFERENCE = RodinIndexer
			.getOccurrenceKind("fr.systerel.eventb.indexer.reference");

	public static final IOccurrenceKind MODIFICATION = RodinIndexer
			.getOccurrenceKind("fr.systerel.eventb.indexer.modification");

	/**
	 * When extracting a location from a SourceLocation, using that method is
	 * mandatory, as long as {@link SourceLocation} and
	 * {@link IAttributeSubstringLocation} do not share the same range
	 * convention.
	 * 
	 * @param element
	 * @param attributeType
	 * @param location
	 * @return the corresponding IInternalLocation
	 */
	public static IInternalLocation getRodinLocation(IInternalElement element,
			IAttributeType.String attributeType, SourceLocation location) {
		return RodinIndexer.getRodinLocation(element, attributeType, location
				.getStart(), location.getEnd() + 1);
	}

}

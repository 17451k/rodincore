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
package org.rodinp.internal.core.index.persistence.xml;

import static org.rodinp.internal.core.index.persistence.xml.XMLElementTypes.*;

import org.rodinp.core.index.IDeclaration;
import org.rodinp.core.index.IOccurrence;
import org.rodinp.internal.core.index.Descriptor;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

/**
 * @author Nicolas Beauger
 *
 */
public class DescPersistor {

	public static void addOccurrences(Element descNode, Descriptor desc) {
		final NodeList occNodes = getElementsByTagName(descNode, OCCURRENCE);
		for (int i=0;i<occNodes.getLength();i++) {
			final Element occNode = (Element) occNodes.item(i);
			final IOccurrence occ = OccPersistor.getOccurrence(occNode);
			desc.addOccurrence(occ);
		}
	}
	
	public static IDeclaration getDeclaration(Element descNode) {
		return DeclPersistor.getDeclaration(descNode);
	}
	
	public static void save(Descriptor desc, Document doc, Element descNode) {
		final IDeclaration declaration = desc.getDeclaration();
		DeclPersistor.save(declaration, doc, descNode);
		
		for (IOccurrence occurrence : desc.getOccurrences()) {
			final Element occNode = createElement(doc, OCCURRENCE);
			OccPersistor.save(occurrence, doc, occNode);
			descNode.appendChild(occNode);
		}
	}

}

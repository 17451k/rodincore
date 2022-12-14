/*******************************************************************************
 * Copyright (c) 2008, 2009 Systerel and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Systerel - initial API and implementation
 *******************************************************************************/
package org.rodinp.internal.core.indexer.persistence.xml;

import static org.rodinp.internal.core.indexer.persistence.xml.XMLAttributeTypes.*;
import static org.rodinp.internal.core.indexer.persistence.xml.XMLElementTypes.*;

import java.util.List;
import java.util.Map;

import org.rodinp.core.IRodinFile;
import org.rodinp.internal.core.indexer.persistence.PersistenceException;
import org.rodinp.internal.core.indexer.sort.Node;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

/**
 * @author Nicolas Beauger
 * 
 */
public class NodePersistor {

	public static Node<IRodinFile> restoreIRFNode(Element nodeNode,
			Map<IRodinFile, List<IRodinFile>> predMap)
			throws PersistenceException {
		final IRodinFile label = IREPersistor.getIRFAtt(nodeNode, LABEL);
		final String markAtt = getAttribute(nodeNode, MARK);
		final boolean mark = Boolean.parseBoolean(markAtt);
		final String orderPosAtt = getAttribute(nodeNode, ORDER_POS);
		final int orderPos = Integer.parseInt(orderPosAtt);

		final Node<IRodinFile> result = new Node<IRodinFile>(label);
		result.setMark(mark);
		result.setOrderPos(orderPos);

		final NodeList preds = getElementsByTagName(nodeNode, PREDECESSOR);
		final List<IRodinFile> predecessors =
				FileNodeListPersistor.restore(preds, LABEL);
		predMap.put(label, predecessors);

		return result;
	}

	public static void save(Node<IRodinFile> node, Document doc,
			Element nodeNode) {
		IREPersistor.setIREAtt(node.getLabel(), LABEL, nodeNode);
		setAttribute(nodeNode, MARK, Boolean.toString(node.isMarked()));
		setAttribute(nodeNode, ORDER_POS, Integer.toString(node.getOrderPos()));

		FileNodeListPersistor.saveFilesInNodes(node.getPredecessors(), doc,
				nodeNode, PREDECESSOR, LABEL);

	}
}

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
package org.rodinp.internal.core.index.tables;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * @author Nicolas Beauger
 * 
 */
public class TotalOrder<T> implements Iterator<T> {

	private final Map<T, Node<T>> graph;
	private final SortedNodes<T> sortedNodes;
	private boolean isSorted;

	/**
	 * 
	 */
	public TotalOrder() {
		this.graph = new HashMap<T, Node<T>>();
		this.sortedNodes = new SortedNodes<T>();
		this.isSorted = false;
	}

	public List<T> getPredecessors(T label) {
		// TODO test and implement
		final Node<T> node = graph.get(label);
		if (node == null) {
			return new ArrayList<T>();
		}
		final List<Node<T>> predecessors = node.getPredecessors();
		final List<T> result = new ArrayList<T>();

		for (Node<T> pred : predecessors) {
			result.add(pred.getLabel());
		}
		return result;
	}

	public void setPredecessors(T label, T[] predecessors) {
		final Node<T> node = fetchNode(label);

		final List<Node<T>> prevPreds = node.getPredecessors();
		final List<Node<T>> newPreds = new ArrayList<Node<T>>();
		for (T pred : predecessors) {
			newPreds.add(fetchNode(pred));
		}

		final List<Node<T>> toRemove = new ArrayList<Node<T>>(prevPreds);
		toRemove.removeAll(newPreds);
		final List<Node<T>> toAdd = new ArrayList<Node<T>>(newPreds);
		toAdd.removeAll(prevPreds);

		if (!(toRemove.isEmpty() && toAdd.isEmpty())) { // there are changes
			processPredChanges(node, toRemove, toAdd);
			isSorted = false;
		}
	}

	private void processPredChanges(final Node<T> node,
			final List<Node<T>> toRemove, final List<Node<T>> toAdd) {

		for (Node<T> addNode : toAdd) {
			node.addPredecessor(addNode);
		}

		for (Node<T> remNode : toRemove) {
			node.removePredecessor(remNode);
		}
	}

	public boolean contains(T label) {
		return graph.containsKey(label);
	}

	public void remove(T label) {
		final Node<T> node = graph.get(label);
		if (node == null) {
			return;
		}
		remove(node);
	}

	public void clear() {
		graph.clear();
		sortedNodes.clear();
		isSorted = false;
	}

	/**
	 * Should be called before each iteration. Can be called to restart an
	 * iteration in progress.
	 */
	public void start() { // TODO consider getting rid of it (otherwise test with iter)
		sortedNodes.start();
	}

	public boolean hasNext() {
		updateSort();

		return sortedNodes.hasNext();
	}

	public T next() {
		updateSort();

		return sortedNodes.next();
	}

	public void remove() {
		updateSort();

		sortedNodes.remove();
		remove(sortedNodes.getCurrentNode());
	}

	private void remove(Node<T> node) {
		node.clear();
		graph.remove(node.getLabel());
		isSorted = false;
	}

	private void updateSort() {
		if (!isSorted) {
			sortedNodes.sort(graph);
			isSorted = true;
			sortedNodes.start();
		}
	}

	public void setToIter(T label) {
		final Node<T> node = fetchNode(label);
		sortedNodes.setToIter(node);
	}

	// successors of the current node will be iterated
	public void setToIterSuccessors() {
		sortedNodes.setToIterSuccessors();
	}

	public void setToIterNone() {
		for (T label : graph.keySet()) {
			final Node<T> node = graph.get(label);
			node.setMark(false);
		}
		sortedNodes.start();
	}

	private Node<T> fetchNode(T label) {
		Node<T> node = graph.get(label);

		if (node == null) {
			node = new Node<T>(label);
			graph.put(label, node);
			isSorted = false;
		}
		return node;
	}

}

/*******************************************************************************
 * Copyright (c) 2006 ETH Zurich.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.eventb.internal.core.tool.graph;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Hashtable;
import java.util.List;



/**
 * @author Stefan Hallerstede
 *
 */
public abstract class Graph<T> {
	
	protected static final int DEFAULT_SIZE = 100;

	protected final String creator;
	
	private final Hashtable<String, Node<T>> graph;
	private final List<Node<T>> nodes;
	private final List<Node<T>> sorted;
	
	public Graph(String creator) {
		graph = new Hashtable<String, Node<T>>(DEFAULT_SIZE * 4 / 3 + 1);
		nodes = new ArrayList<Node<T>>(DEFAULT_SIZE);
		sorted = new ArrayList<Node<T>>(DEFAULT_SIZE);
		this.creator = creator;
	}
	
	// return descriptive name of graph for error messages
	public String getName() {
		return creator;
	}
	
	protected abstract Node<T> createNode(T object);
	
	public Node<T> add(T object) {
		Node<T> node = createNode(object);
		graph.put(node.getId(), node);
		nodes.add(node);
		return node;
	}
	
	public void addAll(Collection<T> objects) {
		for (T object : objects) {
			add(object);
		}
	}

	public Node<T> getNode(String id) {
		return graph.get(id);
	}
	
	private void connect() {
		for (Node<T> node : nodes) {
			node.connect(this);
		}
	}
	
	private void sort() {
		Node<T> next;
		do {
			next = null;
			for (Node<T> node : nodes) {
				if (node.getCount() == 0) {
					next = node;
					break;
				}
			}
			if (next != null) { // cannot be in the for-loop because next is removed from nodes list
				sorted.add(next);
				nodes.remove(next);
				sort(next);
			}
		} while (next != null);
	}
	
	private void sort(Node<T> node) {
		for (Node<T> next : node) {
			next.decCount();
			if (next.getCount() == 0) {
				sorted.add(next);
				nodes.remove(next);
				sort(next);
			}
		}
	}
	
	public void analyse() {
		connect();
		sort();
		if(!isPartialOrder())
			throw new IllegalStateException(
					getName() + 
					" is cyclic. Involved nodes: " + 
					getCycle(), null);
	}
	
	public boolean isPartialOrder() {
		return nodes.size() == 0;
	}
	
	public List<Node<T>> getSorted() {
		return sorted;
	}
	
	public List<Node<T>> getCycle() {
		return nodes;
	}
}

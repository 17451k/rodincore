/*******************************************************************************
 * Copyright (c) 2005, 2021 ETH Zurich and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     ETH Zurich - initial API and implementation
 *     Systerel - separation of file and root element
 *     Systerel - rework traces
 *******************************************************************************/
package org.rodinp.internal.core.builder;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.rodinp.core.IInternalElementType;
import org.rodinp.internal.core.ElementTypeManager;
import org.rodinp.internal.core.FileAssociation;
import org.rodinp.internal.core.util.Messages;

/**
 * @author Stefan Hallerstede
 *
 */
public class Node implements Serializable, Comparable<Node> {
	
	private static final long serialVersionUID = -710145997192071089L;

	public static class File implements Serializable {

		private static final long serialVersionUID = -5374536727511878483L;
		private String name;
		private transient IPath path;
		private transient IFile file;
		
		protected IPath getPath() {
			if (path == null && name != null)
				path = new Path(name);
			return path;
		}

		protected String getName() {
			return name;
		}

		protected IFile getFile() {
			if (file == null) {
				IPath p = getPath();
				if (p != null)
					file = ResourcesPlugin.getWorkspace().getRoot().getFile(p);
			}
			return file;
		}

		protected void setPath(IPath path) {
			this.path = path;
			name = path.toString();
		}

		@Override
		public int hashCode() {
			final int prime = 31;
			int result = 1;
			result = prime * result + ((name == null) ? 0 : name.hashCode());
			return result;
		}

		@Override
		public boolean equals(Object obj) {
			if (this == obj) {
				return true;
			}
			if (obj == null) {
				return false;
			}
			if (getClass() != obj.getClass()) {
				return false;
			}
			File other = (File) obj;
			if (name == null) {
				if (other.name != null) {
					return false;
				}
			} else if (!name.equals(other.name)) {
				return false;
			}
			return true;
		}
				
	}

	private File target; // name of the resource (full name in workspace!)
	private File creator; // name of the resource whose extractor created this node
	private LinkedList<Link> predecessorLinks; // the predecessor list
	private String toolId; // toolId to be run to produce the resource of this node
	private boolean dated; // true if the resource of this node needs to be (re-)created
	private boolean phantom; // a node that was created by a dependency requirement
	private boolean cycle; // node is on a cycle
	private boolean preferred; // node should be treated as early as possible
	
	private ArrayList<Node> successorNodes; // successors of this node (for topological sort)
	private ArrayList<Link> successorLinks; // successors of this node (for topological sort)
	
	// temporary data for construction of topological order
	private transient int successorPos; // Position in successor lists during graph traversal	
	protected transient int count; // number of predecessors of this node remaining in the unprocessed top sort
	protected transient boolean done; // nodes with count zero and done are already in the ordered list
	
//	transient private IPath targetPath; // the path corresponding to target name (cache)
//	transient private IPath sourcePath; // the path corresponding to source name (cache)
	private transient IInternalElementType<?> rootElementType; // the element type of the resource (cache)
//	transient private IFile file; // the file corresponding to name (cache)
	
	public Node() {
		creator = new File();
		target = new File();
		toolId = null;
		dated = true;
		done = false;
		predecessorLinks = new LinkedList<Link>();
		successorNodes = new ArrayList<Node>(3);
		successorLinks = new ArrayList<Link>(3);
	}
	
	@Override
	public String toString() {
		return printNode();
	}
	
	@Override
	public int compareTo(Node o) {
		return target.getName().compareTo(o.target.getName());
	}

	protected List<Link> getPredecessorLinks() {
		return predecessorLinks;
	}
	
	protected void addPredecessorLink(Link link) { 
		if(predecessorLinks.contains(link))
			return;
		predecessorLinks.add(link);
		if(link.source.successorPos <= link.source.getSuccessorCount())
			count++;
		
		if(link.prio == Link.Priority.LOW) {
			link.source.successorNodes.add(this);
			link.source.successorLinks.add(link);
		} else {
			link.source.successorNodes.add(0, this);
			link.source.successorLinks.add(0, link);
		}
	}

	protected void addPredecessorLink(Node origin, Node source, String id, Link.Provider prov, Link.Priority prio) { 
		Link link = new Link(prov, prio, id, source, origin);
		addPredecessorLink(link);
	}
	
	protected void removeAllLinks(String id) {
		LinkedList<Link> predCopy = new LinkedList<Link>(predecessorLinks);
		for(Link link : predCopy) {
			if(link.id.equals(id)) {
				predecessorLinks.remove(link);
				count--;
				
				link.source.successorNodes.remove(this);
				link.source.successorLinks.remove(link);
			}
		}
	}
	
	protected Collection<IPath> getSources(String id) {
		ArrayList<IPath> sources = new  ArrayList<IPath>(predecessorLinks.size());
		for(Link link : predecessorLinks) {
			if(link.id.equals(id))
				sources.add(link.source.getTarget().getPath());
		}
		return sources;
	}
	
	protected File getCreator() {
		return creator;
	}
	
	protected File getTarget() {
		return target;
	}
	
	protected int getPredecessorCount() {
		return predecessorLinks.size();
	}
	
	protected boolean isDerived() {
		return toolId != null;
	}

	protected void setToolId(String toolId) {
		assert toolId == null || ! toolId.equals("");
		this.toolId = toolId;
	}
	
	protected String getToolId() {
		return toolId;
	}
	
	protected void markSuccessorsDated(boolean revivePhantoms) {
		for(Node suc : successorNodes) {
			suc.setDated(true);
			if (revivePhantoms)
				suc.setPhantom(false);
		}
	}
	
	protected HashSet<Node> getSuccessorNodes(final String id) {
		HashSet<Node> nodes = new HashSet<Node>(successorNodes.size() * 4 / 3 + 1);
		for (int i=0; i< successorLinks.size(); i++) {
			if (successorLinks.get(i).id.equals(id))
				nodes.add(successorNodes.get(i));
		}
		return nodes;
	}

	protected boolean hasSuccessorNode(Node node) {
		return successorNodes.contains(node);
	}
	
	protected void advanceSuccessorPos() {
		successorPos++;
	}
	
	protected int getSuccessorPos() {
		return successorPos;
	}

	protected Node getCurrentSuccessorNode() {
		return (successorPos < successorNodes.size()) ? successorNodes.get(successorPos) : null;
	}

	protected Link getCurrentSuccessorLink() {
		return (successorPos < successorLinks.size()) ? successorLinks.get(successorPos) : null;
	}

	protected int getSuccessorCount() {
		return successorNodes.size();
	}

	protected void removeSuccessorToolCount() {
		for(int pos = 0; pos < successorNodes.size(); pos++)
			if(successorLinks.get(pos).prov == Link.Provider.TOOL) {
				successorNodes.get(pos).count--;
			}
	}

	protected void setDated(boolean value) {
		dated = value;
	}
	
	protected boolean isDated() {
		return dated;
	}
	
	protected void initForSort() {
		count = getPredecessorCount();
		done = false;
		successorPos = 0;
	}
	
	protected String printNode() {
		String res = target.getName()  + "[";
		res += isDated() ? "D" : "N";
		res += isPhantom() ? "-P" : "-N";
		res += "] :";
		for(Node node : successorNodes) {
			res = res + " " + node.target.getName();
		}
		return res;
	}
	
	protected void unlinkNode() {
		for(Link link : predecessorLinks) {
			
			link.source.successorNodes.remove(this);
			link.source.successorLinks.remove(link);
		}
		int size = successorNodes.size();
		for(int pos = 0; pos < size; pos++) {
			Node node = successorNodes.get(pos);
			node.dated = true;
			node.predecessorLinks.remove(successorLinks.get(pos));
			node.count--;
		}
	}
	
	protected void markReachableToolSuccessorsUndone() {
		if(!done)
			return;
		done = false;
		for(int pos = 0; pos < successorNodes.size(); pos++)
			if(successorLinks.get(pos).prov == Link.Provider.TOOL) {
				successorNodes.get(pos).markReachableToolSuccessorsUndone();
			}
	}
	
	protected void addOriginToCycle() {
		for(Link link : predecessorLinks) {
			if(link.source.count > 0) {
				IFile originFile = link.origin.target.getFile();
				link.origin.dated = true;
				if(originFile != null)
					MarkerHelper.addMarker(
							originFile,
							true,
							Messages.build_resourceInCycle
					);
				else if(RodinBuilder.DEBUG_GRAPH)
					System.out.println(getClass().getName() + ": File not found: " + link.origin.target.getName()); //$NON-NLS-1$
			}
		}
	}
	
	protected boolean dependsOnPhantom() {
		for(Link link : predecessorLinks) {
			if(link.source.isPhantom())
				return true;
		}
		return false;
	}
	
	protected void printPhantomProblem() {
		for(Link link : predecessorLinks) {
			if(link.source.isPhantom())
				if(link.prov == Link.Provider.USER && link.origin != null) {
					IFile originFile = link.origin.target.getFile();
					if(originFile != null)
						MarkerHelper.addMarker(
								originFile, 
								false,
								Messages.build_resourceDoesNotExist,
								link.source.target.getName()
						);
				}
		}
	}
	
	/**
	 * @return Returns the phantom.
	 */
	protected boolean isPhantom() {
		return phantom;
	}

	/**
	 * @param phantom The phantom to set.
	 */
	protected void setPhantom(boolean phantom) {
		this.phantom = phantom;
	}

	/**
	 * @return Returns the cycle.
	 */
	protected boolean isCycle() {
		return cycle;
	}

	/**
	 * @param cycle The cycle to set.
	 */
	protected void setCycle(boolean cycle) {
		this.cycle = cycle;
	}

	/**
	 * @return Returns the rootElementType.
	 */
	public IInternalElementType<?> getRootElementType() {
		
		final IFile targetFile = target.getFile();
		if (rootElementType == null && targetFile != null) {
			final ElementTypeManager manager = ElementTypeManager.getInstance();

			final FileAssociation fileAssociation = manager.getFileAssociation(targetFile);
			this.rootElementType = fileAssociation.getRootElementType();
		}

		return rootElementType;
	}

	public boolean isPreferred() {
		return preferred;
	}

	public void setPreferred(boolean preferred) {
		this.preferred = preferred;
	}
	
	public void markReachablePredecessorsPreferred() {
		if(preferred)
			return;
		preferred = true;
		for(Link link : predecessorLinks)
			link.source.markReachablePredecessorsPreferred();

	}

}

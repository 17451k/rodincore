/**
 * 
 */
package org.rodinp.internal.core.builder;

import java.util.ArrayList;
import java.util.HashSet;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.rodinp.core.RodinCore;
import org.rodinp.core.builder.IGraph;
import org.rodinp.internal.core.util.Messages;

/**
 * @author Stefan Hallerstede
 *
 */
public class GraphTransaction implements IGraph {

	private boolean opened;
	private boolean closed;
	
	private final ArrayList<Link> links;
	private final ArrayList<Node> targets;
	private final GraphModifier handler;
	
	private final HashSet<Node> targetSet; // all target nodes
	private final String toolId;
	
	public GraphTransaction(GraphModifier handler, String toolId) {
		
		opened = false;
		closed = false;
		
		this.handler = handler;
		this.toolId = toolId;
		
		links = new ArrayList<Link>(7);
		targets = new ArrayList<Node>(7);
		targetSet = new HashSet<Node>(7);
	}
	
	/* (non-Javadoc)
	 * @see org.rodinp.core.builder.IGraph#putUserDependency(IFile, IFile, IFile, String, boolean)
	 */
	public void addUserDependency(
			IFile origin, 
			IFile source, 
			IFile target,
			boolean prioritize) throws CoreException {
		if (!opened)
			throw makeGraphTransactionError();
		
		IPath originPath =  origin.getFullPath();
		IPath sourcePath =  source.getFullPath();
		IPath targetPath =  target.getFullPath();
		
		links.add(new Link(Link.Provider.USER, 
						prioritize ? Link.Priority.HIGH : Link.Priority.LOW, 
						toolId, 
						handler.getNodeOrPhantom(sourcePath), 
						handler.getNodeOrPhantom(originPath)));
		Node node = handler.getNodeOrPhantom(targetPath);
		targets.add(node);
		targetSet.add(node);
	}

	/* (non-Javadoc)
	 * @see org.rodinp.core.builder.IGraph#putToolDependency(IFile, IFile, String, boolean)
	 */
	public void addToolDependency(
			IFile source, 
			IFile target, 
			boolean prioritize) throws CoreException {
		if (!opened)
			throw makeGraphTransactionError();
		
		IPath sourcePath =  source.getFullPath();
		IPath targetPath =  target.getFullPath();
		
		links.add(new Link(Link.Provider.TOOL, 
				prioritize ? Link.Priority.HIGH : Link.Priority.LOW, 
				toolId, 
				handler.getNodeOrPhantom(sourcePath), 
				null));
		Node node = handler.getNodeOrPhantom(targetPath);
		targets.add(node);
		targetSet.add(node);
	}

	/* (non-Javadoc)
	 * @see org.rodinp.core.builder.IGraph#addNode(org.eclipse.core.resources.IFile, java.lang.String)
	 */
	public void addNode(IFile file) throws CoreException {
		if (!opened)
			throw makeGraphTransactionError();
		
		handler.addNode(file.getFullPath(), toolId);
	}

	/* (non-Javadoc)
	 * @see org.rodinp.core.builder.IGraph#closeGraph()
	 */
	public void closeGraph() throws CoreException {
		if (opened && !closed) {
			opened = false;
			closed = true;
		} else
			throw makeGraphTransactionError();
		
		boolean[] found = new boolean[targets.size()];
		for(int i=0; i<found.length; i++) found[i] = false;
		
		// in the first phase all ids of links that must be removed are computed
		HashSet<String> changedIds = new HashSet<String>(5);
		for (Node node : targetSet) {
			for (Link link : node.getPredessorLinks()) {
				if(toolId.equals(link.id)) {
					int p = links.indexOf(link);
					if (p == -1 || targets.get(p) != node) {
						changedIds.add(link.id);
					} else
						found[p] = true;
				}
			}
		}
		
		// next all links with these ids are removed
		handler.removeDependencies(changedIds);
		
		// add all links
		for (int i=0; i<found.length; i++) {
			Link link = links.get(i);
			if (!found[i] || changedIds.contains(link.id)) {
				handler.addDependency(link, targets.get(i));
//				targets.get(i).addLink(links.get(i));
			}
		}
	}

	/* (non-Javadoc)
	 * @see org.rodinp.core.builder.IGraph#openGraph()
	 */
	public void openGraph() throws CoreException {
		if (!opened && !closed) 
			opened = true;
		else
			throw makeGraphTransactionError();
	}
	
	private CoreException makeGraphTransactionError() {
		return new CoreException(
				new Status(
						IStatus.ERROR, 
						RodinCore.PLUGIN_ID, 
						IStatus.OK, 
						Messages.build_graphTransactionError, 
						null));
	}

}

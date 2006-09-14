package org.rodinp.core.tests.builder;


import java.util.HashSet;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.rodinp.core.RodinCore;
import org.rodinp.core.builder.IAutomaticTool;
import org.rodinp.core.builder.IExtractor;
import org.rodinp.core.builder.IGraph;

public class CSCTool extends SCTool implements IExtractor, IAutomaticTool {
	
	private static final String CSC = "CSC";
	// Id of this tool
	private static String SC_ID = "org.rodinp.core.tests.testCSC";
	
	public void clean(IFile file, IProgressMonitor monitor) throws CoreException {
		if (SCTool.SHOW_CLEAN)
			ToolTrace.addTrace(CSC, "clean", file);

		if (file.getFileExtension().equals("csc"))
			file.delete(true, monitor);
	}
	
	public void extract(IFile file, IGraph graph, IProgressMonitor monitor) throws CoreException {
		if (!RUN_SC)
			return;
		if (SCTool.SHOW_EXTRACT)
			ToolTrace.addTrace(CSC, "extract", file);
		
		IContext ctx = (IContext) RodinCore.create(file);
		
		ISCContext sctx = ctx.getCheckedVersion();
		IPath scPath = sctx.getResource().getFullPath();
		graph.addNode(scPath, SC_ID);
		graph.putToolDependency(ctx.getResource().getFullPath(), scPath, SC_ID, true);
		
		HashSet<IPath> newSources = new HashSet<IPath>(ctx.getUsedContexts().length * 4 / 3 + 1);
		for (IContext usedContext: ctx.getUsedContexts()) {
			IPath source = usedContext.getCheckedVersion().getResource().getFullPath();
			newSources.add(source);
		}
		for (IPath path : newSources)
			graph.putUserDependency(ctx.getResource().getFullPath(), path, scPath, SC_ID, false);
		
		graph.updateGraph();
	}
	
	public boolean run(IFile file, IProgressMonitor monitor) throws CoreException {
		if (SCTool.SHOW_RUN)
			ToolTrace.addTrace(CSC, "run", file);

		ISCContext target = (ISCContext) RodinCore.create(file);
		IContext ctx = target.getUncheckedVersion(); 
		
		// First clean up target
		if (target.exists()) {
			target.delete(true, null);
		}
		target = (ISCContext) target.getRodinProject().createRodinFile(target.getElementName(), true, null);
		
		// Populate with a copy of inputs
		copyDataElements(ctx, target);
		for (IContext usedContext: ctx.getUsedContexts()) {
			copyDataElements(usedContext.getCheckedVersion(), target);
		}
		
		target.save(null, true);
		return true;
	}
	
}

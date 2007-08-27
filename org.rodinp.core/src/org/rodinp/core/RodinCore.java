/*******************************************************************************
 * Copyright (c) 2005 ETH Zurich.
 * Strongly inspired by org.eclipse.jdt.core.JavaCore.java which is
 * 
 * Copyright (c) 2000, 2005 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.rodinp.core;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IResourceStatus;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.IWorkspaceRunnable;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.Plugin;
import org.eclipse.core.runtime.jobs.ISchedulingRule;
import org.osgi.framework.BundleContext;
import org.rodinp.internal.core.BatchOperation;
import org.rodinp.internal.core.ElementTypeManager;
import org.rodinp.internal.core.Region;
import org.rodinp.internal.core.RodinDB;
import org.rodinp.internal.core.RodinDBManager;
import org.rodinp.internal.core.util.MementoTokenizer;
import org.rodinp.internal.core.util.WeakHashSet;
import org.rodinp.internal.core.version.Result;

/**
 * The plug-in runtime class for the Rodin core plug-in containing the core
 * (UI-free) support for Rodin projects.
 * <p>
 * Like all plug-in runtime classes (subclasses of <code>Plugin</code>), this
 * class is automatically instantiated by the platform when the plug-in gets
 * activated. Clients must not attempt to instantiate plug-in runtime classes
 * directly.
 * </p>
 * <p>
 * The single instance of this class can be accessed from any plug-in declaring
 * the Rodin core plug-in as a prerequisite via
 * <code>RodinCore.getRodinCore()</code>. The Rodin core plug-in will be
 * activated automatically if not already active.
 * </p>
 */

public class RodinCore extends Plugin {

	// The shared instance.
	private static RodinCore PLUGIN;

	/**
	 * The plug-in identifier of the Rodin core support (value
	 * <code>"org.rodinp.core"</code>).
	 */
	public static final String PLUGIN_ID = "org.rodinp.core"; //$NON-NLS-1$

	/**
	 * The identifier for the Rodin builder
	 * (value <code>"org.rodinp.core.rodinbuilder"</code>).
	 */
	public static final String BUILDER_ID = PLUGIN_ID + ".rodinbuilder" ; //$NON-NLS-1$

	/**
	 * The identifier for the Rodin nature
	 * (value <code>"org.rodinp.core.rodinnature"</code>).
	 * The presence of this nature on a project indicates that it is 
	 * Rodin-capable.
	 *
	 * @see org.eclipse.core.resources.IProject#hasNature(java.lang.String)
	 */
	public static final String NATURE_ID = PLUGIN_ID + ".rodinnature" ; //$NON-NLS-1$

	/*
	 * Pools of symbols used in the Rodin database. Used as a replacement for
	 * String#intern() that could prevent garbage collection of strings on some
	 * VMs.
	 */
	// TODO use java.util.WeakHashMap here, instead of WeakHashSet
	private WeakHashSet<String> stringSymbols = new WeakHashSet<String>(5);

	/**
	 * Creates the Rodin core plug-in.
	 * <p>
	 * The plug-in instance is created automatically by the Eclipse platform.
	 * Clients must not call.
	 * </p>
	 */
	public RodinCore() {
		super();
		PLUGIN = this;
	}
	
	/**
	 * Adds the given listener for changes to Rodin elements.
	 * Has no effect if an identical listener is already registered.
	 *
	 * This listener will only be notified during the POST_CHANGE resource change notification
	 * and any reconcile operation (POST_RECONCILE).
	 * For finer control of the notification, use <code>addElementChangedListener(IElementChangedListener,int)</code>,
	 * which allows to specify a different eventMask.
	 * 
	 * @param listener the listener
	 * @see ElementChangedEvent
	 */
	public static void addElementChangedListener(IElementChangedListener listener) {
		addElementChangedListener(listener, ElementChangedEvent.POST_CHANGE);
	}

	/**
	 * Adds the given listener for changes to Rodin elements.
	 * Has no effect if an identical listener is already registered.
	 * After completion of this method, the given listener will be registered for exactly
	 * the specified events.  If they were previously registered for other events, they
	 * will be deregistered.  
	 * <p>
	 * Once registered, a listener starts receiving notification of changes to
	 * Rodin elements in the model. The listener continues to receive 
	 * notifications until it is replaced or removed. 
	 * </p>
	 * <p>
	 * Listeners can listen for several types of event as defined in <code>ElementChangeEvent</code>.
	 * Clients are free to register for any number of event types however if they register
	 * for more than one, it is their responsibility to ensure they correctly handle the
	 * case where the same java element change shows up in multiple notifications.  
	 * Clients are guaranteed to receive only the events for which they are registered.
	 * </p>
	 * 
	 * @param listener the listener
	 * @param eventMask the bit-wise OR of all event types of interest to the listener
	 * @see IElementChangedListener
	 * @see ElementChangedEvent
	 * @see #removeElementChangedListener(IElementChangedListener)
	 * @since 2.0
	 */
	public static void addElementChangedListener(IElementChangedListener listener, int eventMask) {
		RodinDBManager.getRodinDBManager().deltaState.addElementChangedListener(listener, eventMask);
	}

	/**
	 * Returns the Rodin element corresponding to the given handle identifier
	 * generated by <code>IRodinElement.getHandleIdentifier()</code>, or
	 * <code>null</code> if unable to create the associated element.
	 * 
	 * @param handleIdentifier
	 *            the given handle identifier
	 * @return the Rodin element corresponding to the handle identifier
	 * @deprecated This method has been replaced by {@link #valueOf(String)}
	 *             whose name is less misleading (nothing was actually created
	 *             here).
	 */
	@Deprecated
	public static IRodinElement create(String handleIdentifier) {
		return valueOf(handleIdentifier);
	}

	/**
	 * Returns the Rodin element corresponding to the given file, or
	 * <code>null</code> if unable to associate the given file with a Rodin
	 * element.
	 * 
	 * <p>
	 * Calling this method has the side effect of creating and opening all
	 * of the element's parents if they are not yet open.
	 * </p>
	 * 
	 * @param file
	 *            the given file
	 * @return the Rodin element corresponding to the given file, or
	 *         <code>null</code> if unable to associate the given file with a
	 *         Rodin file element
	 * @deprecated This method has been replaced by {@link #valueOf(IFile)}
	 *             whose name is less misleading (nothing was actually created
	 *             here).
	 */
	@Deprecated
	public static IRodinFile create(IFile file) {
		return valueOf(file);
	}

	/**
	 * Returns the Rodin project corresponding to the given project.
	 * <p>
	 * Calling this method has the side effect of creating and opening all of
	 * the project's parents if they are not yet open.
	 * </p>
	 * 
	 * @param project
	 *            the given project
	 * @return the Rodin project corresponding to the given project, or
	 *         <code>null</code> if the given project is <code>null</code>
	 * @deprecated This method has been replaced by {@link #valueOf(IProject)}
	 *             whose name is less misleading (nothing was actually created
	 *             here).
	 */
	@Deprecated
	public static IRodinProject create(IProject project) {
		if (project == null) {
			return null;
		}
		RodinDB javaModel = RodinDBManager.getRodinDBManager().getRodinDB();
		return javaModel.getRodinProject(project);
	}

	/**
	 * Returns the Rodin element corresponding to the given resource, or
	 * <code>null</code> if unable to associate the given resource with a
	 * Rodin element.
	 * <p>
	 * The resource must be one of:
	 * <ul>
	 * <li>a project - the element returned is the corresponding
	 * <code>IRodinProject</code></li>
	 * <li>a Rodin file - the element returned is the corresponding
	 * <code>RodinFile</code></li>
	 * <li>the workspace root resource - the element returned is the
	 * <code>IRodinDB</code></li>
	 * </ul>
	 * </p>
	 * <p>
	 * Calling this method has the side effect of creating and opening all of
	 * the element's parents if they are not yet open.
	 * </p>
	 * 
	 * @param resource
	 *            the given resource
	 * @return the Rodin element corresponding to the given resource, or
	 *         <code>null</code> if unable to associate the given resource
	 *         with a Rodin element
	 * @deprecated This method has been replaced by {@link #valueOf(IResource)}
	 *             whose name is less misleading (nothing was actually created
	 *             here).
	 */
	@Deprecated
	public static IRodinElement create(IResource resource) {
		return RodinDBManager.valueOf(resource, null/* unknown Rodin project */);
	}

	/**
	 * Returns the Rodin database.
	 * 
	 * @param root
	 *            the given root
	 * @return the Rodin database, or <code>null</code> if the root is
	 *         <code>null</code>
	 * @deprecated This method has been replaced by
	 *             {@link #valueOf(IWorkspaceRoot)} whose name is less
	 *             misleading (nothing was actually created here).
	 */
	@Deprecated
	public static IRodinDB create(IWorkspaceRoot root) {
		if (root == null) {
			return null;
		}
		return RodinDBManager.getRodinDBManager().getRodinDB();
	}

	/**
	 * Returns the internal element type with the given id.
	 * 
	 * @param id
	 *            unique identifier of the element type
	 * @return the internal element type with the given id
	 * @throws IllegalArgumentException
	 *             if no such internal element type has been contributed
	 */
	@SuppressWarnings("unchecked")
	public static <T extends IInternalElement> IInternalElementType<T> getInternalElementType(
			String id) {
		final ElementTypeManager manager = ElementTypeManager.getInstance();
		final IInternalElementType result = manager.getInternalElementType(id);
		if (result != null) {
			return result;
		}
		throw new IllegalArgumentException("Unknown internal element type: " + id);
	}

	/**
	 * Returns the attribute type with the given id.
	 * 
	 * @param id
	 *            unique identifier of the attribute type
	 * @return the attribute type with the given id
	 * @throws IllegalArgumentException
	 *             if no such attribute type has been contributed
	 */
	public static IAttributeType getAttributeType(String id) {
		final ElementTypeManager manager = ElementTypeManager.getInstance();
		IAttributeType result = manager.getAttributeType(id);
		if (result != null) {
			return result;
		}
		throw new IllegalArgumentException("Unknown attribute type: " + id);
	}
	
	/**
	 * Returns the attribute type of kind boolean and with the given id.
	 * 
	 * @param id
	 *            unique identifier of the attribute type
	 * @return the attribute type with the given id and kind boolean
	 * @throws IllegalArgumentException
	 *             if no such attribute type has been contributed
	 */
	public static IAttributeType.Boolean getBooleanAttrType(String id) {
		IAttributeType type = getAttributeType(id);
		if (type instanceof IAttributeType.Boolean) {
			return (IAttributeType.Boolean) type;
		}
		throw new IllegalArgumentException(
				"Attribute type " + type.getId() + " is not of kind boolean");
	}
	
	/**
	 * Returns the attribute type of kind handle and with the given id.
	 * 
	 * @param id
	 *            unique identifier of the attribute type
	 * @return the attribute type with the given id and kind handle
	 * @throws IllegalArgumentException
	 *             if no such attribute type has been contributed
	 */
	public static IAttributeType.Handle getHandleAttrType(String id) {
		IAttributeType type = getAttributeType(id);
		if (type instanceof IAttributeType.Handle) {
			return (IAttributeType.Handle) type;
		}
		throw new IllegalArgumentException(
				"Attribute type " + type.getId() + " is not of kind handle");
	}
	
	/**
	 * Returns the attribute type of kind integer and with the given id.
	 * 
	 * @param id
	 *            unique identifier of the attribute type
	 * @return the attribute type with the given id and kind integer
	 * @throws IllegalArgumentException
	 *             if no such attribute type has been contributed
	 */
	public static IAttributeType.Integer getIntegerAttrType(String id) {
		IAttributeType type = getAttributeType(id);
		if (type instanceof IAttributeType.Integer) {
			return (IAttributeType.Integer) type;
		}
		throw new IllegalArgumentException(
				"Attribute type " + type.getId() + " is not of kind integer");
	}
	
	/**
	 * Returns the attribute type of kind long and with the given id.
	 * 
	 * @param id
	 *            unique identifier of the attribute type
	 * @return the attribute type with the given id and kind long
	 * @throws IllegalArgumentException
	 *             if no such attribute type has been contributed
	 */
	public static IAttributeType.Long getLongAttrType(String id) {
		IAttributeType type = getAttributeType(id);
		if (type instanceof IAttributeType.Long) {
			return (IAttributeType.Long) type;
		}
		throw new IllegalArgumentException(
				"Attribute type " + type.getId() + " is not of kind long");
	}
	
	/**
	 * Returns the attribute type of kind string and with the given id.
	 * 
	 * @param id
	 *            unique identifier of the attribute type
	 * @return the attribute type with the given id and kind string
	 * @throws IllegalArgumentException
	 *             if no such attribute type has been contributed
	 */
	public static IAttributeType.String getStringAttrType(String id) {
		IAttributeType type = getAttributeType(id);
		if (type instanceof IAttributeType.String) {
			return (IAttributeType.String) type;
		}
		throw new IllegalArgumentException(
				"Attribute type " + type.getId() + " is not of kind string");
	}
	
	/**
	 * Returns the element type with the given id.
	 * 
	 * @param id
	 *            unique identifier of the element type
	 * @return the element type with the given id
	 * @throws IllegalArgumentException
	 *             if no such element type has been contributed
	 */
	public static IElementType<? extends IRodinElement> getElementType(String id) {
		final ElementTypeManager manager = ElementTypeManager.getInstance();
		final IElementType<? extends IRodinElement> result = manager.getElementType(id);
		if (result != null) {
			return result;
		}
		throw new IllegalArgumentException("Unknown element type: " + id);
	}

	/**
	 * Returns the file element type with the given id.
	 * 
	 * @param id
	 *            unique identifier of the element type
	 * @return the file element type with the given id
	 * @throws IllegalArgumentException
	 *             if no such file element type has been contributed
	 */
	@SuppressWarnings("unchecked")
	public static <T extends IRodinFile> IFileElementType<T> getFileElementType(String id) {
		final ElementTypeManager manager = ElementTypeManager.getInstance();
		final IFileElementType result = manager.getFileElementType(id);
		if (result != null) {
			return result;
		}
		throw new IllegalArgumentException("Unknown file element type: " + id);
	}

	/**
	 * Returns the single instance of the Rodin core plug-in runtime class.
	 * Equivalent to <code>(RodinCore) getPlugin()</code>.
	 * 
	 * @return the single instance of the Rodin core plug-in runtime class
	 */
	public static RodinCore getRodinCore() {
		return PLUGIN;
	}

	/**
	 * Returns the single instance of the Rodin core plug-in runtime class.
	 * 
	 * @return the single instance of the Rodin core plug-in runtime class
	 */
	public static Plugin getPlugin() {
		return PLUGIN;
	}

	@Deprecated
	public synchronized String intern(String s) {
		// make sure to copy the string (so that it doesn't hold on the
		// underlying char[] that might be much bigger than necessary)
		return this.stringSymbols.add(new String(s));

		// Note: String#intern() cannot be used as on some VMs this prevents the
		// string from being garbage collected
	}

	/**
	 * Returns a new empty region.
	 * 
	 * @return a new empty region
	 */
	public static IRegion newRegion() {
		return new Region();
	}

	/**
	 * Removes the given element changed listener.
	 * Has no affect if an identical listener is not registered.
	 *
	 * @param listener the listener
	 */
	public static void removeElementChangedListener(IElementChangedListener listener) {
		RodinDBManager.getRodinDBManager().deltaState.removeElementChangedListener(listener);
	}

	/**
	 * Runs the given action as an atomic Rodin database operation.
	 * <p>
	 * After running a method that modifies Rodin elements,
	 * registered listeners receive after-the-fact notification of
	 * what just transpired, in the form of an element changed event.
	 * This method allows clients to call a number of
	 * methods that modify Rodin elements and only have element
	 * changed event notifications reported at the end of the entire
	 * batch.
	 * </p>
	 * <p>
	 * If this method is called outside the dynamic scope of another such
	 * call, this method runs the action and then reports a single
	 * element changed event describing the net effect of all changes
	 * done to Rodin elements by the action.
	 * </p>
	 * <p>
	 * If this method is called in the dynamic scope of another such
	 * call, this method simply runs the action.
	 * </p>
	 *
	 * @param action the action to perform
	 * @param monitor a progress monitor, or <code>null</code> if progress
	 *    reporting and cancellation are not desired
	 * @exception RodinDBException if the operation failed.
	 */
	public static void run(IWorkspaceRunnable action, IProgressMonitor monitor)
			throws RodinDBException {
		run(action, ResourcesPlugin.getWorkspace().getRoot(), monitor);
	}

	/**
	 * Runs the given action as an atomic Rodin database operation.
	 * <p>
	 * After running a method that modifies Rodin elements,
	 * registered listeners receive after-the-fact notification of
	 * what just transpired, in the form of an element changed event.
	 * This method allows clients to call a number of
	 * methods that modify Rodin elements and only have element
	 * changed event notifications reported at the end of the entire
	 * batch.
	 * </p>
	 * <p>
	 * If this method is called outside the dynamic scope of another such
	 * call, this method runs the action and then reports a single
	 * element changed event describing the net effect of all changes
	 * done to Rodin elements by the action.
	 * </p>
	 * <p>
	 * If this method is called in the dynamic scope of another such
	 * call, this method simply runs the action.
	 * </p>
	 * <p>
 	 * The supplied scheduling rule is used to determine whether this operation can be
	 * run simultaneously with workspace changes in other threads. See 
	 * <code>IWorkspace.run(...)</code> for more details.
 	 * </p>
	 *
	 * @param action the action to perform
	 * @param rule the scheduling rule to use when running this operation, or
	 * <code>null</code> if there are no scheduling restrictions for this operation.
	 * @param monitor a progress monitor, or <code>null</code> if progress
	 *    reporting and cancellation are not desired
	 * @exception RodinDBException if the operation failed.
	 */
	// TODO throw a Rodin DB Exception instead (possibly encapsulating the core exception)
	public static void run(IWorkspaceRunnable action, ISchedulingRule rule,
			IProgressMonitor monitor) throws RodinDBException {
		final IWorkspace workspace = ResourcesPlugin.getWorkspace();
		if (workspace.isTreeLocked()) {
			new BatchOperation(action).run(monitor);
		} else {
			// use IWorkspace.run(...) to ensure that a build will be done in
			// autobuild mode
			try {
				workspace.run(new BatchOperation(action), rule,
						IWorkspace.AVOID_UPDATE, monitor);
			} catch (RodinDBException re) {
				throw re;
			} catch (CoreException ce) {
				if (ce.getStatus().getCode() == IResourceStatus.OPERATION_FAILED) {
					Throwable e = ce.getStatus().getException();
					if (e instanceof RodinDBException) {
						throw (RodinDBException) e;
					}
				}
				throw new RodinDBException(ce);
			}
		}
	}	

	@Override
	public void start(BundleContext context) throws Exception {
		super.start(context);
		RodinDBManager.getRodinDBManager().startup();
	}

	@Override
	public void stop(BundleContext context) throws Exception {
		try {
			RodinDBManager.getRodinDBManager().shutdown();
		} finally {
			super.stop(context);
			PLUGIN = null;
		}
	}

	/**
	 * Returns the Rodin element corresponding to the given handle identifier
	 * generated by <code>IRodinElement.getHandleIdentifier()</code>, or
	 * <code>null</code> if unable to create the associated element.
	 * 
	 * @param handleIdentifier
	 *            the given handle identifier
	 * @return the Rodin element corresponding to the handle identifier, or
	 *         <code>null</code> if the identifier can't be parsed
	 */
	public static IRodinElement valueOf(String handleIdentifier) {
		if (handleIdentifier == null) {
			return null;
		}
		MementoTokenizer memento = new MementoTokenizer(handleIdentifier);
		RodinDB model = RodinDBManager.getRodinDBManager().getRodinDB();
		return model.getHandleFromMemento(memento);
	}

	/**
	 * Returns the Rodin file element corresponding to the given file, or
	 * <code>null</code> if unable to associate the given file with a Rodin
	 * element.
	 * 
	 * <p>
	 * Creating a Rodin element has the side effect of creating and opening all
	 * of the element's parents if they are not yet open.
	 * </p>
	 * 
	 * @param file
	 *            the given file
	 * @return the Rodin file element corresponding to the given file, or
	 *         <code>null</code> if unable to associate the given file with a
	 *         Rodin file element
	 */
	public static IRodinFile valueOf(IFile file) {
		return (IRodinFile) RodinDBManager.valueOf(file, null/* unknown Rodin project */);
	}

	/**
	 * Returns the Rodin project corresponding to the given project.
	 * <p>
	 * Calling this method has the side effect of creating and opening all of
	 * the project's parents if they are not yet open.
	 * </p>
	 * 
	 * @param project
	 *            the given project
	 * @return the Rodin project corresponding to the given project, or
	 *         <code>null</code> if the given project is <code>null</code>
	 */
	public static IRodinProject valueOf(IProject project) {
		if (project == null) {
			return null;
		}
		RodinDB rodinDB = RodinDBManager.getRodinDBManager().getRodinDB();
		return rodinDB.getRodinProject(project);
	}

	/**
	 * Returns the Rodin element corresponding to the given resource, or
	 * <code>null</code> if unable to associate the given resource with a
	 * Rodin element.
	 * <p>
	 * The resource must be one of:
	 * <ul>
	 * <li>a project - the element returned is the corresponding
	 * <code>IRodinProject</code></li>
	 * <li>a Rodin file - the element returned is the corresponding
	 * <code>RodinFile</code></li>
	 * <li>the workspace root resource - the element returned is the
	 * <code>IRodinDB</code></li>
	 * </ul>
	 * </p>
	 * <p>
	 * Calling this method has the side effect of creating and opening all of
	 * the element's parents if they are not yet open.
	 * </p>
	 * 
	 * @param resource
	 *            the given resource
	 * @return the Rodin element corresponding to the given resource, or
	 *         <code>null</code> if unable to associate the given resource
	 *         with a Rodin element
	 */
	public static IRodinElement valueOf(IResource resource) {
		return RodinDBManager.valueOf(resource, null/* unknown Rodin project */);
	}

	/**
	 * Returns the Rodin database.
	 * 
	 * @param root
	 *            the given root
	 * @return the Rodin database, or <code>null</code> if the root is
	 *         <code>null</code>
	 */
	public static IRodinDB valueOf(IWorkspaceRoot root) {
		if (root == null) {
			return null;
		}
		return RodinDBManager.getRodinDBManager().getRodinDB();
	}
	
	/**
	 * Convert a Rodin project to a new version. This method does not manipulate the project
	 * but only computes what is necessary for the conversion to be done. This is returned as
	 * a result (<code>IConversionResult</code>).
	 * 
	 * @param project the Rodin project to be converted
	 * @param force whether or not files are expected to be in synchrony
	 * @param monitor a progress monitor, or <code>null</code> if progress
	 *    reporting and cancellation are not desired
	 * @return the proposed conversion in an <code>IConversionResult</code>
	 * @throws RodinDBException if there was a problem creating a conversion
	 */
	public static IConversionResult convert(
			IRodinProject project, boolean force, IProgressMonitor monitor) throws RodinDBException {
		Result result = new Result(project);
		
		result.convert(force, monitor);
		
		return result;
			
	}


}

/*******************************************************************************
 * Copyright (c) 2007 ETH Zurich.
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Rodin @ ETH Zurich
 ******************************************************************************/

package org.eventb.internal.ui.eventbeditor.editpage;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.commands.IHandler;
import org.eclipse.core.runtime.ListenerList;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.SafeRunner;
import org.eclipse.jface.util.SafeRunnable;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.ISelectionProvider;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.contexts.IContextActivation;
import org.eclipse.ui.contexts.IContextService;
import org.eclipse.ui.forms.IManagedForm;
import org.eclipse.ui.forms.editor.FormEditor;
import org.eclipse.ui.forms.widgets.FormText;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.ScrolledForm;
import org.eclipse.ui.handlers.IHandlerActivation;
import org.eclipse.ui.handlers.IHandlerService;
import org.eventb.core.EventBPlugin;
import org.eventb.core.ICommentedElement;
import org.eventb.core.IContextFile;
import org.eventb.core.IMachineFile;
import org.eventb.internal.ui.EventBText;
import org.eventb.internal.ui.IEventBInputText;
import org.eventb.internal.ui.Pair;
import org.eventb.internal.ui.TimerText;
import org.eventb.internal.ui.elementSpecs.IElementRelationship;
import org.eventb.internal.ui.eventbeditor.EventBEditor;
import org.eventb.internal.ui.eventbeditor.EventBEditorUtils;
import org.eventb.internal.ui.utils.Messages;
import org.eventb.ui.EventBFormText;
import org.eventb.ui.EventBUIPlugin;
import org.eventb.ui.eventbeditor.EventBEditorPage;
import org.eventb.ui.eventbeditor.IEventBEditor;
import org.rodinp.core.ElementChangedEvent;
import org.rodinp.core.IElementChangedListener;
import org.rodinp.core.IElementType;
import org.rodinp.core.IInternalElement;
import org.rodinp.core.IInternalElementType;
import org.rodinp.core.IInternalParent;
import org.rodinp.core.IRodinElement;
import org.rodinp.core.IRodinElementDelta;
import org.rodinp.core.IRodinFile;
import org.rodinp.core.RodinDBException;

public class EditPage extends EventBEditorPage implements ISelectionProvider,
		IElementChangedListener {

	// Title, tab title and ID of the page.
	public static final String PAGE_ID = EventBUIPlugin.PLUGIN_ID + ".edit"; //$NON-NLS-1$

	public static final String PAGE_TITLE = Messages.editorPage_edit_title;

	public static final String PAGE_TAB_TITLE = Messages.editorPage_edit_tabTitle;

	// A list of section composites. The list or its element must not be
	// <code>null</code>.
	List<ISectionComposite> sectionComps;

	// The main scrolled form
	ScrolledForm form;
	
	// Different handler activation for moving up and down with keyboard
	private IHandlerActivation moveUpHandlerActivation;

	private IHandlerActivation moveDownHandlerActivation;
	
	// The Context activation 
	IContextActivation activateContext;
	
	// Comment text widget at file level
	IEventBInputText commentText;

	/**
	 * Constructor: This default constructor will be used to create the page
	 */
	public EditPage() {
		super(PAGE_ID, PAGE_TAB_TITLE, PAGE_TITLE);
		listenerList = new ListenerList();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.ui.forms.editor.IFormPage#initialize(org.eclipse.ui.forms.editor.FormEditor)
	 */
	@Override
	public void initialize(FormEditor editor) {
		super.initialize(editor);
		((IEventBEditor<?>) editor).addElementChangedListener(this);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.ui.forms.editor.FormPage#createFormContent(org.eclipse.ui.forms.IManagedForm)
	 */
	@Override
	protected void createFormContent(IManagedForm managedForm) {
		super.createFormContent(managedForm);

		// Store the reference to the main scrolled form.
		form = managedForm.getForm();
		
		// The body of the main scrolled form has a grid layout (by default has
		// only one column).
		Composite body = form.getBody();
		GridLayout gLayout = new GridLayout();
		gLayout.marginWidth = 0;
		gLayout.marginHeight = 0;
		body.setLayout(gLayout);

		// Try to set the background color if in debug mode.
		if (EventBEditorUtils.DEBUG) {
			body.setBackground(form.getDisplay().getSystemColor(SWT.COLOR_BLUE));
		}
		
		// Create the top declaration. 
		createDeclaration(body);

		// Create the different section composites.
		createSections(body);
		
		// Refresh the main scrolled form.
		form.reflow(true);

		// Activate the context for Edit Page.
		// TODO: Should this be for Event-B Editor instead of just Edit Page.
		IContextService contextService = (IContextService) getSite()
				.getService(IContextService.class);
		activateContext = contextService
				.activateContext("org.eventb.ui.keybindings.contexts.editpage");

		// Activate different handlers for keyboard action.
		activateHandlers();
	}

	/**
	 * Utility method for activate different handlers, e.g. Move Up/Down.
	 */
	private void activateHandlers() {

		// Get the workbench handler service.
		IHandlerService handlerService = (IHandlerService) EventBUIPlugin
				.getDefault().getWorkbench().getAdapter(IHandlerService.class);
		
		// Do nothing if the workbench does not have a handler service.
		if (handlerService == null)
			return;
		
		// Creat the Move Up Handler
		IHandler moveUpHandler= new AbstractHandler() {
			@Override
			public Object execute(ExecutionEvent event) throws ExecutionException {
				move(true);
				return null;
			}
		};
		moveUpHandlerActivation = handlerService.activateHandler(
				"org.eventb.ui.keybindings.moveUp", moveUpHandler);

		// Create the Move Down Handler
		IHandler moveDownHandler= new AbstractHandler() {
			@Override
			public Object execute(ExecutionEvent event) throws ExecutionException {
				move(false);
				return null;
			}
		};
		moveDownHandlerActivation = handlerService.activateHandler(
				"org.eventb.ui.keybindings.moveDown", moveDownHandler);
	}

	/**
	 * Utility method for moving elements up and down.
	 * 
	 * @param up
	 *            <code>true</code> for moving up, <code>false</code> for
	 *            moving down.
	 */
	protected void move(boolean up) {
		// Assume that the global selection contain the list of element of the
		// same type and has the same parent
		if (globalSelection instanceof StructuredSelection
				&& !globalSelection.isEmpty()) {
			StructuredSelection ssel = (StructuredSelection) globalSelection;
			Object[] elements = ssel.toArray();
			IInternalElement firstElement = (IInternalElement) elements[0];
			IInternalElement lastElement = (IInternalElement) elements[elements.length - 1];
			IRodinElement parent = firstElement.getParent();
			IInternalElementType<?> type = firstElement.getElementType();

			if (parent != null && parent instanceof IInternalParent) {
				try {
					IInternalElement[] children = ((IInternalParent) parent)
							.getChildrenOfType(type);
					assert (children.length > 0);
					IInternalElement prevElement = null;
					for (int i = 0; i < children.length; ++i) {
						if (children[i].equals(firstElement))
							break;
						prevElement = children[i];
					}
					IInternalElement nextElement = null;
					for (int i = children.length - 1; i >= 0; --i) {
						if (children[i].equals(lastElement))
							break;
						nextElement = children[i];
					}
					if (up) {
						if (prevElement != null) {
							prevElement.move(parent, nextElement, null, false,
									new NullProgressMonitor());
						}
					} else {
						if (nextElement != null) {
							nextElement.move(parent, firstElement, null, false,
									new NullProgressMonitor());
						}
					}
				} catch (RodinDBException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
	}

	/**
	 * Utility method for creating the declaration part of this Edit page.
	 * 
	 * @param parent the composite parent.
	 */
	private void createDeclaration(Composite parent) {
		FormToolkit toolkit = this.getManagedForm().getToolkit();
		EventBEditor<?> editor = (EventBEditor<?>) this.getEditor();
		final Composite comp = toolkit.createComposite(parent);
		if (EventBEditorUtils.DEBUG) {
			comp.setBackground(comp.getDisplay().getSystemColor(SWT.COLOR_CYAN));
		}
		comp.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		GridLayout gridLayout = new GridLayout();
		gridLayout.numColumns = 3;
		comp.setLayout(gridLayout);
		FormText widget = toolkit.createFormText(comp, true);
		GridData gd = new GridData(SWT.FILL, SWT.FILL, false, false);
		widget.setLayoutData(gd);
		new EventBFormText(widget);
		final IRodinFile rodinInput = editor.getRodinInput();
		String declaration = "";
		if (rodinInput instanceof IMachineFile)
			declaration = "MACHINE";
		else if (rodinInput instanceof IContextFile)
			declaration = "CONTEXT";

		Label label = toolkit.createLabel(comp, "//");
		label.setLayoutData(new GridData());
		
		String text = "<form><li style=\"text\" bindent = \"-20\"><b>"
				+ declaration + "</b> "
				+ EventBPlugin.getComponentName(rodinInput.getElementName())
				+ "</li></form>";
		widget.setText(text, true, true);

		final Text commentWidget = toolkit.createText(comp, "", SWT.MULTI);
		commentWidget.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true,
				false));
		commentWidget.setForeground(commentWidget.getDisplay().getSystemColor(
				SWT.COLOR_DARK_GREEN));
		commentText = new EventBText(commentWidget);
		new TimerText(commentWidget, 1000) {

			@Override
			protected void response() {
				if (rodinInput instanceof ICommentedElement) {
					ICommentedElement cElement = (ICommentedElement) rodinInput;
					try {
						if (!cElement.hasComment()
								|| !cElement.getComment().equals(
										commentWidget.getText())) {
							cElement.setComment(commentWidget.getText(),
									new NullProgressMonitor());
						}
					} catch (RodinDBException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			}
		};
		if (rodinInput instanceof ICommentedElement) {
			ICommentedElement cElement = (ICommentedElement) rodinInput;
			try {
				if (cElement.hasComment()) {
					commentWidget.setText(cElement.getComment());
				}
			} catch (RodinDBException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		toolkit.paintBordersFor(comp);
	}

	/**
	 * Utility method for creating different sections composite. The information
	 * about the section composites is read from the element relationship UI
	 * Spec. registry.
	 * 
	 * @param parent
	 *            the composite parent of the section composites.
	 */
	private void createSections(final Composite parent) {
		EventBEditor<?> editor = (EventBEditor<?>) this.getEditor();
		IRodinFile rodinInput = editor.getRodinInput();
		IElementRelUISpecRegistry editSectionRegistry = ElementRelUISpecRegistry
				.getDefault();
		FormToolkit toolkit = this.getManagedForm().getToolkit();

		// Get the list of element relationships depending on the type (e.g.
		// IMachineFile or IContextFile) of the input file.
		List<IElementRelationship> rels = editSectionRegistry
				.getElementRelationships(rodinInput.getElementType());

		// Create the section composite corresponding with each relationship.
		sectionComps = new ArrayList<ISectionComposite>(rels.size());
		for (IElementRelationship rel : rels) {
			// Create the section composite
			SectionComposite sectionComp = new SectionComposite(this, toolkit,
					form, parent, rodinInput, rel, 0);
			sectionComps.add(sectionComp);
		}

	}

	// This is related to how the page is refreshed.
	
	// The set of elements that has changed.
	Set<IRodinElement> isChanged;

	// The set of elements that has been removed.
	Set<IRodinElement> isRemoved;

	// The set of elements that has been added.
	Set<IRodinElement> isAdded;

	// The set of pairs between element and the type of the children that has
	// changed.
	Set<Pair<IRodinElement, IElementType<?>>> childrenHasChanged;

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.rodinp.core.IElementChangedListener#elementChanged(org.rodinp.core.ElementChangedEvent)
	 */
	public void elementChanged(ElementChangedEvent event) {
		// Record the starting time.
		long beforeTime = System.currentTimeMillis();
		
		// Reset the information collected for the changed event.
		// TODO: What about concurrency?
		isChanged = new HashSet<IRodinElement>();
		isRemoved = new HashSet<IRodinElement>();
		isAdded = new HashSet<IRodinElement>();
		childrenHasChanged = new HashSet<Pair<IRodinElement, IElementType<?>>>();
		
		// Process the input changed event.
		processDelta(event.getDelta());
		
		// Refresh the page according to the collected information.
		postRefresh();

		// Update the comment of the file.
		updateComment();

		// Record the end time.
		long afterTime = System.currentTimeMillis();
		
		// Measure the duration for refreshing the page. 
		if (EventBEditorUtils.DEBUG)
			EventBEditorUtils.debug("Duration: " + (afterTime - beforeTime)
					+ " ms");
	}

	/**
	 * Utility method for updating comment of the file. 
	 */
	private void updateComment() {
		final Text commentWidget = commentText.getTextWidget();
		String text = commentWidget.getText();
		IRodinFile rodinInput = this.getEventBEditor().getRodinInput();
		if (rodinInput instanceof ICommentedElement) {
			final ICommentedElement cElement = (ICommentedElement) rodinInput;
			try {
				if (cElement.hasComment()) {
					final String comment = cElement.getComment();
					if (!comment.equals(text)) {
						Display display = commentWidget.getDisplay();
						if (!display.isDisposed()) {
							display.asyncExec(new Runnable() {
								public void run() {
									commentWidget.setText(comment);
								}
							});
						}
					}
				}
			} catch (RodinDBException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			internalPack(commentWidget.getParent());
		}
	}

	/**
	 * Utility method for packing the composite to the preferred size.
	 * 
	 * @param c
	 *            the composite to be packed.
	 */
	void internalPack(Composite c) {
		if (c.equals(form.getBody())) {
			if (EventBEditorUtils.DEBUG)
				EventBEditorUtils.debug("Full resize");
			form.reflow(true);			
		}
		Rectangle bounds = c.getBounds();
		Point preferredSize = c.computeSize(SWT.DEFAULT, SWT.DEFAULT);

		if (preferredSize.x > bounds.width || preferredSize.y > bounds.height) {
			internalPack(c.getParent());
		} else {
			c.layout(true);
			c.setBounds(bounds);
		}
	}

	/**
	 * Utility method for refreshing the page according to a collected
	 * information from the delta.
	 */
	private void postRefresh() {
		if (form == null || form.isDisposed())
			return;

		Display display = form.getDisplay();
		display.syncExec(new Runnable() {

			public void run() {
				// Do not redraw the page.
				form.getBody().setRedraw(false);

				// Process the removed element first.
				for (IRodinElement element : isRemoved) {
					for (ISectionComposite sectionComp : sectionComps) {
						sectionComp.elementRemoved(element);
					}
					if (isSelected(element)) {
						deselect(globalSelection);
						setSelection(new StructuredSelection());
					}
				}

				// Process the added element second.
				for (IRodinElement element : isAdded) {
					for (ISectionComposite sectionComp : sectionComps) {
						sectionComp.elementAdded(element);
					}
				}

				// Process the changed element third.
				for (IRodinElement element : isChanged) {
					for (ISectionComposite sectionComp : sectionComps) {
						sectionComp.refresh(element);
					}
				}
			
				// Process the elements that changed order last.
				for (Pair<IRodinElement, IElementType<?>> pair : childrenHasChanged) {
					for (ISectionComposite sectionComp : sectionComps) {
						sectionComp.childrenChanged(pair.getFirst(), pair
								.getSecond());
					}
				}

				// Redraw the page.
				form.getBody().setRedraw(true);
			}

		});
	}

	/**
	 * Utility method to check if an element is currently selected within the
	 * page.
	 * 
	 * @param element
	 *            a Rodin element.
	 * @return <code>true</code> if the element is selected. Return
	 *         <code>false<code> otherwise.
	 */
	protected boolean isSelected(IRodinElement element) {
		if (globalSelection instanceof StructuredSelection) {
			StructuredSelection ssel = (StructuredSelection) globalSelection;
			for (Iterator<?> it = ssel.iterator(); it.hasNext();) {
				if (it.next().equals(element))
					return true;
			}
		}
		return false;
	}

	/**
	 * Process the delta to collect the information about changes.
	 * 
	 * @param delta
	 *            a Rodin Element Delta.
	 */
	void processDelta(IRodinElementDelta delta) {
		IRodinElement element = delta.getElement();
		int kind = delta.getKind();
		if (element instanceof IRodinFile && kind == IRodinElementDelta.CHANGED) {
			for (IRodinElementDelta subDelta : delta.getAffectedChildren()) {
				processDelta(subDelta);
			}
			return;
		}

		if (kind == IRodinElementDelta.ADDED) {
			isAdded.add(element);
			childrenHasChanged.add(new Pair<IRodinElement, IElementType<?>>(
					element.getParent(), element.getElementType()));
			return;
		}
		if (kind == IRodinElementDelta.REMOVED) {
			isRemoved.add(element);
			childrenHasChanged.add(new Pair<IRodinElement, IElementType<?>>(
					element.getParent(), element.getElementType()));
			return;
		} else { // kind == CHANGED
			int flags = delta.getFlags();
			if ((flags & IRodinElementDelta.F_REORDERED) != 0) {
				if (EventBEditorUtils.DEBUG)
					EventBEditorUtils.debug("REORDERED");
				childrenHasChanged.add(new Pair<IRodinElement, IElementType<?>>(
						element.getParent(), element.getElementType()));
				return;
			} else if ((flags & IRodinElementDelta.F_CHILDREN) != 0) {
				for (IRodinElementDelta subDelta : delta.getAffectedChildren()) {
					processDelta(subDelta);
				}
			} else if ((flags & IRodinElementDelta.F_ATTRIBUTE) != 0) {
				isChanged.add(element);
				childrenHasChanged.add(new Pair<IRodinElement, IElementType<?>>(
						element.getParent(), element.getElementType()));
			}
			return;
		}

	}

	/* (non-Javadoc)
	 * @see org.eclipse.ui.forms.editor.FormPage#dispose()
	 */
	@Override
	public void dispose() {
		IEventBEditor<?> editor = this.getEventBEditor();
		editor.removeElementChangedListener(this);
		if (activateContext != null)
			activateContext.getContextService().deactivateContext(activateContext);
		deactivateHandlers();
		if (commentText != null) {
			commentText.dispose();
		}
		super.dispose();
	}

	/**
	 * Utility method to de-activate the handlers. 
	 */
	private void deactivateHandlers() {
		IHandlerService handlerService = (IHandlerService) EventBUIPlugin
				.getDefault().getWorkbench().getAdapter(IHandlerService.class);

		if (handlerService != null) {
			if (moveUpHandlerActivation != null)
				handlerService.deactivateHandler(moveUpHandlerActivation);
			if (moveDownHandlerActivation != null)
				handlerService.deactivateHandler(moveDownHandlerActivation);
		}
		moveUpHandlerActivation = null;
		moveDownHandlerActivation = null;
	}

	// This part to make this page to be a selection provider.
	// TODO Make this as a separate class?

	// A list of listener.
	private ListenerList listenerList;

	// The global selection.
	ISelection globalSelection;

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.jface.viewers.ISelectionProvider#addSelectionChangedListener(org.eclipse.jface.viewers.ISelectionChangedListener)
	 */
	public void addSelectionChangedListener(ISelectionChangedListener listener) {
		listenerList.add(listener);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.jface.viewers.ISelectionProvider#removeSelectionChangedListener(org.eclipse.jface.viewers.ISelectionChangedListener)
	 */
	public void removeSelectionChangedListener(
			ISelectionChangedListener listener) {
		listenerList.remove(listener);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.jface.viewers.ISelectionProvider#getSelection()
	 */
	public ISelection getSelection() {
		return globalSelection;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.jface.viewers.ISelectionProvider#setSelection(org.eclipse.jface.viewers.ISelection)
	 */
	public void setSelection(ISelection selection) {
		this.globalSelection = selection;
		fireSelectionChanged(new SelectionChangedEvent(this, globalSelection));
		IEventBEditor<?> editor = (IEventBEditor<?>) this.getEditor();
		ISelectionProvider selectionProvider = editor.getSite()
				.getSelectionProvider();
		selectionProvider.setSelection(selection);
	}

	/**
	 * Notifies all registered selection changed listeners that the editor's
	 * selection has changed. Only listeners registered at the time this method
	 * is called are notified.
	 * 
	 * @param event
	 *            the selection changed event
	 */
	private void fireSelectionChanged(final SelectionChangedEvent event) {
		Object[] listeners = this.listenerList.getListeners();
		for (int i = 0; i < listeners.length; ++i) {
			final ISelectionChangedListener l = (ISelectionChangedListener) listeners[i];
			SafeRunner.run(new SafeRunnable() {
				public void run() {
					l.selectionChanged(event);
				}
			});
		}
	}

	private IRodinElement lastSelectedElement;

	public void selectionChanges(IRodinElement element, boolean shiftPressed) {
		long beginTime = System.currentTimeMillis();
		if (globalSelection instanceof StructuredSelection
				&& ((StructuredSelection) globalSelection).size() == 1
				&& ((StructuredSelection) globalSelection).getFirstElement()
						.equals(element)) {
			select(element, false);
			setSelection(new StructuredSelection());
			return;

		} else {
			deselect(globalSelection);
			if (shiftPressed
					&& lastSelectedElement != null
					&& element.getParent().equals(
							lastSelectedElement.getParent())
					&& element.getElementType().equals(
							lastSelectedElement.getElementType())) {
				selectRange(lastSelectedElement, element);
			} else {
				select(element, true);
				setSelection(new StructuredSelection(element));
				lastSelectedElement = element;
			}
		}
		long afterTime = System.currentTimeMillis();
		if (EventBEditorUtils.DEBUG) {
			EventBEditorUtils.debug("Duration " + (afterTime - beginTime)
					+ " ms");
		}
	}

	private void selectRange(IRodinElement firstElement,
			IRodinElement secondElement) {
		assert firstElement.getParent().equals(secondElement.getParent());
		assert firstElement.getElementType().equals(
				secondElement.getElementType());
		IRodinElement parent = firstElement.getParent();
		IElementType<? extends IRodinElement> type = firstElement
				.getElementType();
		assert parent instanceof IInternalParent;
		try {
			IRodinElement[] children = ((IInternalParent) parent)
					.getChildrenOfType(type);
			boolean found = false;
			List<IRodinElement> selected = new ArrayList<IRodinElement>();
			for (IRodinElement child : children) {
				if (child.equals(firstElement) || child.equals(secondElement)) {
					select(child, true);
					selected.add(child);
					if (found)
						break;
					else
						found = true;
				} else {
					if (found) {
						select(child, true);
						selected.add(child);
					}
				}
			}
			setSelection(new StructuredSelection(selected));
		} catch (RodinDBException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	void deselect(ISelection ssel) {
		if (ssel instanceof StructuredSelection) {
			for (Iterator<?> it = ((StructuredSelection) ssel).iterator(); it
					.hasNext();) {
				Object obj = it.next();
				if (obj instanceof IRodinElement) {
					select((IRodinElement) obj, false);
				}
			}
		}
	}

	private void select(IRodinElement element, boolean select) {
		for (ISectionComposite sectionComp : sectionComps) {
			sectionComp.select(element, select);
		}
	}

	public void recursiveExpand(IRodinElement element) {
		for (ISectionComposite sectionComp : sectionComps) {
			sectionComp.recursiveExpand(element);
		}
	}

}

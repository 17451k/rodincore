/*******************************************************************************
 * Copyright (c) 2007, 2008 ETH Zurich and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     ETH Zurich - initial API and implementation
 *     Systerel - added "show borders" preference
 *     Systerel - used EventBSharedColor and EventBPreferenceStore
 *     Systerel - added history support
 *     Systerel - separation of file and root element
 *******************************************************************************/
package org.eventb.internal.ui.eventbeditor.editpage;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IMarkerDelta;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IResourceChangeEvent;
import org.eclipse.core.resources.IResourceChangeListener;
import org.eclipse.core.resources.IResourceDelta;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.jface.viewers.ISelection;
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
import org.eclipse.ui.forms.IManagedForm;
import org.eclipse.ui.forms.editor.FormEditor;
import org.eclipse.ui.forms.events.HyperlinkEvent;
import org.eclipse.ui.forms.events.IHyperlinkListener;
import org.eclipse.ui.forms.widgets.FormText;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.ImageHyperlink;
import org.eclipse.ui.forms.widgets.ScrolledForm;
import org.eventb.core.ICommentedElement;
import org.eventb.core.IContextRoot;
import org.eventb.core.IMachineRoot;
import org.eventb.internal.ui.EventBImage;
import org.eventb.internal.ui.EventBSharedColor;
import org.eventb.internal.ui.EventBText;
import org.eventb.internal.ui.IEventBInputText;
import org.eventb.internal.ui.Pair;
import org.eventb.internal.ui.TimerText;
import org.eventb.internal.ui.UIUtils;
import org.eventb.internal.ui.elementSpecs.IElementRelationship;
import org.eventb.internal.ui.eventbeditor.EventBEditor;
import org.eventb.internal.ui.eventbeditor.EventBEditorUtils;
import org.eventb.internal.ui.eventbeditor.operations.AtomicOperation;
import org.eventb.internal.ui.eventbeditor.operations.History;
import org.eventb.internal.ui.eventbeditor.operations.OperationFactory;
import org.eventb.internal.ui.preferences.EventBPreferenceStore;
import org.eventb.internal.ui.preferences.PreferenceConstants;
import org.eventb.internal.ui.utils.Messages;
import org.eventb.ui.EventBFormText;
import org.eventb.ui.EventBUIPlugin;
import org.eventb.ui.IEventBSharedImages;
import org.eventb.ui.eventbeditor.EventBEditorPage;
import org.eventb.ui.eventbeditor.IEventBEditor;
import org.rodinp.core.ElementChangedEvent;
import org.rodinp.core.IAttributeType;
import org.rodinp.core.IElementChangedListener;
import org.rodinp.core.IElementType;
import org.rodinp.core.IInternalElement;
import org.rodinp.core.IInternalElementType;
import org.rodinp.core.IInternalParent;
import org.rodinp.core.IRodinElement;
import org.rodinp.core.IRodinElementDelta;
import org.rodinp.core.IRodinFile;
import org.rodinp.core.RodinDBException;
import org.rodinp.core.RodinMarkerUtil;

/**
 * @author htson
 *         <p>
 *         This is the implementation of the Edit Page in Event-B editor.
 *         Basically, this contains several {@link ISectionComposite}.
 */
public class EditPage extends EventBEditorPage implements
		IElementChangedListener, IResourceChangeListener {

	// Title, tab title and ID of the page.
	public static final String PAGE_ID = EventBUIPlugin.PLUGIN_ID + ".edit"; //$NON-NLS-1$

	public static final String PAGE_TITLE = Messages.editorPage_edit_title;

	public static final String PAGE_TAB_TITLE = Messages.editorPage_edit_tabTitle;

	// A list of section composites. The list or its element must not be
	// <code>null</code>.
	List<ISectionComposite> sectionComps;

	// The main scrolled form
	ScrolledForm form;
	
	// Comment text widget at file level
	IEventBInputText commentText;

	/**
	 * Constructor: This default constructor will be used to create the page
	 */
	public EditPage() {
		super(PAGE_ID, PAGE_TAB_TITLE, PAGE_TITLE);
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
		ResourcesPlugin.getWorkspace().addResourceChangeListener(this);
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
			body
					.setBackground(EventBSharedColor
							.getSystemColor(SWT.COLOR_BLUE));
		}
		
		// Create the top declaration. 
		createDeclaration(body);

		// Create the different section composites.
		createSections(body);
		
		// Refresh the main scrolled form.
		form.reflow(true);
	}

	/**
	 * Utility method for moving elements up and down. An important assumption
	 * here is that the current selection contain the list of consecutive
	 * elements of the same type and has the same parent.
	 * 
	 * @param up
	 *            <code>true</code> for moving up, <code>false</code> for
	 *            moving down.
	 */
	protected void move(boolean up) {
		if (currentSelection instanceof StructuredSelection
				&& !currentSelection.isEmpty()) {
			StructuredSelection ssel = (StructuredSelection) currentSelection;
			Object[] elements = ssel.toArray();
			IInternalElement firstElement = (IInternalElement) elements[0];
			IInternalElement lastElement = (IInternalElement) elements[elements.length - 1];
			IRodinElement parent = firstElement.getParent();
			IInternalElementType<?> type = firstElement.getElementType();

			if (parent != null && parent instanceof IInternalParent) {
				AtomicOperation operation = OperationFactory.move(
						getEventBEditor(), up, (IInternalParent) parent, type,
						firstElement, lastElement);
				History.getInstance().addOperation(operation);
			}
		}
	}

	/**
	 * Utility method for creating the declaration part of this Edit page.
	 * 
	 * @param parent
	 *            the composite parent.
	 */
	private void createDeclaration(Composite parent) {
		FormToolkit toolkit = this.getManagedForm().getToolkit();
		EventBEditor<?> editor = (EventBEditor<?>) this.getEditor();
		final Composite comp = toolkit.createComposite(parent);
		if (EventBEditorUtils.DEBUG) {
			comp
					.setBackground(EventBSharedColor
							.getSystemColor(SWT.COLOR_CYAN));
		}
		comp.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		GridLayout gridLayout = new GridLayout();
		gridLayout.numColumns = 4;
		comp.setLayout(gridLayout);
		
		// Expand all button
		ImageHyperlink hyperlink = toolkit.createImageHyperlink(comp, SWT.TOP);
		hyperlink.setImage(EventBImage
				.getImage(IEventBSharedImages.IMG_EXPAND_ALL));
		GridData gd = new GridData(SWT.FILL, SWT.FILL, false, false);
		hyperlink.setLayoutData(gd);
		hyperlink.addHyperlinkListener(new IHyperlinkListener() {

			public void linkActivated(HyperlinkEvent e) {
				assert sectionComps != null;
				for (ISectionComposite sectionComp : sectionComps) {
					sectionComp.recursiveExpand();
				}
			}

			public void linkEntered(HyperlinkEvent e) {
				// Do nothing
			}

			public void linkExited(HyperlinkEvent e) {
				// Do nothing
			}
			
		});
		
		FormText widget = toolkit.createFormText(comp, true);
		gd = new GridData(SWT.FILL, SWT.FILL, false, false);
		widget.setLayoutData(gd);
		new EventBFormText(widget);
		final IInternalElement rodinInput = editor.getRodinInput();
		String declaration = "";
		if (rodinInput instanceof IMachineRoot)
			declaration = "MACHINE";
		else if (rodinInput instanceof IContextRoot)
			declaration = "CONTEXT";

		Label label = toolkit.createLabel(comp, "//");
		label.setLayoutData(new GridData());
		
		String text = "<form><li style=\"text\" bindent = \"-20\"><b>"
				+ declaration + "</b> " + rodinInput.getElementName()
				+ "</li></form>";
		widget.setText(text, true, true);

		final Text commentWidget = toolkit.createText(comp, "", SWT.MULTI);
		commentWidget.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true,
				false));
		commentWidget.setForeground(EventBPreferenceStore
				.getColorPreference(PreferenceConstants.P_TEXT_FOREGROUND));
		commentText = new EventBText(commentWidget);
		new TimerText(commentWidget, 1000) {

			@Override
			protected void response() {
				if (rodinInput instanceof ICommentedElement) {
					ICommentedElement ce = (ICommentedElement) rodinInput;
					UIUtils.setStringAttribute(ce,
							new CommentAttributeFactory(), commentWidget
									.getText(), null);
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
		if (EventBPreferenceStore
				.getBooleanPreference(PreferenceConstants.P_BORDER_ENABLE)) {
			toolkit.paintBordersFor(comp);
		}
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
		IInternalElement rodinInput = editor.getRodinInput();
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
		
		if (form == null || form.isDisposed())
			return;

		if (commentText == null)
			return;
		final Text commentWidget = commentText.getTextWidget();
		Display display = form.getDisplay();
		display.syncExec(new Runnable() {

			public void run() {
				String text = commentWidget.getText();
				IInternalElement rodinInput = EditPage.this.getEventBEditor()
						.getRodinInput();
				if (rodinInput instanceof ICommentedElement) {
					final ICommentedElement cElement = (ICommentedElement) rodinInput;
					try {
						if (cElement.hasComment()) {
							final String comment = cElement.getComment();
							if (!comment.equals(text)) {
								commentWidget.setText(comment);
							}
						}
					} catch (RodinDBException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					internalPack(commentWidget.getParent());
				}
			}
			
		});
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

				// Process the removed element
				for (IRodinElement element : isRemoved) {
					for (ISectionComposite sectionComp : sectionComps) {
						sectionComp.elementRemoved(element);
					}
					if (isSelected(element)) {
						setEditorSelection(new StructuredSelection());
					}
				}

				// Process the added element
				for (IRodinElement element : isAdded) {
					for (ISectionComposite sectionComp : sectionComps) {
						sectionComp.elementAdded(element);
					}
				}

				// Process the changed element
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
		if (currentSelection instanceof StructuredSelection) {
			StructuredSelection ssel = (StructuredSelection) currentSelection;
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
			} 
			if ((flags & IRodinElementDelta.F_CHILDREN) != 0) {
				for (IRodinElementDelta subDelta : delta.getAffectedChildren()) {
					processDelta(subDelta);
				}
			}
			if ((flags & IRodinElementDelta.F_ATTRIBUTE) != 0) {
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
		if (commentText != null) {
			commentText.dispose();
		}
		ResourcesPlugin.getWorkspace().removeResourceChangeListener(this);
		super.dispose();
	}

	// The maintain the current selection.
	private ISelection currentSelection = new StructuredSelection();

	// This should be called from Event-B Editor only.
	public void setSelection(ISelection selection) {
		// TODO Should try to compare the selection first ?
		// De-select the current selection
		deselect(currentSelection);

		List<IRodinElement> elements = new ArrayList<IRodinElement>();
		
		if (selection instanceof StructuredSelection) {
			for (Iterator<?> it = ((StructuredSelection) selection).iterator(); it
					.hasNext();) {
				Object obj = it.next();
				if (obj instanceof IRodinElement) {
					if (select((IRodinElement) obj, true)) {
						elements.add((IRodinElement) obj);
					}
				}
			}
		}

		// Create a new selection with the set of selected elements.
		currentSelection = new StructuredSelection(elements);			
	}

	// The last selected element (from ButtonComposite).
	private IRodinElement lastSelectedElement;

	// This is the callback from ButtonComposite for selecting an element with
	// the input specifies if Shift is pressed.
	public void selectionChanges(IRodinElement element, boolean shiftPressed) {
		long beginTime = System.currentTimeMillis();
		if (currentSelection instanceof StructuredSelection
				&& ((StructuredSelection) currentSelection).size() == 1
				&& ((StructuredSelection) currentSelection).getFirstElement()
						.equals(element)) {
			setEditorSelection(new StructuredSelection());
			return;

		} else {
			if (shiftPressed
					&& lastSelectedElement != null
					&& element.getParent().equals(
							lastSelectedElement.getParent())
					&& element.getElementType().equals(
							lastSelectedElement.getElementType())) {
				selectRange(lastSelectedElement, element);
			} else {
				lastSelectedElement = element;
				setEditorSelection(new StructuredSelection(element));
			}
		}
		long afterTime = System.currentTimeMillis();
		if (EventBEditorUtils.DEBUG) {
			EventBEditorUtils.debug("Duration " + (afterTime - beginTime)
					+ " ms");
		}
	}

	void setEditorSelection(ISelection selection) {
		IEventBEditor<?> eventBEditor = this.getEventBEditor();
		eventBEditor.getSite().getSelectionProvider().setSelection(selection);
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
					selected.add(child);
					if (found)
						break;
					else
						found = true;
				} else {
					if (found) {
						selected.add(child);
					}
				}
			}
			setEditorSelection(new StructuredSelection(selected));
		} catch (RodinDBException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private void deselect(ISelection ssel) {
		
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

	public boolean select(IRodinElement element, boolean select) {
		for (ISectionComposite sectionComp : sectionComps) {
			if (sectionComp.select(element, select))
				return true;
		}
		return false;
	}

	public void recursiveExpand(IRodinElement element) {
		for (ISectionComposite sectionComp : sectionComps) {
			sectionComp.recursiveExpand(element);
		}
	}

	public void edit(IInternalElement element, IAttributeType attributeType,
			int charStart, int charEnd) {
		for (ISectionComposite sectionComp : sectionComps) {
			sectionComp.edit(element, attributeType, charStart, charEnd);
		}
	}
	
	public void resourceChanged(IResourceChangeEvent event) {
		Map<IRodinElement, Set<IAttributeType>> map = new HashMap<IRodinElement, Set<IAttributeType>>();
		IEventBEditor<?> editor = (IEventBEditor<?>) this.getEditor();
		IFile rodinInput = editor.getRodinInput().getRodinFile().getResource();
		IMarkerDelta[] rodinProblemMarkerDeltas = event.findMarkerDeltas(
			RodinMarkerUtil.RODIN_PROBLEM_MARKER, true);
		for (IMarkerDelta delta : rodinProblemMarkerDeltas) {
			IResource resource = delta.getResource();
			if (rodinInput.equals(resource)) {
				if (EventBEditorUtils.DEBUG) {
					printRodinMarkerDelta(delta);
				}
				IRodinElement element = RodinMarkerUtil
						.getElement(delta);
				if (element == null)
					continue;
				IAttributeType attributeType = RodinMarkerUtil
						.getAttributeType(delta);
				map = addMap(map, element, attributeType);
			}
		}
		if (EventBEditorUtils.DEBUG) {
			printMarkers(map);
		}
		resourceChangedRefresh(map);
		
	}

	private void printMarkers(Map<IRodinElement, Set<IAttributeType>> markers) {
		EventBEditorUtils.debug(markers.toString());
	}

	private Map<IRodinElement, Set<IAttributeType>> addMap(
			Map<IRodinElement, Set<IAttributeType>> markers,
			IRodinElement element, IAttributeType attributeType) {
		Set<IAttributeType> list = markers.get(element);
		if (list == null) {
			list = new HashSet<IAttributeType>();
			if (attributeType != null)
				list.add(attributeType);
			markers.put(element, list);
		}
		else if (list.size() != 0 && attributeType != null) {
			list.add(attributeType);
		}
		return markers;
	}

	private void printRodinMarkerDelta(IMarkerDelta delta) {
		EventBEditorUtils.debug("******");
		int kind = delta.getKind();
		if (kind == IResourceDelta.ADDED) {
			EventBEditorUtils.debug("Marker added");
		} else if (kind == IResourceDelta.REMOVED) {
			EventBEditorUtils.debug("Marker removed");
		} else if (kind == IResourceDelta.CHANGED) {
			EventBEditorUtils.debug("Marker changed");
		}
		Map<?,?> attributes = delta.getAttributes();
		Set<?> keySet = attributes.keySet();
		for (Object key : keySet) {
			EventBEditorUtils.debug(key.toString() + " --> "
					+ attributes.get(key).toString());
		}
	}

	private void resourceChangedRefresh(final Map<IRodinElement, Set<IAttributeType>> map) {
		IEventBEditor<?> editor = (IEventBEditor<?>) this.getEditor();
		final IRodinFile rodinInput = editor.getRodinInput().getRodinFile();
		Display display = this.getSite().getShell().getDisplay();
		display.syncExec(new Runnable() {

			public void run() {
				Set<IRodinElement> keySet = map.keySet();
				for (IRodinElement key : keySet) {
					Set<IAttributeType> set = map.get(key);
					if (rodinInput.equals(key)
							|| rodinInput.isAncestorOf(key)) {
						if (sectionComps != null)
							for (ISectionComposite sectionComposite : sectionComps) {
								sectionComposite.refresh(key, set);
							}
					}
				}
			}
			
		});
	}


}

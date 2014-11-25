/*******************************************************************************
 * Copyright (c) 2005, 2014 ETH Zurich and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     ETH Zurich - initial API and implementation
 *     Systerel - separation of file and root element
 *******************************************************************************/
package org.eventb.internal.ui.eventbeditor;

import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.TreeColumn;
import org.eventb.core.IInvariant;
import org.eventb.core.IMachineRoot;
import org.eventb.ui.ElementSorter;
import org.eventb.ui.eventbeditor.IEventBEditor;
import org.rodinp.core.IParent;
import org.rodinp.core.IRodinElement;
import org.rodinp.core.RodinDBException;

/**
 * @author htson
 *         <p>
 *         This sub-class Event-B Editable tree viewer for editing invariant
 *         elements.
 */
public class InvariantEditableTreeViewer extends EventBEditableTreeViewer {

	/**
	 * @author htson
	 *         <p>
	 *         The content provider class.
	 */
	class InvariantContentProvider implements ITreeContentProvider {
		// The invisible root
		private IMachineRoot invisibleRoot = null;

		/*
		 * (non-Javadoc)
		 * 
		 * @see org.eclipse.jface.viewers.ITreeContentProvider#getParent(java.lang.Object)
		 */
		@Override
		public Object getParent(Object child) {
			if (child instanceof IRodinElement)
				return ((IRodinElement) child).getParent();
			return null;
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see org.eclipse.jface.viewers.ITreeContentProvider#getChildren(java.lang.Object)
		 */
		@Override
		public Object[] getChildren(Object parent) {
			if (parent instanceof IMachineRoot) {
				IMachineRoot root = (IMachineRoot) parent;
				try {
					return root.getChildrenOfType(IInvariant.ELEMENT_TYPE);
				} catch (RodinDBException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}

			if (parent instanceof IParent) {
				try {
					return ((IParent) parent).getChildren();
				} catch (RodinDBException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}

			return new Object[0];
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see org.eclipse.jface.viewers.ITreeContentProvider#hasChildren(java.lang.Object)
		 */
		@Override
		public boolean hasChildren(Object parent) {
			return getChildren(parent).length > 0;
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see org.eclipse.jface.viewers.IStructuredContentProvider#getElements(java.lang.Object)
		 */
		@Override
		public Object[] getElements(Object parent) {
			if (parent instanceof IMachineRoot) {
				if (invisibleRoot == null) {
					invisibleRoot = (IMachineRoot) parent;
					return getChildren(invisibleRoot);
				}
			}
			return getChildren(parent);
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see org.eclipse.jface.viewers.IContentProvider#dispose()
		 */
		@Override
		public void dispose() {
			// Do nothing
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see org.eclipse.jface.viewers.IContentProvider#inputChanged(org.eclipse.jface.viewers.Viewer,
		 *      java.lang.Object, java.lang.Object)
		 */
		@Override
		public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
			invisibleRoot = null;
		}
	}

	/**
	 * Constructor.
	 * <p>
	 * 
	 * @param editor
	 *            an Event-B Editor
	 * @param parent
	 *            the composite parent for this tree viewer
	 * @param style
	 *            the style to create the tree viewer
	 */
	public InvariantEditableTreeViewer(IEventBEditor<IMachineRoot> editor, Composite parent,
			int style) {
		super(editor, parent, style);
		this.setContentProvider(new InvariantContentProvider());
		this.setLabelProvider(new EventBTreeLabelProvider(editor, this));
		this.setSorter(new ElementSorter());
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eventb.internal.ui.eventbeditor.EventBEditableTreeViewer#createTreeColumns()
	 */
	@Override
	protected void createTreeColumns() {
		numColumn = 2;

		Tree tree = this.getTree();
		TreeColumn elementColumn = new TreeColumn(tree, SWT.LEFT);
		elementColumn.setText("Label");
		elementColumn.setResizable(true);
		elementColumn.setWidth(200);

		TreeColumn predicateColumn = new TreeColumn(tree, SWT.LEFT);
		predicateColumn.setText("Predicate");
		predicateColumn.setResizable(true);
		predicateColumn.setWidth(250);

		tree.setHeaderVisible(true);
	}


}

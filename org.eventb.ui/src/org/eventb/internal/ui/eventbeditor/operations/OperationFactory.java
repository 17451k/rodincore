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
package org.eventb.internal.ui.eventbeditor.operations;

import java.util.Collection;

import org.eclipse.core.commands.operations.IUndoContext;
import org.eclipse.jface.viewers.TreeViewer;
import org.eventb.core.IMachineRoot;
import org.eventb.internal.ui.Pair;
import org.eventb.internal.ui.eventbeditor.editpage.IAttributeFactory;
import org.eventb.ui.eventbeditor.IEventBEditor;
import org.rodinp.core.IAttributeType;
import org.rodinp.core.IAttributedElement;
import org.rodinp.core.IInternalElement;
import org.rodinp.core.IInternalElementType;
import org.rodinp.core.IInternalParent;
import org.rodinp.core.IRodinElement;
import org.rodinp.core.IRodinFile;

public class OperationFactory {

	private OperationFactory() {
		// non instanciable class
	}

	public static AtomicOperation createTheoremWizard(IEventBEditor<?> editor,
			String label, String content) {
		final OperationBuilder builder = new OperationBuilder();
		final AtomicOperation cmd = new AtomicOperation(builder.createTheorem(
				editor, label, content));
		cmd.addContext(getContext(editor));
		return cmd;
	}

	public static AtomicOperation createTheoremWizard(IEventBEditor<?> editor,
			String[] labels, String[] contents) {
		final OperationBuilder builder = new OperationBuilder();
		final AtomicOperation cmd = new AtomicOperation(builder.createTheorem(
				editor, labels, contents));
		cmd.addContext(getContext(editor));
		return cmd;
	}

	/**
	 * @param label
	 *            null to set a default label
	 * @param predicate
	 */
	public static AtomicOperation createAxiomWizard(IEventBEditor<?> editor,
			String label, String predicate) {
		final OperationBuilder builder = new OperationBuilder();
		AtomicOperation cmd = new AtomicOperation(builder.createAxiom(editor,
				label, predicate));
		cmd.addContext(getContext(editor));
		return cmd;
	}

	public static AtomicOperation createAxiomWizard(IEventBEditor<?> editor,
			String[] labels, String[] predicates) {
		final OperationBuilder builder = new OperationBuilder();
		AtomicOperation cmd = new AtomicOperation(builder.createAxiom(editor,
				labels, predicates));
		cmd.addContext(getContext(editor));
		return cmd;
	}

	public static AtomicOperation createConstantWizard(IEventBEditor<?> editor,
			String identifier, String[] labels, String[] predicates) {
		final AtomicOperation cmd;
		final OperationBuilder builder = new OperationBuilder();
		cmd = new AtomicOperation(builder.createConstant(editor, identifier,
				labels, predicates));
		cmd.addContext(getContext(editor));
		return cmd;
	}

	public static AtomicOperation createEnumeratedSetWizard(
			IEventBEditor<?> editor, String identifier, String[] elements) {
		final OperationBuilder builder = new OperationBuilder();
		final AtomicOperation cmd = new AtomicOperation(builder
				.createEnumeratedSet(editor, identifier, elements));
		cmd.addContext(getContext(editor));
		return cmd;

	}

	public static AtomicOperation createVariantWizard(IEventBEditor<?> editor,
			String predicate) {
		final OperationBuilder builder = new OperationBuilder();
		final AtomicOperation cmd = new AtomicOperation(builder.createVariant(
				editor, predicate));
		cmd.addContext(getContext(editor));
		return cmd;
	}

	public static AtomicOperation createVariableWizard(
			final IEventBEditor<IMachineRoot> editor, final String varName,
			final Collection<Pair<String, String>> invariant,
			final String actName, final String actSub) {
		final OperationBuilder builder = new OperationBuilder();
		final AtomicOperation cmd = new AtomicOperation(builder.createVariable(
				editor, varName, invariant, actName, actSub));
		cmd.addContext(getContext(editor));
		return cmd;
	}

	/**
	 * @param label
	 *            if null the label of created element is the next free label.
	 */
	public static AtomicOperation createInvariantWizard(
			IEventBEditor<?> editor, String label, String content) {
		final OperationBuilder builder = new OperationBuilder();
		final AtomicOperation cmd = new AtomicOperation(builder
				.createInvariant(editor, label, content));
		cmd.addContext(getContext(editor));
		return cmd;
	}

	public static AtomicOperation createInvariantWizard(
			IEventBEditor<?> editor, String[] labels, String[] contents) {
		final OperationBuilder builder = new OperationBuilder();
		final AtomicOperation cmd = new AtomicOperation(builder
				.createInvariant(editor, labels, contents));
		cmd.addContext(getContext(editor));
		return cmd;
	}

	public static AtomicOperation createCarrierSetWizard(
			IEventBEditor<?> editor, String identifier) {
		final OperationBuilder builder = new OperationBuilder();
		final AtomicOperation cmd = new AtomicOperation(builder
				.createCarrierSet(editor, identifier));
		cmd.addContext(getContext(editor));
		return cmd;
	}

	public static AtomicOperation createCarrierSetWizard(
			IEventBEditor<?> editor, String[] identifier) {
		final OperationBuilder builder = new OperationBuilder();
		final AtomicOperation cmd = new AtomicOperation(builder
				.createCarrierSet(editor, identifier));
		cmd.addContext(getContext(editor));
		return cmd;
	}

	public static <T extends IInternalElement> AtomicOperation createElementGeneric(
			IEventBEditor<?> editor, IInternalParent parent,
			final IInternalElementType<T> type, final IInternalElement sibling) {
		final OperationBuilder builder = new OperationBuilder();
		final AtomicOperation cmd = new AtomicOperation(builder
				.createDefaultElement(editor, parent, type, sibling));
		cmd.addContext(getContext(editor));
		return cmd;
	}

	/**
	 * 
	 * grNames and grdPredicates must be not null and have the same length
	 * <p>
	 * varNames and varSubstitutions must be not null and have the same length
	 * 
	 * @param editor
	 *            editor of parent file
	 * @param name
	 *            name of the element
	 * @param varNames
	 *            variables name
	 * @param grdNames
	 *            guards name
	 * @param grdPredicates
	 *            guards predicate
	 * @param actNames
	 *            actions name
	 * @param actSubstitutions
	 *            actions substitution
	 */
	public static AtomicOperation createEvent(IEventBEditor<?> editor,
			String name, String[] varNames, String[] grdNames,
			String[] grdPredicates, String[] actNames, String[] actSubstitutions) {
		final OperationBuilder builder = new OperationBuilder();
		final AtomicOperation cmd = new AtomicOperation(builder.createEvent(
				editor, name, varNames, grdNames, grdPredicates, actNames,
				actSubstitutions));
		cmd.addContext(getContext(editor));
		return cmd;
	}

	public static AtomicOperation deleteElement(IInternalElement element) {
		final OperationBuilder builder = new OperationBuilder();
		final AtomicOperation cmd = new AtomicOperation(builder.deleteElement(
				element, true));
		cmd.addContext(getContext(element.getRodinFile()));
		return cmd;
	}

	public static AtomicOperation deleteElement(IInternalElement[] elements,
			boolean force) {
		assert elements != null;
		final OperationBuilder builder = new OperationBuilder();
		final AtomicOperation cmd = new AtomicOperation(builder.deleteElement(
				elements, force));
		if (elements.length > 0) {
			cmd.addContext(getContext(elements[0].getRodinFile()));
		}
		return cmd;
	}

	public static IUndoContext getContext(IEventBEditor<?> editor) {
		return getContext(editor.getRodinInput().getRodinFile());
	}

	public static IUndoContext getContext(IRodinFile rodinFile) {
		final String bareName = rodinFile.getBareName();

		return new IUndoContext() {

			public String getLabel() {
				return bareName;
			}

			public boolean matches(IUndoContext context) {
				return getLabel().equals(context.getLabel());
			}

		};
	}

	public static IUndoContext getContext(IInternalElement root) {
		return getContext(root.getRodinFile());
	}

	/**
	 * Change the attribute of a element with a factory
	 * 
	 * @param value
	 *            if value is null, the attribute is removed. Else it is changed
	 */
	public static <E extends IAttributedElement> AtomicOperation changeAttribute(
			IRodinFile file, IAttributeFactory<E> factory, E element,
			String value) {
		final OperationBuilder builder = new OperationBuilder();
		final AtomicOperation cmd = new AtomicOperation(builder
				.changeAttribute(factory, element, value));
		cmd.addContext(getContext(file));
		return cmd;
	}

	public static AtomicOperation renameElements(IRodinFile file,
			IInternalElementType<?> type, IAttributeFactory factory,
			String prefix) {
		final OperationBuilder builder = new OperationBuilder();
		final AtomicOperation op = new AtomicOperation(builder.renameElement(
				file, type, factory, prefix));
		op.addContext(getContext(file));
		return op;
	}

	public static AtomicOperation createGuard(IEventBEditor<?> editor,
			IInternalElement event, String label, String predicate,
			IInternalElement sibling) {
		final OperationBuilder builder = new OperationBuilder();
		final AtomicOperation cmd = new AtomicOperation(builder.createGuard(
				editor, event, label, predicate, sibling));
		cmd.addContext(getContext(editor));
		return cmd;
	}

	public static AtomicOperation createAction(IEventBEditor<?> editor,
			IInternalElement event, String label, String assignement,
			IInternalElement sibling) {
		final OperationBuilder builder = new OperationBuilder();
		final AtomicOperation cmd = new AtomicOperation(builder.createAction(
				editor, event, label, assignement, sibling));
		cmd.addContext(getContext(editor));
		return cmd;
	}

	public static AtomicOperation createAction(IEventBEditor<?> editor,
			IInternalElement event, String label[], String predicate[],
			IInternalElement sibling) {
		final OperationBuilder builder = new OperationBuilder();
		final AtomicOperation cmd = new AtomicOperation(builder.createAction(
				editor, event, label, predicate, sibling));
		cmd.addContext(getContext(editor));
		return cmd;
	}

	/**
	 * If all the element into parent
	 */
	public static AtomicOperation copyElements(IInternalElement parent,
			IRodinElement[] elements) {
		final OperationBuilder builder = new OperationBuilder();
		final AtomicOperation cmd = new AtomicOperation(builder.copyElements(
				parent, elements));
		cmd.addContext(getContext(parent));
		return cmd;
	}

	public static <T extends IInternalElement> AtomicOperation createElement(
			IEventBEditor<?> editor,
			IInternalElementType<T> internalElementType,
			IAttributeType.String attribute, String value) {
		final OperationBuilder builder = new OperationBuilder();
		final AtomicOperation op = new AtomicOperation(builder
				.createElementOneStringAttribute(editor,
						editor.getRodinInput(), internalElementType, null,
						attribute, value));
		op.addContext(getContext(editor));
		return op;
	}

	public static AtomicOperation handle(IEventBEditor<?> editor,
			TreeViewer viewer, boolean up) {
		final OperationBuilder builder = new OperationBuilder();
		final AtomicOperation op = new AtomicOperation(builder.handle(viewer,
				up));
		op.addContext(getContext(editor));
		return op;
	}

	public static AtomicOperation move(IEventBEditor<?> editor, boolean up,
			IInternalParent parent, IInternalElementType<?> type,
			IInternalElement firstElement, IInternalElement lastElement) {
		final OperationBuilder builder = new OperationBuilder();
		final OperationTree op = builder.move(up, parent, type, firstElement,
				lastElement);
		if (op != null) {
			final AtomicOperation atomOp = new AtomicOperation(op);
			atomOp.addContext(getContext(editor));
			return atomOp;
		} else {
			return null;
		}
	}
}

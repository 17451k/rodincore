/*******************************************************************************
 * Copyright (c) 2007, 2010 ETH Zurich and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     ETH Zurich - initial API and implementation
 *     Systerel - used EventBSharedColor
 *     Systerel - separation of file and root element
 *     Systerel - used ElementDescRegistry
 *     Systerel - optimized tree traversal
 *     Systerel - fixed expanding
 *     Systerel - refactored using IElementRelationship
 *******************************************************************************/
package org.eventb.internal.ui.eventbeditor.editpage;

import static org.eventb.internal.ui.preferences.EventBPreferenceStore.getBooleanPreference;
import static org.eventb.internal.ui.preferences.PreferenceConstants.P_EXPAND_SECTIONS;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.ScrolledForm;
import org.eventb.internal.ui.EventBSharedColor;
import org.eventb.internal.ui.eventbeditor.EventBEditorUtils;
import org.eventb.internal.ui.eventbeditor.elementdesc.ElementDescRegistry;
import org.eventb.internal.ui.eventbeditor.elementdesc.IElementRelationship;
import org.eventb.ui.eventbeditor.IEventBEditor;
import org.rodinp.core.IAttributeType;
import org.rodinp.core.IElementType;
import org.rodinp.core.IInternalElement;
import org.rodinp.core.IRodinElement;

public class ElementComposite implements IElementComposite {

	public static ElementDescRegistry registry = ElementDescRegistry.getInstance();
	
	private final FormToolkit toolkit;

	private final ScrolledForm form;

	private final Composite compParent;

	private final IRodinElement rElement;

	private final EditPage page;
	
	private final int level;

	private EditRow row;

	private Composite composite;

	private Composite mainSectionComposite;

	// The next two variables maintain a link to the sections embedded in this
	// composite. The list gives the order of the sections, while the map allows
	// direct access to a section, based on the type of the elements it
	// contains.
	private ArrayList<ISectionComposite> sectionComps;
	private Map<IElementType<?>, ISectionComposite> mapComps;

	private boolean isExpanded;

	public ElementComposite(EditPage page, FormToolkit toolkit,
			ScrolledForm form, Composite compParent, IRodinElement element,
			int level) {
		this.page = page;
		this.toolkit = toolkit;
		this.form = form;
		this.compParent = compParent;
		this.rElement = element;
		this.level = level;
		createContents();
	}

	private void createContents() {
		composite = toolkit.createComposite(compParent);
		if (EventBEditorUtils.DEBUG) {
			composite.setBackground(EventBSharedColor.getSystemColor(
					SWT.COLOR_GRAY));
		}
		composite.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		final GridLayout gridLayout = new GridLayout();
		gridLayout.marginWidth = 0;
		gridLayout.marginHeight = 0;
		gridLayout.verticalSpacing = 0;
		composite.setLayout(gridLayout);

		row = new EditRow(this, form, toolkit);
		row.createContents((IEventBEditor<?>) page.getEditor(), toolkit,
				composite, null, level);

		mainSectionComposite = toolkit.createComposite(composite);
		mainSectionComposite.setLayoutData(new GridData(
				GridData.FILL_HORIZONTAL));
		mainSectionComposite.setLayout(gridLayout);

		setExpand(false, false);
	}

	@Override
	public void folding() {
		setExpand(!isExpanded, false);
		form.reflow(true);
	}

	@Override
	public void setExpand(boolean isExpanded, boolean recursive) {
		long beforeTime = 0;
		if (EventBEditorUtils.DEBUG)
			beforeTime = System.currentTimeMillis();
		this.isExpanded = isExpanded;
		if (isExpanded) {
			if (sectionComps == null) {
				createSectionComposites(false);
			}
			final GridData gridData = (GridData) mainSectionComposite.getLayoutData();
			if (sectionComps.size() == 0) {
				gridData.heightHint = 0;
			} else {
				gridData.heightHint = SWT.DEFAULT;
			}
			expandSections(recursive);
		} else {
			final GridData gridData = (GridData) mainSectionComposite.getLayoutData();
			gridData.heightHint = 0;
			// collapse is always recursive
			expandSections(true);
		}
		row.updateExpandStatus();
		if (EventBEditorUtils.DEBUG) {
			long afterTime = System.currentTimeMillis();
			EventBEditorUtils.debug("Duration: " + (afterTime - beforeTime)
					+ " ms");
		}
	}

	private void expandSections(boolean recursive) {
		if (sectionComps == null) {
			return;
		}
		if (recursive || getBooleanPreference(P_EXPAND_SECTIONS)) {
			for (final ISectionComposite sectionComp : sectionComps) {
				sectionComp.setExpandNoReflow(isExpanded, recursive);
			}
		}
	}

	protected void createSectionComposites(boolean reflow) {
		final IElementRelationship[] rels = registry
				.getChildRelationships(rElement.getElementType());
		sectionComps = new ArrayList<ISectionComposite>(rels.length);
		mapComps = new HashMap<IElementType<?>, ISectionComposite>();
		for (IElementRelationship rel : rels) {
			// Create the section composite
			final ISectionComposite comp = new SectionComposite(page, toolkit,
					form, mainSectionComposite, (IInternalElement) rElement,
					rel, level + 1);
			sectionComps.add(comp);
			mapComps.put(rel.getChildType(), comp);
		}
		if (reflow) {
			form.reflow(true);
		}
	}

	@Override
	public EditPage getPage() {
		return page;
	}

	@Override
	public void refresh(IRodinElement element) {
		if (!rElement.exists())
			return;
		if (rElement.equals(element)) {
			row.refresh();
			if (sectionComps == null)
				return;

			// Refresh sub section composite as well?
			final IElementRelationship[] rels = registry
					.getChildRelationships(element.getElementType());

			boolean recreate = false;
			if (rels.length != sectionComps.size()) {
				recreate = true;
			} else {
				for (int i = 0; i < rels.length; ++i) {
					if (sectionComps.get(i).getElementType() != rels[i]
							.getChildType()) {
						recreate = true;
						break;
					}
				}
			}
			if (recreate) {
				for (ISectionComposite sectionComp : sectionComps) {
					sectionComp.dispose();
				}
				createSectionComposites(true);
			}
		} else {
			final ISectionComposite comp = getCompositeTowards(element);
			if (comp != null)
				comp.refresh(element);
		}
	}

	@Override
	public void elementRemoved(IRodinElement element) {
		if (!rElement.exists())
			return;
		assert (!rElement.equals(element));
		final ISectionComposite comp = getCompositeTowards(element);
		if (comp != null)
			comp.elementRemoved(element);
	}

	@Override
	public void elementAdded(IRodinElement element) {
		if (!rElement.exists())
			return;
		final ISectionComposite comp = getCompositeTowards(element);
		if (comp != null)
			comp.elementAdded(element);
	}

	@Override
	public void dispose() {
		composite.dispose();
	}

	@Override
	public IRodinElement getElement() {
		return rElement;
	}

	@Override
	public boolean isExpanded() {
		return isExpanded;
	}

	@Override
	public void childrenChanged(IRodinElement element,
			IElementType<?> childrenType) {
		if (!rElement.exists())
			return;

		// Only continue if the children section composites already exists
		if (sectionComps == null)
			return;
		
		if (rElement.equals(element)) {
			final ISectionComposite comp = mapComps.get(childrenType);
			if (comp != null) {
				comp.childrenChanged(element, childrenType);
			}

			row.updateLinks();
		} else {
			final ISectionComposite comp = getCompositeTowards(element);
			if (comp != null) {
				comp.childrenChanged(element, childrenType);
			}			
		}
	}

	@Override
	public Composite getComposite() {
		return composite;
	}

	@Override
	public boolean select(IRodinElement element, boolean selected) {
		if (!rElement.exists())
			return false;

		if (rElement.equals(element)) {
			row.setSelected(selected);
			return true;
		}

		final IRodinElement child = getChildTowards(element);
		if (child == null)
			return false;

		if (selected)
			setExpand(true, false);
		final ISectionComposite comp = getComposite(child);
		if (comp == null)
			return false;

		return comp.select(element, selected);
	}

	@Override
	public void recursiveExpand(IRodinElement element) {
		if (!rElement.exists())
			return;

		if (element.equals(rElement) || element.isAncestorOf(rElement)) {
			setExpand(true, true);
		} else {
			final IRodinElement child = getChildTowards(element);
			if (child == null)
				return;

			setExpand(true, false);
			final ISectionComposite comp = getComposite(child);
			if (comp != null) {
				comp.recursiveExpand(element);
			}
		}
	}

	@Override
	public void edit(IInternalElement element, IAttributeType attributeType,
			int charStart, int charEnd) {
		if (!rElement.exists())
			return;

		if (rElement.equals(element)) {
			row.edit(attributeType, charStart, charEnd);
		}

		final IRodinElement child = getChildTowards(element);
		if (child == null)
			return;

		if (!isExpanded())
			setExpand(true, false);
		final ISectionComposite comp = getComposite(child);
		if (comp != null) {
			comp.edit(element, attributeType, charStart, charEnd);
		}
	}

	@Override
	public void refresh(IRodinElement element, Set<IAttributeType> set) {
		if (!rElement.exists())
			return;
		if (element.equals(rElement)) {
			row.refresh(set);
			if (sectionComps == null)
				return;

			// Refresh sub section composite as well?
			final IElementRelationship[] rels = registry
					.getChildRelationships(element.getElementType());

			boolean recreate = false;
			if (rels.length != sectionComps.size()) {
				recreate = true;
			} else {
				for (int i = 0; i < rels.length; ++i) {
					if (sectionComps.get(i).getElementType() != rels[i]
							.getChildType()) {
						recreate = true;
						break;
					}
				}
			}

			if (recreate) {
				for (ISectionComposite sectionComp : sectionComps) {
					sectionComp.dispose();
				}
				createSectionComposites(true);
			}
		} else {
			row.updateLinks();
			if (mapComps == null)
				return;
			final ISectionComposite comp = getCompositeTowards(element);
			if (comp != null) {
				page.addToRefreshPrefixMarker(comp);
				comp.refresh(element, set);
			}
		}
	}

	protected IRodinElement getChildTowards(IRodinElement element) {
		return EventBEditorUtils.getChildTowards(rElement, element);
	}
	
	protected ISectionComposite getCompositeTowards(IRodinElement element) {
		final IRodinElement child = getChildTowards(element);
		return getComposite(child);
	}

	protected ISectionComposite getComposite(IRodinElement element) {
		if (element == null || mapComps == null)
			return null;
		final IElementType<?> type = element.getElementType();
		return mapComps.get(type);
	}

	@Override
	public ElementDescRegistry getElemDescRegistry() {
		return registry;
	}
	
}

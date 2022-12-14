/*******************************************************************************
 * Copyright (c) 2006, 2012 ETH Zurich and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     ETH Zurich - initial API and implementation
 *******************************************************************************/
package org.eventb.internal.ui;

import java.util.HashSet;
import java.util.Set;

import org.eclipse.core.resources.IMarkerDelta;
import org.eclipse.core.resources.IResourceChangeEvent;
import org.eclipse.jface.viewers.TableViewer;
import org.rodinp.core.IRodinElement;
import org.rodinp.core.RodinMarkerUtil;

/**
 * @author htson
 *         <p>
 *         This class extends
 *         <code>org.eclipse.jface.viewers.LabelProvider</code> and provides
 *         labels for different elements appeared in the UI
 */
public class RodinElementTableLabelProvider extends
		RodinElementStructuredLabelProvider {

	public RodinElementTableLabelProvider(TableViewer viewer) {
		super(viewer);
	}

	@Override
	protected Set<Object> getRefreshElements(IResourceChangeEvent event) {
		IMarkerDelta[] rodinProblemMakerDeltas = event.findMarkerDeltas(
				RodinMarkerUtil.RODIN_PROBLEM_MARKER, true);

		final Set<Object> elements = new HashSet<Object>();
		for (IMarkerDelta delta : rodinProblemMakerDeltas) {
			IRodinElement element = RodinMarkerUtil.getElement(delta);
			if (element != null && !elements.contains(element)) {
				elements.add(element);
				element = element.getParent();
				while (element != null) {
					elements.add(element);
					element = element.getParent();
				}
			}
		}
		return elements;
	}
		
}
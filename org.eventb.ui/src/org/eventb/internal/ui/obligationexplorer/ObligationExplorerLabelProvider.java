/*******************************************************************************
 * Copyright (c) 2007, 2008 ETH Zurich and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     ETH Zurich - initial API and implementation
 *     Systerel - Added a constant for the user support manager
 *     Systerel - used EventBSharedColor
 *     Systerel - separation of file and root element
 ******************************************************************************/
package org.eventb.internal.ui.obligationexplorer;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import org.eclipse.core.resources.IMarkerDelta;
import org.eclipse.core.resources.IResourceChangeEvent;
import org.eclipse.core.resources.IResourceChangeListener;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.jface.resource.ImageRegistry;
import org.eclipse.jface.resource.JFaceResources;
import org.eclipse.jface.util.IPropertyChangeListener;
import org.eclipse.jface.util.PropertyChangeEvent;
import org.eclipse.jface.viewers.IColorProvider;
import org.eclipse.jface.viewers.IFontProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.LabelProviderChangedEvent;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Display;
import org.eventb.core.EventBPlugin;
import org.eventb.core.IContextRoot;
import org.eventb.core.IMachineRoot;
import org.eventb.core.IPSRoot;
import org.eventb.core.IPSStatus;
import org.eventb.core.pm.IProofState;
import org.eventb.core.pm.IUserSupport;
import org.eventb.core.pm.IUserSupportManager;
import org.eventb.core.seqprover.IConfidence;
import org.eventb.core.seqprover.IProofTree;
import org.eventb.eventBKeyboard.preferences.PreferenceConstants;
import org.eventb.internal.ui.EventBImage;
import org.eventb.internal.ui.EventBSharedColor;
import org.eventb.ui.EventBUIPlugin;
import org.eventb.ui.IEventBSharedImages;
import org.rodinp.core.IOpenable;
import org.rodinp.core.IRodinElement;
import org.rodinp.core.IRodinFile;
import org.rodinp.core.IRodinProject;
import org.rodinp.core.RodinDBException;
import org.rodinp.core.RodinMarkerUtil;

/**
 * @author htson
 *         <p>
 *         This class provides the label for object in the tree.
 */

public class ObligationExplorerLabelProvider extends LabelProvider implements
		IFontProvider, IColorProvider, IPropertyChangeListener,
		IResourceChangeListener {

	private static final IUserSupportManager USM = EventBPlugin
			.getUserSupportManager();

	TreeViewer viewer;
	
	private final Color yellow = EventBSharedColor.getSystemColor(
			SWT.COLOR_YELLOW);

	public ObligationExplorerLabelProvider(TreeViewer viewer) {
		this.viewer = viewer;
		JFaceResources.getFontRegistry().addListener(this);
		IWorkspace workspace = ResourcesPlugin.getWorkspace();
		workspace.addResourceChangeListener(this,
				IResourceChangeEvent.POST_BUILD
						| IResourceChangeEvent.POST_CHANGE);
	}

	@Override
	public Image getImage(Object element) {
		ImageRegistry registry = EventBUIPlugin.getDefault().getImageRegistry();
		if (element instanceof IPSStatus) {
			IPSStatus status = (IPSStatus) element;
			try {

				// Try to synchronize with the proof tree in memory
				Collection<IUserSupport> userSupports = USM.getUserSupports();
				for (IUserSupport userSupport : userSupports) {
					// UIUtils.debugObligationExplorer("Get US: "
					// + userSupport);
					IProofState[] proofStates = userSupport.getPOs();
					for (IProofState proofState : proofStates) {
						if (proofState.getPSStatus().equals(element)) {
							IProofTree tree = proofState.getProofTree();

							if (tree != null && proofState.isDirty()) {
								int confidence = tree.getConfidence();

								final boolean proofBroken = status.isBroken();
								if (confidence <= IConfidence.PENDING) {
									if (false && proofBroken)
										return registry
												.get(IEventBSharedImages.IMG_PENDING_BROKEN);
									else
										return registry
												.get(IEventBSharedImages.IMG_PENDING);
								}
								if (confidence <= IConfidence.REVIEWED_MAX) {
									if (false && proofBroken)
										return registry
												.get(IEventBSharedImages.IMG_REVIEWED_BROKEN);
									else
										return registry
												.get(IEventBSharedImages.IMG_REVIEWED);
								}
								if (confidence <= IConfidence.DISCHARGED_MAX) {
									if (false && proofBroken)
										return registry
												.get(IEventBSharedImages.IMG_DISCHARGED_BROKEN);
									else
										return registry
												.get(IEventBSharedImages.IMG_DISCHARGED);
								}
								return registry
										.get(IEventBSharedImages.IMG_DEFAULT);
							}
						}
					}
				}

				// Otherwise, setting the label accordingly.
				return EventBImage.getPRSequentImage(status);
			} catch (RodinDBException e) {
				e.printStackTrace();
			}
		}
		if (element instanceof IRodinElement)
			return EventBImage.getRodinImage((IRodinElement) element);
		return null;
	}

	@Override
	public String getText(Object obj) {
		// if (ObligationExplorerUtils.DEBUG)
		// ObligationExplorerUtils.debug("Label for: " + obj);
		if (obj instanceof IRodinProject) {
			if (ObligationExplorerUtils.DEBUG)
				ObligationExplorerUtils.debug("Project: "
						+ ((IRodinProject) obj).getElementName());
			return ((IRodinProject) obj).getElementName();
		} else if (obj instanceof IRodinFile) {
			IRodinFile rf = (IRodinFile) obj;
			return getText(rf.getRoot());
		} else if (obj instanceof IMachineRoot) {
			IPSRoot psRoot = ((IMachineRoot) obj).getPSRoot();
			String bareName = psRoot.getRodinFile().getBareName();
			ProofStatus proofStatus = new ProofStatus(psRoot, false);
			return bareName + proofStatus;
		} else if (obj instanceof IContextRoot) {
			IPSRoot psRoot = ((IContextRoot) obj).getPSRoot();
			String bareName = psRoot.getRodinFile().getBareName();
			ProofStatus proofStatus = new ProofStatus(psRoot, false);
			return bareName + proofStatus;
		} else if (obj instanceof IPSStatus) {
			final IPSStatus psStatus = (IPSStatus) obj;
			final String poName = psStatus.getElementName();

			// Find the label in the list of UserSupport.
			Collection<IUserSupport> userSupports = USM.getUserSupports();
			for (IUserSupport userSupport : userSupports) {
				// UIUtils.debugObligationExplorer("Get US: " +
				// userSupport);
				IProofState[] proofStates = userSupport.getPOs();
				for (IProofState proofState : proofStates) {
					if (proofState.getPSStatus().equals(psStatus)) {
						if (proofState.isDirty())
							return "* " + poName;
						else
							return poName;
					}
				}
			}
			return poName;
		}

		return obj.toString();
	}

	@Override
	public void dispose() {
		JFaceResources.getFontRegistry().removeListener(this);
		ResourcesPlugin.getWorkspace().removeResourceChangeListener(this);
		super.dispose();
	}

	public Font getFont(Object element) {
		return JFaceResources.getFont(PreferenceConstants.EVENTB_MATH_FONT);
	}

	public Color getForeground(Object element) {
		return null;
	}

	public Color getBackground(Object element) {
		if (element instanceof IPSStatus) {
			Collection<IUserSupport> userSupports = USM.getUserSupports();
			for (IUserSupport userSupport : userSupports) {
				IProofState[] proofStates = userSupport.getPOs();
				for (IProofState proofState : proofStates) {
					if (proofState.getPSStatus().equals(element)) {
						if (proofState.isDirty())
							return yellow;
						else
							return null;
					}
				}
			}
		}
		return null;
	}

	// If the font changed, all labels should be refreshed
	public void propertyChange(PropertyChangeEvent event) {
		final String property = event.getProperty();
		if (property.equals(PreferenceConstants.EVENTB_MATH_FONT)) {
			fireLabelProviderChanged(new LabelProviderChangedEvent(this));
		}
	}

	protected Set<Object> getRefreshElements(IResourceChangeEvent event) {
		IMarkerDelta[] rodinProblemMakerDeltas = event.findMarkerDeltas(
				RodinMarkerUtil.RODIN_PROBLEM_MARKER, true);
		final Set<Object> elements = new HashSet<Object>();
		for (IMarkerDelta delta : rodinProblemMakerDeltas) {
			IRodinElement element = RodinMarkerUtil.getElement(delta);
			IOpenable openable = element.getOpenable();
			if(openable instanceof IRodinFile){
				elements.add(openable);
				elements.add(((IRodinFile)openable).getRodinProject());
			}
//			if (openable instanceof IMachineFile) {
//				elements.add(openable);
//				elements.add(((IMachineFile) openable).getRodinProject());
//			} else if (openable instanceof IMachineFile) {
//				elements.add(openable);
//				elements.add(((IMachineFile) openable).getRodinProject());
//			}
		}
		return elements;
	}

	public void resourceChanged(IResourceChangeEvent event) {
		final Set<Object> elements = getRefreshElements(event);

		if (elements.size() != 0) {
			final String[] properties = new String[] { RodinMarkerUtil.RODIN_PROBLEM_MARKER };
			Display display = viewer.getControl().getDisplay();
			display.syncExec(new Runnable() {

				public void run() {
					for (Object element : elements) {
						viewer.update(element, properties);
					}
				}

			});
		}
		
	}

	@Override
	public boolean isLabelProperty(Object element, String property) {
		if (property.equals("content"))
			return true;
		if (property.equals(RodinMarkerUtil.RODIN_PROBLEM_MARKER))
			return true;
		return super.isLabelProperty(element, property);
	}

}

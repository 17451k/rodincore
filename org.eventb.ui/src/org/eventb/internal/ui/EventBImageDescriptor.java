/*******************************************************************************
 * Copyright (c) 2005 ETH Zurich.
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Rodin @ ETH Zurich
 ******************************************************************************/

package org.eventb.internal.ui;

import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.swt.graphics.ImageData;
import org.eventb.ui.EventBUIPlugin;

/**
 * @author htson
 *         <p>
 *         Class for Images' descriptors that are used in the Event-B UI.
 */
public class EventBImageDescriptor extends ImageDescriptor {

	// Store the image data
	private ImageData imageData;

	/**
	 * Constructor: Create the object with the given Image Id
	 * <p>
	 * 
	 * @param string
	 *            the Image Id
	 */
	public EventBImageDescriptor(String string) {
		imageData = EventBUIPlugin.getDefault().getImageRegistry().get(string)
				.getImageData();
	}

	/**
	 * Getting the image data of the object
	 * <p>
	 * 
	 * @return the image data stored in the object
	 */
	public ImageData getImageData() {
		return imageData;
	}

}

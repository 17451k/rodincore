/*******************************************************************************
 * Copyright (c) 2007, 2012 ETH Zurich and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     ETH Zurich - initial API and implementation
 *******************************************************************************/
package org.eventb.internal.ui.goal;

/**
 * @author htson
 */
public class GoalUtils {

	public static boolean DEBUG = false;

	public final static String DEBUG_PREFIX = "*** Goal *** ";

	public static void debug(String message) {
		System.out.println(DEBUG_PREFIX + message);
	}

}

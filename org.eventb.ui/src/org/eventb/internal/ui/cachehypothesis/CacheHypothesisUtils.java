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

package org.eventb.internal.ui.cachehypothesis;

import java.util.StringTokenizer;

/**
 * @author htson
 *         <p>
 *         This is an utility class for supporting the Cached Hypothesis View.
 */
public class CacheHypothesisUtils {

	public static boolean DEBUG = false;

	public final static String DEBUG_PREFIX = "*** Cache Hypothesis *** "; // $NON-NLS-1$

	/**
	 * Print the debug message with the prefix for cache hypothesis.
	 * 
	 * @param message
	 *            the debug message
	 */
	public static void debug(String message) {
		StringTokenizer tokenizer = new StringTokenizer(message, "\n");
		while (tokenizer.hasMoreTokens()) {
			System.out.println(DEBUG_PREFIX + tokenizer.nextToken());
		}
	}

}

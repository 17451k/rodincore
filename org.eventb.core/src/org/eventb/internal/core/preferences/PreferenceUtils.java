/*******************************************************************************
 * Copyright (c) 2010, 2011 Systerel and others.
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 * 
 * Contributors:
 *     Systerel - Initial API and implementation
 *******************************************************************************/
package org.eventb.internal.core.preferences;

import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

import org.eventb.core.seqprover.IAutoTacticRegistry;
import org.eventb.core.seqprover.ICombinatorDescriptor;
import org.eventb.core.seqprover.SequentProver;
import org.eventb.core.seqprover.IAutoTacticRegistry.ITacticDescriptor;
import org.eventb.core.seqprover.eventbExtensions.AutoTactics;

/**
 * Utility class for preferences using.
 */
public class PreferenceUtils {

	/**
	 * The debug flag. This is set by the option when the platform is launched.
	 * Client should not try to reset this flag.
	 */
	public static boolean DEBUG = false;
	
	public static class PreferenceException extends RuntimeException {

		private static final long serialVersionUID = -4388540765121161963L;
		
		private static final PreferenceException INSTANCE = new PreferenceException();
		
		private PreferenceException() {
			// singleton
		}
		
		public static PreferenceException getInstance() {
			return INSTANCE;
		}
	}
	
	/**
	 * Returns a string representation of a list of input objects. The objects
	 * are separated by a given character.
	 * 
	 * @param objects
	 *            a list of objects
	 * @param separator
	 *            the character to use to separate the objects
	 * @return the string representation of input objects
	 */
	public static <T> String flatten(List<T> objects, String separator) {
		final StringBuffer buffer = new StringBuffer();
		boolean first = true;
		for (T item : objects) {
			if (first) {
				first = false;
			} else {
				buffer.append(separator);
			}
			buffer.append(item);
		}
		return buffer.toString();
	}

	/**
	 * Parse a character separated string to a list of string.
	 * 
	 * @param stringList
	 *            the comma separated string.
	 * @param c
	 *            the character separates the string
	 * @return an array of strings that make up the character separated input
	 *         string.
	 */
	public static String[] parseString(String stringList, String c) {
		StringTokenizer st = new StringTokenizer(stringList, c);//$NON-NLS-1$
		ArrayList<String> result = new ArrayList<String>();
		while (st.hasMoreElements()) {
			result.add((String) st.nextElement());
		}
		return result.toArray(new String[result.size()]);
	}

	// for compatibility
	public static ITacticDescriptor loopOnAllPending(List<ITacticDescriptor> descs, String id) {
		final IAutoTacticRegistry reg = SequentProver.getAutoTacticRegistry();
		final ICombinatorDescriptor comb = reg
				.getCombinatorDescriptor(AutoTactics.LoopOnAllPending.COMBINATOR_ID);
		return comb.instantiate(descs, id);
	}

}

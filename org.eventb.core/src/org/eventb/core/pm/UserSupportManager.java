/*******************************************************************************
 * Copyright (c) 2005-2006 ETH Zurich.
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Rodin @ ETH Zurich
 ******************************************************************************/

package org.eventb.core.pm;

import java.util.ArrayList;
import java.util.Collection;

import org.eclipse.core.runtime.ISafeRunnable;
import org.eclipse.core.runtime.Platform;
import org.eventb.core.IPRFile;
import org.rodinp.core.RodinDBException;

public class UserSupportManager {

	private static Collection<IUSManagerListener> listeners = new ArrayList<IUSManagerListener>();

	private static Collection<UserSupport> userSupports = new ArrayList<UserSupport>();

	public static int REMOVED = 0x1;

	public static int ADDED = 0x2;

	public static int CHANGED = 0x4;

	public static UserSupport newUserSupport() {
		UserSupport userSupport = new UserSupport();
		userSupports.add(userSupport);
		notifyUSManagerListener(userSupport, ADDED);
		return userSupport;
	}

	public static void disposeUserSupport(UserSupport userSupport) {
		userSupport.dispose();
		synchronized (userSupports) {
			if (userSupports.contains(userSupport))
				userSupports.remove(userSupport);
		}

		notifyUSManagerListener(userSupport, REMOVED);
	}

	public static Collection<UserSupport> getUserSupports() {
		return userSupports;
	}

	public static void addUSManagerListener(IUSManagerListener listener) {
		synchronized (listeners) {
			if (!listeners.contains(listener))
				listeners.add(listener);
		}

	}

	public static void removeUSManagerListener(IUSManagerListener listener) {
		synchronized (listeners) {
			if (listeners.contains(listener))
				listeners.remove(listener);
		}
	}

	private static void notifyUSManagerListener(final UserSupport userSupport,
			final int status) {
		IUSManagerListener[] safeCopy;
		synchronized (listeners) {
			safeCopy = listeners.toArray(new IUSManagerListener[listeners
					.size()]);
		}
		for (final IUSManagerListener listener : safeCopy) {
			Platform.run(new ISafeRunnable() {
				public void handleException(Throwable exception) {
					// do nothing, will be logged by the platform
				}

				public void run() throws Exception {
					listener.USManagerChanged(userSupport, status);
				}
			});
		}

	}

	public static void setInput(UserSupport userSupport, IPRFile prFile)
			throws RodinDBException {
		userSupport.setInput(prFile);
		notifyUSManagerListener(userSupport, CHANGED);
	}
}

/*******************************************************************************
 * Copyright (c) 2012 Systerel and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     Systerel - initial API and implementation
 *******************************************************************************/
package org.eventb.internal.ui.prover.registry;

import static org.eventb.internal.ui.UIUtils.log;

import org.eclipse.swt.graphics.Point;
import org.eventb.core.ast.Predicate;
import org.eventb.ui.prover.IPositionApplication;
import org.eventb.ui.prover.ITacticApplication;

/**
 * Internal implementation of {@link IPositionApplication} that encapsulates the
 * implementations provided by clients. The purpose of this class is to act as a
 * firewall with respect to clients. Moreover, it also implements the logic of
 * reverting to the tactic provider when the application returns
 * <code>null</code>.
 * 
 * @author Laurent Voisin
 */
public class PositionApplicationProxy extends
		TacticApplicationProxy<IPositionApplication> {

	/**
	 * Factory for creating position application proxys.
	 */
	public static class PositionApplicationFactory extends
			TacticApplicationFactory<PositionApplicationProxy> {

		@Override
		public PositionApplicationProxy create(TacticProviderInfo provider,
				ITacticApplication application) {
			if (application instanceof IPositionApplication) {
				return new PositionApplicationProxy(provider,
						(IPositionApplication) application);
			}
			return null;
		}

	}

	protected PositionApplicationProxy(TacticProviderInfo provider,
			IPositionApplication client) {
		super(provider, client);
	}

	public Point getHyperlinkBounds(String image, Predicate parsedPred) {
		final Point result;
		try {
			result = client.getHyperlinkBounds(image, parsedPred);
		} catch (Throwable exc) {
			log(exc, "when calling getHyperlinkBounds() for " + getTacticID());
			return null;
		}
		if (result == null) {
			log(null, "Null returned by getHyperlinkBounds() for tactic "
					+ getTacticID());
			return null;
		}
		if (!checkRange(result, image)) {
			log(null, "Invalid hyperlink bounds (" + result + ") for tactic "
					+ getTacticID());
			return null;
		}
		return result;
	}

	private static boolean checkRange(Point pt, String string) {
		return 0 <= pt.x && pt.x < pt.y && pt.y <= string.length();
	}

	public String getHyperlinkLabel() {
		final String clientResult = getHyperlinkLabelFromClient();
		if (clientResult != null) {
			return clientResult;
		}
		return provider.getTooltip();
	}

	private String getHyperlinkLabelFromClient() {
		try {
			return client.getHyperlinkLabel();
		} catch (Throwable exc) {
			log(exc, "when calling getHyperlinkLabel() for " + getTacticID());
			return null;
		}
	}

}

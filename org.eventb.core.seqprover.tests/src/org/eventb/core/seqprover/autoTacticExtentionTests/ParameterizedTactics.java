/*******************************************************************************
 * Copyright (c) 2011, 2013 Systerel and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Systerel - initial API and implementation
 *******************************************************************************/
package org.eventb.core.seqprover.autoTacticExtentionTests;

import static org.junit.Assert.assertEquals;

import java.util.ConcurrentModificationException;

import org.eventb.core.seqprover.IParameterSetting;
import org.eventb.core.seqprover.IParameterValuation;
import org.eventb.core.seqprover.IProofMonitor;
import org.eventb.core.seqprover.IProofTreeNode;
import org.eventb.core.seqprover.ITactic;
import org.eventb.core.seqprover.ITacticParameterizer;
import org.eventb.core.seqprover.tactics.BasicTactics;

/**
 * @author Nicolas Beauger
 *
 */
public class ParameterizedTactics {
	
	public static class FakeTactic implements ITactic {
	
		private final boolean bool1;
		private final boolean bool2;
		private final int int1;
		private final long long1;
		private final String string;
	
		
		public FakeTactic(boolean bool1, boolean bool2, int int1, long long1,
				String string) {
			super();
			this.bool1 = bool1;
			this.bool2 = bool2;
			this.int1 = int1;
			this.long1 = long1;
			this.string = string;
		}
	
	
		@Override
		public Object apply(IProofTreeNode ptNode, IProofMonitor pm) {
			return null;
		}
		
		public void assertParameterValues(IParameterSetting parameters) {
			assertEquals(parameters.getBoolean("bool1"), bool1);
			assertEquals(parameters.getBoolean("bool2"), bool2);
			assertEquals(parameters.getInt("int1"), int1);
			assertEquals(parameters.getLong("long1"), long1);
			assertEquals(parameters.getString("string1"), string);
		}
	
	}

	public static class TacParameterizer implements ITacticParameterizer {

		public static final String PARAMETERIZER_ID = "org.eventb.core.seqprover.tests.tacParam";

		private static final String PRM_BOOL1 = "bool1";
		private static final String PRM_BOOL2 = "bool2";
		private static final String PRM_INT = "int1";
		private static final String PRM_LONG = "long1";
		private static final String PRM_STRING = "string1";

		@Override
		public ITactic getTactic(IParameterValuation parameters) {
			return new FakeTactic(parameters.getBoolean(PRM_BOOL1),
					parameters.getBoolean(PRM_BOOL2), parameters.getInt(PRM_INT),
					parameters.getLong(PRM_LONG), parameters.getString(PRM_STRING));
		}
	}
	
	public static class SimpleTacWithParam implements ITactic {

		@Override
		public Object apply(IProofTreeNode ptNode, IProofMonitor pm) {
			return null;
		}
		
	}
	
	public static class ParamTacNoParam implements ITacticParameterizer {

		@Override
		public ITactic getTactic(IParameterValuation parameters) {
			return BasicTactics.failTac("No param");
		}
		
	}
	
	public static class ParamNullInstance implements ITacticParameterizer {

		@Override
		public ITactic getTactic(IParameterValuation parameters) {
			return null;
		}
		
	}
	
	public static class ParamThrowsException implements ITacticParameterizer {

		@Override
		public ITactic getTactic(IParameterValuation parameters) {
			throw new ConcurrentModificationException();
		}
		
	}
}

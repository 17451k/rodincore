/*******************************************************************************
 * Copyright (c) 2007, 2013 ETH Zurich and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     ETH Zurich - initial API and implementation
 *     Systerel - added replay tests for successful reasoners
 *     Systerel - added factory with math extensions
 *******************************************************************************/
package org.eventb.core.seqprover.reasonerExtentionTests;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.eventb.core.ast.Expression;
import org.eventb.core.ast.FormulaFactory;
import org.eventb.core.ast.ITypeEnvironment;
import org.eventb.core.ast.Predicate;
import org.eventb.core.ast.datatype.IDatatype;
import org.eventb.core.ast.extension.IFormulaExtension;
import org.eventb.core.seqprover.IHypAction;
import org.eventb.core.seqprover.IProofRule;
import org.eventb.core.seqprover.IProofRule.IAntecedent;
import org.eventb.core.seqprover.IProofTree;
import org.eventb.core.seqprover.IProverSequent;
import org.eventb.core.seqprover.IReasoner;
import org.eventb.core.seqprover.IReasonerFailure;
import org.eventb.core.seqprover.IReasonerInput;
import org.eventb.core.seqprover.IReasonerInputReader;
import org.eventb.core.seqprover.IReasonerInputWriter;
import org.eventb.core.seqprover.IReasonerOutput;
import org.eventb.core.seqprover.ITactic;
import org.eventb.core.seqprover.ProverFactory;
import org.eventb.core.seqprover.ProverLib;
import org.eventb.core.seqprover.SequentProver;
import org.eventb.core.seqprover.SerializeException;
import org.eventb.core.seqprover.eventbExtensions.DLib;
import org.eventb.core.seqprover.tests.TestLib;
import org.eventb.internal.core.seqprover.IInternalHypAction;
import org.eventb.internal.core.seqprover.ProverChecks;
import org.junit.Before;
import org.junit.Test;

//import com.b4free.rodin.core.B4freeCore;

/**
 * An abstract class that can be extended in order to be used to test reasoner extensions.
 * 
 * <p>
 * Tests performed include :
 * <ul>
 * <li> Registry entry tests
 * <li> Reasoner failure tests
 * <li> Reasoner success tests (including input serialization tests, replay tests, and optional logical justification tests)
 * </ul>
 * </p>
 * 
 * 
 * @author Farhad Mehta
 *
 */
public abstract class AbstractReasonerTests {

	protected static final FormulaFactory DEFAULT_FACTORY = FormulaFactory.getDefault();

	private static final IDatatype SIMPLE_DT = SimpleDatatype.getInstance();
	private static final IDatatype INDUCTIVE_DT = InductiveDatatype.getInstance();
	private static final Set<IFormulaExtension> EXTENSIONS = new HashSet<IFormulaExtension>();
	static {
		EXTENSIONS.addAll(SIMPLE_DT.getExtensions());
		EXTENSIONS.addAll(INDUCTIVE_DT.getExtensions());
		EXTENSIONS.add(PrimePredicate.getInstance());
	}

	/**
	 * A factory supporting the simple and inductive datatype extensions,
	 * together with the prime predicate.
	 * 
	 * @see SimpleDatatype
	 * @see InductiveDatatype
	 * @see PrimePredicate
	 */
	public static final FormulaFactory DT_FAC = FormulaFactory
			.getInstance(EXTENSIONS);

	protected final FormulaFactory ff;

	private IReasoner reasoner;

	public AbstractReasonerTests() {
		this(DEFAULT_FACTORY);
	}

	public AbstractReasonerTests(FormulaFactory ff) {
		this.ff = ff;
	}
	/**
	 * Returns the reasoner id of the reasoner to test
	 * 
	 * @return the reasoner id of the reasoner to test
	 * 		
	 */
	public abstract String getReasonerID();
	
	/**
	 * Returns the successful reasoner applications to test
	 * 
	 * @return the successful reasoner applications to test
	 */
	public abstract SuccessfullReasonerApplication[] getSuccessfulReasonerApplications();
	
	/**
	 * Returns the unsuccessful reasoner applications to test
	 * 
	 * @return the unsuccessful reasoner applications to test
	 */
	public abstract UnsuccessfullReasonerApplication[] getUnsuccessfullReasonerApplications();
	

	/**
	 * Returns the tactic that should be used to test that the justifications for the rules
	 * generated by the reasoner are valid.
	 * 
	 * <p>
	 * By default this method returns <code>null</code> and rule justifications are not tested 
	 * for validity. Clients that wish to do this may override this method with their tactic of
	 * choice.
	 * </p>
	 * 
	 * <p>
	 * This method of testing generated proof rules for validity is intended to be used more as logical debugging
	 * support than to be used in regular reasoner unit tests.
	 * </p>
	 * 
	 * @return the tactic to use to discharge the justifications of all rules created by this test case.
	 * 
	 */
	public ITactic getJustDischTactic(){
		return null;
//		The following line can be committed in to run PP or ML as the justification tactic for all clients that 
//		do not override this method
//		return B4freeCore.externalPP(false);
//		return B4freeCore.externalML(B4freeCore.ML_FORCE_0);
	}
	
	@Before
	public void setUp() throws Exception {
		// Setup needs to be done only once since reasoner is not modified.
		if (reasoner == null)
		{ 
			assertTrue("Reasoner with id " + getReasonerID() + " is not registered",
					SequentProver.getReasonerRegistry().isRegistered(getReasonerID()));
			reasoner = SequentProver.getReasonerRegistry().getReasonerDesc(getReasonerID()).getInstance();
			assertFalse("Reasoner with id " + getReasonerID() + " is a dummy reasoner.",
					SequentProver.getReasonerRegistry().isDummyReasoner(reasoner));

		}
	}
	
	/**
	 * Tests that the entry for the reasoner in the reasoner registry is correct.
	 */
	@Test
	public final void testReasonerRegistryEntry(){
		assertEquals("Reasoner ID from registry is not identical to the reasoner ID returned by the reasoner",
				getReasonerID(), reasoner.getReasonerID());
	}
	
	
	/**
	 * Tests the correct failure of the reasoner.
	 */
	@Test
	public final void testReasonerFailure(){
		UnsuccessfullReasonerApplication[] reasonerApplications = getUnsuccessfullReasonerApplications();
		for (UnsuccessfullReasonerApplication reasonerApp : reasonerApplications) {
			IReasonerOutput output = reasoner.apply(reasonerApp.getSequent(), reasonerApp.getInput(), null);
			assertTrue("Reasoner Application (" + reasonerApp.toString() + ") did not result in failure.",
					output instanceof IReasonerFailure);
			if (reasonerApp.getReason() != null){
				assertEquals("Reason for reasoner application failure for (" + reasonerApp.toString() + ") is not as expected.",
						reasonerApp.getReason(), ((IReasonerFailure)output).getReason());
			}
		}
		
	}
	
	/**
	 * Tests the correct succcess of the reasoner and the rule generated by it.
	 */
	@Test
	public final void testReasonerSuccess() {
		final SuccessfullReasonerApplication[] reasonerApplications = getSuccessfulReasonerApplications();
		for (SuccessfullReasonerApplication reasonerApp : reasonerApplications) {
			final IProofRule rule = applyAndCheckChildren(reasonerApp);
			checkMinimalReplay(reasonerApp, rule);
			checkNormalReplay(reasonerApp, rule);
			checkSerialization(reasonerApp, rule);
			checkJustification(reasonerApp, rule);
		}
	}

	/**
	 * Applies the reasoner and checks that the generated child sequents are as
	 * expected.
	 * 
	 * @param reasonerApp
	 * @return the rule produced by the reasoner
	 */
	private IProofRule applyAndCheckChildren(
			SuccessfullReasonerApplication reasonerApp) {
		final IProofRule rule = apply(reasonerApp);
		final IProverSequent[] actuals = rule.apply(reasonerApp.getSequent());
		assertNotNull("Rule generated by reasoner application (" + reasonerApp
				+ ") could not be applied to its sequent", actuals);
		final IProverSequent[] expecteds = reasonerApp.getNewSequents();
		assertEquals(expecteds.length, actuals.length);
		for (int i = 0; i < expecteds.length; i++) {
			if (!ProverLib.deepEquals(expecteds[i], actuals[i])) {
				fail("For reasoner application " + reasonerApp
						+ ": Expected sequent:<" + expecteds[i] + "> but was:<"
						+ actuals[i] + ">");
			}
		}
		return rule;
	}

	/**
	 * Applies the reasoner and checks that it was successful.
	 * 
	 * @param reasonerApp
	 * @return the rule produced by the reasoner
	 */
	private IProofRule apply(ReasonerApplication reasonerApp) {
		final IReasonerOutput output = reasoner.apply(reasonerApp.getSequent(),
				reasonerApp.getInput(), null);
		assertTrue("Reasoner Application (" + reasonerApp
				+ ") did not result in success.", output instanceof IProofRule);
		return (IProofRule) output;
	}

	/**
	 * Ensures that a given rule, when it is actually producing new subgoal(s),
	 * is replayable from a minimal sequent (constructed only from needed
	 * hypotheses and goal), and that the replay produces the same subgoals as
	 * the original rule.
	 * <p>
	 * The sequent must contain the same type-environment as the original
	 * sequent from which the rule was created to ensure that the reasoner input
	 * still type-checks.
	 * </p>
	 * <p>
	 * The rule returned by the replay might be different from the original
	 * rule, in the sense that it might contain less actions on hypotheses.
	 * Therefore, it would be meaningless to compare directly the two rules, and
	 * we instead compare their effect on the minimal sequent.
	 * </p>
	 * 
	 * @param app
	 * @param rule
	 */
	private void checkMinimalReplay(ReasonerApplication app,
			IProofRule rule) {
		final IProverSequent sequent = makeSequent(app, rule, true);
		final IProverSequent[] childSequents = rule.apply(sequent);
		assertNotNull(childSequents);
		if (childSequents.length == 1 && childSequents[0] == sequent) {
			// The rule does not produce any new subgoal.
			return;
		}
		// The rule would change the sequent, therefore it should be
		// replayable and replay should produce a rule with the same effect
		final IReasonerInput input = app.getInput();
		applyAndCheckChildren(new SuccessfullReasonerApplication(sequent,
				input, childSequents));
	}

	/**
	 * Ensures that a given rule is replayable from a simple sequent
	 * (constructed from needed hypotheses, goal and acted hypotheses), and that
	 * the replay produces the exact same rule.
	 * <p>
	 * The sequent must contain the same type-environment as the original
	 * sequent from which the rule was created to ensure that the reasoner input
	 * still type-checks.
	 * </p>
	 * 
	 * @param app
	 * @param rule
	 */
	private void checkNormalReplay(ReasonerApplication app, IProofRule rule) {
		final IProverSequent sequent = makeSequent(app, rule, false);
		final IReasonerInput input = app.getInput();
		final IProofRule newRule = applyAndCheckChildren(new SuccessfullReasonerApplication(
				sequent, input, rule.apply(sequent)));
		assertTrue(ProverLib.deepEquals(rule, newRule));
	}

	private IProverSequent makeSequent(ReasonerApplication app,
			IProofRule rule, boolean minimal) {
		final ITypeEnvironment typenv = app.getSequent().typeEnvironment();
		final Predicate goal = getGoal(rule, typenv.getFormulaFactory());
		final Set<Predicate> hyps = new LinkedHashSet<Predicate>();
		hyps.addAll(rule.getNeededHyps());
		if (!minimal) {
			hyps.addAll(actedHyps(rule));
		}
		return ProverFactory.makeSequent(typenv, hyps, null, hyps, goal);
	}

	private Predicate getGoal(IProofRule rule, FormulaFactory ff) {
		final Predicate goal = rule.getGoal();
		return goal == null ? DLib.False(ff) : goal;
	}

	/**
	 * Returns the hypotheses that are acted upon by some antecedent of this
	 * rule. In other terms, the hypotheses returned are the ones that are
	 * needed for the given rule to apply fully, although they are not required
	 * to apply this rule.
	 * 
	 * @param rule
	 *            some proof rule
	 * @return the hypotheses acted upon by the given rule
	 */
	private Set<Predicate> actedHyps(IProofRule rule) {
		final Set<Predicate> result = new LinkedHashSet<Predicate>();
		for (final IAntecedent antecedent : rule.getAntecedents()) {
			for (final IHypAction action : antecedent.getHypActions()) {
				final IInternalHypAction act = (IInternalHypAction) action;
				result.addAll(act.getHyps());
			}
		}
		return result;
	}

	// Test proper serialization and deserialization of the rule
	private void checkSerialization(SuccessfullReasonerApplication reasonerApp,
			IProofRule rule) {
		final ReasonerInputSerializer serializer = new ReasonerInputSerializer(rule);
		IReasonerInput deserializedInput = null;
		try {
			reasoner.serializeInput(reasonerApp.getInput(), serializer);
			deserializedInput = reasoner.deserializeInput(serializer);
		} catch (SerializeException e) {
			// This should not happen.
			fail();
		}

		assertNotNull(deserializedInput);
		assertFalse("Deserialized input for (" + reasonerApp.toString()
				+ ") has an error", deserializedInput.hasError());
		assertTrue(
				"Deserialized input class not equal to original class for ("
						+ reasonerApp.toString(),
				deserializedInput.getClass().equals(
						reasonerApp.getInput().getClass()));

		// Test that deserialized version of the input behaves in the same way
		// as the original input.
		// (At the moment the only way to do this is with a replay)
		applyAndCheckChildren(new SuccessfullReasonerApplication(
				reasonerApp.getSequent(), deserializedInput,
				reasonerApp.getNewSequents()));
	}

	// Test that reasoner justifications can be discharged by the given tactic.
	private void checkJustification(SuccessfullReasonerApplication reasonerApp,
			IProofRule rule) {
		final ITactic tactic = getJustDischTactic();
		if (tactic == null) {
			return;
		}
		final FormulaFactory ff = reasonerApp.getSequent().getFormulaFactory();
		final List<IProverSequent> justifications = ProverChecks
		.genRuleJustifications(rule, ff);
		for (final IProverSequent j : justifications) {
			final IProofTree proofTree = ProverFactory.makeProofTree(j, null);
			tactic.apply(proofTree.getRoot(), null);
			assertTrue("Justificaton " + j + " for rule generated by ("
					+ reasonerApp
					+ ") could not be discharged using the given tactic",
					proofTree.isClosed());
		}
	}

	/**
	 * This class contains the inputs to the {@link IReasoner#apply()} method and its expected result.
	 * 
	 * @author Farhad Mehta
	 *
	 */
	private static class ReasonerApplication{
		
		final IProverSequent sequent;
		final IReasonerInput input;
		
		/**
		 * Constructs a new {@link ReasonerApplication} instance. 
		 * 
		 * @param sequent
		 * 		the sequent to use as input to the {@link IReasoner#apply()} method (non <code>null</code>)
		 * @param input
		 * 		the reasoner input to use as input to the {@link IReasoner#apply()} method (non <code>null</code>)
		 */
		protected ReasonerApplication(final IProverSequent sequent, final IReasonerInput input) {
			super();
			this.sequent = sequent;
			this.input = input;
		}

		/**
		 * @return the input
		 */
		public IReasonerInput getInput() {
			return input;
		}

		/**
		 * @return the sequent
		 */
		public IProverSequent getSequent() {
			return sequent;
		}
		
		public String toString(){
			return "Sequent: " + sequent.toString() + ", Input: " + input.toString();
		}
		
	}
	
	/**
	 * This class contains the inputs to a successfull call to the {@link IReasoner#apply()} method 
	 * and its expected result.
	 * 
	 * @author Farhad Mehta
	 *
	 */
	public static class SuccessfullReasonerApplication extends ReasonerApplication{

		private static final IProverSequent[] NO_SEQUENTS = new IProverSequent[0];

		private static final Pattern pattern = Pattern.compile("^\\{([^}]*)\\}"
				+ "\\[(.*)\\]\\[(.*)\\]\\[(.*)\\]\\s*\\|-\\s*(.*)$");

		private static IProverSequent parseSequent(String sequent,
				FormulaFactory factory) {
		final Matcher matcher = pattern.matcher(sequent);
			if (!matcher.matches()) {
				throw new IllegalArgumentException("Invalid sequent image: "
						+ sequent);
			}

			final String typeEnv = matcher.group(1);
			final String hiddenHyps = matcher.group(2);
			final String defaultHyps = matcher.group(3);
			final String selHyps = matcher.group(4);
			final String goal = matcher.group(5);
			return TestLib.genFullSeq(typeEnv, hiddenHyps, defaultHyps,
					selHyps, goal, factory);
		}

		final private IProverSequent[] newSequents;

		/**
		 * Constructs a new {@link SuccessfullReasonerApplication} instance with expected new sequents.
		 * 
		 * @param sequent
		 * 		the sequent to use as input to the {@link IReasoner#apply()} method (non <code>null</code>)
		 * @param input
		 * 		the reasoner input to use as input to the {@link IReasoner#apply()} method (non <code>null</code>)
		 * @param newSequents
		 * 		the string version of the array of new sequents obtained by applying the rule generated by the reasoner
		 * 		application on the given sequent, or <code>null</code> in case this check should not be performed. 
		 */
		public SuccessfullReasonerApplication(IProverSequent sequent, IReasonerInput input, String... newSequents) {
			super(sequent, input);
			this.newSequents = new IProverSequent[newSequents.length];
			for (int i = 0; i < newSequents.length; i++) {
				this.newSequents[i] = parseSequent(newSequents[i],
						sequent.getFormulaFactory());
			}
		}

		public SuccessfullReasonerApplication(IProverSequent sequent, IReasonerInput input, IProverSequent... newSequents) {
			super(sequent, input);
			this.newSequents = newSequents.clone();
		}
		
		/**
		 * Constructs a new {@link SuccessfullReasonerApplication} instance without expected new sequents.
		 * 
		 * @param sequent
		 * 		the sequent to use as input to the {@link IReasoner#apply()} method (non <code>null</code>)
		 * @param input
		 * 		the reasoner input to use as input to the {@link IReasoner#apply()} method (non <code>null</code>)
		 */
		public SuccessfullReasonerApplication(IProverSequent sequent, IReasonerInput input) {
			super(sequent, input);
			this.newSequents = NO_SEQUENTS;
		}

		/**
		 * @return the string version of the array of new sequents obtained by applying the rule generated by the reasoner
		 * 		application on the given sequent, or <code>null</code> in case this check should not be performed.
		 * 
		 */
		public IProverSequent[] getNewSequents() {
			return newSequents;
		}
		
		public String toString(){
			return super.toString();
		}		
		
	}
	
	/**
	 * This class contains the inputs to an unsuccessfull call to the {@link IReasoner#apply()} method 
	 * and its expected result.
	 * 
	 * @author Farhad Mehta
	 *
	 */
	public static class UnsuccessfullReasonerApplication extends ReasonerApplication{
		
		final private String reason;
		
		/**
		 * Constructs a new {@link UnsuccessfullReasonerApplication} instance with expected reason.
		 * 
		 * @param sequent
		 * 		the sequent to use as input to the {@link IReasoner#apply()} method (non <code>null</code>)
		 * @param input
		 * 		the reasoner input to use as input to the {@link IReasoner#apply()} method (non <code>null</code>)
		 * @param reason
		 * 		the expected reason for reasoner failure, or <code>null</code> in case this check should not be performed. 
		 */
		public UnsuccessfullReasonerApplication(IProverSequent sequent, IReasonerInput input, String reason) {
			super(sequent, input);
			this.reason = reason;
		}

		/**
		 * Constructs a new {@link UnsuccessfullReasonerApplication} instance without an expected reason.
		 * 
		 * @param sequent
		 * 		the sequent to use as input to the {@link IReasoner#apply()} method (non <code>null</code>)
		 * @param input
		 * 		the reasoner input to use as input to the {@link IReasoner#apply()} method (non <code>null</code>)
		 */
		public UnsuccessfullReasonerApplication(IProverSequent sequent, IReasonerInput input) {
			super(sequent, input);
			this.reason = null;
		}
		
		/**
		 * @return the expected reason for reasoner failure, or <code>null</code> in case no such check should not be performed.
		 */
		public String getReason() {
			return reason;
		}
		
		public String toString(){
			return super.toString();
		}		
		
	}
	
	/**
	 * Trivial implementation for reasoner serialisation tests.
	 * 
	 * @author Farhad Mehta
	 *
	 */
	private static class ReasonerInputSerializer implements IReasonerInputReader, IReasonerInputWriter{

		private final IProofRule rule;
		private final Map<String, Predicate[]> predicates;
		private final Map<String, Expression[]> expressions;
		private final Map<String, String> strings;

		public ReasonerInputSerializer(IProofRule rule) {
			this.rule = rule;
			this.predicates = new HashMap<String, Predicate[]>();
			this.expressions = new HashMap<String, Expression[]>();
			this.strings = new HashMap<String, String>();
		}
		
		public FormulaFactory getFormulaFactory() {
			return FormulaFactory.getDefault();
		}

		public IAntecedent[] getAntecedents() {
			return rule.getAntecedents();
		}

		public int getConfidence() {
			return rule.getConfidence();
		}

		public String getDisplayName() {
			return rule.getDisplayName();
		}

		public Expression[] getExpressions(String key) throws SerializeException {
			return expressions.get(key);
		}

		public Predicate getGoal() {
			return rule.getGoal();
		}

		public Set<Predicate> getNeededHyps() {
			return rule.getNeededHyps();
		}

		public Predicate[] getPredicates(String key) throws SerializeException {
			return predicates.get(key);
		}

		public String getString(String key) throws SerializeException {
			return strings.get(key);
		}

		public void putExpressions(String key, Expression... exprs) throws SerializeException {
			expressions.put(key, exprs);	
		}

		public void putPredicates(String key, Predicate... preds) throws SerializeException {
			predicates.put(key,preds);
		}

		public void putString(String key, String str) throws SerializeException {
			strings.put(key, str);
		}
	}
	

}

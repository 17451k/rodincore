/*******************************************************************************
 * Copyright (c) 2007, 2018 ETH Zurich and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     ETH Zurich - initial API and implementation
 *     Systerel - implemented parameterized auto tactics
 *     Systerel - implemented tactic combinators
 *     Systerel - added dynamically provided auto tactics
 *******************************************************************************/
package org.eventb.internal.core.seqprover;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IExtensionPoint;
import org.eclipse.core.runtime.IExtensionRegistry;
import org.eclipse.core.runtime.Platform;
import org.eventb.core.seqprover.IAutoTacticRegistry;
import org.eventb.core.seqprover.ICombinatorDescriptor;
import org.eventb.core.seqprover.IDynTacticProvider;
import org.eventb.core.seqprover.IDynamicTacticRef;
import org.eventb.core.seqprover.IParameterDesc;
import org.eventb.core.seqprover.IParameterizerDescriptor;
import org.eventb.core.seqprover.ITacticDescriptor;
import org.eventb.core.seqprover.SequentProver;
import org.eventb.internal.core.seqprover.Placeholders.CombinatorDescriptorPlaceholder;
import org.eventb.internal.core.seqprover.Placeholders.ParameterizerPlaceholder;
import org.eventb.internal.core.seqprover.Placeholders.TacticPlaceholder;
import org.eventb.internal.core.seqprover.TacticDescriptors.CombinatorDescriptor;
import org.eventb.internal.core.seqprover.TacticDescriptors.DynTacticProviderRef;
import org.eventb.internal.core.seqprover.TacticDescriptors.DynamicTacticRef;
import org.eventb.internal.core.seqprover.TacticDescriptors.ParameterizerDescriptor;
import org.eventb.internal.core.seqprover.TacticDescriptors.TacticDescriptor;
import org.eventb.internal.core.seqprover.TacticDescriptors.UninstantiableTacticDescriptor;
import org.eventb.internal.core.seqprover.paramTactics.ParameterDesc;

/**
 * Singleton class implementing the auto tactic registry.
 * 
 * 
 * @see org.eventb.core.seqprover.IAutoTacticRegistry
 * 
 * 
 * @author Farhad Mehta
 */
public class AutoTacticRegistry implements IAutoTacticRegistry {
	
	// TODO make loader class hierarchy for simple, parameterizer, combinator
	
	private static final String TACTICS_ID =
		SequentProver.PLUGIN_ID + ".autoTactics";
	private static final String PARAMETERIZERS_ID =
			SequentProver.PLUGIN_ID + ".tacticParameterizers";
	private static final String COMBINATORS_ID =
			SequentProver.PLUGIN_ID + ".tacticCombinators";

	private static final String DYN_TACTIC_PROVIDERS_NAME = "dynTacticProvider";
	
	private static final AutoTacticRegistry SINGLETON_INSTANCE = new AutoTacticRegistry();

	private static final String[] NO_STRING = new String[0];
	
	/**
	 * Debug flag for <code>TACTIC_REGISTRY_TRACE</code>
	 */
	public static boolean DEBUG;
	
	private Map<String, ITacticDescriptor> registry;
	private final Map<String, IParameterizerDescriptor> parameterizers = new HashMap<String, IParameterizerDescriptor>();
	private final Map<String, ICombinatorDescriptor> combinators = new HashMap<String, ICombinatorDescriptor>();
	private final Map<String, IDynTacticProvider> dynTacticProviders = new HashMap<String, IDynTacticProvider>();
	
	/**
	 * Private default constructor enforces that only one instance of this class
	 * is present.
	 */
	private AutoTacticRegistry() {
		// Singleton implementation
	}
	
	/**
	 * Returns the singleton instance.
	 * <p>
	 * Note: returns the instance type instead of the interface in order to
	 * provide non API methods to classes of this plug-in.
	 * </p>
	 * 
	 * @return the auto tactic registry
	 */
	public static AutoTacticRegistry getTacticRegistry() {
		return SINGLETON_INSTANCE;
	}
	
	@Override
	public synchronized boolean isRegistered(String id) {
		if (registry == null) {
			loadRegistry();
		}
		return registry.containsKey(id);
	}
	
	@Override
	public synchronized String[] getRegisteredIDs(){
		if (registry == null) {
			loadRegistry();
		}
		return registry.keySet().toArray(NO_STRING);
	}
	
	@Override
	public  synchronized ITacticDescriptor getTacticDescriptor(String id) throws IllegalArgumentException{
		if (registry == null) {
			loadRegistry();
		}
		final ITacticDescriptor tacticDesc = registry.get(id);
		if (tacticDesc == null) {
			// Unknown tactic id, return placeholder.
			return new TacticPlaceholder(id);
		}
		return tacticDesc;
	}
	
	/**
	 * Initializes the registry using extensions to the tactic extension point.
	 */
	private synchronized void loadRegistry() {
		if (registry != null) {
			// Prevents loading by two threads in parallel
			return;
		}
		registry = new HashMap<String, ITacticDescriptor>();
		loadTacticDescriptors(TACTICS_ID);
		loadTacticDescriptors(PARAMETERIZERS_ID);
		loadTacticDescriptors(COMBINATORS_ID);
	}

	private void loadTacticDescriptors(String extPointId) {
		final IExtensionRegistry xRegistry = Platform.getExtensionRegistry();
		final IExtensionPoint xPoint = xRegistry.getExtensionPoint(extPointId);
		for (IConfigurationElement element : xPoint.getConfigurationElements()) {
			try {
				if (isDynTacticProvider(element)) {
					 loadDynTacticProvider(element);
				} else {
					loadTacticExtension(element);
				}
			} catch (Exception e) {
				// logged before
				continue;
			}
		}
	}

	private void loadDynTacticProvider(IConfigurationElement element) {
		final String id = checkAndMakeId(element);
		if (id == null) {
			return;
		}
		final IDynTacticProvider dynTacProv = TacticDescriptors.loadInstance(
				element, IDynTacticProvider.class, id);
		dynTacticProviders.put(id, dynTacProv);		
	}

	private static <T> void putCheckDuplicate(Map<String, T> map, String id,
			T t) {
		final T old = map.put(id, t);
		if (old != null) {
			map.put(id, old);
			Util.log(null, "Duplicate tactic extension " + id + " ignored");
		} else {
			if (DEBUG)
				System.out.println("Registered tactic extension " + id);
		}
	}

	// configuration element can represent either:
	// - a simple auto tactic
	// - a parameterized auto tactic
	// - a tactic combinator
	// they share common attributes, while others are specific
	private void loadTacticExtension(IConfigurationElement element) {
		// common attributes
		final UninstantiableTacticDescriptor baseDesc = loadBaseDesc(element);
		if (baseDesc == null) {
			return;
		}
		final String id = baseDesc.getTacticID();

		// specific loading
		if (isCombinator(element)) {
			final ICombinatorDescriptor comb = loadCombinator(baseDesc,
					element);
			putCheckDuplicate(combinators, id, comb);
			return;
		} else if (isParameterizer(element)) {
			final IParameterizerDescriptor parameterizer = loadParameterizer(baseDesc,
					element);
			putCheckDuplicate(parameterizers, id, parameterizer);
		} else {
			final ITacticDescriptor desc = loadSimpleTactic(baseDesc, element);
			putCheckDuplicate(registry, id, desc);
		}
	}

	private static UninstantiableTacticDescriptor loadBaseDesc(IConfigurationElement element) {
		final String id = checkAndMakeId(element);
		if (id == null) {
			return null;
		}
		final String name = element.getAttribute("name");
		String description = element.getAttribute("description");
		if (description == null)
			description = "";

		return new UninstantiableTacticDescriptor(
				id, name, description);

	}

	private static boolean isDynTacticProvider(IConfigurationElement element) {
		return element.getName().equals(DYN_TACTIC_PROVIDERS_NAME);
	}
	
	private static boolean isCombinator(IConfigurationElement element) {
		return element.getDeclaringExtension()
				.getExtensionPointUniqueIdentifier().equals(COMBINATORS_ID);
	}
	
	private static boolean isParameterizer(IConfigurationElement element) {
		return element.getDeclaringExtension()
				.getExtensionPointUniqueIdentifier().equals(PARAMETERIZERS_ID);
	}

	private static IConfigurationElement[] getParameters(
			IConfigurationElement element) {
		return element.getChildren("tacticParameter");
	}
	
	private static ITacticDescriptor loadSimpleTactic(
			UninstantiableTacticDescriptor desc, IConfigurationElement element) {
		return new TacticDescriptor(element, desc.getTacticID(),
				desc.getTacticName(), desc.getTacticDescription());
	}
	
	private static IParameterizerDescriptor loadParameterizer(
			UninstantiableTacticDescriptor baseDesc, IConfigurationElement element) {
		final Collection<IParameterDesc> paramDescs = loadTacticParameters(
				getParameters(element), baseDesc.getTacticID());
		return new ParameterizerDescriptor(baseDesc, paramDescs, element);
	}
	
	private static Collection<IParameterDesc> loadTacticParameters(
			final IConfigurationElement[] children, String id) {
		final Collection<IParameterDesc> paramDescs = new ArrayList<IParameterDesc>(
				children.length);
		final Set<String> knownLabels = new HashSet<String>(children.length);
		for (IConfigurationElement paramConfig : children) {
			final IParameterDesc param = ParameterDesc.load(paramConfig);
			final String label = param.getLabel();
			final boolean newLabel = knownLabels.add(label);
			if (newLabel) {
				paramDescs.add(param);
			} else {
				final IllegalArgumentException e = new IllegalArgumentException(
						"duplicate tactic parameter label: " + label);
				Util.log(e, "while loading parameterized tactic " + id);
				throw e;
			}
		}
		return paramDescs;
	}

	private static ICombinatorDescriptor loadCombinator(
			UninstantiableTacticDescriptor desc, IConfigurationElement element) {
		final String sMinArity = element.getAttribute("minArity");
		final int minArity = Integer.parseInt(sMinArity);
		if (minArity < 0) {
			final IllegalArgumentException e = new IllegalArgumentException(
					"invalid arity: " + sMinArity
							+ " expected a number greater than or equal to 1");
			Util.log(e, "while loading tactic combinator " + desc.getTacticID());
			throw e;
		}

		final String sBoundArity = element.getAttribute("boundArity");
		final boolean isArityBound = Boolean.parseBoolean(sBoundArity);
		return new CombinatorDescriptor(desc, minArity, isArityBound,
				element);
	}

	private static String checkAndMakeId(IConfigurationElement element) {
		final String localId = element.getAttribute("id");
		final String id;
		if (localId.indexOf('.') != -1) {
			id = null;
			Util.log(null,
					"Invalid id: " + localId + " (must not contain a dot)");
		} else if (containsWhitespace(localId)) {
			id = null;
			Util.log(null,
					"Invalid id: " + localId + " (must not contain a whitespace)");
		} else {
			final String nameSpace = element.getNamespaceIdentifier();
			id = nameSpace + "." + localId;
		}
		return id;
	}
	
	/**
	 * Checks if a string contains a whitespace character
	 * 
	 * @param str
	 * 		String to check for.
	 * @return
	 * 		<code>true</code> iff the string contains a whitespace character.
	 */
	private static boolean containsWhitespace(String str){
		for (int i = 0; i < str.length(); i++) {
			if (Character.isWhitespace(str.charAt(i))) return true;
		}
		return false;
	}

	@Override
	public boolean isRegisteredParameterizer(String id) {
		return parameterizers.containsKey(id);
	}

	@Override
	public IParameterizerDescriptor[] getParameterizerDescriptors() {
		if (registry == null) {
			loadRegistry();
		}
		return parameterizers.values().toArray(
				new IParameterizerDescriptor[parameterizers.size()]);
	}

	@Override
	public IParameterizerDescriptor getParameterizerDescriptor(String id) {
		if (registry == null) {
			loadRegistry();
		}
		final IParameterizerDescriptor parameterizer = parameterizers.get(id);
		if (parameterizer == null) {
			// unknown parameterizer, return placeholder
			return new ParameterizerPlaceholder(id);
		}
		return parameterizer;
	}

	@Override
	public boolean isRegisteredCombinator(String id) {
		return combinators.containsKey(id);
	}

	@Override
	public ICombinatorDescriptor[] getCombinatorDescriptors() {
		if (registry == null) {
			loadRegistry();
		}
		return combinators.values().toArray(
				new ICombinatorDescriptor[combinators.size()]);
	}
	
	@Override
	public ICombinatorDescriptor getCombinatorDescriptor(String id) {
		if (registry == null) {
			loadRegistry();
		}
		final ICombinatorDescriptor combinator = combinators.get(id);
		if (combinator == null) {
			// unknown combinator, return a placeholder
			return new CombinatorDescriptorPlaceholder(id);
		}
		return combinator;
	}

	public ITacticDescriptor[] getDynTactics() {
		final Collection<ITacticDescriptor> result = new ArrayList<ITacticDescriptor>();
		for (DynTacticProviderRef providerRef : getDynTacticProviderRefs()) {
			result.addAll(providerRef.getDynTactics());
		}
		return result.toArray(new ITacticDescriptor[result.size()]) ;
	}

	/**
	 * Returns the dynamic tactic with the given id, or a placeholder if no
	 * dynamic tactic with the given id is provided at call time.
	 * 
	 * @param id
	 *            a dynamic tactic id
	 * @return a tactic descriptor
	 * @since 3.0
	 */
	public ITacticDescriptor getDynTactic(String id) {
		for (final ITacticDescriptor desc : getDynTactics()) {
			if (id.equals(desc.getTacticID())) {
				return desc;
			}
		}
		return new TacticPlaceholder(id);
	}
	
	@Override
	public IDynamicTacticRef[] getDynTacticRefs() {
		final ITacticDescriptor[] dynTactics = getDynTactics();
		final IDynamicTacticRef[] dynTacticRefs = new IDynamicTacticRef[dynTactics.length];
		for (int i = 0; i < dynTactics.length; i++) {
			dynTacticRefs[i] = new DynamicTacticRef(dynTactics[i]);
		}
		return dynTacticRefs;
	}
	
	@Override
	public IDynamicTacticRef getDynTacticRef(String id) {
		final ITacticDescriptor dynTactic = getDynTactic(id);
		return new DynamicTacticRef(dynTactic);
	}

	/**
	 * Returns references to all the dynamic tactic providers that have been registered.
	 * 
	 * @return references to all the registered dynamic tactic providers
	 */
	public Collection<DynTacticProviderRef> getDynTacticProviderRefs() {
		final Collection<DynTacticProviderRef> result = new ArrayList<>();
		for (Entry<String, IDynTacticProvider> entry : dynTacticProviders.entrySet()) {
			result.add(new DynTacticProviderRef(entry.getKey(), entry.getValue()));
		}
		return result;
	}

}

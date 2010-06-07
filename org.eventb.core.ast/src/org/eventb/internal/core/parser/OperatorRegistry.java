/*******************************************************************************
 * Copyright (c) 2010 Systerel and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Systerel - initial API and implementation
 *******************************************************************************/
package org.eventb.internal.core.parser;

import java.util.EnumMap;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;

import org.eventb.core.ast.LanguageVersion;
import org.eventb.core.ast.extension.CycleError;

/**
 * @author Nicolas Beauger
 *  
 */
public class OperatorRegistry {

	public static final String GROUP0 = "GROUP 0";

	private static final OperatorGroup GROUP_0 = new OperatorGroup(GROUP0);
	
	private static class Relation<T> {
		private final Map<T, Set<T>> maplets = new HashMap<T, Set<T>>();

		public void add(T a, T b) {
			Set<T> set = maplets.get(a);
			if (set == null) {
				set = new HashSet<T>();
				maplets.put(a, set);
			}
			set.add(b);
		}

		public boolean contains(T a, T b) {
			Set<T> set = maplets.get(a);
			if (set == null) {
				return false;
			}
			return set.contains(b);
		}

		@Override
		public String toString() {
			return maplets.toString();
		}
	}
	
	private static class Closure<T> {// TODO extends Relation<T> ?
		private final Map<T, Set<T>> reachable = new HashMap<T, Set<T>>();
		private final Map<T, Set<T>> reachableReverse = new HashMap<T, Set<T>>();

		public boolean contains(T a, T b) {
			return contains(reachable, a, b);
		}

		public void add(T a, T b) throws CycleError {
			add(reachable, a, b);
			addAll(reachable, a, get(reachable, b));
			add(reachableReverse, b, a);
			addAll(reachableReverse, b, get(reachableReverse, a));
			if (!a.equals(b) && contains(reachableReverse, a, b)) {
				throw new CycleError("Adding " + a + "|->" + b
						+ " makes a cycle.");
			}
			for (T e : get(reachableReverse, a)) {
				addAll(reachable, e, get(reachable, a));
			}
			for (T e : get(reachable, b)) {
				addAll(reachableReverse, e, get(reachableReverse, b));
			}
		}

		private static <T> void add(Map<T, Set<T>> map, T a, T b) {
			final Set<T> set = get(map, a, true);
			set.add(b);
		}

		private static <T> Set<T> get(Map<T, Set<T>> map, T a, boolean addIfNeeded) {
			Set<T> set = map.get(a);
			if (set == null) {
				set = new HashSet<T>();
				if (addIfNeeded) {
					map.put(a, set);
				}
			}
			return set;
		}

		private static <T> void addAll(Map<T, Set<T>> map, T a, Set<T> s) {
			final Set<T> set = get(map, a, true);
			set.addAll(s);
		}

		private static <T> Set<T> get(Map<T, Set<T>> map, T a) {
			return get(map, a, false);
		}

		private static <T> boolean contains(Map<T, Set<T>> map, T a, T b) {
			return get(map, a).contains(b);
		}
		
		@Override
		public String toString() {
			return reachable.toString();
		}
	}
	
	private static class OperatorGroup {
		private final Set<Integer> operators = new HashSet<Integer>();
		private final EnumMap<LanguageVersion, Relation<Integer>> compatibilityRelation = new EnumMap<LanguageVersion, Relation<Integer>>(LanguageVersion.class);
		private final Closure<Integer> operatorPriority = new Closure<Integer>();

		private final String id;

		public OperatorGroup(String id) {
			this.id = id;
			for (LanguageVersion version: LanguageVersion.values()) {
				compatibilityRelation.put(version, new Relation<Integer>());
			}
		}

		public String getId() {
			return id;
		}
		
		/**
		 * Adds a compatibility between a and b for all language versions.
		 * 
		 * @param a
		 *            an operator kind
		 * @param b
		 *            an operator kind
		 */
		public void addCompatibility(Integer a, Integer b) {
			operators.add(a);
			operators.add(b);
			for(Relation<Integer> compat: compatibilityRelation.values()) {
				compat.add(a, b);
			}
		}

		/**
		 * Adds a compatibility between a and b for a given language version
		 * only.
		 * 
		 * @param a
		 *            an operator kind
		 * @param b
		 *            an operator kind
		 * @param version
		 *            a language version
		 */
		public void addCompatibility(Integer a, Integer b, LanguageVersion version) {
			operators.add(a);
			operators.add(b);
			compatibilityRelation.get(version).add(a, b);
		}

		public void addPriority(Integer a, Integer b)
				throws CycleError {
			operatorPriority.add(a, b);
		}

		public boolean contains(Integer a) {
			return operators.contains(a);
		}

		public boolean hasLessPriority(Integer a, Integer b) {
			return operatorPriority.contains(a, b);
		}
		
		public boolean isCompatible(Integer a, Integer b, LanguageVersion version) {
			return compatibilityRelation.get(version).contains(a, b)
					|| operatorPriority.contains(a, b)
					|| operatorPriority.contains(b, a);
		}
		
		@Override
		public String toString() {
			return id;
		}
	}
	
	private static class AllInOnceMap<K,V> {
		
		private final Map<K,V> map = new HashMap<K, V>();
		
		public V get(K key) {
			final V value = map.get(key);
			if (value == null) {
				throw new IllegalArgumentException("no value set for key: " + key);
			}
			return value;
		}
		
		public V getNoCheck(K key) {
			return map.get(key);
		}
		
		public K getKey(V value) {
			final Set<Entry<K, V>> entrySet = map.entrySet();
			for (Entry<K, V> entry : entrySet) {
				if (entry.getValue().equals(value)) {
					return entry.getKey();
				}
			}
			return null;
		}
		
		public void put(K key, V value) {
			final V oldValue = map.put(key, value);
			if (oldValue != null && oldValue != value) {
				throw new IllegalArgumentException(
						"trying to override value for: " + key);
			}
		}
	}
	
	private final AllInOnceMap<String, OperatorGroup> idOpGroup = new AllInOnceMap<String, OperatorGroup>();
	private final AllInOnceMap<Integer, OperatorGroup> kindOpGroup = new AllInOnceMap<Integer, OperatorGroup>();
	private final AllInOnceMap<String, Integer> idKind = new AllInOnceMap<String, Integer>();
	
	
	private final Closure<OperatorGroup> groupPriority = new Closure<OperatorGroup>();
	
	public OperatorRegistry() {
		idOpGroup.put(GROUP0, GROUP_0);
	}
	
	public void addOperator(Integer kind, String operatorId, String groupId) {
		idKind.put(operatorId, kind);
		
		OperatorGroup operatorGroup = idOpGroup.getNoCheck(groupId);
		if (operatorGroup == null) {
			operatorGroup = new OperatorGroup(groupId);
			idOpGroup.put(groupId, operatorGroup);
		}
		kindOpGroup.put(kind, operatorGroup);
	}
	
	public void addCompatibility(String leftOpId, String rightOpId) {
		final Integer leftKind = idKind.get(leftOpId);
		final Integer rightKind = idKind.get(rightOpId);
		final OperatorGroup group = getAndCheckSameGroup(leftKind, rightKind);
		group.addCompatibility(leftKind, rightKind);
	}

	public void addCompatibility(String leftOpId, String rightOpId, LanguageVersion version) {
		final Integer leftKind = idKind.get(leftOpId);
		final Integer rightKind = idKind.get(rightOpId);
		final OperatorGroup group = getAndCheckSameGroup(leftKind, rightKind);
		group.addCompatibility(leftKind, rightKind, version);
	}

	// lowOpId gets a lower priority than highOpId
	public void addPriority(String lowOpId, String highOpId)
			throws CycleError {
		final Integer leftKind = idKind.get(lowOpId);
		final Integer rightKind = idKind.get(highOpId);
		final OperatorGroup group = getAndCheckSameGroup(leftKind, rightKind);
		group.addPriority(leftKind, rightKind);
	}

	// FIXME public operations that call this method should throw a caught exception
	private OperatorGroup getAndCheckSameGroup(Integer leftKind, Integer rightKind) {
		final OperatorGroup leftGroup = kindOpGroup.get(leftKind);
		final OperatorGroup rightGroup = kindOpGroup.get(rightKind);
		if (leftGroup != rightGroup) {
			throw new IllegalArgumentException("Operators " + leftKind + " and "
					+ rightKind + " do not belong to the same group");
		}
		return leftGroup;
	}
	
	/**
	 * <code>true</code> iff leftKind and rightKind are incompatible.
	 */
	public boolean isCompatible(int leftKind, int rightKind, LanguageVersion version) {
		final OperatorGroup leftGroup = kindOpGroup.get(leftKind);
		final OperatorGroup rightGroup = kindOpGroup.get(rightKind);
		if (leftGroup != rightGroup) {
			return true;
		}
		if (leftGroup == GROUP_0) {
			// Group0 operators are all compatible with each other
			return true;
		}
		return leftGroup.isCompatible(leftKind, rightKind, version);
	}
	
	/**
	 * <code>true</code> iff priority(leftKind) < priority(rightKind)
	 * for the given language version
	 */
	public boolean hasLessPriority(int leftKind, int rightKind, LanguageVersion version) {
		// TODO right associativity
		final OperatorGroup leftGroup = kindOpGroup.get(leftKind);
		final OperatorGroup rightGroup = kindOpGroup.get(rightKind);
		
		if (leftGroup == GROUP_0 && rightGroup == GROUP_0) {
			return false;
		// Unknown groups have a priority greater than GROUP0
		} else if (leftGroup == GROUP_0) {
			return true;
		} else if (rightGroup == GROUP_0) {
			return false;
		} else if (groupPriority.contains(leftGroup, rightGroup)) {
			return true;
		} else if (groupPriority.contains(rightGroup, leftGroup)) {
			return false;
		} else if (leftGroup == rightGroup) {
			final OperatorGroup group = leftGroup;
			if (group.hasLessPriority(leftKind, rightKind)) {
				return true;
			} else if (group.hasLessPriority(rightKind, leftKind)) {
				return false;
			} else if (group.isCompatible(leftKind, rightKind, version)) {
				return false; // left associativity
			} else {
				throw new IllegalArgumentException(
						"symbols must be checked to be compatible before calling this method: "
								+ idKind.getKey(leftKind) + " with "
								+ idKind.getKey(rightKind));
			}
		} else {
			return false;
		}

	}

	// lowGroupId gets a lower priority than highGroupId
	public void addGroupPriority(String lowGroupId, String highGroupId)
			throws CycleError {
		final OperatorGroup lowGroup = idOpGroup.get(lowGroupId);
		final OperatorGroup highGroup = idOpGroup.get(highGroupId);
		groupPriority.add(lowGroup, highGroup);
	}

	public boolean hasGroup(int kind) {
		return kindOpGroup.getNoCheck(kind) != null;
	}	
	
}

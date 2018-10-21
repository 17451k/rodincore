/*******************************************************************************
 * Copyright (c) 2012, 2018 Systerel and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Systerel - initial API and implementation
 *******************************************************************************/
package org.rodinp.core.tests.relations;

import static java.util.regex.Pattern.compile;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.rodinp.core.tests.AbstractRodinDBTests.PLUGIN_ID;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.eclipse.core.runtime.IConfigurationElement;
import org.rodinp.internal.core.ElementTypeManager;
import org.rodinp.internal.core.relations.ElementParser;
import org.rodinp.internal.core.relations.ItemRelation;
import org.rodinp.internal.core.relations.ItemRelationParser;

/**
 * Abstract class to define unit tests for classes {@link ItemRelationParser},
 * {@link ElementParser} and their subclasses.
 * 
 * @author Thomas Muller
 */
public class AbstractItemRelationParserTests {

	public static final String PREFIX = PLUGIN_ID + ".";
	public static final ElementTypeManager typeManager = ElementTypeManager
			.getInstanceForTests();
	public static final InternalTestTypes eTypes = new InternalTestTypes(
			typeManager);
	public static final AttributeTestTypes aTypes = new AttributeTestTypes(
			typeManager);
	private static final String REL_ATTR_SEP = "\\s*:\\s*";
	private static final String ATTR_SEP = "\\s*;\\s*";
	protected final ItemRelationParser parser = new ItemRelationParser(eTypes,
			aTypes);

	protected static IConfigurationElement node(String nodeSpec,
			IConfigurationElement... children) {
		final String[] specs = nodeSpec.split(REL_ATTR_SEP);
		final String nodeName = specs[0];
		final String[] attributeStrs = getIDs(specs[1].split(ATTR_SEP));
		return new FakeConfigurationElement(nodeName, attributeStrs, children);
	}

	protected void assertSuccess(IConfigurationElement[] nodes,
			ItemRelation... expected) {
		assertTrue(parser.parse(nodes));
		final List<ItemRelation> actual = parser.getRelations();
		assertEquals(expected.length, actual.size());
		int i = 0;
		for (ItemRelation rel : expected) {
			assertEquals(rel, actual.get(i));
			i++;
		}
	}

	protected void assertFailure(IConfigurationElement[] nodes,
			ItemRelation... expected) {
		assertFalse(parser.parse(nodes));
		final List<ItemRelation> relations = parser.getRelations();
		assertEquals(expected.length, relations.size());
		int i = 0;
		for (ItemRelation rel : expected) {
			assertEquals(rel, relations.get(i));
			i++;
		}
	}

	private static String[] getIDs(String[] ids) {
		final String[] result = new String[ids.length];
		final String separator = "='";
		for (int i = 0; i < ids.length; i++) {
			result[i] = ids[i].replaceAll(separator, separator + PREFIX);
		}
		return result;
	}

	protected static ItemRelation relation(String relationImage) {
		final Pattern p = compile("(\\S+):(\\S*):(\\S*)");
		final Matcher m = p.matcher(relationImage);
		if (m.matches()) {
			final String parentId = m.group(1);
			final ItemRelation itemRelation = new ItemRelation(
					eTypes.get(p(parentId)));
			final Pattern p2 = compile(",");
			final String children = m.group(2);
			for (String child : p2.split(children)) {
				if (child.isEmpty())
					continue;
				itemRelation.addChildType(eTypes.get(p(child)));
			}
			final String attributes = m.group(3);
			for (String attr : p2.split(attributes)) {
				if (attr.isEmpty())
					continue;
				itemRelation.addAttributeType(aTypes.get(p(attr)));
			}
			return itemRelation;
		}
		return null;
	}

	private static String p(String string) {
		return PREFIX + string;
	}

	protected static IConfigurationElement[] t(IConfigurationElement... ts) {
		return ts;
	}

	public AbstractItemRelationParserTests() {
		super();
	}

}

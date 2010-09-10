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
package org.eventb.internal.core.ast.extension.datatype;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.eventb.core.ast.FormulaFactory;
import org.eventb.core.ast.ParametricType;
import org.eventb.core.ast.Type;
import org.eventb.core.ast.extension.IExpressionExtension;
import org.eventb.core.ast.extension.IExtensionKind;
import org.eventb.core.ast.extension.IFormulaExtension;
import org.eventb.core.ast.extension.ITypeDistribution;
import org.eventb.core.ast.extension.datatype.IArgument;
import org.eventb.core.ast.extension.datatype.IArgumentType;
import org.eventb.core.ast.extension.datatype.IDatatype;
import org.eventb.core.ast.extension.datatype.ITypeParameter;
import org.eventb.internal.core.ast.extension.TypeMediator;

/**
 * @author Nicolas Beauger
 * 
 */
public class Datatype implements IDatatype {

	private static final int INDEX_NOT_FOUND = -1;

	private static class Constructor {
		private final IExpressionExtension constructor;
		private final List<IExpressionExtension> destructors;
		private final List<IArgument> arguments;

		public Constructor(IExpressionExtension constructor,
				List<IExpressionExtension> destructors,
				List<IArgument> arguments) {
			final IExtensionKind kind = constructor.getKind();
			final ITypeDistribution childTypes = kind.getProperties()
					.getChildTypes();
			assert childTypes.getExprArity().check(destructors.size());
			this.arguments = arguments;
			this.constructor = constructor;
			this.destructors = destructors;
		}

		public IExpressionExtension getConstructor() {
			return constructor;
		}

		public IExpressionExtension getDestructor(int argNumber) {
			if (argNumber < 0 || argNumber >= destructors.size()) {
				return null;
			}
			return destructors.get(argNumber);
		}
		
		public int findDestructor(IExpressionExtension destructor) {
			for (int i = 0; i < destructors.size(); i++) {
				if (destructors.get(i) == destructor) {
					return i;
				}
			}
			return INDEX_NOT_FOUND;
		}

		public List<IArgument> getArguments() {
			return new ArrayList<IArgument>(arguments);
		}

		// non null destructors
		public Collection<IExpressionExtension> getDestructors() {
			final List<IExpressionExtension> destrs = new ArrayList<IExpressionExtension>();
			for (IExpressionExtension destr : destructors) {
				if (destr != null) {
					destrs.add(destr);
				}
			}
			return destrs;
		}

	}

	private final List<ITypeParameter> typeParams;
	private final IExpressionExtension typeConstructor;
	private final Map<String, Constructor> constructors = new LinkedHashMap<String, Constructor>();

	public Datatype(IExpressionExtension typeConstructor,
			List<ITypeParameter> typeParams) {
		this.typeConstructor = typeConstructor;
		this.typeParams = typeParams;
	}

	@Override
	public List<ITypeParameter> getTypeParameters() {
		return new ArrayList<ITypeParameter>(typeParams);
	}
	
	@Override
	public IExpressionExtension getTypeConstructor() {
		return typeConstructor;
	}

	@Override
	public IExpressionExtension getConstructor(String constructorId) {
		final Constructor constr = constructors.get(constructorId);
		if (constr == null) {
			return null;
		}
		return constr.getConstructor();
	}

	@Override
	public Set<IExpressionExtension> getConstructors() {
		final Set<IExpressionExtension> constrs = new LinkedHashSet<IExpressionExtension>();
		for (Constructor constr : constructors.values()) {
			constrs.add(constr.getConstructor());
		}
		return constrs;
	}

	@Override
	public boolean isConstructor(IExpressionExtension extension) {
		return getConstructor(extension.getId()) == extension;
	}
	
	@Override
	public IExpressionExtension getDestructor(String constructorId,
			int argNumber) {
		final Constructor constr = constructors.get(constructorId);
		if (constr == null) {
			return null;
		}
		return constr.getDestructor(argNumber);
	}

	@Override
	public List<IArgument> getArguments(IExpressionExtension constructor) {
		final Constructor constr = constructors.get(constructor.getId());
		if (constr == null) {
			return null;
		}
		return constr.getArguments();
	}

	@Override
	public int getDestructorIndex(IExpressionExtension constructor,
			IExpressionExtension destructor) {
		final Constructor constr = constructors.get(constructor.getId());
		if (constr == null) {
			return INDEX_NOT_FOUND;
		}
		return constr.findDestructor(destructor);
	}

	@Override
	public Set<IFormulaExtension> getExtensions() {
		final Set<IFormulaExtension> extensions = new HashSet<IFormulaExtension>();

		extensions.add(typeConstructor);
		for (Constructor constr : constructors.values()) {
			extensions.add(constr.getConstructor());
			extensions.addAll(constr.getDestructors());
		}
		return extensions;
	}

	// don't forget to insert null destructors for arguments with no
	// destructor, in order to have correct argument numbers
	public void addConstructor(IExpressionExtension constructor,
			List<IExpressionExtension> destructors, List<IArgument> arguments) {
		assert destructors.size() == arguments.size();
		final Constructor old = constructors.get(constructor);
		if (old != null) {
			throw new IllegalArgumentException("constructor "
					+ constructor.getId() + " already exists");
		}
		final Constructor constr = new Constructor(constructor, destructors,
				arguments);
		constructors.put(constructor.getId(), constr);
	}

	@Override
	public List<Type> getArgumentTypes(IExpressionExtension constructor,
			ParametricType type, FormulaFactory factory) {
		final TypeInstantiation typeInst = makeTypeInstantiation(type);
		final List<IArgument> arguments = getArguments(constructor);
		if (typeInst == null || arguments == null) {
			return null;
		}
		final List<Type> argTypes = new ArrayList<Type>(arguments.size());
		for (IArgument arg : arguments) {
			final Type argType = getType(arg, typeInst, factory);
			argTypes.add(argType);
		}
		return argTypes;
	}
	
	private static Type getType(IArgument arg, TypeInstantiation typeInst,
			FormulaFactory ff) {
		final IArgumentType argAType = arg.getType();
		final Type argType = argAType.toType(new TypeMediator(ff), typeInst);
		return argType;
	}
	
	private TypeInstantiation makeTypeInstantiation(ParametricType type) {
		if (type.getExprExtension() != typeConstructor) {
			return null;
		}
		return makeTypeInst(type, typeParams);
	}
	
	public static TypeInstantiation makeTypeInst(ParametricType type,
			List<ITypeParameter> typePrms) {
		final TypeInstantiation instantiation = new TypeInstantiation();
		final Type[] typeValues = type.getTypeParameters();
		assert typePrms.size() == typeValues.length;
		for (int i = 0; i < typeValues.length; i++) {
			instantiation.put(typePrms.get(i), typeValues[i]);
		}
		return instantiation;
	}
	
}

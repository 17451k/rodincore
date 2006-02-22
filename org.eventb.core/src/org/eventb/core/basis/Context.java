/*******************************************************************************
 * Copyright (c) 2005 ETH Zurich.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/

package org.eventb.core.basis;

import java.util.ArrayList;

import org.eclipse.core.resources.IFile;
import org.eventb.core.EventBPlugin;
import org.eventb.core.IAxiom;
import org.eventb.core.ICarrierSet;
import org.eventb.core.IConstant;
import org.eventb.core.IContext;
import org.eventb.core.IPOFile;
import org.eventb.core.IPRFile;
import org.eventb.core.ISCContext;
import org.eventb.core.ITheorem;
import org.rodinp.core.IRodinElement;
import org.rodinp.core.IRodinProject;
import org.rodinp.core.RodinDBException;
import org.rodinp.core.basis.RodinFile;

/**
 * Implementation of Event-B (unchecked) contexts as an extension of the Rodin database.
 * <p>
 * This class is intended to be implemented by clients that want to extend this
 * file element type.
 * </p>
 * <p>
 * This class should not be used in any other way than subclassing it
 * in a database extension. In particular, clients should not use it,
 * but rather use its associated interface <code>IContext</code>.
 * </p>
 *
 * @author Laurent Voisin
 */
public class Context extends RodinFile implements IContext {
	
	/**
	 *  Constructor used by the Rodin database. 
	 */
	public Context(IFile file, IRodinElement parent) {
		super(file, parent);
	}

	@Override
	public String getElementType() {
		return ELEMENT_TYPE;
	}

	public CarrierSet[] getCarrierSets() throws RodinDBException {
		ArrayList<IRodinElement> list = getFilteredChildrenList(ICarrierSet.ELEMENT_TYPE);
		CarrierSet[] carrierSets = new CarrierSet[list.size()];
		list.toArray(carrierSets);
		return carrierSets; 
	}
	
	public Constant[] getConstants() throws RodinDBException {
		ArrayList<IRodinElement> list = getFilteredChildrenList(IConstant.ELEMENT_TYPE);
		Constant[] constants = new Constant[list.size()];
		list.toArray(constants);
		return constants; 
	}
	
	public Axiom[] getAxioms() throws RodinDBException {
		ArrayList<IRodinElement> list = getFilteredChildrenList(IAxiom.ELEMENT_TYPE);
		Axiom[] axioms = new Axiom[list.size()];
		list.toArray(axioms);
		return axioms; 
	}
	
	public Theorem[] getTheorems() throws RodinDBException {
		ArrayList<IRodinElement> list = getFilteredChildrenList(ITheorem.ELEMENT_TYPE);
		Theorem[] theorems = new Theorem[list.size()];
		list.toArray(theorems);
		return theorems;
	}

	public ISCContext getCheckedContext() {
		final String bareName = EventBPlugin.getComponentName(getElementName());
		final String scName = EventBPlugin.getSCContextFileName(bareName);
		final IRodinProject project = (IRodinProject) getParent();
		return (ISCContext) project.getRodinFile(scName);
	}

	public IPOFile getPOFile() {
		final String bareName = EventBPlugin.getComponentName(getElementName());
		final String poName = EventBPlugin.getPOFileName(bareName);
		final IRodinProject project = (IRodinProject) getParent();
		return (IPOFile) project.getRodinFile(poName);
	}

	public IPRFile getPRFile() {
		final String bareName = EventBPlugin.getComponentName(getElementName());
		final String prName = EventBPlugin.getPRFileName(bareName);
		final IRodinProject project = (IRodinProject) getParent();
		return (IPRFile) project.getRodinFile(prName);
	}

}

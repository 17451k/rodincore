/*******************************************************************************
 * Copyright (c) 2005, 2008 ETH Zurich and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     ETH Zurich - initial API and implementation
 *     Systerel - separation of file and root element
 ******************************************************************************/
package org.eventb.core.basis;

import org.eventb.core.ISCEvent;
import org.eventb.core.ISCInternalContext;
import org.eventb.core.ISCInvariant;
import org.eventb.core.ISCMachineRoot;
import org.eventb.core.ISCRefinesMachine;
import org.eventb.core.ISCSeesContext;
import org.eventb.core.ISCTheorem;
import org.eventb.core.ISCVariable;
import org.eventb.core.ISCVariant;
import org.eventb.core.ast.FormulaFactory;
import org.eventb.core.ast.ITypeEnvironment;
import org.eventb.internal.core.Messages;
import org.rodinp.core.IInternalElementType;
import org.rodinp.core.IRodinElement;
import org.rodinp.core.IRodinFile;
import org.rodinp.core.RodinDBException;

/**
 * Implementation of Event-B (unchecked) contexts as an extension of the Rodin
 * database.
 * <p>
 * This class should not be used directly by any client except the Rodin
 * database. In particular, clients should not use it, but rather use its
 * associated interface <code>IContextFile</code>.
 * </p>
 * 
 * @author Laurent Voisin
 */
public class SCMachineRoot extends EventBRoot implements ISCMachineRoot {

	/**
	 * Constructor used by the Rodin database.
	 */
	public SCMachineRoot(String name, IRodinElement parent) {
		super(name, parent);
	}

	@Override
	public IInternalElementType<ISCMachineRoot> getElementType() {
		return ELEMENT_TYPE;
	}

	public ISCVariable[] getSCVariables() throws RodinDBException {
		return getChildrenOfType(ISCVariable.ELEMENT_TYPE);
	}

	public ISCEvent[] getSCEvents() throws RodinDBException {
		return getChildrenOfType(ISCEvent.ELEMENT_TYPE);
	}

	@Deprecated
	public IRodinFile getAbstractSCMachine() throws RodinDBException {
		ISCRefinesMachine machine = getRefinesClause();
		if (machine == null)
			return null;
		else
			return machine.getAbstractSCMachine();
	}

	public ISCInternalContext[] getSCSeenContexts() throws RodinDBException {
		return getChildrenOfType(ISCInternalContext.ELEMENT_TYPE);
	}

	public ISCInvariant[] getSCInvariants() throws RodinDBException {
		return getChildrenOfType(ISCInvariant.ELEMENT_TYPE);
	}

	public ISCTheorem[] getSCTheorems() throws RodinDBException {
		return getChildrenOfType(ISCTheorem.ELEMENT_TYPE);
	}

	@Deprecated
	public ISCRefinesMachine getRefinesClause() throws RodinDBException {
		return getSingletonChild(ISCRefinesMachine.ELEMENT_TYPE,
				Messages.database_SCMachineMultipleRefinesFailure);
	}

	@Deprecated
	public ISCVariant getSCVariant() throws RodinDBException {
		return getSingletonChild(ISCVariant.ELEMENT_TYPE,
				Messages.database_SCMachineMultipleVariantFailure);
	}

	public IRodinFile[] getAbstractSCMachines() throws RodinDBException {
		ISCRefinesMachine[] refinesMachines = getSCRefinesClauses();
		final int length = refinesMachines.length;
		IRodinFile[] machineFiles = new IRodinFile[length];
		for (int i = 0; i < length; i++) {
			machineFiles[i] = refinesMachines[i].getAbstractSCMachine();
		}
		return machineFiles;
	}

	public ISCRefinesMachine[] getSCRefinesClauses() throws RodinDBException {
		return getChildrenOfType(ISCRefinesMachine.ELEMENT_TYPE);
	}

	public ISCSeesContext[] getSCSeesClauses() throws RodinDBException {
		return getChildrenOfType(ISCSeesContext.ELEMENT_TYPE);
	}

	public ISCVariant[] getSCVariants() throws RodinDBException {
		return getChildrenOfType(ISCVariant.ELEMENT_TYPE);
	}

	public ISCEvent getSCEvent(String elementName) {
		return getInternalElement(ISCEvent.ELEMENT_TYPE, elementName);
	}

	public ISCInvariant getSCInvariant(String elementName) {
		return getInternalElement(ISCInvariant.ELEMENT_TYPE, elementName);
	}

	public ISCRefinesMachine getSCRefinesClause(String elementName) {
		return getInternalElement(ISCRefinesMachine.ELEMENT_TYPE, elementName);
	}

	public ISCSeesContext getSCSeesClause(String elementName) {
		return getInternalElement(ISCSeesContext.ELEMENT_TYPE, elementName);
	}

	public ISCTheorem getSCTheorem(String elementName) {
		return getInternalElement(ISCTheorem.ELEMENT_TYPE, elementName);
	}

	public ISCVariable getSCVariable(String elementName) {
		return getInternalElement(ISCVariable.ELEMENT_TYPE, elementName);
	}

	public ISCVariant getSCVariant(String elementName) {
		return getInternalElement(ISCVariant.ELEMENT_TYPE, elementName);
	}

	public ISCInternalContext getSCSeenContext(String elementName) {
		return getInternalElement(ISCInternalContext.ELEMENT_TYPE, elementName);
	}

	public ITypeEnvironment getTypeEnvironment(FormulaFactory factory)
			throws RodinDBException {

		ITypeEnvironment typenv = factory.makeTypeEnvironment();
		for (ISCInternalContext ictx : getSCSeenContexts()) {
			SCContextUtil.augmentTypeEnvironment(ictx, typenv, factory);
		}
		for (ISCVariable vrb : getSCVariables()) {
			typenv.add(vrb.getIdentifier(factory));
		}
		return typenv;
	}

}

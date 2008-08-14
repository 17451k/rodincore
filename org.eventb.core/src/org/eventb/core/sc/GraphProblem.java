/*******************************************************************************
 * Copyright (c) 2006 ETH Zurich.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.eventb.core.sc;

import java.text.MessageFormat;

import org.eclipse.core.resources.IMarker;
import org.eventb.core.EventBPlugin;
import org.eventb.internal.core.sc.Messages;
import org.rodinp.core.IRodinProblem;

/**
 * @author Stefan Hallerstede
 *
 */
public enum GraphProblem implements IRodinProblem {

	ConfigurationMissingError(IMarker.SEVERITY_ERROR, Messages.scuser_ConfigurationMissing),
	IdentifierUndefError(IMarker.SEVERITY_ERROR, Messages.scuser_IdentifierUndef),
	PredicateUndefError(IMarker.SEVERITY_ERROR, Messages.scuser_PredicateUndef),
	ExpressionUndefError(IMarker.SEVERITY_ERROR, Messages.scuser_ExpressionUndef),
	AssignmentUndefError(IMarker.SEVERITY_ERROR, Messages.scuser_AssignmentUndef),
	ConvergenceUndefError(IMarker.SEVERITY_ERROR, Messages.scuser_ConvergenceUndef),
	ExtendedUndefError(IMarker.SEVERITY_ERROR, Messages.scuser_ExtendedUndef),
	InvalidIdentifierError(IMarker.SEVERITY_ERROR, Messages.scuser_InvalidIdentifierName),
	InvalidIdentifierSpacesError(IMarker.SEVERITY_ERROR, Messages.scuser_InvalidIdentifierContainsSpaces),
	LabelUndefError(IMarker.SEVERITY_ERROR, Messages.scuser_LabelUndef),
	AbstractContextNameUndefError(IMarker.SEVERITY_ERROR, Messages.scuser_AbstractContextNameUndef),
	AbstractContextNotFoundError(IMarker.SEVERITY_ERROR, Messages.scuser_AbstractContextNotFound),
	AbstractContextRedundantWarning(IMarker.SEVERITY_WARNING, Messages.scuser_AbstractContextRedundant),
	SeenContextRedundantWarning(IMarker.SEVERITY_WARNING, Messages.scuser_SeenContextRedundant),
	SeenContextNameUndefError(IMarker.SEVERITY_ERROR, Messages.scuser_SeenContextNameUndef),
	SeenContextNotFoundError(IMarker.SEVERITY_ERROR, Messages.scuser_SeenContextNotFound),
	SeenContextWithoutConfigurationError(IMarker.SEVERITY_ERROR, Messages.scuser_SeenContextWithoutConfiguration),
	AbstractMachineNameUndefError(IMarker.SEVERITY_ERROR, Messages.scuser_AbstractMachineNameUndef),
	TooManyAbstractMachinesError(IMarker.SEVERITY_ERROR, Messages.scuser_OnlyOneAbstractMachine),
	AbstractMachineWithoutConfigurationError(IMarker.SEVERITY_ERROR, Messages.scuser_AbstractMachineWithoutConfiguration),
	AbstractContextWithoutConfigurationError(IMarker.SEVERITY_ERROR, Messages.scuser_AbstractContextWithoutConfiguration),
	AbstractMachineNotFoundError(IMarker.SEVERITY_ERROR, Messages.scuser_AbstractMachineNotFound),
	AbstractEventLabelUndefError(IMarker.SEVERITY_ERROR, Messages.scuser_AbstractEventLabelUndef),
	AbstractEventNotFoundError(IMarker.SEVERITY_ERROR, Messages.scuser_AbstractEventNotFound),
	AbstractEventNotRefinedError(IMarker.SEVERITY_ERROR, Messages.scuser_AbstractEventNotRefined),
	AbstractEventLabelConflictWarning(IMarker.SEVERITY_WARNING, Messages.scuser_AbstractEventLabelConflict),
	EventMergeSplitError(IMarker.SEVERITY_ERROR, Messages.scuser_EventMergeSplitConflict),
	EventMergeMergeError(IMarker.SEVERITY_ERROR, Messages.scuser_EventMergeMergeConflict),
	EventInheritedMergeSplitError(IMarker.SEVERITY_ERROR, Messages.scuser_EventInheritedMergeSplitConflict),
	EventExtendedUnrefinedError(IMarker.SEVERITY_ERROR, Messages.scuser_EventExtendedUnrefined),
	EventExtendedMergeError(IMarker.SEVERITY_ERROR, Messages.scuser_EventExtendedMerge),
	EventMergeVariableTypeError(IMarker.SEVERITY_ERROR, Messages.scuser_EventMergeParameterTypeConflict),
	EventMergeActionError(IMarker.SEVERITY_ERROR, Messages.scuser_EventMergeActionConflict),
	EventMergeLabelError(IMarker.SEVERITY_ERROR, Messages.scuser_EventMergeLabelConflict),
	EventRefinementError(IMarker.SEVERITY_ERROR, Messages.scuser_EventRefinementError),
	MachineWithoutInitialisationWarning(IMarker.SEVERITY_WARNING, Messages.scuser_MachineWithoutInitialisation),
	InitialisationRefinedError(IMarker.SEVERITY_ERROR, Messages.scuser_InitialisationRefinedError),
	InitialisationRefinesEventWarning(IMarker.SEVERITY_WARNING, Messages.scuser_InitialisationRefinesEventError),
	InitialisationVariableError(IMarker.SEVERITY_ERROR, Messages.scuser_InitialisationVariableError),
	InitialisationGuardError(IMarker.SEVERITY_ERROR, Messages.scuser_InitialisationGuardError),
	InitialisationActionRHSError(IMarker.SEVERITY_ERROR, Messages.scuser_InitialisationActionRHSError),
	InitialisationIncompleteWarning(IMarker.SEVERITY_WARNING, Messages.scuser_InitialisationIncomplete),
	CarrierSetNameImportConflictError(IMarker.SEVERITY_ERROR, Messages.scuser_CarrierSetNameImportConflict),
	CarrierSetNameImportConflictWarning(IMarker.SEVERITY_WARNING, Messages.scuser_CarrierSetNameImportConflict),
	CarrierSetNameConflictError(IMarker.SEVERITY_ERROR, Messages.scuser_CarrierSetNameConflict),
	CarrierSetNameConflictWarning(IMarker.SEVERITY_WARNING, Messages.scuser_CarrierSetNameConflict),
	ConstantNameImportConflictError(IMarker.SEVERITY_ERROR, Messages.scuser_ConstantNameImportConflict),
	ConstantNameImportConflictWarning(IMarker.SEVERITY_WARNING, Messages.scuser_ConstantNameImportConflict),
	ConstantNameConflictError(IMarker.SEVERITY_ERROR, Messages.scuser_ConstantNameConflict),
	ConstantNameConflictWarning(IMarker.SEVERITY_WARNING, Messages.scuser_ConstantNameConflict),
	VariableNameImportConflictError(IMarker.SEVERITY_ERROR, Messages.scuser_VariableNameImportConflict),
	VariableNameImportConflictWarning(IMarker.SEVERITY_WARNING, Messages.scuser_VariableNameImportConflict),
	VariableNameConflictError(IMarker.SEVERITY_ERROR, Messages.scuser_VariableNameConflict),
	VariableNameConflictWarning(IMarker.SEVERITY_WARNING, Messages.scuser_VariableNameConflict),
	EventParameterNameConflictError(IMarker.SEVERITY_ERROR, Messages.scuser_EventParameterNameConflict),
	EventParameterNameConflictWarning(IMarker.SEVERITY_WARNING, Messages.scuser_EventParameterNameConflict),
	UntypedCarrierSetError(IMarker.SEVERITY_ERROR, Messages.scuser_UntypedCarrierSetError),
	UntypedConstantError(IMarker.SEVERITY_ERROR, Messages.scuser_UntypedConstantError),
	UntypedVariableError(IMarker.SEVERITY_ERROR, Messages.scuser_UntypedVariableError),
	UntypedParameterError(IMarker.SEVERITY_ERROR, Messages.scuser_UntypedParameterError),
	UntypedIdentifierError(IMarker.SEVERITY_ERROR, Messages.scuser_UntypedIdentifierError),
	UndeclaredFreeIdentifierError(IMarker.SEVERITY_ERROR, Messages.scuser_UndeclaredFreeIdentifierError),
	FreeIdentifierFaultyDeclError(IMarker.SEVERITY_ERROR, Messages.scuser_FreeIdentifierFaultyDeclError),
	VariantFreeIdentifierError(IMarker.SEVERITY_ERROR, Messages.scuser_VariantFreeIdentifierError),
	AxiomFreeIdentifierError(IMarker.SEVERITY_ERROR, Messages.scuser_AxiomFreeIdentifierError),
	TheoremFreeIdentifierError(IMarker.SEVERITY_ERROR, Messages.scuser_TheoremFreeIdentifierError),
	InvariantFreeIdentifierError(IMarker.SEVERITY_ERROR, Messages.scuser_InvariantFreeIdentifierError),
	GuardFreeIdentifierError(IMarker.SEVERITY_ERROR, Messages.scuser_GuardFreeIdentifierError),
	ActionFreeIdentifierError(IMarker.SEVERITY_ERROR, Messages.scuser_ActionFreeIdentifierError),
	ActionDisjointLHSError(IMarker.SEVERITY_ERROR, Messages.scuser_ActionDisjointLHSProblem),
	ActionDisjointLHSWarining(IMarker.SEVERITY_WARNING, Messages.scuser_ActionDisjointLHSProblem),
	WitnessFreeIdentifierError(IMarker.SEVERITY_ERROR, Messages.scuser_WitnessFreeIdentifierError),
	InvalidVariantTypeError(IMarker.SEVERITY_ERROR, Messages.scuser_InvalidVariantTypeError),
	TooManyVariantsError(IMarker.SEVERITY_ERROR, Messages.scuser_TooManyVariants),
	ConvergentFaultyConvergenceWarning(IMarker.SEVERITY_WARNING, Messages.scuser_ConvergentFaultyConvergence),
	OrdinaryFaultyConvergenceWarning(IMarker.SEVERITY_WARNING, Messages.scuser_OrdinaryFaultyConvergence),
	AnticipatedFaultyConvergenceWarning(IMarker.SEVERITY_WARNING, Messages.scuser_AnticipatedFaultyConvergence),
	NoConvergentEventButVariantWarning(IMarker.SEVERITY_WARNING, Messages.scuser_NoConvergentEventButVariant),
	ConvergentEventNoVariantWarning(IMarker.SEVERITY_WARNING, Messages.scuser_ConvergentEventNoVariant),
	InitialisationNotOrdinaryWarning(IMarker.SEVERITY_WARNING, Messages.scuser_InitialisationNotOrdinary),
	AxiomLabelConflictError(IMarker.SEVERITY_ERROR, Messages.scuser_AxiomLabelConflict),
	AxiomLabelConflictWarning(IMarker.SEVERITY_WARNING, Messages.scuser_AxiomLabelConflict),
	TheoremLabelConflictError(IMarker.SEVERITY_ERROR, Messages.scuser_TheoremLabelConflict),
	TheoremLabelConflictWarning(IMarker.SEVERITY_WARNING, Messages.scuser_TheoremLabelConflict),
	InvariantLabelConflictError(IMarker.SEVERITY_ERROR, Messages.scuser_InvariantLabelConflict),
	InvariantLabelConflictWarning(IMarker.SEVERITY_WARNING, Messages.scuser_InvariantLabelConflict),
	EventLabelConflictError(IMarker.SEVERITY_ERROR, Messages.scuser_EventLabelConflict),
	EventLabelConflictWarning(IMarker.SEVERITY_WARNING, Messages.scuser_EventLabelConflict),
	GuardLabelConflictError(IMarker.SEVERITY_ERROR, Messages.scuser_GuardLabelConflict),
	GuardLabelConflictWarning(IMarker.SEVERITY_WARNING, Messages.scuser_GuardLabelConflict),
	ActionLabelConflictError(IMarker.SEVERITY_ERROR, Messages.scuser_ActionLabelConflict),
	ActionLabelConflictWarning(IMarker.SEVERITY_WARNING, Messages.scuser_ActionLabelConflict),
	WitnessLabelConflictError(IMarker.SEVERITY_ERROR, Messages.scuser_WitnessLabelConflict),
	WitnessLabelConflictWarning(IMarker.SEVERITY_WARNING, Messages.scuser_WitnessLabelConflict),
	WitnessLabelMissingWarning(IMarker.SEVERITY_WARNING, Messages.scuser_WitnessLabelMissing),
	WitnessLabelNeedLessError(IMarker.SEVERITY_ERROR, Messages.scuser_WitnessLabelNeedLess),
	WitnessLabelNotPermissible(IMarker.SEVERITY_ERROR, Messages.scuser_WitnessLabelNotPermissible),
	ContextOnlyInAbstractMachineWarning(IMarker.SEVERITY_WARNING, Messages.scuser_ContextOnlyPresentInAbstractMachine),
	WasAbstractEventLabelWarning(IMarker.SEVERITY_WARNING, Messages.scuser_WasAbstractEventLabelProblem),
	InconsistentEventLabelWarning(IMarker.SEVERITY_WARNING, Messages.scuser_InconsistentEventLabelProblem),
	VariableHasDisappearedError(IMarker.SEVERITY_ERROR, Messages.scuser_VariableHasDisappearedError),
	DisappearedVariableRedeclaredError(IMarker.SEVERITY_ERROR, Messages.scuser_DisappearedVariableRedeclaredError),
	VariableIsParameterInAbstractMachineError(IMarker.SEVERITY_ERROR, Messages.scuser_VariableIsParameterInAbstractMachine),
	AssignedIdentifierNotVariableError(IMarker.SEVERITY_ERROR, Messages.scuser_AssignedIdentifierNotVariable),
	ParameterChangedTypeError(IMarker.SEVERITY_ERROR, Messages.scuser_ParameterChangedTypeError),
	AssignmentToParameterError(IMarker.SEVERITY_ERROR, Messages.scuser_AssignmentToParameter);
	
	private final String errorCode;
	
	private final String message;
	
	private final int severity;
	
	private int arity;

	private GraphProblem(int severity, String message) {
		this.severity = severity;
		this.message = message;
		this.errorCode = EventBPlugin.PLUGIN_ID + "." + name();
		arity = -1;
	}
	
	public static GraphProblem valueOfErrorCode(String errorCode) {
		String instName = errorCode.substring(errorCode.lastIndexOf('.')+1);
		return valueOf(instName);
	}

	/* (non-Javadoc)
	 * @see org.rodinp.core.IRodinProblem#getSeverity()
	 */
	public int getSeverity() {
		return severity;
	}
	
	/**
	 * Returns the number of parameters needed by the message of this problem,
	 * i.e. the length of the object array to be passed to 
	 * <code>getLocalizedMessage()</code>.
	 * 
	 * @return the number of parameters needed by the message of this problem
	 */
	public int getArity() {
		if (arity == -1) {
			MessageFormat mf = new MessageFormat(message);
		    arity = mf.getFormatsByArgumentIndex().length;
		}
		return arity;
	}

	/* (non-Javadoc)
	 * @see org.rodinp.core.IRodinProblem#getErrorCode()
	 */
	public String getErrorCode() {
		return errorCode;
	}

	/* (non-Javadoc)
	 * @see org.rodinp.core.IRodinProblem#getLocalizedMessage(java.lang.Object[])
	 */
	public String getLocalizedMessage(Object[] args) {
		return MessageFormat.format(message, args);
	}

}

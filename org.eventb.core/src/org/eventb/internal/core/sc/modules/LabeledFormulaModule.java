/*******************************************************************************
 * Copyright (c) 2006 ETH Zurich.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.eventb.internal.core.sc.modules;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eventb.core.ast.ASTProblem;
import org.eventb.core.ast.Formula;
import org.eventb.core.ast.FormulaFactory;
import org.eventb.core.ast.FreeIdentifier;
import org.eventb.core.ast.IResult;
import org.eventb.core.ast.ITypeCheckResult;
import org.eventb.core.ast.ITypeEnvironment;
import org.eventb.core.ast.ProblemKind;
import org.eventb.core.ast.SourceLocation;
import org.eventb.core.sc.GraphProblem;
import org.eventb.core.sc.ISCFilterModule;
import org.eventb.core.sc.ParseProblem;
import org.eventb.core.sc.state.IIdentifierSymbolTable;
import org.eventb.core.sc.state.IParsedFormula;
import org.eventb.core.sc.state.ISCState;
import org.eventb.core.sc.state.ISCStateRepository;
import org.eventb.core.sc.state.ITypingState;
import org.eventb.core.sc.symbolTable.ILabelSymbolInfo;
import org.eventb.core.tool.state.IStateRepository;
import org.eventb.internal.core.sc.ParsedFormula;
import org.rodinp.core.IAttributeType;
import org.rodinp.core.IInternalElement;
import org.rodinp.core.IInternalParent;
import org.rodinp.core.IRodinElement;
import org.rodinp.core.IRodinProblem;
import org.rodinp.core.RodinDBException;

/**
 * @author Stefan Hallerstede
 *
 */
public abstract class LabeledFormulaModule<F extends Formula, I extends IInternalElement> 
extends LabeledElementModule {

	protected IIdentifierSymbolTable identifierSymbolTable;
	protected ITypingState typingState;
	
	protected List<I> formulaElements;
	protected List<F> formulas;
	
	/* (non-Javadoc)
	 * @see org.eventb.core.sc.Module#initModule(org.eventb.core.sc.IStateRepository, org.eclipse.core.runtime.IProgressMonitor)
	 */
	@Override
	public void initModule(
			IRodinElement element, 
			ISCStateRepository repository, 
			IProgressMonitor monitor) throws CoreException {
		super.initModule(element, repository, monitor);
		identifierSymbolTable = 
			(IIdentifierSymbolTable) repository.getState(IIdentifierSymbolTable.STATE_TYPE);
		typingState = 
			(ITypingState) repository.getState(ITypingState.STATE_TYPE);
		
		formulaElements = getFormulaElements(element);

		final int size = formulaElements.size();
		formulas = new ArrayList<F>(size);
		for (int i=0; i<size; i++) {
			formulas.add(null);
		}
	}

	protected abstract List<I> getFormulaElements(IRodinElement element) throws CoreException;

	/* (non-Javadoc)
	 * @see org.eventb.core.sc.Module#endModule(org.eventb.core.sc.IStateRepository, org.eclipse.core.runtime.IProgressMonitor)
	 */
	@Override
	public void endModule(
			IRodinElement element, 
			ISCStateRepository repository, 
			IProgressMonitor monitor) throws CoreException {
		identifierSymbolTable = null;
		typingState = null;
		super.endModule(element, repository, monitor);
	}

	protected void issueASTProblemMarkers(
			IInternalElement element, 
			IAttributeType.String attributeType, 
			IResult result) throws RodinDBException {
		
		for (ASTProblem parserProblem : result.getProblems()) {
			SourceLocation location = parserProblem.getSourceLocation();
			ProblemKind problemKind = parserProblem.getMessage();
			Object[] args = parserProblem.getArgs();
			
			IRodinProblem problem;
			Object[] objects; // parameters for the marker
			
			switch (problemKind) {
			
			case FreeIdentifierHasBoundOccurences:
				problem = ParseProblem.FreeIdentifierHasBoundOccurencesWarning;
				objects = new Object[] {
					args[0]
				};				
				break;
				
			case BoundIdentifierHasFreeOccurences:
				// ignore
				// this is just the symmetric message to FreeIdentifierHasBoundOccurences
				continue;

			case BoundIdentifierIsAlreadyBound:
				problem = ParseProblem.BoundIdentifierIsAlreadyBoundWarning;
				objects = new Object[] {
					args[0]
				};
				break;
				
			case BoundIdentifierIndexOutOfBounds:
				// internal error
				problem = ParseProblem.InternalError;
				objects = new Object[0];
				break;
				
			case Circularity:
				problem = ParseProblem.CircularityError;
				objects = new Object[0];
				break;
				
			case InvalidTypeExpression:
				// internal error
				problem = ParseProblem.InternalError;
				objects = new Object[0];
				break;
				
			case LexerError:
				problem = ParseProblem.LexerError;
				objects = new Object[] {
						args[0]
				};			
				break;
				
			case LexerException:
				// internal error
				problem = ParseProblem.InternalError;
				objects = new Object[0];
				break;
				
			case ParserException:
				// internal error
				problem = ParseProblem.InternalError;
				objects = new Object[0];
				break;
				
			case SyntaxError:
				
				// TODO: prepare detailed error messages "args[0]" obtained from the parser for 
				//       internationalisation
				
				problem = ParseProblem.SyntaxError;
				objects = new Object[] {
						args[0]
				};						
				break;
				
			case TypeCheckFailure:
				problem = ParseProblem.TypeCheckError;
				objects = new Object[0];			
				break;
				
			case TypesDoNotMatch:
				problem = ParseProblem.TypesDoNotMatchError;
				objects = new Object[] {
						args[0],
						args[1]
				};						
				break;
				
			case TypeUnknown:
				problem = ParseProblem.TypeUnknownError;
				objects = new Object[0];			
				break;
				
			default:
				
				problem = ParseProblem.InternalError;
				objects = new Object[0];
				
				break;
			}
			
			if (location == null) {
				createProblemMarker(element, attributeType, problem, objects);
			} else {	
				createProblemMarker(
						element, attributeType, 
						location.getStart(), 
						location.getEnd(), problem, objects);
			}
		}
	}

	/**
	 * @param formulaElement the formula element
	 * @param freeIdentifierContext the free identifier context of this predicate
	 * (@see org.eventb.core.ast.Formula#isLegible(Collection))
	 * @param factory the formula factory to use 
	 * @return parsed formula, iff the formula was successfully parsed, <code>null</code> otherwise
	 * @throws CoreException if there was a problem accessing the database or the symbol table
	 */
	protected abstract F parseFormula(
			I formulaElement,
			Collection<FreeIdentifier> freeIdentifierContext,
			FormulaFactory factory) throws CoreException;
	
	/**
	 * @param formulaElement the formula element
	 * @param formula the parsed formula
	 * @return the inferred type environment
	 * @throws CoreException if there was a problem accessing the database or the symbol table
	 */
	protected ITypeEnvironment typeCheckFormula(
			I formulaElement,
			F formula,
			ITypeEnvironment typeEnvironment) throws CoreException {
		
		ITypeCheckResult typeCheckResult = formula.typeCheck(typeEnvironment);
		
		if (!typeCheckResult.isSuccess()) {
			issueASTProblemMarkers(formulaElement, getFormulaAttributeType(), typeCheckResult);
			
			return null;
		}
		
		return typeCheckResult.getInferredEnvironment();

	}
	
	protected abstract IAttributeType.String getFormulaAttributeType();

	protected boolean updateIdentifierSymbolTable(
			IInternalElement formulaElement,
			ITypeEnvironment inferredEnvironment, 
			ITypeEnvironment typeEnvironment) throws CoreException {
		
		if (inferredEnvironment.isEmpty())
			return true;
		
		ITypeEnvironment.IIterator iterator = inferredEnvironment.getIterator();
		while (iterator.hasNext()) {
			iterator.advance();
			createProblemMarker(
					formulaElement, 
					getFormulaAttributeType(), 
					GraphProblem.UntypedIdentifierError, 
					iterator.getName());
		}
		return false;
	}

		/**
	 * @param target the target static checked container
		 * @param modules additional rules for the predicate elements
		 * @param component the name of the component that contains the predicate elements
		 * @param repository the state repository
		 * @throws CoreException if there was a problem accessing the database or the symbol table
	 */
	protected void checkAndType(
			IInternalParent target,
			ISCFilterModule[] modules,
			String component,
			ISCStateRepository repository,
			IProgressMonitor monitor) throws CoreException {
		
		final FormulaFactory factory = repository.getFormulaFactory();
		
		final ITypeEnvironment typeEnvironment = typingState.getTypeEnvironment();
		
		final Collection<FreeIdentifier> freeIdentifiers = 
			identifierSymbolTable.getFreeIdentifiers();
		
		createParsedState(repository);
		
		initFilterModules(modules, repository, null);
		
		for (int i=0; i<formulaElements.size(); i++) {
			
			I formulaElement = formulaElements.get(i);
			
			ILabelSymbolInfo symbolInfo = 
				fetchLabel(
					formulaElement, 
					component,
					null);
			
			F formula = parseFormula(
					formulaElement,
					freeIdentifiers,
					factory);
			
			formulas.set(i, formula);
			
			boolean ok = formula != null;
			
			if (ok) {
				
				ok = symbolInfo != null;
				
				setParsedState(formula);
			
				if (!filterModules(modules, formulaElement, repository, null)) {
					// the predicate will be rejected
					// and will not contribute to the type environment!
					ok = false;
				}
				
				ITypeEnvironment inferredEnvironment = 
					typeCheckFormula(formulaElement, formula, typeEnvironment);
				
				ok &= inferredEnvironment != null;
			
				if (ok && !inferredEnvironment.isEmpty()) {
					ok = updateIdentifierSymbolTable(
							formulaElement,
							inferredEnvironment, 
							typeEnvironment);
				}
			}
			
			if (!ok) {
				if (symbolInfo != null)
					symbolInfo.setError();
				formulas.set(i, null);
			}
			
			setImmutable(symbolInfo);
			
			makeProgress(monitor);
			
		}
		
		endFilterModules(modules, repository, null);
		
		removeParsedState(repository);
	}

	protected void setImmutable(ILabelSymbolInfo symbolInfo) {
		if (symbolInfo != null)
			symbolInfo.makeImmutable();
	}
	
	private IParsedFormula parsedFormula;
	
	private void createParsedState(IStateRepository<ISCState> repository) throws CoreException {
		parsedFormula = new ParsedFormula();
		repository.setState(parsedFormula);
	}
	
	private void setParsedState(Formula formula) throws CoreException {
		parsedFormula.setFormula(formula);
	}
	
	private void removeParsedState(IStateRepository repository) throws CoreException {
		repository.removeState(IParsedFormula.STATE_TYPE);
	}
	
	protected abstract void makeProgress(IProgressMonitor monitor);
	
}

/*******************************************************************************
 * Copyright (c) 2008, 2013 Systerel and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License  v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Systerel - initial API and implementation
 *******************************************************************************/
package fr.systerel.editor.internal.editors;

import static org.eclipse.ui.actions.ActionFactory.REDO;
import static org.eclipse.ui.actions.ActionFactory.UNDO;
import static org.eclipse.ui.texteditor.ITextEditorActionDefinitionIds.TOGGLE_OVERWRITE;
import static org.rodinp.keyboard.preferences.PreferenceConstants.RODIN_MATH_FONT;

import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.operations.IUndoContext;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.commands.ActionHandler;
import org.eclipse.jface.resource.ImageRegistry;
import org.eclipse.jface.resource.JFaceResources;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.source.IAnnotationModel;
import org.eclipse.jface.text.source.ISourceViewer;
import org.eclipse.jface.text.source.IVerticalRuler;
import org.eclipse.jface.text.source.projection.ProjectionViewer;
import org.eclipse.jface.util.IPropertyChangeListener;
import org.eclipse.jface.util.PropertyChangeEvent;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.IActionBars;
import org.eclipse.ui.IPartListener;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.actions.ActionFactory;
import org.eclipse.ui.contexts.IContextActivation;
import org.eclipse.ui.contexts.IContextService;
import org.eclipse.ui.editors.text.TextEditor;
import org.eclipse.ui.operations.OperationHistoryActionHandler;
import org.eclipse.ui.texteditor.IElementStateListener;
import org.eclipse.ui.texteditor.ITextEditorActionConstants;
import org.eclipse.ui.texteditor.ITextEditorActionDefinitionIds;
import org.eventb.core.IContextRoot;
import org.eventb.core.IEventBRoot;
import org.eventb.core.IMachineRoot;
import org.eventb.ui.EventBUIPlugin;
import org.eventb.ui.IEventBSharedImages;
import org.eventb.ui.manipulation.ElementManipulationFacade;
import org.rodinp.core.IInternalElement;
import org.rodinp.core.IInternalElementType;
import org.rodinp.core.IRodinElement;
import org.rodinp.core.emf.api.itf.ILElement;
import org.rodinp.core.emf.api.itf.ILFile;

import fr.systerel.editor.EditorPlugin;
import fr.systerel.editor.internal.actions.HistoryAction;
import fr.systerel.editor.internal.documentModel.DocumentMapper;
import fr.systerel.editor.internal.documentModel.Interval;
import fr.systerel.editor.internal.documentModel.RodinDocumentProvider;
import fr.systerel.editor.internal.presentation.RodinConfiguration;
import fr.systerel.editor.internal.presentation.updaters.ProblemMarkerAnnotationsUpdater;

public class RodinEditor extends TextEditor implements IPropertyChangeListener {

	public static boolean DEBUG;
	
	public static final String EDITOR_ID = EditorPlugin.PLUGIN_ID
			+ ".editors.RodinEditor";
	public static final String EDITOR_SCOPE = EditorPlugin.PLUGIN_ID
			+ ".contexts.rodinEditorScope";

	private final RodinDocumentProvider documentProvider;
	private DocumentMapper mapper;
	private RodinConfiguration rodinViewerConfiguration;
	
	private IElementStateListener stateListener;
	private DNDManager dndManager;
	private IUndoContext  undoContext;
	
	/** The overlay editor to edit elements and attributes */
	private OverlayEditor overlayEditor;

	/** The source viewer on which projection for folding is enabled */
	private ProjectionViewer viewer;
	/** The font used by the underlying viewer */
	private Font font;
	/** The graphical text component carried by the viewer */
	private StyledText styledText;
	
	///** The support for folding on the viewer */
	//private ProjectionSupport projectionSupport;
	///** The annotation model containing folding annotations */
	//private ProjectionAnnotationModel projectionAnnotationModel;
	///** The basic annotations currently carried by the source viewer */
	//private Annotation[] oldPojectionAnnotations = new Annotation[0];
	
	/** The viewer's model of basic annotations (e.g. problem annotations) */
	 private IAnnotationModel annotationModel;

	/** A controller for selection on the styled text */
	private SelectionController selController;
	/** An updater for problem annotations which listens to the resource changes */
	private ProblemMarkerAnnotationsUpdater markerAnnotationsUpdater;
	/** A listener to update overlay editor's contents in case of indirect typing modification (e.g. undo-redo) */
	private OverlayBackModificationUpdater overlayUpdater;

	private IContextActivation specificContext;

	private IContextActivation defaultContext;

	private ContextMenuSimplifier contextMenuSimplifier;

	public RodinEditor() {
		setEditorContextMenuId(EDITOR_ID);
		documentProvider = new RodinDocumentProvider(mapper, this);
		setDocumentProvider(documentProvider);
		stateListener = EditorElementStateListener.getNewListener(this,
				documentProvider);
		documentProvider.addElementStateListener(stateListener);
	}

	@Override
	public void createPartControl(Composite parent) {
		super.createPartControl(parent);
		activateRodinEditorContext();
		viewer = (ProjectionViewer) getSourceViewer();
//		projectionSupport = new ProjectionSupport(viewer,
//				getAnnotationAccess(), getSharedColors());
//		projectionSupport.install();
//		viewer.doOperation(ProjectionViewer.TOGGLE);
//		projectionAnnotationModel = viewer.getProjectionAnnotationModel();
		annotationModel = viewer.getAnnotationModel();
		if (markerAnnotationsUpdater == null)
			markerAnnotationsUpdater = new ProblemMarkerAnnotationsUpdater(
					this, annotationModel);
	
		styledText = viewer.getTextWidget();
	
		overlayEditor = new OverlayEditor(styledText, mapper, viewer, this);
		overlayUpdater = new OverlayBackModificationUpdater(overlayEditor);
		getDocument().addDocumentListener(overlayUpdater);
		
		selController = new SelectionController(styledText, mapper, viewer,
				overlayEditor);
		getSite().setSelectionProvider(selController);
		styledText.addMouseListener(selController);
		styledText.addVerifyKeyListener(selController);
		dndManager = new DNDManager(selController, styledText, mapper,
				documentProvider);
		dndManager.install();
	
		font = JFaceResources.getFont(RODIN_MATH_FONT);
		JFaceResources.getFontRegistry().addListener(this);
		styledText.setFont(font);
		makeWideCaret();
		
		markerAnnotationsUpdater.initializeMarkersAnnotations();
	
		setTitleImageAndPartName();
		contextMenuSimplifier = ContextMenuSimplifier.startSimplifying(styledText.getMenu());
	}

	/**
	 * Let the caret be wider by using the 'insert overwrite' caret of the basic
	 * text editor. It was chosen to not create a custom caret, but rather reuse
	 * the action of enabling the overwrite mode, which changes the form of the
	 * caret (i.e. what is expected here). This is indeed allowed as the Rodin
	 * Editor presents a non-editable text.
	 */
	private void makeWideCaret() {
		final IAction action = getAction(TOGGLE_OVERWRITE);
		final ActionHandler actionHandler = new ActionHandler(action);
		try {
			actionHandler.execute(new ExecutionEvent());
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void setTitleImageAndPartName() {
		final IEventBRoot inputRoot = documentProvider.getInputRoot();
		final IInternalElementType<?> rootType = inputRoot.getElementType();
		String img = null;
		setPartName(inputRoot.getComponentName());
		if (rootType == IMachineRoot.ELEMENT_TYPE) {
			img = IEventBSharedImages.IMG_MACHINE;
		} else if (rootType == IContextRoot.ELEMENT_TYPE) {
			img = IEventBSharedImages.IMG_CONTEXT;
		}
		if (img != null) {
			final ImageRegistry imgReg = EventBUIPlugin.getDefault()
					.getImageRegistry();
			setTitleImage(imgReg.get(img));
		}
	}

	@Override
	public void dispose() {
		close(false);
		if (contextMenuSimplifier != null) {
			contextMenuSimplifier.finishSimplifying();
		}
		if (markerAnnotationsUpdater != null){
			markerAnnotationsUpdater.dispose();			
		}
		if (stateListener != null) {
			documentProvider.removeElementStateListener(stateListener);
		}
		if (overlayUpdater != null)
			getDocument().removeDocumentListener(overlayUpdater);
		JFaceResources.getFontRegistry().removeListener(this);
		documentProvider.unloadResource();
		deactivateRodinEditorContext();
		super.dispose();
	}

	@Override
	protected void initializeEditor() {
		super.initializeEditor();
		mapper = new DocumentMapper();
		rodinViewerConfiguration = new RodinConfiguration(this);
		setSourceViewerConfiguration(rodinViewerConfiguration);
	}
	
	@Override
	protected void initializeDragAndDrop(ISourceViewer viewer) {
		// Removing the drag and drop initialization done above by the
		// AbstractTextEditor
	}
	
	/**
	 * Creates a projection viewer to allow folding
	 */
	@Override
	protected ISourceViewer createSourceViewer(Composite parent,
			IVerticalRuler ruler, int styles) {
		final ISourceViewer viewer = new ProjectionViewer(parent, ruler,
				getOverviewRuler(), isOverviewRulerVisible(), styles);
		// ensure decoration support has been created and configured.
		getSourceViewerDecorationSupport(viewer);
		return viewer;
	}

	public void activateRodinEditorContext() {
		// Activate Event-B Editor Context
		final IContextService contextService = (IContextService) getSite()
				.getService(IContextService.class);
		final IInternalElement inputRoot = documentProvider.getInputRoot();
		if (inputRoot instanceof IMachineRoot) {
			specificContext = contextService
					.activateContext(EditorPlugin.PLUGIN_ID
							+ ".contexts.rodinEditorMachineScope");
		} else if (inputRoot instanceof IContextRoot) {
			specificContext = contextService
					.activateContext(EditorPlugin.PLUGIN_ID
							+ ".contexts.rodinEditorContextScope");
		}
		defaultContext = contextService.activateContext(EditorPlugin.PLUGIN_ID
				+ ".contexts.rodinEditorDefaultScope");
	}
	
	public void deactivateRodinEditorContext() {
		final IContextService contextService = (IContextService) getSite()
				.getService(IContextService.class);
		if (specificContext != null)
			contextService.deactivateContext(specificContext);
		if (defaultContext != null)
			contextService.deactivateContext(defaultContext);
	}
	
	/**
	 * It is mandatory to remove the actions so that the commands contributed
	 * through extension points are taken into account.
	 */
	@Override
	protected void createActions() {
		super.createActions();
		removeAction(ActionFactory.CUT.getId());
		removeAction(ActionFactory.COPY.getId());
		removeAction(ActionFactory.PASTE.getId());
		removeAction(ActionFactory.DELETE.getId());
		removeAction(ActionFactory.REFRESH.getId());
		removeAction(ITextEditorActionConstants.SHIFT_RIGHT);
		removeAction(ITextEditorActionConstants.SHIFT_LEFT);
		removeAction(ITextEditorActionConstants.MOVE_LINE_DOWN);
		removeAction(ITextEditorActionConstants.MOVE_LINE_UP);
		removeAction(ITextEditorActionDefinitionIds.SELECT_LINE_UP);
		removeAction(ITextEditorActionDefinitionIds.SELECT_LINE_DOWN);
		removeAction(ITextEditorActionDefinitionIds.LINE_END);
		removeAction(ITextEditorActionDefinitionIds.LINE_START);
		removeAction(ITextEditorActionDefinitionIds.SELECT_LINE_UP);
		removeAction(ITextEditorActionDefinitionIds.SELECT_LINE_DOWN);
	}

	@Override
	protected void createUndoRedoActions() {
		if (getUndoContext() != null) {
			final IWorkbenchWindow ww = getEditorSite().getWorkbenchWindow();
			final Action undoAction = new HistoryAction.Undo(ww);
			final Action redoAction = new HistoryAction.Redo(ww);
			final IPartListener listener = new PartListener();
			setHistoryHandler(UNDO.getId(), undoAction, listener);
			setHistoryHandler(REDO.getId(), redoAction, listener);
		}
	}
	
	private IUndoContext getUndoContext() {
		final IEventBRoot root = getInputRoot();
		if (root != null) {
			undoContext = ElementManipulationFacade.getRodinFileUndoContext(root);
		}
		return undoContext;
	}

	/**
	 * Set a global action handler for a undo/redo action and add the given part
	 * listener.
	 * */
	private void setHistoryHandler(String actionId, IAction handler,
			IPartListener listener) {
		if (!(getGlobalActionHandler(actionId) == handler)) {
			registerUndoRedoAction(actionId, (HistoryAction) handler);
			getEditorSite().getPage().addPartListener(listener);
		}
	}

	private IAction getGlobalActionHandler(String actionId) {
		final IActionBars bars = getEditorSite().getActionBars();
		return bars.getGlobalActionHandler(actionId);
	}

	/**
	 * Pushed down from AbstractTextEditor
	 */
	private void registerUndoRedoAction(String actionId, HistoryAction action) {
		final IAction oldAction = getAction(actionId);
		if (oldAction instanceof OperationHistoryActionHandler)
			((OperationHistoryActionHandler) oldAction).dispose();
		if (action == null)
			return;
		setAction(actionId, action);
		final IActionBars actionBars = getEditorSite().getActionBars();
		if (actionBars != null)
			actionBars.setGlobalActionHandler(actionId, action);
		action.setEnabled(false);
	}

	/**
	 * A listener to update the undo/redo action when the Event-B editor is
	 * activated.
	 */
	class PartListener implements IPartListener {
		
		private void refreshUndoRedoAction() {
			final IAction undoAction = getGlobalActionHandler(UNDO.getId());
			final IAction redoAction = getGlobalActionHandler(REDO.getId());
	
			if (undoAction instanceof HistoryAction
					&& redoAction instanceof HistoryAction) {
				((HistoryAction) undoAction).refresh();
				((HistoryAction) redoAction).refresh();
			}
		}
	
		@Override
		public void partActivated(IWorkbenchPart part) {
			refreshUndoRedoAction();
		}
	
		@Override
		public void partBroughtToTop(IWorkbenchPart part) {
			// do nothing
		}
	
		@Override
		public void partClosed(IWorkbenchPart part) {
			// do nothing
		}
	
		@Override
		public void partDeactivated(IWorkbenchPart part) {
			// do nothing
		}
	
		@Override
		public void partOpened(IWorkbenchPart part) {
			// do nothing
		}
	}

	private void removeAction(String actionId) {
		if (actionId == null)
			return;
		setAction(actionId, null);
	}
	
	/**
	 * Sets the selection. If the selection is a <code>IRodinElement</code> the
	 * corresponding area in the editor is highlighted
	 */
	@Override
	protected void doSetSelection(ISelection selection) {
		super.doSetSelection(selection);
		if (!selection.isEmpty() && selection instanceof IStructuredSelection) {
			final Interval interval = mapper
					.findInterval((IRodinElement) ((IStructuredSelection) selection)
							.getFirstElement());
			if (interval != null) {
				setHighlightRange(interval.getOffset(), interval.getLength(),
						true);
			}
		}
	}

	/**
	 * Aborts the current overlay edition. The modification are not saved. This
	 * has no effect if the overlay is inactive.
	 * <p>
	 * This method can be called from outside the UI thred.
	 * </p>
	 */
	public void abordEdition() {
		if (styledText != null && !styledText.isDisposed()) {
			final Display display = styledText.getDisplay();
			display.syncExec(new Runnable() {
				public void run() {
					if (overlayEditor.isActive()) {
						overlayEditor.abortEdition(true);
					}

				};
			});
		}
	}

	public int getCurrentOffset() {
		return styledText.getCaretOffset();
	}

	public DocumentMapper getDocumentMapper() {
		return mapper;
	}
	
	@Override
	public RodinDocumentProvider getDocumentProvider() {
		return documentProvider;
	}

	public OverlayEditor getOverlayEditor() {
		return overlayEditor;
	}

	public IDocument getDocument() {
		return documentProvider.getDocument();
	}

	public IEventBRoot getInputRoot() {
		return documentProvider.getInputRoot();
	}
	
	/** Returns the registered action or <code>null</code> if not found */
	public IAction getOverlayEditorAction(int actionConstant) {
		return overlayEditor.getOverlayAction(actionConstant);
	}

	public ILFile getResource() {
		return documentProvider.getResource();
	}
	
	public SelectionController getSelectionController() {
		return selController;
	}

	public StyledText getStyledText() {
		return styledText;
	}

	/** Tells if the overlay is currently visible as the user is editing */
	public boolean isOverlayActive() {
		return overlayEditor.isActive();
	}
	
	public boolean threadSafeIsOverlayActive() {
		final ActivationChecker checker = new ActivationChecker(this);
		getSite().getShell().getDisplay().syncExec(checker);
		return checker.getResult();
	}
	
	private static class ActivationChecker implements Runnable {

		boolean result = false;
		private RodinEditor editor;

		public ActivationChecker(RodinEditor editor) {
			this.editor = editor;
		}

		public void run() {
			this.result = editor.isOverlayActive();
		}

		public boolean getResult() {
			return result;
		}

	}
	
	@Override
	public void propertyChange(PropertyChangeEvent event) {
		if (event.getProperty().equals(RODIN_MATH_FONT)) {
			font = JFaceResources.getFont(RODIN_MATH_FONT);
			if (styledText == null || styledText.isDisposed()) {
				return;
			}
			styledText.setFont(font);
			resync(null, true);
		}
	}

	/**
	 * Refreshes the editor and avoids making the document dirty if the
	 * parameter <code>silent</code> is <code>true</code>. Indeed, this
	 * parameter represents the fact that nothing shall have changed (i.e. the
	 * user can not save it, as it is not supposed to be anything to save,
	 * typically in case of the "refresh" of the editor). Note: this is
	 * necessary, as the resynchronisation will make the underlying document
	 * change, even if there is no change in the rodin database.
	 */
	public void resync(final IProgressMonitor monitor, final boolean silent) {
		resync(monitor, silent, null);
	}
	
	/**
	 * See comment of resync(monitor, silent) method. Sets the caret at the 
	 * position of the first editable interval of the given element.
	 */
	public void resync(final IProgressMonitor monitor, final boolean silent,
			final ILElement newElement) {
		if (styledText != null && !styledText.isDisposed()) {
			final Display display = styledText.getDisplay();
			display.asyncExec(new Runnable() {
				@Override
				public void run() {
					if (styledText.isDisposed()) {
						return;
					}
					final int currentOffset = getCurrentOffset();
					final int topIndex = styledText.getTopIndex();
					final ILElement[] selection = selController
							.getSelectedElements();
					final long start = System.currentTimeMillis();
					if (DEBUG)
						System.out.println("\\ Start refreshing Rodin Editor.");
					documentProvider.synchronizeRoot(monitor, silent);
					styledText.setTopIndex(topIndex);
					if (newElement != null) {
						final int elemFirstOffset = mapper.findEditorElement(
								newElement).getOffset();
						final Interval elemEditInter = mapper
								.findEditableIntervalAfter(elemFirstOffset);
						styledText.setCaretOffset(elemEditInter.getOffset());
					} else {
						styledText.setCaretOffset(currentOffset);
					}
					selController.selectItems(selection);
					if (DEBUG) {
						System.out
								.println("\\ Finished refreshing Rodin Editor.");
						final long time = System.currentTimeMillis() - start;
						System.out.println("\\ Elapsed time : " + time + "ms.");
					}
					markerAnnotationsUpdater.recalculateAnnotations();
				}
			});
		}
	}

	public void reveal(EditPos pos) {
		selectAndReveal(pos.getOffset(), 0, pos.getOffset(), pos.getLength());
	}

//	/**
//	 * Replaces the old folding structure with the current one.
//	 */
//	public void updateFoldingStructure() {
//		for (Annotation a : oldPojectionAnnotations) {
//			projectionAnnotationModel.removeAnnotation(a);
//		}
//		final Position[] positions = mapper.getFoldingPositions();
//		final Annotation[] annotations = mapper.getFoldingAnnotations();
//		Assert.isLegal(annotations.length == positions.length);
//		// TODO use AnnotationModel.replaceAnnotations(Annotation[], Map)
//		for (int i = 0; i < positions.length; i++) {
//			projectionAnnotationModel.addAnnotation(annotations[i],
//					positions[i]);
//		}
//		oldPojectionAnnotations = annotations;
//	}

//	/**
//	 * Recalculates the old marker structure.
//	 */
//	public void updateMarkerStructure() {
//		markerAnnotationsUpdater.recalculateAnnotations();
//	}

}

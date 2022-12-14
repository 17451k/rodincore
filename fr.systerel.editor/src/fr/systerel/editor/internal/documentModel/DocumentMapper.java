/*******************************************************************************
 * Copyright (c) 2008, 2017 Systerel and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Systerel - initial API and implementation
 *******************************************************************************/
package fr.systerel.editor.internal.documentModel;

import static fr.systerel.editor.internal.actions.operations.RodinOperationUtils.isReadOnly;
import static fr.systerel.editor.internal.documentModel.DocumentElementUtils.getChildrenTypes;
import static fr.systerel.editor.internal.editors.EditPos.isValidStartEnd;
import static fr.systerel.editor.internal.editors.EditPos.newPosStartEnd;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.DocumentEvent;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.Position;
import org.eclipse.jface.text.source.projection.ProjectionAnnotation;
import org.eventb.ui.manipulation.IAttributeManipulation;
import org.rodinp.core.IElementType;
import org.rodinp.core.IInternalElementType;
import org.rodinp.core.IRodinElement;
import org.rodinp.core.emf.api.itf.ILElement;
import org.rodinp.core.emf.api.itf.ILUtils;

import fr.systerel.editor.internal.documentModel.ModelOperations.ModelPosition;
import fr.systerel.editor.internal.editors.EditPos;
import fr.systerel.editor.internal.handlers.context.ChildCreationInfo;
import fr.systerel.editor.internal.presentation.RodinConfiguration.ContentType;

/**
 * Maps <code>Intervals</code> to a document.
 * 
 * The following rules applies for Intervals:
 * <ul>
 * <li>an editable is never next to another editable interval.</li>
 * <li>for each offset there can be at most three intervals at that position.</li>
 * <li>only editable intervals can be zero length.</li>
 * </ul>
 */
public class DocumentMapper {
	
	public static boolean DEBUG;

	private ArrayList<Interval> intervals = new ArrayList<Interval>();
	private ILElement root;
	private Interval previous;
	private IDocument document;
	private RodinDocumentProvider documentProvider;

	private OrderedEditorItemMap editorElements = new OrderedEditorItemMap();
	private Map<IInternalElementType<?>, EditorSection> sections = new LinkedHashMap<IInternalElementType<?>, EditorSection>();

	/**
	 * Adds an interval to the document mapper at the end of the list. The
	 * intervals must be added in the order they appear in the text!
	 * 
	 * @param interval
	 * @throws Exception
	 */
	private void addInterval(Interval interval) throws Exception {
		if (intervals.size() > 0) {
			if (getLastInterval().compareTo(interval) > 0) {
				throw new Exception("Insertion must be sorted");
			}
		}
		intervals.add(interval);

	}

	/**
	 * Adds an interval to the document mapper just after a given interval. If
	 * the given previous interval is not found in the list, the new interval is
	 * added at the end of the list. The intervals must be added in the order
	 * they appear in the text!
	 * 
	 * @param interval
	 * @param previous
	 * @throws Exception
	 */
	private void addIntervalAfter(Interval interval, Interval previous) {
		final int index = intervals.indexOf(previous);
		if (index >= 0 && index < intervals.size()) {
			intervals.add(index + 1, interval);
		} else {
			try {
				addInterval(interval);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

	}
	
	public Interval findInterval(int offset) {
		final int index = findIntervalIndex(offset);
		if (index != -1) {
			return intervals.get(index);
		}
		return null;
	}

	public Interval findIntervalAfter(Interval inter) {
		final int index = intervals.indexOf(inter) + 1;
		if (index < intervals.size()) {
			return intervals.get(index);
		}
		return null;
	}
	
	public Interval[] findIntervalsBetween(int offset1, int offset2) {
		final List<Interval> result = new ArrayList<Interval>();
		final EditPos zone = EditPos.newPosStartEnd(offset1, offset2);
		for (Interval inter : intervals) {
			final EditPos pos = inter.getPos();
			if (zone.includes(pos) && inter.getLength() > 0) {
				result.add(inter);
			}
		}
		return result.toArray(new Interval[result.size()]);
	}

	/**
	 * Binary search to find an interval that contains an offset
	 * 
	 * @param offset
	 * @return
	 */
	private int findIntervalIndex(int offset) {
		int low = 0;
		int high = intervals.size() - 1;
		int mid;
		while (low <= high) {
			mid = (low + high) / 2;

			if (intervals.get(mid).getOffset() > offset) {
				high = mid - 1;
			} else if (	intervals.get(mid).getLastIndex() < offset) {
				low = mid + 1;
			} else
				return mid;
		}
		return -1;
	}

	/**
	 * Finds the first interval that contains an offset (includes intervals
	 * ending at that position)
	 * 
	 * @param offset
	 * @return
	 */
	private int findFirstIntervalIndex(int offset) {
		int result = findIntervalIndex(offset);
		// check the two previous intervals.
		if (result > 0) {
			final Interval previous = intervals.get(result - 1);
			if (previous.getLastIndex() >= offset) {
				result = result - 1;
			}
		}
		return result;
	}

	/**
	 * Finds an editable interval for a given offset
	 * 
	 * @param offset
	 * @return the editable interval at the given offset or <code>null</code> if
	 *         none exists.
	 */
	public Interval findEditableInterval(int offset) {
		final int index = findEditableIntervalIndex(offset);
		if (index >= 0) {
			return intervals.get(index);
		}
		return null;
	}

	/**
	 * Finds the first editable interval that starts after a given offset.
	 * 
	 * @param offset
	 * @return the first editable interval after the given offset or
	 *         <code>null</code> if none exists.
	 */
	public Interval findEditableIntervalAfter(int offset) {
		for (Interval interval : intervals) {
			final ILElement element = interval.getElement();
			if (interval.getOffset() > offset && interval.isEditable()
					&& element != null && !element.isImplicit()) {
				return interval;
			}
		}
		return null;
	}
	
	/**
	 * Finds the first interval that starts after a given offset and has a
	 * content type which is normally (kind of) editable.
	 * 
	 * @param offset
	 * @return the first kind of editable interval after the given offset or
	 *         <code>null</code> if none exists.
	 */
	public Interval findEditableKindOfIntervalAfter(int offset) {
		for (Interval interval : intervals) {
			final ILElement element = interval.getElement();
			if (interval.getOffset() > offset && interval.isKindOfEditable()
					&& element != null) {
				return interval;
			}
		}
		return null;
	}
	
	/**
	 * Finds the first interval that starts after a given offset which are from
	 * editable type. Includes the implicit children intervals.
	 * 
	 * @param offset
	 * @return the first editable interval after the given offset or
	 *         <code>null</code> if none exists.
	 */
	public Interval findPotentiallyEditableIntervalAfter(int offset) {
		for (Interval interval : intervals) {
			final ILElement element = interval.getElement();
			if (interval.getOffset() > offset && element != null) {
				return interval;
			}
		}
		return null;
	}
	
	/**
	 * Finds the first interval that starts after a given offset and has an element.
	 * 
	 * @param offset
	 * @return the first interval with element after the given offset or
	 *         <code>null</code> if none exists.
	 */
	public Interval findFirstElementIntervalAfter(int offset) {
		for (Interval interval : intervals) {
			if (interval.getOffset() > offset && interval.getElement() != null) {
				return interval;
			}
		}
		return null;
	}

	/**
	 * Finds the last editable interval that ends before a given offset.
	 * 
	 * @param offset
	 * @return the editable interval before given offset or <code>null</code> if
	 *         none exists.
	 */
	public Interval findEditableIntervalBefore(int offset) {
		Interval previous = null;
		for (Interval interval : intervals) {
			if (interval.getLastIndex() >= offset) {
				return previous;
			}
			final ILElement element = interval.getElement();
			if (interval.isEditable() && element != null
					&& !element.isImplicit()) {
				previous = interval;
			}
		}
		return null;
	}

	/**
	 * Finds the index of an interval that contains a given offset and is
	 * editable.
	 * 
	 * @param offset
	 * @return The index of the resulting interval or -1 if none was found.
	 */
	private int findEditableIntervalIndex(int offset) {
		// an editable is never next to another editable interval (or in the
		// same position)
		// for each offset there can be at most three intervals at that position
		// only editable intervals can be zero length.
		int index = findFirstIntervalIndex(offset);
		if (index >= 0 && index < intervals.size()) {
			Interval interval = intervals.get(index);
			if (interval.isEditable()) {
				return index;
			}
			// try the next one
			if (index + 1 < intervals.size()) {
				interval = intervals.get(index + 1);
				if (interval.containsOrTouches(offset)) {
					if (interval.isEditable()) {
						return index + 1;
					}
				}

			}
		}
		return -1;
	}

	/**
	 * Processes an interval. Creates and adds a new interval, if there exists
	 * none yet. Otherwise updates length and offset. It is expected that this
	 * method is called in the order the intervals appear in the document.
	 */
	private void processInterval(EditPos pos, ILElement element,
			ContentType contentType, IAttributeManipulation manipulation,
			boolean multiLine, String align, boolean addWhitespace) {
		Interval inter;
		final IInternalElementType<?> type = (element == null) ? null : element
				.getElementType();
		if (contentType.isEditable()) {
			inter = findInterval(element, contentType);
			if (inter != null) {
				inter.setPos(pos);
			} else {
				inter = new Interval(pos, element, type, contentType,
						manipulation, multiLine, addWhitespace);
				inter.setAlignement(align);
				try {
					addIntervalAfter(inter, previous);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		} else {
			if (intervals.indexOf(previous) < intervals.size() - 1) {
				inter = intervals.get(intervals.indexOf(previous) + 1);
				inter.setPos(pos);
			} else {
				inter = new Interval(pos, element, type, contentType,
						manipulation, multiLine, addWhitespace);
				inter.setAlignement(align);
				try {
					addInterval(inter);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
		previous = inter;
		if (element != null) {
			EditorElement el = editorElements.getOrCreate(element);
			el.addInterval(inter);
		}

	}

	/** 
	 * Processes the interval corresponding to the given region description.
	 */
	private void processInterval(EditorRegion r) {
		processInterval(r.getPos(), r.getElement(),
				r.getType(), r.getManipulation(), r.getMultiline(),
				r.getAlignement(), r.isAddWhitespace());
	}

	/**
	 * Processes a ordered list of regions as they appear in the document.
	 * 
	 * @param regions
	 *            the regions to create intervals for
	 */
	public void processIntervals(List<EditorRegion> regions) {
		for (EditorRegion region : regions) {
			processInterval(region);
		}
		updateElementFolding();
	}

	private void updateElementFolding() {
		for (EditorElement el : editorElements.getItems()) {
			final EditPos pos = getItemPosition(el);
			if (el.isFoldable() && pos != null) {
				el.setFoldingPosition(pos.getOffset(), pos.getLength() + 1);
			} else {
				el.clearFolding();
			}
		}
	}

	/**
	 * Finds the first interval that belongs to the given element
	 * 
	 * @param element
	 * @return the first interval that belongs to the given element
	 */
	public Interval findInterval(IRodinElement element) {
		if (element == null)
			return null;
		final EditorElement editorItem = findEditorElement(ILUtils.findElement(
				element, getRoot()));
		if (editorItem != null) {
			final List<Interval> itemIntervals = editorItem.getIntervals();
			if (itemIntervals.size() > 0) {
				return itemIntervals.get(0);
			}
		}
		return null;
	}
	
	public EditorElement findEditorElementAt(int offset) {
		for (EditorElement item : editorElements.getItems()) {
			if (item.explicitlyContains(offset)) {
				return item;
			}
		}
		return null;
	}
	
	/**
	 * Return all elements which are included in the zone covered between
	 * offset1 and offset2. This method assumes offset1 <= offset2.
	 * 
	 * @param offset1
	 *            the offset starting the zone
	 * @param offset2
	 *            the offset ending the zone
	 * @return all editor elements which are in the zone
	 */
	public EditorElement[] findEditorElementsBetween(int offset1, int offset2) {
		final List<EditorElement> result = new ArrayList<EditorElement>();
		final EditPos zone = EditPos.newPosStartEnd(offset1, offset2);
		for (EditorElement item : editorElements.getItems()) {
			final EditPos pos = item.getPos();
			if (zone.includes(pos)) {
				result.add(item);
			}
		}
		return result.toArray(new EditorElement[result.size()]);
	}

	public EditPos getItemPosition(ILElement element) {
		final EditorElement editorItem = findEditorElement(element);
		if (editorItem == null) return null;
		return getItemPosition(editorItem);
	}

	public EditPos getItemPosition(EditorElement editorItem) {
		final EditPos pos = editorItem.getPos();
		int start = pos.getStart();
		int end = pos.getEnd();
		if(!isValidStartEnd(start, end, false)) return null;
		return newPosStartEnd(start, end);
	}
	
	/**
	 * Finds the first interval that belongs to the given element and has a
	 * given contentType.
	 * 
	 * @param element
	 * @param contentType
	 * @return the first interval that belongs to the given element
	 */
	private Interval findInterval(ILElement element, ContentType contentType) {
		final EditorElement item = findEditorElement(element);
		if (item == null)
			return null;
		return item.getInterval(contentType);
	}

	public ArrayList<Interval> getIntervals() {
		return intervals;
	}

	public ILElement getRoot() {
		return root;
	}

	public void setRoot(ILElement root) {
		this.root = root;
	}

	private void resetPrevious() {
		previous = null;
	}

	public void addEditorSection(IInternalElementType<?> type,
			int folding_start, int folding_length) {
		EditorSection el = sections.get(type);
		if (el == null) {
			el = new EditorSection(type);
			sections.put(type, el);
		}
		el.setFoldingPosition(folding_start, folding_length);
	}

	public Position[] getFoldingPositions() {
		final List<Position> result = new ArrayList<Position>();
		addFoldingPositions(getElementsInOrder(), result);		
		if (DEBUG)
			System.out.println("folding " + root.getElement() + ": " + result);
		return result.toArray(new Position[result.size()]);
	}
	
	public List<EditorItem> getElementsInOrder() {
		final List<EditorItem> elems = new ArrayList<EditorItem>();
		elems.addAll(editorElements.getItems());
		elems.addAll(sections.values());
		Collections.sort(elems, new Comparator<EditorItem>() {

			@Override
			public int compare(EditorItem o1, EditorItem o2) {
				if (o1.getFoldingPosition() == null) {
					if (o2.getFoldingPosition() == null)
						return 0;
					return 1;
				} else if (o2.getFoldingPosition() == null) {
					return -1;
				}
				if (o1.getFoldingPosition().offset < o2.getFoldingPosition().offset)
					return -1;
				return 1;
			}
			
		});
		return elems;
	}
	

	private static void addFoldingPositions(
			Collection<? extends EditorItem> items, List<Position> positions) {
		for (EditorItem editorItem : items) {
			addIfNotNull(editorItem.getFoldingPosition(), positions);
		}
	}

	private static <T> void addIfNotNull(T obj, List<T> list) {
		if (obj != null) {
			list.add(obj);
		}
	}
	
	public ProjectionAnnotation[] getFoldingAnnotations() {
		final List<ProjectionAnnotation> result = new ArrayList<ProjectionAnnotation>();
		addFoldingAnnotations(getElementsInOrder(), result);
		return result.toArray(new ProjectionAnnotation[result.size()]);
	}
	
	private static void addFoldingAnnotations(
			Collection<? extends EditorItem> items,
			List<ProjectionAnnotation> annotations) {
		for (EditorItem editorItem : items) {
			addIfNotNull(editorItem.getFoldingAnnotation(), annotations);
		}
	}

	private Interval getLastInterval() {
		return intervals.get(intervals.size() - 1);
	}

	public void setDocument(IDocument document) {
		this.document = document;
	}

	/**
	 * Adapts the offset of the intervals starting from a given index in the
	 * list.
	 * 
	 * @param index
	 *            The first interval to adapt
	 * @param delta
	 *            The delta of change to the offset.
	 */
	private void adaptIntervalOffsetsFrom(int index, int delta) {
		if (index > 0 && delta != 0) {
			for (int i = index; i < intervals.size(); i++) {
				Interval interval = intervals.get(i);
				interval.setOffset(interval.getOffset() + delta);
			}
		}
	}

	/**
	 * Gets the text that is bound by the given interval from the underlying
	 * document.
	 * 
	 * @param interval
	 * @return The text to be found in the document within the interval bounds,
	 *         or <code>null</code> if the bounds of the interval do not conform
	 *         with the document.
	 */
	private String getTextFromDocument(Interval interval) {
		if (document != null) {
			try {
				return document.get(interval.getOffset(), interval.getLength());
			} catch (BadLocationException e) {
				e.printStackTrace();
			}
		}
		return null;
	}

	private void adaptAfter(Interval interval, int delta) {
		adaptIntervalOffsetsFrom(intervals.indexOf(interval) + 1, delta);
	}

	/**
	 * Synchronizes a given interval with its representation in the text. I.e.
	 * the text area in the document represented by the document is replaced by
	 * the new_text and the interval length is adapted accordingly. The folding
	 * positions are adapted to the changes and the interval offsets of the
	 * following intervals, too.
	 * 
	 * @param interval
	 *            The interval where the change happened
	 * @param newText
	 *            The new text to be set into that interval
	 */
	public void synchronizeInterval(Interval interval, String newText) {
		final String pNewText = RodinTextStream.processMulti(
				interval.isMultiLine(), interval.getAlignement(),
				interval.isAddWhiteSpace(), newText);
		final String old_text = getTextFromDocument(interval);
		if (!pNewText.equals(old_text)) {
			final int newTextLength = pNewText.length();
			final int delta = newTextLength
					- ((old_text == null) ? 0 : old_text.length());
			adaptAfter(interval, delta);
			documentProvider.replaceTextInDocument(interval, pNewText);
			interval.setLength(newTextLength);
		}
	}
	
	public void synchronizeIntervalWithoutModifyingDocument(Interval interval,
			DocumentEvent event) {
		final int oldTextLength = interval.getLength();
		final int newTextLength = event.getText().length();
		final int delta = newTextLength - oldTextLength;
		adaptAfter(interval, delta);
		interval.setLength(newTextLength);
	}
	
	public void setDocumentProvider(RodinDocumentProvider documentProvider) {
		this.documentProvider = documentProvider;
	}

	public EditorElement findEditorElement(int offset, int length) {
		for (EditorElement element : editorElements.getItems()) {
			if (element.getOffset() == offset && element.getLength() == length) {
				return element;
			}
		}
		return null;
	}

	public EditorElement findEditorElement(ILElement el) {
		return editorElements.get(el);
	}

	public EditorElement findEditorElement(IRodinElement rodinElement) {
		for (EditorElement element : editorElements.getItems()) {
			if (element.getRodinElement().equals(rodinElement)) {
				return element;
			}
		}
		return null;
	}

	/**
	 * Find a model position at the given offset.
	 * <p>
	 * If an element of the given sibling type is found after the offset, then
	 * returned position is just before the found sibling. Else if a parent of
	 * the given parent type is found before the offset, then returned position
	 * is the last child of the parent. Else <code>null</code> is returned.
	 * </p>
	 * 
	 * @param offset
	 *            an offset
	 * @param siblingType
	 *            a type
	 * @param parentType
	 *            a type
	 * @return a model position or <code>null</code>
	 */
	public ModelPosition findModelPosition(int offset,
			IElementType<?> siblingType, IElementType<?> parentType) {
		final ILElement parent = findILElementTypeAt(offset, parentType);
		final ILElement sibling = findElementAfter(offset, siblingType);
		if (parent != null) {
			final ILElement nextSibling = (sibling != null && !sibling
					.isImplicit()) ? sibling : null;
			return new ModelPosition(parent, nextSibling);
		}
		return null;
	}

	private ILElement findILElementTypeAt(int offset, IElementType<?> type) {
		for (EditorElement e : editorElements.getItems()) {
			final ILElement ilElement = e.getLightElement();
			final EditPos pos = e.getPos();
			final boolean includes = pos.isIncludedOrTouches(offset);
			if (includes && type.equals(ilElement.getElementType()) && pos.getEnd()!= offset) {
				return ilElement;
			}
		}
		return null;
	}

	// TODO with a given parent rather than a type
	private ILElement findElementAfter(int offset, IElementType<?> type) {
		final Interval intervalAfter = findEditableIntervalAfter(offset);
		if (intervalAfter == null)
			return null;
		return findElementFromTypeAt(intervalAfter.getLastIndex(), type);
	}

	private ILElement findElementFromTypeAt(int offset, IElementType<?> type) {
		final EditorElement item = findEditorElementAt(offset);
		if (item == null)
			return null;
		final ILElement element = findAncestorOftype(item.getLightElement(),
				type);
		return element;
	}

	private static ILElement findAncestorOftype(ILElement descendant,
			IElementType<?> type) {
		if (descendant.getElementType() == type)
			return descendant;
		final ILElement descParent = descendant.getParent();
		if (descParent == null) { // parent of root
			return null;
		}
		return findAncestorOftype(descParent, type);
	}

	public void reinitialize() {
		resetPrevious();
		intervals.clear();
		sections.clear();
		editorElements.clear();
	}

	public ChildCreationInfo getChildCreationPossibility(final int selOffset) {
		final int findIntervalIndex = findIntervalIndex(selOffset);
		if (findIntervalIndex != -1) {
			final Interval interval = intervals.get(findIntervalIndex);
			final ILElement element = interval.getElement();
			if (element != null) {
				return getChildTypesFor(element, null);
			}
		}
		return null;
	}
	
	public ChildCreationInfo getSiblingCreationPossibility(final int selOffset) {
		final int findIntervalIndex = findIntervalIndex(selOffset);
		if (findIntervalIndex != -1) {
			final Interval interval = intervals.get(findIntervalIndex);
			final ILElement element = interval.getElement();
			if (element == null)
				return null;
			ILElement parent = element.getParent();
			if (parent != null) {
				return getChildTypesFor(parent, element);
			}
			final Interval beforeElem = findEditableIntervalBefore(selOffset);
			if (beforeElem != null) {
				parent = beforeElem.getElement().getParent();
				if (parent != null)
				return getChildTypesFor(parent, null);
			}
		}
		return null;
	}

	public ChildCreationInfo getChildTypesFor(final ILElement element,
			ILElement sibling) {
		if (isReadOnly(element)) {
			return null;
		}
		final ILElement creationSibling;
		if (sibling != null) {
			final EditorElement siblingElem = findEditorElement(sibling);
			creationSibling = findElementAfter(siblingElem.getPos().getEnd(),
					sibling.getElementType());
		} else {
			creationSibling = null;
		}
		return new ChildCreationInfo(getChildrenTypes(element), element,
				creationSibling);
	}

}

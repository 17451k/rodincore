package org.rodinp.internal.core.index.tables;

import java.util.HashMap;
import java.util.Map;

import org.rodinp.core.IInternalElement;
import org.rodinp.core.IRodinFile;

public class ExportTable {

	// TODO consider removing the name
	Map<IRodinFile, Map<IInternalElement, String>> table;

	public ExportTable() {
		table = new HashMap<IRodinFile, Map<IInternalElement, String>>();
	}

	// TODO consider providing a type that hides the map
	public Map<IInternalElement, String> get(IRodinFile file) {
		final Map<IInternalElement, String> map = table.get(file);
		if (map == null) {
			return new HashMap<IInternalElement, String>();
		}
		return new HashMap<IInternalElement, String>(map);
	}

	/**
	 * Overwrites any previous mapping from the given file to the element, and
	 * from the given element to the name.
	 * 
	 * @param file
	 * @param element
	 * @param name
	 */
	public void add(IRodinFile file, IInternalElement element, String name) {
		Map<IInternalElement, String> map = table.get(file);
		if (map == null) {
			map = new HashMap<IInternalElement, String>();
			table.put(file, map);
		}
		map.put(element, name);
	}

	public void remove(IRodinFile file) {
		table.remove(file);
	}

	public void clear() {
		table.clear();
	}

}

package org.rodinp.internal.core.index;

import java.util.Collection;
import java.util.HashMap;

import org.rodinp.core.IInternalElement;
import org.rodinp.core.index.IDescriptor;
import org.rodinp.core.index.IRodinIndex;

public final class RodinIndex implements IRodinIndex {

	private HashMap<Object, IDescriptor> map;

	public RodinIndex() {
		map = new HashMap<Object, IDescriptor>();
	}

	public IDescriptor getDescriptor(Object key) {
		return map.get(key);
	}

	public Collection<IDescriptor> getDescriptors() {
		return map.values();
	}

	public IDescriptor makeDescriptor(IInternalElement element,
			String name) {
		IDescriptor descriptor = map.get(element);
		if (descriptor == null) {
			descriptor = new Descriptor(name, element);
			map.put(element, descriptor);
		} else { // requesting to make an already existing descriptor
			if (descriptor.getElement() != element
					|| !descriptor.getName().equals(name)) {
				// TODO: throw an exception
			}
			// else return the already existing one
			// as it is coherent with the requested one
		}
		return descriptor;
	}

	public void removeDescriptor(Object key) {
		map.remove(key);
	}

	// DEBUG
	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder("index\n");
		for (Object o : map.keySet()) {
			sb.append(map.get(o).toString() + "\n");
		}
		return sb.toString();
	}

}

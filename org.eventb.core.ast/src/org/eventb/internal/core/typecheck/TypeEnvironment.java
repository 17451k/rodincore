package org.eventb.internal.core.typecheck;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Set;
import java.util.Map.Entry;

import org.eventb.core.ast.FormulaFactory;
import org.eventb.core.ast.FreeIdentifier;
import org.eventb.core.ast.ITypeEnvironment;
import org.eventb.core.ast.Type;

/**
 * This class represents a type environment used to type check an event-B
 * formula.
 * <p>
 * A type environment is a map from names to their respective type.
 * </p>
 * 
 * @author François Terrier
 */
public class TypeEnvironment implements Cloneable, ITypeEnvironment {
	
	static class InternalIterator implements IIterator {
		
		Iterator<Map.Entry<String, Type>> iterator;
		
		Map.Entry<String, Type> current;

		public InternalIterator(Iterator<Map.Entry<String, Type>> iterator) {
			this.iterator = iterator;
			this.current = null;
		}
		
		public boolean hasNext() {
			return iterator.hasNext();
		}

		public void advance() throws NoSuchElementException {
			current = iterator.next();
		}

		public String getName() throws NoSuchElementException {
			if (current == null) {
				throw new NoSuchElementException();
			}
			return current.getKey();
		}

		public Type getType() throws NoSuchElementException {
			if (current == null) {
				throw new NoSuchElementException();
			}
			return current.getValue();
		}
	}

	public final FormulaFactory ff;
	
	// implementation
	private Map<String, Type> map;

	/**
	 * Constructs an initially empty type environment.
	 */
	public TypeEnvironment(FormulaFactory ff) {
		this.ff = ff;
		this.map = new HashMap<String, Type>();
	}

	/**
	 * Constructs a new type environment with the same map as the given one.
	 * 
	 * @param typenv
	 *            type environment to copy
	 */
	public TypeEnvironment(TypeEnvironment typenv) {
		this.ff = typenv.ff;
		this.map = new HashMap<String, Type>(typenv.map);
	}

	public void add(FreeIdentifier freeIdent) {
		addName(freeIdent.getName(), freeIdent.getType());
	}

	/* (non-Javadoc)
	 * @see org.eventb.core.ast.ITypeEnvironment#addAll(org.eventb.core.ast.ITypeEnvironment)
	 */
	public void addAll(ITypeEnvironment other) {
		Map<String, Type> otherMap = ((TypeEnvironment) other).map;
		// Use addName() to check for duplicates.
		for (Entry<String, Type> entry: otherMap.entrySet()) {
			addName(entry.getKey(), entry.getValue());
		}
	}
	

	/* (non-Javadoc)
	 * @see org.eventb.core.ast.ITypeEnvironment#addAll(org.eventb.core.ast.FreeIdentifier[])
	 */
	public void addAll(FreeIdentifier[] freeIdents) {
		for (FreeIdentifier freeIdent: freeIdents) {
			add(freeIdent);
		}
	}

	/* (non-Javadoc)
	 * @see org.eventb.core.ast.ITypeEnvironment#addGivenSet(java.lang.String)
	 */
	public void addGivenSet(String name) {
		addName(name, ff.makePowerSetType(ff.makeGivenType(name)));
	}

	/* (non-Javadoc)
	 * @see org.eventb.core.ast.ITypeEnvironment#addName(java.lang.String, org.eventb.core.ast.Type)
	 */
	public void addName(String name, Type type) {
		assert name != null && type != null;
		Type oldType = map.get(name);
		assert oldType == null || oldType.equals(type);
		map.put(name, type);
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#clone()
	 */
	@Override
	public ITypeEnvironment clone() {
		return new TypeEnvironment(this);
	}
	

	/* (non-Javadoc)
	 * @see org.eventb.core.ast.ITypeEnvironment#contains(java.lang.String)
	 */
	public boolean contains(String name) {
		return map.containsKey(name);
	}

	/* (non-Javadoc)
	 * @see org.eventb.core.ast.ITypeEnvironment#containsAll(org.eventb.internal.core.typecheck.TypeEnvironment)
	 */
	public boolean containsAll(ITypeEnvironment typenv){
		if (this == typenv)
			return true;
		final TypeEnvironment other = (TypeEnvironment) typenv;
		for (Entry<String, Type> entry: other.map.entrySet()){
			String name = entry.getKey();
			if (! entry.getValue().equals(this.getType(name)))
				return false;
		}
		return true;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj instanceof TypeEnvironment) {
			TypeEnvironment temp = (TypeEnvironment) obj;
			return map.equals(temp.map);
		}
		return false;
	}

	/* (non-Javadoc)
	 * @see org.eventb.core.ast.ITypeEnvironment#getIterator()
	 */
	public IIterator getIterator() {
		return new InternalIterator(map.entrySet().iterator());
	}

	/* (non-Javadoc)
	 * @see org.eventb.core.ast.ITypeEnvironment#getNames()
	 */
	public Set<String> getNames(){
		return map.keySet();
	}

	/* (non-Javadoc)
	 * @see org.eventb.core.ast.ITypeEnvironment#getType(java.lang.String)
	 */
	public Type getType(String name) {
		return map.get(name);
	}

	/*
	 * (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		return map.hashCode();
	}

	/* (non-Javadoc)
	 * @see org.eventb.core.ast.ITypeEnvironment#isEmpty()
	 */
	public boolean isEmpty() {
		return map.isEmpty();
	}

	// solves the unknown types (names who have type variable as their
	// corresponding type).
	public void solveVariables(TypeUnifier unifier) {
		for (Entry<String, Type> element: map.entrySet()) {
			element.setValue(unifier.solve(element.getValue()));
		}
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return map.toString();
	}

}

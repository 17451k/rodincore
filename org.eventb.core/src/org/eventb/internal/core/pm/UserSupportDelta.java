package org.eventb.internal.core.pm;

import java.util.ArrayList;

import org.eventb.core.pm.IProofStateDelta;
import org.eventb.core.pm.IUserSupport;
import org.eventb.core.pm.IUserSupportDelta;
import org.eventb.core.seqprover.IProofTreeDelta;

public class UserSupportDelta implements IUserSupportDelta {

	IUserSupport userSupport;

	/*
	 * @see IUserSupportDelta#getKind()
	 */
	private int kind = 0;

	/*
	 * @see IUserSupportDelta#getFlags()
	 */
	private int flags = 0;

	private IProofStateDelta[] affectedStates = emptyStates;

	/**
	 * Empty array of IProofStateDelta
	 */
	private static IProofStateDelta[] emptyStates = new IProofStateDelta[] {};

	// private Object[] information;

	public UserSupportDelta(IUserSupport userSupport) {
		this.userSupport = userSupport;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eventb.core.pm.IUserSupportDelta#getUserSupport()
	 */
	public IUserSupport getUserSupport() {
		return userSupport;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eventb.core.pm.IUserSupportDelta#getKind()
	 */
	public int getKind() {
		return kind;
	}

	/**
	 * Sets the kind of this delta - one of <code>ADDED</code>,
	 * <code>REMOVED</code>, or <code>CHANGED</code>.
	 * <p>
	 * 
	 * @param type
	 *            the kind to be set
	 */
	public void setKind(int type) {
		this.kind = type;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eventb.core.pm.IUserSupportDelta#getFlags()
	 */
	public int getFlags() {
		return flags;
	}

	/**
	 * Sets the changes flags to describe how an user support has changed.
	 * <p>
	 * 
	 * @param flags
	 *            that describe how an user support has changed
	 */
	public void setFlags(int flags) {
		this.flags = flags;
	}

	public IProofStateDelta[] getAddedProofStates() {
		return getStatesOfType(IProofStateDelta.ADDED);
	}

	public IProofStateDelta[] getRemovedProofStates() {
		return getStatesOfType(IProofStateDelta.REMOVED);
	}

	public IProofStateDelta[] getChangedProofStates() {
		return getStatesOfType(IProofStateDelta.CHANGED);
	}

	private IProofStateDelta[] getStatesOfType(int type) {
		int length = affectedStates.length;
		if (length == 0) {
			return new IProofStateDelta[] {};
		}
		ArrayList<IProofStateDelta> children = new ArrayList<IProofStateDelta>(
				length);
		for (int i = 0; i < length; i++) {
			if (affectedStates[i].getKind() == type) {
				children.add(affectedStates[i]);
			}
		}

		IProofStateDelta[] childrenOfType = new IProofStateDelta[children
				.size()];
		children.toArray(childrenOfType);

		return childrenOfType;
	}

	public IProofStateDelta[] getAffectedProofStates() {
		return affectedStates;
	}

	//
	// public Object[] getInformation() {
	// return information;
	// }

	protected void addAffectedProofState(IProofStateDelta affectedState) {
		switch (this.kind) {
		case ADDED:
		case REMOVED:
			// no need to add a proof state delta if this user support is added
			// or removed
			return;
		case CHANGED:
			this.flags |= F_STATE;
			break;
		default:
			this.kind = CHANGED;
			this.flags |= F_STATE;
		}
		if (affectedStates.length == 0) {
			affectedStates = new IProofStateDelta[] { affectedState };
			return;
		}
		IProofStateDelta existingState = null;
		int existingStateIndex = -1;
		if (affectedStates != null) {
			for (int i = 0; i < affectedStates.length; i++) {
				if (affectedStates[i].getProofState() == affectedState
						.getProofState()) {
					existingState = affectedStates[i];
					existingStateIndex = i;
					break;
				}
			}
		}
		if (existingState == null) { // new affected proof state
			affectedStates = growAndAddToArray(affectedStates, affectedState);
		} else {
			IProofTreeDelta existingProofTreeDelta = existingState
					.getProofTreeDelta();
			IProofTreeDelta affectedProofTreeDelta = affectedState
					.getProofTreeDelta();
			switch (existingState.getKind()) {
			case IProofStateDelta.ADDED:
				switch (affectedState.getKind()) {
				case IProofStateDelta.ADDED:
					// proof state was added then added -> it is added
				case IProofStateDelta.CHANGED:
					// proof state was added then changed -> it is added
					return;
				case IProofStateDelta.REMOVED:
					// proof state was added then removed -> noop
					affectedStates = this.removeAndShrinkArray(affectedStates,
							existingStateIndex);
					return;
				}
				break;
			case IProofStateDelta.REMOVED:
				switch (affectedState.getKind()) {
				case IProofStateDelta.ADDED:
					// proof state was removed then added -> it is changed
					((ProofStateDelta) affectedState)
							.setKind(IProofStateDelta.CHANGED);
					// affectedUserSupport.setFlags(F_CONTENT | F_CHILDREN |
					// F_REORDERED |
					// F_REPLACED);
					affectedStates[existingStateIndex] = affectedState;
					if (affectedProofTreeDelta == null
							&& existingProofTreeDelta != null) {
						((ProofStateDelta) affectedState)
								.setFlags(affectedState.getFlags()
										| IProofStateDelta.F_PROOFTREE);
						((ProofStateDelta) affectedState)
								.setProofTreeDelta(existingProofTreeDelta);
					}
					return;
				case IProofStateDelta.CHANGED:
					// proof state was removed then changed -> it is removed
				case IProofStateDelta.REMOVED:
					// proof state was removed then removed -> it is removed
					return;
				}
				break;
			case IProofStateDelta.CHANGED:
				switch (affectedState.getKind()) {
				case IProofStateDelta.ADDED:
					// user support was changed then added -> it is added
				case IProofStateDelta.REMOVED:
					// user support was changed then removed -> it is removed
					affectedStates[existingStateIndex] = affectedState;
					return;
				case IProofStateDelta.CHANGED:
					// update flags
					((ProofStateDelta) existingState).setFlags(existingState
							.getFlags()
							| affectedState.getFlags());
					// append information

					if (affectedProofTreeDelta != null
							&& existingProofTreeDelta == null) {
						((ProofStateDelta) existingState)
								.setFlags(existingState.getFlags()
										| IProofStateDelta.F_PROOFTREE);
						((ProofStateDelta) existingState)
								.setProofTreeDelta(affectedProofTreeDelta);
					}
					return;
				}
				break;
			default:
				// unknown -> existing user support becomes the user support
				// with the existing child's flags
				int stateFlags = existingState.getFlags();
				affectedStates[existingStateIndex] = affectedState;
				((ProofStateDelta) affectedState).setFlags(affectedState
						.getFlags()
						| stateFlags);
			}
		}
	}

	/**
	 * Adds the new element to a new array that contains all of the elements of
	 * the old array. Returns the new array.
	 */
	private IProofStateDelta[] growAndAddToArray(IProofStateDelta[] array,
			IProofStateDelta addition) {
		IProofStateDelta[] old = array;
		array = new IProofStateDelta[old.length + 1];
		System.arraycopy(old, 0, array, 0, old.length);
		array[old.length] = addition;
		return array;
	}

	/**
	 * Removes the element from the array. Returns the a new array which has
	 * shrunk.
	 */
	protected IProofStateDelta[] removeAndShrinkArray(IProofStateDelta[] old,
			int index) {
		IProofStateDelta[] array = new IProofStateDelta[old.length - 1];
		if (index > 0)
			System.arraycopy(old, 0, array, 0, index);
		int rest = old.length - index - 1;
		if (rest > 0)
			System.arraycopy(old, index + 1, array, index, rest);
		return array;
	}

	//
	// protected void mergeInformation(Object[] preinformation,
	// Object[] postinformation) {
	// information = new Object[preinformation.length + postinformation.length];
	// System.arraycopy(preinformation, 0, information, 0,
	// preinformation.length);
	// System.arraycopy(postinformation, 0, information,
	// preinformation.length, postinformation.length);
	// }

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();

		if (kind == ADDED)
			builder.append("[+] ");
		else if (kind == REMOVED)
			builder.append("[-] ");
		else if (kind == CHANGED)
			builder.append("[*] ");

		builder.append(userSupport.getInput()); // Can be null
		builder.append(" [");
		boolean sep = false;
		sep = toStringFlag(builder, F_CURRENT, "CURRENT", sep);
		sep = toStringFlag(builder, F_STATE, "STATE", sep);
		sep = toStringFlag(builder, F_INFORMATION, "INFORMATION", sep);
		builder.append("]");
		for (IProofStateDelta state : affectedStates) {
			builder.append("\n");
			builder.append(state.toString());
		}

		return builder.toString();
	}

	private boolean toStringFlag(StringBuilder builder, int flagToTest,
			String flagName, boolean sep) {

		if ((flags & flagToTest) != 0) {
			if (sep)
				builder.append('|');
			builder.append(flagName);
			return true;
		}
		return sep;
	}

}

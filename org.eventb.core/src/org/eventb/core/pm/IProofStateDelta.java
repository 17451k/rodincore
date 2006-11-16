package org.eventb.core.pm;

import java.util.List;

import org.eventb.core.seqprover.IProofTreeDelta;
import org.eventb.core.seqprover.IProofTreeNode;


public interface IProofStateDelta {
	
	public List<Object> getInformation();
	public IProofState getProofState();
	public IProofTreeNode getNewProofTreeNode();
	public IProofTreeDelta getProofTreeDelta();
	public boolean getNewSearch();
	public boolean getNewCache();
	public boolean isDeleted();
	public IUserSupport getSource();
	public boolean isNewProofState();
}

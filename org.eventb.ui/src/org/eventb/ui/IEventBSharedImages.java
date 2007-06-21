/*******************************************************************************
 * Copyright (c) 2006-2007 ETH Zurich.
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Rodin @ ETH Zurich
 ******************************************************************************/

package org.eventb.ui;

/**
 * A registry for common images used by the Event-B User Interface which may be useful 
 * to other plug-ins.
 * <p>
 * This inteface provides <code>Image</code> and <code>ImageDescriptor</code>s
 * for each named image in the interface.  All <code>Image</code> objects provided 
 * by this class are managed by this class and must never be disposed 
 * by other clients.
 * </p>
 * <p>
 * This interface is not intended to be implemented by clients.
 * </p>
 */

public interface IEventBSharedImages {

	/**
	 * Image IDs for RODIN Elements
	 */
	public static final String IMG_MACHINE = "Machine";

	public static final String IMG_CONTEXT = "Context";

	public static final String IMG_VARIABLES = "Variables";

	public static final String IMG_INVARIANTS = "Invariants";

	public static final String IMG_THEOREMS = "Theorems";

	public static final String IMG_EVENTS = "Events";

	public static final String IMG_CARRIER_SETS = "CarrierSets";

	public static final String IMG_CONSTANTS = "Constants";

	public static final String IMG_AXIOMS = "Axioms";

	public static final String IMG_DISCHARGED = "Discharged";

	public static final String IMG_DISCHARGED_BROKEN = "Discharged Broken";

	public static final String IMG_PENDING = "Pending";

	public static final String IMG_PENDING_BROKEN = "Pending Broken";

	public static final String IMG_REVIEWED = "REVIEWED";

	public static final String IMG_REVIEWED_BROKEN = "Reviewed Broken";

	public static final String IMG_APPLIED = "Applied";

	public static final String IMG_UNATTEMPTED = "Unattempted";

	public static final String IMG_DEFAULT = "Default";

	public static final String IMG_REFINES = "Refines";

	public static final String IMG_NULL = "NULL";

	public static final String IMG_ADD = "Add";
	
	public static final String IMG_REMOVE = "Remove";
	
	public static final String IMG_COLLAPSED = "Collapsed";
	
	public static final String IMG_COLLAPSED_HOVER = "Collapsed Hover";

	public static final String IMG_EXPANDED = "Expanded";
	
	public static final String IMG_EXPANDED_HOVER = "Expanded Hover";	
	
	/**
	 * Paths to the icons for buttons, menus, etc.
	 */
	public static final String IMG_NEW_PROJECT_PATH = "icons/full/clcl16/newprj_wiz.gif";

	public static final String IMG_NEW_COMPONENT_PATH = "icons/full/clcl16/newcomp_wiz.gif";

	public static final String IMG_NEW_VARIABLES_PATH = "icons/full/ctool16/newvar_edit.gif";

	public static final String IMG_NEW_INVARIANTS_PATH = "icons/full/ctool16/newinv_edit.gif";

	public static final String IMG_NEW_THEOREMS_PATH = "icons/full/ctool16/newthm_edit.gif";

	public static final String IMG_NEW_EVENT_PATH = "icons/full/ctool16/newevt_edit.gif";

	public static final String IMG_NEW_CARRIER_SETS_PATH = "icons/full/ctool16/newset_edit.gif";

	public static final String IMG_NEW_CONSTANTS_PATH = "icons/full/ctool16/newcst_edit.gif";

	public static final String IMG_NEW_AXIOMS_PATH = "icons/full/ctool16/newaxm_edit.gif";

	public static final String IMG_NEW_GUARD_PATH = "icons/full/ctool16/newgrd_edit.gif";

	public static final String IMG_NEW_ACTION_PATH = "icons/full/ctool16/newact_edit.gif";

	public static final String IMG_UP_PATH = "icons/full/ctool16/up_edit.gif";

	public static final String IMG_DOWN_PATH = "icons/full/ctool16/down_edit.gif";

	public static final String IMG_MACHINE_PATH = "icons/full/obj16/mch_obj.gif";

	public static final String IMG_REFINE_OVERLAY_PATH = "icons/full/ovr16/ref_ovr.gif";

	public static final String IMG_COMMENT_OVERLAY_PATH = "icons/full/ovr16/cmt_ovr.gif";

	public static final String IMG_AUTO_OVERLAY_PATH = "icons/full/ovr16/auto_ovr.gif";

	public static final String IMG_WARNING_OVERLAY_PATH = "icons/full/ovr16/warning_ovr.gif";

	public static final String IMG_ERROR_OVERLAY_PATH = "icons/full/ovr16/error_ovr.gif";

	public static final String IMG_EXPERT_MODE_PATH = "icons/full/ctool16/xp_prover.gif";

	public static final String IMG_PENDING_PATH = "icons/pending.gif";

	public static final String IMG_PENDING_PALE_PATH = "icons/pending_pale.gif";

	public static final String IMG_PENDING_BROKEN_PATH = "icons/pending_broken.gif";

	/**
	 * @deprecated used IMG_PENDING_PALE_PATH instead.
	 */
	@Deprecated
	public static final String IMG_APPLIED_PATH = "icons/applied.gif";

	public static final String IMG_REVIEWED_PATH = "icons/reviewed.gif";

	public static final String IMG_REVIEWED_PALE_PATH = "icons/reviewed_pale.gif";

	public static final String IMG_REVIEWED_BROKEN_PATH = "icons/reviewed_broken.gif";

	public static final String IMG_DISCHARGED_PATH = "icons/discharged.gif";

	public static final String IMG_DISCHARGED_PALE_PATH = "icons/discharged_pale.gif";

	public static final String IMG_DISCHARGED_BROKEN_PATH = "icons/discharged_broken.gif";

	public static final String IMG_UNATTEMPTED_PATH = "icons/unattempted.gif";

	public static final String IMG_REFINES_PATH = "icons/full/ctool16/refines.gif";

	public static final String IMG_NULL_PATH = "icons/full/ctool16/null.gif";

	public static final String IMG_SHOW_GOAL_PATH = "icons/full/ctool16/showgoal.gif";

}

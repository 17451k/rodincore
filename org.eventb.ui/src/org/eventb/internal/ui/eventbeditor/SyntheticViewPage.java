package org.eventb.internal.ui.eventbeditor;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.forms.IManagedForm;
import org.eclipse.ui.forms.editor.FormEditor;
import org.eclipse.ui.forms.widgets.Section;
import org.eventb.core.IMachine;

public class SyntheticViewPage
	extends EventBFormPage 
{
	// Title, tab title and ID of the page.
	public static final String PAGE_ID = "Synthetic View"; //$NON-NLS-1$
	public static final String PAGE_TITLE = "Synthetic View";
	public static final String PAGE_TAB_TITLE = "Synthetic";
	
	/**
	 * Constructor.
	 * @param editor The form editor that holds the page 
	 */
	public SyntheticViewPage(FormEditor editor) {
		super(editor, PAGE_ID, PAGE_TITLE, PAGE_TAB_TITLE);  //$NON-NLS-1$
	}

	/* (non-Javadoc)
	 * @see org.eventb.internal.ui.eventbeditor.EventBFormPage#createMasterSection(org.eclipse.ui.forms.IManagedForm, org.eclipse.swt.widgets.Composite, int, org.eventb.internal.ui.eventbeditor.EventBEditor)
	 */
	@Override
	protected EventBPartWithButtons createMasterSection(IManagedForm managedForm, Composite parent, int style, EventBEditor editor) {
		EventBPartWithButtons part;
		if (((EventBEditor) this.getEditor()).getRodinInput() instanceof IMachine) 
			part = new SyntheticMachineViewSection(managedForm, parent, managedForm.getToolkit(), Section.NO_TITLE, (EventBEditor) this.getEditor());
		else
			part = new SyntheticContextViewSection(managedForm, parent, managedForm.getToolkit(), Section.NO_TITLE, (EventBEditor) this.getEditor());
		GridData gd = new GridData(SWT.FILL, SWT.FILL, true, true);
		gd.heightHint = 200;
		gd.minimumHeight = 150;
		gd.widthHint = 150;
		part.getSection().setLayoutData(gd);
		return part;
	}
	
}

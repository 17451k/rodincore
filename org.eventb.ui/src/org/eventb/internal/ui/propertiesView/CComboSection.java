package org.eventb.internal.ui.propertiesView;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CCombo;
import org.eclipse.swt.custom.CLabel;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.views.properties.tabbed.AbstractPropertySection;
import org.eclipse.ui.views.properties.tabbed.ITabbedPropertyConstants;
import org.eclipse.ui.views.properties.tabbed.TabbedPropertySheetPage;
import org.eventb.internal.ui.EventBUIExceptionHandler;
import org.rodinp.core.ElementChangedEvent;
import org.rodinp.core.IElementChangedListener;
import org.rodinp.core.IInternalElement;
import org.rodinp.core.RodinCore;
import org.rodinp.core.RodinDBException;

public abstract class CComboSection extends AbstractPropertySection implements
		IElementChangedListener {

	CCombo comboWidget;

	IInternalElement element;

	public CComboSection() {
		// Do nothing
	}

	@Override
	public void createControls(Composite parent,
			TabbedPropertySheetPage aTabbedPropertySheetPage) {
		super.createControls(parent, aTabbedPropertySheetPage);

		Composite composite = getWidgetFactory()
				.createFlatFormComposite(parent);
		FormData data;

		comboWidget = getWidgetFactory().createCCombo(composite,
				SWT.DEFAULT);

		data = new FormData();
		data.left = new FormAttachment(0, STANDARD_LABEL_WIDTH);
		data.right = new FormAttachment(100, 0);
		data.top = new FormAttachment(0, ITabbedPropertyConstants.VSPACE);
		comboWidget.setLayoutData(data);

		comboWidget.addSelectionListener(new SelectionListener() {

			public void widgetDefaultSelected(SelectionEvent e) {
				widgetSelected(e);
			}

			public void widgetSelected(SelectionEvent e) {
				try {
					setText(comboWidget.getText(), new NullProgressMonitor());
				} catch (RodinDBException exception) {
					EventBUIExceptionHandler
							.handleSetAttributeException(exception);
				}
			}

		});

		CLabel labelLabel = getWidgetFactory().createCLabel(composite,
				getLabel() + ":");
		data = new FormData();
		data.left = new FormAttachment(0, 0);
		data.right = new FormAttachment(comboWidget,
				ITabbedPropertyConstants.HSPACE);
		data.top = new FormAttachment(comboWidget, 0, SWT.CENTER);
		labelLabel.setLayoutData(data);
	}

	abstract String getLabel();

	abstract void setText(String text, IProgressMonitor monitor)
			throws RodinDBException;

	abstract void setData();

	abstract String getText() throws RodinDBException;

	@Override
	public void refresh() {
		if (comboWidget.isDisposed())
			return;
		
		try {
			comboWidget.removeAll();
			setData();
			comboWidget.setText(getText());
		} catch (RodinDBException e) {
			e.printStackTrace();
		}
		super.refresh();
	}

	@Override
	public void setInput(IWorkbenchPart part, ISelection selection) {
		super.setInput(part, selection);
		if (selection instanceof IStructuredSelection) {
			Object input = ((IStructuredSelection) selection).getFirstElement();
			if (input instanceof IInternalElement) {
				this.element = (IInternalElement) input;
			}
		}
		refresh();
	}

	public void elementChanged(ElementChangedEvent event) {
		// TODO Filter out the delta first
		if (comboWidget.isDisposed())
			return;
		Display display = comboWidget.getDisplay();
		display.asyncExec(new Runnable() {

			public void run() {
				refresh();
			}

		});
	}

	@Override
	public void aboutToBeHidden() {
		RodinCore.removeElementChangedListener(this);
		super.aboutToBeHidden();
	}

	@Override
	public void aboutToBeShown() {
		RodinCore.addElementChangedListener(this);
		super.aboutToBeShown();
	}

}

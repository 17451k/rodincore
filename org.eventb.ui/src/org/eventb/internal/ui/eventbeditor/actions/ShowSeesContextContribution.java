package org.eventb.internal.ui.eventbeditor.actions;

import org.eclipse.jface.action.ContributionItem;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eventb.core.IEventBProject;
import org.eventb.core.ISeesContext;
import org.eventb.internal.ui.EventBImage;
import org.eventb.internal.ui.UIUtils;
import org.eventb.ui.IEventBSharedImages;
import org.rodinp.core.IRodinElement;
import org.rodinp.core.IRodinFile;
import org.rodinp.core.IRodinProject;
import org.rodinp.core.RodinDBException;

public class ShowSeesContextContribution extends ContributionItem {

	private IRodinFile file;

	private IEventBProject evbProject;

	public ShowSeesContextContribution(IRodinFile file) {
		this.file = file;
		IRodinProject rp = file.getRodinProject();
		evbProject = (IEventBProject) rp.getAdapter(IEventBProject.class);
	}

	@Override
	public void fill(Menu menu, int index) {

		try {
			IRodinElement[] elements = file
					.getChildrenOfType(ISeesContext.ELEMENT_TYPE);
			for (IRodinElement element : elements) {
				ISeesContext seesContext = (ISeesContext) element;
				String name = seesContext.getSeenContextName();
				IRodinFile contextFile = evbProject.getContextFile(name);
				if (contextFile != null & contextFile.exists()) {
					createMenuItem(menu, contextFile);
					// submenu.add(new ShowSeesContext(contextFile));
				}
			}
		} catch (RodinDBException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private void createMenuItem(Menu menu, final IRodinFile contextFile) {
		final MenuItem menuItem = new MenuItem(menu, SWT.PUSH);
		menuItem.setText(contextFile.getBareName());
		menuItem.setImage(EventBImage.getImage(IEventBSharedImages.IMG_CONTEXT));

		Listener listener = new Listener() {
			public void handleEvent(Event event) {
				switch (event.type) {
				case SWT.Selection:
					UIUtils.linkToEventBEditor(contextFile);
					break;
				}
			}
		};
		menuItem.addListener(SWT.Selection, listener);

	}

}

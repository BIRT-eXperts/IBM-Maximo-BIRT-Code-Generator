package com.mmkarton.mx7.reportgenerator.wizards;

/*
 ********************************************************************************
 * Copyright (c) 2009 Ing. Gerd Stockner (Mayr-Melnhof Karton Gesellschaft m.b.H.), Christian Voller (Mayr-Melnhof Karton Gesellschaft m.b.H.), CoSMIT GmbH
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *  Ing. Gerd Stockner (Mayr-Melnhof Karton Gesellschaft m.b.H.) - initial API and implementation
 *  Christian Voller (Mayr-Melnhof Karton Gesellschaft m.b.H.) - initial API and implementation
 *  CoSMIT GmbH - publishing, maintenance
 *******************************************************************************/

import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.Path;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.wizard.IWizardPage;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.dialogs.ContainerSelectionDialog;

import com.mmkarton.mx7.reportgenerator.engine.MAXIMOReportDesignerUtil;

/**
 * The "New" wizard page allows setting the container for the new file as well
 * as the file name. The page will only accept file name without the extension
 * OR with the extension that matches the expected one (rptdesign).
 */

public class BIRTReportWizardPage extends WizardPage {
	private Text containerText;
	private Text ReportTemplateText;

	private Text reportTitleText;
	private Text reportDescriptionText;
	private Text reportAuthorText;
	private Text fileText;
	
	private boolean canflip=false;

	private ISelection selection;
	/**
	 * Constructor for SampleNewWizardPage.
	 * 
	 * @param pageName
	 */
	public BIRTReportWizardPage(ISelection selection) {
		super(MAXIMOReportDesignerUtil.titleName);
		setTitle(MAXIMOReportDesignerUtil.titleName);
		setDescription(MAXIMOReportDesignerUtil.titleName);
		this.selection = selection;
	}

	/**
	 * @see IDialogPage#createControl(Composite)
	 */
	public void createControl(Composite parent) 
	{
		Composite container = new Composite(parent, SWT.NULL);
		GridLayout layout = new GridLayout();
		container.setLayout(layout);
		layout.numColumns = 3;
		layout.verticalSpacing = 9;

		Label label = new Label(container, SWT.NULL);
		label.setText("&Report-Dir:");

		containerText = new Text(container, SWT.BORDER | SWT.SINGLE);
		GridData gd = new GridData(GridData.FILL_HORIZONTAL);
		containerText.setLayoutData(gd);
		containerText.addModifyListener(new ModifyListener() {
			public void modifyText(ModifyEvent e) {
				dialogChanged();
			}
		});

		Button button = new Button(container, SWT.PUSH);
		button.setText("Browse...");
		button.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				handleBrowse(containerText);
			}
		});
		
		Label templatelabel = new Label(container, SWT.NULL);
		templatelabel.setText("&Report Template:");
		
		ReportTemplateText = new Text(container, SWT.BORDER | SWT.SINGLE);
		GridData gd2 = new GridData(GridData.FILL_HORIZONTAL);
		ReportTemplateText.setLayoutData(gd2);
		ReportTemplateText.addModifyListener(new ModifyListener() {
			public void modifyText(ModifyEvent e) {
				dialogChanged();
			}
		});

		Button templateButton = new Button(container, SWT.PUSH);
		templateButton.setText("Browse...");
		templateButton.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				handleReportTemplate(ReportTemplateText);
			}
		});

		//*****************Filename Part****************************
		label = new Label(container, SWT.NULL);
		label.setText("&File name:");

		fileText = new Text(container, SWT.BORDER | SWT.SINGLE);
		gd = new GridData(GridData.FILL_HORIZONTAL);
		fileText.setLayoutData(gd);
		fileText.addModifyListener(new ModifyListener() {
			public void modifyText(ModifyEvent e) {
				dialogChanged();
			}
		});
		
		label = new Label(container, SWT.NULL);
		label.setText("");

		//*****************Report Title Part****************************
		label = new Label(container, SWT.NULL);
		label.setText("&Report Title:");

		reportTitleText = new Text(container, SWT.BORDER | SWT.SINGLE);
		gd = new GridData(GridData.FILL_HORIZONTAL);
		reportTitleText.setLayoutData(gd);
		/*reportTitleText.addModifyListener(new ModifyListener() {
			public void modifyText(ModifyEvent e) {
				dialogChanged();
			}
		});*/
		
		label = new Label(container, SWT.NULL);
		label.setText("");

//		*****************Report Author Part****************************
		label = new Label(container, SWT.NULL);
		label.setText("&Report Author:");

		reportAuthorText = new Text(container, SWT.BORDER | SWT.SINGLE);
		gd = new GridData(GridData.FILL_HORIZONTAL);
		reportAuthorText.setLayoutData(gd);
		/*reportTitleText.addModifyListener(new ModifyListener() {
			public void modifyText(ModifyEvent e) {
				dialogChanged();
			}
		});*/
		
		label = new Label(container, SWT.NULL);
		label.setText("");
		
//		*****************Report Description Part****************************
		label = new Label(container, SWT.NULL);
		label.setText("&Report Description:");

		reportDescriptionText = new Text(container, SWT.BORDER | SWT.SINGLE);
		gd = new GridData(GridData.FILL_HORIZONTAL);
		reportDescriptionText.setLayoutData(gd);
		/*reportDescriptionText.addModifyListener(new ModifyListener() {
			public void modifyText(ModifyEvent e) {
				dialogChanged();
			}
		});*/
		
		label = new Label(container, SWT.NULL);
		label.setText("");
		
		
		
		initialize();
		dialogChanged();
		setControl(container);
		setPageComplete(false);
		canflip=false;
	}
	
	
	
	@Override
	public boolean canFlipToNextPage() 
	{
		
		return canflip;
		//return true;
	}

	/**
	 * Tests if the current workbench selection is a suitable container to use.
	 */

	private void initialize() 
	{
		if (selection != null && selection.isEmpty() == false
				&& selection instanceof IStructuredSelection) {
			IStructuredSelection ssel = (IStructuredSelection) selection;
			if (ssel.size() > 1)
				return;
			Object obj = ssel.getFirstElement();
			if (obj instanceof IResource) {
				IContainer container;
				if (obj instanceof IContainer)
					container = (IContainer) obj;
				else
					container = ((IResource) obj).getParent();
				containerText.setText(container.getFullPath().toString());
			}
		}
		fileText.setText("mm_new_report.rptdesign");
	}

	/**
	 * Uses the standard container selection dialog to choose the new value for
	 * the container field.
	 */

	private void handleBrowse(Text text) {
		ContainerSelectionDialog dialog = new ContainerSelectionDialog(
				getShell(), ResourcesPlugin.getWorkspace().getRoot(), false,
				"Select new file container");
		if (dialog.open() == ContainerSelectionDialog.OK) {
			Object[] result = dialog.getResult();
			if (result.length == 1) {
				text.setText(((Path) result[0]).toString());
			}
		}
	}
	
	private void handleReportTemplate(Text text) 
	{
		FileDialog filedialog=new FileDialog(getShell(),SWT.OPEN);
		filedialog.setText("Choose Report Template file.rpttemplate");
		filedialog.setFilterPath(ResourcesPlugin.getWorkspace().getRoot().toString());
		String[] filterExt = { "*.rpttemplate","*.rptdesign" };
		filedialog.setFilterExtensions(filterExt);

		//if (filedialog.open() == ContainerSelectionDialog.OK) 
		{
			String result = filedialog.open();
			if (result.length()>= 1) {
				text.setText(result);
			}
		}
	}

	/**
	 * Ensures that both text fields are set.
	 */

	private void dialogChanged() {
		IResource container = ResourcesPlugin.getWorkspace().getRoot()
				.findMember(new Path(getContainerName()));
		String fileName = getFileName();

		setPageComplete(false);
		canflip=false;
		
		if (getContainerName().length() == 0) {
			updateStatus("File container must be specified");
			return;
		}
		if (getReportTemplateText().length() == 0) {
			updateStatus("Template must be specified");
			return;
		}
		
		if (container == null
				|| (container.getType() & (IResource.PROJECT | IResource.FOLDER)) == 0) {
			updateStatus("File container must exist");
			return;
		}
		if (!container.isAccessible()) {
			updateStatus("Project must be writable");
			return;
		}
		if (fileName.length() == 0) {
			updateStatus("File name must be specified");
			return;
		}
		if (fileName.replace('\\', '/').indexOf('/', 1) > 0) {
			updateStatus("File name must be valid");
			return;
		}
		int dotLoc = fileName.lastIndexOf('.');
		if (dotLoc != -1) {
			String ext = fileName.substring(dotLoc + 1);
			if (ext.equalsIgnoreCase("rptdesign") == false) {
				updateStatus("File extension must be \"rptdesign\"");
				return;
			}
		}
		canflip=true;
		updateStatus(null);
		setPageComplete(true);
		
	}

	private void updateStatus(String message) {
		setErrorMessage(message);
		setPageComplete(message == null);
	}

	public String getContainerName() {
		return containerText.getText();
	}
	
	public String getReportTemplateText() {
		return ReportTemplateText.getText();
	}
	

	public String getFileName() {
		return fileText.getText();
	}

	public String getReportAuthorText() {
		return reportAuthorText.getText();
	}

	public String getReportDescriptionText() {
		return reportDescriptionText.getText();
	}

	public String getReportTitleText() {
		return reportTitleText.getText();
	}
	
	
}
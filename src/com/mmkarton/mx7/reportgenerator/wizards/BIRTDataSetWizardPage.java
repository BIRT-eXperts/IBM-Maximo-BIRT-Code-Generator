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

import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;

import com.mmkarton.mx7.reportgenerator.engine.MAXIMOReportDesignerUtil;
import com.mmkarton.mx7.reportgenerator.util.Utility;

public class BIRTDataSetWizardPage extends WizardPage 
{
	private ISelection selection;
	private Text DataSetNameText;
	private boolean canflip=false;

	public BIRTDataSetWizardPage(ISelection selection) {
		super(MAXIMOReportDesignerUtil.titleName);
		setTitle(MAXIMOReportDesignerUtil.titleName);
		setDescription(MAXIMOReportDesignerUtil.titleName);
		this.selection = selection;
	}

	public void createControl(Composite parent) 
	{
		Composite container = new Composite(parent, SWT.NULL);
		GridLayout layout = new GridLayout();
		container.setLayout(layout);
		layout.numColumns = 2;
		layout.verticalSpacing = 2;
		
		//Utility.loadConnectionInfo();
		
		//*****************Report Title Part****************************
		Label label = new Label(container, SWT.NULL);
		label.setText("&Data Set Name:");

		DataSetNameText = new Text(container, SWT.BORDER | SWT.SINGLE);
		GridData gd = new GridData(GridData.FILL_HORIZONTAL);
		DataSetNameText.setLayoutData(gd);
		DataSetNameText.addModifyListener(new ModifyListener() {
			public void modifyText(ModifyEvent e) {
				dialogChanged();
			}
		});
		
		dialogChanged();
		setControl(container);
		setPageComplete(false);
	}
	
	@Override
	public boolean canFlipToNextPage() 
	{
		
		return canflip;
		//return true;
	}
	
	private void dialogChanged() 
	{
		String fileName = getDataSetNameText();

		setPageComplete(false);
		canflip=false;
		
		if (fileName.length() == 0) {
			updateStatus("DataSet Name must be specified");
			return;
		}
		canflip=true;
		updateStatus(null);
		setPageComplete(true);
		
	}
	
	public String getDataSetNameText() 
	{
		return DataSetNameText.getText();
	}
	
	private void updateStatus(String message) {
		setErrorMessage(message);
		setPageComplete(message == null);
	}
}

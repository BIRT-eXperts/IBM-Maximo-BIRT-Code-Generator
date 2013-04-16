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

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.InvocationTargetException;

import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.ui.INewWizard;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.ide.IDE;

import com.mmkarton.mx7.reportgenerator.engine.MAXIMOReportDesigner;
import com.mmkarton.mx7.reportgenerator.engine.SQLQuery;
import com.mmkarton.mx7.reportgenerator.sqledit.SQLUtility;

public class BIRTDataSetWizard extends Wizard implements INewWizard 
{
	private BIRTDataSetWizardPage datasetpage;
	private BIRTSQLWizardPage sqlpage;
	private ISelection selection;
	private IFile reportFile;
	private String reportFileName;
	
	
	private SQLQuery queryList=null;

	public BIRTDataSetWizard() 
	{
		super();
		setNeedsProgressMonitor(true);
	}
	
	@Override
	public void addPages() 
	{
		datasetpage=new BIRTDataSetWizardPage(selection);
		addPage(datasetpage);
		sqlpage= new BIRTSQLWizardPage("");
		addPage(sqlpage);
	}

	@Override
	public boolean performFinish() 
	{
		final String query=sqlpage.getQueryText();
		final String fileName = reportFileName;
		final String dataSetName = datasetpage.getDataSetNameText();
		
		
		IRunnableWithProgress op = new IRunnableWithProgress() {
			public void run(IProgressMonitor monitor) throws InvocationTargetException {
				try {
					doFinish(fileName,dataSetName,query,monitor);
				} catch (CoreException e) {
					throw new InvocationTargetException(e);
				} finally {
					monitor.done();
				}
			}
		};
		try {
			getContainer().run(true, false, op);
		} catch (InterruptedException e) {
			return false;
		} catch (InvocationTargetException e) {
			Throwable realException = e.getTargetException();
			MessageDialog.openError(getShell(), "Error", realException.getMessage());
			return false;
		}
		return true;
	}
	
	private void doFinish(String fileName,String dataSetName,
						String sqlQueryText,
			IProgressMonitor monitor)
			throws CoreException {
//		 create a sample file
		monitor.beginTask("Creating " + fileName, 2);
		//IWorkspaceRoot root = ResourcesPlugin.getWorkspace().getRoot();
		getShell().getDisplay().asyncExec(new Runnable() {
			public void run() {
				{
					IWorkbenchPage page =
						PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage();
					page.saveEditor(page.getActiveEditor(), false);
					page.closeEditor(page.getActiveEditor(),true);
				} 
			}
		});
		

	    // retrieve the full system path:
	    String fullfilename = reportFile.getRawLocation().toOSString();
	    //String fullfilename=osfile+"/"+fileName;

			monitor.worked(1);
			
			monitor.setTaskName("Fetching SQL...");
			
			queryList=SQLUtility.getBIRTSQLFields(sqlQueryText);
			monitor.worked(2);
			monitor.setTaskName("Generate SQL Data");
			
			
			//SQLUtility.saveDataSetDesign(null, null, null);
			
			MAXIMOReportDesigner birtReport=new MAXIMOReportDesigner(fullfilename,fullfilename,sqlQueryText,queryList);
						
			birtReport.addDataSet(dataSetName);
			
			//Close Connection
			sqlpage.closeConnection();
			
			
		
		monitor.worked(3);
		monitor.setTaskName("Opening file for editing...");
		getShell().getDisplay().asyncExec(new Runnable() {
			public void run() {
				IWorkbenchPage page =
					PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage();
				try {
					
					//page.close();
					IDE.openEditor(page, reportFile, true);
				} catch (PartInitException e) {
				}
			}
		});
		monitor.worked(4);
	}
	
	
	
	@Override
	public boolean performCancel() 
	{
		sqlpage.closeConnection();
		return super.performCancel();
	}

	public void init(IWorkbench workbench, IStructuredSelection selection) 
	{
		boolean false_file=true;
		
		if(selection.getFirstElement() instanceof IFile)
		{
			IFile file =(IFile)selection.getFirstElement();
			reportFileName=file.getName();
			reportFile=file;
			if (file.getFileExtension().equals("rptdesign"))
					false_file=false;
		}

		if(false_file)
		{
			MessageDialog.openError( getShell( ),
					"Wizzard Error", //$NON-NLS-1$
					"Das gewählte File ist kein MAXIMO-BIRT Report File (.rptdesign)!" ); //$NON-NLS-1$

			// abort wizard. There is no clean way to do it.
			/**
			 * Remove the exception here 'cause It's safe since the wizard won't
			 * create any file without an open project.
			 */
			 throw new RuntimeException( );
		}
		

		// OK
		
				
		this.selection = selection;
		setWindowTitle( "New MAXIMO Report" );

	}
	private void throwCoreException(String message) throws CoreException {
		IStatus status =
			new Status(IStatus.ERROR, "com.mmkarton.mx7.reportgenerator", IStatus.OK, message, null);
		throw new CoreException(status);
	}

}

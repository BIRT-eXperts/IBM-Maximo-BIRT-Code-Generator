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

import org.eclipse.jface.viewers.IStructuredSelection;

import org.eclipse.jface.wizard.Wizard;
import org.eclipse.ui.INewWizard;
import org.eclipse.ui.IWorkbench;

import org.eclipse.core.runtime.*;
import org.eclipse.jface.operation.*;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;


import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.core.resources.*;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.datatools.connectivity.oda.IParameterMetaData;
import org.eclipse.datatools.connectivity.oda.IResultSetMetaData;

import java.io.*;

import org.eclipse.ui.*;
import org.eclipse.ui.ide.IDE;

import com.mmkarton.mx7.reportgenerator.engine.MAXIMOReportDesigner;
import com.mmkarton.mx7.reportgenerator.engine.SQLQuery;
import com.mmkarton.mx7.reportgenerator.sqledit.MetaDataRetriever;
import com.mmkarton.mx7.reportgenerator.sqledit.SQLUtility;

/**
 * This is a sample new wizard. Its role is to create a new file 
 * resource in the provided container. If the container resource
 * (a folder or a project) is selected in the workspace 
 * when the wizard is opened, it will accept it as the target
 * container. The wizard creates one file with the extension
 * "rptdesign". If a sample multi-page editor (also available
 * as a template) is registered for the same extension, it will
 * be able to open it.
 */

public class BIRTReportWizard extends Wizard implements INewWizard 
{
	//WizardNewReportCreationPage newReportFileWizardPage;
	//private WizardTemplateChoicePage templateChoicePage;
	
	private BIRTReportWizardPage page;
	private BIRTSQLWizardPage sqlpage;
	private ISelection selection;
	//private SQLQuery sqlQuery=null;
	private SQLQuery queryList=null;

	/**
	 * Constructor for BIRTReportWizard.
	 */
	public BIRTReportWizard() {
		super();
		setNeedsProgressMonitor(true);
	}
	
	/**
	 * Adding the page to the wizard.
	 */

	public void addPages() 
	{
		page = new BIRTReportWizardPage(selection);
		addPage(page);
		sqlpage= new BIRTSQLWizardPage("");
		addPage(sqlpage);
	}
	
	/**
	 * This method is called when 'Finish' button is pressed in
	 * the wizard. We will create an operation and run it
	 * using wizard as execution context.
	 */
	
	//public IWizardPage getNextPage
	public boolean performCancel() 
	{
		//Close Connection
		sqlpage.closeConnection();
		return super.performCancel();
	}
	
	public boolean performFinish() 
	{
		final String containerName = page.getContainerName(); //Report Directory
		final String fileName = page.getFileName(); //New-Report Filename
		final String templatefile = page.getReportTemplateText(); //Report Template Filename
		final String query=sqlpage.getQueryText(); //SQL-Query Text
		
		final String reportTitle=page.getReportTitleText();
		final String reportDescription=page.getReportDescriptionText();
		final String reportAuthor=page.getReportAuthorText();
		
		
		
		IRunnableWithProgress op = new IRunnableWithProgress() {
			public void run(IProgressMonitor monitor) throws InvocationTargetException {
				try {
					doFinish(containerName, fileName,templatefile,query,
							reportTitle,reportDescription,reportAuthor, monitor);
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
	
	/**
	 * The worker method. It will find the container, create the
	 * file if missing or just replace its contents, and open
	 * the editor on the newly created file.
	 */

	private void doFinish(
			String containerName,
			String fileName,
			String templateName,
			String sqlQueryText,
			String reportTitle,
			String reportDescription,
			String reportAuthor,
			IProgressMonitor monitor)
			throws CoreException 
	{
		
		monitor.beginTask("Creating " + fileName, 2);
		IWorkspaceRoot root = ResourcesPlugin.getWorkspace().getRoot();
		IResource resource = root.findMember(new Path(containerName));
		if (!resource.exists() || !(resource instanceof IContainer)) {
			throwCoreException("Container \"" + containerName + "\" does not exist.");
		}
		IContainer container = (IContainer) resource;
		
	    IFile file2 = ResourcesPlugin.getWorkspace().getRoot().getFile(resource.getFullPath());

	    // retrieve the full system path for Reportfile
	    String osfile = file2.getRawLocation().toOSString();
	    String fullfilename=osfile+"/"+fileName;

		final IFile file = container.getFile(new Path(fileName));
		try 
		{
			InputStream stream = openContentStream(templateName);
			if (file.exists()) {
				file.setContents(stream, true, true, monitor); //Replace existing...
			} else {
				file.create(stream, true, monitor);
			}
			
			monitor.worked(1);
			monitor.setTaskName("Fetching SQL...");
			
			queryList=SQLUtility.getBIRTSQLFields(sqlQueryText);
			monitor.worked(2);
			monitor.setTaskName("Generate SQL Data");
			
			
			//SQLUtility.saveDataSetDesign(null, null, null);
			
			MAXIMOReportDesigner birtReport=new MAXIMOReportDesigner(fullfilename,fullfilename,sqlQueryText,queryList);
			
			birtReport.addProperties(reportTitle, reportDescription, reportAuthor);
			birtReport.run();
			
			//Close Connection
			sqlpage.closeConnection();
			
			stream.close();
		} catch (IOException e) {
		}
		monitor.worked(3);
		monitor.setTaskName("Opening file for editing...");
		getShell().getDisplay().asyncExec(new Runnable() {
			public void run() {
				IWorkbenchPage page =
					PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage();
				try {
					IDE.openEditor(page, file, true);
				} catch (PartInitException e) {
				}
			}
		});
		monitor.worked(4);
	}

	

	/**
	 * We will initialize file contents with a sample text.
	 */

	private InputStream openContentStream(String filename) 
	{
		try {
		InputStream inputStream=new FileInputStream(filename);
	    return inputStream;
			
		} catch (FileNotFoundException e) 
		{
			try {
				this.throwCoreException("File not found:"+filename);
			} catch (CoreException e1) {

				e1.printStackTrace();
			}
		}
		return null;
	}

	private void throwCoreException(String message) throws CoreException {
		IStatus status =
			new Status(IStatus.ERROR, "com.mmkarton.mx7.reportgenerator", IStatus.OK, message, null);
		throw new CoreException(status);
	}

	/**
	 * We will accept the selection in the workbench to see if
	 * we can initialize from it.
	 * @see IWorkbenchWizard#init(IWorkbench, IStructuredSelection)
	 */
	public void init(IWorkbench workbench, IStructuredSelection selection) 
	{
		// check existing open project
		IWorkspaceRoot root = ResourcesPlugin.getWorkspace( ).getRoot( );
		IProject projects[] = root.getProjects( );
		boolean foundOpenProject = false;
		for ( int i = 0; i < projects.length; i++ )
		{
			if ( projects[i].isOpen( ) )
			{
				foundOpenProject = true;
				break;
			}
		}
		if ( !foundOpenProject )
		{
			MessageDialog.openError( getShell( ),
					"Wizzard Error", //$NON-NLS-1$
					"No Project available" ); //$NON-NLS-1$

			// abort wizard. There is no clean way to do it.
			/**
			 * Remove the exception here 'cause It's safe since the wizard won't
			 * create any file without an open project.
			 */
			// throw new RuntimeException( );
		}
		// OK
		this.selection = selection;
		setWindowTitle( "New MAXIMO Report" );
	}
}
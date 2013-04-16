package com.mmkarton.mx7.reportgenerator.engine;
/*
 *******************************************************************************
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
import java.sql.SQLException;
import java.util.Iterator;
import java.util.List;
import java.util.logging.Level;

import org.eclipse.birt.core.exception.BirtException;
import org.eclipse.birt.core.framework.Platform;
import org.eclipse.birt.report.designer.data.ui.dataset.DataSetUIUtil;
import org.eclipse.birt.report.engine.api.EngineConfig;
import org.eclipse.birt.report.engine.api.EngineException;
import org.eclipse.birt.report.engine.api.IReportEngine;
import org.eclipse.birt.report.engine.api.IReportEngineFactory;
import org.eclipse.birt.report.engine.api.IReportRunnable;
import org.eclipse.birt.report.model.api.DataSourceHandle;
import org.eclipse.birt.report.model.api.ReportDesignHandle;
import org.eclipse.birt.report.model.api.ScriptDataSetHandle;
import org.eclipse.birt.report.model.api.activity.SemanticException;
import org.eclipse.birt.report.model.api.core.IModuleModel;
import org.eclipse.birt.report.model.elements.interfaces.IDesignElementModel;
import org.eclipse.datatools.connectivity.oda.IParameterMetaData;
import org.eclipse.datatools.connectivity.oda.IResultSetMetaData;
import org.eclipse.datatools.connectivity.oda.design.DataSetDesign;


public class MAXIMOReportDesigner 
{
	private MAXIMOReportDesignerPojo mxReportPojo=null;
	private String reportTitle;
	private String reportDescription;
	private String reportAuthor;
	private boolean isprop=false;
	
	public MAXIMOReportDesigner() {
	
	}
	
	public MAXIMOReportDesigner(String reportTemplate, String newReportFilename,
			String sqlQueryText,SQLQuery query) 
	{
		super();
		
		mxReportPojo=new MAXIMOReportDesignerPojo(sqlQueryText,reportTemplate, 
				newReportFilename, query,false);
	}
	
	public void addProperties(String reportTitle,String reportDescription,String reportAuthor)
	{
		this.reportTitle=reportTitle;
		this.reportDescription=reportDescription;
		this.reportAuthor=reportAuthor;
		
		this.isprop=true;
	}
	
	public void run() 
	{
		try 
		{
			IReportEngine engine = getEngine();

			IReportRunnable design = engine.openReportDesign(mxReportPojo.getReportTemplate());

			ReportDesignHandle dh = (ReportDesignHandle) design
					.getDesignHandle();
		
			if(isprop)
				addReportProperties(dh);
			
			Iterator iterator = dh.getDataSets().iterator();
			Iterator<ScriptDataSetHandle> datasetIter = iterator;

			ScriptDataSetHandle dshandle=null;
			while (datasetIter.hasNext()) 
			{
				dshandle = datasetIter.next();
				dshandle.setOpen(MAXIMOReportDesignerUtil.generateOpenCode(mxReportPojo.getQuery()).toString());
				dshandle.setFetch(MAXIMOReportDesignerUtil.generateFetchCode(mxReportPojo.getQuery()).toString());

				MAXIMOReportDesignerUtil.setResultSet(dshandle, mxReportPojo.getQuery());
			}
			if(dshandle!=null)
				DataSetUIUtil.updateColumnCacheAfterCleanRs( dshandle );
			try {
				dh.save();
				
			} catch (IOException e) {
				e.printStackTrace();
			}
			engine.destroy();
		} catch (EngineException e) {
			e.printStackTrace();
		} catch (BirtException e) {
			e.printStackTrace();
		}  finally {

		}
	}
	
	public void addDataSet(String dsname) 
	{
		try 
		{
			IReportEngine engine = getEngine();

			IReportRunnable design = engine.openReportDesign(mxReportPojo.getReportTemplate());

			ReportDesignHandle dh = (ReportDesignHandle) design
					.getDesignHandle();
			
			String dsourceName=dh.getDataSources().get(0).getName();
			
			ScriptDataSetHandle dataSetHandle = dh.getElementFactory().newScriptDataSet(dsname);
			dataSetHandle.setDataSource(dsourceName);
			
			dataSetHandle.setOpen(MAXIMOReportDesignerUtil.generateOpenCode(mxReportPojo.getQuery()).toString());
			dataSetHandle.setFetch(MAXIMOReportDesignerUtil.generateFetchCode(mxReportPojo.getQuery()).toString());

			MAXIMOReportDesignerUtil.setResultSet(dataSetHandle, mxReportPojo.getQuery());

			dh.getDataSets().add(dataSetHandle);
			if(dataSetHandle!=null)
				DataSetUIUtil.updateColumnCacheAfterCleanRs( dataSetHandle );

			try {
				dh.save();
				
			} catch (IOException e) {
				e.printStackTrace();
			}
			engine.destroy();
		} catch (EngineException e) {
			e.printStackTrace();
		} catch (BirtException e) {
			e.printStackTrace();
		}  finally {

		}
	}


	private void addReportProperties(ReportDesignHandle designHandle)
			throws SemanticException {
		designHandle
				.setProperty(IDesignElementModel.COMMENTS_PROP,
						"COMMENT: Report generated through code by Sales Simple Report Java program");
		if(reportTitle.length()<=0)
			designHandle.setProperty(IModuleModel.TITLE_PROP,
				"MM-Karton MAXIMO Report");
		else
			designHandle.setProperty(IModuleModel.TITLE_PROP,
					reportTitle);
		
		if(reportDescription.length()<=0)
			designHandle.setProperty(IModuleModel.DESCRIPTION_PROP,
				"DESCRIPTION: MM-Karton MAXIMO Report");
		else
			designHandle.setProperty(IModuleModel.DESCRIPTION_PROP,
					reportDescription);
		
		designHandle.setProperty(IModuleModel.CREATED_BY_PROP,
				"CREATED BY: System Maintenance Department");
		
		if(reportAuthor.length()<=0)
			designHandle.setProperty(IModuleModel.AUTHOR_PROP,
				"AUTHOR: System Maintenance");
		else
			designHandle.setProperty(IModuleModel.AUTHOR_PROP,
					reportAuthor);
		
	}
	
	private static IReportEngine getEngine() throws BirtException 
	{
		EngineConfig config = new EngineConfig();
		
		// Create the report engine
		IReportEngineFactory factory = (IReportEngineFactory) Platform
				.createFactoryObject( IReportEngineFactory.EXTENSION_REPORT_ENGINE_FACTORY );
		IReportEngine engine = factory.createReportEngine( config );

		return engine;
	}

}

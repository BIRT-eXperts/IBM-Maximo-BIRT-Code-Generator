package com.mmkarton.mx7.reportgenerator.test;


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
import java.util.Iterator;
import java.util.logging.Level;

import org.eclipse.birt.core.exception.BirtException;
import org.eclipse.birt.core.framework.IPlatformContext;
import org.eclipse.birt.core.framework.Platform;
import org.eclipse.birt.core.framework.PlatformFileContext;
import org.eclipse.birt.report.engine.api.EngineConfig;
import org.eclipse.birt.report.engine.api.EngineException;
import org.eclipse.birt.report.engine.api.IReportDocument;
import org.eclipse.birt.report.engine.api.IReportEngine;
import org.eclipse.birt.report.engine.api.IReportEngineFactory;
import org.eclipse.birt.report.engine.api.IReportRunnable;
import org.eclipse.birt.report.engine.api.ReportEngine;
import org.eclipse.birt.report.model.api.CellHandle;
import org.eclipse.birt.report.model.api.DesignConfig;
import org.eclipse.birt.report.model.api.DesignElementHandle;
import org.eclipse.birt.report.model.api.DesignEngine;
import org.eclipse.birt.report.model.api.ElementFactory;
import org.eclipse.birt.report.model.api.GridHandle;
import org.eclipse.birt.report.model.api.IDesignEngine;
import org.eclipse.birt.report.model.api.IDesignEngineFactory;
import org.eclipse.birt.report.model.api.ImageHandle;
import org.eclipse.birt.report.model.api.LabelHandle;
import org.eclipse.birt.report.model.api.PropertyHandle;
import org.eclipse.birt.report.model.api.ReportDesignHandle;
import org.eclipse.birt.report.model.api.ResultSetColumnHandle;
import org.eclipse.birt.report.model.api.RowHandle;
import org.eclipse.birt.report.model.api.ScriptDataSetHandle;
import org.eclipse.birt.report.model.api.SessionHandle;
import org.eclipse.birt.report.model.api.StructureFactory;
import org.eclipse.birt.report.model.api.activity.SemanticException;
import org.eclipse.birt.report.model.api.elements.structures.CachedMetaData;
import org.eclipse.birt.report.model.api.elements.structures.ColumnHint;
import org.eclipse.birt.report.model.api.elements.structures.ResultSetColumn;

public class ReadBIRTReportTest 
{

	public static void main( String[] args )
	{
		IReportEngine engine=null;
		try
		{

			engine=getBIRTRunTime();
			IReportRunnable design =
			engine.openReportDesign( "asset.rptdesign" );
			
			ReportDesignHandle dh = (ReportDesignHandle)design.getDesignHandle();
			
			Iterator datasetIter = dh.getDataSets().iterator();
			
			int pos=0;
			ScriptDataSetHandle dshandle;
			while (datasetIter.hasNext())
			{
			   dshandle =	(ScriptDataSetHandle)datasetIter.next();			   
			   System.out.println(dshandle.getOpen());
			   
			   dshandle.setOpen("test open");
			   
			   PropertyHandle computedSet = dshandle.getPropertyHandle(ScriptDataSetHandle.RESULT_SET_PROP);
			   ResultSetColumn resultColumn = StructureFactory.createResultSetColumn();
			   resultColumn.setPosition(1);
			   resultColumn.setColumnName("asset");
			   resultColumn.setDataType("integer");
			   computedSet.addItem(resultColumn);
			   
			   PropertyHandle columnhint = dshandle.getPropertyHandle(ScriptDataSetHandle.COLUMN_HINTS_PROP);
			   ColumnHint ch= StructureFactory.createColumnHint();
			   ch.setProperty("columnName", "asset");
			  columnhint.addItem(ch);
			   
			   PropertyHandle chached = dshandle.getPropertyHandle(ScriptDataSetHandle.CACHED_METADATA_PROP);
			   CachedMetaData metadata = StructureFactory.createCachedMetaData();
			   metadata.setProperty("columnName", "asset");			   
			   chached.addItem(metadata);
			   
			   
			   System.out.println(dshandle.getFetch());
			   
			   dshandle.setFetch("test fetch");
			   
			   dh.getDataSets().drop(pos);
			   dh.getDataSets().add(dshandle);			   
			}
			
			try {
				dh.saveAs("mmAsset.rptdesign");
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			engine.destroy();
		}
		catch (EngineException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (BirtException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		finally 
		{
			
			Platform.shutdown();
		}
	}
	
	private static IReportEngine getBIRTRunTime() throws BirtException {
		EngineConfig config;
		IReportEngine engine;
		config = new EngineConfig();
		//config					.setEngineHome("C:/birt-runtime-2_1_0/birt-runtime-2_1_0/ReportEngine");
//			config.setEngineHome("C:/SOA/birt_rt221/birt-runtime-2_2_1/ReportEngine");
		//config.setEngineHome("C:/MAXIMOBIRTDesigner212/birt_rt/birt-runtime-2_1_1/ReportEngine");
		config.setEngineHome("C:/SOA/birt_rt211/birt-runtime-2_1_1/ReportEngine");
		config.setLogConfig("c:/temp", Level.FINE);

		Platform.startup(config); // If using RE API in Eclipse/RCP
									// application this is not needed.
		IReportEngineFactory factory = (IReportEngineFactory) Platform
				.createFactoryObject(IReportEngineFactory.EXTENSION_REPORT_ENGINE_FACTORY);
		engine = factory.createReportEngine(config);
		engine.changeLogLevel(Level.WARNING);
		return engine;
	}
	
}

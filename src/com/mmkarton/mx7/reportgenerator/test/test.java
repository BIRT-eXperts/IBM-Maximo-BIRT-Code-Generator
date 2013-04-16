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

import java.util.logging.Level;

import org.eclipse.birt.core.exception.BirtException;
import org.eclipse.birt.core.framework.Platform;
import org.eclipse.birt.report.engine.api.EngineConfig;
import org.eclipse.birt.report.engine.api.IReportEngine;
import org.eclipse.birt.report.engine.api.IReportEngineFactory;

import com.mmkarton.mx7.reportgenerator.engine.MAXIMOReportDesigner;

public class test {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		MAXIMOReportDesigner birtReport=new MAXIMOReportDesigner("asset.rptdesign","new.rptdesign","select * from labor",null);
		birtReport.run();

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

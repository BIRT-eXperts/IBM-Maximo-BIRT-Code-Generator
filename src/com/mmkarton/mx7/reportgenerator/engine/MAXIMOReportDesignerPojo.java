package com.mmkarton.mx7.reportgenerator.engine;

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

public class MAXIMOReportDesignerPojo 
{

	private String sqlQueryText = "";
	private String reportTemplate = "";
	private String newReportFilename = "";
	private SQLQuery query=null;
	private boolean RCPMode = false;
	
	public MAXIMOReportDesignerPojo() 
	{
	}

	public MAXIMOReportDesignerPojo(String sqlQueryText, 
			String reportTemplate, String newReportFilename, 
			SQLQuery query, boolean mode) {
		super();
		this.sqlQueryText = sqlQueryText;
		this.reportTemplate = reportTemplate;
		this.newReportFilename = newReportFilename;
		this.query = query;
		RCPMode = mode;
	}

	public String getNewReportFilename() {
		return newReportFilename;
	}

	public void setNewReportFilename(String newReportFilename) {
		this.newReportFilename = newReportFilename;
	}

	public SQLQuery getQuery() {
		return query;
	}

	public void setQuery(SQLQuery query) {
		this.query = query;
	}

	public boolean isRCPMode() {
		return RCPMode;
	}

	public void setRCPMode(boolean mode) {
		RCPMode = mode;
	}


	public String getReportTemplate() {
		return reportTemplate;
	}

	public void setReportTemplate(String reportTemplate) {
		this.reportTemplate = reportTemplate;
	}

	public String getSqlQueryText() {
		return sqlQueryText;
	}

	public void setSqlQueryText(String sqlQueryText) {
		this.sqlQueryText = sqlQueryText;
	}
	
	
	
	

}

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


import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

public class SQLQuery 
{
	private Map<String,Integer> fields;
	private String whereclause;
	private String sqlstring;
	
	public SQLQuery() 
	{
		this.fields = new HashMap<String, Integer>();
	}
	
	public SQLQuery(String fields, String sqlstring, String whereclause) 
	{
		super();
		this.fields = new HashMap<String, Integer>();
		this.sqlstring = sqlstring;
		this.whereclause = whereclause;
	}

	public Map getFields() 
	{
		Map sortedmap= new TreeMap(fields);
		
		return sortedmap;
	}

	public void setFields(String fieldname, Integer datatype) 
	{
		this.fields.put(fieldname, datatype);
	}

	public String getSqlQueryString() {
		return sqlstring;
	}

	public void setSqlQueryString(String sqlstring) {
		this.sqlstring = sqlstring;
	}

	public String getWhereclause() {
		return whereclause;
	}

	public void setWhereclause(String whereclause) {
		this.whereclause = whereclause;
	}
	
}

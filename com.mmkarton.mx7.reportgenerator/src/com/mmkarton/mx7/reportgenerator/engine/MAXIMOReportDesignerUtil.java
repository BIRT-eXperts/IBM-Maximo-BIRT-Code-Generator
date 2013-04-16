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

import java.sql.Types;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import org.eclipse.birt.report.model.api.PropertyHandle;
import org.eclipse.birt.report.model.api.ScriptDataSetHandle;
import org.eclipse.birt.report.model.api.StructureFactory;
import org.eclipse.birt.report.model.api.activity.SemanticException;
import org.eclipse.birt.report.model.api.elements.structures.CachedMetaData;
import org.eclipse.birt.report.model.api.elements.structures.ColumnHint;
import org.eclipse.birt.report.model.api.elements.structures.ResultSetColumn;

public class MAXIMOReportDesignerUtil 
{
	public static String titleName="Codegenerator für MAXIMO Scripted DataSets";

	static String parseQueryWhere(String sql2) 
	{
		if (sql2==null) {
			return null;
			
		}
		String tempsql = sql2.toUpperCase();
	
		String sql_query="";
		
		
		int where_pos = tempsql.toUpperCase().indexOf("WHERE");
	
		if (where_pos > 0) 
		{
			sql_query = tempsql.substring(where_pos + "WHERE".length());
			return sql_query;
		}	
	
		return null;
	}

	static void setResultSet(ScriptDataSetHandle dshandle,
			SQLQuery query) throws SemanticException 
	{
		int position = 1;
		
		
		Map<String, Integer> fieldnames = query.getFields();
		Set<String> set = fieldnames.keySet();
		
		Iterator i = set.iterator();
	
		while (i.hasNext()) {
			String name = (String) i.next();
	
			PropertyHandle computedSet = dshandle
					.getPropertyHandle(ScriptDataSetHandle.RESULT_SET_PROP);
			ResultSetColumn resultColumn = StructureFactory
					.createResultSetColumn();
			resultColumn.setPosition(position);
			resultColumn.setColumnName(name);
			resultColumn.setDataType(MAXIMOReportDesignerUtil.getBIRTDataType(fieldnames.get(name)));
			computedSet.addItem(resultColumn);
	
			PropertyHandle columnhint = dshandle
					.getPropertyHandle(ScriptDataSetHandle.COLUMN_HINTS_PROP);
			ColumnHint ch = StructureFactory.createColumnHint();
			ch.setProperty("columnName", name);
			columnhint.addItem(ch);
	
			PropertyHandle chached = dshandle
					.getPropertyHandle(ScriptDataSetHandle.CACHED_METADATA_PROP);
			CachedMetaData metadata = StructureFactory.createCachedMetaData();
			metadata.setProperty("cachedMetaData", resultColumn);
	
			dshandle.setCachedMetaData(metadata);
			position++;
		}
	}

	static String getBIRTDataType(Integer datatype) 
	{
		switch (datatype) {
		case Types.BOOLEAN:
			return "boolean";
		
		case Types.DATE:
			return "date-time";
		
		case Types.DECIMAL:
			return "decimal";
			
		case Types.DOUBLE:
			return "decimal";
			
		case Types.FLOAT:
			return "float";
			
		case Types.INTEGER:
			return "integer";
		
		case Types.NUMERIC:
			return "float";
			
		case Types.TIME:
			return "time";
		
		case Types.TIMESTAMP:
			return "date-time";
			
		case Types.VARCHAR:
			return "string";
	
		default:
			return "string";
		} 
		
	}

	static String getBIRTDataTypeFetchFunction(Integer datatype) 
	{
		switch (datatype) {
		case Types.BOOLEAN:
			return "getBooleanString";
		
		case Types.DATE:
			return "getTimestamp";
		
		case Types.DECIMAL:
			return "getDouble";
			
		case Types.DOUBLE:
			return "getDouble";
			
		case Types.FLOAT:
			return "getFloat";
			
		case Types.INTEGER:
			return "getInteger";
		
		case Types.NUMERIC:
			return "getFloat";
			
		case Types.TIME:
			return "getTimestamp";
			
		case Types.TIMESTAMP:
			return "getTimestamp";
		
		case Types.VARCHAR:
			return "getString";
	
		default:
			return "getString";
		} 
		
	}

	static StringBuffer generateFetchCode(SQLQuery query) {
		/*
		 * if (!maximoDataSet.fetch()) return (false); // Add a line for each
		 * output column // The specific get method should match the data type
		 * of the output column. row["textfield"] =
		 * maximoDataSet.getString("textfield"); row["datefield"] =
		 * maximoDataSet.getTimestamp("datefield");
		 * 
		 * return (true);
		 */
	
		StringBuffer fetchCode = new StringBuffer();
		fetchCode.append("if (!maximoDataSet.fetch())").append("\n\t");
		fetchCode.append("   return (false);").append("\n\t");
		fetchCode.append("// Add a line for each output column").append("\n\t");
	
		Map<String, Integer> fieldnames = query.getFields();
		Set<String> set = fieldnames.keySet();
	
		Iterator i = set.iterator();
	
		while (i.hasNext()) {
			String name = (String) i.next();
			fetchCode.append(
					"row[\"" + name + "\"] = maximoDataSet."+getBIRTDataTypeFetchFunction(fieldnames.get(name))+"(\"" + name
							+ "\");").append("\n\t");
		}
	
		fetchCode.append("return (true);");
		return fetchCode;
	}

	static StringBuffer generateOpenCode(SQLQuery query) {
		/*
		 * maximoDataSet =
		 * MXReportDataSetProvider.create(this.getDataSource().getName(),
		 * this.getName()); maximoDataSet.open(); var sqlText = new String(); //
		 * Add query to sqlText variable. sqlText =
		 * "select textfield, datefield from table" + " where " +
		 * params["where"]; maximoDataSet.setQuery(sqlText);
		 */
		
		StringBuffer openCode = new StringBuffer();
		openCode
				.append(
						"maximoDataSet = MXReportDataSetProvider.create(this.getDataSource().getName(), this.getName());")
				.append("\n\t");
		openCode.append("maximoDataSet.open();").append("\n\t");
		openCode.append("var sqlText = new String();").append("\n\t");
		openCode.append("// Add MM-Maximo query to sqlText variable.").append(
				"\n\t");
		openCode.append("sqlText = \" ");
		
		String newSQLstr =query.getSqlQueryString(); 
		newSQLstr=newSQLstr.replaceAll("\n", " \"\n + \" ");
		
		openCode.append(newSQLstr + "\"").append("\n\t");
				
		String where=parseQueryWhere(query.getSqlQueryString());
	
		if (where!=null)
			openCode.append(" + \" and \" + params[\"where\"];").append("\n\t");
		else
			openCode.append("+ \" where \" + params[\"where\"];")
					.append("\n\t");
	
		openCode.append("maximoDataSet.setQuery(sqlText);");
		return openCode;
	}
	

}

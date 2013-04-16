/*******************************************************************************
 * Copyright (c) 2004 Actuate Corporation.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *  Actuate Corporation  - initial API and implementation
 *  Ing. Gerd Stockner (Mayr-Melnhof Karton Gesellschaft m.b.H.) - modifications
 *  Christian Voller (Mayr-Melnhof Karton Gesellschaft m.b.H.) - modifications
 *  CoSMIT GmbH - publishing, maintenance
 *******************************************************************************/
package com.mmkarton.mx7.reportgenerator.sqledit;

import java.sql.Types;
import java.text.Bidi;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.eclipse.datatools.connectivity.oda.IParameterMetaData;
import org.eclipse.datatools.connectivity.oda.IResultSetMetaData;
import org.eclipse.datatools.connectivity.oda.OdaException;
import org.eclipse.datatools.connectivity.oda.design.DataSetParameters;
import org.eclipse.datatools.connectivity.oda.design.DesignFactory;
import org.eclipse.datatools.connectivity.oda.design.ParameterDefinition;
import org.eclipse.datatools.connectivity.oda.design.ParameterMode;
import org.eclipse.datatools.connectivity.oda.design.ResultSetColumns;
import org.eclipse.datatools.connectivity.oda.design.ResultSetDefinition;
import org.eclipse.datatools.connectivity.oda.design.ui.designsession.DesignSessionUtil;

import com.mmkarton.mx7.reportgenerator.engine.SQLQuery;
import com.mmkarton.mx7.reportgenerator.jdbc.ResultSetMetaData;
import com.mmkarton.mx7.reportgenerator.wizards.BIRTReportWizard;



/**
 * The utility class for SQLDataSetEditorPage
 *
 */
public class SQLUtility
{
	/**
	 * save the dataset design's metadata info
	 * 
	 * @param design
	 */
	public static SQLQuery getBIRTSQLFields(String sqlQueryText) {
		MetaDataRetriever retriever = new MetaDataRetriever( addDummyWhere(sqlQueryText));
		IResultSetMetaData resultsetMeta = retriever.getResultSetMetaData( );
		IParameterMetaData paramMeta = retriever.getParameterMetaData( );
		 return saveDataSetDesign( resultsetMeta, paramMeta ,sqlQueryText);
	}
	
	public static SQLQuery saveDataSetDesign( IResultSetMetaData meta, IParameterMetaData paramMeta, String sqlQueryText )
	{
		try
		{
			setParameterMetaData( paramMeta );
			// set resultset metadata
			return setResultSetMetaData(meta, sqlQueryText );
		}
		catch ( OdaException e )
		{
			return null;
			
		}
	}

	/**
	 * Set parameter metadata in dataset design
	 * 
	 * @param design
	 * @param query
	 */
	private static void setParameterMetaData(IParameterMetaData paramMeta )
	{
		try
		{
			// set parameter metadata
			mergeParameterMetaData(  paramMeta );
		}
		catch ( OdaException e )
		{
			// do nothing, to keep the parameter definition in dataset design
			// dataSetDesign.setParameters( null );
		}
	}
	
	/**
	 * solve the BIDI line problem
	 * @param lineText
	 * @return
	 */
	public static int[] getBidiLineSegments( String lineText )
	{
		int[] seg = null;
		if ( lineText != null
				&& lineText.length( ) > 0
				&& !new Bidi( lineText, Bidi.DIRECTION_LEFT_TO_RIGHT ).isLeftToRight( ) )
		{
			List list = new ArrayList( );

			// Punctuations will be regarded as delimiter so that different
			// splits could be rendered separately.
			Object[] splits = lineText.split( "\\p{Punct}" );

			// !=, <> etc. leading to "" will be filtered to meet the rule that
			// segments must not have duplicates.
			for ( int i = 0; i < splits.length; i++ )
			{
				if ( !splits[i].equals( "" ) )
					list.add( splits[i] );
			}
			splits = list.toArray( );

			// first segment must be 0
			// last segment does not necessarily equal to line length
			seg = new int[splits.length + 1];
			for ( int i = 0; i < splits.length; i++ )
			{
				seg[i + 1] = lineText.indexOf( (String) splits[i], seg[i] )
						+ ( (String) splits[i] ).length( );
			}
		}

		return seg;
	}
    
	
	/**
	 * Return pre-defined query text pattern with every element in a cell.
	 * 
	 * @return pre-defined query text
	 */
	public static String getQueryPresetTextString( String extensionId )
	{
		String[] lines = getQueryPresetTextArray( extensionId );
		String result = "";
		if ( lines != null && lines.length > 0 )
		{
			for ( int i = 0; i < lines.length; i++ )
			{
				result = result
						+ lines[i] + ( i == lines.length - 1 ? " " : " \n" );
			}
		}
		return result;
	}
	
	
	/**
	 * Return pre-defined query text pattern with every element in a cell in an
	 * Array
	 * 
	 * @return pre-defined query text in an Array
	 */
	public static String[] getQueryPresetTextArray( String extensionId )
	{
		final String[] lines;
		if ( extensionId.equals( "org.eclipse.birt.report.data.oda.jdbc.SPSelectDataSet" ) )
			lines = new String[]{
				"{call procedure-name(arg1,arg2, ...)}"
			};
		else
			lines = new String[]{
					"select", "from"
			};
		return lines;
	}
	
	
    /**
	 * merge paramter meta data between dataParameter and datasetDesign's
	 * parameter.
	 * 
	 * @param dataSetDesign
	 * @param md
	 * @throws OdaException
	 */
	private static void mergeParameterMetaData( IParameterMetaData md ) throws OdaException
	{
		if ( md == null)
			return;
		DataSetParameters dataSetParameter = DesignSessionUtil.toDataSetParametersDesign( md,
				ParameterMode.IN_LITERAL );

		if ( dataSetParameter != null )
		{
			Iterator iter = dataSetParameter.getParameterDefinitions( )
					.iterator( );
			while ( iter.hasNext( ) )
			{
				ParameterDefinition defn = (ParameterDefinition) iter.next( );
				proccessParamDefn( defn, dataSetParameter );
			}
		}
		//dataSetDesign.setParameters( dataSetParameter );
	}

	/**
	 * Process the parameter definition for some special case
	 * 
	 * @param defn
	 * @param parameters
	 */
	private static void proccessParamDefn( ParameterDefinition defn,
			DataSetParameters parameters )
	{
		if ( defn.getAttributes( ).getNativeDataTypeCode( ) == Types.NULL )
		{
			defn.getAttributes( ).setNativeDataTypeCode( Types.CHAR );
		}
	}

	/**
	 * Set the resultset metadata in dataset design
	 * 
	 * @param dataSetDesign
	 * @param md
	 * @throws OdaException
	 */
	private static SQLQuery setResultSetMetaData(IResultSetMetaData md, String sqlQueryText ) throws OdaException
	{
		SQLQuery query=null;
		ResultSetColumns columns = DesignSessionUtil.toResultSetColumnsDesign( md );

		if ( columns != null )
		{
			query=new SQLQuery();
			ResultSetDefinition resultSetDefn = DesignFactory.eINSTANCE.createResultSetDefinition( );
			resultSetDefn.setResultSetColumns( columns );
			int count=resultSetDefn.getResultSetColumns().getResultColumnDefinitions().size();
			query.setSqlQueryString(sqlQueryText);
			
			for (int i = 0; i < count; i++) 
			{
				int columntype=-1;
				String columname="";
				try {
					ResultSetMetaData dataset=(ResultSetMetaData)md;
					columname=dataset.getColumnName(i+1);
					columntype=dataset.getColumnType(i+1);
				} catch (Exception e) 
				{
					return null;
				}
				
				query.setFields(columname, columntype);
			}
		}
		return query;
		
	}
	
	private static String addDummyWhere(String sqlQueryText) 
	{
		if (sqlQueryText==null) {
			return null;
			
		}
		String tempsql = sqlQueryText.toUpperCase();

		String sql_query="";
		
		
		int where_pos = tempsql.toUpperCase().indexOf("WHERE");

		if (where_pos > 0) 
		{
			sql_query = tempsql.substring(0,where_pos );
		}
		else
		{
			sql_query = tempsql;
		}

		return sql_query+" Where 1=2";
	}

}

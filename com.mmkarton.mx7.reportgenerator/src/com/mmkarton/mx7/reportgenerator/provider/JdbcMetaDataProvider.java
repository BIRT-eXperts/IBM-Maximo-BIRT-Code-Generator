/*
 *************************************************************************
 * Copyright (c) 2006, 2007 Actuate Corporation.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *  Actuate Corporation - initial API and implementation
 *  Ing. Gerd Stockner (Mayr-Melnhof Karton Gesellschaft m.b.H.) - modifications
 *  Christian Voller (Mayr-Melnhof Karton Gesellschaft m.b.H.) - modifications
 *  CoSMIT GmbH - publishing, maintenance
 *  
 *************************************************************************
 */
package com.mmkarton.mx7.reportgenerator.provider;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;


import org.eclipse.datatools.connectivity.oda.OdaException;
import org.eclipse.datatools.connectivity.oda.design.DataSetDesign;
import org.eclipse.datatools.connectivity.oda.design.DataSourceDesign;
import org.eclipse.datatools.connectivity.oda.design.ui.designsession.DesignSessionUtil;
import org.eclipse.jface.preference.IPreferenceStore;

import com.mmkarton.mx7.reportgenerator.Activator;
import com.mmkarton.mx7.reportgenerator.util.Constants;
import com.mmkarton.mx7.reportgenerator.util.DriverLoader;
import com.mmkarton.mx7.reportgenerator.util.Utility;

public class JdbcMetaDataProvider
{
	private String userName;
	private String url;
	private String driverClass;
	private String password;
	private String defaultschema;
	private Connection connection;
	
	private static Logger logger = Logger.getLogger( JdbcMetaDataProvider.class.getName( ) );
	
	private static JdbcMetaDataProvider instance = null;
	
	private JdbcMetaDataProvider(String driverClass, String url, String userName, String password,String schema)
	{
		this.driverClass = driverClass;
		this.url = url;
		this.userName = userName;
		this.password = password;
		this.defaultschema= schema;
	}
	
	public static void createInstance(  ) throws FileNotFoundException
	{
		release( );
		
		

		//DataSourceDesign dataSourceDesign = dataSetDesign.getDataSourceDesign( );
		//Properties props = new Properties( );
		/*try
		{
			//props = DesignSessionUtil.getEffectiveDataSourceProperties( dataSourceDesign );
			FileInputStream stream = new FileInputStream("mxreportdatasources.properties");
			props.load(stream);
			stream.close();
		}
		 catch (FileNotFoundException e) {
			
			e.printStackTrace();
		} catch (IOException e) {
			
			e.printStackTrace();
		}*/
		String propFilePath=Utility.getConnectionPropertiesFilePath();
		String userName;
		String password;
		String url;
		String driverClass;
		String defaultschema;
		
		if(propFilePath==null)
		{
			throw new FileNotFoundException();
		}
		else
		{
			//IPreferenceStore store = Activator.getDefault().getPreferenceStore();
			Properties properties = new Properties();
			FileInputStream stream;
			try {
				stream = new FileInputStream(propFilePath);
				properties.load(stream);
				stream.close();
				
			} catch (FileNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			userName = properties.getProperty("maximoDataSource.username");
			password = properties.getProperty("maximoDataSource.password");
			url =  properties.getProperty("maximoDataSource.url");
			driverClass = properties.getProperty("maximoDataSource.driver");
			defaultschema= properties.getProperty("maximoDataSource.schemaowner");
		}
		instance = new JdbcMetaDataProvider( driverClass, url, userName, password,defaultschema);
	}
	
	public static void release( )
	{
		if ( instance != null )
		{
			instance.closeConnection( );
			instance = null;
			logger.log( Level.INFO, "Close Connection!");
		}
	}
	
	public void reconnect( ) throws SQLException, OdaException
	{
		closeConnection( );
		connection = DriverLoader.getConnection( driverClass,
				url,
				userName,
				password );
	}
	
	private void closeConnection( )
	{
		if ( connection != null )
		{
			try
			{
				connection.close( );
			}
			catch ( SQLException e )
			{
				//just ignore it
			}
		}
	}
	
	public static JdbcMetaDataProvider getInstance( )
	{
		return instance;
	}
	
	public String getIdentifierQuoteString( )
	{
		if ( connection == null )
		{
			try
			{
				reconnect( );
			}
			catch ( Exception e )
			{
				logger.log( Level.WARNING, e.getMessage( ), e );
				return "";
			}
			try
			{
				return connection.getMetaData( ).getIdentifierQuoteString( );
			}
			catch ( SQLException e )
			{
				logger.log( Level.WARNING, e.getMessage( ), e );
				return "";
			}
		}
		try
		{
			return connection.getMetaData( ).getIdentifierQuoteString( );
		}
		catch ( SQLException e )
		{
			try 
			{
				reconnect( );
				return connection.getMetaData( ).getIdentifierQuoteString( );
			}
			catch ( Exception e1 )
			{
				logger.log( Level.WARNING, e.getMessage( ), e );
				return "";
			}
		}
	}
	
	public boolean isSupportProcedure( )
	{
		if ( connection == null )
		{
			try
			{
				reconnect( );
			}
			catch ( Exception e )
			{
				logger.log( Level.WARNING, e.getMessage( ), e );
				return false;
			}
			try
			{
				return connection.getMetaData( ).supportsStoredProcedures( );
			}
			catch ( SQLException e )
			{
				logger.log( Level.WARNING, e.getMessage( ), e );
				return false;
			}
		}
		try
		{
			return connection.getMetaData( ).supportsStoredProcedures( );
		}
		catch ( SQLException e )
		{
			try 
			{
				reconnect( );
				return connection.getMetaData( ).supportsStoredProcedures( );
			}
			catch ( Exception e1 )
			{
				logger.log( Level.WARNING, e.getMessage( ), e );
				return false;
			}
		}
	}
	
	public boolean isSupportSchema( )
	{
		if ( connection == null )
		{
			try
			{
				reconnect( );
			}
			catch ( Exception e )
			{
				logger.log( Level.WARNING, e.getMessage( ), e );
				return false;
			}
			try
			{
				return connection.getMetaData( ).supportsSchemasInTableDefinitions( );
			}
			catch ( SQLException e )
			{
				logger.log( Level.WARNING, e.getMessage( ), e );
				return false;
			}
		}
		try
		{
			return connection.getMetaData( ).supportsSchemasInTableDefinitions( );
		}
		catch ( SQLException e )
		{
			try 
			{
				reconnect( );
				return connection.getMetaData( ).supportsSchemasInTableDefinitions( );
			}
			catch ( Exception e1 )
			{
				logger.log( Level.WARNING, e.getMessage( ), e );
				return false;
			}
		}
	}
	
	public ResultSet getTableColumns( String schemaPattern,
			String tableNamePattern, String columnNamePattern )
	{
		if ( connection == null )
		{
			try
			{
				reconnect( );
			}
			catch ( Exception e )
			{
				logger.log( Level.WARNING, e.getMessage( ), e );
				return null;
			}
			try
			{
				return connection.getMetaData( ).getColumns( 
						connection.getCatalog( ), schemaPattern, tableNamePattern, columnNamePattern );
			}
			catch ( SQLException e )
			{
				logger.log( Level.WARNING, e.getMessage( ), e );
				return null;
			}
		}
		try
		{
			return connection.getMetaData( ).getColumns( 
					connection.getCatalog( ), schemaPattern, tableNamePattern, columnNamePattern );
		}
		catch ( SQLException e )
		{
			try 
			{
				reconnect( );
				return connection.getMetaData( ).getColumns( 
						connection.getCatalog( ), schemaPattern, tableNamePattern, columnNamePattern );
			}
			catch ( Exception e1 )
			{
				logger.log( Level.WARNING, e.getMessage( ), e );
				return null;
			}
		}
	}
	
	
	public ResultSet getProcedures( String schemaPattern,
			String procedureNamePattern )
	{
		if ( connection == null )
		{
			try
			{
				reconnect( );
			}
			catch ( Exception e )
			{
				logger.log( Level.WARNING, e.getMessage( ), e );
				return null;
			}
			try
			{
				return connection.getMetaData( ).getProcedures( 
						connection.getCatalog( ), schemaPattern, procedureNamePattern );
			}
			catch ( SQLException e )
			{
				logger.log( Level.WARNING, e.getMessage( ), e );
				return null;
			}
		}
		try
		{
			return connection.getMetaData( ).getProcedures( 
					connection.getCatalog( ), schemaPattern, procedureNamePattern );
		}
		catch ( SQLException e )
		{
			try 
			{
				reconnect( );
				return connection.getMetaData( ).getProcedures( 
						connection.getCatalog( ), schemaPattern, procedureNamePattern );
			}
			catch ( Exception e1 )
			{
				logger.log( Level.WARNING, e.getMessage( ), e );
				return null;
			}
		}
	}
	
	public ResultSet getProcedureColumns( String schemaPattern, 
			String procedureNamePattern, String columnNamePattern )
	{
		if ( connection == null )
		{
			try
			{
				reconnect( );
			}
			catch ( Exception e )
			{
				logger.log( Level.WARNING, e.getMessage( ), e );
				return null;
			}
			try
			{
				return connection.getMetaData( ).getProcedureColumns( 
						connection.getCatalog( ), schemaPattern, procedureNamePattern, columnNamePattern );
			}
			catch ( SQLException e )
			{
				logger.log( Level.WARNING, e.getMessage( ), e );
				return null;
			}
		}
		try
		{
			return connection.getMetaData( ).getProcedureColumns( 
					connection.getCatalog( ), schemaPattern, procedureNamePattern, columnNamePattern );
		}
		catch ( SQLException e )
		{
			try 
			{
				reconnect( );
				return connection.getMetaData( ).getProcedureColumns( 
						connection.getCatalog( ), schemaPattern, procedureNamePattern, columnNamePattern );
			}
			catch ( Exception e1 )
			{
				logger.log( Level.WARNING, e.getMessage( ), e );
				return null;
			}
		}
	}
	
	public ResultSet getAlltables( String schemaPattern,
			String namePattern, String[] types )
	{
		if ( connection == null )
		{
			try
			{
				reconnect( );
			}
			catch ( Exception e )
			{
				logger.log( Level.WARNING, e.getMessage( ), e );
				return null;
			}
			try
			{
				return connection.getMetaData( ).getTables( 
						connection.getCatalog( ), schemaPattern, namePattern, types );
			}
			catch ( SQLException e )
			{
				logger.log( Level.WARNING, e.getMessage( ), e );
				return null;
			}
		}
		try
		{
			return connection.getMetaData( ).getTables( 
					connection.getCatalog( ), schemaPattern, namePattern, types );
		}
		catch ( SQLException e )
		{
			try 
			{
				reconnect( );
				return connection.getMetaData( ).getTables( 
						connection.getCatalog( ), schemaPattern, namePattern, types );
			}
			catch ( Exception e1 )
			{
				logger.log( Level.WARNING, e.getMessage( ), e );
				return null;
			}
		}
	}
	
	public ResultSet getAllSchemas( )
	{
		if ( connection == null )
		{
			try
			{
				reconnect( );
			}
			catch ( Exception e )
			{
				logger.log( Level.WARNING, e.getMessage( ), e );
				return null;
			}
			try
			{
				return connection.getMetaData( ).getSchemas( );
			}
			catch ( SQLException e )
			{
				logger.log( Level.WARNING, e.getMessage( ), e );
				return null;
			}
		}
		try
		{
			return connection.getMetaData( ).getSchemas( );
		}
		catch ( SQLException e )
		{
			try 
			{
				reconnect( );
				return connection.getMetaData( ).getSchemas( );
			}
			catch ( Exception e1 )
			{
				logger.log( Level.WARNING, e.getMessage( ), e );
				return null;
			}
		}
	}
	
	public String[] getAllSchemaNames( )
	{
		ResultSet rs = this.getAllSchemas( );
		List<String> names = new ArrayList<String>( );
		if ( rs != null )
		{
			try
			{
				while ( rs.next( ) )
				{
					names.add( rs.getString( "TABLE_SCHEM" ) );
				}
			}
			catch ( SQLException e )
			{
				logger.log( Level.WARNING, e.getMessage( ), e );
			}
		}
		return names.toArray( new String[0] );
	}
	
	public String[] getMaximoSchema( )
	{
		ResultSet rs = this.getAllSchemas( );
		List<String> names = new ArrayList<String>( );
		if ( rs != null )
		{
			try
			{
				while ( rs.next( ) )
				{
					String name= rs.getString( "TABLE_SCHEM" ) ;
					//if (name.toLowerCase().equals(this.defaultschema))
						names.add( name );
				}
			}
			catch ( SQLException e )
			{
				logger.log( Level.WARNING, e.getMessage( ), e );
			}
		}
		return names.toArray( new String[0] );
	}
	
	public Connection getConnection() 
	{
		if ( connection == null )
		{
			try
			{
				reconnect( );
			}
			catch ( Exception e )
			{
				logger.log( Level.WARNING, e.getMessage( ), e );
				return null;
			}
		}
		return connection;
	}

	public String getDefaultschema() {
		return defaultschema;
	}

	public String getDriverClass() {
		return driverClass;
	}

	public String getPassword() {
		return password;
	}

	public String getUrl() {
		return url;
	}

	public String getUserName() {
		return userName;
	}
	
	
}
/*******************************************************************************
 * Copyright (c) 2004 Actuate Corporation. All rights reserved. This program and
 * the accompanying materials are made available under the terms of the Eclipse
 * Public License v1.0 which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: Actuate Corporation - initial API and implementation
 *  Ing. Gerd Stockner (Mayr-Melnhof Karton Gesellschaft m.b.H.) - modifications
 *  Christian Voller (Mayr-Melnhof Karton Gesellschaft m.b.H.) - modifications
 *  CoSMIT GmbH - publishing, maintenance
 ******************************************************************************/

package com.mmkarton.mx7.reportgenerator.util;

import java.sql.Connection;
import java.sql.SQLException;
import java.text.StringCharacterIterator;


import org.eclipse.datatools.connectivity.internal.ui.dialogs.ExceptionHandler;
import org.eclipse.datatools.connectivity.oda.OdaException;
import org.eclipse.ui.PlatformUI;

import com.mmkarton.mx7.reportgenerator.jdbc.JDBCDriverManager;


public final class DriverLoader
{
	private DriverLoader( )
	{
	}

	public static Connection getConnection( String driverClassName,
			String connectionString, String userId, String password ) throws SQLException, OdaException
	{
		return JDBCDriverManager.getInstance( )
				.getConnection( driverClassName,
						connectionString,
						userId,
						password, null );
	}
	
	public static Connection getConnectionWithExceptionTip( String driverClassName,
			String connectionString, String userId, String password )
			throws SQLException
	{
		try
		{
			return JDBCDriverManager.getInstance( )
					.getConnection( driverClassName,
							connectionString,
							userId,
							password, null );
		}
		catch ( Exception e )
		{
			ExceptionHandler.showException( PlatformUI.getWorkbench( )
					.getDisplay( )
					.getActiveShell( ),
					"Error" ,
					e.getLocalizedMessage( ),
					e );
			return null;
		}
	}

	static String escapeCharacters( String value )
	{
		final StringCharacterIterator iterator = new StringCharacterIterator( value );
		char character = iterator.current( );
		final StringBuffer result = new StringBuffer( );

		while ( character != StringCharacterIterator.DONE )
		{
			if ( character == '\\' )
			{
				result.append( "\\" ); //$NON-NLS-1$
			}
			else
			{
				//the char is not a special one
				//add it to the result as is
				result.append( character );
			}
			character = iterator.next( );
		}
		return result.toString( );

	}
	
	/**
	 * Tests whether the given connection properties can be used to create a connection.
	 * @param driverClassName the name of driver class
	 * @param connectionString the connection URL
	 * @param userId the user id
	 * @param password the pass word
	 * @return boolean whether could the connection being created
	 * @throws OdaException 
	 */
	public static boolean testConnection( String driverClassName,
			String connectionString, String userId,
			String password ) throws OdaException 
	{
        return testConnection( driverClassName, connectionString, null,
                                userId, password );
    }

    /**
     * Tests whether the given connection properties can be used to obtain a connection.
     * @param driverClassName the name of driver class
     * @param connectionString the JDBC driver connection URL
     * @param jndiNameUrl   the JNDI name to look up a Data Source name service; 
	 *						may be null or empty
     * @param userId        the login user id
     * @param password      the login password
     * @return  true if the the specified properties are valid to obtain a connection;
     *          false otherwise
     * @throws OdaException 
     */
    public static boolean testConnection( String driverClassName,
            String connectionString, String jndiNameUrl, String userId,
            String password ) throws OdaException 
    {
		return JDBCDriverManager.getInstance().testConnection( driverClassName, 
                connectionString, jndiNameUrl, userId, password );
	}
    
}


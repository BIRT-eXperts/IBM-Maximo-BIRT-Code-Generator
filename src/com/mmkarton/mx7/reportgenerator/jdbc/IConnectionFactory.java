/*
 *************************************************************************
 * Copyright (c) 2005 Actuate Corporation.
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
 *  
 *************************************************************************
 */ 
package com.mmkarton.mx7.reportgenerator.jdbc;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Properties;

/**
 * Defines a source for obtaining JDBC connections. An extension may implement this
 * interface to provide connections to its custom data source as an 
 * alternative to the java.sql.DriverManager facility.
 */
public interface IConnectionFactory
{
	public static final String DRIVER_CLASSPATH = "OdaJDBCDriverClassPath";

	public static final String PASS_IN_CONNECTION = "OdaJDBCDriverPassInConnection";

	/**
     * Establishes a connection to the given database URL. 
     *
     * @param driverClass driverClass defined in the extension 
     * @param url a database url  
     * @param connectionProperties a list of arbitrary string tag/value pairs as
     * connection arguments; normally at least a "user" and
     * "password" property should be included
     * @return a Connection to the URL 
     * @exception SQLException if a database access error occurs
     */
    public Connection getConnection( String driverClass, String url, Properties connectionProperties) 
    		throws SQLException;
	
}

/*******************************************************************************
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
 *******************************************************************************/

package com.mmkarton.mx7.reportgenerator.util;

import java.io.Serializable;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Iterator;

/**
 * This object represents a schema in the data base It contains methods to
 * retrieve the tables from the data base
 * 
 * @version $Revision: 1.4 $ $Date: 2007/02/01 10:58:57 $
 */

public class Schema implements Serializable
{

	/**
	 * serial version id
	 */
	private static final long serialVersionUID = 4701365854117670629L;
	private String name = null;
	private ArrayList tables = null;
	private ConnectionMetaData metaData = null;

	/**
	 *  
	 */
	Schema( ConnectionMetaData metaData )
	{
		super( );
		this.metaData = metaData;
	}

	/**
	 * @return Returns the name.
	 */
	public String getName( )
	{
		return name;
	}

	/**
	 * @param name
	 *            The name to set.
	 */
	public void setName( String name )
	{
		this.name = name;
	}

	/**
	 * @return Returns the tables.
	 * @throws SQLException
	 */
	public ArrayList getTables( ) throws SQLException
	{
		if ( tables == null )
		{
			retrieveTables( );
		}
		return tables;
	}

	public Table getTable( String tableName ) throws SQLException
	{
		Table table = null;
		Iterator iter = getTables( ).iterator( );
		while ( iter.hasNext( ) )
		{
			table = (Table) iter.next( );
			if ( tableName.equals( table.getName( ) ) )
			{
				return table;
			}
		}
		return null;
	}

	private synchronized void retrieveTables( ) throws SQLException
	{
		if ( tables == null )
		{
			tables = new ArrayList( );
			ResultSet resultset = metaData.getMetaData( )
					.getTables( metaData.getCatalogname( ),
							getName( ),
							null,
							null );
			//Create table objects for each item
			Table table = null;
			while ( resultset.next( ) )
			{
				table = new Table( this );
				table.setName( resultset.getString( "TABLE_NAME" ) );//$NON-NLS-1$
				table.setType( resultset.getString( "TABLE_TYPE" ) );//$NON-NLS-1$
				tables.add( table );
			}
		}
	}

	/**
	 * @return Returns the metaData.
	 */
	ConnectionMetaData getMetaData( )
	{
		return metaData;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#finalize()
	 */
	protected void finalize( ) throws Throwable
	{
		tables.clear( );
		super.finalize( );
	}
}
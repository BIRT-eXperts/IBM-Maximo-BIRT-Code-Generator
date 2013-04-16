/*******************************************************************************
 * Copyright (c) 2008 Actuate Corporation.
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

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;


import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.resource.ImageRegistry;
import org.eclipse.jface.resource.JFaceResources;
import org.eclipse.swt.graphics.Image;

import com.mmkarton.mx7.reportgenerator.Activator;
import com.mmkarton.mx7.reportgenerator.provider.JdbcMetaDataProvider;


public class ProcedureNode extends ChildrenAllowedNode
		implements
			Comparable<ProcedureNode>
{
	private static Logger logger = Logger.getLogger( ProcedureNode.class.getName( ) );
	private static String PROCEDURE_ICON = ProcedureNode.class.getName( ) + ".ProcedureIcon";
	static
	{
		ImageRegistry reg = JFaceResources.getImageRegistry( );
		reg.put( PROCEDURE_ICON, Activator.getImageDescriptor("icons/column.gif" ) );//$NON-NLS-1$
		//reg.put( PROCEDURE_ICON,
			//	ImageDescriptor.createFromFile( JdbcPlugin.class, "icons/column.gif" ) );//$NON-NLS-1$
	}
	
	private String schemaName;
	private String procedureName;
	public ProcedureNode( String schemaName, String procedureName )
	{
		assert procedureName != null;
		this.schemaName = schemaName;
		this.procedureName = procedureName;
	}
	
	public int compareTo( ProcedureNode o )
	{
		/**
		 * In our case, 2 <code>ProcedureNode</code> instances need to be compared
		 * <p>only when they belong to the same schema
		 */
		return this.procedureName.compareTo( o.procedureName );
	}

	public String getDisplayName( )
	{
		return procedureName;
	}

	public Image getImage( )
	{
		return JFaceResources.getImageRegistry( ).get( PROCEDURE_ICON );
	}

	public String getQualifiedNameInSQL( boolean includeSchema )
	{
		StringBuffer sb = new StringBuffer( );
		String quoteFlag = "";
		/*if ( useIdentifierQuoteString )
		{
			quoteFlag
				= JdbcMetaDataProvider.getInstance( ).getIdentifierQuoteString( );
		}*/
		if ( includeSchema && schemaName != null )
		{
			sb.append( Utility.quoteString( schemaName, quoteFlag ) ).append( "." );
			sb.append( Utility.quoteString( procedureName, quoteFlag ) );
		}
		
		return sb.toString( );
	}

	@Override
	protected IDBNode[] refetchChildren( FilterConfig fc )
	{
		List<ProcedureColumnNode> columns = new ArrayList<ProcedureColumnNode>( );
		ResultSet rs = JdbcMetaDataProvider.getInstance( ).getProcedureColumns( schemaName, procedureName, null );
		if ( rs != null )
		{
			try
			{
				int n = 0;
				while ( rs.next( ) )
				{
					String columnName = rs.getString( "COLUMN_NAME" );
					if ( columnName == null )
					{
						// if the column name cannot retrieved ,give the unique name for this column
						n++;
						columnName = "param" + n;
					}
					String mode = Utility.toModeType( rs.getInt( "COLUMN_TYPE" ) ); 
					String type = rs.getString( "TYPE_NAME" );
					ProcedureColumnNode column = new ProcedureColumnNode( columnName,
							type, mode );
					columns.add( column );
				}
			}
			catch ( SQLException e )
			{
				logger.log( Level.WARNING, e.getLocalizedMessage( ), e );
			}
		}
		return columns.toArray( new ProcedureColumnNode[0] );
	}
}

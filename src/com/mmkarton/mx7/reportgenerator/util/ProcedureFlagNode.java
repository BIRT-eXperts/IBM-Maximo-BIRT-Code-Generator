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


public class ProcedureFlagNode extends ChildrenAllowedNode
{
	private static Logger logger = Logger.getLogger( ProcedureFlagNode.class.getName( ) );
	private static String PROCEDURE_FLAG_ICON = ProcedureFlagNode.class.getName( ) + ".ProcedureFlagIcon";
	static
	{
		ImageRegistry reg = JFaceResources.getImageRegistry( );
		reg.put( PROCEDURE_FLAG_ICON, Activator.getImageDescriptor("icons/table.gif" ) );//$NON-NLS-1$
		//reg.put( PROCEDURE_FLAG_ICON,
			//	ImageDescriptor.createFromFile( JdbcPlugin.class, "icons/table.gif" ) );//$NON-NLS-1$
	}
	
	private String schemaName;

	
	public ProcedureFlagNode( String schemaName )
	{
		this.schemaName = schemaName;
	}

	public String getDisplayName( )
	{
		return "STORED PROCEDURES";
	}

	public Image getImage( )
	{
		return JFaceResources.getImage( PROCEDURE_FLAG_ICON );
	}

	/**
	 * Just a flag node, can't be part of a SQL text
	 */
	public String getQualifiedNameInSQL(boolean includeSchema )
	{
		return null;
	}

	@Override
	protected IDBNode[] refetchChildren( FilterConfig fc )
	{
		ResultSet rs = JdbcMetaDataProvider.getInstance( )
						.getProcedures( schemaName, fc.getNamePattern( ) );
		List<ProcedureNode> procedures = new ArrayList<ProcedureNode>( );
		if ( rs != null )
		{
			try
			{
				while ( rs.next( ) )
				{
					String procedureName = rs.getString( "PROCEDURE_NAME" );
					ProcedureNode procedure = new ProcedureNode( schemaName, procedureName);
					procedures.add( procedure );
				}
			}
			catch ( SQLException e )
			{
				logger.log( Level.WARNING, e.getLocalizedMessage( ), e );
			}
		}
		return procedures.toArray( new ProcedureNode[0] );
	}

}

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

import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.resource.ImageRegistry;
import org.eclipse.jface.resource.JFaceResources;
import org.eclipse.swt.graphics.Image;

import com.mmkarton.mx7.reportgenerator.Activator;


public class ProcedureColumnNode implements IDBNode, Comparable<ProcedureColumnNode>
{
	private static String PROCEDURE_COLUMN_ICON = ProcedureColumnNode.class.getName( ) + ".ProcedureColumnIcon";
	static
	{
		ImageRegistry reg = JFaceResources.getImageRegistry( );
		reg.put( PROCEDURE_COLUMN_ICON, Activator.getImageDescriptor("icons/column.gif" ) );//$NON-NLS-1$
		//reg.put( PROCEDURE_COLUMN_ICON,
			//	ImageDescriptor.createFromFile( JdbcPlugin.class, "icons/column.gif" ) );//$NON-NLS-1$
	}
	
	private String name;
	private String type;
	private String mode;
	
	public ProcedureColumnNode( String name, String type, String mode )
	{
		assert name != null && type != null && mode != null;
		this.name = name;
		this.type = type;
		this.mode = mode;
	}

	public int compareTo( ProcedureColumnNode o )
	{
		/**
		 * In our case, 2 <code>ProcedureParameterNode</code> instances need to be compared
		 * <p>only when they belong to the same procedure
		 */
		return this.name.compareTo( o.name );
	}

	public String getDisplayName( )
	{
		return name + " (" + type + ", " + mode + ")";
	}

	public Image getImage( )
	{
		// TODO Auto-generated method stub
		return JFaceResources.getImageRegistry( ).get( PROCEDURE_COLUMN_ICON );
	}

	/**
	 * can't be part of a SQL text
	 */
	public String getQualifiedNameInSQL( boolean includeSchema )
	{
		return null;
	}

}

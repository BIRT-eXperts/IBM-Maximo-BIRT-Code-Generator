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

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.sql.ParameterMetaData;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;

import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.Platform;
import org.eclipse.swt.widgets.Control;
import org.eclipse.ui.PlatformUI;
import org.osgi.framework.Bundle;


/**
 * TODO: Please document
 * 
 * @version $Revision: 1.25 $ $Date: 2008/08/04 07:55:18 $
 */
public class Utility
{
	// flag to indicate whether JarInfo and DriverInfo in preference page have
	// been updated from String[] to JarFile and DriverInfo 
	private static boolean updatedOfJarInfo = false;
	private static boolean updatedOfDriverInfo = false;
    /**
     * 
     */
    private Utility()
    {
    }

	/**
	 * give the stored procedure's column type name from the type.
	 * @param type
	 * @return
	 */
	public static String toModeType( int type )
	{
		switch ( type )
		{
			case ParameterMetaData.parameterModeUnknown:
				return "Unknown";
			case ParameterMetaData.parameterModeIn:
				return "Input";
			case ParameterMetaData.parameterModeInOut:
				return "Input/Output";
			case ParameterMetaData.parameterModeOut:
				return "Output";
			case 5:
				return "Return Value";
			default:
				return "Unknown";
		}
	}
	
	
	/**
	 * Get Map from PreferenceStore by key
	 * @param mapKey the key of the map
	 * @return Map 
	 */
	public static Map getPreferenceStoredMap( String mapKey )
	{
		

		return new HashMap( );
	}
	
	/**
	 * Since the data type stored in this map has been changed,this method is
	 * design to surpport the former and the new preference
	 * @param map
	 * @return 
	 */
	private static Map updatePreferenceMap( Map map, String mapKey )
	{
		return new HashMap();
	}
	
	private static String getFileNameFromFilePath( String  filePath )
	{
		String fileName = filePath.substring( filePath.lastIndexOf( File.separator )
				+ File.separator.length( ) );
		return fileName;
	}
	
	/**
	 * Put <tt>value</tt> with key <tt>keyInMap</tt>into the map whose key
	 * is <tt>keyOfPreference</tt>
	 * 
	 * @param keyOfPreference
	 *            key of PreferenceStore Map
	 * @param keyInMap
	 *            key in the Map
	 * @param value
	 *            the value to be set
	 */
	public static void putPreferenceStoredMapValue( String keyOfPreference,
			String keyInMap, Object value )
	{
		Map map = getPreferenceStoredMap( keyOfPreference );
		map.put( keyInMap, value );
		setPreferenceStoredMap( keyOfPreference, map );
	}

	/**
	 * Removes map entry with key <tt>keyInMap</tt>from the map whose key
	 * is <tt>keyOfPreference</tt>
	 * @param keyOfPreference
	 *            key of PreferenceStore Map
	 * @param keyInMap
	 * 			  key in the Map
	 */
	public static void removeMapEntryFromPreferenceStoredMap(
			String keyOfPreference, String keyInMap )
	{
		Map map = getPreferenceStoredMap( keyOfPreference );
		if ( map.containsKey( keyInMap ) )
		{
			map.remove( keyInMap );
		}
		setPreferenceStoredMap( keyOfPreference, map );
	}

	/**
	 * Reset the map in PreferenceStored
	 * @param keyOfPreference key in PreferenceStore
	 * @param map the map to be set 
	 */
	public static void setPreferenceStoredMap( String keyOfPreference, Map map )
	{
		
	}
	
	public static String getConnectionPropertiesFilePath() 
	{
		Bundle bundle = Platform.getBundle( "org.eclipse.birt.report.viewer" );
		
		String loc=bundle.getLocation();
		Enumeration files = bundle.getEntryPaths("birt/WEB-INF/classes");
		URL files2 = bundle.getEntry("birt/WEB-INF/classes/mxreportdatasources.properties");
		String path= files2.getPath();
		
		while ( files!= null && files.hasMoreElements() )
		{
			String fileName = (String) files.nextElement();
			
			if ( fileName.contains("mxreportdatasources.properties"))
			{
				URL bundleURL = bundle.getEntry( fileName );
				URL fileURL=null;
				try {
					fileURL = FileLocator.resolve( bundleURL );
					return fileURL.getPath();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
				
			}
		}

		return null;
	}
	
	/**
	 * 
	 * @param control
	 * @param contextId
	 */
	public static void setSystemHelp( Control control, String contextId )
	{
		PlatformUI.getWorkbench( )
				.getHelpSystem( )
				.setHelp( control, contextId );
	}
	
	public static String quoteString( String quoted, String quoteFlag )
	{
		assert quoteFlag != null;
		if ( quoted == null )
		{
			return "";
		}
		else
		{
			return quoteFlag + quoted + quoteFlag;
		}
	}
}

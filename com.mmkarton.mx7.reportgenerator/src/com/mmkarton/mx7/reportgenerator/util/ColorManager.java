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

package com.mmkarton.mx7.reportgenerator.util;

import org.eclipse.jface.resource.JFaceResources;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.RGB;

/**
 * Manages color resource.
 */

public final class ColorManager
{

	/**
	 * This map stores color name - Color pairs, used to quickly lookup a Color
	 * of a predefined color.
	 * 
	 * @param c
	 *            color value
	 */
	public static Color getColor( int c )
	{
		RGB rgb = DEUtil.getRGBValue( c );
		return getColor( rgb );
	}

	/**
	 * This map stores color name - Color pairs, used to quickly lookup a Color
	 * of a predefined color.
	 * 
	 * @param red
	 *            red value of RGB
	 * @param green
	 *            green value of RGB
	 * @param blue
	 *            blue value of RGB
	 */
	public static Color getColor( int red, int green, int blue )
	{
		return getColor( new RGB( red, green, blue ) );
	}

	/**
	 * This map stores color name - Color pairs, used to quickly lookup a Color
	 * of a predefined color.
	 * 
	 * @param rgb
	 *            RGB value of color
	 */
	public static Color getColor( RGB rgb )
	{
		if ( rgb == null )
		{
			return null;
		}

		String key = rgb.toString( );
		Color color = JFaceResources.getColorRegistry( ).get( key );
		if ( color == null )
		{
			JFaceResources.getColorRegistry( ).put( key, rgb );
			color = JFaceResources.getColorRegistry( ).get( key );
		}
		return color;
	}
}
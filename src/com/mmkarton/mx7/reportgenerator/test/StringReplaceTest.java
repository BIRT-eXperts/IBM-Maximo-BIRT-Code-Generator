package com.mmkarton.mx7.reportgenerator.test;


/*
 ********************************************************************************
 * Copyright (c) 2009 Ing. Gerd Stockner (Mayr-Melnhof Karton Gesellschaft m.b.H.), Christian Voller (Mayr-Melnhof Karton Gesellschaft m.b.H.), CoSMIT GmbH
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *  Ing. Gerd Stockner (Mayr-Melnhof Karton Gesellschaft m.b.H.) - initial API and implementation
 *  Christian Voller (Mayr-Melnhof Karton Gesellschaft m.b.H.) - initial API and implementation
 *  CoSMIT GmbH - publishing, maintenance
 *******************************************************************************/

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class StringReplaceTest {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
        String str = "select assetnum,\ndescription,\nlocation\n from \n asset\n where orgid='FLORG'";
   String newstr = str.replaceAll("\n", "\"\n + \"");
    
   System.out.println("----old-------");
   System.out.println(str);

    System.out.println("----new-----");
    System.out.println(newstr);

   
   
}

}

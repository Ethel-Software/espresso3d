/*
 * Created on Dec 5, 2004
 *
 
   	Copyright 2004 Curtis Moxley
   	This file is part of Espresso3D.
 
   	Espresso3D is free software: you can redistribute it and/or modify
    it under the terms of the GNU Lesser General Public License as published by
    the Free Software Foundation, either version 3 of the License, or
    (at your option) any later version.

    Espresso3D is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU Lesser General Public License for more details.

    You should have received a copy of the GNU Lesser General Public License
    along with Espresso3D.  If not, see <http://www.gnu.org/licenses/>.
 */
package espresso3d.engine.logo;

import espresso3d.engine.E3DEngine;
import espresso3d.engine.logger.E3DEngineLogger;
import espresso3d.engine.logo.base.IE3DLogo;
import espresso3d.engine.logo.logos.E3DLogo0dot1;
import espresso3d.engine.logo.logos.E3DLogo0dot2;
import espresso3d.engine.logo.logos.E3DLogo0dot3;
import espresso3d.engine.logo.logos.E3DLogo0dot4;

/**
 * @author Curt
 *
 * The master class for E3DLogos.  This will randomly determine which logo to display and run it
 */
public class E3DLogoRunner 
{
    private static final int NUM_LOGOS = 4;
    /**
     * When this is called, it will determine which logo to display (randomly) and will display
     * that logo.
     * @param engine
     */
    public static void displayEngineLogo(E3DEngine engine) throws Exception
    {
        int whichLogo = (int)Math.floor(Math.random() * NUM_LOGOS);
        engine.getLogger().writeLine(E3DEngineLogger.SEVERITY_INFO, "Displaying Espresso3D logo number " + whichLogo);
        IE3DLogo logo = null;
        
        switch(whichLogo)
        {
        case 0:
        	logo = new E3DLogo0dot1();
        	break;
        case 1:
            logo = new E3DLogo0dot2();
            break;
        case 2: 
           logo = new E3DLogo0dot3();
            break;
        case 3:
        	logo = new E3DLogo0dot4();
           break;
        default:
            logo = new E3DLogo0dot1();
        }
//        logo.displayEngineLogo(engine);
    }
    
}

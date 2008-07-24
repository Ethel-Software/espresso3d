/*
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
package espresso3d.engine.fileloaders;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

import javax.imageio.ImageIO;

public class E3DFileLoader 
{
    /**
     * Loads a file by first checking the jar, then checking local file system
     * @param fileNameLocalOrSys
     * @return
     * @throws IOException
     */
    public static InputStream openFile(String fileNameLocalOrSys) throws IOException
    {
        //try from jar 1st
        InputStream is = null;
        is = Class.class.getResourceAsStream(fileNameLocalOrSys);

        //then try local file system 
        if(is == null)
            is = new BufferedInputStream(new FileInputStream(new File(fileNameLocalOrSys)));
        
        return is;
        
    }
    
    public static void closeFile(InputStream fileIS) throws IOException
    {
        if(fileIS != null)
            fileIS.close();
    }
}

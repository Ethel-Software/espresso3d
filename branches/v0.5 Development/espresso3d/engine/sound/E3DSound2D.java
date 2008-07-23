/*
 * Created on Apr 17, 2005
  *
 
   	Copyright 2005 Curtis Moxley
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
package espresso3d.engine.sound;

import espresso3d.engine.E3DEngine;

/**
 * @author Curt
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class E3DSound2D extends E3DSound 
{
    public E3DSound2D(E3DEngine engine, String soundName, int soundBufferID)
    {
        super(engine, soundName, soundBufferID);
    }
    
    public E3DSound2D(E3DSound toCopySound)
    {
        super(toCopySound);
    }
    
    public E3DSound2D(E3DSound3D toCopySound2D)
    {
        super(toCopySound2D);
    }
}

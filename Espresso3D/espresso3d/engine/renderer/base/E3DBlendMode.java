/*
 * Created on Mar 2, 2005
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
package espresso3d.engine.renderer.base;

import espresso3d.engine.E3DEngine;
import espresso3d.engine.base.E3DEngineItem;

/**
 * @author Curt
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class E3DBlendMode extends E3DEngineItem 
{
    /**
     * If an object supports blending and its blendmode is solid, no
     * blending will occur.  The object will be opaque.
     */
    public static final int BLENDMODE_SOLID = 0;
    /**
     * If an object supports blending and its blendmode is blend,
     * the object will blend with the scene.  Any alpha information
     * in the texture on the object (if it has a texture) will also
     * be used and make those sections of the texture transparent/translucent.
     */
    public static final int BLENDMODE_BLEND = 1;

    int blendMode;
    
    public E3DBlendMode(E3DEngine engine)
    {
        this(engine, BLENDMODE_SOLID);
    }

    public E3DBlendMode(E3DBlendMode toCopyBlendMode)
    {
    	this(toCopyBlendMode.getEngine(), toCopyBlendMode.getBlendMode());
    }
    
    public E3DBlendMode(E3DEngine engine, int blendMode)
    {
        super(engine);
        this.blendMode = blendMode;
    }
    public int getBlendMode() {
        return blendMode;
    }
    public void setBlendMode(int blendMode) {
        this.blendMode = blendMode;
    }
}

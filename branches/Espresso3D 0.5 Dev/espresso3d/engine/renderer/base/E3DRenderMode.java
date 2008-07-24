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
public class E3DRenderMode extends E3DEngineItem 
{
    /**
     * Render the object as a wireframe
     */
    public static final int RENDERMODE_WIREFRAME = 0;
    
    /**
     * Render the object as a solid polygon.
     */
    public static final int RENDERMODE_SOLID = 1;

    /**
     * Render the object as a textured polygon
     */
    public static final int RENDERMODE_TEXTURED = 2;
    
    private int renderMode;
    
    /**
     * Defaults to textured rendermode
     * @param engine
     */
    public E3DRenderMode(E3DEngine engine)
    {
        this(engine, RENDERMODE_TEXTURED);
    }

    public E3DRenderMode(E3DEngine engine, int renderMode)
    {
        super(engine);
        this.renderMode = renderMode;
    }

    
    public E3DRenderMode(E3DRenderMode toCopyRenderMode)
    {
    	this(toCopyRenderMode.getEngine(), toCopyRenderMode.getRenderMode());
    }
    /**
     * Get the mode that this object will be rendered.  Will be one of the RENDERMODE_* 
     * 
     * This defaults to RENDERMODE_TEXTURED
     * @return
     */
    public int getRenderMode()
    {
        return renderMode;
    }
    
    /**
     * Set the rendermode for this object.  Valid types are RENDERMODE_*
     * 
     * This defaults to RENDERMODE_TEXTURED
     * @param renderMode
     */
    public void setRenderMode(int renderMode)
    {
        this.renderMode = renderMode;
    }
    
    public int hashCode() {
    	return renderMode;
    }
    
    public boolean equals(Object arg0) {
    	return(arg0 != null && arg0 instanceof E3DRenderMode &&
    		((E3DRenderMode)arg0).renderMode == this.renderMode);
    }
}

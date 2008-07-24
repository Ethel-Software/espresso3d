/*
 * Created on Dec 21, 2004
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
package espresso3d.engine.world.sky;

import espresso3d.engine.E3DEngine;
import espresso3d.engine.lowlevel.vector.E3DVector3F;
import espresso3d.engine.renderer.base.E3DRenderable;
import espresso3d.engine.world.E3DWorld;

/**
 * @author Curt
 *
 * The base class for SkyObjects in the world.  A sky object always stays centered
 * around a viewports camera actor as it moves.  This gives the appearance that 
 * the sky object is far away (like far away mountains or sky) and simulates parallax
 * as the camera moves (tricks the eye).
 * 
 * Extending this class allows creation of your own sky objects.  The engine currently implements
 * an E3DSkyBox object.
 */
public abstract class E3DSkyObject  extends E3DRenderable 
{
    private E3DWorld world; //the world the skybox is for

    protected E3DVector3F position = new E3DVector3F(0.0, 0.0, 0.0);
    protected double scale = 1.0; //keeps track of current scale
    
    public E3DSkyObject(E3DEngine engine)
    {
        super(engine);
    }
    
    /**
     * Set the position of  the sky object and its geometry.
     * @param position
     */
    abstract public void setPosition(E3DVector3F position);
 
    /**
     * Translate the position and geometry by translationAmt
     * @param translationAmt
     */
    abstract public void translate(E3DVector3F translationAmt);

    /**
     * Scale the object geometry by scaleAmt.  This should scale the current by this (so the current scale WOULD
     * be multiplied by this scale) 
     * @param scaleAmt
     */
    abstract public void scale(double scaleAmt);
    
    /**
     * Get the world the skyobject is in
     * @return
     */
    public E3DWorld getWorld() {
        return world;
    }
    
    /**
     * Set the world the skyobject is in.  This will be handled automatically
     * when the skyobject is added to a world
     * @param world
     */
    public void setWorld(E3DWorld world) {
        this.world = world;
    }

    /**
     * Called whenever the position of the camera moves or the viewport size changes.
     * A sky object should always center itself around the position and 
     * set its size to just under the far clipping plane.  
     * @param position
     * @param scale
     */
    public void update(E3DVector3F position, double scale)
    {
        E3DVector3F translationAmt = position.subtract(this.position);
        
        translate(translationAmt);
        
        setScale(scale);
    }

    /**
     * Get the position the skyobject is currently at
     * @return
     */
    public E3DVector3F getPosition() {
        return position;
    }
    
    /**
     * Set the scale to a given scaleAmt.  This reduces the scale of the object to 1
     * before applying this (it doesn't multiply the current scale by this scale)
     * @param scaleAmt
     */
    public void setScale(double scaleAmt)
    {
        if(scale == scaleAmt)
            return;
        
        scale(1/scale);
        scale(scaleAmt);
    }

    /**
     * Get the current scale of the sky object
     * @return
     */
    public double getScale() {
        return scale;
    }
    
    
}

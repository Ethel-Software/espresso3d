/*
 * Created on Jan 13, 2005
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
package espresso3d.engine.renderer.texture;

import espresso3d.engine.E3DEngine;
import espresso3d.engine.base.E3DEngineItem;

/**
 * @author Curt
 *
 * A single frame of animation for an animated texture.  Multiple frames
 * are combined to create an animated texture for objects that support it.
 */
public class E3DAnimatedTextureFrame extends E3DEngineItem
{
    private double life;
    private double age;
    private E3DTexture texture;
    
    /**
     * Create an animated texture frame.
     * @param engine Engine the texture will reside in
     * @param textureName Name of the texture for this frame
     * @param life Length of time (Seconds) this frame will be rendered before going to the next
     */
    public E3DAnimatedTextureFrame(E3DEngine engine, String textureName, double life)
    {
        super(engine);
        texture = new E3DTexture(engine, textureName);
        this.life = life;
        this.age = 0;
    }
    
    /**
     * Resets the frame's age.  Normally only used by the engine
     */
    public void reset(){
    	this.age = 0;
    }
    
    /**
     * Gets the life of the frame (number of seconds it renders before going to the next frame)
     * @return
     */
    public double getLife() {
        return life;
    }

    /**
     * Gets the life of the frame (number of seconds it renders before going to the next frame)
     * @param life  Number of seconds to render this frame
     */
    public void setLife(double life) {
        this.life = life;
    }
    
    /**
     * Returns the E3DTexture object for this frame
     * @return
     */
    public E3DTexture getTexture() {
        return texture;
    }

    /**
     * Sets the textue for the frame
     * @param texture
     */
    public void setTexture(E3DTexture texture) {
        this.texture = texture;
    }
    
    /**
     * Returns the age of the frame (how long its been rendererd already)
     * @return
     */
    public double getAge() {
        return age;
    }
    
    protected boolean update(double lastFrameTimeSeconds)
    {
        if(life > 0)
        {
	        this.age += lastFrameTimeSeconds;
	        if(age > life)
	            return false;
	        else
	            return true;
        }
        else return true;
    }
    
    protected boolean isFrameChangeNeeded(double lastFrameTimeSeconds)
    {
        if(life > 0)
        {
	        if(age + lastFrameTimeSeconds > life)
	            return true;
	        else
	            return false;
        }
        return false;
    }
}

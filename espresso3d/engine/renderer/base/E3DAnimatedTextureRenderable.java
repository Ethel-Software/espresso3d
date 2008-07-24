/*
 * Created on Jan 14, 2005
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
import espresso3d.engine.renderer.texture.E3DAnimatedTexture;
import espresso3d.engine.renderer.texture.E3DAnimatedTextureFrame;

/**
 * @author cmoxley
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
abstract public class E3DAnimatedTextureRenderable extends E3DTexturedRenderable
{
	private E3DAnimatedTexture animatedTexture;
	
	/**
	 * Create the object without animation
	 * @param engine
	 * @param textureName
	 */
    public E3DAnimatedTextureRenderable(E3DEngine engine, E3DRenderMode renderMode, E3DBlendMode blendMode, String textureName)
    {
        super(engine, renderMode, blendMode, textureName);
  
        animatedTexture = new E3DAnimatedTexture(engine, new String[]{textureName}, new double[]{-1}, -1);
    }
	
    public E3DAnimatedTextureRenderable(E3DEngine engine, E3DRenderMode renderMode, E3DBlendMode blendMode, E3DAnimatedTextureFrame[] animatedTextureFrames, int loops)
    {
        super(engine, renderMode, blendMode, animatedTextureFrames[0].getTexture().getTextureName());
        
        animatedTexture = new E3DAnimatedTexture(engine, animatedTextureFrames, loops);
        
    }
    
    /* (non-Javadoc)
	 * @see espresso3d.engine.renderer.base.E3DRenderable#render()
	 */
	abstract public void render();
	
	public E3DAnimatedTexture getAnimatedTexture()
	{
		return animatedTexture;
	}
	public void setAnimatedTexture(E3DAnimatedTexture animatedTexture)
	{
		this.animatedTexture = animatedTexture;
	}
	
	/**
     * 
     * @param lastFrameTimeSeconds
     * @return
     *  Returns true if the animation changed
	 */
    public boolean update(double lastFrameTimeSeconds)
	{
        //If the frame updated, update the texture and tell return true so it gets rehashed if necessary
		if(animatedTexture.update(lastFrameTimeSeconds))
        {
            setTexture(animatedTexture.getCurFrame().getTexture());
		    return true;
        }
        return false;
	}
}

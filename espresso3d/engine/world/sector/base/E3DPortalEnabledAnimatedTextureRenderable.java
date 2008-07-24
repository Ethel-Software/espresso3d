/*
 * Created on Jan 25, 2005
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
package espresso3d.engine.world.sector.base;

import espresso3d.engine.E3DEngine;
import espresso3d.engine.lowlevel.vector.E3DVector3F;
import espresso3d.engine.renderer.base.E3DAnimatedTextureRenderable;
import espresso3d.engine.renderer.base.E3DBlendMode;
import espresso3d.engine.renderer.base.E3DRenderMode;
import espresso3d.engine.renderer.texture.E3DAnimatedTextureFrame;
import espresso3d.engine.world.sector.E3DSector;
import espresso3d.engine.world.sector.particle.E3DParticle;
import espresso3d.engine.world.sector.particle.E3DSprite;
import espresso3d.engine.world.sector.portal.E3DPortalCollision;

/**
 * @author cmoxley
 *
 * A base class for renderable's that need to be portal-ready, blended, and have the ability to have animated textures.
 */
public abstract class E3DPortalEnabledAnimatedTextureRenderable extends E3DAnimatedTextureRenderable implements IE3DPortalEnabledItem
{
	private E3DSector sector;
	
	public E3DPortalEnabledAnimatedTextureRenderable(E3DEngine engine, String textureName)
	{
	    this(engine, textureName, new E3DBlendMode(engine, E3DBlendMode.BLENDMODE_SOLID));
	}

	public E3DPortalEnabledAnimatedTextureRenderable(E3DEngine engine, String textureName, E3DBlendMode blendMode)
	{
		super(engine, new E3DRenderMode(engine, E3DRenderMode.RENDERMODE_TEXTURED), blendMode, textureName);
	}

    public E3DPortalEnabledAnimatedTextureRenderable(E3DEngine engine, E3DAnimatedTextureFrame[] animatedTextureFrames, int animationLoops)
    {
        this(engine, animatedTextureFrames, animationLoops, new E3DBlendMode(engine, E3DBlendMode.BLENDMODE_SOLID));
    }

    public E3DPortalEnabledAnimatedTextureRenderable(E3DEngine engine, E3DAnimatedTextureFrame[] animatedTextureFrames, int animationLoops, E3DBlendMode blendMode)
    {
        super(engine, new E3DRenderMode(engine, E3DRenderMode.RENDERMODE_TEXTURED), blendMode, animatedTextureFrames, animationLoops);
    }
    
	public E3DSector getSector()
	{
		return sector;
	}
	public void setSector(E3DSector sector)
	{
		this.sector = sector;
	}
	
	public void checkSectorChangeDuringMovement(E3DVector3F startPos, E3DVector3F endPos)
	{
        if(sector == null)
            return;

	    if(this instanceof E3DParticle)
	    {
	        //TODO: Look at this.  At the time, I don't think a single particle should move itself into a different sector for render.
	        //  Because collision detection currently checks against only the sourceObject's containing sector, this could be a problem
	        //  for particle -> object collision (ie: it crosses into a different sector, but the entire system isn't in that sector so it 
	        //  wouldn't get checked against).  This could be bad for things like flame throwers, etc..  or just ps collision in general
	        //  Consider having a "temporary" PS list that would hold a PS as long as some particles are in the sector so it would be registered
	        //  in both sectors (But only rendered from the root sector).  Would have to keep track of each particle in the "temp" sector
	        //  and update that list whenever they are no longer in the list..
	        return;
	    }	        
	    
        E3DPortalCollision portalCollision =  sector.checkPortalCollision(startPos, endPos);
        if(portalCollision == null)
            return;
        
        if(portalCollision.getToSector() != null && portalCollision.getToSector() != sector)
        {
            sector.moveFromSector(this);
            portalCollision.getToSector().moveToSector(this);
	    }
	}

}

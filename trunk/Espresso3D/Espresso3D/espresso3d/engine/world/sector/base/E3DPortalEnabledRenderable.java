/*
 * Created on Dec 7, 2004
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
package espresso3d.engine.world.sector.base;

import espresso3d.engine.E3DEngine;
import espresso3d.engine.lowlevel.vector.E3DVector3F;
import espresso3d.engine.renderer.base.E3DRenderable;
import espresso3d.engine.world.sector.E3DSector;
import espresso3d.engine.world.sector.actor.E3DActor;
import espresso3d.engine.world.sector.light.E3DLight;
import espresso3d.engine.world.sector.particle.E3DParticleSystem;
import espresso3d.engine.world.sector.portal.E3DPortalCollision;

/**
 * @author cmoxley
 *
 * For any class to support movement through portals automatically,
 * they must extend this class or E3DPortalEnabledTexturedRenderableItem
 * 
 * This is completely useless to a user outside of the engine because
 * this file needs to have its user objects defined in it to allow the object to
 * switch sectors automatically.
 */
public abstract class E3DPortalEnabledRenderable extends E3DRenderable implements IE3DPortalEnabledItem
{
	private E3DSector sector;
	
	public E3DPortalEnabledRenderable(E3DEngine engine)
	{
		super(engine);
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
        
        E3DPortalCollision portalCollision =  sector.checkPortalCollision(startPos, endPos);
	    if(portalCollision == null)
            return;
        
        if(portalCollision.getToSector() != null && portalCollision.getToSector() != sector)
	    {
            sector.moveFromSector(this);
            sector.moveToSector(this);
	    }
	}
}

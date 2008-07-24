/*
 * Created on Oct 16, 2004
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
package espresso3d.engine.logo.actors;

import espresso3d.engine.E3DEngine;
import espresso3d.engine.collision.E3DCollision;
import espresso3d.engine.world.E3DWorld;
import espresso3d.engine.world.sector.actor.E3DActor;

/**
 * @author Curt
 *
 * A high res version of the espresso3d logo actor
 */
public final class E3DHighResLogoActor extends E3DActor 
{
	public E3DHighResLogoActor(E3DEngine engine, E3DWorld world, boolean lit)
	{
		super(engine, world, "logoActor");

		getMesh().setLit(lit);

		try{
			world.loadTextureSet("/espresso3d/engine/logo/texture/logotextureset.txt");
		}catch(Exception e){
			e.printStackTrace();
			return;
		}
		
		try{
		    loadActor("/espresso3d/engine/logo/models/highreslogo.e3a");
		}catch(Exception e){
		    e.printStackTrace();
		    return;
		    
		}
		
	}

	public void onCollisionActor(E3DCollision collision) {
		// TODO Auto-generated method stub

	}
	
    public void onCollisionSprite(E3DCollision collision) {
        // TODO Auto-generated method stub

    }
	
	/* (non-Javadoc)
	 * @see espresso3d.engine.world.actor.E3DActor#onLoadFromMapfile()
	 */
	public E3DActor onGetClone() throws Exception{
		return (E3DActor)this.clone();
	}
	
	public boolean isCollideable(){
	    return false;
	}
}

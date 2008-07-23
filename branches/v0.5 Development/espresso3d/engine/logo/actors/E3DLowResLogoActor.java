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
import espresso3d.engine.lowlevel.geometry.E3DTriangle;
import espresso3d.engine.lowlevel.vector.E3DVector2F;
import espresso3d.engine.lowlevel.vector.E3DVector3F;
import espresso3d.engine.world.E3DWorld;
import espresso3d.engine.world.sector.actor.E3DActor;

/**
 * @author Curt
 *
 * A low res version of the espresso3d logo actor
 */
public final class E3DLowResLogoActor extends E3DActor 
{
	public E3DLowResLogoActor(E3DEngine engine, E3DWorld world, boolean lit)
	{
		super(engine, world, "logoActor");
		try{
			world.loadTextureSet("/espresso3d/engine/logo/texture/logotextureset.txt");
		}catch(Exception e){
			e.printStackTrace();
			return;
		}
		super.getMesh().addTriangle(new E3DTriangle(engine, new E3DVector3F(-10, 0, 10), new E3DVector3F(10, 0, 10), new E3DVector3F(-10, 0, -10), 
                                                new E3DVector2F(0, 1), new E3DVector2F(1, 1), new E3DVector2F(0, 0), "ESPRESSO3DLOGO")); //getTextureIDByName("ESPRESSO3DLOGO")));
		super.getMesh().addTriangle(new E3DTriangle(engine, new E3DVector3F(-10, 0, -10), new E3DVector3F(10, 0, 10), new E3DVector3F(10, 0, -10),  
                                                new E3DVector2F(0, 0),  new E3DVector2F(1, 1), new E3DVector2F(1, 0), "ESPRESSO3DLOGO"));

		getMesh().setLit(lit);
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

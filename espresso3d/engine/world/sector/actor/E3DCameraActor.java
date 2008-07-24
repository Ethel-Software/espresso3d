/*
 * Created on Oct 5, 2004
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
package espresso3d.engine.world.sector.actor;

import espresso3d.engine.E3DEngine;
import espresso3d.engine.collision.E3DCollision;
import espresso3d.engine.world.E3DWorld;
/**
 * @author Curt
 *
 * This is a basic actor with no polygons.  Useful for setting a viewport camera without havng
 * to extend E3DActor and create your own actor.
 */
public class E3DCameraActor extends E3DActor 
{
	public E3DCameraActor(E3DEngine engine, E3DWorld world, String actorID)
	{
		super(engine, world, actorID);
//		setBoundingObject(new E3DBoundingSphere(engine, 2.2));
	}
	
	public E3DCameraActor(E3DCameraActor toCopyActor)
	{
		super(toCopyActor);
	}
	
	public void onCollisionActor(E3DCollision collision) {
		// TODO Auto-generated method stub
	
	}	

	public void onCollisionSprite(E3DCollision collision) {
		// TODO Auto-generated method stub
	
	}	
	
	public E3DActor onGetClone() throws Exception {
		return new E3DCameraActor(this);
	}
	
	public boolean isCollideable(){
	    return false;
	}
}

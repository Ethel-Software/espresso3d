/*
 * Created on May 3, 2005
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
package espresso3d.engine.logo.actors;

import espresso3d.engine.E3DEngine;
import espresso3d.engine.collision.E3DCollision;
import espresso3d.engine.world.E3DWorld;
import espresso3d.engine.world.sector.actor.E3DActor;

/**
 * @author Curt
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class E3DReceiverActor extends E3DActor 
{
    public E3DReceiverActor(E3DEngine engine, E3DWorld world, String actorID)
    {
        super(engine, world, actorID);
    
        try{
            loadActor("/espresso3d/engine/logo/models/receiver.e3a");
        }catch(Exception e){
            e.printStackTrace();
            return;
            
        }
    }

    /* (non-Javadoc)
     * @see espresso3d.engine.collision.base.IE3DCollisionDetectableObject#onCollisionActor(espresso3d.engine.collision.E3DCollision)
     */
    public void onCollisionActor(E3DCollision collision) {
        // TODO Auto-generated method stub

    }

    /* (non-Javadoc)
     * @see espresso3d.engine.collision.base.IE3DCollisionDetectableObject#onCollisionSprite(espresso3d.engine.collision.E3DCollision)
     */
    public void onCollisionSprite(E3DCollision collision) {
        // TODO Auto-generated method stub

    }

    /* (non-Javadoc)
     * @see espresso3d.engine.world.sector.actor.E3DActor#onGetClone()
     */
    public E3DActor onGetClone() throws Exception {
        // TODO Auto-generated method stub
        return null;
    }

    /* (non-Javadoc)
     * @see espresso3d.engine.collision.base.IE3DCollisionDetectableObject#isCollideable()
     */
    public boolean isCollideable() {
        // TODO Auto-generated method stub
        return false;
    }

}

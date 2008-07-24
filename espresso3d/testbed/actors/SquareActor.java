/*
 * Created on Oct 27, 2004
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
package espresso3d.testbed.actors;

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
public class SquareActor extends E3DActor 
{
	public SquareActor(E3DEngine engine, E3DWorld world, String actorID) throws Exception
	{
		super(engine, world, actorID);
		
		super.getMesh().setRendered(true);
		super.getMesh().setLit(false);
		
//		this.loadActor("espresso3d\\testbed\\media\\models\\triangle.e3a", false);
		this.loadActor("espresso3d\\testbed\\media\\models\\receiver.e3a");
//        this.scale(5);
   //     this.setDebugSkeletonDisplayed(true);
        
/*        E3DAnimation animation = new E3DAnimation(engine, "animation1");
        E3DAnimationKeyFrame keyFrame = new E3DAnimationKeyFrame(engine, "frame1", 20);
        E3DAnimationCommand command = new E3DAnimationCommand(engine, "groupa", "bone1");*/
	}

	public SquareActor(SquareActor toCopyActor)
	{
		super(toCopyActor); //copies all positions & triangles, we're responsible for anything local to this class and bounding objects
	}
	
	/* (non-Javadoc)
	 * @see espresso3d.engine.collision.base.E3DCollisionResponseCallback#onCollision(espresso3d.engine.collision.E3DCollision)
	 */
	public void onCollisionActor(E3DCollision collision)
	{
	}

	public void onCollisionSprite(E3DCollision collision)
	{
	}

	/* (non-Javadoc)
	 * @see espresso3d.engine.world.sector.actor.E3DActor#onGetClone()
	 */
	public E3DActor onGetClone() throws Exception {
		SquareActor actor = new SquareActor(this);
		return actor;
	}

	public boolean isCollideable(){
	    return true;
	}	
}

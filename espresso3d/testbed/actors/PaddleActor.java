/*
 * Created on Jul 26, 2004
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
import espresso3d.engine.collision.E3DCollisionHandler;
import espresso3d.engine.lowlevel.vector.E3DVector3F;
import espresso3d.engine.world.E3DWorld;
import espresso3d.engine.world.sector.actor.E3DActor;

/**
 * @author espresso3d
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class PaddleActor extends E3DActor
{
	private double speed = 0.0;
	
	public PaddleActor(E3DEngine engine, E3DWorld world, String actorID, double speed) throws Exception
	{
		super(engine, world, actorID);
		setSpeed(speed);
		super.getMesh().setLit(true);
		loadActor("espresso3d\\testbed\\media\\models\\paddle.e3a");
	
		scale(1.5);
	}
	
	public PaddleActor(PaddleActor toCopyActor)
	{
		super(toCopyActor); //copies all positions & triangles, we're responsible for anything local to this class and bounding objects
		
		setSpeed(toCopyActor.getSpeed());
	}

	/**
	 * This moves the paddle in the direction of translationVec.
	 * @param translationVec
	 */
	public void move(E3DVector3F translationVec)
	{
		if(translationVec != null)
		{
			//Check if there's a collision where we want to move it
            E3DCollision collision = getEngine().getCollisionHandler().checkCollision(E3DCollisionHandler.COLLISIONTYPE_SOURCE_BOUNDINGOBJECT, E3DCollisionHandler.COLLISIONTYPE_COLLIDEE_BOUNDINGOBJECT_IFAVAIL, true, this, this.getOrientation().getPosition(), this.getOrientation().getPosition().add(translationVec));
	
			//If there is no collision, move it.  Otherwise don't
			if(collision == null)
				this.translate(translationVec);
		}
	}
	
	public double getSpeed()
	{
		return speed;
	}
	
	public double getTimeScaledSpeed()
	{
		return getEngine().getFpsTimer().getLastUpdateTimeSeconds() * speed;
	}
	
	/**
	 * @param speed The speed to set.
	 */
	public void setSpeed(double speed) {
		this.speed = speed;
	}

	public void onCollisionActor(E3DCollision collision) {
		//For a paddle, onCollision is only hit when a ball runs into it.  We don't need to do anything right now
		// but someday we might play a sound here..
	}	
	
	public void onCollisionSprite(E3DCollision collision){
	}
	
	//onGetClone is required for loading an actor from a mapfile.  This needs to create a 100% clone from the 
	// preloading version of itself.
	public E3DActor onGetClone() throws Exception {
		PaddleActor actor = new PaddleActor(this);
		return actor;
	}
	
	public boolean isCollideable(){
	    return true;
	}	
}
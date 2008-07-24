/*
 * Created on Aug 8, 2004
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
import espresso3d.engine.collision.bounding.E3DBoundingSphere;
import espresso3d.engine.common.E3DConstants;
import espresso3d.engine.lowlevel.geometry.E3DTriangle;
import espresso3d.engine.lowlevel.vector.E3DVector3F;
import espresso3d.engine.renderer.base.E3DRenderMode;
import espresso3d.engine.world.E3DWorld;
import espresso3d.engine.world.sector.actor.E3DActor;

/**s
 * @author espresso3d
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class BallActor extends E3DActor
{
	double speed = 0.0;
	
	public BallActor(E3DEngine engine, E3DWorld world, String actorID, double speed) throws Exception
	{
		super(engine, world, actorID); 
		setSpeed(speed);
		super.getMesh().setLit(false);
		setBoundingObject(new E3DBoundingSphere(engine, 0.5));
		this.loadActor("espresso3d\\testbed\\media\\models\\dwarf2.e3a"); //testExport3Pieces.e3a", false); //lotsFacesBall.e3a", false);
		
		for(int i=0; i < this.getTriangleList().size(); i++)
		{
			E3DTriangle triangle = (E3DTriangle)getTriangleList().get(i);
			triangle.setAlpha(1);
		}
        scale(0.5);
//        setRenderMode(new E3DRenderMode(engine, E3DRenderMode.RENDERMODE_TEXTURED));
//        setRenderMode(new E3DRenderMode(engine, E3DRenderMode.RENDERMODE_WIREFRAME));
        getMesh().setAlpha(1);
//        super.getMesh().setRendered(false);
	}

	public BallActor(BallActor toCopyActor)
	{
		super(toCopyActor); //copies all positions & triangles, we're responsible for anything local to this class and bounding objects
		
		setBoundingObject(new E3DBoundingSphere(toCopyActor.getEngine(), 0.5));
		setSpeed(toCopyActor.getSpeed());
	}
	
	/**
	 * 
	 * @return Returns the speed.
	 */
	public double getSpeed() {
		return getEngine().getFpsTimer().getLastUpdateTimeSeconds() * speed;
	}
	/**
	 * @param speed The speed to set.
	 */
	public void setSpeed(double speed) {
		this.speed = speed;
	}
	
	public void move()
	{
	//	E3DVector3F indicatorColor = new E3DVector3F(0.0f, 1.0f, 0.0f);
		
		if(getSpeed() > 3)
		    return;
		
		//Check if there's a collision
//        E3DCollision collision = getEngine().getCollisionHandler().checkCollision(E3DCollisionHandler.COLLISIONTYPE_SOURCE_BOUNDINGOBJECT, E3DCollisionHandler.COLLISIONTYPE_COLLIDEE_BOUNDINGOBJECT_IFAVAIL, false, this,getOrientation().getPosition(), getOrientation().getPosition().add(getOrientation().getForward().scale(getSpeed())));
		
	//	if(collision != null) //if there is a collision
	//		onCollisionActor(collision); //call the handler

		moveForward(getSpeed()); //move the ball forward
	}
	
	public void respondToCollision(E3DCollision collision)
	{
		//1.  When the ball hits an object, rotate it the amount needed depending on how it hit the object
		//Get angle between the forward vec and collision triangle normal. Then rotate the thing 180deg - 2xThat
		E3DVector3F collisionNormal = null;
		
		collisionNormal = collision.getColliderCollisionNormal(); //This is the normal of the thing it hit
		
		E3DVector3F directionVecReversed = getOrientation().getForward().scale(-1); //reverse the balls direction vector
		
		E3DVector3F directionCollisionUpVec = collisionNormal.crossProduct(directionVecReversed).normalise(); //Get a vector pointing up between the normal and reversed vector for use in rotate the balls direction

		double angleBetween = collisionNormal.angleBetweenRads(directionVecReversed); //Get the angle between the direction the ball was coming when it hit the object and the direction collision point of the object is facing
		
		double halfPi = E3DConstants.PI / 2; //We need a halfPi variable here (90 degrees)
		
		//Depending on whether the balls hits the front of the back of the object, the angle it needs to go differs
		if((angleBetween <= halfPi && angleBetween >= 0) || 
			(angleBetween >= -halfPi && angleBetween <=0))
			rotate(E3DConstants.PI - (2*angleBetween), directionCollisionUpVec);
		else
			rotate(-(E3DConstants.PI - (2*angleBetween)), directionCollisionUpVec);
		
		//2. Set its position so its off the triangle/object (can't get behind it).  Factor in the balls bounding sphere to push it off the object that much
		E3DBoundingSphere boundingSphere = (E3DBoundingSphere)getBoundingObject();
		setPosition(collision.getIntersectionPt().add(collisionNormal.scale(boundingSphere.getRadius()))); 
	}
	
	/**
	 * onCollisionParticle is a callback that is called if another actor runs into the ball
	 * @param collision
	 */
	public void onCollisionActor(E3DCollision collision) 
	{
	  //  respondToCollision(collision);
	}
	
	/**
	 * onCollisionParticle is a callback that is called if a sprite (sprite, billboard sprite, particle) runs into the ball
	 * @param collision
	 */
	public void onCollisionSprite(E3DCollision collision)
	{
	    //do nothing if a particle collides with the actor
	}
	
	//onGetClone is required for loading an actor from a mapfile.  This needs to create a 100% clone from the 
	// preloading version of itself.
	public E3DActor onGetClone() throws Exception {
		BallActor actor = new BallActor(this);
		return actor;
	}
	
	public boolean isCollideable(){
	    return true;
	}
}
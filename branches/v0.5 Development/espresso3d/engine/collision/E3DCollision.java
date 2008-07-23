/*
 * Created on Aug 1, 2004
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
package espresso3d.engine.collision;
import espresso3d.engine.E3DEngine;
import espresso3d.engine.collision.base.IE3DCollisionDetectableObject;
import espresso3d.engine.lowlevel.geometry.E3DPoint;
import espresso3d.engine.lowlevel.vector.E3DVector3F;
import espresso3d.engine.lowlevel.vector.E3DVector4F;
import espresso3d.engine.renderer.base.E3DRenderable;
import espresso3d.engine.world.E3DWorld;
import espresso3d.engine.world.sector.E3DSector;
/**
 * @author espresso3d
 *
 * This is the object that is returned when a collision is detected that contains information about the collision.
 */
public class E3DCollision extends E3DRenderable{
	private Object collideeBoundingObject = null;
	private E3DSector collisionSector = null;
	private E3DWorld collisionWorld = null;
    private E3DVector3F intersectionPt = null;
    private E3DVector3F colliderCollisionNormal = null;
    private E3DVector3F collideeCollisionNormal = null; //If another actor was collided into, we put what its collision normal should be here.  If we then notify the collided actor, we replace the collision normal with this
    private IE3DCollisionDetectableObject collideeObject = null; //what we ran into
    
	public E3DCollision(E3DEngine engine)
	{
		super(engine);
	}
	
	public E3DCollision(E3DCollision toCopy)
	{
		super(toCopy.getEngine());
		setCollideeBoundingObject(toCopy.getCollideeBoundingObject());
		setCollisionSector(toCopy.getCollisionSector());
		setCollisionWorld(toCopy.getCollisionWorld());
        setIntersectionPt(toCopy.getIntersectionPt());
        setColliderCollisionNormal(toCopy.getColliderCollisionNormal());
        setCollideeCollisionNormal(toCopy.getCollideeCollisionNormal());
        setCollideeObject(toCopy.getCollideeObject());
	}

    /**
	 * To see a collision graphically, a collision object
	 * can be placed on the externalrenderable list of the engine.
	 * 
	 * When rendered will this indicate collision area with red marker at the intersection point
	 */
    public void render() 
    {
        E3DVector4F indicatorColor = new E3DVector4F(1.0f, 0.0f, 0.0f, 1.0);
        
        //render the intersection point
        if(getIntersectionPt() != null)
        {
            E3DPoint point = new E3DPoint(getEngine(), getIntersectionPt(), indicatorColor, 2.0);
            point.render();
        }
    }
	/**
	 * @return Returns the collisionSector.
	 */
	public E3DSector getCollisionSector() {
		return collisionSector;
	}
	/**
	 * @param collisionSector The collisionSector to set.
	 */
	public void setCollisionSector(E3DSector collisionSector) {
		this.collisionSector = collisionSector;
	}
	/**
	 * @return Returns the type of object the sourceActor collided with.
	 * This will either be a Trangle, or it will be one of the bounding object types (use instanceof to determine which)
	 */
	public Object getCollideeBoundingObject() {
		return collideeBoundingObject;
	}
	/**
	 * @param collideeBoundingObject The collidees bounding object to set.
	 */
	public void setCollideeBoundingObject(Object collideeBoundingObject) {
		this.collideeBoundingObject = collideeBoundingObject;
	}
	/**
	 * @return Returns the collisionWorld.
	 */
	public E3DWorld getCollisionWorld() {
		return collisionWorld;
	}
	/**
	 * @param collisionWorld The collisionWorld to set.
	 */
	public void setCollisionWorld(E3DWorld collisionWorld) {
		this.collisionWorld = collisionWorld;
	}
	
	/**
	 * Print out basic information regarding a collision
	 */
	public String toString()
	{
		return "Collision===\n" + 
			   "IntersectionPt: " + getIntersectionPt() +
			   (getCollideeObject() != null ? "With object." : "");
		
	}
	
    /**
     * @return 
     *  Returns the collisionPt.
     */
    public E3DVector3F getIntersectionPt() {
        return intersectionPt;
    }
    /**
     * @param intersectionPt The collisionPt to set.
     */
    public void setIntersectionPt(E3DVector3F intersectionPt) {
        this.intersectionPt = intersectionPt;
    }

    
    /**
     * Get the normal at the point of collision if available
     * @return
     */
    public E3DVector3F getColliderCollisionNormal() {
        return colliderCollisionNormal;
    }
    
    /**
     * Set the normal at the point of the collision
     * @param collisionNormal
     */
    public void setColliderCollisionNormal(E3DVector3F colliderCollisionNormal) {
        this.colliderCollisionNormal = colliderCollisionNormal;
    }

    /**
     * This is only applicable for new collision detectors.
     *  If another actor was collided into, we put what its collision normal should be here.  
     * If we then notify the collided actor, we replace the collision normal with this so
     * it has the correct collision normal.  So, this is the collision normal of
     * the actor that was collided into
     * @return
     */
    public E3DVector3F getCollideeCollisionNormal() {
        return collideeCollisionNormal;
    }
    
    /**
     * This is only applicable for new collision detectors.
     *  If another actor was collided into, we put what its collision normal should be here.  
     * If we then notify the collided actor, we replace the collision normal with this so
     * it has the correct collision normal
     * @param collidedActorCollisionNormal
     */
    public void setCollideeCollisionNormal(
            E3DVector3F collideeCollisionNormal) {
        this.collideeCollisionNormal = collideeCollisionNormal;
    }
    public IE3DCollisionDetectableObject getCollideeObject() {
        return collideeObject;
    }
    public void setCollideeObject(IE3DCollisionDetectableObject collideeObject) {
        this.collideeObject = collideeObject;
    }
}

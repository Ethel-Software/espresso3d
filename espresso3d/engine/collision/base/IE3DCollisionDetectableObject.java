/*
 * Created on Nov 24, 2004
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
package espresso3d.engine.collision.base;

import java.util.ArrayList;

import espresso3d.engine.E3DEngine;
import espresso3d.engine.collision.E3DCollision;
import espresso3d.engine.collision.bounding.IE3DBoundingObject;
import espresso3d.engine.lowlevel.matrix.E3DOrientation;
import espresso3d.engine.world.sector.E3DSector;

/**
 * @author Curt
 *
 * For an object to be collision detectable, it must implement this interface
 */
public interface IE3DCollisionDetectableObject {
    /**
     * Must return the engine the object is in
     * @return
     */
    public E3DEngine getEngine();
    

    /**
     * Must return the sector the object is in
     * @return
     */
    public E3DSector getSector();
    
    /**
     * Return the bounding object or null for bounding collision tests
     * @return
     */
    public IE3DBoundingObject getBoundingObject();

    public E3DOrientation getOrientation();
    
    /**
     * This is called when another actor collides with this object
     * @param collision
     */
	public void onCollisionActor(E3DCollision collision);    

	/**
	 * This is called when a sprite collides with this object
	 * @param collision
	 */
	public void onCollisionSprite(E3DCollision collision);
	
	/**
	 * Must return an ArrayList of E3DTriangles that make up the geometry of the object.
	 * This is used for direct triangle collisions
	 * @return
	 */
	public ArrayList getTriangleList();
	
	/**
	 * This will ideally only return unique vertices that the object has.
	 * @return
	 */
	public ArrayList getUniqueVertexPositionList();
	
	/**
	 * Return true if the collision detector should include this when checking for collisions of other objects
	 * into this.  Otherwise, false and it will be ignored in other object's collision detection
	 *	 
	 * This does not automatically make this stop causing collisions with their own movement (in all
	 * cases except particles, this can only be triggered by the user).
	 * it just keeps the object from being run into by other object's movement.
	 *
	 *  @return
	 */
	public boolean isCollideable();
}

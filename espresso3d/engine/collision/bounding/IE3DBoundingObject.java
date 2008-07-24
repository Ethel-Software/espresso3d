/*
 * Created on Sep 22, 2004
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
package espresso3d.engine.collision.bounding;

import espresso3d.engine.collision.E3DCollision;
import espresso3d.engine.collision.base.IE3DCollisionDetectableObject;
import espresso3d.engine.collision.base.IE3DCollisionDetector;
import espresso3d.engine.lowlevel.matrix.E3DOrientation;
import espresso3d.engine.lowlevel.vector.E3DVector3F;

/**
 * @author Curt
 *
 * The base bounding object class which other bounding objects extend
 */
public interface IE3DBoundingObject 
{
    /**
     * Every bounding object must implement this method that takes a list of E3DLine's
     * and returns the closest collision between any of the lines and the bounding object itself,
     * therefore, it must have a collision detector
     * @param lineList
     * @return
     */
    public IE3DCollisionDetector getCollisionDetector();
    
    /**
     * Bounding objects need to have an actor that they are bounding
     * @return
     */
    public IE3DCollisionDetectableObject getParentObject();

    /**
     * Set the parent object of the bounding object.  This is normally
     * handled by the engine itself when a bounding object is added to an object (like an actor).
     * @param actor
     */
    public void setParentObject(IE3DCollisionDetectableObject actor);

    /**
     * This will automatically set the position of the bounding object to be that
     * of its parent object
     */
    public void centerAroundParentObject();
    
    /**
     * Returns a clone of the bounding object
     * @return
     */
    public IE3DBoundingObject onGetClone();
    
    /**
     * 
     * @param startPos World coordinate start position
     * @param endPos World coordinate end position
     * @return
     */
    public E3DCollision checkLineIntersectionCollision(E3DVector3F startPos, E3DVector3F endPos);

    /**
     * Every bounding object has an orientation (position/rotation).  This is NOT directly related to its
     * parent objects orientation.  IE: its not cumulative, this orientation is the actual orientation
     * of the bounding object in the world.
     * @return
     */
    public E3DOrientation getOrientation();
    
    /**
     * Scale the bounding object around its position
     * @param scaleAmt
     */
    public void scale(double scaleAmt);

    /**
     * Set the size of hte bounding object.  If a boundingsphere,
     * the radius will be set to .5(size).
     * If a boundingbox, width, height, and depth will be size.
     * 
     * @param size  Total size of the bounding object
     */
    public void setSize(double size);
    
    /**
     * Get the size of the object.  This will only be completely accurate for spheres.
     * @return
     */
    public double getSize();
    
    public void render();
}

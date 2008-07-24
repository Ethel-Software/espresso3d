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
package espresso3d.engine.collision.base;

import espresso3d.engine.collision.E3DCollision;
import espresso3d.engine.collision.bounding.IE3DBoundingObject;
import espresso3d.engine.lowlevel.geometry.E3DTriangle;
import espresso3d.engine.lowlevel.vector.E3DVector3F;
/**
 * @author espresso3d
 * 
 * The base interface from which to derive a custom collisiondetector.  A collision
 * detector is a class that actually determines if two bodies collide and, if they do,
 * returns an E3DCollision object containing the collision information.
 * 
 * Several collision detectors are already implemented and available from the engine's 
 * collisionDetect object.
 */
public interface IE3DCollisionDetector 
{
    public IE3DBoundingObject getBoundingObject();
    public void setBoundingObject(IE3DBoundingObject boundingObject);
    
    /**
	 * Interface to check the collision between sourceObject and triangle.
	 * @param sourceObject The object that is moving that we are looking for collisions from
	 * @param startPos SourceObject's starting position
	 * @param endPos SourceObject's ending position
	 * @param world  The world this collision is being checked for in
	 * @param sector The sector this collision is being checked for in
	 * @param triangle The actual triangle we are checking for collision into by sourceObject
	 * @param possibleCollisionObject The object that the triangle is from that we are checking if source object will run into
	 * @return
	 *  An E3DCollision object containing collision information if a collision occurs, or null otherwise.
	 */
//	public E3DCollision checkCollisionDetectableObjectTriangleCollision(IE3DCollisionDetectableObject sourceObject, E3DVector3F startPos, E3DVector3F endPos, 
//									  E3DWorld world, E3DSector sector, E3DTriangle triangle, IE3DCollisionDetectableObject possibleCollisionObject); //possibleCollisionActor can be null, and will be used to indicate we are checking against an actor0


    
//    public E3DCollision checkCollisionDetectableObjectBoundingObjectCollision(IE3DCollisionDetectableObject sourceObject, E3DVector3F startPos, E3DVector3F endPos, 
//			  E3DWorld world, E3DSector sector, IE3DBoundingObject boundingObject,  IE3DCollisionDetectableObject possibleCollisionObject); //possibleCollisionActor can be null, and will be used to indicate we are checking against an actor0	
    

//    public E3DVector3F checkForCollisionWithLine(ArrayList lineList);    
    
    public E3DCollision checkForCollisionWithBoundingObject(IE3DCollisionDetectableObject sourceObject, E3DVector3F sourceStartPosInDestCoords, E3DVector3F sourceEndPosInDestCoords, IE3DBoundingObject boundingObject);
    
    public E3DCollision checkForCollisionWithTriangle(IE3DCollisionDetectableObject sourceObject, E3DVector3F sourceStartPosInDestCoords, E3DVector3F sourceEndPosInDestCoords, E3DTriangle worldTriangle);
    
//    public E3DVector3F checkForCollisionWithTriangle(ArrayList triangleList, E3DVector3F sourceStartPos, E3DVector3F sourceEndPos);
}

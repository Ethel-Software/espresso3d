/*
 * Created on Oct 23, 2004
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
package espresso3d.engine.collision.detectors;
import java.util.ArrayList;

import espresso3d.engine.E3DEngine;
import espresso3d.engine.collision.E3DCollision;
import espresso3d.engine.collision.E3DCollisionHandler;
import espresso3d.engine.collision.base.IE3DCollisionDetectableObject;
import espresso3d.engine.collision.base.IE3DCollisionDetector;
import espresso3d.engine.collision.bounding.IE3DBoundingObject;
import espresso3d.engine.collision.bounding.E3DBoundingSphere;
import espresso3d.engine.lowlevel.geometry.E3DTriangle;
import espresso3d.engine.lowlevel.vector.E3DVector3F;

/**
 * 
 * @author Curt
 *
 * A fast bounding sphere collision detector.  
 *
 * Fast bounding spheres take less calculation and will hit all cases except that 
 * when an object is sort of parallel to the direction the sphere is moving.
 * IE: It works with head on collisions where the direction vector goes straight into
 * a triangle or boundingobject, but not if it runs alongside (but the boundingsphere still intersects to the left/right)
 * 
 * This collision detector will use sourceObject's bounding sphere object to 
 * check for collisions against other triangles or bounding objects.
 * 
 */
public class E3DCollisionDetectorBoundingSphere extends E3DCollisionDetectorSegment implements IE3DCollisionDetector 
{
    public E3DCollisionDetectorBoundingSphere(E3DEngine engine)
    {
        super(engine);
    }
    
   public E3DCollision checkForCollisionWithTriangle(
            IE3DCollisionDetectableObject sourceObject,
            E3DVector3F startPos, E3DVector3F endPos,
            E3DTriangle triangle) {
        return checkFastSphereVsTriangle(sourceObject, startPos, endPos, triangle);
    }
    
    public E3DCollision checkForCollisionWithBoundingObject(
            IE3DCollisionDetectableObject sourceObject,
            E3DVector3F startPos, E3DVector3F endPos,
            IE3DBoundingObject boundingObject) {
        return checkSphereVsBoundingObject(sourceObject, startPos, endPos, boundingObject);
    }	
	
	public E3DCollision checkFastSphereVsTriangle(IE3DCollisionDetectableObject sourceObject, E3DVector3F startPos,
			E3DVector3F endPos, E3DTriangle triangle)
	{
		E3DBoundingSphere sphere = null;

        //It will always be this if it has this sort of detector!
		sphere = (E3DBoundingSphere)sourceObject.getBoundingObject();
		
		E3DVector3F frontOfRadiusVec = endPos.subtract(startPos); //this is endPos vec + (unit(directionVec)*radius)
		frontOfRadiusVec.normaliseEqual();
		frontOfRadiusVec.scaleEqual(sphere.getRadius());
		frontOfRadiusVec.addEqual(endPos);
		
		//TODO: Testing, show the movement line
//		sourceActor.getEngine().addExternalRenderable(new E3DLine(sourceActor.getEngine(), startPos, frontOfRadiusVec, new E3DVector3F(0.0, 1.0, 0.0), new E3DVector3F(1.0, 0.0, 0.0)), 10);
		
		return super.checkForCollisionWithTriangle(sourceObject, startPos, frontOfRadiusVec, triangle);
	}

    public E3DCollision checkSphereVsBoundingObject(IE3DCollisionDetectableObject sourceObject, E3DVector3F startPos,
            E3DVector3F endPos, IE3DBoundingObject boundingObject) 
    {
        E3DBoundingSphere sphere = null;
         
        //will always be a sphere i we have this detector
        sphere = (E3DBoundingSphere)sourceObject.getBoundingObject();

        E3DVector3F direction = endPos.subtract(startPos);
        direction.normaliseEqual();
        direction.scale(sphere.getRadius());

        ArrayList collisions = new ArrayList();
        //This will miss glancing blows completely
        collisions.add(boundingObject.checkLineIntersectionCollision(startPos, endPos.add(direction)));

        //Create two more segments that goes from sphere origin (start and end) towards the other object origin that is sphere radius
        direction = boundingObject.getParentObject().getOrientation().getPosition().subtract(startPos);
        direction.normaliseEqual();
        direction.scale(sphere.getRadius());
        collisions.add(boundingObject.checkLineIntersectionCollision(startPos, startPos.add(direction)));
        collisions.add(boundingObject.checkLineIntersectionCollision(endPos, endPos.add(direction)));
        
        return E3DCollisionHandler.getClosestCollisionToPoint(startPos, collisions);
    }       
}

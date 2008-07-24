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
package espresso3d.engine.collision.detectors;
import espresso3d.engine.E3DEngine;
import espresso3d.engine.base.E3DEngineItem;
import espresso3d.engine.collision.E3DCollision;
import espresso3d.engine.collision.base.IE3DCollisionDetectableObject;
import espresso3d.engine.collision.base.IE3DCollisionDetector;
import espresso3d.engine.collision.bounding.IE3DBoundingObject;
import espresso3d.engine.lowlevel.geometry.E3DTriangle;
import espresso3d.engine.lowlevel.vector.E3DVector3F;


/**
 * @author espresso3d
 *
 * A collision detector that will check the basic case of a segment colliding with (or crossing through)
 * another object such as a triangle, boundingObject, etc.
 */

public class E3DCollisionDetectorSegment extends E3DEngineItem implements IE3DCollisionDetector
{
    public E3DCollisionDetectorSegment(E3DEngine engine)
    {
        super(engine);
    }
    
    public E3DCollision checkForCollisionWithBoundingObject(IE3DCollisionDetectableObject sourceObject, E3DVector3F startPos, E3DVector3F endPos, IE3DBoundingObject boundingObject)
    {
        return boundingObject.checkLineIntersectionCollision(startPos, endPos);
    }
    
    public E3DCollision checkForCollisionWithTriangle(IE3DCollisionDetectableObject sourceObject, E3DVector3F startPos, E3DVector3F endPos, E3DTriangle triangle)
    {
        E3DVector3F intersectionPoint = triangle.checkSegmentCollision(startPos, endPos);
        if(intersectionPoint != null)
        {
            E3DCollision collision = new E3DCollision(triangle.getEngine());
            collision.setIntersectionPt(intersectionPoint);
/*            System.out.println("Intersection");
            System.out.println(triangle);
            System.out.println(startPos + ", " + endPos);
            System.out.println(intersectionPoint); */
            collision.setColliderCollisionNormal(triangle.getNormal());
            collision.setCollideeCollisionNormal(endPos.subtract(startPos).normalise()); //normal of a segment is the segment coming at it
            return collision;
        }
        else
            return null;
    }

    //This detector doesn't have a bounding object
    public IE3DBoundingObject getBoundingObject() {
        return null;
    }
    //This detector doesn't have a bounding object
    public void setBoundingObject(IE3DBoundingObject boundingObject) {
    }
}

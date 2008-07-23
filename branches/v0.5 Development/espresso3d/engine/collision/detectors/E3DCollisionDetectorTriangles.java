/*
 * Created on Aug 2, 2004
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
import espresso3d.engine.lowlevel.geometry.E3DTriangle;
import espresso3d.engine.lowlevel.vector.E3DVector3F;

/**
 * @author espresso3d
 *
 * An accurate triangle vs: other object collision detector.  Not extremely fast 
 */
public class E3DCollisionDetectorTriangles extends E3DCollisionDetectorSegment implements IE3DCollisionDetector 
{
    public E3DCollisionDetectorTriangles(E3DEngine engine)
    {
        super(engine);
    }

    public E3DCollision checkForCollisionWithTriangle(IE3DCollisionDetectableObject sourceObject, E3DVector3F sourceStartPos, E3DVector3F sourceEndPos,E3DTriangle triangle) {
        return checkCollisionDetectableObjectTrisVsTri(sourceObject, sourceStartPos, sourceEndPos, triangle);
    }
    
    public E3DCollision checkForCollisionWithBoundingObject(IE3DCollisionDetectableObject sourceObject, E3DVector3F sourceStartPos, E3DVector3F sourceEndPos, IE3DBoundingObject boundingObject) {
        return checkTrisVsBoundingObject(sourceObject, sourceStartPos, sourceEndPos, boundingObject);
    }


    public IE3DBoundingObject getBoundingObject() {
        return super.getBoundingObject();
    }
    
    public void setBoundingObject(IE3DBoundingObject boundingObject) {
        super.setBoundingObject(boundingObject);
    }
	
    //TODO: this really isn't good enough, some things could slip through
	public E3DCollision checkCollisionDetectableObjectTrisVsTri(IE3DCollisionDetectableObject sourceObject, E3DVector3F startPos,
	                                                    E3DVector3F endPos, E3DTriangle triangle) 
	{
		E3DCollision collision = null;
		E3DVector3F actVertex = null;
		ArrayList vertexList = null;
		ArrayList collisions = new ArrayList();
        
		vertexList = sourceObject.getUniqueVertexPositionList();
	
        E3DVector3F direction = endPos.subtract(startPos);
        
		for(int i=0; i<vertexList.size(); i++)
		{
			actVertex = (E3DVector3F)vertexList.get(i);

            //Convert the local vector to a world vector
            actVertex = sourceObject.getOrientation().getWorldVector(actVertex);
            
            //Do a segment check using the segment collision detector
			collision = super.checkForCollisionWithTriangle(sourceObject, actVertex, actVertex.add(direction), triangle);
			if(collision != null)
                collisions.add(collision);
		}
		
		return E3DCollisionHandler.getClosestCollisionToPoint(sourceObject.getOrientation().getPosition(), collisions);
	}

    public E3DCollision checkTrisVsBoundingObject(IE3DCollisionDetectableObject sourceObject, E3DVector3F startPos, E3DVector3F endPos, IE3DBoundingObject destBoundingObject) 
    {
        ArrayList collisions = new ArrayList();
        E3DTriangle triangle = null, scratch = new E3DTriangle(getEngine());
        E3DVector3F translationAmt = endPos.subtract(startPos);
        
        ArrayList triangleList = sourceObject.getTriangleList();
        
        E3DVector3F translatedA, translatedB, translatedC;
        
        for(int i=0; i < triangleList.size(); i++)
        {
            triangle = (E3DTriangle)triangleList.get(i);

            //Convert the triangle to world coords
            scratch.setVertexPosA(sourceObject.getOrientation().getWorldVector(triangle.getVertexPosA()));
            scratch.setVertexPosB(sourceObject.getOrientation().getWorldVector(triangle.getVertexPosB()));
            scratch.setVertexPosC(sourceObject.getOrientation().getWorldVector(triangle.getVertexPosC()));
            
            //For each triangle we need to do line segment checks for the edges of the triangle, the edges of the triangle + (startPos - endPos), and connecting lines between, in all, 12 checks per triangle!!

            //Do source triangle position
            //I'm assuming add won't add nulls??
            collisions.add(destBoundingObject.checkLineIntersectionCollision(scratch.getVertexPosA(), scratch.getVertexPosB()));
            collisions.add(destBoundingObject.checkLineIntersectionCollision(scratch.getVertexPosB(), scratch.getVertexPosC()));
            collisions.add(destBoundingObject.checkLineIntersectionCollision(scratch.getVertexPosC(), scratch.getVertexPosA()));
            
            translatedA = scratch.getVertexPosA().add(translationAmt);
            translatedB = scratch.getVertexPosB().add(translationAmt);
            translatedC = scratch.getVertexPosC().add(translationAmt);
            
            //Do dest triangle position
            collisions.add(destBoundingObject.checkLineIntersectionCollision(translatedA, translatedB));
            collisions.add(destBoundingObject.checkLineIntersectionCollision(translatedB, translatedC));
            collisions.add(destBoundingObject.checkLineIntersectionCollision(translatedC, translatedA));

            //Now do connecting at the corners
            collisions.add(destBoundingObject.checkLineIntersectionCollision(scratch.getVertexPosA(), translatedA));
            collisions.add(destBoundingObject.checkLineIntersectionCollision(scratch.getVertexPosB(), translatedB));
            collisions.add(destBoundingObject.checkLineIntersectionCollision(scratch.getVertexPosC(), translatedC));

            //TODO: If necessary, we could do some more connecting lines between vertices
        }
        
        return E3DCollisionHandler.getClosestCollisionToPoint(sourceObject.getOrientation().getPosition(), collisions);
    }
}

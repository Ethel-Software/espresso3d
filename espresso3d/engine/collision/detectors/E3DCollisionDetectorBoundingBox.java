/*
 * Created on Mar 7, 2005
 *
 
   	Copyright 2005 Curtis Moxley
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
import espresso3d.engine.collision.bounding.E3DBoundingBox;
import espresso3d.engine.collision.bounding.IE3DBoundingObject;
import espresso3d.engine.lowlevel.geometry.E3DLine;
import espresso3d.engine.lowlevel.geometry.E3DTriangle;
import espresso3d.engine.lowlevel.vector.E3DVector3F;

/**
 * @author Curt
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class E3DCollisionDetectorBoundingBox extends E3DCollisionDetectorSegment implements IE3DCollisionDetector 
{
    public E3DCollisionDetectorBoundingBox(E3DEngine engine)
    {
        super(engine);
    }
    
    public E3DCollision checkForCollisionWithTriangle(
            IE3DCollisionDetectableObject sourceObject,
            E3DVector3F sourceStartPos, E3DVector3F sourceEndPos,
            E3DTriangle triangle) {
        return checkBoxVsTriangle(sourceObject, sourceStartPos, sourceEndPos, triangle);
    }
    
    public E3DCollision checkForCollisionWithBoundingObject(
            IE3DCollisionDetectableObject sourceObject,
            E3DVector3F sourceStartPos, E3DVector3F sourceEndPos,
            IE3DBoundingObject boundingObject) {
        return checkBoxVsBoundingObject(sourceObject, sourceStartPos, sourceEndPos, boundingObject);
    }   
    
    public E3DCollision checkBoxVsTriangle(IE3DCollisionDetectableObject sourceObject, E3DVector3F startPos,
            E3DVector3F endPos, E3DTriangle triangle) 
    {
        ArrayList collisions = new ArrayList();
        
        //Always known
        E3DBoundingBox sourceBox = (E3DBoundingBox)sourceObject.getBoundingObject();

        E3DVector3F frontA, frontB, frontC, frontD, backA, backB, backC, backD;
        E3DVector3F transFrontA, transFrontB, transFrontC, transFrontD, transBackA, transBackB, transBackC, transBackD;
        E3DVector3F translationAmt = endPos.subtract(startPos);
        
        frontA = sourceBox.getOrientation().getWorldVector(sourceBox.getQuads()[0].getVertexPosA());
        frontB = sourceBox.getOrientation().getWorldVector(sourceBox.getQuads()[0].getVertexPosB());
        frontC = sourceBox.getOrientation().getWorldVector(sourceBox.getQuads()[0].getVertexPosC());
        frontD = sourceBox.getOrientation().getWorldVector(sourceBox.getQuads()[0].getVertexPosD());
        
        backA = sourceBox.getOrientation().getWorldVector(sourceBox.getQuads()[1].getVertexPosA());
        backB = sourceBox.getOrientation().getWorldVector(sourceBox.getQuads()[1].getVertexPosB());
        backC = sourceBox.getOrientation().getWorldVector(sourceBox.getQuads()[1].getVertexPosC());
        backD = sourceBox.getOrientation().getWorldVector(sourceBox.getQuads()[1].getVertexPosD());
        
        transFrontA = frontA.add(translationAmt);
        transFrontB = frontB.add(translationAmt);
        transFrontC = frontC.add(translationAmt);
        transFrontD = frontC.add(translationAmt);
        
        transBackA = backA.add(translationAmt);
        transBackB = backB.add(translationAmt);
        transBackC = backC.add(translationAmt);
        transBackD = backD.add(translationAmt);
        
        //Get the 3 segments

        //Check from front of original to back of translated (essentially builds a big square
        collisions.add(super.checkForCollisionWithTriangle(sourceObject, frontA, transBackA, triangle));
        collisions.add(super.checkForCollisionWithTriangle(sourceObject, frontB, transBackB, triangle));
        collisions.add(super.checkForCollisionWithTriangle(sourceObject, frontC, transBackC, triangle));
        collisions.add(super.checkForCollisionWithTriangle(sourceObject, frontD, transBackD, triangle));

        //Do others
        collisions.add(super.checkForCollisionWithTriangle(sourceObject, backA, transFrontA, triangle));
        collisions.add(super.checkForCollisionWithTriangle(sourceObject, backB, transFrontB, triangle));
        collisions.add(super.checkForCollisionWithTriangle(sourceObject, backC, transFrontC, triangle));
        collisions.add(super.checkForCollisionWithTriangle(sourceObject, backD, transFrontD, triangle));
        
        //TODO: this needs some more help, this won't cut it all!
        collisions.add(super.checkForCollisionWithTriangle(sourceObject, startPos, endPos, triangle));
        
        return E3DCollisionHandler.getClosestCollisionToPoint(sourceObject.getOrientation().getPosition(), collisions);
    }

    public E3DCollision checkBoxVsBoundingObject(IE3DCollisionDetectableObject sourceObject, E3DVector3F startPos,
            E3DVector3F endPos, IE3DBoundingObject boundingObject) 
    {
        ArrayList collisions = new ArrayList();
        
        //Always known
        E3DBoundingBox sourceBox = (E3DBoundingBox)sourceObject.getBoundingObject();

        E3DVector3F frontA, frontB, frontC, frontD, backA, backB, backC, backD;
        E3DVector3F transFrontA, transFrontB, transFrontC, transFrontD, transBackA, transBackB, transBackC, transBackD;
        E3DVector3F translationAmt = endPos.subtract(startPos);
        
        frontA = sourceBox.getOrientation().getWorldVector(sourceBox.getQuads()[0].getVertexPosA());
        frontB = sourceBox.getOrientation().getWorldVector(sourceBox.getQuads()[0].getVertexPosB());
        frontC = sourceBox.getOrientation().getWorldVector(sourceBox.getQuads()[0].getVertexPosC());
        frontD = sourceBox.getOrientation().getWorldVector(sourceBox.getQuads()[0].getVertexPosD());
        
        backA = sourceBox.getOrientation().getWorldVector(sourceBox.getQuads()[1].getVertexPosA());
        backB = sourceBox.getOrientation().getWorldVector(sourceBox.getQuads()[1].getVertexPosB());
        backC = sourceBox.getOrientation().getWorldVector(sourceBox.getQuads()[1].getVertexPosC());
        backD = sourceBox.getOrientation().getWorldVector(sourceBox.getQuads()[1].getVertexPosD());
        
        transFrontA = frontA.add(translationAmt);
        transFrontB = frontB.add(translationAmt);
        transFrontC = frontC.add(translationAmt);
        transFrontD = frontD.add(translationAmt);
        
        transBackA = backA.add(translationAmt);
        transBackB = backB.add(translationAmt);
        transBackC = backC.add(translationAmt);
        transBackD = backD.add(translationAmt);
        
        //Get the 3 segments

        //Check from front of original to back of translated (essentially builds a big square
        collisions.add(boundingObject.checkLineIntersectionCollision(frontA, transBackA));
        collisions.add(boundingObject.checkLineIntersectionCollision(frontB, transBackB));
        collisions.add(boundingObject.checkLineIntersectionCollision(frontC, transBackC));
        collisions.add(boundingObject.checkLineIntersectionCollision(frontD, transBackD));

        collisions.add(boundingObject.checkLineIntersectionCollision(frontA, transBackC));
        collisions.add(boundingObject.checkLineIntersectionCollision(frontB, transBackD));
        collisions.add(boundingObject.checkLineIntersectionCollision(frontC, transBackA));
        collisions.add(boundingObject.checkLineIntersectionCollision(frontD, transBackB));
        
        //Do others
        collisions.add(boundingObject.checkLineIntersectionCollision(backA, transFrontA));
        collisions.add(boundingObject.checkLineIntersectionCollision(backB, transFrontB));
        collisions.add(boundingObject.checkLineIntersectionCollision(backC, transFrontC));
        collisions.add(boundingObject.checkLineIntersectionCollision(backD, transFrontD));

        collisions.add(boundingObject.checkLineIntersectionCollision(backA, transFrontC));
        collisions.add(boundingObject.checkLineIntersectionCollision(backB, transFrontD));
        collisions.add(boundingObject.checkLineIntersectionCollision(backC, transFrontA));
        collisions.add(boundingObject.checkLineIntersectionCollision(backD, transFrontB));

        //DEBUGGING
        E3DLine line = new E3DLine(getEngine(), frontA, transBackA, new E3DVector3F(1.0, 0.0, 0.0), new E3DVector3F(1.0, 1.0, 0.0));
        getEngine().addExternalRenderable(line, 2);
        line = new E3DLine(getEngine(), frontB, transBackB, new E3DVector3F(1.0, 0.0, 0.0), new E3DVector3F(1.0, 1.0, 0.0));
        getEngine().addExternalRenderable(line, 2);
        line = new E3DLine(getEngine(), frontC, transBackC, new E3DVector3F(1.0, 0.0, 0.0), new E3DVector3F(1.0, 1.0, 0.0));
        getEngine().addExternalRenderable(line, 2);
        line = new E3DLine(getEngine(), frontD, transBackD, new E3DVector3F(1.0, 0.0, 0.0), new E3DVector3F(1.0, 1.0, 0.0));
        getEngine().addExternalRenderable(line, 2);
        line = new E3DLine(getEngine(), backA, transFrontA, new E3DVector3F(1.0, 0.0, 0.0), new E3DVector3F(1.0, 1.0, 0.0));
        getEngine().addExternalRenderable(line, 2);
        line = new E3DLine(getEngine(), backB, transFrontB, new E3DVector3F(1.0, 0.0, 0.0), new E3DVector3F(1.0, 1.0, 0.0));
        getEngine().addExternalRenderable(line, 2);
        line = new E3DLine(getEngine(), backC, transFrontC, new E3DVector3F(1.0, 0.0, 0.0), new E3DVector3F(1.0, 1.0, 0.0));
        getEngine().addExternalRenderable(line, 2);
        line = new E3DLine(getEngine(), backD, transFrontD, new E3DVector3F(1.0, 0.0, 0.0), new E3DVector3F(1.0, 1.0, 0.0));
        getEngine().addExternalRenderable(line, 2);

        line = new E3DLine(getEngine(), frontA, transBackC, new E3DVector3F(1.0, 0.0, 0.0), new E3DVector3F(1.0, 1.0, 0.0));
        getEngine().addExternalRenderable(line, 2);
        line = new E3DLine(getEngine(), frontB, transBackD, new E3DVector3F(1.0, 0.0, 0.0), new E3DVector3F(1.0, 1.0, 0.0));
        getEngine().addExternalRenderable(line, 2);
        line = new E3DLine(getEngine(), frontC, transBackA, new E3DVector3F(1.0, 0.0, 0.0), new E3DVector3F(1.0, 1.0, 0.0));
        getEngine().addExternalRenderable(line, 2);
        line = new E3DLine(getEngine(), frontD, transBackB, new E3DVector3F(1.0, 0.0, 0.0), new E3DVector3F(1.0, 1.0, 0.0));
        getEngine().addExternalRenderable(line, 2);        
        
        line = new E3DLine(getEngine(), backA, transFrontC, new E3DVector3F(1.0, 0.0, 0.0), new E3DVector3F(1.0, 1.0, 0.0));
        getEngine().addExternalRenderable(line, 2);
        line = new E3DLine(getEngine(), backB, transFrontD, new E3DVector3F(1.0, 0.0, 0.0), new E3DVector3F(1.0, 1.0, 0.0));
        getEngine().addExternalRenderable(line, 2);
        line = new E3DLine(getEngine(), backC, transFrontA, new E3DVector3F(1.0, 0.0, 0.0), new E3DVector3F(1.0, 1.0, 0.0));
        getEngine().addExternalRenderable(line, 2);
        line = new E3DLine(getEngine(), backD, transFrontB, new E3DVector3F(1.0, 0.0, 0.0), new E3DVector3F(1.0, 1.0, 0.0));
        getEngine().addExternalRenderable(line, 2);
        
        //TODO: this needs some more help, this won't cut it all!
        collisions.add(boundingObject.checkLineIntersectionCollision(startPos, endPos));
        line = new E3DLine(getEngine(), startPos, endPos, new E3DVector3F(0.0, 1.0, 0.0), new E3DVector3F(1.0, 1.0, 0.0));
        getEngine().addExternalRenderable(line, 2);
        
        return E3DCollisionHandler.getClosestCollisionToPoint(sourceObject.getOrientation().getPosition(), collisions);
    }
    
    
}

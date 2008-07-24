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
package espresso3d.engine.collision.bounding;

import java.util.ArrayList;

import org.lwjgl.opengl.GL11;

import espresso3d.engine.E3DEngine;
import espresso3d.engine.collision.E3DCollision;
import espresso3d.engine.collision.E3DCollisionHandler;
import espresso3d.engine.collision.base.IE3DCollisionDetectableObject;
import espresso3d.engine.collision.base.IE3DCollisionDetector;
import espresso3d.engine.collision.detectors.E3DCollisionDetectorBoundingBox;
import espresso3d.engine.lowlevel.geometry.E3DQuad;
import espresso3d.engine.lowlevel.matrix.E3DOrientation;
import espresso3d.engine.lowlevel.vector.E3DVector2F;
import espresso3d.engine.lowlevel.vector.E3DVector3F;
import espresso3d.engine.lowlevel.vector.E3DVector4F;
import espresso3d.engine.renderer.base.E3DRenderMode;
import espresso3d.engine.renderer.base.E3DRenderable;
/**
 * @author espresso3d
 *
 * A bounding box bounding object.  Bounding boxes are currently unsupported bounding objects
 */
public class E3DBoundingBox extends E3DRenderable implements IE3DBoundingObject {

    private IE3DCollisionDetectableObject parentObject;
    private E3DOrientation orientation;
    
	private E3DVector3F min;
	private E3DVector3F max;
    
    private E3DQuad[] quads = new E3DQuad[6];
	
    private E3DCollisionDetectorBoundingBox collisionDetector;
    
	public E3DBoundingBox(E3DEngine engine)
	{
		this(engine, new E3DVector3F(), new E3DVector3F());
	}
	
	public E3DBoundingBox(E3DEngine engine, E3DVector3F min, E3DVector3F max)
	{
		super(engine);
		this.min = min;
        this.max = max;
        this.collisionDetector = new E3DCollisionDetectorBoundingBox(engine);
        this.orientation = new E3DOrientation(engine);
        
        rebuildQuads();
	}

	public E3DBoundingBox(E3DEngine engine, double minX, double minY, double minZ,
						 double maxX, double maxY, double maxZ)
	{
		super(engine);
		min = new E3DVector3F(minX, minY, minZ);
		max = new E3DVector3F(maxX, maxY, maxZ);
        this.collisionDetector = new E3DCollisionDetectorBoundingBox(engine);
        this.orientation = new E3DOrientation(engine);

        rebuildQuads();
	}
	
    public E3DBoundingBox(E3DBoundingBox toCopyBoundingBox)
    {
        super(toCopyBoundingBox.getEngine());
        min = new E3DVector3F(toCopyBoundingBox.getMin());
        max = new E3DVector3F(toCopyBoundingBox.getMax());
        this.orientation = new E3DOrientation(toCopyBoundingBox.getOrientation());
        this.collisionDetector = new E3DCollisionDetectorBoundingBox(toCopyBoundingBox.getEngine());

        rebuildQuads();
    }
    
	/* (non-Javadoc)
	 * @see espresso3d.engine.base.E3DRenderable#renderAtPosition(espresso3d.engine.lowlevel.E3DVector3F)
	 */
	public void render() 
	{
        ArrayList quadList = new ArrayList();
        for(int i=0; i<quads.length; i++)
            quadList.add(quads[i]);

        GL11.glPushMatrix();
            GL11.glMultMatrix(getParentObject().getOrientation().getFloatBuffer());
            getEngine().getGeometryRenderer().initSolidAndLineRendering();
            getEngine().getGeometryRenderer().renderQuadList(getEngine(), quadList, E3DRenderMode.RENDERMODE_WIREFRAME);
       GL11.glPopMatrix();
	}
	
    //TODO: fixme
    public E3DCollision checkLineIntersectionCollision(E3DVector3F startPos, E3DVector3F endPos) 
    {
        ArrayList collisions = new ArrayList();
        E3DVector3F intersectionPt;
        
        //Convert the coordinates to local coordinates
        E3DVector3F localStartPos = orientation.getLocalVector(startPos),
                    localEndPos = orientation.getLocalVector(endPos);
        
        for(int a=0; a <quads.length; a++)
        {
            //If we have a collision, hold onto it until we can find the closest on
            intersectionPt = quads[a].checkSegmentCollision(localStartPos, localEndPos);
            if(intersectionPt != null)
            {
                E3DCollision collision = new E3DCollision(getEngine());
                collision.setIntersectionPt(orientation.getWorldVector(intersectionPt)); //convert this back to a world coord
                collision.setColliderCollisionNormal(orientation.getWorldVector(quads[a].getNormal()).normalise()); //convert back to world coods and normalise
                collision.setCollideeCollisionNormal(orientation.getWorldVector(quads[a].getNormal().scale(-1)).normalise()); //convert back to world coords, reverse, and normalise
                collisions.add(collision);
            }
        }

        return E3DCollisionHandler.getClosestCollisionToPoint(startPos, collisions);
    }

    public IE3DCollisionDetector getCollisionDetector() {
       return collisionDetector;
    }

	/**
	 * @return Returns the max.
	 */
	public E3DVector3F getMax() {
		return max;
	}
	/**
	 * @param max The max to set.
	 */
	public void setMax(E3DVector3F max) {
		this.max = max;
	}
	/**
	 * @return Returns the min.
	 */
	public E3DVector3F getMin() {
		return min;
	}
	/**
	 * @param min The min to set.
	 */
	public void setMin(E3DVector3F min) {
		this.min = min;
	}
    
    
    private void rebuildQuads()
    {
        //The 8 vertices of the quad
        E3DVector3F frontA = new E3DVector3F(min.getX(), min.getY(), min.getZ());
        E3DVector3F frontB = new E3DVector3F(min.getX(), max.getY(), min.getZ());
        E3DVector3F frontC = new E3DVector3F(max.getX(), max.getY(), min.getZ());
        E3DVector3F frontD = new E3DVector3F(max.getX(), min.getY(), min.getZ());
        E3DVector3F backA = new E3DVector3F(min.getX(), min.getY(), max.getZ());
        E3DVector3F backB = new E3DVector3F(min.getX(), max.getY(), max.getZ());
        E3DVector3F backC = new E3DVector3F(max.getX(), max.getY(), max.getZ());
        E3DVector3F backD = new E3DVector3F(max.getX(), min.getY(), max.getZ());
        
        E3DVector4F color = new E3DVector4F(0, 0, 1, 1.0);
        E3DVector2F tcA = new E3DVector2F(1, 0);
        E3DVector2F tcB = new E3DVector2F(1, 1);
        E3DVector2F tcC = new E3DVector2F(0, 1);
        E3DVector2F tcD = new E3DVector2F(0, 0);
        
        //Front face (in a sense)
        quads[0] = new E3DQuad(getEngine(), frontD, frontC, frontB, frontA, 
                               tcA, tcB, tcC, tcD, 
                                color, 
                                color,
                                color, 
                                color, null);

        //Back face
        quads[1] = new E3DQuad(getEngine(), backD, backC, backB, backA, 
                tcA, tcB, tcC, tcD, 
                color, 
                color,
                color, 
                color, null);
        
        //left side
        quads[2] = new E3DQuad(getEngine(), frontA, frontB, backB, backA, 
                tcA, tcB, tcC, tcD, 
                color, 
                color,
                color, 
                color, null);
        
        //right side
        quads[3] = new E3DQuad(getEngine(), backD, backC, frontC, frontD, 
                tcA, tcB, tcC, tcD, 
                color, 
                color,
                color, 
                color, null);
        
        //top
        quads[4] = new E3DQuad(getEngine(), frontC, backC, backB, frontB, 
                tcA, tcB, tcC, tcD, 
                color, 
                color,
                color, 
                color, null);
        
        //bottom
        quads[5] = new E3DQuad(getEngine(), backD, frontD, frontA, backA, 
                tcA, tcB, tcC, tcD, 
                color, 
                color,
                color, 
                color, null);
    }
    
    /**
     * Return the 6 faces of the bounding box as quads
     * @return
     */
    public E3DQuad[] getQuads(){
        return quads;
    }
    
    public IE3DBoundingObject onGetClone() {
        return new E3DBoundingBox(this);
    }    
    public IE3DCollisionDetectableObject getParentObject() {
        return parentObject;
    }
    public void setParentObject(IE3DCollisionDetectableObject parentObject) {
        this.parentObject = parentObject;
    }
    
    public void centerAroundParentObject() 
    {
        if(parentObject != null)
            orientation.setPosition(parentObject.getOrientation().getPosition());
    }
    
    public void scale(double scaleAmt) 
    {
        min.scale(scaleAmt);
        max.scale(scaleAmt);
        //This covers all vertices in all faces (since shared)
        quads[0].scale(scaleAmt);
        quads[1].scale(scaleAmt);
    }
    
    public void setSize(double size) 
    {
        double minLength = min.getLength();
        double maxLength = max.getLength(); //add thse two to get total size
        double totalSize = minLength + maxLength;
        double minPercentOfTotal = minLength / totalSize;
        double maxPercentOfTotal = maxLength / totalSize;
        
        E3DVector3F direction = max.subtract(min);
        direction.normaliseEqual();
        
        //Max is the normalised direction * the percentage of the size it was before * the new size
        max = direction.scale(maxPercentOfTotal * size);

        //Min is the reverse of the normalised direction * the percentage of the size it was before * the new size
        min = direction.scale(-1).scale(minPercentOfTotal * size);

        //Do a rebuild instead of recalcing everything.
        rebuildQuads();
    }

    public double getSize()
    {
        double minLength = min.getLength();
        double maxLength = max.getLength();
        return minLength + maxLength;
    }
    
    public E3DOrientation getOrientation() {
        return orientation;
    }
}

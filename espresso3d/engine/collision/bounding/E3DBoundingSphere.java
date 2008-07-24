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
import java.util.ArrayList;

import org.lwjgl.opengl.GL11;

import espresso3d.engine.E3DEngine;
import espresso3d.engine.collision.E3DCollision;
import espresso3d.engine.collision.base.IE3DCollisionDetectableObject;
import espresso3d.engine.collision.base.IE3DCollisionDetector;
import espresso3d.engine.collision.detectors.E3DCollisionDetectorBoundingSphere;
import espresso3d.engine.lowlevel.geometry.E3DLine;
import espresso3d.engine.lowlevel.matrix.E3DOrientation;
import espresso3d.engine.lowlevel.vector.E3DVector3F;
import espresso3d.engine.renderer.base.E3DRenderable;
/**
 * @author Curt
 *
 * A bounding sphere bounding object
 */
public class E3DBoundingSphere extends E3DRenderable implements IE3DBoundingObject
{
    private IE3DCollisionDetectableObject parentObject;
    
	private double radius;
	private int boundingSphereType;
    private E3DCollisionDetectorBoundingSphere collisionDetector;

    private E3DOrientation orientation;
    
	public E3DBoundingSphere(E3DEngine engine, double radius)
	{
		super(engine);
		setRadius(radius);
        this.collisionDetector = new E3DCollisionDetectorBoundingSphere(engine);
        this.orientation = new E3DOrientation(engine);
	}
	
    public E3DBoundingSphere(E3DBoundingSphere toCopyBoundingSphere)
    {
        super(toCopyBoundingSphere.getEngine());
        setRadius(toCopyBoundingSphere.getRadius());
        this.collisionDetector = new E3DCollisionDetectorBoundingSphere(toCopyBoundingSphere.getEngine());
        this.orientation = new E3DOrientation(toCopyBoundingSphere.getOrientation());
    }
    
	public void render() {
        ArrayList lineList = new ArrayList();

        GL11.glPushMatrix();
            GL11.glMultMatrix(orientation.getFloatBuffer());
            getEngine().getGeometryRenderer().initSolidAndLineRendering();
    
            //Shoot out lines from the origin in all 3 axis directions
            lineList.add(new E3DLine(getEngine(), new E3DVector3F(0.0, 0.0, 0.0), orientation.getForward().scale(radius), new E3DVector3F(1.0, 0.0, 0.0), new E3DVector3F(0.0, 1.0, 0.0)));
            lineList.add(new E3DLine(getEngine(), new E3DVector3F(0.0, 0.0, 0.0), orientation.getForward().scale(-radius), new E3DVector3F(1.0, 0.0, 0.0), new E3DVector3F(0.0, 1.0, 0.0)));
            lineList.add(new E3DLine(getEngine(), new E3DVector3F(0.0, 0.0, 0.0), orientation.getUp().scale(radius), new E3DVector3F(1.0, 0.0, 0.0), new E3DVector3F(0.0, 1.0, 0.0)));
            lineList.add(new E3DLine(getEngine(), new E3DVector3F(0.0, 0.0, 0.0), orientation.getUp().scale(-radius), new E3DVector3F(1.0, 0.0, 0.0), new E3DVector3F(0.0, 1.0, 0.0)));
            lineList.add(new E3DLine(getEngine(), new E3DVector3F(0.0, 0.0, 0.0), orientation.getLeft().scale(radius), new E3DVector3F(1.0, 0.0, 0.0), new E3DVector3F(0.0, 1.0, 0.0)));
            lineList.add(new E3DLine(getEngine(), new E3DVector3F(0.0, 0.0, 0.0), orientation.getLeft().scale(-radius), new E3DVector3F(1.0, 0.0, 0.0), new E3DVector3F(0.0, 1.0, 0.0)));
            
            getEngine().getGeometryRenderer().renderLineList(lineList);
        GL11.glPopMatrix();
    }

	/**
	 * @return Returns the radius.
	 */
	public double getRadius() {
		return radius;
	}
	/**
	 * @param radius The radius to set.
	 */
	public void setRadius(double radius) {
		this.radius = radius;
	}
    
    public E3DCollision checkLineIntersectionCollision(E3DVector3F startPos, E3DVector3F endPos) 
    {
        if(parentObject == null)
            return null;
     
        E3DVector3F localStartPos = orientation.getLocalVector(startPos), 
                    localEndPos = orientation.getLocalVector(endPos);
        
        E3DLine line = new E3DLine(getEngine(), localStartPos, localEndPos);
        
        E3DVector3F intersectionPt = null;
        double distance = 0.0;
        
        //Get the closest point on htel ine between start and endPos and if the length of the line formed by that point
        // to the center of the bounding sphere is less than the radius, then that closest point hit the sphere
        E3DVector3F closestPoint = line.getClosestPointToPoint(new E3DVector3F(0,0,0)); //assuming local space

//        distance = closestPoint.subtract(parentObject.getOrientation().getPosition()).getLengthSquared();
        distance = closestPoint.getLengthSquared();
        if(distance <= radius) //if its closer then the radius, then it did collide
            intersectionPt = closestPoint;
        
        if(intersectionPt != null)
        {
            E3DCollision collision = new E3DCollision(parentObject.getEngine());
            intersectionPt = orientation.getWorldVector(intersectionPt);
            collision.setIntersectionPt(intersectionPt);
            collision.setColliderCollisionNormal(intersectionPt.subtract(orientation.getPosition()).normalise());
            collision.setCollideeCollisionNormal(orientation.getPosition().subtract(intersectionPt).normalise());
            
//            collision.setColliderCollisionNormal(intersectionPt.normalise());
//            collision.setCollideeCollisionNormal(intersectionPt.scale(-1).normalise());
            return collision;
        }
        else
            return null;
    }

    public void centerAroundParentObject() 
    {
        if(parentObject != null)
            orientation.setPosition(parentObject.getOrientation().getPosition());
    }
    
    public IE3DBoundingObject onGetClone() {
        return new E3DBoundingSphere(this);
    }    
    public int getBoundingSphereType() {
        return boundingSphereType;
    }
    public void setBoundingSphereType(int boundingSphereType) {
        this.boundingSphereType = boundingSphereType;
    }

    public IE3DCollisionDetector getCollisionDetector() {
        return collisionDetector;
    }
    
    public IE3DCollisionDetectableObject getParentObject() {
        return parentObject;
    }
    public void setParentObject(IE3DCollisionDetectableObject parentObject) {
        this.parentObject = parentObject;
    }
    
    public void scale(double scaleAmt) {
        radius *= scaleAmt;
    }
    
    public void setSize(double size) {
        radius = size/2.0;
    }
    
    /**
     * Size of this is 2x the radius
     */
    public double getSize() {
        return radius * 2;
    }
    
    public E3DOrientation getOrientation() {
        return orientation;
    }
}

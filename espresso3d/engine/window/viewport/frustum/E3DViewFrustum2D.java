/*
 * Created on Nov 4, 2004
 *
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
package espresso3d.engine.window.viewport.frustum;


import espresso3d.engine.E3DEngine;
import espresso3d.engine.base.E3DEngineItem;
import espresso3d.engine.lowlevel.geometry.E3DTriangle;
import espresso3d.engine.lowlevel.vector.E3DVector2F;
import espresso3d.engine.lowlevel.vector.E3DVector3F;
import espresso3d.engine.window.viewport.E3DViewport;
import espresso3d.engine.world.sector.portal.E3DPortal;

/**
 * @author Curt
 *
 * This is a viewing frustum that will perform 2D frustum checks (Unproject 3D coordinates to 2D and check within bounds of that)
 * This normally is not used by the user (its used by the engine), but provided in case it is found to be useful.
 */
public class E3DViewFrustum2D extends E3DEngineItem 
{
	private E3DViewport viewport; //The current viewport being used with this frustum
	
    private E3DVector2F a, b, c, d;
//    private boolean needRecalc = true;
//    private float[][] modelViewArray;
//    private float[][] projViewArray;
//    private int[] intViewArray;
    private E3DTriangle triOne;
    private E3DTriangle triTwo;
    
    public E3DViewFrustum2D(E3DEngine engine, E3DViewport viewport, E3DVector2F minMin, E3DVector2F minMax, E3DVector2F maxMax, E3DVector2F maxMin)
    {
        super(engine);
        
        setA(minMin);
        setB(minMax);
        setC(maxMax);
        setD(maxMin);
        
        setViewport(viewport);
        
        triOne = new E3DTriangle(getEngine(),  new E3DVector3F(a.getX(), a.getY(), 0.0), new E3DVector3F(b.getX(), b.getY(), 0.0), new E3DVector3F(c.getX(), c.getY(), 0.0));
		triTwo = new E3DTriangle(getEngine(),  new E3DVector3F(a.getX(), a.getY(), 0.0), new E3DVector3F(c.getX(), c.getY(), 0.0), new E3DVector3F(d.getX(), d.getY(), 0.0));
    }
    


  /*  public boolean isTriangleInFrustum(E3DTriangle triangle)
    {
        double projX, projY, projZ;
        E3DVector3F[] triVertex = triangle.getVertex();
        E3DVector3F[] proj = new E3DVector3F[3];
        float[] projArray = new float[3];
        
        recalcViewMatrices();
        
        for(int i=0; i<3; i++)
        {
            proj[i] = getProjectedPoint(triVertex[i]);
            proj[i].setZ(0.0);
            
            if(projectedPointInFrustum(proj[i]))
                return true;
        }
        
        return false;
    }
    */

    /**
     * Check if a Portal is in view of the frustum.
     * @return
     *  Returns true if the portal is visible by this frustum
     */
    public boolean isPortalInFrustum(E3DPortal portal)
    {
        E3DVector3F[] proj = new E3DVector3F[4];
        
        E3DVector3F portalA = portal.getA();
        E3DVector3F portalB = portal.getB();
        E3DVector3F portalC = portal.getC();
        E3DVector3F portalD = portal.getD();
       
        //Project all the corners of the portal that we are trying to see is in this frustum
        
        proj[0] = viewport.projectPoint(portalA);
        proj[0].setZ(0.0);

        proj[1] = viewport.projectPoint(portalB);
        proj[1].setZ(0.0);
        
        proj[2] = viewport.projectPoint(portalC);
        proj[2].setZ(0.0);

        proj[3] = viewport.projectPoint(portalD);
        proj[3].setZ(0.0);

        /**
         * 1. Check if a portal corner point is in the frustum
         */
        for(int i=0; i<4; i++)
        {
	        if(projectedPointInFrustum(proj[i]))
	            return true;
        }

        /**
         * 2. Check if a frustum corner point is in the portal
         */
        E3DTriangle portalTriA = new E3DTriangle(getEngine(), proj[0], proj[1], proj[3]);
        E3DTriangle portalTriB = new E3DTriangle(getEngine(), proj[1], proj[2], proj[3]);
        
        if(portalTriA.isPointInTriangle(a) ||
           portalTriB.isPointInTriangle(a) ||   
           portalTriA.isPointInTriangle(b) ||
           portalTriB.isPointInTriangle(b)||    
           portalTriA.isPointInTriangle(c)||
           portalTriB.isPointInTriangle(c) ||
           portalTriA.isPointInTriangle(d) ||
           portalTriB.isPointInTriangle(d))   
             return true;
        
        /**
         * 3. Check if any of the line segments intersect
         */
        if(get2DLineIntersectionPt(a, b, proj[0], proj[1]) != null || 
           get2DLineIntersectionPt(a, b, proj[1], proj[2]) != null ||  
           get2DLineIntersectionPt(a, b, proj[2], proj[3]) != null || 
           get2DLineIntersectionPt(a, b, proj[3], proj[0]) != null || 
           get2DLineIntersectionPt(b, c, proj[0], proj[1]) != null || 
           get2DLineIntersectionPt(b, c, proj[1], proj[2]) != null || 
           get2DLineIntersectionPt(b, c, proj[2], proj[3]) != null ||            
           get2DLineIntersectionPt(b, c, proj[3], proj[0]) != null ||            
           get2DLineIntersectionPt(c, d, proj[0], proj[1]) != null || 
           get2DLineIntersectionPt(c, d, proj[1], proj[2]) != null || 
           get2DLineIntersectionPt(c, d, proj[2], proj[3]) != null ||            
           get2DLineIntersectionPt(c, d, proj[3], proj[0]) != null ||            
           get2DLineIntersectionPt(d, a, proj[0], proj[1]) != null ||            
           get2DLineIntersectionPt(d, a, proj[1], proj[2]) != null ||            
           get2DLineIntersectionPt(d, a, proj[2], proj[3]) != null ||            
           get2DLineIntersectionPt(d, a, proj[3], proj[0]) != null)
            return true;
        
        return false;
    }
    
    /**
     * Get the intersection pt of two 2d lines made of A->B  and C->D.  TODO: This is a misplaced
     * method, but its only used currently by this frustum.
     * @param ptA First point of segment1
     * @param ptB Second point of segment1
     * @param ptC First point of segment2
     * @param ptD Second point of segment2
     * @return
     */
    public E3DVector3F get2DLineIntersectionPt(E3DVector2F ptA, E3DVector2F ptB, E3DVector3F ptC, E3DVector3F ptD)
    {
        //First, put into y = mx+b form
        boolean vertical1 = false;
        double m1=0.0;
        double b1;
        if(ptB.getX() != ptA.getX())
        {
            m1 = (ptB.getY() - ptA.getY()) / (ptB.getX() - ptA.getX()); //rise
            b1 = ptA.getY() - (m1 * ptA.getX()); //Solve for B we now have m and b
        }
        else
        {
            vertical1 = true;
            b1 = ptA.getX();
        }
        
        //Segment 2 in y=mx+b form
        boolean vertical2 = false;
        double m2 = 0.0;
        double b2;
        if(ptD.getX() != ptC.getX()) //denominator of 0 means vertical line 
        {
            m2 = (ptD.getY() - ptC.getY()) / (ptD.getX() - ptC.getX()); //rise
            b2 = ptC.getY() - (m2 * ptC.getX());
        }
        else
        {
            vertical2 = true;
            b2 = ptC.getX();
        }
        
        //If both are vertical see if the two segments intersect
        if(vertical1 && vertical2)
        {
            if(b1 != b2) //parallel lines, no intersection
                return null;
            else //equal means they are the same line, see if the segments intersect
            {
                //check if either pt of segment 2 is withing segment 1. If so, return that point (since there can be multiple intersection pts when they're in the same line, it doesn't matter exactly what pt)
                if((ptD.getY() >= ptA.getY() && ptD.getY() <= ptB.getY()) || //D is between A and B
                   (ptD.getY() <= ptA.getY() && ptD.getY() >= ptB.getY()))
                   return ptD;
                else if((ptC.getY() >= ptA.getY() && ptC.getY() <= ptB.getY()) || //C is between A and B
                        (ptC.getY() <= ptA.getY() && ptC.getY() >= ptB.getY()))
                    return ptC;
                else //don't intersect
                    return null;
            }
        }
        else if(vertical1) //if one is vertical, find out if an x value of the vertical yields a Y value on the line is between the pts of the vertical line
        {
            double intX = b1;
            double intY = (m2*intX) + b2;
            if(((intY >= ptD.getY() && intY <= ptC.getY()) || 
               (intY >= ptC.getY() && intY <= ptD.getY())) &&
               (intX >= ptD.getX() && intX <= ptC.getX()) ||
               (intX >= ptC.getX() && intX <= ptD.getX()))
                return new E3DVector3F(intX, intY, 0.0);
            else
                return null;
        }
        else if(vertical2) //C->D is vertical
        {
            double intX = b2;
            double intY = (m1*intX) + b1;
            if(((intY >= ptA.getY() && intY <= ptB.getY()) || 
               (intY >= ptB.getY() && intY <= ptA.getY())) &&
               (intX >= ptA.getX() && intX <= ptB.getX()) ||
               (intX >= ptB.getX() && intX <= ptA.getX()))
                return new E3DVector3F(intX, intY, 0.0);
            else
                return null;
            
        }
        else
        {
	        //Now set the Y's equal to each other to solve for X, then replace X in one of the equations to get Y. That is the intersection pt of the lines
	        //m1x + b1 = m2x + b2
	        //= m1x = m2x + b2 - b1 =
	        //= m1x - m2x = b2 - b1
	        // = (m1-m2)x = (b2 - b1)
	        // = x = (b2 - b1) / (m1 - m2)
	
	        double intX = 0.0;
	        if(m1 - m2 != 0.0)
	            intX = (b2 - b1) /  (m1 - m2);
	        else
	            intX = (b2 - b1);
	        
	        double intY = (m1 * intX) + b1; //get Y from that X
	        if(intY == 0)
	            intY = (m2 * intX) + b2;

	        //Now determine if intX and intY lie  on both lines ptA->ptB  and ptC->ptD
	        if( ((intX >= ptA.getX() && intX <= ptB.getX()) || (intX >= ptB.getX() && intX <= ptA.getX())) && 
	            ((intX >= ptC.getX() && intX <= ptD.getX()) || (intX >= ptD.getX() && intX <= ptC.getX())) &&
	            ((intY >= ptA.getY() && intY <= ptB.getY()) || (intY >= ptB.getY() && intY <= ptA.getY())) && 
	            ((intY >= ptC.getY() && intY <= ptD.getY()) || (intY >= ptD.getY() && intY <= ptC.getY())) )
	            return new E3DVector3F(intX, intY, 0.0);
	        else
	            return null;
        }
    }
    
    /**
     * See if a projected point lies within the frustum
     * @param point
     * @return
     */
    public boolean projectedPointInFrustum(E3DVector3F point) //this could be sped up...
    {
        return triOne.isPointInTriangle(point) || triTwo.isPointInTriangle(point);
    }
    
    /**
     * Get corner A projected into 2D space of the frustum
     * @return
     */
    public E3DVector2F getA() {
        return a;
    }
	/**
	 * Set corner A of the frustum .  This must be projected before setting!
	 * @param a
	 */
    public void setA(E3DVector2F a) {
        this.a = a;
    }
    /**
     * Get corner B projected into 2D space of the frustum
     * @return
     */
    public E3DVector2F getB() {
        return b;
    }
	/**
	 * Set corner B of the frustum .  This must be projected before setting!
	 * @param a
	 */
    public void setB(E3DVector2F b) {
        this.b = b;
    }
    /**
     * Get corner C projected into 2D space of the frustum
     * @return
     */
    public E3DVector2F getC() {
        return c;
    }
	/**
	 * Set corner C of the frustum .  This must be projected before setting!
	 * @param a
	 */
    public void setC(E3DVector2F c) {
        this.c = c;
    }
    /**
     * Get corner C projected into 2D space of the frustum
     * @return
     */
    public E3DVector2F getD() {
        return d;
    }
	/**
	 * Set corner D of the frustum .  This must be projected before setting!
	 * @param a
	 */
    public void setD(E3DVector2F d) {
        this.d = d;
    }
	public E3DViewport getViewport()
	{
		return viewport;
	}
	
	private void setViewport(E3DViewport viewport)
	{
		this.viewport = viewport;
	}
}

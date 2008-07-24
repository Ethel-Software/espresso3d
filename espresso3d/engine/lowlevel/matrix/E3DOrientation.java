/*
 * Created on Mar 10, 2005
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
package espresso3d.engine.lowlevel.matrix;

import java.nio.FloatBuffer;
import java.util.ArrayList;

import espresso3d.engine.E3DEngine;
import espresso3d.engine.common.E3DConstants;
import espresso3d.engine.lowlevel.geometry.E3DLine;
import espresso3d.engine.lowlevel.vector.E3DVector3F;
import espresso3d.engine.renderer.base.E3DRenderable;

/**
 * @author Curt
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class E3DOrientation extends E3DRenderable 
{
    private E3DMatrix4x4F orientationMatrix;
    
    private static E3DMatrix4x4F scratchMatrix;

    public E3DOrientation(E3DEngine engine)
    {
        super(engine);
        
        orientationMatrix = new E3DMatrix4x4F();
        init();
    }

    public E3DOrientation(E3DEngine engine, E3DMatrix4x4F orientationMatrix)
    {
        super(engine);
        
        this.orientationMatrix = orientationMatrix;
        init();
    }
    
    public E3DOrientation(E3DOrientation toCopyOrientation)
    {
        super(toCopyOrientation.getEngine());
        
        orientationMatrix = new E3DMatrix4x4F(toCopyOrientation.orientationMatrix);
        init();
    }
    
    private void init()
    {
    	if(scratchMatrix == null)
    		scratchMatrix = new E3DMatrix4x4F();
    }
    public FloatBuffer getFloatBuffer()
    {
        return orientationMatrix.getFloatBuffer();
    }
    /* (non-Javadoc)
     * @see espresso3d.engine.renderer.base.E3DRenderable#renderAtPosition(espresso3d.engine.lowlevel.vector.E3DVector3F)
     */
    public void render() {
        ArrayList lineList = new ArrayList();
        
        E3DVector3F position = getPosition();
        E3DVector3F up = getUp();
        E3DVector3F forward = getForward();
        E3DVector3F left = getLeft();
        
	      lineList.add(new E3DLine(getEngine(), position, position.add(up.scale(5)), new E3DVector3F(0.0, 1.0, 1.0)));
	      lineList.add(new E3DLine(getEngine(), position, position.add(forward.scale(5)), new E3DVector3F(1.0, 0.0, 0.0)));
	      lineList.add(new E3DLine(getEngine(), position, position.add(left.scale(5)), new E3DVector3F(0.0, 0.0, 1.0)));
	      
	      getEngine().getGeometryRenderer().initSolidAndLineRendering();
	      getEngine().getGeometryRenderer().renderLineList(lineList);
	      
    }

    public void rotateAroundNonNormalised(double angle, E3DVector3F upVec)
    {
    	
        if(angle == 0.0)
            return;
        
//        [t(xx) + c,  txy+sz, txz-sy, 0]
//        [txy - sz,  t(yy)+c, tyz+sx, 0]
//        [txz + sy,  tyz-sx, t(zz)+c, 0]
//        [0, 0, 0, 1]
// Where c = cos(angle),  s = sin(angle),  t = 1-cos(angle)  
 // http://www.gamedev.net/reference/articles/article1199.asp
        
        
/*
 * X2 + c,  tXY + sZ, tXZ - sY, 0
 * tXY-sZ, tY2 + c, tYZ + sX, 0
 * tXY + sY, tYZ - sX, tZ2 + c
    


 */        
  //      E3DVector3F position = getPosition();
  //      translate(position.scale(-1));
        
//        upVec.normaliseEqual();
        double c = Math.cos(angle);
        double s = Math.sin(angle);
        double t = 1-c;
        double x = upVec.getX(), y = upVec.getY(), z = upVec.getZ();
        double xx = x * x, yy = y * y, zz = z * z;
        double sx = s * x, sy = s * y, sz = s * z;
        
        scratchMatrix.loadIdentity();
        
/*        scratchMatrix.matrix4x4[0][0] = (t * xx) +  c;
        scratchMatrix.matrix4x4[0][1] = (t * x * y) + sz;
        scratchMatrix.matrix4x4[0][2] = (t * x * z) - sy;
        scratchMatrix.matrix4x4[1][0] = (t * x * y) - sz;
        scratchMatrix.matrix4x4[1][1] = (t * yy) + c;
        scratchMatrix.matrix4x4[1][2] = (t * y * z) + sx;
        scratchMatrix.matrix4x4[2][0] = (t * x * z) + sy;
        scratchMatrix.matrix4x4[2][1] = (t * y * z) - sx;
        scratchMatrix.matrix4x4[2][2] = (t * zz)  + c;
 */
        scratchMatrix.matrix4x4[0][0] = (t * xx) +  c;
        scratchMatrix.matrix4x4[0][1] = (t * x * y) - sz;
        scratchMatrix.matrix4x4[0][2] = (t * x * z) + sy;
        scratchMatrix.matrix4x4[1][0] = (t * x * y) + sz;
        scratchMatrix.matrix4x4[1][1] = (t * yy) + c;
        scratchMatrix.matrix4x4[1][2] = (t * y * z) - sx;
        scratchMatrix.matrix4x4[2][0] = (t * x * z) - sy;
        scratchMatrix.matrix4x4[2][1] = (t * y * z) + sx;
        scratchMatrix.matrix4x4[2][2] = (t * zz)  + c;
        
       orientationMatrix = scratchMatrix.multiply(orientationMatrix); //.multiplyEqual(scratchMatrix);
      // translate(position);
    }
    
   
    public void rotate(double angle, E3DVector3F upVec)
    {
        if(angle == 0.0)
            return;
        
//        [t(xx) + c,  txy+sz, txz-sy, 0]
//        [txy - sz,  t(yy)+c, tyz+sx, 0]
//        [txz + sy,  tyz-sx, t(zz)+c, 0]
//        [0, 0, 0, 1]
// Where c = cos(angle),  s = sin(angle),  t = 1-cos(angle)  
 // http://www.gamedev.net/reference/articles/article1199.asp
        
        
/*
 * X2 + c,  tXY + sZ, tXZ - sY, 0
 * tXY-sZ, tY2 + c, tYZ + sX, 0
 * tXY + sY, tYZ - sX, tZ2 + c
    


 */        
        E3DVector3F position = getPosition();
        translate(position.scale(-1));
        
        upVec.normaliseEqual();
        double c = Math.cos(angle);
        double s = Math.sin(angle);
        double t = 1-c;
        double x = upVec.getX(), y = upVec.getY(), z = upVec.getZ();
        double xx = x * x, yy = y * y, zz = z * z;
        double sx = s * x, sy = s * y, sz = s * z;
        
        scratchMatrix.loadIdentity();
        
/*        scratchMatrix.matrix4x4[0][0] = (t * xx) +  c;
        scratchMatrix.matrix4x4[0][1] = (t * x * y) + sz;
        scratchMatrix.matrix4x4[0][2] = (t * x * z) - sy;
        scratchMatrix.matrix4x4[1][0] = (t * x * y) - sz;
        scratchMatrix.matrix4x4[1][1] = (t * yy) + c;
        scratchMatrix.matrix4x4[1][2] = (t * y * z) + sx;
        scratchMatrix.matrix4x4[2][0] = (t * x * z) + sy;
        scratchMatrix.matrix4x4[2][1] = (t * y * z) - sx;
        scratchMatrix.matrix4x4[2][2] = (t * zz)  + c;
 */
        scratchMatrix.matrix4x4[0][0] = (t * xx) +  c;
        scratchMatrix.matrix4x4[0][1] = (t * x * y) - sz;
        scratchMatrix.matrix4x4[0][2] = (t * x * z) + sy;
        scratchMatrix.matrix4x4[1][0] = (t * x * y) + sz;
        scratchMatrix.matrix4x4[1][1] = (t * yy) + c;
        scratchMatrix.matrix4x4[1][2] = (t * y * z) - sx;
        scratchMatrix.matrix4x4[2][0] = (t * x * z) - sy;
        scratchMatrix.matrix4x4[2][1] = (t * y * z) + sx;
        scratchMatrix.matrix4x4[2][2] = (t * zz)  + c;
        
       orientationMatrix = scratchMatrix.multiply(orientationMatrix); //.multiplyEqual(scratchMatrix);
       translate(position);
       
    }
    
    public void rotate(double x, double y, double z)
    {
    	double cosX = Math.cos(x);
    	double sinX = Math.sin(x);
    	double cosY = Math.cos(y);
    	double sinY = Math.sin(y);
    	double cosZ = Math.cos(z);
    	double sinZ = Math.sin(z);

        
        //A = cosX
        //B = sinX
        //C = cosY
        //D = sinY
        //E = cosZ
        //F =sinZ
    	
//    		 |  0   0   1   0 |
//        M  = |  V   W   0   0 |
//             | -W   V   0   0 |
//             |  0   0   0   1 |

             double v = (sinX * cosZ) + (cosX * sinZ);
        	 double w = (cosX * cosZ) - (sinX * sinZ);
        	 
    	scratchMatrix.loadIdentity();

//    	scratchMatrix.matrix4x4[0][0] = 0;
//    	scratchMatrix.matrix4x4[0][1] = v;
//    	scratchMatrix.matrix4x4[0][2] = -w;
//    	scratchMatrix.matrix4x4[0][3] = 0;
//    	scratchMatrix.matrix4x4[1][0] = 0;
//    	scratchMatrix.matrix4x4[1][1] = w;
//    	scratchMatrix.matrix4x4[1][2] = v;
//    	scratchMatrix.matrix4x4[1][3] = 0;
//    	scratchMatrix.matrix4x4[2][0] = 1;
//    	scratchMatrix.matrix4x4[2][1] = 0;
//    	scratchMatrix.matrix4x4[2][2] = 0;
//    	scratchMatrix.matrix4x4[2][3] = 0;
//    	scratchMatrix.matrix4x4[3][0] = 0;
//    	scratchMatrix.matrix4x4[3][1] = 0;
//    	scratchMatrix.matrix4x4[3][2] = 0;
//    	scratchMatrix.matrix4x4[3][3] = 1;
    	
    	scratchMatrix.matrix4x4[0][0] = cosY * cosZ; //CE
    	scratchMatrix.matrix4x4[0][1] = (sinX * sinY * cosZ) + (cosX * sinZ); //BDE+AF
    	scratchMatrix.matrix4x4[0][2] = (-cosX * sinY * cosZ) + (sinX * sinZ); //-ADE+BF
    	scratchMatrix.matrix4x4[0][3] = 0;
    	scratchMatrix.matrix4x4[1][0] = -cosY * sinZ; //-CF
    	scratchMatrix.matrix4x4[1][1] = (-sinX * sinY * sinZ) + (cosX * cosZ); //-BDF+AE
    	scratchMatrix.matrix4x4[1][2] = (-cosX * sinY * sinZ) + (sinX * cosZ); //-ADF+BE
    	scratchMatrix.matrix4x4[1][3] = 0;
    	scratchMatrix.matrix4x4[2][0] = sinY; //D
    	scratchMatrix.matrix4x4[2][1] = -sinX * cosY; //-BC
    	scratchMatrix.matrix4x4[2][2] = -cosX * cosY; //AC
    	scratchMatrix.matrix4x4[2][3] = 0;
    	scratchMatrix.matrix4x4[3][0] = 0;
    	scratchMatrix.matrix4x4[3][1] = 0;
    	scratchMatrix.matrix4x4[3][2] = 0;
    	scratchMatrix.matrix4x4[3][3] = 1;

    	
    	
    		
    	orientationMatrix = scratchMatrix.multiply(orientationMatrix); 
    }
    public void rotateXEqual(double angle)
    {
        double cosAngle = Math.cos(angle);
        double sinAngle = Math.sin(angle);
        
        //  1, 0, 0, 0
        //  0, cosAngle, sinAngle, 0
        //  0, -sinAngle, cosAngle, 0
        //  0, 0, 0, 1
        scratchMatrix.loadIdentity();
        scratchMatrix.matrix4x4[1][1] = cosAngle;
        scratchMatrix.matrix4x4[1][2] = sinAngle;
        scratchMatrix.matrix4x4[2][1] = -sinAngle;
        scratchMatrix.matrix4x4[2][2] = cosAngle;
        
        
        
        orientationMatrix = scratchMatrix.multiply(orientationMatrix); 
    }
    
    public void rotateYEqual(double angle)
    {
        double cosAngle = Math.cos(angle);
        double sinAngle = Math.sin(angle);
       //cos, 0, -sin, 0
       //0, 1, 0, 0
       //sin, 0, cos, 0
       //0, 0, 0, 1

       scratchMatrix.loadIdentity();
       scratchMatrix.matrix4x4[0][0] = cosAngle;
       scratchMatrix.matrix4x4[0][2] = -sinAngle;
       scratchMatrix.matrix4x4[2][0] = sinAngle;
       scratchMatrix.matrix4x4[2][2] = cosAngle;
       
       orientationMatrix = scratchMatrix.multiply(orientationMatrix); 
    }

    public void rotateZEqual(double angle)
    {
        double cosAngle = Math.cos(angle);
        double sinAngle = Math.sin(angle);
        
        //cos, sin, 0, 0
        //-sin, cos, 0, 0,
        //0, 0, 1, 0
        //0, 0, 0, 1
             
        scratchMatrix.loadIdentity();
        scratchMatrix.matrix4x4[0][0] = cosAngle;
        scratchMatrix.matrix4x4[0][1] = sinAngle;
        scratchMatrix.matrix4x4[1][0] = -sinAngle;
        scratchMatrix.matrix4x4[1][1] = cosAngle;

        orientationMatrix = scratchMatrix.multiply(orientationMatrix); 
    }
    


    public void translate(E3DVector3F translationAmt)
    {
        scratchMatrix.loadIdentity();
        
        scratchMatrix.matrix4x4[0][3] = translationAmt.getX();
        scratchMatrix.matrix4x4[1][3] = translationAmt.getY();
        scratchMatrix.matrix4x4[2][3] = translationAmt.getZ();
        orientationMatrix = scratchMatrix.multiply(orientationMatrix); 
    }
    
    public E3DVector3F getUp()
    {
       return new E3DVector3F(orientationMatrix.matrix4x4[0][1], orientationMatrix.matrix4x4[1][1], orientationMatrix.matrix4x4[2][1]); 
    }
    
    public void setUp(E3DVector3F newUp)
    {
        E3DVector3F up = getUp();
        
        double angle = up.angleBetweenRads(newUp);
        E3DVector3F cross = up.crossProduct(newUp);

        if(cross.equals(0.0, 0.0, 0.0))
            cross = getForward();
        cross.normaliseEqual();
        
        rotate(angle, cross);
    }
    
    public E3DVector3F getForward()
    {
  //      return orientationMatrix.multiplyVector(new E3DVector3F(0, 0, 1));
        
        return new E3DVector3F(orientationMatrix.matrix4x4[0][2], orientationMatrix.matrix4x4[1][2], orientationMatrix.matrix4x4[2][2]); 
    }
    
    public void setForward(E3DVector3F newForward)
    {
        E3DVector3F forward = getForward();
        
        double angle = forward.angleBetweenRads(newForward);
        E3DVector3F cross = forward.crossProduct(newForward);

        if(cross.equals(0.0, 0.0, 0.0))
            cross = getUp();
        cross.normaliseEqual();
        
        rotate(angle, cross);
    }
    
    public E3DVector3F getPosition()
    {
        return new E3DVector3F(orientationMatrix.matrix4x4[0][3], orientationMatrix.matrix4x4[1][3], orientationMatrix.matrix4x4[2][3]); 
    }
    
    public void setPosition(E3DVector3F position)
    {
        translate(position.subtract(getPosition()));
    }
    
    public boolean equals(Object arg0) 
    {
    	if(arg0 == null || !(arg0 instanceof E3DOrientation))
    		return false;
    	
    	E3DOrientation argOrientation = (E3DOrientation)arg0;
    	for(int a=0; a < 4; a++)
    	{
    		for(int b=0; b < 4; b++)
    		{
    			if(argOrientation.orientationMatrix.matrix4x4[a][b] != orientationMatrix.matrix4x4[a][b])
    				return false;
    		}
    	}
    	return true;
    }
    
    public int hashCode() 
    {
    	int cd = 0;
    	for(int a=0; a < 4; a++)
    	{
    		for(int b=0; b < 4; b++)
    		{
    			cd += orientationMatrix.matrix4x4[a][b];
    		}
    	}
    	
    	return cd;
    }
    
    public E3DVector3F getLeft()
    {
        return getUp().crossProduct(getForward()).normalise();        
    }
    
    public String toString() {
        return "Position: " + getPosition() + "\n" +
               "Forward: " + getForward() + "\n" + 
               "Up: " + getUp() + "\n";
    }
    
    /**
     * Gets a world vector and converts to an object space vector (Fast, without inverses)
     * @param worldVector
     * @return
     */
    public E3DVector3F getLocalVector(E3DVector3F worldVector)
    {
        double tX = worldVector.getX() - orientationMatrix.matrix4x4[0][3];
        double tY = worldVector.getY() - orientationMatrix.matrix4x4[1][3];
        double tZ = worldVector.getZ() - orientationMatrix.matrix4x4[2][3];
        
        return new E3DVector3F(tX * orientationMatrix.matrix4x4[0][0] + tY * orientationMatrix.matrix4x4[1][0] + tZ * orientationMatrix.matrix4x4[2][0],
                tX * orientationMatrix.matrix4x4[0][1] + tY * orientationMatrix.matrix4x4[1][1] + tZ * orientationMatrix.matrix4x4[2][1],
                tX * orientationMatrix.matrix4x4[0][2] + tY * orientationMatrix.matrix4x4[1][2] + tZ * orientationMatrix.matrix4x4[2][2]);
    }
    
    public void multiplyEqual(E3DMatrix4x4F multMatrix)
    {
    	orientationMatrix.multiplyEqual(multMatrix);
    }

    public E3DOrientation multiply(E3DMatrix4x4F multMatrix)
    {
    	return new E3DOrientation(getEngine(), orientationMatrix.multiply(multMatrix));
    }
    
    public E3DVector3F getWorldVector(E3DVector3F localVector)
    {
        E3DVector3F vec = orientationMatrix.multiplyVector(localVector);
        vec.addEqual(getPosition());
        return vec;
    }

	/**
	 * Actually does a Vector * Matrix to transform the vector by this matrix
	 * @param vecToMultiply
	 * @return
	 */
	public E3DVector3F multiply(E3DVector3F vecToMultiply)
	{
		return orientationMatrix.multiplyVector(vecToMultiply);
	}

	public E3DMatrix4x4F getOrientationMatrix() {
		return orientationMatrix;
	}
	
	public void loadIdentity()
	{
		orientationMatrix.loadIdentity();
	}
	
	public E3DOrientation normaliseForwardAndUp()
	{
		E3DOrientation retOrientation = new E3DOrientation(this);
		retOrientation.normaliseForwardAndUpEqual();
		return retOrientation;
	}
	
	public void normaliseForwardAndUpEqual()
	{
		E3DVector3F forward = getForward();
		forward.normaliseEqual();
//		if(E3DConstants.closeToPrecisionError(forward.getX()))
//			forward.setX(0.0);
//		if(E3DConstants.closeToPrecisionError(forward.getY()))
//			forward.setY(0.0);
//		if(E3DConstants.closeToPrecisionError(forward.getZ()))
//			forward.setZ(0.0);
//		forward.normaliseEqual();
		orientationMatrix.matrix4x4[0][2] = forward.getX();
		orientationMatrix.matrix4x4[1][2] = forward.getY();
		orientationMatrix.matrix4x4[2][2] = forward.getZ();

		E3DVector3F up = getUp();
		up.normaliseEqual();
//		if(E3DConstants.closeToPrecisionError(up.getX()))
//			up.setX(0.0);
//		if(E3DConstants.closeToPrecisionError(up.getY()))
//			up.setY(0.0);
//		if(E3DConstants.closeToPrecisionError(up.getZ()))
//			up.setZ(0.0);
//		up.normaliseEqual();
		orientationMatrix.matrix4x4[0][1] = up.getX();
	    orientationMatrix.matrix4x4[1][1] = up.getY();
	    orientationMatrix.matrix4x4[2][1] = up.getZ();
	}
	
	/**
	 * Inverts the rotation portion of the matrix as if it were a 3x3 - translation and scale data is not touched
	 */
	public void invertFastEqual()
	{
		scratchMatrix.loadIdentity();
		
//		scratchMatrix.matrix4x4[0][0] =  scratchMatrix.matrix4x4[]
		scratchMatrix.matrix4x4[0][1] = orientationMatrix.matrix4x4[1][0];
		scratchMatrix.matrix4x4[0][2] = orientationMatrix.matrix4x4[2][0];
		scratchMatrix.matrix4x4[1][0] = orientationMatrix.matrix4x4[0][1];
//		scratchMatrix.matrix4x4[1][1] = 
		scratchMatrix.matrix4x4[1][2] = orientationMatrix.matrix4x4[2][1];
		scratchMatrix.matrix4x4[2][0] = orientationMatrix.matrix4x4[0][2];
		scratchMatrix.matrix4x4[2][1] = orientationMatrix.matrix4x4[1][2];
//		scratchMatrix.matrix4x4[2][2] =
		
		orientationMatrix.matrix4x4[0][1] = scratchMatrix.matrix4x4[0][1];
		orientationMatrix.matrix4x4[0][2] = scratchMatrix.matrix4x4[0][2];
		orientationMatrix.matrix4x4[1][0] = scratchMatrix.matrix4x4[1][0];
		orientationMatrix.matrix4x4[1][2] = scratchMatrix.matrix4x4[1][2];
		orientationMatrix.matrix4x4[2][0] = scratchMatrix.matrix4x4[2][0];
		orientationMatrix.matrix4x4[2][1] = scratchMatrix.matrix4x4[2][1];
	}
}

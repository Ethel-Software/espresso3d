/*
 * Created on Jul 17, 2004
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
package espresso3d.engine.lowlevel.vector;

import java.nio.FloatBuffer;

import org.lwjgl.BufferUtils;

import espresso3d.engine.common.E3DConstants;
import espresso3d.engine.lowlevel.tree.base.IE3DHashableNode;

/**
 * @author espresso3d
 *
 * 3D floating point vector class.
 */
public class E3DVector3F implements IE3DHashableNode
{
	private double x, y, z;
    private static FloatBuffer buffer;
    
	public E3DVector3F()
	{	
	    x = 0.0;
	    y = 0.0;
	    z = 0.0;
	}
	
	public E3DVector3F(E3DVector3F toCopy)
	{
	    if(toCopy == null)
	        return;
	    this.x = toCopy.x;
	    this.y = toCopy.y;
	    this.z = toCopy.z;
	}
	
	public E3DVector3F(double x, double y, double z)
	{
		this.x = x;
		this.y = y;
		this.z = z;
	}

	public boolean equals(E3DVector3F vec)
	{
		return equals(vec.x, vec.y, vec.z);
	}
	
	public boolean equals(double x, double y, double z)
	{
		return (this.x == x && this.y == y && this.z == z);
	}

	/**
	 * Returns true if the coords are close to the coord passed in
	 * (ie: they are essentially the same 
	 * but could be off by as much as E3DContants.DBL_PRECISION_ERROR)
	 * @param vec
	 * @return
	 */
	public boolean closeTo(E3DVector3F vec)
	{
		return closeTo(vec.x, vec.y, vec.z);
	}

	/**
	 * Returns true if the coords are close to the coord passed in
	 * (ie: they are essentially the same 
	 * but could be off by as much as E3DContants.DBL_PRECISION_ERROR)
	 * @param x
	 * @param y
	 * @param z
	 * @return
	 */
	public boolean closeTo(double x, double y, double z)
	{
        return (this.x >= x - E3DConstants.DBL_PRECISION_ERROR && this.x <= x + E3DConstants.DBL_PRECISION_ERROR && 
        	    this.y >= y - E3DConstants.DBL_PRECISION_ERROR && this.y <= y + E3DConstants.DBL_PRECISION_ERROR &&
                this.z >= z - E3DConstants.DBL_PRECISION_ERROR && this.z <= z + E3DConstants.DBL_PRECISION_ERROR);        
	}
	/**
	 * @return Returns the x.
	 */
	public double getX() {
		return x;
	}
	/**
	 * @param x The x to set.
	 */
	public void setX(double x) {
		this.x = x;
	}
	/**
	 * @return Returns the y.
	 */
	public double getY() {
		return y;
	}
	/**
	 * @param y The y to set.
	 */
	public void setY(double y) {
		this.y = y;
	}
	/**
	 * @return Returns the z.
	 */
	public double getZ() {
		return z;
	}
	/**
	 * @param z The z to set.
	 */
	public void setZ(double z) {
		this.z = z;
	}

	/**
	 * Set the x, y, and z coord of this vec to copies of the passed in vector
	 * @param newVec
	 */
	public void set(E3DVector3F newVec)
	{
	    x = newVec.x;
	    y = newVec.y;
	    z = newVec.z;
	}
	

	/**
	 * Set the coordinates to x, y, and z
	 * @param x
	 * @param y
	 * @param z
	 */
	public void set(double x, double y, double z)
	{
	    this.x = x;
	    this.y = y;
	    this.z = z;
	}
	
	/**
	 * Return a new E3DVector3F with this + translationAmt added
	 * @param translationAmt
	 * @return
	 */
	public E3DVector3F add(E3DVector3F translationAmt)
	{
		return new E3DVector3F(this.x + translationAmt.x, 
				this.y + translationAmt.y,
				this.z + translationAmt.z);
	}
	
	/**
	 * Set this vector to this + translationAmt
	 * @param translationAmt
	 */
	public void addEqual(E3DVector3F translationAmt)
	{
	    x += translationAmt.x;
	    y += translationAmt.y;
	    z += translationAmt.z;
	}
	
	/**
	 * Return a new E3DVector3F with this - translationAmt subtract
	 * @param translationAmt
	 * @return
	 */	
	public E3DVector3F subtract(E3DVector3F translationAmt)
	{
		return new E3DVector3F(this.x - translationAmt.x, 
				this.y - translationAmt.y,
				this.z - translationAmt.z);
	}
	
	public E3DVector3F subtract(E3DVector2F translationAmt)
	{
		return new E3DVector3F(this.x - translationAmt.getX(), 
				this.y - translationAmt.getY(),
				this.z);
	}
	/**
	 * Set this vector to this - translationAmt
	 * @param translationAmt
	 */
	public void subtractEqual(E3DVector3F translationAmt)
	{
	    x -= translationAmt.x;
	    y -= translationAmt.y;
	    z -= translationAmt.z;
	}	
	
	/**
	 * Return a new vector of this vector scaled by scaleAmt
	 * @param scaleAmt
	 * @return
	 */
	public E3DVector3F scale(double scaleAmt)
	{
		return new E3DVector3F(x * scaleAmt, y * scaleAmt, z * scaleAmt);
	}
	
	/**
	 * Set this vector to this scaled by scaleAmt
	 * @param translationAmt
	 */
	public void scaleEqual(double scaleAmt)
	{
	    x *= scaleAmt;
	    y *= scaleAmt;
	    z *= scaleAmt;
	}
	
	/**
	 * Get a new vector with thisVector * multiplyAmt
	 * @param multiplyAmt
	 * @return
	 */
	public E3DVector3F multiply(E3DVector3F multiplyAmt)
	{
		return new E3DVector3F(x * multiplyAmt.x, y * multiplyAmt.y, z * multiplyAmt.z);		
	}

	/**
	 * Set this vector equal to this * multiplyAmt
	 * @param multiplyAmt
	 */
	public void multiplyEqual(E3DVector3F multiplyAmt)
	{
	    x *= multiplyAmt.x;
	    y *= multiplyAmt.y;
	    z *= multiplyAmt.z;
	}

	/**
	 * Get a new vector that is rotated angle radians around the around vec
	 * @param angle  Angle to rotate in radians
	 * @param aroundVec Vector that creates the central axis to rotate around
	 * @return
	 * 	Returns a new vector that is this vector rotated angle radians around the around vec
	 */
	public E3DVector3F rotate(double angle, E3DVector3F aroundVec)
	{
		return getRotatedVector(angle, aroundVec);
	}
	
	/**
	 * Rotate this vector angle radians around the aroundVec 
	 * @param angle
	 * @param aroundVec  Vector that creates the central axis to rotate around
	 * 
	 */
	public void rotateEqual(double angle, E3DVector3F aroundVec)
	{
		set(getRotatedVector(angle, aroundVec));
	}

	/**
	 * Performs the actual rotation
	 * @param angle
	 * @param aroundVec
	 * @return
	 */
	private E3DVector3F getRotatedVector(double angle, E3DVector3F aroundVec)
	{
		// Calculate the sine and cosine of the angle once
		double cosTheta = Math.cos(angle);
		double sinTheta = Math.sin(angle);

		double aroundX = aroundVec.x;
		double aroundY = aroundVec.y;
		double aroundZ = aroundVec.z;
		
		// Find the new x position for the new rotated point
		double x = ((cosTheta + (1 - cosTheta) * aroundX * aroundX) * this.x) + 
				   (((1 - cosTheta) * aroundX * aroundY - aroundZ * sinTheta) * this.y) +
				   (((1 - cosTheta) *  aroundX *  aroundZ +  aroundY * sinTheta) * this.z);
	
		// Find the new y position for the new rotated point
		double y = (((1 - cosTheta) * aroundX * aroundY + aroundZ * sinTheta) * this.x) +
					((cosTheta + (1 - cosTheta) * aroundY * aroundY) * this.y) +
					((1 - cosTheta) * aroundY * aroundZ - aroundX * sinTheta) * this.z;
	
		// Find the new z position for the new rotated point
		double z = (((1 - cosTheta) * aroundX * aroundZ - aroundY * sinTheta)	* this.x) +
					(((1 - cosTheta) * aroundY * aroundZ + aroundX * sinTheta) * this.y) +
					((cosTheta + (1 - cosTheta) * aroundZ * aroundZ) * this.z);

		return new E3DVector3F(x, y, z);
	}
	
	/**
	 * Get the dot product of this vector and otherVec
	 * @param otherVec
	 * @return
	 */
	public double dotProduct(E3DVector3F otherVec)
	{
		return (x * otherVec.x) + (y * otherVec.y) + (z * otherVec.z);
	}

	/**
	 * Get the length of the vector squared.  Actual length needs
	 * takes the sqrt of this value.
	 * @return
	 */
	public double getLengthSquared()
	{
		return (x * x) + (y * y) + (z * z);
	}

	/**
	 * Get the actual length of this vector
	 * @return
	 */
	public double getLength()
	{	
		return Math.sqrt(getLengthSquared());
	}
	
	/**
	 * Get the angle between this vector and otherVec in degrees
	 * @param otherVec
	 * @return
	 */
	public double angleBetweenDegrees(E3DVector3F otherVec)
	{
        return Math.toDegrees(angleBetweenRads(otherVec));
	}
	
	/**
	 * Get the angle between this vector and otherVec in radians
	 * @param otherVec
	 * @return
	 * Returns positive numbers between 0 and PI
	 */
	public double angleBetweenRads(E3DVector3F otherVec)
	{
		double dls = dotProduct(otherVec) / Math.sqrt((getLengthSquared() * otherVec.getLengthSquared()));
	//	System.out.println("Angle: " + Math.acos(dls) + " direction: " + Math.atan2(otherVec.getLength(), getLength()) + "  " );
        if (dls < -1.0)
            dls = -1.0;
        else if (dls > 1.0)
            dls = 1.0;
        return Math.acos(dls);
	}	
	
	/**
	 * Returns whether the angle is position or negative between the two single
	 * angle between can only return position numbers between 0 and PI
	 * @param otherVec
	 * @return
	 *  -1 if negative, 0 if no angle between, +1 if position
	 */
	public double getDirectionOfAngleBetween(E3DVector3F otherVec)
	{
		  // I operator overload:
		  // % is dotproduct
		  // * is cross product

		  double costheta = this.dotProduct(otherVec) / (getLength() * otherVec.getLength());
		  double sintheta = Math.asin(crossProduct(otherVec).getLength() / (getLength() * otherVec.getLength()));

		  double angle = Math.atan2(costheta, sintheta);
		  System.out.println("Angle atan: " + angle);
	
		  return angle;
//		  
////		  return angle 
//		  return angle < 0 ? -1 : 1;
		  
//		if(angleBetween >= E3DConstants.HALFPI && angle > 0)
//			return 1;
//		else if (angleBetween >= E3DConstants.HALFPI && angle < 0)
//			return -1;
//		else if(angleBetween < E3DConstants.HALFPI && angle > 0)
//			return -1;
//		else if(angleBetween < E3DConstants.HALFPI && angle < 0)
//			return 1;
//		else 
//			return 0;
//		double dls = dotProduct(otherVec) / (getLength() * otherVec.getLength());
//		if(dls < 0)
//			return -1;
//		else if(dls == 0)
//			return 0;
//		else
//			return 1;
	}

	/**
	 * Get a new vector that is the normalised version of this vector
	 * @return
	 */
	public E3DVector3F normalise()
	{
		double length = getLength();

		if(length == 0.0)
			return new E3DVector3F(this); //make a copy, its assumed this returns a new one
		else
			return new E3DVector3F(x / length, y / length, z / length);
	}
	
	/**
	 * Normalise this vector
	 */
	public void normaliseEqual()
	{
		double length = getLength();
		x /= length;
		y /= length;
		z /= length;
	}
	
	/**
	 * Get the cross product of this vector x otherVec
	 * @param otherVec
	 * @return
	 */
	public E3DVector3F crossProduct(E3DVector3F otherVec)
	{
	    return new E3DVector3F((y * otherVec.z) - (z * otherVec.y),
							  (otherVec.x * z) - (otherVec.z * x),
							  (x * otherVec.y) - (y * otherVec.x));
	}
	
//    private static FloatBuffer buffer = BufferUtils.createFloatBuffer(3);
	public FloatBuffer getFloatBuffer()
	{
        if(buffer == null)
            buffer = BufferUtils.createFloatBuffer(3);
        else
            buffer.clear();
       /* FloatBuffer buffer =  BufferUtils.createFloatBuffer(3)*/ buffer.put(new float[] { (float)x , (float)y, (float)z });
        buffer.rewind();
        return buffer;
	}
	
	public void appendToBuffer(FloatBuffer buffer)
	{
	    buffer.put((float)x);
	    buffer.put((float)y);
	    buffer.put((float)z);
	}
	/**
	 * Get a string of vertex coordinates for this vector
	 */
	public String toString()
	{
		return "(" + x +", " + y + ", " + z + ")";
	}

	public boolean equals(Object arg0) {
		if(arg0 == null || !(arg0 instanceof E3DVector3F))
			return false;
		return equals((E3DVector3F)arg0);
	}
	public int hashCode() {
		return (int)(x + y + z);
	}
}
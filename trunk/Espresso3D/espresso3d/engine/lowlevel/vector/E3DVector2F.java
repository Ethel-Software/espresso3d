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

import espresso3d.engine.lowlevel.tree.base.IE3DHashableNode;

/**
 * @author espresso3d
 *
 * 2D floating point vector class.
 */
public class E3DVector2F implements IE3DHashableNode
{
	private double x, y;
	
	public E3DVector2F()
	{	
		this(0.0, 0.0);
	}
	
	public E3DVector2F(E3DVector2F toCopy)
	{
	    if(toCopy == null)
	        return;
	    x = toCopy.x;
	    y = toCopy.y;
	}
	
	public E3DVector2F(double x, double y)
	{
	    this.x = x;
	    this.y = y;
	}

	public boolean equals(E3DVector2F vec)
	{
		if(vec == null)
			return false;
		
		return equals(vec.x, vec.y);
	}
	
	public boolean equals(double x, double y)
	{
		if(this.x == x && this.y == y)
			return true;
		else
			return false;
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

	public void set(E3DVector2F newVec)
	{
	    x = newVec.x;
	    y = newVec.y;
	}
	
	
	public void set(double x, double y)
	{
	    this.x = x;
	    this.y = y;
	}
	
	public E3DVector2F add(E3DVector2F translationAmt)
	{
		return new E3DVector2F(x + translationAmt.x, 
				y + translationAmt.y);
	}
	
	public void addEqual(E3DVector2F translationAmt)
	{
	    x += translationAmt.x;
	    y += translationAmt.y;
	}
	
	public E3DVector2F subtract(E3DVector2F translationAmt)
	{
		return new E3DVector2F(x - translationAmt.x, 
				y - translationAmt.y);
	}
	
	public void subtractEqual(E3DVector2F translationAmt)
	{
	    x -= translationAmt.x;
	    y -= translationAmt.y;
	}	
	
	public E3DVector2F scale(double scaleAmt)
	{
		return new E3DVector2F(x * scaleAmt, y * scaleAmt);
	}
	
	public void scaleEqual(double scaleAmt)
	{
	    x *= scaleAmt;
	    y *= scaleAmt;
	}
	
	public E3DVector2F multiply(E3DVector2F multiplyAmt)
	{
		return new E3DVector2F(x * multiplyAmt.x, y * multiplyAmt.y);		
	}
	
	public void multiplyEqual(E3DVector2F multiplyAmt)
	{
	    x *= multiplyAmt.x;
	    y *= multiplyAmt.y;
	}

	public double getLengthSquared()
	{
		return (x * x) + (y * y);
	}

	public double getLength()
	{	
		return Math.sqrt(getLengthSquared());
	}
	
	public E3DVector2F normalise()
	{
		double length = getLength();

		if(length == 0.0)
			return new E3DVector2F(this); //make a copy, its assumed this returns a new one
		else
			return new E3DVector2F(x / length, y / length);
	}
	
	public void normaliseEqual()
	{
		double length = getLength();
		x /= length;
		y /= length;
	}
	
	public FloatBuffer getFloatBuffer()
	{
		return FloatBuffer.wrap(new float[]{(float)x, (float)y});
	}
	
	public void appendToBuffer(FloatBuffer buffer)
	{
	    buffer.put((float)x);
	    buffer.put((float)y);
	}
	
	public String toString()
	{
		return "(" + x +", " + y + ")";
	}
	
	public boolean equals(Object arg0) 
	{
		if(arg0 == null || !(arg0 instanceof E3DVector2F))
			return false;
		return equals((E3DVector2F)arg0);
	}
	
	public int hashCode() {
		return (int)(x + y);
	}
}

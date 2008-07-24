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

import java.nio.IntBuffer;

import espresso3d.engine.lowlevel.tree.base.IE3DHashableNode;

/**
 * @author espresso3d
 *
 * 2D int-based vector class
 */
public class E3DVector2I implements IE3DHashableNode
{
	private int x, y;
	
	public E3DVector2I()
	{	
		this(0, 0);
	}
	
	public E3DVector2I(E3DVector2I toCopy)
	{
	    if(toCopy == null)
	        return;
	    x = toCopy.x;
	    y = toCopy.y;
	}
	
	public E3DVector2I(int x, int y)
	{
	    this.x = x;
	    this.y = y;
	}

	public boolean equals(E3DVector2I vec)
	{
		return equals(vec.x, vec.y);
	}
	
	public boolean equals(int x, int y)
	{
		if(this.x == x && this.y == y)
			return true;
		else
			return false;
	}
	/**
	 * @return Returns the x.
	 */
	public int getX() {
		return x;
	}
	/**
	 * @param x The x to set.
	 */
	public void setX(int x) {
		this.x = x;
	}
	/**
	 * @return Returns the y.
	 */
	public int getY() {
		return y;
	}
	/**
	 * @param y The y to set.
	 */
	public void setY(int y) {
		this.y = y;
	}

	public void set(E3DVector2I newVec)
	{
	    x = newVec.x;
	    y = newVec.y;
	}
	
	
	public void set(int x, int y)
	{
	    this.x = x;
	    this.y = y;
	}
	
	public E3DVector2I add(E3DVector2I translationAmt)
	{
		return new E3DVector2I(x + translationAmt.x, 
				y + translationAmt.y);
	}
	
	public void addEqual(E3DVector2I translationAmt)
	{
	    x += translationAmt.x;
	    y += translationAmt.y;
	}
	
	public E3DVector2I subtract(E3DVector2I translationAmt)
	{
		return new E3DVector2I(x - translationAmt.x, 
				y - translationAmt.y);
	}
	
	public void subtractEqual(E3DVector2I translationAmt)
	{
	    x -= translationAmt.x;
	    y -= translationAmt.y;
	}	
	
	public E3DVector2I scale(int scaleAmt)
	{
		return new E3DVector2I(x * scaleAmt, y * scaleAmt);
	}
	
	public void scaleEqual(int scaleAmt)
	{
	    x *= scaleAmt;
	    y *= scaleAmt;
	}
	
	public E3DVector2I multiply(E3DVector2I multiplyAmt)
	{
		return new E3DVector2I(x * multiplyAmt.x, y * multiplyAmt.y);		
	}
	
	public void multiplyEqual(E3DVector2I multiplyAmt)
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
	
	public IntBuffer getIntBuffer()
	{
		return IntBuffer.wrap(new int[]{(int)x, (int)y});
	}
	
	public String toString()
	{
		return "(" + x +", " + y + ")";
	}
	
	public boolean equals(Object arg0) 
	{
		if(arg0 == null || !(arg0 instanceof E3DVector2I))
			return false;
		return equals((E3DVector2I)arg0);
	}
	
	public int hashCode() {
		return x + y;
	}
}

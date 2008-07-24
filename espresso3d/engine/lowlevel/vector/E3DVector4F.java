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
package espresso3d.engine.lowlevel.vector;

import espresso3d.engine.common.E3DConstants;
import espresso3d.engine.lowlevel.matrix.E3DQuaternion;
import espresso3d.engine.lowlevel.tree.base.IE3DHashableNode;

/**
 * @author espresso3d
 *
 * 4D floating point vector class.
 */
public class E3DVector4F implements IE3DHashableNode
{
	private double a, b, c, d;
	
	public E3DVector4F()
	{
	    a = 0.0;
	    b = 0.0;
	    c = 0.0;
	    d = 0.0;
	}
	
	public E3DVector4F(double a, double b, double c, double d)
	{
	    this.a = a;
	    this.b = b;
	    this.c = c;
	    this.d = d;
	}
    
    public E3DVector4F(E3DVector4F toCopy)
    {
        if(toCopy == null)
            return;
        this.a = toCopy.a;
        this.b = toCopy.b;
        this.c = toCopy.c;
        this.d = toCopy.d;
    }

	/**
	 * @return Returns the a.
	 */
	public double getA() {
		return a;
	}
	/**
	 * @param a The a to set.
	 */
	public void setA(double a) {
		this.a = a;
	}
	/**
	 * @return Returns the b.
	 */
	public double getB() {
		return b;
	}
	/**
	 * @param b The b to set.
	 */
	public void setB(double b) {
		this.b = b;
	}
	/**
	 * @return Returns the c.
	 */
	public double getC() {
		return c;
	}
	/**
	 * @param c The c to set.
	 */
	public void setC(double c) {
		this.c = c;
	}
	/**
	 * @return Returns the d.
	 */
	public double getD() {
		return d;
	}
	/**
	 * @param d The d to set.
	 */
	public void setD(double d) {
		this.d = d;
	}
	
	public boolean equals(E3DVector4F vec)
	{
		return equals(vec.a, vec.b, vec.c, vec.d);
	}
	
	public boolean equals(double a, double b, double c, double d)
	{
		if(this.a == a && this.b == b && this.c == c && this.d == d)
			return true;
		else
			return false;
	}

	/**
	 * Returns true if the coords are close to the coord passed in
	 * (ie: they are essentially the same 
	 * but could be off by as much as E3DContants.DBL_PRECISION_ERROR)
	 * @param vec
	 * @return
	 */
	public boolean closeTo(E3DVector4F vec)
	{
		return closeTo(vec.a, vec.b, vec.c, vec.d);
	}
	
	public boolean closeTo(double a, double b, double c, double d)
	{
        return (this.a >= a - E3DConstants.DBL_PRECISION_ERROR && this.a <= a + E3DConstants.DBL_PRECISION_ERROR && 
        	    this.b >= b - E3DConstants.DBL_PRECISION_ERROR && this.b <= b + E3DConstants.DBL_PRECISION_ERROR &&
                this.c >= c - E3DConstants.DBL_PRECISION_ERROR && this.c <= c + E3DConstants.DBL_PRECISION_ERROR &&
        	    this.d >= d - E3DConstants.DBL_PRECISION_ERROR && this.d <= d + E3DConstants.DBL_PRECISION_ERROR);        
	}

	public boolean equals(Object arg0) 
	{
		if(arg0 == null || !(arg0 instanceof E3DVector4F))
			return false;
		return equals((E3DVector4F)arg0);
	}
	
	public int hashCode() {
		return (int)(a + b + c + d);
	}
    
    public void set(double a, double b, double c, double d)
    {
        this.a = a;
        this.b = b;
        this.c = c;
        this.d = d;
    }
    
    public E3DVector4F add(E3DVector4F toAdd)
    {
        return new E3DVector4F(a + toAdd.a, b + toAdd.b, c + toAdd.c, d + toAdd.d);
    }

    public void addEqual(E3DVector4F toAdd)
    {
        this.a += toAdd.a;
        this.b += toAdd.b;
        this.c += toAdd.c;
        this.d += toAdd.d;
    }

    public E3DVector4F subtract(E3DVector4F toSubtract)
    {
        return new E3DVector4F(a - toSubtract.a, b - toSubtract.b, c - toSubtract.c, d - toSubtract.d);
    }

    public void subtractEqual(E3DVector4F toSubtract)
    {
        this.a -= toSubtract.a;
        this.b -= toSubtract.b;
        this.c -= toSubtract.c;
        this.d -= toSubtract.d;
    }
    
    public E3DVector4F scale(double scaleAmt)
    {
        return new E3DVector4F(a * scaleAmt, b * scaleAmt, c * scaleAmt, d * scaleAmt);
    }
    
    public void scaleEqual(double scaleAmt)
    {
        this.a *= scaleAmt;
        this.b *= scaleAmt;
        this.c *= scaleAmt;
        this.d *= scaleAmt;
    }

	/**
	 * Get the length of the vector squared.  Actual length needs
	 * takes the sqrt of this value.
	 * @return
	 */
	public double getLengthSquared()
	{
		return (a * a) + (b * b) + (c * c) + (d * d);
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
	 * Get the dot product of thisVec.otherVec
	 * @param otherVec Other vector in the dot product besides this one
	 * @return
	 */
	public double dotProduct(E3DVector4F otherVec)
	{
		return (a * otherVec.a) + (b * otherVec.b) + (c * otherVec.c) + (d * otherVec.d);
	}
	

	/**
	 * Return a new normalised quaternion vector
	 * @return
	 */
	public E3DVector4F normalise()
	{
		E3DVector4F ret = new E3DVector4F(this);
		ret.normaliseEqual();
		return ret;
	}
	
	/**
	 * Normalise this vector
	 */
	public void normaliseEqual()
	{
		double length = getLength();
		if(length == 0.0)
			return;
		scaleEqual(1.0/length);
	}

	public String toString()
	{
		return "(" + a + ", " + b + ", " + c +", " + d +")";
	}

}

/*
 * Created on Jul 18, 2004
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
package espresso3d.engine.lowlevel.geometry;

import java.nio.FloatBuffer;

import espresso3d.engine.E3DEngine;
import espresso3d.engine.lowlevel.vector.E3DVector3F;
import espresso3d.engine.renderer.base.E3DRenderable;
/**
 * @author espresso3d
 *
 * A basic line class used in the engine
 */
public class E3DLine extends E3DRenderable {
	private static final E3DVector3F defaultColor = new E3DVector3F(1.0, 1.0, 1.0);
	private E3DVector3F startColor;
	private E3DVector3F endColor;
	private E3DVector3F startPos;
	private E3DVector3F endPos;
	
	public E3DLine(E3DEngine engine){
		this(engine, null, null, null);
	}
	
	public E3DLine(E3DEngine engine, E3DVector3F startPos, E3DVector3F endPos)
	{
		this(engine, startPos, endPos, defaultColor, defaultColor);
	}
	
	public E3DLine(E3DEngine engine, E3DVector3F startPos, E3DVector3F endPos, E3DVector3F color)
	{
		this(engine, startPos, endPos, color, color);
	}
	
	public E3DLine(E3DEngine engine, E3DVector3F startPos, E3DVector3F endPos, E3DVector3F startColor, E3DVector3F endColor)
	{
		super(engine);
		this.startPos = startPos;
		this.endPos = endPos;
		if(startColor != null)
			this.startColor = startColor;
		else
			this.startColor = new E3DVector3F(1.0, 1.0, 1.0);

		if(endColor != null)
		    this.endColor = endColor;
		else
			this.endColor = new E3DVector3F(1.0, 1.0, 1.0);
	}
	
	/**
	 * @return Returns the endColor.
	 */
	public E3DVector3F getEndColor() {
		return endColor;
	}
	/**
	 * @param endColor The endColor to set.
	 */
	public void setEndColor(E3DVector3F endColor) {
		this.endColor = endColor;
	}
	/**
	 * @return Returns the startColor.
	 */
	public E3DVector3F getStartColor() {
		return startColor;
	}
	/**
	 * @param startColor The startColor to set.
	 */
	public void setStartColor(E3DVector3F startColor) {
		this.startColor = startColor;
	}
	/**
	 * @return Returns the endPos.
	 */
	public E3DVector3F getEndPos() {
		return endPos;
	}
	/**
	 * @param endPos The endPos to set.
	 */
	public void setEndPos(E3DVector3F endPos) {
		this.endPos = endPos;
	}
	/**
	 * @return Returns the startPos.
	 */
	public E3DVector3F getStartPos() {
		return startPos;
	}
	/**
	 * @param startPos The startPos to set.
	 */
	public void setStartPos(E3DVector3F startPos) {
		this.startPos = startPos;
	}
	
    public void appendFloatVertexBuffer(FloatBuffer vertexBuffer)
    {
        vertexBuffer.put((float)getStartPos().getX());
        vertexBuffer.put((float)getStartPos().getY());
        vertexBuffer.put((float)getStartPos().getZ());
        vertexBuffer.put((float)getEndPos().getX());
        vertexBuffer.put((float)getEndPos().getY());
        vertexBuffer.put((float)getEndPos().getZ());
    }
    
    public void appendFloatVertexColorBuffer(FloatBuffer vertexColorBuffer)
    {
        vertexColorBuffer.put((float)getStartColor().getX());
        vertexColorBuffer.put((float)getStartColor().getY());
        vertexColorBuffer.put((float)getStartColor().getZ());
        vertexColorBuffer.put((float)getEndColor().getX());
        vertexColorBuffer.put((float)getEndColor().getY());
        vertexColorBuffer.put((float)getEndColor().getZ());
    }
	
	public String toString()
	{
		return "E3DLine - StartPos: " + getStartPos() + ", EndPos: " + getEndPos();
		
	}

	/**
	 * Returns the closest point in this line segment to the given point
	 * @param point
	 * @return
	 */
	public E3DVector3F getClosestPointToPoint(E3DVector3F point)
	{
		E3DVector3F direction = endPos.subtract(startPos);
		E3DVector3F w = point.subtract(startPos);

		double c1 = w.dotProduct(direction);
		if(c1 <= 0)
			return startPos; //point.subtract(startPos).getLength(); //dotProduct(startPos);
		
		//end being closest
		double c2 = w.dotProduct(w); //dot(v,v);
		if ( c2 <= c1 )
			return endPos; //point.subtract(endPos).getLength(); //d(P, S.P1);

		//soemwhere in the middle
	    double b = c1 / c2;
	    E3DVector3F pointOnLine = startPos.add(direction.scale(b));
	    return pointOnLine; //point.subtract(pB).getLength(); //d(P, Pb);
		
	}
	
	/**
	 * Return the distance the line is from a point
	 * @param point
	 * @return
	 */
	public double getDistanceFromPoint(E3DVector3F point)
	{
		E3DVector3F direction = endPos.subtract(startPos);
		E3DVector3F w = point.subtract(startPos);
		
	//	   Vector v = S.P1 - S.P0;
	//	    Vector w = P - S.P0;
		//start being closest
		double c1 = w.dotProduct(direction);
		if(c1 <= 0)
			return point.subtract(startPos).getLength(); //dotProduct(startPos);
		
		//end being closest
		double c2 = w.dotProduct(w); //dot(v,v);
		if ( c2 <= c1 )
			return point.subtract(endPos).getLength(); //d(P, S.P1);

		//soemwhere in the middle
	    double b = c1 / c2;
	    E3DVector3F pointOnLine = startPos.add(direction.scale(b));
	    return point.subtract(pointOnLine).getLength(); //d(P, Pb);
	}

	/**
	 * Put on the externalrenderable list to display the line
	 */
	public void render() {
        getEngine().getGeometryRenderer().initSolidAndLineRendering();
		getEngine().getGeometryRenderer().renderLine(this);
	}
}

/*
 * Created on Aug 10, 2004
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

import espresso3d.engine.lowlevel.vector.E3DVector3F;
;

/**
 * @author espresso3d
 *
 * A plane class used in the engine
 */
public class E3DPlane {
	private E3DVector3F normal;
	private E3DVector3F[] points;
	
	public E3DPlane(E3DTriangle triangle)
	{
		setNormal(triangle.getNormal());
        points[0] = triangle.getVertexPosA();
        points[1] = triangle.getVertexPosB();
        points[2] = triangle.getVertexPosC();
	}
	
	/**
	 * @return Returns the normal.
	 */
	public E3DVector3F getNormal() {
		return normal;
	}
	/**
	 * @param normal The normal to set.
	 */
	public void setNormal(E3DVector3F normal) {
		this.normal = normal;
	}
	/**
	 * @return Returns the points.
	 */
	public E3DVector3F[] getPoints() {
		return points;
	}
	/**
	 * @param points The points to set.
	 */
	public void setPoints(E3DVector3F[] points) {
		this.points = points;
	}
	
	public E3DVector3F reflectVector(E3DVector3F vectorToReflect)
	{
		return getNormal().scale(2).scale(vectorToReflect.dotProduct(getNormal())).subtract(vectorToReflect).scale(-1);
		
//		Vector3 tmp = p.n * 2 * v.Dot(p.n) - v;

	//	x = tmp.x; y = tmp.y; z = tmp.z;
	}
}

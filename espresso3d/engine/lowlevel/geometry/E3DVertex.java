/*
 * Created on Mar 3, 2005
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
package espresso3d.engine.lowlevel.geometry;

import espresso3d.engine.E3DEngine;
import espresso3d.engine.lowlevel.tree.base.IE3DHashableNode;
import espresso3d.engine.lowlevel.vector.E3DVector3F;
import espresso3d.engine.lowlevel.vector.E3DVector4F;
import espresso3d.engine.renderer.base.E3DRenderable;

/**
 * @author Curt
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class E3DVertex extends E3DRenderable implements IE3DHashableNode
{
    private E3DVector3F vertexPos;
    private E3DVector4F vertexColor; //This is the color of the vertices.  It is changed by things like software per-vertex lighting

    public E3DVertex(E3DEngine engine, E3DVector3F vertexPos, E3DVector4F vertexColor)
    {
        super(engine);
        this.vertexPos = vertexPos;
        this.vertexColor = vertexColor;
    }
    
    
    public void render() {
        // TODO Auto-generated method stub

    }

    public void normaliseVertexColor()
    {
        if(vertexColor.getA() > 1)
            vertexColor.setA(1.0);
        if(vertexColor.getB() > 1)
            vertexColor.setB(1.0);
        if(vertexColor.getC() > 1)
            vertexColor.setC(1.0);
        if(vertexColor.getD() > 1)
            vertexColor.setD(1.0);
    }    
    
    public E3DVector3F getVertexPos() {
        return vertexPos;
    }
    public void setVertexPos(E3DVector3F vertexPos) {
        this.vertexPos = vertexPos;
    }

    /**
     * Gets the vector3F of colors = this is an ABCD = RGBA
     */
    public E3DVector4F getVertexColor() {
        return vertexColor;
    }
    /**
     * Sets the vector3F of colors = this is an ABCD = RGBA
     */
    public void setVertexColor(E3DVector4F vertexColor) {
        this.vertexColor = vertexColor;
    }
    
    public void translate(E3DVector3F translationAmt) {
    	vertexPos.addEqual(translationAmt);
    }
    
    public void rotate(double angle, E3DVector3F aroundVec) {
    	vertexPos.rotateEqual(angle, aroundVec);
    }
    
    public boolean equals(Object arg0) 
    {
    	if(arg0 == null || !(arg0 instanceof E3DVertex))
    		return false;
    	
    	E3DVertex compareVertex = (E3DVertex)arg0;
    	return compareVertex.getVertexColor().equals(getVertexColor()) &&
   			   compareVertex.getVertexPos().equals(getVertexPos());
    }
    
    public int hashCode() {
    	return getVertexPos().hashCode() + getVertexColor().hashCode();
    }
}

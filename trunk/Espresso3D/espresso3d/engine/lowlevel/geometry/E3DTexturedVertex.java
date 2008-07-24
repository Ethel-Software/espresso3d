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
import espresso3d.engine.lowlevel.vector.E3DVector2F;
import espresso3d.engine.lowlevel.vector.E3DVector3F;
import espresso3d.engine.lowlevel.vector.E3DVector4F;

/**
 * @author Curt
 *
 *
 */
public class E3DTexturedVertex extends E3DVertex implements IE3DHashableNode
{
    E3DVector2F textureCoord;
    E3DVector2F textureCoordDetail0;
    E3DVector2F textureCoordDetail1;
    
    public E3DTexturedVertex(E3DEngine engine, E3DVector3F vertexPos, E3DVector4F vertexColor, E3DVector2F textureCoord)
    {
        this(engine, vertexPos, vertexColor, textureCoord, null, null);
    }

    public E3DTexturedVertex(E3DEngine engine, E3DVector3F vertexPos, E3DVector4F vertexColor, E3DVector2F textureCoord, E3DVector2F textureCoordDetail0)
    {
        this(engine, vertexPos, vertexColor, textureCoord, textureCoordDetail0, null);
    }

    public E3DTexturedVertex(E3DEngine engine, E3DVector3F vertexPos, E3DVector4F vertexColor, E3DVector2F textureCoord, E3DVector2F textureCoordDetail0, E3DVector2F textureCoordDetail1)
    {
        super(engine, vertexPos, vertexColor);
        this.textureCoord = textureCoord;
        this.textureCoordDetail0 = textureCoordDetail0;
        this.textureCoordDetail1 = textureCoordDetail1;
    }
    
    public E3DVector2F getTextureCoord() {
        return textureCoord;
    }
    public void setTextureCoord(E3DVector2F textureCoord) {
        this.textureCoord = textureCoord;
    }
    public E3DVector2F getTextureCoordDetail0() {
        return textureCoordDetail0;
    }
    public void setTextureCoordDetail0(E3DVector2F textureCoordDetail0) {
        this.textureCoordDetail0 = textureCoordDetail0;
    }
    public E3DVector2F getTextureCoordDetail1() {
        return textureCoordDetail1;
    }
    public void setTextureCoordDetail1(E3DVector2F textureCoordDetail1) {
        this.textureCoordDetail1 = textureCoordDetail1;
    }
    
    public boolean equals(Object arg0) 
    {
    	if(!super.equals(arg0) || arg0 == null || !(arg0 instanceof E3DTexturedVertex))
    		return false;

    	E3DTexturedVertex compareToVertex = (E3DTexturedVertex)arg0;

    	//First texture coord comparison
    	if(textureCoord != null && compareToVertex.textureCoord == null)
    		return false;
    	else if(textureCoord == null && compareToVertex.textureCoord != null)
    		return false;
    	else //both null or not null
    	{
    		if(textureCoord != null && compareToVertex.textureCoord != null)
    		{
    			if(!textureCoord.equals(compareToVertex.textureCoord))
    				return false;
    		}
    	}
    	
    	if(textureCoordDetail0 != null && compareToVertex.textureCoordDetail0 == null)
    		return false;
    	else if(textureCoordDetail0 == null && compareToVertex.textureCoordDetail0 != null)
    		return false;
    	else //both null or not null
    	{
    		if(textureCoordDetail0 != null && compareToVertex.textureCoordDetail0 != null)
    		{
    			if(!textureCoordDetail0.equals(compareToVertex.textureCoordDetail0))
    				return false;
    		}
    	}
    	
    	if(textureCoordDetail1 != null && compareToVertex.textureCoordDetail1 == null)
    		return false;
    	else if(textureCoordDetail1 == null && compareToVertex.textureCoordDetail1 != null)
    		return false;
    	else //both null or not null
    	{
    		if(textureCoordDetail1 != null && compareToVertex.textureCoordDetail1 != null)
    		{
    			if(!textureCoordDetail1.equals(compareToVertex.textureCoordDetail1))
    				return false;
    		}
    	}
    	
    	return true;
    }
    
    public int hashCode() {
    	return super.hashCode() + 
    			(textureCoord != null ? textureCoord.hashCode() : 0) + 
    			(textureCoordDetail0 != null ? textureCoordDetail0.hashCode() : 0) + 
    			(textureCoordDetail0 != null ? textureCoordDetail1.hashCode() : 0);
    }
}

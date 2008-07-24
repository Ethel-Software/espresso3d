/*
 * Created on Mar 9, 2005
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
package espresso3d.engine.world.sector.mesh;

import java.util.ArrayList;
import java.util.HashMap;

import espresso3d.engine.E3DEngine;
import espresso3d.engine.lowlevel.geometry.E3DTriangle;
import espresso3d.engine.lowlevel.vector.E3DVector3F;
import espresso3d.engine.renderer.base.E3DRenderable;
import espresso3d.engine.world.sector.E3DSector;
import espresso3d.engine.world.sector.actor.E3DActor;
import espresso3d.engine.world.sector.light.base.IE3DLightableObject;
import espresso3d.engine.world.sector.terrain.E3DTerrainPatch;

/**
 * @author Curt
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class E3DMesh extends E3DRenderable implements IE3DLightableObject
{
	private E3DRenderable parentRenderable;
//    private E3DActor actor = null;
    private ArrayList triangleList = null;
//    private E3DSortedRenderableMap textureSortedTriangleMap = null; //contains a hashmap keyed off textureID & each entry has an ArrayList of triangles
    private boolean needingReducedVertexListRecalc = true;
	private boolean needingLightRecalc = true; //When true, it means that this has moved and needs to be relit
	private boolean lit = true;
    private boolean rendered = true;

    private ArrayList reducedVertexList = null;
    
    public E3DMesh(E3DEngine engine, E3DRenderable parentRenderable)
    {
        super(engine);
        triangleList = new ArrayList();
        this.parentRenderable = parentRenderable;
    }

    public E3DMesh(E3DMesh toCopyMesh, E3DRenderable newParentRenderable)
    {
        super(toCopyMesh.getEngine());

        this.parentRenderable = newParentRenderable; 
        this.lit = toCopyMesh.lit;
        this.needingLightRecalc = toCopyMesh.needingLightRecalc;
        this.needingReducedVertexListRecalc = toCopyMesh.needingReducedVertexListRecalc;
        this.rendered = toCopyMesh.rendered;
        
        //This will actually rebuild all the necessary stored information for the triangles
        triangleList = new ArrayList();
        E3DTriangle triangle;
        ArrayList triangleList = toCopyMesh.getTriangleList();
        for(int i=0; i < triangleList.size(); i++)
        {
            triangle = (E3DTriangle)triangleList.get(i);
            
            addTriangle(new E3DTriangle(triangle, newParentRenderable));
        }
    }
    
    public void render() {
    }

    /**
     * Returns a list of unique E3DVector3F vertex positions that make up the mesh
     * @return
     */
    public ArrayList getUniqueVertexPosList()
    {
        HashMap reducedList = new HashMap();
        ArrayList actorTris = getTriangleList();
        E3DVector3F vertex = null;
        E3DTriangle triangle = null;
        
        if(needingReducedVertexListRecalc)
        {
            reducedVertexList = new ArrayList();
            for(int i=0; actorTris != null && i < actorTris.size(); i++)
            {
                triangle = (E3DTriangle)actorTris.get(i);
                
                for(int v=0; v<3; v++)
                {
                    vertex = triangle.getVertexPos(v);
                    if(!reducedList.containsKey(vertex.toString()))
                    {
                        //put it in the map just for lookups, and put it in an arraylist for return if its not there
                        reducedList.put(vertex.toString(), vertex); 
                        reducedVertexList.add(vertex);
                    }
                }
                
            }
            needingReducedVertexListRecalc = false;
        }
        return reducedVertexList;
    }
    
    public ArrayList getTriangleList()
    {
        return triangleList;
    }
    
    public void addTriangle(E3DTriangle triangle)
    {
    	triangle.setParentObject(parentRenderable);
        triangleList.add(triangle);
        
        if(parentRenderable instanceof E3DActor)
        	((E3DActor)parentRenderable).getRenderTree().getActorHandler().addTriangle((E3DActor)parentRenderable, triangle);
        else if(parentRenderable instanceof E3DSector)
        	((E3DSector)parentRenderable).getRenderTree().getTriangleHandler().add(triangle);
        	
        needingReducedVertexListRecalc = true;
		needingLightRecalc = true;
    }
    
    public void addTriangles(ArrayList triangles)
    {
        triangleList.addAll(triangles);
        for(int i=0; i < triangles.size(); i++)
        {
        	E3DTriangle triangle = (E3DTriangle)triangles.get(i);
        	addTriangle(triangle);
        }
    }
    

    public void removeTriangle(E3DTriangle triangle)
    {
    	triangle.setParentObject(null);
    	triangleList.remove(triangle);
    	
        if(parentRenderable instanceof E3DActor)
        	((E3DActor)parentRenderable).getRenderTree().getActorHandler().removeTriangle(triangle);
        else if(parentRenderable instanceof E3DSector)
        	((E3DSector)parentRenderable).getRenderTree().getTriangleHandler().remove(triangle);
    	
    	needingReducedVertexListRecalc = true;
    }
    
    public void removeAllTriangles()
    {
    	if(parentRenderable instanceof E3DActor)
    		((E3DActor)parentRenderable).getRenderTree().getActorHandler().remove((E3DActor)parentRenderable); //removing them all
    	else if(parentRenderable instanceof E3DSector)
    		((E3DSector)parentRenderable).getRenderTree().getSectorHandler().remove((E3DSector)parentRenderable); //removing them all
    		
    	triangleList.clear();
    	needingReducedVertexListRecalc = true;
    }
    
    public void scale(double scaleAmt)
    {
        E3DTriangle triangle;
        for(int i=0; i < triangleList.size(); i++)
        {
            triangle = (E3DTriangle)triangleList.get(i);
            triangle.scale(scaleAmt);
        }
		needingLightRecalc = true;
    }
    
    public void translate(E3DVector3F translationAmt)
    {
        E3DTriangle triangle;
        for(int i=0; i < triangleList.size(); i++)
        {
            triangle = (E3DTriangle)triangleList.get(i);
            triangle.translate(translationAmt);
        }
		needingLightRecalc = true;
    }
    
    public void rotate(double angle, E3DVector3F aroundVec, E3DVector3F position)
    {
        E3DTriangle triangle;
        for(int i=0; i < triangleList.size(); i++)
        {
            triangle = (E3DTriangle)triangleList.get(i);
            triangle.rotate(angle, aroundVec, position);
        }
		needingLightRecalc = true;
    }

	public boolean isNeedingLightRecalc() {
		return needingLightRecalc;
	}

	public void setNeedingLightRecalc(boolean needingLightRecalc) {
		this.needingLightRecalc = needingLightRecalc;
	}

	public E3DRenderable getParentRenderable() {
		return parentRenderable;
	}

	public void setParentRenderable(E3DRenderable parentRenderable) {
		this.parentRenderable = parentRenderable;
	}
	
	public boolean isLit() {
		return lit;
	}
	
	public void setLit(boolean lit) {
		this.lit = lit;
        if(!lit) //change everything to fullbright
        {
            E3DTriangle triangle = null;
            for(int i=0; i < triangleList.size(); i++)
            {
                triangle = (E3DTriangle)triangleList.get(i);
                triangle.resetVertexColor(1.0);
            }
        }
	}

	public boolean isRendered() {
		return rendered;
	}

	public void setRendered(boolean meshDisplayed) {
		this.rendered = meshDisplayed;
	}
    
    /**
     * Sets this alpha to all triangles in the mesh
     * @param alpha
     */
    public void setAlpha(double alpha)
    {
        E3DTriangle triangle;
        ArrayList triangleList = this.triangleList;
        for(int i=0; i < triangleList.size(); i++)
        {
            triangle = (E3DTriangle)triangleList.get(i);
            triangle.getVertexColorA().setD(alpha);
        }
    }

}

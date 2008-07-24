/*
 
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
 * 
 */
package espresso3d.engine.renderer.rendertree.nodes.leaf;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import espresso3d.engine.E3DEngine;
import espresso3d.engine.lowlevel.geometry.E3DLine;
import espresso3d.engine.lowlevel.geometry.E3DTriangle;
import espresso3d.engine.lowlevel.tree.E3DTreeNode;
import espresso3d.engine.lowlevel.vector.E3DVector3F;
import espresso3d.engine.renderer.rendertree.E3DRenderTree;
import espresso3d.engine.renderer.rendertree.nodes.base.E3DRenderTreeNode;

public class E3DTriangleNode extends E3DRenderTreeNode
{
	public static final int NODE_LEVEL = 5;

	private HashMap triangleMap;
	private ArrayList triangleList;
	
//	public E3DTriangleNode(E3DEngine engine, E3DRenderTree tree)
//	{
//		super(engine, tree, NODE_LEVEL);
//		triangleMap = new HashMap();
//		triangleList = new ArrayList();
//		
//	}
	
	/**
	 * Only use if you plan on setting tree and triangle/or triangle list later
	 */
	public E3DTriangleNode(E3DEngine engine)
	{
		super(engine, null, NODE_LEVEL);
		init(engine);
	}

	public E3DTriangleNode(E3DEngine engine, E3DRenderTree tree, E3DTriangle initialTriangle, E3DTreeNode parentNode)
	{
		super(engine, tree, NODE_LEVEL);
		setParentNode(parentNode);
		init(engine, initialTriangle);
	}
	
	public E3DTriangleNode(E3DEngine engine, E3DRenderTree tree, E3DTriangle initialTriangle)
	{
		super(engine, tree, NODE_LEVEL);
		init(engine, initialTriangle);
	}

	public E3DTriangleNode(E3DEngine engine, E3DRenderTree tree, ArrayList initialTriangles, E3DTreeNode parentNode)
	{
		super(engine, tree, NODE_LEVEL);
		setParentNode(parentNode);
		init(engine, initialTriangles);
	}

	public E3DTriangleNode(E3DEngine engine, E3DRenderTree tree, ArrayList initialTriangles)
	{
		super(engine, tree, NODE_LEVEL);
		init(engine, initialTriangles);
	}
	
	private void init(E3DEngine engine)
	{
		triangleMap = new HashMap();
		triangleList = new ArrayList();
	}
	
	private void init(E3DEngine engine, E3DTriangle triangle)
	{
		init(engine);
		addTriangleToNode(triangle);
	}
	private void init(E3DEngine engine, ArrayList initialTriangles)
	{
		init(engine);
		
		if(initialTriangles == null)
			return;
		E3DTriangle triangle;
		for(int i=0; i < initialTriangles.size(); i++)
		{
			triangle = (E3DTriangle)initialTriangles.get(i);
			addTriangleToNode(triangle);
		}
		
	}
	public E3DTreeNode getClone() 
	{
		E3DTriangleNode triangleNode = new E3DTriangleNode(getEngine(), (E3DRenderTree)getTree(), triangleList);
		E3DTriangleNode newParentTriangleNode = (E3DTriangleNode)getTree().addNode(triangleNode, getParentNode());
//		if(triangleNode != newParentTriangleNode) //found the parent node of the quad

		E3DTriangle triangle;
		for(int i=0; i < triangleList.size(); i++)
		{
			triangle = (E3DTriangle)triangleList.get(i);
			getTree().addObjectToLeaf(newParentTriangleNode, triangle); //found it, add the triangle
		}
	
		return newParentTriangleNode;
//		return new E3DTriangleNode(getEngine(), (E3DRenderTree)getTree(), getTriangleList());
	}
	
	protected void applyPreRenderOptions()
	{
		getEngine().getGeometryRenderer().renderTriangleList(getEngine(), getTriangleList(), getRenderMode(this.getParentNode()));
		
		if(getEngine().getDebugFlags().isDebugNormalsRendered())
		{
			ArrayList lineList = new ArrayList();
			for(int i=0; i < getTriangleList().size(); i++)
			{
				E3DTriangle triangle = (E3DTriangle)getTriangleList().get(i);
				
				lineList.add(new E3DLine(getEngine(), triangle.getCentroid(),  triangle.getCentroid().add(triangle.getNormal()), new E3DVector3F(1.0, 0, 0)));
				
			}
			
			getEngine().getGeometryRenderer().renderLineList(lineList);
		}
	}

	protected void applyPostRenderOptions() {
	}
	
	public void addTriangleToNode(E3DTriangle triangle)
	{
		if(!triangleMap.containsKey(triangle))
		{
			triangleMap.put(triangle, triangle);
			triangleList.add(triangle);
		}
	}

	private boolean removeTriangleFromNode(E3DTriangle triangle)
	{
		if(!triangleMap.containsKey(triangle))
			return true;
		
		triangleMap.remove(triangle);
		triangleList.remove(triangle);
		return !triangleMap.isEmpty();
	}
	
	/**
	 * Returns false if its empty and was removed
	 * @param triangleList
	 * @return
	 */
	public boolean removeTrianglesFromNode(ArrayList triangleList)
	{
		for(int i=0; i < triangleList.size(); i++)
		{
			if(!removeTriangleFromNode((E3DTriangle)triangleList.get(i)))
				return false;
		}
//		triangleListChanged = true;
		return true;
	}
	
	public ArrayList getTriangleList()
	{
		if(triangleList.isEmpty())
			return null;
//		if(triangleMap == null || triangleMap.isEmpty())
//			return null;
		
//		if(triangleListChanged)
//		{
//			triangleList.clear();
//			Iterator it = triangleMap.entrySet().iterator();
//			Map.Entry entry;
//			while(it.hasNext())
//			{
//				entry = (Map.Entry)it.next();
//				triangleList.add((E3DTriangle)entry.getValue());
//			}
//			triangleListChanged = false;
//		}
		return triangleList;
	}
	
	public boolean equals(Object arg0) 
	{
		if(arg0 != null && arg0 instanceof E3DTriangleNode)
			return true;
		return false;
	}

	public int hashCode() {
		return 1;
	}

	public HashMap getTriangleMap() {
		return triangleMap;
	}

	public boolean isLeaf() {
		return true;
	}
	
	public List getLeafObjects() {
		return triangleList;
	}

	public void addLeafObject(Object leafObject) 
	{
		addTriangleToNode((E3DTriangle)leafObject);
	}
	
	public boolean removeLeafObject(Object leafObject) 
	{
		return removeTriangleFromNode((E3DTriangle)leafObject);
	}
	
	public void clear()
	{
		if(triangleList == null || triangleList.isEmpty())
			return;
		
		removeTrianglesFromNode(triangleList);
	}

}

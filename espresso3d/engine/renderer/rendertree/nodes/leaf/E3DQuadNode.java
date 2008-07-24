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
import espresso3d.engine.lowlevel.geometry.E3DQuad;
import espresso3d.engine.lowlevel.geometry.E3DTriangle;
import espresso3d.engine.lowlevel.tree.E3DTreeNode;
import espresso3d.engine.renderer.rendertree.E3DRenderTree;
import espresso3d.engine.renderer.rendertree.nodes.base.E3DRenderTreeNode;

public class E3DQuadNode extends E3DRenderTreeNode
{
	public static final int NODE_LEVEL = 5;

	public ArrayList quadList;
	private HashMap quadMap;

//	public E3DQuadNode(E3DEngine engine, E3DRenderTree tree)
//	{
//		super(engine, tree, NODE_LEVEL);
//		quadList = new ArrayList();
//		quadMap = new HashMap();
//	}

	public E3DQuadNode(E3DEngine engine, E3DRenderTree tree, E3DQuad initialQuad)
	{
		super(engine, tree, NODE_LEVEL);
		quadList = new ArrayList();
		quadMap = new HashMap();
		addQuadToNode(initialQuad);
	}
	
	public E3DQuadNode(E3DEngine engine, E3DRenderTree tree, ArrayList initialQuads)
	{
		super(engine, tree, NODE_LEVEL);
		if(initialQuads == null)
			return;
		quadMap = new HashMap();
		quadList = new ArrayList();
		E3DQuad quad;
		for(int i=0; i < initialQuads.size(); i++)
		{
			quad = (E3DQuad)initialQuads.get(i);
			addQuadToNode(quad);
		}
	}	

	public E3DTreeNode getClone() 
	{
		E3DQuadNode quadNode = new E3DQuadNode(getEngine(), (E3DRenderTree)getTree(), quadList);
		E3DQuadNode newParentQuadNode = (E3DQuadNode)getTree().addNode(quadNode, getParentNode());

		E3DQuad quad;
		for(int i=0; i < quadList.size(); i++)
		{
			quad = (E3DQuad)quadList.get(i);
			getTree().addObjectToLeaf(newParentQuadNode, quad); //found it, add the triangle
		}
	
		return newParentQuadNode;

//		return new E3DQuadNode(getEngine(), (E3DRenderTree)getTree(), quadList);
	}
	
	protected void applyPreRenderOptions()
	{
		getEngine().getGeometryRenderer().renderQuadList(getEngine(), getQuadList(), getRenderMode(this.getParentNode()));
	}
	protected void applyPostRenderOptions() {
	}

	private void addQuadToNode(E3DQuad quad)
	{
		if(!quadMap.containsKey(quad))
		{
			quadMap.put(quad, quad);
			quadList.add(quad);
		}
	}

	/**
	 * Returns false if its the last quad in the quadmap for this node (ie: remove it)
	 * @param quad
	 * @return
	 */
	private boolean removeQuadFromNode(E3DQuad quad)
	{
		if(!quadMap.containsKey(quad))
			return true;
		
		quadMap.remove(quad);
		quadList.remove(quad);
		return !quadMap.isEmpty();
	}

	public boolean removeQuadsFromNode(ArrayList quadList)
	{
		for(int i=0; i < quadList.size(); i++)
		{
			if(!removeQuadFromNode((E3DQuad)quadList.get(i)))
				return false;
		}
//		quadListChanged = true;
		return true;
	}
	
	public boolean equals(Object arg0) 
	{
		if(arg0 != null && arg0 instanceof E3DQuadNode)
			return true;
		return false;
	}

	public int hashCode() {
		return 0;
	}

	public ArrayList getQuadList()
	{
		if(quadList.isEmpty())
			return null;
//		if(quadMap == null || quadMap.isEmpty())
//			return null;
		
//		if(quadListChanged)
//		{
//			quadList.clear();
//			Iterator it = quadMap.entrySet().iterator();
//			Map.Entry entry;
//			while(it.hasNext())
//			{
//				entry = (Map.Entry)it.next();
//				quadList.add((E3DQuad)entry.getValue());
//			}
//			quadListChanged = false;
//		}
		return quadList;
	}

	public boolean isLeaf() {
		return true;
	}
	
	public List getLeafObjects() {
		return quadList;
	}
	
	public void addLeafObject(Object leafObject) 
	{
		addQuadToNode((E3DQuad)leafObject);
	}
	
	public boolean removeLeafObject(Object leafObject) 
	{
		return removeQuadFromNode((E3DQuad)leafObject);
	}
	
	public void clear()
	{
		if(quadList == null || quadList.isEmpty())
			return;
		
		removeQuadsFromNode(quadList);
	}


}

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
package espresso3d.engine.renderer.rendertree.handler;

import java.util.ArrayList;

import espresso3d.engine.E3DEngine;
import espresso3d.engine.lowlevel.geometry.E3DTriangle;
import espresso3d.engine.lowlevel.tree.E3DTreeNode;
import espresso3d.engine.renderer.base.E3DTexturedRenderable;
import espresso3d.engine.renderer.rendertree.E3DRenderTree;
import espresso3d.engine.renderer.rendertree.handler.base.E3DRenderTreeHandler;
import espresso3d.engine.renderer.rendertree.nodes.leaf.E3DTriangleNode;

public class E3DRenderTreeTriangleHandler extends E3DRenderTreeHandler 
{
	public E3DRenderTreeTriangleHandler(E3DEngine engine, E3DRenderTree renderTree)
	{
		super(engine, renderTree);
	}
	
	public E3DTreeNode add(Object triangleObj)
	{
		if(triangleObj == null || !(triangleObj instanceof E3DTriangle))
			return null;
		E3DTriangle triangle = (E3DTriangle)triangleObj;
		E3DTreeNode currentParent = addRenderable((E3DTexturedRenderable)triangle);
		return addTriangle(triangle, currentParent);
	}
	
	public E3DTreeNode add(E3DTriangle triangle)
	{
		if(triangle == null)
			return null;
		E3DTreeNode currentParent = addRenderable((E3DTexturedRenderable)triangle);
		return addTriangle(triangle, currentParent);
	}

	public void addAll(ArrayList triangles)
	{
		if(triangles == null || triangles.isEmpty())
			return;
		for(int i=0; i < triangles.size(); i++)
			add((E3DTriangle)triangles.get(i));
	}

	public E3DTreeNode addTriangle(E3DTriangle triangle, E3DTreeNode currentParent)
	{
		return addTriangleNode(triangle, currentParent);
	}
	
	protected E3DTreeNode addTriangleNode(E3DTriangle triangle, E3DTreeNode parentNode)
	{
		E3DTriangleNode triangleNode = new E3DTriangleNode(getEngine(), getRenderTree(), triangle);
		E3DTriangleNode newParentTriangleNode = (E3DTriangleNode)renderTree.addNode(triangleNode, parentNode);
		if(triangleNode != newParentTriangleNode) //found the parent node of the quad
			renderTree.addObjectToLeaf(newParentTriangleNode, triangle); //found it, add the triangle
		return newParentTriangleNode;
	}	

	public boolean remove(Object triangleObj)
	{
		if(triangleObj == null || !(triangleObj instanceof E3DTriangle))
			return false;

		E3DTriangle triangle = (E3DTriangle)triangleObj;
		return remove(triangle);
	}	
	
	public boolean remove(E3DTriangle triangle)
	{
		E3DTriangleNode triangleNode = (E3DTriangleNode)renderTree.findLeafNodeForObject(triangle);
        if(triangleNode == null)
            return false;
        getRenderTree().removeObjectFromLeaf(triangleNode, triangle);
        return true;
	}

	public void rehash(Object triangleObj) {
		remove(triangleObj);
		add(triangleObj);
	}
}

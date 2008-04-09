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
import espresso3d.engine.lowlevel.geometry.E3DQuad;
import espresso3d.engine.lowlevel.geometry.E3DTriangle;
import espresso3d.engine.lowlevel.tree.E3DTreeNode;
import espresso3d.engine.renderer.base.E3DTexturedRenderable;
import espresso3d.engine.renderer.rendertree.E3DRenderTree;
import espresso3d.engine.renderer.rendertree.handler.base.E3DRenderTreeHandler;
import espresso3d.engine.renderer.rendertree.nodes.leaf.E3DQuadNode;
import espresso3d.engine.renderer.rendertree.nodes.leaf.E3DTriangleNode;

public class E3DRenderTreeQuadHandler extends E3DRenderTreeHandler {

	public E3DRenderTreeQuadHandler(E3DEngine engine, E3DRenderTree renderTree)
	{
		super(engine, renderTree);
	}
	
	public E3DTreeNode add(Object quadObj)
	{
		if(quadObj == null || !(quadObj instanceof E3DQuad))
			return null;
		E3DQuad quad = (E3DQuad)quadObj;
		E3DTreeNode currentParent = addRenderable((E3DTexturedRenderable)quad);
		return addQuad(quad, currentParent);
	}

	public E3DTreeNode add(E3DQuad quad)
	{
		if(quad == null)
			return null;
		E3DTreeNode currentParent = addRenderable((E3DTexturedRenderable)quad);
		return addQuad(quad, currentParent);
	}

	public void addAll(ArrayList quads)
	{
		if(quads == null || quads.isEmpty())
			return;
		for(int i=0; i < quads.size(); i++)
			add((E3DQuad)quads.get(i));
	}
	
	public E3DTreeNode addQuad(E3DQuad quad, E3DTreeNode currentParent)
	{
		return addQuadNode(quad, currentParent);
	}
	
	private E3DTreeNode addQuadNode(E3DQuad quad, E3DTreeNode parentNode)
	{
		E3DQuadNode quadNode = new E3DQuadNode(getEngine(), getRenderTree(), quad);
		E3DQuadNode newParentQuadNode = (E3DQuadNode)renderTree.addNode(quadNode, parentNode);
		if(quadNode != newParentQuadNode) //found the parent node of the quad
			renderTree.addObjectToLeaf(newParentQuadNode, quad); //found it, add the triangle
		return newParentQuadNode;
	}	

	public boolean remove(Object quadObj)
	{
		if(quadObj == null || !(quadObj instanceof E3DQuad))
			return false;

		return remove((E3DQuad)quadObj);
	}	
	
	public boolean remove(E3DQuad quad)
	{
		E3DQuadNode quadNode = (E3DQuadNode)renderTree.findLeafNodeForObject(quad);
        if(quadNode == null)
            return false;
        getRenderTree().removeObjectFromLeaf(quadNode, quad);
        return true;
	}
	
	public void rehash(Object quadObj) {
		remove(quadObj);
		add(quadObj);
	}
}

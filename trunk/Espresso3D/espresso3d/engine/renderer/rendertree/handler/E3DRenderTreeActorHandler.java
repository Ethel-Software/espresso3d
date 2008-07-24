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
import java.util.List;

import espresso3d.engine.E3DEngine;
import espresso3d.engine.lowlevel.geometry.E3DTriangle;
import espresso3d.engine.lowlevel.matrix.E3DOrientation;
import espresso3d.engine.lowlevel.tree.E3DTreeNode;
import espresso3d.engine.renderer.rendertree.E3DRenderTree;
import espresso3d.engine.renderer.rendertree.handler.base.E3DRenderTreeHandler;
import espresso3d.engine.renderer.rendertree.nodes.leaf.E3DTriangleNode;
import espresso3d.engine.world.sector.actor.E3DActor;

public class E3DRenderTreeActorHandler extends E3DRenderTreeHandler
{
	public E3DRenderTreeActorHandler(E3DEngine engine, E3DRenderTree renderTree)
	{
		super(engine, renderTree);
	}

	/**
	 * Actors
	 * @param actor
	 */
	public E3DTreeNode add(Object actorObj)
	{
		if(actorObj == null || !(actorObj instanceof E3DActor))
			return null;
		
		E3DActor actor = (E3DActor)actorObj;
		return add(actor);
	}
	
	public E3DTreeNode add(E3DActor actor)
	{
		if(actor.getMesh() == null)
			return null;
		
		ArrayList triangleList = actor.getMesh().getTriangleList();
		if(triangleList == null || triangleList.isEmpty())
			return null;

		E3DOrientation orientation = actor.getOrientation();
		E3DTreeNode currentParent = null;
		
		E3DTriangle triangle;
		for(int i=0; i < triangleList.size(); i++)
		{
			triangle = (E3DTriangle)triangleList.get(i);
			currentParent = addRenderable(triangle);
			currentParent = addOrientationNode(orientation, currentParent);
			currentParent = renderTree.getTriangleHandler().addTriangle(triangle, currentParent);
		}
		
		return null;
	}

	public E3DTreeNode addTriangle(E3DActor actor, E3DTriangle triangle)
	{
		if(actor == null || triangle == null)
			return null;
		
		E3DOrientation orientation = actor.getOrientation();
		E3DTreeNode currentParent = addRenderable(triangle);
		currentParent = addOrientationNode(orientation, currentParent);
		return renderTree.getTriangleHandler().addTriangle(triangle, currentParent);
	}
	public boolean remove(Object actorObj)
	{
		if(actorObj == null)
			return false;
		return remove((E3DActor)actorObj);
	}	
	
	public boolean remove(E3DActor actor)
	{
		if(actor == null)
			return false;
		
		List triangleList = actor.getMesh().getTriangleList();
		for(int i=0; i < triangleList.size(); i++)
			removeTriangle((E3DTriangle)triangleList.get(i));
		return true;
	}
	
	public void removeTriangle(E3DTriangle triangle)
	{
		if(triangle == null)
			return;
		E3DTriangleNode triangleNode = (E3DTriangleNode)renderTree.findLeafNodeForObject(triangle);
		renderTree.removeObjectFromLeaf(triangleNode, triangle);
	}

	public void rehash(Object actorObj)
	{
		if(actorObj != null && actorObj instanceof E3DActor)
			rehash((E3DActor)actorObj);
	}	

	public void rehash(E3DActor actor)
	{
		remove(actor);
		add(actor);
	}	

	public void rehashTriangle(E3DActor parentActor, E3DTriangle triangle)
	{
		removeTriangle(triangle);
		addTriangle(parentActor, triangle);
	}
}
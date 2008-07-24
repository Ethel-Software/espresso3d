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

import espresso3d.engine.E3DEngine;
import espresso3d.engine.lowlevel.geometry.E3DQuad;
import espresso3d.engine.lowlevel.tree.E3DTreeNode;
import espresso3d.engine.renderer.rendertree.E3DRenderTree;
import espresso3d.engine.renderer.rendertree.handler.base.E3DRenderTreeHandler;
import espresso3d.engine.renderer.rendertree.nodes.leaf.E3DQuadNode;
import espresso3d.engine.world.sector.particle.E3DSprite;

public class E3DRenderTreeSpriteHandler extends E3DRenderTreeHandler 
{
	public E3DRenderTreeSpriteHandler(E3DEngine engine, E3DRenderTree renderTree)
	{
		super(engine, renderTree);
	}

	public E3DTreeNode add(Object spriteObj)
	{
		if(spriteObj == null || !(spriteObj instanceof E3DSprite))
			return null;
		E3DSprite sprite = (E3DSprite)spriteObj;
		return add(sprite);
	}
	
	public E3DTreeNode add(E3DSprite sprite)
	{
		if(sprite.getQuad() == null)
			return null;
		
		E3DQuad quad = sprite.getQuad();
		E3DTreeNode currentParent = null;
		
		currentParent = addRenderable(quad);
		currentParent = addOrientationNode(sprite.getOrientation(), currentParent);
		currentParent = renderTree.getQuadHandler().addQuad(quad, currentParent);
		return currentParent;
	}
	
	public boolean remove(Object spriteObj) 
	{
		if(spriteObj == null || !(spriteObj instanceof E3DSprite))
			return false;
		
		E3DSprite sprite = (E3DSprite)spriteObj;
		return remove(sprite);
	}
	
	public boolean remove(E3DSprite sprite)
	{
		E3DQuadNode quadNode = (E3DQuadNode)renderTree.findLeafNodeForObject(sprite.getQuad());
		if(quadNode == null)
			return false;
		renderTree.removeObjectFromLeaf(quadNode, sprite.getQuad());
		return true;
	}
	
	public void rehash(Object spriteObj)
	{
		remove(spriteObj);
		add(spriteObj);
	}	
}

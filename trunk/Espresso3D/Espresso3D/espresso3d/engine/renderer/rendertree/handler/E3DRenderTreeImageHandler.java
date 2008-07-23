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
import espresso3d.engine.lowlevel.tree.E3DTreeNode;
import espresso3d.engine.renderer.base.E3DAnimatedTextureRenderable;
import espresso3d.engine.renderer.rendertree.E3DRenderTree;
import espresso3d.engine.renderer.rendertree.handler.base.E3DRenderTreeHandler;
import espresso3d.engine.renderer.rendertree.nodes.leaf.E3DImageNode;
import espresso3d.engine.window.viewport.image.E3DImage;

public class E3DRenderTreeImageHandler extends E3DRenderTreeHandler 
{
	public E3DRenderTreeImageHandler(E3DEngine engine, E3DRenderTree renderTree)
	{
		super(engine, renderTree);
	}
	
	public boolean remove(Object imageObj) 
	{
		if(imageObj == null || !(imageObj instanceof E3DImage))
			return false;

		E3DImage image = (E3DImage)imageObj;
		E3DImageNode imageNode = (E3DImageNode)renderTree.findLeafNodeForObject(image);
        if(imageNode == null)
        	return false;
        getRenderTree().removeObjectFromLeaf(imageNode, image);
        return true;
	}

	public E3DTreeNode add(Object imageObj)
	{
		if(imageObj == null || !(imageObj instanceof E3DImage))
			return null;
		E3DImage image = (E3DImage)imageObj;
		E3DTreeNode currentParent = addRenderable((E3DAnimatedTextureRenderable)image);
		return renderTree.getImageHandler().add(image, currentParent);
	}

	public E3DTreeNode add(E3DImage image, E3DTreeNode currentParent)
	{
		return addImageNode(image, currentParent);
	}
	
	private E3DTreeNode addImageNode(E3DImage image, E3DTreeNode parentNode)
	{
		E3DImageNode imageNode = new E3DImageNode(getEngine(), getRenderTree(), image);
		E3DImageNode newParentImageNode = (E3DImageNode)renderTree.addNode(imageNode, parentNode);
		if(imageNode != newParentImageNode) //found the parent node of the quad
			renderTree.addObjectToLeaf(newParentImageNode, image); //found it, add the triangle
		return newParentImageNode;
	}	

	public void rehash(Object imageObj) {
		remove(imageObj);
		add(imageObj);
	}
}

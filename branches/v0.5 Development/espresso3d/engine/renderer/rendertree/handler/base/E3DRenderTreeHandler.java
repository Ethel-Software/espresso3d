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
 */
package espresso3d.engine.renderer.rendertree.handler.base;

import espresso3d.engine.E3DEngine;
import espresso3d.engine.base.E3DEngineItem;
import espresso3d.engine.lowlevel.matrix.E3DOrientation;
import espresso3d.engine.lowlevel.tree.E3DTreeNode;
import espresso3d.engine.renderer.base.E3DAnimatedTextureRenderable;
import espresso3d.engine.renderer.base.E3DBlendMode;
import espresso3d.engine.renderer.base.E3DRenderMode;
import espresso3d.engine.renderer.base.E3DRenderable;
import espresso3d.engine.renderer.base.E3DTexturedRenderable;
import espresso3d.engine.renderer.rendertree.E3DRenderTree;
import espresso3d.engine.renderer.rendertree.nodes.E3DBlendModeNode;
import espresso3d.engine.renderer.rendertree.nodes.E3DNonTextureNode;
import espresso3d.engine.renderer.rendertree.nodes.E3DOrientationNode;
import espresso3d.engine.renderer.rendertree.nodes.E3DRenderModeNode;
import espresso3d.engine.renderer.rendertree.nodes.E3DRenderRootNode;
import espresso3d.engine.renderer.rendertree.nodes.E3DTextureNode;
import espresso3d.engine.renderer.texture.E3DTexture;

public abstract class E3DRenderTreeHandler extends E3DEngineItem
{
	protected E3DRenderTree renderTree;
	public E3DRenderTreeHandler(E3DEngine engine, E3DRenderTree renderTree)
	{
		super(engine);
		this.renderTree = renderTree;
	}
	
	protected E3DTreeNode addRenderable(E3DAnimatedTextureRenderable renderable)
	{
		//do 1 and 2
		E3DTreeNode currentParent = addRenderable((E3DRenderable)renderable);
		
		//3 Add texture node
		if(renderable.getTexture() != null)
			currentParent = addTextureNode(renderable.getTexture(), currentParent);

		//4 Add Detailtexture node
        if(renderable.getTextureDetail0() != null)
            currentParent = addTextureNode(renderable.getTextureDetail0(), currentParent);

		//5 Add Detail texture node
        if(renderable.getTextureDetail1() != null)
            currentParent = addTextureNode(renderable.getTextureDetail1(), currentParent);
		
		return currentParent;
		
	}
	
	protected E3DTreeNode addRenderable(E3DTexturedRenderable renderable)
	{
		//do 1 and 2
		E3DTreeNode currentParent = addRenderable((E3DRenderable)renderable);

		//3 Add texture node (if textured)
		if(renderable.getRenderMode().getRenderMode() == E3DRenderMode.RENDERMODE_TEXTURED)
		{
			if(renderable.getTexture() != null)
				currentParent = addTextureNode(renderable.getTexture(), currentParent);
	
			//4 Add Detailtexture node
			if(renderable.getTextureDetail0() != null)
				currentParent = addTextureNode(renderable.getTextureDetail0(), currentParent);
	
			//5 Add Detail texture node
			if(renderable.getTextureDetail1() != null)
				currentParent = addTextureNode(renderable.getTextureDetail1(), currentParent);
		}
		else
			currentParent = addNonTextureNode(currentParent);
		
		return currentParent;
	}

	/**
	 * Adds it and returns the current parent
	 * @param renderable
	 * @return
	 */
	protected E3DTreeNode addRenderable(E3DRenderable renderable)
	{
		E3DTreeNode currentParent = addRootNode();
		
		//Level 1: RenderMode
		currentParent = addRenderMode(renderable, currentParent);
		
		//Level2: BlendMode
		currentParent = addBlendMode(renderable, currentParent);
		
		return currentParent;
	}
	
	protected E3DTreeNode addRootNode()
	{
		if(getRenderTree().getRootNode() == null)
			getRenderTree().setRootNode(new E3DRenderRootNode(getEngine(), getRenderTree()));
		return getRenderTree().getRootNode();
	}
	/**
	 * Adds or finds the render mode and returns the parent
	 * @param renderable
	 * @return
	 */
	protected E3DTreeNode addRenderMode(E3DRenderable renderable, E3DTreeNode parentNode)
	{
		E3DRenderMode renderMode = renderable.getRenderMode();
		E3DRenderModeNode renderModeNode = new E3DRenderModeNode(getEngine(), getRenderTree(), renderMode);
		return getRenderTree().addNode(renderModeNode, parentNode);
	}
	
	protected E3DTreeNode addBlendMode(E3DRenderable renderable, E3DTreeNode parentNode)
	{
		E3DBlendMode blendMode = renderable.getBlendMode();
		E3DBlendModeNode blendModeNode = new E3DBlendModeNode(getEngine(), getRenderTree(), blendMode);
		return getRenderTree().addNode(blendModeNode, parentNode);
	}	
	
	protected E3DTreeNode addTextureNode(E3DTexture texture, E3DTreeNode parentNode)
	{
		E3DTextureNode textureNode = new E3DTextureNode(getEngine(), getRenderTree(), texture);
		return getRenderTree().addNode(textureNode, parentNode);
	}	
	
	protected E3DTreeNode addNonTextureNode(E3DTreeNode parentNode)
	{
		E3DNonTextureNode nonTextureNode = new E3DNonTextureNode(getEngine(), getRenderTree());
		return getRenderTree().addNode(nonTextureNode, parentNode);
	}	
	
	protected E3DTreeNode addOrientationNode(E3DOrientation orientation, E3DTreeNode parentNode)
	{
		E3DOrientationNode orientationNode = new E3DOrientationNode(getEngine(), getRenderTree(), orientation);
		return getRenderTree().addNode(orientationNode, parentNode);
	}	

	
	public E3DRenderTree getRenderTree()
	{
		return renderTree;
	}
	
	abstract public boolean remove(Object obj);
	abstract public E3DTreeNode add(Object obj);
	abstract public void rehash(Object obj);
}

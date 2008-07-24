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
package espresso3d.engine.renderer.rendertree;

import espresso3d.engine.E3DEngine;
import espresso3d.engine.lowlevel.tree.E3DTree;
import espresso3d.engine.renderer.base.IE3DRenderable;
import espresso3d.engine.renderer.rendertree.handler.E3DRenderTreeActorHandler;
import espresso3d.engine.renderer.rendertree.handler.E3DRenderTreeImageHandler;
import espresso3d.engine.renderer.rendertree.handler.E3DRenderTreeParticleHandler;
import espresso3d.engine.renderer.rendertree.handler.E3DRenderTreeQuadHandler;
import espresso3d.engine.renderer.rendertree.handler.E3DRenderTreeSectorHandler;
import espresso3d.engine.renderer.rendertree.handler.E3DRenderTreeSpriteHandler;
import espresso3d.engine.renderer.rendertree.handler.E3DRenderTreeTriangleHandler;
import espresso3d.engine.renderer.rendertree.nodes.E3DRenderRootNode;
import espresso3d.engine.renderer.rendertree.nodes.base.E3DRenderTreeNode;

public class E3DRenderTree extends E3DTree implements IE3DRenderable
{
	//Keyed off actorID and contains copied list of triangles for the actor how
	// it was currently added
	private E3DRenderTreeActorHandler actorHandler;
	private E3DRenderTreeParticleHandler particleHandler;
	private E3DRenderTreeQuadHandler quadHandler;
	private E3DRenderTreeTriangleHandler triangleHandler;
	private E3DRenderTreeSectorHandler sectorHandler;
	private E3DRenderTreeSpriteHandler spriteHandler;
	private E3DRenderTreeImageHandler imageHandler;
	
	public E3DRenderTree(E3DEngine engine)
	{
		super(engine);

		actorHandler = new E3DRenderTreeActorHandler(engine, this);
		particleHandler = new E3DRenderTreeParticleHandler(engine, this);
		quadHandler = new E3DRenderTreeQuadHandler(engine, this);
		triangleHandler = new E3DRenderTreeTriangleHandler(engine, this);
		sectorHandler = new E3DRenderTreeSectorHandler(engine, this);
		spriteHandler = new E3DRenderTreeSpriteHandler(engine, this);
		imageHandler = new E3DRenderTreeImageHandler(engine, this);
		
		clear();
	}

	//FIXME: implement
	public E3DRenderTree(E3DRenderTree renderTree)
	{
		super(renderTree.getEngine());
	}
	
	public void render() {
        if(getRootNode() == null)
            return;
		((E3DRenderTreeNode)getRootNode()).applyOptions();
	}
	
	public void clear()
	{
		super.clear();
		setRootNode(new E3DRenderRootNode(getEngine(), this));
	}

	public E3DRenderTreeActorHandler getActorHandler() {
		return actorHandler;
	}

	public E3DRenderTreeParticleHandler getParticleHandler() {
		return particleHandler;
	}

	public E3DRenderTreeQuadHandler getQuadHandler() {
		return quadHandler;
	}

	public E3DRenderTreeSectorHandler getSectorHandler() {
		return sectorHandler;
	}

	public E3DRenderTreeSpriteHandler getSpriteHandler() {
		return spriteHandler;
	}

	public E3DRenderTreeTriangleHandler getTriangleHandler() {
		return triangleHandler;
	}

	public E3DRenderTreeImageHandler getImageHandler() {
		return imageHandler;
	}
}

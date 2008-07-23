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
import espresso3d.engine.renderer.base.E3DAnimatedTextureRenderable;
import espresso3d.engine.renderer.rendertree.E3DRenderTree;
import espresso3d.engine.renderer.rendertree.handler.base.E3DRenderTreeHandler;
import espresso3d.engine.renderer.rendertree.nodes.leaf.E3DParticleNode;
import espresso3d.engine.renderer.rendertree.nodes.leaf.E3DQuadNode;
import espresso3d.engine.world.sector.particle.E3DParticle;

public class E3DRenderTreeParticleHandler extends E3DRenderTreeHandler 
{
	public E3DRenderTreeParticleHandler(E3DEngine engine, E3DRenderTree renderTree)
	{
		super(engine, renderTree);
	}
	
	
	public E3DTreeNode add(Object particleObj)
	{
		if(particleObj == null || !(particleObj instanceof E3DParticle))
			return null;
		
        return add((E3DParticle)particleObj);
	}
	
    private E3DTreeNode add(E3DParticle particle)
    {
        if(particle.isAlive())
        {
            E3DTreeNode currentParent = addRenderable((E3DAnimatedTextureRenderable)particle);
            return add(particle, currentParent);
        }
        return null;
        
    }
    
    public E3DTreeNode add(E3DParticle particle, E3DTreeNode currentParent)
    {
        return addParticleNode(particle, currentParent);
    }

	private E3DTreeNode addParticleNode(E3DParticle particle, E3DTreeNode parentNode)
	{
		E3DParticleNode particleNode = new E3DParticleNode(getEngine(), getRenderTree(), particle);
		E3DParticleNode newParentParticleNode = (E3DParticleNode)renderTree.addNode(particleNode, parentNode);
		if(particleNode != newParentParticleNode)
			renderTree.addObjectToLeaf(newParentParticleNode, particle); //found it, add the triangle
		return newParentParticleNode;
    }	
	
	public boolean remove(Object particleObj)
	{
		if(particleObj == null || !(particleObj instanceof E3DParticle))
			return false;
		return remove((E3DParticle)particleObj);
	}
    
    public boolean remove(E3DParticle particle)
    {
    	if(particle == null)
    		return false;
    	
        E3DParticleNode particleNode = (E3DParticleNode)renderTree.findLeafNodeForObject(particle);
        if(particleNode == null)
        	return false;
        
        getRenderTree().removeObjectFromLeaf(particleNode, particle);
        return true;
    }
	
	public void rehash(Object particleObj) 
    {
        if(particleObj == null || !(particleObj instanceof E3DParticle))
            return;

        E3DParticle particle = (E3DParticle)particleObj;
		remove(particle);
		add(particle);
	}
    
    public void rehash(E3DParticle particle)
    {
        remove(particle);
        add(particle);
    }
}

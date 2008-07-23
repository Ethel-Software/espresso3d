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
import espresso3d.engine.lowlevel.tree.E3DTreeNode;
import espresso3d.engine.renderer.rendertree.E3DRenderTree;
import espresso3d.engine.renderer.rendertree.nodes.base.E3DRenderTreeNode;
import espresso3d.engine.world.sector.particle.E3DParticle;

public class E3DParticleNode extends E3DRenderTreeNode
{
	public static final int NODE_LEVEL = 4; //No orientation for a particle list, each particle will have its own orientation

	public ArrayList particleList;
	private HashMap particleMap;

//	public E3DParticleNode(E3DEngine engine, E3DRenderTree tree)
//	{
//		super(engine, tree, NODE_LEVEL);
//		particleList = new ArrayList();
//		particleMap = new HashMap();
//	}
	
	public E3DParticleNode(E3DEngine engine, E3DRenderTree tree, E3DParticle initialParticle)
	{
		super(engine, tree, NODE_LEVEL);
		particleList = new ArrayList();
		particleMap = new HashMap();
		addParticleToNode(initialParticle);
	}
	
	public E3DParticleNode(E3DEngine engine, E3DRenderTree tree, ArrayList initialQuads)
	{
		super(engine, tree, NODE_LEVEL);
		if(initialQuads == null)
			return;
		particleList = new ArrayList();
		particleMap = new HashMap();
	
		E3DParticle particle;
		for(int i=0; i < initialQuads.size(); i++)
		{
			particle = (E3DParticle)initialQuads.get(i);
			addParticleToNode(particle);
		}
	}	

	public E3DTreeNode getClone() 
	{
		E3DParticleNode particleNode = new E3DParticleNode(getEngine(), (E3DRenderTree)getTree(), particleList);
		E3DParticleNode newParentParticleNode = (E3DParticleNode)getTree().addNode(particleNode, getParentNode());

		E3DParticle particle;
		for(int i=0; i < particleList.size(); i++)
		{
			particle = (E3DParticle)particleList.get(i);
			getTree().addObjectToLeaf(newParentParticleNode, particle); //found it, add the triangle
		}
	
		return newParentParticleNode;
		
//		return new E3DParticleNode(getEngine(), (E3DRenderTree)getTree(), particleList);
	}
	
	protected void applyPreRenderOptions()
	{
		getEngine().getGeometryRenderer().renderParticleList(getParticleList(), getRenderMode(this.getParentNode()));
	}
	
	protected void applyPostRenderOptions() {
	}

	private void addParticleToNode(E3DParticle particle)
	{
		if(!particleMap.containsKey(particle))
		{
			particleMap.put(particle, particle);
			particleList.add(particle);
		}
        
	}

	/**
	 * Returns false if its the last quad in the quadmap for this node (ie: remove it)
	 * @param quad
	 * @return
	 */
	private boolean removeParticleFromNode(E3DParticle particle)
	{
		if(!particleMap.containsKey(particle))
			return true;
		
		particleMap.remove(particle);
		particleList.remove(particle);
		return !particleMap.isEmpty();
	}

	private boolean removeParticlesFromNode(ArrayList particleList)
	{
		for(int i=0; i < particleList.size(); i++)
		{
			if(!removeParticleFromNode((E3DParticle)particleList.get(i)))
				return false;
		}
		return true;
	}
	
	public boolean equals(Object arg0) 
	{
		if(arg0 != null && arg0 instanceof E3DParticleNode && ((E3DParticleNode)arg0).particleList.size() > 0 && 
            particleMap.containsKey(((E3DParticle)((E3DParticleNode)arg0).particleList.get(0))))
			return true;
		return false;
	}

	public int hashCode() {
		return 2;
	}

	public ArrayList getParticleList()
	{
		if(particleList.isEmpty())
			return null;

		return particleList;
	}

	public boolean isLeaf() {
		return true;
	}

	public List getLeafObjects() {
		return particleList;
	}
	
	public void addLeafObject(Object leafObject) 
	{
		addParticleToNode((E3DParticle)leafObject);
	}
	
	public boolean removeLeafObject(Object leafObject) 
	{
		return removeParticleFromNode((E3DParticle)leafObject);
	}
	
	public void clear()
	{
		if(particleList == null || particleList.isEmpty())
			return;
		
		removeParticlesFromNode(particleList);
	}

}

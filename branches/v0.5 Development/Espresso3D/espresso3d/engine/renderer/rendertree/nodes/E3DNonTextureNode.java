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
package espresso3d.engine.renderer.rendertree.nodes;

import java.util.List;

import espresso3d.engine.E3DEngine;
import espresso3d.engine.lowlevel.tree.E3DTreeNode;
import espresso3d.engine.renderer.rendertree.E3DRenderTree;
import espresso3d.engine.renderer.rendertree.nodes.base.E3DRenderTreeNode;
import espresso3d.engine.renderer.texture.E3DTexture;

public class E3DNonTextureNode extends E3DRenderTreeNode
{
	private static final int NODE_LEVEL = 3;
	
	public E3DNonTextureNode(E3DEngine engine, E3DRenderTree tree)
	{
		super(engine, tree, NODE_LEVEL);
	}

	public E3DTreeNode getClone() 
	{
		return new E3DNonTextureNode(getEngine(), (E3DRenderTree)getTree());
	}
	
	protected void applyPreRenderOptions() 
	{
		getEngine().getGeometryRenderer().disableAllTextureUnits();
		getEngine().getGeometryRenderer().initSolidAndLineRendering();
	}


	protected void applyPostRenderOptions() {
	}
	
	public boolean equals(Object arg0) {
		return(arg0 != null && arg0 instanceof E3DNonTextureNode);
	}

	public int hashCode() {
		return NODE_LEVEL;
	}
	
	public boolean isLeaf() {
		return false;
	}
	
	public List getLeafObjects() {
		return null;
	}
	public void addLeafObject(Object leafObject) {
	}
	
	public boolean removeLeafObject(Object leafObject) {
		return true;
	}
	
	public void clear()
	{
	}
}

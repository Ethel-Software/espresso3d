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
import espresso3d.engine.renderer.base.E3DBlendMode;
import espresso3d.engine.renderer.rendertree.E3DRenderTree;
import espresso3d.engine.renderer.rendertree.nodes.base.E3DRenderTreeNode;

public class E3DBlendModeNode extends E3DRenderTreeNode
{
	private static final int NODE_LEVEL = 2;
	private E3DBlendMode blendMode;
	
	public E3DBlendModeNode(E3DEngine engine, E3DRenderTree tree, E3DBlendMode blendMode)
	{
		super(engine, tree, NODE_LEVEL);
		this.blendMode = blendMode;
	}
	
	public E3DBlendModeNode(E3DEngine engine, E3DRenderTree tree, int blendMode)
	{
		this(engine, tree, new E3DBlendMode(engine, blendMode));
	}

	public E3DTreeNode getClone() 
	{
		return new E3DBlendModeNode(getEngine(), (E3DRenderTree)getTree(), blendMode);
	}

	public boolean equals(Object arg0) 
	{
		return (arg0 != null && arg0 instanceof E3DBlendModeNode &&
				((E3DBlendModeNode)arg0).blendMode.getBlendMode() == blendMode.getBlendMode());
	}

	public int hashCode() {
		if(blendMode == null)
			return -1;
		return blendMode.getBlendMode();
	}

	public E3DBlendMode getBlendMode() {
		return blendMode;
	}

	public void setBlendMode(E3DBlendMode blendMode) {
		this.blendMode = blendMode;
	}
	
	public int getBlendModeValue()
	{
		return blendMode.getBlendMode();
	}
	
	protected void applyPreRenderOptions() 
	{
		if(blendMode.getBlendMode() == E3DBlendMode.BLENDMODE_BLEND)
			getEngine().getGeometryRenderer().initBlendedRendering();
		else
			getEngine().getGeometryRenderer().disableBlendedRendering();
	}

	protected void applyPostRenderOptions() 
	{
//		getEngine().getGeometryRenderer().initBlendedRendering();
		if(blendMode.getBlendMode() == E3DBlendMode.BLENDMODE_BLEND)
			getEngine().getGeometryRenderer().disableBlendedRendering();
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
		blendMode = null;
	}
}

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

import org.lwjgl.opengl.GL11;

import espresso3d.engine.E3DEngine;
import espresso3d.engine.lowlevel.matrix.E3DOrientation;
import espresso3d.engine.lowlevel.tree.E3DTreeNode;
import espresso3d.engine.renderer.rendertree.E3DRenderTree;
import espresso3d.engine.renderer.rendertree.nodes.base.E3DRenderTreeNode;

public class E3DOrientationNode extends E3DRenderTreeNode
{
	private static final int NODE_LEVEL = 4;
	private E3DOrientation orientation;
	
	public E3DOrientationNode(E3DEngine engine, E3DRenderTree tree, E3DOrientation orientation)
	{
		super(engine, tree, NODE_LEVEL);
		this.orientation = orientation;
	}
	
	public E3DTreeNode getClone() 
	{
		return new E3DOrientationNode(getEngine(), (E3DRenderTree)getTree(), orientation);
	}
	
	protected void applyPreRenderOptions() 
	{
		GL11.glPushMatrix();

		if(orientation != null)
			GL11.glMultMatrix(orientation.getFloatBuffer());
	}
	
	protected void applyPostRenderOptions() 
	{
		GL11.glPopMatrix();
	}	

	public boolean equals(Object arg0) 
	{
		if(arg0 == null || !(arg0 instanceof E3DOrientationNode))
			return false;
		return (((E3DOrientationNode)arg0).orientation.equals(this.orientation));
	}

	public int hashCode() 
	{
		if(orientation == null)
			return -1;
		return orientation.hashCode();
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
		orientation = null;
	}

	public E3DOrientation getOrientation()
	{
		return orientation;
	}

	public void setOrientation(E3DOrientation orientation)
	{
		this.orientation = orientation;
	}
}

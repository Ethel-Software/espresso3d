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

public class E3DTextureNode extends E3DRenderTreeNode
{
	private static final int NODE_LEVEL = 3;
	private E3DTexture texture;
	
	public E3DTextureNode(E3DEngine engine, E3DRenderTree tree, E3DTexture texture)
	{
		super(engine, tree, NODE_LEVEL);
		this.texture = texture;
	}

	public E3DTreeNode getClone() 
	{
		return new E3DTextureNode(getEngine(), (E3DRenderTree)getTree(), texture);
	}
	
	protected void applyPreRenderOptions() 
	{
		if(texture != null)
		{
			//Clear them for the first texture unit only
			int textureUnitNumber = getTextureUnitNumber();
			if(textureUnitNumber == 0)
				getEngine().getGeometryRenderer().initTexturing();
			getEngine().getGeometryRenderer().initTextureUnit(textureUnitNumber, texture.getGlTextureID());
		}
	}

	/**
	 * Determines what unit this is if there are multiples strung together (multitexturing)
	 * @return
	 */
	private int getTextureUnitNumber()
	{
		E3DTreeNode node;
		int num = 0;
		node = getParentNode();
		while(node != null && node instanceof E3DTextureNode)
		{
			num++;
			node = node.getParentNode();
		}
		return num;
	}
	protected void applyPostRenderOptions() {
	}
	
	public boolean equals(Object arg0) {
		return(arg0 != null && arg0 instanceof E3DTextureNode && 
				((E3DTextureNode)arg0).texture != null &&
		   ((E3DTextureNode)arg0).texture.getGlTextureID() == texture.getGlTextureID());
	}

	public int hashCode() {
		if(texture == null)
			return -1;
		return texture.getGlTextureID();
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
		texture = null;
	}
}

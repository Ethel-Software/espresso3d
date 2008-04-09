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
package espresso3d.engine.renderer.rendertree.nodes.base;

import java.util.Iterator;
import java.util.Map;

import espresso3d.engine.E3DEngine;
import espresso3d.engine.lowlevel.tree.E3DTreeNode;
import espresso3d.engine.renderer.rendertree.E3DRenderTree;
import espresso3d.engine.renderer.rendertree.nodes.E3DRenderModeNode;

abstract public class E3DRenderTreeNode extends E3DTreeNode 
{
	public E3DRenderTreeNode(E3DEngine engine, E3DRenderTree tree, int nodeLevel)
	{
		super(engine, tree, nodeLevel);
	}
	
	public void setParentNode(E3DTreeNode parentNode)
	{
		// TODO Auto-generated method stub
		super.setParentNode(parentNode);
	}

	/**
	 * Recursively apply the options.
	 * This needs to be overridden to apply the correct option for each node
	 *
	 */
	public void applyOptions()
	{
		applyPreRenderOptions(); //call this nodes option
		
		Map childNodes = getChildNodes();
		if(childNodes != null)
		{
			Iterator children = childNodes.entrySet().iterator();
			Map.Entry entry = null;
			E3DRenderTreeNode child;
			if(children != null)
			{
				while(children.hasNext())
				{
					entry = (Map.Entry)children.next();
					child = (E3DRenderTreeNode)entry.getValue();
					child.applyOptions();
				}
			}
		}
		applyPostRenderOptions(); //call this nodes option
	}
	
	protected int getRenderMode(E3DTreeNode node)
	{
		if(node == null)
			return -1;
		
		if(node instanceof E3DRenderModeNode)
			return ((E3DRenderModeNode)node).getRenderModeValue();
		else
			return getRenderMode(node.getParentNode());
	}
	
	abstract protected void applyPreRenderOptions();
	abstract protected void applyPostRenderOptions();
	abstract public void clear();
}

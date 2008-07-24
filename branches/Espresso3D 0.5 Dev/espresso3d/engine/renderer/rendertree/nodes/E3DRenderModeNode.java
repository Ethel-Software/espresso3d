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
import espresso3d.engine.renderer.base.E3DRenderMode;
import espresso3d.engine.renderer.rendertree.E3DRenderTree;
import espresso3d.engine.renderer.rendertree.nodes.base.E3DRenderTreeNode;
import espresso3d.engine.window.viewport.E3DViewport;

public class E3DRenderModeNode extends E3DRenderTreeNode 
{
	private static final int NODE_LEVEL = 1;
	private E3DRenderMode renderMode;
	
	public E3DRenderModeNode(E3DEngine engine, E3DRenderTree tree, E3DRenderMode renderMode)
	{
		super(engine, tree, NODE_LEVEL);
		this.renderMode = renderMode;
	}
	
	public E3DRenderModeNode(E3DEngine engine, E3DRenderTree tree, int renderMode)
	{
		this(engine, tree, new E3DRenderMode(engine, renderMode));
	}

	public E3DTreeNode getClone() 
	{
		return new E3DRenderModeNode(getEngine(), (E3DRenderTree)getTree(), renderMode);
	}	
	protected void applyPreRenderOptions()
	{
		E3DViewport currentViewport = getEngine().getCurrentViewport();
		if((currentViewport != null && (currentViewport.getRenderMode().getRenderMode() == E3DRenderMode.RENDERMODE_WIREFRAME || currentViewport.getRenderMode().getRenderMode() == E3DRenderMode.RENDERMODE_SOLID)) ||
				getRenderMode().getRenderMode() == E3DRenderMode.RENDERMODE_WIREFRAME || getRenderMode().getRenderMode() == E3DRenderMode.RENDERMODE_SOLID)
			getEngine().getGeometryRenderer().initSolidAndLineRendering();
		else
			getEngine().getGeometryRenderer().initTexturedRendering();
	}

	protected void applyPostRenderOptions() {
	}		
	
	public boolean equals(Object arg0) 
	{
		if(arg0 != null && arg0 instanceof E3DRenderModeNode)
		{ 
			E3DRenderModeNode node = (E3DRenderModeNode)arg0;

			if(renderMode == null)
				return false;
			
			if(node.renderMode.getRenderMode() == renderMode.getRenderMode())
				return true;
		}
		
		return false;
	}

	public int hashCode() {
		if(renderMode == null)
			return -1;
		return renderMode.getRenderMode();
	}

	public E3DRenderMode getRenderMode() {
		return renderMode;
	}

	public void setRenderMode(E3DRenderMode renderMode) {
		this.renderMode = renderMode;
	}
	
	public int getRenderModeValue()
	{
		return renderMode.getRenderMode();
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
		renderMode = null;
	}
}

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
package espresso3d.engine.renderer.rendertree.handler;

import java.util.ArrayList;
import java.util.List;
import espresso3d.engine.E3DEngine;
import espresso3d.engine.lowlevel.geometry.E3DTriangle;
import espresso3d.engine.lowlevel.tree.E3DTreeNode;
import espresso3d.engine.renderer.rendertree.E3DRenderTree;
import espresso3d.engine.renderer.rendertree.handler.base.E3DRenderTreeHandler;
import espresso3d.engine.world.sector.E3DSector;

public class E3DRenderTreeSectorHandler extends E3DRenderTreeHandler 
{
	public E3DRenderTreeSectorHandler(E3DEngine engine, E3DRenderTree renderTree)
	{
		super(engine, renderTree);
	}

	public E3DTreeNode add(Object sectorObj)
	{
		if(sectorObj == null || !(sectorObj instanceof E3DSector))
			return null;

		E3DSector sector = (E3DSector)sectorObj;
		return add(sector);
	}
	
	public E3DTreeNode add(E3DSector sector)
	{
		if(sector.getTriangleList() == null || sector.getTriangleList().isEmpty())
			return null;
		
		ArrayList triangleList = sector.getTriangleList();

		E3DTriangle triangle;
		for(int i=0; i < triangleList.size(); i++)
		{
			triangle = (E3DTriangle)triangleList.get(i);
			getRenderTree().getTriangleHandler().add(triangle);
		}
		return null;
	}
	
	public boolean remove(Object sectorObj)
	{
		if(sectorObj == null || !(sectorObj instanceof E3DSector))
			return false;
		
		E3DSector sector = (E3DSector)sectorObj;
		remove(sector);
		return true;
	}
	
	public void remove(E3DSector sector)
	{
		E3DTriangle triangle;
		List triangleList = sector.getTriangleList();
		for(int i=0; i < triangleList.size(); i++)
		{
			triangle = (E3DTriangle)triangleList.get(i);
			renderTree.getTriangleHandler().remove(triangle);
		}
	}
	
	public void rehash(Object sectorObj)
	{
		remove(sectorObj);
		add(sectorObj);
	}
}

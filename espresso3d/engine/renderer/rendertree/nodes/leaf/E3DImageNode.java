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
import espresso3d.engine.lowlevel.tree.E3DTreeNode;
import espresso3d.engine.renderer.rendertree.E3DRenderTree;
import espresso3d.engine.renderer.rendertree.nodes.base.E3DRenderTreeNode;
import espresso3d.engine.window.viewport.image.E3DImage;
import espresso3d.engine.world.sector.particle.E3DParticle;

public class E3DImageNode extends E3DRenderTreeNode
{
	public static final int NODE_LEVEL = 4;

	public ArrayList imageList;
	private HashMap imageMap;

//	public E3DImageNode(E3DEngine engine, E3DRenderTree tree)
//	{
//		super(engine, tree, NODE_LEVEL);
//		imageList = new ArrayList();
//		imageMap = new HashMap();
//	}

	public E3DImageNode(E3DEngine engine, E3DRenderTree tree, E3DImage initialImage)
	{
		super(engine, tree, NODE_LEVEL);
		imageList = new ArrayList();
		imageMap = new HashMap();
		addImageToNode(initialImage);
	}
	
	public E3DImageNode(E3DEngine engine, E3DRenderTree tree, ArrayList initialImages)
	{
		super(engine, tree, NODE_LEVEL);
		if(initialImages == null)
			return;
		imageMap = new HashMap();
		imageList = new ArrayList();
		E3DImage image;
		for(int i=0; i < initialImages.size(); i++)
		{
			image = (E3DImage)initialImages.get(i);
			addImageToNode(image);
		}
	}	

	public E3DTreeNode getClone() 
	{
		E3DImageNode imageNode = new E3DImageNode(getEngine(), (E3DRenderTree)getTree(), imageList);
		E3DImageNode newParentImageNode = (E3DImageNode)getTree().addNode(imageNode, getParentNode());

		E3DImage image;
		for(int i=0; i < imageList.size(); i++)
		{
			image = (E3DImage)imageList.get(i);
			getTree().addObjectToLeaf(newParentImageNode, image); 
		}
	
		return newParentImageNode;
				
//		return new E3DImageNode(getEngine(), (E3DRenderTree)getTree(), imageList);
	}
	
	protected void applyPreRenderOptions()
	{
		getEngine().getGeometryRenderer().renderQuadList(getEngine(), getQuadList(), getRenderMode(this.getParentNode()));
	}
	protected void applyPostRenderOptions() {
	}

	private void addImageToNode(E3DImage image)
	{
		if(!imageMap.containsKey(image.getImageID()))
		{
			imageMap.put(image.getImageID(), image);
			imageList.add(image);
		}
	}

	/**
	 * Returns false if its the last quad in the quadmap for this node (ie: remove it)
	 * @param quad
	 * @return
	 */
	private boolean removeImageFromNode(E3DImage image)
	{
		if(!imageMap.containsKey(image.getImageID()))
			return true;
		
		imageMap.remove(image.getImageID());
        imageList.remove(image);
		return !imageMap.isEmpty();	
	}

	public boolean removeImagesFromNode(ArrayList imageList)
	{
		for(int i=0; i < imageList.size(); i++)
		{
			if(!removeImageFromNode((E3DImage)imageList.get(i)))
				return false;
		}
		return true;
	}

    /**
     * This always assumes that we will be comparing a main image node with
     * a temporary image node (ie: will only have 1 image)
     */
	public boolean equals(Object arg0) 
	{
		if(arg0 != null && arg0 instanceof E3DImageNode &&
            imageMap.containsKey(((E3DImage)((E3DImageNode)arg0).imageList.get(0)).getImageID()))
			return true;
		return false;
	}

	public int hashCode() {
		return 3;
	}

	public ArrayList getQuadList()
	{
		if(imageList.isEmpty())
			return null;
		ArrayList quadList = new ArrayList();
		for(int i=0; i < imageList.size(); i++)
			quadList.add(((E3DImage)imageList.get(i)).getPositionedQuad());
		return quadList;
	}

	public boolean isLeaf() {
		return true;
	}

	public List getLeafObjects() {
		return imageList;
	}
	
	public void addLeafObject(Object leafObject) 
	{
		addImageToNode((E3DImage)leafObject);
	}
	
	public boolean removeLeafObject(Object leafObject) 
	{
		return removeImageFromNode((E3DImage)leafObject);
	}

	public void clear()
	{
		if(imageList == null || imageList.isEmpty())
			return;
		
		removeImagesFromNode(imageList);
	}

}

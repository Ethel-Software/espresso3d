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
package espresso3d.engine.lowlevel.tree;

import java.util.HashMap;
import java.util.List;

import espresso3d.engine.E3DEngine;
import espresso3d.engine.base.E3DEngineItem;
import espresso3d.engine.lowlevel.tree.base.IE3DHashableNode;

public abstract class E3DTreeNode extends E3DEngineItem implements IE3DHashableNode
{
	private E3DTree tree;
	private E3DTreeNode parentNode;
	private HashMap childNodes;
	private int nodeLevel;
	
	abstract public boolean equals(Object arg0);
	abstract public int hashCode();
	abstract public E3DTreeNode getClone(); //clone the node, but NOT the children
	abstract public boolean isLeaf();
	abstract public List getLeafObjects();
	abstract public void addLeafObject(Object leafObject);
	abstract public boolean removeLeafObject(Object leafObject);// return false if the node needs removal
	
	public E3DTreeNode(E3DEngine engine, E3DTree tree, int nodeLevel)
	{
		super(engine);
		this.tree = tree;
		this.nodeLevel = nodeLevel;
		childNodes = new HashMap();
	}
	
	void addChildNode(E3DTreeNode childNode)
	{
		childNodes.put(childNode, childNode);
		childNode.parentNode = this;
	}
	
	void removeChildNode(E3DTreeNode childNode)
	{
		childNodes.remove(childNode);
		if(childNodes.isEmpty())
			tree.removeNode(this);
	}

	public HashMap getChildNodes() {
		return childNodes;
	}

	public E3DTreeNode getParentNode() {
		return parentNode;
	}

	public E3DTree getTree() {
		return tree;
	}
	public int getNodeLevel() {
		return nodeLevel;
	}
	public void setTree(E3DTree tree)
	{
		this.tree = tree;
	}
	protected void setChildNodes(HashMap childNodes)
	{
		this.childNodes = childNodes;
	}
	protected void setNodeLevel(int nodeLevel)
	{
		this.nodeLevel = nodeLevel;
	}
	protected void setParentNode(E3DTreeNode parentNode)
	{
		this.parentNode = parentNode;
	}
}

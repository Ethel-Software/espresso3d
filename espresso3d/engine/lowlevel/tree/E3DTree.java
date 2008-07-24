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
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import espresso3d.engine.E3DEngine;
import espresso3d.engine.base.E3DEngineItem;

public class E3DTree extends E3DEngineItem
{
	HashMap leafObjectToNodeMap;
	HashMap leafNodeMap;
	E3DTreeNode rootNode;

	public E3DTree(E3DEngine engine)
	{
		super(engine);
		leafObjectToNodeMap = new HashMap();
		leafNodeMap = new HashMap();
	}
	
	public void setRootNode(E3DTreeNode rootNode)
	{
		this.rootNode = rootNode;
	}
	
	public E3DTreeNode addNode(E3DTreeNode node, E3DTreeNode parentNode)
	{
//		E3DTreeNode currentParent = parentNode;

//		This works, but is slow, i don't think it is really necessary for adds, we always know the parent node	
//		E3DTreeNode foundNode = findNodeRecursive(parentNode, node);
		
		E3DTreeNode foundNode = findNodeAtLevel(parentNode, node);
		if(foundNode == null)
		{
			parentNode.addChildNode(node);
			mapLeafObjectsToNode(node);
			return node;
		}
		else
		{
			mapLeafObjectsToNode(foundNode);
			return foundNode;
		}		
	}
	
	public void removeNode(E3DTreeNode node)
	{
		unmapLeafObjectsForNode(node);
		if(node.equals(rootNode))
			rootNode = null;
		else if(node.getParentNode() != null)
			node.getParentNode().removeChildNode(node);
	}
	
	public void addObjectToLeaf(E3DTreeNode node, Object leafObject)
	{
		List leafObjects = node.getLeafObjects();
		if(leafObjects != null)
		{
			node.addLeafObject(leafObject);
			mapLeafObjectToNode(leafObject, node);
		}
	}

	public void removeObjectFromLeaf(E3DTreeNode node, Object leafObject)
	{
		if(node == null || leafObject == null)
			return;
		
		List leafObjects = node.getLeafObjects();
		if(leafObjects != null)
		{
			unmapLeafObjectFromNode(leafObject);
			if(!node.removeLeafObject(leafObject))
				removeNode(node);
		}
	}
	
	public E3DTreeNode findNode(E3DTreeNode searchNode)
	{
		return findNodeRecursive(rootNode, searchNode);
	}
	
	public E3DTreeNode findLeafNode(E3DTreeNode searchNode)
	{
		if(leafNodeMap.containsKey(searchNode))
			return (E3DTreeNode)leafNodeMap.get(searchNode);
		return null;
	}
	
	public E3DTreeNode findLeafNodeForObject(Object leafObject)
	{
		if(leafObjectToNodeMap.containsKey(leafObject))
			return (E3DTreeNode)leafObjectToNodeMap.get(leafObject);
		return null;
	}

	
	/**
	 * Find a node from a starting point
	 * 
	 * FIXME: This can be sped up by adding a "level" that when exceed stops search the tree any deeper
	 * @param startingNode
	 * @param searchNode
	 * @return
	 */
	public E3DTreeNode findNodeRecursive(E3DTreeNode startingNode, E3DTreeNode searchNode)
	{
		if(startingNode.equals(searchNode))
			return startingNode;
		
		HashMap childNodes = startingNode.getChildNodes();
		if(childNodes == null || childNodes.isEmpty())
			return null;
		
		E3DTreeNode foundNode = null, childNode = null;
		Iterator it = startingNode.getChildNodes().entrySet().iterator();
		Map.Entry entry;
		while(it.hasNext())
		{
			entry = (Map.Entry)it.next();
			childNode = (E3DTreeNode)entry.getValue();
			if(childNode.equals(searchNode))
				return childNode;
			else
			{
				foundNode = findNodeRecursive(childNode, searchNode);
				if(foundNode != null)
					return foundNode;
			}
		}
		return null;
	}
	public E3DTreeNode findNodeAtLevel(E3DTreeNode startingNode, E3DTreeNode searchNode)
	{
		if(startingNode.equals(searchNode))
			return startingNode;
		
		HashMap childNodes = startingNode.getChildNodes();
		if(childNodes == null || childNodes.isEmpty())
			return null;
		
		if(childNodes.containsKey(searchNode))
			return (E3DTreeNode)childNodes.get(searchNode);
		
/*		E3DTreeNode foundNode = null, childNode = null;
		Iterator it = startingNode.getChildNodes().entrySet().iterator();
		Map.Entry entry;
		while(it.hasNext())
		{
			entry = (Map.Entry)it.next();
			childNode = (E3DTreeNode)entry.getValue();
			if(childNode.equals(searchNode))
				return childNode;
		}*/
		return null;
	}
	
	private void mapLeafObjectsToNode(E3DTreeNode leafNode)
	{
		if(leafNode.isLeaf())
		{
			leafNodeMap.put(leafNode, leafNode); //keep track of all leaf nodes
			
			List leafObjects = leafNode.getLeafObjects();
			if(leafObjects == null || leafObjects.isEmpty())
				return;
			for(int i=0; i < leafObjects.size(); i++)
				mapLeafObjectToNode(leafObjects.get(i), leafNode);
		}
	}

	private void unmapLeafObjectsForNode(E3DTreeNode leafNode)
	{
		if(leafNode.isLeaf())
		{
			leafNodeMap.remove(leafNode);
			
			List leafObjects = leafNode.getLeafObjects();
			if(leafObjects == null || leafObjects.isEmpty())
				return;
			for(int i=0; i < leafObjects.size(); i++)
				unmapLeafObjectFromNode(leafObjects.get(i));
		}
	}

	private void unmapLeafObjectFromNode(Object leafObject)
	{
		if(!leafObjectToNodeMap.containsKey(leafObject))
			return;
		leafObjectToNodeMap.remove(leafObject);
	}
	
	private void mapLeafObjectToNode(Object leafObject, E3DTreeNode leafNode)
	{
		leafObjectToNodeMap.put(leafObject, leafNode);
	}
	
	
	public void addAll(E3DTree tree)
	{
		if(tree == null)
			return;
		
		E3DTreeNode rootNode = tree.rootNode;
		if(rootNode == null)
			return;
		
		//If adding to an empty tree, simply push over the rootnode
		if(getRootNode() == null)
			setRootNode(rootNode.getClone());
		else
			addAllRecursively(rootNode, this.rootNode);
	}
	
	private void addAllRecursively(E3DTreeNode node, E3DTreeNode parentNode)
	{
		//Add the node to the tree if its not the root from the other tree
		E3DTreeNode copiedNode = parentNode;
		if(node.getParentNode() != null)
		{
			copiedNode = node.getClone();
			addNode(copiedNode, parentNode);
		}
		
		//Now, go through the new nodes childNodes adding them all
		HashMap map = node.getChildNodes();
		if(map == null)
			return;
		Iterator it = map.entrySet().iterator();
		if(it == null)
			return;
		
		E3DTreeNode childNode;
		Map.Entry entry;
		while(it.hasNext())
		{
			entry = (Map.Entry)it.next();
			childNode = (E3DTreeNode)entry.getValue();
			addAllRecursively(childNode, copiedNode);
		}
	}

	public E3DTreeNode getRootNode() {
		return rootNode;
	}
	
	public void clear()
	{
		leafObjectToNodeMap.clear();
	    leafNodeMap.clear();
		rootNode = null;
	}
	
	public String toString()
	{
		StringBuffer buffer = new StringBuffer();
		buffer.append("Tree Statistics: \n");
		buffer.append("================\n");
		buffer.append("leafObjectToNodeMap size: " + leafObjectToNodeMap.size() + "\n");
		buffer.append("leafNodeMap size: " + leafNodeMap.size() + "\n");
		
		long numLeafObjects = 0;
		if(leafNodeMap != null && leafNodeMap.size() > 0)
		{
			Iterator it = leafNodeMap.entrySet().iterator();
			while(it.hasNext())
			{
				E3DTreeNode leafNode = (E3DTreeNode)((Map.Entry)it.next()).getValue();
				if(leafNode.getLeafObjects() != null)
					numLeafObjects += leafNode.getLeafObjects().size();
			}
		}
		buffer.append("Leaf objects: " + numLeafObjects + "\n\n");
		
		return buffer.toString();
	}
}

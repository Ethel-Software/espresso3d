/*
 * 
 
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
package espresso3d.engine.lowlevel.map;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * A HashMap that can store more than one object for a given key
 * 
 * So (key, ArrayList(values))
 * 
 * @author n899570x
 *
 */
public class E3DHashListMap extends HashMap
{
	/**
	 * Returns the ArrayList of values on the key (including the newest value)
	 */
	public Object put(Object key, Object value) 
	{
		ArrayList valueList;
		if(containsKey(key))
		{
			valueList = (ArrayList)get(key);
			valueList.add(value);
		}
		else
		{
			valueList = new ArrayList();
			valueList.add(value);
			super.put(key, valueList);
		}
		
		return valueList;
	}
	
	public Object get(Object arg0) 
	{
		return super.get(arg0);
	}
	
	public Object get(Object key, int listPos)
	{
		if(!containsKey(key))
			return null;
		ArrayList list = (ArrayList)get(key);
		if(listPos >= list.size())
			return null;
		return list.get(listPos);
	}
	
	public ArrayList getList(Object key)
	{
		if(!containsKey(key))
			return null;
		else return (ArrayList)get(key);
	}
	
	public int listSize(Object key)
	{
		if(!containsKey(key))
			return 0;
		return ((ArrayList)get(key)).size();
	}
	
	/**
	 * Remove one value from a key that could contain a list of values
	 * @param key
	 * @param value
	 * @return
	 */
	public Object remove(Object key, Object value) {
		if(!containsKey(key))
			return null;
		ArrayList list = (ArrayList)get(key);
		for(int i=0; i < list.size(); i++)
		{
			if(value == list.get(i))
			{
				list.remove(i);
				break;
			}
		}
		if(list.isEmpty()) //if the list is empty, remove the entire thing
			return super.remove(key);
		else
			return list;
	}
}

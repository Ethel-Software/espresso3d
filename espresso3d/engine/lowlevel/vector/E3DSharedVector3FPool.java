/*
 * Created on Mar 9, 2005
 *
 
   	Copyright 2005 Curtis Moxley
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
package espresso3d.engine.lowlevel.vector;

import java.util.ArrayList;

import espresso3d.engine.E3DEngine;
import espresso3d.engine.base.E3DEngineItem;


/**
 * @author Curt
 *
 * SharedVector3FPool's is basically a pool that given
 * an x, y, and z coordinate, will return you a shared
 * vector.  This is useful in meshes so a triangle
 * only unique vertices are translated at a time.  
 * 
 */
public class E3DSharedVector3FPool extends E3DEngineItem {
    private static final int INITIAL_POOL_SIZE = 100;
//    HashMap vectors = null;
    private ArrayList uniqueVectorList = null;

    public E3DSharedVector3FPool(E3DEngine engine)
    {
        this(engine, INITIAL_POOL_SIZE);
    }

    public E3DSharedVector3FPool(E3DEngine engine, int poolSize)
    {
        super(engine);
//        vectors = new HashMap(poolSize); 
        uniqueVectorList = new ArrayList(poolSize);
    }

    //Returns a vector that may be shared among other objects.
    // If it doesn't exist yet, it is created.
    public E3DVector3F getSharedVector(double x, double y, double z)
    {
        E3DVector3F searchVector = findVector(x, y, z);
        if(searchVector == null)
        {
            searchVector = new E3DVector3F(x, y, z);
            uniqueVectorList.add(searchVector);
        }
        
        return searchVector;
    }
    
    private E3DVector3F findVector(double x, double y, double z)
    {
        E3DVector3F search = null;
        for(int i=0; i < uniqueVectorList.size(); i++)
        {
            search = (E3DVector3F)uniqueVectorList.get(i);
            if(search.equals(x, y, z))
                return search;
        }
        return null;
    }
/*
    private String buildKey(double x, double y, double z)
    {
        keyBuffer.delete(0, keyBuffer.length());
        keyBuffer.append("X");
        keyBuffer.append(x);
        keyBuffer.append("Y");
        keyBuffer.append(y);
        keyBuffer.append("Z");
        keyBuffer.append(z);
        
        return keyBuffer.toString();
    }
  */  
    public ArrayList getUniqueVector3FList()
    {
        return uniqueVectorList;
    }
    
    
}
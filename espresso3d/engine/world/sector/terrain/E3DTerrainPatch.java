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
 * 
 */
package espresso3d.engine.world.sector.terrain;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import espresso3d.engine.E3DEngine;
import espresso3d.engine.lowlevel.geometry.E3DTriangle;
import espresso3d.engine.lowlevel.vector.E3DVector2F;
import espresso3d.engine.lowlevel.vector.E3DVector3F;
import espresso3d.engine.world.E3DWorld;
import espresso3d.engine.world.sector.E3DSector;
import espresso3d.engine.world.sector.portal.E3DPortal;

public class E3DTerrainPatch extends E3DSector
{
	private ArrayList connectedPatches;
	private ArrayList triangleList;
	
	public E3DTerrainPatch(E3DEngine engine, E3DWorld world, String terrainPatchID)
	{
		super(engine, terrainPatchID);
		connectedPatches = new ArrayList();
		setWorld(world);
	}

	/**
	 * True if adds, false if its already there
	 * @param patch
	 * @return
	 */
	public boolean addConnectedPatch(E3DTerrainPatch patch)
	{
		if(!connectedPatches.contains(patch))
		{
			connectedPatches.add(patch);
			return true;
		}
		
		return false;
	}
	
	/**
	 * Used only during creation to get the minimum x coordinate of all the triangles
	 * The value is no longer valid after the thing has been rotated
	 * @return
	 */
	Double getMinX()
	{
		if(getMesh() == null || getMesh().getTriangleList() == null)
			return null;
		
		Double minX = null;
		E3DTriangle triangle;
		for(int i=0; i < getMesh().getTriangleList().size(); i++)
		{
			triangle = (E3DTriangle)getMesh().getTriangleList().get(i);

			double check;
			for(int v=0; v < 3; v++)
			{
				check = triangle.getVertexPos(v).getX();
				if(minX == null)
					minX = new Double(check);
				else
				{
					if(check < minX.doubleValue())
						minX = new Double(check);
				}
			}
		}
		
		return minX;
	}
	
	/**
	 * Used only during creation to get the minimum x coordinate of all the triangles
	 * 
	 * The value is no longer valid after the thing has been rotated
	 * @return
	 */
	Double getMaxX()
	{
		if(getMesh() == null || getMesh().getTriangleList() == null)
			return null;
		
		Double maxX = null;
		E3DTriangle triangle;
		for(int i=0; i < getMesh().getTriangleList().size(); i++)
		{
			triangle = (E3DTriangle)getMesh().getTriangleList().get(i);

			double check;
			for(int v=0; v < 3; v++)
			{
				check = triangle.getVertexPos(v).getX();
				if(maxX == null)
					maxX = new Double(check);
				else
				{
					if(check > maxX.doubleValue())
						maxX = new Double(check);
				}
			}
		}
		
		return maxX;
	}
	
	
	/**
	 * Used only during creation to get the minimum y coordinate of all the triangles
	 * The value is no longer valid after the thing has been rotated
	 * @return
	 */
	Double getMinY()
	{
		if(getMesh() == null || getMesh().getTriangleList() == null)
			return null;
		
		Double minY = null;
		E3DTriangle triangle;
		for(int i=0; i < getMesh().getTriangleList().size(); i++)
		{
			triangle = (E3DTriangle)getMesh().getTriangleList().get(i);

			double check;
			for(int v=0; v < 3; v++)
			{
				check = triangle.getVertexPos(v).getY();
				if(minY == null)
					minY = new Double(check);
				else
				{
					if(check < minY.doubleValue())
						minY = new Double(check);
				}
			}
		}
		
		return minY;
	}
	
	/**
	 * Used only during creation to get the maximum y coordinate of all the triangles
	 * 
	 * The value is no longer valid after the thing has been rotated
	 * @return
	 */
	Double getMaxY()
	{
		if(getMesh() == null || getMesh().getTriangleList() == null)
			return null;
		
		Double maxY = null;
		E3DTriangle triangle;
		for(int i=0; i < getMesh().getTriangleList().size(); i++)
		{
			triangle = (E3DTriangle)getMesh().getTriangleList().get(i);

			double check;
			for(int v=0; v < 3; v++)
			{
				check = triangle.getVertexPos(v).getY();
				if(maxY == null)
					maxY = new Double(check);
				else
				{
					if(check > maxY.doubleValue())
						maxY = new Double(check);
				}
			}
		}
		
		return maxY;
	}

    /**
     * Check if the point is on the same side of all portals in the patch.  That should mean it is contained within
     * all portals - the patch itself
     * @param point
     * @return
     */
    public boolean isPointContainedInPatchPortals(E3DVector3F point)
    {
        HashMap portalMap = getPortalMap();
        if(portalMap.isEmpty())
            return false;
        
        boolean first = true;
        double holdval = 0; 
        E3DPortal portal = null;
        Map.Entry entry = null;
        Iterator it = portalMap.entrySet().iterator();
        while(it.hasNext())
        {
            entry = (Map.Entry)it.next();
            portal = (E3DPortal)entry.getValue();
            
            //If the distance changes directions for any of hte portal calcs, then it isn't contained within
            double distance = portal.getTriangleA().getDistanceToPoint(point);
            System.out.println("" + distance);
            if(first) //unset
            {
                holdval = distance;
                first = false;
            }
            else
            {
                //If this distance is signed differently, then it is on the opposite of the portal and we aren't contained (Surrounded) by portals
                if((holdval > 0 && distance  <= 0) || 
                   (holdval <= 0 && distance > 0))
                    return false;
            }
        }
        
        return true;
        
    }
}

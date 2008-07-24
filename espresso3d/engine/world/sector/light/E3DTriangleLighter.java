/*
 * Created on Oct 18, 2004
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
package espresso3d.engine.world.sector.light;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import espresso3d.engine.lowlevel.geometry.E3DTriangle;
import espresso3d.engine.lowlevel.matrix.E3DOrientation;
import espresso3d.engine.lowlevel.vector.E3DVector3F;
import espresso3d.engine.lowlevel.vector.E3DVector4F;
import espresso3d.engine.world.sector.E3DSector;
/**
 * @author Curt
 *
 * This provides the lighting calculatinos used on geometry in the engine.
 * 
 * This is normally only used by the engine.
 */
public class E3DTriangleLighter 
{
	/**
	 * Will recalculate all the triangle's vertexColor's to simulate vertex lighting.  All
	 * previous triangle vertexColor information is lost
	 * 
	 * This will use lights in the current sector and all sectors that are directly linked to this sector
	 * via a portal.  This doesn't depend on visibility since even if a portal isn't visible,
	 * the light from the linked sector would affect this sector
	 * 
	 * @param sector  Sector to light
	 * @param triangleList
	 */
	public static void lightTriangles(E3DSector sector, ArrayList triangleList, E3DOrientation orientation)
	{
		E3DLight light = null;
		E3DTriangle triangle = null;
		E3DVector3F originalPosition = null;
        E3DVector4F litVertex = null;
		Map.Entry entry = null;
		
		int t = 0, i=0, v=0;
		
		//First, reset all the triangles in this sector to vertex color to 0 (unlit)
		for(t=0; t < triangleList.size(); t++)
		{
			triangle = (E3DTriangle)triangleList.get(t);
			triangle.resetVertexColor(0.0);
		}
		
		ArrayList linkedSectorList = sector.getUniqueDirectlyLinkedSectorList(); //get all the linked sectors
		if(linkedSectorList == null)
		    linkedSectorList = new ArrayList();
		linkedSectorList.add(sector); //also add this one
		
		E3DSector curSector = null;
		for(i=0; i<linkedSectorList.size(); i++)
		{
		    curSector = (E3DSector)linkedSectorList.get(i);
		    
			HashMap lightMap = curSector.getLightMap();
			Iterator it = lightMap.entrySet().iterator();
			//Go through all the lights in the curSector lighting every vertex on every triangle of the sector we want lit
			while(it.hasNext())
			{
				entry = (Map.Entry)it.next();
				light = (E3DLight)entry.getValue();
                originalPosition = light.getPosition();
                
                if(orientation != null)
                    light.setPosition(orientation.getLocalVector(light.getPosition()));
                
				for(t=0; t < triangleList.size(); t++)
				{
					triangle = (E3DTriangle)triangleList.get(t);
					
					for(v = 0; v < 3; v++)
					{
						litVertex = light.getLitVertex(triangle.getVertexPos(v));
						if(litVertex != null)
							triangle.getVertexColor()[v].addEqual(litVertex);
						
					}
					triangle.normaliseVertexColors(); //TODO: this is a lot of processing.. could this be done without somehow?
			   }
                
               light.setPosition(originalPosition);
			}
		}
	}

}

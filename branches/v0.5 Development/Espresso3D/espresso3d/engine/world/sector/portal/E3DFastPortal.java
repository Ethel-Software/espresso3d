/*
 * Created on Nov 7, 2004
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
package espresso3d.engine.world.sector.portal;

import espresso3d.engine.E3DEngine;
import espresso3d.engine.lowlevel.vector.E3DVector3F;

/**
 * @author Curt
 *
 * Fast portals cause the entire geometry of a linked sector to be rendered.
 *   They do not clip or cull the connected sectors geometry
 */
public class E3DFastPortal extends E3DPortal 
{
    public E3DFastPortal(E3DEngine engine, String portalID, String linkSectorID, String linkPortalID, E3DVector3F a, E3DVector3F b, E3DVector3F c, E3DVector3F d)
    {
        super(engine, portalID, linkSectorID, linkPortalID, a, b, c, d);
    }

}
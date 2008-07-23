/*
 * Created on Jan 25, 2005
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
package espresso3d.engine.world.sector.base;

import espresso3d.engine.lowlevel.vector.E3DVector3F;
import espresso3d.engine.world.sector.E3DSector;

/**
 * @author cmoxley
 *
 * Interface for any object that needs to be portal ready and automatically update its sector information
 * on collision with a portal.
 */
public interface IE3DPortalEnabledItem
{
	public E3DSector getSector();
	public void setSector(E3DSector sector);
	public void checkSectorChangeDuringMovement(E3DVector3F startPos, E3DVector3F endPos);
}

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
package espresso3d.engine.world.sector.portal;

import espresso3d.engine.world.sector.E3DSector;

public class E3DPortalCollision 
{
    
    private String currentSectorID;
    private String toSectorID;
    private E3DSector currentSector;
    private E3DSector toSector;
    public E3DSector getCurrentSector() {
        return currentSector;
    }
    public void setCurrentSector(E3DSector currentSector) {
        this.currentSector = currentSector;
    }
    public String getCurrentSectorID() {
        return currentSectorID;
    }
    public void setCurrentSectorID(String currentSectorID) {
        this.currentSectorID = currentSectorID;
    }
    public E3DSector getToSector() {
        return toSector;
    }
    public void setToSector(E3DSector toSector) {
        this.toSector = toSector;
    }
    public String getToSectorID() {
        return toSectorID;
    }
    public void setToSectorID(String toSectorID) {
        this.toSectorID = toSectorID;
    }
}

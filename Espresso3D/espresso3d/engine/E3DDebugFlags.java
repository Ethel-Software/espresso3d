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
package espresso3d.engine;

public class E3DDebugFlags 
{
    private boolean debugRenderPortals = false;
    private boolean debugNormalsRendered = false;

    public boolean isDebugRenderPortals() {
        return debugRenderPortals;
    }

    public void setDebugRenderPortals(boolean debugRenderPortals) {
        this.debugRenderPortals = debugRenderPortals;
    }

    public boolean isDebugNormalsRendered() {
        return debugNormalsRendered;
    }

    public void setDebugNormalsRendered(boolean debugNormalsRendered) {
        this.debugNormalsRendered = debugNormalsRendered;
    }
    
    

}

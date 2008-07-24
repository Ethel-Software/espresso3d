/*
 * Created on Dec 21, 2004
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
package espresso3d.engine.input;

import org.lwjgl.input.Mouse;

import espresso3d.engine.input.base.IE3DInputCallback;
import espresso3d.engine.input.E3DInputHandler;

/**
 * @author Curt
 *
 * Class to handle mouse movement
 */
class E3DMouseMovementHandler {
	private IE3DInputCallback mouseMovementCallback = null;

	private int x=0, y=0;
	
	public E3DMouseMovementHandler(){
		boolean grabbed = Mouse.isGrabbed();
		Mouse.setGrabbed(false);
	    x = Mouse.getEventX();
	    y = Mouse.getEventY();
	    Mouse.setGrabbed(grabbed);
	}

	void bindMouseMovementToHandler(IE3DInputCallback callback)
	{
	    mouseMovementCallback = callback;
	}    
    /**
	 * If the mouse position has changed, the bound mouse movement callback will get fired
	 *
	 */
	void checkMouseMovementInput()
	{
	    if(mouseMovementCallback == null)
	        return;

	    int dx=0, dy=0, dz=0; //dx, dy are change in mouse position.  dz is change in scroll wheel (if available)
	    if(Mouse.hasWheel())
	        dz = Mouse.getDWheel();
	    
	    if(Mouse.isGrabbed())
	    {
	        dx = Mouse.getDX();
	        dy = Mouse.getDY();
	    }
	    else
	    {
	        int localX = Mouse.getEventX(), localY = Mouse.getEventY();
	        
	        dx = localX - x;
	        x = localX;
	        
	        dy = localY - y;
	        y = localY;
	    }
	    if(dx != 0 || dy != 0 || dz != 0)
	        mouseMovementCallback.onMouseMovementInput(dx, dy, dz);
	}
}

/*
 * Created on Jul 24, 2004
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
package espresso3d.engine.input.base;

import java.util.ArrayList;

/**
 * @author espresso3d
 *
 * Any input callback class must implement this to be able to get input callbacks
 */
public interface IE3DInputCallback{

	/**
	 * When input is captured that has keys/actions bound to a class implementing this,
	 * onInput is called.  An arraylist of all keycodes that have been registered
	 * to the class and have been pressed since the last time this classes onInput was
	 * called are passed in
	 * @param deviceNum Specifies one of the static variables in E3DInputHandler.DEVICE_TYPE_KEYBOARD, ..MOUSE, etc., indicating which device to check the keycode against
	 * @param keyCodes
	 */
	public abstract void onKeyboardInput(ArrayList keyCodes);
	
	/**
	 * Mouse input is triggered when a mouse button is pressed 
	 * @param keyCodes  If any mouse button is pressed, it will be in this list
	 */
	public abstract void onMouseButtonInput(ArrayList keyCodes);

	/**
	 * Mouse movement input is trigger when the mouse moves
	 * @param changeInX If the mouse has moved in the X direction this will be nonZero indicating the amount it moved
	 * @param changeInY If the mouse has moved in the Y direciton this will be nonZero indicating the amount it moved
	 * @param changeInScrollWheel If the mouse scroll wheel has moved, this will indicate how far. This will always be 0 if the mouse doesn't have a wheel
	 */
	public abstract void onMouseMovementInput(int changeInX, int changeInY, int changeInScrollWheel);
}

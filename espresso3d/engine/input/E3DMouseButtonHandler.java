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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.lwjgl.input.Mouse;

import espresso3d.engine.input.base.IE3DInputCallback;

/**
 * @author Curt
 *
 * Class to handle mouse button input
 */
class E3DMouseButtonHandler 
{
    HashMap buttonsDown = new HashMap();
    HashMap mouseButtonToEventMap = new HashMap();
    
    
	/**
	 * Bind mouse movement to a IE3DInputCallback implementing class.
	 * @param callback
	 */
	public void bindMouseButtonToHandler(Integer mouseButton, IE3DInputCallback callback)
	{
	    mouseButtonToEventMap.put(mouseButton, callback);
	}

	
    void checkMouseButtonInput()
	{
		for(int i = 0; i < Mouse.getButtonCount(); i++)
		{
			if(!Mouse.next())
			    break;
		    //If mouse button is pressed, add it to the list of buttons pressed
			if(Mouse.getEventButton() != -1)
			{
				if(Mouse.getEventButtonState() == true)
				{
					Integer key = new Integer(Mouse.getEventButton());
					buttonsDown.put(key, key);
				}
				else if(Mouse.getEventButtonState() == false)
				{
				    Integer key = new Integer(Mouse.getEventButton());
					if(buttonsDown.containsKey(key))
						buttonsDown.remove(new Integer(Mouse.getEventButton()));
				}
			}
		}

		executeMouseButtonCallback(buttonsDown);
	}
	
	/**
	 * Find and call the callbacks for the pressed mouse buttons
	 * @param buttonsDown
	 */
	private void executeMouseButtonCallback(HashMap buttonsDown)
	{
		IE3DInputCallback callback = null;
		HashMap mouseEventBindings = new HashMap(); //Will contain a map of CMInputCallbacks with values being an arraylist of all the mouse buttons to be sent to that callback
		
		//Build an array of the keys to be sent to each CMINputCallback so we can send a list of them to each
		Iterator it = buttonsDown.entrySet().iterator();
		Map.Entry entry = null;
		Integer buttonCode = null;
		ArrayList buttonList = null;
		while(it.hasNext())
		{
			entry = (Map.Entry)it.next();
			buttonCode = (Integer)entry.getValue();

			if(mouseButtonToEventMap.containsKey(buttonCode))
			{
				callback = (IE3DInputCallback)mouseButtonToEventMap.get(buttonCode);
				if(mouseEventBindings.containsKey(callback)) //if it already has some keys for this event, just add this one
				{
					buttonList = (ArrayList)mouseEventBindings.get(callback);
					buttonList.add(buttonCode);
				}
				else //otherwise createa  new array list for this one in the event bindings map
				{
				    buttonList = new ArrayList();
					buttonList.add(buttonCode);
					mouseEventBindings.put(callback, buttonList);
				}
			}
		}

		it = mouseEventBindings.entrySet().iterator();
		entry  = null;
		callback = null;
		ArrayList callbackKeys = null;
		while(it.hasNext())
		{
			entry = (Map.Entry)it.next();
			
			callback = (IE3DInputCallback)entry.getKey();
			callbackKeys = (ArrayList)entry.getValue();
			
			callback.onMouseButtonInput(callbackKeys);
		}
	}

	boolean isMouseButtonDown(Integer buttonCode)
	{
	    return buttonsDown.containsKey(buttonCode);
	}

}
